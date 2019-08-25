package com.gwidgets.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Response;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;

import gwt.material.design.addins.client.autocomplete.MaterialAutoComplete;
import gwt.material.design.addins.client.autocomplete.base.MaterialSuggestionOracle;

public class MyMaterialAutoComplete extends MaterialAutoComplete {

    public MyMaterialAutoComplete() {
        super(new MaterialSuggestionOracle() {
            
            com.google.gwt.http.client.Request request;

            @Override
            public void requestSuggestions(SuggestOracle.Request suggestRequest, SuggestOracle.Callback callback) {                
                Response resp = new Response();

                String baseUrl = "https://geoview.bl.ch/main/wsgi/bl_fulltextsearch?limit=15&query=egr+";
             
                String searchText = suggestRequest.getQuery().replace("(EGRID)", "");
                searchText = searchText.toLowerCase();

                GWT.log("*:"+searchText);        
                
                if (searchText.length() < 3) {
                    GWT.log("kleiner 3");        
                    resp.setSuggestions(null);
                    callback.onSuggestionsReady(suggestRequest, resp);
                    return;
                }

                RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, baseUrl + searchText);
                builder.setHeader("content-type", "application/json");
                        
                try {
                    if (request != null) {
                        request.cancel();
                        GWT.log("CANCEL request: " + request.toString());
                    }
                    request = builder.sendRequest("", new RequestCallback() {
                        
                        @Override
                        public void onResponseReceived(com.google.gwt.http.client.Request request, com.google.gwt.http.client.Response response) {
                            List<EgridSuggestion> list = new ArrayList<>();

                            int statusCode = response.getStatusCode();
                            if (statusCode == com.google.gwt.http.client.Response.SC_OK) {
                                String responseBody = response.getText();
                                //GWT.log(responseBody);
                                
                                JSONObject responseObj = new JSONObject(JsonUtils.safeEval(responseBody));
                                JSONObject rootObj = responseObj.isObject();
                                JSONArray featuresArray = rootObj.get("features").isArray();
                                
//                                List<EgridSuggestion> list = new ArrayList<>();
                                for (int i = 0; i < featuresArray.size(); i++) {
                                    JSONObject properties = featuresArray.get(i).isObject().get("properties").isObject();
                                    
                                    // generisches Suchresultat. Enum (egrid, etc..
                                    Egrid egrid = new Egrid();
                                    egrid.setEgrid(properties.get("label").toString().replace("(EGRID)", "").replaceAll("^.|.$", ""));
                                    egrid.setLabel(properties.get("label").toString().replace("(EGRID)", "").replaceAll("^.|.$", ""));
                                    list.add(new EgridSuggestion(egrid));
//                                    GWT.log(String.valueOf(list.size()));
                                }
                                
                                if (list.isEmpty()) {
                                    GWT.log("RETURNING empty list.");
                                    Response resp = new Response();
                                    resp.setSuggestions(null);
                                    callback.onSuggestionsReady(suggestRequest, resp);
                                    return;
                                }
                                
//                                GWT.log("HAAAALLLLOOO");
//                                GWT.log(builder.getUrl());
                                
//                                        for (EgridSuggestion egrid : list) {
//                                            GWT.log("*"+egrid.getEgrid().getEgrid());
//                                        }
                                resp.setSuggestions(list);
                                callback.onSuggestionsReady(suggestRequest, resp);
                                return;
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
//                GWT.log(String.valueOf(list.size()));
            }
        });
    }
}

