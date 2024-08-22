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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyRelatedAllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRelatedAllianceLeverManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRelatedAllianceLever;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyRelatedAllianceLeverManagerImpl implements ProjectExpectedStudyRelatedAllianceLeverManager {


  private ProjectExpectedStudyRelatedAllianceLeverDAO projectExpectedStudyRelatedAllianceLeverDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyRelatedAllianceLeverManagerImpl(ProjectExpectedStudyRelatedAllianceLeverDAO projectExpectedStudyRelatedAllianceLeverDAO) {
    this.projectExpectedStudyRelatedAllianceLeverDAO = projectExpectedStudyRelatedAllianceLeverDAO;


  }

  @Override
  public void deleteProjectExpectedStudyRelatedAllianceLever(long projectExpectedStudyRelatedAllianceLeverId) {

    projectExpectedStudyRelatedAllianceLeverDAO.deleteProjectExpectedStudyRelatedAllianceLever(projectExpectedStudyRelatedAllianceLeverId);
  }

  @Override
  public boolean existProjectExpectedStudyRelatedAllianceLever(long projectExpectedStudyRelatedAllianceLeverID) {

    return projectExpectedStudyRelatedAllianceLeverDAO.existProjectExpectedStudyRelatedAllianceLever(projectExpectedStudyRelatedAllianceLeverID);
  }

  @Override
  public List<ProjectExpectedStudyRelatedAllianceLever> findAll() {

    return projectExpectedStudyRelatedAllianceLeverDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyRelatedAllianceLever getProjectExpectedStudyRelatedAllianceLeverById(long projectExpectedStudyRelatedAllianceLeverID) {

    return projectExpectedStudyRelatedAllianceLeverDAO.find(projectExpectedStudyRelatedAllianceLeverID);
  }

  @Override
  public ProjectExpectedStudyRelatedAllianceLever saveProjectExpectedStudyRelatedAllianceLever(ProjectExpectedStudyRelatedAllianceLever projectExpectedStudyRelatedAllianceLever) {

    return projectExpectedStudyRelatedAllianceLeverDAO.save(projectExpectedStudyRelatedAllianceLever);
  }


}
