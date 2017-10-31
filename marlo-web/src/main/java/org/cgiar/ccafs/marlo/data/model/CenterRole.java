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

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * Modified by @author nmatovu last on Sep 29, 2016
 */
public class CenterRole implements Serializable {


  private static final long serialVersionUID = 5596676652614917686L;


  @Expose
  private Long id;

  private Center researchCenter;
  @Expose
  private GlobalUnit globalUnit;

  @Expose
  private String description;

  @Expose
  private String acronym;

  @Expose
  private Set<CenterUserRole> userRoles = new HashSet<CenterUserRole>(0);

  public CenterRole() {
  }

  public CenterRole(Center researchCenter, String description, String acronym) {
    this.researchCenter = researchCenter;
    this.description = description;
    this.acronym = acronym;

  }

  public CenterRole(Center researchCenter, String description, String acronym, Set<CenterUserRole> userRoles) {
    this.researchCenter = researchCenter;
    this.description = description;
    this.acronym = acronym;

    this.userRoles = userRoles;
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
    CenterRole other = (CenterRole) obj;
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
    return this.acronym;
  }

  public String getDescription() {
    return this.description;
  }

  public GlobalUnit getGlobalUnit() {
    return globalUnit;
  }


  public Long getId() {
    return this.id;
  }

  /**
   * @return the researchCenter
   */
  public Center getResearchCenter() {
    return researchCenter;
  }

  public Set<CenterUserRole> getUserRoles() {
    return this.userRoles;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }


  public void setDescription(String description) {
    this.description = description;
  }

  public void setGlobalUnit(GlobalUnit globalUnit) {
    this.globalUnit = globalUnit;
  }

  public void setId(Long id) {
    this.id = id;
  }

  /**
   * @param researchCenter the researchCenter to set
   */
  public void setResearchCenter(Center researchCenter) {
    this.researchCenter = researchCenter;
  }

  public void setUserRoles(Set<CenterUserRole> userRoles) {
    this.userRoles = userRoles;
  }

  @Override
  public String toString() {
    return id.toString();
  }
}
