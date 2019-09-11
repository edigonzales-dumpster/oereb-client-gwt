package com.gwidgets.client;

import java.io.Serializable;

public class SearchResult implements Serializable {
    private String display;
    
    private String dataproductId;
    
    private String featureId;
    
    private String idFieldName;
    
    public String getDisplay() {
        return display;
    }
    public void setDisplay(String display) {
        this.display = display;
    }
    public String getDataproductId() {
        return dataproductId;
    }
    public void setDataproductId(String dataproductId) {
        this.dataproductId = dataproductId;
    }
    public String getFeatureId() {
        return featureId;
    }
    public void setFeatureId(String featureId) {
        this.featureId = featureId;
    }
    public String getIdFieldName() {
        return idFieldName;
    }
    public void setIdFieldName(String idFieldName) {
        this.idFieldName = idFieldName;
    }
}
