package ch.so.agi.oereb.webclient.shared.models.cadastralsurveying;

import com.google.gwt.user.client.rpc.IsSerializable;

import ch.so.agi.oereb.webclient.shared.models.cadastralsurveying.Office;
import ch.so.agi.oereb.webclient.shared.models.cadastralsurveying.RealEstateDPR;

public class Extract implements IsSerializable {
    String extractIdentifier; 
    
    RealEstateDPR realEstate;

    String pdfLink;
    
    Office cadastralSurveyingAuthority;

    public String getExtractIdentifier() {
        return extractIdentifier;
    }

    public void setExtractIdentifier(String extractIdentifier) {
        this.extractIdentifier = extractIdentifier;
    }

    public RealEstateDPR getRealEstate() {
        return realEstate;
    }

    public void setRealEstate(RealEstateDPR realEstate) {
        this.realEstate = realEstate;
    }

    public String getPdfLink() {
        return pdfLink;
    }

    public void setPdfLink(String pdfLink) {
        this.pdfLink = pdfLink;
    }

    public Office getCadastralSurveyingAuthority() {
        return cadastralSurveyingAuthority;
    }

    public void setCadastralSurveyingAuthority(Office cadastralSurveyingAuthority) {
        this.cadastralSurveyingAuthority = cadastralSurveyingAuthority;
    }
}
