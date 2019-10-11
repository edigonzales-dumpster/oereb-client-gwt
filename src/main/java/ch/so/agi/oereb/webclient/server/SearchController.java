package ch.so.agi.oereb.webclient.server;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import ch.so.agi.oereb.webclient.server.models.Result;
import ch.so.agi.oereb.webclient.server.models.SearchServiceResponse;


@RestController
public class SearchController {
    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @GetMapping("/search/{searchText}")
    public List<Result> searchByFTS(@PathVariable String searchText) {
        logger.info(searchText);
                
        RestTemplate restTemplate = new RestTemplate();
        SearchServiceResponse searchServiceResponse = restTemplate.getForObject("https://geo.so.ch/api/search/v2/?filter=ch.so.agi.av.grundstuecke.rechtskraeftig,ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge&limit=10&searchtext="+searchText, SearchServiceResponse.class);
        logger.info(searchServiceResponse.getResults().get(0).getFeature().getDisplay().toString());
        return searchServiceResponse.getResults();  
    }
    
    //http://localhost:8080/search/ch.so.agi.av.grundstuecke.rechtskraeftig/t_id/767109749
    @GetMapping("/search/{dataproductId}/{idFieldName}/{tid}")
    public void searchById(@PathVariable String dataproductId, @PathVariable String idFieldName, @PathVariable String tid) {
        logger.info(dataproductId);
        logger.info(idFieldName);
        logger.info(tid);

    }

    
}
