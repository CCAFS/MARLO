package org.cgiar.ccafs.marlo.data.model;
// Generated Dec 5, 2016 1:23:27 PM by Hibernate Tools 4.3.1.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * ProjectHighligthsCountry generated by hbm2java
 */
public class ProjectHighligthCountry implements java.io.Serializable, IAuditLog {


  /**
   * 
   */
  private static final long serialVersionUID = 3040210263258419226L;
  @Expose
  private Integer id;

  private ProjectHighligth projectHighligth;
  @Expose
  private LocElement locElement;

  public ProjectHighligthCountry() {
  }

  public ProjectHighligthCountry(ProjectHighligth projectHighligth, LocElement locElement) {
    this.projectHighligth = projectHighligth;
    this.locElement = locElement;
  }

  @Override
  public Integer getId() {
    return this.id;
  }

  public LocElement getLocElement() {
    return locElement;
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


  public ProjectHighligth getProjectHighligth() {
    return projectHighligth;
  }


  @Override
  public boolean isActive() {

    return true;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setLocElement(LocElement locElement) {
    this.locElement = locElement;
  }

  public void setProjectHighligth(ProjectHighligth projectHighligth) {
    this.projectHighligth = projectHighligth;
  }


}

