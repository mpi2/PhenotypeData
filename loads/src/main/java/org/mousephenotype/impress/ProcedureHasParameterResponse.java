
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
 *         &lt;element name="procedureHasParameterResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "procedureHasParameterResult"
})
@XmlRootElement(name = "procedureHasParameterResponse")
public class ProcedureHasParameterResponse {

    protected boolean procedureHasParameterResult;

    /**
     * Gets the value of the procedureHasParameterResult property.
     * 
     */
    public boolean isProcedureHasParameterResult() {
        return procedureHasParameterResult;
    }

    /**
     * Sets the value of the procedureHasParameterResult property.
     * 
     */
    public void setProcedureHasParameterResult(boolean value) {
        this.procedureHasParameterResult = value;
    }

}
