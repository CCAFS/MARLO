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
import org.cgiar.ccafs.marlo.data.manager.ExpectedStudyProjectManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;
import org.cgiar.ccafs.marlo.data.model.GlobalScopeEnum;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.TypeExpectedStudiesEnum;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.utils.HistoryDifference;
import org.cgiar.ccafs.marlo.validation.projects.ProjectExpectedStudiesValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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

/**
 * @author Christian Garcia - CIAT/CCAFS
 */
public class ProjectExpectedStudiesAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 597647662288518417L;


  private ProjectExpectedStudiesValidator projectExpectedStudiesValidator;


  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ExpectedStudyProjectManager expectedStudyProjectManager;


  private AuditLogManager auditLogManager;

  private GlobalUnitManager crpManager;


  private HistoryComparator historyComparator;


  private GlobalUnit loggedCrp;


  private Project project;

  private long projectID;

  private ProjectManager projectManager;
  private PhaseManager phaseManager;

  private SrfSloIndicatorManager srfSloIndicatorManager;
  private SrfSubIdoManager srfSubIdoManager;

  private Map<Long, String> subIdos;
  private Map<Long, String> targets;
  private Map<Integer, String> types;
  private Map<Integer, String> scopes;
  private List<Project> myProjects;


  private String transaction;


  @Inject
  public ProjectExpectedStudiesAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, SrfSloIndicatorManager srfSloIndicatorManager,
    SrfSubIdoManager srfSubIdoManager, AuditLogManager auditLogManager,
    ExpectedStudyProjectManager expectedStudyProjectManager,
    ProjectExpectedStudiesValidator projectExpectedStudiesValidator, HistoryComparator historyComparator,
    PhaseManager phaseManager) {
    super(config);
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.phaseManager = phaseManager;
    this.auditLogManager = auditLogManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.srfSloIndicatorManager = srfSloIndicatorManager;
    this.expectedStudyProjectManager = expectedStudyProjectManager;
    this.historyComparator = historyComparator;
    this.projectExpectedStudiesValidator = projectExpectedStudiesValidator;

  }


  public void expectedStudiesNewData() {

    for (ProjectExpectedStudy projectExpectedStudy : project.getExpectedStudies()) {
      ProjectExpectedStudy projectExpectedStudyNew = null;
      if (projectExpectedStudy != null) {

        if (projectExpectedStudy.getProjects() == null) {
          projectExpectedStudy.setProjects(new ArrayList<>());
        }
        if (projectExpectedStudy.getSrfSloIndicator() != null
          && projectExpectedStudy.getSrfSloIndicator().getId() > 0) {
          projectExpectedStudy.setSrfSloIndicator(
            srfSloIndicatorManager.getSrfSloIndicatorById(projectExpectedStudy.getSrfSloIndicator().getId()));
        }
        if (projectExpectedStudy.getSrfSubIdo() != null && projectExpectedStudy.getSrfSubIdo().getId() > 0) {
          projectExpectedStudy
            .setSrfSubIdo(srfSubIdoManager.getSrfSubIdoById(projectExpectedStudy.getSrfSubIdo().getId()));
        }

        if (projectExpectedStudy.getId() == null) {

          projectExpectedStudyNew = new ProjectExpectedStudy();
          projectExpectedStudyNew.setActive(true);
          projectExpectedStudyNew.setCreatedBy(this.getCurrentUser());
          projectExpectedStudyNew.setModifiedBy(this.getCurrentUser());
          projectExpectedStudyNew.setModificationJustification("");
          projectExpectedStudyNew.setActiveSince(new Date());
          projectExpectedStudyNew.setProject(project);
          projectExpectedStudyNew.setPhase(this.getActualPhase());
          projectExpectedStudyNew.setComposedId(projectExpectedStudy.getComposedId());
        } else {

          projectExpectedStudyNew =
            projectExpectedStudyManager.getProjectExpectedStudyById(projectExpectedStudy.getId());
          projectExpectedStudyNew.setModifiedBy(this.getCurrentUser());


        }
        projectExpectedStudyNew.setComments(projectExpectedStudy.getComments());
        projectExpectedStudyNew.setOtherType(projectExpectedStudy.getOtherType());
        projectExpectedStudyNew.setScope(projectExpectedStudy.getScope());
        projectExpectedStudyNew.setSrfSloIndicator(projectExpectedStudy.getSrfSloIndicator());
        projectExpectedStudyNew.setSrfSubIdo(projectExpectedStudy.getSrfSubIdo());
        projectExpectedStudyNew.setTopicStudy(projectExpectedStudy.getTopicStudy());
        projectExpectedStudyNew.setType(projectExpectedStudy.getType());
        projectExpectedStudyNew.setProjects(projectExpectedStudy.getProjects());
        projectExpectedStudyNew = projectExpectedStudyManager.saveProjectExpectedStudy(projectExpectedStudyNew);


        if (projectExpectedStudy.getProjects() != null) {

          List<ExpectedStudyProject> expectedStudyProjectsPrew = projectExpectedStudyNew.getExpectedStudyProjects()
            .stream().filter(da -> da.isActive()).collect(Collectors.toList());

          for (ExpectedStudyProject expectedStudy : expectedStudyProjectsPrew) {
            if (!projectExpectedStudy.getProjects().contains(expectedStudy)) {

              expectedStudyProjectManager.deleteExpectedStudyProject(expectedStudy.getId());
            }
          }

          for (ExpectedStudyProject expectedStudy : projectExpectedStudy.getProjects()) {
            if (expectedStudy.getId() == null || expectedStudy.getId() == -1) {

              ExpectedStudyProject expectedStudyNew = new ExpectedStudyProject();

              expectedStudyNew.setProjectExpectedStudy(projectExpectedStudyNew);
              expectedStudyNew.setActive(true);
              expectedStudyNew.setCreatedBy(this.getCurrentUser());
              expectedStudyNew.setModifiedBy(this.getCurrentUser());
              expectedStudyNew.setModificationJustification("");
              expectedStudyNew.setActiveSince(new Date());
              expectedStudyNew.setMyProject(expectedStudy.getMyProject());


              expectedStudyProjectManager.saveExpectedStudyProject(expectedStudyNew);
            }
          }
        }
      }

    }

  }


  public void expectedStudiesPreviousData(List<ProjectExpectedStudy> expectedStudies) {

    List<ProjectExpectedStudy> expectedStudiesPrev;
    Project projectBD = projectManager.getProjectById(projectID);


    expectedStudiesPrev = projectBD.getProjectExpectedStudies().stream()
      .filter(a -> a.isActive() && a.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());


    for (ProjectExpectedStudy projectExpectedStudy : expectedStudiesPrev) {
      if (!expectedStudies.contains(projectExpectedStudy)) {
        for (ExpectedStudyProject expectedStudyProject : projectExpectedStudy.getExpectedStudyProjects().stream()
          .filter(da -> da.isActive()).collect(Collectors.toList())) {
          expectedStudyProjectManager.deleteExpectedStudyProject(expectedStudyProject.getId());
        }
        projectExpectedStudyManager.deleteProjectExpectedStudy(projectExpectedStudy.getId());
      }
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


  public List<Project> getMyProjects() {
    return myProjects;
  }


  public Project getProject() {
    return project;
  }


  public long getProjectID() {
    return projectID;
  }

  public Map<Integer, String> getScopes() {
    return scopes;
  }


  public Map<Long, String> getSubIdos() {
    return subIdos;
  }

  public Map<Long, String> getTargets() {
    return targets;
  }


  public String getTransaction() {
    return transaction;
  }


  public Map<Integer, String> getTypes() {
    return types;
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
        project.setExpectedStudies(
          project.getProjectExpectedStudies().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
        for (ProjectExpectedStudy expectedStudy : project.getExpectedStudies()) {
          int[] index = new int[1];
          index[0] = i;
          differences.addAll(historyComparator.getDifferencesList(expectedStudy, transaction, specialList,
            "project.expectedStudy[" + i + "]", "project", 1));
          i++;
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
        if (project.getExpectedStudies() != null) {


          for (ProjectExpectedStudy expectedStudy : project.getExpectedStudies()) {
            if (expectedStudy.getProjects() != null) {

              for (ExpectedStudyProject expectedStudyProject : expectedStudy.getProjects()) {
                expectedStudyProject
                  .setMyProject(projectManager.getProjectById(expectedStudyProject.getMyProject().getId()));
                expectedStudyProject.getMyProject()
                  .setProjectInfo(expectedStudyProject.getMyProject().getProjecInfoPhase(this.getActualPhase()));
              }
            }
          }
        }
        this.setDraft(true);
      } else {
        this.setDraft(false);
        project.setExpectedStudies(project.getProjectExpectedStudies().stream()
          .filter(a -> a.isActive() && a.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));

        for (ProjectExpectedStudy expectedStudy : project.getExpectedStudies()) {
          expectedStudy.setProjects(
            expectedStudy.getExpectedStudyProjects().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
          for (ExpectedStudyProject expectedStudyProject : expectedStudy.getProjects()) {
            expectedStudyProject
              .setMyProject(projectManager.getProjectById(expectedStudyProject.getMyProject().getId()));
            expectedStudyProject.getMyProject()
              .setProjectInfo(expectedStudyProject.getMyProject().getProjecInfoPhase(this.getActualPhase()));
          }
        }
        project.setSharedExpectedStudies(new ArrayList<>());
        for (ExpectedStudyProject expectedStudyProject : project.getExpectedStudyProjects().stream()
          .filter(c -> c.isActive() && c.getProjectExpectedStudy().getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList())) {
          project.getSharedExpectedStudies().add(expectedStudyProject.getProjectExpectedStudy());

        }
        for (ProjectExpectedStudy expectedStudy : project.getSharedExpectedStudies()) {
          expectedStudy.setProjects(
            expectedStudy.getExpectedStudyProjects().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
          for (ExpectedStudyProject expectedStudyProject : expectedStudy.getProjects()) {
            expectedStudyProject
              .setMyProject(projectManager.getProjectById(expectedStudyProject.getMyProject().getId()));
            expectedStudyProject.getMyProject()
              .setProjectInfo(expectedStudyProject.getMyProject().getProjecInfoPhase(this.getActualPhase()));
          }
        }

        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));


      }

      types = new HashMap<>();
      List<TypeExpectedStudiesEnum> list = Arrays.asList(TypeExpectedStudiesEnum.values());
      for (TypeExpectedStudiesEnum typeExpectedStudiesEnum : list) {
        types.put(typeExpectedStudiesEnum.getId(), typeExpectedStudiesEnum.getType());
      }
      scopes = new HashMap<>();
      List<GlobalScopeEnum> listScope = Arrays.asList(GlobalScopeEnum.values());
      for (GlobalScopeEnum globalScopeEnum : listScope) {
        scopes.put(globalScopeEnum.getId(), globalScopeEnum.getType());
      }
      subIdos = new HashMap<>();
      for (SrfSubIdo srfSubIdo : srfSubIdoManager.findAll()) {
        subIdos.put(srfSubIdo.getId(), srfSubIdo.getDescription());
      }
      targets = new HashMap<>();
      for (SrfSloIndicator srfSloIndicator : srfSloIndicatorManager.findAll()) {
        targets.put(srfSloIndicator.getId(), srfSloIndicator.getTitle());
      }
      Phase phase = this.getActualPhase();
      phase = phaseManager.getPhaseById(phase.getId());
      myProjects = new ArrayList<>();
      for (ProjectPhase projectPhase : phase.getProjectPhases().stream().filter(c -> c.isActive())
        .collect(Collectors.toList())) {
        if (projectPhase.getProject().isActive()) {
          projectPhase.getProject().setProjectInfo(projectPhase.getProject().getProjecInfoPhase(this.getActualPhase()));
          if (projectPhase.getProject().getProjectInfo().getStatus().intValue() == Integer
            .parseInt(ProjectStatusEnum.Ongoing.getStatusId())
            || projectPhase.getProject().getProjectInfo().getStatus().intValue() == Integer
              .parseInt(ProjectStatusEnum.Extended.getStatusId())) {
            myProjects.add(projectPhase.getProject());
          }

        }

      }

      Collections.sort(myProjects, (p1, p2) -> p1.getId().compareTo(p2.getId()));
      myProjects.remove(project);
      String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
      this.setBasePermission(this.getText(Permission.PROJECT_EXPECTED_STUDIES_BASE_PERMISSION, params));
    }
    if (this.isHttpPost()) {
      if (project.getExpectedStudies() != null) {
        project.getExpectedStudies().clear();
        // project.setIndicators(null);
      }


    }


  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {


      if (project.getExpectedStudies() == null) {
        project.setExpectedStudies(new ArrayList<>());
      }
      this.expectedStudiesPreviousData(project.getExpectedStudies());
      this.expectedStudiesNewData();
      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_RELATION);
      relationsName.add(APConstants.PROJECT_INFO_RELATION);


      Project projectDB = projectManager.getProjectById(projectID);
      projectDB.setActiveSince(new Date());
      projectDB.setModifiedBy(this.getCurrentUser());
      projectManager.saveProject(projectDB, this.getActionName(), relationsName, this.getActualPhase());
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists()) {
        path.toFile().delete();
      }

      if (this.getUrl() == null || this.getUrl().isEmpty()) {
        Collection<String> messages = this.getActionMessages();
        if (this.getInvalidFields() == null) {
          this.setInvalidFields(new HashMap<>());
        }
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


  public void setMyProjects(List<Project> myProjects) {
    this.myProjects = myProjects;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setScopes(Map<Integer, String> scopes) {
    this.scopes = scopes;
  }


  public void setSubIdos(Map<Long, String> subIdos) {
    this.subIdos = subIdos;
  }


  public void setTargets(Map<Long, String> targets) {
    this.targets = targets;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  public void setTypes(Map<Integer, String> types) {
    this.types = types;
  }


  @Override
  public void validate() {
    if (save) {
      //
      if (project.getExpectedStudies() != null) {
        for (ProjectExpectedStudy projectExpectedStudy : project.getExpectedStudies()) {
          if (projectExpectedStudy.getSrfSloIndicator() != null
            && projectExpectedStudy.getSrfSloIndicator().getId().longValue() == -1) {
            projectExpectedStudy.setSrfSloIndicator(null);
          }
          if (projectExpectedStudy.getSrfSubIdo() != null
            && projectExpectedStudy.getSrfSubIdo().getId().longValue() == -1) {
            projectExpectedStudy.setSrfSubIdo(null);
          }
        }
      }
      projectExpectedStudiesValidator.validate(this, project, true);

    }
  }

}
