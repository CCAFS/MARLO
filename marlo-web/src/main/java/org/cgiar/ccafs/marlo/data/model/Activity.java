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

public class Activity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -963914989396761020L;


  @Expose
  private Long id;


  @Expose
  private ProjectPartnerPerson projectPartnerPerson;
  @Expose
  private Phase phase;
  @Expose
  private String composeID;

  @Expose
  private Project project;


  @Expose
  private User modifiedBy;


  @Expose
  private User createdBy;


  @Expose
  private String title;


  @Expose
  private String description;


  @Expose
  private Date startDate;
  @Expose
  private Date endDate;
  @Expose
  private Integer activityStatus;
  @Expose
  private String activityProgress;
  @Expose
  private boolean active;
  @Expose
  private Date activeSince;
  private Set<DeliverableActivity> deliverableActivities = new HashSet<DeliverableActivity>(0);
  @Expose
  private String modificationJustification;
  private List<DeliverableActivity> deliverables;

  public Activity() {
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    Activity other = (Activity) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public Date getActiveSince() {
    return activeSince;
  }

  public String getActivityProgress() {
    return activityProgress;
  }

  public Integer getActivityStatus() {
    return activityStatus;
  }

  public String getComposeID() {
    return composeID;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  public Set<DeliverableActivity> getDeliverableActivities() {
    return deliverableActivities;
  }

  public List<DeliverableActivity> getDeliverables() {
    return deliverables;
  }

  public String getDescription() {
    return description;
  }


  public Date getEndDate() {
    return endDate;
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
    return modificationJustification;
  }


  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }


  public Phase getPhase() {
    return phase;
  }


  public Project getProject() {
    return project;
  }


  public ProjectPartnerPerson getProjectPartnerPerson() {
    return projectPartnerPerson;
  }


  public Date getStartDate() {
    return startDate;
  }


  public String getTitle() {
    return title;
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


  public void setActivityProgress(String activityProgress) {
    this.activityProgress = activityProgress;
  }


  public void setActivityStatus(Integer activityStatus) {
    this.activityStatus = activityStatus;
  }


  public void setComposeID(String composeID) {
    this.composeID = composeID;
  }


  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }


  public void setDeliverableActivities(Set<DeliverableActivity> deliverableActivities) {
    this.deliverableActivities = deliverableActivities;
  }


  public void setDeliverables(List<DeliverableActivity> deliverables) {
    this.deliverables = deliverables;
  }


  public void setDescription(String description) {
    this.description = description;
  }


  public void setEndDate(Date endDate) {
    this.endDate = endDate;
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


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectPartnerPerson(ProjectPartnerPerson projectPartnerPerson) {
    this.projectPartnerPerson = projectPartnerPerson;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  @Override
  public String toString() {
    return "Activity [id=" + id + ", title=" + title + ", description=" + description + "]";
  }


}

