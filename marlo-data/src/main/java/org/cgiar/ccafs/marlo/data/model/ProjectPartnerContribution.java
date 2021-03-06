package org.cgiar.ccafs.marlo.data.model;
// Generated Jul 29, 2016 8:50:03 AM by Hibernate Tools 4.3.1.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * ProjectPartnerContribution generated by hbm2java
 */
public class ProjectPartnerContribution extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  /**
   * 
   */
  private static final long serialVersionUID = -4423203403135427412L;


  @Expose
  private ProjectPartner projectPartner;
  @Expose
  private ProjectPartner projectPartnerContributor;

  public ProjectPartnerContribution() {
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
    ProjectPartnerContribution other = (ProjectPartnerContribution) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();

    sb.append("Id : ").append(this.getId());


    return sb.toString();
  }


  public ProjectPartner getProjectPartner() {
    return this.projectPartner;
  }

  public ProjectPartner getProjectPartnerContributor() {
    return this.projectPartnerContributor;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }

  public void setProjectPartner(ProjectPartner projectPartnersByProjectPartnerId) {
    this.projectPartner = projectPartnersByProjectPartnerId;
  }

  public void setProjectPartnerContributor(ProjectPartner projectPartnersByProjectPartnerContributorId) {
    this.projectPartnerContributor = projectPartnersByProjectPartnerContributorId;
  }

  @Override
  public String toString() {
    return "ProjectPartnerContribution [id=" + this.getId() + ", projectPartner=" + projectPartner + "]";
  }


}

