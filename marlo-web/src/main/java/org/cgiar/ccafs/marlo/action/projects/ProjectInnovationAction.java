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
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.projects.ProjectInnovationValidator;

import java.nio.file.Path;
import java.nio.file.Paths;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class ProjectInnovationAction extends BaseAction {


  private static final long serialVersionUID = 2025842196563364380L;


  private GlobalUnitManager crpManager;


  private GlobalUnit loggedCrp;


  private ProjectInnovationValidator validator;


  private AuditLogManager auditLogManager;

  private ProjectInnovationManager projectInnovationManager;


  private ProjectInnovation innovation;


  private String transaction;


  private long innovationID;


  private long projectID;


  @Inject
  public ProjectInnovationAction(APConfig config, GlobalUnitManager crpManager, GlobalUnit loggedCrp,
    ProjectInnovationValidator validator, AuditLogManager auditLogManager,
    ProjectInnovationManager projectInnovationManager) {
    super(config);
    this.crpManager = crpManager;
    this.loggedCrp = loggedCrp;
    this.validator = validator;
    this.auditLogManager = auditLogManager;
    this.projectInnovationManager = projectInnovationManager;
  }

  private Path getAutoSaveFilePath() {
    // get the class simple name
    String composedClassName = innovation.getClass().getSimpleName();
    // get the action name and replace / for _
    String actionFile = this.getActionName().replace("/", "_");
    // concatane name and add the .json extension
    String autoSaveFile = innovation.getId() + "_" + composedClassName + "_" + this.getActualPhase().getDescription()
      + "_" + this.getActualPhase().getYear() + "_" + actionFile + ".json";
    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public ProjectInnovation getInnovation() {
    return innovation;
  }

  public long getInnovationID() {
    return innovationID;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public long getProjectID() {
    return projectID;
  }

  public String getTransaction() {
    return transaction;
  }

  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    innovationID =
      Integer.parseInt(StringUtils.trim(this.getRequest().getParameter(APConstants.INNOVATION_REQUEST_ID)));

    // History TODO
    this.innovation = projectInnovationManager.getProjectInnovationById(innovationID);


    String params[] = {loggedCrp.getAcronym(), innovation.getProject().getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_INNOVATIONS_BASE_PERMISSION, params));


  }

  public void setInnovation(ProjectInnovation innovation) {
    this.innovation = innovation;
  }

  public void setInnovationID(long innovationID) {
    this.innovationID = innovationID;
  }

  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

}
