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
import java.util.Set;

import com.google.gson.annotations.Expose;


/**
 * Modified by @author nmatovu last on Oct 9, 2016
 */
public class CenterTopic extends MarloAuditableEntity implements Serializable, IAuditLog {


  private static final long serialVersionUID = 365817271325565483L;

  /**
   * The research topic description text.
   */
  @Expose
  private String researchTopic;


  /**
   * The research program related to this research topic or flagship project.
   */
  @Expose
  private CrpProgram researchProgram;

  @Expose
  private String color;

  @Expose
  private String shortName;

  @Expose
  private Integer order;


  private Set<CenterOutcome> researchOutcomes = new HashSet<>(0);


  /**
   * 
   */
  public CenterTopic() {
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
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public String getColor() {
    return color;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public Integer getOrder() {
    return order;
  }

  public Set<CenterOutcome> getResearchOutcomes() {
    return researchOutcomes;
  }


  public CrpProgram getResearchProgram() {
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
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setColor(String color) {
    this.color = color;
  }


  public void setOrder(Integer order) {
    this.order = order;
  }


  public void setResearchOutcomes(Set<CenterOutcome> researchOutcomes) {
    this.researchOutcomes = researchOutcomes;
  }

  public void setResearchProgram(CrpProgram researchProgram) {
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

  @Override
  public String toString() {
    return "CenterTopic [id=" + this.getId() + ", researchTopic=" + researchTopic + ", researchProgram="
      + researchProgram + ", shortName=" + shortName + "]";
  }


}
