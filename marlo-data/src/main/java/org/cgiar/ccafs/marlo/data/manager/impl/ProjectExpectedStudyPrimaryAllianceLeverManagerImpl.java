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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPrimaryAllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPrimaryAllianceLeverManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPrimaryAllianceLever;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyPrimaryAllianceLeverManagerImpl implements ProjectExpectedStudyPrimaryAllianceLeverManager {


  private ProjectExpectedStudyPrimaryAllianceLeverDAO projectExpectedStudyPrimaryAllianceLeverDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyPrimaryAllianceLeverManagerImpl(ProjectExpectedStudyPrimaryAllianceLeverDAO projectExpectedStudyPrimaryAllianceLeverDAO) {
    this.projectExpectedStudyPrimaryAllianceLeverDAO = projectExpectedStudyPrimaryAllianceLeverDAO;


  }

  @Override
  public void deleteProjectExpectedStudyPrimaryAllianceLever(long projectExpectedStudyPrimaryAllianceLeverId) {

    projectExpectedStudyPrimaryAllianceLeverDAO.deleteProjectExpectedStudyPrimaryAllianceLever(projectExpectedStudyPrimaryAllianceLeverId);
  }

  @Override
  public boolean existProjectExpectedStudyPrimaryAllianceLever(long projectExpectedStudyPrimaryAllianceLeverID) {

    return projectExpectedStudyPrimaryAllianceLeverDAO.existProjectExpectedStudyPrimaryAllianceLever(projectExpectedStudyPrimaryAllianceLeverID);
  }

  @Override
  public List<ProjectExpectedStudyPrimaryAllianceLever> findAll() {

    return projectExpectedStudyPrimaryAllianceLeverDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyPrimaryAllianceLever getProjectExpectedStudyPrimaryAllianceLeverById(long projectExpectedStudyPrimaryAllianceLeverID) {

    return projectExpectedStudyPrimaryAllianceLeverDAO.find(projectExpectedStudyPrimaryAllianceLeverID);
  }

  @Override
  public ProjectExpectedStudyPrimaryAllianceLever saveProjectExpectedStudyPrimaryAllianceLever(ProjectExpectedStudyPrimaryAllianceLever projectExpectedStudyPrimaryAllianceLever) {

    return projectExpectedStudyPrimaryAllianceLeverDAO.save(projectExpectedStudyPrimaryAllianceLever);
  }


}
