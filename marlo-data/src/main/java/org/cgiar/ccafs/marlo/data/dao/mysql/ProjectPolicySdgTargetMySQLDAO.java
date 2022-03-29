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

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicySdgTargetDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySdgTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ProjectPolicySdgTargetMySQLDAO extends AbstractMarloDAO<ProjectPolicySdgTarget, Long>
  implements ProjectPolicySdgTargetDAO {


  @Inject
  public ProjectPolicySdgTargetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPolicySdgTarget(long projectPolicySdgTargetId) {
    ProjectPolicySdgTarget projectPolicySdgTarget = this.find(projectPolicySdgTargetId);
    this.delete(projectPolicySdgTarget);
  }

  @Override
  public boolean existProjectPolicySdgTarget(long projectPolicySdgTargetID) {
    ProjectPolicySdgTarget projectPolicySdgTarget = this.find(projectPolicySdgTargetID);
    if (projectPolicySdgTarget == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPolicySdgTarget find(long id) {
    return super.find(ProjectPolicySdgTarget.class, id);

  }

  @Override
  public List<ProjectPolicySdgTarget> findAll() {
    String query = "from " + ProjectPolicySdgTarget.class.getName();
    List<ProjectPolicySdgTarget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectPolicySdgTarget> getAllPolicySdgTargetsByPolicy(long policyId) {
    String query =
      "select ppsdg from ProjectPolicySdgTarget ppsdg where ppsdg.projectPolicy.id = :policyId order by ppsdg.phase.id";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("policyId", policyId);

    List<ProjectPolicySdgTarget> result = super.findAll(createQuery);

    return result;
  }

  @Override
  public ProjectPolicySdgTarget getPolicySdgTargetByPolicySdgTargetAndPhase(long policyId, long sdgTargetId,
    long idPhase) {
    String query = "select distinct pp from ProjectPolicySdgTarget pp "
      + "where pp.projectPolicy.id = :policyId and pp.phase.id = :idPhase and pp.sdgTarget.id = :sdgTargetId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("policyId", policyId);
    createQuery.setParameter("idPhase", idPhase);
    createQuery.setParameter("sdgTargetId", sdgTargetId);

    Object findSingleResult = super.findSingleResult(ProjectPolicySdgTarget.class, createQuery);
    ProjectPolicySdgTarget projectPolicySdgTarget = (ProjectPolicySdgTarget) findSingleResult;

    return projectPolicySdgTarget;
  }

  @Override
  public ProjectPolicySdgTarget save(ProjectPolicySdgTarget projectPolicySdgTarget) {
    if (projectPolicySdgTarget.getId() == null) {
      super.saveEntity(projectPolicySdgTarget);
    } else {
      projectPolicySdgTarget = super.update(projectPolicySdgTarget);
    }


    return projectPolicySdgTarget;
  }
}