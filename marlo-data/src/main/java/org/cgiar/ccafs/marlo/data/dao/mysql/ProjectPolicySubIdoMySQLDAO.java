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

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicySubIdoDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ProjectPolicySubIdoMySQLDAO extends AbstractMarloDAO<ProjectPolicySubIdo, Long>
  implements ProjectPolicySubIdoDAO {


  @Inject
  public ProjectPolicySubIdoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPolicySubIdo(long projectPolicySubIdoId) {
    ProjectPolicySubIdo projectPolicySubIdo = this.find(projectPolicySubIdoId);
    this.delete(projectPolicySubIdo);
  }

  @Override
  public boolean existProjectPolicySubIdo(long projectPolicySubIdoID) {
    ProjectPolicySubIdo projectPolicySubIdo = this.find(projectPolicySubIdoID);
    if (projectPolicySubIdo == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPolicySubIdo find(long id) {
    return super.find(ProjectPolicySubIdo.class, id);

  }

  @Override
  public List<ProjectPolicySubIdo> findAll() {
    String query = "from " + ProjectPolicySubIdo.class.getName();
    List<ProjectPolicySubIdo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectPolicySubIdo> getAllPolicySubIdosByPolicy(long policyId) {
    String query =
      "select ppsi from ProjectPolicySubIdo ppsi where ppsi.projectPolicy.id = :policyId order by ppsi.phase.id";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("policyId", policyId);

    List<ProjectPolicySubIdo> result = super.findAll(createQuery);

    return result;
  }

  @Override
  public ProjectPolicySubIdo getProjectPolicySubIdoByPhase(long projectPolicyID, long subIdoID, long phaseID) {
    String query = "from " + ProjectPolicySubIdo.class.getName() + " WHERE project_policy_id=" + projectPolicyID
      + " AND sub_ido_id=" + subIdoID + " AND id_phase=" + phaseID;
    List<ProjectPolicySubIdo> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public ProjectPolicySubIdo save(ProjectPolicySubIdo projectPolicySubIdo) {
    if (projectPolicySubIdo.getId() == null) {
      super.saveEntity(projectPolicySubIdo);
    } else {
      projectPolicySubIdo = super.update(projectPolicySubIdo);
    }


    return projectPolicySubIdo;
  }
}