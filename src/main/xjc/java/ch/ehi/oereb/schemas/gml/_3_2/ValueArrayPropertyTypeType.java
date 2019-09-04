//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.07.28 at 05:34:43 PM CEST 
//


package ch.ehi.oereb.schemas.gml._3_2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ValueArrayPropertyType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ValueArrayPropertyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;group ref="{http://www.opengis.net/gml/3.2}Value"/>
 *       &lt;/sequence>
 *       &lt;attGroup ref="{http://www.opengis.net/gml/3.2}OwnershipAttributeGroup"/>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ValueArrayPropertyType", propOrder = {
    "abstractValueOrAbstractGeometryOrAbstractTimeObject"
})
public class ValueArrayPropertyTypeType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElementRefs({
        @XmlElementRef(name = "AbstractGeometry", namespace = "http://www.opengis.net/gml/3.2", type = AbstractGeometry.class, required = false),
        @XmlElementRef(name = "AbstractValue", namespace = "http://www.opengis.net/gml/3.2", type = AbstractValue.class, required = false),
        @XmlElementRef(name = "Null", namespace = "http://www.opengis.net/gml/3.2", type = Null.class, required = false),
        @XmlElementRef(name = "AbstractTimeObject", namespace = "http://www.opengis.net/gml/3.2", type = AbstractTimeObject.class, required = false)
    })
    protected List<JAXBElement<?>> abstractValueOrAbstractGeometryOrAbstractTimeObject;
    @XmlAttribute(name = "owns")
    protected java.lang.Boolean owns;

    /**
     * Gets the value of the abstractValueOrAbstractGeometryOrAbstractTimeObject property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the abstractValueOrAbstractGeometryOrAbstractTimeObject property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAbstractValueOrAbstractGeometryOrAbstractTimeObject().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RectifiedGrid }
     * {@link MultiSurface }
     * {@link TimeNode }
     * {@link AbstractRing }
     * {@link TimePeriod }
     * {@link TimeEdge }
     * {@link GeometricComplex }
     * {@link BooleanList }
     * {@link QuantityExtent }
     * {@link CompositeSurface }
     * {@link AbstractGeometricPrimitive }
     * {@link AbstractGeometricAggregate }
     * {@link AbstractTimePrimitive }
     * {@link Category }
     * {@link MultiCurve }
     * {@link AbstractGeometry }
     * {@link CompositeValue }
     * {@link AbstractTimeTopologyPrimitive }
     * {@link AbstractCurve }
     * {@link QuantityList }
     * {@link Tin }
     * {@link Polygon }
     * {@link Grid }
     * {@link AbstractValue }
     * {@link ValueArray }
     * {@link Quantity }
     * {@link CategoryList }
     * {@link CountExtent }
     * {@link CompositeSolid }
     * {@link TriangulatedSurface }
     * {@link LineString }
     * {@link LinearRing }
     * {@link AbstractScalarValue }
     * {@link AbstractSolid }
     * {@link Null }
     * {@link MultiGeometry }
     * {@link OrientableCurve }
     * {@link AbstractSurface }
     * {@link Count }
     * {@link MultiPoint }
     * {@link Curve }
     * {@link Shell }
     * {@link AbstractImplicitGeometry }
     * {@link AbstractScalarValueList }
     * {@link AbstractTimeGeometricPrimitive }
     * {@link Surface }
     * {@link Point }
     * {@link Ring }
     * {@link AbstractTimeComplex }
     * {@link Solid }
     * {@link CompositeCurve }
     * {@link AbstractTimeObject }
     * {@link MultiSolid }
     * {@link TimeTopologyComplex }
     * {@link OrientableSurface }
     * {@link TimeInstant }
     * {@link CategoryExtent }
     * {@link PolyhedralSurface }
     * {@link ch.ehi.oereb.schemas.gml._3_2.Boolean }
     * {@link CountList }
     * 
     * 
     */
    public List<JAXBElement<?>> getAbstractValueOrAbstractGeometryOrAbstractTimeObject() {
        if (abstractValueOrAbstractGeometryOrAbstractTimeObject == null) {
            abstractValueOrAbstractGeometryOrAbstractTimeObject = new ArrayList<JAXBElement<?>>();
        }
        return this.abstractValueOrAbstractGeometryOrAbstractTimeObject;
    }

    /**
     * Gets the value of the owns property.
     * 
     * @return
     *     possible object is
     *     {@link java.lang.Boolean }
     *     
     */
    public boolean isOwns() {
        if (owns == null) {
            return false;
        } else {
            return owns;
        }
    }

    /**
     * Sets the value of the owns property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.lang.Boolean }
     *     
     */
    public void setOwns(java.lang.Boolean value) {
        this.owns = value;
    }

}