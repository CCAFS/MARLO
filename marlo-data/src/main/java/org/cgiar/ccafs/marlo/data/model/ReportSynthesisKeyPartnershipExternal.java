package org.cgiar.ccafs.marlo.data.model;
// Generated Mar 7, 2019 7:42:16 AM by Hibernate Tools 5.3.6.Final


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * ReportSynthesisKeyPartnershipExternal generated by hbm2java
 */
public class ReportSynthesisKeyPartnershipExternal extends MarloBaseEntity implements java.io.Serializable, IAuditLog {

  private static final long serialVersionUID = 8337149970089974373L;

  @Expose
  private ReportSynthesisKeyPartnership reportSynthesisKeyPartnership;
  @Expose
  private String description;
  @Expose
  private String other;
  @Expose
  private FileDB file;


  private Set<ReportSynthesisKeyPartnershipExternalMainArea> reportSynthesisKeyPartnershipExternalMainAreas =
    new HashSet<ReportSynthesisKeyPartnershipExternalMainArea>(0);


  private Set<ReportSynthesisKeyPartnershipExternalInstitution> reportSynthesisKeyPartnershipExternalInstitutions =
    new HashSet<ReportSynthesisKeyPartnershipExternalInstitution>(0);

  private List<ReportSynthesisKeyPartnershipExternalMainArea> mainAreas;


  private List<ReportSynthesisKeyPartnershipExternalInstitution> institutions;

  public ReportSynthesisKeyPartnershipExternal() {
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
    ReportSynthesisKeyPartnershipExternal other = (ReportSynthesisKeyPartnershipExternal) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public String getDescription() {
    return description;
  }


  public FileDB getFile() {
    return file;
  }


  public List<ReportSynthesisKeyPartnershipExternalInstitution> getInstitutions() {
    return institutions;
  }


  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public List<ReportSynthesisKeyPartnershipExternalMainArea> getMainAreas() {
    return mainAreas;
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


  public String getOther() {
    return other;
  }


  public ReportSynthesisKeyPartnership getReportSynthesisKeyPartnership() {
    return reportSynthesisKeyPartnership;
  }


  public Set<ReportSynthesisKeyPartnershipExternalInstitution> getReportSynthesisKeyPartnershipExternalInstitutions() {
    return reportSynthesisKeyPartnershipExternalInstitutions;
  }


  public Set<ReportSynthesisKeyPartnershipExternalMainArea> getReportSynthesisKeyPartnershipExternalMainAreas() {
    return reportSynthesisKeyPartnershipExternalMainAreas;
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

  public void setDescription(String description) {
    this.description = description;
  }


  public void setFile(FileDB file) {
    this.file = file;
  }


  public void setInstitutions(List<ReportSynthesisKeyPartnershipExternalInstitution> institutions) {
    this.institutions = institutions;
  }


  public void setMainAreas(List<ReportSynthesisKeyPartnershipExternalMainArea> mainAreas) {
    this.mainAreas = mainAreas;
  }


  @Override
  public void setModifiedBy(User modifiedBy) {
  }


  public void setOther(String other) {
    this.other = other;
  }

  public void setReportSynthesisKeyPartnership(ReportSynthesisKeyPartnership reportSynthesisKeyPartnership) {
    this.reportSynthesisKeyPartnership = reportSynthesisKeyPartnership;
  }

  public void setReportSynthesisKeyPartnershipExternalInstitutions(
    Set<ReportSynthesisKeyPartnershipExternalInstitution> reportSynthesisKeyPartnershipExternalInstitutions) {
    this.reportSynthesisKeyPartnershipExternalInstitutions = reportSynthesisKeyPartnershipExternalInstitutions;
  }

  public void setReportSynthesisKeyPartnershipExternalMainAreas(
    Set<ReportSynthesisKeyPartnershipExternalMainArea> reportSynthesisKeyPartnershipExternalMainAreas) {
    this.reportSynthesisKeyPartnershipExternalMainAreas = reportSynthesisKeyPartnershipExternalMainAreas;
  }


}

