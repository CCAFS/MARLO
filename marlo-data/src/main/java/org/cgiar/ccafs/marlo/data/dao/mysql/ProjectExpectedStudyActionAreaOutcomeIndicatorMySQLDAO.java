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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyActionAreaOutcomeIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyActionAreaOutcomeIndicator;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyActionAreaOutcomeIndicatorMySQLDAO
  extends AbstractMarloDAO<ProjectExpectedStudyActionAreaOutcomeIndicator, Long>
  implements ProjectExpectedStudyActionAreaOutcomeIndicatorDAO {


  @Inject
  public ProjectExpectedStudyActionAreaOutcomeIndicatorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void
    deleteProjectExpectedStudyActionAreaOutcomeIndicator(long projectExpectedStudyActionAreaOutcomeIndicatorId) {
    ProjectExpectedStudyActionAreaOutcomeIndicator projectExpectedStudyActionAreaOutcomeIndicator =
      this.find(projectExpectedStudyActionAreaOutcomeIndicatorId);
    this.delete(projectExpectedStudyActionAreaOutcomeIndicator);
  }

  @Override
  public boolean
    existProjectExpectedStudyActionAreaOutcomeIndicator(long projectExpectedStudyActionAreaOutcomeIndicatorID) {
    ProjectExpectedStudyActionAreaOutcomeIndicator projectExpectedStudyActionAreaOutcomeIndicator =
      this.find(projectExpectedStudyActionAreaOutcomeIndicatorID);
    if (projectExpectedStudyActionAreaOutcomeIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyActionAreaOutcomeIndicator find(long id) {
    return super.find(ProjectExpectedStudyActionAreaOutcomeIndicator.class, id);

  }

  @Override
  public List<ProjectExpectedStudyActionAreaOutcomeIndicator> findAll() {
    String query = "from " + ProjectExpectedStudyActionAreaOutcomeIndicator.class.getName();
    List<ProjectExpectedStudyActionAreaOutcomeIndicator> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectExpectedStudyActionAreaOutcomeIndicator>
    getAllStudyActionAreaOutcomeIndicatorsByStudy(long studyId) {
    String query = "select pesactionAreaOutcomeIndicator from ProjectExpectedStudyActionAreaOutcomeIndicator "
      + "pesactionAreaOutcomeIndicator where pesactionAreaOutcomeIndicator.projectExpectedStudy.id = :studyId "
      + "order by pesactionAreaOutcomeIndicator.phase.id";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("studyId", studyId);

    List<ProjectExpectedStudyActionAreaOutcomeIndicator> result = super.findAll(createQuery);

    return result;
  }

  @Override
  public ProjectExpectedStudyActionAreaOutcomeIndicator getProjectExpectedStudyActionAreaOutcomeIndicatorByPhase(
    Long expectedID, Long actionAreaOutcomeIndicatorID, Long phaseID) {
    String query = "select pesactionAreaOutcomeIndicator from ProjectExpectedStudyActionAreaOutcomeIndicator "
      + "pesactionAreaOutcomeIndicator where pesactionAreaOutcomeIndicator.projectExpectedStudy.id = :studyId "
      + "and pesactionAreaOutcomeIndicator.outcomeIndicator.id = :outcomeIndicatorId and pesactionAreaOutcomeIndicator.phase.id = :phaseId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("studyId", expectedID);
    createQuery.setParameter("outcomeIndicatorId", actionAreaOutcomeIndicatorID);
    createQuery.setParameter("phaseId", phaseID);

    List<ProjectExpectedStudyActionAreaOutcomeIndicator> list = super.findAll(query);

    return (list.size() > 0) ? list.get(0) : null;
  }

  @Override
  public ProjectExpectedStudyActionAreaOutcomeIndicator
    save(ProjectExpectedStudyActionAreaOutcomeIndicator projectExpectedStudyActionAreaOutcomeIndicator) {
    if (projectExpectedStudyActionAreaOutcomeIndicator.getId() == null) {
      super.saveEntity(projectExpectedStudyActionAreaOutcomeIndicator);
    } else {
      projectExpectedStudyActionAreaOutcomeIndicator = super.update(projectExpectedStudyActionAreaOutcomeIndicator);
    }


    return projectExpectedStudyActionAreaOutcomeIndicator;
  }
}