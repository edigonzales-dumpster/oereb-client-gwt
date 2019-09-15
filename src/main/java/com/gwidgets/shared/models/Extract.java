package com.gwidgets.shared.models;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Extract implements IsSerializable {
    String extractIdentifier; 
    
    RealEstateDPR realEstate;

    String pdfLink;
    
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
}
