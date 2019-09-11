package com.gwidgets.shared.models;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ReferenceWMS implements IsSerializable {
    private String baseUrl;
    
    private String layers;
    
    private String imageFormat;
    
    private double layerOpacity;
    
    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getLayers() {
        return layers;
    }

    public void setLayers(String layers) {
        this.layers = layers;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public double getLayerOpacity() {
        return layerOpacity;
    }

    public void setLayerOpacity(double layerOpacity) {
        this.layerOpacity = layerOpacity;
    }
}
