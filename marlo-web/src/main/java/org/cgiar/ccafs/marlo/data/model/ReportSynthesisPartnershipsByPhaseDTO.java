package org.cgiar.ccafs.marlo.data.model;

import java.util.List;

/**
 * @author Andres Valencia - CIAT/CCAFS
 */
public class ReportSynthesisPartnershipsByPhaseDTO implements java.io.Serializable {


  private static final long serialVersionUID = -9163699543131777447L;


  private RepIndPhaseResearchPartnership repIndPhaseResearchPartnership;


  private List<ProjectPartnerPartnership> projectPartnerPartnerships;

  public ReportSynthesisPartnershipsByPhaseDTO() {
  }

  public List<ProjectPartnerPartnership> getProjectPartnerPartnerships() {
    return projectPartnerPartnerships;
  }

  public RepIndPhaseResearchPartnership getRepIndPhaseResearchPartnership() {
    return repIndPhaseResearchPartnership;
  }

  public void setProjectPartnerPartnerships(List<ProjectPartnerPartnership> projectPartnerPartnerships) {
    this.projectPartnerPartnerships = projectPartnerPartnerships;
  }


  public void setRepIndPhaseResearchPartnership(RepIndPhaseResearchPartnership repIndPhaseResearchPartnership) {
    this.repIndPhaseResearchPartnership = repIndPhaseResearchPartnership;
  }


  @Override
  public String toString() {
    return "ReportSynthesisPartnershipsByPhaseDTO [repIndPhaseResearchPartnership=" + repIndPhaseResearchPartnership
      + ", projectPartnerPartnerships=" + projectPartnerPartnerships + "]";
  }


}

