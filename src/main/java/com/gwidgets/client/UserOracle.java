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
import com.google.gwt.user.client.ui.SuggestOracle;

import gwt.material.design.addins.client.autocomplete.base.MaterialSuggestionOracle;

public class UserOracle extends MaterialSuggestionOracle {
//public class UserOracle extends SuggestOracle{

    private List<User> contacts = new LinkedList<>();

    public void addContacts(List<User> users) {
        contacts.addAll(users);
    }

    @Override
    public void requestSuggestions(SuggestOracle.Request suggestRequest, SuggestOracle.Callback callback) {
        
        String baseUrl = "https://geoview.bl.ch/main/wsgi/bl_fulltextsearch?limit=15&query=egr+";
     
        String searchText = suggestRequest.getQuery().replace("(EGRID)", "");
        searchText = searchText.toLowerCase();

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, baseUrl + searchText);
        builder.setHeader("content-type", "application/json");

        try {
            com.google.gwt.http.client.Request response = builder.sendRequest("",
                    new RequestCallback() {
                        @Override
                        public void onResponseReceived(com.google.gwt.http.client.Request request,
                                com.google.gwt.http.client.Response response) {
                            int statusCode = response.getStatusCode();

                            if (statusCode == com.google.gwt.http.client.Response.SC_OK) {
                                String responseBody = response.getText();
                                //GWT.log(responseBody);
                                
                                JSONObject responseObj = new JSONObject(JsonUtils.safeEval(responseBody));
                                JSONObject rootObj = responseObj.isObject();
                                JSONArray featuresArray = rootObj.get("features").isArray();
                                
                                List<EgridSuggestion> list = new ArrayList<>();
                                for (int i = 0; i < featuresArray.size(); i++) {
                                    JSONObject properties = featuresArray.get(i).isObject().get("properties").isObject();
                                    
                                    // generisches Suchresultat. Enum (egrid, etc..
                                    Egrid egrid = new Egrid();
                                    egrid.setEgrid(properties.get("label").toString().replace("(EGRID)", "").replaceAll("^.|.$", ""));
                                    egrid.setLabel(properties.get("label").toString().replaceAll("^.|.$", ""));
                                    list.add(new EgridSuggestion(egrid));
                                }
                                
                                Response resp = new Response();
                                if (list.isEmpty()) {
                                    resp.setSuggestions(null);
                                    callback.onSuggestionsReady(suggestRequest, resp);
                                    return;
                                }
                                
                                resp.setSuggestions(list);
                                callback.onSuggestionsReady(suggestRequest, resp);
                            } else {
                                GWT.log("error");
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
