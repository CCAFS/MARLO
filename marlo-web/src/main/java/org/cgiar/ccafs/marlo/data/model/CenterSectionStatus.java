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

public class CenterSectionStatus implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -5830242271701358980L;


  private Long id;
  private CenterProgram researchProgram;
  private CenterOutcome researchOutcome;
  private CenterOutput researchOutput;
  private CenterProject project;
  private CenterDeliverable deliverable;
  private String sectionName;
  private String missingFields;
  private String cycle;
  private Integer year;

  public CenterSectionStatus() {
  }

  public CenterSectionStatus(CenterProgram researchProgram, CenterOutcome researchOutcome, CenterOutput researchOutput,
    String sectionName, String missingFields, String cycle, Integer year) {
    this.researchProgram = researchProgram;
    this.researchOutcome = researchOutcome;
    this.researchOutput = researchOutput;
    this.sectionName = sectionName;
    this.missingFields = missingFields;
    this.cycle = cycle;
    this.year = year;
  }

  public CenterSectionStatus(String sectionName) {
    this.sectionName = sectionName;
  }

  public String getCycle() {
    return this.cycle;
  }

  public CenterDeliverable getDeliverable() {
    return deliverable;
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

  public CenterProgram getResearchProgram() {
    return researchProgram;
  }

  public String getSectionName() {
    return this.sectionName;
  }

  public Integer getYear() {
    return this.year;
  }

  @Override
  public boolean isActive() {
    return true;
  }

  public void setCycle(String cycle) {
    this.cycle = cycle;
  }

  public void setDeliverable(CenterDeliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setMissingFields(String missingFields) {
    this.missingFields = missingFields;
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

  public void setResearchProgram(CenterProgram researchProgram) {
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
    return "CenterSectionStatus [id=" + id + ", researchProgram=" + researchProgram + ", researchOutcome="
      + researchOutcome + ", researchOutput=" + researchOutput + ", project=" + project + ", deliverable=" + deliverable
      + ", sectionName=" + sectionName + ", cycle=" + cycle + ", year=" + year + "]";
  }


}

