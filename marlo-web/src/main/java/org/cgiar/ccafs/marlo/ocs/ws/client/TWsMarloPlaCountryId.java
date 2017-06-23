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
 * Java class for tWsMarloPlaCountryId complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tWsMarloPlaCountryId">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="country" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="countryText" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="percentage" type="{http://www.w3.org/2001/XMLSchema}double" minOccurs="0"/>
 *         &lt;element name="plaId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tWsMarloPlaCountryId", propOrder = {"country", "countryText", "percentage", "plaId"})
public class TWsMarloPlaCountryId {

  protected String country;
  protected String countryText;
  protected Double percentage;
  protected String plaId;

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
   * Gets the value of the countryText property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getCountryText() {
    return countryText;
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
   * Sets the value of the countryText property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setCountryText(String value) {
    this.countryText = value;
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
