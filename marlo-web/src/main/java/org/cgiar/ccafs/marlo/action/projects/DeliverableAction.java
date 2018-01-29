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
import org.cgiar.ccafs.marlo.data.manager.CrpClusterKeyOutputManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPandrManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrpManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDataSharingFileManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDisseminationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableFundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableGenderLevelManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataElementManager;
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
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.MetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerDivisionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.manager.RepositoryChannelManager;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpPandr;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableCrp;
import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharingFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableFile;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
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
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.LicensesTypeEnum;
import org.cgiar.ccafs.marlo.data.model.PartnerDivision;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudget;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.RepositoryChannel;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
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
import java.util.Date;
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

  private List<DeliverableQualityAnswer> answers;

  private AuditLogManager auditLogManager;


  private List<RepositoryChannel> repositoryChannels;

  private CrpClusterKeyOutputManager crpClusterKeyOutputManager;


  private GlobalUnitManager crpManager;


  private Map<String, String> crps;


  private Map<String, String> programs;

  private Deliverable deliverable;


  private DeliverableDataSharingFileManager deliverableDataSharingFileManager;


  private DeliverablePublicationMetadataManager deliverablePublicationMetadataManager;


  private DeliverableFundingSourceManager deliverableFundingSourceManager;


  private DeliverableUserManager deliverableUserManager;


  private DeliverableCrpManager deliverableCrpManager;


  private DeliverableGenderLevelManager deliverableGenderLevelManager;

  private CrpPandrManager crpPandrManager;

  private long deliverableID;


  private DeliverableManager deliverableManager;


  private DeliverableInfoManager deliverableInfoManager;
  private HistoryComparator historyComparator;
  private DeliverableMetadataElementManager deliverableMetadataElementManager;

  private DeliverablePartnershipManager deliverablePartnershipManager;
  private InstitutionManager institutionManager;

  private DeliverableQualityAnswerManager deliverableQualityAnswerManager;

  private DeliverableQualityCheckManager deliverableQualityCheckManager;

  private DeliverableDisseminationManager deliverableDisseminationManager;
  private List<DeliverableType> deliverableSubTypes;

  private Boolean has_specific_management_deliverables;
  private Boolean isManagingPartnerPersonRequerid;


  // Managers
  private DeliverableTypeManager deliverableTypeManager;
  private List<DeliverableType> deliverableTypeParent;


  private DeliverableValidator deliverableValidator;

  private FileDBManager fileDBManager;


  private GenderTypeManager genderTypeManager;

  private IpProgramManager ipProgramManager;


  private FundingSourceManager fundingSourceManager;

  private List<FundingSource> fundingSources;

  private List<GenderType> genderLevels;

  private List<CrpClusterKeyOutput> keyOutputs;

  private GlobalUnit loggedCrp;

  private MetadataElementManager metadataElementManager;


  private List<ProjectPartnerPerson> partnerPersons;

  private List<ProjectPartner> partners;

  private Project project;
  private long projectID;


  private ProjectManager projectManager;


  private List<ProjectOutcome> projectOutcome;


  private ProjectPartnerManager projectPartnerManager;

  private ProjectPartnerPersonManager projectPartnerPersonManager;

  private List<ProjectFocus> projectPrograms;

  private Map<String, String> status;

  private String transaction;

  private int indexTab;


  private PartnerDivisionManager partnerDivisionManager;


  private RepositoryChannelManager repositoryChannelManager;

  private List<PartnerDivision> divisions;


  @Inject
  public DeliverableAction(APConfig config, DeliverableTypeManager deliverableTypeManager,
    DeliverableMetadataElementManager deliverableMetadataElementManager, DeliverableManager deliverableManager,
    GlobalUnitManager crpManager, ProjectManager projectManager,
    ProjectPartnerPersonManager projectPartnerPersonManager, CrpClusterKeyOutputManager crpClusterKeyOutputManager,
    DeliverablePartnershipManager deliverablePartnershipManager, AuditLogManager auditLogManager,
    DeliverableValidator deliverableValidator, ProjectPartnerManager projectPartnerManager,
    FundingSourceManager fundingSourceManager, DeliverableFundingSourceManager deliverableFundingSourceManager,
    DeliverableGenderLevelManager deliverableGenderLevelManager,
    DeliverableQualityCheckManager deliverableQualityCheckManager, DeliverableCrpManager deliverableCrpManager,
    DeliverableQualityAnswerManager deliverableQualityAnswerManager,
    DeliverableDataSharingFileManager deliverableDataSharingFileManager, FileDBManager fileDBManager,
    DeliverableUserManager deliverableUserManager, GenderTypeManager genderTypeManager,
    HistoryComparator historyComparator, DeliverablePublicationMetadataManager deliverablePublicationMetadataManager,
    InstitutionManager institutionManager, MetadataElementManager metadataElementManager,
    DeliverableDisseminationManager deliverableDisseminationManager, CrpPandrManager crpPandrManager,
    IpProgramManager ipProgramManager, PartnerDivisionManager partnerDivisionManager,
    RepositoryChannelManager repositoryChannelManager, DeliverableInfoManager deliverableInfoManager) {
    super(config);
    this.deliverableManager = deliverableManager;
    this.deliverableTypeManager = deliverableTypeManager;
    this.crpManager = crpManager;
    this.historyComparator = historyComparator;
    this.deliverableUserManager = deliverableUserManager;
    this.projectManager = projectManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.institutionManager = institutionManager;
    this.deliverableCrpManager = deliverableCrpManager;
    this.deliverablePublicationMetadataManager = deliverablePublicationMetadataManager;
    this.projectPartnerPersonManager = projectPartnerPersonManager;
    this.crpClusterKeyOutputManager = crpClusterKeyOutputManager;
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
    this.crpPandrManager = crpPandrManager;
    this.ipProgramManager = ipProgramManager;
    this.partnerDivisionManager = partnerDivisionManager;
    this.repositoryChannelManager = repositoryChannelManager;
  }


  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
    }

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
    if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
      .parseInt(ProjectStatusEnum.Extended.getStatusId())
      || deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() == Integer
        .parseInt(ProjectStatusEnum.Cancelled.getStatusId())) {
      return true;
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
    partnership.setActive(true);
    partnership.setCreatedBy(this.getCurrentUser());
    partnership.setModifiedBy(this.getCurrentUser());
    partnership.setModificationJustification("");
    partnership.setActiveSince(new Date());
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


  public Map<String, String> getCrps() {
    return crps;
  }


  public Deliverable getDeliverable() {
    return deliverable;
  }

  public long getDeliverableID() {
    return deliverableID;
  }

  public DeliverablePartnership getDeliverablePartnership(long projectPeronID) {

    // List<DeliverablePartnership> deliverablePartnerships = deliverableManager.getDeliverableById(deliverableID)
    // .getDeliverablePartnerships().stream().filter(c -> c.isActive()
    // && c.getProjectPartnerPerson().getId().longValue() == projectPeronID && c.getPartnerType().equals("Other"))
    // .collect(Collectors.toList());

    List<DeliverablePartnership> deliverablePartnerships = deliverablePartnershipManager
      .findForDeliverableIdAndProjectPersonIdPartnerTypeOther(deliverableID, projectPeronID);

    for (DeliverablePartnership deliverablePartnership : deliverablePartnerships) {
      return deliverablePartnership;
    }

    return null;

  }


  private DeliverablePartnership getDeliverablePartnershipDB(Deliverable deliverableDB) {
    DeliverablePartnership partnershipResponsible = null;

    if (deliverableDB.getDeliverablePartnerships() != null && deliverableDB.getDeliverablePartnerships().size() > 0) {

      try {
        partnershipResponsible = deliverableDB.getDeliverablePartnerships().stream()
          .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(this.getActualPhase())
            && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
          .collect(Collectors.toList()).get(0);
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

  public Map<String, String> getPrograms() {
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

  public List<RepositoryChannel> getRepositoryChannels() {
    return repositoryChannels;
  }


  public List<ProjectPartner> getSelectedPartners() {
    Set<ProjectPartner> deliverablePartnerPersonsSet = new HashSet<>();
    List<ProjectPartner> deliverablePartnerPersons = new ArrayList<>();

    for (DeliverablePartnership deliverablePartnership : deliverableManager.getDeliverableById(deliverableID)
      .getDeliverablePartnerships().stream()
      .filter(c -> c.isActive() && c.getPartnerType().equals("Other") && c.getPhase().equals(this.getActualPhase()))
      .collect(Collectors.toList())) {
      deliverablePartnerPersonsSet.add(deliverablePartnership.getProjectPartner());
    }

    deliverablePartnerPersons.addAll(deliverablePartnerPersonsSet);
    return deliverablePartnerPersons;

  }

  public List<Long> getSelectedPersons(long partnerID) {

    List<ProjectPartnerPerson> deliverablePartnerPersons =
      projectPartnerPersonManager.findAllForOtherPartnerTypeWithDeliverableIdAndPartnerId(deliverableID, partnerID);

    for (DeliverablePartnership deliverablePartnership : deliverableManager.getDeliverableById(deliverableID)
      .getDeliverablePartnerships().stream()
      .filter(c -> c.isActive() && c.getProjectPartner().getId().longValue() == partnerID
        && c.getPartnerType().equals("Other") && c.getPhase().equals(this.getActualPhase()))
      .collect(Collectors.toList())) {
      if (deliverablePartnership.getProjectPartnerPerson() != null) {
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
          .filter(c -> c.getCrp().getId().longValue() == loggedCrp.getId().longValue() && c.isActive())
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


  public List<DeliverablePartnership> otherPartners() {
    try {
      List<DeliverablePartnership> list =
        deliverable.getDeliverablePartnerships().stream()
          .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(this.getActualPhase())
            && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.OTHER.getValue()))
        .collect(Collectors.toList());


      return list;
    } catch (Exception e) {
      logger.error("unable to get otherPartners", e);
      return null;
    }


  }


  public List<DeliverablePartnership> otherPartnersAutoSave() {
    try {
      List<DeliverablePartnership> list = new ArrayList<>();
      for (DeliverablePartnership partnership : deliverable.getOtherPartners()) {
        if (partnership.getId() == null || partnership.getId() == -1) {
          ProjectPartnerPerson partnerPersonDb =
            projectPartnerPersonManager.getProjectPartnerPersonById(partnership.getProjectPartnerPerson().getId());
          ProjectPartner partnerDb =
            projectPartnerManager.getProjectPartnerById(partnership.getProjectPartner().getId());
          DeliverablePartnership partnershipOth = new DeliverablePartnership();

          partnershipOth.setDeliverable(deliverable);
          partnershipOth.setProjectPartnerPerson(partnerPersonDb);
          partnershipOth.setProjectPartner(partnerDb);
          partnershipOth.setPartnerType(DeliverablePartnershipTypeEnum.OTHER.getValue());
          partnershipOth.setActive(true);
          list.add(partnershipOth);
        } else {
          list.add(deliverablePartnershipManager.getDeliverablePartnershipById(partnership.getId()));
        }
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
              partnership.setActive(true);
              partnership.setCreatedBy(this.getCurrentUser());
              partnership.setModifiedBy(this.getCurrentUser());
              partnership.setModificationJustification("");
              partnership.setActiveSince(new Date());
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
                partnershipNew.setActive(true);
                partnershipNew.setCreatedBy(this.getCurrentUser());
                partnershipNew.setModifiedBy(this.getCurrentUser());
                partnershipNew.setModificationJustification("");
                partnershipNew.setActiveSince(new Date());
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

        Map<String, String> specialList = new HashMap<>();


        this.setDifferences(historyComparator.getDifferences(transaction, specialList, "deliverable"));
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }

      if (deliverable.getDeliverableQualityChecks() != null) {
        List<DeliverableQualityCheck> checks = new ArrayList<>(
          deliverable.getDeliverableQualityChecks().stream().filter(qc -> qc.isActive()).collect(Collectors.toList()));
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

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

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

              if (deliverableCrp.getIpProgram() == null || deliverableCrp.getIpProgram().getId() == null
                || deliverableCrp.getIpProgram().getId().intValue() == -1) {
                deliverableCrp.setCrpPandr(crpPandrManager.getCrpPandrById(deliverableCrp.getCrpPandr().getId()));

              } else {
                deliverableCrp.setIpProgram(ipProgramManager.getIpProgramById(deliverableCrp.getIpProgram().getId()));
                deliverableCrp.setCrpPandr(crpPandrManager.getCrpPandrById(3));
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

          if (deliverable.getQualityCheck().getFileTools() != null) {
            if (deliverable.getQualityCheck().getFileTools().getId() != null) {
              FileDB db;
              // Set FileDB to null if an error occur (-1 id)
              try {
                db = fileDBManager.getFileDBById(deliverable.getQualityCheck().getFileTools().getId());
              } catch (IllegalArgumentException e) {
                db = null;
              }
              deliverable.getQualityCheck().setFileTools(db);
            }
          }
        }


        this.setDraft(true);
      } else {
        deliverable.getDeliverableInfo(this.getActualPhase());
        deliverable.setResponsiblePartner(this.responsiblePartner());
        deliverable.setOtherPartners(this.otherPartners());

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
              .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
          }

          if (deliverable.getDeliverableDisseminations() != null) {
            deliverable.setDisseminations(new ArrayList<>(deliverable.getDeliverableDisseminations().stream()
              .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
            if (deliverable.getDeliverableDisseminations().size() > 0) {
              deliverable.setDissemination(deliverable.getDisseminations().get(0));
            } else {
              deliverable.setDissemination(new DeliverableDissemination());
            }

          }

          if (deliverable.getDeliverableDataSharingFiles() != null) {
            deliverable.setDataSharingFiles(new ArrayList<>(deliverable.getDeliverableDataSharingFiles().stream()
              .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
          }

          if (deliverable.getDeliverablePublicationMetadatas() != null) {
            deliverable.setPublicationMetadatas(new ArrayList<>(deliverable.getDeliverablePublicationMetadatas()
              .stream().filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
          }
          if (!deliverable.getPublicationMetadatas().isEmpty()) {
            deliverable.setPublication(deliverable.getPublicationMetadatas().get(0));
          }

          if (deliverable.getDeliverableDataSharings() != null) {
            deliverable.setDataSharing(new ArrayList<>(deliverable.getDeliverableDataSharings().stream()
              .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())));
          }


          deliverable.setUsers(deliverable.getDeliverableUsers().stream()
            .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
          deliverable.setCrps(deliverable.getDeliverableCrps().stream()
            .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
          deliverable.setFiles(new ArrayList<>());
          for (DeliverableDataSharingFile dataSharingFile : deliverable.getDeliverableDataSharingFiles().stream()
            .filter(c -> c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {

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
        }

        this.setDraft(false);
      }
      for (DeliverableGenderLevel deliverableGenderLevel : deliverable.getGenderLevels()) {
        try {
          deliverableGenderLevel.setNameGenderLevel(
            genderTypeManager.getGenderTypeById(deliverableGenderLevel.getGenderLevel()).getDescription());
          deliverableGenderLevel.setDescriptionGenderLevel(
            genderTypeManager.getGenderTypeById(deliverableGenderLevel.getGenderLevel()).getCompleteDescription());
        } catch (Exception e) {
          logger.error("unable to update DeliverableGenderLevel", e);
          /**
           * Original code swallows the exception and didn't even log it. Now we at least log it,
           * but we need to revisit to see if we should continue processing or re-throw the exception.
           */
        }
      }
      if (metadataElementManager.findAll() != null) {
        deliverable.setMetadata(new ArrayList<>(metadataElementManager.findAll()));
      }
      answers = new ArrayList<>(
        deliverableQualityAnswerManager.findAll().stream().filter(qa -> qa.isActive()).collect(Collectors.toList()));


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
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus().intValue() != Integer
            .parseInt(ProjectStatusEnum.Cancelled.getStatusId())) {
            status.remove(ProjectStatusEnum.Cancelled.getStatusId());
          }

        }
      } else {
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getYear() <= this.getReportingYear()) {

          status.remove(ProjectStatusEnum.Cancelled.getStatusId());
        }
      }
      if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() != null) {
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
          .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
          status.remove(ProjectStatusEnum.Complete.getStatusId());
        }
      }


      genderLevels = new ArrayList<>();
      List<GenderType> genderTypes = null;
      if (this.hasSpecificities(APConstants.CRP_CUSTOM_GENDER)) {
        genderTypes = genderTypeManager.findAll().stream()
          .filter(c -> c.getCrp() != null && c.getCrp().getId().longValue() == loggedCrp.getId().longValue())
          .collect(Collectors.toList());
      } else {
        genderTypes = genderTypeManager.findAll().stream().filter(c -> c.getCrp() == null).collect(Collectors.toList());
      }

      for (GenderType projectStatusEnum : genderTypes) {
        genderLevels.add(projectStatusEnum);
      }
      crps = new HashMap<>();
      for (CrpPandr crp : crpPandrManager.findAll().stream().filter(c -> c.getId() != 3 && c.isActive())
        .collect(Collectors.toList())) {
        crps.put(crp.getId().toString(), crp.getName());
      }

      programs = new HashMap<>();
      for (IpProgram program : ipProgramManager.findAll().stream().filter(c -> c.getIpProgramType().getId() == 4)
        .collect(Collectors.toList())) {
        programs.put(program.getId().toString(), program.getAcronym());
      }

      deliverableTypeParent = new ArrayList<>(deliverableTypeManager.findAll().stream()
        .filter(dt -> dt.getDeliverableType() == null && dt.getCrp() == null && !dt.getAdminType().booleanValue())
        .collect(Collectors.toList()));

      deliverableTypeParent.addAll(new ArrayList<>(deliverableTypeManager.findAll().stream()
        .filter(dt -> dt.getDeliverableType() == null && dt.getCrp() != null
          && dt.getCrp().getId().longValue() == loggedCrp.getId().longValue() && !dt.getAdminType().booleanValue())
        .collect(Collectors.toList())));

      if (project.getProjecInfoPhase(this.getActualPhase()).getAdministrative() != null
        && project.getProjecInfoPhase(this.getActualPhase()).getAdministrative().booleanValue()) {

        deliverableTypeParent
          .addAll(deliverableTypeManager
            .findAll().stream().filter(dt -> dt.getDeliverableType() == null && dt.getCrp() == null
              && dt.getAdminType().booleanValue() && !has_specific_management_deliverables)
          .collect(Collectors.toList()));

        deliverableTypeParent.addAll(new ArrayList<>(deliverableTypeManager.findAll().stream()
          .filter(dt -> dt.getDeliverableType() == null && dt.getCrp() != null
            && dt.getCrp().getId().longValue() == loggedCrp.getId().longValue() && dt.getAdminType().booleanValue())
          .collect(Collectors.toList())));
      }
      if (deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType() != null) {
        Long deliverableTypeParentId =
          deliverable.getDeliverableInfo(this.getActualPhase()).getDeliverableType().getDeliverableType().getId();

        deliverableSubTypes = new ArrayList<>(deliverableTypeManager.findAll().stream()
          .filter(dt -> dt.getDeliverableType() != null && dt.getDeliverableType().getId() == deliverableTypeParentId)
          .collect(Collectors.toList()));
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

      // Getting partners list
      partners = new ArrayList<>();
      for (ProjectPartner partner : projectPartnerManager.findAll().stream()
        .filter(
          pp -> pp.isActive() && pp.getProject().getId() == projectID && pp.getPhase().equals(this.getActualPhase()))
        .collect(Collectors.toList())) {
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

      // List<ProjectPartner> projectPartnersWithActiveProjectPartnerPersons = projectPartnerManager.findAll().stream()
      // .filter(pp -> pp.isActive() && (pp.getProject().getId() == projectID
      // && !pp.getProjectPartnerPersons().stream().filter(c -> c.isActive()).collect(Collectors.toList()).isEmpty()))
      // .collect(Collectors.toList());

      partnerPersons = new ArrayList<>();
      for (ProjectPartner partner : projectPartnerManager.findAll().stream()
        .filter(
          pp -> pp.isActive() && pp.getProject().getId() == projectID && pp.getPhase().equals(this.getActualPhase()))
        .collect(Collectors.toList())) {

        // partnerPersons = new ArrayList<>();
        partnerPersons =
          partners.stream().flatMap(e -> e.getProjectPartnerPersons().stream()).collect(Collectors.toList());

        // List<ProjectPartner> projectPartners = projectPartnerManager.findAll().stream()
        // .filter(pp -> pp.isActive() && pp.getProject().getId() == projectID).collect(Collectors.toList());
        //
        // for (ProjectPartner partner : projectPartners) {
        //
        // for (ProjectPartnerPerson partnerPerson : partner.getProjectPartnerPersons().stream()
        // .filter(ppa -> ppa.isActive()).collect(Collectors.toList())) {
        //
        // partnerPersons.add(partnerPerson);
        // }
        // }

        this.fundingSources = new ArrayList<>();
        List<FundingSource> fundingSources =
          fundingSourceManager.findAll().stream().filter(fs -> fs.isActive()).collect(Collectors.toList());
        for (FundingSource fundingSource : fundingSources) {

          for (ProjectBudget budget : fundingSource.getProjectBudgets().stream().filter(c -> c.isActive())
            .collect(Collectors.toList())) {
            if (budget.getProject().getId().longValue() == deliverable.getProject().getId()) {
              fundingSource.setFundingSourceInfo(fundingSource.getFundingSourceInfo(this.getActualPhase()));
              if (fundingSource.getFundingSourceInfo() == null) {
                fundingSource.setFundingSourceInfo(fundingSource.getFundingSourceInfoLast(this.getActualPhase()));

              }
              if (fundingSource.getFundingSourceInfo() != null) {
                this.fundingSources.add(fundingSource);
              }

            }

          }
        }
        Set<FundingSource> hs = new HashSet();
        hs.addAll(this.fundingSources);
        this.fundingSources.clear();
        this.fundingSources.addAll(hs);
        this.fundingSources.sort((o1, o2) -> {
          if (o1.getFundingSourceInfo(this.getActualPhase()) != null
            && o2.getFundingSourceInfo(this.getActualPhase()) != null &&

          o1.getFundingSourceInfo(this.getActualPhase()).getBudgetType() != null
            && o2.getFundingSourceInfo(this.getActualPhase()).getBudgetType() != null
            && o2.getFundingSourceInfo(this.getActualPhase()).getTitle() != null) {

            int cmp = o1.getFundingSourceInfo(this.getActualPhase()).getBudgetType().getId()
              .compareTo(o2.getFundingSourceInfo(this.getActualPhase()).getBudgetType().getId());
            if (cmp == 0) {
              cmp = o1.getFundingSourceInfo(this.getActualPhase()).getTitle()
                .compareTo(o2.getFundingSourceInfo(this.getActualPhase()).getTitle());
            }

            return cmp;
          }
          return 0;
        });

      }
      if (deliverable.getFiles() != null) {
        deliverable.getFiles().sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
      }


      repositoryChannels = repositoryChannelManager.findAll();
      if (repositoryChannels != null && repositoryChannels.size() > 0) {
        repositoryChannels.sort((rc1, rc2) -> rc1.getShortName().compareTo(rc2.getShortName()));
      } else {
        repositoryChannels = new LinkedList<RepositoryChannel>();
      }

      String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
      this.setBasePermission(this.getText(Permission.PROJECT_DELIVERABLE_BASE_PERMISSION, params));

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

        if (deliverable.getQualityCheck() != null) {
          deliverable.getQualityCheck().setFileAssurance(null);
          deliverable.getQualityCheck().setFileDictionary(null);
          deliverable.getQualityCheck().setFileTools(null);
        }
        deliverable.getDeliverableInfo(this.getActualPhase()).setCrpClusterKeyOutput(null);
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
      List<DeliverablePartnership> partnerShipsOtherPrew =
        deliverablePrew.getDeliverablePartnerships().stream()
          .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(this.getActualPhase())
            && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.OTHER.getValue()))
        .collect(Collectors.toList());

      if (deliverable.getOtherPartners() == null) {
        deliverable.setOtherPartners(new ArrayList<>());
      }
      for (DeliverablePartnership deliverablePartnership : deliverable.getOtherPartners()) {
        if (deliverablePartnership.getProjectPartner() == null) {
          deliverablePartnership.setId(null);
        } else {
          if (isManagingPartnerPersonRequerid && deliverablePartnership.getProjectPartnerPerson() == null) {
            deliverablePartnership.setId(null);
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

  private DeliverablePartnership responsiblePartner() {
    try {
      DeliverablePartnership partnership = deliverable.getDeliverablePartnerships().stream()
        .filter(dp -> dp.isActive() && dp.getPhase() != null && dp.getPhase().equals(this.getActualPhase())
          && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
        .collect(Collectors.toList()).get(0);
      return partnership;
    } catch (Exception e) {
      return null;
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
        DeliverablePartnership partnershipDB = deliverableDB.getDeliverablePartnerships().stream()
          .filter(
            dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
          .collect(Collectors.toList()).get(0);
        if (partnershipDB != null) {
          partnership.setId(partnershipDB.getId());
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
      partnership.setActive(true);

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

      // The next three statements, could possibly be merged into a single mapping class.
      Deliverable deliverableManagedState = this.updateDeliverable();
      this.updateDeliverableInReportingPhase(deliverableManagedState);
      this.updateDeliverableInPlanningPhase(deliverableManagedState);

      // Set CrpClusterKeyOutput to null if has an -1 id
      if (deliverableManagedState.getDeliverableInfo(this.getActualPhase()).getCrpClusterKeyOutput() != null
        && deliverableManagedState.getDeliverableInfo(this.getActualPhase()).getCrpClusterKeyOutput().getId() != null
        && deliverableManagedState.getDeliverableInfo(this.getActualPhase()).getCrpClusterKeyOutput().getId()
          .longValue() == -1) {
        deliverableManagedState.getDeliverableInfo(this.getActualPhase()).setCrpClusterKeyOutput(null);
      }

      /**
       * This seems like an unnecessary duplicate call as our deliverable already has an id (we only update in this
       * action)
       */
      // deliverablePrew = deliverableManager.saveDeliverable(deliverablePrew);

      if (deliverable.getDeliverableInfo(this.getActualPhase()).getStatus() == Integer
        .parseInt(ProjectStatusEnum.Extended.getStatusId())
        && deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null) {
        deliverableManagedState.getDeliverableInfo(this.getActualPhase())
          .setNewExpectedYear(deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear());
      } else {
        deliverableManagedState.getDeliverableInfo(this.getActualPhase()).setNewExpectedYear(null);
      }
      // This gets a DeliverablePartnership responsible entity in managed state.
      DeliverablePartnership partnershipResponsibleManaged = deliverable.getResponsiblePartner();
      Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
      // gets delivetablePartnership responsible from database
      DeliverablePartnership partnershipResponsibleDB = this.getDeliverablePartnershipDB(deliverableDB);
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
      }

      deliverableManagedState.getDeliverableInfo(this.getActualPhase()).setModifiedBy(this.getCurrentUser());
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
      }
      deliverableManagedState.setActiveSince(new Date());
      deliverableManagedState.setCreatedBy(this.getCurrentUser());
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
    Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
    for (DeliverableCrp deliverableCrp : deliverableDB.getDeliverableCrps().stream()
      .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList())) {
      if (!deliverable.getCrps().contains(deliverableCrp)) {
        deliverableCrpManager.deleteDeliverableCrp(deliverableCrp.getId());
      }
    }

    for (DeliverableCrp deliverableCrp : deliverable.getCrps()) {

      if (deliverableCrp.getId() == null || deliverableCrp.getId().intValue() == -1) {
        deliverableCrp.setId(null);
        deliverableCrp.setDeliverable(deliverable);

        if (deliverableCrp.getCrpPandr() != null) {
          if (deliverableCrp.getCrpPandr().getId() == null) {
            deliverableCrp.setCrpPandr(null);
          } else {
            if (deliverableCrp.getCrpPandr().getId().intValue() == -1) {
              deliverableCrp.setCrpPandr(null);
            }
          }
        }

        if (deliverableCrp.getCrpPandr() == null) {
          deliverableCrp.setCrpPandr(crpPandrManager.getCrpPandrById(new Long(3)));
        } else {
          deliverableCrp.setIpProgram(null);
        }
        deliverableCrp.setPhase(this.getActualPhase());
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
        List<DeliverableGenderLevel> fundingSourcesPrew = deliverablePrew.getDeliverableGenderLevels().stream()
          .filter(dp -> dp.isActive() && dp.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());


        for (DeliverableGenderLevel deliverableFundingSource : fundingSourcesPrew) {
          if (!deliverable.getGenderLevels().contains(deliverableFundingSource)) {
            deliverableGenderLevelManager.deleteDeliverableGenderLevel(deliverableFundingSource.getId());
          }
        }
      }

      for (DeliverableGenderLevel deliverableGenderLevel : deliverable.getGenderLevels()) {
        if (deliverableGenderLevel.getId() == null || deliverableGenderLevel.getId() == -1) {


          deliverableGenderLevel.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
          deliverableGenderLevel.setActive(true);
          deliverableGenderLevel.setCreatedBy(this.getCurrentUser());
          deliverableGenderLevel.setModifiedBy(this.getCurrentUser());
          deliverableGenderLevel.setModificationJustification("");
          deliverableGenderLevel.setActiveSince(new Date());
          deliverableGenderLevel.setPhase(this.getActualPhase());
          deliverableGenderLevelManager.saveDeliverableGenderLevel(deliverableGenderLevel);


        } else {
          DeliverableGenderLevel deliverableGenderLevelDB =
            deliverableGenderLevelManager.getDeliverableGenderLevelById(deliverableGenderLevel.getId());
          deliverableGenderLevelDB.setModifiedBy(this.getCurrentUser());
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

  /**
   * All we are doing here is setting the modification justification.
   */
  private void saveProjectAuditData() {
    // projectDB is a managed hibernate, project is in a detached state
    Project projectDB = projectManager.getProjectById(project.getId());
    projectDB.setActive(true);
    projectDB.setModifiedBy(this.getCurrentUser());
    // projectDB.setModificationJustification(this.getJustification());
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
      if (deliverable.getQualityCheck().getFileAssurance().getId() != null) {
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
      if (deliverable.getQualityCheck().getFileDictionary().getId() != null) {
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
      if (deliverable.getQualityCheck().getFileTools().getId() != null) {
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


    qualityCheck.setActive(true);
    qualityCheck.setActiveSince(new Date());
    qualityCheck.setModifiedBy(this.getCurrentUser());
    qualityCheck.setCreatedBy(this.getCurrentUser());
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

  public void setCrps(Map<String, String> crps) {
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

  public void setPrograms(Map<String, String> programs) {
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

  public void setRepositoryChannels(List<RepositoryChannel> repositoryChannels) {
    this.repositoryChannels = repositoryChannels;
  }

  public void setStatus(Map<String, String> status) {
    this.status = status;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  private Deliverable updateDeliverable() {
    // deliverableDb is in a managed state, deliverable is in a detached state.
    Deliverable deliverableBase = deliverableManager.getDeliverableById(deliverableID);
    DeliverableInfo deliverableInfoDb = deliverableBase.getDeliverableInfo(this.getActualPhase());

    deliverableInfoDb.setTitle(deliverable.getDeliverableInfo(this.getActualPhase()).getTitle());
    deliverableInfoDb.setDescription(deliverable.getDeliverableInfo(this.getActualPhase()).getDescription());

    deliverableInfoDb.setYear(deliverable.getDeliverableInfo(this.getActualPhase()).getYear());

    if (deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear() != null) {
      deliverableInfoDb.setNewExpectedYear(deliverable.getDeliverableInfo(this.getActualPhase()).getNewExpectedYear());
    }

    if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingCapacity() == null) {
      deliverableInfoDb.setCrossCuttingCapacity(false);
    } else {
      deliverableInfoDb.setCrossCuttingCapacity(true);
    }
    if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingNa() == null) {
      deliverableInfoDb.setCrossCuttingNa(false);
    } else {
      deliverableInfoDb.setCrossCuttingNa(true);
    }
    if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingGender() == null) {
      deliverableInfoDb.setCrossCuttingGender(false);
    } else {
      deliverableInfoDb.setCrossCuttingGender(true);
    }
    if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrossCuttingYouth() == null) {
      deliverableInfoDb.setCrossCuttingYouth(false);
    } else {
      deliverableInfoDb.setCrossCuttingYouth(true);
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


    deliverableInfoDb
      .setStatusDescription(deliverable.getDeliverableInfo(this.getActualPhase()).getStatusDescription());


    deliverableInfoDb.setModifiedBy(this.getCurrentUser());
    deliverableInfoDb.setModificationJustification(this.getJustification());
    deliverableBase.setDeliverableInfo(deliverableInfoDb);
    return deliverableBase;
  }

  /**
   * This could be merged into a common mapping class.
   * 
   * @param deliverablePrew
   */
  private void updateDeliverableInPlanningPhase(Deliverable deliverablePrew) {
    if (this.isPlanningActive()) {
      if (deliverable.getDeliverableInfo(this.getActualPhase()).getCrpClusterKeyOutput() != null
        && deliverable.getDeliverableInfo(this.getActualPhase()).getCrpClusterKeyOutput().getId() != null
        && deliverable.getDeliverableInfo(this.getActualPhase()).getCrpClusterKeyOutput().getId().longValue() != -1) {
        CrpClusterKeyOutput keyOutput = crpClusterKeyOutputManager.getCrpClusterKeyOutputById(
          deliverable.getDeliverableInfo(this.getActualPhase()).getCrpClusterKeyOutput().getId());

        deliverablePrew.getDeliverableInfo(this.getActualPhase()).setCrpClusterKeyOutput(keyOutput);
      } else {
        deliverablePrew.getDeliverableInfo(this.getActualPhase()).setCrpClusterKeyOutput(null);
      }

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
            deliverableFundingSource.setActive(true);
            deliverableFundingSource.setCreatedBy(this.getCurrentUser());
            deliverableFundingSource.setModifiedBy(this.getCurrentUser());
            deliverableFundingSource.setModificationJustification("");
            deliverableFundingSource.setActiveSince(new Date());
            deliverableFundingSource.setPhase(this.getActualPhase());
            deliverableFundingSourceManager.saveDeliverableFundingSource(deliverableFundingSource);


          }
        }
      }
    }
  }

  /**
   * Updates the deliverable when in reporting phase. This could be all moved to a more general mapping class.
   * 
   * @param deliverableDb - is in a managed hibernate state.
   */
  private void updateDeliverableInReportingPhase(Deliverable deliverableDb) {
    if (this.isReportingActive()) {

      if (deliverable.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense() != null) {
        deliverableDb.getDeliverableInfo(this.getActualPhase())
          .setAdoptedLicense(deliverable.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense());
        if (deliverable.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense().booleanValue()) {
          deliverableDb.getDeliverableInfo(this.getActualPhase())
            .setLicense(deliverable.getDeliverableInfo(this.getActualPhase()).getLicense());
          if (deliverable.getDeliverableInfo(this.getActualPhase()).getLicense() != null) {
            if (deliverable.getDeliverableInfo(this.getActualPhase()).getLicense()
              .equals(LicensesTypeEnum.OTHER.getValue())) {
              deliverableDb.getDeliverableInfo(this.getActualPhase())
                .setOtherLicense(deliverable.getDeliverableInfo(this.getActualPhase()).getOtherLicense());
              deliverableDb.getDeliverableInfo(this.getActualPhase())
                .setAllowModifications(deliverable.getDeliverableInfo(this.getActualPhase()).getAllowModifications());
            } else {
              deliverableDb.getDeliverableInfo(this.getActualPhase()).setOtherLicense(null);
              deliverableDb.getDeliverableInfo(this.getActualPhase()).setAllowModifications(null);
            }
          }
          deliverableDb.getDeliverableInfo(this.getActualPhase())
            .setAdoptedLicense(deliverable.getDeliverableInfo(this.getActualPhase()).getAdoptedLicense());
        } else {

          deliverableDb.getDeliverableInfo(this.getActualPhase()).setLicense(null);
          deliverableDb.getDeliverableInfo(this.getActualPhase()).setOtherLicense(null);
          deliverableDb.getDeliverableInfo(this.getActualPhase()).setAllowModifications(null);
        }
      } else {
        deliverableDb.getDeliverableInfo(this.getActualPhase()).setLicense(null);
        deliverableDb.getDeliverableInfo(this.getActualPhase()).setOtherLicense(null);
        deliverableDb.getDeliverableInfo(this.getActualPhase()).setAllowModifications(null);
      }


    }
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
            this.createAndSaveNewDeliverablePartnership(deliverablePartnershipOther,
              DeliverablePartnershipTypeEnum.OTHER.getValue());
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
