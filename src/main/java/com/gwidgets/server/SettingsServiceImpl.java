package com.gwidgets.server;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.gwidgets.shared.SettingsResponse;
import com.gwidgets.shared.SettingsService;

public class SettingsServiceImpl extends RemoteServiceServlet implements SettingsService {

    @Value("${app.searchServiceUrl}")
    private String searchServiceUrl;

    @Override
    public void init() throws ServletException {
         super.init();
         SpringBeanAutowiringSupport.processInjectionBasedOnServletContext(this, getServletContext());
    }
    
    @Override
    public SettingsResponse settingsServer() throws IllegalArgumentException, IOException {
        HashMap<String,String> settings = new HashMap<String,String>();
        
        settings.put("SEARCH_SERVICE_URL", searchServiceUrl);
        
        SettingsResponse response = new SettingsResponse();
        response.setSettings(settings);
        
        return response;
    }
}
