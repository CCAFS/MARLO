
package org.cgiar.ccafs.marlo.ocs.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for tWsMarloAgree complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tWsMarloAgree">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="agreementId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="centerAmount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="donor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="donorText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="extentionDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="fundingType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="grantAmount" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="objectives" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="originalDonor" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="originalDonorText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tWsMarloAgree",
  propOrder = {"agreementId", "centerAmount", "description", "donor", "donorText", "endDate", "extentionDate",
    "fundingType", "grantAmount", "objectives", "originalDonor", "originalDonorText", "researcher", "researcherText",
    "shortName", "startDate", "status"})
public class TWsMarloAgree {

  protected String agreementId;
  protected Double centerAmount;
  protected String description;
  protected String donor;
  protected String donorText;
  protected String endDate;
  protected String extentionDate;
  protected String fundingType;
  protected Double grantAmount;
  protected String objectives;
  protected String originalDonor;
  protected String originalDonorText;
  protected String researcher;
  protected String researcherText;
  protected String shortName;
  protected String startDate;
  protected String status;

  /**
   * Gets the value of the agreementId property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getAgreementId() {
    return agreementId;
  }

  /**
   * Gets the value of the centerAmount property.
   * 
   * @return
   *         possible object is
   *         {@link Double }
   */
  public Double getCenterAmount() {
    return centerAmount;
  }

  /**
   * Gets the value of the description property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getDescription() {
    return description;
  }

  /**
   * Gets the value of the donor property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getDonor() {
    return donor;
  }

  /**
   * Gets the value of the donorText property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getDonorText() {
    return donorText;
  }

  /**
   * Gets the value of the endDate property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getEndDate() {
    return endDate;
  }

  /**
   * Gets the value of the extentionDate property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getExtentionDate() {
    return extentionDate;
  }

  /**
   * Gets the value of the fundingType property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getFundingType() {
    return fundingType;
  }

  /**
   * Gets the value of the grantAmount property.
   * 
   * @return
   *         possible object is
   *         {@link Double }
   */
  public Double getGrantAmount() {
    return grantAmount;
  }

  /**
   * Gets the value of the objectives property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getObjectives() {
    return objectives;
  }

  /**
   * Gets the value of the originalDonor property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getOriginalDonor() {
    return originalDonor;
  }

  /**
   * Gets the value of the originalDonorText property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getOriginalDonorText() {
    return originalDonorText;
  }

  /**
   * Gets the value of the researcher property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getResearcher() {
    return researcher;
  }

  /**
   * Gets the value of the researcherText property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getResearcherText() {
    return researcherText;
  }

  /**
   * Gets the value of the shortName property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getShortName() {
    return shortName;
  }

  /**
   * Gets the value of the startDate property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getStartDate() {
    return startDate;
  }

  /**
   * Gets the value of the status property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getStatus() {
    return status;
  }

  /**
   * Sets the value of the agreementId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setAgreementId(String value) {
    this.agreementId = value;
  }

  /**
   * Sets the value of the centerAmount property.
   * 
   * @param value
   *        allowed object is
   *        {@link Double }
   */
  public void setCenterAmount(Double value) {
    this.centerAmount = value;
  }

  /**
   * Sets the value of the description property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setDescription(String value) {
    this.description = value;
  }

  /**
   * Sets the value of the donor property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setDonor(String value) {
    this.donor = value;
  }

  /**
   * Sets the value of the donorText property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setDonorText(String value) {
    this.donorText = value;
  }

  /**
   * Sets the value of the endDate property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setEndDate(String value) {
    this.endDate = value;
  }

  /**
   * Sets the value of the extentionDate property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setExtentionDate(String value) {
    this.extentionDate = value;
  }

  /**
   * Sets the value of the fundingType property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setFundingType(String value) {
    this.fundingType = value;
  }

  /**
   * Sets the value of the grantAmount property.
   * 
   * @param value
   *        allowed object is
   *        {@link Double }
   */
  public void setGrantAmount(Double value) {
    this.grantAmount = value;
  }

  /**
   * Sets the value of the objectives property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setObjectives(String value) {
    this.objectives = value;
  }

  /**
   * Sets the value of the originalDonor property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setOriginalDonor(String value) {
    this.originalDonor = value;
  }

  /**
   * Sets the value of the originalDonorText property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setOriginalDonorText(String value) {
    this.originalDonorText = value;
  }

  /**
   * Sets the value of the researcher property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setResearcher(String value) {
    this.researcher = value;
  }

  /**
   * Sets the value of the researcherText property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setResearcherText(String value) {
    this.researcherText = value;
  }

  /**
   * Sets the value of the shortName property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setShortName(String value) {
    this.shortName = value;
  }

  /**
   * Sets the value of the startDate property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setStartDate(String value) {
    this.startDate = value;
  }

  /**
   * Sets the value of the status property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setStatus(String value) {
    this.status = value;
  }

}