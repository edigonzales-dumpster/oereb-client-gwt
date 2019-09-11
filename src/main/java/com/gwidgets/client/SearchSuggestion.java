package com.gwidgets.client;

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class SearchSuggestion implements Suggestion {
    private SearchResult searchResult;
    
    public SearchSuggestion(SearchResult searchResult) {
        this.searchResult = searchResult;
    }
    
    @Override
    public String getDisplayString() {
        return getReplacementString();
    }

    @Override
    public String getReplacementString() {
        return searchResult.getDisplay();
    }
    
    public SearchResult getSearchResult() {
        return searchResult;
    }
}
