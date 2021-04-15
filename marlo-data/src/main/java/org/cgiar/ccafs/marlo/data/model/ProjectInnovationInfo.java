package org.cgiar.ccafs.marlo.data.model;
// Generated Apr 18, 2018 3:39:52 PM by Hibernate Tools 3.4.0.CR1

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.List;

import com.google.gson.annotations.Expose;

/**
 * ProjectInnovationInfo generated by hbm2java
 */
public class ProjectInnovationInfo extends MarloBaseEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 6466188559245022941L;


  @Expose
  private ProjectInnovation projectInnovation;


  @Expose
  private RepIndPhaseResearchPartnership repIndPhaseResearchPartnership;

  @Expose
  private RepIndStageInnovation repIndStageInnovation;

  @Expose
  private Phase phase;

  @Expose
  private RepIndRegion repIndRegion;

  @Expose
  private RepIndInnovationType repIndInnovationType;

  @Expose
  private RepIndGeographicScope repIndGeographicScope;

  @Expose
  private RepIndDegreeInnovation repIndDegreeInnovation;

  @Expose
  private RepIndGenderYouthFocusLevel genderFocusLevel;

  @Expose
  private RepIndGenderYouthFocusLevel youthFocusLevel;

  @Expose
  private String title;

  @Expose
  private String narrative;

  @Expose
  private String evidenceLink;

  @Expose
  private String genderExplaniation;

  @Expose
  private String youthExplaniation;

  @Expose
  private Long year;

  @Expose
  private Long innovationNumber;

  @Expose
  private ProjectExpectedStudy projectExpectedStudy;

  @Expose
  private String descriptionStage;

  @Expose
  private String adaptativeResearchNarrative;

  @Expose
  private Institution leadOrganization;

  @Expose
  private Boolean clearLead;

  @Expose
  private String otherInnovationType;
  @Expose
  private Boolean hasMilestones;

  private List<String> evidencesLink;

  public ProjectInnovationInfo() {
  }

  public ProjectInnovationInfo(ProjectInnovation projectInnovation, Phase phase, String title, String narrative,
    String descriptionStage, String evidenceLink, String genderExplaniation, String youthExplaniation, Long year) {
    super();
    this.projectInnovation = projectInnovation;
    this.phase = phase;
    this.title = title;
    this.descriptionStage = descriptionStage;
    this.narrative = narrative;
    this.evidenceLink = evidenceLink;
    this.genderExplaniation = genderExplaniation;
    this.youthExplaniation = youthExplaniation;
    this.year = year;
  }

  public ProjectInnovationInfo(ProjectInnovation projectInnovation,
    RepIndPhaseResearchPartnership repIndPhaseResearchPartnership, RepIndStageInnovation repIndStageInnovation,
    Phase phase, RepIndRegion repIndRegion, RepIndInnovationType repIndInnovationType,
    RepIndGeographicScope repIndGeographicScope, RepIndGenderYouthFocusLevel genderFocusLevel,
    RepIndGenderYouthFocusLevel youthFocusLevel, String title, String narrative, String descriptionStage,
    String evidenceLink, String genderExplaniation, String youthExplaniation, Long year) {
    super();
    this.projectInnovation = projectInnovation;
    this.repIndPhaseResearchPartnership = repIndPhaseResearchPartnership;
    this.repIndStageInnovation = repIndStageInnovation;
    this.phase = phase;
    this.repIndRegion = repIndRegion;
    this.repIndInnovationType = repIndInnovationType;
    this.repIndGeographicScope = repIndGeographicScope;
    this.genderFocusLevel = genderFocusLevel;
    this.youthFocusLevel = youthFocusLevel;
    this.title = title;
    this.narrative = narrative;

    this.evidenceLink = evidenceLink;
    this.genderExplaniation = genderExplaniation;
    this.youthExplaniation = youthExplaniation;
    this.year = year;
  }


  public String getAdaptativeResearchNarrative() {
    return adaptativeResearchNarrative;
  }

  public Boolean getClearLead() {
    return clearLead;
  }

  public String getDescriptionStage() {
    return descriptionStage;
  }

  public String getEvidenceLink() {
    return evidenceLink;
  }


  public List<String> getEvidencesLink() {
    return evidencesLink;
  }

  public String getGenderExplaniation() {
    return genderExplaniation;
  }

  public RepIndGenderYouthFocusLevel getGenderFocusLevel() {
    return genderFocusLevel;
  }

  public Boolean getHasMilestones() {
    return hasMilestones;
  }

  public Long getInnovationNumber() {
    return innovationNumber;
  }

  public Institution getLeadOrganization() {
    return leadOrganization;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }

  @Override
  public String getModificationJustification() {
    return "";
  }

  @Override
  public User getModifiedBy() {
    User u = new User();
    u.setId(new Long(3));
    return u;
  }

  public String getNarrative() {
    return narrative;
  }

  public String getOtherInnovationType() {
    return otherInnovationType;
  }

  public Phase getPhase() {
    return phase;
  }

  public ProjectExpectedStudy getProjectExpectedStudy() {
    return projectExpectedStudy;
  }


  public ProjectInnovation getProjectInnovation() {
    return projectInnovation;
  }

  public RepIndDegreeInnovation getRepIndDegreeInnovation() {
    return repIndDegreeInnovation;
  }


  public RepIndGeographicScope getRepIndGeographicScope() {
    return repIndGeographicScope;
  }


  public RepIndInnovationType getRepIndInnovationType() {
    return repIndInnovationType;
  }


  public RepIndPhaseResearchPartnership getRepIndPhaseResearchPartnership() {
    return repIndPhaseResearchPartnership;
  }


  public RepIndRegion getRepIndRegion() {
    return repIndRegion;
  }


  public RepIndStageInnovation getRepIndStageInnovation() {
    return repIndStageInnovation;
  }


  public String getTitle() {
    return title;
  }


  public Long getYear() {
    return year;
  }


  public String getYouthExplaniation() {
    return youthExplaniation;
  }


  public RepIndGenderYouthFocusLevel getYouthFocusLevel() {
    return youthFocusLevel;
  }


  @Override
  public boolean isActive() {
    return true;
  }


  public void setAdaptativeResearchNarrative(String adaptativeResearchNarrative) {
    this.adaptativeResearchNarrative = adaptativeResearchNarrative;
  }


  public void setClearLead(Boolean clearLead) {
    this.clearLead = clearLead;
  }


  public void setDescriptionStage(String descriptionStage) {
    this.descriptionStage = descriptionStage;
  }


  public void setEvidenceLink(String evidenceLink) {
    this.evidenceLink = evidenceLink;
  }


  public void setEvidencesLink(List<String> evidencesLink) {
    this.evidencesLink = evidencesLink;
  }


  public void setGenderExplaniation(String genderExplaniation) {
    this.genderExplaniation = genderExplaniation;
  }

  public void setGenderFocusLevel(RepIndGenderYouthFocusLevel genderFocusLevel) {
    this.genderFocusLevel = genderFocusLevel;
  }

  public void setHasMilestones(Boolean hasMilestones) {
    this.hasMilestones = hasMilestones;
  }

  public void setInnovationNumber(Long innovationNumber) {
    this.innovationNumber = innovationNumber;
  }

  public void setLeadOrganization(Institution leadOrganization) {
    this.leadOrganization = leadOrganization;
  }

  @Override
  public void setModifiedBy(User modifiedBy) {

  }

  public void setNarrative(String narrative) {
    this.narrative = narrative;
  }

  public void setOtherInnovationType(String otherInnovationType) {
    this.otherInnovationType = otherInnovationType;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }

  public void setProjectExpectedStudy(ProjectExpectedStudy projectExpectedStudy) {
    this.projectExpectedStudy = projectExpectedStudy;
  }

  public void setProjectInnovation(ProjectInnovation projectInnovation) {
    this.projectInnovation = projectInnovation;
  }

  public void setRepIndDegreeInnovation(RepIndDegreeInnovation repIndDegreeInnovation) {
    this.repIndDegreeInnovation = repIndDegreeInnovation;
  }

  public void setRepIndGeographicScope(RepIndGeographicScope repIndGeographicScope) {
    this.repIndGeographicScope = repIndGeographicScope;
  }

  public void setRepIndInnovationType(RepIndInnovationType repIndInnovationType) {
    this.repIndInnovationType = repIndInnovationType;
  }

  public void setRepIndPhaseResearchPartnership(RepIndPhaseResearchPartnership repIndPhaseResearchPartnership) {
    this.repIndPhaseResearchPartnership = repIndPhaseResearchPartnership;
  }


  public void setRepIndRegion(RepIndRegion repIndRegion) {
    this.repIndRegion = repIndRegion;
  }

  public void setRepIndStageInnovation(RepIndStageInnovation repIndStageInnovation) {
    this.repIndStageInnovation = repIndStageInnovation;
  }


  public void setTitle(String title) {
    this.title = title;
  }

  public void setYear(Long year) {
    this.year = year;
  }


  public void setYouthExplaniation(String youthExplaniation) {
    this.youthExplaniation = youthExplaniation;
  }


  public void setYouthFocusLevel(RepIndGenderYouthFocusLevel youthFocusLevel) {
    this.youthFocusLevel = youthFocusLevel;
  }

  @Override
  public String toString() {
    return "ProjectInnovationInfo [projectInnovation=" + projectInnovation + ", phase=" + phase + ", title=" + title
      + ", narrative=" + narrative + ", year=" + year + "]";
  }

  /**
   * Add the save information to reply the next Phase
   * 
   * @param projectInnovationInfoUpdate - a ProjectInnovationInfo object.
   * @param phase - The next Phase
   */
  public void updateProjectInnovationInfo(ProjectInnovationInfo projectInnovationInfoUpdate, Phase phase) {

    this.setPhase(phase);

    this.setTitle(projectInnovationInfoUpdate.getTitle());
    this.setNarrative(projectInnovationInfoUpdate.getNarrative());
    this.setProjectInnovation(projectInnovationInfoUpdate.getProjectInnovation());
    this.setRepIndPhaseResearchPartnership(projectInnovationInfoUpdate.getRepIndPhaseResearchPartnership());
    this.setRepIndStageInnovation(projectInnovationInfoUpdate.getRepIndStageInnovation());
    this.setRepIndRegion(projectInnovationInfoUpdate.getRepIndRegion());
    this.setRepIndInnovationType(projectInnovationInfoUpdate.getRepIndInnovationType());
    this.setRepIndGeographicScope(projectInnovationInfoUpdate.getRepIndGeographicScope());
    this.setRepIndDegreeInnovation(projectInnovationInfoUpdate.getRepIndDegreeInnovation());
    this.setGenderFocusLevel(projectInnovationInfoUpdate.getGenderFocusLevel());
    this.setYouthFocusLevel(projectInnovationInfoUpdate.getYouthFocusLevel());
    this.setDescriptionStage(projectInnovationInfoUpdate.getDescriptionStage());
    this.setEvidenceLink(projectInnovationInfoUpdate.getEvidenceLink());
    this.setGenderExplaniation(projectInnovationInfoUpdate.getGenderExplaniation());
    this.setYouthExplaniation(projectInnovationInfoUpdate.getYouthExplaniation());
    this.setYear(projectInnovationInfoUpdate.getYear());
    this.setAdaptativeResearchNarrative(projectInnovationInfoUpdate.getAdaptativeResearchNarrative());
    this.setProjectExpectedStudy(projectInnovationInfoUpdate.getProjectExpectedStudy());
  }
}

