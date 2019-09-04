//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.07.28 at 05:34:43 PM CEST 
//


package ch.ehi.oereb.schemas.gml._3_2;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AbstractGriddedSurfaceType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractGriddedSurfaceType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.opengis.net/gml/3.2}AbstractParametricCurveSurfaceType">
 *       &lt;sequence>
 *         &lt;group ref="{http://www.opengis.net/gml/3.2}PointGrid"/>
 *       &lt;/sequence>
 *       &lt;attribute name="rows" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *       &lt;attribute name="columns" type="{http://www.w3.org/2001/XMLSchema}integer" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractGriddedSurfaceType", propOrder = {
    "arows"
})
@XmlSeeAlso({
    SphereTypeType.class,
    CylinderTypeType.class,
    ConeTypeType.class
})
public abstract class AbstractGriddedSurfaceTypeType
    extends AbstractParametricCurveSurfaceTypeType
    implements Serializable
{

    private final static long serialVersionUID = 1L;
    @XmlElement(name = "rows", required = true)
    protected AbstractGriddedSurfaceTypeType.Rows arows;
    @XmlAttribute(name = "rows")
    protected BigInteger rows;
    @XmlAttribute(name = "columns")
    protected BigInteger columns;

    /**
     * Gets the value of the arows property.
     * 
     * @return
     *     possible object is
     *     {@link AbstractGriddedSurfaceTypeType.Rows }
     *     
     */
    public AbstractGriddedSurfaceTypeType.Rows getArows() {
        return arows;
    }

    /**
     * Sets the value of the arows property.
     * 
     * @param value
     *     allowed object is
     *     {@link AbstractGriddedSurfaceTypeType.Rows }
     *     
     */
    public void setArows(AbstractGriddedSurfaceTypeType.Rows value) {
        this.arows = value;
    }

    /**
     * Gets the value of the rows property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRows() {
        return rows;
    }

    /**
     * Sets the value of the rows property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRows(BigInteger value) {
        this.rows = value;
    }

    /**
     * Gets the value of the columns property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getColumns() {
        return columns;
    }

    /**
     * Sets the value of the columns property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setColumns(BigInteger value) {
        this.columns = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Row" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;group ref="{http://www.opengis.net/gml/3.2}geometricPositionListGroup"/>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "row"
    })
    public static class Rows
        implements Serializable
    {

        private final static long serialVersionUID = 1L;
        @XmlElement(name = "Row", required = true)
        protected List<AbstractGriddedSurfaceTypeType.Rows.Row> row;

        /**
         * Gets the value of the row property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the row property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRow().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AbstractGriddedSurfaceTypeType.Rows.Row }
         * 
         * 
         */
        public List<AbstractGriddedSurfaceTypeType.Rows.Row> getRow() {
            if (row == null) {
                row = new ArrayList<AbstractGriddedSurfaceTypeType.Rows.Row>();
            }
            return this.row;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;group ref="{http://www.opengis.net/gml/3.2}geometricPositionListGroup"/>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "posList",
            "geometricPositionGroup"
        })
        public static class Row
            implements Serializable
        {

            private final static long serialVersionUID = 1L;
            @XmlElementRef(name = "posList", namespace = "http://www.opengis.net/gml/3.2", type = PosList.class, required = false)
            protected PosList posList;
            @XmlElementRefs({
                @XmlElementRef(name = "pos", namespace = "http://www.opengis.net/gml/3.2", type = Pos.class, required = false),
                @XmlElementRef(name = "pointProperty", namespace = "http://www.opengis.net/gml/3.2", type = PointProperty.class, required = false)
            })
            protected List<JAXBElement<? extends Serializable>> geometricPositionGroup;

            /**
             * Gets the value of the posList property.
             * 
             * @return
             *     possible object is
             *     {@link PosList }
             *     
             */
            public PosList getPosList() {
                return posList;
            }

            /**
             * Sets the value of the posList property.
             * 
             * @param value
             *     allowed object is
             *     {@link PosList }
             *     
             */
            public void setPosList(PosList value) {
                this.posList = value;
            }

            /**
             * Gets the value of the geometricPositionGroup property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the geometricPositionGroup property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getGeometricPositionGroup().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link Pos }
             * {@link PointProperty }
             * 
             * 
             */
            public List<JAXBElement<? extends Serializable>> getGeometricPositionGroup() {
                if (geometricPositionGroup == null) {
                    geometricPositionGroup = new ArrayList<JAXBElement<? extends Serializable>>();
                }
                return this.geometricPositionGroup;
            }

        }

    }

}