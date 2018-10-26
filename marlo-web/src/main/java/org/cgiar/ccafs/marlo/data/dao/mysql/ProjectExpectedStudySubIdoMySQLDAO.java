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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudySubIdoDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudySubIdoMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudySubIdo, Long>
  implements ProjectExpectedStudySubIdoDAO {


  @Inject
  public ProjectExpectedStudySubIdoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudySubIdo(long projectExpectedStudySubIdoId) {
    ProjectExpectedStudySubIdo projectExpectedStudySubIdo = this.find(projectExpectedStudySubIdoId);
    this.delete(projectExpectedStudySubIdo);
  }

  @Override
  public boolean existProjectExpectedStudySubIdo(long projectExpectedStudySubIdoID) {
    ProjectExpectedStudySubIdo projectExpectedStudySubIdo = this.find(projectExpectedStudySubIdoID);
    if (projectExpectedStudySubIdo == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudySubIdo find(long id) {
    return super.find(ProjectExpectedStudySubIdo.class, id);

  }

  @Override
  public List<ProjectExpectedStudySubIdo> findAll() {
    String query = "from " + ProjectExpectedStudySubIdo.class.getName();
    List<ProjectExpectedStudySubIdo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudySubIdo save(ProjectExpectedStudySubIdo projectExpectedStudySubIdo) {
    if (projectExpectedStudySubIdo.getId() == null) {
      super.saveEntity(projectExpectedStudySubIdo);
    } else {
      projectExpectedStudySubIdo = super.update(projectExpectedStudySubIdo);
    }


    return projectExpectedStudySubIdo;
  }


}