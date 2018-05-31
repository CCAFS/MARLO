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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationOrganizationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationOrganizationManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationOrganization;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectInnovationOrganizationManagerImpl implements ProjectInnovationOrganizationManager {


  private ProjectInnovationOrganizationDAO projectInnovationOrganizationDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectInnovationOrganizationManagerImpl(ProjectInnovationOrganizationDAO projectInnovationOrganizationDAO,
    PhaseDAO phaseDAO) {
    this.projectInnovationOrganizationDAO = projectInnovationOrganizationDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectInnovationOrganization(long projectInnovationOrganizationId) {

    ProjectInnovationOrganization projectInnovationOrganization =
      this.getProjectInnovationOrganizationById(projectInnovationOrganizationId);

    if (projectInnovationOrganization.getPhase().getNext() != null) {
      this.deleteProjectInnovationOrganizationPhase(projectInnovationOrganization.getPhase().getNext(),
        projectInnovationOrganization.getProjectInnovation().getId(), projectInnovationOrganization);
    }

    projectInnovationOrganizationDAO.deleteProjectInnovationOrganization(projectInnovationOrganizationId);
  }

  public void deleteProjectInnovationOrganizationPhase(Phase next, long innovationID,
    ProjectInnovationOrganization projectInnovationOrganization) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationOrganization> projectInnovationOrganizations = phase.getProjectInnovationOrganizations()
      .stream()
      .filter(c -> c.isActive() && c.getProjectInnovation().getId().longValue() == innovationID && c
        .getRepIndOrganizationType().getId().equals(projectInnovationOrganization.getRepIndOrganizationType().getId()))
      .collect(Collectors.toList());

    for (ProjectInnovationOrganization projectInnovationOrganizationDB : projectInnovationOrganizations) {
      projectInnovationOrganizationDAO.deleteProjectInnovationOrganization(projectInnovationOrganizationDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationOrganizationPhase(phase.getNext(), innovationID, projectInnovationOrganization);
    }
  }

  @Override
  public boolean existProjectInnovationOrganization(long projectInnovationOrganizationID) {

    return projectInnovationOrganizationDAO.existProjectInnovationOrganization(projectInnovationOrganizationID);
  }


  @Override
  public List<ProjectInnovationOrganization> findAll() {

    return projectInnovationOrganizationDAO.findAll();

  }

  @Override
  public ProjectInnovationOrganization getProjectInnovationOrganizationById(long projectInnovationOrganizationID) {

    return projectInnovationOrganizationDAO.find(projectInnovationOrganizationID);
  }

  public void saveInnovationOrganizationPhase(Phase next, long innovationid,
    ProjectInnovationOrganization projectInnovationOrganization) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationOrganization> projectInnovationOrganizations = phase.getProjectInnovationOrganizations()
      .stream().filter(c -> c.getProjectInnovation().getId().longValue() == innovationid && c
        .getRepIndOrganizationType().getId().equals(projectInnovationOrganization.getRepIndOrganizationType().getId()))
      .collect(Collectors.toList());

    if (projectInnovationOrganizations.isEmpty()) {
      ProjectInnovationOrganization projectInnovationOrganizationAdd = new ProjectInnovationOrganization();
      projectInnovationOrganizationAdd.setProjectInnovation(projectInnovationOrganization.getProjectInnovation());
      projectInnovationOrganizationAdd.setPhase(phase);
      projectInnovationOrganizationAdd
        .setRepIndOrganizationType(projectInnovationOrganization.getRepIndOrganizationType());
      projectInnovationOrganizationDAO.save(projectInnovationOrganizationAdd);
    }


    if (phase.getNext() != null) {
      this.saveInnovationOrganizationPhase(phase.getNext(), innovationid, projectInnovationOrganization);
    }
  }

  @Override
  public ProjectInnovationOrganization
    saveProjectInnovationOrganization(ProjectInnovationOrganization projectInnovationOrganization) {

    ProjectInnovationOrganization organization = projectInnovationOrganizationDAO.save(projectInnovationOrganization);

    Phase phase = phaseDAO.find(organization.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (organization.getPhase().getNext() != null) {
        this.saveInnovationOrganizationPhase(organization.getPhase().getNext(),
          organization.getProjectInnovation().getId(), projectInnovationOrganization);
      }
    }
    return organization;
  }


}
