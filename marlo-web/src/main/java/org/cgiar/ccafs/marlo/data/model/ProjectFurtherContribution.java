package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 4, 2017 9:27:17 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * @author Hermes Jimenez - CCAFS
 */
public class ProjectFurtherContribution extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 2742663860736765203L;

  @Expose
  private CrpProgramOutcome crpProgramOutcome;


  @Expose
  private Project project;

  @Expose
  private Integer year;

  @Expose
  private String description;

  @Expose
  private Integer value;

  public ProjectFurtherContribution() {
  }

  public CrpProgramOutcome getCrpProgramOutcome() {
    return crpProgramOutcome;
  }


  public String getDescription() {
    return description;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public Project getProject() {
    return project;
  }


  public Integer getValue() {
    return value;
  }

  public Integer getYear() {
    return year;
  }

  public void setCrpProgramOutcome(CrpProgramOutcome crpProgramOutcome) {
    this.crpProgramOutcome = crpProgramOutcome;
  }

  public void setDescription(String description) {
    this.description = description;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setValue(Integer value) {
    this.value = value;
  }


  public void setYear(Integer year) {
    this.year = year;
  }


  @Override
  public String toString() {
    return "ProjectFurtherContribution [id=" + this.getId() + ", crpProgramOutcome=" + crpProgramOutcome + ", project="
      + project + ", year=" + year + ", description=" + description + ", value=" + value + "]";
  }


}

