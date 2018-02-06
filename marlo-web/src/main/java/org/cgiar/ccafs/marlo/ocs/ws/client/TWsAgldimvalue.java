
package org.cgiar.ccafs.marlo.ocs.ws.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for tWsAgldimvalue complex type.
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="tWsAgldimvalue">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="id" type="{http://logic.control.abw.ciat.cgiar.org/}tWsAgldimvalueId" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tWsAgldimvalue", propOrder = {"id"})
public class TWsAgldimvalue {

  protected TWsAgldimvalueId id;

  /**
   * Gets the value of the id property.
   * 
   * @return
   *         possible object is
   *         {@link TWsAgldimvalueId }
   */
  public TWsAgldimvalueId getId() {
    return id;
  }

  /**
   * Sets the value of the id property.
   * 
   * @param value
   *        allowed object is
   *        {@link TWsAgldimvalueId }
   */
  public void setId(TWsAgldimvalueId value) {
    this.id = value;
  }

}
