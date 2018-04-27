
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
 *         &lt;element name="procedureKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pipelineKey" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
    "procedureKey",
    "pipelineKey"
})
@XmlRootElement(name = "getProcedure")
public class GetProcedure {

    @XmlElement(required = true, nillable = true)
    protected String procedureKey;
    @XmlElement(required = true, nillable = true)
    protected String pipelineKey;

    /**
     * Gets the value of the procedureKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcedureKey() {
        return procedureKey;
    }

    /**
     * Sets the value of the procedureKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcedureKey(String value) {
        this.procedureKey = value;
    }

    /**
     * Gets the value of the pipelineKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPipelineKey() {
        return pipelineKey;
    }

    /**
     * Sets the value of the pipelineKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPipelineKey(String value) {
        this.pipelineKey = value;
    }

}
