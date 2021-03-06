package org.cgiar.ccafs.marlo.data.model;
// default package
// Generated May 30, 2017 3:06:49 PM by Hibernate Tools 3.4.0.CR1


import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * CapacityDevelopment generated by hbm2java
 */
public class CapacityDevelopment extends MarloAuditableEntity implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 1L;


  @Expose
  private CenterProject project;


  @Expose
  private CenterArea researchArea;


  @Expose
  private GlobalUnit crp;


  @Expose
  private CrpProgram researchProgram;


  @Expose
  private String title;

  @Expose
  private CapacityDevelopmentType capdevType;

  @Expose
  private int category;

  @Expose
  private String ctFirstName;

  @Expose
  private String ctLastName;

  @Expose
  private String ctEmail;

  @Expose
  private Date startDate;

  @Expose
  private Date endDate;

  @Expose
  private Long duration;

  @Expose
  private String durationUnit;

  @Expose
  private Participant participant;

  @Expose
  private Boolean global;

  @Expose
  private Boolean focused;

  @Expose
  private Boolean regional;

  @Expose
  private Boolean estimate;

  @Expose
  private Boolean estimateMen;

  @Expose
  private Boolean estimateWomen;

  @Expose
  private Boolean estimateOthers;

  @Expose
  private String sGlobal;

  @Expose
  private String sRegional;

  @Expose
  private Integer numParticipants;

  @Expose
  private Integer numMen;

  @Expose
  private Integer numWomen;

  @Expose
  private Integer numOther;

  @Expose
  private String otherDiscipline;

  @Expose
  private String disciplineSuggested;

  @Expose
  private String otherTargetGroup;

  @Expose
  private String targetGroupSuggested;

  @Expose
  private String otherPartner;

  @Expose
  private String partnerSuggested;

  @Expose
  private String otherFunding;

  private Set<CenterSubmission> submissions = new HashSet<CenterSubmission>(0);

  private Set<CapdevLocations> capdevLocations = new HashSet<CapdevLocations>(0);

  private List<CapdevLocations> capDevCountries;

  private List<CapdevLocations> capDevRegions;

  private Set<CapdevDiscipline> capdevDiscipline = new HashSet<CapdevDiscipline>(0);

  private List<CapdevDiscipline> capdevDisciplineList;

  private Set<CapdevTargetgroup> capdevTargetgroup = new HashSet<CapdevTargetgroup>(0);

  private List<CapdevTargetgroup> capdevTargetGroupList;

  private Set<CapdevParticipant> capdevParticipant = new HashSet<CapdevParticipant>(0);
  private Set<CapdevOutputs> capdevOutputs = new HashSet<CapdevOutputs>(0);
  private List<CapdevOutputs> capdevOutputsList;
  private Set<CapdevPartners> capdevPartners = new HashSet<CapdevPartners>(0);
  private List<CapdevPartners> capdevPartnersList;
  private Set<CapdevSupportingDocs> capdevSupportingDocs = new HashSet<CapdevSupportingDocs>(0);
  private Set<CenterDeliverable> deliverables = new HashSet<CenterDeliverable>(0);
  private Set<CenterSectionStatus> sectionStatuses = new HashSet<CenterSectionStatus>(0);

  public CapacityDevelopment() {
  }

  public List<CapdevLocations> getCapDevCountries() {
    return capDevCountries;
  }

  public Set<CapdevDiscipline> getCapdevDiscipline() {
    return capdevDiscipline;
  }

  public List<CapdevDiscipline> getCapdevDisciplineList() {
    return capdevDisciplineList;
  }

  public Set<CapdevLocations> getCapdevLocations() {
    return capdevLocations;
  }


  public Set<CapdevOutputs> getCapdevOutputs() {
    return capdevOutputs;
  }

  public List<CapdevOutputs> getCapdevOutputsList() {
    return capdevOutputsList;
  }


  public Set<CapdevParticipant> getCapdevParticipant() {
    return capdevParticipant;
  }


  public Set<CapdevPartners> getCapdevPartners() {
    return capdevPartners;
  }

  public List<CapdevPartners> getCapdevPartnersList() {
    return capdevPartnersList;
  }

  public List<CapdevLocations> getCapDevRegions() {
    return capDevRegions;
  }

  public Set<CapdevSupportingDocs> getCapdevSupportingDocs() {
    return capdevSupportingDocs;
  }

  public Set<CapdevTargetgroup> getCapdevTargetgroup() {
    return capdevTargetgroup;
  }

  public List<CapdevTargetgroup> getCapdevTargetGroupList() {
    return capdevTargetGroupList;
  }

  public CapacityDevelopmentType getCapdevType() {
    return this.capdevType;
  }

  public int getCategory() {
    return this.category;
  }

  public GlobalUnit getCrp() {
    return crp;
  }

  public String getCtEmail() {
    return this.ctEmail;
  }


  public String getCtFirstName() {
    return this.ctFirstName;
  }


  public String getCtLastName() {
    return this.ctLastName;
  }


  public Set<CenterDeliverable> getDeliverables() {
    return deliverables;
  }


  public String getDisciplineSuggested() {
    return disciplineSuggested;
  }


  public Long getDuration() {
    return duration;
  }

  public String getDurationUnit() {
    return durationUnit;
  }


  public Date getEndDate() {
    return this.endDate;
  }

  public String getEndDateFormat() {
    final String pattern = "yyyy-MM-dd";
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String date = "";
    if (this.endDate != null) {
      date = simpleDateFormat.format(this.endDate);
    }
    return date;
  }

  public Boolean getEstimate() {
    return estimate;
  }

  public Boolean getEstimateMen() {
    return estimateMen;
  }

  public Boolean getEstimateOthers() {
    return estimateOthers;
  }

  public Boolean getEstimateWomen() {
    return estimateWomen;
  }

  public Boolean getFocused() {
    return focused;
  }


  public Boolean getGlobal() {
    return global;
  }


  @Override
  public String getLogDeatil() {
    final StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public Integer getNumMen() {
    return numMen;
  }


  public Integer getNumOther() {
    return numOther;
  }


  public Integer getNumParticipants() {
    return this.numParticipants;
  }

  public Integer getNumWomen() {
    return numWomen;
  }

  public String getOtherDiscipline() {
    return otherDiscipline;
  }

  public String getOtherFunding() {
    return otherFunding;
  }

  public String getOtherPartner() {
    return otherPartner;
  }

  public String getOtherTargetGroup() {
    return otherTargetGroup;
  }


  public Participant getParticipant() {
    return participant;
  }


  public String getPartnerSuggested() {
    return partnerSuggested;
  }

  public CenterProject getProject() {
    return this.project;
  }


  public Boolean getRegional() {
    return regional;
  }


  public CenterArea getResearchArea() {
    return this.researchArea;
  }

  public CrpProgram getResearchProgram() {
    return researchProgram;
  }


  public Set<CenterSectionStatus> getSectionStatuses() {
    return sectionStatuses;
  }


  public String getsGlobal() {
    return sGlobal;
  }


  public String getsRegional() {
    return sRegional;
  }


  public String getStarDateFormat() {
    final String pattern = "yyyy-MM-dd";
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    String date = "";
    if (this.startDate != null) {
      date = simpleDateFormat.format(this.startDate);
    }
    return date;
  }


  public Date getStartDate() {
    return this.startDate;
  }


  public Set<CenterSubmission> getSubmissions() {
    return submissions;
  }


  public String getTargetGroupSuggested() {
    return targetGroupSuggested;
  }


  public String getTitle() {
    return this.title;
  }


  public void setCapDevCountries(List<CapdevLocations> capDevCountries) {
    this.capDevCountries = capDevCountries;
  }


  public void setCapdevDiscipline(Set<CapdevDiscipline> capdevDiscipline) {
    this.capdevDiscipline = capdevDiscipline;
  }


  public void setCapdevDisciplineList(List<CapdevDiscipline> capdevDisciplineList) {
    this.capdevDisciplineList = capdevDisciplineList;
  }


  public void setCapdevLocations(Set<CapdevLocations> capdevLocations) {
    this.capdevLocations = capdevLocations;
  }


  public void setCapdevOutputs(Set<CapdevOutputs> capdevOutputs) {
    this.capdevOutputs = capdevOutputs;
  }


  public void setCapdevOutputsList(List<CapdevOutputs> capdevOutputsList) {
    this.capdevOutputsList = capdevOutputsList;
  }


  public void setCapdevParticipant(Set<CapdevParticipant> capdevParticipant) {
    this.capdevParticipant = capdevParticipant;
  }


  public void setCapdevPartners(Set<CapdevPartners> capdevPartners) {
    this.capdevPartners = capdevPartners;
  }


  public void setCapdevPartnersList(List<CapdevPartners> capdevPartnersList) {
    this.capdevPartnersList = capdevPartnersList;
  }


  public void setCapDevRegions(List<CapdevLocations> capDevRegions) {
    this.capDevRegions = capDevRegions;
  }


  public void setCapdevSupportingDocs(Set<CapdevSupportingDocs> capdevSupportingDocs) {
    this.capdevSupportingDocs = capdevSupportingDocs;
  }


  public void setCapdevTargetgroup(Set<CapdevTargetgroup> capdevTargetgroup) {
    this.capdevTargetgroup = capdevTargetgroup;
  }

  public void setCapdevTargetGroupList(List<CapdevTargetgroup> capdevTargetGroupList) {
    this.capdevTargetGroupList = capdevTargetGroupList;
  }


  public void setCapdevType(CapacityDevelopmentType capdevType) {
    this.capdevType = capdevType;
  }


  public void setCategory(int category) {
    this.category = category;
  }


  public void setCrp(GlobalUnit crp) {
    this.crp = crp;
  }


  public void setCtEmail(String ctEmail) {
    this.ctEmail = ctEmail;
  }


  public void setCtFirstName(String ctFirstName) {
    this.ctFirstName = ctFirstName;
  }


  public void setCtLastName(String ctLastName) {
    this.ctLastName = ctLastName;
  }


  public void setDeliverables(Set<CenterDeliverable> deliverables) {
    this.deliverables = deliverables;
  }


  public void setDisciplineSuggested(String disciplineSuggested) {
    this.disciplineSuggested = disciplineSuggested;
  }


  public void setDuration(Long duration) {
    this.duration = duration;
  }


  public void setDurationUnit(String durationUnit) {
    this.durationUnit = durationUnit;
  }


  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }


  public void setEstimate(Boolean estimate) {
    this.estimate = estimate;
  }


  public void setEstimateMen(Boolean estimateMen) {
    this.estimateMen = estimateMen;
  }


  public void setEstimateOthers(Boolean estimateOthers) {
    this.estimateOthers = estimateOthers;
  }


  public void setEstimateWomen(Boolean estimateWomen) {
    this.estimateWomen = estimateWomen;
  }


  public void setFocused(Boolean focused) {
    this.focused = focused;
  }


  public void setGlobal(Boolean global) {
    this.global = global;
  }


  public void setNumMen(Integer numMen) {
    this.numMen = numMen;
  }


  public void setNumOther(Integer numOther) {
    this.numOther = numOther;
  }


  public void setNumParticipants(Integer numParticipants) {
    this.numParticipants = numParticipants;
  }


  public void setNumWomen(Integer numWomen) {
    this.numWomen = numWomen;
  }


  public void setOtherDiscipline(String otherDiscipline) {
    this.otherDiscipline = otherDiscipline;
  }

  public void setOtherFunding(String otherFunding) {
    this.otherFunding = otherFunding;
  }


  public void setOtherPartner(String otherPartner) {
    this.otherPartner = otherPartner;
  }


  public void setOtherTargetGroup(String otherTargetGroup) {
    this.otherTargetGroup = otherTargetGroup;
  }


  public void setParticipant(Participant participant) {
    this.participant = participant;
  }


  public void setPartnerSuggested(String partnerSuggested) {
    this.partnerSuggested = partnerSuggested;
  }


  public void setProject(CenterProject project) {
    this.project = project;
  }


  public void setRegional(Boolean regional) {
    this.regional = regional;
  }


  public void setResearchArea(CenterArea researchArea) {
    this.researchArea = researchArea;
  }


  public void setResearchProgram(CrpProgram researchProgram) {
    this.researchProgram = researchProgram;
  }


  public void setSectionStatuses(Set<CenterSectionStatus> sectionStatuses) {
    this.sectionStatuses = sectionStatuses;
  }


  public void setsGlobal(String sGlobal) {
    this.sGlobal = sGlobal;
  }


  public void setsRegional(String sRegional) {
    this.sRegional = sRegional;
  }


  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }


  public void setSubmissions(Set<CenterSubmission> submissions) {
    this.submissions = submissions;
  }


  public void setTargetGroupSuggested(String targetGroupSuggested) {
    this.targetGroupSuggested = targetGroupSuggested;
  }


  public void setTitle(String title) {
    this.title = title;
  }


}

