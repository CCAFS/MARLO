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

/**
 * 
 */
package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * Modified by @author nmatovu last on Sep 29, 2016
 */
public class Center implements Serializable, IAuditLog {


  private static final long serialVersionUID = -5311843838644643688L;


  @Expose
  private Long id;

  @Expose
  private String name;


  @Expose
  private String acronym;

  @Expose
  private boolean active;


  @Expose
  private Date activeSince;

  @Expose
  private User createdBy;


  @Expose
  private User modifiedBy;


  @Expose
  private String modificationJustification;


  private Set<CenterUser> centerUsers = new HashSet<CenterUser>(0);


  private Set<CenterArea> researchAreas = new HashSet<CenterArea>(0);


  private Set<CenterObjective> researchObjectives = new HashSet<CenterObjective>(0);


  private Set<CenterCustomParameter> centerCustomParameters = new HashSet<CenterCustomParameter>(0);


  public Center() {
  }

  public Center(String name) {
    this.name = name;
  }


  /**
   * @param name
   * @param acronym
   * @param roles
   */
  public Center(String name, String acronym) {
    super();
    this.name = name;
    this.acronym = acronym;
  }

  /**
   * @param name
   * @param acronym
   * @param centerUsers
   */
  public Center(String name, String acronym, Set<CenterUser> centerUsers) {
    super();
    this.name = name;
    this.acronym = acronym;
    this.centerUsers = centerUsers;
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
    Center other = (Center) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
  }

  public String getAcronym() {
    return acronym;
  }

  public Date getActiveSince() {
    return activeSince;
  }

  public Set<CenterCustomParameter> getCenterCustomParameters() {
    return centerCustomParameters;
  }

  /**
   * @return the centerUsers
   */
  public Set<CenterUser> getCenterUsers() {
    return centerUsers;
  }

  public User getCreatedBy() {
    return createdBy;
  }

  @Override
  public Long getId() {
    return this.id;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public String getModificationJustification() {
    return modificationJustification;
  }


  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }


  public String getName() {
    return this.name;
  }

  public Set<CenterArea> getResearchAreas() {
    return researchAreas;
  }

  public Set<CenterObjective> getResearchObjectives() {
    return researchObjectives;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }


  @Override
  public boolean isActive() {
    return active;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }


  public void setCenterCustomParameters(Set<CenterCustomParameter> centerCustomParameters) {
    this.centerCustomParameters = centerCustomParameters;
  }


  /**
   * @param centerUsers the centerUsers to set
   */
  public void setCenterUsers(Set<CenterUser> centerUsers) {
    this.centerUsers = centerUsers;
  }


  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }


  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }


  public void setName(String name) {
    this.name = name;
  }

  public void setResearchAreas(Set<CenterArea> researchAreas) {
    this.researchAreas = researchAreas;
  }

  public void setResearchObjectives(Set<CenterObjective> researchObjectives) {
    this.researchObjectives = researchObjectives;
  }

  @Override
  public String toString() {
    return id.toString();
  }

}
