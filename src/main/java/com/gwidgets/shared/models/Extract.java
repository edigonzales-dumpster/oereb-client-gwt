package com.gwidgets.shared.models;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Extract implements IsSerializable {
    String extractIdentifier;
    
    ReferenceWMS referenceWMS;
    
    String geometry;

    public String getExtractIdentifier() {
        return extractIdentifier;
    }

    public void setExtractIdentifier(String extractIdentifier) {
        this.extractIdentifier = extractIdentifier;
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
