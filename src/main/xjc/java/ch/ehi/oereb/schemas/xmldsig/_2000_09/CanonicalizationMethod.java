//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.07.28 at 05:34:43 PM CEST 
//


package ch.ehi.oereb.schemas.xmldsig._2000_09;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class CanonicalizationMethod
    extends JAXBElement<CanonicalizationMethodType>
{

    protected final static QName NAME = new QName("http://www.w3.org/2000/09/xmldsig#", "CanonicalizationMethod");

    public CanonicalizationMethod(CanonicalizationMethodType value) {
        super(NAME, ((Class) CanonicalizationMethodType.class), null, value);
    }

    public CanonicalizationMethod() {
        super(NAME, ((Class) CanonicalizationMethodType.class), null, null);
    }

}
