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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;


/**
 * This class represents the Research Area (such as DAPA, AgBio, Soils, etc) in the application
 * Modified by @author nmatovu last on Oct 6, 2016
 */
public class CenterArea extends MarloAuditableEntity implements Serializable, IAuditLog {


  private static final long serialVersionUID = -2457377813686256015L;


  @Expose
  private String name;


  @Expose
  private String acronym;

  @Expose
  private GlobalUnit researchCenter;

  @Expose
  private String color;

  private Set<CrpProgram> researchPrograms = new HashSet<CrpProgram>(0);

  private Set<CenterLeader> researchLeaders = new HashSet<CenterLeader>(0);


  private List<CrpProgram> programs;

  private List<CenterLeader> leaders;

  public CenterArea() {
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
    CenterArea other = (CenterArea) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  /**
   * @return the acronym
   */
  public String getAcronym() {
    return acronym;
  }

  public String getColor() {
    return color;
  }

  public List<CenterLeader> getLeaders() {
    return leaders;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  public List<CrpProgram> getPrograms() {
    return programs;
  }

  public GlobalUnit getResearchCenter() {
    return researchCenter;
  }

  public Set<CenterLeader> getResearchLeaders() {
    return researchLeaders;
  }

  public Set<CrpProgram> getResearchPrograms() {
    return researchPrograms;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  /**
   * @param acronym the acronym to set
   */
  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }


  public void setColor(String color) {
    this.color = color;
  }


  public void setLeaders(List<CenterLeader> leaders) {
    this.leaders = leaders;
  }


  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }


  public void setPrograms(List<CrpProgram> programs) {
    this.programs = programs;
  }


  public void setResearchCenter(GlobalUnit researchCenter) {
    this.researchCenter = researchCenter;
  }


  public void setResearchLeaders(Set<CenterLeader> researchLeaders) {
    this.researchLeaders = researchLeaders;
  }


  public void setResearchPrograms(Set<CrpProgram> researchPrograms) {
    this.researchPrograms = researchPrograms;
  }


  @Override
  public String toString() {
    return "CenterArea [id=" + this.getId() + ", name=" + name + ", acronym=" + acronym + "]";
  }

}
