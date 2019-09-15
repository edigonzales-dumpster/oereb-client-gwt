package com.gwidgets.shared.models;

import java.net.URL;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Document implements IsSerializable {
    private String title;
    
    private String abbreviation;
    
    private String textAtWeb;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public String getTextAtWeb() {
        return textAtWeb;
    }

    public void setTextAtWeb(String textAtWeb) {
        this.textAtWeb = textAtWeb;
    }
}
