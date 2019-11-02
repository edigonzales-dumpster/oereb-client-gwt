package ch.so.agi.oereb.webclient.shared.models.plr;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Restriction implements IsSerializable {
    private String information;
    
    private String symbol;
    
    private String symbolRef;
    
    private String typeCode;
    
    private Integer areaShare;
    
    private Integer lengthShare;
    
    private Integer nrOfPoints;
    
    private Double partInPercent;
    
    private String multiPolygonGeometry;
    
    private String multiLineStringGeometry;
    
    private String multiPointGeometry;

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

    public String getSymbolRef() {
        return symbolRef;
    }

    public void setSymbolRef(String symbolRef) {
        this.symbolRef = symbolRef;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public Integer getAreaShare() {
        return areaShare;
    }

    public void setAreaShare(int areaShare) {
        this.areaShare = areaShare;
    }

    public Integer getLengthShare() {
        return lengthShare;
    }

    public void setLengthShare(int lengthShare) {
        this.lengthShare = lengthShare;
    }

    public Integer getNrOfPoints() {
        return nrOfPoints;
    }

    public void setNrOfPoints(int nrOfPoints) {
        this.nrOfPoints = nrOfPoints;
    }

    public Double getPartInPercent() {
        return partInPercent;
    }

    public void setPartInPercent(double partInPercent) {
        this.partInPercent = partInPercent;
    }

    public String getMultiPolygonGeometry() {
        return multiPolygonGeometry;
    }

    public void setMultiPolygonGeometry(String multiPolygonGeometry) {
        this.multiPolygonGeometry = multiPolygonGeometry;
    }

    public String getMultiLineStringGeometry() {
        return multiLineStringGeometry;
    }

    public void setMultiLineStringGeometry(String multiLineStringGeometry) {
        this.multiLineStringGeometry = multiLineStringGeometry;
    }

    public String getMultiPointGeometry() {
        return multiPointGeometry;
    }

    public void setMultiPointGeometry(String multiPointGeometry) {
        this.multiPointGeometry = multiPointGeometry;
    }
    
}
