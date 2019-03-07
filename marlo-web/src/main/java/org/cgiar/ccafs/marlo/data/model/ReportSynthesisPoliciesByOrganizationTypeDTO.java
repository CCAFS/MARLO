package org.cgiar.ccafs.marlo.data.model;

import java.util.List;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class ReportSynthesisPoliciesByOrganizationTypeDTO implements java.io.Serializable {

  private static final long serialVersionUID = 7006255042599289945L;

  private RepIndOrganizationType repIndOrganizationType;


  private List<ProjectPolicy> projectPolicies;


  public ReportSynthesisPoliciesByOrganizationTypeDTO() {
  }


  public List<ProjectPolicy> getProjectPolicies() {
    return projectPolicies;
  }


  public RepIndOrganizationType getRepIndOrganizationType() {
    return repIndOrganizationType;
  }


  public void setProjectPolicies(List<ProjectPolicy> projectPolicies) {
    this.projectPolicies = projectPolicies;
  }


  public void setRepIndOrganizationType(RepIndOrganizationType repIndOrganizationType) {
    this.repIndOrganizationType = repIndOrganizationType;
  }


  @Override
  public String toString() {
    return "ReportSynthesisStudiesByOrganizationTypeDTO [repIndOrganizationType=" + repIndOrganizationType
      + ", projectPolicies=" + projectPolicies + "]";
  }


}

