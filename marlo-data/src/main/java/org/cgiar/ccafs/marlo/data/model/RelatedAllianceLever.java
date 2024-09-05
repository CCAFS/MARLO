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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.annotations.Expose;

public class RelatedAllianceLever extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -963914989396761020L;

  @Expose
  private String name;
  @Expose
  private String description;
  @Expose
  private Phase phase;


  private Set<RelatedLeversSDGContribution> relatedLeversSDGContribution = new HashSet<RelatedLeversSDGContribution>(0);

  private List<RelatedLeversSDGContribution> relatedSdgContribution;


  public RelatedAllianceLever() {
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    RelatedAllianceLever other = (RelatedAllianceLever) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public String getDescription() {
    return description;
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


  public Phase getPhase() {
    return phase;
  }

  public Set<RelatedLeversSDGContribution> getRelatedLeversSDGContribution() {
    return relatedLeversSDGContribution;
  }

  public List<RelatedLeversSDGContribution> getRelatedSdgContribution() {
    return relatedSdgContribution;
  }

  public List<RelatedLeversSDGContribution> getRelatedSdgContribution(Phase phase) {
    List<RelatedLeversSDGContribution> relatedLeversSDGContribution = this.getRelatedLeversSDGContribution().stream()
      .filter(dm -> dm.isActive() && dm.getPhase().equals(phase)).collect(Collectors.toList());
    if (relatedLeversSDGContribution != null && !relatedLeversSDGContribution.isEmpty()) {
      return relatedLeversSDGContribution;
    }
    return new ArrayList<RelatedLeversSDGContribution>();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setRelatedLeversSDGContribution(Set<RelatedLeversSDGContribution> relatedLeversSDGContribution) {
    this.relatedLeversSDGContribution = relatedLeversSDGContribution;
  }

  public void setRelatedSdgContribution(List<RelatedLeversSDGContribution> relatedSdgContribution) {
    this.relatedSdgContribution = relatedSdgContribution;
  }

  @Override
  public String toString() {
    return "Activity [id=" + this.getId() + ", name=" + name + ", description=" + description + "]";
  }
}