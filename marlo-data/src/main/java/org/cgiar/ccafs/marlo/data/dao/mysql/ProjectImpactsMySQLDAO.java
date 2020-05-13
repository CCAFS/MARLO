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

import org.cgiar.ccafs.marlo.data.dao.ProjectImpactsDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectImpacts;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectImpactsMySQLDAO extends AbstractMarloDAO<ProjectImpacts, Long> implements ProjectImpactsDAO {


  @Inject
  public ProjectImpactsMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectImpacts(long projectImpactsId) {
    ProjectImpacts projectImpacts = this.find(projectImpactsId);
    projectImpacts.setActive(false);
    this.update(projectImpacts);
  }

  @Override
  public boolean existProjectImpacts(long projectImpactsID) {
    ProjectImpacts projectImpacts = this.find(projectImpactsID);
    if (projectImpacts == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectImpacts find(long id) {
    return super.find(ProjectImpacts.class, id);

  }

  @Override
  public List<ProjectImpacts> findAll() {
    String query = "from " + ProjectImpacts.class.getName() + " where is_active=1";
    List<ProjectImpacts> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectImpacts> findByProjectId(long projectId) {
    String query = "from " + ProjectImpacts.class.getName() + " where is_active=1 and project_id=" + projectId;
    List<ProjectImpacts> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public ProjectImpacts save(ProjectImpacts projectImpacts) {
    if (projectImpacts.getId() == null) {
      super.saveEntity(projectImpacts);
    } else {
      projectImpacts = super.update(projectImpacts);
    }


    return projectImpacts;
  }


}