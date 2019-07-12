package org.cgiar.ccafs.marlo.data.model;

import java.util.List;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class ReportSynthesisStudiesByOrganizationTypeDTO implements java.io.Serializable {

  private static final long serialVersionUID = -7845322065326169339L;


  private RepIndOrganizationType repIndOrganizationType;

  private List<ProjectExpectedStudy> projectExpectedStudies;


  public ReportSynthesisStudiesByOrganizationTypeDTO() {
  }


  public List<ProjectExpectedStudy> getProjectExpectedStudies() {
    return projectExpectedStudies;
  }


  public RepIndOrganizationType getRepIndOrganizationType() {
    return repIndOrganizationType;
  }


  public void setProjectExpectedStudies(List<ProjectExpectedStudy> projectExpectedStudies) {
    this.projectExpectedStudies = projectExpectedStudies;
  }


  public void setRepIndOrganizationType(RepIndOrganizationType repIndOrganizationType) {
    this.repIndOrganizationType = repIndOrganizationType;
  }


  @Override
  public String toString() {
    return "ReportSynthesisStudiesByOrganizationTypeDTO [repIndOrganizationType=" + repIndOrganizationType
      + ", projectExpectedStudies=" + projectExpectedStudies + "]";
  }


}

