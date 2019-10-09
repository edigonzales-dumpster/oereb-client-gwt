package ch.so.agi.oereb.webclient.server;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {
    Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    @GetMapping("/search/{searchText}")
    public void searchByFTS(@PathVariable String searchText) {
        logger.info("fubar");
        logger.info(searchText);
    }
    
}
