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

/**
 * A value chain selected for an innovation in one phase (A2-2613).
 */
public class ProjectInnovationValueChain extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -2741651328853940327L;

  @Expose
  private ProjectInnovation projectInnovation;

  @Expose
  private Phase phase;

  @Expose
  private ValueChain valueChain;

  public ProjectInnovationValueChain() {
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof ProjectInnovationValueChain)) {
      return false;
    }

    ProjectInnovationValueChain other = (ProjectInnovationValueChain) obj;
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

  public Phase getPhase() {
    return phase;
  }

  public ProjectInnovation getProjectInnovation() {
    return projectInnovation;
  }

  public ValueChain getValueChain() {
    return valueChain;
  }

  @Override
  public int hashCode() {
    return this.getId() == null ? 0 : this.getId().hashCode();
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProjectInnovation(ProjectInnovation projectInnovation) {
    this.projectInnovation = projectInnovation;
  }

  public void setValueChain(ValueChain valueChain) {
    this.valueChain = valueChain;
  }

  @Override
  public String toString() {
    return "ProjectInnovationValueChain [id=" + this.getId() + "]";
  }
}
