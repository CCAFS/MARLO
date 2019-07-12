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
public class CenterOutput extends MarloAuditableEntity implements Serializable, IAuditLog {


  private static final long serialVersionUID = -185814169741386135L;

  @Expose
  private String title;


  @Expose
  private Date dateAdded;

  @Expose
  private String shortName;

  @Expose
  private CrpProgram centerProgram;


  private Set<CenterSectionStatus> sectionStatuses = new HashSet<CenterSectionStatus>(0);

  private Set<CenterOutputsNextUser> researchOutputsNextUsers = new HashSet<CenterOutputsNextUser>(0);

  private Set<CenterProjectOutput> projectOutputs = new HashSet<CenterProjectOutput>(0);


  private List<CenterOutputsNextUser> nextUsers;

  private Set<CenterOutputsOutcome> centerOutputsOutcomes = new HashSet<CenterOutputsOutcome>(0);


  private List<CenterOutputsOutcome> outcomes;

  public CenterOutput() {
    super();
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
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public Set<CenterOutputsOutcome> getCenterOutputsOutcomes() {
    return centerOutputsOutcomes;
  }

  public CrpProgram getCenterProgram() {
    return centerProgram;
  }

  public String getComposedName() {
    return "O" + this.getId() + "- " + (this.title != null ? this.title : "title not defined");
  }

  /**
   * @return the dateAdded
   */
  public Date getDateAdded() {
    return dateAdded;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public List<CenterOutputsNextUser> getNextUsers() {
    return nextUsers;
  }

  public List<CenterOutputsOutcome> getOutcomes() {
    return outcomes;
  }


  public Set<CenterProjectOutput> getProjectOutputs() {
    return projectOutputs;
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
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setCenterOutputsOutcomes(Set<CenterOutputsOutcome> centerOutputsOutcomes) {
    this.centerOutputsOutcomes = centerOutputsOutcomes;
  }

  public void setCenterProgram(CrpProgram centerProgram) {
    this.centerProgram = centerProgram;
  }

  /**
   * @param dateAdded the dateAdded to set
   */
  public void setDateAdded(Date dateAdded) {
    this.dateAdded = dateAdded;
  }

  public void setNextUsers(List<CenterOutputsNextUser> nextUsers) {
    this.nextUsers = nextUsers;
  }

  public void setOutcomes(List<CenterOutputsOutcome> outcomes) {
    this.outcomes = outcomes;
  }


  public void setProjectOutputs(Set<CenterProjectOutput> projectOutputs) {
    this.projectOutputs = projectOutputs;
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
    return "CenterOutput [id=" + this.getId() + ", title=" + title + "]";
  }

}
