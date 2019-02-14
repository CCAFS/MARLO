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

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPolicyGeographicScopeMySQLDAO extends AbstractMarloDAO<ProjectPolicyGeographicScope, Long>
  implements ProjectPolicyGeographicScopeDAO {


  @Inject
  public ProjectPolicyGeographicScopeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPolicyGeographicScope(long projectPolicyGeographicScopeId) {
    ProjectPolicyGeographicScope projectPolicyGeographicScope = this.find(projectPolicyGeographicScopeId);
    this.delete(projectPolicyGeographicScope);
  }

  @Override
  public boolean existProjectPolicyGeographicScope(long projectPolicyGeographicScopeID) {
    ProjectPolicyGeographicScope projectPolicyGeographicScope = this.find(projectPolicyGeographicScopeID);
    if (projectPolicyGeographicScope == null) {
      return false;
    }
    return true;
  }

  @Override
  public ProjectPolicyGeographicScope find(long id) {
    return super.find(ProjectPolicyGeographicScope.class, id);

  }

  @Override
  public List<ProjectPolicyGeographicScope> findAll() {
    String query = "from " + ProjectPolicyGeographicScope.class.getName();
    List<ProjectPolicyGeographicScope> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectPolicyGeographicScope save(ProjectPolicyGeographicScope projectPolicyGeographicScope) {
    if (projectPolicyGeographicScope.getId() == null) {
      super.saveEntity(projectPolicyGeographicScope);
    } else {
      projectPolicyGeographicScope = super.update(projectPolicyGeographicScope);
    }


    return projectPolicyGeographicScope;
  }


}