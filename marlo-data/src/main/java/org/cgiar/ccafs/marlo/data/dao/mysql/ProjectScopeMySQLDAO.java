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

import org.cgiar.ccafs.marlo.data.dao.ProjectScopeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectScope;

import java.util.List;

import com.google.inject.Inject;

public class ProjectScopeMySQLDAO implements ProjectScopeDAO {

  private StandardDAO dao;

  @Inject
  public ProjectScopeMySQLDAO(StandardDAO dao) {
    this.dao = dao;
  }

  @Override
  public boolean deleteProjectScope(long projectScopeId) {
    ProjectScope projectScope = this.find(projectScopeId);
    projectScope.setActive(false);
    return this.save(projectScope) > 0;
  }

  @Override
  public boolean existProjectScope(long projectScopeID) {
    ProjectScope projectScope = this.find(projectScopeID);
    if (projectScope == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectScope find(long id) {
    return dao.find(ProjectScope.class, id);

  }

  @Override
  public List<ProjectScope> findAll() {
    String query = "from " + ProjectScope.class.getName() + " where is_active=1";
    List<ProjectScope> list = dao.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectScope projectScope) {
    if (projectScope.getId() == null) {
      dao.save(projectScope);
    } else {
      dao.update(projectScope);
    }


    return projectScope.getId();
  }


}