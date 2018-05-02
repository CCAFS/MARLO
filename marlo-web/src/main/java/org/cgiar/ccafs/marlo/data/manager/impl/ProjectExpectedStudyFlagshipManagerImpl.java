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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyFlagshipDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyFlagshipManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyFlagshipManagerImpl implements ProjectExpectedStudyFlagshipManager {


  private ProjectExpectedStudyFlagshipDAO projectExpectedStudyFlagshipDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyFlagshipManagerImpl(ProjectExpectedStudyFlagshipDAO projectExpectedStudyFlagshipDAO) {
    this.projectExpectedStudyFlagshipDAO = projectExpectedStudyFlagshipDAO;


  }

  @Override
  public void deleteProjectExpectedStudyFlagship(long projectExpectedStudyFlagshipId) {

    projectExpectedStudyFlagshipDAO.deleteProjectExpectedStudyFlagship(projectExpectedStudyFlagshipId);
  }

  @Override
  public boolean existProjectExpectedStudyFlagship(long projectExpectedStudyFlagshipID) {

    return projectExpectedStudyFlagshipDAO.existProjectExpectedStudyFlagship(projectExpectedStudyFlagshipID);
  }

  @Override
  public List<ProjectExpectedStudyFlagship> findAll() {

    return projectExpectedStudyFlagshipDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyFlagship getProjectExpectedStudyFlagshipById(long projectExpectedStudyFlagshipID) {

    return projectExpectedStudyFlagshipDAO.find(projectExpectedStudyFlagshipID);
  }

  @Override
  public ProjectExpectedStudyFlagship saveProjectExpectedStudyFlagship(ProjectExpectedStudyFlagship projectExpectedStudyFlagship) {

    return projectExpectedStudyFlagshipDAO.save(projectExpectedStudyFlagship);
  }


}
