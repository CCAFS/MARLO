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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.security.APCustomRealm;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class ProjectBudgetByPartnersAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 7833194831832715444L;
  private InstitutionManager institutionManager;
  private ProjectManager projectManager;
  private CrpManager crpManager;
  private long projectID;
  private Crp loggedCrp;
  private Project project;

  // Model for the view
  private Map<String, String> w3bilateralBudgetTypes; // List of W3/Bilateral budget types (W3, Bilateral).
  private List<ProjectPartner> projectPPAPartners; // Is used to list all the PPA partners that belongs to the project.

  @Inject
  public ProjectBudgetByPartnersAction(APConfig config, InstitutionManager institutionManager,
    ProjectManager projectManager, CrpManager crpManager) {
    super(config);

    this.institutionManager = institutionManager;
    this.projectManager = projectManager;
    this.crpManager = crpManager;
  }


  @Override
  public String cancel() {
    return SUCCESS;
  }


  /**
   * This method clears the cache and re-load the user permissions in the next iteration.
   */
  public void clearPermissionsCache() {
    ((APCustomRealm) securityContext.getRealm())
      .clearCachedAuthorizationInfo(securityContext.getSubject().getPrincipals());
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


  public List<ProjectPartner> getProjectPPAPartners() {
    return projectPPAPartners;
  }


  public Map<String, String> getW3bilateralBudgetTypes() {
    return w3bilateralBudgetTypes;
  }


  public boolean isPPA(Institution institution) {
    if (institution == null) {
      return false;
    }

    if (institution.getId() != null) {
      institution = institutionManager.getInstitutionById(institution.getId());
      if (institution != null) {
        if (institution.getCrpPpaPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList())
          .size() > 0) {
          return true;
        }
      }

    }

    return false;
  }


  @Override
  public void prepare() throws Exception {
    projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_REQUEST_ID)));
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    w3bilateralBudgetTypes = new HashMap<>();
    w3bilateralBudgetTypes.put("w3", "W3");
    w3bilateralBudgetTypes.put("bilateral", "Bilateral");

    project = projectManager.getProjectById(projectID);

    if (project != null) {
      this.setDraft(false);
      project.setPartners(project.getProjectPartners().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

      for (ProjectPartner projectPartner : project.getPartners()) {
        projectPartner.setPartnerPersons(
          projectPartner.getProjectPartnerPersons().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      }

      this.projectPPAPartners = new ArrayList<ProjectPartner>();
      for (ProjectPartner pp : project.getPartners()) {
        if (this.isPPA(pp.getInstitution())) {
          this.projectPPAPartners.add(pp);
        }
      }
    }

    String params[] = {loggedCrp.getAcronym(), project.getId() + ""};
    this.setBasePermission(this.getText(Permission.PROJECT_PARTNER_BASE_PERMISSION, params));


    ProjectPartner leader = project.getLeader();
    if (leader != null) {
      // First we remove the element from the array.
      project.getPartners().remove(leader);
      // then we add it to the first position.
      project.getPartners().add(0, leader);
    }

    if (this.isHttpPost()) {
      project.getPartners().clear();
    }

  }


  @Override
  public String save() {
    return SUCCESS;
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


  public void setProjectPPAPartners(List<ProjectPartner> projectPPAPartners) {
    this.projectPPAPartners = projectPPAPartners;
  }


  public void setW3bilateralBudgetTypes(Map<String, String> w3bilateralBudgetTypes) {
    this.w3bilateralBudgetTypes = w3bilateralBudgetTypes;
  }


  @Override
  public void validate() {
    if (save) {
    }
  }
}
