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

    projectInnovationContributingOrganizationDAO
      .deleteProjectInnovationContributingOrganization(projectInnovationContributingOrganizationId);
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
  public ProjectInnovationContributingOrganization saveProjectInnovationContributingOrganization(
    ProjectInnovationContributingOrganization projectInnovationContributingOrganization) {

    ProjectInnovationContributingOrganization projectInnovationContributing =
      projectInnovationContributingOrganizationDAO.save(projectInnovationContributingOrganization);
    /*
     * if (projectInnovationContributing.getPhase().getNext() != null) {
     * this.saveProjectInnovationContributingPhase(projectInnovationContributing.getPhase().getNext(),
     * projectInnovationContributing.getProjectInnovation().getId(), projectInnovationContributing);
     * }
     */
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
