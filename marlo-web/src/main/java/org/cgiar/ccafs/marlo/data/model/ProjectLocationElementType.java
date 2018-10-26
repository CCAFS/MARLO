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

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectLocationElementType extends MarloBaseEntity implements java.io.Serializable {


  private static final long serialVersionUID = -127486425529773813L;

  private LocElementType locElementType;


  private Project project;


  private Boolean isGlobal;


  public ProjectLocationElementType() {
  }

  public Boolean getIsGlobal() {
    return isGlobal;
  }

  public LocElementType getLocElementType() {
    return locElementType;
  }

  public Project getProject() {
    return project;
  }

  public void setIsGlobal(Boolean isGlobal) {
    this.isGlobal = isGlobal;
  }

  public void setLocElementType(LocElementType locElementType) {
    this.locElementType = locElementType;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  @Override
  public String toString() {
    return "ProjectLocationElementType [id=" + this.getId() + ", locElementType=" + locElementType + ", project="
      + project + ", isGlobal=" + isGlobal + "]";
  }

}
