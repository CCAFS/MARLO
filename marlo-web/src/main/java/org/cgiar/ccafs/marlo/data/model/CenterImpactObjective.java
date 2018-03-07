package org.cgiar.ccafs.marlo.data.model;
// Generated Oct 13, 2016 1:24:28 PM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;


public class CenterImpactObjective extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = -8979609328318401070L;


  @Expose
  private CenterObjective researchObjective;


  @Expose
  private CenterImpact researchImpact;


  public CenterImpactObjective() {
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
    CenterImpactObjective other = (CenterImpactObjective) obj;
    if (this.getResearchImpact() == null) {
      if (other.getResearchImpact() != null) {
        return false;
      }
    } else if (!this.getResearchImpact().equals(other.getResearchImpact())) {
      return false;
    }
    if (this.getResearchObjective() == null) {
      if (other.getResearchObjective() != null) {
        return false;
      }
    } else if (!this.getResearchObjective().getId().equals(other.getResearchObjective().getId())) {
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


  public CenterImpact getResearchImpact() {
    return researchImpact;
  }

  public CenterObjective getResearchObjective() {
    return researchObjective;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (this.isActive() ? 1231 : 1237);
    result = prime * result + ((researchImpact == null) ? 0 : researchImpact.hashCode());
    result = prime * result + ((researchObjective == null) ? 0 : researchObjective.hashCode());
    return result;
  }

  public void setResearchImpact(CenterImpact researchImpact) {
    this.researchImpact = researchImpact;
  }

  public void setResearchObjective(CenterObjective researchObjective) {
    this.researchObjective = researchObjective;
  }

  @Override
  public String toString() {
    return "CenterImpactObjective [id=" + this.getId() + ", researchObjective=" + researchObjective
      + ", researchImpact=" + researchImpact + "]";
  }


}

