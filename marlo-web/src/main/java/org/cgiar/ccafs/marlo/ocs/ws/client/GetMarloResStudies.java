
package org.cgiar.ccafs.marlo.ocs.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for getMarloResStudies complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMarloResStudies">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="resourceId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMarloResStudies", propOrder = {"resourceId"})
public class GetMarloResStudies {

  protected String resourceId;

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
   * Sets the value of the resourceId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setResourceId(String value) {
    this.resourceId = value;
  }

}
