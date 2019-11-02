package ch.so.agi.oereb.webclient.shared.models.plr;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ConcernedTheme extends AbstractTheme implements IsSerializable {
    private ReferenceWMS referenceWMS;
    
    private List<Restriction> restrictions; 
    
    private String legendAtWeb;
    
    private List<Document> legalProvisions; 
    
    private List<Document> laws; 
    
    private List<Document> hints;
    
    private List<Office> responsibleOffice;

    public ReferenceWMS getReferenceWMS() {
        return referenceWMS;
    }

    public void setReferenceWMS(ReferenceWMS referenceWMS) {
        this.referenceWMS = referenceWMS;
    }

    public List<Restriction> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(List<Restriction> restrictions) {
        this.restrictions = restrictions;
    }

    public String getLegendAtWeb() {
        return legendAtWeb;
    }

    public void setLegendAtWeb(String legendAtWeb) {
        this.legendAtWeb = legendAtWeb;
    }

    public List<Document> getLegalProvisions() {
        return legalProvisions;
    }

    public void setLegalProvisions(List<Document> legalProvisions) {
        this.legalProvisions = legalProvisions;
    }

    public List<Document> getLaws() {
        return laws;
    }

    public void setLaws(List<Document> laws) {
        this.laws = laws;
    }

    public List<Document> getHints() {
        return hints;
    }

    public void setHints(List<Document> hints) {
        this.hints = hints;
    }

    public List<Office> getResponsibleOffice() {
        return responsibleOffice;
    }

    public void setResponsibleOffice(List<Office> responsibleOffice) {
        this.responsibleOffice = responsibleOffice;
    }
}
