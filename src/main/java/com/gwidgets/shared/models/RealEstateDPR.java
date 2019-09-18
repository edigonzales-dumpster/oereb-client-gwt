package com.gwidgets.shared.models;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gwt.user.client.rpc.IsSerializable;

public class RealEstateDPR implements IsSerializable {
    private String realEstateType;
    
    private String number;
    
    private String identND;
    
    private String egrid;
    
    private String canton;
    
    private String municipality;
    
    private String subunitOfLandRegister;
    
    private int fosnNr;
    
    private int landRegistryArea;
    
    // Wkt representation
    private String limit;
    
    private ArrayList<ThemeWithoutData> themesWithoutData;
    
    private ArrayList<NotConcernedTheme> notConcernedThemes;
    
    private ArrayList<ConcernedTheme> concernedThemes;

    public String getRealEstateType() {
        return realEstateType;
    }

    public void setRealEstateType(String realEstateType) {
        this.realEstateType = realEstateType;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getIdentND() {
        return identND;
    }

    public void setIdentND(String identND) {
        this.identND = identND;
    }

    public String getEgrid() {
        return egrid;
    }

    public void setEgrid(String egrid) {
        this.egrid = egrid;
    }

    public String getCanton() {
        return canton;
    }

    public void setCanton(String canton) {
        this.canton = canton;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getSubunitOfLandRegister() {
        return subunitOfLandRegister;
    }

    public void setSubunitOfLandRegister(String subunitOfLandRegister) {
        this.subunitOfLandRegister = subunitOfLandRegister;
    }

    public int getFosnNr() {
        return fosnNr;
    }

    public void setFosnNr(int fosnNr) {
        this.fosnNr = fosnNr;
    }

    public int getLandRegistryArea() {
        return landRegistryArea;
    }

    public void setLandRegistryArea(int landRegistryArea) {
        this.landRegistryArea = landRegistryArea;
    }

    public String getLimit() {
        return limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public ArrayList<ThemeWithoutData> getThemesWithoutData() {
        return themesWithoutData;
    }

    public void setThemesWithoutData(ArrayList<ThemeWithoutData> themesWithoutData) {
        this.themesWithoutData = themesWithoutData;
    }

    public ArrayList<NotConcernedTheme> getNotConcernedThemes() {
        return notConcernedThemes;
    }

    public void setNotConcernedThemes(ArrayList<NotConcernedTheme> notConcernedThemes) {
        this.notConcernedThemes = notConcernedThemes;
    }

    public ArrayList<ConcernedTheme> getConcernedThemes() {
        return concernedThemes;
    }

    public void setConcernedThemes(ArrayList<ConcernedTheme> concernedThemes) {
        this.concernedThemes = concernedThemes;
    }
}
