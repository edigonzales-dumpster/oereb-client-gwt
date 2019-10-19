package ch.so.agi.oereb.webclient.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsonUtils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.ui.SuggestOracle;

import gwt.material.design.addins.client.autocomplete.base.MaterialSuggestionOracle;


public class SearchOracle extends MaterialSuggestionOracle {
    private String searchServicePath;

    private com.google.gwt.http.client.Request request;

    public SearchOracle(String searchServicePath) {
        this.searchServicePath = searchServicePath;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void requestSuggestions(SuggestOracle.Request suggestRequest, SuggestOracle.Callback callback) {
        Response resp = new Response();        
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

        String searchServiceUrl = GWT.getHostPageBaseURL() + searchServicePath;        
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, searchServiceUrl + searchText);

        // CORS preflight problems with "application/json"
        // Response headers from server seem to be ok.
        // builder.setHeader("content-type", "application/json");
        builder.setHeader("Content-Type", "application/x-www-form-urlencoded");

        try {
            // Verhindert, dass ein älterer Request an den Browser
            // geschickt wird, wenn bereits ein neuerer Request
            // geschickt wurde.
            if (request != null) {
                request.cancel();
            }

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
                        
                        for (int i = 0; i < resultsArray.size(); i++) {
                            JSONObject properties = resultsArray.get(i).isObject().get("feature").isObject();
                            SearchResult searchResult = new SearchResult();
                            searchResult.setDisplay(properties.get("display").isString().stringValue().trim());
                            searchResult.setDataproductId(properties.get("dataproduct_id").isString().stringValue().trim());
                            // FIXME: this seems to be a bug in our data service
                            if (properties.get("feature_id").toString().startsWith("\"")) {
                                searchResult.setFeatureId(properties.get("feature_id").isString().stringValue().trim());
                            } else {
                                searchResult.setFeatureId(properties.get("feature_id").isNumber().toString());
                            }
                            searchResult.setIdFieldName(properties.get("id_field_name").isString().stringValue().trim());

                            list.add(new SearchSuggestion(searchResult));
                        }
                        
                        if (list.isEmpty()) {
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
