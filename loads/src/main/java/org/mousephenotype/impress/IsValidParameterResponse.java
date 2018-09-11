
package org.mousephenotype.impress;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
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
 *         &lt;element name="isValidParameterResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "isValidParameterResult"
})
@XmlRootElement(name = "isValidParameterResponse")
public class IsValidParameterResponse {

    protected boolean isValidParameterResult;

    /**
     * Gets the value of the isValidParameterResult property.
     * 
     */
    public boolean isIsValidParameterResult() {
        return isValidParameterResult;
    }

    /**
     * Sets the value of the isValidParameterResult property.
     * 
     */
    public void setIsValidParameterResult(boolean value) {
        this.isValidParameterResult = value;
    }

}
