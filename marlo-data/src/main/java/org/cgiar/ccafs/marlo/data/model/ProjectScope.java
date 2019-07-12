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
public class ProjectScope extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  /**
   * 
   */
  private static final long serialVersionUID = 1054158126894011294L;

  @Expose
  private LocElementType locElementType;

  @Expose
  private Project project;

  public ProjectScope() {
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
    ProjectScope other = (ProjectScope) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
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

  public Project getProject() {
    return project;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setLocElementType(LocElementType locElementType) {
    this.locElementType = locElementType;
  }

  public void setProject(Project project) {
    this.project = project;
  }


  @Override
  public String toString() {
    return "ProjectScope [id=" + this.getId() + ", locElementType=" + locElementType + ", project=" + project + "]";
  }

}
