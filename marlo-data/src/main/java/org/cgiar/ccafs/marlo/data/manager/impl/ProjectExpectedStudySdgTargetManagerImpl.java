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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudySdgTargetDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySdgTargetManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySdgTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudySdgTargetManagerImpl implements ProjectExpectedStudySdgTargetManager {


  private ProjectExpectedStudySdgTargetDAO projectExpectedStudySdgTargetDAO;
  // Managers


  @Inject
  public ProjectExpectedStudySdgTargetManagerImpl(ProjectExpectedStudySdgTargetDAO projectExpectedStudySdgTargetDAO) {
    this.projectExpectedStudySdgTargetDAO = projectExpectedStudySdgTargetDAO;


  }

  @Override
  public void deleteProjectExpectedStudySdgTarget(long projectExpectedStudySdgTargetId) {

    projectExpectedStudySdgTargetDAO.deleteProjectExpectedStudySdgTarget(projectExpectedStudySdgTargetId);
  }

  @Override
  public boolean existProjectExpectedStudySdgTarget(long projectExpectedStudySdgTargetID) {

    return projectExpectedStudySdgTargetDAO.existProjectExpectedStudySdgTarget(projectExpectedStudySdgTargetID);
  }

  @Override
  public List<ProjectExpectedStudySdgTarget> findAll() {

    return projectExpectedStudySdgTargetDAO.findAll();

  }

  @Override
  public List<ProjectExpectedStudySdgTarget> getAllStudySdgTargetsByStudy(Long studyId) {
    return this.projectExpectedStudySdgTargetDAO.getAllStudySdgTargetsByStudy(studyId.longValue());
  }

  @Override
  public ProjectExpectedStudySdgTarget getProjectExpectedStudySdgTargetById(long projectExpectedStudySdgTargetID) {

    return projectExpectedStudySdgTargetDAO.find(projectExpectedStudySdgTargetID);
  }

  @Override
  public ProjectExpectedStudySdgTarget
    saveProjectExpectedStudySdgTarget(ProjectExpectedStudySdgTarget projectExpectedStudySdgTarget) {

    return projectExpectedStudySdgTargetDAO.save(projectExpectedStudySdgTarget);
  }
}
