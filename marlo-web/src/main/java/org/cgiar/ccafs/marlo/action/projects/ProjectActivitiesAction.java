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
import org.cgiar.ccafs.marlo.data.manager.ActivityManager;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.utils.HistoryDifference;
import org.cgiar.ccafs.marlo.validation.projects.ProjectActivitiesValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectActivitiesAction extends BaseAction {


  private static final long serialVersionUID = 2146101620783927003L;

  // Variables
  private ProjectActivitiesValidator activitiesValidator;
  private HistoryComparator historyComparator;
  private GlobalUnit loggedCrp;
  private List<ProjectPartnerPerson> partnerPersons;
  private Project project;
  private long projectID;
  private Map<String, String> status;
  private String transaction;

  // Managers
  private ActivityManager activityManager;
  private AuditLogManager auditLogManager;
  private GlobalUnitManager crpManager;
  private DeliverableManager deliverableManager;
  private ProjectPartnerManager projectPartnerManager;
  private ProjectManager projectManager;
  private ProjectPartnerPersonManager projectPartnerPersonManager;

  @Inject
  public ProjectActivitiesAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    ProjectPartnerPersonManager projectPartnerPersonManager, ActivityManager activityManager,
    DeliverableManager deliverableManager, AuditLogManager auditLogManager,
    ProjectActivitiesValidator activitiesValidator, HistoryComparator historyComparator,
    ProjectPartnerManager projectPartnerManager) {
    super(config);
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.projectPartnerPersonManager = projectPartnerPersonManager;
    this.activityManager = activityManager;
    this.deliverableManager = deliverableManager;
    this.auditLogManager = auditLogManager;
    this.historyComparator = historyComparator;
    this.activitiesValidator = activitiesValidator;
    this.projectPartnerManager = projectPartnerManager;
  }

  public void activitiesPreviousData(Project projectBD) {
    List<Activity> activitiesPrew;
    activitiesPrew = projectBD.getActivities().stream()
      .filter(a -> a.isActive() && a.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());
    for (Activity activity : activitiesPrew) {
      if (!project.getProjectActivities().contains(activity)) {
        activityManager.deleteActivity(activity.getId());
      }
    }
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


  public List<Activity> getActivities(boolean open) {

    try {
      if (open) {

        List<Activity> openA = project.getProjectActivities().stream()
          .filter(
            a -> a.isActive() && ((a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
              || (a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))))
          .collect(Collectors.toList());
        return openA;

      } else {

        List<Activity> openA = project.getProjectActivities().stream()
          .filter(
            a -> a.isActive() && ((a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())
              || (a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId())))))
          .collect(Collectors.toList());
        return openA;
      }
    } catch (Exception e) {
      return new ArrayList<>();
    }
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public int getIndexActivities(long id) {
    Activity activity = new Activity();
    activity.setId(id);
    return project.getProjectActivities().indexOf(activity);

  }


  public GlobalUnit getLoggedCrp() {
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

  public Map<String, String> getStatus() {
    return status;
  }

  public String getTransaction() {
    return transaction;
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);

      if (history != null) {
        project = history;

        List<HistoryDifference> differences = new ArrayList<>();
        Map<String, String> specialList = new HashMap<>();
        int i = 0;
        project.setProjectActivities(
          project.getActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        for (Activity activity : project.getProjectActivities()) {
          int[] index = new int[1];
          index[0] = i;
          differences.addAll(historyComparator.getDifferencesList(activity, transaction, specialList,
            "project.projectActivities[" + i + "]", "project", 1));
          i++;


          if (activity.getDeliverableActivities() != null && !activity.getDeliverableActivities().isEmpty()) {
            for (DeliverableActivity deliverableActivity : activity.getDeliverableActivities()) {
              if (deliverableActivity.getDeliverable() != null
                && deliverableActivity.getDeliverable().getId() != null) {

                if (deliverableManager.getDeliverableById(deliverableActivity.getDeliverable().getId()) != null) {
                  Deliverable deliverable =
                    deliverableManager.getDeliverableById(deliverableActivity.getDeliverable().getId());
                  deliverableActivity.setDeliverable(deliverable);
                  deliverableActivity.getDeliverable().getDeliverableInfo(this.getActualPhase());
                }

              }

            }
          }
        }

        this.setDifferences(differences);


      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }


    } else {
      project = projectManager.getProjectById(projectID);
    }
    if (project != null) {

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();


        AutoSaveReader autoSaveReader = new AutoSaveReader();

        project = (Project) autoSaveReader.readFromJson(jReader);
        Project projectDb = projectManager.getProjectById(project.getId());
        project.setProjectInfo(projectDb.getProjecInfoPhase(this.getActualPhase()));
        project.setProjectLocations(projectDb.getProjectLocations());


        for (Activity activity : project.getProjectActivities()) {
          if (activity.getDeliverables() != null) {
            for (DeliverableActivity deliverableActivity : activity.getDeliverables()) {
              if (deliverableActivity.getId() == -1) {
                Deliverable deliverable =
                  deliverableManager.getDeliverableById(deliverableActivity.getDeliverable().getId());
                deliverable.getDeliverableInfo(this.getActualPhase());
                deliverableActivity.setDeliverable(deliverable);
              }
            }
          }
        }

        this.setDraft(true);
      } else {
        this.setDraft(false);

        // GlobalUnitProject gp = globalUnitProjectManager.findByProjectId(project.getId());

        List<Activity> activities = project.getActivities().stream()
          .filter(a -> a.isActive() && a.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());

        project.setProjectActivities(new ArrayList<Activity>(activities));
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        if (project.getProjectActivities() != null) {
          for (Activity openActivity : project.getProjectActivities()) {
            openActivity
              .setDeliverables(new ArrayList<DeliverableActivity>(openActivity.getDeliverableActivities().stream()
                .filter(da -> da.isActive() && da.getPhase() != null && da.getPhase().equals(this.getActualPhase()))
                .collect(Collectors.toList())));
            for (DeliverableActivity deliverableActivity : openActivity.getDeliverables()) {
              deliverableActivity.getDeliverable().getDeliverableInfo(this.getActualPhase());
            }
          }
        }

      }

      status = new HashMap<>();
      List<ProjectStatusEnum> list = Arrays.asList(ProjectStatusEnum.values());
      for (ProjectStatusEnum projectStatusEnum : list) {
        status.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
      }
      status.remove(ProjectStatusEnum.Extended.getStatusId());
      if (project.getDeliverables() != null) {
        if (project.getDeliverables().isEmpty()) {
          project.setProjectDeliverables(new ArrayList<Deliverable>(projectManager.getProjectById(projectID)
            .getDeliverables().stream().filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null)
            .collect(Collectors.toList())));
        } else {
          project.setProjectDeliverables(new ArrayList<Deliverable>(project.getDeliverables().stream()
            .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null)
            .collect(Collectors.toList())));
        }
      }

      partnerPersons = new ArrayList<>();
      for (ProjectPartner partner : projectPartnerManager.findAll().stream()
        .filter(
          pp -> pp.isActive() && pp.getProject().getId() == projectID && pp.getPhase().equals(this.getActualPhase()))
        .collect(Collectors.toList())) {

        for (ProjectPartnerPerson partnerPerson : partner.getProjectPartnerPersons().stream()
          .filter(ppa -> ppa.isActive()).collect(Collectors.toList())) {

          partnerPersons.add(partnerPerson);
        }
      }
    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_ACTIVITIES_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (project.getProjectActivities() != null) {
        project.getProjectActivities().clear();
      }

      /*
       * if (project.getClosedProjectActivities() != null) {
       * project.getClosedProjectActivities().clear();
       * }
       */

      if (partnerPersons != null) {
        partnerPersons.clear();
      }

      if (project.getProjectDeliverables() != null) {
        project.getProjectDeliverables().clear();
      }
    }

    if (this.isHttpPost()) {
      if (!this.isDraft()) {
        activitiesValidator.validate(this, project, true);
        if (!this.getInvalidFields().isEmpty()) {
          this.setActionMessages(null);
          // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
          List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
          for (String key : keys) {
            this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
          }

        }
      }
    }


  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      Project projectBD = projectManager.getProjectById(projectID);
      List<Activity> activitiesDB = projectBD.getActivities().stream()
        .filter(a -> a.isActive() && a.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());
      this.activitiesPreviousData(projectBD);
      // Check activities from UI
      if (project.getProjectActivities() != null && !project.getProjectActivities().isEmpty()) {
        this.saveActivitiesNewData();
      } else {
        // Delete activities
        if (activitiesDB != null && !activitiesDB.isEmpty()) {
          for (Activity activity : activitiesDB) {
            activityManager.deleteActivity(activity.getId());
          }
        }
      }


      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_ACTIVITIES_RELATION);
      relationsName.add(APConstants.PROJECT_INFO_RELATION);
      project = projectManager.getProjectById(projectID);
      /**
       * The following is required because we need to update something on the @Project if we want a row created in
       * the auditlog table.
       */
      this.setModificationJustification(project);
      projectManager.saveProject(project, this.getActionName(), relationsName, this.getActualPhase());
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


  public void saveActivitiesNewData() {

    for (Activity activityUI : project.getProjectActivities()) {
      if (activityUI != null) {

        // New Activity
        if (activityUI.getId() == null || activityUI.getId() == -1) {

          activityUI.setProject(project);
          activityUI.setPhase(this.getActualPhase());
          if (activityUI.getActivityStatus() == -1) {
            activityUI.setActivityStatus(Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()));
          }
          try {
            ProjectPartnerPerson partnerPerson =
              projectPartnerPersonManager.getProjectPartnerPersonById(activityUI.getProjectPartnerPerson().getId());
            activityUI.setProjectPartnerPerson(partnerPerson);
          } catch (Exception e) {
            activityUI.setProjectPartnerPerson(null);
          }
          // Save new activity and deliverable activities
          activityUI = activityManager.saveActivity(activityUI);
          // This is to add Activity to generate correct auditlog.
          project.getActivities().add(activityUI);
        } else {
          // Update Activity
          Activity activityUpdate = activityManager.getActivityById(activityUI.getId());
          activityUpdate.setPhase(this.getActualPhase());
          activityUpdate.setTitle(activityUI.getTitle());
          activityUpdate.setDescription(activityUI.getDescription());
          activityUpdate.setStartDate(activityUI.getStartDate());
          activityUpdate.setEndDate(activityUI.getEndDate());
          if (activityUI.getActivityStatus() != -1) {
            activityUpdate.setActivityStatus(activityUI.getActivityStatus());
          } else {
            activityUpdate.setActivityStatus(Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()));
          }
          activityUpdate.setActivityProgress(activityUI.getActivityProgress());


          if (activityUI.getProjectPartnerPerson() != null
            && activityUI.getProjectPartnerPerson().getId().longValue() != -1) {
            ProjectPartnerPerson partnerPerson =
              projectPartnerPersonManager.getProjectPartnerPersonById(activityUI.getProjectPartnerPerson().getId());
            activityUpdate.setProjectPartnerPerson(partnerPerson);
          } else {
            activityUpdate.setProjectPartnerPerson(null);
          }
          // Set deliverables here to add inside saveActivity
          activityUpdate.setDeliverables(activityUI.getDeliverables());

          // Save new activity and deliverable activities
          activityUpdate = activityManager.saveActivity(activityUpdate);
          // This is to add Activity to generate correct auditlog.
          project.getActivities().add(activityUpdate);
        }
      }


    }

  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
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

  public void setStatus(Map<String, String> status) {
    this.status = status;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      activitiesValidator.validate(this, project, true);
    }
  }

}
