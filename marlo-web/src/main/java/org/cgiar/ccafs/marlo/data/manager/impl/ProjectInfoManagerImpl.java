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


import org.cgiar.ccafs.marlo.data.dao.ProjectInfoDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;

import java.util.List;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectInfoManagerImpl implements ProjectInfoManager {


  private ProjectInfoDAO projectInfoDAO;
  // Managers


  @Inject
  public ProjectInfoManagerImpl(ProjectInfoDAO projectInfoDAO) {
    this.projectInfoDAO = projectInfoDAO;


  }

  @Override
  public boolean deleteProjectInfo(long projectInfoId) {

    return projectInfoDAO.deleteProjectInfo(projectInfoId);
  }

  @Override
  public boolean existProjectInfo(long projectInfoID) {

    return projectInfoDAO.existProjectInfo(projectInfoID);
  }

  @Override
  public List<ProjectInfo> findAll() {

    return projectInfoDAO.findAll();

  }

  @Override
  public ProjectInfo getProjectInfoById(long projectInfoID) {

    return projectInfoDAO.find(projectInfoID);
  }

  @Override
  public long saveProjectInfo(ProjectInfo projectInfo) {

    return projectInfoDAO.save(projectInfo);
  }


}
