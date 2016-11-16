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
  private FundingSource fundingSource;


  @Expose
  private User modifiedBy;


  @Expose
  private String modificationJustification;


  @Expose
  private CrpClusterKeyOutput crpClusterKeyOutput;


  @Expose
  private CrpProgramOutcome crpProgramOutcome;

  @Expose
  private Date createDate;


  private Set<DeliverablePartnership> deliverablePartnerships = new HashSet<DeliverablePartnership>(0);

  private Set<DeliverableActivity> deliverableActivities = new HashSet<DeliverableActivity>(0);


  private Set<SectionStatus> sectionStatuses = new HashSet<SectionStatus>(0);

  private DeliverablePartnership responsiblePartner;
  private Set<DeliverableFundingSource> deliverableFundingSources = new HashSet<DeliverableFundingSource>(0);


  private List<DeliverableFundingSource> fundingSources;

  private List<DeliverablePartnership> otherPartners;

  @Expose
  private Boolean crossCuttingGender;
  @Expose
  private Boolean crossCuttingYouth;
  @Expose
  private Boolean crossCuttingCapacity;
  @Expose
  private Boolean crossCuttingNa;


  public Deliverable() {
  }


  public Deliverable(Project project, DeliverableType deliverableType, String title, String typeOther, int year,
    Integer status, String statusDescription, boolean active, Date activeSince, User createdBy, User modifiedBy,
    String modificationJustification, CrpClusterKeyOutput crpClusterKeyOutput, CrpProgramOutcome crpProgramOutcome,
    Set<DeliverablePartnership> deliverablePartnerships, Set<DeliverableActivity> deliverableActivities,
    Date createDate) {
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
    this.createDate = createDate;
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
    Deliverable other = (Deliverable) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }


  public Date getActiveSince() {
    return this.activeSince;
  }


  public Date getCreateDate() {
    return createDate;
  }


  public User getCreatedBy() {
    return createdBy;
  }


  public Boolean getCrossCuttingCapacity() {
    return crossCuttingCapacity;
  }


  public Boolean getCrossCuttingGender() {
    return crossCuttingGender;
  }


  public Boolean getCrossCuttingNa() {
    return crossCuttingNa;
  }


  public Boolean getCrossCuttingYouth() {
    return crossCuttingYouth;
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


  public Set<DeliverableFundingSource> getDeliverableFundingSources() {
    return deliverableFundingSources;
  }


  public Set<DeliverablePartnership> getDeliverablePartnerships() {
    return deliverablePartnerships;
  }


  public DeliverableType getDeliverableType() {
    return this.deliverableType;
  }


  public FundingSource getFundingSource() {
    return fundingSource;
  }


  public List<DeliverableFundingSource> getFundingSources() {
    return fundingSources;
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


  @Override
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

  public Set<SectionStatus> getSectionStatuses() {
    return sectionStatuses;
  }

  public Integer getStatus() {
    return this.status;
  }

  public String getStatusDescription() {
    return this.statusDescription;
  }

  public String getStatusName() {
    try {
      if (this.status != null) {
        return ProjectStatusEnum.getValue(this.status).getStatus() != null
          ? ProjectStatusEnum.getValue(this.status).getStatus() : "";
      } else {
        return "";
      }
    } catch (Exception e) {
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
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
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

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }


  public void setCrossCuttingCapacity(Boolean crossCuttingCapacity) {
    this.crossCuttingCapacity = crossCuttingCapacity;
  }

  public void setCrossCuttingGender(Boolean crossCuttingGender) {
    this.crossCuttingGender = crossCuttingGender;
  }

  public void setCrossCuttingNa(Boolean crossCuttingNa) {
    this.crossCuttingNa = crossCuttingNa;
  }

  public void setCrossCuttingYouth(Boolean crossCuttingYouth) {
    this.crossCuttingYouth = crossCuttingYouth;
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

  public void setDeliverableFundingSources(Set<DeliverableFundingSource> deliverableFundingSources) {
    this.deliverableFundingSources = deliverableFundingSources;
  }

  public void setDeliverablePartnerships(Set<DeliverablePartnership> deliverablePartnerships) {
    this.deliverablePartnerships = deliverablePartnerships;
  }

  public void setDeliverableType(DeliverableType deliverableType) {
    this.deliverableType = deliverableType;
  }


  public void setFundingSource(FundingSource fundingSource) {
    this.fundingSource = fundingSource;
  }

  public void setFundingSources(List<DeliverableFundingSource> fundingSources) {
    this.fundingSources = fundingSources;
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

  public void setSectionStatuses(Set<SectionStatus> sectionStatuses) {
    this.sectionStatuses = sectionStatuses;
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
