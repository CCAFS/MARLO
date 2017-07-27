
package org.cgiar.ccafs.marlo.ocs.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for tWsMarloAgree complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tWsMarloAgree">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="agreementId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="donor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="donorText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="extentionDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fundingType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="grantAmount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="objectives" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="researcher" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="researcherText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="shortName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="startDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tWsMarloAgree", propOrder = {
    "agreementId",
    "description",
    "donor",
    "donorText",
    "endDate",
    "extentionDate",
    "fundingType",
    "grantAmount",
    "objectives",
    "researcher",
    "researcherText",
    "shortName",
    "startDate",
    "status"
})
public class TWsMarloAgree {

    protected String agreementId;
    protected String description;
    protected String donor;
    protected String donorText;
    protected String endDate;
    protected String extentionDate;
    protected String fundingType;
    protected Double grantAmount;
    protected String objectives;
    protected String researcher;
    protected String researcherText;
    protected String shortName;
    protected String startDate;
    protected String status;

    /**
     * Gets the value of the agreementId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAgreementId() {
        return agreementId;
    }

    /**
     * Sets the value of the agreementId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAgreementId(String value) {
        this.agreementId = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the donor property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDonor() {
        return donor;
    }

    /**
     * Sets the value of the donor property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDonor(String value) {
        this.donor = value;
    }

    /**
     * Gets the value of the donorText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDonorText() {
        return donorText;
    }

    /**
     * Sets the value of the donorText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDonorText(String value) {
        this.donorText = value;
    }

    /**
     * Gets the value of the endDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Sets the value of the endDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEndDate(String value) {
        this.endDate = value;
    }

    /**
     * Gets the value of the extentionDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getExtentionDate() {
        return extentionDate;
    }

    /**
     * Sets the value of the extentionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setExtentionDate(String value) {
        this.extentionDate = value;
    }

    /**
     * Gets the value of the fundingType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFundingType() {
        return fundingType;
    }

    /**
     * Sets the value of the fundingType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFundingType(String value) {
        this.fundingType = value;
    }

    /**
     * Gets the value of the grantAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getGrantAmount() {
        return grantAmount;
    }

    /**
     * Sets the value of the grantAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setGrantAmount(Double value) {
        this.grantAmount = value;
    }

    /**
     * Gets the value of the objectives property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectives() {
        return objectives;
    }

    /**
     * Sets the value of the objectives property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectives(String value) {
        this.objectives = value;
    }

    /**
     * Gets the value of the researcher property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResearcher() {
        return researcher;
    }

    /**
     * Sets the value of the researcher property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResearcher(String value) {
        this.researcher = value;
    }

    /**
     * Gets the value of the researcherText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResearcherText() {
        return researcherText;
    }

    /**
     * Sets the value of the researcherText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResearcherText(String value) {
        this.researcherText = value;
    }

    /**
     * Gets the value of the shortName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getShortName() {
        return shortName;
    }

    /**
     * Sets the value of the shortName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setShortName(String value) {
        this.shortName = value;
    }

    /**
     * Gets the value of the startDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Sets the value of the startDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStartDate(String value) {
        this.startDate = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.status = value;
    }

}
