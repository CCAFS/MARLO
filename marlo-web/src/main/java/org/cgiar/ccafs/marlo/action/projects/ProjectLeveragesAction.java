/*****************************************************************
 * This file is part of CCAFS Planning and Reporting Platform.
 * CCAFS P&R is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * at your option) any later version.
 * CCAFS P&R is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with CCAFS P&R. If not, see <http://www.gnu.org/licenses/>.
 *****************************************************************/
package org.cgiar.ccafs.marlo.action.projects;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.IpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLeverageManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.IpProgram;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectLeverage;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.utils.HistoryComparator;
import org.cgiar.ccafs.marlo.utils.HistoryDifference;
import org.cgiar.ccafs.marlo.validation.projects.ProjectLeverageValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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
 * @author Christian David Garcia Oviedo- CIAT/CCAFS
 */
public class ProjectLeveragesAction extends BaseAction {


  private static final long serialVersionUID = -3179251766947184219L;


  // Manager
  private ProjectManager projectManager;
  private InstitutionManager institutionManager;
  private IpProgramManager ipProgrammManager;
  private ProjectLeverageManager projectLeverageManager;
  private GlobalUnitManager crpManager;
  private AuditLogManager auditLogManager;
  private CrpProgramManager crpProgramManager;

  // Variables
  private long projectID;
  private Project project;
  private Map<String, String> allInstitutions;
  private List<IpProgram> flagshipsPhaseOne;
  private List<CrpProgram> flagshipsPhaseTwo;
  private ProjectLeverageValidator projectLeverageValidator;
  private HistoryComparator historyComparator;
  private GlobalUnit loggedCrp;
  private String transaction;


  @Inject
  public ProjectLeveragesAction(APConfig config, ProjectManager projectManager, InstitutionManager institutionManager,
    IpProgramManager ipProgrammManager, AuditLogManager auditLogManager, GlobalUnitManager crpManager,
    ProjectLeverageManager projectLeverageManager, ProjectLeverageValidator projectLeverageValidator,
    HistoryComparator historyComparator, CrpProgramManager crpProgramManager) {
    super(config);
    this.projectManager = projectManager;
    this.institutionManager = institutionManager;
    this.ipProgrammManager = ipProgrammManager;
    this.projectLeverageManager = projectLeverageManager;
    this.crpManager = crpManager;
    this.auditLogManager = auditLogManager;
    this.historyComparator = historyComparator;
    this.projectLeverageValidator = projectLeverageValidator;
    this.crpProgramManager = crpProgramManager;
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

  public Map<String, String> getAllInstitutions() {
    return allInstitutions;
  }


  private Path getAutoSaveFilePath() {
    String composedClassName = project.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public List<IpProgram> getFlagshipsPhaseOne() {
    return flagshipsPhaseOne;
  }

  public List<CrpProgram> getFlagshipsPhaseTwo() {
    return flagshipsPhaseTwo;
  }


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public Project getProject() {
    return project;
  }

  public long getProjectID() {
    return projectID;
  }

  public ProjectManager getProjectManager() {
    return projectManager;
  }


  public String getProjectRequest() {
    return APConstants.PROJECT_REQUEST_ID;
  }

  public String getTransaction() {
    return transaction;
  }

  public void leveragesNewData(List<ProjectLeverage> projectLeverages) {

    for (ProjectLeverage projectLeverage : projectLeverages) {
      if (projectLeverage != null) {
        if (projectLeverage.getId() == null || projectLeverage.getId() == -1) {
          projectLeverage.setModificationJustification(this.getJustification());
          projectLeverage.setYear(this.getCurrentCycleYear());
          projectLeverage.setProject(project);
          projectLeverage.setPhase(this.getActualPhase());

        } else {
          ProjectLeverage projectLeverageDB = projectLeverageManager.getProjectLeverageById(projectLeverage.getId());

          projectLeverage.setModificationJustification(this.getJustification());
          projectLeverage.setYear(projectLeverageDB.getYear());
          projectLeverage.setProject(project);
          projectLeverage.setPhase(this.getActualPhase());

        }
      }
      if (projectLeverage.getInstitution().getId().intValue() == -1) {
        projectLeverage.setInstitution(null);
      }
      if (projectLeverage.getIpProgram() == null || projectLeverage.getIpProgram().getId().intValue() == -1) {
        projectLeverage.setIpProgram(null);
      }
      if (projectLeverage.getCrpProgram() == null || projectLeverage.getCrpProgram().getId().intValue() == -1) {
        projectLeverage.setCrpProgram(null);
      }
      projectLeverageManager.saveProjectLeverage(projectLeverage);
      project.getProjectLeverages().add(projectLeverage);
    }

  }

  public void leveragesPreviousData(List<ProjectLeverage> projectLeverages, boolean activitiesOpen) {

    List<ProjectLeverage> projectLeveragesPrew;
    Project projectBD = projectManager.getProjectById(projectID);


    projectLeveragesPrew =
      projectBD.getProjectLeverages().stream().filter(a -> a.isActive() && a.getYear() == this.getCurrentCycleYear()
        && a.getPhase() != null && a.getPhase().equals(this.getActualPhase())).collect(Collectors.toList());


    for (ProjectLeverage projectLeverage : projectLeveragesPrew) {
      if (!projectLeverages.contains(projectLeverage)) {
        projectLeverageManager.deleteProjectLeverage(projectLeverage.getId());
      }
    }

  }


  @Override
  public String next() {
    String result = this.save();
    if (result.equals(BaseAction.SUCCESS)) {
      return BaseAction.NEXT;
    } else {
      return result;
    }
  }


  @Override
  public void prepare() throws Exception {
    super.prepare();

    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());


    projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);

      if (history != null) {
        project = history;

        List<HistoryDifference> differences = new ArrayList<>();
        Map<String, String> specialList = new HashMap<>();
        int i = 0;
        project.setLeverages(project.getProjectLeverages().stream()
          .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().equals(this.getActualPhase()))
          .collect(Collectors.toList()));
        for (ProjectLeverage leverage : project.getLeverages()) {
          int[] index = new int[1];
          index[0] = i;
          differences.addAll(historyComparator.getDifferencesList(leverage, transaction, specialList,
            "project.leverages[" + i + "]", "project", 1));
          i++;
        }

        this.setDifferences(differences);

        // load crpProgram relations
        if (project.getProjectLeverages() != null && !project.getProjectLeverages().isEmpty()) {
          for (ProjectLeverage projectLeverage : project.getProjectLeverages()) {
            if (projectLeverage.getCrpProgram() != null && projectLeverage.getCrpProgram().getId() != null) {
              projectLeverage
                .setCrpProgram(crpProgramManager.getCrpProgramById(projectLeverage.getCrpProgram().getId()));
            }
          }
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
        reader.close();


        AutoSaveReader autoSaveReader = new AutoSaveReader();

        project = (Project) autoSaveReader.readFromJson(jReader);

        Project projectDb = projectManager.getProjectById(project.getId());
        project.setProjectInfo(projectDb.getProjecInfoPhase(this.getActualPhase()));

        if (project.getLeveragesClosed() == null) {

          project.setLeveragesClosed(new ArrayList<ProjectLeverage>());
        } else {
          project.getLeveragesClosed().sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
        }

        if (project.getLeverages() == null) {

          project.setLeverages(new ArrayList<ProjectLeverage>());
        }
        this.setDraft(true);
      } else {
        project.setLeverages(
          project.getProjectLeverages().stream().filter(c -> c.isActive() && c.getYear() == this.getCurrentCycleYear()
            && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
        project.setLeveragesClosed(
          project.getProjectLeverages().stream().filter(c -> c.isActive() && c.getYear() != this.getCurrentCycleYear()
            && c.getPhase() != null && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));
        project.getLeverages().sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
        project.getLeveragesClosed().sort((p1, p2) -> p1.getId().compareTo(p2.getId()));
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        this.setDraft(false);
      }
    }


    // Getting the list of all institutions
    this.allInstitutions = new HashMap<>();
    List<Institution> allInstitutions =
      institutionManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());
    for (Institution institution : allInstitutions) {
      this.allInstitutions.put(String.valueOf(institution.getId()), institution.getComposedName());
    }
    this.flagshipsPhaseOne = new ArrayList<>();
    this.flagshipsPhaseTwo = new ArrayList<>();
    if (this.isPhaseOne()) {
      // Getting the information of the Flagships program for the View
      List<IpProgram> ipProgramFlagships = ipProgrammManager.findAll().stream()
        .filter(c -> c.getIpProgramType().getId().intValue() == 4).collect(Collectors.toList());
      for (IpProgram ipProgram : ipProgramFlagships) {
        flagshipsPhaseOne.add(ipProgram);
      }
    } else {
      List<CrpProgram> crpProgramFlagships = this.getLoggedCrp().getCrpPrograms().stream()
        .filter(c -> c.isActive() && c.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .sorted((p1, p2) -> p1.getAcronym().compareTo(p2.getAcronym())).collect(Collectors.toList());
      for (CrpProgram crpProgram : crpProgramFlagships) {
        flagshipsPhaseTwo.add(crpProgram);
      }
    }

    if (this.isHttpPost()) {

      if (project.getLeverages() != null) {
        project.getLeverages().clear();
      }


    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_LEVERAGES_BASE_PERMISSION, params));


  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {

      this.leveragesPreviousData(project.getLeverages(), true);
      this.leveragesNewData(project.getLeverages());
      List<String> relationsName = new ArrayList<>();
      relationsName.add(APConstants.PROJECT_LEVERAGES_RELATION);
      relationsName.add(APConstants.PROJECT_INFO_RELATION);
      project = projectManager.getProjectById(projectID);
      /**
       * The following is required because we need to update something on the @Project if we want a row
       * created in the auditlog table.
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
    }
    return NOT_AUTHORIZED;
  }


  public void setAllInstitutions(Map<String, String> allInstitutions) {
    this.allInstitutions = allInstitutions;
  }

  public void setFlagshipsPhaseOne(List<IpProgram> flagshipsPhaseOne) {
    this.flagshipsPhaseOne = flagshipsPhaseOne;
  }

  public void setFlagshipsPhaseTwo(List<CrpProgram> flagshipsPhaseTwo) {
    this.flagshipsPhaseTwo = flagshipsPhaseTwo;
  }


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setProjectManager(ProjectManager projectManager) {
    this.projectManager = projectManager;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {

      projectLeverageValidator.validate(this, project, true);
    }
  }
}