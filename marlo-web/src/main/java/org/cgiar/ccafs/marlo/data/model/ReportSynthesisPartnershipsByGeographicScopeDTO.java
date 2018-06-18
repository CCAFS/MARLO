package org.cgiar.ccafs.marlo.data.model;

import java.util.List;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class ReportSynthesisPartnershipsByGeographicScopeDTO implements java.io.Serializable {

  private static final long serialVersionUID = 4592872889267610920L;


  private RepIndGeographicScope repIndGeographicScope;

  private List<ProjectPartnerPartnership> projectPartnerPartnerships;

  public ReportSynthesisPartnershipsByGeographicScopeDTO() {
  }


  public List<ProjectPartnerPartnership> getProjectPartnerPartnerships() {
    return projectPartnerPartnerships;
  }


  public RepIndGeographicScope getRepIndGeographicScope() {
    return repIndGeographicScope;
  }


  public void setProjectPartnerPartnerships(List<ProjectPartnerPartnership> projectPartnerPartnerships) {
    this.projectPartnerPartnerships = projectPartnerPartnerships;
  }


  public void setRepIndGeographicScope(RepIndGeographicScope repIndGeographicScope) {
    this.repIndGeographicScope = repIndGeographicScope;
  }


  @Override
  public String toString() {
    return "ReportSynthesisPartnershipsByGeographicScopeDTO [repIndGeographicScope=" + repIndGeographicScope
      + ", projectPartnerPartnerships=" + projectPartnerPartnerships + "]";
  }


}

