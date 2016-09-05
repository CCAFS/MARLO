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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class Deliverable implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 1867818669925473872L;


  @Expose
  private Long id;


  @Expose
  private Project project;


  @Expose
  private DeliverableType deliverableType;


  @Expose
  private String title;


  @Expose
  private String typeOther;


  @Expose
  private int year;


  @Expose
  private Integer status;


  @Expose
  private String statusDescription;


  @Expose
  private boolean active;

  @Expose
  private Date activeSince;


  @Expose
  private User createdBy;

  @Expose
  private User modifiedBy;


  @Expose
  private String modificationJustification;

  @Expose
  private CrpClusterKeyOutput crpClusterKeyOutput;


  @Expose
  private CrpProgramOutcome crpProgramOutcome;


  private Set<DeliverablePartnership> deliverablePartnerships = new HashSet<DeliverablePartnership>(0);


  private Set<DeliverableActivity> deliverableActivities = new HashSet<DeliverableActivity>(0);


  private DeliverablePartnership responsiblePartner;

  private List<DeliverablePartnership> otherPartners;


  public Deliverable() {
  }


  public Deliverable(Project project, DeliverableType deliverableType, String title, String typeOther, int year,
    Integer status, String statusDescription, boolean active, Date activeSince, User createdBy, User modifiedBy,
    String modificationJustification, CrpClusterKeyOutput crpClusterKeyOutput, CrpProgramOutcome crpProgramOutcome,
    Set<DeliverablePartnership> deliverablePartnerships, Set<DeliverableActivity> deliverableActivities) {
    this.project = project;
    this.deliverableType = deliverableType;
    this.title = title;
    this.typeOther = typeOther;
    this.year = year;
    this.status = status;
    this.statusDescription = statusDescription;
    this.active = active;
    this.activeSince = activeSince;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.modificationJustification = modificationJustification;
    this.crpProgramOutcome = crpProgramOutcome;
    this.crpClusterKeyOutput = crpClusterKeyOutput;
    this.deliverablePartnerships = deliverablePartnerships;
    this.deliverableActivities = deliverableActivities;
  }


  public Deliverable(Project project, int year, boolean active, Date activeSince, User createdBy, User modifiedBy,
    String modificationJustification) {
    this.project = project;
    this.year = year;
    this.active = active;
    this.activeSince = activeSince;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.modificationJustification = modificationJustification;
  }

  public Date getActiveSince() {
    return this.activeSince;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public CrpClusterKeyOutput getCrpClusterKeyOutput() {
    return crpClusterKeyOutput;
  }

  public CrpProgramOutcome getCrpProgramOutcome() {
    return crpProgramOutcome;
  }

  public Set<DeliverableActivity> getDeliverableActivities() {
    return deliverableActivities;
  }

  public Set<DeliverablePartnership> getDeliverablePartnerships() {
    return deliverablePartnerships;
  }

  public DeliverableType getDeliverableType() {
    return this.deliverableType;
  }

  @Override
  public Long getId() {
    return id;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public String getModificationJustification() {
    return this.modificationJustification;
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  public List<DeliverablePartnership> getOtherPartners() {
    return otherPartners;
  }

  public Project getProject() {
    return project;
  }

  public DeliverablePartnership getResponsiblePartner() {
    return responsiblePartner;
  }

  public Integer getStatus() {
    return this.status;
  }

  public String getStatusDescription() {
    return this.statusDescription;
  }


  public String getStatusName() {
    if (this.status != null) {
      return ProjectStatusEnum.getValue(this.status).getStatus() != null
        ? ProjectStatusEnum.getValue(this.status).getStatus() : "";
    } else {
      return "";
    }
  }

  public String getTitle() {
    return this.title;
  }

  public String getTypeOther() {
    return this.typeOther;
  }

  public int getYear() {
    return this.year;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setCrpClusterKeyOutput(CrpClusterKeyOutput crpClusterKeyOutput) {
    this.crpClusterKeyOutput = crpClusterKeyOutput;
  }

  public void setCrpProgramOutcome(CrpProgramOutcome crpProgramOutcome) {
    this.crpProgramOutcome = crpProgramOutcome;
  }


  public void setDeliverableActivities(Set<DeliverableActivity> deliverableActivities) {
    this.deliverableActivities = deliverableActivities;
  }

  public void setDeliverablePartnerships(Set<DeliverablePartnership> deliverablePartnerships) {
    this.deliverablePartnerships = deliverablePartnerships;
  }

  public void setDeliverableType(DeliverableType deliverableType) {
    this.deliverableType = deliverableType;
  }


  public void setId(Long id) {
    this.id = id;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }


  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }


  public void setOtherPartners(List<DeliverablePartnership> otherPartners) {
    this.otherPartners = otherPartners;
  }


  public void setProject(Project project) {
    this.project = project;
  }

  public void setResponsiblePartner(DeliverablePartnership responsiblePartner) {
    this.responsiblePartner = responsiblePartner;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public void setStatusDescription(String statusDescription) {
    this.statusDescription = statusDescription;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  public void setTypeOther(String typeOther) {
    this.typeOther = typeOther;
  }


  public void setYear(int year) {
    this.year = year;
  }

}
