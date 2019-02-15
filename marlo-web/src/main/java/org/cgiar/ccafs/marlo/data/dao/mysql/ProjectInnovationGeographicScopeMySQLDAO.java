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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationGeographicScopeMySQLDAO extends AbstractMarloDAO<ProjectInnovationGeographicScope, Long>
  implements ProjectInnovationGeographicScopeDAO {


  @Inject
  public ProjectInnovationGeographicScopeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationGeographicScope(long projectInnovationGeographicScopeId) {
    ProjectInnovationGeographicScope projectInnovationGeographicScope = this.find(projectInnovationGeographicScopeId);
    this.delete(projectInnovationGeographicScope);
  }

  @Override
  public boolean existProjectInnovationGeographicScope(long projectInnovationGeographicScopeID) {
    ProjectInnovationGeographicScope projectInnovationGeographicScope = this.find(projectInnovationGeographicScopeID);
    if (projectInnovationGeographicScope == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationGeographicScope find(long id) {
    return super.find(ProjectInnovationGeographicScope.class, id);

  }

  @Override
  public List<ProjectInnovationGeographicScope> findAll() {
    String query = "from " + ProjectInnovationGeographicScope.class.getName();
    List<ProjectInnovationGeographicScope> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationGeographicScope save(ProjectInnovationGeographicScope projectInnovationGeographicScope) {
    if (projectInnovationGeographicScope.getId() == null) {
      super.saveEntity(projectInnovationGeographicScope);
    } else {
      projectInnovationGeographicScope = super.update(projectInnovationGeographicScope);
    }


    return projectInnovationGeographicScope;
  }


}