package org.cgiar.ccafs.marlo.data.model;

import java.util.List;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class ReportSynthesisPartnershipsByInstitutionTypeDTO implements java.io.Serializable {

  private static final long serialVersionUID = 5982869469556880508L;

  private InstitutionType institutionType;

  private List<ProjectPartnerPartnership> projectPartnerPartnerships;


  public ReportSynthesisPartnershipsByInstitutionTypeDTO() {
  }


  public InstitutionType getInstitutionType() {
    return institutionType;
  }


  public List<ProjectPartnerPartnership> getProjectPartnerPartnerships() {
    return projectPartnerPartnerships;
  }


  public void setInstitutionType(InstitutionType institutionType) {
    this.institutionType = institutionType;
  }


  public void setProjectPartnerPartnerships(List<ProjectPartnerPartnership> projectPartnerPartnerships) {
    this.projectPartnerPartnerships = projectPartnerPartnerships;
  }


  @Override
  public String toString() {
    return "ReportSynthesisPartnershipsByInstitutionTypeDTO [institutionType=" + institutionType
      + ", projectPartnerPartnerships=" + projectPartnerPartnerships + "]";
  }


}

