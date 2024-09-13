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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class ProjectExpectedStudyPartnership extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -963914989396761020L;

  @Expose
  private String name;
  @Expose
  private ProjectExpectedStudy projectExpectedStudy;
  @Expose
  private Phase phase;
  @Expose
  private ProjectExpectedStudyPartnerType projectExpectedStudyPartnerType;
  @Expose
  private Institution institution;


  private Set<ProjectExpectedStudyPartnershipsPerson> projectExpectedStudyPartnershipsPersons = new HashSet<>(0);


  private List<ProjectExpectedStudyPartnershipsPerson> partnershipsPersons;


  public ProjectExpectedStudyPartnership() {
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    ProjectExpectedStudyPartnership other = (ProjectExpectedStudyPartnership) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public Institution getInstitution() {
    return institution;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public String getName() {
    return name;
  }


  public List<ProjectExpectedStudyPartnershipsPerson> getPartnershipsPersons() {
    return partnershipsPersons;
  }


  public Phase getPhase() {
    return phase;
  }


  public ProjectExpectedStudy getProjectExpectedStudy() {
    return projectExpectedStudy;
  }


  public Set<ProjectExpectedStudyPartnershipsPerson> getProjectExpectedStudyPartnershipsPersons() {
    return projectExpectedStudyPartnershipsPersons;
  }


  public ProjectExpectedStudyPartnerType getProjectExpectedStudyPartnerType() {
    return projectExpectedStudyPartnerType;
  }


  public void setInstitution(Institution institution) {
    this.institution = institution;
  }


  public void setName(String name) {
    this.name = name;
  }


  public void setPartnershipsPersons(List<ProjectExpectedStudyPartnershipsPerson> partnershipsPersons) {
    this.partnershipsPersons = partnershipsPersons;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy) {
    this.projectExpectedStudy = projectExpectedStudy;
  }


  public void setProjectExpectedStudyPartnershipsPersons(
    Set<ProjectExpectedStudyPartnershipsPerson> projectExpectedStudyPartnershipsPersons) {
    this.projectExpectedStudyPartnershipsPersons = projectExpectedStudyPartnershipsPersons;
  }


  public void setProjectExpectedStudyPartnerType(ProjectExpectedStudyPartnerType projectExpectedStudyPartnerType) {
    this.projectExpectedStudyPartnerType = projectExpectedStudyPartnerType;
  }


  @Override
  public String toString() {
    return "Activity [id=" + this.getId() + ", name=" + name;
  }
}