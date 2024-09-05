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

import org.cgiar.ccafs.marlo.data.dao.ExpectedStudyProjectDAO;
import org.cgiar.ccafs.marlo.data.model.ExpectedStudyProject;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;


@Named
public class ExpectedStudyProjectMySQLDAO extends AbstractMarloDAO<ExpectedStudyProject, Long>
  implements ExpectedStudyProjectDAO {


  @Inject
  public ExpectedStudyProjectMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteExpectedStudyProject(long expectedStudyProjectId) {
    ExpectedStudyProject expectedStudyProject = this.find(expectedStudyProjectId);
    this.delete(expectedStudyProject);
  }

  @Override
  public boolean existExpectedStudyProject(long expectedStudyProjectID) {
    ExpectedStudyProject expectedStudyProject = this.find(expectedStudyProjectID);
    if (expectedStudyProject == null) {
      return false;
    }
    return true;

  }

  @Override
  public ExpectedStudyProject find(long id) {
    return super.find(ExpectedStudyProject.class, id);

  }

  @Override
  public List<ExpectedStudyProject> findAll() {
    String query = "from " + ExpectedStudyProject.class.getName();
    List<ExpectedStudyProject> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ExpectedStudyProject> getByProjectAndPhase(long projectId, long phaseId) {
    String query = "select distinct esp from ExpectedStudyProject esp "
      + "where project.id = :projectId and phase.id = :phaseId and is_active = 1";
    Query<ExpectedStudyProject> createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("projectId", projectId);
    createQuery.setParameter("phaseId", phaseId);
    List<ExpectedStudyProject> result = super.findAll(createQuery);

    if (result != null && !result.isEmpty()) {
      return result;
    }

    return null;
  }

  @Override
  public ExpectedStudyProject save(ExpectedStudyProject expectedStudyProject) {
    if (expectedStudyProject.getId() == null) {
      super.saveEntity(expectedStudyProject);
    } else {
      expectedStudyProject = super.update(expectedStudyProject);
    }


    return expectedStudyProject;
  }
}