package org.cgiar.ccafs.marlo.data.model;
// Generated Apr 30, 2018 10:52:36 AM by Hibernate Tools 3.4.0.CR1

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * ProjectExpectedStudyInstitution generated by hbm2java
 */
public class ProjectExpectedStudyInstitution implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 7717738211831506497L;

  @Expose
  private Long id;

  @Expose
  private Phase phase;

  @Expose
  private ProjectExpectedStudy projectExpectedStudy;

  @Expose
  private Institution institution;


  public ProjectExpectedStudyInstitution() {
  }


  @Override
  public Long getId() {
    return id;
  }


  public Institution getInstitution() {
    return institution;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  @Override
  public String getModificationJustification() {
    return "";
  }

  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return u;
  }

  public Phase getPhase() {
    return phase;
  }

  public ProjectExpectedStudy getProjectExpectedStudy() {
    return projectExpectedStudy;
  }

  @Override
  public boolean isActive() {
    return true;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setInstitution(Institution institution) {
    this.institution = institution;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy) {
    this.projectExpectedStudy = projectExpectedStudy;
  }


}

