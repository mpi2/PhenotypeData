
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
 *         &lt;element name="isValidProcedureResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "isValidProcedureResult"
})
@XmlRootElement(name = "isValidProcedureResponse")
public class IsValidProcedureResponse {

    protected boolean isValidProcedureResult;

    /**
     * Gets the value of the isValidProcedureResult property.
     * 
     */
    public boolean isIsValidProcedureResult() {
        return isValidProcedureResult;
    }

    /**
     * Sets the value of the isValidProcedureResult property.
     * 
     */
    public void setIsValidProcedureResult(boolean value) {
        this.isValidProcedureResult = value;
    }

}
