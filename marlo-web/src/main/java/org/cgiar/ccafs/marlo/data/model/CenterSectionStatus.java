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

public class CenterSectionStatus extends MarloSoftDeleteableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -5830242271701358980L;

  private CrpProgram researchProgram;

  private CenterOutcome researchOutcome;
  private CenterOutput researchOutput;
  private CenterProject project;
  private CenterDeliverable deliverable;
  private CapacityDevelopment capacityDevelopment;
  private String sectionName;
  private String missingFields;
  private String cycle;
  private Integer year;

  public CenterSectionStatus() {
  }

  public CapacityDevelopment getCapacityDevelopment() {
    return capacityDevelopment;
  }

  public String getCycle() {
    return this.cycle;
  }

  public CenterDeliverable getDeliverable() {
    return deliverable;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  public String getMissingFields() {
    return this.missingFields;
  }

  @Override
  public String getModificationJustification() {
    return "";
  }

  @Override
  public User getModifiedBy() {
    return researchProgram.getModifiedBy();
  }

  public CenterProject getProject() {
    return project;
  }

  public CenterOutcome getResearchOutcome() {
    return researchOutcome;
  }


  public CenterOutput getResearchOutput() {
    return researchOutput;
  }

  public CrpProgram getResearchProgram() {
    return researchProgram;
  }

  public String getSectionName() {
    return this.sectionName;
  }

  public Integer getYear() {
    return this.year;
  }

  public void setCapacityDevelopment(CapacityDevelopment capacityDevelopment) {
    this.capacityDevelopment = capacityDevelopment;
  }

  public void setCycle(String cycle) {
    this.cycle = cycle;
  }

  public void setDeliverable(CenterDeliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setMissingFields(String missingFields) {
    this.missingFields = missingFields;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {
    User u = new User();
    u.setId(new Long(3));
    u = modifiedBy;
  }

  public void setProject(CenterProject project) {
    this.project = project;
  }

  public void setResearchOutcome(CenterOutcome researchOutcome) {
    this.researchOutcome = researchOutcome;
  }

  public void setResearchOutput(CenterOutput researchOutput) {
    this.researchOutput = researchOutput;
  }

  public void setResearchProgram(CrpProgram researchProgram) {
    this.researchProgram = researchProgram;
  }

  public void setSectionName(String sectionName) {
    this.sectionName = sectionName;
  }

  public void setYear(Integer year) {
    this.year = year;
  }

  @Override
  public String toString() {
    return "CenterSectionStatus [id=" + this.getId() + ", researchProgram=" + researchProgram + ", researchOutcome="
      + researchOutcome + ", researchOutput=" + researchOutput + ", project=" + project + ", deliverable=" + deliverable
      + ", sectionName=" + sectionName + ", cycle=" + cycle + ", year=" + year + "]";
  }


}

