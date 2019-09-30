package ch.so.agi.oereb.webclient.client;

import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

public class EgridSuggestion implements Suggestion {

    private Egrid egrid;
    
    public EgridSuggestion(Egrid egrid) {
        this.egrid = egrid;
    }
    
    @Override
    public String getDisplayString() {
        return getReplacementString();
    }

    @Override
    public String getReplacementString() {
        return egrid.getLabel();
    }
    
    public Egrid getEgrid() {
        return egrid;
    }

}
