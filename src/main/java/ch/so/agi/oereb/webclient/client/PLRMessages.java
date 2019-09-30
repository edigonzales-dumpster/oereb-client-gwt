package ch.so.agi.oereb.webclient.client;

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
    
    @DefaultMessage("Subunit of Land Register")
    String resultSubunitOfLandRegister();

    @DefaultMessage("Opacity")
    String resultOpacity();

    @DefaultMessage("Close extract")
    String resultCloseTooltip();
    
    @DefaultMessage("Request extract as PDF")
    String resultPDFTooltip();
    
    @DefaultMessage("Type")
    String resultType();
    
    @DefaultMessage("Share")
    String resultShare();
        
    @DefaultMessage("Share in %")
    String resultShareInPercent();
    
    @DefaultMessage("Show full legend")
    String resultShowLegend();

    @DefaultMessage("Hide full legend")
    String resultHideLegend();

    @DefaultMessage("Legal provisions")
    String legalProvisions();
    
    @DefaultMessage("Laws")
    String laws();

    @DefaultMessage("Responsible office")
    String responsibleOffice();
    
    @DefaultMessage("Cadastre authority")
    String plrCadastreAuthority();
}
