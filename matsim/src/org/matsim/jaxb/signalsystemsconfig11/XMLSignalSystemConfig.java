//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-520 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2009.03.06 at 03:08:23 PM CET 
//


package org.matsim.jaxb.signalsystemsconfig11;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *          This is the root element for configuration of the traffic light system.
 *       
 * 
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="signalSystemConfiguration" type="{http://www.matsim.org/files/dtd}signalSystemConfigurationType" maxOccurs="unbounded" minOccurs="0"/>
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
    "signalSystemConfiguration"
})
@XmlRootElement(name = "signalSystemConfig")
public class XMLSignalSystemConfig {

    protected List<XMLSignalSystemConfigurationType> signalSystemConfiguration;

    /**
     * Gets the value of the signalSystemConfiguration property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the signalSystemConfiguration property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSignalSystemConfiguration().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link XMLSignalSystemConfigurationType }
     * 
     * 
     */
    public List<XMLSignalSystemConfigurationType> getSignalSystemConfiguration() {
        if (signalSystemConfiguration == null) {
            signalSystemConfiguration = new ArrayList<XMLSignalSystemConfigurationType>();
        }
        return this.signalSystemConfiguration;
    }

}
