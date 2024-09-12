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

import com.google.gson.annotations.Expose;

public class ProjectExpectedStudySdgAllianceLever extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -963914989396761020L;

  @Expose
  private String leverComments;

  @Expose
  private Boolean isPrimary;


  @Expose
  private ProjectExpectedStudy projectExpectedStudy;


  @Expose
  private Phase phase;


  @Expose
  private SDGContribution sDGContribution;


  @Expose
  private AllianceLever allianceLever;


  public ProjectExpectedStudySdgAllianceLever() {
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    ProjectExpectedStudySdgAllianceLever other = (ProjectExpectedStudySdgAllianceLever) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public AllianceLever getAllianceLever() {
    return allianceLever;
  }

  public Boolean getIsPrimary() {
    return isPrimary;
  }

  public String getLeverComments() {
    return leverComments;
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

  public ProjectExpectedStudy getProjectExpectedStudy() {
    return projectExpectedStudy;
  }


  public SDGContribution getsDGContribution() {
    return sDGContribution;
  }

  public void setAllianceLever(AllianceLever allianceLever) {
    this.allianceLever = allianceLever;
  }


  public void setIsPrimary(Boolean isPrimary) {
    this.isPrimary = isPrimary;
  }


  public void setLeverComments(String leverComments) {
    this.leverComments = leverComments;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy) {
    this.projectExpectedStudy = projectExpectedStudy;
  }


  public void setsDGContribution(SDGContribution sDGContribution) {
    this.sDGContribution = sDGContribution;
  }

  @Override
  public String toString() {
    return "Activity [id=" + this.getId() + ", isPrimary=" + isPrimary + ", leverComments=" + leverComments + "]";
  }
}

