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
import java.util.Set;

import com.google.gson.annotations.Expose;

public class InstitutionType extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  static final long serialVersionUID = -943657365260109270L;

  @Expose
  private String name;

  @Expose
  private String acronym;

  @Expose
  private String description;

  @Expose
  private Boolean old;

  @Expose
  private Boolean isLegacy;

  @Expose
  private Boolean subDepartmentActive;

  @Expose
  private RepIndOrganizationType repIndOrganizationType;


  private Set<Institution> institutions = new HashSet<Institution>(0);


  public InstitutionType() {
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
    InstitutionType other = (InstitutionType) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public String getAcronym() {
    return this.acronym;
  }


  public String getDescription() {
    return description;
  }

  public Set<Institution> getInstitutions() {
    return institutions;
  }

  public Boolean getIsLegacy() {
    return isLegacy;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();

    sb.append("Id : ").append(this.getId());


    return sb.toString();
  }

  @Override
  public String getModificationJustification() {

    return "";
  }

  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return u;
  }

  public String getName() {
    return this.name;
  }

  public Boolean getOld() {
    return old;
  }

  public RepIndOrganizationType getRepIndOrganizationType() {
    return repIndOrganizationType;
  }

  public Boolean getSubDepartmentActive() {
    return subDepartmentActive;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  @Override
  public boolean isActive() {

    return true;
  }

  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setInstitutions(Set<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setIsLegacy(Boolean isLegacy) {
    this.isLegacy = isLegacy;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {

  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOld(Boolean old) {
    this.old = old;
  }

  public void setRepIndOrganizationType(RepIndOrganizationType repIndOrganizationType) {
    this.repIndOrganizationType = repIndOrganizationType;
  }

  public void setSubDepartmentActive(Boolean subDepartmentActive) {
    this.subDepartmentActive = subDepartmentActive;
  }

  @Override
  public String toString() {
    return "InstitutionType [id=" + this.getId() + ", name=" + name + ", acronym=" + acronym + ", subDepartmentActive="
      + subDepartmentActive + ", repIndOrganizationType=" + repIndOrganizationType + "]";
  }


}
