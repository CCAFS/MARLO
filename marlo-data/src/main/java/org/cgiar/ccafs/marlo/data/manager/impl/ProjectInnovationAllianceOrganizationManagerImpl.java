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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationAllianceOrganizationDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationAllianceOrganizationManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationAllianceOrganization;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectInnovationAllianceOrganizationManagerImpl implements ProjectInnovationAllianceOrganizationManager {


  private ProjectInnovationAllianceOrganizationDAO projectInnovationAllianceOrganizationDAO;
  private PhaseDAO phaseDAO;
  // Managers


  @Inject
  public ProjectInnovationAllianceOrganizationManagerImpl(
    ProjectInnovationAllianceOrganizationDAO projectInnovationAllianceOrganizationDAO, PhaseDAO phaseDAO) {
    this.projectInnovationAllianceOrganizationDAO = projectInnovationAllianceOrganizationDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectInnovationAllianceOrganization(long projectInnovationAllianceOrganizationsId) {

    ProjectInnovationAllianceOrganization projectInnovationAllianceOrganization =
      this.getProjectInnovationAllianceOrganizationById(projectInnovationAllianceOrganizationsId);

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (projectInnovationAllianceOrganization.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectInnovationAllianceOrganization.getPhase().getNext() != null) {
      this.deleteProjectInnovationAllianceOrganizationPhase(projectInnovationAllianceOrganization.getPhase().getNext(),
        projectInnovationAllianceOrganization.getProjectInnovation().getId(), projectInnovationAllianceOrganization);
    }

    if (projectInnovationAllianceOrganization.getPhase().getDescription().equals(APConstants.REPORTING)
      && projectInnovationAllianceOrganization.getPhase().getNext() != null
      && projectInnovationAllianceOrganization.getPhase().getNext().getNext() != null) {
      Phase upkeepPhase = projectInnovationAllianceOrganization.getPhase().getNext().getNext();
      if (upkeepPhase != null) {
        this.deleteProjectInnovationAllianceOrganizationPhase(upkeepPhase,
          projectInnovationAllianceOrganization.getProjectInnovation().getId(), projectInnovationAllianceOrganization);
      }
    }

    projectInnovationAllianceOrganizationDAO
      .deleteProjectInnovationAllianceOrganization(projectInnovationAllianceOrganizationsId);
  }

  public void deleteProjectInnovationAllianceOrganizationPhase(Phase next, long innovationID,
    ProjectInnovationAllianceOrganization projectInnovationAllianceOrganizations) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationAllianceOrganization> innovationAllianceOrganizations =
      projectInnovationAllianceOrganizationDAO
        .getProjectInnovationAllianceOrganizationsByInnovationAndPhase(innovationID, phase.getId()).stream()
        .filter(ao -> ao.getInstitution() != null && projectInnovationAllianceOrganizations.getInstitution() != null
          && ao.getInstitution().getId() != null
          && ao.getInstitution().getId().equals(projectInnovationAllianceOrganizations.getInstitution().getId()))
        .collect(Collectors.toList());

    for (ProjectInnovationAllianceOrganization projectInnovationAllianceOrganizationsDB : innovationAllianceOrganizations) {
      if (projectInnovationAllianceOrganizationsDB.getId() != null) {
        projectInnovationAllianceOrganizationDAO
          .deleteProjectInnovationAllianceOrganization(projectInnovationAllianceOrganizationsDB.getId());
      }
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationAllianceOrganizationPhase(phase.getNext(), innovationID,
        projectInnovationAllianceOrganizations);
    }
  }

  @Override
  public boolean existProjectInnovationAllianceOrganization(long projectInnovationAllianceOrganizationID) {

    return projectInnovationAllianceOrganizationDAO
      .existProjectInnovationAllianceOrganization(projectInnovationAllianceOrganizationID);
  }

  @Override
  public List<ProjectInnovationAllianceOrganization> findAll() {

    return projectInnovationAllianceOrganizationDAO.findAll();

  }

  @Override
  public ProjectInnovationAllianceOrganization
    getProjectInnovationAllianceOrganizationById(long projectInnovationAllianceOrganizationID) {

    return projectInnovationAllianceOrganizationDAO.find(projectInnovationAllianceOrganizationID);
  }

  @Override
  public List<ProjectInnovationAllianceOrganization>
    getProjectInnovationAllianceOrganizationsByInnovationAndPhase(long innovationID, long phaseID) {
    return projectInnovationAllianceOrganizationDAO
      .getProjectInnovationAllianceOrganizationsByInnovationAndPhase(innovationID, phaseID);
  }

  @Override
  public ProjectInnovationAllianceOrganization saveProjectInnovationAllianceOrganization(
    ProjectInnovationAllianceOrganization projectInnovationAllianceOrganizations) {

    ProjectInnovationAllianceOrganization innovationAllianceOrganization =
      projectInnovationAllianceOrganizationDAO.save(projectInnovationAllianceOrganizations);
    Phase phase = phaseDAO.find(innovationAllianceOrganization.getPhase().getId());

    // Conditions to Project Innovation Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectInnovationAllianceOrganizationPhase(innovationAllianceOrganization.getPhase().getNext(),
        innovationAllianceOrganization.getProjectInnovation().getId(), projectInnovationAllianceOrganizations);
    }

    if (phase.getDescription().equals(APConstants.REPORTING) && phase.getNext() != null
      && phase.getNext().getNext() != null) {
      Phase upkeepPhase = phase.getNext().getNext();
      if (upkeepPhase != null) {
        this.saveProjectInnovationAllianceOrganizationPhase(upkeepPhase,
          innovationAllianceOrganization.getProjectInnovation().getId(), projectInnovationAllianceOrganizations);
      }
    }
    return innovationAllianceOrganization;
  }

  private void saveProjectInnovationAllianceOrganizationPhase(Phase next, Long innovationID,
    ProjectInnovationAllianceOrganization projectInnovationAllianceOrganization) {
    Phase phase = phaseDAO.find(next.getId());
    List<ProjectInnovationAllianceOrganization> innovationAllianceOrganizations = new ArrayList<>();
    try {
      innovationAllianceOrganizations = projectInnovationAllianceOrganizationDAO
        .getProjectInnovationAllianceOrganizationsByInnovationAndPhase(innovationID, phase.getId()).stream()
        .filter(c -> c.getInstitution().getId().equals(projectInnovationAllianceOrganization.getInstitution().getId()))
        .collect(Collectors.toList());
    } catch (Exception e) {
      System.out.println("Error in InnovationAllianceOrganizationPhase( method" + e);
    }

    if (innovationAllianceOrganizations.isEmpty()) {
      ProjectInnovationAllianceOrganization projectInnovationAllianceOrganizationAdd =
        new ProjectInnovationAllianceOrganization();

      projectInnovationAllianceOrganizationAdd.setPhase(phase);
      projectInnovationAllianceOrganizationAdd
        .setInstitutionType(projectInnovationAllianceOrganization.getInstitutionType());
      projectInnovationAllianceOrganizationAdd.setInstitution(projectInnovationAllianceOrganization.getInstitution());
      projectInnovationAllianceOrganizationAdd
        .setOrganizationName(projectInnovationAllianceOrganization.getOrganizationName());
      projectInnovationAllianceOrganizationAdd
        .setScalingPartner(projectInnovationAllianceOrganization.getScalingPartner());
      projectInnovationAllianceOrganizationAdd.setNumber(projectInnovationAllianceOrganization.getNumber());
      projectInnovationAllianceOrganizationAdd
        .setProjectInnovation(projectInnovationAllianceOrganization.getProjectInnovation());
      projectInnovationAllianceOrganizationAdd.setPhase(phase);

      projectInnovationAllianceOrganizationDAO.save(projectInnovationAllianceOrganizationAdd);
    }
    if (phase.getNext() != null) {
      this.saveProjectInnovationAllianceOrganizationPhase(phase.getNext(), innovationID,
        projectInnovationAllianceOrganization);
    }
  }

}
