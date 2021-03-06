package org.cgiar.ccafs.marlo.data.model;
// Generated Jan 11, 2019 9:52:41 AM by Hibernate Tools 5.3.6.Final

import org.cgiar.ccafs.marlo.data.IAuditLog;

import com.google.gson.annotations.Expose;

/**
 * ProjectPolicyCrossCuttingMarkers generated by hbm2java
 */
public class ReportSynthesisFlagshipProgressCrossCuttingMarker extends MarloBaseEntity
  implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = -5739071488591041675L;

  @Expose
  private CgiarCrossCuttingMarker marker;
  @Expose
  private ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone;
  @Expose
  private RepIndGenderYouthFocusLevel focus;
  @Expose
  private String just;


  public ReportSynthesisFlagshipProgressCrossCuttingMarker() {
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
    ReportSynthesisFlagshipProgressCrossCuttingMarker other = (ReportSynthesisFlagshipProgressCrossCuttingMarker) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public RepIndGenderYouthFocusLevel getFocus() {
    return focus;
  }


  public String getJust() {
    return just;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public CgiarCrossCuttingMarker getMarker() {
    return marker;
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


  public ReportSynthesisFlagshipProgressOutcomeMilestone getReportSynthesisFlagshipProgressOutcomeMilestone() {
    return reportSynthesisFlagshipProgressOutcomeMilestone;
  }


  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((this.getId() == null) ? 0 : this.getId().hashCode());
    return result;
  }


  @Override
  public boolean isActive() {
    return true;
  }


  public void setFocus(RepIndGenderYouthFocusLevel focus) {
    this.focus = focus;
  }


  public void setJust(String just) {
    this.just = just;
  }


  public void setMarker(CgiarCrossCuttingMarker marker) {
    this.marker = marker;
  }


  @Override
  public void setModifiedBy(User modifiedBy) {
  }


  public void setReportSynthesisFlagshipProgressOutcomeMilestone(
    ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone) {
    this.reportSynthesisFlagshipProgressOutcomeMilestone = reportSynthesisFlagshipProgressOutcomeMilestone;
  }


}

