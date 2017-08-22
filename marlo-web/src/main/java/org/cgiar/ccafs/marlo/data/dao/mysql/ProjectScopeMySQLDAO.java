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
import org.hibernate.SessionFactory;

public class ProjectScopeMySQLDAO extends AbstractMarloDAO<ProjectScope, Long> implements ProjectScopeDAO {


  @Inject
  public ProjectScopeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectScope(long projectScopeId) {
    ProjectScope projectScope = this.find(projectScopeId);
    projectScope.setActive(false);
    this.save(projectScope);
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
    return super.find(ProjectScope.class, id);

  }

  @Override
  public List<ProjectScope> findAll() {
    String query = "from " + ProjectScope.class.getName() + " where is_active=1";
    List<ProjectScope> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectScope save(ProjectScope projectScope) {
    if (projectScope.getId() == null) {
      super.saveEntity(projectScope);
    } else {
      projectScope = super.update(projectScope);
    }


    return projectScope;
  }


}