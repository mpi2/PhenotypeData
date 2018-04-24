
package org.mousephenotype.impress;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="getParameterOntologyOptionsResult" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
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
    "getParameterOntologyOptionsResult"
})
@XmlRootElement(name = "getParameterOntologyOptionsResponse")
public class GetParameterOntologyOptionsResponse {

    @XmlElement(required = true)
    protected Object getParameterOntologyOptionsResult;

    /**
     * Gets the value of the getParameterOntologyOptionsResult property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getGetParameterOntologyOptionsResult() {
        return getParameterOntologyOptionsResult;
    }

    /**
     * Sets the value of the getParameterOntologyOptionsResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setGetParameterOntologyOptionsResult(Object value) {
        this.getParameterOntologyOptionsResult = value;
    }

}
