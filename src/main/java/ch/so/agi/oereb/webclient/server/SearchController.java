package ch.so.agi.oereb.webclient.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@RestController
public class SearchController {
    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    
    @Value("${app.searchServiceUrl}")
    private String searchServiceUrl;
    
    @Value("${app.dataServiceUrl}")
    private String dataServiceUrl;

    @Value("${app.dataProductParcel}")
    private String dataProductParcel;

    @Value("${app.dataProductAddress}")
    private String dataProductAddress;
    
    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value = "/search/{searchText}", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public ResponseEntity<?> searchByText(@PathVariable String searchText) throws IOException {
        String encodedSearchText = URLEncoder.encode(searchText, StandardCharsets.UTF_8.toString());        
        URL url = new URL(searchServiceUrl+encodedSearchText);
        URLConnection request = url.openConnection();
        request.connect();
        
        JsonNode root = objectMapper.readTree(new InputStreamReader((InputStream) request.getContent()));
        
        return new ResponseEntity<JsonNode>(root,HttpStatus.OK);
    }
    
    @RequestMapping(value = "/search/{dataproductId}/{idFieldName}/{tid}", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public ResponseEntity<?> searchById(@PathVariable String dataproductId, @PathVariable String idFieldName, @PathVariable String tid) throws IOException {        
        ArrayNode parcelsArray = objectMapper.createArrayNode();

        if (dataproductId.equalsIgnoreCase(dataProductParcel)) {
            URL url = new URL(dataServiceUrl + dataproductId + "/?filter=[[\"" + idFieldName + "\",\"=\"," + tid + "]]");
            parcelsArray = getEgrid(url);
            return new ResponseEntity<JsonNode>(parcelsArray,HttpStatus.OK);
        } else if (dataproductId.equalsIgnoreCase(dataProductAddress)) {
            // 1) Koordinate des Einganges ermitteln.
            URL url = new URL(dataServiceUrl + dataproductId + "/?filter=[[\""+idFieldName+"\",\"=\","+tid+"]]");
            URLConnection request = url.openConnection();
            request.connect();

            JsonNode root = objectMapper.readTree(new InputStreamReader((InputStream) request.getContent()));
            ArrayNode featureArray = (ArrayNode) root.get("features");
            
            ArrayNode coordinates = objectMapper.createArrayNode();
            Iterator<JsonNode> it = featureArray.iterator();
            while(it.hasNext()) {            
                JsonNode node = it.next();
                coordinates = (ArrayNode) node.get("geometry").get("coordinates");
            }
            if (coordinates.size() == 0) {
                return new ResponseEntity<JsonNode>(parcelsArray,HttpStatus.NO_CONTENT);
            }
            String bbox = coordinates.get(0) + "," + coordinates.get(1) + "," + coordinates.get(0) + "," + coordinates.get(1); 
            
            // 2) Grundstücke aus Gebäudeeingangkoordinate ermitteln.
            parcelsArray = getEgrid(new URL(dataServiceUrl + dataProductParcel + "/?bbox=" + bbox));
            return new ResponseEntity<JsonNode>(parcelsArray,HttpStatus.OK);
        }
        return new ResponseEntity<JsonNode>(parcelsArray,HttpStatus.NO_CONTENT);
    }
    
    @RequestMapping(value = "/search/{dataproductId}/bbox/{bbox}", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public ResponseEntity<?> searchByBbox(@PathVariable String dataproductId, @PathVariable String bbox) throws IOException {
        ArrayNode parcelsArray = getEgrid(new URL(dataServiceUrl + dataProductParcel + "/?bbox=" + bbox));
        return new ResponseEntity<JsonNode>(parcelsArray,HttpStatus.OK);
    }
    
    private ArrayNode getEgrid(URL url) throws IOException {
        URLConnection request = url.openConnection();
        request.connect();
        
        JsonNode root = objectMapper.readTree(new InputStreamReader((InputStream) request.getContent()));
        ArrayNode featureArray = (ArrayNode) root.get("features");
        
        ArrayNode parcelsArray = objectMapper.createArrayNode();
        Iterator<JsonNode> it = featureArray.iterator();
        while(it.hasNext()) { 
            JsonNode node = it.next();
            String number = node.get("properties").get("nummer").textValue();
            String egrid = node.get("properties").get("egrid").textValue();
            String type = node.get("properties").get("art_txt").textValue();
            
            ObjectNode parcelNode = objectMapper.createObjectNode();
            parcelNode.put("nummer", number);
            parcelNode.put("egrid", egrid);
            parcelNode.put("art", type);
            parcelsArray.add(parcelNode);
        }
        return parcelsArray;
    }
}
