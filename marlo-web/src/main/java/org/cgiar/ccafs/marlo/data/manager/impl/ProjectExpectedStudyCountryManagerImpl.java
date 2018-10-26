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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyCountryDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyCountryManagerImpl implements ProjectExpectedStudyCountryManager {


  private ProjectExpectedStudyCountryDAO projectExpectedStudyCountryDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudyCountryManagerImpl(ProjectExpectedStudyCountryDAO projectExpectedStudyCountryDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudyCountryDAO = projectExpectedStudyCountryDAO;
    this.phaseDAO = phaseDAO;

  }

  @Override
  public void deleteProjectExpectedStudyCountry(long projectExpectedStudyCountryId) {

    ProjectExpectedStudyCountry projectExpectedStudyCountry =
      this.getProjectExpectedStudyCountryById(projectExpectedStudyCountryId);
    Phase currentPhase = projectExpectedStudyCountry.getPhase();

    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.deleteProjectExpectedStudyCountryPhase(currentPhase.getNext(),
          projectExpectedStudyCountry.getProjectExpectedStudy().getId(), projectExpectedStudyCountry);
      }
    }
    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.deleteProjectExpectedStudyCountryPhase(upkeepPhase,
    // projectExpectedStudyCountry.getProjectExpectedStudy().getId(), projectExpectedStudyCountry);
    // }
    // }
    // }

    projectExpectedStudyCountryDAO.deleteProjectExpectedStudyCountry(projectExpectedStudyCountryId);
  }

  public void deleteProjectExpectedStudyCountryPhase(Phase next, long expectedID,
    ProjectExpectedStudyCountry projectExpectedStudyCountry) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyCountry> projectExpectedStudyCountries = phase.getProjectExpectedStudyCountries().stream()
      .filter(c -> c.isActive() && c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getLocElement().getId().equals(projectExpectedStudyCountry.getLocElement().getId()))
      .collect(Collectors.toList());
    for (ProjectExpectedStudyCountry projectExpectedStudyCountryDB : projectExpectedStudyCountries) {
      projectExpectedStudyCountryDAO.deleteProjectExpectedStudyCountry(projectExpectedStudyCountryDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectExpectedStudyCountryPhase(phase.getNext(), expectedID, projectExpectedStudyCountry);
    }
  }

  @Override
  public boolean existProjectExpectedStudyCountry(long projectExpectedStudyCountryID) {

    return projectExpectedStudyCountryDAO.existProjectExpectedStudyCountry(projectExpectedStudyCountryID);
  }


  @Override
  public List<ProjectExpectedStudyCountry> findAll() {

    return projectExpectedStudyCountryDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyCountry getProjectExpectedStudyCountryById(long projectExpectedStudyCountryID) {

    return projectExpectedStudyCountryDAO.find(projectExpectedStudyCountryID);
  }

  @Override
  public List<ProjectExpectedStudyCountry> getProjectExpectedStudyCountrybyPhase(long expectedID, long phaseID) {
    return projectExpectedStudyCountryDAO.getProjectExpectedStudyCountrybyPhase(expectedID, phaseID);
  }

  public void saveExpectedStudyCountryPhase(Phase next, long expectedID,
    ProjectExpectedStudyCountry projectExpectedStudyCountry) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyCountry> projectExpectedStudyCountries = phase.getProjectExpectedStudyCountries().stream()
      .filter(c -> c.getProjectExpectedStudy().getId().longValue() == expectedID
        && c.getLocElement().getId().equals(projectExpectedStudyCountry.getLocElement().getId()))
      .collect(Collectors.toList());

    if (projectExpectedStudyCountries.isEmpty()) {
      ProjectExpectedStudyCountry projectExpectedStudyCountriesAdd = new ProjectExpectedStudyCountry();
      projectExpectedStudyCountriesAdd.setProjectExpectedStudy(projectExpectedStudyCountry.getProjectExpectedStudy());
      projectExpectedStudyCountriesAdd.setPhase(phase);
      projectExpectedStudyCountriesAdd.setLocElement(projectExpectedStudyCountry.getLocElement());
      projectExpectedStudyCountryDAO.save(projectExpectedStudyCountriesAdd);
    }


    if (phase.getNext() != null) {
      this.saveExpectedStudyCountryPhase(phase.getNext(), expectedID, projectExpectedStudyCountry);
    }
  }

  @Override
  public ProjectExpectedStudyCountry
    saveProjectExpectedStudyCountry(ProjectExpectedStudyCountry projectExpectedStudyCountry) {

    ProjectExpectedStudyCountry country = projectExpectedStudyCountryDAO.save(projectExpectedStudyCountry);
    Phase currentPhase = phaseDAO.find(country.getPhase().getId());
    if (currentPhase.getDescription().equals(APConstants.PLANNING)) {
      if (currentPhase.getNext() != null) {
        this.saveExpectedStudyCountryPhase(currentPhase.getNext(), country.getProjectExpectedStudy().getId(),
          projectExpectedStudyCountry);
      }
    }
    // Uncomment this line to allow reporting replication to upkeep
    // if (currentPhase.getDescription().equals(APConstants.REPORTING)) {
    // if (currentPhase.getNext() != null && currentPhase.getNext().getNext() != null) {
    // Phase upkeepPhase = currentPhase.getNext().getNext();
    // if (upkeepPhase != null) {
    // this.saveExpectedStudyCountryPhase(upkeepPhase, country.getProjectExpectedStudy().getId(),
    // projectExpectedStudyCountry);
    // }
    // }
    // }

    return country;
  }


}
