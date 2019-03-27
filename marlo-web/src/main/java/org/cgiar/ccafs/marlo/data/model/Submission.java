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
// Generated Jul 7, 2016 11:23:47 AM by Hibernate Tools 4.3.1.Final


import java.util.Date;

/**
 * Submission generated by hbm2java
 */
public class Submission extends MarloBaseEntity implements java.io.Serializable {


  private static final long serialVersionUID = 235989260663664812L;

  private CrpProgram crpProgram;


  private User user;

  private Date dateTime;


  private String modificationJustification;
  private String cycle;
  private Short year;
  private Project project;
  private User unSubmitUser;
  private Boolean unSubmit;
  private String unSubmitJustification;
  private PowbSynthesis powbSynthesis;
  private ReportSynthesis reportSynthesis;


  public Submission() {
  }


  public CrpProgram getCrpProgram() {
    return this.crpProgram;
  }

  public String getCycle() {
    return this.cycle;
  }

  public Date getDateTime() {
    return this.dateTime;
  }

  public String getModificationJustification() {
    return this.modificationJustification;
  }

  public PowbSynthesis getPowbSynthesis() {
    return powbSynthesis;
  }


  public Project getProject() {
    return project;
  }


  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }


  public String getUnSubmitJustification() {
    return unSubmitJustification;
  }


  public User getUnSubmitUser() {
    return unSubmitUser;
  }

  public User getUser() {
    return this.user;
  }

  public Short getYear() {
    return this.year;
  }


  public Boolean isUnSubmit() {
    return unSubmit;
  }

  public void setCrpProgram(CrpProgram crpProgram) {
    this.crpProgram = crpProgram;
  }

  public void setCycle(String cycle) {
    this.cycle = cycle;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setPowbSynthesis(PowbSynthesis powbSynthesis) {
    this.powbSynthesis = powbSynthesis;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }

  public void setUnSubmit(Boolean unSubmit) {
    this.unSubmit = unSubmit;
  }

  public void setUnSubmitJustification(String unSubmitJustification) {
    this.unSubmitJustification = unSubmitJustification;
  }

  public void setUnSubmitUser(User unSubmitUser) {
    this.unSubmitUser = unSubmitUser;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setYear(Short year) {
    this.year = year;
  }

  @Override
  public String toString() {
    return "Submission [id=" + this.getId() + ", crpProgram=" + crpProgram + ", user=" + user + ", dateTime=" + dateTime
      + ", cycle=" + cycle + ", year=" + year + ", project=" + project + ", unSubmitUser=" + unSubmitUser
      + ", unSubmit=" + unSubmit + ", unSubmitJustification=" + unSubmitJustification + "]";
  }
}

