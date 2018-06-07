package org.cgiar.ccafs.marlo.data.model;
// Generated May 31, 2018 4:07:34 PM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * ReportSynthesisCrossCuttingDimensionInnovation generated by hbm2java
 */
public class ReportSynthesisCrossCuttingDimensionInnovation extends MarloAuditableEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -4891984481343017041L;

  @Expose
  private ProjectInnovation projectInnovation;

  @Expose
  private ReportSynthesisCrossCuttingDimension reportSynthesisCrossCuttingDimension;

  public ReportSynthesisCrossCuttingDimensionInnovation() {
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
    ReportSynthesisCrossCuttingDimensionInnovation other = (ReportSynthesisCrossCuttingDimensionInnovation) obj;
    if (this.getProjectInnovation() == null) {
      if (other.getProjectInnovation() != null) {
        return false;
      }
    } else if (!this.getProjectInnovation().equals(other.getProjectInnovation())) {
      return false;
    }
    if (this.getReportSynthesisCrossCuttingDimension() == null) {
      if (other.getReportSynthesisCrossCuttingDimension() != null) {
        return false;
      }
    } else if (!this.getReportSynthesisCrossCuttingDimension()
      .equals(other.getReportSynthesisCrossCuttingDimension())) {
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

  public ProjectInnovation getProjectInnovation() {
    return projectInnovation;
  }


  public ReportSynthesisCrossCuttingDimension getReportSynthesisCrossCuttingDimension() {
    return reportSynthesisCrossCuttingDimension;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((projectInnovation == null) ? 0 : projectInnovation.hashCode());
    result = prime * result
      + ((reportSynthesisCrossCuttingDimension == null) ? 0 : reportSynthesisCrossCuttingDimension.hashCode());
    return result;
  }

  public void setProjectInnovation(ProjectInnovation projectInnovation) {
    this.projectInnovation = projectInnovation;
  }


  public void
    setReportSynthesisCrossCuttingDimension(ReportSynthesisCrossCuttingDimension reportSynthesisCrossCuttingDimension) {
    this.reportSynthesisCrossCuttingDimension = reportSynthesisCrossCuttingDimension;
  }


}

