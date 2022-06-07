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
import org.cgiar.ccafs.marlo.data.manager.BiParametersManager;
import org.cgiar.ccafs.marlo.data.manager.BiReportsManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.SectionStatusManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.BiParameters;
import org.cgiar.ccafs.marlo.data.model.BiReports;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.List;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FeedbackStatusAction extends BaseAction {


  private static final long serialVersionUID = -793652591843623397L;


  private static final Logger LOG = LoggerFactory.getLogger(FeedbackStatusAction.class);

  // Managers
  private ProjectManager projectManager;
  private String transaction;

  // Front-end
  private long projectID;
  private GlobalUnit loggedCrp;
  private Project project;
  private Project projectDB;

  private BiReportsManager biReportsManager;
  private BiParametersManager biParametersManager;

  // Front-end
  private List<BiReports> biReports;
  private List<BiParameters> biParameters;

  @Inject
  public FeedbackStatusAction(APConfig config, ProjectManager projectManager, UserManager userManager,
    SectionStatusManager sectionStatusManager, AuditLogManager auditLogManager, BiReportsManager biReportsManager,
    BiParametersManager biParametersManager) {
    super(config);
    this.projectManager = projectManager;
    this.projectManager = projectManager;
    this.biReportsManager = biReportsManager;
    this.biParametersManager = biParametersManager;
  }

  public List<BiParameters> getBiParameters() {
    return biParameters;
  }

  public List<BiReports> getBiReports() {
    return biReports;
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


  @Override
  public void prepare() throws Exception {

    biReports = biReportsManager.findAll();
    biParameters = biParametersManager.findAll();

    // Get current CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
      // safeguardID =
      // Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.SAFEGUARD_REQUEST_ID)));
    } catch (Exception e) {
      LOG.error("unable to parse projectID", e);
      /**
       * Original code swallows the exception and didn't even log it. Now we at least log it,
       * but we need to revisit to see if we should continue processing or re-throw the exception.
       */
    }

    // We check that you have a TRANSACTION_ID to know if it is history version

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {


    } else {
      // get project info for DB
      project = projectManager.getProjectById(projectID);
    }

    if (project != null) {


      // DB version
      this.setDraft(false);

      // Load the DB information and adjust it to the structures with which the front end
      project.setProjectInfo(project.getProjecInfoPhase(this.getActualPhase()));
      if (project.getProjectInfo() == null) {
        project.setProjectInfo(new ProjectInfo());
      }
    }

    projectDB = projectManager.getProjectById(projectID);

    // The base permission is established for the current section

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_DESCRIPTION_BASE_PERMISSION, params));

    /*
     * If it is post, the lists are cleaned, this is done because there is a bug of struts, if this is not done it does
     * not delete the items deleted by the user
     */
    if (this.isHttpPost()) {
    }

  }

  public void setBiParameters(List<BiParameters> biParameters) {
    this.biParameters = biParameters;
  }


  public void setBiReports(List<BiReports> biReports) {
    this.biReports = biReports;
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

  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }

  @Override
  public void validate() {
  }

}