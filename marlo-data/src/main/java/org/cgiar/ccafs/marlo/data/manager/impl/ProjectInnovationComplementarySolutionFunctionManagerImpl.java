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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationComplementarySolutionDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationComplementarySolutionFunctionDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationComplementarySolutionFunctionManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationComplementarySolution;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationComplementarySolutionFunction;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationFunction;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationComplementarySolutionFunctionManagerImpl
  implements ProjectInnovationComplementarySolutionFunctionManager {

  private PhaseDAO phaseDAO;
  private ProjectInnovationComplementarySolutionFunctionDAO projectInnovationComplementarySolutionFunctionDAO;
  private ProjectInnovationComplementarySolutionDAO projectInnovationComplementarySolutionDAO;
  private ProjectInnovationComplementarySolution projectInnovationComplementarySolutionInitial;
  // Managers


  @Inject
  public ProjectInnovationComplementarySolutionFunctionManagerImpl(
    ProjectInnovationComplementarySolutionFunctionDAO projectInnovationComplementarySolutionFunctionDAO,
    ProjectInnovationComplementarySolutionDAO projectInnovationComplementarySolutionDAO, PhaseDAO phaseDAO) {
    this.projectInnovationComplementarySolutionFunctionDAO = projectInnovationComplementarySolutionFunctionDAO;
    this.projectInnovationComplementarySolutionDAO = projectInnovationComplementarySolutionDAO;
    this.phaseDAO = phaseDAO;
  }


  /**
   * Searches for a complementary solution in the given phase that matches the initial one (title, short title,
   * description and innovation type).
   * This method assumes that {@code projectInnovationComplementarySolutionInitial} has been previously set,
   * typically during the save process.
   *
   * @param projectInnovationComplementarySolutionFunction The function containing the reference innovation.
   * @param phase The phase in which to search for a matching complementary solution.
   * @return The ID of the matching complementary solution in the given phase, or {@code 0} if not found.
   */
  public long calculateComplementarySolutionFunctionPhase(
    ProjectInnovationComplementarySolutionFunction projectInnovationComplementarySolutionFunction, Phase phase) {

    if (projectInnovationComplementarySolutionFunction == null
      || projectInnovationComplementarySolutionFunction.getProjectInnovationComplementarySolution() == null
      || projectInnovationComplementarySolutionFunction.getProjectInnovationComplementarySolution()
        .getProjectInnovation() == null
      || projectInnovationComplementarySolutionFunction.getProjectInnovationComplementarySolution()
        .getProjectInnovation().getId() == null
      || projectInnovationComplementarySolutionInitial == null) {
      return 0;
    }

    Long innovationId = projectInnovationComplementarySolutionFunction.getProjectInnovationComplementarySolution()
      .getProjectInnovation().getId();

    List<ProjectInnovationComplementarySolution> complementarySolutionsPhase = projectInnovationComplementarySolutionDAO
      .getProjectInnovationComplementarySolutionByInnovationAndPhase(innovationId, phase.getId());

    return complementarySolutionsPhase.stream()
      .filter(c -> c != null && c.getId() != null
        && Objects.equals(c.getTitle(), projectInnovationComplementarySolutionInitial.getTitle()))
      .map(ProjectInnovationComplementarySolution::getId).findFirst().orElse(0L);
  }


  @Override
  public void
    deleteProjectInnovationComplementarySolutionFunction(long projectInnovationComplementarySolutionFunctionId) {
    ProjectInnovationComplementarySolutionFunction projectInnovationComplementarySolutionFunction =
      this.getProjectInnovationComplementarySolutionFunctionById(projectInnovationComplementarySolutionFunctionId);

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (projectInnovationComplementarySolutionFunction.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectInnovationComplementarySolutionFunction.getPhase().getNext() != null) {
      this.deleteProjectInnovationComplementarySolutionFunctionPhase(
        projectInnovationComplementarySolutionFunction.getPhase().getNext(),
        projectInnovationComplementarySolutionFunction.getProjectInnovationComplementarySolution().getId(),
        projectInnovationComplementarySolutionFunction);
    }

    if (projectInnovationComplementarySolutionFunction.getPhase().getDescription().equals(APConstants.REPORTING)
      && projectInnovationComplementarySolutionFunction.getPhase().getNext() != null
      && projectInnovationComplementarySolutionFunction.getPhase().getNext().getNext() != null) {
      Phase upkeepPhase = projectInnovationComplementarySolutionFunction.getPhase().getNext().getNext();
      if (upkeepPhase != null) {
        this.deleteProjectInnovationComplementarySolutionFunctionPhase(upkeepPhase,
          projectInnovationComplementarySolutionFunction.getProjectInnovationFunction().getId(),
          projectInnovationComplementarySolutionFunction);
      }
    }
    projectInnovationComplementarySolutionFunctionDAO
      .deleteProjectInnovationComplementarySolutionFunction(projectInnovationComplementarySolutionFunctionId);
  }

  @Transactional
  public void deleteProjectInnovationComplementarySolutionFunctionPhase(Phase next,
    long projectInnovationComplementarySolutionID,
    ProjectInnovationComplementarySolutionFunction projectInnovationComplementarySolutionFunction) {

    // Recalculate matching solution ID for the current phase
    projectInnovationComplementarySolutionID =
      this.calculateComplementarySolutionFunctionPhase(projectInnovationComplementarySolutionFunction, next);

    if (projectInnovationComplementarySolutionID == 0L) {
      return; // No match in this phase, stop deletion
    }

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationComplementarySolutionFunction> functionsToDelete =
      Optional.ofNullable(projectInnovationComplementarySolutionFunctionDAO
        .getProjectInnovationComplementarySolutionFunctionByComplementarySolutionId(
          projectInnovationComplementarySolutionID))
        .orElse(Collections.emptyList()).stream().filter(c -> {
          ProjectInnovationFunction selA = c.getProjectInnovationFunction();
          ProjectInnovationFunction selB =
            projectInnovationComplementarySolutionFunction.getProjectInnovationFunction();
          return selA != null && selA.getId() != null && selB != null && selB.getId() != null
            && selA.getId().equals(selB.getId());
        }).collect(Collectors.toList());

    for (ProjectInnovationComplementarySolutionFunction function : functionsToDelete) {
      if (function != null && function.getId() != null) {
        projectInnovationComplementarySolutionFunctionDAO
          .deleteProjectInnovationComplementarySolutionFunction(function.getId());
      }
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationComplementarySolutionFunctionPhase(phase.getNext(),
        projectInnovationComplementarySolutionID, projectInnovationComplementarySolutionFunction);
    }
  }


  @Override
  public boolean
    existProjectInnovationComplementarySolutionFunction(long projectInnovationComplementarySolutionFunctionID) {
    return projectInnovationComplementarySolutionFunctionDAO
      .existProjectInnovationComplementarySolutionFunction(projectInnovationComplementarySolutionFunctionID);
  }

  @Override
  public List<ProjectInnovationComplementarySolutionFunction> findAll() {

    return projectInnovationComplementarySolutionFunctionDAO.findAll();

  }

  @Override
  public List<ProjectInnovationComplementarySolutionFunction>
    getProjectInnovationComplementarySolutionFunctionByComplementarySolutionId(long complementarySolutionID) {
    return projectInnovationComplementarySolutionFunctionDAO
      .getProjectInnovationComplementarySolutionFunctionByComplementarySolutionId(complementarySolutionID);
  }

  @Override
  public ProjectInnovationComplementarySolutionFunction
    getProjectInnovationComplementarySolutionFunctionById(long projectInnovationComplementarySolutionFunctionID) {

    return projectInnovationComplementarySolutionFunctionDAO.find(projectInnovationComplementarySolutionFunctionID);
  }

  @Override
  public ProjectInnovationComplementarySolutionFunction saveProjectInnovationComplementarySolutionFunction(
    ProjectInnovationComplementarySolutionFunction projectInnovationComplementarySolutionFunction) {
    if (projectInnovationComplementarySolutionFunction != null
      && projectInnovationComplementarySolutionFunction.getProjectInnovationComplementarySolution() != null
      && projectInnovationComplementarySolutionFunction.getProjectInnovationComplementarySolution().getId() != null) {
      projectInnovationComplementarySolutionInitial =
        projectInnovationComplementarySolutionFunction.getProjectInnovationComplementarySolution();
    }

    ProjectInnovationComplementarySolutionFunction innovationComplementarySolutionFunction =
      projectInnovationComplementarySolutionFunctionDAO.save(projectInnovationComplementarySolutionFunction);
    Phase phase = phaseDAO.find(projectInnovationComplementarySolutionFunction.getPhase().getId());

    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectInnovationComplementarySolutionFunctionPhase(
        innovationComplementarySolutionFunction.getPhase().getNext(),
        innovationComplementarySolutionFunction.getProjectInnovationComplementarySolution().getId(),
        innovationComplementarySolutionFunction);
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectInnovationComplementarySolutionFunctionPhase(upkeepPhase,
            innovationComplementarySolutionFunction.getProjectInnovationComplementarySolution().getId(),
            innovationComplementarySolutionFunction);
        }
      }
    }
    return innovationComplementarySolutionFunction;
  }

  private void saveProjectInnovationComplementarySolutionFunctionPhase(Phase next, Long complementarySolutionID,
    ProjectInnovationComplementarySolutionFunction projectInnovationComplementarySolutionFunction) {

    complementarySolutionID =
      this.calculateComplementarySolutionFunctionPhase(projectInnovationComplementarySolutionFunction, next);

    if (complementarySolutionID == 0L) {
      return; // No matching solution found in this phase
    }

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationComplementarySolutionFunction> existingFunctionsInPhase = Optional
      .ofNullable(projectInnovationComplementarySolutionFunctionDAO
        .getProjectInnovationComplementarySolutionFunctionByComplementarySolutionId(complementarySolutionID))
      .orElse(Collections.emptyList()).stream().filter(c -> {
        ProjectInnovationComplementarySolution selA = c.getProjectInnovationComplementarySolution();
        ProjectInnovationComplementarySolution selB =
          projectInnovationComplementarySolutionFunction.getProjectInnovationComplementarySolution();
        return selA != null && selA.getId() != null && selB != null && selB.getId() != null
          && selA.getId().equals(selB.getId());
      }).collect(Collectors.toList());

    if (existingFunctionsInPhase.isEmpty()) {
      ProjectInnovationComplementarySolution matchingSolution =
        projectInnovationComplementarySolutionDAO.find(complementarySolutionID);

      ProjectInnovationComplementarySolutionFunction newFunction = new ProjectInnovationComplementarySolutionFunction();
      newFunction.setProjectInnovationComplementarySolution(matchingSolution);
      newFunction.setPhase(phase);
      newFunction
        .setProjectInnovationFunction(projectInnovationComplementarySolutionFunction.getProjectInnovationFunction());

      projectInnovationComplementarySolutionFunctionDAO.save(newFunction);
    }

    if (phase.getNext() != null) {
      this.saveProjectInnovationComplementarySolutionFunctionPhase(phase.getNext(), complementarySolutionID,
        projectInnovationComplementarySolutionFunction);
    }
  }

}
