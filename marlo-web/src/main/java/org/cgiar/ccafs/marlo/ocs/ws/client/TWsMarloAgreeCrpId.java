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
 * Java class for tWsMarloAgreeCrpId complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tWsMarloAgreeCrpId">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="agreementId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="crp" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="crpText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="percentage" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tWsMarloAgreeCrpId", propOrder = {"agreementId", "crp", "crpText", "percentage"})
public class TWsMarloAgreeCrpId {

  protected String agreementId;
  protected String crp;
  protected String crpText;
  protected Double percentage;

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
   * Gets the value of the crp property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getCrp() {
    return crp;
  }

  /**
   * Gets the value of the crpText property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getCrpText() {
    return crpText;
  }

  /**
   * Gets the value of the percentage property.
   * 
   * @return
   *         possible object is
   *         {@link Double }
   */
  public Double getPercentage() {
    return percentage;
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
   * Sets the value of the crp property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setCrp(String value) {
    this.crp = value;
  }

  /**
   * Sets the value of the crpText property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setCrpText(String value) {
    this.crpText = value;
  }

  /**
   * Sets the value of the percentage property.
   * 
   * @param value
   *        allowed object is
   *        {@link Double }
   */
  public void setPercentage(Double value) {
    this.percentage = value;
  }

}
