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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectLeverageManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectLeverage;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Christian David Garcia Oviedo- CIAT/CCAFS
 */
public class ProjectLeveragesAction extends BaseAction {

  private static final long serialVersionUID = -3179251766947184219L;

  // Manager
  private ProjectManager projectManager;
  private InstitutionManager institutionManager;
  private CrpProgramManager crpProgrammManager;
  private ProjectLeverageManager projectLeverageManager;
  private long projectID;
  private Project project;
  private Map<String, String> allInstitutions;
  private Map<String, String> flagships;
  private List<ProjectLeverage> leveragesPreview;
  private CrpManager crpManager;


  private Crp loggedCrp;


  private String transaction;

  private AuditLogManager auditLogManager;

  @Inject
  public ProjectLeveragesAction(APConfig config, ProjectManager projectManager, InstitutionManager institutionManager,
    CrpProgramManager crpProgrammManager, ProjectLeverageManager projectLeverageManager) {
    super(config);
    this.projectManager = projectManager;
    this.institutionManager = institutionManager;
    this.crpProgrammManager = crpProgrammManager;
    this.projectLeverageManager = projectLeverageManager;

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
    String autoSaveFile = project.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public Map<String, String> getFlagships() {
    return flagships;
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

  public ProjectManager getProjectManager() {
    return projectManager;
  }

  public String getProjectRequest() {
    return APConstants.PROJECT_REQUEST_ID;
  }


  public String getTransaction() {
    return transaction;
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

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());
    projectID = Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    // project = projectManager.getPrRojectById(projectID);

    // Getting the list of all institutions
    this.allInstitutions = new HashMap<>();
    List<Institution> allInstitutions = institutionManager.findAll().stream()
      .filter(c -> c.isActive() && c.getHeadquarter() == null).collect(Collectors.toList());
    for (Institution institution : allInstitutions) {
      this.allInstitutions.put(String.valueOf(institution.getId()), institution.getComposedName());
    }
    this.flagships = new HashMap<>();
    // Getting the information of the Flagships program for the View
    List<CrpProgram> ipProgramFlagships =
      crpProgrammManager.findAll().stream().filter(c -> c.getProgramType() == 1).collect(Collectors.toList());
    for (CrpProgram ipProgram : ipProgramFlagships) {
      this.flagships.put(String.valueOf(ipProgram.getId()), ipProgram.getComposedName());
    }
    project.setLeverages(project.getProjectLeverages().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    if (project.getLeverages() != null) {
      leveragesPreview = project.getProjectLeverages().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      if (this.getRequest().getMethod().equalsIgnoreCase("post")) {
        // Clear out the list if it has some element
        if (project.getLeverages() != null) {
          project.getLeverages().clear();
        }


      }


    }

  }


  @Override
  public String save() {
    /*
     * if (this.hasProjectPermission("update", project.getId())) {
     * if (leveragesPreview != null) {
     * for (ProjectLeverage projectLeverage : leveragesPreview) {
     * if (!project.getLeverages().contains(projectLeverage)) {
     * projectLeverageManager.deleteProjectLeverage(projectLeverage.getId(), this.getCurrentUser(),
     * this.getJustification());
     * }
     * }
     * }
     * for (ProjectLeverage projectLeverage : project.getLeverages()) {
     * // projectLeverage.setInstitution(projectLeverage.getMyInstitution().getId());
     * projectLeverage.setProjectId(projectID);
     * projectLeverageManager.saveProjectLeverage(projectID, projectLeverage, this.getCurrentUser(),
     * this.getJustification());
     * }
     * // Get the validation messages and append them to the save message
     * Collection<String> messages = this.getActionMessages();
     * if (!messages.isEmpty()) {
     * String validationMessage = messages.iterator().next();
     * this.setActionMessages(null);
     * this.addActionWarning(this.getText("saving.saved") + validationMessage);
     * } else {
     * this.addActionMessage(this.getText("saving.saved"));
     * }
     * return SUCCESS;
     * }
     * return NOT_AUTHORIZED;
     */return NOT_AUTHORIZED;
  }


  public void setAllInstitutions(Map<String, String> allInstitutions) {
    this.allInstitutions = allInstitutions;
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


  public void setProjectManager(ProjectManager projectManager) {
    this.projectManager = projectManager;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
    if (save) {


    }
  }
}