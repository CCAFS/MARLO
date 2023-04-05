package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class ProjectExpectedStudyReference extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -8855879487687610305L;
  @Expose
  private Phase phase;
  @Expose
  private ProjectExpectedStudy projectExpectedStudy;
  @Expose
  private String reference;
  @Expose
  private String link;

  public ProjectExpectedStudyReference() {
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

    ProjectExpectedStudyReference other = (ProjectExpectedStudyReference) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }

    return true;
  }

  public String getLink() {
    return link;
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

  public String getReference() {
    return reference;
  }


  @Override
  /*
   * WARNING: because the way hashCode() is implemented on Phase and ProjectExpectedStudy there is a possibility a clash
   * will happen. Let's pray it does not happen...
   */
  public int hashCode() {
    HashCodeBuilder hashBuilder = new HashCodeBuilder();
    hashBuilder.append(this.phase);
    hashBuilder.append(this.projectExpectedStudy);
    hashBuilder.append(this.reference);
    return hashBuilder.hashCode();
  }


  @Override
  public boolean isActive() {
    return true;
  }

  public void setLink(String link) {
    this.link = link;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {

  }

  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy) {
    this.projectExpectedStudy = projectExpectedStudy;
  }

  public void setReference(String link) {
    this.reference = link;
  }

}