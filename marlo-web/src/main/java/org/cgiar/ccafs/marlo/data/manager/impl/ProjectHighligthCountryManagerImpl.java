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
import org.cgiar.ccafs.marlo.data.dao.ProjectHighligthCountryDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectHighligthCountryManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightCountry;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectHighligthCountryManagerImpl implements ProjectHighligthCountryManager {


  private ProjectHighligthCountryDAO projectHighligthCountryDAO;
  // Managers
  private PhaseDAO phaseDAO;

  @Inject
  public ProjectHighligthCountryManagerImpl(ProjectHighligthCountryDAO projectHighligthCountryDAO, PhaseDAO phaseDAO) {
    this.projectHighligthCountryDAO = projectHighligthCountryDAO;
    this.phaseDAO = phaseDAO;

  }

  @Override
  public void deleteProjectHighligthCountry(long projectHighligthCountryId) {

    ProjectHighlightCountry projectHighlightCountry = this.getProjectHighligthCountryById(projectHighligthCountryId);

    if (projectHighlightCountry.getPhase().getNext() != null) {
      this.deleteProjectHighligthCountryPhase(projectHighlightCountry.getPhase().getNext(),
        projectHighlightCountry.getProjectHighligth().getId(), projectHighlightCountry);
    }

    projectHighligthCountryDAO.deleteProjectHighligthCountry(projectHighligthCountryId);

  }

  public void deleteProjectHighligthCountryPhase(Phase next, long projecID,
    ProjectHighlightCountry projectHighlightCountry) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectHighlightCountry> projectHighlightCountries = phase.getProjectHighlightCountries().stream()
      .filter(c -> c.isActive() && c.getProjectHighligth().getId().longValue() == projecID
        && c.getLocElement().getId().equals(projectHighlightCountry.getLocElement().getId()))
      .collect(Collectors.toList());
    for (ProjectHighlightCountry projectHighlightCountryDB : projectHighlightCountries) {
      projectHighligthCountryDAO.deleteProjectHighligthCountry(projectHighlightCountryDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectHighligthCountryPhase(phase.getNext(), projecID, projectHighlightCountry);
    }


  }

  @Override
  public boolean existProjectHighligthCountry(long projectHighligthCountryID) {

    return projectHighligthCountryDAO.existProjectHighligthCountry(projectHighligthCountryID);
  }

  @Override
  public List<ProjectHighlightCountry> findAll() {

    return projectHighligthCountryDAO.findAll();

  }

  @Override
  public List<ProjectHighlightCountry> getHighlightCountrybyPhase(long higlightID, long phaseID) {
    return projectHighligthCountryDAO.getHighlightCountrybyPhase(higlightID, phaseID);
  }

  @Override
  public ProjectHighlightCountry getProjectHighligthCountryById(long projectHighligthCountryID) {

    return projectHighligthCountryDAO.find(projectHighligthCountryID);
  }


  public void saveHighlightCountryPhase(Phase next, long projectHighlightid,
    ProjectHighlightCountry projectHighlightCountry) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectHighlightCountry> projectHighlightCountries = phase.getProjectHighlightCountries().stream()
      .filter(c -> c.getProjectHighligth().getId().longValue() == projectHighlightid
        && c.getLocElement().getId().equals(projectHighlightCountry.getLocElement().getId()))
      .collect(Collectors.toList());
    if (projectHighlightCountries.isEmpty()) {


      ProjectHighlightCountry projectHighlightCountryAdd = new ProjectHighlightCountry();
      projectHighlightCountryAdd.setProjectHighligth(projectHighlightCountry.getProjectHighligth());
      projectHighlightCountryAdd.setPhase(phase);
      projectHighlightCountryAdd.setLocElement(projectHighlightCountry.getLocElement());
      projectHighligthCountryDAO.save(projectHighlightCountryAdd);


    }


    if (phase.getNext() != null) {
      this.saveHighlightCountryPhase(phase.getNext(), projectHighlightid, projectHighlightCountry);
    }
  }

  @Override
  public ProjectHighlightCountry saveProjectHighligthCountry(ProjectHighlightCountry projectHighlightCountry) {

    ProjectHighlightCountry country = projectHighligthCountryDAO.save(projectHighlightCountry);

    Phase phase = phaseDAO.find(country.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (country.getPhase().getNext() != null) {
        this.saveHighlightCountryPhase(country.getPhase().getNext(), country.getProjectHighligth().getId(),
          projectHighlightCountry);
      }
    }
    return country;
  }


}
