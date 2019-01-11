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

    if (projectPolicyCountry.getPhase().getNext() != null) {
      this.deleteProjectPolicyCountryPhase(projectPolicyCountry.getPhase().getNext(),
        projectPolicyCountry.getProjectPolicy().getId(), projectPolicyCountry);
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
  public ProjectPolicyCountry saveProjectPolicyCountry(ProjectPolicyCountry projectPolicyCountry) {


    ProjectPolicyCountry country = projectPolicyCountryDAO.save(projectPolicyCountry);

    Phase phase = phaseDAO.find(country.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (country.getPhase().getNext() != null) {
        this.saveProjectPolicyCountryPhase(country.getPhase().getNext(), country.getProjectPolicy().getId(),
          projectPolicyCountry);
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
