package org.cgiar.ccafs.marlo.data.model;
// Generated Jun 6, 2018 11:11:29 AM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;


public class ReportSynthesisFlagshipProgress extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 7688189402530792639L;


  private ReportSynthesis reportSynthesis;


  @Expose
  private String summary;

  private Set<ReportSynthesisFlagshipProgressOutcome> reportSynthesisFlagshipProgressOutcomes =
    new HashSet<ReportSynthesisFlagshipProgressOutcome>(0);

  private List<ReportSynthesisFlagshipProgressOutcome> outcomeList;


  private Set<ReportSynthesisFlagshipProgressMilestone> reportSynthesisFlagshipProgressMilestones =
    new HashSet<ReportSynthesisFlagshipProgressMilestone>(0);

  private List<ReportSynthesisFlagshipProgressMilestone> milestones;


  /**
   * AR2018 Fields
   */
  @Expose
  private String overallProgress;


  @Expose
  private String progressByFlagships;


  @Expose
  private String detailedAnnex;

  @Expose
  private String relevanceCovid;

  @Expose
  private String expandedResearchAreas;

  @Expose
  private String droppedResearchLines;
  @Expose
  private String changedDirection;
  @Expose
  private String altmetricScore;
  // policies checks
  private Set<ReportSynthesisFlagshipProgressPolicy> reportSynthesisFlagshipProgressPolicies =
    new HashSet<ReportSynthesisFlagshipProgressPolicy>(0);
  private List<ReportSynthesisFlagshipProgressPolicy> plannedPolicies;
  private List<ProjectPolicy> projectPolicies;
  private String policiesValue;

  // Studies checks
  private Set<ReportSynthesisFlagshipProgressStudy> reportSynthesisFlagshipProgressStudies =
    new HashSet<ReportSynthesisFlagshipProgressStudy>(0);
  private List<ReportSynthesisFlagshipProgressStudy> plannedStudies;
  private List<ProjectExpectedStudy> projectStudies;
  private String studiesValue;

  // Innovations checks
  private Set<ReportSynthesisFlagshipProgressInnovation> reportSynthesisFlagshipProgressInnovations =
    new HashSet<ReportSynthesisFlagshipProgressInnovation>(0);
  private List<ReportSynthesisFlagshipProgressInnovation> plannedInnovations;
  private List<ProjectInnovation> projectInnovations;
  private String innovationsValue;

  // Publications checks
  private Set<ReportSynthesisFlagshipProgressDeliverable> reportSynthesisFlagshipProgressDeliverables =
    new HashSet<ReportSynthesisFlagshipProgressDeliverable>(0);
  private List<ReportSynthesisFlagshipProgressDeliverable> plannedDeliverables;
  private List<Deliverable> deliverables;
  private String deliverablesValue;

  // Milestone checks (include in QA PMU level)
  private Set<ReportSynthesisFlagshipProgressOutcomeMilestone> reportSynthesisFlagshipProgressOutcomeMilestones =
    new HashSet<ReportSynthesisFlagshipProgressOutcomeMilestone>(0);
  private List<ReportSynthesisFlagshipProgressOutcomeMilestone> outcomeMilestones;
  private List<CrpMilestone> crpMilestones;
  private String milestonesValue;


  public ReportSynthesisFlagshipProgress() {
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
    ReportSynthesisFlagshipProgress other = (ReportSynthesisFlagshipProgress) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }

  public String getAltmetricScore() {
    return altmetricScore;
  }

  public String getChangedDirection() {
    return changedDirection;
  }

  public List<CrpMilestone> getCrpMilestones() {
    return crpMilestones;
  }

  public List<Deliverable> getDeliverables() {
    return deliverables;
  }

  /**
   * @return Get an array of deliverables IDs checked.
   */
  public long[] getDeliverablesIds() {
    List<Deliverable> deliverable = this.getDeliverables();
    if (deliverable != null) {
      long[] ids = new long[deliverable.size()];
      for (int i = 0; i < ids.length; i++) {
        ids[i] = deliverable.get(i).getId();
      }
      return ids;
    }
    return null;
  }

  public String getDeliverablesValue() {
    return deliverablesValue;
  }


  public String getDetailedAnnex() {
    return detailedAnnex;
  }

  public String getDroppedResearchLines() {
    return droppedResearchLines;
  }

  public String getExpandedResearchAreas() {
    return expandedResearchAreas;
  }

  /**
   * @return Get an array of innovations IDs checked.
   */
  public long[] getInnovationsIds() {
    List<ProjectInnovation> projectInnovation = this.getProjectInnovations();
    if (projectInnovation != null) {
      long[] ids = new long[projectInnovation.size()];
      for (int i = 0; i < ids.length; i++) {
        ids[i] = projectInnovation.get(i).getId();
      }
      return ids;
    }
    return null;
  }


  public String getInnovationsValue() {
    return innovationsValue;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  /**
   * @return Get an array of milestone IDs checked.
   */
  public long[] getMilestoneIds() {
    List<CrpMilestone> crpMilestones = this.getCrpMilestones();
    if (crpMilestones != null) {
      long[] ids = new long[crpMilestones.size()];
      for (int i = 0; i < ids.length; i++) {
        ids[i] = crpMilestones.get(i).getId();
      }
      return ids;
    }
    return null;
  }


  public List<ReportSynthesisFlagshipProgressMilestone> getMilestones() {
    return milestones;
  }


  public String getMilestonesValue() {
    return milestonesValue;
  }

  public List<ReportSynthesisFlagshipProgressOutcome> getOutcomeList() {
    return outcomeList;
  }

  public List<ReportSynthesisFlagshipProgressOutcomeMilestone> getOutcomeMilestones() {
    return outcomeMilestones;
  }

  public String getOverallProgress() {
    return overallProgress;
  }


  public List<ReportSynthesisFlagshipProgressDeliverable> getPlannedDeliverables() {
    return plannedDeliverables;
  }

  public List<ReportSynthesisFlagshipProgressInnovation> getPlannedInnovations() {
    return plannedInnovations;
  }

  public List<ReportSynthesisFlagshipProgressPolicy> getPlannedPolicies() {
    return plannedPolicies;
  }

  public List<ReportSynthesisFlagshipProgressStudy> getPlannedStudies() {
    return plannedStudies;
  }


  /**
   * @return Get an array of policies IDs checked.
   */
  public long[] getPoliciesIds() {
    List<ProjectPolicy> projectPolicies = this.getProjectPolicies();
    if (projectPolicies != null) {
      long[] ids = new long[projectPolicies.size()];
      for (int i = 0; i < ids.length; i++) {
        ids[i] = projectPolicies.get(i).getId();
      }
      return ids;
    }
    return null;
  }


  public String getPoliciesValue() {
    return policiesValue;
  }


  public String getProgressByFlagships() {
    return progressByFlagships;
  }


  public List<ProjectInnovation> getProjectInnovations() {
    return projectInnovations;
  }

  public List<ProjectPolicy> getProjectPolicies() {
    return projectPolicies;
  }


  public List<ProjectExpectedStudy> getProjectStudies() {
    return projectStudies;
  }


  public String getRelevanceCovid() {
    return relevanceCovid;
  }


  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }


  public Set<ReportSynthesisFlagshipProgressDeliverable> getReportSynthesisFlagshipProgressDeliverables() {
    return reportSynthesisFlagshipProgressDeliverables;
  }


  public Set<ReportSynthesisFlagshipProgressInnovation> getReportSynthesisFlagshipProgressInnovations() {
    return reportSynthesisFlagshipProgressInnovations;
  }

  public Set<ReportSynthesisFlagshipProgressMilestone> getReportSynthesisFlagshipProgressMilestones() {
    return reportSynthesisFlagshipProgressMilestones;
  }


  public Set<ReportSynthesisFlagshipProgressOutcomeMilestone> getReportSynthesisFlagshipProgressOutcomeMilestones() {
    return reportSynthesisFlagshipProgressOutcomeMilestones;
  }

  public Set<ReportSynthesisFlagshipProgressOutcome> getReportSynthesisFlagshipProgressOutcomes() {
    return reportSynthesisFlagshipProgressOutcomes;
  }

  public Set<ReportSynthesisFlagshipProgressPolicy> getReportSynthesisFlagshipProgressPolicies() {
    return reportSynthesisFlagshipProgressPolicies;
  }


  public Set<ReportSynthesisFlagshipProgressStudy> getReportSynthesisFlagshipProgressStudies() {
    return reportSynthesisFlagshipProgressStudies;
  }

  /**
   * @return Get an array of studies IDs checked.
   */
  public long[] getStudiesIds() {
    List<ProjectExpectedStudy> projectStudies = this.getProjectStudies();
    if (projectStudies != null) {
      long[] ids = new long[projectStudies.size()];
      for (int i = 0; i < ids.length; i++) {
        ids[i] = projectStudies.get(i).getId();
      }
      return ids;
    }
    return null;
  }


  public String getStudiesValue() {
    return studiesValue;
  }


  public String getSummary() {
    return summary;
  }

  public void setAltmetricScore(String altmetricScore) {
    this.altmetricScore = altmetricScore;
  }


  public void setChangedDirection(String changedDirection) {
    this.changedDirection = changedDirection;
  }


  public void setCrpMilestones(List<CrpMilestone> crpMilestones) {
    this.crpMilestones = crpMilestones;
  }


  public void setDeliverables(List<Deliverable> deliverables) {
    this.deliverables = deliverables;
  }


  public void setDeliverablesValue(String deliverablesValue) {
    this.deliverablesValue = deliverablesValue;
  }

  public void setDetailedAnnex(String detailedAnnex) {
    this.detailedAnnex = detailedAnnex;
  }


  public void setDroppedResearchLines(String droppedResearchLines) {
    this.droppedResearchLines = droppedResearchLines;
  }

  public void setExpandedResearchAreas(String expandedResearchAreas) {
    this.expandedResearchAreas = expandedResearchAreas;
  }


  public void setInnovationsValue(String innovationsValue) {
    this.innovationsValue = innovationsValue;
  }


  public void setMilestones(List<ReportSynthesisFlagshipProgressMilestone> milestones) {
    this.milestones = milestones;
  }


  public void setMilestonesValue(String milestonesValue) {
    this.milestonesValue = milestonesValue;
  }


  public void setOutcomeList(List<ReportSynthesisFlagshipProgressOutcome> outcomeList) {
    this.outcomeList = outcomeList;
  }


  public void setOutcomeMilestones(List<ReportSynthesisFlagshipProgressOutcomeMilestone> outcomeMilestones) {
    this.outcomeMilestones = outcomeMilestones;
  }


  public void setOverallProgress(String overallProgress) {
    this.overallProgress = overallProgress;
  }


  public void setPlannedDeliverables(List<ReportSynthesisFlagshipProgressDeliverable> plannedDeliverables) {
    this.plannedDeliverables = plannedDeliverables;
  }


  public void setPlannedInnovations(List<ReportSynthesisFlagshipProgressInnovation> plannedInnovations) {
    this.plannedInnovations = plannedInnovations;
  }


  public void setPlannedPolicies(List<ReportSynthesisFlagshipProgressPolicy> plannedPolicies) {
    this.plannedPolicies = plannedPolicies;
  }


  public void setPlannedStudies(List<ReportSynthesisFlagshipProgressStudy> plannedStudies) {
    this.plannedStudies = plannedStudies;
  }


  public void setPoliciesValue(String policiesValue) {
    this.policiesValue = policiesValue;
  }


  public void setProgressByFlagships(String progressByFlagships) {
    this.progressByFlagships = progressByFlagships;
  }


  public void setProjectInnovations(List<ProjectInnovation> projectInnovations) {
    this.projectInnovations = projectInnovations;
  }


  public void setProjectPolicies(List<ProjectPolicy> projectPolicies) {
    this.projectPolicies = projectPolicies;
  }

  public void setProjectStudies(List<ProjectExpectedStudy> projectStudies) {
    this.projectStudies = projectStudies;
  }

  public void setRelevanceCovid(String relevanceCovid) {
    this.relevanceCovid = relevanceCovid;
  }


  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }


  public void setReportSynthesisFlagshipProgressDeliverables(
    Set<ReportSynthesisFlagshipProgressDeliverable> reportSynthesisFlagshipProgressDeliverables) {
    this.reportSynthesisFlagshipProgressDeliverables = reportSynthesisFlagshipProgressDeliverables;
  }


  public void setReportSynthesisFlagshipProgressInnovations(
    Set<ReportSynthesisFlagshipProgressInnovation> reportSynthesisFlagshipProgressInnovations) {
    this.reportSynthesisFlagshipProgressInnovations = reportSynthesisFlagshipProgressInnovations;
  }


  public void setReportSynthesisFlagshipProgressMilestones(
    Set<ReportSynthesisFlagshipProgressMilestone> reportSynthesisFlagshipProgressMilestones) {
    this.reportSynthesisFlagshipProgressMilestones = reportSynthesisFlagshipProgressMilestones;
  }


  public void setReportSynthesisFlagshipProgressOutcomeMilestones(
    Set<ReportSynthesisFlagshipProgressOutcomeMilestone> reportSynthesisFlagshipProgressOutcomeMilestones) {
    this.reportSynthesisFlagshipProgressOutcomeMilestones = reportSynthesisFlagshipProgressOutcomeMilestones;
  }


  public void setReportSynthesisFlagshipProgressOutcomes(
    Set<ReportSynthesisFlagshipProgressOutcome> reportSynthesisFlagshipProgressOutcomes) {
    this.reportSynthesisFlagshipProgressOutcomes = reportSynthesisFlagshipProgressOutcomes;
  }

  public void setReportSynthesisFlagshipProgressPolicies(
    Set<ReportSynthesisFlagshipProgressPolicy> reportSynthesisFlagshipProgressPolicies) {
    this.reportSynthesisFlagshipProgressPolicies = reportSynthesisFlagshipProgressPolicies;
  }

  public void setReportSynthesisFlagshipProgressStudies(
    Set<ReportSynthesisFlagshipProgressStudy> reportSynthesisFlagshipProgressStudies) {
    this.reportSynthesisFlagshipProgressStudies = reportSynthesisFlagshipProgressStudies;
  }


  public void setStudiesValue(String studiesValue) {
    this.studiesValue = studiesValue;
  }

  public void setSummary(String summary) {
    this.summary = summary;
  }
}

