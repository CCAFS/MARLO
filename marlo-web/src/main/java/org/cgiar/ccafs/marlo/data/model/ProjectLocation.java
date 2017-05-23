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

import java.util.Date;

import com.google.gson.annotations.Expose;


/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectLocation implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 7097495494769482469L;


  @Expose
  private Long id;

  @Expose
  private LocElement locElement;

  @Expose
  private Project project;

  @Expose
  private boolean active;


  private boolean selected;


  @Expose
  private boolean scope;


  @Expose
  private LocElementType locElementType;
  @Expose
  private Date activeSince;

  @Expose
  private User createdBy;


  @Expose
  private User modifiedBy;


  @Expose
  private String modificationJustification;


  public ProjectLocation() {
  }


  public ProjectLocation(LocElement locElement, Project project, boolean active, Date activeSince, User createdBy,
    User modifiedBy, String modificationJustification) {
    this.locElement = locElement;
    this.project = project;
    this.active = active;
    this.activeSince = activeSince;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.modificationJustification = modificationJustification;
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
    if (id == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public Date getActiveSince() {
    return activeSince;
  }


  public User getCreatedBy() {
    return createdBy;
  }

  @Override
  public Long getId() {
    return id;
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

  @Override
  public String getModificationJustification() {
    return modificationJustification;
  }

  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  public Project getProject() {
    return project;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  public boolean isScope() {
    return scope;
  }

  public boolean isSelected() {
    return selected;
  }


  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setId(Long id) {
    this.id = id;
  }


  public void setLocElement(LocElement locElement) {
    this.locElement = locElement;
  }


  public void setLocElementType(LocElementType locElementType) {
    this.locElementType = locElementType;
  }


  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setScope(boolean scope) {
    this.scope = scope;
  }

  public void setSelected(boolean selected) {
    this.selected = selected;
  }


  @Override
  public String toString() {
    try {
      return this.getLocElement().getName();
    } catch (Exception e) {
      return this.getLocElementType().getName();
    }
  }

}
