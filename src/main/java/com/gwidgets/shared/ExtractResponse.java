package com.gwidgets.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ExtractResponse implements IsSerializable {
    private String egrid;

    public String getEgrid() {
        return egrid;
    }

    public void setEgrid(String egrid) {
        this.egrid = egrid;
    }
}
