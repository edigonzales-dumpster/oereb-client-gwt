package ch.so.agi.oereb.webclient.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import ch.so.agi.oereb.webclient.shared.SettingsResponse;
import ch.so.agi.oereb.webclient.shared.SettingsService;

public class SettingsServiceImpl extends RemoteServiceServlet implements SettingsService {

    @Value("${app.oerebWebServiceUrlClient}")
    private String oerebWebServiceUrl;

    @Value("${app.searchServiceUrl}")
    private String searchServiceUrl;
    
    @Value("${app.dataServiceUrl}")
    private String dataServiceUrl;

    @Value("${app.backgroundWmtsUrl}")
    private String backgroundWmtsUrl;

    @Value("${app.backgroundWmtsLayer}")
    private String backgroundWmtsLayer;

    @Value("${app.availabilityWmsUrl}")
    private String availabilityWmsUrl;

    @Value("${app.availabilityWmsLayers}")
    private String availabilityWmsLayers;

    @Value("#{${app.wmsHostMapping}}")
    private HashMap<String, String> wmsHostMapping;

    @Override
    public void init() throws ServletException {
         super.init();
         SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }
    
    @Override
    public SettingsResponse settingsServer() throws IllegalArgumentException, IOException {
        HashMap<String,Object> settings = new HashMap<String,Object>();
        
        settings.put("OEREB_SERVICE_URL", oerebWebServiceUrl);
        settings.put("SEARCH_SERVICE_URL", searchServiceUrl);
        settings.put("DATA_SERVICE_URL", dataServiceUrl);
        settings.put("WMS_HOST_MAPPING", wmsHostMapping);
        settings.put("AVAILABILITY_WMS_URL", availabilityWmsUrl);
        settings.put("AVAILABILITY_WMS_LAYERS", availabilityWmsLayers);
        settings.put("BACKGROUND_WMTS_URL", backgroundWmtsUrl);
        settings.put("BACKGROUND_WMTS_LAYER", backgroundWmtsLayer);

        SettingsResponse response = new SettingsResponse();
        response.setSettings(settings);
        
        return response;
    }
}
