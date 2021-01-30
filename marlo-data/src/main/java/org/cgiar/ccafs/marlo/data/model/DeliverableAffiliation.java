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

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableAffiliation extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 5926712975974248056L;

  @Expose
  private Institution institution;
  @Expose
  private Deliverable deliverable;
  @Expose
  private Phase phase;
  private Integer institutionMatchConfidence;
  private String institutionNameWebOfScience;

  private DeliverableMetadataExternalSources deliverableMetadataExternalSources;
  private Date createDate;

  public DeliverableAffiliation() {
  }

  public void copyFields(DeliverableAffiliation other) {
    this.setInstitutionMatchConfidence(other.getInstitutionMatchConfidence());
    this.setInstitutionNameWebOfScience(other.getInstitutionNameWebOfScience());
    this.setPhase(other.getPhase());
    this.setActive(other.isActive());
    this.setCreatedBy(other.getCreatedBy());
    this.setCreateDate(other.getCreateDate());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (this.getClass() != obj.getClass()) {
      return false;
    }
    DeliverableAffiliation other = (DeliverableAffiliation) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public Deliverable getDeliverable() {
    return deliverable;
  }

  public DeliverableMetadataExternalSources getDeliverableMetadataExternalSources() {
    return deliverableMetadataExternalSources;
  }

  public Institution getInstitution() {
    return institution;
  }

  public Integer getInstitutionMatchConfidence() {
    return institutionMatchConfidence;
  }

  public String getInstitutionNameWebOfScience() {
    return institutionNameWebOfScience;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public Phase getPhase() {
    return phase;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void
    setDeliverableMetadataExternalSources(DeliverableMetadataExternalSources deliverableMetadataExternalSources) {
    this.deliverableMetadataExternalSources = deliverableMetadataExternalSources;
  }

  public void setInstitution(Institution institution) {
    this.institution = institution;
  }

  public void setInstitutionMatchConfidence(Integer institutionMatchConfidence) {
    this.institutionMatchConfidence = institutionMatchConfidence;
  }

  public void setInstitutionNameWebOfScience(String institutionNameWebOfScience) {
    this.institutionNameWebOfScience = institutionNameWebOfScience;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }

}