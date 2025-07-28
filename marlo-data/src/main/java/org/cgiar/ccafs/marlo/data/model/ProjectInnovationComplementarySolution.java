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

public class ProjectInnovationComplementarySolution extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -963914989396761020L;

  @Expose
  private String title;
  @Expose
  private String shortTitle;
  @Expose
  private String shortDescription;
  @Expose
  private RepIndInnovationType projectInnovationType;
  @Expose
  private Phase phase;

  private Set<ProjectInnovationComplementarySolutionFunction> projectInnovationComplementarySolutionFunctions =
    new HashSet<ProjectInnovationComplementarySolutionFunction>(0);

  private List<ProjectInnovationComplementarySolutionFunction> complementarySolutionFunctions;

  public ProjectInnovationComplementarySolution() {
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    ProjectInnovationComplementarySolution other = (ProjectInnovationComplementarySolution) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public List<ProjectInnovationComplementarySolutionFunction> getComplementarySolutionFunctions() {
    return complementarySolutionFunctions;
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

  public Set<ProjectInnovationComplementarySolutionFunction> getProjectInnovationComplementarySolutionFunctions() {
    return projectInnovationComplementarySolutionFunctions;
  }


  public RepIndInnovationType getProjectInnovationType() {
    return projectInnovationType;
  }

  public String getShortDescription() {
    return shortDescription;
  }

  public String getShortTitle() {
    return shortTitle;
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


  public void setComplementarySolutionFunctions(
    List<ProjectInnovationComplementarySolutionFunction> complementarySolutionFunctions) {
    this.complementarySolutionFunctions = complementarySolutionFunctions;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setProjectInnovationComplementarySolutionFunctions(
    Set<ProjectInnovationComplementarySolutionFunction> projectInnovationComplementarySolutionFunctions) {
    this.projectInnovationComplementarySolutionFunctions = projectInnovationComplementarySolutionFunctions;
  }

  public void setProjectInnovationType(RepIndInnovationType projectInnovationType) {
    this.projectInnovationType = projectInnovationType;
  }

  public void setShortDescription(String shortDescription) {
    this.shortDescription = shortDescription;
  }


  public void setShortTitle(String shortTitle) {
    this.shortTitle = shortTitle;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  @Override
  public String toString() {
    return "Activity [id=" + this.getId() + ", title=" + title + "]";
  }
}
