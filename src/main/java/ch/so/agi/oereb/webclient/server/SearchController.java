package ch.so.agi.oereb.webclient.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

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
    
    @RequestMapping(value = "/search/{searchText}", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public String searchByFTS(@PathVariable String searchText) throws IOException {
        logger.info("searchText: " + searchText);
        
        URL url = new URL(searchServiceUrl+searchText);
        URLConnection request = url.openConnection();
        request.connect();
        
        JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));                
        return root.toString();  
    }
    
    // http://localhost:8080/search/ch.so.agi.av.grundstuecke.rechtskraeftig/t_id/771899418
    // -> https://geo.so.ch/api/data/v1/ch.so.agi.av.grundstuecke.rechtskraeftig/?filter=[[%22t_id%22,%22=%22,771899418]]
    // http://localhost:8080/search/ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge/t_id/771029388
    // -> https://geo.so.ch/api/data/v1/ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge/?filter=[["t_id","=","771029388"]]
    // http://localhost:8080/search/ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge/t_id/771029417
    // -> https://geo.so.ch/api/data/v1/ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge/?filter=[["t_id","=","771029417"]]
    @RequestMapping(value = "/search/{dataproductId}/{idFieldName}/{tid}", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public String searchById(@PathVariable String dataproductId, @PathVariable String idFieldName, @PathVariable String tid) throws IOException {
        JsonArray parcelsArray = new JsonArray();
        if (dataproductId.equalsIgnoreCase(dataProductParcel)) {
            URL url = new URL(dataServiceUrl + dataproductId + "/?filter=[[\""+idFieldName+"\",\"=\","+tid+"]]");
            parcelsArray = getEgrid(url);
            return parcelsArray.toString();
        } else if (dataproductId.equalsIgnoreCase(dataProductAddress)) {
            // 1) Koordinate des Einganges ermitteln.
            URL url = new URL(dataServiceUrl + dataproductId + "/?filter=[[\""+idFieldName+"\",\"=\","+tid+"]]");
            URLConnection request = url.openConnection();
            request.connect();

            JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
            JsonArray featureArray = root.getAsJsonObject().get("features").getAsJsonArray();  
            
            JsonArray coordinates = new JsonArray();
            Iterator<JsonElement> it = featureArray.iterator();
            while(it.hasNext()) {            
                JsonElement ele = it.next();
                logger.info(ele.getAsJsonObject().get("geometry").getAsJsonObject().get("coordinates").getAsJsonArray().toString());
                coordinates = ele.getAsJsonObject().get("geometry").getAsJsonObject().get("coordinates").getAsJsonArray();
            }
            String bbox = coordinates.get(0) + "," + coordinates.get(1) + "," + coordinates.get(0) + "," + coordinates.get(1); 
            
            // 2) Grundstücke aus Gebäudeeingangkoordinate ermitteln.
            parcelsArray = getEgrid(new URL(dataServiceUrl + dataProductParcel + "/?bbox=" + bbox));
            return parcelsArray.toString();
        }
        return parcelsArray.toString();
    }
    
    // http://localhost:8080/search/ch.so.agi.av.grundstuecke.rechtskraeftig/bbox/2600467.304,1215411.564,2600467.304,1215411.564
    // -> https://geo.so.ch/api/data/v1/ch.so.agi.av.grundstuecke.rechtskraeftig/?bbox=2600467.304,1215411.564,2600467.304,1215411.564
    @RequestMapping(value = "/search/{dataproductId}/bbox/{bbox}", produces = {MediaType.APPLICATION_JSON_VALUE}, method = RequestMethod.GET)
    public String searchByBbox(@PathVariable String dataproductId, @PathVariable String bbox) throws IOException {
        JsonArray parcelsArray = getEgrid(new URL(dataServiceUrl + dataProductParcel + "/?bbox=" + bbox));
        return parcelsArray.toString();
    }
    
    private JsonArray getEgrid(URL url) throws IOException {
        URLConnection request = url.openConnection();
        request.connect();
        
        JsonElement root = JsonParser.parseReader(new InputStreamReader((InputStream) request.getContent()));
        JsonArray featureArray = root.getAsJsonObject().get("features").getAsJsonArray();  
        
        JsonArray parcelsArray = new JsonArray();
        Iterator<JsonElement> it = featureArray.iterator();
        while(it.hasNext()) {            
            JsonElement ele = it.next();
            String number = ele.getAsJsonObject().get("properties").getAsJsonObject().get("nummer").getAsString();
            String egrid = ele.getAsJsonObject().get("properties").getAsJsonObject().get("egrid").getAsString();
            String type = ele.getAsJsonObject().get("properties").getAsJsonObject().get("art_txt").getAsString();

            JsonObject parcelObj = new JsonObject();
            parcelObj.add("nummer", new JsonPrimitive(number));
            parcelObj.add("egrid", new JsonPrimitive(egrid));
            parcelObj.add("art", new JsonPrimitive(type));
            parcelsArray.add(parcelObj);
        }
        return parcelsArray;
    }
}
