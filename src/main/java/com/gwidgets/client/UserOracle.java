package com.gwidgets.client;

//import gwt.material.design.addins.client.autocomplete.base.MaterialSuggestionOracle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
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
        
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, "https://geoview.bl.ch/main/wsgi/bl_fulltextsearch?_dc=1566232303797&limit=15&query=egr+CH1070080");
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
                                //GWT.log(responseBody);
                            } else {
                                GWT.log("error");
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

        Response resp = new Response();
        if(contacts.isEmpty()){
            callback.onSuggestionsReady(suggestRequest, resp);
            return;
        }
        String text = suggestRequest.getQuery();
        text = text.toLowerCase();
        GWT.log(text);

        List<UserSuggestion> list = new ArrayList<>();

        for(User contact : contacts){
            if(contact.getName().toLowerCase().contains(text)){
                list.add(new UserSuggestion(contact));
            }
        }
        resp.setSuggestions(list);
        callback.onSuggestionsReady(suggestRequest, resp);
    }
}
