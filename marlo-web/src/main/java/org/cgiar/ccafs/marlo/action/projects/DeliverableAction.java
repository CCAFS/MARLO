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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpPandrManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableCrpManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDataSharingFileManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDisseminationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableFundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableGenderLevelManager;
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
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.MetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerDivisionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.model.ChannelEnum;
import org.cgiar.ccafs.marlo.data.model.Crp;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableAction extends BaseAction {

  private static final long serialVersionUID = -4474372683580321612L;

  private final Logger logger = LoggerFactory.getLogger(DeliverableAction.class);


  private List<DeliverableQualityAnswer> answers;

  private AuditLogManager auditLogManager;


  private Map<String, String> channels;

  private CrpClusterKeyOutputManager crpClusterKeyOutputManager;


  private CrpManager crpManager;


  private CrpProgramOutcomeManager crpProgramOutcomeManager;


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

  private HistoryComparator historyComparator;

  private DeliverableMetadataElementManager deliverableMetadataElementManager;

  private DeliverablePartnershipManager deliverablePartnershipManager;
  private InstitutionManager institutionManager;

  private DeliverableQualityAnswerManager deliverableQualityAnswerManager;


  private DeliverableQualityCheckManager deliverableQualityCheckManager;


  private DeliverableDisseminationManager deliverableDisseminationManager;

  private List<DeliverableType> deliverableSubTypes;


  // Managers
  private DeliverableTypeManager deliverableTypeManager;

  private List<DeliverableType> deliverableTypeParent;


  private DeliverableValidator deliverableValidator;

  private FileDBManager fileDBManager;

  private CrpProgramManager crpProgramManager;

  private GenderTypeManager genderTypeManager;

  private IpProgramManager ipProgramManager;

  private FundingSourceManager fundingSourceManager;

  private List<FundingSource> fundingSources;


  private List<GenderType> genderLevels;

  private List<CrpClusterKeyOutput> keyOutputs;

  private Crp loggedCrp;
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


  private List<PartnerDivision> divisions;

  @Inject
  public DeliverableAction(APConfig config, DeliverableTypeManager deliverableTypeManager,
    DeliverableMetadataElementManager deliverableMetadataElementManager, DeliverableManager deliverableManager,
    CrpManager crpManager, ProjectManager projectManager, ProjectPartnerPersonManager projectPartnerPersonManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, CrpClusterKeyOutputManager crpClusterKeyOutputManager,
    DeliverablePartnershipManager deliverablePartnershipManager, AuditLogManager auditLogManager,
    DeliverableValidator deliverableValidator, ProjectPartnerManager projectPartnerManager,
    FundingSourceManager fundingSourceManager, DeliverableFundingSourceManager deliverableFundingSourceManager,
    DeliverableGenderLevelManager deliverableGenderLevelManager,
    DeliverableQualityCheckManager deliverableQualityCheckManager, DeliverableCrpManager deliverableCrpManager,
    DeliverableQualityAnswerManager deliverableQualityAnswerManager, CrpProgramManager crpProgramManager,
    DeliverableDataSharingFileManager deliverableDataSharingFileManager, FileDBManager fileDBManager,
    DeliverableUserManager deliverableUserManager, GenderTypeManager genderTypeManager,
    HistoryComparator historyComparator, DeliverablePublicationMetadataManager deliverablePublicationMetadataManager,
    InstitutionManager institutionManager, MetadataElementManager metadataElementManager,
    DeliverableDisseminationManager deliverableDisseminationManager, CrpPandrManager crpPandrManager,
    IpProgramManager ipProgramManager, PartnerDivisionManager partnerDivisionManager) {
    super(config);
    this.deliverableManager = deliverableManager;
    this.deliverableTypeManager = deliverableTypeManager;
    this.crpManager = crpManager;
    this.historyComparator = historyComparator;
    this.deliverableUserManager = deliverableUserManager;
    this.crpProgramManager = crpProgramManager;
    this.projectManager = projectManager;
    this.institutionManager = institutionManager;
    this.deliverableCrpManager = deliverableCrpManager;
    this.deliverablePublicationMetadataManager = deliverablePublicationMetadataManager;
    this.projectPartnerPersonManager = projectPartnerPersonManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
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

  /**
   * Delete Deliverable Gender Levels if there is no cross cutting gender component.
   * 
   * @param deliverablePrew
   */
  private void deleteDeliverableGenderLevels(Deliverable deliverablePrew) {
    if (!deliverablePrew.getCrossCuttingGender().booleanValue()) {
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
    String composedClassName = deliverable.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = deliverable.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public Map<String, String> getChannels() {
    return channels;
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

  /**
   * Get the DeliverablePartnership from the submitted form.
   * 
   * @return
   */
  private DeliverablePartnership getDeliverablePartnership(Deliverable deliverablePrew) {
    DeliverablePartnership partnershipResponsible = null;

    if (deliverablePrew.getDeliverablePartnerships() != null
      && deliverablePrew.getDeliverablePartnerships().size() > 0) {

      try {
        partnershipResponsible = deliverablePrew.getDeliverablePartnerships().stream()
          .filter(
            dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
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


  public Crp getLoggedCrp() {
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

  public List<ProjectPartner> getSelectedPartners() {
    Set<ProjectPartner> deliverablePartnerPersonsSet = new HashSet<>();
    List<ProjectPartner> deliverablePartnerPersons = new ArrayList<>();

    List<DeliverablePartnership> deliverablePartnerships =
      deliverablePartnershipManager.findForDeliverableIdAndPartnerTypeOther(deliverableID);


    // List<DeliverablePartnership> deliverablePartnerships =
    // deliverableManager.getDeliverableById(deliverableID).getDeliverablePartnerships().stream()
    // .filter(c -> c.isActive() && c.getPartnerType().equals("Other")).collect(Collectors.toList());

    for (DeliverablePartnership deliverablePartnership : deliverablePartnerships) {
      deliverablePartnerPersonsSet.add(deliverablePartnership.getProjectPartnerPerson().getProjectPartner());
    }

    deliverablePartnerPersons.addAll(deliverablePartnerPersonsSet);
    return deliverablePartnerPersons;

  }

  public List<Long> getSelectedPersons(long partnerID) {

    List<ProjectPartnerPerson> deliverablePartnerPersons =
      projectPartnerPersonManager.findAllForOtherPartnerTypeWithDeliverableIdAndPartnerId(deliverableID, partnerID);

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
  public Boolean isDeliverableNew(long deliverableID) {

    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);

    SimpleDateFormat dateFormat = new SimpleDateFormat(APConstants.DATE_FORMAT);

    if (this.isReportingActive()) {

      try {
        Date reportingDate = dateFormat.parse(this.getSession().get(APConstants.CRP_OPEN_REPORTING_DATE).toString());
        if (deliverable.getCreateDate().compareTo(reportingDate) >= 0) {
          return true;
        } else {
          return false;
        }

      } catch (ParseException e) {
        e.printStackTrace();
        return false;
      }

    } else {
      try {
        Date reportingDate = dateFormat.parse(this.getSession().get(APConstants.CRP_OPEN_PLANNING_DATE).toString());
        if (deliverable.getCreateDate().compareTo(reportingDate) >= 0) {
          return true;
        } else {
          return false;
        }

      } catch (ParseException e) {
        e.printStackTrace();
        return false;
      }

    }
  }


  public Boolean isDeliverabletNew(long deliverableID) {

    Deliverable deliverable = deliverableManager.getDeliverableById(deliverableID);
    SimpleDateFormat dateFormat = new SimpleDateFormat(APConstants.DATE_FORMAT);
    if (this.isReportingActive()) {

      try {
        Date reportingDate = dateFormat.parse(this.getSession().get(APConstants.CRP_OPEN_REPORTING_DATE).toString());
        if (deliverable.getCreateDate().compareTo(reportingDate) >= 0) {
          return true;
        } else {
          return false;
        }

      } catch (ParseException e) {
        e.printStackTrace();
        return false;
      }

    } else {
      try {
        Date reportingDate = dateFormat.parse(this.getSession().get(APConstants.CRP_OPEN_PLANNING_DATE).toString());
        if (deliverable.getCreateDate().compareTo(reportingDate) >= 0) {
          return true;
        } else {
          return false;
        }

      } catch (ParseException e) {
        e.printStackTrace();
        return false;
      }

    }
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

  public List<DeliverablePartnership> otherPartners() {
    try {
      List<DeliverablePartnership> list = deliverable.getDeliverablePartnerships().stream()
        .filter(dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.OTHER.getValue()))
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
          DeliverablePartnership partnershipOth = new DeliverablePartnership();

          partnershipOth.setDeliverable(deliverable);
          partnershipOth.setProjectPartnerPerson(partnerPersonDb);
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

  @Override
  public void prepare() throws Exception {


    // Get current CRP
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

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

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        deliverable = (Deliverable) autoSaveReader.readFromJson(jReader);
        if (metadataElementManager.findAll() != null) {
          deliverable.setMetadata(new ArrayList<>(metadataElementManager.findAll()));
        }
        Deliverable deliverableDb = deliverableManager.getDeliverableById(deliverable.getId());
        deliverable.setProject(deliverableDb.getProject());
        project.setProjectEditLeader(deliverableDb.getProject().isProjectEditLeader());
        project.setProjectLocations(deliverableDb.getProject().getProjectLocations());
        reader.close();


        if (deliverable.getNewExpectedYear() == null) {
          deliverable.setNewExpectedYear(deliverableDb.getNewExpectedYear());
        }
        deliverable.setResponsiblePartner(this.responsiblePartnerAutoSave());
        deliverable.setOtherPartners(this.otherPartnersAutoSave());
        if (deliverable.getFundingSources() != null) {
          for (DeliverableFundingSource fundingSource : deliverable.getFundingSources()) {
            if (fundingSource != null && fundingSource.getFundingSource() != null) {
              fundingSource
                .setFundingSource(fundingSourceManager.getFundingSourceById(fundingSource.getFundingSource().getId()));
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
              FileDB db = fileDBManager.getFileDBById(deliverable.getQualityCheck().getFileAssurance().getId());
              deliverable.getQualityCheck().setFileAssurance(db);
            }
          }

          if (deliverable.getQualityCheck().getFileDictionary() != null) {
            if (deliverable.getQualityCheck().getFileDictionary().getId() != null) {
              FileDB db = fileDBManager.getFileDBById(deliverable.getQualityCheck().getFileDictionary().getId());
              deliverable.getQualityCheck().setFileDictionary(db);
            }
          }

          if (deliverable.getQualityCheck().getFileTools() != null) {
            if (deliverable.getQualityCheck().getFileTools().getId() != null) {
              FileDB db = fileDBManager.getFileDBById(deliverable.getQualityCheck().getFileTools().getId());
              deliverable.getQualityCheck().setFileTools(db);
            }
          }
        }


        this.setDraft(true);
      } else {
        deliverable.setResponsiblePartner(this.responsiblePartner());
        deliverable.setOtherPartners(this.otherPartners());
        deliverable.setFundingSources(
          deliverable.getDeliverableFundingSources().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        deliverable.setGenderLevels(
          deliverable.getDeliverableGenderLevels().stream().filter(c -> c.isActive()).collect(Collectors.toList()));


        if (this.isReportingActive()) {

          DeliverableQualityCheck deliverableQualityCheck =
            deliverableQualityCheckManager.getDeliverableQualityCheckByDeliverable(deliverable.getId());
          deliverable.setQualityCheck(deliverableQualityCheck);

          if (deliverable.getDeliverableMetadataElements() != null) {
            deliverable.setMetadataElements(new ArrayList<>(deliverable.getDeliverableMetadataElements()));
          }

          if (deliverable.getDeliverableDisseminations() != null) {
            deliverable.setDisseminations(new ArrayList<>(deliverable.getDeliverableDisseminations()));
            if (deliverable.getDeliverableDisseminations().size() > 0) {
              deliverable.setDissemination(deliverable.getDisseminations().get(0));
            } else {
              deliverable.setDissemination(new DeliverableDissemination());
            }

          }

          if (deliverable.getDeliverableDataSharingFiles() != null) {
            deliverable.setDataSharingFiles(new ArrayList<>(deliverable.getDeliverableDataSharingFiles()));
          }

          if (deliverable.getDeliverablePublicationMetadatas() != null) {
            deliverable.setPublicationMetadatas(new ArrayList<>(deliverable.getDeliverablePublicationMetadatas()));
          }
          if (!deliverable.getPublicationMetadatas().isEmpty()) {
            deliverable.setPublication(deliverable.getPublicationMetadatas().get(0));
          }

          if (deliverable.getDeliverableDataSharings() != null) {
            deliverable.setDataSharing(new ArrayList<>(deliverable.getDeliverableDataSharings()));
          }


          deliverable.setUsers(deliverable.getDeliverableUsers().stream().collect(Collectors.toList()));
          deliverable.setCrps(deliverable.getDeliverableCrps().stream().collect(Collectors.toList()));
          deliverable.setFiles(new ArrayList<>());
          for (DeliverableDataSharingFile dataSharingFile : deliverable.getDeliverableDataSharingFiles()) {

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
        if (deliverable.getStatus() != null) {
          if (deliverable.getStatus().intValue() != Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())) {
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
          if (deliverable.getYear() < this.getReportingYear()) {
            status.remove(ProjectStatusEnum.Ongoing.getStatusId());
          }
        }
      } else {
        if (deliverable.getYear() <= this.getReportingYear()) {
          status.remove(ProjectStatusEnum.Ongoing.getStatusId());
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
        .filter(dt -> dt.getDeliverableType() == null && dt.getCrp() == null).collect(Collectors.toList()));

      deliverableTypeParent.addAll(new ArrayList<>(
        deliverableTypeManager.findAll().stream().filter(dt -> dt.getDeliverableType() == null && dt.getCrp() != null
          && dt.getCrp().getId().longValue() == loggedCrp.getId().longValue()).collect(Collectors.toList())));

      if (deliverable.getDeliverableType() != null) {
        Long deliverableTypeParentId = deliverable.getDeliverableType().getDeliverableType().getId();

        deliverableSubTypes = new ArrayList<>(deliverableTypeManager.findAll().stream()
          .filter(dt -> dt.getDeliverableType() != null && dt.getDeliverableType().getId() == deliverableTypeParentId)
          .collect(Collectors.toList()));
      }

      if (project.getProjectOutcomes() != null) {

        keyOutputs = new ArrayList<>();

        for (ProjectOutcome projectOutcome : project.getProjectOutcomes().stream().filter(ca -> ca.isActive())
          .collect(Collectors.toList())) {

          for (CrpClusterKeyOutputOutcome keyOutcome : projectOutcome.getCrpProgramOutcome()
            .getCrpClusterKeyOutputOutcomes().stream().filter(ko -> ko.isActive()).collect(Collectors.toList())) {
            keyOutputs.add(keyOutcome.getCrpClusterKeyOutput());
          }

        }
      }


      // List<ProjectPartner> projectPartnersWithActiveProjectPartnerPersons = projectPartnerManager.findAll().stream()
      // .filter(pp -> pp.isActive() && (pp.getProject().getId() == projectID
      // && !pp.getProjectPartnerPersons().stream().filter(c -> c.isActive()).collect(Collectors.toList()).isEmpty()))
      // .collect(Collectors.toList());

      partners = projectPartnerManager.getProjectPartnersForProjectWithActiveProjectPartnerPersons(projectID);

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
            this.fundingSources.add(fundingSource);
          }

        }
      }
      Set<FundingSource> hs = new HashSet();
      hs.addAll(this.fundingSources);
      this.fundingSources.clear();
      this.fundingSources.addAll(hs);
      this.fundingSources.sort((o1, o2) -> {
        if (o1.getBudgetType() != null && o2.getBudgetType() != null && o1.getTitle() != null
          && o2.getTitle() != null) {

          int cmp = o1.getBudgetType().getId().compareTo(o2.getBudgetType().getId());
          if (cmp == 0) {
            cmp = o1.getTitle().compareTo(o2.getTitle());
          }

          return cmp;
        }
        return 0;
      });

    }
    if (deliverable.getFiles() != null) {
      deliverable.getFiles().sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
    }

    channels = new HashMap<>();


    for (ChannelEnum channel : ChannelEnum.values()) {
      channels.put(channel.getId(), channel.getDesc());
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

      deliverable.setCrossCuttingGender(null);
      deliverable.setCrossCuttingCapacity(null);
      deliverable.setCrossCuttingNa(null);
      deliverable.setCrossCuttingYouth(null);

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
    }

    try {
      indexTab = Integer.parseInt(this.getSession().get("indexTab").toString());
      this.getSession().remove("indexTab");
    } catch (Exception e) {
      indexTab = 0;
    }
  }


  public void removeDeliverablePartnerships(Deliverable deliverablePrew) {
    if (deliverablePrew.getDeliverablePartnerships() != null
      && deliverablePrew.getDeliverablePartnerships().size() > 0) {
      List<DeliverablePartnership> partnerShipsPrew = deliverablePrew.getDeliverablePartnerships().stream()
        .filter(dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.OTHER.getValue()))
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


  private DeliverablePartnership responsiblePartner() {
    try {
      DeliverablePartnership partnership = deliverable.getDeliverablePartnerships().stream()
        .filter(
          dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
        .collect(Collectors.toList()).get(0);
      return partnership;
    } catch (Exception e) {
      return null;
    }
  }

  private DeliverablePartnership responsiblePartnerAutoSave() {
    try {
      ProjectPartnerPerson partnerPerson = projectPartnerPersonManager
        .getProjectPartnerPersonById(deliverable.getResponsiblePartner().getProjectPartnerPerson().getId());
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
      partnership.setProjectPartnerPerson(partnerPerson);
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

      // This gets a DeliverablePartnership entity in managed state.
      DeliverablePartnership partnershipResponsible = this.getDeliverablePartnership(deliverableManagedState);
      // This is a managed entity.
      ProjectPartnerPerson partnerPerson = this.getPartnerPerson();

      /**
       * This seems like an unnecessary duplicate call as our deliverable already has an id (we only update in this
       * action)
       */
      // deliverablePrew = deliverableManager.saveDeliverable(deliverablePrew);

      this.saveDeliverablePartnership(deliverableManagedState, partnershipResponsible, partnerPerson);

      this.removeDeliverablePartnerships(deliverableManagedState);

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


      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_DELIVERABLE_PARTNERSHIPS_RELATION);
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

      deliverableManagedState =
        deliverableManager.saveDeliverable(deliverableManagedState, this.getActionName(), relationsName);
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
    for (DeliverableCrp deliverableCrp : deliverableDB.getDeliverableCrps()) {
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
        deliverableDataSharingFileManager.saveDeliverableDataSharingFile(dataSharingFile);
      }

    }
  }

  private void saveDeliverableGenderLevels(Deliverable deliverablePrew) {
    if (deliverable.getGenderLevels() != null) {
      if (deliverablePrew.getDeliverableGenderLevels() != null
        && deliverablePrew.getDeliverableGenderLevels().size() > 0) {
        List<DeliverableGenderLevel> fundingSourcesPrew = deliverablePrew.getDeliverableGenderLevels().stream()
          .filter(dp -> dp.isActive()).collect(Collectors.toList());


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

  /**
   * @param deliverableDb - managed entity.
   * @param partnershipResponsible - managed entity
   * @param partnerPerson - maanged entity
   */
  private void saveDeliverablePartnership(Deliverable deliverableDb, DeliverablePartnership partnershipResponsible,
    ProjectPartnerPerson partnerPerson) {
    if (partnershipResponsible != null && partnerPerson != null) {
      // id of managed entity
      Long partnerId1 = partnershipResponsible.getProjectPartnerPerson().getId();
      // id of detached entity.
      Long partnerId2 = partnerPerson.getId();

      // Check to see if we have different partners
      if (partnerId1.longValue() != partnerId2.longValue()) {

        // Delete the one in the database.
        deliverablePartnershipManager.deleteDeliverablePartnership(partnershipResponsible.getId());

        // Create a new one.
        DeliverablePartnership partnership = new DeliverablePartnership();
        partnership.setProjectPartnerPerson(partnerPerson);
        partnership.setPartnerType(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue());
        partnership.setDeliverable(deliverableDb);
        partnership.setActive(true);
        partnership.setCreatedBy(this.getCurrentUser());
        partnership.setModifiedBy(this.getCurrentUser());
        partnership.setModificationJustification("");
        partnership.setActiveSince(new Date());


        if (deliverable.getResponsiblePartner().getPartnerDivision() != null
          && deliverable.getResponsiblePartner().getPartnerDivision().getId().longValue() != -1) {
          try {
            PartnerDivision division = partnerDivisionManager
              .getPartnerDivisionById(deliverable.getResponsiblePartner().getPartnerDivision().getId());
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

        deliverablePartnershipManager.saveDeliverablePartnership(partnership);
      } else {

        if (deliverable.getResponsiblePartner() != null && deliverable.getResponsiblePartner().getId() != null) {
          DeliverablePartnership partnershipDB =
            deliverablePartnershipManager.getDeliverablePartnershipById(deliverable.getResponsiblePartner().getId());

          if (deliverable.getResponsiblePartner().getPartnerDivision() != null
            && deliverable.getResponsiblePartner().getPartnerDivision().getId() != null
            && deliverable.getResponsiblePartner().getPartnerDivision().getId().longValue() != -1) {
            try {
              PartnerDivision division = partnerDivisionManager
                .getPartnerDivisionById(deliverable.getResponsiblePartner().getPartnerDivision().getId());
              partnershipDB.setPartnerDivision(division);
            } catch (Exception e) {
              // NEVER EVER JUST SWALLOW UNCHECKED EXCEPTIONS! Logging this now.
              logger.error("unable to set the PartnerDivision for partnership : " + partnershipDB, e);
              /**
               * Generally when an unchecked exception occurs you don't try and continue processing, something is
               * wrong and you need to deal with it. We need to review this logic at some point in time, as the user
               * will not be aware that the partnerDivision has not been saved and that is confusing.
               */
              partnershipDB.setPartnerDivision(null);
            }
          } else {
            partnershipDB.setPartnerDivision(null);
          }
          deliverablePartnershipManager.saveDeliverablePartnership(partnershipDB);
        }


      }
    } else if (partnershipResponsible == null && partnerPerson != null) {

      DeliverablePartnership partnership = new DeliverablePartnership();
      partnership.setProjectPartnerPerson(partnerPerson);
      partnership.setPartnerType(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue());
      partnership.setDeliverable(deliverableDb);
      partnership.setActive(true);
      partnership.setCreatedBy(this.getCurrentUser());
      partnership.setModifiedBy(this.getCurrentUser());
      partnership.setModificationJustification("");
      partnership.setActiveSince(new Date());


      if (deliverable.getResponsiblePartner().getPartnerDivision() != null
        && deliverable.getResponsiblePartner().getPartnerDivision().getId().longValue() != -1) {
        try {
          PartnerDivision division = partnerDivisionManager
            .getPartnerDivisionById(deliverable.getResponsiblePartner().getPartnerDivision().getId());
          partnership.setPartnerDivision(division);
        } catch (Exception e) {
          partnership.setPartnerDivision(null);
        }
      } else {
        partnership.setPartnerDivision(null);
      }

      deliverablePartnershipManager.saveDeliverablePartnership(partnership);
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


      deliverableDisseminationManager.saveDeliverableDissemination(dissemination);

    }


  }

  public void saveMetadata() {
    if (deliverable.getMetadataElements() != null) {
      for (DeliverableMetadataElement deliverableMetadataElement : deliverable.getMetadataElements()) {
        if (deliverableMetadataElement != null && deliverableMetadataElement.getMetadataElement() != null) {
          deliverableMetadataElement.setDeliverable(deliverable);
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
    projectDB.setModificationJustification(this.getJustification());
    // No need to call save as hibernate will detect the changes and auto flush.
  }

  public void savePublicationMetadata() {
    if (deliverable.getPublication() != null) {
      deliverable.getPublication().setDeliverable(deliverable);
      if (deliverable.getPublication().getId() != null && deliverable.getPublication().getId().intValue() == -1) {
        deliverable.getPublication().setId(null);
      }
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
        FileDB fileDb = fileDBManager.getFileDBById(deliverable.getQualityCheck().getFileAssurance().getId());
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
        FileDB fileDb = fileDBManager.getFileDBById(deliverable.getQualityCheck().getFileDictionary().getId());
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
        FileDB fileDb = fileDBManager.getFileDBById(deliverable.getQualityCheck().getFileTools().getId());
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

    deliverableQualityCheckManager.saveDeliverableQualityCheck(qualityCheck);

  }

  public void saveUsers() {
    if (deliverable.getUsers() == null) {

      deliverable.setUsers(new ArrayList<>());
    }
    Deliverable deliverableDB = deliverableManager.getDeliverableById(deliverableID);
    for (DeliverableUser deliverableUser : deliverableDB.getDeliverableUsers()) {
      if (!deliverable.getUsers().contains(deliverableUser)) {
        deliverableUserManager.deleteDeliverableUser(deliverableUser.getId());
      }
    }

    for (DeliverableUser deliverableUser : deliverable.getUsers()) {

      if (deliverableUser.getId() == null || deliverableUser.getId().intValue() == -1) {
        deliverableUser.setId(null);
        deliverableUser.setDeliverable(deliverable);
        deliverableUserManager.saveDeliverableUser(deliverableUser);
      }
    }
  }

  public void setAnswers(List<DeliverableQualityAnswer> answers) {
    this.answers = answers;
  }


  public void setChannels(Map<String, String> channels) {
    this.channels = channels;
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

  public void setLoggedCrp(Crp loggedCrp) {
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

  public void setStatus(Map<String, String> status) {
    this.status = status;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  private Deliverable updateDeliverable() {
    // deliverableDb is in a managed state, deliverable is in a detached state.
    Deliverable deliverableDb = deliverableManager.getDeliverableById(deliverableID);

    deliverableDb.setTitle(deliverable.getTitle());
    deliverableDb.setDescription(deliverable.getDescription());

    deliverableDb.setYear(deliverable.getYear());

    if (deliverable.getNewExpectedYear() != null) {
      deliverableDb.setNewExpectedYear(deliverable.getNewExpectedYear());
    }

    if (deliverable.getCrossCuttingCapacity() == null) {
      deliverableDb.setCrossCuttingCapacity(false);
    } else {
      deliverableDb.setCrossCuttingCapacity(true);
    }
    if (deliverable.getCrossCuttingNa() == null) {
      deliverableDb.setCrossCuttingNa(false);
    } else {
      deliverableDb.setCrossCuttingNa(true);
    }
    if (deliverable.getCrossCuttingGender() == null) {
      deliverableDb.setCrossCuttingGender(false);
    } else {
      deliverableDb.setCrossCuttingGender(true);
    }
    if (deliverable.getCrossCuttingYouth() == null) {
      deliverableDb.setCrossCuttingYouth(false);
    } else {
      deliverableDb.setCrossCuttingYouth(true);
    }

    if (deliverable.getStatus() != null) {
      deliverableDb.setStatus(deliverable.getStatus());
    }

    DeliverableType deliverableType =
      deliverableTypeManager.getDeliverableTypeById(deliverable.getDeliverableType().getId());

    deliverableDb.setDeliverableType(deliverableType);

    deliverableDb.setStatusDescription(deliverable.getStatusDescription());

    deliverableDb.setActiveSince(new Date());
    deliverableDb.setModifiedBy(this.getCurrentUser());
    deliverableDb.setModificationJustification(this.getJustification());

    return deliverableDb;
  }

  /**
   * This could be merged into a common mapping class.
   * 
   * @param deliverablePrew
   */
  private void updateDeliverableInPlanningPhase(Deliverable deliverablePrew) {
    if (this.isPlanningActive()) {
      if (deliverable.getCrpClusterKeyOutput() != null) {
        CrpClusterKeyOutput keyOutput =
          crpClusterKeyOutputManager.getCrpClusterKeyOutputById(deliverable.getCrpClusterKeyOutput().getId());

        deliverablePrew.setCrpClusterKeyOutput(keyOutput);
      }

      if (deliverable.getFundingSources() != null) {
        if (deliverablePrew.getDeliverableFundingSources() != null
          && deliverablePrew.getDeliverableFundingSources().size() > 0) {
          List<DeliverableFundingSource> fundingSourcesPrew = deliverablePrew.getDeliverableFundingSources().stream()
            .filter(dp -> dp.isActive()).collect(Collectors.toList());


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

      if (deliverable.getAdoptedLicense() != null) {
        deliverableDb.setAdoptedLicense(deliverable.getAdoptedLicense());
        if (deliverable.getAdoptedLicense().booleanValue()) {
          deliverableDb.setLicense(deliverable.getLicense());
          if (deliverable.getLicense() != null) {
            if (deliverable.getLicense().equals(LicensesTypeEnum.OTHER.getValue())) {
              deliverableDb.setOtherLicense(deliverable.getOtherLicense());
              deliverableDb.setAllowModifications(deliverable.getAllowModifications());
            } else {
              deliverableDb.setOtherLicense(null);
              deliverableDb.setAllowModifications(null);
            }
          }
          deliverableDb.setAdoptedLicense(deliverable.getAdoptedLicense());
        } else {

          deliverableDb.setLicense(null);
          deliverableDb.setOtherLicense(null);
          deliverableDb.setAllowModifications(null);
        }
      } else {
        deliverableDb.setLicense(null);
        deliverableDb.setOtherLicense(null);
        deliverableDb.setAllowModifications(null);
      }


    }
  }

  /**
   * This is for updating the list of other Deliverable Partnerships.
   * This method needs refactoring as it is big and confusing.
   */
  private void updateOtherDeliverablePartnerships() {
    if (deliverable.getOtherPartners() != null) {
      // Iterate through the list of detached entities.
      for (DeliverablePartnership deliverablePartnership : deliverable.getOtherPartners()) {
        if (deliverablePartnership.getProjectPartnerPerson() != null) {

          if (deliverablePartnership.getId() == null
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

              if (deliverablePartnership.getPartnerDivision() != null
                && deliverablePartnership.getPartnerDivision().getId().longValue() != -1) {
                try {
                  PartnerDivision division =
                    partnerDivisionManager.getPartnerDivisionById(deliverablePartnership.getPartnerDivision().getId());
                  partnership.setPartnerDivision(division);
                } catch (Exception e) {
                  // NEVER EVER JUST SWALLOW UNCHECKED EXCEPTIONS! Logging this now.
                  logger.error("unable to set PartnerDivision with id : " + deliverablePartnership.getPartnerDivision()
                    + ", on partnership: " + partnership, e);
                  /**
                   * Generally when an unchecked exception occurs you don't try and continue processing, something is
                   * wrong and you need to deal with it. We need to review this logic at some point in time, as the user
                   * will not be aware that the partnerDivision has not been saved and that is confusing.
                   */
                  partnership.setPartnerDivision(null);
                }
              } else {
                // This means that a partnerDivision can be set to -1, if yes how so? Should something be doing that?
                partnership.setPartnerDivision(null);
              }

              deliverablePartnershipManager.saveDeliverablePartnership(partnership);

            } else {
              logger.debug("Unable to find projectPartnerPerson with id: "
                + deliverablePartnership.getProjectPartnerPerson().getId());
            }

            /**
             * This has met one of two conditions; the deliverablePartnership is existing or it
             * does not have a projectPartnerPerson.
             */
          } else {

            long partnerShipPrewId = 0;
            // This looks like a NPE waiting to happen.
            partnerShipPrewId = deliverablePartnershipManager
              .getDeliverablePartnershipById(deliverablePartnership.getId()).getProjectPartnerPerson().getId();


            long partnerShipId = deliverablePartnership.getProjectPartnerPerson().getId();
            // Check to see if this deliverablePartnership is different from what we already had.
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

                if (deliverablePartnership.getPartnerDivision() != null
                  && deliverablePartnership.getPartnerDivision().getId().longValue() != -1) {
                  try {
                    PartnerDivision division = partnerDivisionManager
                      .getPartnerDivisionById(deliverablePartnership.getPartnerDivision().getId());
                    partnershipNew.setPartnerDivision(division);
                  } catch (Exception e) {
                    // NEVER EVER JUST SWALLOW UNCHECKED EXCEPTIONS! Logging this now.
                    logger.error("unable to set PartnerDivision with id : "
                      + deliverablePartnership.getPartnerDivision() + ", on partnership: " + deliverablePartnership, e);
                    /**
                     * Generally when an unchecked exception occurs you don't try and continue processing, something is
                     * wrong and you need to deal with it. We need to review this logic at some point in time, as the
                     * user
                     * will not be aware that the partnerDivision has not been saved and that is confusing.
                     */
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

              ProjectPartnerPerson partnerPerson = projectPartnerPersonManager
                .getProjectPartnerPersonById(deliverablePartnership.getProjectPartnerPerson().getId());

              partnershipDB.setProjectPartnerPerson(partnerPerson);
              if (deliverablePartnership.getPartnerDivision() != null
                && deliverablePartnership.getPartnerDivision().getId().longValue() != -1) {
                try {
                  PartnerDivision division =
                    partnerDivisionManager.getPartnerDivisionById(deliverablePartnership.getPartnerDivision().getId());
                  partnershipDB.setPartnerDivision(division);
                } catch (Exception e) {
                  // NEVER EVER JUST SWALLOW UNCHECKED EXCEPTIONS! Logging this now.
                  logger.error("unable to set PartnerDivision with id : " + deliverablePartnership.getPartnerDivision()
                    + ", on partnership: " + partnershipDB, e);
                  /**
                   * Generally when an unchecked exception occurs you don't try and continue processing, something is
                   * wrong and you need to deal with it. We need to review this logic at some point in time, as the user
                   * will not be aware that the partnerDivision has not been saved and that is confusing.
                   */
                  partnershipDB.setPartnerDivision(null);
                }

              } else {
                partnershipDB.setPartnerDivision(null);
              }

              deliverablePartnershipManager.saveDeliverablePartnership(partnershipDB);

            }
          }
        } else {
          // This seems like something I am interested in.
          logger
            .debug("deliverablePartnership.getProjectPartnerPerson is null so not updating the deliverablePartnership");
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
