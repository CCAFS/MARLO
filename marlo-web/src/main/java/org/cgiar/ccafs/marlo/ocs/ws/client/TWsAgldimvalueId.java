
package org.cgiar.ccafs.marlo.ocs.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for tWsAgldimvalueId complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tWsAgldimvalueId">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="attName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="attributeId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="dimValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="periodFrom" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="periodTo" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="relValue" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="status" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tWsAgldimvalueId",
  propOrder = {"attName", "attributeId", "description", "dimValue", "periodFrom", "periodTo", "relValue", "status"})
public class TWsAgldimvalueId {

  protected String attName;
  protected String attributeId;
  protected String description;
  protected String dimValue;
  protected Integer periodFrom;
  protected Integer periodTo;
  protected String relValue;
  protected String status;

  /**
   * Gets the value of the attName property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getAttName() {
    return attName;
  }

  /**
   * Gets the value of the attributeId property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getAttributeId() {
    return attributeId;
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
   * Gets the value of the dimValue property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getDimValue() {
    return dimValue;
  }

  /**
   * Gets the value of the periodFrom property.
   * 
   * @return
   *         possible object is
   *         {@link Integer }
   */
  public Integer getPeriodFrom() {
    return periodFrom;
  }

  /**
   * Gets the value of the periodTo property.
   * 
   * @return
   *         possible object is
   *         {@link Integer }
   */
  public Integer getPeriodTo() {
    return periodTo;
  }

  /**
   * Gets the value of the relValue property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getRelValue() {
    return relValue;
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
   * Sets the value of the attName property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setAttName(String value) {
    this.attName = value;
  }

  /**
   * Sets the value of the attributeId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setAttributeId(String value) {
    this.attributeId = value;
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
   * Sets the value of the dimValue property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setDimValue(String value) {
    this.dimValue = value;
  }

  /**
   * Sets the value of the periodFrom property.
   * 
   * @param value
   *        allowed object is
   *        {@link Integer }
   */
  public void setPeriodFrom(Integer value) {
    this.periodFrom = value;
  }

  /**
   * Sets the value of the periodTo property.
   * 
   * @param value
   *        allowed object is
   *        {@link Integer }
   */
  public void setPeriodTo(Integer value) {
    this.periodTo = value;
  }

  /**
   * Sets the value of the relValue property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setRelValue(String value) {
    this.relValue = value;
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
