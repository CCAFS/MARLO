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


package org.cgiar.ccafs.marlo.action.crp.admin;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPhaseManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

public class CrpProjectPhases extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -8565941816588436403L;


  private ProjectManager projectManager;


  private PhaseManager phaseManager;
  private ProjectPhaseManager projectPhaseManager;
  private GlobalUnitManager globalUnitManager;


  private List<Project> allProjects;


  private List<Project> phasesProjects;

  @Inject
  public CrpProjectPhases(APConfig config, ProjectManager projectManager, PhaseManager phaseManager,
    ProjectPhaseManager projectPhaseManager, GlobalUnitManager globalUnitManager) {
    super(config);
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.projectPhaseManager = projectPhaseManager;
    this.globalUnitManager = globalUnitManager;


  }

  public List<Project> getAllProjects() {
    return allProjects;
  }

  public List<Project> getPhasesProjects() {
    return phasesProjects;
  }

  @Override
  public void prepare() throws Exception {

    /*
     * Replace the allprojects List obtained by crp relation, now the project list is obtain whit GlobalUnitPorject
     * cross relation
     */
    allProjects = new ArrayList<>();
    GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(this.getCrpID());
    List<GlobalUnitProject> globalUnitProjects = new ArrayList<>(
      globalUnit.getGlobalUnitProjects().stream().filter(gup -> gup.isActive()).collect(Collectors.toList()));
    for (GlobalUnitProject globalUnitProject : globalUnitProjects) {
      allProjects.add(globalUnitProject.getProject());
    }


    Phase phase = phaseManager.findCycle(this.getCurrentCycle(), this.getCurrentCycleYear(), this.getCrpID());
    phasesProjects = new ArrayList<Project>();
    for (ProjectPhase projectPhase : phase.getProjectPhases()) {
      phasesProjects.add(projectManager.getProjectById(projectPhase.getProject().getId()));
    }

    allProjects.removeAll(phasesProjects);
    Collections.sort(allProjects, (tu1, tu2) -> tu1.getId().compareTo(tu2.getId()));

    Collections.sort(phasesProjects, (tu1, tu2) -> tu1.getId().compareTo(tu2.getId()));

    if (this.isHttpPost()) {
      if (allProjects != null) {
        allProjects.clear();
      }

      if (phasesProjects != null) {
        phasesProjects.clear();
      }

    }
  }


  @Override
  public String save() {
    Phase phase = phaseManager.findCycle(this.getCurrentCycle(), this.getCurrentCycleYear(), this.getCrpID());

    if (phasesProjects == null) {
      phasesProjects = new ArrayList<>();
    }
    List<Project> previousProjects = new ArrayList<>();
    for (ProjectPhase projectPhase : phase.getProjectPhases()) {
      previousProjects.add(projectPhase.getProject());
      if (!phasesProjects.contains(projectPhase.getProject())) {
        projectPhaseManager.deleteProjectPhase(projectPhase.getId());
      }

    }
    Collections.sort(previousProjects, (tu1, tu2) -> tu1.getId().compareTo(tu2.getId()));

    for (Project project : phasesProjects) {
      if (project != null) {
        if (!previousProjects.contains(project)) {
          ProjectPhase projectPhase = new ProjectPhase();
          projectPhase.setPhase(phase);
          projectPhase.setProject(projectManager.getProjectById(project.getId()));
          projectPhaseManager.saveProjectPhase(projectPhase);
        }
      }


    }

    this.addActionMessage("message:" + this.getText("saving.saved"));

    return SUCCESS;
  }

  public void setAllProjects(List<Project> allProjects) {
    this.allProjects = allProjects;
  }

  public void setPhasesProjects(List<Project> phasesProjects) {
    this.phasesProjects = phasesProjects;
  }
}

