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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationCountryDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationCountryManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectInnovationCountryManagerImpl implements ProjectInnovationCountryManager {


  private ProjectInnovationCountryDAO projectInnovationCountryDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectInnovationCountryManagerImpl(ProjectInnovationCountryDAO projectInnovationCountryDAO,
    PhaseDAO phaseDAO) {
    this.projectInnovationCountryDAO = projectInnovationCountryDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectInnovationCountry(long projectInnovationCountryId) {

    ProjectInnovationCountry projectInnovationCountry =
      this.getProjectInnovationCountryById(projectInnovationCountryId);

    if (projectInnovationCountry.getPhase().getNext() != null) {
      this.deleteProjectInnovationCountryPhase(projectInnovationCountry.getPhase().getNext(),
        projectInnovationCountry.getProjectInnovation().getId(), projectInnovationCountry);
    }

    projectInnovationCountryDAO.deleteProjectInnovationCountry(projectInnovationCountryId);
  }


  public void deleteProjectInnovationCountryPhase(Phase next, long innovationID,
    ProjectInnovationCountry projectInnovationCountry) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationCountry> projectInnovationCountries = phase.getProjectInnovationCountries().stream()
      .filter(c -> c.isActive() && c.getProjectInnovation().getId().longValue() == innovationID
        && c.getLocElement().getId().equals(projectInnovationCountry.getLocElement().getId()))
      .collect(Collectors.toList());
    for (ProjectInnovationCountry projectInnovationCountryDB : projectInnovationCountries) {
      projectInnovationCountryDAO.deleteProjectInnovationCountry(projectInnovationCountryDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectInnovationCountryPhase(phase.getNext(), innovationID, projectInnovationCountry);
    }
  }

  @Override
  public boolean existProjectInnovationCountry(long projectInnovationCountryID) {

    return projectInnovationCountryDAO.existProjectInnovationCountry(projectInnovationCountryID);
  }

  @Override
  public List<ProjectInnovationCountry> findAll() {

    return projectInnovationCountryDAO.findAll();

  }

  @Override
  public List<ProjectInnovationCountry> getInnovationCountrybyPhase(long innovationID, long phaseID) {
    return projectInnovationCountryDAO.getInnovationCountrybyPhase(innovationID, phaseID);
  }

  @Override
  public ProjectInnovationCountry getProjectInnovationCountryById(long projectInnovationCountryID) {

    return projectInnovationCountryDAO.find(projectInnovationCountryID);
  }

  public void saveInnovationCountryPhase(Phase next, long innovationid,
    ProjectInnovationCountry projectInnovationCountry) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationCountry> projectInnovationCountries = phase.getProjectInnovationCountries().stream()
      .filter(c -> c.getProjectInnovation().getId().longValue() == innovationid
        && c.getLocElement().getId().equals(projectInnovationCountry.getLocElement().getId()))
      .collect(Collectors.toList());

    if (projectInnovationCountries.isEmpty()) {
      ProjectInnovationCountry projectInnovationCountryAdd = new ProjectInnovationCountry();
      projectInnovationCountryAdd.setProjectInnovation(projectInnovationCountry.getProjectInnovation());
      projectInnovationCountryAdd.setPhase(phase);
      projectInnovationCountryAdd.setLocElement(projectInnovationCountry.getLocElement());
      projectInnovationCountryDAO.save(projectInnovationCountryAdd);
    }


    if (phase.getNext() != null) {
      this.saveInnovationCountryPhase(phase.getNext(), innovationid, projectInnovationCountry);
    }
  }

  @Override
  public ProjectInnovationCountry saveProjectInnovationCountry(ProjectInnovationCountry projectInnovationCountry) {

    ProjectInnovationCountry country = projectInnovationCountryDAO.save(projectInnovationCountry);

    Phase phase = phaseDAO.find(country.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (country.getPhase().getNext() != null) {
        this.saveInnovationCountryPhase(country.getPhase().getNext(), country.getProjectInnovation().getId(),
          projectInnovationCountry);
      }
    }
    return country;
  }


}
