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


import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyInfoDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyInfoManagerImpl implements ProjectExpectedStudyInfoManager {


  private ProjectExpectedStudyInfoDAO projectExpectedStudyInfoDAO;
  // Managers


  @Inject
  public ProjectExpectedStudyInfoManagerImpl(ProjectExpectedStudyInfoDAO projectExpectedStudyInfoDAO) {
    this.projectExpectedStudyInfoDAO = projectExpectedStudyInfoDAO;


  }

  @Override
  public void deleteProjectExpectedStudyInfo(long projectExpectedStudyInfoId) {

    projectExpectedStudyInfoDAO.deleteProjectExpectedStudyInfo(projectExpectedStudyInfoId);
  }

  @Override
  public boolean existProjectExpectedStudyInfo(long projectExpectedStudyInfoID) {

    return projectExpectedStudyInfoDAO.existProjectExpectedStudyInfo(projectExpectedStudyInfoID);
  }

  @Override
  public List<ProjectExpectedStudyInfo> findAll() {

    return projectExpectedStudyInfoDAO.findAll();

  }

  @Override
  public ProjectExpectedStudyInfo getProjectExpectedStudyInfoById(long projectExpectedStudyInfoID) {

    return projectExpectedStudyInfoDAO.find(projectExpectedStudyInfoID);
  }

  @Override
  public ProjectExpectedStudyInfo saveProjectExpectedStudyInfo(ProjectExpectedStudyInfo projectExpectedStudyInfo) {

    return projectExpectedStudyInfoDAO.save(projectExpectedStudyInfo);
  }


}
