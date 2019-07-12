package org.cgiar.ccafs.marlo.data.model;

import java.util.List;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class ReportSynthesisPoliciesByRepIndStageProcessDTO implements java.io.Serializable {

  private static final long serialVersionUID = 7006255042599289945L;

  private RepIndStageProcess repIndStageProcess;


  private List<ProjectPolicy> projectPolicies;


  public ReportSynthesisPoliciesByRepIndStageProcessDTO() {
  }


  public List<ProjectPolicy> getProjectPolicies() {
    return projectPolicies;
  }


  public RepIndStageProcess getRepIndStageProcess() {
    return repIndStageProcess;
  }


  public void setProjectPolicies(List<ProjectPolicy> projectPolicies) {
    this.projectPolicies = projectPolicies;
  }


  public void setRepIndStageProcess(RepIndStageProcess repIndStageProcess) {
    this.repIndStageProcess = repIndStageProcess;
  }


  @Override
  public String toString() {
    return "ReportSynthesisStudiesByOrganizationTypeDTO [repIndStageProcess=" + repIndStageProcess
      + ", projectPolicies=" + projectPolicies + "]";
  }


}

