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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationSubIdoDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSubIdo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationSubIdoMySQLDAO extends AbstractMarloDAO<ProjectInnovationSubIdo, Long> implements ProjectInnovationSubIdoDAO {


  @Inject
  public ProjectInnovationSubIdoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationSubIdo(long projectInnovationSubIdoId) {
    ProjectInnovationSubIdo projectInnovationSubIdo = this.find(projectInnovationSubIdoId);
    this.delete(projectInnovationSubIdo);
  }

  @Override
  public boolean existProjectInnovationSubIdo(long projectInnovationSubIdoID) {
    ProjectInnovationSubIdo projectInnovationSubIdo = this.find(projectInnovationSubIdoID);
    if (projectInnovationSubIdo == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationSubIdo find(long id) {
    return super.find(ProjectInnovationSubIdo.class, id);

  }

  @Override
  public List<ProjectInnovationSubIdo> findAll() {
    String query = "from " + ProjectInnovationSubIdo.class.getName();
    List<ProjectInnovationSubIdo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationSubIdo save(ProjectInnovationSubIdo projectInnovationSubIdo) {
    if (projectInnovationSubIdo.getId() == null) {
      super.saveEntity(projectInnovationSubIdo);
    } else {
      projectInnovationSubIdo = super.update(projectInnovationSubIdo);
    }


    return projectInnovationSubIdo;
  }


}