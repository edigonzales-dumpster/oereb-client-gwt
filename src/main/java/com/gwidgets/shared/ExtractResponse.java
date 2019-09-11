package com.gwidgets.shared;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.gwidgets.shared.models.Extract;

public class ExtractResponse implements IsSerializable {
    private Extract extract;
    
    private String egrid; // delete
    
    private String testWmsUrl; // delete

    public String getEgrid() {
        return egrid;
    }

    public void setEgrid(String egrid) {
        this.egrid = egrid;
    }

    public Extract getExtract() {
        return extract;
    }

    public void setExtract(Extract extract) {
        this.extract = extract;
    }

    public String getTestWmsUrl() {
        return testWmsUrl;
    }

    public void setTestWmsUrl(String testWmsUrl) {
        this.testWmsUrl = testWmsUrl;
    }
}
