package ch.so.agi.oereb.webclient.shared.models;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ReferenceWMS implements IsSerializable {
    private String baseUrl;
    
    private String layers;
    
    private String imageFormat;
    
    private double layerOpacity;
    
    private int layerIndex;
    
    private String legendAtWeb;
    
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

    public int getLayerIndex() {
        return layerIndex;
    }

    public void setLayerIndex(int layerIndex) {
        this.layerIndex = layerIndex;
    }

    public String getLegendAtWeb() {
        return legendAtWeb;
    }

    public void setLegendAtWeb(String legendAtWeb) {
        this.legendAtWeb = legendAtWeb;
    }
}
