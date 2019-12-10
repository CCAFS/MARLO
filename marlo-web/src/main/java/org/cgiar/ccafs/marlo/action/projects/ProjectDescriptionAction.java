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
import org.cgiar.ccafs.marlo.data.manager.CrpClusterOfActivityManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectCenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectClusterActivityManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectScopeManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.manager.impl.CenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectCenterOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectScope;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.validation.projects.ProjectDescriptionValidator;

import java.io.BufferedReader;
import java.io.File;
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
 * @author Sebastian Amariles - CIAT/CCAFS
 * @author Christian Garcia- CIAT/CCAFS
 */
public class ProjectDescriptionAction extends BaseAction {


  private static final long serialVersionUID = -793652591843623397L;


  private static final Logger LOG = LoggerFactory.getLogger(ProjectDescriptionAction.class);

  // Managers
  private ProjectManager projectManager;


  private ProjectInfoManager projectInfoManagerManager;


  private SectionStatusManager sectionStatusManager;


  private ProjectFocusManager projectFocusManager;

  private FileDBManager fileDBManager;


  private GlobalUnitManager crpManager;

  private CrpProgramManager programManager;
  private ProjectClusterActivityManager projectClusterActivityManager;

  private CrpClusterOfActivityManager crpClusterOfActivityManager;

  private GlobalUnitProjectManager globalUnitProjectManager;
  private ProjectCenterOutcomeManager projectCenterOutcomeManager;

  private AuditLogManager auditLogManager;
  private ProjectScopeManager projectScopeManager;
  private LocElementTypeManager locationTypeManager;
  private String transaction;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private CenterOutcomeManager centerOutcomeManager;
  private LiaisonUserManager liaisonUserManager;
  private HistoryComparator historyComparator;
  private CrpProgramManager crpProgramManager;

  private List<CrpProgram> centerPrograms;
  private List<CrpProgram> regionPrograms;
  // Front-end
  private long projectID;
  private GlobalUnit loggedCrp;
  private Project project;
  private List<CrpProgram> programFlagships;
  private List<CrpProgram> regionFlagships;
  private List<LiaisonInstitution> liaisonInstitutions;
  private List<CrpClusterOfActivity> clusterofActivites;
  private Project projectDB;

  /*
   * private LiaisonInstitutionManager liaisonInstitutionManager;
   * private LiaisonUserManager liaisonUserManager;
   * private UserManager userManager;
   */

  private Map<String, String> projectStatuses;
  private List<LiaisonUser> allOwners;
  private Map<String, String> projectTypes;
  private Map<String, String> projectScales;
  private List<CenterOutcome> centerOutcomes;
  private File file;
  private File fileReporting;

  private String fileContentType;

  private String fileFileName;


  private String fileReportingFileName;


  private ProjectDescriptionValidator validator;

  @Inject
  public ProjectDescriptionAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    CrpProgramManager programManager, LiaisonUserManager liaisonUserManager,
    LiaisonInstitutionManager liaisonInstitutionManager, UserManager userManager,
    SectionStatusManager sectionStatusManager, ProjectFocusManager projectFocusManager, FileDBManager fileDBManager,
    AuditLogManager auditLogManager, ProjectDescriptionValidator validator,
    ProjectClusterActivityManager projectClusterActivityManager,
    CrpClusterOfActivityManager crpClusterOfActivityManager, LocElementTypeManager locationManager,
    ProjectScopeManager projectLocationManager, HistoryComparator historyComparator,
    ProjectInfoManager projectInfoManagerManager, GlobalUnitProjectManager globalUnitProjectManager,
    CenterOutcomeManager centerOutcomeManager, ProjectCenterOutcomeManager projectCenterOutcomeManager,
    CrpProgramManager crpProgramManager) {
    super(config);
    this.projectManager = projectManager;
    this.projectInfoManagerManager = projectInfoManagerManager;
    this.programManager = programManager;
    this.crpManager = crpManager;
    // this.userManager = userManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.projectManager = projectManager;
    this.projectFocusManager = projectFocusManager;
    this.validator = validator;
    this.sectionStatusManager = sectionStatusManager;
    this.crpClusterOfActivityManager = crpClusterOfActivityManager;
    this.auditLogManager = auditLogManager;
    this.projectClusterActivityManager = projectClusterActivityManager;
    this.fileDBManager = fileDBManager;
    this.historyComparator = historyComparator;
    // this.liaisonUserManager = liaisonUserManager;
    this.liaisonUserManager = liaisonUserManager;
    this.projectScopeManager = projectLocationManager;
    this.locationTypeManager = locationManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.projectCenterOutcomeManager = projectCenterOutcomeManager;
    this.centerOutcomeManager = centerOutcomeManager;
    this.crpProgramManager = crpProgramManager;
  }

  /**
   * In this method it is checked if there is a draft file and it is eliminated
   * 
   * @return Sucess always
   */
  @Override
  public String cancel() {

    // get the path auto save
    Path path = this.getAutoSaveFilePath();
    // if file exist
    if (path.toFile().exists()) {
      // delete the file
      boolean fileDeleted = path.toFile().delete();
    }

    // Set the action No draft
    this.setDraft(false);

    // Put succes message to front
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


  public List<LiaisonUser> getAllOwners() {
    return allOwners;
  }


  /**
   * Return the absolute path where the bilateral contract is or should be located.
   * 
   * @return complete path where the image is stored
   */
  private String getAnnualReportAbsolutePath() {
    return config.getUploadsBaseFolder() + File.separator + this.getAnualReportRelativePath() + File.separator;
  }

  private String getAnualReportRelativePath() {
    return config.getProjectsBaseFolder(loggedCrp.getAcronym()) + File.separator + project.getId() + File.separator
      + config.getAnualReportFolder();
  }

  public String getAnualReportURL() {
    return config.getDownloadURL() + "/" + this.getAnualReportRelativePath().replace('\\', '/');
  }

  /**
   * The name of the autosave file is constructed and the path is searched
   * 
   * @return Auto save file path
   */
  private Path getAutoSaveFilePath() {
    // get the class simple name
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  /**
   * Return the absolute path where the bilateral contract is or should be located.
   * 
   * @return complete path where the image is stored
   */
  private String getBilateralContractAbsolutePath() {
    return config.getUploadsBaseFolder() + File.separator + this.getBilateralProposalRelativePath() + File.separator;
  }

  public String getBilateralContractURL() {
    return config.getDownloadURL() + "/" + this.getBilateralProposalRelativePath().replace('\\', '/');
  }

  private String getBilateralProposalRelativePath() {


    return config.getProjectsBaseFolder(loggedCrp.getAcronym()) + File.separator + project.getId() + File.separator
      + config.getBilateralProjectContractProposalFolder() + File.separator;
  }

  public List<CenterOutcome> getCenterOutcomes() {
    return centerOutcomes;
  }

  public List<CrpProgram> getCenterPrograms() {
    return centerPrograms;
  }

  public List<CrpClusterOfActivity> getClusterofActivites() {
    return clusterofActivites;
  }

  public File getFile() {
    return file;
  }

  public String getFileContentType() {
    return fileContentType;
  }

  public String getFileFileName() {
    return fileFileName;
  }

  public File getFileReporting() {
    return fileReporting;
  }

  public String getFileReportingFileName() {
    return fileReportingFileName;
  }

  /**
   * This method returns an array of flagship ids depending on the project.flagships attribute.
   * 
   * @return an array of integers.
   */
  public long[] getFlagshipIds() {

    List<CrpProgram> projectFocuses = project.getFlagships();

    if (projectFocuses != null && !projectFocuses.isEmpty()) {
      long[] ids = new long[projectFocuses.size()];
      for (int c = 0; c < ids.length; c++) {
        ids[c] = projectFocuses.get(c).getId();
      }
      return ids;
    }
    return null;
  }

  public List<LiaisonInstitution> getLiaisonInstitutions() {
    return liaisonInstitutions;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }


  public List<CrpProgram> getProgramFlagships() {
    return programFlagships;
  }

  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }


  public Map<String, String> getProjectScales() {
    return projectScales;
  }


  public Map<String, String> getProjectStatuses() {
    return projectStatuses;
  }

  public Map<String, String> getProjectTypes() {
    return projectTypes;
  }


  public List<CrpProgram> getRegionFlagships() {
    return regionFlagships;
  }

  public List<CrpProgram> getRegionPrograms() {
    return regionPrograms;
  }


  public long[] getRegionsIds() {

    List<CrpProgram> projectFocuses = project.getRegions();

    if (projectFocuses != null && !projectFocuses.isEmpty()) {
      long[] ids = new long[projectFocuses.size()];
      for (int c = 0; c < ids.length; c++) {
        ids[c] = projectFocuses.get(c).getId();
      }
      return ids;
    }
    return null;
  }


  public String getTransaction() {
    return transaction;
  }


  private String getWorkplanRelativePath() {

    return config.getProjectsBaseFolder(loggedCrp.getAcronym()) + File.separator + project.getId() + File.separator
      + config.getProjectWorkplanFolder() + File.separator;
  }


  public String getWorkplanURL() {
    return config.getDownloadURL() + "/" + this.getWorkplanRelativePath().replace('\\', '/');
  }


  /**
   * Return the absolute path where the work plan is or should be located.
   * 
   * @param workplan name
   * @return complete path where the image is stored
   */
  private String getWorplansAbsolutePath() {
    return config.getUploadsBaseFolder() + File.separator + this.getWorkplanRelativePath() + File.separator;
  }


  @Override
  public void prepare() throws Exception {

    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    } catch (Exception e) {
      LOG.error("unable to parse projectID", e);
      /**
       * Original code swallows the exception and didn't even log it. Now we at least log it,
       * but we need to revisit to see if we should continue processing or re-throw the exception.
       */
    }

    // We check that you have a TRANSACTION_ID to know if it is history version

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      // auditLogManager.getHistory Bring us the history with the transaction id
      Project history = (Project) auditLogManager.getHistory(transaction);


      // In case there are some relationships that are displayed on the front in a particular field, add to this list by
      // passing the name of the relationship and the name of the attribute with which it is displayed on the front
      Map<String, String> specialList = new HashMap<>();
      specialList.put(APConstants.PROJECT_FOCUSES_RELATION, "flagshipValue");

      // We load the differences of this version with the previous version

      this.setDifferences(new ArrayList<>());
      try {
        this.getDifferences().addAll(historyComparator.getDifferencesList(
          history.getProjecInfoPhase(this.getActualPhase()), transaction, specialList, "project.projectInfo", "", 1));
      } catch (Exception e) {

      }

      if (history != null) {
        project = history;

      } else {
        // not a valid transatacion
        this.transaction = null;

        this.setTransaction("-1");
      }

    } else {
      // get project info for DB
      project = projectManager.getProjectById(projectID);
    }

    if (project != null) {

      // We validate if there is a draft version
      // Get the path
      Path path = this.getAutoSaveFilePath();
      // validate if file exist and user has enabled auto-save funcionallity
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {

        BufferedReader reader = null;
        // instace de buffer from file
        reader = new BufferedReader(new FileReader(path.toFile()));

        Gson gson = new GsonBuilder().create();


        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();

        // instance class AutoSaveReader (made by US)
        AutoSaveReader autoSaveReader = new AutoSaveReader();

        // We read the JSON serialized by the front-end and cast it to the object

        project = (Project) autoSaveReader.readFromJson(jReader);
        // We load some BD objects, since the draft only keeps IDs and some data is shown with a different labe
        Project projectDb = projectManager.getProjectById(project.getId());
        project.getProjectInfo()
          .setProjectEditLeader(projectDb.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader());
        project.getProjectInfo()
          .setAdministrative(projectDb.getProjecInfoPhase(this.getActualPhase()).getAdministrative());
        project.getProjectInfo().setPhase(projectDb.getProjecInfoPhase(this.getActualPhase()).getPhase());
        // load Cluster of activites info
        if (project.getClusterActivities() != null) {
          for (ProjectClusterActivity projectClusterActivity : project.getClusterActivities()) {
            projectClusterActivity.setCrpClusterOfActivity(crpClusterOfActivityManager
              .getCrpClusterOfActivityById(projectClusterActivity.getCrpClusterOfActivity().getId()));
            projectClusterActivity.getCrpClusterOfActivity().setLeaders(projectClusterActivity.getCrpClusterOfActivity()
              .getCrpClusterActivityLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

          }
        }
        // load scope info
        if (project.getScopes() != null) {
          for (ProjectScope projectScope : project.getScopes()) {
            projectScope
              .setLocElementType(locationTypeManager.getLocElementTypeById(projectScope.getLocElementType().getId()));
          }
        }

        // load LiaisonUser info
        if (project.getProjectInfo().getLiaisonInstitution() != null) {
          project.getProjectInfo().setLiaisonInstitution(liaisonInstitutionManager
            .getLiaisonInstitutionById(project.getProjectInfo().getLiaisonInstitution().getId()));
        } else {
          project.getProjecInfoPhase(this.getActualPhase()).setLiaisonInstitution(null);
        }

        // load fps value
        List<CrpProgram> programs = new ArrayList<>();
        if (project.getFlagshipValue() != null) {
          for (String programID : project.getFlagshipValue().trim().replace("[", "").replace("]", "").split(",")) {
            try {
              CrpProgram program = programManager.getCrpProgramById(Long.parseLong(programID.trim()));
              programs.add(program);
            } catch (Exception e) {
              LOG.error("unable to add program to programs list", e);
              /**
               * Original code swallows the exception and didn't even log it. Now we at least log it,
               * but we need to revisit to see if we should continue processing or re-throw the exception.
               */
            }
          }
        }

        // load regions value
        List<CrpProgram> regions = new ArrayList<>();
        if (project.getRegionsValue() != null) {
          for (String programID : project.getRegionsValue().trim().replace("[", "").replace("]", "").split(",")) {
            try {
              CrpProgram program = programManager.getCrpProgramById(Long.parseLong(programID.trim()));
              regions.add(program);
            } catch (Exception e) {
              LOG.error("unable to add program to regions list", e);
              /**
               * Original code swallows the exception and didn't even log it. Now we at least log it,
               * but we need to revisit to see if we should continue processing or re-throw the exception.
               */
            }
          }
        }
        project.setFlagships(programs);
        project.setRegions(regions);


        // We change this variable so that the user knows that he is working on a draft version

        // Center Outcomes
        if (this.isProjectCenter(projectID)) {
          if (project.getCenterOutcomes() != null) {
            for (ProjectCenterOutcome projectCenterOutcome : project.getCenterOutcomes()) {
              projectCenterOutcome.setCenterOutcome(
                centerOutcomeManager.getResearchOutcomeById(projectCenterOutcome.getCenterOutcome().getId()));
            }
          }
        }

        this.setDraft(true);
      } else {
        // DB version
        this.setDraft(false);

        GlobalUnitProject gp = globalUnitProjectManager.findByProjectId(project.getId());

        // Load the DB information and adjust it to the structures with which the front end
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        if (project.getProjectInfo() == null) {
          project.setProjectInfo(new ProjectInfo());
        }

        // load LiaisonUser info
        if (project.getProjectInfo().getLiaisonInstitution() != null) {
          project.getProjectInfo().setLiaisonInstitution(liaisonInstitutionManager
            .getLiaisonInstitutionById(project.getProjectInfo().getLiaisonInstitution().getId()));
        } else {
          project.getProjecInfoPhase(this.getActualPhase()).setLiaisonInstitution(null);
        }
        project.setFlagshipValue("");
        project.setRegionsValue("");
        List<CrpProgram> programs = new ArrayList<>();

        for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())
            && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
            && c.getCrpProgram().getCrp().getId().equals(gp.getGlobalUnit().getId()))
          .collect(Collectors.toList())) {
          programs.add(projectFocuses.getCrpProgram());
          if (project.getFlagshipValue().isEmpty()) {
            project.setFlagshipValue(projectFocuses.getCrpProgram().getId().toString());
          } else {
            project
              .setFlagshipValue(project.getFlagshipValue() + "," + projectFocuses.getCrpProgram().getId().toString());
          }
        }

        List<CrpProgram> regions = new ArrayList<>();

        for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())
            && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
            && c.getCrpProgram().getCrp().getId().equals(gp.getGlobalUnit().getId()))
          .collect(Collectors.toList())) {
          regions.add(projectFocuses.getCrpProgram());
          if (project.getRegionsValue() != null && project.getRegionsValue().isEmpty()) {
            project.setRegionsValue(projectFocuses.getCrpProgram().getId().toString());
          } else {
            project
              .setRegionsValue(project.getRegionsValue() + "," + projectFocuses.getCrpProgram().getId().toString());
          }
        }
        // load the info for Cluster of activities
        List<ProjectClusterActivity> projectClusterActivities = new ArrayList<>();
        for (ProjectClusterActivity projectClusterActivity : project.getProjectClusterActivities().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList())) {
          projectClusterActivity.setCrpClusterOfActivity(crpClusterOfActivityManager
            .getCrpClusterOfActivityById(projectClusterActivity.getCrpClusterOfActivity().getId()));
          projectClusterActivity.getCrpClusterOfActivity().setLeaders(projectClusterActivity.getCrpClusterOfActivity()
            .getCrpClusterActivityLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
          projectClusterActivities.add(projectClusterActivity);
        }
        // load the scopes
        List<ProjectScope> projectLocations = new ArrayList<>();
        for (ProjectScope projectLocation : project.getProjectScopes().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {

          projectLocations.add(projectLocation);
        }
        List<String> activities = new ArrayList<>();
        List<ProjectClusterActivity> projectActivities = new ArrayList<>();
        for (ProjectClusterActivity projectClusterActivity : projectClusterActivities) {
          if (!activities.contains(projectClusterActivity.getCrpClusterOfActivity().getIdentifier())) {
            projectActivities.add(projectClusterActivity);
            activities.add(projectClusterActivity.getCrpClusterOfActivity().getIdentifier());

          }
        }
        project.setClusterActivities(projectActivities);
        project.setFlagships(programs);
        project.setRegions(regions);
        project.setScopes(projectLocations);

        // Center Outcomes
        if (this.isProjectCenter(projectID)) {
          List<ProjectCenterOutcome> projectCenterOutcomes = new ArrayList<>();
          for (ProjectCenterOutcome projectCenterOutcome : project.getProjectCenterOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
            .collect(Collectors.toList())) {
            projectCenterOutcome.setCenterOutcome(
              centerOutcomeManager.getResearchOutcomeById(projectCenterOutcome.getCenterOutcome().getId()));
            projectCenterOutcomes.add(projectCenterOutcome);
          }
          project.setCenterOutcomes(projectCenterOutcomes);
        }

      }
    }

    allOwners = new ArrayList<LiaisonUser>();
    // load the liason users for the crp
    if (this.isCenterGlobalUnit()) {
      if (!this.getActualPhase().getCrp().isCenterType()) {
        allOwners.addAll(this.getActualPhase().getCrp().getLiasonUsers());
      } else {
        allOwners.addAll(loggedCrp.getLiasonUsers());
      }
    } else {
      allOwners.addAll(loggedCrp.getLiasonUsers());
    }

    liaisonInstitutions = new ArrayList<LiaisonInstitution>();
    if (this.isCenterGlobalUnit()) {
      // dperez 2019-11-20
      if (!this.getActualPhase().getCrp().isCenterType()) {
        liaisonInstitutions.addAll(this.getActualPhase().getCrp().getLiaisonInstitutions().stream()
          .filter(c -> c.isActive() && c.getCrpProgram() != null).collect(Collectors.toList()));
      } else {
        liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
          .filter(c -> c.isActive() && c.getCrpProgram() != null
            && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList()));
      }

    } else {
      liaisonInstitutions
        .addAll(loggedCrp.getLiaisonInstitutions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      liaisonInstitutions.addAll(
        liaisonInstitutionManager.findAll().stream().filter(c -> c.getCrp() == null).collect(Collectors.toList()));
    }
    // load the liasons intitutions for the crp


    // load the flaghsips an regions
    programFlagships = new ArrayList<>();
    regionFlagships = new ArrayList<>();
    if (this.isCenterGlobalUnit()) {
      if (!this.getActualPhase().getCrp().isCenterType()) {
        List<CrpProgram> crpProgramList = crpProgramManager.findAll();
        crpProgramList = crpProgramList.stream()
          .filter(c -> c.getCrp().getId().longValue() == this.getActualPhase().getCrp().getId().longValue())
          .collect(Collectors.toList());
        programFlagships.addAll(crpProgramList.stream()
          .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList()));
      } else {
        programFlagships.addAll(loggedCrp.getCrpPrograms().stream()
          .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList()));
      }
    } else {
      programFlagships.addAll(loggedCrp.getCrpPrograms().stream()
        .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList()));
    }


    programFlagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));
    clusterofActivites = new ArrayList<>();

    for (CrpProgram crpProgram : project.getFlagships()) {
      crpProgram = programManager.getCrpProgramById(crpProgram.getId());
      clusterofActivites.addAll(crpClusterOfActivityManager.findClusterProgramPhase(crpProgram, this.getActualPhase()));
    }

    // add regions programs
    if (this.isCenterGlobalUnit()) {
      if (!this.getActualPhase().getCrp().isCenterType()) {
        List<CrpProgram> crpProgramList = crpProgramManager.findAll();
        crpProgramList = crpProgramList.stream()
          .filter(c -> c.getCrp().getId().longValue() == this.getActualPhase().getCrp().getId().longValue())
          .collect(Collectors.toList());
        regionFlagships.addAll(crpProgramList.stream()
          .filter(c -> c.isActive() && c.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList()));
        regionFlagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));
      } else {
        regionFlagships.addAll(loggedCrp.getCrpPrograms().stream()
          .filter(c -> c.isActive() && c.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList()));
        regionFlagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));
      }
    } else {
      regionFlagships.addAll(loggedCrp.getCrpPrograms().stream()
        .filter(c -> c.isActive() && c.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList()));
      regionFlagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));
    }


    // Center Outcomes
    if (this.isProjectCenter(projectID)) {
      centerOutcomes = new ArrayList<>();

      for (CrpProgram crpProgram : project.getFlagships()) {
        crpProgram = programManager.getCrpProgramById(crpProgram.getId());
        List<CenterTopic> centerTopics = new ArrayList<>(
          crpProgram.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList()));
        for (CenterTopic centerTopic : centerTopics) {
          List<CenterOutcome> centerOutcomesList = new ArrayList<>(
            centerTopic.getResearchOutcomes().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));
          for (CenterOutcome centerOutcome : centerOutcomesList) {
            centerOutcomes.add(centerOutcome);
          }
        }
      }
    }

    // Project Statuses
    projectStatuses = new HashMap<>();
    List<ProjectStatusEnum> list = Arrays.asList(ProjectStatusEnum.values());
    for (ProjectStatusEnum projectStatusEnum : list) {

      projectStatuses.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
    }


    // add project scales

    projectScales = new HashMap<>();
    projectScales.put(APConstants.PROJECT_SCAPE_NATIONAL, this.getText("project.projectScape.national"));
    projectScales.put(APConstants.PROJECT_SCAPE_REGIONAL, this.getText("project.projectScape.regional"));
    projectScales.put(APConstants.PROJECT_SCAPE_GLOBAL, this.getText("project.projectType.global"));


    projectDB = projectManager.getProjectById(projectID);

    // The base permission is established for the current section

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_DESCRIPTION_BASE_PERMISSION, params));

    /*
     * If it is post, the lists are cleaned, this is done because there is a bug of struts, if this is not done it does
     * not delete the items deleted by the user
     */
    if (this.isHttpPost()) {
      if (project.getClusterActivities() != null) {
        project.getClusterActivities().clear();
      }
      project.setFlagshipValue(null);
      project.setRegionsValue(null);
      if (project.getCenterOutcomes() != null) {
        project.getCenterOutcomes().clear();
      }

      project.getProjecInfoPhase(this.getActualPhase()).setLiaisonInstitution(null);

      project.getProjectInfo().setNoRegional(null);
      project.getProjectInfo().setCrossCuttingCapacity(null);
      project.getProjectInfo().setCrossCuttingClimate(null);
      project.getProjectInfo().setCrossCuttingNa(null);
    }

  }


  @Override
  public String save() {

    if (this.hasPermission("canEdit")) {


      projectDB.setProjectInfo(projectDB.getProjecInfoPhase(this.getActualPhase()));
      // Load basic info project to be saved

      project.setCreateDate(projectDB.getCreateDate());
      project.getProjectInfo().setPresetDate(projectDB.getProjectInfo().getPresetDate());

      // Validations to fill the checkbox fields

      if (project.getProjectInfo().getNoRegional() == null) {
        project.getProjectInfo().setNoRegional(false);
      }
      if (project.getProjectInfo().getCrossCuttingCapacity() == null) {
        project.getProjectInfo().setCrossCuttingCapacity(false);
      }
      if (project.getProjectInfo().getCrossCuttingClimate() == null) {
        project.getProjectInfo().setCrossCuttingClimate(false);
      }
      if (project.getProjectInfo().getCrossCuttingNa() == null) {
        project.getProjectInfo().setCrossCuttingNa(false);
      }

      if (this.isReportingActive()) {

        // Capacity Development
        project.getProjectInfo()
          .setCrossCuttingCapacity(projectDB.getProjecInfoPhase(this.getActualPhase()).getCrossCuttingCapacity());
        // Climate Change
        project.getProjectInfo()
          .setCrossCuttingClimate(projectDB.getProjecInfoPhase(this.getActualPhase()).getCrossCuttingClimate());
        // N/A
        project.getProjectInfo()
          .setCrossCuttingNa(projectDB.getProjecInfoPhase(this.getActualPhase()).getCrossCuttingNa());
      }


      // no liaison institution selected
      if (project.getProjectInfo().getLiaisonInstitution() != null) {
        if (project.getProjectInfo().getLiaisonInstitution().getId() == -1) {
          project.getProjectInfo().setLiaisonInstitution(null);
        }
      }

      // Saving the flaghsips
      if (project.getFlagshipValue() != null && project.getFlagshipValue().length() > 0) {

        List<ProjectFocus> fpsPreview = projectDB.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())
            && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
            && c.getCrpProgram().getCrp().getId().equals(loggedCrp.getId()))
          .collect(Collectors.toList());
        for (ProjectFocus projectFocus : fpsPreview) {
          if (project.getFlagshipValue() == null || project.getFlagshipValue().isEmpty()
            || !project.getFlagshipValue().contains(projectFocus.getCrpProgram().getId().toString())) {
            projectFocusManager.deleteProjectFocus(projectFocus.getId());
          }
        }

        for (String programID : project.getFlagshipValue().trim().split(",")) {
          if (programID.length() > 0) {
            CrpProgram program =
              programManager.getCrpProgramById(Long.parseLong(programID.trim().replaceAll("[^0-9]", "")));
            ProjectFocus projectFocus = new ProjectFocus();
            projectFocus.setCrpProgram(program);
            projectFocus.setProject(project);
            projectFocus.setPhase(this.getActualPhase());
            if (projectDB.getProjectFocuses().stream()
              .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == program.getId().longValue()
                && c.getPhase().equals(this.getActualPhase()))
              .collect(Collectors.toList()).isEmpty()) {
              projectFocus.setPhase(this.getActualPhase());
              projectFocusManager.saveProjectFocus(projectFocus);
              // This add projectFocus to generate correct auditlog.
              project.getProjectFocuses().add(projectFocus);
            }
          }

        }
      } else {
        // Delete All flagships
        for (ProjectFocus projectFocus : projectDB.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())
            && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
            && c.getCrpProgram().getCrp().getId().equals(loggedCrp.getId()))
          .collect(Collectors.toList())) {
          projectFocusManager.deleteProjectFocus(projectFocus.getId());
        }
      }

      // Saving the regions
      if (project.getRegionsValue() != null && project.getRegionsValue().length() > 0) {

        List<ProjectFocus> regionsPreview = projectDB.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())
            && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
            && c.getCrpProgram().getCrp().getId().equals(loggedCrp.getId()))
          .collect(Collectors.toList());
        for (ProjectFocus projectFocus : regionsPreview) {
          if (project.getRegionsValue() == null || project.getRegionsValue().isEmpty()
            || !project.getRegionsValue().contains(projectFocus.getCrpProgram().getId().toString())) {
            projectFocusManager.deleteProjectFocus(projectFocus.getId());
          }
        }

        for (String programID : project.getRegionsValue().trim().split(",")) {
          if (programID.length() > 0) {
            CrpProgram program = programManager.getCrpProgramById(Long.parseLong(programID.trim()));
            ProjectFocus projectFocus = new ProjectFocus();
            projectFocus.setCrpProgram(program);
            projectFocus.setProject(project);
            projectFocus.setPhase(this.getActualPhase());
            if (projectDB.getProjectFocuses().stream()
              .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())
                && c.getCrpProgram().getId().longValue() == program.getId().longValue())
              .collect(Collectors.toList()).isEmpty()) {
              projectFocus.setPhase(this.getActualPhase());
              projectFocusManager.saveProjectFocus(projectFocus);
              // This add projectFocus to generate correct auditlog.
              project.getProjectFocuses().add(projectFocus);
            }
          }

        }

      } else {
        // Delete All Regions
        for (ProjectFocus projectFocus : projectDB.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())
            && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
            && c.getCrpProgram().getCrp().getId().equals(loggedCrp.getId()))
          .collect(Collectors.toList())) {
          projectFocusManager.deleteProjectFocus(projectFocus.getId());
        }
      }


      // Removing Project Cluster Activities
      for (ProjectClusterActivity projectClusterActivity : projectDB.getProjectClusterActivities().stream()
        .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
        .collect(Collectors.toList())) {

        if (project.getClusterActivities() == null) {
          project.setClusterActivities(new ArrayList<>());
        }
        if (!project.getClusterActivities().contains(projectClusterActivity)) {
          projectClusterActivityManager.deleteProjectClusterActivity(projectClusterActivity.getId());

        }
      }
      // Add Project Cluster Activities

      if (project.getClusterActivities() != null) {
        for (ProjectClusterActivity projectClusterActivity : project.getClusterActivities()) {
          if (projectClusterActivity.getId() == null) {
            projectClusterActivity.setProject(project);
            projectClusterActivity.setPhase(this.getActualPhase());
            projectClusterActivity = projectClusterActivityManager.saveProjectClusterActivity(projectClusterActivity);
            // This add clusterActivity to generate correct auditlog.
            project.getProjectClusterActivities().add(projectClusterActivity);
          }

        }
      }


      // Removing Project Scopes

      for (ProjectScope projectLocation : projectDB.getProjectScopes().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {

        if (project.getScopes() == null) {
          project.setScopes(new ArrayList<>());
        }
        if (!project.getScopes().contains(projectLocation)) {
          projectScopeManager.deleteProjectScope(projectLocation.getId());

        }
      }
      // Add Project Scopes

      if (project.getScopes() != null) {
        for (ProjectScope projectLocation : project.getScopes()) {
          if (projectLocation.getId() == null) {
            projectLocation.setProject(project);
            projectScopeManager.saveProjectScope(projectLocation);
            project.getProjectScopes().add(projectLocation);
          }

        }
      }


      // Removing Project Center Outcomes
      if (this.isProjectCenter(projectID)) {

        // Saving Project Center Outcomes
        if (project.getCenterOutcomes() != null && !project.getCenterOutcomes().isEmpty()) {
          // Removing Project Center Outcomes
          for (ProjectCenterOutcome projectCenterOutcome : projectDB.getProjectCenterOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
            .collect(Collectors.toList())) {
            if (!project.getCenterOutcomes().contains(projectCenterOutcome)) {
              projectCenterOutcomeManager.deleteProjectCenterOutcome(projectCenterOutcome.getId());
            }
          }
          // Add Project Center Outcomes
          for (ProjectCenterOutcome projectCenterOutcome : project.getCenterOutcomes()) {
            if (projectCenterOutcome.getId() == null) {
              projectCenterOutcome.setProject(project);
              projectCenterOutcome.setPhase(this.getActualPhase());
              projectCenterOutcome = projectCenterOutcomeManager.saveProjectCenterOutcome(projectCenterOutcome);
              // This add centerOutcome to generate correct auditlog.
              project.getProjectCenterOutcomes().add(projectCenterOutcome);
            } else {
              // update or reply info
              ProjectCenterOutcome ProjectCenterOutcomeDB =
                projectCenterOutcomeManager.getProjectCenterOutcomeById(projectCenterOutcome.getId());
              projectCenterOutcome = projectCenterOutcomeManager.saveProjectCenterOutcome(ProjectCenterOutcomeDB);
            }
          }

        } else {
          // Removing All Project Center Outcomes
          for (ProjectCenterOutcome projectCenterOutcome : projectDB.getProjectCenterOutcomes().stream()
            .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
            .collect(Collectors.toList())) {
            projectCenterOutcomeManager.deleteProjectCenterOutcome(projectCenterOutcome.getId());
          }
        }
      }

      // load basic info to project
      project.getProjectInfo().setCofinancing(projectDB.getProjectInfo().isCofinancing());
      // project.setGlobal(projectDB.isGlobal());


      // Saving project and add relations we want to save on the history

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_FOCUSES_RELATION);
      relationsName.add(APConstants.PROJECT_CLUSTER_ACTIVITIES_RELATION);
      relationsName.add(APConstants.PROJECT_SCOPES_RELATION);
      relationsName.add(APConstants.PROJECT_INFO_RELATION);

      project.getProjectInfo().setPhase(this.getActualPhase());
      project.getProjectInfo().setProject(project);
      project.getProjectInfo().setReporting(projectDB.getProjectInfo().getReporting());
      project.getProjectInfo().setAdministrative(projectDB.getProjectInfo().getAdministrative());
      project.getProjectInfo().setLocationRegional(projectDB.getProjectInfo().getLocationRegional());
      project.getProjectInfo().setLocationGlobal(projectDB.getProjectInfo().getLocationGlobal());

      project.getProjectInfo().setModificationJustification(this.getJustification());

      project.getProjectInfo().setModifiedBy(this.getCurrentUser());
      projectInfoManagerManager.saveProjectInfo(project.getProjectInfo());

      Path path = this.getAutoSaveFilePath();
      // if is auto-save, load relations to generate auditlog correctly
      if (path.toFile().exists() && this.getCurrentUser().isAutoSave()) {
        project.getProjectInfos().add(project.getProjectInfo());
        project.getProjectFocuses()
          .addAll(projectDB.getProjectFocuses().stream()
            .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
            .collect(Collectors.toList()));
        project.getProjectClusterActivities()
          .addAll(projectDB.getProjectClusterActivities().stream()
            .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
            .collect(Collectors.toList()));
      }

      /**
       * The following is required because we need to update something on the @Project if we want a row created in the
       * auditlog table.
       */
      this.setModificationJustification(project);
      projectDB = projectManager.saveProject(project, this.getActionName(), relationsName, this.getActualPhase());

      // delete the section stust for budgets by coA when there is one Coa selectd to the project
      /*
       * List<ProjectClusterActivity> currentClusters =
       * projectDB.getProjectClusterActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList());
       * if (currentClusters.isEmpty() || currentClusters.size() == 1) {
       * SectionStatus sectionStatus =
       * sectionStatusManager.getSectionStatusByProject(projectID, this.getCurrentCycle(), this.getCurrentCycleYear(),
       * this.getActualPhase().getUpkeep(), ProjectSectionStatusEnum.BUDGETBYCOA.getStatus());
       * if (sectionStatus != null) {
       * sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
       * }
       * }
       */

      // delete the draft file if exists
      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      // check if there is a url to redirect
      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        // check if there are missing field
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
        // No messages to next page

        this.addActionMessage("");
        this.setActionMessages(null);
        // redirect the url select by user
        return REDIRECT;
      }

    } else {
      // no permissions to edit
      return NOT_AUTHORIZED;
    }

  }

  public void setAllOwners(List<LiaisonUser> allOwners) {
    this.allOwners = allOwners;
  }


  public void setCenterOutcomes(List<CenterOutcome> centerOutcomes) {
    this.centerOutcomes = centerOutcomes;
  }

  public void setCenterPrograms(List<CrpProgram> centerPrograms) {
    this.centerPrograms = centerPrograms;
  }


  public void setClusterofActivites(List<CrpClusterOfActivity> clusterofActivites) {
    this.clusterofActivites = clusterofActivites;
  }


  public void setFile(File file) {
    this.file = file;
  }

  public void setFileContentType(String fileContentType) {
    this.fileContentType = fileContentType;
  }


  public void setFileFileName(String fileFileName) {
    this.fileFileName = fileFileName;
  }

  public void setFileReporting(File fileReporting) {
    this.fileReporting = fileReporting;
  }


  public void setFileReportingFileName(String fileReportingFileName) {
    this.fileReportingFileName = fileReportingFileName;
  }


  public void setLiaisonInstitutions(List<LiaisonInstitution> liaisonInstitutions) {
    this.liaisonInstitutions = liaisonInstitutions;
  }


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setProgramFlagships(List<CrpProgram> programFlagships) {
    this.programFlagships = programFlagships;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setProjectScales(Map<String, String> projectScales) {
    this.projectScales = projectScales;
  }


  public void setProjectStatuses(Map<String, String> projectStatuses) {
    this.projectStatuses = projectStatuses;
  }


  public void setProjectTypes(Map<String, String> projectTypes) {
    this.projectTypes = projectTypes;
  }


  public void setRegionFlagships(List<CrpProgram> regionFlagships) {
    this.regionFlagships = regionFlagships;
  }

  public void setRegionPrograms(List<CrpProgram> regionPrograms) {
    this.regionPrograms = regionPrograms;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    // if is saving call the validator to check for the missing fields
    if (save) {
      validator.validate(this, project, true);
    }
  }

}