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
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectCommunicationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectNextuserManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.SrfTargetUnitManager;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcomeIndicator;
import org.cgiar.ccafs.marlo.data.model.FileDB;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectCommunication;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectNextuser;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcomeIndicator;
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
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
 * @author Sebastian Amariles - CIAT/CCAFS
 * @author Christian Garcia- CIAT/CCAFS
 */
public class ProjectOutcomeAction extends BaseAction {

  private static final long serialVersionUID = 4520862722467820286L;

  private static final Logger LOG = LoggerFactory.getLogger(ProjectOutcomeAction.class);

  // Managers
  private ProjectManager projectManager;
  private ProjectMilestoneManager projectMilestoneManager;
  private ProjectCommunicationManager projectCommunicationManager;
  private GlobalUnitManager crpManager;
  private SrfTargetUnitManager srfTargetUnitManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private AuditLogManager auditLogManager;
  private ProjectOutcomeManager projectOutcomeManager;
  private ProjectNextuserManager projectNextuserManager;
  private ProjectOutcomeIndicatorManager projectOutcomeIndicatorManager;
  private CrpMilestoneManager crpMilestoneManager;
  private PhaseManager phaseManager;

  // Front-end
  private long projectID;
  private long projectOutcomeID;
  private GlobalUnit loggedCrp;
  private Project project;
  private List<CrpMilestone> milestones;
  private List<CrpMilestone> milestonesProject;
  private List<SrfTargetUnit> targetUnits;
  private CrpProgramOutcome crpProgramOutcome;
  private ProjectOutcome projectOutcome;
  private ProjectOutcome projectOutcomeLastPhase;
  private ProjectOutcomeValidator projectOutcomeValidator;
  private String transaction;
  private ProjectOutcome projectOutcomeDB;
  private boolean editOutcomeExpectedValue;
  private boolean editMilestoneExpectedValue;

  @Inject
  public ProjectOutcomeAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, ProjectOutcomeManager projectOutcomeManager,
    SrfTargetUnitManager srfTargetUnitManager, ProjectMilestoneManager projectMilestoneManager,
    ProjectCommunicationManager projectCommunicationManager, AuditLogManager auditLogManager,
    CrpMilestoneManager crpMilestoneManager, ProjectNextuserManager projectNextuserManager,
    ProjectOutcomeValidator projectOutcomeValidator, ProjectOutcomeIndicatorManager projectOutcomeIndicatorManager,
    PhaseManager phaseManager) {
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
    this.projectOutcomeIndicatorManager = projectOutcomeIndicatorManager;
    this.phaseManager = phaseManager;
  }

  public void addAllCrpMilestones() {
    if (projectOutcome != null && milestones != null) {
      List<ProjectMilestone> projectMilestones = new ArrayList<>();
      for (CrpMilestone crpMilestone : milestones) {
        ProjectMilestone projectMilestone = new ProjectMilestone();
        projectMilestone.setCrpMilestone(crpMilestone);
        projectMilestone.setProjectOutcome(projectOutcome);

        if (crpMilestone.getExtendedYear() != null) {
          projectMilestone.setYear(crpMilestone.getExtendedYear());
        } else if (crpMilestone.getYear() != null) {
          projectMilestone.setYear(crpMilestone.getYear());
        }


        if (projectOutcome.getMilestones() != null && !projectOutcome.getMilestones().isEmpty()) {

          boolean exist = false;
          for (ProjectMilestone prevProjectMilestone : projectOutcome.getMilestones()) {
            if (prevProjectMilestone.getCrpMilestone() != null && prevProjectMilestone.getCrpMilestone() != null
              && crpMilestone != null && crpMilestone.getId() != null
              && prevProjectMilestone.getCrpMilestone().getId().equals(crpMilestone.getId())
              && prevProjectMilestone.getProjectOutcome() != null
              && prevProjectMilestone.getProjectOutcome().getId() != null
              && prevProjectMilestone.getProjectOutcome().getId().equals(projectOutcome.getId())) {
              exist = true;
            }
          }

          if (exist == false) {
            // If not exist previously this project Milestone then it is added to the list
            projectMilestone = projectMilestoneManager.saveProjectMilestone(projectMilestone);
            projectMilestones.add(projectMilestone);
          }

        } else {
          projectMilestone = projectMilestoneManager.saveProjectMilestone(projectMilestone);
          projectMilestones.add(projectMilestone);
        }
      }

      if (projectMilestones != null && !projectMilestones.isEmpty()) {
        projectOutcome.setMilestones(projectMilestones);
      }
    }
  }

  public void canBeEditedMilestoneExpectedValue() {
    editMilestoneExpectedValue = false;
    // Modify the editExpectedValue value for AICCRA
    if (this.isAiccra()) {
      if (!this.isAdmin()) {
        if (projectOutcome != null && projectOutcome.getMilestones() != null) {

          for (ProjectMilestone milestone : projectOutcome.getMilestones()) {
            if (milestone.getExpectedValue() == null
              || (milestone.getExpectedValue() != null && milestone.getExpectedValue() == 0.0)) {
              // Null expected value
              editMilestoneExpectedValue = true;
            } else {
              // Not null Expected value
              editMilestoneExpectedValue = false;
            }
          }

        } else {
          // Null project outcome
          editMilestoneExpectedValue = false;
        }
      } else {
        // User is admin
        editMilestoneExpectedValue = true;
      }
    }
  }

  public void canBeEditedOutcomeExpectedValue() {
    editOutcomeExpectedValue = false;
    // Modify the editExpectedValue value for AICCRA
    if (this.isAiccra()) {
      if (!this.isAdmin()) {
        if (projectOutcome != null) {
          if (projectOutcome.getExpectedValue() == null
            || (projectOutcome.getExpectedValue() != null && projectOutcome.getExpectedValue() == 0.0)) {
            // Null expected value
            editOutcomeExpectedValue = true;
          } else {
            // Not null Expected value
            editOutcomeExpectedValue = false;
          }
        } else {
          // Null project outcome
          editOutcomeExpectedValue = false;
        }
      } else {
        // User is admin
        editOutcomeExpectedValue = true;
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

  public double defineProjectOutcomeOrder(ProjectOutcome projectOutcome) {
    double orderIndex = 1;
    if (projectOutcome != null && projectOutcome.getCrpProgramOutcome() != null
      && projectOutcome.getCrpProgramOutcome().getDescription() != null) {
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("Indicator 1")) {
        orderIndex = 1;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("Indicator 2")) {
        orderIndex = 2;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("Indicator 3")) {
        orderIndex = 3;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("1.1")) {
        orderIndex = 11;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("1.2")) {
        orderIndex = 12;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("1.3")) {
        orderIndex = 13;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("1.4")) {
        orderIndex = 14;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("1.5")) {
        orderIndex = 15;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("2.1")) {
        orderIndex = 21;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("2.1")) {
        orderIndex = 21;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("2.2")) {
        orderIndex = 22;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("2.3")) {
        orderIndex = 23;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("2.4")) {
        orderIndex = 24;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("2.5")) {
        orderIndex = 25;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("3.1")) {
        orderIndex = 31;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("3.1")) {
        orderIndex = 31;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("3.2")) {
        orderIndex = 32;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("3.3")) {
        orderIndex = 33;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("3.4")) {
        orderIndex = 34;
      }
      if (projectOutcome.getCrpProgramOutcome().getDescription().contains("3.5")) {
        orderIndex = 35;
      }
    }
    return orderIndex;
  }

  private Path getAutoSaveFilePath() {
    String composedClassName = projectOutcome.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = projectOutcome.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public String getBaseLineFileURL(String outcomeID) {
    return config.getDownloadURL() + "/file.do?" + this.getBaseLineFileUrlPath(outcomeID).replace('\\', '/');
  }

  public String getBaseLineFileUrlPath(String outcomeID) {
    return "crp=" + this.getActualPhase().getCrp().getAcronym() + "&category=projects&id=" + outcomeID;
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

  public int getIndexIndicator(Long indicatorID) {

    ProjectOutcomeIndicator projectOutcomeIndicator = this.getIndicator(indicatorID);
    if (projectOutcomeIndicator != null && projectOutcome.getIndicators() != null) {
      int i = 0;
      for (ProjectOutcomeIndicator projectOutcomeIndicatorList : projectOutcome.getIndicators()) {
        if (projectOutcomeIndicatorList.getCrpProgramOutcomeIndicator().getId().longValue() == projectOutcomeIndicator
          .getCrpProgramOutcomeIndicator().getId().longValue()) {
          return i;
        }
        i++;
      }
    }
    return 0;
  }

  public int getIndexMilestone(long milestoneId, int year) {

    int i = 0;
    for (ProjectMilestone crpMilestone : projectOutcome.getMilestones()) {

      if (crpMilestone.getCrpMilestone().getId().longValue() == milestoneId) {
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

  public ProjectOutcomeIndicator getIndicator(Long indicatorID) {
    for (ProjectOutcomeIndicator projectOutcomeIndicator : projectOutcome.getIndicators()) {
      if (projectOutcomeIndicator.getCrpProgramOutcomeIndicator().getId().longValue() == indicatorID) {
        return projectOutcomeIndicator;
      }
    }
    ProjectOutcomeIndicator projectOutcomeIndicator = new ProjectOutcomeIndicator();
    projectOutcomeIndicator.setCrpProgramOutcomeIndicator(new CrpProgramOutcomeIndicator(indicatorID));
    projectOutcome.getIndicators().add(projectOutcomeIndicator);
    return projectOutcomeIndicator;
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

  public ProjectOutcomeIndicator getPreIndicator(Long indicatorID) {
    for (ProjectOutcomeIndicator projectOutcomeIndicator : projectOutcome.getIndicators()) {
      if (projectOutcomeIndicator.getCrpProgramOutcomeIndicator().getId().longValue() == indicatorID) {
        return projectOutcomeIndicator;
      }
    }
    ProjectOutcomeIndicator projectOutcomeIndicator = new ProjectOutcomeIndicator();
    projectOutcomeIndicator.setCrpProgramOutcomeIndicator(new CrpProgramOutcomeIndicator(indicatorID));
    projectOutcome.getIndicators().add(projectOutcomeIndicator);
    return projectOutcomeIndicator;

  }

  public int getPrevIndexIndicator(Long indicatorID) {
    if (this.getPrevIndicator(indicatorID) == null && this.getPrevIndicator(indicatorID - 1) != null) {
      indicatorID = indicatorID - 1;
    }
    if (this.getPrevIndicator(indicatorID) != null) {
      ProjectOutcomeIndicator projectOutcomeIndicator = this.getPrevIndicator(indicatorID);
      int i = 0;
      for (ProjectOutcomeIndicator projectOutcomeIndicatorList : projectOutcomeLastPhase.getIndicators()) {
        if (projectOutcomeIndicatorList.getCrpProgramOutcomeIndicator().getId().longValue() == projectOutcomeIndicator
          .getCrpProgramOutcomeIndicator().getId().longValue()) {
          return i;
        }
        i++;
      }
    }
    return 0;
  }

  public ProjectOutcomeIndicator getPrevIndicator(Long indicatorID) {
    for (ProjectOutcomeIndicator projectOutcomeIndicator : projectOutcomeLastPhase.getIndicators()) {
      if (projectOutcomeIndicator.getCrpProgramOutcomeIndicator().getId().longValue() == indicatorID) {
        return projectOutcomeIndicator;
      }
    }
    ProjectOutcomeIndicator projectOutcomeIndicator = new ProjectOutcomeIndicator();
    projectOutcomeIndicator.setCrpProgramOutcomeIndicator(new CrpProgramOutcomeIndicator(indicatorID));
    projectOutcomeLastPhase.getIndicators().add(projectOutcomeIndicator);
    return projectOutcomeIndicator;

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

  public ProjectOutcome getProjectOutcomeLastPhase() {
    return projectOutcomeLastPhase;
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

  public boolean isEditMilestoneExpectedValue() {
    return editMilestoneExpectedValue;
  }

  public boolean isEditOutcomeExpectedValue() {
    return editOutcomeExpectedValue;
  }


  public boolean isExpectedValueEditable(Long milestoneId) {
    boolean editable = false;
    if (milestoneId != null && milestoneId != 0) {
      // Modify the editExpectedValue value for AICCRA
      if (this.isAiccra()) {
        if (!this.isAdmin()) {
          ProjectMilestone projectMilestone = new ProjectMilestone();
          if (projectMilestoneManager.getProjectMilestoneById(milestoneId) != null) {
            projectMilestone = projectMilestoneManager.getProjectMilestoneById(milestoneId);

            if (projectMilestone.getExpectedValue() == null
              || (projectMilestone.getExpectedValue() != null && projectMilestone.getExpectedValue() == 0.0)) {
              // Null expected value
              editable = true;
            } else {
              // Not null Expected value
              editable = false;
            }
          }

        } else {
          // User is admin
          editable = true;
        }
      }
    } else {
      editable = true;
    }
    return editable;
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
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

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
        if (this.hasSpecificities(APConstants.CRP_SHOW_PROJECT_OUTCOME_COMMUNICATIONS)) {
          projectOutcome.setCommunications(
            projectOutcome.getProjectCommunications().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        }
        projectOutcome.setNextUsers(
          projectOutcome.getProjectNextusers().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        /*
         * if (projectOutcome.getProjectOutcomeIndicators().stream()
         * .filter(
         * c -> c.isActive() && c.getPhase() != null && c.getPhase().getId().equals(this.getActualPhase().getId()))
         * .collect(Collectors.toList()) != null
         * && !projectOutcome.getProjectOutcomeIndicators().stream()
         * .filter(c -> c.isActive() && c.getPhase().getId().equals(this.getActualPhase().getId()))
         * .collect(Collectors.toList()).isEmpty()) {
         * projectOutcome.setIndicators(projectOutcome.getProjectOutcomeIndicators().stream().filter(c -> c.isActive())
         * .collect(Collectors.toList()));
         * } else {
         */

        if (this.isReportingActive()) {
          Phase previousPhase = phaseManager.findPreviousPhase(this.getActualPhase().getId());
          // get Project outcome for the last phase
          List<ProjectOutcome> outcomesLastPhase = new ArrayList<>();
          outcomesLastPhase = projectOutcomeManager
            .getProjectOutcomeByProgramOutcomeAndProject(projectOutcome.getCrpProgramOutcome().getId(), projectID);
          if (outcomesLastPhase != null && !outcomesLastPhase.isEmpty() && previousPhase != null) {
            outcomesLastPhase = outcomesLastPhase.stream()
              .filter(o -> o.getPhase() != null && o.getPhase().getId().equals(previousPhase.getId()))
              .collect(Collectors.toList());
          }
          if (outcomesLastPhase != null && !outcomesLastPhase.isEmpty() && outcomesLastPhase.get(0) != null) {
            projectOutcomeLastPhase = outcomesLastPhase.get(0);
            if (projectOutcomeLastPhase != null && projectOutcomeLastPhase.getId() != null) {
              projectOutcomeLastPhase = projectOutcomeManager.getProjectOutcomeById(projectOutcomeLastPhase.getId());
            }
            projectOutcomeLastPhase.setIndicators(projectOutcomeLastPhase.getProjectOutcomeIndicators().stream()
              .filter(c -> c.isActive()).collect(Collectors.toList()));
          }
        }
        projectOutcome.setIndicators(
          projectOutcome.getProjectOutcomeIndicators().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        // }
        if (this.isLessonsActive()) {
          this.loadLessonsOutcome(loggedCrp, projectOutcome);
        }

      }

    }


    Set<CrpMilestone> crpMilestones = new HashSet<>();
    if (projectOutcome.getMilestones() != null) {
      for (ProjectMilestone crpMilestone : projectOutcome.getMilestones()) {
        CrpMilestone milestone = crpMilestoneManager.getCrpMilestoneById(crpMilestone.getCrpMilestone().getId());

        milestone.setIndex(crpMilestone.getId());
        crpMilestones.add(milestone);
        // }
      }
    }

    milestonesProject = new ArrayList<>();
    milestonesProject.addAll(crpMilestones);
    milestonesProject.sort(Comparator.comparing(CrpMilestone::getYear));
    // Collections.sort(milestonesProject, (m1, m2) -> m1.getIndex().compareTo(m2.getIndex()));

    if (this.isReportingActive()) {
      if (projectOutcomeLastPhase != null) {
        CrpProgramOutcome crpProgramOutcomeLastPhase;
        crpProgramOutcomeLastPhase =
          crpProgramOutcomeManager.getCrpProgramOutcomeById(projectOutcomeLastPhase.getCrpProgramOutcome().getId());

        projectOutcomeLastPhase.setCrpProgramOutcome(crpProgramOutcomeLastPhase);
      }

      /*
       * Loading basic List
       */
      if (projectOutcomeLastPhase != null && projectOutcomeLastPhase.getCrpProgramOutcome() != null
        && projectOutcomeLastPhase.getCrpProgramOutcome().getId() != null) {
        projectOutcomeLastPhase.setCrpProgramOutcome(
          crpProgramOutcomeManager.getCrpProgramOutcomeById(projectOutcomeLastPhase.getCrpProgramOutcome().getId()));
      }

      if (projectOutcomeLastPhase != null && projectOutcomeLastPhase.getCrpProgramOutcome() != null
        && projectOutcomeLastPhase.getCrpProgramOutcome().getCrpProgramOutcomeIndicators() != null
        && projectOutcomeLastPhase.getCrpProgramOutcome().getCrpProgramOutcomeIndicators().stream()
          .filter(c -> c.isActive()).sorted((d1, d2) -> d1.getIndicator().compareTo((d2.getIndicator())))
          .collect(Collectors.toList()) != null) {
        projectOutcomeLastPhase.getCrpProgramOutcome()
          .setIndicators(projectOutcomeLastPhase.getCrpProgramOutcome().getCrpProgramOutcomeIndicators().stream()
            .filter(c -> c.isActive()).sorted((d1, d2) -> d1.getIndicator().compareTo((d2.getIndicator())))
            .collect(Collectors.toList()));
      }

      /*
       * List<CrpProgramOutcomeIndicator> indicators = new ArrayList<>();
       * indicators = crpProgramOutcomeIndicatorManager
       * .getCrpProgramOutcomeIndicatorByOutcome(projectOutcomeLastPhase.getCrpProgramOutcome());
       * if (indicators != null && !indicators.isEmpty()) {
       * projectOutcomeLastPhase.getCrpProgramOutcome().setIndicators(indicators);
       * }
       */
    }

    if (projectOutcome != null) {
      crpProgramOutcome =
        crpProgramOutcomeManager.getCrpProgramOutcomeById(projectOutcome.getCrpProgramOutcome().getId());

      projectOutcome.setCrpProgramOutcome(crpProgramOutcome);

      milestones = projectOutcome.getCrpProgramOutcome().getCrpMilestones().stream().filter(c -> c.isActive())
        .collect(Collectors.toList());
      milestones.sort(Comparator.comparing(CrpMilestone::getYear));
    }

    if (this.isAiccra()) {
      this.addAllCrpMilestones();
    }

    /*
     * Loading basic List
     */
    targetUnits = srfTargetUnitManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    projectOutcome.setCrpProgramOutcome(
      crpProgramOutcomeManager.getCrpProgramOutcomeById(projectOutcome.getCrpProgramOutcome().getId()));
    projectOutcome.getCrpProgramOutcome().setIndicators(
      projectOutcome.getCrpProgramOutcome().getCrpProgramOutcomeIndicators().stream().filter(c -> c.isActive())
        .sorted((d1, d2) -> d1.getIndicator().compareTo((d2.getIndicator()))).collect(Collectors.toList()));
    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};

    projectOutcomeDB = projectOutcomeManager.getProjectOutcomeById(projectOutcomeID);
    this.canBeEditedOutcomeExpectedValue();
    this.canBeEditedMilestoneExpectedValue();
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
      if (projectOutcome.getIndicators() != null) {
        projectOutcome.getIndicators().clear();
      }
      if (projectOutcomeLastPhase != null && projectOutcomeLastPhase.getIndicators() != null) {
        projectOutcomeLastPhase.getIndicators().clear();
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


      this.saveMilestones(projectOutcomeDB);
      if (this.hasSpecificities(APConstants.CRP_SHOW_PROJECT_OUTCOME_COMMUNICATIONS)) {
        this.saveCommunications(projectOutcomeDB);
      }
      this.saveNextUsers(projectOutcomeDB);
      this.saveIndicators(projectOutcomeDB);
      if (this.isLessonsActive()) {
        this.saveLessonsOutcome(loggedCrp, projectOutcomeDB, projectOutcome);
      }

      this.saveProjectOutcome();
      projectOutcome.setPhase(this.getActualPhase());
      projectOutcome.setModificationJustification(this.getJustification());
      projectOutcome.setOrder(this.defineProjectOutcomeOrder(projectOutcome));

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_OUTCOMES_MILESTONE_RELATION);
      relationsName.add(APConstants.PROJECT_OUTCOMES_INDICATORS_RELATION);
      if (this.hasSpecificities(APConstants.CRP_SHOW_PROJECT_OUTCOME_COMMUNICATIONS)) {
        relationsName.add(APConstants.PROJECT_OUTCOMES_COMMUNICATION_RELATION);
      }
      relationsName.add(APConstants.PROJECT_NEXT_USERS_RELATION);
      relationsName.add(APConstants.PROJECT_OUTCOME_LESSONS_RELATION);
      /**
       * The following is required because we need to update something on the @ProjectOutcome if we want a row
       * created in the auditlog table.
       */
      this.setModificationJustification(projectOutcome);
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
    } else {

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
            projectCommunication.setProjectOutcome(projectOutcomeDB);

            if (projectCommunication.getFile() != null) {

              FileDB summary = this.getFileDB(null, projectCommunication.getFile(),
                projectCommunication.getFileFileName(), this.getSummaryAbsolutePath());

              projectCommunication.setSummary(summary);
              FileManager.copyFile(projectCommunication.getFile(),
                this.getSummaryAbsolutePath() + projectCommunication.getFileFileName());
            }

            if (projectCommunication.getSummary() != null && projectCommunication.getSummary().getFileName() != null
              && projectCommunication.getSummary().getFileName().isEmpty()) {
              projectCommunication.setSummary(null);
            }

            projectCommunication = projectCommunicationManager.saveProjectCommunication(projectCommunication);
            // This add projectCommunication to generate correct auditlog.
            projectOutcome.getProjectCommunications().add(projectCommunication);
          } else {
            // Update existing entity.
            ProjectCommunication projectCommunicationDB =
              projectCommunicationManager.getProjectCommunicationById(projectCommunication.getId());

            projectCommunicationDB.setProjectOutcome(projectOutcomeDB);

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
            // This add projectCommunication to generate correct auditlog.
            projectOutcome.getProjectCommunications().add(projectCommunicationDB);
          }
        }
      }
    }
  }

  public void saveIndicators(ProjectOutcome projectOutcomeDB) {

    for (ProjectOutcomeIndicator projectOutcomeIndicator : projectOutcomeDB.getProjectOutcomeIndicators().stream()
      .filter(c -> c.isActive()).collect(Collectors.toList())) {

      if (projectOutcome.getIndicators() == null) {
        projectOutcome.setIndicators(new ArrayList<>());
      }
      if (!projectOutcome.getIndicators().contains(projectOutcomeIndicator)) {
        projectOutcomeIndicatorManager.deleteProjectOutcomeIndicator(projectOutcomeIndicator.getId());

      }
    }

    if (projectOutcome.getIndicators() != null) {
      for (ProjectOutcomeIndicator projectOutcomeIndicator : projectOutcome.getIndicators()) {
        if (projectOutcomeIndicator != null) {
          if (projectOutcomeIndicator.getId() == null) {
            // Create new entity
            projectOutcomeIndicator.setProjectOutcome(projectOutcomeDB);
            projectOutcomeIndicator.setPhase(this.getActualPhase());

            projectOutcomeIndicatorManager.saveProjectOutcomeIndicator(projectOutcomeIndicator);
            // This add projectOutcomeIndicator to generate correct auditlog.
            projectOutcome.getProjectOutcomeIndicators().add(projectOutcomeIndicator);

          } else {
            // Update existing entity
            ProjectOutcomeIndicator projectOutcomeIndicatorDB =
              projectOutcomeIndicatorManager.getProjectOutcomeIndicatorById(projectOutcomeIndicator.getId());

            projectOutcomeIndicatorDB.setProjectOutcome(projectOutcomeDB);

            // update existing fields
            projectOutcomeIndicatorDB.setNarrative(projectOutcomeIndicator.getNarrative());
            projectOutcomeIndicatorDB.setValue(projectOutcomeIndicator.getValue());
            projectOutcomeIndicator.setPhase(this.getActualPhase());

            if (this.isReportingActive()) {
              projectOutcomeIndicatorDB.setValueReporting(projectOutcomeIndicator.getValueReporting());
              projectOutcomeIndicatorDB.setAchievedNarrative(projectOutcomeIndicator.getAchievedNarrative());
            }

            projectOutcomeIndicatorDB =
              projectOutcomeIndicatorManager.saveProjectOutcomeIndicator(projectOutcomeIndicatorDB);
            // This add projectOutcomeIndicator to generate correct auditlog.
            projectOutcome.getProjectOutcomeIndicators().add(projectOutcomeIndicatorDB);
          }


        }

      }
    }
  }

  private void saveMilestones(ProjectOutcome projectOutcomeDB) {

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

            projectMilestone.setProjectOutcome(projectOutcomeDB);

            if (projectMilestone.getExpectedUnit() != null) {
              if (projectMilestone.getExpectedUnit().getId() == null
                || projectMilestone.getExpectedUnit().getId().longValue() == -1) {
                projectMilestone.setExpectedUnit(null);
              }
            }
            projectMilestoneManager.saveProjectMilestone(projectMilestone);
            // This add projectMilestone to generate correct auditlog.
            projectOutcome.getProjectMilestones().add(projectMilestone);
          } else {
            // Update existing entity.
            ProjectMilestone projectMilestoneDB =
              projectMilestoneManager.getProjectMilestoneById(projectMilestone.getId());

            projectMilestoneDB.setProjectOutcome(projectOutcomeDB);
            /**
             * Set fields from non managed entity to managed entity (double check if these fields are editable in the
             * client)
             */

            if (this.isPlanningActive()) {
              projectMilestoneDB.setNarrativeTarget(projectMilestone.getNarrativeTarget());
              projectMilestoneDB.setYear(projectMilestone.getYear());
              if (projectMilestone.getExpectedUnit() != null) {
                if (projectMilestone.getExpectedUnit().getId() == null
                  || projectMilestone.getExpectedUnit().getId().longValue() == -1) {
                  projectMilestoneDB.setExpectedUnit(null);
                } else {
                  projectMilestoneDB.setExpectedUnit(projectMilestone.getExpectedUnit());
                  projectMilestoneDB.setExpectedValue(projectMilestone.getExpectedValue());
                  projectMilestoneDB.setAchievedValue(projectMilestone.getAchievedValue());
                  projectMilestoneDB.setSettedValue(projectMilestone.getSettedValue());
                }
              }

            }
            // Reporting phase
            else {
              projectMilestoneDB.setAchievedValue(projectMilestone.getAchievedValue());
              projectMilestoneDB.setNarrativeAchieved(projectMilestone.getNarrativeAchieved());
            }
            projectMilestoneDB.setCrpMilestone(projectMilestone.getCrpMilestone());
            projectMilestoneDB.setExpectedValue(projectMilestone.getExpectedValue());
            projectMilestoneDB.setAchievedValue(projectMilestone.getAchievedValue());
            projectMilestoneDB.setSettedValue(projectMilestone.getSettedValue());
            projectMilestoneDB = projectMilestoneManager.saveProjectMilestone(projectMilestoneDB);
            // This add projectMilestone to generate correct auditlog.
            projectOutcome.getProjectMilestones().add(projectMilestoneDB);
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
            projectNextuser.setProjectOutcome(projectOutcomeDB);

            projectNextuserManager.saveProjectNextuser(projectNextuser);
            // This add projectNextuser to generate correct auditlog.
            projectOutcome.getProjectNextusers().add(projectNextuser);

          } else {
            // Update existing entity
            ProjectNextuser projectNextuserDB = projectNextuserManager.getProjectNextuserById(projectNextuser.getId());

            projectNextuserDB.setProjectOutcome(projectOutcomeDB);

            // update existing fields
            projectNextuserDB.setKnowledge(projectNextuser.getKnowledge());
            projectNextuserDB.setNextUser(projectNextuser.getNextUser());
            projectNextuserDB.setStrategies(projectNextuser.getStrategies());
            projectNextuserDB.setStrategiesReport(projectNextuser.getStrategiesReport());
            projectNextuserDB.setKnowledgeReport(projectNextuser.getKnowledgeReport());

            projectNextuserDB = projectNextuserManager.saveProjectNextuser(projectNextuserDB);
            // This add projectNextuser to generate correct auditlog.
            projectOutcome.getProjectNextusers().add(projectNextuserDB);
          }


        }

      }
    }
  }

  private ProjectOutcome saveProjectOutcome() {


    if (this.isPlanningActive()) {


      if (projectOutcome.getExpectedUnit() != null) {
        if (projectOutcome.getExpectedUnit().getId() == null
          || projectOutcome.getExpectedUnit().getId().longValue() == -1) {
          projectOutcomeDB.setExpectedUnit(null);
          projectOutcomeDB.setExpectedValue(null);
        } else {
          projectOutcomeDB.setExpectedUnit(projectOutcome.getExpectedUnit());
          projectOutcomeDB.setExpectedValue(projectOutcome.getExpectedValue());
        }
      }

      if (projectOutcome.getAchievedUnit() != null) {
        if (projectOutcome.getAchievedUnit().getId() == null
          || projectOutcome.getAchievedUnit().getId().longValue() == -1) {
          projectOutcome.setAchievedUnit(null);
        } else {
          projectOutcome.setAchievedUnit(projectOutcome.getAchievedUnit());
        }
      }

      projectOutcomeDB.setNarrativeTarget(projectOutcome.getNarrativeTarget());

      // Reporting phase
    } else {

      if (projectOutcome.getAchievedUnit() != null && (projectOutcome.getAchievedUnit().getId() == null
        || projectOutcome.getAchievedUnit().getId().longValue() == -1)) {
        projectOutcomeDB.setAchievedUnit(null);
        projectOutcomeDB.setAchievedValue(null);
      } else {
        projectOutcomeDB.setAchievedUnit(projectOutcome.getAchievedUnit());
        projectOutcomeDB.setAchievedValue(projectOutcome.getAchievedValue());
      }

      projectOutcomeDB.setNarrativeAchieved(projectOutcome.getNarrativeAchieved());

      // Reporting phase

      if (projectOutcome.getExpectedUnit() != null) {
        if (projectOutcome.getExpectedUnit().getId() == null
          || projectOutcome.getExpectedUnit().getId().longValue() == -1) {
          projectOutcome.setExpectedUnit(null);
        } else {
          projectOutcome.setExpectedUnit(projectOutcome.getExpectedUnit());
        }
      }

      if (projectOutcome.getAchievedUnit() != null) {
        if (projectOutcome.getAchievedUnit().getId() == null
          || projectOutcome.getAchievedUnit().getId().longValue() == -1) {
          projectOutcome.setAchievedUnit(null);
        } else {
          projectOutcome.setAchievedUnit(projectOutcome.getAchievedUnit());
        }
      }
    }

    projectOutcomeDB.setCrpProgramOutcome(crpProgramOutcome);
    projectOutcomeDB.setProject(project);
    projectOutcomeDB.setPhase(this.getActualPhase());
    if (projectOutcome.getOrder() != null) {
      projectOutcomeDB.setOrder(projectOutcome.getOrder());
    } else {
      projectOutcomeDB.setOrder(this.defineProjectOutcomeOrder(projectOutcomeDB));
    }

    projectOutcomeDB = projectOutcomeManager.saveProjectOutcome(projectOutcomeDB);

    return projectOutcomeDB;

  }


  public void setEditMilestoneExpectedValue(boolean editMilestoneExpectedValue) {
    this.editMilestoneExpectedValue = editMilestoneExpectedValue;
  }

  public void setEditOutcomeExpectedValue(boolean editOutcomeExpectedValue) {
    this.editOutcomeExpectedValue = editOutcomeExpectedValue;
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

  public void setProjectOutcomeLastPhase(ProjectOutcome projectOutcomeLastPhase) {
    this.projectOutcomeLastPhase = projectOutcomeLastPhase;
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

