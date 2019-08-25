package com.gwidgets.client;

//import gwt.material.design.addins.client.autocomplete.base.MaterialSuggestionOracle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.SuggestOracle;

import gwt.material.design.addins.client.autocomplete.base.MaterialSuggestionOracle;

public class UserOracle extends MaterialSuggestionOracle {
    
    com.google.gwt.http.client.Request request;
    
    @Override
    public void requestSuggestions(SuggestOracle.Request suggestRequest, SuggestOracle.Callback callback) {                
        Response resp = new Response();

        String baseUrl = "https://geoview.bl.ch/main/wsgi/bl_fulltextsearch?limit=15&query=egr+";
     
        String searchText = suggestRequest.getQuery().replace("(EGRID)", "");
        searchText = searchText.toLowerCase();

        // Es wird erst bei mehr als 2 Zeichen gesucht.
        // Verhindert ebenfalls folgenden Use Case: Der
        // Benutzer löscht alles mit der Backspace-Taste.
        // Der letzte Request (mit nur einem Zeichen) führt
        // zu einem Resultat, dass auch dargestellt wird im
        // Browser. Für dieses Problem habe ich keine andere
        // Lösung gefunden.
        if (searchText.length() < 3) {
            resp.setSuggestions(null);
            callback.onSuggestionsReady(suggestRequest, resp);
            return;
        }
        
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, baseUrl + searchText);
        builder.setHeader("content-type", "application/json");
                
        try {
            // Verhindert, dass ein älterer Request an den Browser
            // geschickt wird, wenn bereits ein neuerer Request
            // geschicht wurde.
            if (request != null) {
                request.cancel();
                GWT.log("previous request canceled");
            }
            request = builder.sendRequest("", new RequestCallback() {
                
                @Override
                public void onResponseReceived(com.google.gwt.http.client.Request request, com.google.gwt.http.client.Response response) {
                    List<EgridSuggestion> list = new ArrayList<>();

                    int statusCode = response.getStatusCode();
                    if (statusCode == com.google.gwt.http.client.Response.SC_OK) {
                        String responseBody = response.getText();
                        
                        JSONObject responseObj = new JSONObject(JsonUtils.safeEval(responseBody));
                        JSONObject rootObj = responseObj.isObject();
                        JSONArray featuresArray = rootObj.get("features").isArray();
                        
                        for (int i = 0; i < featuresArray.size(); i++) {
                            JSONObject properties = featuresArray.get(i).isObject().get("properties").isObject();
                            
                            // generisches Suchresultat. Enum (egrid, etc..
                            Egrid egrid = new Egrid();
                            egrid.setEgrid(properties.get("label").toString().replace("(EGRID)", "").replaceAll("^.|.$", ""));
                            egrid.setLabel(properties.get("label").toString().replace("(EGRID)", "").replaceAll("^.|.$", ""));
                            list.add(new EgridSuggestion(egrid));
                        }
                        
                        if (list.isEmpty()) {
                            GWT.log("Nothing found: returning empty list.");
                            resp.setSuggestions(null);
                            callback.onSuggestionsReady(suggestRequest, resp);
                            return;
                        }
                        
                        resp.setSuggestions(list);
                        callback.onSuggestionsReady(suggestRequest, resp);
                        return;
                    } else {
                        GWT.log("error from request");
                        GWT.log(String.valueOf(statusCode));
                        GWT.log(response.getStatusText());
                    }
                }

                @Override
                public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
                       GWT.log("error actually sending the request, never got sent");
                } 
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }
}
