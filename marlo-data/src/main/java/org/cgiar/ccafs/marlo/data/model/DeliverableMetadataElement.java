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

package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableMetadataElement extends MarloBaseEntity implements Serializable, IAuditLog {


  private static final long serialVersionUID = -44359971548335546L;

  @Expose
  private MetadataElement metadataElement;
  @Expose
  private Deliverable deliverable;
  @Expose
  private String elementValue;

  @Expose
  private Boolean hide;
  @Expose
  private Phase phase;

  public DeliverableMetadataElement() {
  }

  public Deliverable getDeliverable() {
    return deliverable;
  }

  public String getElementValue() {
    return elementValue;
  }

  public Boolean getHide() {
    return hide;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public MetadataElement getMetadataElement() {
    return metadataElement;
  }


  @Override
  public String getModificationJustification() {

    return "";
  }

  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return u;
  }

  public Phase getPhase() {
    return phase;
  }

  @Override
  public boolean isActive() {

    return true;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setElementValue(String elementValue) {
    this.elementValue = elementValue;
  }


  public void setHide(Boolean hide) {
    this.hide = hide;
  }

  public void setMetadataElement(MetadataElement metadataElement) {
    this.metadataElement = metadataElement;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {

  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  @Override
  public String toString() {
    return "DeliverableMetadataElement [id=" + this.getId() + ", metadataElement=" + metadataElement + ", deliverable="
      + deliverable + ", elementValue=" + elementValue + ", hide=" + hide + "]";
  }

}
