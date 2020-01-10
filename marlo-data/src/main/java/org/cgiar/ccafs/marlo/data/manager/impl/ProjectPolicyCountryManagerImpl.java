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
import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyCountryDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCountryManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicyCountryManagerImpl implements ProjectPolicyCountryManager {


  private ProjectPolicyCountryDAO projectPolicyCountryDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectPolicyCountryManagerImpl(ProjectPolicyCountryDAO projectPolicyCountryDAO, PhaseDAO phaseDAO) {
    this.projectPolicyCountryDAO = projectPolicyCountryDAO;
    this.phaseDAO = phaseDAO;
  }


  @Override
  public void deleteProjectPolicyCountry(long projectPolicyCountryId) {


    ProjectPolicyCountry projectPolicyCountry = this.getProjectPolicyCountryById(projectPolicyCountryId);

    // Conditions to Project Policy Works In AR phase and Upkeep Phase
    if (projectPolicyCountry.getPhase().getDescription().equals(APConstants.PLANNING)
      && projectPolicyCountry.getPhase().getNext() != null) {
      this.deleteProjectPolicyCountryPhase(projectPolicyCountry.getPhase().getNext(),
        projectPolicyCountry.getProjectPolicy().getId(), projectPolicyCountry);
    }

    if (projectPolicyCountry.getPhase().getDescription().equals(APConstants.REPORTING)) {
      if (projectPolicyCountry.getPhase().getNext() != null
        && projectPolicyCountry.getPhase().getNext().getNext() != null) {
        Phase upkeepPhase = projectPolicyCountry.getPhase().getNext().getNext();
        if (upkeepPhase != null) {
          this.deleteProjectPolicyCountryPhase(upkeepPhase, projectPolicyCountry.getProjectPolicy().getId(),
            projectPolicyCountry);
        }
      }
    }


    projectPolicyCountryDAO.deleteProjectPolicyCountry(projectPolicyCountryId);
  }

  public void deleteProjectPolicyCountryPhase(Phase next, long policyID, ProjectPolicyCountry projectPolicyCountry) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyCountry> projectPolicyCountries =
      phase.getProjectPolicyCountries().stream()
        .filter(c -> c.isActive() && c.getProjectPolicy().getId().longValue() == policyID
          && c.getLocElement().getId().equals(projectPolicyCountry.getLocElement().getId()))
        .collect(Collectors.toList());
    for (ProjectPolicyCountry projectPolicyCountryDB : projectPolicyCountries) {
      projectPolicyCountryDAO.deleteProjectPolicyCountry(projectPolicyCountryDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectPolicyCountryPhase(phase.getNext(), policyID, projectPolicyCountry);
    }
  }

  @Override
  public boolean existProjectPolicyCountry(long projectPolicyCountryID) {

    return projectPolicyCountryDAO.existProjectPolicyCountry(projectPolicyCountryID);
  }

  @Override
  public List<ProjectPolicyCountry> findAll() {

    return projectPolicyCountryDAO.findAll();

  }

  @Override
  public List<ProjectPolicyCountry> getPolicyCountrybyPhase(long policyID, long phaseID) {
    return projectPolicyCountryDAO.getPolicyCountrybyPhase(policyID, phaseID);
  }

  @Override
  public ProjectPolicyCountry getProjectPolicyCountryById(long projectPolicyCountryID) {

    return projectPolicyCountryDAO.find(projectPolicyCountryID);
  }

  @Override
  public ProjectPolicyCountry getProjectPolicyCountryByPhase(long projectPolicyID, long countryID, long phaseID) {

    return projectPolicyCountryDAO.getProjectPolicyCountryByPhase(projectPolicyID, countryID, phaseID);
  }

  @Override
  public ProjectPolicyCountry saveProjectPolicyCountry(ProjectPolicyCountry projectPolicyCountry) {


    ProjectPolicyCountry country = projectPolicyCountryDAO.save(projectPolicyCountry);
    Phase phase = phaseDAO.find(country.getPhase().getId());

    // Conditions to Project Policy Works In AR phase and Upkeep Phase
    if (phase.getDescription().equals(APConstants.PLANNING) && phase.getNext() != null) {
      this.saveProjectPolicyCountryPhase(country.getPhase().getNext(), country.getProjectPolicy().getId(),
        projectPolicyCountry);
    }

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveProjectPolicyCountryPhase(upkeepPhase, country.getProjectPolicy().getId(), projectPolicyCountry);
        }
      }
    }

    return country;
  }


  public void saveProjectPolicyCountryPhase(Phase next, long policyID, ProjectPolicyCountry projectPolicyCountry) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyCountry> projectPolicyCountries =
      phase.getProjectPolicyCountries().stream()
        .filter(c -> c.getProjectPolicy().getId().longValue() == policyID
          && c.getLocElement().getId().equals(projectPolicyCountry.getLocElement().getId()))
        .collect(Collectors.toList());

    if (projectPolicyCountries.isEmpty()) {
      ProjectPolicyCountry projectPolicyCountryAdd = new ProjectPolicyCountry();
      projectPolicyCountryAdd.setProjectPolicy(projectPolicyCountry.getProjectPolicy());
      projectPolicyCountryAdd.setPhase(phase);
      projectPolicyCountryAdd.setLocElement(projectPolicyCountry.getLocElement());
      projectPolicyCountryDAO.save(projectPolicyCountryAdd);
    }


    if (phase.getNext() != null) {
      this.saveProjectPolicyCountryPhase(phase.getNext(), policyID, projectPolicyCountry);
    }
  }


}
