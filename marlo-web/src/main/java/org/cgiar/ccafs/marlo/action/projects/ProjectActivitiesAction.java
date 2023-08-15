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
import org.cgiar.ccafs.marlo.data.manager.ActivityTitleManager;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectDeliverableSharedManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.ActivityTitle;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectActivitiesAction extends BaseAction {


  private static final long serialVersionUID = 2146101620783927003L;
  private final Logger logger = LoggerFactory.getLogger(ProjectActivitiesAction.class);
  // Variables
  private ProjectActivitiesValidator activitiesValidator;
  private HistoryComparator historyComparator;
  private GlobalUnit loggedCrp;
  private List<ProjectPartnerPerson> partnerPersons;
  private List<ActivityTitle> activityTitles;
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
  private ActivityTitleManager activityTitleManager;
  private ProjectDeliverableSharedManager projectDeliverableSharedManager;

  private List<Deliverable> deliverablesMissingActivity = new ArrayList<>();

  @Inject
  public ProjectActivitiesAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    ProjectPartnerPersonManager projectPartnerPersonManager, ActivityManager activityManager,
    DeliverableManager deliverableManager, AuditLogManager auditLogManager,
    ProjectActivitiesValidator activitiesValidator, HistoryComparator historyComparator,
    ProjectPartnerManager projectPartnerManager, ActivityTitleManager activityTitleManager,
    ProjectDeliverableSharedManager projectDeliverableSharedManager) {
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
    this.activityTitleManager = activityTitleManager;
    this.projectDeliverableSharedManager = projectDeliverableSharedManager;
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

  public List<ActivityTitle> getActivityTitles() {
    return activityTitles;
  }


  private Path getAutoSaveFilePath() {
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  /**
   * Get the value of deliverablesMissingActivity
   *
   * @return the value of deliverablesMissingActivity
   */
  public List<Deliverable> getDeliverablesMissingActivity() {
    return deliverablesMissingActivity;
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
          // fix dperez 2019-11-18
          try {
            index[0] = i;
            differences.addAll(historyComparator.getDifferencesList(activity, transaction, specialList,
              "project.projectActivities[" + i + "]", "project", 1));
            i++;
          } catch (Exception e) {
            logger.error("Error getting differences between audilog ");
          }
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
            List<DeliverableActivity> deliverableActivities = new ArrayList<>();
            for (DeliverableActivity deliverableActivity : activity.getDeliverables()) {
              Deliverable deliverable =
                deliverableManager.getDeliverableById(deliverableActivity.getDeliverable().getId());
              deliverable.getDeliverableInfo(this.getActualPhase());
              deliverableActivity.setDeliverable(deliverable);
              if (deliverable.isActive() && deliverable.getDeliverableInfo(this.getActualPhase()) != null
                && deliverable.getDeliverableInfo(this.getActualPhase()).isActive()) {
                deliverableActivities.add(deliverableActivity);
              }
            }
            activity.setDeliverables(deliverableActivities);
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
                .filter(da -> da.isActive() && da.getPhase() != null && da.getPhase().equals(this.getActualPhase())
                  && da.getDeliverable().isActive()
                  && da.getDeliverable().getDeliverableInfo(this.getActualPhase()) != null
                  && da.getDeliverable().getDeliverableInfo(this.getActualPhase()).isActive())
                .collect(Collectors.toList())));
          }
        }

      }

      status = new HashMap<>();
      List<ProjectStatusEnum> list = Arrays.asList(ProjectStatusEnum.values());
      for (ProjectStatusEnum projectStatusEnum : list) {
        status.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
      }
      status.remove(ProjectStatusEnum.Extended.getStatusId());
      List<Deliverable> deliverables = new ArrayList<>();
      if (project.getDeliverables() != null) {
        if (project.getDeliverables().isEmpty()) {
          /*
           * project.setProjectDeliverables(new ArrayList<Deliverable>(projectManager.getProjectById(projectID)
           * .getDeliverables().stream().filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) !=
           * null)
           * .collect(Collectors.toList())));
           */
          deliverables = projectManager.getProjectById(projectID).getDeliverables().stream()
            .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null)
            .collect(Collectors.toList());
        } else {
          /*
           * project.setProjectDeliverables(new ArrayList<Deliverable>(project.getDeliverables().stream()
           * .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null)
           * .collect(Collectors.toList())));
           */
          deliverables = project.getDeliverables().stream()
            .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null)
            .collect(Collectors.toList());
        }
      }

      for (Deliverable deliverable : deliverables) {
        deliverable.setTagTitle(deliverable.getComposedName());
      }
      /*
       * try {
       * // Load Shared deliverables
       * List<ProjectDeliverableShared> deliverableShared = this.projectDeliverableSharedManager
       * .getByProjectAndPhase(project.getId(), this.getActualPhase().getId()) != null
       * ? this.projectDeliverableSharedManager.getByProjectAndPhase(project.getId(), this.getActualPhase().getId())
       * .stream()
       * .filter(px -> px.isActive() && px.getDeliverable().isActive()
       * && px.getDeliverable().getDeliverableInfo(this.getActualPhase()) != null)
       * .collect(Collectors.toList())
       * : Collections.emptyList();
       * if (deliverableShared != null && !deliverableShared.isEmpty()) {
       * for (ProjectDeliverableShared deliverableS : deliverableShared) {
       * if (!deliverables.contains(deliverableS.getDeliverable())) {
       * if (deliverableS.getDeliverable().getProject() != null
       * && deliverableS.getDeliverable().getProject().getId() != null
       * && !deliverableS.getDeliverable().getProject().getId().equals(projectID)) {
       * DeliverableInfo deliverableInfo =
       * deliverableS.getDeliverable().getDeliverableInfo(this.getActualPhase());
       * deliverableS.getDeliverable().setDeliverableInfo(deliverableInfo);
       * deliverableS.getDeliverable().setTagTitle(
       * "<span class=\"label label-info\">From C" + deliverableS.getDeliverable().getProject().getId()
       * + "</span> ");
       * } else {
       * deliverableS.getDeliverable().setTagTitle(deliverableS.getDeliverable().getComposedName());
       * }
       * deliverables.add(deliverableS.getDeliverable());
       * }
       * }
       * }
       * } catch (Exception e) {
       * logger.error("unable to get shared deliverables", e);
       * }
       */
      project.setProjectDeliverables(deliverables);

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

      activityTitles = new ArrayList<>();

      if (this.isAiccra()) {
        if (activityTitleManager.findAll() != null && !activityTitleManager.findAll().isEmpty()) {


          try {
            activityTitles = activityTitleManager.findByCurrentYear(this.getActualPhase().getYear());
          } catch (Exception e) {
            logger.error("unable to get activity title by date", e);
          }

          if (activityTitles == null || (activityTitles != null && activityTitles.isEmpty())) {
            activityTitles = activityTitleManager.findAll();
          }
          /*
           * List<ActivityTitle> tempActivityTitles = new ArrayList<>();
           * for (ActivityTitle activityTitle : activityTitles) {
           * if (activityTitle != null && activityTitle.getStartDate() != null && activityTitle.getEndDate() != null) {
           * if (activityTitle.getStartDate().before(this.getActualPhase().getStartDate())
           * && activityTitle.getEndDate().after(this.getActualPhase().getEndDate())) {
           * tempActivityTitles.add(activityTitle);
           * }
           * }
           * }
           */
          /*
           * if (activityTitles != null && !activityTitles.isEmpty()) {
           * activityTitles = tempActivityTitles;
           * }
           */
          if (activityTitles != null && activityTitles.isEmpty()) {
            activityTitles.sort((a1, a2) -> a1.getTitle().compareTo(a2.getTitle()));
          }
        }
      }

      deliverablesMissingActivity = new ArrayList<>();
      List<Deliverable> prevMissingActivity = new ArrayList<>();

      try {
        prevMissingActivity = project.getCurrentDeliverables(this.getActualPhase());

        if (prevMissingActivity != null && !prevMissingActivity.isEmpty()) {
          prevMissingActivity = prevMissingActivity.stream()
            .filter(d -> d != null && d.getDeliverableInfo(this.getActualPhase()).getStatus() != null
              && d.getDeliverableInfo(this.getActualPhase()).getStatus() != 5)
            .collect(Collectors.toList());
        }
      } catch (Exception e) {
        logger.error("unable to get deliverables without activities", e);
        prevMissingActivity = new ArrayList<>();
      }


      prevMissingActivity.stream()
        .filter(
          (deliverable) -> (deliverable.getDeliverableActivities().isEmpty()
            || deliverable.getDeliverableActivities().stream().filter(
              da -> da.isActive()).collect(
                Collectors.toList())
              .isEmpty()
            || deliverable.getDeliverableActivities().stream()
              .filter(da -> da.getPhase().getId().equals(this.getActualPhase().getId()) && da.getActivity().isActive()
                && da.isActive())
              .collect(Collectors.toList()).isEmpty()))
        .forEachOrdered((_item) -> {
          deliverablesMissingActivity.add(_item);
        });

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

      if (activityTitles != null) {
        activityTitles.clear();
      }

      if (project.getProjectDeliverables() != null) {
        project.getProjectDeliverables().clear();
      }
    }
    /*
     * if (this.isHttpPost()) {
     * if (!this.isDraft()) {
     * activitiesValidator.validate(this, project, true);
     * if (!this.getInvalidFields().isEmpty()) {
     * this.setActionMessages(null);
     * // this.addActionMessage(Map.toString(this.getInvalidFields().toArray()));
     * List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
     * for (String key : keys) {
     * this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
     * }
     * }
     * }
     * }
     */
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

          // Set Activity Title
          if (this.isAiccra()) {
            ActivityTitle title;
            if (activityUI.getActivityTitle() != null && activityUI.getActivityTitle().getId() != null
              && activityTitleManager.getActivityTitleById(activityUI.getActivityTitle().getId()) != null) {
              title = activityTitleManager.getActivityTitleById(activityUI.getActivityTitle().getId());
              if (title != null) {
                activityUI.setActivityTitle(title);
                activityUI.setTitle(title.getTitle());
              } else {
                activityUI.setActivityTitle(null);
              }
            } else {
              activityUI.setActivityTitle(null);
            }
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
          if (this.isAiccra()) {
            if (activityUI.getActivityTitle() != null && activityUI.getActivityTitle().getId().longValue() != -1) {
              ActivityTitle title = activityTitleManager.getActivityTitleById(activityUI.getActivityTitle().getId());
              activityUpdate.setActivityTitle(title);
              activityUpdate.setTitle(title.getTitle());
            } else {
              activityUpdate.setActivityTitle(null);
            }
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


  public void setActivityTitles(List<ActivityTitle> activityTitles) {
    this.activityTitles = activityTitles;
  }

  /**
   * Set the value of deliverablesMissingActivity
   *
   * @param deliverablesMissingActivity new value of
   *        deliverablesMissingActivity
   */
  public void setDeliverablesMissingActivity(List<Deliverable> deliverablesMissingActivity) {
    this.deliverablesMissingActivity = deliverablesMissingActivity;
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
