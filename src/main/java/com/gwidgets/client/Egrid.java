package com.gwidgets.client;

import java.io.Serializable;

public class Egrid implements Serializable {
    private String egrid;
    private String label;
    
    public String getEgrid() {
        return egrid;
    }
    public void setEgrid(String egrid) {
        this.egrid = egrid;
    }
    public String getLabel() {
        return label;
    }
    public void setLabel(String label) {
        this.label = label;
    }
}
