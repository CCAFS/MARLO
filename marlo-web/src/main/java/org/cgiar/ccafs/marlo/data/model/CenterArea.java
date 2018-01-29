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
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;


/**
 * This class represents the Research Area (such as DAPA, AgBio, Soils, etc) in the application
 * Modified by @author nmatovu last on Oct 6, 2016
 */
public class CenterArea implements Serializable, IAuditLog {


  private static final long serialVersionUID = -2457377813686256015L;


  @Expose
  private Long id;

  @Expose
  private String name;


  @Expose
  private String acronym;

  @Expose
  private GlobalUnit researchCenter;


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

  @Expose
  private String color;


  private Set<CenterProgram> researchPrograms = new HashSet<CenterProgram>(0);

  private Set<CenterLeader> researchLeaders = new HashSet<CenterLeader>(0);


  private List<CenterProgram> programs;

  private List<CenterLeader> leaders;


  /**
   * 
   */
  public CenterArea() {
    super();
    // TODO Auto-generated constructor stub
  }


  /**
   * @param name
   */
  public CenterArea(String name) {
    super();
    this.name = name;
  }


  /**
   * @param name
   * @param acronym
   * @param active
   * @param createdBy
   * @param modifiedBy
   */
  public CenterArea(String name, String acronym, boolean active) {
    super();
    this.name = name;
    this.acronym = acronym;
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
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
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


  public Date getActiveSince() {
    return activeSince;
  }

  public String getColor() {
    return color;
  }

  public User getCreatedBy() {
    return createdBy;
  }


  @Override
  public Long getId() {
    return this.id;
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

  @Override
  public String getModificationJustification() {
    return modificationJustification;
  }


  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  public List<CenterProgram> getPrograms() {
    return programs;
  }

  public GlobalUnit getResearchCenter() {
    return researchCenter;
  }

  public Set<CenterLeader> getResearchLeaders() {
    return researchLeaders;
  }


  /**
   * @return the researchPrograms
   */
  public Set<CenterProgram> getResearchPrograms() {
    return researchPrograms;
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

  /**
   * @param acronym the acronym to set
   */
  public void setAcronym(String acronym) {
    this.acronym = acronym;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }


  public void setColor(String color) {
    this.color = color;
  }


  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }


  /**
   * @param id the id to set
   */
  public void setId(Long id) {
    this.id = id;
  }


  public void setLeaders(List<CenterLeader> leaders) {
    this.leaders = leaders;
  }


  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }


  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }


  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }


  public void setPrograms(List<CenterProgram> programs) {
    this.programs = programs;
  }


  public void setResearchCenter(GlobalUnit researchCenter) {
    this.researchCenter = researchCenter;
  }


  public void setResearchLeaders(Set<CenterLeader> researchLeaders) {
    this.researchLeaders = researchLeaders;
  }


  /**
   * @param researchPrograms the researchPrograms to set
   */
  public void setResearchPrograms(Set<CenterProgram> researchPrograms) {
    this.researchPrograms = researchPrograms;
  }

  @Override
  public String toString() {
    return "CenterArea [id=" + id + ", name=" + name + ", acronym=" + acronym + "]";
  }

}
