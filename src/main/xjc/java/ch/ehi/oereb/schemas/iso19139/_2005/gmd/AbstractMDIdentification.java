//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.07.28 at 05:34:43 PM CEST 
//


package ch.ehi.oereb.schemas.iso19139._2005.gmd;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class AbstractMDIdentification
    extends JAXBElement<AbstractMDIdentificationType>
{

    protected final static QName NAME = new QName("http://www.isotc211.org/2005/gmd", "AbstractMD_Identification");

    public AbstractMDIdentification(AbstractMDIdentificationType value) {
        super(NAME, ((Class) AbstractMDIdentificationType.class), null, value);
    }

    public AbstractMDIdentification() {
        super(NAME, ((Class) AbstractMDIdentificationType.class), null, null);
    }

}
