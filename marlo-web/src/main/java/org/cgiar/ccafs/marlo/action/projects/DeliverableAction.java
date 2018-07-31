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

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrossCuttingScoringManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrpManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDataSharingFileManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDisseminationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableFundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableGenderLevelManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableIntellectualAssetManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableParticipantLocationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableParticipantManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePublicationMetadataManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableQualityAnswerManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableQualityCheckManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GenderTypeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.MetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerDivisionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndFillingTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPatentStatusManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndRegionManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndTypeActivityManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndTypeParticipantManager;
import org.cgiar.ccafs.marlo.data.manager.RepositoryChannelManager;
import org.cgiar.ccafs.marlo.data.model.CrossCuttingScoring;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.DeliverableIntellectualAssetTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.DeliverableParticipantLocation;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityAnswer;
import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.FileDB;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.GenderType;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LicensesTypeEnum;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerDivision;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepIndFillingType;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndPatentStatus;
import org.cgiar.ccafs.marlo.data.model.RepIndRegion;
import org.cgiar.ccafs.marlo.data.model.RepIndTypeActivity;
import org.cgiar.ccafs.marlo.data.model.RepIndTypeParticipant;
import org.cgiar.ccafs.marlo.data.model.RepositoryChannel;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.projects.DeliverableValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 * @author avalencia - CCAFS
 * @date Nov 8, 2017
 * @time 9:48:25 AM: Added repositoryChannel List from Database
 * @time 1:36:16 PM: Fix -1 id error
 */
public class DeliverableAction extends BaseAction {


  private static final long serialVersionUID = -4474372683580321612L;
  private final Logger logger = LoggerFactory.getLogger(DeliverableAction.class);

  // Managers
  private AuditLogManager auditLogManager;
  private GlobalUnitManager crpManager;
  private DeliverableDataSharingFileManager deliverableDataSharingFileManager;
  private DeliverablePublicationMetadataManager deliverablePublicationMetadataManager;
  private DeliverableFundingSourceManager deliverableFundingSourceManager;
  private DeliverableUserManager deliverableUserManager;
  private DeliverableCrpManager deliverableCrpManager;
  private DeliverableGenderLevelManager deliverableGenderLevelManager;
  private DeliverableManager deliverableManager;
  private DeliverableInfoManager deliverableInfoManager;
  private DeliverableMetadataElementManager deliverableMetadataElementManager;
  private DeliverablePartnershipManager deliverablePartnershipManager;
  private InstitutionManager institutionManager;
  private DeliverableQualityAnswerManager deliverableQualityAnswerManager;
  private DeliverableQualityCheckManager deliverableQualityCheckManager;
  private DeliverableDisseminationManager deliverableDisseminationManager;
  private DeliverableTypeManager deliverableTypeManager;
  private CrpProgramManager crpProgramManager;
  private FileDBManager fileDBManager;
  private GenderTypeManager genderTypeManager;
  private FundingSourceManager fundingSourceManager;
  private CrossCuttingScoringManager crossCuttingManager;
  private MetadataElementManager metadataElementManager;
  private ProjectManager projectManager;
  private ProjectPartnerManager projectPartnerManager;
  private ProjectPartnerPersonManager projectPartnerPersonManager;
  private PartnerDivisionManager partnerDivisionManager;
  private RepositoryChannelManager repositoryChannelManager;
  private DeliverableIntellectualAssetManager deliverableIntellectualAssetManager;
  private DeliverableParticipantManager deliverableParticipantManager;
  private RepIndTypeActivityManager repIndTypeActivityManager;
  private RepIndTypeParticipantManager repIndTypeParticipantManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private RepIndRegionManager repIndRegionManager;
  private LocElementManager locElementManager;
  private DeliverableParticipantLocationManager deliverableParticipantLocationManager;
  private RepIndFillingTypeManager repIndFillingTypeManager;
  private RepIndPatentStatusManager repIndPatentStatusManager;

  // Parameters
  private List<DeliverableQualityAnswer> answers;
  private List<RepositoryChannel> repositoryChannels;
  private ArrayList<GlobalUnit> crps;
  private ArrayList<CrpProgram> programs;
  private Deliverable deliverable;
  private long deliverableID;
  private List<DeliverableType> deliverableSubTypes;
  private Boolean has_specific_management_deliverables;
  private Boolean isManagingPartnerPersonRequerid;
  private List<DeliverableType> deliverableTypeParent;
  private DeliverableValidator deliverableValidator;
  private List<FundingSource> fundingSources;
  private List<GenderType> genderLevels;
  private List<CrpClusterKeyOutput> keyOutputs;
  private GlobalUnit loggedCrp;
  private List<ProjectPartnerPerson> partnerPersons;
  private List<ProjectPartner> partners;
  private Project project;
  private long projectID;
  private List<ProjectOutcome> projectOutcome;
  private List<ProjectFocus> projectPrograms;
  private Map<String, String> status;
  private String transaction;
  private int indexTab;
  private List<PartnerDivision> divisions;
  private List<CrossCuttingScoring> crossCuttingDimensions;
  private Map<Long, String> crossCuttingScoresMap;
  private List<RepIndTypeActivity> repIndTypeActivities;
  private List<RepIndTypeParticipant> repIndTypeParticipants;
  private List<RepIndGeographicScope> repIndGeographicScopes;
  private List<RepIndRegion> repIndRegions;
  private List<LocElement> countries;
  private List<RepIndFillingType> repIndFillingTypes;
  private List<RepIndPatentStatus> repIndPatentStatuses;
  private Map<String, String> statuses;


  @Inject
  public DeliverableAction(APConfig config, DeliverableTypeManager deliverableTypeManager,
    DeliverableMetadataElementManager deliverableMetadataElementManager, DeliverableManager deliverableManager,
    GlobalUnitManager crpManager, ProjectManager projectManager,
    ProjectPartnerPersonManager projectPartnerPersonManager,
    DeliverablePartnershipManager deliverablePartnershipManager, AuditLogManager auditLogManager,
    DeliverableValidator deliverableValidator, ProjectPartnerManager projectPartnerManager,
    FundingSourceManager fundingSourceManager, DeliverableFundingSourceManager deliverableFundingSourceManager,
    DeliverableGenderLevelManager deliverableGenderLevelManager,
    DeliverableQualityCheckManager deliverableQualityCheckManager, DeliverableCrpManager deliverableCrpManager,
    DeliverableQualityAnswerManager deliverableQualityAnswerManager,
    DeliverableDataSharingFileManager deliverableDataSharingFileManager, FileDBManager fileDBManager,
    DeliverableUserManager deliverableUserManager, GenderTypeManager genderTypeManager,
    DeliverablePublicationMetadataManager deliverablePublicationMetadataManager, InstitutionManager institutionManager,
    MetadataElementManager metadataElementManager, DeliverableDisseminationManager deliverableDisseminationManager,
    PartnerDivisionManager partnerDivisionManager, RepositoryChannelManager repositoryChannelManager,
    DeliverableInfoManager deliverableInfoManager, CrossCuttingScoringManager crossCuttingManager,
    CrpProgramManager crpProgramManager, DeliverableIntellectualAssetManager deliverableIntellectualAssetManager,
    RepIndTypeActivityManager repIndTypeActivityManager, RepIndTypeParticipantManager repIndTypeParticipantManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager, RepIndRegionManager repIndRegionManager,
    LocElementManager locElementManager, DeliverableParticipantLocationManager deliverableParticipantLocationManager,
    DeliverableParticipantManager deliverableParticipantManager, RepIndFillingTypeManager repIndFillingTypeManager,
    RepIndPatentStatusManager repIndPatentStatusManager) {
    super(config);
    this.deliverableManager = deliverableManager;
    this.deliverableTypeManager = deliverableTypeManager;
    this.crpManager = crpManager;
    this.deliverableUserManager = deliverableUserManager;
    this.projectManager = projectManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.institutionManager = institutionManager;
    this.deliverableCrpManager = deliverableCrpManager;
    this.deliverablePublicationMetadataManager = deliverablePublicationMetadataManager;
    this.projectPartnerPersonManager = projectPartnerPersonManager;
    this.deliverablePartnershipManager = deliverablePartnershipManager;
    this.auditLogManager = auditLogManager;
    this.deliverableValidator = deliverableValidator;
    this.projectPartnerManager = projectPartnerManager;
    this.genderTypeManager = genderTypeManager;
    this.deliverableFundingSourceManager = deliverableFundingSourceManager;
    this.fundingSourceManager = fundingSourceManager;
    this.deliverableGenderLevelManager = deliverableGenderLevelManager;
    this.deliverableQualityCheckManager = deliverableQualityCheckManager;
    this.deliverableQualityAnswerManager = deliverableQualityAnswerManager;
    this.fileDBManager = fileDBManager;
    this.deliverableDataSharingFileManager = deliverableDataSharingFileManager;
    this.metadataElementManager = metadataElementManager;
    this.deliverableMetadataElementManager = deliverableMetadataElementManager;
    this.deliverableDisseminationManager = deliverableDisseminationManager;
    this.partnerDivisionManager = partnerDivisionManager;
    this.repositoryChannelManager = repositoryChannelManager;
    this.crossCuttingManager = crossCuttingManager;
    this.crpProgramManager = crpProgramManager;
    this.deliverableIntellectualAssetManager = deliverableIntellectualAssetManager;
    this.deliverableParticipantManager = deliverableParticipantManager;
    this.repIndTypeActivityManager = repIndTypeActivityManager;
    this.repIndTypeParticipantManager = repIndTypeParticipantManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.repIndRegionManager = repIndRegionManager;
    this.locElementManager = locElementManager;
    this.deliverableParticipantLocationManager = deliverableParticipantLocationManager;
    this.repIndFillingTypeManager = repIndFillingTypeManager;
    this.repIndPatentStatusManager = repIndPatentStatusManager;
  }


  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
    }
    deliverable.getDeliverableInfo().setCrpClusterKeyOutput(null);

    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    } else {
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();

    return SUCCESS;
  }


  public Boolean candEditExpectedYear(long deliverableID) {
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);

    if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() == null) {
      return false;
    }

    if (this.isReportingActive()) {
      if (((deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null
        && deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != -1)
        && deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Complete.getStatusId()))
        || deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
        return true;
      }


    } else {
      if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
        .parseInt(ProjectStatusEnum.Extended.getStatusId())
        || deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
          .parseInt(ProjectStatusEnum.Cancelled.getStatusId())) {
        return true;
      }
    }

    return false;

  }


  public Boolean candEditYear(long deliverableID) {
    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
    SimpleDateFormat dateFormat = new SimpleDateFormat(APConstants.DATE_FORMAT);
    if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() == null) {
      return true;
    }


    if (deliverable.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getActualPhase().getYear()) {
      return true;
    }

    if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
      .parseInt(ProjectStatusEnum.Extended.getStatusId())
      || deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
        .parseInt(ProjectStatusEnum.Ongoing.getStatusId())) {
      return false;
    }


    if (this.isDeliverableNew(deliverableID)) {
      return true;
    } else {
      return false;
    }


  }


  /**
   * Check changes and update the partnership existing in DB
   * 
   * @param partnershipDBUpdated: Partnership from DB
   * @param partnershipManaged: Partnership sent from interface
   * @param partnershipType: partnership type
   */
  private void checkChangesAndUpdateDeliverablePartnership(DeliverablePartnership partnershipDBUpdated,
    DeliverablePartnership partnershipManaged) {
    Boolean hasChanges = false;
    DeliverablePartnership partnershipDB = this.copyDeliverablePartnership(partnershipDBUpdated);

    if (partnershipDBUpdated.getProjectPartnerPerson() != null) {
      if (partnershipManaged.getProjectPartnerPerson() != null) {
        partnershipManaged.setProjectPartnerPerson(projectPartnerPersonManager
          .getProjectPartnerPersonById(partnershipManaged.getProjectPartnerPerson().getId()));
        if (!partnershipDBUpdated.getProjectPartnerPerson().getUser().getId()
          .equals(partnershipManaged.getProjectPartnerPerson().getUser().getId())) {
          hasChanges = true;
          partnershipDBUpdated.setProjectPartnerPerson(partnershipManaged.getProjectPartnerPerson());
        }

        if (partnershipManaged.getPartnerDivision() != null && partnershipManaged.getPartnerDivision().getId() != -1) {
          if (partnershipDBUpdated.getPartnerDivision() != null) {
            if (!partnershipManaged.getPartnerDivision().equals(partnershipDBUpdated.getPartnerDivision())) {
              hasChanges = true;
              partnershipDBUpdated.setPartnerDivision(partnershipManaged.getPartnerDivision());
            }
          } else {
            hasChanges = true;
            partnershipDBUpdated.setPartnerDivision(partnershipManaged.getPartnerDivision());
          }
        } else {
          if (partnershipDBUpdated.getPartnerDivision() != null) {
            hasChanges = true;
            partnershipDBUpdated.setPartnerDivision(null);
          }
        }

      } else {
        if (!isManagingPartnerPersonRequerid) {
          hasChanges = true;
          partnershipDBUpdated.setProjectPartnerPerson(null);
        }
      }
    } else {
      if (partnershipManaged.getProjectPartnerPerson() != null) {
        hasChanges = true;
        partnershipDBUpdated.setProjectPartnerPerson(partnershipManaged.getProjectPartnerPerson());
        if (partnershipManaged.getPartnerDivision() != null && partnershipManaged.getPartnerDivision().getId() != -1) {
          partnershipDBUpdated.setPartnerDivision(partnershipManaged.getPartnerDivision());
        }
      }
    }
    if (hasChanges) {
      deliverablePartnershipManager.updateDeliverablePartnership(partnershipDBUpdated, partnershipDB);
    }
  }


  private DeliverablePartnership copyDeliverablePartnership(DeliverablePartnership partnershipResponsibleDB) {
    DeliverablePartnership deliverablePartnership = new DeliverablePartnership();
    deliverablePartnership.setProjectPartner(partnershipResponsibleDB.getProjectPartner());
    deliverablePartnership.setProjectPartnerPerson(partnershipResponsibleDB.getProjectPartnerPerson());
    if (partnershipResponsibleDB.getPartnerDivision() != null
      && partnershipResponsibleDB.getPartnerDivision().getId() != -1) {
      deliverablePartnership.setPartnerDivision(partnershipResponsibleDB.getPartnerDivision());
    } else {
      deliverablePartnership.setPartnerDivision(null);
    }
    deliverablePartnership.setPartnerType(partnershipResponsibleDB.getPartnerType());
    return deliverablePartnership;
  }


  private void createAndSaveNewDeliverablePartnership(DeliverablePartnership partnershipResponsibleManaged,
    String partnershipType) {
    DeliverablePartnership newDelivetablePartnership =
      this.createNewDeliverablePartnership(partnershipResponsibleManaged, partnershipType);
    deliverablePartnershipManager.saveDeliverablePartnership(newDelivetablePartnership);
  }


  private DeliverablePartnership createNewDeliverablePartnership(DeliverablePartnership partnershipResponsibleManaged,
    String value) {
    // Create a new one.
    DeliverablePartnership partnership = new DeliverablePartnership();
    if (partnershipResponsibleManaged.getProjectPartnerPerson() != null
      && partnershipResponsibleManaged.getProjectPartnerPerson().getId() != null) {
      partnership.setProjectPartnerPerson(partnershipResponsibleManaged.getProjectPartnerPerson());
    } else {
      partnership.setProjectPartnerPerson(null);
    }
    partnership.setPartnerType(value);
    partnership.setDeliverable(deliverable);
    partnership.setProjectPartner(partnershipResponsibleManaged.getProjectPartner());
    partnership.setPhase(this.getActualPhase());
    partnership = this.saveUpdateDeliverablePartnershipDivision(partnership, partnershipResponsibleManaged);
    return partnership;
  }


  /**
   * Delete Deliverable Gender Levels if there is no cross cutting gender component.
   * 
   * @param deliverablePrew
   */
  private void deleteDeliverableGenderLevels(Deliverable deliverablePrew) {
    if (!deliverablePrew.getDeliverableInfo(this.getActualPhase()).getCrossCuttingGender().booleanValue()) {
      Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
      List<DeliverableGenderLevel> deliverableGenderLevels =
        deliverableDB.getDeliverableGenderLevels().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      for (DeliverableGenderLevel genderLevel : deliverableGenderLevels) {
        deliverableGenderLevelManager.deleteDeliverableGenderLevel(genderLevel.getId());
      }
    }
  }


  private void deleteParticipantLocations(List<DeliverableParticipantLocation> locationsDB) {
    if (locationsDB != null) {
      for (DeliverableParticipantLocation deliverableParticipantLocation : locationsDB) {
        deliverableParticipantLocationManager
          .deleteDeliverableParticipantLocation(deliverableParticipantLocation.getId());
      }
    }
  }


  public List<DeliverableQualityAnswer> getAnswers() {
    return answers;
  }


  private Path getAutoSaveFilePath() {

    // get the class simple name
    String composedClassName = deliverable.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = deliverable.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);


  }

  public List<LocElement> getCountries() {
    return countries;
  }

  public List<CrossCuttingScoring> getCrossCuttingDimensions() {
    return crossCuttingDimensions;
  }

  public Map<Long, String> getCrossCuttingScoresMap() {
    return crossCuttingScoresMap;
  }

  public ArrayList<GlobalUnit> getCrps() {
    return crps;
  }


  public Deliverable getDeliverable() {
    return deliverable;
  }


  public long getDeliverableID() {
    return deliverableID;
  }

  public DeliverablePartnership getDeliverablePartnership(long projectPersonID) {

    if (deliverable.getOtherPartners() != null) {
      List<DeliverablePartnership> deliverablePartnerships = deliverable
        .getOtherPartners().stream().filter(d -> d.isActive() && d.getProjectPartnerPerson() != null
          && d.getProjectPartnerPerson().getId() != null && d.getProjectPartnerPerson().getId() == projectPersonID)
        .collect(Collectors.toList());

      for (DeliverablePartnership deliverablePartnership : deliverablePartnerships) {
        return deliverablePartnership;
      }
    }


    return null;

  }


  private DeliverablePartnership getDeliverablePartnershipResponsibleDB(Deliverable deliverableDB) {
    DeliverablePartnership partnershipResponsible = null;
    List<DeliverablePartnership> deliverablePartnerships = deliverableDB.getDeliverablePartnerships().stream()
      .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(this.getActualPhase())
        && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue())
        && dp.getProjectPartner() != null
        && (dp.getProjectPartnerPerson() == null || dp.getProjectPartnerPerson().isActive()))
      .collect(Collectors.toList());
    if (deliverablePartnerships != null && deliverablePartnerships.size() > 0) {
      try {
        partnershipResponsible = deliverablePartnerships.get(0);
      } catch (Exception e) {
        // NEVER EVER JUST SWALLOW UNCHECKED EXCEPTIONS! Logging this now.
        logger.error("unable to filter DeliverablePartnership list", e);
        // This is strange, shouldn't we re-throw the exception, in fact we shouldn't even catch it in the first place!
        partnershipResponsible = null;
      }

    }
    return partnershipResponsible;
  }

  public List<Map<String, Object>> getDeliverablesSubTypes(long deliverableTypeID) {
    List<Map<String, Object>> subTypes = new ArrayList<>();
    Map<String, Object> keyOutput;

    DeliverableType deliverableType = deliverableTypeManager.getDeliverableTypeById(deliverableTypeID);
    if (deliverableType != null) {
      if (deliverableType.getDeliverableTypes() != null) {
        for (DeliverableType deliverableSubType : deliverableType.getDeliverableTypes().stream()
          .collect(Collectors.toList())) {
          keyOutput = new HashMap<String, Object>();
          keyOutput.put("id", deliverableSubType.getId());
          keyOutput.put("name", deliverableSubType.getName());
          keyOutput.put("description", deliverableSubType.getDescription());
          keyOutput.put("fair", deliverableSubType.getFair());
          subTypes.add(keyOutput);
        }
      }
    }
    return subTypes;

  }


  public List<DeliverableType> getDeliverableSubTypes() {
    return deliverableSubTypes;
  }


  public List<DeliverableType> getDeliverableTypeParent() {
    return deliverableTypeParent;
  }

  public String getDeliverableUrl(String fileType) {
    return config.getDownloadURL() + "/" + this.getDeliverableUrlPath(fileType).replace('\\', '/');
  }

  public String getDeliverableUrlPath(String fileType) {
    return config.getProjectsBaseFolder(this.getCrpSession()) + File.separator + deliverable.getId() + File.separator
      + "deliverable" + File.separator + fileType + File.separator;
  }

  public List<PartnerDivision> getDivisions() {
    return divisions;
  }


  public List<FundingSource> getFundingSources() {
    return fundingSources;
  }


  public List<GenderType> getGenderLevels() {
    return genderLevels;
  }


  public int getIndexTab() {
    return indexTab;
  }


  public List<CrpClusterKeyOutput> getKeyOutputs() {
    return keyOutputs;
  }


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }


  /**
   * Get the ProjectPartnerPerson from the submitted form.
   * 
   * @return
   */
  private ProjectPartnerPerson getPartnerPerson() {
    ProjectPartnerPerson partnerPerson = null;

    if (deliverable.getResponsiblePartner() != null
      && deliverable.getResponsiblePartner().getProjectPartnerPerson() != null
      && deliverable.getResponsiblePartner().getProjectPartnerPerson().getId() != null
      && deliverable.getResponsiblePartner().getProjectPartnerPerson().getId().longValue() != -1) {
      partnerPerson = projectPartnerPersonManager
        .getProjectPartnerPersonById(deliverable.getResponsiblePartner().getProjectPartnerPerson().getId());
    }
    return partnerPerson;
  }


  public List<ProjectPartnerPerson> getPartnerPersons() {
    return partnerPersons;
  }

  private ProjectPartner getPartnerResponsible() {
    ProjectPartner projectPartner = null;

    if (deliverable.getResponsiblePartner() != null && deliverable.getResponsiblePartner().getProjectPartner() != null
      && deliverable.getResponsiblePartner().getProjectPartner().getId() != null
      && deliverable.getResponsiblePartner().getProjectPartner().getId().longValue() != -1) {
      projectPartner =
        projectPartnerManager.getProjectPartnerById(deliverable.getResponsiblePartner().getProjectPartner().getId());
    }
    return projectPartner;
  }

  public List<ProjectPartner> getPartners() {
    return partners;
  }

  public List<ProjectPartnerPerson> getPersons(long projectPartnerId) {
    List<ProjectPartnerPerson> projectPartnerPersons =
      projectPartnerPersonManager.findAllForProjectPartner(projectPartnerId);
    return projectPartnerPersons;
  }


  public ArrayList<CrpProgram> getPrograms() {
    return programs;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public List<ProjectOutcome> getProjectOutcome() {
    return projectOutcome;
  }

  public List<ProjectFocus> getProjectPrograms() {
    return projectPrograms;
  }


  public List<RepIndFillingType> getRepIndFillingTypes() {
    return repIndFillingTypes;
  }


  public List<RepIndGeographicScope> getRepIndGeographicScopes() {
    return repIndGeographicScopes;
  }

  public List<RepIndPatentStatus> getRepIndPatentStatuses() {
    return repIndPatentStatuses;
  }


  public List<RepIndRegion> getRepIndRegions() {
    return repIndRegions;
  }

  public List<RepIndTypeActivity> getRepIndTypeActivities() {
    return repIndTypeActivities;
  }

  public List<RepIndTypeParticipant> getRepIndTypeParticipants() {
    return repIndTypeParticipants;
  }


  public List<RepositoryChannel> getRepositoryChannels() {
    return repositoryChannels;
  }

  public List<ProjectPartner> getSelectedPartners() {

    Set<ProjectPartner> deliverablePartnerPersonsSet = new HashSet<>();
    List<ProjectPartner> deliverablePartnerPersons = new ArrayList<>();

    if (deliverable.getOtherPartners() != null) {
      for (DeliverablePartnership deliverablePartnership : deliverable.getOtherPartners().stream()
        .collect(Collectors.toList())) {
        if (deliverablePartnership.getProjectPartner() != null) {
          deliverablePartnerPersonsSet.add(deliverablePartnership.getProjectPartner());
        }

      }
    }


    deliverablePartnerPersons.addAll(deliverablePartnerPersonsSet);
    return deliverablePartnerPersons;

  }

  public List<Long> getSelectedPersons(long partnerID) {


    List<ProjectPartnerPerson> deliverablePartnerPersons = new ArrayList<ProjectPartnerPerson>();

    for (DeliverablePartnership deliverablePartnership : deliverable.getOtherPartners().stream()
      .filter(o -> o.isActive() && o.getProjectPartner() != null && o.getProjectPartner().getId() == partnerID)
      .collect(Collectors.toList())) {
      if (deliverablePartnership.getProjectPartnerPerson() != null
        && deliverablePartnership.getProjectPartnerPerson().isActive()
        && deliverablePartnership.getProjectPartnerPerson().getProjectPartner() != null
        && deliverablePartnership.getProjectPartnerPerson().getProjectPartner().isActive()) {
        deliverablePartnerPersons.add(deliverablePartnership.getProjectPartnerPerson());
      }
    }
    List<Long> projectPartnerPersonIds =
      deliverablePartnerPersons.stream().map(e -> e.getId()).collect(Collectors.toList());

    return projectPartnerPersonIds;

  }


  public Map<String, String> getStatus() {
    return status;
  }


  public Map<String, String> getStatuses() {
    return statuses;
  }


  public String getTransaction() {
    return transaction;
  }


  @Override
  public boolean isPPA(Institution institution) {
    if (institution == null) {
      return false;
    }

    if (institution.getId() != null) {
      institution = institutionManager.getInstitutionById(institution.getId());
      if (institution != null) {
        if (institution.getCrpPpaPartners().stream()
          .filter(c -> c.isActive() && c.getCrp().getId().longValue() == loggedCrp.getId().longValue() && c.isActive())
          .collect(Collectors.toList()).size() > 0) {
          return true;
        }
      }

    }

    return false;
  }


  public boolean isSelectedPerson(long projectPartnerPersonId, long projectPartner) {
    return this.getSelectedPersons(projectPartner).contains(new Long(projectPartnerPersonId));
  }


  public List<DeliverablePartnership> otherPartnersAutoSave() {
    try {
      List<DeliverablePartnership> list = new ArrayList<>();
      for (DeliverablePartnership partnership : deliverable.getOtherPartners()) {

        ProjectPartnerPerson partnerPersonDb = new ProjectPartnerPerson();
        if (partnership.getProjectPartnerPerson() != null && partnership.getProjectPartnerPerson().getId() != null) {
          partnerPersonDb =
            projectPartnerPersonManager.getProjectPartnerPersonById(partnership.getProjectPartnerPerson().getId());
        }

        ProjectPartner partnerDb = projectPartnerManager.getProjectPartnerById(partnership.getProjectPartner().getId());
        DeliverablePartnership partnershipOth = new DeliverablePartnership();
        partnershipOth.setId(partnership.getId());
        partnershipOth.setDeliverable(deliverable);
        partnershipOth.setProjectPartnerPerson(partnerPersonDb);
        partnershipOth.setProjectPartner(partnerDb);
        partnershipOth.setPartnerType(DeliverablePartnershipTypeEnum.OTHER.getValue());
        if (partnership.getPartnerDivision() != null && partnership.getPartnerDivision().getId() != null) {
          partnershipOth.setPartnerDivision(partnership.getPartnerDivision());
        }
        list.add(partnershipOth);
      }

      return list;
    } catch (Exception e) {
      logger.error("unable to do otherPartnersAutoSave", e);
      return null;
    }

  }

  public void parnershipNewData() {
    if (deliverable.getOtherPartners() != null) {
      for (DeliverablePartnership deliverablePartnership : deliverable.getOtherPartners()) {
        if (deliverablePartnership.getProjectPartnerPerson() != null) {
          if (deliverablePartnership.getId() == null && (deliverablePartnership.getProjectPartnerPerson() != null)
            && (deliverablePartnership.getProjectPartnerPerson().getId() != null)) {


            ProjectPartnerPerson partnerPerson = projectPartnerPersonManager
              .getProjectPartnerPersonById(deliverablePartnership.getProjectPartnerPerson().getId());

            if (partnerPerson != null) {
              DeliverablePartnership partnership = new DeliverablePartnership();
              partnership.setProjectPartnerPerson(partnerPerson);
              partnership.setPartnerType(DeliverablePartnershipTypeEnum.OTHER.getValue());
              partnership.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
              partnership.setPhase(this.getActualPhase());
              if (deliverablePartnership.getPartnerDivision() != null
                && deliverablePartnership.getPartnerDivision().getId().longValue() != -1) {
                try {
                  PartnerDivision division =
                    partnerDivisionManager.getPartnerDivisionById(deliverablePartnership.getPartnerDivision().getId());
                  partnership.setPartnerDivision(division);
                } catch (Exception e) {
                  partnership.setPartnerDivision(null);
                }
              } else {
                partnership.setPartnerDivision(null);
              }

              deliverablePartnershipManager.saveDeliverablePartnership(partnership);

            }


          } else {

            long partnerShipPrewId = 0;

            partnerShipPrewId = deliverablePartnershipManager
              .getDeliverablePartnershipById(deliverablePartnership.getId()).getProjectPartnerPerson().getId();


            long partnerShipId = deliverablePartnership.getProjectPartnerPerson().getId();
            if (partnerShipPrewId != partnerShipId) {

              ProjectPartnerPerson partnerPerson = projectPartnerPersonManager
                .getProjectPartnerPersonById(deliverablePartnership.getProjectPartnerPerson().getId());

              deliverablePartnershipManager.deleteDeliverablePartnership(deliverablePartnership.getId());

              if (partnerPerson != null) {

                DeliverablePartnership partnershipNew = new DeliverablePartnership();
                partnershipNew.setProjectPartnerPerson(partnerPerson);
                partnershipNew.setPartnerType(DeliverablePartnershipTypeEnum.OTHER.getValue());
                partnershipNew.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
                partnershipNew.setPhase(this.getActualPhase());
                if (deliverablePartnership.getPartnerDivision() != null
                  && deliverablePartnership.getPartnerDivision().getId().longValue() != -1) {
                  try {
                    PartnerDivision division = partnerDivisionManager
                      .getPartnerDivisionById(deliverablePartnership.getPartnerDivision().getId());
                    partnershipNew.setPartnerDivision(division);
                  } catch (Exception e) {
                    partnershipNew.setPartnerDivision(null);
                  }
                } else {
                  partnershipNew.setPartnerDivision(null);
                }
                deliverablePartnershipManager.saveDeliverablePartnership(partnershipNew);
              }


            } else {
              DeliverablePartnership partnershipDB =
                deliverablePartnershipManager.getDeliverablePartnershipById(deliverablePartnership.getId());

              if (deliverablePartnership.getPartnerDivision() != null
                && deliverablePartnership.getPartnerDivision().getId().longValue() != -1) {
                try {
                  PartnerDivision division =
                    partnerDivisionManager.getPartnerDivisionById(deliverablePartnership.getPartnerDivision().getId());
                  partnershipDB.setPartnerDivision(division);
                } catch (Exception e) {
                  partnershipDB.setPartnerDivision(null);
                }

              } else {
                partnershipDB.setPartnerDivision(null);
              }

              deliverablePartnershipManager.saveDeliverablePartnership(partnershipDB);

            }
          }
        }

      }
    }
  }

  public void partnershipPreviousData(Deliverable deliverablePrew) {
    if (deliverablePrew.getDeliverablePartnerships() != null
      && deliverablePrew.getDeliverablePartnerships().size() > 0) {
      List<DeliverablePartnership> partnerShipsPrew =
        deliverablePrew.getDeliverablePartnerships().stream()
          .filter(dp -> dp.isActive() && dp.getPhase().equals(this.getActualPhase())
            && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.OTHER.getValue()))
          .collect(Collectors.toList());

      if (deliverable.getOtherPartners() == null) {
        deliverable.setOtherPartners(new ArrayList<>());
      }
      for (DeliverablePartnership deliverablePartnership : deliverable.getOtherPartners()) {
        if (deliverablePartnership.getProjectPartnerPerson() == null) {
          deliverablePartnership.setId(null);
        }
      }

      for (DeliverablePartnership deliverablePartnership : partnerShipsPrew) {
        if (deliverable.getOtherPartners() != null) {
          if (!deliverable.getOtherPartners().contains(deliverablePartnership)) {
            deliverablePartnershipManager.deleteDeliverablePartnership(deliverablePartnership.getId());
          }
        }

      }
    }
  }

  @Override
  public void prepare() throws Exception {

    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    try {
      deliverableID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_DELIVERABLE_REQUEST_ID)));
    } catch (Exception e) {
      logger.error("unable to parse deliverableID", e);
      /**
       * Original code swallows the exception and didn't even log it. Now we at least log it,
       * but we need to revisit to see if we should continue processing or re-throw the exception.
       */
    }
    has_specific_management_deliverables =
      this.hasSpecificities(APConstants.CRP_HAS_SPECIFIC_MANAGEMENT_DELIVERABLE_TYPES);
    isManagingPartnerPersonRequerid = this.hasSpecificities(APConstants.CRP_MANAGING_PARTNERS_CONTACT_PERSONS);

    divisions = new ArrayList<>(
      partnerDivisionManager.findAll().stream().filter(pd -> pd.isActive()).collect(Collectors.toList()));


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Deliverable history = (Deliverable) auditLogManager.getHistory(transaction);

      if (history != null) {
        deliverable = history;
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }

      if (deliverable.getDeliverableQualityChecks() != null) {
        List<DeliverableQualityCheck> checks = new ArrayList<>(deliverable.getDeliverableQualityChecks().stream()
          .filter(qc -> qc.isActive() && qc.getPhase() != null && qc.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList()));
        if (!checks.isEmpty()) {
          deliverable.setQualityCheck(checks.get(0));
        }
      }

    } else {
      deliverable = deliverableManager.getDeliverableById(deliverableID);
    }

    if (deliverable != null) {

      project = projectManager.getProjectById(deliverable.getProject().getId());
      projectID = project.getId();
      project.getProjecInfoPhase(this.getActualPhase());
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave() && !this.isHttpPost()) {

        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();

        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        deliverable = (Deliverable) autoSaveReader.readFromJson(jReader);
        if (metadataElementManager.findAll() != null) {
          deliverable.setMetadata(new ArrayList<>(metadataElementManager.findAll()));
        }

        Deliverable deliverableDb = deliverableManager.getDeliverableById(deliverable.getId());
        deliverable.setProject(deliverableDb.getProject());
        project.setProjectInfo(deliverableDb.getProject().getProjecInfoPhase(this.getActualPhase()));
        project.setProjectLocations(deliverableDb.getProject().getProjectLocations());


        if (deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() == null) {
          deliverable.getDeliverableInfo(this.getActualPhase())
            .setNewExpectedYear(deliverableDb.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear());
        }

        deliverable.setResponsiblePartner(this.responsiblePartnerAutoSave());
        deliverable.setOtherPartners(this.otherPartnersAutoSave());
        if (deliverable.getFundingSources() != null) {
          for (DeliverableFundingSource fundingSource : deliverable.getFundingSources()) {
            if (fundingSource != null && fundingSource.getFundingSource() != null) {
              fundingSource
                .setFundingSource(fundingSourceManager.getFundingSourceById(fundingSource.getFundingSource().getId()));
              fundingSource.getFundingSource().getFundingSourceInfo(this.getActualPhase());
            }

          }
        }

        if (deliverable.getCrps() != null) {
          for (DeliverableCrp deliverableCrp : deliverable.getCrps()) {
            if (deliverableCrp != null) {
              if (deliverableCrp.getCrpProgram() == null || deliverableCrp.getCrpProgram().getId() == null
                || deliverableCrp.getCrpProgram().getId().intValue() == -1) {
                if (deliverableCrp.getGlobalUnit() != null && deliverableCrp.getGlobalUnit().getId() != null
                  && deliverableCrp.getGlobalUnit().getId().intValue() != -1) {
                  deliverableCrp.setGlobalUnit(crpManager.getGlobalUnitById(deliverableCrp.getGlobalUnit().getId()));
                }
              } else {
                deliverableCrp
                  .setCrpProgram(crpProgramManager.getCrpProgramById(deliverableCrp.getCrpProgram().getId()));
              }
            }
          }
        }
        if (deliverable.getQualityCheck() != null) {
          if (deliverable.getQualityCheck().getFileAssurance() != null) {
            if (deliverable.getQualityCheck().getFileAssurance().getId() != null) {
              FileDB db;
              // Set FileDB to null if an error occur (-1 id)
              try {
                db = fileDBManager.getFileDBById(deliverable.getQualityCheck().getFileAssurance().getId());
              } catch (IllegalArgumentException e) {
                db = null;
              }
              deliverable.getQualityCheck().setFileAssurance(db);
            }
          }

          if (deliverable.getQualityCheck().getFileDictionary() != null) {
            if (deliverable.getQualityCheck().getFileDictionary().getId() != null) {
              FileDB db;
              // Set FileDB to null if an error occur (-1 id)
              try {
                db = fileDBManager.getFileDBById(deliverable.getQualityCheck().getFileDictionary().getId());
              } catch (IllegalArgumentException e) {
                db = null;
              }
              deliverable.getQualityCheck().setFileDictionary(db);
            }
          }
        }
        if (deliverable.getDissemination() != null && deliverable.getDissemination().getType() != null) {
          String type = deliverable.getDissemination().getType();
          if (type != null) {
            switch (type) {
              case "intellectualProperty":
                deliverable.getDissemination().setIntellectualProperty(true);
                break;
              case "limitedExclusivity":
                deliverable.getDissemination().setLimitedExclusivity(true);
                break;
              case "restrictedUseAgreement":
                deliverable.getDissemination().setRestrictedUseAgreement(true);
                break;
              case "effectiveDateRestriction":
                deliverable.getDissemination().setEffectiveDateRestriction(true);
                break;
              case "notDisseminated":
                deliverable.getDissemination().setNotDisseminated(true);
              default:
                break;
            }
          }
        }

        if (deliverable.getDeliverableParticipant() != null) {
          DeliverableParticipant deliverableParticipant = deliverable.getDeliverableParticipant();
          if (deliverableParticipant.getParticipantLocationsIsosText() != null) {
            String[] locationsIsos =
              deliverableParticipant.getParticipantLocationsIsosText().replace("[", "").replace("]", "").split(",");
            List<String> locations = new ArrayList<>();
            for (String value : Arrays.asList(locationsIsos)) {
              locations.add(value.trim());
            }
            deliverableParticipant.setParticipantLocationsIsos(locations);
          }
        }

        this.setDraft(true);
      } else {
        deliverable.getDeliverableInfo(this.getActualPhase());
        // Get partner responsible
        List<DeliverablePartnership> deliverablePartnershipResponsibles =
          deliverablePartnershipManager.findByDeliverablePhaseAndType(deliverable.getId(),
            this.getActualPhase().getId(), DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue());
        if (deliverablePartnershipResponsibles != null && deliverablePartnershipResponsibles.size() > 0) {
          if (deliverablePartnershipResponsibles.size() > 1) {
            logger.warn("There are more than 1 deliverable responsibles for D" + deliverable.getId() + " "
              + this.getActualPhase().toString());
          }
          deliverable.setResponsiblePartner(deliverablePartnershipResponsibles.get(0));
        }

        // Get other partners
        List<DeliverablePartnership> deliverablePartnershipOthers =
          deliverablePartnershipManager.findByDeliverablePhaseAndType(deliverable.getId(),
            this.getActualPhase().getId(), DeliverablePartnershipTypeEnum.OTHER.getValue());
        if (deliverablePartnershipOthers != null && deliverablePartnershipOthers.size() > 0) {
          deliverable.setOtherPartners(deliverablePartnershipOthers);
        }

        deliverable.setFundingSources(deliverable.getDeliverableFundingSources().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList()));

        for (DeliverableFundingSource deliverableFundingSource : deliverable.getFundingSources()) {

          deliverableFundingSource.setFundingSource(
            fundingSourceManager.getFundingSourceById(deliverableFundingSource.getFundingSource().getId()));
          deliverableFundingSource.getFundingSource().setFundingSourceInfo(
            deliverableFundingSource.getFundingSource().getFundingSourceInfo(this.getActualPhase()));
          if (deliverableFundingSource.getFundingSource().getFundingSourceInfo() == null) {
            deliverableFundingSource.getFundingSource().setFundingSourceInfo(
              deliverableFundingSource.getFundingSource().getFundingSourceInfoLast(this.getActualPhase()));
          }
        }
        deliverable.setGenderLevels(deliverable.getDeliverableGenderLevels().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));


        if (this.isReportingActive()) {

          DeliverableQualityCheck deliverableQualityCheck = deliverableQualityCheckManager
            .getDeliverableQualityCheckByDeliverable(deliverable.getId(), this.getActualPhase().getId());
          deliverable.setQualityCheck(deliverableQualityCheck);

          if (deliverable.getDeliverableMetadataElements() != null) {
            deliverable.setMetadataElements(new ArrayList<>(deliverable.getDeliverableMetadataElements().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
          }

          if (deliverable.getDeliverableDisseminations() != null) {
            deliverable.setDisseminations(new ArrayList<>(deliverable.getDeliverableDisseminations().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
            if (deliverable.getDisseminations().size() > 0) {
              deliverable.setDissemination(deliverable.getDisseminations().get(0));
            } else {
              deliverable.setDissemination(new DeliverableDissemination());
            }
          }

          if (deliverable.getDeliverableDataSharingFiles() != null) {
            deliverable.setDataSharingFiles(new ArrayList<>(deliverable.getDeliverableDataSharingFiles().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
          }

          if (deliverable.getDeliverablePublicationMetadatas() != null) {
            deliverable
              .setPublicationMetadatas(new ArrayList<>(deliverable.getDeliverablePublicationMetadatas().stream()
                .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
          }
          if (!deliverable.getPublicationMetadatas().isEmpty()) {
            deliverable.setPublication(deliverable.getPublicationMetadatas().get(0));
          }

          if (deliverable.getDeliverableDataSharings() != null) {
            deliverable.setDataSharing(new ArrayList<>(deliverable.getDeliverableDataSharings().stream()
              .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
          }


          deliverable.setUsers(deliverable.getDeliverableUsers().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
          deliverable.setCrps(deliverable.getDeliverableCrps().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
          deliverable.setFiles(new ArrayList<>());
          for (DeliverableDataSharingFile dataSharingFile : deliverable.getDeliverableDataSharingFiles().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {

            DeliverableFile deFile = new DeliverableFile();
            switch (dataSharingFile.getTypeId().toString()) {
              case APConstants.DELIVERABLE_FILE_LOCALLY_HOSTED:
                deFile.setHosted(APConstants.DELIVERABLE_FILE_LOCALLY_HOSTED_STR);
                deFile.setName(dataSharingFile.getFile().getFileName());
                break;

              case APConstants.DELIVERABLE_FILE_EXTERNALLY_HOSTED:
                deFile.setHosted(APConstants.DELIVERABLE_FILE_EXTERNALLY_HOSTED_STR);
                deFile.setName(dataSharingFile.getExternalFile());
                break;
            }
            deFile.setId(dataSharingFile.getId());
            deFile.setSize(0);
            deliverable.getFiles().add(deFile);
          }

          if (deliverable.getDeliverableIntellectualAssets() != null) {
            List<DeliverableIntellectualAsset> intellectualAssets =
              deliverable.getDeliverableIntellectualAssets().stream()
                .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());

            if (intellectualAssets.size() > 0) {
              DeliverableIntellectualAsset asset = deliverableIntellectualAssetManager
                .getDeliverableIntellectualAssetById(intellectualAssets.get(0).getId());
              deliverable.setIntellectualAsset(deliverableIntellectualAssetManager
                .getDeliverableIntellectualAssetById(intellectualAssets.get(0).getId()));
              if (this.transaction != null && !this.transaction.equals("-1")) {
                if (deliverable.getIntellectualAsset().getFillingType() != null
                  && deliverable.getIntellectualAsset().getFillingType().getId() != null) {
                  deliverable.getIntellectualAsset().setFillingType(repIndFillingTypeManager
                    .getRepIndFillingTypeById(deliverable.getIntellectualAsset().getFillingType().getId()));
                }
                if (deliverable.getIntellectualAsset().getPatentStatus() != null
                  && deliverable.getIntellectualAsset().getPatentStatus().getId() != null) {
                  deliverable.getIntellectualAsset().setPatentStatus(repIndPatentStatusManager
                    .getRepIndPatentStatusById(deliverable.getIntellectualAsset().getPatentStatus().getId()));
                }
                if (deliverable.getIntellectualAsset().getCountry() != null
                  && deliverable.getIntellectualAsset().getCountry().getId() != null) {
                  deliverable.getIntellectualAsset().setCountry(
                    locElementManager.getLocElementById(deliverable.getIntellectualAsset().getCountry().getId()));
                }
              }
            } else {
              deliverable.setIntellectualAsset(new DeliverableIntellectualAsset());
            }
          }
          if (deliverable.getDeliverableParticipants() != null) {
            List<DeliverableParticipant> deliverableParticipants = deliverable.getDeliverableParticipants().stream()
              .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());

            if (deliverableParticipants.size() > 0) {
              DeliverableParticipant pasdl =
                deliverableParticipantManager.getDeliverableParticipantById(deliverableParticipants.get(0).getId());
              deliverable.setDeliverableParticipant(
                deliverableParticipantManager.getDeliverableParticipantById(deliverableParticipants.get(0).getId()));
              // Participants Locations
              if (deliverable.getDeliverableParticipant().getDeliverableParticipantLocations() == null) {
                deliverable.getDeliverableParticipant().setParticipantLocations(new ArrayList<>());
              } else {
                List<DeliverableParticipantLocation> locations =
                  deliverable.getDeliverableParticipant().getDeliverableParticipantLocations().stream()
                    .filter(pl -> pl.isActive()).collect(Collectors.toList());
                deliverable.getDeliverableParticipant().setParticipantLocations(locations);

              }
              if (deliverable.getDeliverableParticipant().getParticipantLocations() != null) {
                for (DeliverableParticipantLocation location : deliverable.getDeliverableParticipant()
                  .getParticipantLocations()) {
                  deliverable.getDeliverableParticipant().getParticipantLocationsIsos()
                    .add(location.getLocElement().getIsoAlpha2());
                }
              }
              if (this.transaction != null && !this.transaction.equals("-1")) {
                if (deliverable.getDeliverableParticipant().getRepIndTypeActivity() != null
                  && deliverable.getDeliverableParticipant().getRepIndTypeActivity().getId() != null) {
                  deliverable.getDeliverableParticipant()
                    .setRepIndTypeActivity(repIndTypeActivityManager.getRepIndTypeActivityById(
                      deliverable.getDeliverableParticipant().getRepIndTypeActivity().getId()));
                }
                if (deliverable.getDeliverableParticipant().getRepIndTypeParticipant() != null
                  && deliverable.getDeliverableParticipant().getRepIndTypeParticipant().getId() != null) {
                  deliverable.getDeliverableParticipant()
                    .setRepIndTypeParticipant(repIndTypeParticipantManager.getRepIndTypeParticipantById(
                      deliverable.getDeliverableParticipant().getRepIndTypeParticipant().getId()));
                }
                if (deliverable.getDeliverableParticipant().getRepIndGeographicScope() != null
                  && deliverable.getDeliverableParticipant().getRepIndGeographicScope().getId() != null) {
                  deliverable.getDeliverableParticipant()
                    .setRepIndGeographicScope(repIndGeographicScopeManager.getRepIndGeographicScopeById(
                      deliverable.getDeliverableParticipant().getRepIndGeographicScope().getId()));
                }
              }
            } else {
              deliverable.setDeliverableParticipant(new DeliverableParticipant());
            }
          }

        }

        this.setDraft(false);
      }

      if (deliverable.getGenderLevels() != null) {
        for (DeliverableGenderLevel deliverableGenderLevel : deliverable.getGenderLevels()) {
          try {
            GenderType type = genderTypeManager.getGenderTypeById(deliverableGenderLevel.getGenderLevel());
            if (type != null) {
              deliverableGenderLevel.setNameGenderLevel(type.getDescription());
              deliverableGenderLevel.setDescriptionGenderLevel(type.getCompleteDescription());
            }
          } catch (Exception e) {
            logger.error("unable to update DeliverableGenderLevel", e);
          }
        }
      }

      genderLevels = new ArrayList<>();
      List<GenderType> genderTypes = null;
      if (this.hasSpecificities(APConstants.CRP_CUSTOM_GENDER)) {
        genderTypes = genderTypeManager.findAll().stream()
          .filter(
            c -> c.isActive() && c.getCrp() != null && c.getCrp().getId().longValue() == loggedCrp.getId().longValue())
          .collect(Collectors.toList());
      } else {
        genderTypes = genderTypeManager.findAll().stream().filter(c -> c.isActive() && c.getCrp() == null)
          .collect(Collectors.toList());
      }

      for (GenderType projectStatusEnum : genderTypes) {
        genderLevels.add(projectStatusEnum);
      }

      status = new HashMap<>();
      List<ProjectStatusEnum> list = Arrays.asList(ProjectStatusEnum.values());
      for (ProjectStatusEnum projectStatusEnum : list) {

        status.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
      }
      if (this.isPlanningActive()) {
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null) {
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() != Integer
            .parseInt(ProjectStatusEnum.Complete.getStatusId())) {
            status.remove(ProjectStatusEnum.Complete.getStatusId());
          }

        }


        if (this.isDeliverableNew(deliverableID)) {
          // NEW Deliverable
          status.remove(ProjectStatusEnum.Cancelled.getStatusId());
          status.remove(ProjectStatusEnum.Extended.getStatusId());
          status.remove(ProjectStatusEnum.Complete.getStatusId());
        } else {
          // OLD Deliverable
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getYear() >= this.getActualPhase().getYear()) {

            status.remove(ProjectStatusEnum.Extended.getStatusId());

          }
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null) {
            if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
              status.remove(ProjectStatusEnum.Ongoing.getStatusId());
            }
          }
        }
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null) {
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
            .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            status.remove(ProjectStatusEnum.Complete.getStatusId());
          }
        }
      }

      crps = new ArrayList<GlobalUnit>();
      for (GlobalUnit crp : crpManager.findAll().stream()
        .filter(c -> c.getId() != this.getLoggedCrp().getId() && c.isActive()).collect(Collectors.toList())) {
        crps.add(crp);
      }
      crps.sort((c1, c2) -> c1.getComposedName().compareTo(c2.getComposedName()));

      programs = new ArrayList<CrpProgram>();
      for (CrpProgram program : crpProgramManager.findAll().stream().filter(c -> c.isActive()
        && c.getCrp().equals(this.loggedCrp) && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList())) {
        programs.add(program);
      }
      programs.sort((f1, f2) -> f1.getAcronym().compareTo(f2.getAcronym()));

      deliverableTypeParent = new ArrayList<>(
        deliverableTypeManager.findAll().stream().filter(dt -> dt.isActive() && dt.getDeliverableCategory() == null
          && dt.getCrp() == null && !dt.getAdminType().booleanValue()).collect(Collectors.toList()));

      deliverableTypeParent.addAll(new ArrayList<>(deliverableTypeManager.findAll().stream()
        .filter(dt -> dt.isActive() && dt.getDeliverableCategory() == null && dt.getCrp() != null
          && dt.getCrp().getId().longValue() == loggedCrp.getId().longValue() && !dt.getAdminType().booleanValue())
        .collect(Collectors.toList())));

      if (project.getProjecInfoPhase(this.getActualPhase()).getAdministrative() != null
        && project.getProjecInfoPhase(this.getActualPhase()).getAdministrative().booleanValue()) {

        deliverableTypeParent
          .addAll(deliverableTypeManager.findAll().stream()
            .filter(dt -> dt.isActive() && dt.getDeliverableCategory() == null && dt.getCrp() == null
              && dt.getAdminType().booleanValue() && !has_specific_management_deliverables)
            .collect(Collectors.toList()));

        deliverableTypeParent.addAll(new ArrayList<>(deliverableTypeManager.findAll().stream()
          .filter(dt -> dt.isActive() && dt.getDeliverableCategory() == null && dt.getCrp() != null
            && dt.getCrp().getId().longValue() == loggedCrp.getId().longValue() && dt.getAdminType().booleanValue())
          .collect(Collectors.toList())));
      }
      if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType() != null
        && deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() != null
        && deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId().longValue() != -1) {
        DeliverableType typeDB = deliverableTypeManager
          .getDeliverableTypeById(deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId());
        deliverable.getDeliverableInfo(this.getActualPhase()).setDeliverableType(typeDB);
        Long deliverableTypeParentId =
          deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getDeliverableCategory().getId();

        deliverableSubTypes = new ArrayList<>(
          deliverableTypeManager.findAll().stream().filter(dt -> dt.isActive() && dt.getDeliverableCategory() != null
            && dt.getDeliverableCategory().getId() == deliverableTypeParentId).collect(Collectors.toList()));
      }

      if (project.getProjectOutcomes() != null) {
        keyOutputs = new ArrayList<>();

        for (ProjectOutcome projectOutcome : project.getProjectOutcomes().stream()
          .filter(ca -> ca.isActive() && ca.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {

          for (CrpClusterKeyOutputOutcome keyOutcome : projectOutcome.getCrpProgramOutcome()
            .getCrpClusterKeyOutputOutcomes().stream().filter(ko -> ko.isActive()).collect(Collectors.toList())) {

            if (keyOutcome.getCrpClusterKeyOutput().getCrpClusterOfActivity().getPhase()
              .equals(this.getActualPhase())) {
              if (!keyOutputs.contains(keyOutcome.getCrpClusterKeyOutput())) {
                keyOutputs.add(keyOutcome.getCrpClusterKeyOutput());
              }
            }

          }

        }
      }
      keyOutputs.sort((k1, k2) -> k1.getCrpClusterOfActivity().getIdentifier()
        .compareTo(k2.getCrpClusterOfActivity().getIdentifier()));

      partners = new ArrayList<>();


      /**
       * The way to use the list of partners is changed to avoid double call to the database and use the objects in
       * memory.
       * jurodca
       * 20180129
       */

      List<ProjectPartner> partnersTmp = projectPartnerManager.findAll().stream()
        .filter(
          pp -> pp.isActive() && pp.getProject().getId() == projectID && pp.getPhase().equals(this.getActualPhase()))
        .collect(Collectors.toList());

      for (ProjectPartner partner : partnersTmp) {
        List<ProjectPartnerPerson> persons =
          partner.getProjectPartnerPersons().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        if (!isManagingPartnerPersonRequerid) {
          partners.add(partner);
        } else {
          if (!persons.isEmpty()) {
            partners.add(partner);
          }
        }
      }

      partnerPersons = new ArrayList<>();
      /**
       * This for is not being used properly. The internal logic is repeated for each partner that has been consulted.
       * The partner object of the cycle is not being used within it.
       * jurodca
       * 20180129
       */

      partnerPersons =
        partners.stream().flatMap(e -> e.getProjectPartnerPersons().stream()).collect(Collectors.toList());

      this.fundingSources = new ArrayList<>();

      for (ProjectBudget budget : project.getProjectBudgets().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {

        FundingSource fundingSource = budget.getFundingSource();
        fundingSource.setFundingSourceInfo(fundingSource.getFundingSourceInfo(this.getActualPhase()));


        if (fundingSource.getFundingSourceInfo() == null) {
          fundingSource.setFundingSourceInfo(fundingSource.getFundingSourceInfoLast(this.getActualPhase()));

        }
        if (fundingSource.getFundingSourceInfo() != null) {
          this.fundingSources.add(fundingSource);
        }
      }
      Set<FundingSource> hs = new HashSet<FundingSource>();
      hs.addAll(this.fundingSources);
      this.fundingSources.clear();
      this.fundingSources.addAll(hs);
      fundingSources.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));


      String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
      this.setBasePermission(this.getText(Permission.PROJECT_DELIVERABLE_BASE_PERMISSION, params));

      // Read all the cross cutting scoring from database
      this.crossCuttingDimensions = this.crossCuttingManager.findAll();

      // load the map of cross cutting scores
      this.crossCuttingScoresMap = new HashMap<>();
      for (CrossCuttingScoring score : this.crossCuttingDimensions) {
        this.crossCuttingScoresMap.put(score.getId(), score.getDescription());
      }

      // only show cross cutting number 1 and 2

      List<CrossCuttingScoring> crossCuttingDimensionsTemp = this.crossCuttingDimensions;
      this.crossCuttingDimensions = new ArrayList<>();

      for (CrossCuttingScoring score : crossCuttingDimensionsTemp) {
        if (score.getId() != 0) {
          this.crossCuttingDimensions.add(score);
        }
      }
      if (this.isReportingActive()) {
        if (metadataElementManager.findAll() != null) {
          deliverable.setMetadata(new ArrayList<>(metadataElementManager.findAll()));
        }
        answers = new ArrayList<>(
          deliverableQualityAnswerManager.findAll().stream().filter(qa -> qa.isActive()).collect(Collectors.toList()));
        repositoryChannels = repositoryChannelManager.findAll();
        if (repositoryChannels != null && repositoryChannels.size() > 0) {
          repositoryChannels.sort((rc1, rc2) -> rc1.getShortName().compareTo(rc2.getShortName()));
        } else {
          repositoryChannels = new LinkedList<RepositoryChannel>();
        }

        if (deliverable.getFiles() != null) {
          deliverable.getFiles().sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
        }

        this.setRepIndTypeActivities(repIndTypeActivityManager.findAll().stream()
          .sorted((t1, t2) -> t1.getName().compareTo(t2.getName())).collect(Collectors.toList()));
        this.setRepIndTypeParticipants(repIndTypeParticipantManager.findAll().stream()
          .sorted((t1, t2) -> t1.getName().compareTo(t2.getName())).collect(Collectors.toList()));
        this.setRepIndGeographicScopes(repIndGeographicScopeManager.findAll().stream()
          .sorted((g1, g2) -> g1.getName().compareTo(g2.getName())).collect(Collectors.toList()));
        this.setRepIndRegions(repIndRegionManager.findAll().stream()
          .sorted((r1, r2) -> r1.getName().compareTo(r2.getName())).collect(Collectors.toList()));
        this.setCountries(locElementManager.findAll().stream()
          .filter(c -> c.isActive() && c.getLocElementType().getId() == 2).collect(Collectors.toList()));
        this.setRepIndFillingTypes(repIndFillingTypeManager.findAll().stream()
          .sorted((r1, r2) -> r1.getName().compareTo(r2.getName())).collect(Collectors.toList()));
        this.setRepIndPatentStatuses(repIndPatentStatusManager.findAll().stream()
          .sorted((r1, r2) -> r1.getName().compareTo(r2.getName())).collect(Collectors.toList()));
        // Statuses
        statuses = new HashMap<>();
        List<ProjectStatusEnum> statusEnumList = Arrays.asList(ProjectStatusEnum.values());
        for (ProjectStatusEnum projectStatusEnum : statusEnumList) {
          statuses.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
        }
      }

      if (this.isHttpPost()) {
        if (deliverableTypeParent != null) {
          deliverableTypeParent.clear();
        }

        if (deliverable.getPublication() != null) {
          deliverable.getPublication().setIsiPublication(null);
          deliverable.getPublication().setCoAuthor(null);
          deliverable.getPublication().setNasr(null);
        }

        deliverable.getDeliverableInfo(this.getActualPhase()).setDeliverableType(null);
        deliverable.getDeliverableInfo(this.getActualPhase()).setCrossCuttingGender(null);
        deliverable.getDeliverableInfo(this.getActualPhase()).setCrossCuttingCapacity(null);
        deliverable.getDeliverableInfo(this.getActualPhase()).setCrossCuttingNa(null);
        deliverable.getDeliverableInfo(this.getActualPhase()).setCrossCuttingYouth(null);
        deliverable.getDeliverableInfo(this.getActualPhase()).setLicense(null);
        deliverable.setResponsiblePartner(null);
        if (deliverable.getCrps() != null) {
          deliverable.getCrps().clear();
        }

        if (deliverable.getUsers() != null) {
          deliverable.getUsers().clear();
        }

        if (deliverable.getMetadataElements() != null) {
          deliverable.getMetadataElements().clear();
        }
        if (projectOutcome != null) {
          projectOutcome.clear();
        }

        if (deliverable.getResponsiblePartner() != null) {
          deliverable.setResponsiblePartner(null);
        }

        if (deliverable.getOtherPartners() != null) {
          deliverable.getOtherPartners().clear();
        }
        if (deliverable.getFundingSources() != null) {
          deliverable.getFundingSources().clear();
        }
        if (deliverable.getGenderLevels() != null) {
          deliverable.getGenderLevels().clear();
        }

        deliverable.setQualityCheck(null);
        deliverable.getDeliverableInfo(this.getActualPhase()).setCrpClusterKeyOutput(null);

        if (deliverable.getDisseminations() != null) {
          deliverable.getDisseminations().clear();
        }
        if (deliverable.getDeliverableParticipant() != null) {
          deliverable.getDeliverableParticipant().setRepIndGeographicScope(null);
          deliverable.getDeliverableParticipant().setRepIndRegion(null);
          deliverable.getDeliverableParticipant().setRepIndTypeActivity(null);
          deliverable.getDeliverableParticipant().setRepIndTypeParticipant(null);
        }
        if (deliverable.getIntellectualAsset() != null) {
          deliverable.getIntellectualAsset().setFillingType(null);
          deliverable.getIntellectualAsset().setPatentStatus(null);
        }
      }

      try {
        indexTab = Integer.parseInt(this.getSession().get("indexTab").toString());
        this.getSession().remove("indexTab");
      } catch (Exception e) {
        indexTab = 0;
      }
    }

  }

  public void removeOthersDeliverablePartnerships(Deliverable deliverablePrew) {
    if (deliverablePrew.getDeliverablePartnerships() != null
      && deliverablePrew.getDeliverablePartnerships().size() > 0) {
      List<DeliverablePartnership> partnerShipsOtherPrew = deliverablePrew.getDeliverablePartnerships().stream()
        .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(this.getActualPhase())
          && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.OTHER.getValue())
          && (dp.getProjectPartnerPerson() == null || dp.getProjectPartnerPerson().isActive()))
        .collect(Collectors.toList());

      if (deliverable.getOtherPartners() == null) {
        deliverable.setOtherPartners(new ArrayList<>());
      }
      for (DeliverablePartnership deliverablePartnership : deliverable.getOtherPartners()) {
        if (deliverablePartnership != null && deliverablePartnership.getId() != null) {
          if (deliverablePartnership.getProjectPartner() == null) {
            deliverablePartnershipManager.deleteDeliverablePartnership(deliverablePartnership.getId());
          } else {
            if (isManagingPartnerPersonRequerid && deliverablePartnership.getProjectPartnerPerson() == null) {
              deliverablePartnershipManager.deleteDeliverablePartnership(deliverablePartnership.getId());
            }
          }
        }
      }
      for (DeliverablePartnership deliverablePartnership : partnerShipsOtherPrew) {
        if (deliverable.getOtherPartners() != null) {
          if (!deliverable.getOtherPartners().contains(deliverablePartnership)) {
            deliverablePartnershipManager.deleteDeliverablePartnership(deliverablePartnership.getId());
          }
        }
      }
    }
  }


  private DeliverablePartnership responsiblePartnerAutoSave() {
    try {
      PartnerDivision partnerDivision = null;
      if (deliverable.getResponsiblePartner().getPartnerDivision() != null) {
        partnerDivision = partnerDivisionManager
          .getPartnerDivisionById(deliverable.getResponsiblePartner().getPartnerDivision().getId());
      }
      DeliverablePartnership partnership = new DeliverablePartnership();


      Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
      try {
        // Get partner responsible and institution
        List<DeliverablePartnership> deliverablePartnershipResponsibles =
          deliverablePartnershipManager.findByDeliverablePhaseAndType(deliverable.getId(),
            this.getActualPhase().getId(), DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue());
        if (deliverablePartnershipResponsibles != null && deliverablePartnershipResponsibles.size() > 0) {
          if (deliverablePartnershipResponsibles.size() > 1) {
            logger.warn("There are more than 1 deliverable responsibles for D" + deliverableID + " "
              + this.getActualPhase().toString());
          }
          DeliverablePartnership partnershipDB = deliverablePartnershipResponsibles.get(0);
          if (partnershipDB != null) {
            partnership.setId(partnershipDB.getId());
          }
        }
      } catch (Exception e) {
        partnership.setId(null);
      }

      partnership.setDeliverable(deliverable);
      if (deliverable.getResponsiblePartner().getProjectPartnerPerson() != null
        && deliverable.getResponsiblePartner().getProjectPartnerPerson().getId() != null) {
        ProjectPartnerPerson partnerPerson = projectPartnerPersonManager
          .getProjectPartnerPersonById(deliverable.getResponsiblePartner().getProjectPartnerPerson().getId());
        partnership.setProjectPartnerPerson(partnerPerson);
      }
      partnership.setProjectPartner(
        projectPartnerManager.getProjectPartnerById(deliverable.getResponsiblePartner().getProjectPartner().getId()));

      partnership.setPartnerDivision(partnerDivision);
      partnership.setPartnerType(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue());

      return partnership;
    } catch (Exception e) {
      return null;
    }

  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {
      this.getSession().put("indexTab", indexTab);
      // we update the mofification Justification only here.
      this.saveProjectAuditData();

      Deliverable deliverableManagedState = this.updateDeliverableInfo();
      this.updateDeliverableFundingSources(deliverableManagedState);

      // This gets a DeliverablePartnership responsible entity in managed state.
      DeliverablePartnership partnershipResponsibleManaged = deliverable.getResponsiblePartner();
      Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
      // gets delivetablePartnership responsible from database
      DeliverablePartnership partnershipResponsibleDB = this.getDeliverablePartnershipResponsibleDB(deliverableDB);
      this.saveUpdateDeliverablePartnershipResponsible(partnershipResponsibleDB, partnershipResponsibleManaged);

      this.removeOthersDeliverablePartnerships(deliverableManagedState);
      this.updateOtherDeliverablePartnerships();

      this.saveDeliverableGenderLevels(deliverableManagedState);
      this.deleteDeliverableGenderLevels(deliverableManagedState);

      if (this.isReportingActive()) {
        if (deliverable.getQualityCheck() != null) {
          this.saveQualityCheck();
        }
        this.saveDissemination();
        this.saveMetadata();
        this.saveCrps();
        this.savePublicationMetadata();
        this.saveDataSharing();
        this.saveUsers();
        this.saveIntellectualAsset();
        this.saveParticipant();
      }

      deliverableManagedState.getDeliverableInfo(this.getActualPhase())
        .setModificationJustification(this.getJustification());
      deliverableInfoManager.saveDeliverableInfo(deliverableManagedState.getDeliverableInfo(this.getActualPhase()));
      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_DELIVERABLE_PARTNERSHIPS_RELATION);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_INFO);

      relationsName.add(APConstants.PROJECT_DELIVERABLE_FUNDING_RELATION);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_GENDER_LEVELS);
      if (this.isReportingActive()) {
        relationsName.add(APConstants.PROJECT_DELIVERABLE_QUALITY_CHECK);
        relationsName.add(APConstants.PROJECT_DELIVERABLE_METADATA_ELEMENT);
        relationsName.add(APConstants.PROJECT_DELIVERABLE_DATA_SHARING_FILES);
        relationsName.add(APConstants.PROJECT_DELIVERABLE_PUBLICATION_METADATA);
        relationsName.add(APConstants.PROJECT_DELIVERABLE_DISEMINATIONS);
        relationsName.add(APConstants.PROJECT_DELIVERABLE_CRPS);
        relationsName.add(APConstants.PROJECT_DELIVERABLE_USERS);
        relationsName.add(APConstants.PROJECT_DELIVERABLES_INTELLECTUAL_RELATION);
        relationsName.add(APConstants.PROJECT_DELIVERABLES_PARTICIPANT_RELATION);
        relationsName.add(APConstants.PROJECT_DELIVERABLES_PARTICIPANT_LOCATION_RELATION);
      }
      /**
       * The following is required because we need to update something on the @Deliverable if we want a row created in
       * the auditlog table.
       */
      this.setModificationJustification(deliverableManagedState);
      deliverableManagedState = deliverableManager.saveDeliverable(deliverableManagedState, this.getActionName(),
        relationsName, this.getActualPhase());
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }

        } else {
          this.addActionMessage("message:" + this.getText("saving.saved"));
        }
        return SUCCESS;
      } else {
        this.addActionMessage("");
        this.setActionMessages(null);
        return REDIRECT;
      }

    } else {
      return NOT_AUTHORIZED;
    }

  }


  public void saveCrps() {
    if (deliverable.getCrps() == null) {
      deliverable.setCrps(new ArrayList<>());
    }
    /* Delete */
    Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
    for (DeliverableCrp deliverableCrp : deliverableDB.getDeliverableCrps().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
      if (!deliverable.getCrps().contains(deliverableCrp)) {
        deliverableCrpManager.deleteDeliverableCrp(deliverableCrp.getId());
      }
    }

    /* Save */
    for (DeliverableCrp deliverableCrp : deliverable.getCrps()) {
      if (deliverableCrp.getId() == null || deliverableCrp.getId().intValue() == -1) {
        deliverableCrp.setId(null);
        deliverableCrp.setDeliverable(deliverable);
        deliverableCrp.setPhase(this.getActualPhase());
        if (deliverableCrp.getGlobalUnit() != null && deliverableCrp.getGlobalUnit().getId() != null
          && deliverableCrp.getGlobalUnit().getId() != -1) {
          deliverableCrp.setCrpProgram(null);
        } else {
          deliverableCrp.setGlobalUnit(null);
        }
        deliverableCrpManager.saveDeliverableCrp(deliverableCrp);
      }
    }
  }

  public void saveDataSharing() {
    if (deliverable.getFiles() == null) {
      deliverable.setFiles(new ArrayList<>());

    }
    List<DeliverableFile> filesPrev = new ArrayList<>();
    Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);


    for (DeliverableDataSharingFile dataSharingFile : deliverableDB.getDeliverableDataSharingFiles()) {

      DeliverableFile deFile = new DeliverableFile();
      switch (dataSharingFile.getTypeId().toString()) {
        case APConstants.DELIVERABLE_FILE_LOCALLY_HOSTED:
          deFile.setHosted(APConstants.DELIVERABLE_FILE_LOCALLY_HOSTED_STR);
          deFile.setName(dataSharingFile.getFile().getFileName());
          break;

        case APConstants.DELIVERABLE_FILE_EXTERNALLY_HOSTED:
          deFile.setHosted(APConstants.DELIVERABLE_FILE_EXTERNALLY_HOSTED_STR);
          deFile.setName(dataSharingFile.getExternalFile());
          break;
      }
      deFile.setId(dataSharingFile.getId());
      deFile.setSize(0);
      filesPrev.add(deFile);
    }

    for (DeliverableFile deliverableFile : filesPrev) {
      if (!deliverable.getFiles().contains(deliverableFile)) {
        deliverableDataSharingFileManager.deleteDeliverableDataSharingFile(deliverableFile.getId().longValue());
      }
    }

    for (DeliverableFile deliverableFile : deliverable.getFiles()) {
      if (deliverableFile.getId().longValue() == -1) {
        DeliverableDataSharingFile dataSharingFile = new DeliverableDataSharingFile();
        dataSharingFile.setDeliverable(deliverable);
        switch (deliverableFile.getHosted()) {

          case APConstants.DELIVERABLE_FILE_EXTERNALLY_HOSTED_STR:
            dataSharingFile.setTypeId(Integer.parseInt(APConstants.DELIVERABLE_FILE_EXTERNALLY_HOSTED));
            dataSharingFile.setExternalFile(deliverableFile.getName());
            break;
        }
        dataSharingFile.setPhase(this.getActualPhase());
        deliverableDataSharingFileManager.saveDeliverableDataSharingFile(dataSharingFile);
      }

    }
  }

  private void saveDeliverableGenderLevels(Deliverable deliverablePrew) {
    if (deliverable.getGenderLevels() != null) {
      if (deliverablePrew.getDeliverableGenderLevels() != null
        && deliverablePrew.getDeliverableGenderLevels().size() > 0) {
        List<DeliverableGenderLevel> genderLevelsPrew = deliverablePrew.getDeliverableGenderLevels().stream()
          .filter(dp -> dp.isActive() && dp.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());

        for (DeliverableGenderLevel deliverableGenderLevel : genderLevelsPrew) {
          if (!deliverable.getGenderLevels().contains(deliverableGenderLevel)) {
            deliverableGenderLevelManager.deleteDeliverableGenderLevel(deliverableGenderLevel.getId());
          }
        }
      }

      for (DeliverableGenderLevel deliverableGenderLevel : deliverable.getGenderLevels()) {
        if (deliverableGenderLevel.getId() == null || deliverableGenderLevel.getId() == -1) {

          deliverableGenderLevel.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
          deliverableGenderLevel.setPhase(this.getActualPhase());
          deliverableGenderLevelManager.saveDeliverableGenderLevel(deliverableGenderLevel);

        } else {
          DeliverableGenderLevel deliverableGenderLevelDB =
            deliverableGenderLevelManager.getDeliverableGenderLevelById(deliverableGenderLevel.getId());
          deliverableGenderLevelDB.setGenderLevel(deliverableGenderLevel.getGenderLevel());
          deliverableGenderLevelManager.saveDeliverableGenderLevel(deliverableGenderLevelDB);
        }
      }
    }
  }

  public void saveDissemination() {
    if (deliverable.getDissemination() != null) {

      DeliverableDissemination dissemination = new DeliverableDissemination();
      if (deliverable.getDissemination().getId() != null && deliverable.getDissemination().getId() != -1) {
        dissemination =
          deliverableDisseminationManager.getDeliverableDisseminationById(deliverable.getDissemination().getId());
      } else {
        dissemination = new DeliverableDissemination();
        dissemination.setDeliverable(deliverableManager.getDeliverableById(deliverableID));

      }

      dissemination.setSynced(deliverable.getDissemination().getSynced());

      if (deliverable.getDissemination().getIsOpenAccess() != null) {
        dissemination.setIsOpenAccess(deliverable.getDissemination().getIsOpenAccess());
        if (!deliverable.getDissemination().getIsOpenAccess().booleanValue()) {
          String type = deliverable.getDissemination().getType();
          if (type != null) {
            switch (type) {
              case "intellectualProperty":
                dissemination.setNotDisseminated(false);
                dissemination.setIntellectualProperty(true);
                dissemination.setLimitedExclusivity(false);
                dissemination.setRestrictedUseAgreement(false);
                dissemination.setEffectiveDateRestriction(false);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(null);


                break;
              case "limitedExclusivity":
                dissemination.setNotDisseminated(false);
                dissemination.setIntellectualProperty(false);
                dissemination.setLimitedExclusivity(true);
                dissemination.setRestrictedUseAgreement(false);
                dissemination.setEffectiveDateRestriction(false);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(null);

                break;
              case "restrictedUseAgreement":
                dissemination.setNotDisseminated(false);
                dissemination.setIntellectualProperty(false);
                dissemination.setLimitedExclusivity(false);
                dissemination.setRestrictedUseAgreement(true);
                dissemination.setEffectiveDateRestriction(false);

                dissemination.setRestrictedAccessUntil(deliverable.getDissemination().getRestrictedAccessUntil());
                dissemination.setRestrictedEmbargoed(null);

                break;
              case "effectiveDateRestriction":
                dissemination.setNotDisseminated(false);
                dissemination.setIntellectualProperty(false);
                dissemination.setLimitedExclusivity(false);
                dissemination.setRestrictedUseAgreement(false);
                dissemination.setEffectiveDateRestriction(true);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(deliverable.getDissemination().getRestrictedEmbargoed());

                break;
              case "notDisseminated":

                dissemination.setNotDisseminated(true);
                dissemination.setIntellectualProperty(false);
                dissemination.setLimitedExclusivity(false);
                dissemination.setRestrictedUseAgreement(false);
                dissemination.setEffectiveDateRestriction(false);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(null);

                dissemination.setRestrictedAccessUntil(null);
                dissemination.setRestrictedEmbargoed(null);

                break;
              default:
                break;
            }
          }
        } else {

          dissemination.setNotDisseminated(false);
          dissemination.setIntellectualProperty(false);
          dissemination.setLimitedExclusivity(false);
          dissemination.setRestrictedUseAgreement(false);
          dissemination.setEffectiveDateRestriction(false);

          dissemination.setRestrictedAccessUntil(null);
          dissemination.setRestrictedEmbargoed(null);
        }
      } else {

        dissemination.setIsOpenAccess(null);
        dissemination.setNotDisseminated(false);
        dissemination.setIntellectualProperty(false);
        dissemination.setLimitedExclusivity(false);
        dissemination.setRestrictedUseAgreement(false);
        dissemination.setEffectiveDateRestriction(false);

        dissemination.setRestrictedAccessUntil(null);
        dissemination.setRestrictedEmbargoed(null);
      }

      if (deliverable.getDissemination().getAlreadyDisseminated() != null) {
        dissemination.setAlreadyDisseminated(deliverable.getDissemination().getAlreadyDisseminated());
        if (deliverable.getDissemination().getAlreadyDisseminated().booleanValue()) {

          dissemination.setDisseminationUrl(deliverable.getDissemination().getDisseminationUrl());
          dissemination.setDisseminationChannel(deliverable.getDissemination().getDisseminationChannel());
        } else {
          dissemination.setDisseminationUrl(null);
          dissemination.setDisseminationChannel(null);
        }
      } else {
        dissemination.setAlreadyDisseminated(null);
        dissemination.setDisseminationUrl(null);
        dissemination.setDisseminationChannel(null);
      }

      dissemination.setPhase(this.getActualPhase());
      deliverableDisseminationManager.saveDeliverableDissemination(dissemination);

    }
  }


  private void saveIntellectualAsset() {
    if (deliverable.getIntellectualAsset() != null && deliverable.getIntellectualAsset().getHasPatentPvp() != null) {
      DeliverableIntellectualAsset intellectualAsset = new DeliverableIntellectualAsset();

      if (deliverable.getIntellectualAsset().getId() != null && deliverable.getIntellectualAsset().getId() != -1) {
        intellectualAsset = deliverableIntellectualAssetManager
          .getDeliverableIntellectualAssetById(deliverable.getIntellectualAsset().getId());

      } else {
        intellectualAsset = new DeliverableIntellectualAsset();
        intellectualAsset.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
        intellectualAsset.setPhase(this.getActualPhase());
      }

      intellectualAsset.setHasPatentPvp(deliverable.getIntellectualAsset().getHasPatentPvp());

      if (intellectualAsset.getHasPatentPvp()) {
        intellectualAsset.setAdditionalInformation(deliverable.getIntellectualAsset().getAdditionalInformation());
        intellectualAsset.setApplicant(deliverable.getIntellectualAsset().getApplicant());
        intellectualAsset.setLink(deliverable.getIntellectualAsset().getLink());
        intellectualAsset.setPublicCommunication(deliverable.getIntellectualAsset().getPublicCommunication());
        intellectualAsset.setTitle(deliverable.getIntellectualAsset().getTitle());
        intellectualAsset.setDateFilling(deliverable.getIntellectualAsset().getDateFilling());
        intellectualAsset.setDateRegistration(deliverable.getIntellectualAsset().getDateRegistration());
        intellectualAsset.setDateExpiry(deliverable.getIntellectualAsset().getDateExpiry());
        intellectualAsset.setType(deliverable.getIntellectualAsset().getType());

        if (intellectualAsset.getType() != null) {
          if (DeliverableIntellectualAssetTypeEnum.getValue(intellectualAsset.getType())
            .equals(DeliverableIntellectualAssetTypeEnum.Patent)) {
            if (deliverable.getIntellectualAsset().getFillingType() != null
              && deliverable.getIntellectualAsset().getFillingType().getId() != -1) {
              intellectualAsset.setFillingType(deliverable.getIntellectualAsset().getFillingType());
            } else {
              intellectualAsset.setFillingType(null);
            }
            if (deliverable.getIntellectualAsset().getPatentStatus() != null
              && deliverable.getIntellectualAsset().getPatentStatus().getId() != -1) {
              intellectualAsset.setPatentStatus(deliverable.getIntellectualAsset().getPatentStatus());
            } else {
              intellectualAsset.setPatentStatus(null);
            }
            intellectualAsset.setPatentType(deliverable.getIntellectualAsset().getPatentType());
            intellectualAsset.setVarietyName(null);
            intellectualAsset.setStatus(null);
            intellectualAsset.setCountry(null);
            intellectualAsset.setAppRegNumber(null);
            intellectualAsset.setBreederCrop(null);
          } else if (DeliverableIntellectualAssetTypeEnum.getValue(intellectualAsset.getType())
            .equals(DeliverableIntellectualAssetTypeEnum.PVP)) {
            intellectualAsset.setVarietyName(deliverable.getIntellectualAsset().getVarietyName());
            if (deliverable.getIntellectualAsset().getStatus() != null
              && deliverable.getIntellectualAsset().getStatus() != -1) {
              intellectualAsset.setStatus(deliverable.getIntellectualAsset().getStatus());
            } else {
              intellectualAsset.setStatus(null);
            }
            if (deliverable.getIntellectualAsset().getCountry() != null
              && !deliverable.getIntellectualAsset().getCountry().getIsoAlpha2().equals("-1")) {
              LocElement locElement = locElementManager
                .getLocElementByISOCode(deliverable.getIntellectualAsset().getCountry().getIsoAlpha2());
              if (locElement != null) {
                intellectualAsset.setCountry(locElement);
              } else {
                intellectualAsset.setCountry(null);
              }
            } else {
              intellectualAsset.setCountry(null);
            }
            intellectualAsset.setAppRegNumber(deliverable.getIntellectualAsset().getAppRegNumber());
            intellectualAsset.setBreederCrop(deliverable.getIntellectualAsset().getBreederCrop());
            intellectualAsset.setFillingType(null);
            intellectualAsset.setPatentStatus(null);
            intellectualAsset.setPatentType(null);
          }
        } else {
          intellectualAsset.setFillingType(null);
          intellectualAsset.setPatentStatus(null);
          intellectualAsset.setPatentType(null);
          intellectualAsset.setVarietyName(null);
          intellectualAsset.setStatus(null);
          intellectualAsset.setCountry(null);
          intellectualAsset.setAppRegNumber(null);
          intellectualAsset.setBreederCrop(null);
        }
      } else {
        intellectualAsset.setAdditionalInformation(null);
        intellectualAsset.setApplicant(null);
        intellectualAsset.setLink(null);
        intellectualAsset.setPublicCommunication(null);
        intellectualAsset.setTitle(null);
        intellectualAsset.setType(null);
        intellectualAsset.setFillingType(null);
        intellectualAsset.setPatentStatus(null);
        intellectualAsset.setPatentType(null);
        intellectualAsset.setVarietyName(null);
        intellectualAsset.setStatus(null);
        intellectualAsset.setCountry(null);
        intellectualAsset.setAppRegNumber(null);
        intellectualAsset.setBreederCrop(null);
        intellectualAsset.setDateFilling(null);
        intellectualAsset.setDateRegistration(null);
        intellectualAsset.setDateExpiry(null);
      }

      deliverableIntellectualAssetManager.saveDeliverableIntellectualAsset(intellectualAsset);
    }
  }


  public void saveMetadata() {
    if (deliverable.getMetadataElements() != null) {
      for (DeliverableMetadataElement deliverableMetadataElement : deliverable.getMetadataElements()) {
        if (deliverableMetadataElement != null && deliverableMetadataElement.getMetadataElement() != null) {
          deliverableMetadataElement.setDeliverable(deliverable);
          deliverableMetadataElement.setPhase(this.getActualPhase());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElement);
        }
      }
    }
  }


  private void saveParticipant() {
    if (deliverable.getDeliverableParticipant() != null
      && deliverable.getDeliverableParticipant().getHasParticipants() != null) {
      DeliverableParticipant participant = new DeliverableParticipant();
      DeliverableParticipant deliverableParticipantDB = new DeliverableParticipant();
      List<DeliverableParticipant> deliverableParticipants = deliverableParticipantManager
        .findDeliverableParticipantByDeliverableAndPhase(deliverable.getId(), this.getActualPhase().getId());
      if (deliverableParticipants != null && deliverableParticipants.size() > 0) {
        deliverableParticipantDB = deliverableParticipants.get(0);
        if (deliverableParticipants.size() > 1) {
          logger.warn("There is more than one deliverableParticipant in database for deliverable: "
            + deliverable.getId() + ", phase: " + this.getActualPhase().getComposedName());
        }
      }
      if (deliverableParticipantDB != null && deliverableParticipantDB.getId() != null
        && deliverableParticipantDB.getId() != -1) {
        participant = deliverableParticipantManager.getDeliverableParticipantById(deliverableParticipantDB.getId());
      } else {
        participant.setId(null);
        participant.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
        participant.setPhase(this.getActualPhase());
        participant = deliverableParticipantManager.saveDeliverableParticipant(participant);
      }

      List<DeliverableParticipantLocation> locationsDB = new ArrayList<>();
      if (participant.getId() != null && participant.getId() != -1) {
        locationsDB = deliverableParticipantLocationManager.findParticipantLocationsByParticipant(participant.getId());
      }
      if (locationsDB == null) {
        locationsDB = new ArrayList<>();
      }
      participant.setHasParticipants(deliverable.getDeliverableParticipant().getHasParticipants());

      if (participant.getHasParticipants()) {
        participant.setEventActivityName(deliverable.getDeliverableParticipant().getEventActivityName());
        if (deliverable.getDeliverableParticipant().getRepIndTypeActivity() != null
          && deliverable.getDeliverableParticipant().getRepIndTypeActivity().getId() != -1) {

          participant.setRepIndTypeActivity(deliverable.getDeliverableParticipant().getRepIndTypeActivity());

          if (participant.getRepIndTypeActivity().getId().equals(this.getReportingIndTypeActivityAcademicDegree())) {
            participant.setAcademicDegree(deliverable.getDeliverableParticipant().getAcademicDegree());
          } else {
            participant.setAcademicDegree(null);
          }

        } else {
          participant.setRepIndTypeActivity(null);
          participant.setAcademicDegree(null);
        }
        participant.setParticipants(deliverable.getDeliverableParticipant().getParticipants());
        if (deliverable.getDeliverableParticipant().getEstimateParticipants() != null) {
          participant.setEstimateParticipants(deliverable.getDeliverableParticipant().getEstimateParticipants());
        } else {
          participant.setEstimateParticipants(false);
        }
        participant.setFemales(deliverable.getDeliverableParticipant().getFemales());
        if (deliverable.getDeliverableParticipant().getEstimateFemales() != null) {
          participant.setEstimateFemales(deliverable.getDeliverableParticipant().getEstimateFemales());
        } else {
          participant.setEstimateFemales(false);
        }
        if (deliverable.getDeliverableParticipant().getDontKnowFemale() != null) {
          participant.setDontKnowFemale(deliverable.getDeliverableParticipant().getDontKnowFemale());
        } else {
          participant.setDontKnowFemale(false);
        }
        if (deliverable.getDeliverableParticipant().getRepIndTypeParticipant() != null
          && deliverable.getDeliverableParticipant().getRepIndTypeParticipant().getId() != -1) {
          participant.setRepIndTypeParticipant(deliverable.getDeliverableParticipant().getRepIndTypeParticipant());
        } else {
          participant.setRepIndTypeParticipant(null);
        }

        // Save Locations
        if (deliverable.getDeliverableParticipant().getRepIndGeographicScope() != null
          && deliverable.getDeliverableParticipant().getRepIndGeographicScope().getId() != -1) {

          participant.setRepIndGeographicScope(deliverable.getDeliverableParticipant().getRepIndGeographicScope());
          RepIndGeographicScope repIndGeographicScope =
            repIndGeographicScopeManager.getRepIndGeographicScopeById(participant.getRepIndGeographicScope().getId());

          // Global
          if (repIndGeographicScope.getId().equals(this.getReportingIndGeographicScopeGlobal())) {

            participant.setRepIndRegion(null);
            this.deleteParticipantLocations(locationsDB);

          } else
          // Regional
          if (repIndGeographicScope.getId().equals(this.getReportingIndGeographicScopeRegional())) {

            if (deliverable.getDeliverableParticipant().getRepIndRegion() != null
              && deliverable.getDeliverableParticipant().getRepIndRegion().getId() != -1) {
              participant.setRepIndRegion(deliverable.getDeliverableParticipant().getRepIndRegion());
            } else {
              participant.setRepIndRegion(null);
            }
            this.deleteParticipantLocations(locationsDB);

          } else {
            // Multi-national || National || Sub-national
            // Save Locations
            List<DeliverableParticipantLocation> locationsSave = new ArrayList<>();
            if (deliverable.getDeliverableParticipant().getParticipantLocationsIsos() != null
              && !deliverable.getDeliverableParticipant().getParticipantLocationsIsos().isEmpty()) {
              participant
                .setParticipantLocationsIsos(deliverable.getDeliverableParticipant().getParticipantLocationsIsos());
              for (String locationIsoAlpha2 : participant.getParticipantLocationsIsos()) {
                DeliverableParticipantLocation locationParticipant = new DeliverableParticipantLocation();
                locationParticipant.setLocElement(locElementManager.getLocElementByISOCode(locationIsoAlpha2));
                locationParticipant.setDeliverableParticipant(participant);
                locationsSave.add(locationParticipant);
                if (!locationsDB.contains(locationParticipant)) {
                  deliverableParticipantLocationManager.saveDeliverableParticipantLocation(locationParticipant);
                }
              }
            }
            for (DeliverableParticipantLocation deliverableParticipantLocation : locationsDB) {
              if (!locationsSave.contains(deliverableParticipantLocation)) {
                deliverableParticipantLocationManager
                  .deleteDeliverableParticipantLocation(deliverableParticipantLocation.getId());
              }
            }
            participant.setRepIndRegion(null);
          }
        } else {
          participant.setRepIndGeographicScope(null);
          participant.setRepIndRegion(null);
          this.deleteParticipantLocations(locationsDB);
        }
        participant.setActive(true);
      } else {
        participant.setEventActivityName(null);
        participant.setRepIndTypeActivity(null);
        participant.setAcademicDegree(null);
        participant.setParticipants(null);
        participant.setEstimateParticipants(null);
        participant.setFemales(null);
        participant.setEstimateFemales(null);
        participant.setDontKnowFemale(null);
        participant.setRepIndTypeParticipant(null);
        participant.setRepIndGeographicScope(null);
        participant.setRepIndRegion(null);
        this.deleteParticipantLocations(locationsDB);
      }

      deliverableParticipantManager.saveDeliverableParticipant(participant);

    }
  }

  /**
   * All we are doing here is setting the modification justification.
   */
  private void saveProjectAuditData() {
    // projectDB is a managed hibernate, project is in a detached state
    Project projectDB = projectManager.getProjectById(project.getId());

    projectDB.setModificationJustification(this.getJustification());
    // No need to call save as hibernate will detect the changes and auto flush.
  }

  public void savePublicationMetadata() {
    if (deliverable.getPublication() != null) {
      deliverable.getPublication().setDeliverable(deliverable);
      if (deliverable.getPublication().getId() != null && deliverable.getPublication().getId().intValue() == -1) {
        deliverable.getPublication().setId(null);
      }
      deliverable.getPublication().setPhase(this.getActualPhase());
      deliverablePublicationMetadataManager.saveDeliverablePublicationMetadata(deliverable.getPublication());

    }
  }

  public void saveQualityCheck() {
    DeliverableQualityCheck qualityCheck;
    if (deliverable.getQualityCheck() != null && deliverable.getQualityCheck().getId() != -1) {
      qualityCheck =
        deliverableQualityCheckManager.getDeliverableQualityCheckById(deliverable.getQualityCheck().getId());
    } else {
      qualityCheck = new DeliverableQualityCheck();
      qualityCheck.setDeliverable(deliverableManager.getDeliverableById(deliverable.getId()));
    }

    if (deliverable.getQualityCheck().getDataDictionary() != null) {
      long id = deliverable.getQualityCheck().getDataDictionary().getId();
      DeliverableQualityAnswer answer = deliverableQualityAnswerManager.getDeliverableQualityAnswerById(id);

      qualityCheck.setDataDictionary(answer);
    }
    if (deliverable.getQualityCheck().getQualityAssurance() != null) {
      long id = deliverable.getQualityCheck().getQualityAssurance().getId();
      DeliverableQualityAnswer answer = deliverableQualityAnswerManager.getDeliverableQualityAnswerById(id);

      qualityCheck.setQualityAssurance(answer);
    }


    if (deliverable.getQualityCheck().getDataTools() != null) {
      long id = deliverable.getQualityCheck().getDataTools().getId();
      DeliverableQualityAnswer answer = deliverableQualityAnswerManager.getDeliverableQualityAnswerById(id);

      qualityCheck.setDataTools(answer);
    }

    if (deliverable.getQualityCheck().getFileAssurance() != null) {
      if (deliverable.getQualityCheck().getFileAssurance().getId() != null
        && deliverable.getQualityCheck().getQualityAssurance() != null
        && Integer.valueOf(deliverable.getQualityCheck().getQualityAssurance().getId().intValue())
          .equals(APConstants.DELIVERABLE_QUALITY_ANSWER_YES)) {
        FileDB fileDb;
        // Set FileDB to null if an exception occurs (-1 id)
        try {
          fileDb = fileDBManager.getFileDBById(deliverable.getQualityCheck().getFileAssurance().getId());
        } catch (IllegalArgumentException e) {
          fileDb = null;
        }
        qualityCheck.setFileAssurance(fileDb);
      } else {
        qualityCheck.setFileAssurance(null);
      }
    } else {
      qualityCheck.setFileAssurance(null);
    }
    if (qualityCheck.getFileAssurance() != null) {
      if (qualityCheck.getFileAssurance().getId() == null) {
        qualityCheck.setFileAssurance(null);
      }
    }


    if (deliverable.getQualityCheck().getFileDictionary() != null) {
      if (deliverable.getQualityCheck().getFileDictionary().getId() != null
        && deliverable.getQualityCheck().getDataDictionary() != null
        && Integer.valueOf(deliverable.getQualityCheck().getDataDictionary().getId().intValue())
          .equals(APConstants.DELIVERABLE_QUALITY_ANSWER_YES)) {
        FileDB fileDb;
        // Set FileDB to null if an exception occurs (-1 id)
        try {
          fileDb = fileDBManager.getFileDBById(deliverable.getQualityCheck().getFileDictionary().getId());
        } catch (IllegalArgumentException e) {
          fileDb = null;
        }
        qualityCheck.setFileDictionary(fileDb);
      } else {
        qualityCheck.setFileDictionary(null);
      }
    } else {
      qualityCheck.setFileDictionary(null);
    }
    if (qualityCheck.getFileDictionary() != null) {
      if (qualityCheck.getFileDictionary().getId() == null) {
        qualityCheck.setFileDictionary(null);
      }
    }

    if (deliverable.getQualityCheck().getFileTools() != null) {
      if (deliverable.getQualityCheck().getFileTools().getId() != null
        && deliverable.getQualityCheck().getDataTools() != null
        && Integer.valueOf(deliverable.getQualityCheck().getDataTools().getId().intValue())
          .equals(APConstants.DELIVERABLE_QUALITY_ANSWER_YES)) {
        FileDB fileDb;
        // Set FileDB to null if an exception occurs (-1 id)
        try {
          fileDb = fileDBManager.getFileDBById(deliverable.getQualityCheck().getFileTools().getId());
        } catch (IllegalArgumentException e) {
          fileDb = null;
        }
        qualityCheck.setFileTools(fileDb);
      } else {
        qualityCheck.setFileTools(null);
      }
    } else {
      qualityCheck.setFileTools(null);
    }
    if (qualityCheck.getFileTools() != null) {
      if (qualityCheck.getFileTools().getId() == null) {
        qualityCheck.setFileTools(null);
      }
    }

    qualityCheck.setLinkAssurance(deliverable.getQualityCheck().getLinkAssurance());
    qualityCheck.setLinkDictionary(deliverable.getQualityCheck().getLinkDictionary());
    qualityCheck.setLinkTools(deliverable.getQualityCheck().getLinkTools());

    qualityCheck.setPhase(this.getActualPhase());
    deliverableQualityCheckManager.saveDeliverableQualityCheck(qualityCheck);

  }

  private DeliverablePartnership saveUpdateDeliverablePartnershipDivision(DeliverablePartnership partnership,
    DeliverablePartnership partnershipResponsibleManaged) {
    if (partnershipResponsibleManaged.getPartnerDivision() != null
      && partnershipResponsibleManaged.getPartnerDivision().getId().longValue() != -1) {
      try {
        PartnerDivision division =
          partnerDivisionManager.getPartnerDivisionById(partnershipResponsibleManaged.getPartnerDivision().getId());
        partnership.setPartnerDivision(division);
      } catch (Exception e) {
        // NEVER EVER JUST SWALLOW UNCHECKED EXCEPTIONS! Logging this now.
        logger.error("unable to filter DeliverablePartnership list", e);
        /**
         * Generally when an unchecked exception occurs you don't try and continue processing, something is wrong
         * and you need to deal with it. We need to review this logic at some point in time, as the user will not
         * be aware that the partnerDivision has not been saved and that is confusing.
         */
        partnership.setPartnerDivision(null);
      }
    } else {
      partnership.setPartnerDivision(null);
    }
    return partnership;
  }


  /**
   * Save, update or delete partnership's responsible
   * 
   * @param partnershipResponsibleDB partnership responsible from database
   * @param partnershipResponsibleManaged partnership responsible from interface
   */
  private void saveUpdateDeliverablePartnershipResponsible(DeliverablePartnership partnershipResponsibleDB,
    DeliverablePartnership partnershipResponsibleManaged) {
    if (partnershipResponsibleManaged.getProjectPartner() != null
      && partnershipResponsibleManaged.getProjectPartner().getId() != null
      && partnershipResponsibleManaged.getProjectPartner().getId() != -1) {
      partnershipResponsibleManaged.setProjectPartner(
        projectPartnerManager.getProjectPartnerById(partnershipResponsibleManaged.getProjectPartner().getId()));
      if (partnershipResponsibleDB == null) {
        if (!isManagingPartnerPersonRequerid) {
          this.createAndSaveNewDeliverablePartnership(partnershipResponsibleManaged,
            DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue());
        } else if (isManagingPartnerPersonRequerid && partnershipResponsibleManaged.getProjectPartnerPerson() != null) {
          this.createAndSaveNewDeliverablePartnership(partnershipResponsibleManaged,
            DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue());
        }
      } else {
        if (partnershipResponsibleDB.getProjectPartner().getId()
          .equals(partnershipResponsibleManaged.getProjectPartner().getId())) {
          this.checkChangesAndUpdateDeliverablePartnership(partnershipResponsibleDB, partnershipResponsibleManaged);
        } else {
          deliverablePartnershipManager.deleteDeliverablePartnership(partnershipResponsibleDB.getId());
          this.createAndSaveNewDeliverablePartnership(partnershipResponsibleManaged,
            DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue());
        }
      }
    } else {
      if (partnershipResponsibleDB != null) {
        deliverablePartnershipManager.deleteDeliverablePartnership(partnershipResponsibleDB.getId());
      }
    }
  }


  public void saveUsers() {
    if (deliverable.getUsers() == null) {

      deliverable.setUsers(new ArrayList<>());
    }
    Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
    for (DeliverableUser deliverableUser : deliverableDB.getDeliverableUsers().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
      if (!deliverable.getUsers().contains(deliverableUser)) {
        deliverableUserManager.deleteDeliverableUser(deliverableUser.getId());
      }
    }

    for (DeliverableUser deliverableUser : deliverable.getUsers()) {

      if (deliverableUser.getId() == null || deliverableUser.getId().intValue() == -1) {
        deliverableUser.setId(null);
        deliverableUser.setPhase(this.getActualPhase());
        deliverableUser.setDeliverable(deliverable);
        deliverableUserManager.saveDeliverableUser(deliverableUser);
      }
    }
  }


  public void setAnswers(List<DeliverableQualityAnswer> answers) {
    this.answers = answers;
  }

  public void setCountries(List<LocElement> countries) {
    this.countries = countries;
  }

  public void setcrossCuttingDimensions(List<CrossCuttingScoring> crossCuttingScores) {
    this.crossCuttingDimensions = crossCuttingScores;
  }


  public void setCrossCuttingScoresMap(Map<Long, String> crossCuttingScoresMap) {
    this.crossCuttingScoresMap = crossCuttingScoresMap;
  }


  public void setCrps(ArrayList<GlobalUnit> crps) {
    this.crps = crps;
  }


  public void setDeliverable(Deliverable deliverable) {
    this.deliverable = deliverable;
  }

  public void setDeliverableID(long deliverableID) {
    this.deliverableID = deliverableID;
  }


  public void setDeliverableSubTypes(List<DeliverableType> deliverableSubTypes) {
    this.deliverableSubTypes = deliverableSubTypes;
  }


  public void setDeliverableTypeParent(List<DeliverableType> deliverableTypeParent) {
    this.deliverableTypeParent = deliverableTypeParent;
  }

  public void setDivisions(List<PartnerDivision> divisions) {
    this.divisions = divisions;
  }


  public void setFundingSources(List<FundingSource> fundingSources) {
    this.fundingSources = fundingSources;
  }

  public void setGenderLevels(List<GenderType> genderLevels) {
    this.genderLevels = genderLevels;
  }


  public void setIndexTab(int indexTab) {
    this.indexTab = indexTab;
  }


  public void setKeyOutputs(List<CrpClusterKeyOutput> keyOutputs) {
    this.keyOutputs = keyOutputs;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setPartnerPersons(List<ProjectPartnerPerson> partnerPersons) {
    this.partnerPersons = partnerPersons;
  }

  public void setPartners(List<ProjectPartner> partners) {
    this.partners = partners;
  }

  public void setPrograms(ArrayList<CrpProgram> programs) {
    this.programs = programs;
  }

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setProjectOutcome(List<ProjectOutcome> projectOutcome) {
    this.projectOutcome = projectOutcome;
  }

  public void setProjectPrograms(List<ProjectFocus> projectPrograms) {
    this.projectPrograms = projectPrograms;
  }

  public void setRepIndFillingTypes(List<RepIndFillingType> repIndFillingTypes) {
    this.repIndFillingTypes = repIndFillingTypes;
  }

  public void setRepIndGeographicScopes(List<RepIndGeographicScope> repIndGeographicScopes) {
    this.repIndGeographicScopes = repIndGeographicScopes;
  }

  public void setRepIndPatentStatuses(List<RepIndPatentStatus> repIndPatentStatuses) {
    this.repIndPatentStatuses = repIndPatentStatuses;
  }

  public void setRepIndRegions(List<RepIndRegion> repIndRegions) {
    this.repIndRegions = repIndRegions;
  }

  public void setRepIndTypeActivities(List<RepIndTypeActivity> repIndTypeActivities) {
    this.repIndTypeActivities = repIndTypeActivities;
  }

  public void setRepIndTypeParticipants(List<RepIndTypeParticipant> repIndTypeParticipants) {
    this.repIndTypeParticipants = repIndTypeParticipants;
  }


  public void setRepositoryChannels(List<RepositoryChannel> repositoryChannels) {
    this.repositoryChannels = repositoryChannels;
  }


  public void setStatus(Map<String, String> status) {
    this.status = status;
  }


  public void setStatuses(Map<String, String> statuses) {
    this.statuses = statuses;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


  /**
   * Save and update list of deliverables funding sources for all phases
   * 
   * @param deliverablePrew
   */
  private void updateDeliverableFundingSources(Deliverable deliverablePrew) {
    if (deliverable.getFundingSources() != null) {
      if (deliverablePrew.getDeliverableFundingSources() != null
        && deliverablePrew.getDeliverableFundingSources().size() > 0) {
        List<DeliverableFundingSource> fundingSourcesPrew = deliverablePrew.getDeliverableFundingSources().stream()
          .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList());


        for (DeliverableFundingSource deliverableFundingSource : fundingSourcesPrew) {
          if (!deliverable.getFundingSources().contains(deliverableFundingSource)) {
            deliverableFundingSourceManager.deleteDeliverableFundingSource(deliverableFundingSource.getId());
          }
        }
      }

      for (DeliverableFundingSource deliverableFundingSource : deliverable.getFundingSources()) {
        if (deliverableFundingSource.getId() == null || deliverableFundingSource.getId() == -1) {


          deliverableFundingSource.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
          deliverableFundingSource.setPhase(this.getActualPhase());
          deliverableFundingSourceManager.saveDeliverableFundingSource(deliverableFundingSource);
        }
      }
    }
  }

  /**
   * deliverableDb is in a managed state, deliverable is in a detached state.
   * 
   * @return Deliverable with a deliverableInfo with client data
   */
  private Deliverable updateDeliverableInfo() {

    Deliverable deliverableBase = deliverableManager.getDeliverableById(deliverableID);
    DeliverableInfo deliverableInfoDb = deliverableBase.getDeliverableInfo(this.getActualPhase());

    deliverableInfoDb.setTitle(deliverable.getDeliverableInfo(this.getActualPhase()).getTitle());
    deliverableInfoDb.setDescription(deliverable.getDeliverableInfo(this.getActualPhase()).getDescription());

    deliverableInfoDb.setYear(deliverable.getDeliverableInfo(this.getActualPhase()).getYear());

    if (deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null) {
      deliverableInfoDb.setNewExpectedYear(deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear());
    }

    if (this.isPlanningActive()) {
      if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
        .parseInt(ProjectStatusEnum.Extended.getStatusId())
        && deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null) {
        deliverableInfoDb
          .setNewExpectedYear(deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear());
      } else {
        deliverableInfoDb.setNewExpectedYear(null);
      }
    } else {
      if ((deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
        .parseInt(ProjectStatusEnum.Extended.getStatusId())
        || deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
          .parseInt(ProjectStatusEnum.Complete.getStatusId()))
        && deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null) {
        deliverableInfoDb
          .setNewExpectedYear(deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear());
      } else {
        deliverableInfoDb.setNewExpectedYear(null);
      }
    }

    if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingCapacity() == null) {
      deliverableInfoDb.setCrossCuttingCapacity(false);
      deliverableInfoDb.setCrossCuttingScoreCapacity(APConstants.CROSS_CUTTING_NOT_TARGETED);
    } else {
      deliverableInfoDb.setCrossCuttingCapacity(true);
      deliverableInfoDb.setCrossCuttingScoreCapacity(
        deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingScoreCapacity());
    }
    if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingNa() == null) {
      deliverableInfoDb.setCrossCuttingNa(false);
    } else {
      deliverableInfoDb.setCrossCuttingNa(true);
    }
    if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingGender() == null) {
      deliverableInfoDb.setCrossCuttingGender(false);
      deliverableInfoDb.setCrossCuttingScoreGender(APConstants.CROSS_CUTTING_NOT_TARGETED);
    } else {
      deliverableInfoDb.setCrossCuttingGender(true);
      deliverableInfoDb
        .setCrossCuttingScoreGender(deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingScoreGender());
    }
    if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingYouth() == null) {
      deliverableInfoDb.setCrossCuttingYouth(false);
      deliverableInfoDb.setCrossCuttingScoreYouth(APConstants.CROSS_CUTTING_NOT_TARGETED);
    } else {
      deliverableInfoDb.setCrossCuttingYouth(true);
      deliverableInfoDb
        .setCrossCuttingScoreYouth(deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingScoreYouth());
    }

    if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null) {
      deliverableInfoDb.setStatus(deliverable.getDeliverableInfo(this.getActualPhase()).getStatus());
    }

    if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType() != null
      && deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId() != null
      && deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId().longValue() != -1) {
      DeliverableType deliverableType = deliverableTypeManager
        .getDeliverableTypeById(deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getId());

      deliverableInfoDb.setDeliverableType(deliverableType);
    } else {
      deliverableInfoDb.setDeliverableType(null);
    }
    // Set CrpClusterKeyOutput to null if has an -1 id

    if (deliverable.getDeliverableInfo().getCrpClusterKeyOutput() == null
      || deliverable.getDeliverableInfo().getCrpClusterKeyOutput().getId() == null
      || deliverable.getDeliverableInfo().getCrpClusterKeyOutput().getId().longValue() == -1) {
      deliverableInfoDb.setCrpClusterKeyOutput(null);
    } else {
      deliverableInfoDb
        .setCrpClusterKeyOutput(deliverable.getDeliverableInfo(this.getActualPhase()).getCrpClusterKeyOutput());
    }

    deliverableInfoDb
      .setStatusDescription(deliverable.getDeliverableInfo(this.getActualPhase()).getStatusDescription());
    if (this.isReportingActive()) {

      if (deliverable.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense() != null) {
        deliverableInfoDb.setAdoptedLicense(deliverable.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense());
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense().booleanValue()) {
          deliverableInfoDb.setLicense(deliverable.getDeliverableInfo(this.getActualPhase()).getLicense());
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getLicense() != null) {
            if (deliverable.getDeliverableInfo(this.getActualPhase()).getLicense()
              .equals(LicensesTypeEnum.OTHER.getValue())) {
              deliverableInfoDb
                .setOtherLicense(deliverable.getDeliverableInfo(this.getActualPhase()).getOtherLicense());
              deliverableInfoDb
                .setAllowModifications(deliverable.getDeliverableInfo(this.getActualPhase()).getAllowModifications());
            } else {
              deliverableInfoDb.setOtherLicense(null);
              deliverableInfoDb.setAllowModifications(null);
            }
          }
          deliverableInfoDb
            .setAdoptedLicense(deliverable.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense());
        } else {

          deliverableInfoDb.setLicense(null);
          deliverableInfoDb.setOtherLicense(null);
          deliverableInfoDb.setAllowModifications(null);
        }
      } else {
        deliverableInfoDb.setLicense(null);
        deliverableInfoDb.setOtherLicense(null);
        deliverableInfoDb.setAllowModifications(null);
      }
    }

    deliverableInfoDb.setModificationJustification(this.getJustification());

    deliverableBase.setDeliverableInfo(deliverableInfoDb);
    return deliverableBase;
  }


  /**
   * This is for updating the list of Other Deliverable Partnerships.
   */
  private void updateOtherDeliverablePartnerships() {
    if (deliverable.getOtherPartners() != null) {
      // Iterate through the list of detached entities.
      for (DeliverablePartnership deliverablePartnershipOther : deliverable.getOtherPartners()) {
        if (deliverablePartnershipOther.getProjectPartner() != null) {
          if (deliverablePartnershipOther.getId() == null) {
            if (isManagingPartnerPersonRequerid) {
              if (deliverablePartnershipOther.getProjectPartnerPerson() != null) {
                this.createAndSaveNewDeliverablePartnership(deliverablePartnershipOther,
                  DeliverablePartnershipTypeEnum.OTHER.getValue());
              }
            } else {
              this.createAndSaveNewDeliverablePartnership(deliverablePartnershipOther,
                DeliverablePartnershipTypeEnum.OTHER.getValue());
            }
          } else {
            DeliverablePartnership partnershipResponsibleDB =
              deliverablePartnershipManager.getDeliverablePartnershipById(deliverablePartnershipOther.getId());
            if (deliverablePartnershipOther.getProjectPartner().getId()
              .equals(partnershipResponsibleDB.getProjectPartner().getId())) {
              this.checkChangesAndUpdateDeliverablePartnership(partnershipResponsibleDB, deliverablePartnershipOther);
            } else {
              deliverablePartnershipManager.deleteDeliverablePartnership(partnershipResponsibleDB.getId());
              this.createAndSaveNewDeliverablePartnership(deliverablePartnershipOther,
                DeliverablePartnershipTypeEnum.OTHER.getValue());
            }
          }
        }
      }
    }
  }

  @Override
  public void validate() {
    if (save) {
      deliverableValidator.validate(this, deliverable, true);
    }
  }


}
