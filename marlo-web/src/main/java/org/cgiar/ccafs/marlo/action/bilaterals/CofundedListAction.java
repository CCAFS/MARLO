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

package org.cgiar.ccafs.marlo.action.bilaterals;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectBilateralCofinancingManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.ProjectBilateralCofinancing;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CofundedListAction extends BaseAction {


  private static final long serialVersionUID = -8858893084495492581L;


  private Crp loggedCrp;

  private List<ProjectBilateralCofinancing> myProjects;


  private List<ProjectBilateralCofinancing> allProjects;

  private ProjectBilateralCofinancingManager projectBilateralCofinancingManager;


  private CrpManager crpManager;
  private ProjectManager projectManager;

  private long projectID;


  private String justification;


  @Inject
  public CofundedListAction(APConfig config, ProjectBilateralCofinancingManager projectBilateralCofinancingManager,
    CrpManager crpManager, ProjectManager projectManager) {
    super(config);
    this.projectBilateralCofinancingManager = projectBilateralCofinancingManager;
    this.crpManager = crpManager;
    this.projectManager = projectManager;
  }

  @Override
  public String add() {
    ProjectBilateralCofinancing project = new ProjectBilateralCofinancing();
    project.setCreatedBy(this.getCurrentUser());
    project.setModifiedBy(this.getCurrentUser());
    project.setModificationJustification("New expected project bilateral cofunded created");
    project.setActive(true);
    project.setActiveSince(new Date());
    project.setCrp(loggedCrp);

    projectID = projectBilateralCofinancingManager.saveProjectBilateralCofinancing(project);

    if (projectID > 0) {
      return SUCCESS;
    }

    return INPUT;
  }


  @Override
  public String delete() {
    ProjectBilateralCofinancing project =
      projectBilateralCofinancingManager.getProjectBilateralCofinancingById(projectID);

    project.setModifiedBy(this.getCurrentUser());
    project.setModificationJustification(justification);

    projectBilateralCofinancingManager.saveProjectBilateralCofinancing(project);


    if (projectBilateralCofinancingManager.deleteProjectBilateralCofinancing(project.getId())) {
      this.addActionMessage(
        this.getText("deleting.success", new String[] {this.getText("projectCofunded").toLowerCase()}));
    } else {
      this
        .addActionError(this.getText("deleting.problem", new String[] {this.getText("projectCofunded").toLowerCase()}));
    }

    return SUCCESS;
  }

  public List<ProjectBilateralCofinancing> getAllProjects() {
    return allProjects;
  }

  @Override
  public String getJustification() {
    return justification;
  }

  public Crp getLoggedCrp() {
    return loggedCrp;
  }

  public List<ProjectBilateralCofinancing> getMyProjects() {
    return myProjects;
  }

  public long getProjectID() {
    return projectID;
  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    if (projectBilateralCofinancingManager.findAll() != null) {

      if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin()) {
        myProjects =
          loggedCrp.getProjectBilateralCofinancings().stream().filter(p -> p.isActive()).collect(Collectors.toList());
      } else {
        allProjects =
          loggedCrp.getProjectBilateralCofinancings().stream().filter(p -> p.isActive()).collect(Collectors.toList());
        // myProjects = projectManager.getUserProjects(this.getCurrentUser().getId(), loggedCrp.getAcronym());
        // Collections.sort(myProjects, (p1, p2) -> p1.getId().compareTo(p2.getId()));
        myProjects = allProjects;
        // allProjects.removeAll(myProjects);
      }
    }

  }

  public void setAllProjects(List<ProjectBilateralCofinancing> allProjects) {
    this.allProjects = allProjects;
  }


  @Override
  public void setJustification(String justification) {
    this.justification = justification;
  }

  public void setLoggedCrp(Crp loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMyProjects(List<ProjectBilateralCofinancing> myProjects) {
    this.myProjects = myProjects;
  }

  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }

}
