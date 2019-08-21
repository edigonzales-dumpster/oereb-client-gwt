package com.gwidgets.client;

import java.util.Collection;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestBox.DefaultSuggestionDisplay;
import com.google.gwt.user.client.ui.SuggestBox.SuggestionCallback;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class CustomSuggestionDisplay extends DefaultSuggestionDisplay {
    @Override
    protected void showSuggestions(SuggestBox suggestBox, Collection< ? extends Suggestion> suggestions, boolean isDisplayStringHTML, boolean isAutoSelectEnabled, SuggestionCallback callback) {
        super.showSuggestions( suggestBox, suggestions, isDisplayStringHTML, isAutoSelectEnabled, callback );
        getPopupPanel().setWidth( ( suggestBox.getElement().getAbsoluteRight() - suggestBox.getAbsoluteLeft() ) + Unit.PX.getType() );
    }
}
