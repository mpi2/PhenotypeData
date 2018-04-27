
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
 *         &lt;element name="getParameterKeysResult" type="{http://www.mousephenotype.org/impress/soap/server}ArrayOfString"/>
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
    "getParameterKeysResult"
})
@XmlRootElement(name = "getParameterKeysResponse")
public class GetParameterKeysResponse {

    @XmlElement(required = true)
    protected ArrayOfString getParameterKeysResult;

    /**
     * Gets the value of the getParameterKeysResult property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfString }
     *     
     */
    public ArrayOfString getGetParameterKeysResult() {
        return getParameterKeysResult;
    }

    /**
     * Sets the value of the getParameterKeysResult property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfString }
     *     
     */
    public void setGetParameterKeysResult(ArrayOfString value) {
        this.getParameterKeysResult = value;
    }

}
