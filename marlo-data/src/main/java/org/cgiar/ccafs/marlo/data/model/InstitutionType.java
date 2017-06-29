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

public class InstitutionType implements java.io.Serializable, IAuditLog {


  static final long serialVersionUID = -943657365260109270L;

  @Expose
  private Long id;

  @Expose
  private String name;

  @Expose
  private String acronym;

  @Expose
  private String description;

  @Expose
  private Boolean old;

  private Set<Institution> institutions = new HashSet<Institution>(0);


  public InstitutionType() {
  }


  public InstitutionType(String name) {
    this.name = name;
  }


  public InstitutionType(String name, String acronym, Set<Institution> institutions) {
    this.name = name;
    this.acronym = acronym;
    this.institutions = institutions;
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
    return description;
  }

  @Override
  public Long getId() {
    return this.id;
  }

  public Set<Institution> getInstitutions() {
    return institutions;
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

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
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

  public void setId(Long id) {
    this.id = id;
  }

  public void setInstitutions(Set<Institution> institutions) {
    this.institutions = institutions;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOld(Boolean old) {
    this.old = old;
  }

  @Override
  public String toString() {
    return id.toString();
  }
}
