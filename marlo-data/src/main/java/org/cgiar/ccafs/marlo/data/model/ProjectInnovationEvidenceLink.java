package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 31, 2019 9:21:00 AM by Hibernate Tools 5.3.6.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;
import org.apache.commons.lang3.builder.HashCodeBuilder;


public class ProjectInnovationEvidenceLink extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -8855879487687610305L;
  @Expose
  private Phase phase;
  @Expose
  private ProjectInnovation projectInnovation;
  @Expose
  private String link;

  public ProjectInnovationEvidenceLink() {
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

    ProjectInnovationEvidenceLink other = (ProjectInnovationEvidenceLink) obj;
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


  public ProjectInnovation getProjectInnovation() {
    return projectInnovation;
  }


  @Override
  /*
   * WARNING: because the way hashCode() is implemented on Phase and ProjectInnovation there is a possibility a clash
   * will happen. Let's pray it does not happen...
   */
  public int hashCode() {
    HashCodeBuilder hashBuilder = new HashCodeBuilder();
    hashBuilder.append(this.phase);
    hashBuilder.append(this.projectInnovation);
    hashBuilder.append(this.link);
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


  public void setProjectInnovation(ProjectInnovation projectInnovation) {
    this.projectInnovation = projectInnovation;
  }


}

