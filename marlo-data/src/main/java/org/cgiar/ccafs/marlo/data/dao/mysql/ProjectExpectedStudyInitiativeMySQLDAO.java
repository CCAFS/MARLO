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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyInitiativeDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInitiative;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyInitiativeMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyInitiative, Long>
  implements ProjectExpectedStudyInitiativeDAO {


  @Inject
  public ProjectExpectedStudyInitiativeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyInitiative(long projectExpectedStudyInitiativeId) {
    ProjectExpectedStudyInitiative projectExpectedStudyInitiative = this.find(projectExpectedStudyInitiativeId);
    this.delete(projectExpectedStudyInitiative);
  }

  @Override
  public boolean existProjectExpectedStudyInitiative(long projectExpectedStudyInitiativeID) {
    ProjectExpectedStudyInitiative projectExpectedStudyInitiative = this.find(projectExpectedStudyInitiativeID);
    if (projectExpectedStudyInitiative == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyInitiative find(long id) {
    return super.find(ProjectExpectedStudyInitiative.class, id);

  }

  @Override
  public List<ProjectExpectedStudyInitiative> findAll() {
    String query = "from " + ProjectExpectedStudyInitiative.class.getName();
    List<ProjectExpectedStudyInitiative> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectExpectedStudyInitiative> getAllStudyInitiativesByStudy(long studyId) {
    String query = "select pesinitiative from ProjectExpectedStudyInitiative pesinitiative "
      + "where pesinitiative.projectExpectedStudy.id = :studyId order by pesinitiative.phase.id";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("studyId", studyId);

    List<ProjectExpectedStudyInitiative> result = super.findAll(createQuery);

    return result;
  }

  @Override
  public ProjectExpectedStudyInitiative getProjectExpectedStudyInitiativeByPhase(Long expectedID, Long initiativeID,
    Long phaseID) {
    String query = "select pesinitiative from ProjectExpectedStudyInitiative pesinitiative "
      + "where pesinitiative.projectExpectedStudy.id = :studyId and pesinitiative.initiative.id = :initiativeId "
      + "and pesinitiative.phase.id = :phaseId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("studyId", expectedID);
    createQuery.setParameter("initiativeId", initiativeID);
    createQuery.setParameter("phaseId", phaseID);

    List<ProjectExpectedStudyInitiative> list = super.findAll(query);

    return (list.size() > 0) ? list.get(0) : null;
  }

  @Override
  public ProjectExpectedStudyInitiative save(ProjectExpectedStudyInitiative projectExpectedStudyInitiative) {
    if (projectExpectedStudyInitiative.getId() == null) {
      super.saveEntity(projectExpectedStudyInitiative);
    } else {
      projectExpectedStudyInitiative = super.update(projectExpectedStudyInitiative);
    }


    return projectExpectedStudyInitiative;
  }
}