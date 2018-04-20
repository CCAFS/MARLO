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
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectLocation extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 7097495494769482469L;

  @Expose
  private LocElement locElement;

  @Expose
  private Project project;

  @Expose
  private boolean scope;


  @Expose
  private LocElementType locElementType;

  @Expose
  private Phase phase;


  public ProjectLocation() {
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    ProjectLocation other = (ProjectLocation) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public LocElement getLocElement() {
    return locElement;
  }

  public LocElementType getLocElementType() {
    return locElementType;
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

  public boolean isScope() {
    return scope;
  }


  public void setLocElement(LocElement locElement) {
    this.locElement = locElement;
  }


  public void setLocElementType(LocElementType locElementType) {
    this.locElementType = locElementType;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setScope(boolean scope) {
    this.scope = scope;
  }


  @Override
  public String toString() {
    return "ProjectLocation [id=" + this.getId() + ", locElement=" + locElement + ", project=" + project + ", scope="
      + scope + ", locElementType=" + locElementType + "]";
  }

}
