/*****************************************************************
 * This file is part of Managing Agricultural Research for Learning &
 * Outcomes Platform (MARLO).
 * MARLO is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * MARLO is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with MARLO. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/

package org.cgiar.ccafs.marlo.data.model;

import org.cgiar.ccafs.marlo.data.IAuditLog;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class Deliverable implements java.io.Serializable, IAuditLog {


  private static final long serialVersionUID = 1867818669925473872L;


  @Expose
  private Long id;

  @Expose
  private Project project;


  @Expose
  private DeliverableType deliverableType;

  @Expose
  private String title;


  @Expose
  private String description;

  @Expose
  private String typeOther;


  @Expose
  private int year;


  @Expose
  private Integer newExpectedYear;


  @Expose
  private Integer status;

  @Expose
  private String statusDescription;

  @Expose
  private boolean active;

  @Expose
  private Date activeSince;


  @Expose
  private User createdBy;

  @Expose
  private User modifiedBy;
  @Expose
  private String modificationJustification;

  @Expose
  private CrpClusterKeyOutput crpClusterKeyOutput;


  @Expose
  private CrpProgramOutcome crpProgramOutcome;


  @Expose
  private Date createDate;


  private Set<DeliverableGenderLevel> deliverableGenderLevels = new HashSet<DeliverableGenderLevel>(0);


  private List<DeliverableGenderLevel> genderLevels;


  private Set<DeliverablePartnership> deliverablePartnerships = new HashSet<DeliverablePartnership>(0);


  private Set<DeliverableActivity> deliverableActivities = new HashSet<DeliverableActivity>(0);


  private Set<SectionStatus> sectionStatuses = new HashSet<SectionStatus>(0);


  private DeliverablePartnership responsiblePartner;


  private Set<DeliverableFundingSource> deliverableFundingSources = new HashSet<DeliverableFundingSource>(0);

  private List<DeliverableFundingSource> fundingSources;


  private List<DeliverablePartnership> otherPartners;


  private Set<DeliverableQualityCheck> deliverableQualityChecks = new HashSet<DeliverableQualityCheck>(0);


  @Expose
  private Boolean crossCuttingGender;


  @Expose
  private Boolean crossCuttingYouth;


  @Expose
  private Boolean crossCuttingCapacity;


  @Expose
  private Boolean crossCuttingNa;


  @Expose
  private String license;


  @Expose
  private String otherLicense;


  @Expose
  private Boolean allowModifications;


  @Expose
  private Boolean adoptedLicense;


  DeliverableQualityCheck qualityCheck;


  private Set<DeliverableMetadataElement> deliverableMetadataElements = new HashSet<DeliverableMetadataElement>(0);


  private Set<DeliverableDissemination> deliverableDisseminations = new HashSet<DeliverableDissemination>(0);


  private Set<DeliverableDataSharingFile> deliverableDataSharingFiles = new HashSet<DeliverableDataSharingFile>(0);


  private Set<DeliverablePublicationMetadata> deliverablePublicationMetadatas =
    new HashSet<DeliverablePublicationMetadata>(0);


  private Set<DeliverableDataSharing> deliverableDataSharings = new HashSet<DeliverableDataSharing>(0);


  private List<DeliverableMetadataElement> metadataElements;


  private List<DeliverableDissemination> disseminations;


  private List<DeliverableDataSharingFile> dataSharingFiles;


  private List<DeliverableFile> files;


  private List<DeliverablePublicationMetadata> publicationMetadatas;


  private DeliverablePublicationMetadata publication;

  private List<DeliverableDataSharing> dataSharing;

  private Set<DeliverableProgram> deliverablePrograms = new HashSet<DeliverableProgram>(0);


  private Set<DeliverableLeader> deliverableLeaders = new HashSet<DeliverableLeader>(0);


  private List<DeliverableProgram> programs;


  private List<DeliverableProgram> regions;

  private String flagshipValue;

  private String regionsValue;


  private List<DeliverableLeader> leaders;


  private List<MetadataElement> metadata;

  private Set<DeliverableCrp> deliverableCrps = new HashSet<DeliverableCrp>(0);
  private List<DeliverableCrp> crps;

  private DeliverableDissemination dissemination;
  private Set<DeliverableUser> deliverableUsers = new HashSet<DeliverableUser>(0);
  private List<DeliverableUser> users;
  private GlobalUnit crp;
  private Boolean isPublication;


  public Deliverable() {
  }


  public Deliverable(Project project, DeliverableType deliverableType, String title, String typeOther, int year,
    Integer status, String statusDescription, boolean active, Date activeSince, User createdBy, User modifiedBy,
    String modificationJustification, CrpClusterKeyOutput crpClusterKeyOutput, CrpProgramOutcome crpProgramOutcome,
    Set<DeliverablePartnership> deliverablePartnerships, Set<DeliverableActivity> deliverableActivities,
    Date createDate, String description) {
    this.project = project;
    this.deliverableType = deliverableType;
    this.title = title;
    this.typeOther = typeOther;
    this.year = year;
    this.status = status;
    this.statusDescription = statusDescription;
    this.active = active;
    this.activeSince = activeSince;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.modificationJustification = modificationJustification;
    this.crpProgramOutcome = crpProgramOutcome;
    this.crpClusterKeyOutput = crpClusterKeyOutput;
    this.deliverablePartnerships = deliverablePartnerships;
    this.deliverableActivities = deliverableActivities;
    this.createDate = createDate;
    this.description = description;
  }

  public Deliverable(Project project, int year, boolean active, Date activeSince, User createdBy, User modifiedBy,
    String modificationJustification) {
    this.project = project;
    this.year = year;
    this.active = active;
    this.activeSince = activeSince;
    this.createdBy = createdBy;
    this.modifiedBy = modifiedBy;
    this.modificationJustification = modificationJustification;
  }


  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }

    Deliverable other = (Deliverable) obj;
    if (this.getId() == null) {
      if (other.getId() != null) {
        return false;
      }
    } else if (!this.getId().equals(other.getId())) {
      return false;
    }
    return true;
  }


  public Date getActiveSince() {
    return this.activeSince;
  }

  public Boolean getAdoptedLicense() {
    return adoptedLicense;
  }


  public Boolean getAllowModifications() {
    return allowModifications;
  }

  public String getComposedName() {
    try {
      return "<b> (D" + this.id + ") " + this.getDeliverableType().getDescription() + "</b> - " + this.title;
    } catch (Exception e) {
      return "<b> (D" + this.id + ") </b> - " + this.title;

    }
  }


  public Date getCreateDate() {
    return createDate;
  }


  public User getCreatedBy() {
    return createdBy;
  }


  public Boolean getCrossCuttingCapacity() {
    return crossCuttingCapacity;
  }


  public Boolean getCrossCuttingGender() {
    return crossCuttingGender;
  }


  public Boolean getCrossCuttingNa() {
    return crossCuttingNa;
  }


  public Boolean getCrossCuttingYouth() {
    return crossCuttingYouth;
  }


  public GlobalUnit getCrp() {
    return crp;
  }


  public CrpClusterKeyOutput getCrpClusterKeyOutput() {
    return crpClusterKeyOutput;
  }

  public CrpProgramOutcome getCrpProgramOutcome() {
    return crpProgramOutcome;
  }

  public List<DeliverableCrp> getCrps() {
    return crps;
  }

  public List<DeliverableDataSharing> getDataSharing() {
    return dataSharing;
  }

  public List<DeliverableDataSharingFile> getDataSharingFiles() {
    return dataSharingFiles;
  }

  public Set<DeliverableActivity> getDeliverableActivities() {
    return deliverableActivities;
  }

  public Set<DeliverableCrp> getDeliverableCrps() {
    return deliverableCrps;
  }


  public Set<DeliverableDataSharingFile> getDeliverableDataSharingFiles() {
    return deliverableDataSharingFiles;
  }

  public Set<DeliverableDataSharing> getDeliverableDataSharings() {
    return deliverableDataSharings;
  }


  public Set<DeliverableDissemination> getDeliverableDisseminations() {
    return deliverableDisseminations;
  }


  public Set<DeliverableFundingSource> getDeliverableFundingSources() {
    return deliverableFundingSources;
  }


  public Set<DeliverableGenderLevel> getDeliverableGenderLevels() {
    return deliverableGenderLevels;
  }


  public Set<DeliverableLeader> getDeliverableLeaders() {
    return deliverableLeaders;
  }


  public Set<DeliverableMetadataElement> getDeliverableMetadataElements() {
    return deliverableMetadataElements;
  }


  public Set<DeliverablePartnership> getDeliverablePartnerships() {
    return deliverablePartnerships;
  }


  public Set<DeliverableProgram> getDeliverablePrograms() {
    return deliverablePrograms;
  }


  public Set<DeliverablePublicationMetadata> getDeliverablePublicationMetadatas() {
    return deliverablePublicationMetadatas;
  }

  public Set<DeliverableQualityCheck> getDeliverableQualityChecks() {
    return deliverableQualityChecks;
  }

  // Data List from Data Sharing


  public DeliverableType getDeliverableType() {
    return this.deliverableType;
  }

  public Set<DeliverableUser> getDeliverableUsers() {
    return deliverableUsers;
  }


  public String getDescription() {
    return description;
  }


  public DeliverableDissemination getDissemination() {
    return dissemination;
  }

  public List<DeliverableDissemination> getDisseminations() {
    return disseminations;
  }


  public List<DeliverableFile> getFiles() {
    return files;
  }

  public String getFlagshipValue() {
    return flagshipValue;
  }


  public List<DeliverableFundingSource> getFundingSources() {
    return fundingSources;
  }

  public List<DeliverableGenderLevel> getGenderLevels() {
    return genderLevels;
  }


  @Override
  public Long getId() {
    return id;
  }


  public long getID(int metadataID) {

    if (metadataElements != null) {
      for (DeliverableMetadataElement dmetadata : metadataElements) {
        if (dmetadata != null) {
          if (dmetadata.getMetadataElement() != null && dmetadata.getMetadataElement().getId() != null) {
            if (dmetadata.getMetadataElement().getId() == metadataID) {
              return dmetadata.getId().longValue();
            }
          }
        }


      }

    }

    return -1;
  }


  public Boolean getIsPublication() {
    return isPublication;
  }


  public List<DeliverableLeader> getLeaders() {
    return leaders;
  }


  public String getLicense() {
    return license;
  }


  public String getLicenseType() {
    if (license != null) {
      try {
        return LicensesTypeEnum.license(license).getValue();
      } catch (Exception e) {
        return null;
      }
    }
    return null;
  }

  @Override
  public String getLogDeatil() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id : ").append(this.getId());
    return sb.toString();
  }


  public long getMElementID(int metadataID) {
    if (metadataElements != null) {
      for (DeliverableMetadataElement dmetadata : metadataElements) {
        if (dmetadata != null) {
          if (dmetadata.getMetadataElement() != null && dmetadata.getMetadataElement().getId() != null) {
            if (dmetadata.getMetadataElement().getId() == metadataID) {
              return dmetadata.getId().longValue();
            }
          }
        }
      }
    }
    return -1;
  }

  public List<MetadataElement> getMetadata() {
    return metadata;
  }

  public DeliverableMetadataElement getMetadata(int metadataID) {
    String value = "";
    if (metadataElements != null) {
      for (DeliverableMetadataElement dmetadata : metadataElements) {
        if (dmetadata != null) {
          if (dmetadata.getMetadataElement() != null && dmetadata.getMetadataElement().getId() != null) {
            if (dmetadata.getMetadataElement().getId() == metadataID) {
              return dmetadata;
            }
          }
        }


      }
    }
    return null;
  }

  public List<DeliverableMetadataElement> getMetadataElements() {
    return metadataElements;
  }

  public int getMetadataID(String metadataName) {
    for (MetadataElement mData : metadata) {
      if (mData != null) {
        if (mData.getEcondedName().equals(metadataName)) {
          return mData.getId();
        }
      }
    }
    return -1;
  }

  public int getMetadataIndex(String metadataName) {
    int c = 0;
    for (MetadataElement mData : metadata) {
      if (mData != null) {
        if (mData.getEcondedName().equals(metadataName)) {
          return c;
        }
      }

      c++;
    }
    return -1;
  }

  public String getMetadataValue(int metadataID) {
    String value = "";
    if (metadataElements != null) {
      for (DeliverableMetadataElement dmetadata : metadataElements) {
        if (dmetadata != null) {
          if (dmetadata.getMetadataElement() != null && dmetadata.getMetadataElement().getId() != null) {
            if (dmetadata.getMetadataElement().getId() == metadataID) {
              value = dmetadata.getElementValue();
            }
          }
        }


      }
    }
    return value;
  }

  public String getMetadataValue(String metadataName) {
    if (metadataElements != null) {
      for (DeliverableMetadataElement mData : metadataElements) {
        if (mData != null) {
          if (mData.getMetadataElement() != null) {
            if (mData.getMetadataElement().getElement().equals(metadataName)) {
              return mData.getElementValue();
            }
          }
        }

      }
    }
    return "";
  }

  public String getMetadataValueByEncondedName(String metadataName) {
    if (metadataElements != null) {
      for (DeliverableMetadataElement mData : metadataElements) {
        if (mData != null) {
          if (mData.getMetadataElement() != null) {
            if (mData.getMetadataElement().getEcondedName().equals(metadataName)) {
              return mData.getElementValue();
            }
          }
        }

      }
    }
    return "";
  }

  @Override
  public String getModificationJustification() {
    return this.modificationJustification;
  }


  // End


  @Override
  public User getModifiedBy() {
    return modifiedBy;
  }

  public Integer getNewExpectedYear() {
    return newExpectedYear;
  }

  public String getOtherLicense() {
    return otherLicense;
  }

  // End


  public List<DeliverablePartnership> getOtherPartners() {
    return otherPartners;
  }

  public List<DeliverableProgram> getPrograms() {
    return programs;
  }


  public Project getProject() {
    return project;
  }

  public DeliverablePublicationMetadata getPublication() {
    return publication;
  }

  public List<DeliverablePublicationMetadata> getPublicationMetadatas() {
    return publicationMetadatas;
  }

  public DeliverableQualityCheck getQualityCheck() {
    return qualityCheck;
  }

  public List<DeliverableProgram> getRegions() {
    return regions;
  }

  public String getRegionsValue() {
    return regionsValue;
  }

  public DeliverablePartnership getResponsiblePartner() {
    return responsiblePartner;
  }

  public Set<SectionStatus> getSectionStatuses() {
    return sectionStatuses;
  }

  public Integer getStatus() {
    return this.status;
  }

  public String getStatusDescription() {
    return this.statusDescription;
  }

  public String getStatusName() {
    try {
      if (this.status != null) {
        return ProjectStatusEnum.getValue(this.status).getStatus() != null
          ? ProjectStatusEnum.getValue(this.status).getStatus() : "";
      } else {
        return "";
      }
    } catch (Exception e) {
      return "";
    }
  }

  public String getTitle() {
    return this.title;
  }


  public String getTypeOther() {
    return this.typeOther;
  }


  public List<DeliverableUser> getUsers() {
    return users;
  }


  public int getYear() {
    return this.year;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    return result;
  }

  @Override
  public boolean isActive() {
    return active;
  }

  public Boolean isRequieriedReporting(int year) {

    if (status == null && this.year <= year) {
      return true;
    }

    if (status != null && this.year <= year
      && status.intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
      return true;
    }

    if (status != null && newExpectedYear != null && this.newExpectedYear <= year
      && status.intValue() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())) {
      return true;
    }
    if (status != null && this.year == year
      && status.intValue() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())) {
      return true;
    }

    return false;
  }

  public boolean requeriedFair() {
    try {
      if (this.getDeliverableType().getFair()) {
        return true;
      }
      if (this.getDeliverableType().getDeliverableType().getFair()) {
        return true;
      }
    } catch (Exception e) {
      return false;
    }
    return false;
  }

  public void setActive(boolean active) {
    this.active = active;
  }


  public void setActiveSince(Date activeSince) {
    this.activeSince = activeSince;
  }

  public void setAdoptedLicense(Boolean adoptedLicense) {
    this.adoptedLicense = adoptedLicense;
  }


  public void setAllowModifications(Boolean allowModifications) {
    this.allowModifications = allowModifications;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public void setCreatedBy(User createdBy) {
    this.createdBy = createdBy;
  }

  public void setCrossCuttingCapacity(Boolean crossCuttingCapacity) {
    this.crossCuttingCapacity = crossCuttingCapacity;
  }

  public void setCrossCuttingGender(Boolean crossCuttingGender) {
    this.crossCuttingGender = crossCuttingGender;
  }

  public void setCrossCuttingNa(Boolean crossCuttingNa) {
    this.crossCuttingNa = crossCuttingNa;
  }


  public void setCrossCuttingYouth(Boolean crossCuttingYouth) {
    this.crossCuttingYouth = crossCuttingYouth;
  }

  public void setCrp(GlobalUnit crp) {
    this.crp = crp;
  }


  public void setCrpClusterKeyOutput(CrpClusterKeyOutput crpClusterKeyOutput) {
    this.crpClusterKeyOutput = crpClusterKeyOutput;
  }

  public void setCrpProgramOutcome(CrpProgramOutcome crpProgramOutcome) {
    this.crpProgramOutcome = crpProgramOutcome;
  }


  public void setCrps(List<DeliverableCrp> crps) {
    this.crps = crps;
  }


  public void setDataSharing(List<DeliverableDataSharing> dataSharing) {
    this.dataSharing = dataSharing;
  }


  public void setDataSharingFiles(List<DeliverableDataSharingFile> dataSharingFiles) {
    this.dataSharingFiles = dataSharingFiles;
  }

  public void setDeliverableActivities(Set<DeliverableActivity> deliverableActivities) {
    this.deliverableActivities = deliverableActivities;
  }


  public void setDeliverableCrps(Set<DeliverableCrp> deliverableCrps) {
    this.deliverableCrps = deliverableCrps;
  }


  public void setDeliverableDataSharingFiles(Set<DeliverableDataSharingFile> deliverableDataSharingFiles) {
    this.deliverableDataSharingFiles = deliverableDataSharingFiles;
  }


  public void setDeliverableDataSharings(Set<DeliverableDataSharing> deliverableDataSharings) {
    this.deliverableDataSharings = deliverableDataSharings;
  }


  public void setDeliverableDisseminations(Set<DeliverableDissemination> deliverableDisseminations) {
    this.deliverableDisseminations = deliverableDisseminations;
  }

  public void setDeliverableFundingSources(Set<DeliverableFundingSource> deliverableFundingSources) {
    this.deliverableFundingSources = deliverableFundingSources;
  }

  public void setDeliverableGenderLevels(Set<DeliverableGenderLevel> deliverableGenderLevels) {
    this.deliverableGenderLevels = deliverableGenderLevels;
  }


  public void setDeliverableLeaders(Set<DeliverableLeader> deliverableLeaders) {
    this.deliverableLeaders = deliverableLeaders;
  }


  public void setDeliverableMetadataElements(Set<DeliverableMetadataElement> deliverableMetadataElements) {
    this.deliverableMetadataElements = deliverableMetadataElements;
  }


  public void setDeliverablePartnerships(Set<DeliverablePartnership> deliverablePartnerships) {
    this.deliverablePartnerships = deliverablePartnerships;
  }

  public void setDeliverablePrograms(Set<DeliverableProgram> deliverablePrograms) {
    this.deliverablePrograms = deliverablePrograms;
  }


  public void setDeliverablePublicationMetadatas(Set<DeliverablePublicationMetadata> deliverablePublicationMetadatas) {
    this.deliverablePublicationMetadatas = deliverablePublicationMetadatas;
  }


  public void setDeliverableQualityChecks(Set<DeliverableQualityCheck> deliverableQualityChecks) {
    this.deliverableQualityChecks = deliverableQualityChecks;
  }


  public void setDeliverableType(DeliverableType deliverableType) {
    this.deliverableType = deliverableType;
  }

  public void setDeliverableUsers(Set<DeliverableUser> deliverableUsers) {
    this.deliverableUsers = deliverableUsers;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public void setDissemination(DeliverableDissemination dissemination) {
    this.dissemination = dissemination;
  }

  public void setDisseminations(List<DeliverableDissemination> disseminations) {
    this.disseminations = disseminations;
  }

  public void setFiles(List<DeliverableFile> files) {
    this.files = files;
  }


  public void setFlagshipValue(String flagshipValue) {
    this.flagshipValue = flagshipValue;
  }

  public void setFundingSources(List<DeliverableFundingSource> fundingSources) {
    this.fundingSources = fundingSources;
  }


  public void setGenderLevels(List<DeliverableGenderLevel> genderLevels) {
    this.genderLevels = genderLevels;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setIsPublication(Boolean isPublication) {
    this.isPublication = isPublication;
  }

  public void setLeaders(List<DeliverableLeader> leaders) {
    this.leaders = leaders;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  public void setMetadata(List<MetadataElement> metadata) {
    this.metadata = metadata;
  }

  public void setMetadataElements(List<DeliverableMetadataElement> metadataElements) {
    this.metadataElements = metadataElements;
  }


  public void setModificationJustification(String modificationJustification) {
    this.modificationJustification = modificationJustification;
  }

  public void setModifiedBy(User modifiedBy) {
    this.modifiedBy = modifiedBy;
  }

  public void setNewExpectedYear(Integer newExpectedYear) {
    this.newExpectedYear = newExpectedYear;
  }


  public void setOtherLicense(String otherLicense) {
    this.otherLicense = otherLicense;
  }

  public void setOtherPartners(List<DeliverablePartnership> otherPartners) {
    this.otherPartners = otherPartners;
  }

  public void setPrograms(List<DeliverableProgram> programs) {
    this.programs = programs;
  }

  public void setProject(Project project) {
    this.project = project;
  }


  public void setPublication(DeliverablePublicationMetadata publication) {
    this.publication = publication;
  }


  public void setPublicationMetadatas(List<DeliverablePublicationMetadata> publicationMetadatas) {
    this.publicationMetadatas = publicationMetadatas;
  }


  public void setQualityCheck(DeliverableQualityCheck qualityCheck) {
    this.qualityCheck = qualityCheck;
  }

  public void setRegions(List<DeliverableProgram> regions) {
    this.regions = regions;
  }


  public void setRegionsValue(String regionsValue) {
    this.regionsValue = regionsValue;
  }


  public void setResponsiblePartner(DeliverablePartnership responsiblePartner) {
    this.responsiblePartner = responsiblePartner;
  }

  public void setSectionStatuses(Set<SectionStatus> sectionStatuses) {
    this.sectionStatuses = sectionStatuses;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public void setStatusDescription(String statusDescription) {
    this.statusDescription = statusDescription;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  public void setTypeOther(String typeOther) {
    this.typeOther = typeOther;
  }


  public void setUsers(List<DeliverableUser> users) {
    this.users = users;
  }

  public void setYear(int year) {
    this.year = year;
  }

}
