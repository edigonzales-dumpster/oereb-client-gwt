package com.gwidgets.shared.models;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Extract implements IsSerializable {
    String extractIdentifier; 
    
    RealEstateDPR realEstate;
        
    ReferenceWMS referenceWMS; // delete
    
    String geometry; // delete

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

    public ReferenceWMS getReferenceWMS() {
        return referenceWMS;
    }

    public void setReferenceWMS(ReferenceWMS referenceWMS) {
        this.referenceWMS = referenceWMS;
    }

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }
}
