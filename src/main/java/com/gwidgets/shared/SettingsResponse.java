package com.gwidgets.shared;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SettingsResponse implements IsSerializable {    
    private HashMap<String,String> settings;

    public HashMap<String, String> getSettings() {
        return settings;
    }

    public void setSettings(HashMap<String, String> settings) {
        this.settings = settings;
    } 
}
