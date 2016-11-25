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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableFundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableGenderLevelManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutputOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
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
import org.cgiar.ccafs.marlo.validation.projects.DeliverableValidator;

import java.io.BufferedReader;
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

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableAction extends BaseAction {


  private static final long serialVersionUID = -4474372683580321612L;


  private Crp loggedCrp;

  private DeliverableValidator deliverableValidator;

  // Managers
  private DeliverableTypeManager deliverableTypeManager;


  private DeliverableManager deliverableManager;

  private ProjectManager projectManager;

  private FundingSourceManager fundingSourceManager;

  private DeliverablePartnershipManager deliverablePartnershipManager;

  private ProjectPartnerPersonManager projectPartnerPersonManager;


  private CrpProgramOutcomeManager crpProgramOutcomeManager;

  private CrpClusterKeyOutputManager crpClusterKeyOutputManager;

  private CrpManager crpManager;

  private long projectID;

  private long deliverableID;

  private List<DeliverableType> deliverableTypeParent;

  private List<DeliverableType> deliverableSubTypes;

  private List<ProjectOutcome> projectOutcome;

  private List<CrpClusterKeyOutput> keyOutputs;

  private List<ProjectPartnerPerson> partnerPersons;

  private List<FundingSource> fundingSources;


  private Project project;


  private Map<String, String> status;
  private Map<String, String> genderLevels;


  private Deliverable deliverable;


  private List<ProjectFocus> projectPrograms;

  private String transaction;

  private AuditLogManager auditLogManager;

  private DeliverableFundingSourceManager deliverableFundingSourceManager;

  private DeliverableGenderLevelManager deliverableGenderLevelManager;
  private ProjectPartnerManager projectPartnerManager;


  @Inject
  public DeliverableAction(APConfig config, DeliverableTypeManager deliverableTypeManager,
    DeliverableManager deliverableManager, CrpManager crpManager, ProjectManager projectManager,
    ProjectPartnerPersonManager projectPartnerPersonManager, CrpProgramOutcomeManager crpProgramOutcomeManager,
    CrpClusterKeyOutputManager crpClusterKeyOutputManager, DeliverablePartnershipManager deliverablePartnershipManager,
    AuditLogManager auditLogManager, DeliverableValidator deliverableValidator,
    ProjectPartnerManager projectPartnerManager, FundingSourceManager fundingSourceManager,
    DeliverableFundingSourceManager deliverableFundingSourceManager,
    DeliverableGenderLevelManager deliverableGenderLevelManager) {
    super(config);
    this.deliverableManager = deliverableManager;
    this.deliverableTypeManager = deliverableTypeManager;
    this.crpManager = crpManager;
    this.projectManager = projectManager;
    this.projectPartnerPersonManager = projectPartnerPersonManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.crpClusterKeyOutputManager = crpClusterKeyOutputManager;
    this.deliverablePartnershipManager = deliverablePartnershipManager;
    this.auditLogManager = auditLogManager;
    this.deliverableValidator = deliverableValidator;
    this.projectPartnerManager = projectPartnerManager;
    this.deliverableFundingSourceManager = deliverableFundingSourceManager;
    this.fundingSourceManager = fundingSourceManager;
    this.deliverableGenderLevelManager = deliverableGenderLevelManager;
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


  private Path getAutoSaveFilePath() {
    String composedClassName = deliverable.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = deliverable.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public Deliverable getDeliverable() {
    return deliverable;
  }

  public long getDeliverableID() {
    return deliverableID;
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

  public List<FundingSource> getFundingSources() {
    return fundingSources;
  }

  public Map<String, String> getGenderLevels() {
    return genderLevels;
  }

  public List<CrpClusterKeyOutput> getKeyOutputs() {
    return keyOutputs;
  }


  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public List<ProjectPartnerPerson> getPartnerPersons() {
    return partnerPersons;
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

  public List<DeliverablePartnership> otherPartners() {
    try {
      List<DeliverablePartnership> list = deliverable.getDeliverablePartnerships().stream()
        .filter(dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.OTHER.getValue()))
        .collect(Collectors.toList());
      return list;
    } catch (Exception e) {
      return null;
    }


  }

  public List<DeliverablePartnership> otherPartnersAutoSave() {
    try {
      List<DeliverablePartnership> list = new ArrayList<>();
      for (DeliverablePartnership partnership : deliverable.getOtherPartners()) {
        if (partnership.getId() == null || partnership.getId() == -1) {
          ProjectPartnerPerson partnerPerson =
            projectPartnerPersonManager.getProjectPartnerPersonById(partnership.getProjectPartnerPerson().getId());
          DeliverablePartnership partnershipOth = new DeliverablePartnership();

          partnershipOth.setDeliverable(deliverable);
          partnershipOth.setProjectPartnerPerson(partnerPerson);
          partnershipOth.setPartnerType(DeliverablePartnershipTypeEnum.OTHER.getValue());
          partnershipOth.setActive(true);
          list.add(partnershipOth);
        } else {
          list.add(deliverablePartnershipManager.getDeliverablePartnershipById(partnership.getId()));
        }
      }
      return list;
    } catch (Exception e) {
      return null;
    }

  }

  public void parnershipNewData() {
    if (deliverable.getOtherPartners() != null) {
      for (DeliverablePartnership deliverablePartnership : deliverable.getOtherPartners()) {
        if (deliverablePartnership.getId() == null || deliverablePartnership.getId() == -1) {

          ProjectPartnerPerson partnerPerson = projectPartnerPersonManager
            .getProjectPartnerPersonById(deliverablePartnership.getProjectPartnerPerson().getId());


          DeliverablePartnership partnership = new DeliverablePartnership();
          partnership.setProjectPartnerPerson(partnerPerson);
          partnership.setPartnerType(DeliverablePartnershipTypeEnum.OTHER.getValue());
          partnership.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
          partnership.setActive(true);
          partnership.setCreatedBy(this.getCurrentUser());
          partnership.setModifiedBy(this.getCurrentUser());
          partnership.setModificationJustification("");
          partnership.setActiveSince(new Date());

          deliverablePartnershipManager.saveDeliverablePartnership(partnership);


        } else {

          long partnerShipPrewId = deliverablePartnershipManager
            .getDeliverablePartnershipById(deliverablePartnership.getId()).getProjectPartnerPerson().getId();

          long partnerShipId = deliverablePartnership.getProjectPartnerPerson().getId();
          if (partnerShipPrewId != partnerShipId) {

            ProjectPartnerPerson partnerPerson = projectPartnerPersonManager
              .getProjectPartnerPersonById(deliverablePartnership.getProjectPartnerPerson().getId());

            deliverablePartnershipManager.deleteDeliverablePartnership(deliverablePartnership.getId());

            DeliverablePartnership partnershipNew = new DeliverablePartnership();
            partnershipNew.setProjectPartnerPerson(partnerPerson);
            partnershipNew.setPartnerType(DeliverablePartnershipTypeEnum.OTHER.getValue());
            partnershipNew.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
            partnershipNew.setActive(true);
            partnershipNew.setCreatedBy(this.getCurrentUser());
            partnershipNew.setModifiedBy(this.getCurrentUser());
            partnershipNew.setModificationJustification("");
            partnershipNew.setActiveSince(new Date());

            deliverablePartnershipManager.saveDeliverablePartnership(partnershipNew);

          }
        }
      }
    }
  }

  public void partnershipPreviousData(Deliverable deliverablePrew) {
    if (deliverablePrew.getDeliverablePartnerships() != null
      && deliverablePrew.getDeliverablePartnerships().size() > 0) {
      List<DeliverablePartnership> partnerShipsPrew = deliverablePrew.getDeliverablePartnerships().stream()
        .filter(dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.OTHER.getValue()))
        .collect(Collectors.toList());


      for (DeliverablePartnership deliverablePartnership : partnerShipsPrew) {
        if (!deliverable.getOtherPartners().contains(deliverablePartnership)) {
          deliverablePartnershipManager.deleteDeliverablePartnership(deliverablePartnership.getId());
        }
      }
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

    }


    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Deliverable history = (Deliverable) auditLogManager.getHistory(transaction);

      if (history != null) {
        deliverable = history;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
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
        Deliverable deliverableDb = deliverableManager.getDeliverableById(deliverable.getId());
        deliverable.setProject(deliverableDb.getProject());
        project.setProjectEditLeader(deliverableDb.getProject().isProjectEditLeader());
        project.setProjectLocations(deliverableDb.getProject().getProjectLocations());
        reader.close();

        deliverable.setResponsiblePartner(this.responsiblePartnerAutoSave());
        deliverable.setOtherPartners(this.otherPartnersAutoSave());
        for (DeliverableFundingSource fundingSource : deliverable.getFundingSources()) {
          if (fundingSource != null && fundingSource.getFundingSource() != null) {
            fundingSource
              .setFundingSource(fundingSourceManager.getFundingSourceById(fundingSource.getFundingSource().getId()));
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
        this.setDraft(false);
      }


      status = new HashMap<>();
      List<ProjectStatusEnum> list = Arrays.asList(ProjectStatusEnum.values());
      for (ProjectStatusEnum projectStatusEnum : list) {

        status.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
      }
      if (this.isPlanningActive()) {
        status.remove(ProjectStatusEnum.Complete.getStatusId());
        if (this.isDeliverableNew(deliverableID)) {
          status.remove(ProjectStatusEnum.Cancelled.getStatusId());
          status.remove(ProjectStatusEnum.Extended.getStatusId());

          status.remove(ProjectStatusEnum.Extended.getStatusId());
          status.remove(ProjectStatusEnum.Complete.getStatusId());
        } else {
          if (deliverable.getYear() >= this.getCurrentCycleYear()) {
            status.remove(ProjectStatusEnum.Cancelled.getStatusId());
            status.remove(ProjectStatusEnum.Extended.getStatusId());
          } else {
            status.remove(ProjectStatusEnum.Ongoing.getStatusId());
          }
        }
      }

      genderLevels = new HashMap<>();
      List<DeliverableGenderTypeEnum> listGenders = Arrays.asList(DeliverableGenderTypeEnum.values());
      for (DeliverableGenderTypeEnum projectStatusEnum : listGenders) {
        genderLevels.put(projectStatusEnum.getId() + "", projectStatusEnum.getValue());
      }

      deliverableTypeParent = new ArrayList<>(deliverableTypeManager.findAll().stream()
        .filter(dt -> dt.getDeliverableType() == null).collect(Collectors.toList()));


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
      partnerPersons = new ArrayList<>();
      for (ProjectPartner partner : projectPartnerManager.findAll().stream()
        .filter(pp -> pp.isActive() && pp.getProject().getId() == projectID).collect(Collectors.toList())) {

        for (ProjectPartnerPerson partnerPerson : partner.getProjectPartnerPersons().stream()
          .filter(ppa -> ppa.isActive()).collect(Collectors.toList())) {

          partnerPersons.add(partnerPerson);
        }
      }
      this.fundingSources = new ArrayList<>();
      List<FundingSource> fundingSources =
        fundingSourceManager.findAll().stream().filter(fs -> fs.isActive()).collect(Collectors.toList());
      for (FundingSource fundingSource : fundingSources) {
        for (ProjectBudget budget : fundingSource.getProjectBudgets().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          if (budget.getProject().getId().longValue() == deliverable.getProject().getId()) {
            this.fundingSources.add(fundingSource);
          }
          {

          }
        }
      }
      Set<FundingSource> hs = new HashSet();
      hs.addAll(this.fundingSources);
      this.fundingSources.clear();
      this.fundingSources.addAll(hs);

    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_DELIVERABLE_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (deliverableTypeParent != null) {
        deliverableTypeParent.clear();
      }


      deliverable.setCrossCuttingGender(null);
      deliverable.setCrossCuttingCapacity(null);
      deliverable.setCrossCuttingNa(null);
      deliverable.setCrossCuttingYouth(null);


      if (projectOutcome != null) {
        projectOutcome.clear();
      }

      if (status != null) {
        status.clear();
      }

      if (keyOutputs != null) {
        keyOutputs.clear();
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

      DeliverablePartnership partnership = new DeliverablePartnership();

      partnership.setDeliverable(deliverable);
      partnership.setProjectPartnerPerson(partnerPerson);
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

      Project projectDB = projectManager.getProjectById(project.getId());
      project.setActive(true);
      project.setCreatedBy(projectDB.getCreatedBy());
      project.setModifiedBy(this.getCurrentUser());
      project.setModificationJustification(this.getJustification());
      project.setActiveSince(projectDB.getActiveSince());

      Deliverable deliverablePrew = deliverableManager.getDeliverableById(deliverableID);

      deliverablePrew.setTitle(deliverable.getTitle());
      deliverablePrew.setYear(deliverable.getYear());
      deliverablePrew.setStatusDescription(deliverable.getStatusDescription());

      if (deliverable.getCrossCuttingCapacity() == null) {
        deliverablePrew.setCrossCuttingCapacity(false);
      } else {
        deliverablePrew.setCrossCuttingCapacity(true);
      }
      if (deliverable.getCrossCuttingNa() == null) {
        deliverablePrew.setCrossCuttingNa(false);
      } else {
        deliverablePrew.setCrossCuttingNa(true);
      }
      if (deliverable.getCrossCuttingGender() == null) {
        deliverablePrew.setCrossCuttingGender(false);
      } else {
        deliverablePrew.setCrossCuttingGender(true);
      }
      if (deliverable.getCrossCuttingYouth() == null) {
        deliverablePrew.setCrossCuttingYouth(false);
      } else {
        deliverablePrew.setCrossCuttingYouth(true);
      }

      if (deliverable.getStatus() != null) {
        deliverablePrew.setStatus(deliverable.getStatus());
      }

      DeliverableType deliverableType =
        deliverableTypeManager.getDeliverableTypeById(deliverable.getDeliverableType().getId());

      deliverablePrew.setDeliverableType(deliverableType);

      CrpClusterKeyOutput keyOutput =
        crpClusterKeyOutputManager.getCrpClusterKeyOutputById(deliverable.getCrpClusterKeyOutput().getId());

      deliverablePrew.setCrpClusterKeyOutput(keyOutput);

      FundingSource fundingSource = fundingSourceManager.getFundingSourceById(deliverable.getFundingSource().getId());

      deliverablePrew.setFundingSource(fundingSource);

      DeliverablePartnership partnershipResponsible = null;
      ProjectPartnerPerson partnerPerson = null;

      List<DeliverablePartnership> deliverablePartnerships =
        new ArrayList<DeliverablePartnership>(deliverablePrew.getDeliverablePartnerships());

      if (deliverablePrew.getDeliverablePartnerships() != null
        && deliverablePrew.getDeliverablePartnerships().size() > 0) {

        try {
          partnershipResponsible =
            deliverablePrew.getDeliverablePartnerships().stream()
              .filter(dp -> dp.isActive()
                && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
            .collect(Collectors.toList()).get(0);
        } catch (Exception e) {
          partnershipResponsible = null;
        }

      }

      if (deliverable.getResponsiblePartner() != null
        && deliverable.getResponsiblePartner().getProjectPartnerPerson().getId() != -1) {
        partnerPerson = projectPartnerPersonManager
          .getProjectPartnerPersonById(deliverable.getResponsiblePartner().getProjectPartnerPerson().getId());
      }

      Long deliverableSaveId = deliverableManager.saveDeliverable(deliverablePrew);
      Deliverable deliverableSave = deliverableManager.getDeliverableById(deliverableSaveId);

      if (partnershipResponsible != null && partnerPerson != null) {
        Long partnerId1 = partnershipResponsible.getProjectPartnerPerson().getId();
        Long partnerId2 = partnerPerson.getId();

        if (partnerId1 != partnerId2) {


          deliverablePartnershipManager.deleteDeliverablePartnership(partnershipResponsible.getId());

          DeliverablePartnership partnership = new DeliverablePartnership();
          partnership.setProjectPartnerPerson(partnerPerson);
          partnership.setPartnerType(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue());
          partnership.setDeliverable(deliverableSave);
          partnership.setActive(true);
          partnership.setCreatedBy(this.getCurrentUser());
          partnership.setModifiedBy(this.getCurrentUser());
          partnership.setModificationJustification("");
          partnership.setActiveSince(new Date());

          deliverablePartnershipManager.saveDeliverablePartnership(partnership);
        }
      } else if (partnershipResponsible == null && partnerPerson != null) {

        DeliverablePartnership partnership = new DeliverablePartnership();
        partnership.setProjectPartnerPerson(partnerPerson);
        partnership.setPartnerType(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue());
        partnership.setDeliverable(deliverableSave);
        partnership.setActive(true);
        partnership.setCreatedBy(this.getCurrentUser());
        partnership.setModifiedBy(this.getCurrentUser());
        partnership.setModificationJustification("");
        partnership.setActiveSince(new Date());

        deliverablePartnershipManager.saveDeliverablePartnership(partnership);
      }

      this.partnershipPreviousData(deliverableSave);
      this.parnershipNewData();


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

        for (DeliverableGenderLevel deliverableFundingSource : deliverable.getGenderLevels()) {
          if (deliverableFundingSource.getId() == null || deliverableFundingSource.getId() == -1) {


            deliverableFundingSource.setDeliverable(deliverableManager.getDeliverableById(deliverableID));
            deliverableFundingSource.setActive(true);
            deliverableFundingSource.setCreatedBy(this.getCurrentUser());
            deliverableFundingSource.setModifiedBy(this.getCurrentUser());
            deliverableFundingSource.setModificationJustification("");
            deliverableFundingSource.setActiveSince(new Date());

            deliverableGenderLevelManager.saveDeliverableGenderLevel(deliverableFundingSource);


          }
        }
      }

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_DELIVERABLE_PARTNERSHIPS_RELATION);
      relationsName.add(APConstants.PROJECT_DELIVERABLE_FUNDING_RELATION);
      deliverable = deliverableManager.getDeliverableById(deliverableID);
      deliverable.setActiveSince(new Date());
      deliverable.setModifiedBy(this.getCurrentUser());
      deliverableManager.saveDeliverable(deliverable, this.getActionName(), relationsName);
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
        return REDIRECT;
      }

    } else {
      return NOT_AUTHORIZED;
    }

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

  public void setFundingSources(List<FundingSource> fundingSources) {
    this.fundingSources = fundingSources;
  }

  public void setGenderLevels(Map<String, String> genderLevels) {
    this.genderLevels = genderLevels;
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

  @Override
  public void validate() {
    if (save) {
      deliverableValidator.validate(this, deliverable, true);
    }
  }
}
