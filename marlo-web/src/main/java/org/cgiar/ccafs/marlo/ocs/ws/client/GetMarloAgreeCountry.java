
package org.cgiar.ccafs.marlo.ocs.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for getMarloAgreeCountry complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getMarloAgreeCountry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="agreementId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getMarloAgreeCountry", propOrder = {"agreementId"})
public class GetMarloAgreeCountry {

  protected String agreementId;

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
   * Sets the value of the agreementId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setAgreementId(String value) {
    this.agreementId = value;
  }

}
