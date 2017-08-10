package org.cgiar.ccafs.marlo.data.model;
// default package
// Generated May 30, 2017 3:06:49 PM by Hibernate Tools 3.4.0.CR1


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * CapacityDevelopment generated by hbm2java
 */
public class CapacityDevelopment implements java.io.Serializable {


  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private Long id;
  private CenterProject project;
  private User usersByModifiedBy;
  private User usersByCreatedBy;
  private CenterArea researchArea;
  private Crp crp;
  private CenterProgram researchProgram;
  private String title;
  private CapacityDevelopmentType capdevType;
  private int category;
  private boolean active;
  private String modificationJustification;
  private String ctFirstName;
  private String ctLastName;
  private String ctEmail;
  private Date startDate;
  private Date endDate;
  private Long duration;
  private Integer numParticipants;
  private Integer numMen;
  private Integer numWomen;
  private Set<CapdevLocations> capdevLocations = new HashSet<CapdevLocations>(0);
  private List<CapdevLocations> capDevCountries;
  private List<CapdevLocations> capDevRegions;
  private Set<CapdevDiscipline> capdevDisciplines = new HashSet<CapdevDiscipline>(0);
  private Set<CapdevTargetgroup> capdevTargetgroups = new HashSet<CapdevTargetgroup>(0);
  private Set<CapdevParticipant> capdevParticipants = new HashSet<CapdevParticipant>(0);
  private Set<CapdevOutputs> capdevOutputses = new HashSet<CapdevOutputs>(0);
  private Set<CapdevPartners> capdevPartnerses = new HashSet<CapdevPartners>(0);
  private Set<CapdevSupportingDocs> capdevSupportingDocses = new HashSet<CapdevSupportingDocs>(0);


  public CapacityDevelopment() {
  }


  public CapacityDevelopment(CenterProject project, User usersByModifiedBy, User usersByCreatedBy,
    CenterArea researchArea, Crp crp, CenterProgram researchProgram, String title, CapacityDevelopmentType capdevType,
    int category, boolean active, String modificationJustification, String ctFirstName, String ctLastName,
    String ctEmail, Date startDate, Date endDate, Long duration, Integer numParticipants, Integer numMen,
    Integer numWomen, Set<CapdevLocations> capdevLocations, Set<CapdevDiscipline> capdevDisciplines,
    Set<CapdevTargetgroup> capdevTargetgroups, Set<CapdevParticipant> capdevParticipants,
    Set<CapdevOutputs> capdevOutputses, Set<CapdevPartners> capdevPartnerses,
    Set<CapdevSupportingDocs> capdevSupportingDocses) {
    this.project = project;
    this.usersByModifiedBy = usersByModifiedBy;
    this.usersByCreatedBy = usersByCreatedBy;
    this.researchArea = researchArea;
    this.crp = crp;
    this.researchProgram = researchProgram;
    this.title = title;
    this.capdevType = capdevType;
    this.category = category;
    this.active = active;
    this.modificationJustification = modificationJustification;
    this.ctFirstName = ctFirstName;
    this.ctLastName = ctLastName;
    this.ctEmail = ctEmail;
    this.startDate = startDate;
    this.endDate = endDate;
    this.duration = duration;
    this.numParticipants = numParticipants;
    this.capdevLocations = capdevLocations;
    this.capdevDisciplines = capdevDisciplines;
    this.capdevTargetgroups = capdevTargetgroups;
    this.capdevParticipants = capdevParticipants;
    this.numMen = numMen;
    this.numWomen = numWomen;
    this.capdevOutputses = capdevOutputses;
    this.capdevPartnerses = capdevPartnerses;
    this.capdevSupportingDocses = capdevSupportingDocses;
  }

  public CapacityDevelopment(String title, CapacityDevelopmentType capdevType, int category, boolean active,
    String ctFirstName, String ctLastName, String ctEmail, Date startDate) {
    this.title = title;
    this.capdevType = capdevType;
    this.category = category;
    this.active = active;
    this.ctFirstName = ctFirstName;
    this.ctLastName = ctLastName;
    this.ctEmail = ctEmail;
    this.startDate = startDate;
  }

  public List<CapdevLocations> getCapDevCountries() {
    return capDevCountries;
  }


  public Set<CapdevDiscipline> getCapdevDisciplines() {
    return capdevDisciplines;
  }

  public Set<CapdevLocations> getCapdevLocations() {
    return capdevLocations;
  }

  public Set<CapdevOutputs> getCapdevOutputses() {
    return capdevOutputses;
  }

  public Set<CapdevParticipant> getCapdevParticipants() {
    return capdevParticipants;
  }

  public Set<CapdevPartners> getCapdevPartnerses() {
    return capdevPartnerses;
  }

  public List<CapdevLocations> getCapDevRegions() {
    return capDevRegions;
  }


  public Set<CapdevSupportingDocs> getCapdevSupportingDocses() {
    return capdevSupportingDocses;
  }


  public Set<CapdevTargetgroup> getCapdevTargetgroups() {
    return capdevTargetgroups;
  }


  public CapacityDevelopmentType getCapdevType() {
    return this.capdevType;
  }

  public int getCategory() {
    return this.category;
  }

  public Crp getCrp() {
    return this.crp;
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

  public Long getDuration() {
    return duration;
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

  public Long getId() {
    return this.id;
  }

  public String getModificationJustification() {
    return this.modificationJustification;
  }

  public Integer getNumMen() {
    return numMen;
  }

  public Integer getNumParticipants() {
    return this.numParticipants;
  }

  public Integer getNumWomen() {
    return numWomen;
  }

  public CenterProject getProject() {
    return this.project;
  }

  public CenterArea getResearchArea() {
    return this.researchArea;
  }

  public CenterProgram getResearchProgram() {
    return this.researchProgram;
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

  public String getTitle() {
    return this.title;
  }

  public User getUsersByCreatedBy() {
    return this.usersByCreatedBy;
  }


  public User getUsersByModifiedBy() {
    return this.usersByModifiedBy;
  }

  public boolean isActive() {
    return this.active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public void setCapDevCountries(List<CapdevLocations> capDevCountries) {
    this.capDevCountries = capDevCountries;
  }

  public void setCapdevDisciplines(Set<CapdevDiscipline> capdevDisciplines) {
    this.capdevDisciplines = capdevDisciplines;
  }

  public void setCapdevLocations(Set<CapdevLocations> capdevLocations) {
    this.capdevLocations = capdevLocations;
  }


  public void setCapdevOutputses(Set<CapdevOutputs> capdevOutputses) {
    this.capdevOutputses = capdevOutputses;
  }


  public void setCapdevParticipants(Set<CapdevParticipant> capdevParticipants) {
    this.capdevParticipants = capdevParticipants;
  }


  public void setCapdevPartnerses(Set<CapdevPartners> capdevPartnerses) {
    this.capdevPartnerses = capdevPartnerses;
  }


  public void setCapDevRegions(List<CapdevLocations> capDevRegions) {
    this.capDevRegions = capDevRegions;
  }


  public void setCapdevSupportingDocses(Set<CapdevSupportingDocs> capdevSupportingDocses) {
    this.capdevSupportingDocses = capdevSupportingDocses;
  }


  public void setCapdevTargetgroups(Set<CapdevTargetgroup> capdevTargetgroups) {
    this.capdevTargetgroups = capdevTargetgroups;
  }


  public void setCapdevType(CapacityDevelopmentType capdevType) {
    this.capdevType = capdevType;
  }


  public void setCategory(int category) {
    this.category = category;
  }


  public void setCrp(Crp crp) {
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


  public void setDuration(Long duration) {
    this.duration = duration;
  }


  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }


  public void setId(Long id) {
    this.id = id;
  }


  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }


  public void setNumMen(Integer numMen) {
    this.numMen = numMen;
  }


  public void setNumParticipants(Integer numParticipants) {
    this.numParticipants = numParticipants;
  }


  public void setNumWomen(Integer numWomen) {
    this.numWomen = numWomen;
  }


  public void setProject(CenterProject project) {
    this.project = project;
  }


  public void setResearchArea(CenterArea researchArea) {
    this.researchArea = researchArea;
  }

  public void setResearchProgram(CenterProgram researchProgram) {
    this.researchProgram = researchProgram;
  }


  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }


  public void setTitle(String title) {
    this.title = title;
  }


  public void setUsersByCreatedBy(User usersByCreatedBy) {
    this.usersByCreatedBy = usersByCreatedBy;
  }


  public void setUsersByModifiedBy(User usersByModifiedBy) {
    this.usersByModifiedBy = usersByModifiedBy;
  }


}

