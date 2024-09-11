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

public class AllianceLever extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -963914989396761020L;

  @Expose
  private String name;
  @Expose
  private String description;

  private Set<AllianceLeverOutcome> allianceLeverOutcomes = new HashSet<>(0);


  private List<AllianceLeverOutcome> outcomes;

  private Set<AllianceLeversSdgContribution> allianceLeversSdgContributions = new HashSet<>(0);


  private List<AllianceLeversSdgContribution> sdgContributions;


  public AllianceLever() {
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    AllianceLever other = (AllianceLever) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public Set<AllianceLeverOutcome> getAllianceLeverOutcomes() {
    return allianceLeverOutcomes;
  }

  public Set<AllianceLeversSdgContribution> getAllianceLeversSdgContributions() {
    return allianceLeversSdgContributions;
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


  public List<AllianceLeverOutcome> getOutcomes() {
    return outcomes;
  }

  public List<AllianceLeversSdgContribution> getSdgContributions() {
    return sdgContributions;
  }

  public void setAllianceLeverOutcomes(Set<AllianceLeverOutcome> allianceLeverOutcomes) {
    this.allianceLeverOutcomes = allianceLeverOutcomes;
  }


  public void setAllianceLeversSdgContributions(Set<AllianceLeversSdgContribution> allianceLeversSdgContributions) {
    this.allianceLeversSdgContributions = allianceLeversSdgContributions;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOutcomes(List<AllianceLeverOutcome> outcomes) {
    this.outcomes = outcomes;
  }

  public void setSdgContributions(List<AllianceLeversSdgContribution> sdgContributions) {
    this.sdgContributions = sdgContributions;
  }

  @Override
  public String toString() {
    return "Activity [id=" + this.getId() + ", name=" + name + ", description=" + description + "]";
  }
}