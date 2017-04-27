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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonUserManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectClusterActivityManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectScopeManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpClusterOfActivity;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LiaisonUser;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectScope;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SectionStatus;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.FileManager;
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
 * @author Sebastian Amariles - CIAT/CCAFS
 * @author Christian Garcia- CIAT/CCAFS
 */
public class ProjectDescriptionAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  // Managers
  private ProjectManager projectManager;
  private SectionStatusManager sectionStatusManager;

  private ProjectFocusManager projectFocusManager;
  private FileDBManager fileDBManager;
  private CrpManager crpManager;
  private CrpProgramManager programManager;
  private ProjectClusterActivityManager projectClusterActivityManager;
  private CrpClusterOfActivityManager crpClusterOfActivityManager;
  private AuditLogManager auditLogManager;
  private ProjectScopeManager projectScopeManager;
  private LocElementTypeManager locationTypeManager;
  private String transaction;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private LiaisonUserManager liaisonUserManager;
  private HistoryComparator historyComparator;

  /*
   * private LiaisonInstitutionManager liaisonInstitutionManager;
   * private LiaisonUserManager liaisonUserManager;
   * private UserManager userManager;
   */

  // Front-end
  private long projectID;
  private Crp loggedCrp;
  private Project project;
  private List<CrpProgram> programFlagships;
  private List<CrpProgram> regionFlagships;

  private List<LiaisonInstitution> liaisonInstitutions;

  private List<CrpClusterOfActivity> clusterofActivites;


  private Map<String, String> projectStatuses;


  private List<LiaisonUser> allOwners;

  private Map<String, String> projectTypes;
  private Map<String, String> projectScales;


  private File file;


  private File fileReporting;

  private String fileContentType;

  private String fileFileName;

  private String fileReportingFileName;
  private ProjectDescriptionValidator validator;

  @Inject
  public ProjectDescriptionAction(APConfig config, ProjectManager projectManager, CrpManager crpManager,
    CrpProgramManager programManager, LiaisonUserManager liaisonUserManager,
    LiaisonInstitutionManager liaisonInstitutionManager, UserManager userManager,
    SectionStatusManager sectionStatusManager, ProjectFocusManager projectFocusManager, FileDBManager fileDBManager,
    AuditLogManager auditLogManager, ProjectDescriptionValidator validator,
    ProjectClusterActivityManager projectClusterActivityManager,
    CrpClusterOfActivityManager crpClusterOfActivityManager, LocElementTypeManager locationManager,
    ProjectScopeManager projectLocationManager, HistoryComparator historyComparator) {
    super(config);
    this.projectManager = projectManager;
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

  private Path getAutoSaveFilePath() {
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + actionFile + ".json";

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

    if (projectFocuses != null) {
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


  public Crp getLoggedCrp() {
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


  public long[] getRegionsIds() {

    List<CrpProgram> projectFocuses = project.getRegions();

    if (projectFocuses != null) {
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
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    } catch (Exception e) {

    }
    // SendMailGun s = new SendMailGun();
    // s.SendSimple();

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);
      Map<String, String> specialList = new HashMap<>();
      specialList.put(APConstants.PROJECT_FOCUSES_RELATION, "flagshipValue");

      this.setDifferences(historyComparator.getDifferences(transaction, specialList, "project"));


      if (history != null) {
        project = history;
        if (project.getWorkplan() != null) {
          project.setWorkplan(fileDBManager.getFileDBById(project.getWorkplan().getId()));
        }
        if (project.getBilateralContractName() != null) {
          project.setBilateralContractName(fileDBManager.getFileDBById(project.getBilateralContractName().getId()));
        }
        if (project.getAnnualReportToDonnor() != null) {
          project.setAnnualReportToDonnor(fileDBManager.getFileDBById(project.getAnnualReportToDonnor().getId()));
        }


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

        AutoSaveReader autoSaveReader = new AutoSaveReader();

        project = (Project) autoSaveReader.readFromJson(jReader);
        Project projectDb = projectManager.getProjectById(project.getId());
        project.setProjectEditLeader(projectDb.isProjectEditLeader());
        project.setAdministrative(projectDb.getAdministrative());
        if (project.getClusterActivities() != null) {
          for (ProjectClusterActivity projectClusterActivity : project.getClusterActivities()) {
            projectClusterActivity.setCrpClusterOfActivity(crpClusterOfActivityManager
              .getCrpClusterOfActivityById(projectClusterActivity.getCrpClusterOfActivity().getId()));
            projectClusterActivity.getCrpClusterOfActivity().setLeaders(projectClusterActivity.getCrpClusterOfActivity()
              .getCrpClusterActivityLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

          }
        }

        if (project.getScopes() != null) {
          for (ProjectScope projectScope : project.getScopes()) {
            projectScope
              .setLocElementType(locationTypeManager.getLocElementTypeById(projectScope.getLocElementType().getId()));
          }
        }

        if (project.getLiaisonUser() != null) {
          project.setLiaisonUser(liaisonUserManager.getLiaisonUserById(project.getLiaisonUser().getId()));
        }
        if (project.getLiaisonInstitution() != null) {
          project.setLiaisonInstitution(
            liaisonInstitutionManager.getLiaisonInstitutionById(project.getLiaisonInstitution().getId()));
        }


        List<CrpProgram> programs = new ArrayList<>();
        if (project.getFlagshipValue() != null) {
          for (String programID : project.getFlagshipValue().trim().replace("[", "").replace("]", "").split(",")) {
            try {
              CrpProgram program = programManager.getCrpProgramById(Long.parseLong(programID.trim()));
              programs.add(program);
            } catch (Exception e) {

            }
          }
        }


        List<CrpProgram> regions = new ArrayList<>();
        if (project.getRegionsValue() != null) {
          for (String programID : project.getRegionsValue().trim().replace("[", "").replace("]", "").split(",")) {
            try {
              CrpProgram program = programManager.getCrpProgramById(Long.parseLong(programID.trim()));
              regions.add(program);
            } catch (Exception e) {

            }
          }
        }
        project.setFlagships(programs);
        project.setRegions(regions);
        reader.close();
        this.setDraft(true);
      } else {
        this.setDraft(false);
        project.setFlagshipValue("");
        project.setRegionsValue("");
        List<CrpProgram> programs = new ArrayList<>();
        for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
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
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList())) {
          regions.add(projectFocuses.getCrpProgram());
          if (project.getRegionsValue().isEmpty()) {
            project.setRegionsValue(projectFocuses.getCrpProgram().getId().toString());
          } else {
            project
              .setRegionsValue(project.getRegionsValue() + "," + projectFocuses.getCrpProgram().getId().toString());
          }
        }

        List<ProjectClusterActivity> projectClusterActivities = new ArrayList<>();
        for (ProjectClusterActivity projectClusterActivity : project.getProjectClusterActivities().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {

          projectClusterActivity.getCrpClusterOfActivity().setLeaders(projectClusterActivity.getCrpClusterOfActivity()
            .getCrpClusterActivityLeaders().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
          projectClusterActivities.add(projectClusterActivity);
        }

        List<ProjectScope> projectLocations = new ArrayList<>();
        for (ProjectScope projectLocation : project.getProjectScopes().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {

          projectLocations.add(projectLocation);
        }
        project.setClusterActivities(projectClusterActivities);
        project.setFlagships(programs);
        project.setRegions(regions);
        project.setScopes(projectLocations);
      }
    }

    allOwners = new ArrayList<LiaisonUser>();
    allOwners.addAll(loggedCrp.getLiasonUsers());
    liaisonInstitutions = new ArrayList<LiaisonInstitution>();

    liaisonInstitutions
      .addAll(loggedCrp.getLiaisonInstitutions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    liaisonInstitutions.addAll(
      liaisonInstitutionManager.findAll().stream().filter(c -> c.getCrp() == null).collect(Collectors.toList()));
    programFlagships = new ArrayList<>();
    regionFlagships = new ArrayList<>();
    programFlagships.addAll(loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));

    programFlagships.sort((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym()));
    clusterofActivites = new ArrayList<>();

    for (CrpProgram crpProgram : project.getFlagships()) {
      crpProgram = programManager.getCrpProgramById(crpProgram.getId());
      clusterofActivites
        .addAll(crpProgram.getCrpClusterOfActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    }
    try {
      clusterofActivites.sort((p1, p2) -> p1.getIdentifier().compareTo(p2.getIdentifier()));
    } catch (Exception e) {
      clusterofActivites.sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
    }
    /*
     * clusterofActivites = crpClusterOfActivityManager.findAll().stream()
     * .filter(c -> c.isActive() && c.getCrpProgram().getCrp().getId().equals(loggedCrp.getId()))
     * .collect(Collectors.toList());
     */


    regionFlagships.addAll(loggedCrp.getCrpPrograms().stream()
      .filter(c -> c.isActive() && c.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
      .collect(Collectors.toList()));


    // Project Statuses
    projectStatuses = new HashMap<>();
    List<ProjectStatusEnum> list = Arrays.asList(ProjectStatusEnum.values());
    for (ProjectStatusEnum projectStatusEnum : list) {

      projectStatuses.put(projectStatusEnum.getStatusId(), projectStatusEnum.getStatus());
    }

    projectTypes = new HashMap<>();
    projectTypes.put(APConstants.PROJECT_CORE, this.getText("project.projectType.core"));
    projectTypes.put(APConstants.PROJECT_BILATERAL, this.getText("project.projectType.bilateral"));
    projectTypes.put(APConstants.PROJECT_CCAFS_COFUNDED, this.getText("project.projectType.cofounded"));

    projectScales = new HashMap<>();
    projectScales.put(APConstants.PROJECT_SCAPE_NATIONAL, this.getText("project.projectScape.national"));
    projectScales.put(APConstants.PROJECT_SCAPE_REGIONAL, this.getText("project.projectScape.regional"));
    projectScales.put(APConstants.PROJECT_SCAPE_GLOBAL, this.getText("project.projectType.global"));


    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_DESCRIPTION_BASE_PERMISSION, params));
    if (this.isHttpPost()) {
      if (project.getClusterActivities() != null) {
        project.getClusterActivities().clear();

      }
      project.setNoRegional(null);
      project.setCrossCuttingGender(null);
      project.setCrossCuttingCapacity(null);
      project.setCrossCuttingNa(null);
      project.setCrossCuttingYouth(null);
    }

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
      project.setCreateDate(projectDB.getCreateDate());
      project.setPresetDate(projectDB.getPresetDate());

      if (project.isNoRegional() == null) {
        project.setNoRegional(false);
      }


      if (project.getCrossCuttingCapacity() == null) {
        project.setCrossCuttingCapacity(false);
      }
      if (project.getCrossCuttingNa() == null) {
        project.setCrossCuttingNa(false);
      }
      if (project.getCrossCuttingGender() == null) {
        project.setCrossCuttingGender(false);
      }
      if (project.getCrossCuttingYouth() == null) {
        project.setCrossCuttingYouth(false);
      }
      project.setStatus(projectDB.getStatus());
      if (this.isReportingActive()) {

        project.setCrossCuttingCapacity(projectDB.getCrossCuttingCapacity());
        project.setCrossCuttingNa(projectDB.getCrossCuttingNa());
        project.setCrossCuttingGender(projectDB.getCrossCuttingGender());
        project.setCrossCuttingYouth(projectDB.getCrossCuttingYouth());


      }

      if (projectDB.isBilateralProject()) {

        if (file != null) {


          project.setBilateralContractName(this.getFileDB(projectDB.getBilateralContractName(), file, fileFileName,
            this.getBilateralContractAbsolutePath()));
          FileManager.copyFile(file,
            this.getBilateralContractAbsolutePath() + project.getBilateralContractName().getFileName());
        }


        if (this.isReportingActive()) {
          if (fileReporting != null) {
            // FileManager.deleteFile(this.getAnnualReportAbsolutePath() + projectDB.getAnnualReportToDornor());


            project.setAnnualReportToDonnor(this.getFileDB(projectDB.getAnnualReportToDonnor(), fileReporting,
              fileReportingFileName, this.getAnnualReportAbsolutePath()));

            FileManager.copyFile(fileReporting, this.getAnnualReportAbsolutePath() + fileReportingFileName);

          }
          /*
           * if (project.getAnnualReportToDonnor().getFileName().isEmpty()) {
           * project.setAnnualReportToDonnor(null);
           * }
           */
        }

      }

      if (project.getLiaisonInstitution() != null) {
        if (project.getLiaisonInstitution().getId() == -1) {
          project.setLiaisonInstitution(null);
        }
      }

      if (project.getLiaisonUser() != null) {
        if (project.getLiaisonUser().getId() == -1) {
          project.setLiaisonUser(null);
        }
      }

      if (project.getFlagshipValue() != null && project.getFlagshipValue().length() > 0) {

        for (ProjectFocus projectFocus : projectDB.getProjectFocuses().stream()
          .filter(
            c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList())) {

          if (!project.getFlagshipValue().contains(projectFocus.getCrpProgram().getId().toString())) {
            projectFocusManager.deleteProjectFocus(projectFocus.getId());

          }
        }
        for (String programID : project.getFlagshipValue().trim().split(",")) {
          if (programID.length() > 0) {
            CrpProgram program = programManager.getCrpProgramById(Long.parseLong(programID.trim()));
            ProjectFocus projectFocus = new ProjectFocus();
            projectFocus.setCrpProgram(program);
            projectFocus.setProject(project);
            if (projectDB.getProjectFocuses().stream()
              .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == program.getId().longValue())
              .collect(Collectors.toList()).isEmpty()) {
              projectFocus.setActive(true);
              projectFocus.setActiveSince(new Date());
              projectFocus.setCreatedBy(this.getCurrentUser());
              projectFocus.setModifiedBy(this.getCurrentUser());
              projectFocus.setModificationJustification("");
              projectFocusManager.saveProjectFocus(projectFocus);
            }
          }

        }
      }


      List<ProjectFocus> regionsPreview = projectDB.getProjectFocuses().stream()
        .filter(c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());
      for (ProjectFocus projectFocus : regionsPreview) {
        if (!project.getRegionsValue().contains(projectFocus.getCrpProgram().getId().toString())) {
          projectFocusManager.deleteProjectFocus(projectFocus.getId());
        }
      }
      if (project.getRegionsValue() != null && project.getRegionsValue().length() > 0) {
        for (String programID : project.getRegionsValue().trim().split(",")) {
          if (programID.length() > 0) {
            CrpProgram program = programManager.getCrpProgramById(Long.parseLong(programID.trim()));
            ProjectFocus projectFocus = new ProjectFocus();
            projectFocus.setCrpProgram(program);
            projectFocus.setProject(project);

            if (projectDB.getProjectFocuses().stream()
              .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == program.getId().longValue())
              .collect(Collectors.toList()).isEmpty()) {
              projectFocus.setActive(true);
              projectFocus.setActiveSince(new Date());
              projectFocus.setCreatedBy(this.getCurrentUser());
              projectFocus.setModifiedBy(this.getCurrentUser());
              projectFocus.setModificationJustification("");
              projectFocusManager.saveProjectFocus(projectFocus);
            }
          }

        }

      }

      // Removing Project Cluster Activities
      if (this.isPlanningActive()) {


        for (ProjectClusterActivity projectClusterActivity : projectDB.getProjectClusterActivities().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList())) {

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
              projectClusterActivity.setCreatedBy(this.getCurrentUser());

              projectClusterActivity.setActiveSince(new Date());
              projectClusterActivity.setActive(true);
              projectClusterActivity.setProject(project);
              projectClusterActivity.setModifiedBy(this.getCurrentUser());
              projectClusterActivity.setModificationJustification("");
              projectClusterActivityManager.saveProjectClusterActivity(projectClusterActivity);
            }

          }
        }


        Project projectCluster = projectManager.getProjectById(projectID);
        List<ProjectClusterActivity> currentClusters =
          projectCluster.getProjectClusterActivities().stream().filter(c -> c.isActive()).collect(Collectors.toList());
        if (currentClusters.isEmpty() || currentClusters.size() == 1) {
          SectionStatus sectionStatus = sectionStatusManager.getSectionStatusByProject(projectID,
            this.getCurrentCycle(), this.getCurrentCycleYear(), ProjectSectionStatusEnum.BUDGETBYCOA.getStatus());
          if (sectionStatus != null) {
            sectionStatusManager.deleteSectionStatus(sectionStatus.getId());
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
            projectLocation.setCreatedBy(this.getCurrentUser());

            projectLocation.setActiveSince(new Date());
            projectLocation.setActive(true);
            projectLocation.setProject(project);
            projectLocation.setModifiedBy(this.getCurrentUser());
            projectLocation.setModificationJustification("");
            projectScopeManager.saveProjectScope(projectLocation);
          }

        }
      }


      project.setCrp(loggedCrp);
      project.setCofinancing(projectDB.isCofinancing());
      // project.setGlobal(projectDB.isGlobal());
      project.setLeaderResponsabilities(projectDB.getLeaderResponsabilities());
      if (project.getWorkplan() != null) {
        if (project.getWorkplan().getId() == null) {
          project.setWorkplan(null);
        }
      }

      if (project.getBilateralContractName() != null) {
        if (project.getBilateralContractName().getId() == null) {
          project.setBilateralContractName(null);
        }
      }
      if (project.getAnnualReportToDonnor() != null) {
        if (project.getAnnualReportToDonnor().getId() == null) {
          project.setAnnualReportToDonnor(null);
        }
      }
      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_FOCUSES_RELATION);
      relationsName.add(APConstants.PROJECT_CLUSTER_ACTIVITIES_RELATION);
      relationsName.add(APConstants.PROJECT_SCOPES_RELATION);
      project.setActiveSince(new Date());
      project.setReporting(projectDB.getReporting());
      project.setAdministrative(projectDB.getAdministrative());
      project.setModificationJustification(this.getJustification());
      projectManager.saveProject(project, this.getActionName(), relationsName);
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


  public void setAllOwners(List<LiaisonUser> allOwners) {
    this.allOwners = allOwners;
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


  public void setLoggedCrp(Crp loggedCrp) {
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


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {
      validator.validate(this, project, true);
    }
  }

}