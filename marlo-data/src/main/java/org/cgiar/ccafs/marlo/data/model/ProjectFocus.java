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
// Generated Jul 13, 2016 11:45:52 AM by Hibernate Tools 4.3.1.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * ProjectFocus generated by hbm2java
 */
public class ProjectFocus extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  /**
   * s
   */
  private static final long serialVersionUID = 4814612346573890024L;


  @Expose
  private CrpProgram crpProgram;


  @Expose
  private Project project;

  @Expose
  private Phase phase;

  public ProjectFocus() {
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
    ProjectFocus other = (ProjectFocus) obj;
    if (this.isActive() != other.isActive()) {
      return false;
    }
    if (this.getCrpProgram() == null) {
      if (other.getCrpProgram() != null) {
        return false;
      }
    } else if (!this.getCrpProgram().getId().equals(other.getCrpProgram().getId())) {
      return false;
    }
    if (this.getProject() == null) {
      if (other.getProject() != null) {
        return false;
      }
    } else if (!this.getProject().getId().equals(other.getProject().getId())) {
      return false;
    }
    if (this.getPhase() == null) {
      if (other.getPhase() != null) {
        return false;
      }
    } else if (!this.getPhase().getId().equals(other.getPhase().getId())) {
      return false;
    }
    return true;
  }


  public CrpProgram getCrpProgram() {
    return crpProgram;
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

  public Project getProject() {
    return project;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.isActive() ? 1231 : 1237);
    result = prime * result + ((crpProgram == null) ? 0 : crpProgram.hashCode());
    result = prime * result + ((project == null) ? 0 : project.hashCode());
    return result;
  }


  public void setCrpProgram(CrpProgram crpProgram) {
    this.crpProgram = crpProgram;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProject(Project project) {
    this.project = project;
  }


  @Override
  public String toString() {
    return "ProjectFocus [id=" + this.getId() + ", crpProgram=" + crpProgram + ", project=" + project + "]";
  }


}

