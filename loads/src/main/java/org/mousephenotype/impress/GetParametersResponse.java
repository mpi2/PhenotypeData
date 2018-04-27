
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
 *         &lt;element name="getParametersResult" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
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
    "getParametersResult"
})
@XmlRootElement(name = "getParametersResponse")
public class GetParametersResponse {

    @XmlElement(required = true)
    protected Object getParametersResult;

    /**
     * Gets the value of the getParametersResult property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getGetParametersResult() {
        return getParametersResult;
    }

    /**
     * Sets the value of the getParametersResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setGetParametersResult(Object value) {
        this.getParametersResult = value;
    }

}
