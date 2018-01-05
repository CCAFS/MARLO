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


import org.cgiar.ccafs.marlo.data.dao.ProjectClusterActivityDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectClusterActivityManager;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectClusterActivityManagerImpl implements ProjectClusterActivityManager {


  private ProjectClusterActivityDAO projectClusterActivityDAO;
  // Managers


  @Inject
  public ProjectClusterActivityManagerImpl(ProjectClusterActivityDAO projectClusterActivityDAO) {
    this.projectClusterActivityDAO = projectClusterActivityDAO;


  }

  @Override
  public void deleteProjectClusterActivity(long projectClusterActivityId) {

    projectClusterActivityDAO.deleteProjectClusterActivity(projectClusterActivityId);
  }

  @Override
  public boolean existProjectClusterActivity(long projectClusterActivityID) {

    return projectClusterActivityDAO.existProjectClusterActivity(projectClusterActivityID);
  }

  @Override
  public List<ProjectClusterActivity> findAll() {

    return projectClusterActivityDAO.findAll();

  }

  @Override
  public ProjectClusterActivity getProjectClusterActivityById(long projectClusterActivityID) {

    return projectClusterActivityDAO.find(projectClusterActivityID);
  }

  @Override
  public ProjectClusterActivity saveProjectClusterActivity(ProjectClusterActivity projectClusterActivity) {

    return projectClusterActivityDAO.save(projectClusterActivity);
  }


}
