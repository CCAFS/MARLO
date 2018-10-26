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
import org.cgiar.ccafs.marlo.data.dao.ProjectHighligthTypeDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectHighligthTypeManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightType;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectHighligthTypeManagerImpl implements ProjectHighligthTypeManager {


  private ProjectHighligthTypeDAO projectHighligthTypeDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectHighligthTypeManagerImpl(ProjectHighligthTypeDAO projectHighligthTypeDAO, PhaseDAO phaseDAO) {
    this.projectHighligthTypeDAO = projectHighligthTypeDAO;
    this.phaseDAO = phaseDAO;

  }

  @Override
  public void deleteProjectHighligthType(long projectHighligthTypeId) {
    ProjectHighlightType projectHighlightType = this.getProjectHighligthTypeById(projectHighligthTypeId);

    if (projectHighlightType.getPhase().getNext() != null) {
      this.deleteProjectHighligthTypePhase(projectHighlightType.getPhase().getNext(),
        projectHighlightType.getProjectHighligth().getId(), projectHighlightType);
    }
    projectHighligthTypeDAO.deleteProjectHighligthType(projectHighligthTypeId);
  }

  public void deleteProjectHighligthTypePhase(Phase next, long projecID, ProjectHighlightType projectHighlightCountry) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectHighlightType> projectHighlightCountries = phase.getProjectHighligthsTypes().stream()
      .filter(c -> c.isActive() && c.getProjectHighligth().getId().longValue() == projecID
        && c.getIdType() == projectHighlightCountry.getIdType())
      .collect(Collectors.toList());
    for (ProjectHighlightType projectHighlightCountryDB : projectHighlightCountries) {
      projectHighligthTypeDAO.deleteProjectHighligthType(projectHighlightCountryDB.getId());
    }

    if (phase.getNext() != null) {
      this.deleteProjectHighligthTypePhase(phase.getNext(), projecID, projectHighlightCountry);
    }


  }

  @Override
  public boolean existProjectHighligthType(long projectHighligthTypeID) {

    return projectHighligthTypeDAO.existProjectHighligthType(projectHighligthTypeID);
  }

  @Override
  public List<ProjectHighlightType> findAll() {

    return projectHighligthTypeDAO.findAll();

  }

  @Override
  public List<ProjectHighlightType> getHighlightTypebyPhase(long higlightID, long phaseID) {
    return projectHighligthTypeDAO.getHighlightTypebyPhase(higlightID, phaseID);
  }

  @Override
  public ProjectHighlightType getProjectHighligthTypeById(long projectHighligthTypeID) {

    return projectHighligthTypeDAO.find(projectHighligthTypeID);
  }

  public void saveHighlightTypePhase(Phase next, long projectHighlightid, ProjectHighlightType highlightType) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectHighlightType> projectHighlightCountries = phase.getProjectHighligthsTypes().stream()
      .filter(c -> c.getProjectHighligth().getId().longValue() == projectHighlightid
        && c.getIdType() == highlightType.getIdType())
      .collect(Collectors.toList());


    if (projectHighlightCountries.isEmpty()) {


      ProjectHighlightType projectHighlightCountryAdd = new ProjectHighlightType();
      projectHighlightCountryAdd.setProjectHighligth(highlightType.getProjectHighligth());
      projectHighlightCountryAdd.setPhase(phase);
      projectHighlightCountryAdd.setIdType(highlightType.getIdType());
      projectHighligthTypeDAO.save(projectHighlightCountryAdd);


    }


    if (phase.getNext() != null) {
      this.saveHighlightTypePhase(phase.getNext(), projectHighlightid, highlightType);
    }
  }

  @Override
  public ProjectHighlightType saveProjectHighligthType(ProjectHighlightType projectHighlightType) {

    ProjectHighlightType type = projectHighligthTypeDAO.save(projectHighlightType);

    Phase phase = phaseDAO.find(type.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (type.getPhase().getNext() != null) {
        this.saveHighlightTypePhase(type.getPhase().getNext(), type.getProjectHighligth().getId(),
          projectHighlightType);
      }
    }
    return type;
  }

}
