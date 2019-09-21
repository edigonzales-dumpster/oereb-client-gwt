package com.gwidgets.client;

import com.google.gwt.i18n.client.Messages;

public interface PLRMessages extends Messages {
    @DefaultMessage("Concerned Themes")
    String concernedThemes();
    
    @DefaultMessage("Not concerned Themes")
    String notConcernedThemes();
    
    @DefaultMessage("Themes without data")
    String themesWithoutData();
    
    @DefaultMessage("General and legal information")
    String generalInformation();
    
    @DefaultMessage("Search: Real estates and addresses")
    String searchPlaceholder(); 

    @DefaultMessage("Real estate {0} in {1}")
    String resultHeader(String number, String municipality);
    
    @DefaultMessage("Area")
    String resultArea();
}
