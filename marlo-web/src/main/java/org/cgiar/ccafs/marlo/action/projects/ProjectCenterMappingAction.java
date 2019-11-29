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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectCenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectFocusManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.impl.CenterOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectCenterOutcome;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.projects.ProjectCenterMappingValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
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
public class ProjectCenterMappingAction extends BaseAction {

  private static final long serialVersionUID = 6623716093003057656L;

  private static final Logger LOG = LoggerFactory.getLogger(ProjectCenterMappingAction.class);


  private ProjectManager projectManager;

  private CrpProgramManager programManager;
  private GlobalUnitManager crpManager;
  private ProjectFocusManager projectFocusManager;
  private AuditLogManager auditLogManager;
  private long projectID;
  private GlobalUnit loggedCrp;
  private Project project;
  private List<CrpProgram> programFlagships;
  private List<CrpProgram> regionFlagships;
  private String transaction;
  private List<CrpProgram> centerPrograms;
  private List<CrpProgram> regionPrograms;
  private ProjectInfoManager projectInfoManager;
  private Project projectDB;
  private ProjectCenterMappingValidator validator;
  private long sharedPhaseID;
  private PhaseManager phaseManager;
  private List<LiaisonInstitution> liaisonInstitutions;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ProjectCenterOutcomeManager projectCenterOutcomeManager;
  private CenterOutcomeManager centerOutcomeManager;
  private List<CenterOutcome> centerOutcomes;
  private boolean canMapPrograms;

  @Inject
  public ProjectCenterMappingAction(APConfig config, ProjectManager projectManager, CrpProgramManager programManager,
    GlobalUnitManager crpManager, ProjectFocusManager projectFocusManager, AuditLogManager auditLogManager,
    ProjectInfoManager projectInfoManager, ProjectCenterMappingValidator validator, PhaseManager phaseManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ProjectCenterOutcomeManager projectCenterOutcomeManager,
    CenterOutcomeManager centerOutcomeManager) {
    super(config);
    this.projectManager = projectManager;
    this.programManager = programManager;
    this.crpManager = crpManager;
    this.projectFocusManager = projectFocusManager;
    this.auditLogManager = auditLogManager;
    this.projectInfoManager = projectInfoManager;
    this.validator = validator;
    this.phaseManager = phaseManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.projectCenterOutcomeManager = projectCenterOutcomeManager;
    this.centerOutcomeManager = centerOutcomeManager;
  }

  private Path getAutoSaveFilePath(Phase phase) {
    // get the class simple name
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + phase.getName() + "_" + phase.getYear()
      + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<CenterOutcome> getCenterOutcomes() {
    return centerOutcomes;
  }

  public List<CrpProgram> getCenterPrograms() {
    return centerPrograms;
  }

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

  public List<CrpProgram> getRegionFlagships() {
    return regionFlagships;
  }


  public List<CrpProgram> getRegionPrograms() {
    return regionPrograms;
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

  public long getSharedPhaseID() {
    return sharedPhaseID;
  }

  public String getTransaction() {
    return transaction;
  }

  public boolean isCanMapPrograms() {
    return canMapPrograms;
  }


  @Override
  public void prepare() throws Exception {
    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());

    Phase phase = this.getActualPhase();
    sharedPhaseID = phase.getId();
    canMapPrograms = false;

    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    } catch (Exception e) {
      LOG.error("unable to parse projectID", e);
      /**
       * Original code swallows the exception and didn't even log it. Now we at least log it,
       * but we need to revisit to see if we should continue processing or re-throw the exception.
       */
    }

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      // auditLogManager.getHistory Bring us the history with the transaction id
      Project history = (Project) auditLogManager.getHistory(transaction);

      // In case there are some relationships that are displayed on the front in a particular field, add to this list by
      // passing the name of the relationship and the name of the attribute with which it is displayed on the front
      Map<String, String> specialList = new HashMap<>();
      specialList.put(APConstants.PROJECT_FOCUSES_RELATION, "flagshipValue");


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
      Path path = this.getAutoSaveFilePath(phase);
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
        // load LiaisonInstitutionCenter info
        if (project.getProjectInfo().getLiaisonInstitutionCenter() != null) {
          projectDb.getProjectInfo().setLiaisonInstitutionCenter(liaisonInstitutionManager
            .getLiaisonInstitutionById(project.getProjectInfo().getLiaisonInstitutionCenter().getId()));
        } else {
          projectDb.getProjectInfo().setLiaisonInstitutionCenter(null);
        }


        project.setProjectInfo(projectDb.getProjectInfo());
        project.getProjectInfo().setProjectEditLeader(projectDb.getProjecInfoPhase(phase).isProjectEditLeader());
        project.getProjectInfo().setAdministrative(projectDb.getProjecInfoPhase(phase).getAdministrative());
        project.getProjectInfo().setPhase(projectDb.getProjecInfoPhase(phase).getPhase());


        if (project.getCenterOutcomes() != null) {
          for (ProjectCenterOutcome projectCenterOutcome : project.getCenterOutcomes()) {
            projectCenterOutcome.setCenterOutcome(
              centerOutcomeManager.getResearchOutcomeById(projectCenterOutcome.getCenterOutcome().getId()));
          }
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

        this.setDraft(true);

      } else {
        this.setDraft(false);

        // Load the DB information and adjust it to the structures with the front end
        project.setProjectInfo(project.getProjecInfoPhase(phase));
        if (project.getProjectInfo() == null) {
          project.setProjectInfo(new ProjectInfo());
        }

        if (project.getProjectInfo().getLiaisonInstitutionCenter() != null
          && project.getProjectInfo().getLiaisonInstitutionCenter().getId() != null) {
          project.getProjectInfo().setLiaisonInstitutionCenter(liaisonInstitutionManager
            .getLiaisonInstitutionById(project.getProjectInfo().getLiaisonInstitutionCenter().getId()));
        } else {
          project.getProjecInfoPhase(this.getActualPhase()).setLiaisonInstitutionCenter(null);
        }

        // Load the center Programs
        project.setFlagshipValue("");
        project.setRegionsValue("");
        List<CrpProgram> programs = new ArrayList<>();
        for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(phase)
            && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
            && c.getCrpProgram().getCrp().getId().equals(this.getLoggedCrp().getId()))
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
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(phase)
            && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
            && c.getCrpProgram().getCrp().getId().equals(this.getLoggedCrp().getId()))
          .collect(Collectors.toList())) {
          regions.add(projectFocuses.getCrpProgram());
          if (project.getRegionsValue() != null && project.getRegionsValue().isEmpty()) {
            project.setRegionsValue(projectFocuses.getCrpProgram().getId().toString());
          } else {
            project
              .setRegionsValue(project.getRegionsValue() + "," + projectFocuses.getCrpProgram().getId().toString());
          }
        }

        List<ProjectCenterOutcome> projectCenterOutcomes = new ArrayList<>();
        for (ProjectCenterOutcome projectCenterOutcome : project.getProjectCenterOutcomes().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(phase))
          .collect(Collectors.toList())) {
          projectCenterOutcome.setCenterOutcome(
            centerOutcomeManager.getResearchOutcomeById(projectCenterOutcome.getCenterOutcome().getId()));
          projectCenterOutcomes.add(projectCenterOutcome);
        }
        project.setCenterOutcomes(projectCenterOutcomes);
        project.setFlagships(programs);
        project.setRegions(regions);
      }

      liaisonInstitutions = new ArrayList<LiaisonInstitution>();
      // load the liasons intitutions for the crp
      liaisonInstitutions.addAll(loggedCrp.getLiaisonInstitutions().stream()
        .filter(c -> c.isActive() && c.getCrpProgram() != null
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList()));
      // load the flaghsips an regions
      programFlagships = new ArrayList<>();
      regionFlagships = new ArrayList<>();
      programFlagships.addAll(loggedCrp.getCrpPrograms().stream()
        .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList()));

      programFlagships.sort((p1, p2) -> p1.getCenterComposedName().compareTo(p2.getCenterComposedName()));


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

      regionFlagships.addAll(loggedCrp.getCrpPrograms().stream()
        .filter(c -> c.isActive() && c.getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList()));

    }

    projectDB = projectManager.getProjectById(projectID);
    // adjust crp programs leader to enable only modify centersoutcomes.
    if (this.isRole("CRP-Admin") || this.isRole("SuperAdmin")) {
      canMapPrograms = true;
    }

    // The base permission is established for the current section

    String params[] = {loggedCrp.getAcronym() + ""};
    this.setBasePermission(this.getText(Permission.SHARED_PROJECT_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      project.getProjecInfoPhase(this.getActualPhase()).setLiaisonInstitutionCenter(null);
      project.setFlagshipValue(null);
      project.setRegionsValue(null);
      if (project.getCenterOutcomes() != null) {
        project.getCenterOutcomes().clear();
      }
    }
  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit") || this.hasCenterPermissions(projectID)) {
      Phase sharedPhase = phaseManager.getPhaseById(sharedPhaseID);

      // Load basic info project to be saved
      projectDB.setProjectInfo(projectDB.getProjecInfoPhase(sharedPhase));

      project.setCreateDate(projectDB.getCreateDate());
      project.getProjectInfo().setPresetDate(projectDB.getProjectInfo().getPresetDate());

      project.getProjectInfo().setStatus(projectDB.getProjectInfo().getStatus());

      if (project.getProjectInfo().getLiaisonInstitutionCenter() != null) {
        if (project.getProjectInfo().getLiaisonInstitutionCenter().getId() == -1) {
          project.getProjectInfo().setLiaisonInstitutionCenter(null);
        }
      }

      // Saving the flaghsips
      if (project.getFlagshipValue() != null && project.getFlagshipValue().length() > 0) {

        List<ProjectFocus> fpsPreview = projectDB.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(sharedPhase)
            && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
            && c.getCrpProgram().getCrp().getId().equals(loggedCrp.getId()))
          .collect(Collectors.toList());
        for (ProjectFocus projectFocus : fpsPreview) {
          if (!project.getFlagshipValue().contains(projectFocus.getCrpProgram().getId().toString())) {
            projectFocusManager.deleteProjectFocus(projectFocus.getId());
          }
        }

        for (String programID : project.getFlagshipValue().trim().split(",")) {
          if (programID.length() > 0) {
            CrpProgram program =
              programManager.getCrpProgramById(Long.parseLong(programID.trim().replaceAll("[^0-9]", "")));
            ProjectFocus projectFocus = new ProjectFocus();
            projectFocus.setCrpProgram(program);
            projectFocus.setProject(projectDB);

            projectFocus.setPhase(sharedPhase);
            if (projectDB.getProjectFocuses().stream()
              .filter(c -> c.isActive() && c.getCrpProgram().getId().longValue() == program.getId().longValue()
                && c.getPhase().equals(sharedPhase))
              .collect(Collectors.toList()).isEmpty()) {
              projectFocus.setPhase(sharedPhase);
              projectFocusManager.saveProjectFocus(projectFocus);
            }
          }

        }
      } else {
        // Delete All flagships
        for (ProjectFocus projectFocus : projectDB.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(sharedPhase)
            && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
            && c.getCrpProgram().getCrp().getId().equals(loggedCrp.getId()))
          .collect(Collectors.toList())) {
          projectFocusManager.deleteProjectFocus(projectFocus.getId());
        }
      }


      // Saving the regions
      if (project.getRegionsValue() != null && project.getRegionsValue().length() > 0) {

        List<ProjectFocus> regionsPreview = projectDB.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(sharedPhase)
            && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
            && c.getCrpProgram().getCrp().getId().equals(loggedCrp.getId()))
          .collect(Collectors.toList());
        for (ProjectFocus projectFocus : regionsPreview) {
          if (!project.getRegionsValue().contains(projectFocus.getCrpProgram().getId().toString())) {
            projectFocusManager.deleteProjectFocus(projectFocus.getId());
          }
        }

        for (String programID : project.getRegionsValue().trim().split(",")) {
          if (programID.length() > 0) {
            CrpProgram program = programManager.getCrpProgramById(Long.parseLong(programID.trim()));
            ProjectFocus projectFocus = new ProjectFocus();
            projectFocus.setCrpProgram(program);
            projectFocus.setProject(projectDB);
            projectFocus.setPhase(sharedPhase);
            if (projectDB.getProjectFocuses().stream()
              .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(sharedPhase)
                && c.getCrpProgram().getId().longValue() == program.getId().longValue())
              .collect(Collectors.toList()).isEmpty()) {
              projectFocus.setPhase(sharedPhase);
              projectFocusManager.saveProjectFocus(projectFocus);
            }
          }
        }
      } else {
        // Delete All Regions
        for (ProjectFocus projectFocus : projectDB.getProjectFocuses().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(sharedPhase)
            && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
            && c.getCrpProgram().getCrp().getId().equals(loggedCrp.getId()))
          .collect(Collectors.toList())) {
          projectFocusManager.deleteProjectFocus(projectFocus.getId());
        }
      }


      // Saving Project Center Outcomes
      if (project.getCenterOutcomes() != null && !project.getCenterOutcomes().isEmpty()) {
        // Removing Project Center Outcomes
        for (ProjectCenterOutcome projectCenterOutcome : projectDB.getProjectCenterOutcomes().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(sharedPhase))
          .collect(Collectors.toList())) {
          if (!project.getCenterOutcomes().contains(projectCenterOutcome)) {
            projectCenterOutcomeManager.deleteProjectCenterOutcome(projectCenterOutcome.getId());
          }
        }
        // Add Project Center Outcomes
        for (ProjectCenterOutcome projectCenterOutcome : project.getCenterOutcomes()) {
          if (projectCenterOutcome.getId() == null) {
            projectCenterOutcome.setProject(project);
            projectCenterOutcome.setPhase(sharedPhase);
            projectCenterOutcome = projectCenterOutcomeManager.saveProjectCenterOutcome(projectCenterOutcome);
            // This add centerOutcome to generate correct auditlog.
            project.getProjectCenterOutcomes().add(projectCenterOutcome);
          }
        }

      } else {
        // Removing All Project Center Outcomes
        for (ProjectCenterOutcome projectCenterOutcome : projectDB.getProjectCenterOutcomes().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(sharedPhase))
          .collect(Collectors.toList())) {
          projectCenterOutcomeManager.deleteProjectCenterOutcome(projectCenterOutcome.getId());
        }
      }


      project.getProjectInfo().setCofinancing(projectDB.getProjectInfo().isCofinancing());

      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_FOCUSES_RELATION);
      relationsName.add(APConstants.PROJECT_CLUSTER_ACTIVITIES_RELATION);
      relationsName.add(APConstants.PROJECT_SCOPES_RELATION);
      relationsName.add(APConstants.PROJECT_INFO_RELATION);

      project.getProjectInfo().setPhase(sharedPhase);
      project.getProjectInfo().setProject(project);
      project.getProjectInfo().setReporting(projectDB.getProjectInfo().getReporting());
      project.getProjectInfo().setAdministrative(projectDB.getProjectInfo().getAdministrative());
      project.getProjectInfo().setNewPartnershipsPlanned(projectDB.getProjectInfo().getNewPartnershipsPlanned());
      project.getProjectInfo().setLocationRegional(projectDB.getProjectInfo().getLocationRegional());
      project.getProjectInfo().setLocationGlobal(projectDB.getProjectInfo().getLocationGlobal());
      project.getProjectInfo().setLocationRegional(projectDB.getProjectInfo().getLocationRegional());
      project.getProjectInfo().setLiaisonInstitution(projectDB.getProjectInfo().getLiaisonInstitution());
      project.getProjectInfo().setLiaisonUser(projectDB.getProjectInfo().getLiaisonUser());

      project.getProjectInfo().setModificationJustification(this.getJustification());
      project.getProjectInfo().setActiveSince(new Date());
      projectInfoManager.saveProjectInfo(project.getProjectInfo());

      // Saving project and add relations we want to save on the history

      // List<String> relationsName = new ArrayList<>();
      // relationsName.add(APConstants.PROJECT_FOCUSES_RELATION);
      // relationsName.add(APConstants.PROJECT_INFO_RELATION);
      // /**
      // * The following is required because we need to update something on the @Project if we want a row created in
      // the
      // * auditlog table.
      // */
      this.setModificationJustification(project);
      projectManager.saveProject(project, this.getActionName(), relationsName, sharedPhase);

      Path path = this.getAutoSaveFilePath(sharedPhase);
      // delete the draft file if exists
      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      // check if there is a url to redirect
      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        // check if there are missing field
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
      }

      return SUCCESS;
    } else

    {
      return NOT_AUTHORIZED;
    }

  }

  public void setCanMapPrograms(boolean canMapPrograms) {
    this.canMapPrograms = canMapPrograms;
  }

  public void setCenterOutcomes(List<CenterOutcome> centerOutcomes) {
    this.centerOutcomes = centerOutcomes;
  }

  public void setCenterPrograms(List<CrpProgram> centerPrograms) {
    this.centerPrograms = centerPrograms;
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

  public void setRegionFlagships(List<CrpProgram> regionFlagships) {
    this.regionFlagships = regionFlagships;
  }

  public void setRegionPrograms(List<CrpProgram> regionPrograms) {
    this.regionPrograms = regionPrograms;
  }

  public void setSharedPhaseID(long sharedPhaseID) {
    this.sharedPhaseID = sharedPhaseID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    // if is saving call the validator to check for the missing fields
    if (save) {
      validator.validate(this, project, true, sharedPhaseID);
    }
  }

}
