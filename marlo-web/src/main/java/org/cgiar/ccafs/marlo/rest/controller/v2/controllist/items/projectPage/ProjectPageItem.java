/*****************************************************************
 * \ * This file is part of Managing Agricultural Research for Learning &
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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.projectPage;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPageDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.ProjectPageMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Luis Benavides - CIAT/CCAFS
 */

@Named
public class ProjectPageItem<T> {

  private static final String STRING_TRUE = "true";
  private ProjectManager projectManager;
  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private ProjectPageMapper projectPageMapper;
  private List<FieldErrorDTO> fieldErrors;

  @Inject
  public ProjectPageItem(ProjectManager projectManager, PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    ProjectPageMapper projectPageMapper) {
    this.projectManager = projectManager;
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.projectPageMapper = projectPageMapper;
  }

  /**
   * find a project requesting by Id
   * 
   * @param id
   * @return
   */
  public ResponseEntity<ProjectPageDTO> findProjectPageById(Long id, String globalUnitAcronym) {

    this.fieldErrors = new ArrayList<FieldErrorDTO>();

    Project project = projectManager.getProjectById(id);

    if (project == null) {
      this.fieldErrors.add(new FieldErrorDTO("ProjectPageDTO", "Project", "Invalid Project code"));
    }

    GlobalUnit globalUnit = globalUnitManager.findGlobalUnitByAcronym(globalUnitAcronym);

    if (globalUnit == null) {
      this.fieldErrors.add(new FieldErrorDTO("ProjectPageDTO", "GlobalUnitEntity", "Invalid CGIAR entity acronym"));
    } else {
      Boolean crpProjectPage = globalUnit.getCustomParameters().stream()
        .filter(c -> c.getParameter().getKey().equalsIgnoreCase(APConstants.CRP_PROJECT_PAGE))
        .allMatch(t -> (t.getValue() == null) ? false : t.getValue().equalsIgnoreCase(STRING_TRUE));

      if (crpProjectPage) {
        project = (project != null) ? this.getProjectInformation(project, globalUnit) : null;
      } else {
        this.fieldErrors.add(new FieldErrorDTO("ProjectPageDTO", "GlobalUnitEntity", "CGIAR entity not autorized"));
      }
    }

    if (!this.fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", this.fieldErrors);
    }

    return Optional.ofNullable(project).map(this.projectPageMapper::projectToProjectPageDTO)
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
