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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyGeographicScopeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyGeographicScopeMySQLDAO extends
  AbstractMarloDAO<ProjectExpectedStudyGeographicScope, Long> implements ProjectExpectedStudyGeographicScopeDAO {


  @Inject
  public ProjectExpectedStudyGeographicScopeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyGeographicScope(long projectExpectedStudyGeographicScopeId) {
    ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope =
      this.find(projectExpectedStudyGeographicScopeId);
    this.delete(projectExpectedStudyGeographicScope);
  }

  @Override
  public boolean existProjectExpectedStudyGeographicScope(long projectExpectedStudyGeographicScopeID) {
    ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope =
      this.find(projectExpectedStudyGeographicScopeID);
    if (projectExpectedStudyGeographicScope == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyGeographicScope find(long id) {
    return super.find(ProjectExpectedStudyGeographicScope.class, id);
  }

  @Override
  public List<ProjectExpectedStudyGeographicScope> findAll() {
    String query = "from " + ProjectExpectedStudyGeographicScope.class.getName();
    List<ProjectExpectedStudyGeographicScope> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyGeographicScope
    save(ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope) {
    if (projectExpectedStudyGeographicScope.getId() == null) {
      super.saveEntity(projectExpectedStudyGeographicScope);
    } else {
      projectExpectedStudyGeographicScope = super.update(projectExpectedStudyGeographicScope);
    }


    return projectExpectedStudyGeographicScope;
  }


}