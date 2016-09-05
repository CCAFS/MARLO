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
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpClusterKeyOutput;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnership;
import org.cgiar.ccafs.marlo.data.model.DeliverablePartnershipTypeEnum;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  // Managers
  private DeliverableTypeManager deliverableTypeManager;


  private DeliverableManager deliverableManager;

  private ProjectManager projectManager;


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

  private Project project;

  private Map<String, String> status;

  private Deliverable deliverable;

  private List<ProjectFocus> projectPrograms;

  private String transaction;

  private AuditLogManager auditLogManager;

  @Inject
  public DeliverableAction(APConfig config, DeliverableTypeManager deliverableTypeManager,
    DeliverableManager deliverableManager, CrpManager crpManager, ProjectManager projectManager,
    ProjectPartnerPersonManager projectPartnerPersonManager, CrpProgramOutcomeManager crpProgramOutcomeManager,
    CrpClusterKeyOutputManager crpClusterKeyOutputManager, DeliverablePartnershipManager deliverablePartnershipManager,
    AuditLogManager auditLogManager) {
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
  }

  @Override
  public String cancel() {

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
      System.out.println(fileDeleted);
    }

    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionWarning(this.getText("cancel.autoSave") + validationMessage);
    } else {
      this.addActionMessage(this.getText("cancel.autoSave"));
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

  public List<DeliverableType> getDeliverableSubTypes() {
    return deliverableSubTypes;
  }


  public List<DeliverableType> getDeliverableTypeParent() {
    return deliverableTypeParent;
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

  public List<DeliverablePartnership> otherPartners() {
    List<DeliverablePartnership> list = deliverable.getDeliverablePartnerships().stream()
      .filter(dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.OTHER.getValue()))
      .collect(Collectors.toList());
    return list;
  }

  public List<DeliverablePartnership> otherPartnersAutoSave() {
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
  }

  public void parnershipNewData() {
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


      }
    }
  }

  public void partnershipPreviousData(Deliverable deliverablePrew) {
    List<DeliverablePartnership> partnerShipsPrew = deliverablePrew.getDeliverablePartnerships().stream()
      .filter(dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.OTHER.getValue()))
      .collect(Collectors.toList());


    for (DeliverablePartnership deliverablePartnership : partnerShipsPrew) {
      if (!deliverable.getOtherPartners().contains(deliverablePartnership)) {
        deliverablePartnershipManager.deleteDeliverablePartnership(deliverablePartnership.getId());
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
        project.setProjectEditLeader(deliverableDb.getProject().isProjectEditLeader());
        project.setProjectLocations(deliverableDb.getProject().getProjectLocations());
        reader.close();

        deliverable.setResponsiblePartner(this.responsiblePartnerAutoSave());
        deliverable.setOtherPartners(this.otherPartnersAutoSave());

        this.setDraft(true);
      } else {
        deliverable.setResponsiblePartner(this.responsiblePartner());
        deliverable.setOtherPartners(this.otherPartners());
        this.setDraft(false);
      }


      status = new HashMap<>();
      List<ProjectStatusEnum> list = Arrays.asList(ProjectStatusEnum.values());
      for (ProjectStatusEnum projectStatusEnum : list) {
        status.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
      }


      deliverableTypeParent = new ArrayList<>(deliverableTypeManager.findAll().stream()
        .filter(dt -> dt.getDeliverableType() == null).collect(Collectors.toList()));

      if (project.getProjectOutcomes() != null) {
        projectOutcome = new ArrayList<>(project.getProjectOutcomes());
      }

      Long deliverableTypeParentId = deliverable.getDeliverableType().getDeliverableType().getId();

      deliverableSubTypes = new ArrayList<>(deliverableTypeManager.findAll().stream()
        .filter(dt -> dt.getDeliverableType() != null && dt.getDeliverableType().getId() == deliverableTypeParentId)
        .collect(Collectors.toList()));

      if (project.getProjectClusterActivities() != null) {

        keyOutputs = new ArrayList<>();

        for (ProjectClusterActivity clusterActivity : project.getProjectClusterActivities().stream()
          .filter(ca -> ca.isActive()).collect(Collectors.toList())) {
          keyOutputs.addAll(clusterActivity.getCrpClusterOfActivity().getCrpClusterKeyOutputs());
        }
      }

      if (projectPartnerPersonManager.findAll() != null) {
        partnerPersons = new ArrayList<>(projectPartnerPersonManager.findAll().stream()
          .filter(pp -> pp.isActive() && pp.getProjectPartner().getProject().getId() == project.getId())
          .collect(Collectors.toList()));
      }


    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_DELIVERABLE_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (deliverableTypeParent != null) {
        deliverableTypeParent.clear();
      }

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
    }
  }

  private DeliverablePartnership responsiblePartner() {
    DeliverablePartnership partnership =
      deliverable.getDeliverablePartnerships().stream()
        .filter(
          dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
        .collect(Collectors.toList()).get(0);
    return partnership;
  }

  private DeliverablePartnership responsiblePartnerAutoSave() {

    ProjectPartnerPerson partnerPerson = projectPartnerPersonManager
      .getProjectPartnerPersonById(deliverable.getResponsiblePartner().getProjectPartnerPerson().getId());

    DeliverablePartnership partnership = new DeliverablePartnership();

    partnership.setDeliverable(deliverable);
    partnership.setProjectPartnerPerson(partnerPerson);
    partnership.setPartnerType(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue());
    partnership.setActive(true);

    return partnership;
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      Project projectDB = projectManager.getProjectById(project.getId());
      project.setActive(true);
      project.setCreatedBy(projectDB.getCreatedBy());
      project.setModifiedBy(this.getCurrentUser());
      project.setModificationJustification("");
      project.setActiveSince(projectDB.getActiveSince());

      Deliverable deliverablePrew = deliverableManager.getDeliverableById(deliverableID);

      deliverablePrew.setTitle(deliverable.getTitle());
      deliverablePrew.setYear(deliverable.getYear());

      DeliverableType deliverableType =
        deliverableTypeManager.getDeliverableTypeById(deliverable.getDeliverableType().getId());

      deliverablePrew.setDeliverableType(deliverableType);

      CrpProgramOutcome crpProgram =
        crpProgramOutcomeManager.getCrpProgramOutcomeById(deliverable.getCrpProgramOutcome().getId());

      deliverablePrew.setCrpProgramOutcome(crpProgram);

      CrpClusterKeyOutput keyOutput =
        crpClusterKeyOutputManager.getCrpClusterKeyOutputById(deliverable.getCrpClusterKeyOutput().getId());

      deliverablePrew.setCrpClusterKeyOutput(keyOutput);

      DeliverablePartnership partnershipResponsible = deliverablePrew.getDeliverablePartnerships().stream()
        .filter(
          dp -> dp.isActive() && dp.getPartnerType().equals(DeliverablePartnershipTypeEnum.RESPONSIBLE.getValue()))
        .collect(Collectors.toList()).get(0);

      ProjectPartnerPerson partnerPerson = projectPartnerPersonManager
        .getProjectPartnerPersonById(deliverable.getResponsiblePartner().getProjectPartnerPerson().getId());

      Long deliverableSaveId = deliverableManager.saveDeliverable(deliverablePrew);
      Deliverable deliverableSave = deliverableManager.getDeliverableById(deliverableSaveId);

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

      this.partnershipPreviousData(deliverableSave);
      this.parnershipNewData();

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_DELIVERABLE_PARTNERSHIPS_RELATION);
      deliverable = deliverableManager.getDeliverableById(deliverableID);
      deliverable.setActiveSince(new Date());
      deliverable.setModifiedBy(this.getCurrentUser());
      deliverableManager.saveDeliverable(deliverable, this.getActionName(), relationsName);
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      Collection<String> messages = this.getActionMessages();
      if (!messages.isEmpty()) {
        String validationMessage = messages.iterator().next();
        this.setActionMessages(null);
        this.addActionWarning(this.getText("saving.saved") + validationMessage);
      } else {
        this.addActionMessage(this.getText("saving.saved"));
      }
      return SUCCESS;

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
}
