package com.gwidgets.shared.models;

import java.util.List;

public class ConcernedTheme extends AbstractTheme {
    private ReferenceWMS referenceWMS;
    
    private List<Object> restrictions; // Restriction: Aussage, Symbol, Anteil, Anteil in %
    
    private String legendAtWeb;
    
    private List<Object> legalProvisions; //LegalProvision:
    
    private List<Object> laws; // Law: (siehe legal provision)
    
    private List<Object> hints;
    
    private List<String> responsibleOffice;
    
    
}
