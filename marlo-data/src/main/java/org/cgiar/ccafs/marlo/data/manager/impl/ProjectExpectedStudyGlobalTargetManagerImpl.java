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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyGlobalTargetDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyGlobalTargetManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGlobalTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyGlobalTargetManagerImpl implements ProjectExpectedStudyGlobalTargetManager {


  private ProjectExpectedStudyGlobalTargetDAO projectExpectedStudyGlobalTargetDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyGlobalTargetManagerImpl(ProjectExpectedStudyGlobalTargetDAO projectExpectedStudyGlobalTargetDAO) {
    this.projectExpectedStudyGlobalTargetDAO = projectExpectedStudyGlobalTargetDAO;


  }

  @Override
  public void deleteProjectExpectedStudyGlobalTarget(long projectExpectedStudyGlobalTargetId) {

    projectExpectedStudyGlobalTargetDAO.deleteProjectExpectedStudyGlobalTarget(projectExpectedStudyGlobalTargetId);
  }

  @Override
  public boolean existProjectExpectedStudyGlobalTarget(long projectExpectedStudyGlobalTargetID) {

    return projectExpectedStudyGlobalTargetDAO.existProjectExpectedStudyGlobalTarget(projectExpectedStudyGlobalTargetID);
  }

  @Override
  public List<ProjectExpectedStudyGlobalTarget> findAll() {

    return projectExpectedStudyGlobalTargetDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyGlobalTarget getProjectExpectedStudyGlobalTargetById(long projectExpectedStudyGlobalTargetID) {

    return projectExpectedStudyGlobalTargetDAO.find(projectExpectedStudyGlobalTargetID);
  }

  @Override
  public ProjectExpectedStudyGlobalTarget saveProjectExpectedStudyGlobalTarget(ProjectExpectedStudyGlobalTarget projectExpectedStudyGlobalTarget) {

    return projectExpectedStudyGlobalTargetDAO.save(projectExpectedStudyGlobalTarget);
  }


}
