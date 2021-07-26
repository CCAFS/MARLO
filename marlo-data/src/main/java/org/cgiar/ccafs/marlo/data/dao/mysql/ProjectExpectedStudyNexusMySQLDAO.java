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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyNexusDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyNexus;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyNexusMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyNexus, Long>
  implements ProjectExpectedStudyNexusDAO {


  @Inject
  public ProjectExpectedStudyNexusMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyNexus(long projectExpectedStudyNexusId) {
    ProjectExpectedStudyNexus projectExpectedStudyNexus = this.find(projectExpectedStudyNexusId);
    this.delete(projectExpectedStudyNexus);
  }

  @Override
  public boolean existProjectExpectedStudyNexus(long projectExpectedStudyNexusID) {
    ProjectExpectedStudyNexus projectExpectedStudyNexus = this.find(projectExpectedStudyNexusID);
    if (projectExpectedStudyNexus == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyNexus find(long id) {
    return super.find(ProjectExpectedStudyNexus.class, id);

  }

  @Override
  public List<ProjectExpectedStudyNexus> findAll() {
    String query = "from " + ProjectExpectedStudyNexus.class.getName();
    List<ProjectExpectedStudyNexus> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectExpectedStudyNexus> getAllStudyNexussByStudy(long studyId) {
    String query =
      "select pesn from ProjectExpectedStudyNexus pesn where pesn.projectExpectedStudy.id = :studyId order by pesn.phase.id";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("studyId", studyId);

    List<ProjectExpectedStudyNexus> result = super.findAll(createQuery);

    return result;
  }

  @Override
  public ProjectExpectedStudyNexus getStudyNexusByStudyNexusAndPhase(long studyId, long nexusId, long idPhase) {
    String query = "select distinct pp from ProjectExpectedStudyNexus pp "
      + "where pp.projectExpectedStudy.id = :studyId and pp.phase.id = :idPhase and pp.nexus.id = :nexusId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("studyId", studyId);
    createQuery.setParameter("idPhase", idPhase);
    createQuery.setParameter("nexusId", nexusId);

    Object findSingleResult = super.findSingleResult(ProjectExpectedStudyNexus.class, createQuery);
    ProjectExpectedStudyNexus projectExpectedStudyNexus = (ProjectExpectedStudyNexus) findSingleResult;

    return projectExpectedStudyNexus;
  }

  @Override
  public ProjectExpectedStudyNexus save(ProjectExpectedStudyNexus projectExpectedStudyNexus) {
    if (projectExpectedStudyNexus.getId() == null) {
      super.saveEntity(projectExpectedStudyNexus);
    } else {
      projectExpectedStudyNexus = super.update(projectExpectedStudyNexus);
    }


    return projectExpectedStudyNexus;
  }
}