package org.cgiar.ccafs.marlo.data.model;

import java.util.List;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class ReportSynthesisPartnershipsByRepIndOrganizationTypeDTO implements java.io.Serializable {

  private static final long serialVersionUID = 5982869469556880508L;

  private RepIndOrganizationType repIndOrganizationType;

  private List<ProjectPartnerPartnership> projectPartnerPartnerships;


  public ReportSynthesisPartnershipsByRepIndOrganizationTypeDTO() {
  }


  public List<ProjectPartnerPartnership> getProjectPartnerPartnerships() {
    return projectPartnerPartnerships;
  }


  public RepIndOrganizationType getRepIndOrganizationType() {
    return repIndOrganizationType;
  }


  public void setProjectPartnerPartnerships(List<ProjectPartnerPartnership> projectPartnerPartnerships) {
    this.projectPartnerPartnerships = projectPartnerPartnerships;
  }


  public void setRepIndOrganizationType(RepIndOrganizationType repIndOrganizationType) {
    this.repIndOrganizationType = repIndOrganizationType;
  }


  @Override
  public String toString() {
    return "ReportSynthesisPartnershipsByInstitutionTypeDTO [repIndOrganizationType=" + repIndOrganizationType
      + ", projectPartnerPartnerships=" + projectPartnerPartnerships + "]";
  }


}

