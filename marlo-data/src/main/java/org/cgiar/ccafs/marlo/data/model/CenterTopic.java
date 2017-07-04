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
 * Modified by @author nmatovu last on Oct 9, 2016
 */
public class CenterTopic implements Serializable, IAuditLog {


  private static final long serialVersionUID = 365817271325565483L;


  @Expose
  private Long id;

  /**
   * The research topic description text.
   */
  @Expose
  private String researchTopic;


  /**
   * The research program related to this research topic or flagship project.
   */
  @Expose
  private CenterProgram researchProgram;

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


  @Expose
  private String shortName;


  private Set<CenterOutcome> researchOutcomes = new HashSet<>(0);

  /**
   * 
   */
  public CenterTopic() {
    super();
    // TODO Auto-generated constructor stub
  }


  /**
   * @param researchTopic
   * @param researchProgram
   */
  public CenterTopic(String researchTopic, CenterProgram researchProgram) {
    super();
    this.researchTopic = researchTopic;
    this.researchProgram = researchProgram;
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
    CenterTopic other = (CenterTopic) obj;
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    return true;
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


  /**
   * @return the id
   */
  @Override
  public Long getId() {
    return id;
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

  public Set<CenterOutcome> getResearchOutcomes() {
    return researchOutcomes;
  }

  /**
   * @return the researchProgram
   */
  public CenterProgram getResearchProgram() {
    return researchProgram;
  }

  /**
   * @return the researchTopic
   */
  public String getResearchTopic() {
    return researchTopic;
  }

  public String getShortName() {
    return shortName;
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


  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }


  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }


  public void setResearchOutcomes(Set<CenterOutcome> researchOutcomes) {
    this.researchOutcomes = researchOutcomes;
  }


  /**
   * @param researchProgram the researchProgram to set
   */
  public void setResearchProgram(CenterProgram researchProgram) {
    this.researchProgram = researchProgram;
  }


  /**
   * @param researchTopic the researchTopic to set
   */
  public void setResearchTopic(String researchTopic) {
    this.researchTopic = researchTopic;
  }

  public void setShortName(String shortName) {
    this.shortName = shortName;
  }


}
