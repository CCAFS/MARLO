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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyTagDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyTagManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyTag;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyTagManagerImpl implements ProjectExpectedStudyTagManager {


  private ProjectExpectedStudyTagDAO projectExpectedStudyTagDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyTagManagerImpl(ProjectExpectedStudyTagDAO projectExpectedStudyTagDAO) {
    this.projectExpectedStudyTagDAO = projectExpectedStudyTagDAO;


  }

  @Override
  public void deleteProjectExpectedStudyTag(long projectExpectedStudyTagId) {

    projectExpectedStudyTagDAO.deleteProjectExpectedStudyTag(projectExpectedStudyTagId);
  }

  @Override
  public boolean existProjectExpectedStudyTag(long projectExpectedStudyTagID) {

    return projectExpectedStudyTagDAO.existProjectExpectedStudyTag(projectExpectedStudyTagID);
  }

  @Override
  public List<ProjectExpectedStudyTag> findAll() {

    return projectExpectedStudyTagDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyTag getProjectExpectedStudyTagById(long projectExpectedStudyTagID) {

    return projectExpectedStudyTagDAO.find(projectExpectedStudyTagID);
  }

  @Override
  public ProjectExpectedStudyTag saveProjectExpectedStudyTag(ProjectExpectedStudyTag projectExpectedStudyTag) {

    return projectExpectedStudyTagDAO.save(projectExpectedStudyTag);
  }


}
