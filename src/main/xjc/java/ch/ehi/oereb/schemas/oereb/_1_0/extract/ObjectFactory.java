//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.07.28 at 05:34:43 PM CEST 
//


package ch.ehi.oereb.schemas.oereb._1_0.extract;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the ch.ehi.oereb.schemas.oereb._1_0.extract package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _GetEGRIDResponseTypeIdentDN_QNAME = new QName("http://schemas.geo.admin.ch/V_D/OeREB/1.0/Extract", "identDN");
    private final static QName _GetEGRIDResponseTypeNumber_QNAME = new QName("http://schemas.geo.admin.ch/V_D/OeREB/1.0/Extract", "number");
    private final static QName _GetEGRIDResponseTypeEgrid_QNAME = new QName("http://schemas.geo.admin.ch/V_D/OeREB/1.0/Extract", "egrid");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: ch.ehi.oereb.schemas.oereb._1_0.extract
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link GetExtractByIdResponseType }
     * 
     */
    public GetExtractByIdResponseType createGetExtractByIdResponseType() {
        return new GetExtractByIdResponseType();
    }

    /**
     * Create an instance of {@link GetExtractByIdResponseType.Embeddable }
     * 
     */
    public GetExtractByIdResponseType.Embeddable createGetExtractByIdResponseTypeEmbeddable() {
        return new GetExtractByIdResponseType.Embeddable();
    }

    /**
     * Create an instance of {@link GetCapabilitiesResponseType }
     * 
     */
    public GetCapabilitiesResponseType createGetCapabilitiesResponseType() {
        return new GetCapabilitiesResponseType();
    }

    /**
     * Create an instance of {@link GetEGRIDResponseType }
     * 
     */
    public GetEGRIDResponseType createGetEGRIDResponseType() {
        return new GetEGRIDResponseType();
    }

    /**
     * Create an instance of {@link GetExtractByIdResponseType.Embeddable.Datasource }
     * 
     */
    public GetExtractByIdResponseType.Embeddable.Datasource createGetExtractByIdResponseTypeEmbeddableDatasource() {
        return new GetExtractByIdResponseType.Embeddable.Datasource();
    }

    /**
     * Create an instance of {@link GetEGRIDResponse }}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.geo.admin.ch/V_D/OeREB/1.0/Extract", name = "GetEGRIDResponse")
    public GetEGRIDResponse createGetEGRIDResponse(GetEGRIDResponseType value) {
        return new GetEGRIDResponse(value);
    }

    /**
     * Create an instance of {@link GetExtractByIdResponse }}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.geo.admin.ch/V_D/OeREB/1.0/Extract", name = "GetExtractByIdResponse")
    public GetExtractByIdResponse createGetExtractByIdResponse(GetExtractByIdResponseType value) {
        return new GetExtractByIdResponse(value);
    }

    /**
     * Create an instance of {@link GetCapabilitiesResponse }}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.geo.admin.ch/V_D/OeREB/1.0/Extract", name = "GetCapabilitiesResponse")
    public GetCapabilitiesResponse createGetCapabilitiesResponse(GetCapabilitiesResponseType value) {
        return new GetCapabilitiesResponse(value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.geo.admin.ch/V_D/OeREB/1.0/Extract", name = "identDN", scope = GetEGRIDResponseType.class)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createGetEGRIDResponseTypeIdentDN(String value) {
        return new JAXBElement<String>(_GetEGRIDResponseTypeIdentDN_QNAME, String.class, GetEGRIDResponseType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.geo.admin.ch/V_D/OeREB/1.0/Extract", name = "number", scope = GetEGRIDResponseType.class)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createGetEGRIDResponseTypeNumber(String value) {
        return new JAXBElement<String>(_GetEGRIDResponseTypeNumber_QNAME, String.class, GetEGRIDResponseType.class, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://schemas.geo.admin.ch/V_D/OeREB/1.0/Extract", name = "egrid", scope = GetEGRIDResponseType.class)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    public JAXBElement<String> createGetEGRIDResponseTypeEgrid(String value) {
        return new JAXBElement<String>(_GetEGRIDResponseTypeEgrid_QNAME, String.class, GetEGRIDResponseType.class, value);
    }

}