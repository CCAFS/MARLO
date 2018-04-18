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


import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationInfoDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectInnovationInfoManagerImpl implements ProjectInnovationInfoManager {


  private ProjectInnovationInfoDAO projectInnovationInfoDAO;
  // Managers


  @Inject
  public ProjectInnovationInfoManagerImpl(ProjectInnovationInfoDAO projectInnovationInfoDAO) {
    this.projectInnovationInfoDAO = projectInnovationInfoDAO;


  }

  @Override
  public void deleteProjectInnovationInfo(long projectInnovationInfoId) {

    projectInnovationInfoDAO.deleteProjectInnovationInfo(projectInnovationInfoId);
  }

  @Override
  public boolean existProjectInnovationInfo(long projectInnovationInfoID) {

    return projectInnovationInfoDAO.existProjectInnovationInfo(projectInnovationInfoID);
  }

  @Override
  public List<ProjectInnovationInfo> findAll() {

    return projectInnovationInfoDAO.findAll();

  }

  @Override
  public ProjectInnovationInfo getProjectInnovationInfoById(long projectInnovationInfoID) {

    return projectInnovationInfoDAO.find(projectInnovationInfoID);
  }

  @Override
  public ProjectInnovationInfo saveProjectInnovationInfo(ProjectInnovationInfo projectInnovationInfo) {

    return projectInnovationInfoDAO.save(projectInnovationInfo);
  }


}
