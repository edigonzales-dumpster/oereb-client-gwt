package com.gwidgets.shared.models;

public class Restriction {
    private String information;
    
    private String symbol;
    
    private String typeCode;
    
    private int areaShare;
    
    private int lengthShare;
    
    private int nrOfPoints;
    
    private double partInPercent;

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public int getAreaShare() {
        return areaShare;
    }

    public void setAreaShare(int areaShare) {
        this.areaShare = areaShare;
    }

    public int getLengthShare() {
        return lengthShare;
    }

    public void setLengthShare(int lengthShare) {
        this.lengthShare = lengthShare;
    }

    public int getNrOfPoints() {
        return nrOfPoints;
    }

    public void setNrOfPoints(int nrOfPoints) {
        this.nrOfPoints = nrOfPoints;
    }

    public double getPartInPercent() {
        return partInPercent;
    }

    public void setPartInPercent(double partInPercent) {
        this.partInPercent = partInPercent;
    }
    
}
