package com.gwidgets.shared.models;

import java.util.List;

public class ConcernedTheme extends AbstractTheme {
    private ReferenceWMS referenceWMS;
    
    private List<Restriction> restrictions; 
    
    private String legendAtWeb;
    
    private List<Object> legalProvisions; //LegalProvision:
    
    private List<Object> laws; // Law: (siehe legal provision)
    
    private List<Object> hints;
    
    private List<String> responsibleOffice;
    
    
}
