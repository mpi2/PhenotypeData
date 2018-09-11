
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
 *         &lt;element name="getPipelineResult" type="{http://www.w3.org/2001/XMLSchema}anyType"/>
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
    "getPipelineResult"
})
@XmlRootElement(name = "getPipelineResponse")
public class GetPipelineResponse {

    @XmlElement(required = true)
    protected Object getPipelineResult;

    /**
     * Gets the value of the getPipelineResult property.
     * 
     * @return
     *     possible object is
     *     {@link Object }
     *     
     */
    public Object getGetPipelineResult() {
        return getPipelineResult;
    }

    /**
     * Sets the value of the getPipelineResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link Object }
     *     
     */
    public void setGetPipelineResult(Object value) {
        this.getPipelineResult = value;
    }

}
