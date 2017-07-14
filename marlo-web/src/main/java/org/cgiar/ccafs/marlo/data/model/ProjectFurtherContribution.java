package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 4, 2017 9:27:17 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;

import com.google.gson.annotations.Expose;

/**
 * @author Hermes Jimenez - CCAFS
 */
public class ProjectFurtherContribution implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 2742663860736765203L;

  @Expose
  private Long id;

  @Expose
  private User modifiedBy;

  @Expose
  private User createdBy;

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

  @Expose
  private boolean active;

  @Expose
  private Date activeSince;

  @Expose
  private String modificationJustification;


  public ProjectFurtherContribution() {
  }


  public ProjectFurtherContribution(User modifiedBy, User createdBy, CrpProgramOutcome crpProgramOutcome,
    Project project, Integer year, String description, Integer value, boolean active, Date activeSince,
    String modificationJustification) {
    this.modifiedBy = modifiedBy;
    this.createdBy = createdBy;
    this.crpProgramOutcome = crpProgramOutcome;
    this.project = project;
    this.year = year;
    this.description = description;
    this.value = value;
    this.active = active;
    this.activeSince = activeSince;
    this.modificationJustification = modificationJustification;
  }


  public Date getActiveSince() {
    return activeSince;
  }


  public User getCreatedBy() {
    return createdBy;
  }


  public CrpProgramOutcome getCrpProgramOutcome() {
    return crpProgramOutcome;
  }


  public String getDescription() {
    return description;
  }


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


  @Override
  public String getModificationJustification() {
    return modificationJustification;
  }


  @Override
  public User getModifiedBy() {
    return modifiedBy;
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

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setCrpProgramOutcome(CrpProgramOutcome crpProgramOutcome) {
    this.crpProgramOutcome = crpProgramOutcome;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }


  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
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


}

