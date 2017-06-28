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


package org.cgiar.ccafs.marlo.data.dao.impl;

import org.cgiar.ccafs.marlo.data.dao.ICenterProjectDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProject;

import java.util.List;

import com.google.inject.Inject;

public class CenterProjectDAO implements ICenterProjectDAO {

  private StandardDAO dao;

  @Inject
  public CenterProjectDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteCenterProject(long projectId) {
    CenterProject project = this.find(projectId);
    project.setActive(false);
    return this.save(project) > 0;
  }

  @Override
  public boolean existCenterProject(long projectID) {
    CenterProject project = this.find(projectID);
    if (project == null) {
      return false;
    }
    return true;

  }

  @Override
  public CenterProject find(long id) {
    return dao.find(CenterProject.class, id);

  }

  @Override
  public List<CenterProject> findAll() {
    String query = "from " + CenterProject.class.getName();
    List<CenterProject> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<CenterProject> getCenterProjectsByUserId(long userId) {
    String query = "from " + CenterProject.class.getName() + " where user_id=" + userId;
    return dao.findAll(query);
  }

  @Override
  public long save(CenterProject project) {
    if (project.getId() == null) {
      dao.save(project);
    } else {
      dao.update(project);
    }
    return project.getId();
  }

  @Override
  public long save(CenterProject project, String actionName, List<String> relationsName) {
    if (project.getId() == null) {
      dao.save(project, actionName, relationsName);
    } else {
      dao.update(project, actionName, relationsName);
    }
    return project.getId();
  }


}