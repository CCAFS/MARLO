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
public class ProjectLocationElementType implements java.io.Serializable {


  private static final long serialVersionUID = -127486425529773813L;


  private Long id;


  private LocElementType locElementType;


  private Project project;


  private Boolean isGlobal;


  public ProjectLocationElementType() {
  }


  public ProjectLocationElementType(LocElementType locElementType, Project project, Boolean isGlobal) {
    this.locElementType = locElementType;
    this.project = project;
    this.isGlobal = isGlobal;
  }


  public Long getId() {
    return id;
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

  public void setId(Long id) {
    this.id = id;
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

}
