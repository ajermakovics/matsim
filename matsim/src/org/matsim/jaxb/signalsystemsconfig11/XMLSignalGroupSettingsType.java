//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.03.06 at 03:08:23 PM CET 
//


package org.matsim.jaxb.signalsystemsconfig11;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.matsim.jaxb.Adapter1;


/**
 * <p>Java class for signalGroupSettingsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="signalGroupSettingsType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://www.matsim.org/files/dtd}idRefType">
 *       &lt;sequence>
 *         &lt;element name="roughcast">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="sec" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="dropping">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="sec" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="interimTimeRoughcast" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="sec" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="interimTimeDropping" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="sec" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "signalGroupSettingsType", propOrder = {
    "roughcast",
    "dropping",
    "interimTimeRoughcast",
    "interimTimeDropping"
})
public class XMLSignalGroupSettingsType
    extends XMLIdRefType
{

    @XmlElement(required = true)
    protected XMLSignalGroupSettingsType.XMLRoughcast roughcast;
    @XmlElement(required = true)
    protected XMLSignalGroupSettingsType.XMLDropping dropping;
    protected XMLSignalGroupSettingsType.XMLInterimTimeRoughcast interimTimeRoughcast;
    protected XMLSignalGroupSettingsType.XMLInterimTimeDropping interimTimeDropping;

    /**
     * Gets the value of the roughcast property.
     * 
     * @return
     *     possible object is
     *     {@link XMLSignalGroupSettingsType.XMLRoughcast }
     *     
     */
    public XMLSignalGroupSettingsType.XMLRoughcast getRoughcast() {
        return roughcast;
    }

    /**
     * Sets the value of the roughcast property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLSignalGroupSettingsType.XMLRoughcast }
     *     
     */
    public void setRoughcast(XMLSignalGroupSettingsType.XMLRoughcast value) {
        this.roughcast = value;
    }

    /**
     * Gets the value of the dropping property.
     * 
     * @return
     *     possible object is
     *     {@link XMLSignalGroupSettingsType.XMLDropping }
     *     
     */
    public XMLSignalGroupSettingsType.XMLDropping getDropping() {
        return dropping;
    }

    /**
     * Sets the value of the dropping property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLSignalGroupSettingsType.XMLDropping }
     *     
     */
    public void setDropping(XMLSignalGroupSettingsType.XMLDropping value) {
        this.dropping = value;
    }

    /**
     * Gets the value of the interimTimeRoughcast property.
     * 
     * @return
     *     possible object is
     *     {@link XMLSignalGroupSettingsType.XMLInterimTimeRoughcast }
     *     
     */
    public XMLSignalGroupSettingsType.XMLInterimTimeRoughcast getInterimTimeRoughcast() {
        return interimTimeRoughcast;
    }

    /**
     * Sets the value of the interimTimeRoughcast property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLSignalGroupSettingsType.XMLInterimTimeRoughcast }
     *     
     */
    public void setInterimTimeRoughcast(XMLSignalGroupSettingsType.XMLInterimTimeRoughcast value) {
        this.interimTimeRoughcast = value;
    }

    /**
     * Gets the value of the interimTimeDropping property.
     * 
     * @return
     *     possible object is
     *     {@link XMLSignalGroupSettingsType.XMLInterimTimeDropping }
     *     
     */
    public XMLSignalGroupSettingsType.XMLInterimTimeDropping getInterimTimeDropping() {
        return interimTimeDropping;
    }

    /**
     * Sets the value of the interimTimeDropping property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLSignalGroupSettingsType.XMLInterimTimeDropping }
     *     
     */
    public void setInterimTimeDropping(XMLSignalGroupSettingsType.XMLInterimTimeDropping value) {
        this.interimTimeDropping = value;
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
     *       &lt;attribute name="sec" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class XMLDropping {

        @XmlAttribute(required = true)
        @XmlJavaTypeAdapter(Adapter1 .class)
        @XmlSchemaType(name = "nonNegativeInteger")
        protected Integer sec;

        /**
         * Gets the value of the sec property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public Integer getSec() {
            return sec;
        }

        /**
         * Sets the value of the sec property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSec(Integer value) {
            this.sec = value;
        }

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
     *       &lt;attribute name="sec" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class XMLInterimTimeDropping {

        @XmlAttribute
        @XmlJavaTypeAdapter(Adapter1 .class)
        @XmlSchemaType(name = "nonNegativeInteger")
        protected Integer sec;

        /**
         * Gets the value of the sec property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public int getSec() {
            if (sec == null) {
                return new Adapter1().unmarshal("0");
            } else {
                return sec;
            }
        }

        /**
         * Sets the value of the sec property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSec(Integer value) {
            this.sec = value;
        }

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
     *       &lt;attribute name="sec" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" default="0" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class XMLInterimTimeRoughcast {

        @XmlAttribute
        @XmlJavaTypeAdapter(Adapter1 .class)
        @XmlSchemaType(name = "nonNegativeInteger")
        protected Integer sec;

        /**
         * Gets the value of the sec property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public int getSec() {
            if (sec == null) {
                return new Adapter1().unmarshal("0");
            } else {
                return sec;
            }
        }

        /**
         * Sets the value of the sec property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSec(Integer value) {
            this.sec = value;
        }

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
     *       &lt;attribute name="sec" use="required" type="{http://www.w3.org/2001/XMLSchema}nonNegativeInteger" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class XMLRoughcast {

        @XmlAttribute(required = true)
        @XmlJavaTypeAdapter(Adapter1 .class)
        @XmlSchemaType(name = "nonNegativeInteger")
        protected Integer sec;

        /**
         * Gets the value of the sec property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public Integer getSec() {
            return sec;
        }

        /**
         * Sets the value of the sec property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSec(Integer value) {
            this.sec = value;
        }

    }

}
