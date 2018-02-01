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

package org.cgiar.ccafs.marlo.action.home;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DashboardAction extends BaseAction {


  private static final long serialVersionUID = 6686785556753962379L;


  private PhaseManager phaseManager;


  private List<Project> myProjects;


  private ProjectManager projectManager;


  // GlobalUnit Manager
  private GlobalUnitManager crpManager;

  private GlobalUnit loggedCrp;


  @Inject
  public DashboardAction(APConfig config, ProjectManager projectManager, GlobalUnitManager crpManager,
    PhaseManager phaseManager) {
    super(config);
    this.projectManager = projectManager;
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;
  }

  public GlobalUnit getLoggedCrp() {
    return loggedCrp;
  }

  public List<Project> getMyProjects() {
    return myProjects;
  }


  @Override
  public void prepare() throws Exception {
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());
    Phase phase =
      phaseManager.findCycle(this.getCurrentCycle(), this.getCurrentCycleYear(), loggedCrp.getId().longValue());

    if (this.isSwitchSession()) {
      this.clearPermissionsCache();
    }

    if (projectManager.findAll() != null) {

      if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin()) {
        myProjects = new ArrayList<>();
        for (ProjectPhase projectPhase : phase.getProjectPhases()) {
          projectPhase.getProject().setProjectInfo(projectPhase.getProject().getProjecInfoPhase(this.getActualPhase()));
          myProjects.add(projectPhase.getProject());
        }


      } else {

        List<Project> allProjects = new ArrayList<>();
        if (phase != null) {
          for (ProjectPhase projectPhase : phase.getProjectPhases()) {
            allProjects.add(projectManager.getProjectById(projectPhase.getProject().getId()));
          }
        }
        if (this.isPlanningActive()) {

          myProjects = projectManager.getUserProjects(this.getCurrentUser().getId(), loggedCrp.getAcronym()).stream()
            .filter(p -> p.isActive()).collect(Collectors.toList());

        } else {

          myProjects = projectManager.getUserProjectsReporting(this.getCurrentUser().getId(), loggedCrp.getAcronym())
            .stream().filter(p -> p.isActive()).collect(Collectors.toList());

        }
        List<Project> mProjects = new ArrayList<>();
        mProjects.addAll(myProjects);


        for (Project project : mProjects) {
          project.getProjecInfoPhase(this.getActualPhase());

          if (!allProjects.contains(project)) {
            myProjects.remove(project);
          }
        }


      }
      List<Project> closedProjects = projectManager.getCompletedProjects(this.getCrpID());
      if (closedProjects != null) {
        // closedProjects.addAll(projectManager.getNoPhaseProjects(this.getCrpID(), this.getActualPhase()));
        myProjects.removeAll(closedProjects);
      }
      Collections.sort(myProjects, (p1, p2) -> p1.getId().compareTo(p2.getId()));

    }


  }


  public void setLoggedCrp(GlobalUnit loggedCrp) {
    this.loggedCrp = loggedCrp;
  }

  public void setMyProjects(List<Project> myProjects) {
    this.myProjects = myProjects;
  }


}
