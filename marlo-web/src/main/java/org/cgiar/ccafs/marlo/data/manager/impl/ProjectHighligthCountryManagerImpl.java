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


import org.cgiar.ccafs.marlo.data.dao.ProjectHighligthCountryDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectHighligthCountryManager;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightCountry;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectHighligthCountryManagerImpl implements ProjectHighligthCountryManager {


  private ProjectHighligthCountryDAO projectHighligthCountryDAO;
  // Managers


  @Inject
  public ProjectHighligthCountryManagerImpl(ProjectHighligthCountryDAO projectHighligthCountryDAO) {
    this.projectHighligthCountryDAO = projectHighligthCountryDAO;


  }

  @Override
  public void deleteProjectHighligthCountry(long projectHighligthCountryId) {

    projectHighligthCountryDAO.deleteProjectHighligthCountry(projectHighligthCountryId);
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
  public ProjectHighlightCountry getProjectHighligthCountryById(long projectHighligthCountryID) {

    return projectHighligthCountryDAO.find(projectHighligthCountryID);
  }

  @Override
  public ProjectHighlightCountry saveProjectHighligthCountry(ProjectHighlightCountry projectHighlightCountry) {

    return projectHighligthCountryDAO.save(projectHighlightCountry);
  }


}
