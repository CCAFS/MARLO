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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.projects;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.rest.dto.ProjectDTO;
import org.cgiar.ccafs.marlo.rest.mappers.ProjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

@Named
public class ProjectItem<T> {

  private ProjectManager projectManager;
  private ProjectMapper projectMapper;
  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;

  @Inject
  public ProjectItem(ProjectManager projectManager, ProjectMapper projectMapper, PhaseManager phaseManager,
    GlobalUnitManager globalUnitManager) {
    this.projectManager = projectManager;
    this.projectMapper = projectMapper;
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
  }

  /**
   * find a project requesting by Id
   * 
   * @param id
   * @return
   */
  public ResponseEntity<ProjectDTO> findProjectById(Long id, String globalUnitAcronym) {

    Project project = projectManager.getProjectById(id);
    GlobalUnit globalUnit = globalUnitManager.findGlobalUnitByAcronym(globalUnitAcronym);

    if (project != null) {
      project = this.getProjectInformation(project, globalUnit);
    }

    return Optional.ofNullable(project).map(this.projectMapper::projectToProjectDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * for this method get the project information
   * 
   * @param project
   * @return
   */
  public Project getProjectInformation(Project project, GlobalUnit globalUnit) {

    Phase phase = phaseManager.getActivePhase(globalUnit.getId());

    // Get the Project Info
    project.getProjecInfoPhase(phase);


    // Get the Flagship and Programs Regions that belongs in the Project
    List<CrpProgram> programs = new ArrayList<>();
    for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().getId().equals(phase.getId())
        && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()
        && c.getCrpProgram().getCrp().getId().equals(globalUnit.getId()))
      .collect(Collectors.toList())) {
      programs.add(projectFocuses.getCrpProgram());
    }

    List<CrpProgram> regions = new ArrayList<>();

    for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
      .filter(c -> c.isActive() && c.getPhase() != null && c.getPhase().getId().equals(phase.getId())
        && c.getCrpProgram().getProgramType() == ProgramType.REGIONAL_PROGRAM_TYPE.getValue()
        && c.getCrpProgram().getCrp().getId().equals(globalUnit.getId()))
      .collect(Collectors.toList())) {
      regions.add(projectFocuses.getCrpProgram());

    }

    project.setRegions(regions);
    project.setFlagships(programs);


    return project;
  }

}
