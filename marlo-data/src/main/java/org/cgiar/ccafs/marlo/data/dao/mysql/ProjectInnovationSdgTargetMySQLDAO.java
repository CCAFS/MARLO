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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationSdgTargetDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSdgTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationSdgTargetMySQLDAO extends AbstractMarloDAO<ProjectInnovationSdgTarget, Long>
  implements ProjectInnovationSdgTargetDAO {


  @Inject
  public ProjectInnovationSdgTargetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationSdgTarget(long projectInnovationSdgTargetId) {
    ProjectInnovationSdgTarget projectInnovationSdgTarget = this.find(projectInnovationSdgTargetId);
    this.delete(projectInnovationSdgTarget);
  }

  @Override
  public boolean existProjectInnovationSdgTarget(long projectInnovationSdgTargetID) {
    ProjectInnovationSdgTarget projectInnovationSdgTarget = this.find(projectInnovationSdgTargetID);
    if (projectInnovationSdgTarget == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationSdgTarget find(long id) {
    return super.find(ProjectInnovationSdgTarget.class, id);

  }

  @Override
  public List<ProjectInnovationSdgTarget> findAll() {
    String query = "from " + ProjectInnovationSdgTarget.class.getName();
    List<ProjectInnovationSdgTarget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectInnovationSdgTarget> getAllInnovationSdgTargetsByInnovation(long innovationId) {
    String query =
      "select pisdg from ProjectInnovationSdgTarget pisdg where pisdg.projectInnovation.id = :innovationId order by pisdg.phase.id";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("innovationId", innovationId);

    List<ProjectInnovationSdgTarget> result = super.findAll(createQuery);

    return result;
  }

  @Override
  public ProjectInnovationSdgTarget getInnovationSdgTargetByInnovationSdgTargetAndPhase(long innovationId,
    long sdgTargetId, long idPhase) {
    String query = "select distinct pp from ProjectInnovationSdgTarget pp "
      + "where pp.projectInnovation.id = :innovationId and pp.phase.id = :idPhase and pp.sdgTarget.id = :sdgTargetId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("innovationId", innovationId);
    createQuery.setParameter("idPhase", idPhase);
    createQuery.setParameter("sdgTargetId", sdgTargetId);

    Object findSingleResult = super.findSingleResult(ProjectInnovationSdgTarget.class, createQuery);
    ProjectInnovationSdgTarget projectInnovationSdgTarget = (ProjectInnovationSdgTarget) findSingleResult;

    return projectInnovationSdgTarget;
  }

  @Override
  public ProjectInnovationSdgTarget save(ProjectInnovationSdgTarget projectInnovationSdgTarget) {
    if (projectInnovationSdgTarget.getId() == null) {
      super.saveEntity(projectInnovationSdgTarget);
    } else {
      projectInnovationSdgTarget = super.update(projectInnovationSdgTarget);
    }


    return projectInnovationSdgTarget;
  }
}