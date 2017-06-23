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
 * Java class for fileTransfer complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="fileTransfer">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="docBytes" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;element name="docName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "fileTransfer", propOrder = {"docBytes", "docName"})
public class FileTransfer {

  protected byte[] docBytes;
  protected String docName;

  /**
   * Gets the value of the docBytes property.
   * 
   * @return
   *         possible object is
   *         byte[]
   */
  public byte[] getDocBytes() {
    return docBytes;
  }

  /**
   * Gets the value of the docName property.
   * 
   * @return
   *         possible object is
   *         {@link String }
   */
  public String getDocName() {
    return docName;
  }

  /**
   * Sets the value of the docBytes property.
   * 
   * @param value
   *        allowed object is
   *        byte[]
   */
  public void setDocBytes(byte[] value) {
    this.docBytes = value;
  }

  /**
   * Sets the value of the docName property.
   * 
   * @param value
   *        allowed object is
   *        {@link String }
   */
  public void setDocName(String value) {
    this.docName = value;
  }

}
