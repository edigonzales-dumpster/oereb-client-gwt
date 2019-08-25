package com.gwidgets.client;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;
import com.google.gwt.user.client.ui.TextBox;

public class MySuggestBox extends SuggestBox {

    public MySuggestBox() {
        
        super(new SuggestOracle() {
            @Override
            public void requestSuggestions(Request request, Callback callback) {
                ArrayList<Suggestion> suggestions = new ArrayList<Suggestion>();
                suggestions.add(new MySuggestion("aaa"));
                suggestions.add(new MySuggestion("bbb"));
                suggestions.add(new MySuggestion("ccc"));
                suggestions.add(new MySuggestion("ddd"));

                // https://geoview.bl.ch/main/wsgi/bl_fulltextsearch?_dc=1566232303797&limit=15&query=egr+CH1070080

                RequestBuilder builder = new RequestBuilder(RequestBuilder.GET,
                        "https://geoview.bl.ch/main/wsgi/bl_fulltextsearch?_dc=1566232303797&limit=15&query=egr+CH1070080");
                builder.setHeader("content-type", "application/json");

                try {
                    com.google.gwt.http.client.Request response = builder.sendRequest("CH1070080",
                            new RequestCallback() {
                                @Override
                                public void onResponseReceived(com.google.gwt.http.client.Request request,
                                        com.google.gwt.http.client.Response response) {
                                    int statusCode = response.getStatusCode();

                                    if (statusCode == com.google.gwt.http.client.Response.SC_OK) {
                                        String responseBody = response.getText();
                                        GWT.log(responseBody);
                                    } else {
                                        // do in case of server error
                                        GWT.log("error");

                                    }
                                }

                                @Override
                                public void onError(com.google.gwt.http.client.Request request, Throwable exception) {
                                    // error actually sending the request, never got sent
                                }
                            });
                } catch (RequestException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                Response resp = new Response();
                resp.setSuggestions(suggestions);
                callback.onSuggestionsReady(request, resp);
            }
        }, new TextBox(), new MySuggestionDisplay());
    }

    public static class MySuggestionDisplay extends DefaultSuggestionDisplay {
        @Override
        protected void showSuggestions(SuggestBox suggestBox, Collection<? extends Suggestion> suggestions, boolean isDisplayStringHTML, boolean isAutoSelectEnabled, SuggestionCallback callback) {
            if(suggestBox.getText().length() > 2)
                super.showSuggestions(suggestBox, suggestions, isDisplayStringHTML, isAutoSelectEnabled, callback);
        }
    }

    public static class MySuggestion implements Suggestion {

        private String text;

        public MySuggestion(String text) {
            this.text = text;
        }

        @Override
        public String getDisplayString() {
            return text;
        }

        @Override
        public String getReplacementString() {
            return text;
        }
    }
}

