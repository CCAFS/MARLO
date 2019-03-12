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


  public List<ReportSynthesisFlagshipProgressMilestone> getMilestones() {
    return milestones;
  }


  public String getOverallProgress() {
    return overallProgress;
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


  public ReportSynthesis getReportSynthesis() {
    return reportSynthesis;
  }

  public Set<ReportSynthesisFlagshipProgressInnovation> getReportSynthesisFlagshipProgressInnovations() {
    return reportSynthesisFlagshipProgressInnovations;
  }


  public Set<ReportSynthesisFlagshipProgressMilestone> getReportSynthesisFlagshipProgressMilestones() {
    return reportSynthesisFlagshipProgressMilestones;
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


  public void setOverallProgress(String overallProgress) {
    this.overallProgress = overallProgress;
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


  public void setReportSynthesis(ReportSynthesis reportSynthesis) {
    this.reportSynthesis = reportSynthesis;
  }


  public void setReportSynthesisFlagshipProgressInnovations(
    Set<ReportSynthesisFlagshipProgressInnovation> reportSynthesisFlagshipProgressInnovations) {
    this.reportSynthesisFlagshipProgressInnovations = reportSynthesisFlagshipProgressInnovations;
  }

  public void setReportSynthesisFlagshipProgressMilestones(
    Set<ReportSynthesisFlagshipProgressMilestone> reportSynthesisFlagshipProgressMilestones) {
    this.reportSynthesisFlagshipProgressMilestones = reportSynthesisFlagshipProgressMilestones;
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

