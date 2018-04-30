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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudySrfTargetDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySrfTargetManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudySrfTargetManagerImpl implements ProjectExpectedStudySrfTargetManager {


  private ProjectExpectedStudySrfTargetDAO projectExpectedStudySrfTargetDAO;
  // Managers


  @Inject
  public ProjectExpectedStudySrfTargetManagerImpl(ProjectExpectedStudySrfTargetDAO projectExpectedStudySrfTargetDAO) {
    this.projectExpectedStudySrfTargetDAO = projectExpectedStudySrfTargetDAO;


  }

  @Override
  public void deleteProjectExpectedStudySrfTarget(long projectExpectedStudySrfTargetId) {

    projectExpectedStudySrfTargetDAO.deleteProjectExpectedStudySrfTarget(projectExpectedStudySrfTargetId);
  }

  @Override
  public boolean existProjectExpectedStudySrfTarget(long projectExpectedStudySrfTargetID) {

    return projectExpectedStudySrfTargetDAO.existProjectExpectedStudySrfTarget(projectExpectedStudySrfTargetID);
  }

  @Override
  public List<ProjectExpectedStudySrfTarget> findAll() {

    return projectExpectedStudySrfTargetDAO.findAll();

  }

  @Override
  public ProjectExpectedStudySrfTarget getProjectExpectedStudySrfTargetById(long projectExpectedStudySrfTargetID) {

    return projectExpectedStudySrfTargetDAO.find(projectExpectedStudySrfTargetID);
  }

  @Override
  public ProjectExpectedStudySrfTarget saveProjectExpectedStudySrfTarget(ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget) {

    return projectExpectedStudySrfTargetDAO.save(projectExpectedStudySrfTarget);
  }


}
