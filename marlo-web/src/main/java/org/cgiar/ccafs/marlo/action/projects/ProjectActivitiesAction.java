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
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPartnerPersonManager;
import org.cgiar.ccafs.marlo.data.model.Activity;
import org.cgiar.ccafs.marlo.data.model.ActivityTitle;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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

  private List<Deliverable> deliverablesMissingActivity = new ArrayList<>();
  private String maxYear;

  @Inject
  public ProjectActivitiesAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    ProjectPartnerPersonManager projectPartnerPersonManager, ActivityManager activityManager,
    DeliverableManager deliverableManager, AuditLogManager auditLogManager,
    ProjectActivitiesValidator activitiesValidator, HistoryComparator historyComparator,
    ProjectPartnerManager projectPartnerManager, ActivityTitleManager activityTitleManager) {
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

  public void deleteActivities(List<Activity> activitiesDB) {
    try {
      for (Activity activity : activitiesDB) {
        boolean existsInUI = project.getProjectActivities() != null && project.getProjectActivities().stream()
          .anyMatch(a -> a.getId() != null && a.getId().equals(activity.getId()));

        if (!existsInUI) {
          // TODO: delete deliverables associated before to delete the activity

          activityManager.deleteActivity(activity.getId());
          logger.debug("Deleted activity with ID {}", activity.getId());
        }
      }

    } catch (Exception e) {
      logger.error(" unable to get activities in activitiesPreviousDataCustom function ");
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

  // Helper function to extract the number from the title
  private int extractActivityNumber(Activity activity) {
    if (activity == null || activity.getTitle() == null) {
      return 0;
    }

    Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)*)");
    Matcher matcher = pattern.matcher(activity.getTitle());
    if (matcher.find()) {
      String numberStr = matcher.group(1);
      String[] numberParts = numberStr.split("\\.");
      int number = 0;
      for (String part : numberParts) {
        try {
          number = number * 100 + Integer.parseInt(part);
        } catch (NumberFormatException e) {
          // ignora parte malformada
        }
      }
      return number;
    }
    return 0;
  }

  public List<Activity> getActivities(boolean open) {

    try {
      List<Activity> openA;
      if (open) {

        openA = project.getProjectActivities().stream()
          .filter(
            a -> a.isActive() && ((a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId())
              || (a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Extended.getStatusId())))))
          .collect(Collectors.toList());
        // return openA;

      } else {

        openA = project.getProjectActivities().stream()
          .filter(
            a -> a.isActive() && ((a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Complete.getStatusId())
              || (a.getActivityStatus() == Integer.parseInt(ProjectStatusEnum.Cancelled.getStatusId())))))
          .collect(Collectors.toList());
        // return openA;
      }
      // sort activities by title number
      openA.sort(Comparator.comparing(this::extractActivityNumber));
      return openA;
    } catch (Exception e) {
      System.err.println("Error getting activities: " + e.getMessage());
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

  public String getMaxYear() {
    return maxYear;
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
        logger.info("PREPARE (draft): Loaded draft with {} activities", 
          project.getProjectActivities() != null ? project.getProjectActivities().size() : "NULL");

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

        List<Activity> activities = new ArrayList<>(Optional
          .ofNullable(this.activityManager.getActiveActivitiesByProject(projectID, this.getActualPhase().getId()))
          .orElse(Collections.emptyList()));
        
        // Siempre inicializar projectActivities para que Struts2 pueda poblarla en HTTP POST
        project.setProjectActivities(new ArrayList<Activity>(activities));
        logger.info("PREPARE (not draft): Initialized projectActivities with {} existing activities", activities.size());
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        
        if (project.getProjectActivities() != null && !project.getProjectActivities().isEmpty()) {
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

      Project projectForDeliverableFilter = project;
      if (this.isDraft() || StringUtils.isNotEmpty(this.getRequest().getParameter(APConstants.TRANSACTION_ID))) {
        projectForDeliverableFilter = projectManager.getProjectById(projectID);
      }
      List<Deliverable> filteredDeliverables = projectForDeliverableFilter.getDeliverables().stream()
        .filter(d -> d.isActive() && d.getDeliverableInfo(this.getActualPhase()) != null)
        .peek(d -> d.setTagTitle(d.getComposedName())).collect(Collectors.toList());

      project.setProjectDeliverables(filteredDeliverables);

      if (project.getProjectInfo() != null) {
        maxYear = String.valueOf(project.getProjectInfo().getEndYear());
      }

      List<ProjectPartner> ProjectPartnerList = new ArrayList<>();

      try {
        ProjectPartnerList = projectPartnerManager.findAllByPhaseProject(projectID, this.getActualPhase().getId());
      } catch (Exception e) {
        logger.error("unable to get ProjectPartner list in prepapre function ");
      }

      // 04/06/2024 cgamboa findAll() was changed by ProjectPartnerList

      partnerPersons = new ArrayList<>();
      for (ProjectPartner partner : ProjectPartnerList.stream()
        .filter(
          pp -> pp.isActive() && pp.getProject().getId() == projectID && pp.getPhase().equals(this.getActualPhase()))
        .collect(Collectors.toList())) {

        for (ProjectPartnerPerson partnerPerson : partner.getProjectPartnerPersons().stream()
          .filter(ppa -> ppa.isActive()).collect(Collectors.toList())) {

          partnerPersons.add(partnerPerson);
        }
      }

      List<ActivityTitle> ActivityTitleList = new ArrayList<>();
      ActivityTitleList = activityTitleManager.findAll();
      activityTitles = new ArrayList<>();

      if (this.isAiccra()) {
        if (ActivityTitleList != null && !ActivityTitleList.isEmpty()) {

          if (activityTitles == null || (activityTitles != null && activityTitles.isEmpty())) {
            activityTitles = ActivityTitleList;// activityTitleManager.findAll();
            activityTitles.sort((a1, a2) -> a1.getTitle().compareTo(a2.getTitle()));
          }
        }
      }

      deliverablesMissingActivity = new ArrayList<>();

      try {
        List<Deliverable> currentDeliverables = project.getCurrentDeliverables(this.getActualPhase());

        if (currentDeliverables != null && !currentDeliverables.isEmpty()) {
          for (Deliverable deliverable : currentDeliverables) {
            if (deliverable == null) {
              continue;
            }

            DeliverableInfo info = deliverable.getDeliverableInfo(this.getActualPhase());
            if (info == null || info.getStatus() == null || info.getStatus() == 5) {
              continue; // skip status 5
            }

            List<DeliverableActivity> activeDA = deliverable.getDeliverableActivities().stream()
              .filter(da -> da != null && da.isActive()).collect(Collectors.toList());

            boolean hasValidAssociation = activeDA.stream()
              .anyMatch(da -> da.getPhase() != null && da.getPhase().getId().equals(this.getActualPhase().getId())
                && da.getActivity() != null && da.getActivity().isActive());

            if (!hasValidAssociation) {
              deliverablesMissingActivity.add(deliverable);
            }
          }
        }

      } catch (Exception e) {
        logger.error("Unable to get deliverables without activities", e);
        deliverablesMissingActivity = new ArrayList<>();
      }

    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_ACTIVITIES_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      // NO usar clear() - Struts2 necesita una lista nueva para popular correctamente
      // Si usamos clear(), Struts2 agrega elementos null en lugar de crear objetos Activity
      logger.info("PREPARE (HTTP POST): Replacing projectActivities list for Struts2 population");
      project.setProjectActivities(new ArrayList<Activity>());

      /*
       * if (project.getClosedProjectActivities() != null) {
       * project.getClosedProjectActivities().clear();
       * }
       */

      // Reemplazar las otras listas también
      partnerPersons = new ArrayList<>();
      activityTitles = new ArrayList<>();
      project.setProjectDeliverables(new ArrayList<>());
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

  /**
   * Manual binding de actividades desde request parameters.
   * Struts2 tiene problemas para auto-poblar listas complejas, especialmente cuando hay índices no consecutivos.
   */
  private void bindActivitiesFromRequest() {
    try {
      List<Activity> activities = new ArrayList<>();
      int index = 0;
      boolean hasMore = true;
      
      logger.debug("bindActivitiesFromRequest: Starting manual binding");
      
      while (hasMore) {
        String titleParam = this.getRequest().getParameter("project.projectActivities[" + index + "].activityTitle.id");
        
        if (titleParam != null) {
          Activity activity = new Activity();
          activity.setId(-1L); // Nueva actividad por defecto
          
          // ID de la actividad (puede ser -1 para nuevas)
          String idParam = this.getRequest().getParameter("project.projectActivities[" + index + "].id");
          if (idParam != null && !idParam.trim().isEmpty()) {
            try {
              activity.setId(Long.parseLong(idParam));
            } catch (NumberFormatException e) {
              logger.warn("Error parsing activity id at index {}: {}", index, e.getMessage());
            }
          }
          
          // Activity Title
          try {
            long activityTitleId = Long.parseLong(titleParam);
            ActivityTitle activityTitle = activityTitleManager.getActivityTitleById(activityTitleId);
            activity.setActivityTitle(activityTitle);
          } catch (NumberFormatException e) {
            logger.warn("Error parsing activityTitle.id at index {}: {}", index, e.getMessage());
          }
          
          // Description
          String description = this.getRequest().getParameter("project.projectActivities[" + index + "].description");
          activity.setDescription(description);
          
          // Start Date
          String startDate = this.getRequest().getParameter("project.projectActivities[" + index + "].startDate");
          if (startDate != null && !startDate.trim().isEmpty()) {
            try {
              // Asumiendo formato yyyy-MM-dd o similar
              activity.setStartDate(java.sql.Date.valueOf(startDate));
            } catch (Exception e) {
              logger.warn("Error parsing startDate at index {}: {}", index, e.getMessage());
            }
          }
          
          // End Date
          String endDate = this.getRequest().getParameter("project.projectActivities[" + index + "].endDate");
          if (endDate != null && !endDate.trim().isEmpty()) {
            try {
              activity.setEndDate(java.sql.Date.valueOf(endDate));
            } catch (Exception e) {
              logger.warn("Error parsing endDate at index {}: {}", index, e.getMessage());
            }
          }
          
          // Activity Progress
          String progress = this.getRequest().getParameter("project.projectActivities[" + index + "].activityProgress");
          activity.setActivityProgress(progress);
          
          // Activity Status
          String status = this.getRequest().getParameter("project.projectActivities[" + index + "].activityStatus");
          if (status != null && !status.trim().isEmpty()) {
            try {
              activity.setActivityStatus(Integer.parseInt(status));
            } catch (NumberFormatException e) {
              logger.warn("Error parsing activityStatus at index {}: {}", index, e.getMessage());
              activity.setActivityStatus(Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()));
            }
          } else {
            activity.setActivityStatus(Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()));
          }
          
          // Partner Person
          String partnerPersonId = this.getRequest().getParameter("project.projectActivities[" + index + "].projectPartnerPerson.id");
          if (partnerPersonId != null && !partnerPersonId.trim().isEmpty()) {
            try {
              long ppId = Long.parseLong(partnerPersonId);
              if (ppId > 0) {
                ProjectPartnerPerson partnerPerson = projectPartnerPersonManager.getProjectPartnerPersonById(ppId);
                activity.setProjectPartnerPerson(partnerPerson);
              }
            } catch (NumberFormatException e) {
              logger.warn("Error parsing projectPartnerPerson.id at index {}: {}", index, e.getMessage());
            }
          }
          
          // Binding de deliverables para esta actividad
          List<DeliverableActivity> deliverables = bindDeliverablesForActivity(index);
          if (deliverables != null && !deliverables.isEmpty()) {
            activity.setDeliverables(deliverables);
            logger.debug("bindActivitiesFromRequest: Bound {} deliverables to activity at index {}", deliverables.size(), index);
          }
          
          activities.add(activity);
          logger.debug("bindActivitiesFromRequest: Bound activity at index {} with title ID: {}", index, titleParam);
          index++;
        } else {
          hasMore = false;
        }
      }
      
      logger.debug("bindActivitiesFromRequest: Successfully bound {} activities", activities.size());
      project.setProjectActivities(activities);
      
    } catch (Exception e) {
      logger.error("Error in bindActivitiesFromRequest", e);
    }
  }

  /**
   * Binding de deliverables para una actividad específica
   */
  private List<DeliverableActivity> bindDeliverablesForActivity(int activityIndex) {
    List<DeliverableActivity> deliverables = new ArrayList<>();
    int deliverableIndex = 0;
    boolean hasMore = true;
    
    while (hasMore) {
      String deliverableIdParam = this.getRequest().getParameter(
        "project.projectActivities[" + activityIndex + "].deliverables[" + deliverableIndex + "].deliverable.id");
      
      if (deliverableIdParam != null && !deliverableIdParam.trim().isEmpty()) {
        try {
          DeliverableActivity delActivity = new DeliverableActivity();
          
          // ID del deliverable
          long deliverableId = Long.parseLong(deliverableIdParam);
          Deliverable deliverable = deliverableManager.getDeliverableById(deliverableId);
          delActivity.setDeliverable(deliverable);
          
          // Phase
          delActivity.setPhase(this.getActualPhase());
          
          deliverables.add(delActivity);
          logger.debug("bindActivitiesFromRequest: Bound deliverable {} to activity {} at deliverable index {}",
            deliverableId, activityIndex, deliverableIndex);
          
          deliverableIndex++;
        } catch (NumberFormatException e) {
          logger.warn("Error parsing deliverable.id at activity index {}, deliverable index {}: {}", 
            activityIndex, deliverableIndex, e.getMessage());
          hasMore = false;
        }
      } else {
        hasMore = false;
      }
    }
    
    return deliverables;
  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {
      
      // Manual binding de actividades desde request parameters
      this.bindActivitiesFromRequest();

      logger.debug("SAVE: project.getProjectActivities() size = {}",
        project.getProjectActivities() != null ? project.getProjectActivities().size() : "NULL");

      // 2024/07/03 gamboa projectBD.getActivities() was changed by this.activityManager.getActiveActivitiesByProject to
      // improve performance
      List<Activity> existingActivities = new ArrayList<>();
      try {
        existingActivities =
          this.activityManager.getActiveActivitiesByProject(projectID, this.getActualPhase().getId());
      } catch (Exception e) {
        logger.warn("Unable to get activities from the database in save()", e);
      }

      // Sync deletions with UI: remove DB rows not present in the submitted list (same snapshot as the old dual-query path when both calls succeeded).
      if (existingActivities != null && !existingActivities.isEmpty()) {
        this.deleteActivities(existingActivities);
      }

      logger.debug("SAVE after delete: project.getProjectActivities() size = {}",
        project.getProjectActivities() != null ? project.getProjectActivities().size() : "NULL");

      try {
        this.saveActivitiesNewData();
      } catch (Exception e) {
        logger.error("Error saving activities", e);
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
    // Log para diagnóstico - verificar si hay actividades para guardar
    if (project.getProjectActivities() == null) {
      logger.warn("saveActivitiesNewData: project.getProjectActivities() is NULL");
      return;
    }
    logger.debug("saveActivitiesNewData: Found {} activities to save", project.getProjectActivities().size());

    for (Activity activityUI : project.getProjectActivities()) {
      if (activityUI == null) {
        logger.warn("saveActivitiesNewData: Found null activity in list, skipping");
        continue;
      }
      logger.debug("saveActivitiesNewData: Processing activity ID={}, title={}", activityUI.getId(), activityUI.getTitle());

      boolean isNew = activityUI.getId() == null || activityUI.getId() == -1;
      Activity activityEntity = isNew ? new Activity() : activityManager.getActivityById(activityUI.getId());


      activityEntity.setProject(project);
      activityEntity.setPhase(this.getActualPhase());
      activityEntity.setActivityTitle(activityUI.getActivityTitle());
      if (activityUI.getActivityTitle() != null && activityUI.getActivityTitle().getTitle() != null) {
        activityEntity.setTitle(activityUI.getActivityTitle().getTitle());
      }
      activityEntity.setDescription(activityUI.getDescription());
      activityEntity.setStartDate(activityUI.getStartDate());
      activityEntity.setEndDate(activityUI.getEndDate());
      activityEntity.setActivityProgress(activityUI.getActivityProgress());

      int status = activityUI.getActivityStatus() != -1 ? activityUI.getActivityStatus()
        : Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId());
      activityEntity.setActivityStatus(status);

      // Partner person
      activityEntity.setProjectPartnerPerson(getValidPartnerPerson(activityUI));

      // Activity title (just for AICCRA)
      handleActivityTitle(activityEntity);

      // Deliverables - guardar tanto para actividades nuevas como existentes
      if (activityUI.getDeliverables() != null && !activityUI.getDeliverables().isEmpty()) {
        activityEntity.setDeliverables(activityUI.getDeliverables());
        logger.debug("saveActivitiesNewData: Activity {} has {} deliverables to save",
          activityUI.getId(), activityUI.getDeliverables().size());
      }

      Activity saved = activityManager.saveActivity(activityEntity);
      project.getActivities().add(saved);
    }


  }

  private ProjectPartnerPerson getValidPartnerPerson(Activity activity) {
    try {
      if (activity.getProjectPartnerPerson() != null && activity.getProjectPartnerPerson().getId() != null
        && activity.getProjectPartnerPerson().getId() > 0) {
        return projectPartnerPersonManager.getProjectPartnerPersonById(activity.getProjectPartnerPerson().getId());
      }
    } catch (Exception e) {
      logger.warn("Invalid partner person for activity: {}", activity.getId());
    }
    return null;
  }

  private void handleActivityTitle(Activity activity) {
    if (this.isAiccra()) {
      if (activity.getActivityTitle() != null && activity.getActivityTitle().getId() != null) {
        ActivityTitle title = activityTitleManager.getActivityTitleById(activity.getActivityTitle().getId());
        if (title != null) {
          activity.setActivityTitle(title);
          activity.setTitle(title.getTitle());
          return;
        }
      }
      activity.setActivityTitle(null);
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

  public void setMaxYear(String maxYear) {
    this.maxYear = maxYear;
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
