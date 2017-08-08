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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;


/**
 * @author nmatovu
 * @author hjimenez
 */
public class CenterOutput implements Serializable, IAuditLog {


  private static final long serialVersionUID = -185814169741386135L;


  @Expose
  private Long id;

  @Expose
  private String title;


  @Expose
  private Date dateAdded;

  @Expose
  private CenterOutcome researchOutcome;

  @Expose
  private Date activeSince;


  @Expose
  private boolean active;

  @Expose
  private User createdBy;

  @Expose
  private User modifiedBy;

  @Expose
  private String shortName;


  @Expose
  private String modificationJustification;

  private Set<CenterSectionStatus> sectionStatuses = new HashSet<CenterSectionStatus>(0);

  private Set<CenterOutputsNextUser> researchOutputsNextUsers = new HashSet<CenterOutputsNextUser>(0);

  private Set<CenterProjectOutput> projectOutputs = new HashSet<CenterProjectOutput>(0);

  private List<CenterOutputsNextUser> nextUsers;

  public CenterOutput() {
    super();
  }


  /**
   * @param output
   * @param dateAdded
   * @param researchOutcome
   */
  public CenterOutput(String title, Date dateAdded, CenterOutcome researchOutcome, Date activeSince) {
    super();
    this.title = title;
    this.dateAdded = dateAdded;
    this.researchOutcome = researchOutcome;
    this.activeSince = activeSince;
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
    CenterOutput other = (CenterOutput) obj;
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
    return activeSince;
  }


  public String getComposedName() {
    return "O" + this.id + "- " + (this.title != null ? this.title : "title not defined");
  }

  public User getCreatedBy() {
    return createdBy;
  }


  /**
   * @return the dateAdded
   */
  public Date getDateAdded() {
    return dateAdded;
  }


  /**
   * @return the id
   */
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

  public List<CenterOutputsNextUser> getNextUsers() {
    return nextUsers;
  }

  public Set<CenterProjectOutput> getProjectOutputs() {
    return projectOutputs;
  }


  /**
   * @return the researchOutcome
   */
  public CenterOutcome getResearchOutcome() {
    return researchOutcome;
  }

  public Set<CenterOutputsNextUser> getResearchOutputsNextUsers() {
    return researchOutputsNextUsers;
  }

  public Set<CenterSectionStatus> getSectionStatuses() {
    return sectionStatuses;
  }

  public String getShortName() {
    return shortName;
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


  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }


  /**
   * @param dateAdded the dateAdded to set
   */
  public void setDateAdded(Date dateAdded) {
    this.dateAdded = dateAdded;
  }


  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }


  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setNextUsers(List<CenterOutputsNextUser> nextUsers) {
    this.nextUsers = nextUsers;
  }


  public void setProjectOutputs(Set<CenterProjectOutput> projectOutputs) {
    this.projectOutputs = projectOutputs;
  }


  /**
   * @param researchOutcome the researchOutcome to set
   */
  public void setResearchOutcome(CenterOutcome researchOutcome) {
    this.researchOutcome = researchOutcome;
  }


  public void setResearchOutputsNextUsers(Set<CenterOutputsNextUser> researchOutputsNextUsers) {
    this.researchOutputsNextUsers = researchOutputsNextUsers;
  }

  public void setSectionStatuses(Set<CenterSectionStatus> sectionStatuses) {
    this.sectionStatuses = sectionStatuses;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return "CenterOutput [id=" + id + ", title=" + title + "]";
  }

}
