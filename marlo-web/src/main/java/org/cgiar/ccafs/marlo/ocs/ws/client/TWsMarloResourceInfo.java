
package org.cgiar.ccafs.marlo.ocs.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for tWsMarloResourceInfo complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tWsMarloResourceInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="birthDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cityOfBirth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="cityOfBirthId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="couOfBirth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="couOfBirthId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dateTo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="EMail" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="error" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="firstName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="genderFx" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LCCategory" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LCCategoryId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LCDateFrom" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="level3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="level3Id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="profession" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="professionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resourceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resourceType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resourceTypeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="supervisor1" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="supervisor1Id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="supervisor2" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="supervisor2Id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="supervisor3" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="supervisor3Id" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="surname" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tWsMarloResourceInfo",
  propOrder = {"birthDate", "cityOfBirth", "cityOfBirthId", "couOfBirth", "couOfBirthId", "dateFrom", "dateTo", "eMail",
    "error", "firstName", "genderFx", "lcCategory", "lcCategoryId", "lcDateFrom", "level3", "level3Id", "profession",
    "professionId", "resourceId", "resourceType", "resourceTypeId", "status", "supervisor1", "supervisor1Id",
    "supervisor2", "supervisor2Id", "supervisor3", "supervisor3Id", "surname"})
public class TWsMarloResourceInfo {

  protected String birthDate;
  protected String cityOfBirth;
  protected String cityOfBirthId;
  protected String couOfBirth;
  protected String couOfBirthId;
  protected String dateFrom;
  protected String dateTo;
  @XmlElement(name = "EMail")
  protected String eMail;
  protected String error;
  protected String firstName;
  protected String genderFx;
  @XmlElement(name = "LCCategory")
  protected String lcCategory;
  @XmlElement(name = "LCCategoryId")
  protected String lcCategoryId;
  @XmlElement(name = "LCDateFrom")
  protected String lcDateFrom;
  protected String level3;
  protected String level3Id;
  protected String profession;
  protected String professionId;
  protected String resourceId;
  protected String resourceType;
  protected String resourceTypeId;
  protected String status;
  protected String supervisor1;
  protected String supervisor1Id;
  protected String supervisor2;
  protected String supervisor2Id;
  protected String supervisor3;
  protected String supervisor3Id;
  protected String surname;

  /**
   * Gets the value of the birthDate property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getBirthDate() {
    return birthDate;
  }

  /**
   * Gets the value of the cityOfBirth property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getCityOfBirth() {
    return cityOfBirth;
  }

  /**
   * Gets the value of the cityOfBirthId property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getCityOfBirthId() {
    return cityOfBirthId;
  }

  /**
   * Gets the value of the couOfBirth property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getCouOfBirth() {
    return couOfBirth;
  }

  /**
   * Gets the value of the couOfBirthId property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getCouOfBirthId() {
    return couOfBirthId;
  }

  /**
   * Gets the value of the dateFrom property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getDateFrom() {
    return dateFrom;
  }

  /**
   * Gets the value of the dateTo property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getDateTo() {
    return dateTo;
  }

  /**
   * Gets the value of the eMail property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getEMail() {
    return eMail;
  }

  /**
   * Gets the value of the error property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getError() {
    return error;
  }

  /**
   * Gets the value of the firstName property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getFirstName() {
    return firstName;
  }

  /**
   * Gets the value of the genderFx property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getGenderFx() {
    return genderFx;
  }

  /**
   * Gets the value of the lcCategory property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getLCCategory() {
    return lcCategory;
  }

  /**
   * Gets the value of the lcCategoryId property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getLCCategoryId() {
    return lcCategoryId;
  }

  /**
   * Gets the value of the lcDateFrom property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getLCDateFrom() {
    return lcDateFrom;
  }

  /**
   * Gets the value of the level3 property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getLevel3() {
    return level3;
  }

  /**
   * Gets the value of the level3Id property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getLevel3Id() {
    return level3Id;
  }

  /**
   * Gets the value of the profession property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getProfession() {
    return profession;
  }

  /**
   * Gets the value of the professionId property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getProfessionId() {
    return professionId;
  }

  /**
   * Gets the value of the resourceId property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getResourceId() {
    return resourceId;
  }

  /**
   * Gets the value of the resourceType property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getResourceType() {
    return resourceType;
  }

  /**
   * Gets the value of the resourceTypeId property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getResourceTypeId() {
    return resourceTypeId;
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
   * Gets the value of the supervisor1 property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getSupervisor1() {
    return supervisor1;
  }

  /**
   * Gets the value of the supervisor1Id property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getSupervisor1Id() {
    return supervisor1Id;
  }

  /**
   * Gets the value of the supervisor2 property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getSupervisor2() {
    return supervisor2;
  }

  /**
   * Gets the value of the supervisor2Id property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getSupervisor2Id() {
    return supervisor2Id;
  }

  /**
   * Gets the value of the supervisor3 property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getSupervisor3() {
    return supervisor3;
  }

  /**
   * Gets the value of the supervisor3Id property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getSupervisor3Id() {
    return supervisor3Id;
  }

  /**
   * Gets the value of the surname property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getSurname() {
    return surname;
  }

  /**
   * Sets the value of the birthDate property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setBirthDate(String value) {
    this.birthDate = value;
  }

  /**
   * Sets the value of the cityOfBirth property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setCityOfBirth(String value) {
    this.cityOfBirth = value;
  }

  /**
   * Sets the value of the cityOfBirthId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setCityOfBirthId(String value) {
    this.cityOfBirthId = value;
  }

  /**
   * Sets the value of the couOfBirth property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setCouOfBirth(String value) {
    this.couOfBirth = value;
  }

  /**
   * Sets the value of the couOfBirthId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setCouOfBirthId(String value) {
    this.couOfBirthId = value;
  }

  /**
   * Sets the value of the dateFrom property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setDateFrom(String value) {
    this.dateFrom = value;
  }

  /**
   * Sets the value of the dateTo property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setDateTo(String value) {
    this.dateTo = value;
  }

  /**
   * Sets the value of the eMail property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setEMail(String value) {
    this.eMail = value;
  }

  /**
   * Sets the value of the error property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setError(String value) {
    this.error = value;
  }

  /**
   * Sets the value of the firstName property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setFirstName(String value) {
    this.firstName = value;
  }

  /**
   * Sets the value of the genderFx property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setGenderFx(String value) {
    this.genderFx = value;
  }

  /**
   * Sets the value of the lcCategory property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setLCCategory(String value) {
    this.lcCategory = value;
  }

  /**
   * Sets the value of the lcCategoryId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setLCCategoryId(String value) {
    this.lcCategoryId = value;
  }

  /**
   * Sets the value of the lcDateFrom property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setLCDateFrom(String value) {
    this.lcDateFrom = value;
  }

  /**
   * Sets the value of the level3 property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setLevel3(String value) {
    this.level3 = value;
  }

  /**
   * Sets the value of the level3Id property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setLevel3Id(String value) {
    this.level3Id = value;
  }

  /**
   * Sets the value of the profession property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setProfession(String value) {
    this.profession = value;
  }

  /**
   * Sets the value of the professionId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setProfessionId(String value) {
    this.professionId = value;
  }

  /**
   * Sets the value of the resourceId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setResourceId(String value) {
    this.resourceId = value;
  }

  /**
   * Sets the value of the resourceType property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setResourceType(String value) {
    this.resourceType = value;
  }

  /**
   * Sets the value of the resourceTypeId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setResourceTypeId(String value) {
    this.resourceTypeId = value;
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

  /**
   * Sets the value of the supervisor1 property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setSupervisor1(String value) {
    this.supervisor1 = value;
  }

  /**
   * Sets the value of the supervisor1Id property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setSupervisor1Id(String value) {
    this.supervisor1Id = value;
  }

  /**
   * Sets the value of the supervisor2 property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setSupervisor2(String value) {
    this.supervisor2 = value;
  }

  /**
   * Sets the value of the supervisor2Id property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setSupervisor2Id(String value) {
    this.supervisor2Id = value;
  }

  /**
   * Sets the value of the supervisor3 property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setSupervisor3(String value) {
    this.supervisor3 = value;
  }

  /**
   * Sets the value of the supervisor3Id property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setSupervisor3Id(String value) {
    this.supervisor3Id = value;
  }

  /**
   * Sets the value of the surname property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setSurname(String value) {
    this.surname = value;
  }

}
