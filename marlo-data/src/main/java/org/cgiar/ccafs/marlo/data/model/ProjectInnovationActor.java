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

import com.google.gson.annotations.Expose;

public class ProjectInnovationActor extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -963914989396761020L;

  @Expose
  private ProjectInnovation projectInnovation;
  @Expose
  private Phase phase;
  @Expose
  private Actor actor;
  @Expose
  private Boolean womenYouth;
  @Expose
  private Boolean womenNotYouth;
  @Expose
  private Boolean menYouth;
  @Expose
  private Boolean menNotYouth;
  @Expose
  private Boolean nonbinaryYouth;
  @Expose
  private Boolean nonbinaryNotYouth;
  @Expose
  private Boolean sexAgeNotApply;
  @Expose
  private Integer menYouthNumber;
  @Expose
  private Integer menNonYouthNumber;
  @Expose
  private Integer womenYouthNumber;
  @Expose
  private Integer womenNonYouthNumber;
  @Expose
  private Integer total;
  @Expose
  private String other;

  public ProjectInnovationActor() {
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    ProjectInnovationActor other = (ProjectInnovationActor) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public Actor getActor() {
    return actor;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public Integer getMenNonYouthNumber() {
    return menNonYouthNumber;
  }

  public Boolean getMenNotYouth() {
    return menNotYouth;
  }

  public Boolean getMenYouth() {
    return menYouth;
  }

  public Integer getMenYouthNumber() {
    return menYouthNumber;
  }

  public Boolean getNonbinaryNotYouth() {
    return nonbinaryNotYouth;
  }

  public Boolean getNonbinaryYouth() {
    return nonbinaryYouth;
  }

  public String getOther() {
    return other;
  }

  public Phase getPhase() {
    return phase;
  }

  public ProjectInnovation getProjectInnovation() {
    return projectInnovation;
  }

  public Boolean getSexAgeNotApply() {
    return sexAgeNotApply;
  }

  public Integer getTotal() {
    return total;
  }

  public Integer getWomenNonYouthNumber() {
    return womenNonYouthNumber;
  }

  public Boolean getWomenNotYouth() {
    return womenNotYouth;
  }

  public Boolean getWomenYouth() {
    return womenYouth;
  }

  public Integer getWomenYouthNumber() {
    return womenYouthNumber;
  }

  public void setActor(Actor actor) {
    this.actor = actor;
  }

  public void setMenNonYouthNumber(Integer menNonYouthNumber) {
    this.menNonYouthNumber = menNonYouthNumber;
  }

  public void setMenNotYouth(Boolean menNotYouth) {
    this.menNotYouth = menNotYouth;
  }

  public void setMenYouth(Boolean menYouth) {
    this.menYouth = menYouth;
  }

  public void setMenYouthNumber(Integer menYouthNumber) {
    this.menYouthNumber = menYouthNumber;
  }

  public void setNonbinaryNotYouth(Boolean nonbinaryNotYouth) {
    this.nonbinaryNotYouth = nonbinaryNotYouth;
  }

  public void setNonbinaryYouth(Boolean nonbinaryYouth) {
    this.nonbinaryYouth = nonbinaryYouth;
  }

  public void setOther(String other) {
    this.other = other;
  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProjectInnovation(ProjectInnovation projectInnovation) {
    this.projectInnovation = projectInnovation;
  }

  public void setSexAgeNotApply(Boolean sexAgeNotApply) {
    this.sexAgeNotApply = sexAgeNotApply;
  }

  public void setTotal(Integer total) {
    this.total = total;
  }

  public void setWomenNonYouthNumber(Integer womenNonYouthNumber) {
    this.womenNonYouthNumber = womenNonYouthNumber;
  }

  public void setWomenNotYouth(Boolean womenNotYouth) {
    this.womenNotYouth = womenNotYouth;
  }

  public void setWomenYouth(Boolean womenYouth) {
    this.womenYouth = womenYouth;
  }

  public void setWomenYouthNumber(Integer womenYouthNumber) {
    this.womenYouthNumber = womenYouthNumber;
  }

  @Override
  public String toString() {
    return "Actors [id=" + this.getId() + ", innovationId=" + projectInnovation.getId() + "]";
  }
}