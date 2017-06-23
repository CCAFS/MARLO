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
 * Java class for tWsMarloAgreeCountry complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tWsMarloAgreeCountry">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://logic.control.abw.ciat.cgiar.org/}tWsMarloAgreeCountryId" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tWsMarloAgreeCountry", propOrder = {"id"})
public class TWsMarloAgreeCountry {

  protected TWsMarloAgreeCountryId id;

  /**
   * Gets the value of the id property.
   * 
   * @return
   *         possible object is
   *         {@link TWsMarloAgreeCountryId }
   */
  public TWsMarloAgreeCountryId getId() {
    return id;
  }

  /**
   * Sets the value of the id property.
   * 
   * @param value
   *        allowed object is
   *        {@link TWsMarloAgreeCountryId }
   */
  public void setId(TWsMarloAgreeCountryId value) {
    this.id = value;
  }

}
