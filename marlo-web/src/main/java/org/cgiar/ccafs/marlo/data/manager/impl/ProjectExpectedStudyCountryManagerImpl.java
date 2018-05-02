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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyCountryDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyCountryManagerImpl implements ProjectExpectedStudyCountryManager {


  private ProjectExpectedStudyCountryDAO projectExpectedStudyCountryDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyCountryManagerImpl(ProjectExpectedStudyCountryDAO projectExpectedStudyCountryDAO) {
    this.projectExpectedStudyCountryDAO = projectExpectedStudyCountryDAO;


  }

  @Override
  public void deleteProjectExpectedStudyCountry(long projectExpectedStudyCountryId) {

    projectExpectedStudyCountryDAO.deleteProjectExpectedStudyCountry(projectExpectedStudyCountryId);
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
  public ProjectExpectedStudyCountry saveProjectExpectedStudyCountry(ProjectExpectedStudyCountry projectExpectedStudyCountry) {

    return projectExpectedStudyCountryDAO.save(projectExpectedStudyCountry);
  }


}
