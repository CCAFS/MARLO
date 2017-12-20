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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectCommunicationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectNextuserManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.FileDB;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectCommunication;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectNextuser;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.SrfTargetUnit;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.FileManager;
import org.cgiar.ccafs.marlo.validation.projects.ProjectOutcomeValidator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
 * @author Sebastian Amariles - CIAT/CCAFS
 * @author Christian Garcia- CIAT/CCAFS
 */
public class ProjectOutcomeAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 4520862722467820286L;
  private static final Logger LOG = LoggerFactory.getLogger(ProjectOutcomeAction.class);

  private ProjectManager projectManager;
  private ProjectMilestoneManager projectMilestoneManager;
  private ProjectCommunicationManager projectCommunicationManager;
  private CrpManager crpManager;

  private SrfTargetUnitManager srfTargetUnitManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private AuditLogManager auditLogManager;
  private ProjectOutcomeManager projectOutcomeManager;
  private ProjectNextuserManager projectNextuserManager;
  // Front-end
  private long projectID;
  private long projectOutcomeID;
  private Crp loggedCrp;
  private Project project;
  private List<CrpMilestone> milestones;
  private List<CrpMilestone> milestonesProject;

  private List<SrfTargetUnit> targetUnits;


  private CrpProgramOutcome crpProgramOutcome;

  private CrpMilestoneManager crpMilestoneManager;
  private ProjectOutcome projectOutcome;
  private ProjectOutcomeValidator projectOutcomeValidator;
  private String transaction;

  @Inject
  public ProjectOutcomeAction(APConfig config, ProjectManager projectManager, CrpManager crpManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, ProjectOutcomeManager projectOutcomeManager,
    SrfTargetUnitManager srfTargetUnitManager, ProjectMilestoneManager projectMilestoneManager,
    ProjectCommunicationManager projectCommunicationManager, AuditLogManager auditLogManager,
    CrpMilestoneManager crpMilestoneManager, ProjectNextuserManager projectNextuserManager,
    ProjectOutcomeValidator projectOutcomeValidator) {
    super(config);
    this.projectManager = projectManager;
    this.srfTargetUnitManager = srfTargetUnitManager;
    this.crpManager = crpManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.projectOutcomeManager = projectOutcomeManager;
    this.projectMilestoneManager = projectMilestoneManager;
    this.projectCommunicationManager = projectCommunicationManager;
    this.auditLogManager = auditLogManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.projectNextuserManager = projectNextuserManager;
    this.projectOutcomeValidator = projectOutcomeValidator;
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
    String composedClassName = projectOutcome.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = projectOutcome.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public int getIndexCommunication(int year) {

    int i = 0;
    for (ProjectCommunication crpMilestone : projectOutcome.getCommunications()) {

      if (crpMilestone.getYear() == year) {
        return i;
      }
      i++;
    }

    ProjectCommunication com = new ProjectCommunication();
    com.setYear(year);
    projectOutcome.getCommunications().add(com);
    return this.getIndexCommunication(year);

  }


  public int getIndexMilestone(long milestoneId, int year) {

    int i = 0;
    for (ProjectMilestone crpMilestone : projectOutcome.getMilestones()) {

      if (crpMilestone.getCrpMilestone().getId().longValue() == milestoneId && crpMilestone.getYear() == year) {
        return i;
      }
      i++;
    }


    ProjectMilestone projectMilestone = new ProjectMilestone();
    projectMilestone.setYear(year);
    projectMilestone.setCrpMilestone(crpMilestoneManager.getCrpMilestoneById(milestoneId));
    projectOutcome.getMilestones().add(projectMilestone);
    return this.getIndexMilestone(milestoneId, year);
  }


  public ProjectMilestone getMilestone(long milestoneId, int year) {
    ProjectMilestone projectMilestone = new ProjectMilestone();
    if (projectOutcome.getMilestones() != null) {
      int index = this.getIndexMilestone(milestoneId, year);
      if (index != -1) {
        projectMilestone = projectOutcome.getMilestones().get(index);
      } else {
        projectMilestone.setYear(year);
        projectMilestone.setCrpMilestone(crpMilestoneManager.getCrpMilestoneById(milestoneId));
      }
    } else {
      projectMilestone.setYear(year);
      projectMilestone.setCrpMilestone(crpMilestoneManager.getCrpMilestoneById(milestoneId));
    }

    return projectMilestone;


  }

  public List<CrpMilestone> getMilestones() {
    return milestones;
  }


  public List<CrpMilestone> getMilestonesbyYear(int year) {
    List<CrpMilestone> milestoneList =
      milestones.stream().filter(c -> c.getYear() >= year).collect(Collectors.toList());
    return milestoneList;
  }


  public List<CrpMilestone> getMilestonesProject() {
    return milestonesProject;
  }

  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }

  public ProjectOutcome getProjectOutcome() {
    return projectOutcome;
  }


  public long getProjectOutcomeID() {
    return projectOutcomeID;
  }


  /**
   * Return the absolute path where the work plan is or should be located.
   * 
   * @param workplan name
   * @return complete path where the image is stored
   */
  private String getSummaryAbsolutePath() {
    return config.getUploadsBaseFolder() + File.separator + this.getSummaryPath() + File.separator;
  }


  private String getSummaryPath() {

    return config.getProjectsBaseFolder(loggedCrp.getAcronym()) + File.separator + project.getId() + File.separator
      + "outcome" + File.separator;
  }

  public String getSummaryURL() {
    return config.getDownloadURL() + "/" + this.getSummaryPath().replace('\\', '/');
  }

  public List<SrfTargetUnit> getTargetUnits() {
    return targetUnits;
  }


  public String getTransaction() {
    return transaction;
  }

  public ProjectCommunication loadProjectCommunication(int year) {

    List<ProjectCommunication> projectCommunications =
      projectOutcome.getCommunications().stream().filter(c -> c.getYear() == year).collect(Collectors.toList());


    if (projectCommunications.size() > 0) {
      return projectCommunications.get(0);
    }

    return new ProjectCommunication();


  }


  public List<ProjectMilestone> loadProjectMilestones(int year) {

    List<ProjectMilestone> projectMilestones =
      projectOutcome.getMilestones().stream().filter(c -> c.getYear() == year).collect(Collectors.toList());

    return projectMilestones;


  }

  @Override
  public void prepare() throws Exception {

    // Get current CRP
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    try {
      projectOutcomeID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_OUTCOME_REQUEST_ID)));

      ProjectOutcome outcome = projectOutcomeManager.getProjectOutcomeById(projectOutcomeID);
      if (!outcome.getPhase().equals(this.getActualPhase())) {
        List<ProjectOutcome> projectOutcomes = outcome.getProject().getProjectOutcomes().stream()
          .filter(c -> c.isActive()
            && c.getCrpProgramOutcome().getComposeID().equals(outcome.getCrpProgramOutcome().getComposeID())
            && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList());
        if (!projectOutcomes.isEmpty()) {
          projectOutcomeID = projectOutcomes.get(0).getId();
        }
      }
    } catch (Exception e) {
      LOG.error("unable to parse projectOutcomeID", e);
      /**
       * Original code swallows the exception and didn't even log it. Now we at least log it,
       * but we need to revisit to see if we should continue processing or re-throw the exception.
       */
    }

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      ProjectOutcome history = (ProjectOutcome) auditLogManager.getHistory(transaction);
      if (history != null) {
        projectOutcome = history;
      } else {
        this.transaction = null;

        this.setTransaction("-1");
      }
    } else {
      projectOutcome = projectOutcomeManager.getProjectOutcomeById(projectOutcomeID);
    }


    if (projectOutcome != null) {

      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;

        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();


        AutoSaveReader autoSaveReader = new AutoSaveReader();

        projectOutcome = (ProjectOutcome) autoSaveReader.readFromJson(jReader);


        this.setDraft(true);
        project = projectManager.getProjectById(projectOutcome.getProject().getId());
        projectID = project.getId();
        Project projectDb = projectManager.getProjectById(project.getId());
        project.setProjectInfo(projectDb.getProjecInfoPhase(this.getActualPhase()));
        List<ProjectMilestone> milestones = new ArrayList<>();

        if (projectOutcome.getMilestones() != null) {
          for (ProjectMilestone crpMilestone : projectOutcome.getMilestones()) {
            if (crpMilestone.getCrpMilestone() != null) {
              crpMilestone
                .setCrpMilestone(crpMilestoneManager.getCrpMilestoneById(crpMilestone.getCrpMilestone().getId()));
              milestones.add(crpMilestone);
            }

          }
        }
        projectOutcome.setMilestones(milestones);

      } else {
        this.setDraft(false);
        project = projectManager.getProjectById(projectOutcome.getProject().getId());
        projectID = project.getId();
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));

        projectOutcome.setMilestones(
          projectOutcome.getProjectMilestones().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

        projectOutcome.setCommunications(
          projectOutcome.getProjectCommunications().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        projectOutcome.setNextUsers(
          projectOutcome.getProjectNextusers().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

        if (this.isLessonsActive()) {
          this.loadLessonsOutcome(loggedCrp, projectOutcome);
        }

      }

    }


    Set<CrpMilestone> crpMilestones = new HashSet<>();
    if (projectOutcome.getMilestones() != null)

    {
      for (ProjectMilestone crpMilestone : projectOutcome.getMilestones()) {
        CrpMilestone milestone = crpMilestoneManager.getCrpMilestoneById(crpMilestone.getCrpMilestone().getId());

        milestone.setIndex(crpMilestone.getId());
        crpMilestones.add(milestone);

      }

    }
    milestonesProject = new ArrayList<>();
    milestonesProject.addAll(crpMilestones);
    // Collections.sort(milestonesProject, (m1, m2) -> m1.getIndex().compareTo(m2.getIndex()));
    if (projectOutcome != null)

    {
      crpProgramOutcome =
        crpProgramOutcomeManager.getCrpProgramOutcomeById(projectOutcome.getCrpProgramOutcome().getId());

      projectOutcome.setCrpProgramOutcome(crpProgramOutcome);

      milestones = projectOutcome.getCrpProgramOutcome().getCrpMilestones().stream().filter(c -> c.isActive())
        .collect(Collectors.toList());
    }
    /*
     * Loading basic List
     */
    targetUnits = srfTargetUnitManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_CONTRIBRUTIONCRP_BASE_PERMISSION, params));
    if (this.isHttpPost())

    {
      /**
       * This might seem very strange what is going on here, but this is due to issue #1124. The Struts2 Prepare
       * interceptor will set the values on the projectOutcome entity during save, but if we leave the values here and
       * they ids change then Hibernate will be unhappy.
       */
      if (projectOutcome.getMilestones() != null) {
        projectOutcome.getMilestones().clear();
      }
      if (projectOutcome.getCommunications() != null) {
        projectOutcome.getCommunications().clear();
      }

      if (projectOutcome.getNextUsers() != null) {
        projectOutcome.getNextUsers().clear();
      }

      /**
       * Hack to fix ManyToOne issue as a result of issue #1124
       */
      projectOutcome.setAchievedUnit(null);
      projectOutcome.setExpectedUnit(null);
    }

  }


  @Override
  public String save() {


    if (this.hasPermission("canEdit")) {

      ProjectOutcome projectOutcomeDB = this.saveProjectOutcome();
      this.saveMilestones(projectOutcomeDB);
      this.saveCommunications(projectOutcomeDB);
      this.saveNextUsers(projectOutcomeDB);

      if (this.isLessonsActive()) {
        this.saveLessonsOutcome(loggedCrp, projectOutcome);
      }
      // projectOutcome = projectOutcomeManager.getProjectOutcomeById(projectOutcomeID);
      projectOutcome.setModifiedBy(this.getCurrentUser());
      projectOutcome.setActiveSince(new Date());
      projectOutcome.setPhase(this.getActualPhase());
      projectOutcome.setModificationJustification(this.getJustification());
      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_OUTCOMES_MILESTONE_RELATION);
      relationsName.add(APConstants.PROJECT_OUTCOMES_COMMUNICATION_RELATION);
      relationsName.add(APConstants.PROJECT_NEXT_USERS_RELATION);
      relationsName.add(APConstants.PROJECT_OUTCOME_LESSONS_RELATION);
      projectOutcomeManager.saveProjectOutcome(projectOutcome, this.getActionName(), relationsName,
        this.getActualPhase());

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
    } else

    {

      return NOT_AUTHORIZED;
    }
  }


  public void saveCommunications(ProjectOutcome projectOutcomeDB) {

    for (ProjectCommunication projectCommunication : projectOutcomeDB.getProjectCommunications().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {

      if (projectOutcome.getCommunications() == null) {
        projectOutcome.setCommunications(new ArrayList<>());
      }
      if (!projectOutcome.getCommunications().contains(projectCommunication)) {
        projectCommunicationManager.deleteProjectCommunication(projectCommunication.getId());

      }
    }

    if (projectOutcome.getCommunications() != null) {
      for (ProjectCommunication projectCommunication : projectOutcome.getCommunications()) {
        if (projectCommunication != null) {
          if (projectCommunication.getId() == null || projectCommunication.getId() == -1L) {
            // New entity
            projectCommunication.setCreatedBy(this.getCurrentUser());

            projectCommunication.setActiveSince(new Date());
            projectCommunication.setActive(true);
            projectCommunication.setProjectOutcome(projectOutcomeDB);
            projectCommunication.setModifiedBy(this.getCurrentUser());
            projectCommunication.setModificationJustification("");

            if (projectCommunication.getFile() != null) {

              FileDB summary = this.getFileDB(null, projectCommunication.getFile(),
                projectCommunication.getFileFileName(), this.getSummaryAbsolutePath());

              projectCommunication.setSummary(summary);
              FileManager.copyFile(projectCommunication.getFile(),
                this.getSummaryAbsolutePath() + projectCommunication.getFileFileName());
            }

            if (projectCommunication.getSummary().getFileName().isEmpty()) {
              projectCommunication.setSummary(null);
            }

            projectCommunication = projectCommunicationManager.saveProjectCommunication(projectCommunication);

          } else {
            // Update existing entity.
            ProjectCommunication projectCommunicationDB =
              projectCommunicationManager.getProjectCommunicationById(projectCommunication.getId());

            projectCommunicationDB.setActive(true);
            projectCommunicationDB.setProjectOutcome(projectOutcomeDB);
            projectCommunicationDB.setModifiedBy(this.getCurrentUser());
            projectCommunicationDB.setModificationJustification("");

            if (projectCommunication.getFile() != null) {

              FileDB summary = this.getFileDB(projectCommunicationDB.getSummary(), projectCommunication.getFile(),
                projectCommunication.getFileFileName(), this.getSummaryAbsolutePath());

              projectCommunicationDB.setSummary(summary);
              FileManager.copyFile(projectCommunication.getFile(),
                this.getSummaryAbsolutePath() + projectCommunication.getFileFileName());
            }

            if (projectCommunication.getSummary().getFileName().isEmpty()) {
              projectCommunicationDB.setSummary(null);
            }

            projectCommunicationDB = projectCommunicationManager.saveProjectCommunication(projectCommunicationDB);

          }


        }


      }
    }
  }


  public void saveMilestones(ProjectOutcome projectOutcomeDB) {

    for (ProjectMilestone projectMilestone : projectOutcomeDB.getProjectMilestones().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {

      if (projectOutcome.getMilestones() == null) {
        projectOutcome.setMilestones(new ArrayList<>());
      }
      if (!projectOutcome.getMilestones().contains(projectMilestone)) {
        projectMilestoneManager.deleteProjectMilestone(projectMilestone.getId());

      }
    }

    if (projectOutcome.getMilestones() != null) {
      for (ProjectMilestone projectMilestone : projectOutcome.getMilestones()) {
        if (projectMilestone != null) {
          // Add new entity
          if (projectMilestone.getId() == null) {
            projectMilestone.setCreatedBy(this.getCurrentUser());

            projectMilestone.setActiveSince(new Date());
            projectMilestone.setActive(true);
            projectMilestone.setProjectOutcome(projectOutcomeDB);
            projectMilestone.setModifiedBy(this.getCurrentUser());
            projectMilestone.setModificationJustification("");

            if (projectMilestone.getExpectedUnit() != null) {
              if (projectMilestone.getExpectedUnit().getId() == null
                || projectMilestone.getExpectedUnit().getId().longValue() == -1) {
                projectMilestone.setExpectedUnit(null);
              }
            }
            projectMilestoneManager.saveProjectMilestone(projectMilestone);

          } else {
            // Update existing entity.
            ProjectMilestone projectMilestoneDB =
              projectMilestoneManager.getProjectMilestoneById(projectMilestone.getId());

            projectMilestoneDB.setActive(true);
            projectMilestoneDB.setProjectOutcome(projectOutcomeDB);
            projectMilestoneDB.setModifiedBy(this.getCurrentUser());
            projectMilestoneDB.setModificationJustification("");
            /**
             * Set fields from non managed entity to managed entity (double check if these fields are editable in the
             * client)
             */
            projectMilestoneDB.setAchievedValue(projectMilestone.getAchievedValue());
            projectMilestoneDB.setCrpMilestone(projectMilestone.getCrpMilestone());
            projectMilestoneDB.setExpectedValue(projectMilestone.getExpectedValue());
            projectMilestoneDB.setNarrativeAchieved(projectMilestone.getNarrativeAchieved());
            projectMilestoneDB.setNarrativeTarget(projectMilestone.getNarrativeTarget());
            projectMilestoneDB.setYear(projectMilestone.getYear());

            if (projectMilestone.getExpectedUnit() != null) {
              if (projectMilestone.getExpectedUnit().getId() == null
                || projectMilestone.getExpectedUnit().getId().longValue() == -1) {
                projectMilestone.setExpectedUnit(null);
              } else {
                projectMilestoneDB.setExpectedUnit(projectMilestone.getExpectedUnit());
              }
            }

            projectMilestoneDB = projectMilestoneManager.saveProjectMilestone(projectMilestoneDB);

          }

        }

      }
    }
  }


  public void saveNextUsers(ProjectOutcome projectOutcomeDB) {

    for (ProjectNextuser projectNextuser : projectOutcomeDB.getProjectNextusers().stream().filter(c -> c.isActive())
      .collect(Collectors.toList())) {

      if (projectOutcome.getNextUsers() == null) {
        projectOutcome.setNextUsers(new ArrayList<>());
      }
      if (!projectOutcome.getNextUsers().contains(projectNextuser)) {
        projectNextuserManager.deleteProjectNextuser(projectNextuser.getId());

      }
    }

    if (projectOutcome.getNextUsers() != null) {
      for (ProjectNextuser projectNextuser : projectOutcome.getNextUsers()) {
        if (projectNextuser != null) {
          if (projectNextuser.getId() == null) {
            // Create new entity
            projectNextuser.setCreatedBy(this.getCurrentUser());

            projectNextuser.setActiveSince(new Date());
            projectNextuser.setActive(true);
            projectNextuser.setProjectOutcome(projectOutcomeDB);
            projectNextuser.setModifiedBy(this.getCurrentUser());
            projectNextuser.setModificationJustification("");

            projectNextuserManager.saveProjectNextuser(projectNextuser);

          } else {
            // Update existing entity
            ProjectNextuser projectNextuserDB = projectNextuserManager.getProjectNextuserById(projectNextuser.getId());

            projectNextuserDB.setActive(true);
            projectNextuserDB.setProjectOutcome(projectOutcomeDB);
            projectNextuserDB.setModifiedBy(this.getCurrentUser());
            projectNextuserDB.setModificationJustification("");

            // update existing fields
            projectNextuserDB.setKnowledge(projectNextuser.getKnowledge());
            projectNextuserDB.setNextUser(projectNextuser.getNextUser());
            projectNextuserDB.setStrategies(projectNextuser.getStrategies());

            projectNextuserDB = projectNextuserManager.saveProjectNextuser(projectNextuserDB);
          }


        }

      }
    }
  }

  public ProjectOutcome saveProjectOutcome() {


    // ProjectOutcome projectOutcomeDB = new ProjectOutcome();


    Calendar startDate = Calendar.getInstance();
    startDate.setTime(project.getProjecInfoPhase(this.getActualPhase()).getStartDate());

    Calendar endDate = Calendar.getInstance();
    endDate.setTime(project.getProjecInfoPhase(this.getActualPhase()).getEndDate());
    int endYear = endDate.get(Calendar.YEAR);

    if (this.isPlanningActive()) {
      // projectOutcomeDB = projectOutcomeManager.getProjectOutcomeById(projectOutcomeID);
      projectOutcome.setActive(true);
      projectOutcome.setModifiedBy(this.getCurrentUser());
      projectOutcome.setCreatedBy(this.getCurrentUser());
      projectOutcome.setActiveSince(new Date());
      projectOutcome.setCrpProgramOutcome(crpProgramOutcome);
      projectOutcome.setProject(project);
      if (projectOutcome.getExpectedUnit() != null && projectOutcome.getExpectedUnit().getId() == null) {
        projectOutcome.setExpectedUnit(null);
      } else {
        projectOutcome.setExpectedUnit(projectOutcome.getExpectedUnit());
      }
      projectOutcome.setId(projectOutcomeID);
      projectOutcome.setPhase(this.getActualPhase());

      projectOutcome.setModificationJustification("");
      projectOutcome = projectOutcomeManager.saveProjectOutcome(projectOutcome);

    }

    return projectOutcome;

  }

  public void setMilestones(List<CrpMilestone> milestones) {
    this.milestones = milestones;
  }


  public void setMilestonesProject(List<CrpMilestone> milestonesProject) {
    this.milestonesProject = milestonesProject;
  }

  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setProjectOutcome(ProjectOutcome projectOutcome) {
    this.projectOutcome = projectOutcome;
  }

  public void setProjectOutcomeID(long projectOutcomeID) {
    this.projectOutcomeID = projectOutcomeID;
  }

  public void setTargetUnits(List<SrfTargetUnit> targetUnits) {
    this.targetUnits = targetUnits;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      projectOutcomeValidator.validate(this, projectOutcome, true);
    }
  }

}

