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
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.GlobalScopeEnum;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.TypeExpectedStudiesEnum;
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


  private ProjectActivitiesValidator activitiesValidator;


  private ProjectExpectedStudyManager projectExpectedStudyManager;


  private AuditLogManager auditLogManager;

  private CrpManager crpManager;


  private HistoryComparator historyComparator;


  private Crp loggedCrp;


  private Project project;

  private long projectID;

  private ProjectManager projectManager;

  private SrfSloIndicatorManager srfSloIndicatorManager;
  private SrfSubIdoManager srfSubIdoManager;

  private Map<Long, String> subIdos;
  private Map<Long, String> targets;
  private Map<Integer, String> types;
  private Map<Integer, String> scopes;


  private String transaction;


  @Inject
  public ProjectExpectedStudiesAction(APConfig config, ProjectManager projectManager, CrpManager crpManager,
    ProjectExpectedStudyManager projectExpectedStudyManager, SrfSloIndicatorManager srfSloIndicatorManager,
    SrfSubIdoManager srfSubIdoManager, AuditLogManager auditLogManager, ProjectActivitiesValidator activitiesValidator,
    HistoryComparator historyComparator) {
    super(config);
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.auditLogManager = auditLogManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.srfSloIndicatorManager = srfSloIndicatorManager;

    this.historyComparator = historyComparator;
    this.activitiesValidator = activitiesValidator;

  }


  public void expectedStudiesNewData(List<ProjectExpectedStudy> expectedStudies) {

    for (ProjectExpectedStudy projectExpectedStudy : expectedStudies) {
      if (projectExpectedStudy != null) {
        if (projectExpectedStudy.getId() == null || projectExpectedStudy.getId() == -1) {

          ProjectExpectedStudy projectExpectedStudyNew = new ProjectExpectedStudy();
          projectExpectedStudyNew.setActive(true);
          projectExpectedStudyNew.setCreatedBy(this.getCurrentUser());
          projectExpectedStudyNew.setModifiedBy(this.getCurrentUser());
          projectExpectedStudyNew.setModificationJustification("");
          projectExpectedStudyNew.setActiveSince(new Date());
          Project project = projectManager.getProjectById(this.project.getId());
          projectExpectedStudyNew.setProject(project);
          projectExpectedStudyNew.setComments(projectExpectedStudy.getComments());
          projectExpectedStudyNew.setComposedId(projectExpectedStudy.getComposedId());
          projectExpectedStudyNew.setOtherType(projectExpectedStudy.getOtherType());
          projectExpectedStudyNew.setPhase(this.getActualPhase());
          projectExpectedStudyNew.setScope(projectExpectedStudy.getScope());
          projectExpectedStudyNew.setSrfSloIndicator(projectExpectedStudy.getSrfSloIndicator());
          projectExpectedStudyNew.setSrfSubIdo(projectExpectedStudy.getSrfSubIdo());
          projectExpectedStudyNew.setTopicStudy(projectExpectedStudy.getTopicStudy());
          projectExpectedStudyNew.setType(projectExpectedStudy.getType());
          projectExpectedStudyNew = projectExpectedStudyManager.saveProjectExpectedStudy(projectExpectedStudyNew);


        } else {

          ProjectExpectedStudy projectExpectedUpdate =
            projectExpectedStudyManager.getProjectExpectedStudyById(projectExpectedStudy.getId());
          projectExpectedUpdate.setActive(true);
          projectExpectedUpdate.setCreatedBy(this.getCurrentUser());
          projectExpectedUpdate.setModifiedBy(this.getCurrentUser());
          projectExpectedUpdate.setModificationJustification("");
          projectExpectedUpdate.setActiveSince(new Date());
          projectExpectedUpdate.setPhase(this.getActualPhase());
          projectExpectedUpdate.setComments(projectExpectedStudy.getComments());
          projectExpectedUpdate.setComposedId(projectExpectedStudy.getComposedId());
          projectExpectedUpdate.setOtherType(projectExpectedStudy.getOtherType());
          projectExpectedUpdate.setPhase(this.getActualPhase());
          projectExpectedUpdate.setScope(projectExpectedStudy.getScope());
          projectExpectedUpdate.setSrfSloIndicator(projectExpectedStudy.getSrfSloIndicator());
          projectExpectedUpdate.setSrfSubIdo(projectExpectedStudy.getSrfSubIdo());
          projectExpectedUpdate.setTopicStudy(projectExpectedStudy.getTopicStudy());
          projectExpectedUpdate.setType(projectExpectedStudy.getType());

          projectExpectedUpdate = projectExpectedStudyManager.saveProjectExpectedStudy(projectExpectedUpdate);


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


  public Crp getLoggedCrp() {
    return loggedCrp;
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
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

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
        project.setProjectLocations(projectDb.getProjectLocations());


        this.setDraft(true);
      } else {
        this.setDraft(false);
        project.setExpectedStudies(project.getProjectExpectedStudies().stream()
          .filter(a -> a.isActive() && a.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
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
      String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
      this.setBasePermission(this.getText(Permission.PROJECT_EXPECTED_STUDIES_BASE_PERMISSION, params));
    }
    if (this.isHttpPost()) {
      if (project.getProjectActivities() != null) {
        project.getProjectActivities().clear();
      }


    }


  }

  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      Project projectDB = projectManager.getProjectById(project.getId());
      project.setActive(true);
      project.setCreatedBy(projectDB.getCreatedBy());
      project.setActiveSince(projectDB.getActiveSince());
      if (project.getExpectedStudies() == null) {
        project.setExpectedStudies(new ArrayList<>());
      }
      this.expectedStudiesPreviousData(project.getExpectedStudies());
      this.expectedStudiesNewData(project.getExpectedStudies());
      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_EXPECTED_STUDIES_RELATION);
      relationsName.add(APConstants.PROJECT_INFO_RELATION);
      project = projectManager.getProjectById(projectID);
      project.setActiveSince(new Date());
      project.setModifiedBy(this.getCurrentUser());
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


  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
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
      activitiesValidator.validate(this, project, true);
    }
  }

}
