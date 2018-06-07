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

/**
 * 
 */
package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;


/**
 * Represents the research objective in the systems for the research area/research program.
 * Modified by @author nmatovu last on Oct 7, 2016
 */
public class CenterObjective extends MarloAuditableEntity implements Serializable, IAuditLog {


  private static final long serialVersionUID = -3618614156720044325L;

  /**
   * The research objective
   */
  @Expose
  private String objective;

  @Expose
  private GlobalUnit researchCenter;

  private Set<CenterImpactObjective> researchImpactObjectives = new HashSet<>(0);

  /**
   * 
   */
  public CenterObjective() {
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
    CenterObjective other = (CenterObjective) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public String getObjective() {
    return objective;
  }

  public GlobalUnit getResearchCenter() {
    return researchCenter;
  }

  public Set<CenterImpactObjective> getResearchImpactObjectives() {
    return researchImpactObjectives;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  public void setObjective(String objective) {
    this.objective = objective;
  }


  public void setResearchCenter(GlobalUnit researchCenter) {
    this.researchCenter = researchCenter;
  }


  public void setResearchImpactObjectives(Set<CenterImpactObjective> researchImpactObjectives) {
    this.researchImpactObjectives = researchImpactObjectives;
  }

  @Override
  public String toString() {
    return "CenterObjective [id=" + this.getId() + ", objective=" + objective + ", researchCenter=" + researchCenter
      + "]";
  }

}
