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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ProjectClusterActivityDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectClusterActivity;

import java.util.List;

import com.google.inject.Inject;

public class ProjectClusterActivityMySQLDAO implements ProjectClusterActivityDAO {

  private StandardDAO dao;

  @Inject
  public ProjectClusterActivityMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectClusterActivity(long projectClusterActivityId) {
    ProjectClusterActivity projectClusterActivity = this.find(projectClusterActivityId);
    projectClusterActivity.setActive(false);
    return this.save(projectClusterActivity) > 0;
  }

  @Override
  public boolean existProjectClusterActivity(long projectClusterActivityID) {
    ProjectClusterActivity projectClusterActivity = this.find(projectClusterActivityID);
    if (projectClusterActivity == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectClusterActivity find(long id) {
    return dao.find(ProjectClusterActivity.class, id);

  }

  @Override
  public List<ProjectClusterActivity> findAll() {
    String query = "from " + ProjectClusterActivity.class.getName() + " where is_active=1";
    List<ProjectClusterActivity> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectClusterActivity projectClusterActivity) {
    if (projectClusterActivity.getId() == null) {
      dao.save(projectClusterActivity);
    } else {
      dao.update(projectClusterActivity);
    }


    return projectClusterActivity.getId();
  }


}