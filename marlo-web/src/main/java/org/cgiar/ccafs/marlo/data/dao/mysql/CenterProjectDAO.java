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

import org.cgiar.ccafs.marlo.data.dao.ICenterProjectDAO;
import org.cgiar.ccafs.marlo.data.model.CenterProject;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class CenterProjectDAO extends AbstractMarloDAO<CenterProject, Long> implements ICenterProjectDAO {


  @Inject
  public CenterProjectDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteCenterProject(long projectId) {
    CenterProject project = this.find(projectId);
    project.setActive(false);
    this.save(project);
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
    return super.find(CenterProject.class, id);

  }

  @Override
  public List<CenterProject> findAll() {
    String query = "from " + CenterProject.class.getName();
    List<CenterProject> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public CenterProject getCenterProjectsByOcsCode(String ocsCode) {
    String query = "from " + CenterProject.class.getName() + " where ocs_code='" + ocsCode + "'";
    List<CenterProject> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public List<CenterProject> getCenterProjectsByUserId(long userId) {
    String query = "from " + CenterProject.class.getName() + " where user_id=" + userId;
    return super.findAll(query);
  }

  @Override
  public CenterProject save(CenterProject project) {
    if (project.getId() == null) {
      super.saveEntity(project);
    } else {
      project = super.update(project);
    }
    return project;
  }

  @Override
  public CenterProject save(CenterProject project, String actionName, List<String> relationsName) {
    if (project.getId() == null) {
      super.saveEntity(project, actionName, relationsName);
    } else {
      project = super.update(project, actionName, relationsName);
    }
    return project;
  }


}