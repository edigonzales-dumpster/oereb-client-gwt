package com.gwidgets.shared.models;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Extract implements IsSerializable {
    String extractIdentifier;

    public String getExtractIdentifier() {
        return extractIdentifier;
    }

    public void setExtractIdentifier(String extractIdentifier) {
        this.extractIdentifier = extractIdentifier;
    }
}
