package com.gwidgets.client;

//import gwt.material.design.addins.client.autocomplete.base.MaterialSuggestionOracle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.user.client.ui.SuggestOracle;

//public class UserOracle extends MaterialSuggestionOracle{
public class UserOracle extends SuggestOracle{

    private List<User> contacts = new LinkedList<>();

    public void addContacts(List<User> users) {
        contacts.addAll(users);
    }

    @Override
    public void requestSuggestions(Request request, Callback callback) {
        Response resp = new Response();
        if(contacts.isEmpty()){
            callback.onSuggestionsReady(request, resp);
            return;
        }
        String text = request.getQuery();
        text = text.toLowerCase();

        List<UserSuggestion> list = new ArrayList<>();

        for(User contact : contacts){
            if(contact.getName().toLowerCase().contains(text)){
                list.add(new UserSuggestion(contact));
            }
        }
        resp.setSuggestions(list);
        callback.onSuggestionsReady(request, resp);
    }
}
