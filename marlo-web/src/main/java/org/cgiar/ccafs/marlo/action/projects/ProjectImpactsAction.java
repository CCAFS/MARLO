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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectImpactsManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectImpacts;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.projects.ProjectImpactsValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */
public class ProjectImpactsAction extends BaseAction {

  private static final long serialVersionUID = 1329042468240291639L;

  private static final Logger LOG = LoggerFactory.getLogger(ProjectImpactsAction.class);

  // Managers
  private GlobalUnitManager crpManager;
  private ProjectManager projectManager;
  private ProjectImpactsManager projectImpactsManager;
  private String transaction;
  private AuditLogManager auditLogManager;

  // Front-end
  private long projectID;
  private GlobalUnit loggedCrp;
  private Phase phase;
  private ProjectImpacts actualProjectImpact;
  private List<ProjectImpacts> historyProjectImpacts;
  private Project project;

  private ProjectImpactsValidator validator;

  @Inject
  public ProjectImpactsAction(APConfig config, GlobalUnitManager crpManager, ProjectManager projectManager,
    AuditLogManager auditLogManager, ProjectImpactsManager projectImpactsManager, ProjectImpactsValidator validator) {
    super(config);
    this.projectImpactsManager = projectImpactsManager;
    this.crpManager = crpManager;
    this.projectManager = projectManager;
    this.auditLogManager = auditLogManager;
    this.validator = validator;
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

  public ProjectImpacts getActualProjectImpact() {
    return actualProjectImpact;
  }

  private ProjectImpacts getActualProjectImpact(List<ProjectImpacts> projectImpacts, Project project, Phase phase) {
    ProjectImpacts newProjectImpact = new ProjectImpacts();;

    newProjectImpact.setProject(project);
    newProjectImpact.setYear(phase.getYear());;

    if (projectImpacts != null) {

      return projectImpacts.stream()
        .filter(c -> c.isActive() && c.getProject().getId().equals(project.getId()) && c.getYear() == phase.getYear())
        .findFirst().orElse(newProjectImpact);
    }

    return newProjectImpact;
  }


  /**
   * The name of the autosave file is constructed and the path is searched
   * 
   * @return Auto save file path
   */
  private Path getAutoSaveFilePath() {
    // get the class simple name
    String composedClassName = actualProjectImpact.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = actualProjectImpact.getId() + "_" + composedClassName + "_" + this.getActualPhase().getName()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public GlobalUnitManager getCrpManager() {
    return crpManager;
  }


  private List<ProjectImpacts> getHistoryProjectImpact(List<ProjectImpacts> projectImpacts,
    ProjectImpacts actualProjectImpact) {
    List<ProjectImpacts> historyProjectImpacts = new ArrayList();

    if (projectImpacts != null) {
      projectImpacts.remove(actualProjectImpact);
      historyProjectImpacts = projectImpacts;
    }

    return historyProjectImpacts;
  }


  public List<ProjectImpacts> getHistoryProjectImpacts() {
    return historyProjectImpacts;
  }


  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }


  public Phase getPhase() {
    return phase;
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
      ProjectImpacts history = (ProjectImpacts) auditLogManager.getHistory(transaction);

      if (history != null) {
        actualProjectImpact = history;
      } else {
        // not a valid transaction
        this.transaction = null;
        this.setTransaction("-1");
      }

    } else {
      // get project info for DB
      project = projectManager.getProjectById(projectID);
      List<ProjectImpacts> projectImpacts = projectImpactsManager.getProjectImpactsByProjectId(projectID);
      actualProjectImpact = this.getActualProjectImpact(projectImpacts, project, this.getActualPhase());
      historyProjectImpacts = this.getHistoryProjectImpact(projectImpacts, actualProjectImpact);
    }

    if (actualProjectImpact != null) {

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

        actualProjectImpact = (ProjectImpacts) autoSaveReader.readFromJson(jReader);

        this.setDraft(true);
      } else {
        this.setDraft(false);
      }
    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_COVID19_BASE_PERMISSION, params));

  }


  @Override
  public String save() {
    if (this.hasPermission("canEdit")) {
      projectImpactsManager.saveProjectImpacts(actualProjectImpact);
      return SUCCESS;
    } else {
      return NOT_AUTHORIZED;
    }
  }


  public void setActualProjectImpact(ProjectImpacts actualProjectImpact) {
    this.actualProjectImpact = actualProjectImpact;
  }

  public void setCrpManager(GlobalUnitManager crpManager) {
    this.crpManager = crpManager;
  }

  public void setHistoryProjectImpacts(List<ProjectImpacts> historyProjectImpacts) {
    this.historyProjectImpacts = historyProjectImpacts;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }


  public void setPhase(Phase phase) {
    this.phase = phase;
  }


  public void setProject(Project project) {
    this.project = project;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    // if is saving call the validator to check for the missing fields
    if (save) {
      validator.validate(this, actualProjectImpact, true);
    }
  }

}
