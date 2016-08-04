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

import org.cgiar.ccafs.marlo.data.dao.ProjectDAO;
import org.cgiar.ccafs.marlo.data.model.Project;

import java.util.List;
import java.util.Map;

import com.google.inject.Inject;

public class ProjectMySQLDAO implements ProjectDAO {

  private StandardDAO dao;

  @Inject
  public ProjectMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProject(long projectId) {
    Project project = this.find(projectId);
    project.setActive(false);
    return this.save(project) > 0;
  }

  @Override
  public boolean existProject(long projectID) {
    Project project = this.find(projectID);
    if (project == null) {
      return false;
    }
    return true;

  }

  @Override
  public Project find(long id) {
    return dao.find(Project.class, id);

  }

  @Override
  public List<Project> findAll() {
    String query = "from " + Project.class.getName() + " where is_active=1";
    List<Project> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<Map<String, Object>> getUserProjects(long userId, String crp) {
    String query = "select DISTINCT project_id from user_permissions where id=" + userId + " and crp_acronym='" + crp
      + "' and project_id is not null";
    return dao.findCustomQuery(query);
  }

  @Override
  public long save(Project project) {
    if (project.getId() == null) {
      dao.save(project);
    } else {
      dao.update(project);
    }


    return project.getId();
  }

  @Override
  public long save(Project project, String sectionName, List<String> relationsName) {
    if (project.getId() == null) {
      dao.save(project, sectionName, relationsName);
    } else {
      dao.update(project, sectionName, relationsName);
    }


    return project.getId();
  }


}