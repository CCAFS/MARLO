/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.ocs.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for tWsMarloPla complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tWsMarloPla">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="agreementId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="description" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="partner" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="partnertext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="plaId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tWsMarloPla", propOrder = {"agreementId", "description", "partner", "partnertext", "plaId"})
public class TWsMarloPla {

  protected String agreementId;
  protected String description;
  protected String partner;
  protected String partnertext;
  protected String plaId;

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
   * Gets the value of the partner property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getPartner() {
    return partner;
  }

  /**
   * Gets the value of the partnertext property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getPartnertext() {
    return partnertext;
  }

  /**
   * Gets the value of the plaId property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getPlaId() {
    return plaId;
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
   * Sets the value of the partner property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setPartner(String value) {
    this.partner = value;
  }

  /**
   * Sets the value of the partnertext property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setPartnertext(String value) {
    this.partnertext = value;
  }

  /**
   * Sets the value of the plaId property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setPlaId(String value) {
    this.plaId = value;
  }

}
