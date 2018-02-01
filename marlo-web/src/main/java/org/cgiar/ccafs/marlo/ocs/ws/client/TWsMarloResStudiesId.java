
package org.cgiar.ccafs.marlo.ocs.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for tWsMarloResStudiesId complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tWsMarloResStudiesId">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="countryId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="endYear" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="institution" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="institutionId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="levelId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="program" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="programId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="resourceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="stydyLevel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tWsMarloResStudiesId", propOrder = {"country", "countryId", "endYear", "institution", "institutionId",
  "levelId", "program", "programId", "resourceId", "stydyLevel"})
public class TWsMarloResStudiesId {

  protected String country;
  protected String countryId;
  protected Integer endYear;
  protected String institution;
  protected String institutionId;
  protected String levelId;
  protected String program;
  protected String programId;
  protected String resourceId;
  protected String stydyLevel;

  /**
   * Gets the value of the country property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getCountry() {
    return country;
  }

  /**
   * Gets the value of the countryId property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getCountryId() {
    return countryId;
  }

  /**
   * Gets the value of the endYear property.
   * 
   * @return
   *         possible object is
   *         {@link Integer }
   */
  public Integer getEndYear() {
    return endYear;
  }

  /**
   * Gets the value of the institution property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getInstitution() {
    return institution;
  }

  /**
   * Gets the value of the institutionId property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getInstitutionId() {
    return institutionId;
  }

  /**
   * Gets the value of the levelId property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getLevelId() {
    return levelId;
  }

  /**
   * Gets the value of the program property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getProgram() {
    return program;
  }

  /**
   * Gets the value of the programId property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getProgramId() {
    return programId;
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
   * Gets the value of the stydyLevel property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getStydyLevel() {
    return stydyLevel;
  }

  /**
   * Sets the value of the country property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setCountry(String value) {
    this.country = value;
  }

  /**
   * Sets the value of the countryId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setCountryId(String value) {
    this.countryId = value;
  }

  /**
   * Sets the value of the endYear property.
   * 
   * @param value
   *        allowed object is
   *        {@link Integer }
   */
  public void setEndYear(Integer value) {
    this.endYear = value;
  }

  /**
   * Sets the value of the institution property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setInstitution(String value) {
    this.institution = value;
  }

  /**
   * Sets the value of the institutionId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setInstitutionId(String value) {
    this.institutionId = value;
  }

  /**
   * Sets the value of the levelId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setLevelId(String value) {
    this.levelId = value;
  }

  /**
   * Sets the value of the program property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setProgram(String value) {
    this.program = value;
  }

  /**
   * Sets the value of the programId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setProgramId(String value) {
    this.programId = value;
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
   * Sets the value of the stydyLevel property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setStydyLevel(String value) {
    this.stydyLevel = value;
  }

}
