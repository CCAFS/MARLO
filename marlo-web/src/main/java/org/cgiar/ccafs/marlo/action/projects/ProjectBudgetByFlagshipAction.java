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
import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBudgetsFlagshipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectBudgetsFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectBudgetByFlagshipAction extends BaseAction {

  private static final long serialVersionUID = 8935913606376386782L;

  private CrpProgramManager crpProgramManager;

  private BudgetTypeManager budgetTypeManager;

  private ProjectManager projectManager;

  private ProjectBudgetsFlagshipManager projectBudgetsFlagshipManager;

  private AuditLogManager auditLogManager;

  private CrpManager crpManager;

  private long projectID;

  private Crp loggedCrp;

  private Project project;

  private String transaction;

  private List<BudgetType> budgetTypesList;

  @Inject
  public ProjectBudgetByFlagshipAction(APConfig config, CrpProgramManager crpProgramManager,
    BudgetTypeManager budgetTypeManager, ProjectManager projectManager,
    ProjectBudgetsFlagshipManager projectBudgetsFlagshipManager, AuditLogManager auditLogManager,
    CrpManager crpManager) {
    super(config);
    this.crpProgramManager = crpProgramManager;
    this.budgetTypeManager = budgetTypeManager;
    this.projectManager = projectManager;
    this.projectBudgetsFlagshipManager = projectBudgetsFlagshipManager;
    this.auditLogManager = auditLogManager;
    this.crpManager = crpManager;
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
    String composedClassName = project.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription() + "_"
      + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public List<BudgetType> getBudgetTypesList() {
    return budgetTypesList;
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


  public String getTransaction() {
    return transaction;
  }

  @Override
  public void prepare() throws Exception {
    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    // Budget Types list
    budgetTypesList = budgetTypeManager.findAll();

    // Transaction Audilog
    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      Project history = (Project) auditLogManager.getHistory(transaction);

      if (history != null) {
        project = history;
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
        project.getProjectInfo()
          .setProjectEditLeader(projectDb.getProjecInfoPhase(this.getActualPhase()).isProjectEditLeader());
        reader.close();
        this.setDraft(true);

      } else {

        this.setDraft(false);
        project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
        // Setup the Project Budgets Flaship list
        project.setBudgetsFlagship(project.getProjectBudgetsFlagships().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(this.getActualPhase())).collect(Collectors.toList()));

        project.getBudgetsFlagship().removeIf(Objects::isNull);

        Project projectBD = projectManager.getProjectById(projectID);
        project.setProjectInfo(projectBD.getProjecInfoPhase(this.getActualPhase()));

        // List the Project flagships that contribute it
        List<CrpProgram> flagships = new ArrayList<>();
        List<ProjectFocus> projectFocuses = new ArrayList<>(projectBD.getProjectFocuses().stream()
          .filter(pf -> pf.isActive() && pf.getPhase().equals(this.getActualPhase())
            && pf.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
          .collect(Collectors.toList()));
        for (ProjectFocus projectFocus : projectFocuses) {
          flagships.add(projectFocus.getCrpProgram());
        }
        project.setFlagships(flagships);
      }

      // Check Null Crp program Values inside Project Budget
      if (project.getBudgetsFlagship() != null) {
        for (ProjectBudgetsFlagship projectBudgetsFlagship : project.getBudgetsFlagship()) {

          // This additional null check is required for auto save file after a CoA has been deleted from the project
          if (projectBudgetsFlagship.getCrpProgram() != null) {
            projectBudgetsFlagship
              .setCrpProgram(crpProgramManager.getCrpProgramById(projectBudgetsFlagship.getCrpProgram().getId()));
          }

        }
      }

    }

    // Base Permission
    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_BUDGET_FLAGSHIP_BASE_PERMISSION, params));

    if (this.isHttpPost()) {
      if (project.getBudgetsFlagship() != null) {
        project.getBudgetsFlagship().clear();
      }
    }


  }

  /**
   * Roun up the value that the user put in the value field
   * 
   * @param value
   * @param places
   * @return round number value
   */
  public double round(double value, int places) {
    if (places < 0) {
      throw new IllegalArgumentException();
    }

    BigDecimal bd = new BigDecimal(value);
    bd = bd.setScale(places, RoundingMode.HALF_UP);
    return bd.doubleValue();
  }

  public void setBudgetTypesList(List<BudgetType> budgetTypesList) {
    this.budgetTypesList = budgetTypesList;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  // private ProjectBudgetsCoAValidator validator;

  public void setProject(Project project) {
    this.project = project;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


}
