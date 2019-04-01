package org.cgiar.ccafs.marlo.data.model;

import java.util.List;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class ReportSynthesisInnovationsByTypeDTO implements java.io.Serializable {

  private static final long serialVersionUID = 1522258776884462429L;

  private RepIndInnovationType repIndInnovationType;
  private List<ProjectInnovationInfo> projectInnovationInfos;

  public ReportSynthesisInnovationsByTypeDTO() {
  }

  public List<ProjectInnovationInfo> getProjectInnovationInfos() {
    return projectInnovationInfos;
  }


  public RepIndInnovationType getRepIndInnovationType() {
    return repIndInnovationType;
  }


  public void setProjectInnovationInfos(List<ProjectInnovationInfo> projectInnovationInfos) {
    this.projectInnovationInfos = projectInnovationInfos;
  }


  public void setRepIndInnovationType(RepIndInnovationType repIndInnovationType) {
    this.repIndInnovationType = repIndInnovationType;
  }


  @Override
  public String toString() {
    return "ReportSynthesisInnovationsByTypeDTO [repIndInnovationType=" + repIndInnovationType
      + ", projectInnovationInfos=" + projectInnovationInfos + "]";
  }


}

