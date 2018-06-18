package org.cgiar.ccafs.marlo.data.model;

import java.util.List;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class ReportSynthesisInnovationsByStageDTO implements java.io.Serializable {

  private static final long serialVersionUID = 8475202706998085681L;

  private RepIndStageInnovation repIndStageInnovation;

  private List<ProjectInnovationInfo> projectInnovationInfos;


  public ReportSynthesisInnovationsByStageDTO() {
  }


  public List<ProjectInnovationInfo> getProjectInnovationInfos() {
    return projectInnovationInfos;
  }


  public RepIndStageInnovation getRepIndStageInnovation() {
    return repIndStageInnovation;
  }


  public void setProjectInnovationInfos(List<ProjectInnovationInfo> projectInnovationInfos) {
    this.projectInnovationInfos = projectInnovationInfos;
  }


  public void setRepIndStageInnovation(RepIndStageInnovation repIndStageInnovation) {
    this.repIndStageInnovation = repIndStageInnovation;
  }


  @Override
  public String toString() {
    return "ReportSynthesisInnovationsByStageDTO [repIndStageInnovation=" + repIndStageInnovation
      + ", projectInnovationInfos=" + projectInnovationInfos + "]";
  }


}

