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
import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyInnovationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInnovationManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInnovation;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicyInnovationManagerImpl implements ProjectPolicyInnovationManager {


  private ProjectPolicyInnovationDAO projectPolicyInnovationDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectPolicyInnovationManagerImpl(ProjectPolicyInnovationDAO projectPolicyInnovationDAO, PhaseDAO phaseDAO) {
    this.projectPolicyInnovationDAO = projectPolicyInnovationDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectPolicyInnovation(long projectPolicyInnovationId) {

    ProjectPolicyInnovation projectPolicyInnovation = this.getProjectPolicyInnovationById(projectPolicyInnovationId);

    if (projectPolicyInnovation.getPhase().getNext() != null) {
      this.deleteProjectPolicyInnovationPhase(projectPolicyInnovation.getPhase().getNext(),
        projectPolicyInnovation.getProjectPolicy().getId(), projectPolicyInnovation);
    }

    projectPolicyInnovationDAO.deleteProjectPolicyInnovation(projectPolicyInnovationId);
  }

  public void deleteProjectPolicyInnovationPhase(Phase next, long policyID,
    ProjectPolicyInnovation projectPolicyInnovation) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyInnovation> projectPolicyInnovations = phase.getProjectPolicyInnovations().stream()
      .filter(c -> c.isActive() && c.getProjectPolicy().getId().longValue() == policyID
        && c.getProjectInnovation().getId().equals(projectPolicyInnovation.getProjectInnovation().getId()))
      .collect(Collectors.toList());

    for (ProjectPolicyInnovation projectPolicyInnovationDB : projectPolicyInnovations) {
      projectPolicyInnovationDAO.deleteProjectPolicyInnovation(projectPolicyInnovationDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectPolicyInnovationPhase(phase.getNext(), policyID, projectPolicyInnovation);
    }
  }

  @Override
  public boolean existProjectPolicyInnovation(long projectPolicyInnovationID) {

    return projectPolicyInnovationDAO.existProjectPolicyInnovation(projectPolicyInnovationID);
  }

  @Override
  public List<ProjectPolicyInnovation> findAll() {

    return projectPolicyInnovationDAO.findAll();

  }

  @Override
  public ProjectPolicyInnovation getProjectPolicyInnovationById(long projectPolicyInnovationID) {

    return projectPolicyInnovationDAO.find(projectPolicyInnovationID);
  }

  public void savePolicyInnovationPhase(Phase next, long policyID, ProjectPolicyInnovation projectPolicyInnovation) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyInnovation> projectPolicyInnovations = phase.getProjectPolicyInnovations().stream()
      .filter(c -> c.getProjectPolicy().getId().longValue() == policyID
        && c.getProjectInnovation().getId().equals(projectPolicyInnovation.getProjectInnovation().getId()))
      .collect(Collectors.toList());

    if (projectPolicyInnovations.isEmpty()) {
      ProjectPolicyInnovation projectPolicyInnovationAdd = new ProjectPolicyInnovation();
      projectPolicyInnovationAdd.setProjectPolicy(projectPolicyInnovation.getProjectPolicy());
      projectPolicyInnovationAdd.setPhase(phase);
      projectPolicyInnovationAdd.setProjectInnovation(projectPolicyInnovation.getProjectInnovation());
      projectPolicyInnovationDAO.save(projectPolicyInnovationAdd);
    }


    if (phase.getNext() != null) {
      this.savePolicyInnovationPhase(phase.getNext(), policyID, projectPolicyInnovation);
    }
  }

  @Override
  public ProjectPolicyInnovation saveProjectPolicyInnovation(ProjectPolicyInnovation projectPolicyInnovation) {

    ProjectPolicyInnovation innovation = projectPolicyInnovationDAO.save(projectPolicyInnovation);

    Phase phase = phaseDAO.find(innovation.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (innovation.getPhase().getNext() != null) {
        this.savePolicyInnovationPhase(projectPolicyInnovation.getPhase().getNext(),
          projectPolicyInnovation.getProjectPolicy().getId(), projectPolicyInnovation);
      }
    }
    return innovation;
  }


}
