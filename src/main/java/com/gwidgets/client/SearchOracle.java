package com.gwidgets.client;

//import gwt.material.design.addins.client.autocomplete.base.MaterialSuggestionOracle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.SuggestOracle;

import gwt.material.design.addins.client.autocomplete.base.MaterialSuggestionOracle;

//https://geo.so.ch/api/search/v2/?&filter=ch.so.agi.av.grundstuecke.rechtskraeftig&limit=10&searchtext=CH111
//https://geo.so.ch/api/search/v2/?searchtext=rötistrasse+4+solo&filter=ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge&limit=20

//https://geo.so.ch/api/search/v2/?&filter=ch.so.agi.av.grundstuecke.rechtskraeftig,ch.so.agi.av.gebaeudeadressen.gebaeudeeingaenge&limit=10&searchtext=111

//https://geo.so.ch/api/data/v1/ch.so.agi.av.grundstuecke.rechtskraeftig/?filter=[["t_id","=",719432577]]
//https://geo.so.ch/api/data/v1/ch.so.agi.av.grundstuecke.rechtskraeftig/?bbox=2624049.91,1236536.43,2624082.627,1236575.8
//https://geo.so.ch/api/data/v1/ch.so.agi.av.grundstuecke.rechtskraeftig/?bbox=2624049.91,1236536.43,2624049.91,1236536.43

/*
 * 1. search_service
 * 2a. falls grundstück -> data_service Grundstücke mit id-Filter
 * 2b. falls adresse -> data_service Adressen mit id-Filter -> bbox der Adressen -> data_service Grundstücke mit bbox-Filter 
 */

public class SearchOracle extends MaterialSuggestionOracle {
    private String searchServiceUrl;

    private com.google.gwt.http.client.Request request;

    public SearchOracle(String searchServiceUrl) {
        this.searchServiceUrl = searchServiceUrl;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void requestSuggestions(SuggestOracle.Request suggestRequest, SuggestOracle.Callback callback) {
        Response resp = new Response();

        String baseUrl = "https://geoview.bl.ch/main/wsgi/bl_fulltextsearch?limit=15&query=egr+";

//        String searchText = suggestRequest.getQuery().replace("(EGRID)", "");
//        searchText = searchText.toLowerCase();
        String searchText = suggestRequest.getQuery().trim().toLowerCase();

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

        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, searchServiceUrl + searchText);
//        JsonpRequestBuilder builder = new JsonpRequestBuilder();

        // CORS preflight problems with "application/json"
        // Response headers seem to be ok.
//        builder.setHeader("content-type", "application/json");
        builder.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            // Verhindert, dass ein älterer Request an den Browser
            // geschickt wird, wenn bereits ein neuerer Request
            // geschicht wurde.
            if (request != null) {
                request.cancel();
                // GWT.log("previous request canceled");
            }

//            builder.requestObject(searchServiceUrl + searchText, new AsyncCallback() {
//                public void onFailure(Throwable caught) {
//                  GWT.log("Couldn't retrieve JSON");
//                }
//
//                @Override
//                public void onSuccess(Object result) {
//                    GWT.log("fubar");
//                }
//            }); 

            request = builder.sendRequest("", new RequestCallback() {

                @Override
                public void onResponseReceived(com.google.gwt.http.client.Request request,
                        com.google.gwt.http.client.Response response) {
                    List<SearchSuggestion> list = new ArrayList<SearchSuggestion>();

                    int statusCode = response.getStatusCode();
                    if (statusCode == com.google.gwt.http.client.Response.SC_OK) {
                        String responseBody = response.getText();

                        JSONObject responseObj = new JSONObject(JsonUtils.safeEval(responseBody));
                        JSONObject rootObj = responseObj.isObject();
                        JSONArray resultsArray = rootObj.get("results").isArray();
//                        GWT.log("resultsArray: " + resultsArray);
                        
                        for (int i = 0; i < resultsArray.size(); i++) {
                            JSONObject properties = resultsArray.get(i).isObject().get("feature").isObject();
//                            GWT.log(properties.toString());
                            SearchResult searchResult = new SearchResult();
                            searchResult.setDisplay(properties.get("display").toString().trim().replaceAll("^.|.$", ""));
                            searchResult.setDataproductId(properties.get("dataproduct_id").toString().trim().replaceAll("^.|.$", ""));
                            searchResult.setFeatureId(properties.get("feature_id").toString().trim());
                            searchResult.setIdFieldName(properties.get("id_field_name").toString().trim().replaceAll("^.|.$", ""));

                            list.add(new SearchSuggestion(searchResult));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
