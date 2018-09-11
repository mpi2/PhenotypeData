
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
 *         &lt;element name="pipelineHasProcedureResult" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
    "pipelineHasProcedureResult"
})
@XmlRootElement(name = "pipelineHasProcedureResponse")
public class PipelineHasProcedureResponse {

    protected boolean pipelineHasProcedureResult;

    /**
     * Gets the value of the pipelineHasProcedureResult property.
     * 
     */
    public boolean isPipelineHasProcedureResult() {
        return pipelineHasProcedureResult;
    }

    /**
     * Sets the value of the pipelineHasProcedureResult property.
     * 
     */
    public void setPipelineHasProcedureResult(boolean value) {
        this.pipelineHasProcedureResult = value;
    }

}
