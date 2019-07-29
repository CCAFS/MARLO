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

package org.cgiar.ccafs.marlo.action.json.funding;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.FundingSourceManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.security.Permission;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class MapProjectListAction extends BaseAction {


  private static final long serialVersionUID = -5595055892247130791L;


  private InstitutionManager institutionManager;
  private ProjectManager projectManager;
  private GlobalUnitManager globalUnitManager;
  private FundingSourceManager fundingSourceManager;
  private PhaseManager phaseManager;

  private List<Map<String, Object>> projects;

  private List<Project> userProjects;

  private Long institutionId;
  private GlobalUnit loggedCrp;
  private Long fundingId;
  private Long phaseId;
  private boolean permission;
  private boolean genderPermission;

  @Inject
  public MapProjectListAction(APConfig config, InstitutionManager institutionManager, ProjectManager projectManager,
    GlobalUnitManager globalUnitManager, FundingSourceManager fundingSourceManager, PhaseManager phaseManager) {
    super(config);
    this.institutionManager = institutionManager;
    this.projectManager = projectManager;
    this.globalUnitManager = globalUnitManager;
    this.fundingSourceManager = fundingSourceManager;
    this.phaseManager = phaseManager;
  }

  @Override
  public String execute() throws Exception {

    // Load the CRP
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = globalUnitManager.getGlobalUnitById(loggedCrp.getId());


    projects = new ArrayList<Map<String, Object>>();


    Map<String, Object> userProject;

    // Load the Institution, phase and the funding source
    Institution institution = institutionManager.getInstitutionById(institutionId);
    FundingSource fundingSource = fundingSourceManager.getFundingSourceById(fundingId);
    Phase phase = phaseManager.getPhaseById(phaseId);

    // indicator if the user is Admin or superAdmin
    boolean admin = false;

    if (institution != null) {
      // Ask if the institution is a PPA
      if (institution.isPPA(loggedCrp.getId(), phase)) {
        // Load the projects that the user can edit
        if (this.canAccessSuperAdmin() || this.isAdmin()) {
          admin = true;
          userProjects = new ArrayList<>();
          for (ProjectPhase projectPhase : phase.getProjectPhases()) {
            if (projectPhase.getProject().getProjecInfoPhase(phase) != null) {
              userProjects.add(projectPhase.getProject());
            }
          }
        } else {
          userProjects = projectManager.getUserProjects(this.getCurrentUser().getId(), loggedCrp.getAcronym()).stream()
            .filter(p -> p.isActive()).collect(Collectors.toList());
        }


        for (Project project : userProjects) {
          // Ask if the project contains information in this phase
          if (project.getProjecInfoPhase(phase) != null) {


            if (fundingSource != null) {
              if (!admin) {
                // Ask if the user have permissions to edit the budget into the projects
                permission = false;
                genderPermission = false;
                if (fundingSource.getFundingSourceInfo(phase) != null) {

                  if (fundingSource.getFundingSourceInfo().getBudgetType() != null) {

                    switch (fundingSource.getFundingSourceInfo().getBudgetType().getName()) {
                      case "W1/W2":
                        permission = this.hasPermissionNoBase(this
                          .generatePermission(Permission.PROJECT_FUNDING_W1_BASE_PERMISSION, loggedCrp.getAcronym()));
                        break;
                      default:
                        permission = this.hasPermissionNoBase(
                          this.generatePermission(Permission.PROJECT_FUNDING_W3_PROJECT_BASE_PERMISSION,
                            loggedCrp.getAcronym(), project.getId() + ""))
                          || this.hasPermissionNoBase(
                            this.generatePermission(Permission.PROJECT_FUNDING_W3_PROJECT_INSTITUTION_BASE_PERMISSION,
                              loggedCrp.getAcronym(), project.getId() + "", institution.getId() + ""));
                        break;
                    }
                  }
                  // Ask if the user have permissions to edit the gender %
                  genderPermission =
                    this.hasPermissionNoBase(this.generatePermission(Permission.PROJECT_GENDER_PROJECT_BASE_PERMISSION,
                      loggedCrp.getAcronym(), project.getId() + ""));
                } else {
                  permission = true;
                  genderPermission = true;
                }


                List<ProjectPartner> projectPartners = new ArrayList<>(project.getProjectPartners().stream()
                  .filter(pp -> pp.isActive() && pp.getPhase().getId().equals(phase.getId()))
                  .collect(Collectors.toList()));
                // Check who projects contains the PPA institution as partner
                for (ProjectPartner projectPartner : projectPartners) {
                  if (projectPartner.getInstitution().getId().equals(institution.getId())) {
                    if (permission) {
                      userProject = new HashMap<String, Object>();
                      userProject.put("id", project.getId());
                      userProject.put("description", project.getComposedName());
                      userProject.put("gender", genderPermission);
                      projects.add(userProject);
                    }
                  }
                }
              }
            }
          }
        }
      }
    }


    return SUCCESS;
  }


  public List<Map<String, Object>> getProjects() {
    return projects;
  }


  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    institutionId =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.INSTITUTION_REQUEST_ID).getMultipleValues()[0]));
    fundingId =
      Long.parseLong(StringUtils.trim(parameters.get(APConstants.FUNDING_SOURCE_REQUEST_ID).getMultipleValues()[0]));
    phaseId = Long.parseLong(StringUtils.trim(parameters.get(APConstants.PHASE_ID).getMultipleValues()[0]));
  }


  public void setProjects(List<Map<String, Object>> projects) {
    this.projects = projects;
  }


}
