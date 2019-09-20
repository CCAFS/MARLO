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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationContributingOrganizationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationContributingOrganizationManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganization;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationContributingOrganizationManagerImpl
  implements ProjectInnovationContributingOrganizationManager {


  private ProjectInnovationContributingOrganizationDAO projectInnovationContributingOrganizationDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectInnovationContributingOrganizationManagerImpl(
    ProjectInnovationContributingOrganizationDAO projectInnovationContributingOrganizationDAO, PhaseDAO phaseDAO) {
    this.projectInnovationContributingOrganizationDAO = projectInnovationContributingOrganizationDAO;
    this.phaseDAO = phaseDAO;
  }


  @Override
  public void deleteProjectInnovationContributingOrganization(long projectInnovationContributingOrganizationId) {

    ProjectInnovationContributingOrganization projectInnovationContributingOrganization =
      this.getProjectInnovationContributingOrganizationById(projectInnovationContributingOrganizationId);


    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (projectInnovationContributingOrganization.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectInnovationContributingOrganization.getPhase().getNext() != null) {
      this.deleteProjectInnovationContributingOrganizationPhase(
        projectInnovationContributingOrganization.getPhase().getNext(),
        projectInnovationContributingOrganization.getProjectInnovation().getId(),
        projectInnovationContributingOrganization);
    }

    if (projectInnovationContributingOrganization.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (projectInnovationContributingOrganization.getPhase().getNext() != null
        && projectInnovationContributingOrganization.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = projectInnovationContributingOrganization.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectInnovationContributingOrganizationPhase(upkeepPhase,
            projectInnovationContributingOrganization.getProjectInnovation().getId(),
            projectInnovationContributingOrganization);
        }
      }
    }

    projectInnovationContributingOrganizationDAO
      .deleteProjectInnovationContributingOrganization(projectInnovationContributingOrganizationId);
  }
  
   public void deleteProjectInnovationContributingOrganizationPhase(Phase next, long innovationID,
    ProjectInnovationContributingOrganization projectInnovationContributingOrganization) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationContributingOrganization> projectInnovationContributingOrganizations =
      phase.getProjectInnovationContribution().stream()
        .filter(c -> c.getProjectInnovation().getId().longValue() == innovationID
          && c.getInstitution().getId().equals(projectInnovationContributingOrganization.getInstitution().getId()))
        .collect(Collectors.toList());

    for (ProjectInnovationContributingOrganization projectInnovationContributingOrganizationDB : projectInnovationContributingOrganizations) {
      projectInnovationContributingOrganizationDAO
        .deleteProjectInnovationContributingOrganization(projectInnovationContributingOrganizationDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationContributingOrganizationPhase(phase.getNext(), innovationID,
        projectInnovationContributingOrganization);
    }
  }

  @Override
  public boolean existProjectInnovationContributingOrganization(long projectInnovationContributingOrganizationID) {

    return projectInnovationContributingOrganizationDAO
      .existProjectInnovationContributingOrganization(projectInnovationContributingOrganizationID);
  }

  @Override
  public List<ProjectInnovationContributingOrganization> findAll() {

    return projectInnovationContributingOrganizationDAO.findAll();

  }

  @Override
  public ProjectInnovationContributingOrganization
    getProjectInnovationContributingOrganizationById(long projectInnovationContributingOrganizationID) {

    return projectInnovationContributingOrganizationDAO.find(projectInnovationContributingOrganizationID);
  }

  @Override
  public ProjectInnovationContributingOrganization
    getProjectInnovationContributingOrganizationById(long projectInnovationId, long institutionId, long phaseId) {

    return projectInnovationContributingOrganizationDAO
      .getProjectInnovationContributingOrganization(projectInnovationId, institutionId, phaseId);
  }


   @Override
  public ProjectInnovationContributingOrganization saveProjectInnovationContributingOrganization(
    ProjectInnovationContributingOrganization projectInnovationContributingOrganization) {

    ProjectInnovationContributingOrganization projectInnovationContributing =
      projectInnovationContributingOrganizationDAO.save(projectInnovationContributingOrganization);
    Phase phase = phaseDAO.find(projectInnovationContributing.getPhase().getId());

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectInnovationContributingPhase(projectInnovationContributing.getPhase().getNext(),
        projectInnovationContributing.getProjectInnovation().getId(), projectInnovationContributing);
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectInnovationContributingPhase(upkeepPhase,
            projectInnovationContributing.getProjectInnovation().getId(), projectInnovationContributing);
        }
      }
    }

    return projectInnovationContributing;
  }

  public void saveProjectInnovationContributingPhase(Phase next, long innovationid,
    ProjectInnovationContributingOrganization projectInnovationContributing) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationContributingOrganization> projectInnovatioCrps =
      phase.getProjectInnovationContribution().stream()
        .filter(c -> c.getProjectInnovation().getId().longValue() == innovationid
          && c.getInstitution().getId().equals(projectInnovationContributing.getInstitution().getId()))
        .collect(Collectors.toList());

    if (projectInnovatioCrps.isEmpty()) {
      ProjectInnovationContributingOrganization projectInnovationContributingAdd =
        new ProjectInnovationContributingOrganization();
      projectInnovationContributingAdd.setProjectInnovation(projectInnovationContributing.getProjectInnovation());
      projectInnovationContributingAdd.setPhase(phase);
      projectInnovationContributingAdd.setInstitution(projectInnovationContributing.getInstitution());
      projectInnovationContributingOrganizationDAO.save(projectInnovationContributingAdd);
    }

    if (phase.getNext() != null) {
      this.saveProjectInnovationContributingPhase(phase.getNext(), innovationid, projectInnovationContributing);
    }
  }


}
