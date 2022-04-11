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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyImpactAreaIndicatorDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyImpactAreaIndicator;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyImpactAreaIndicatorMySQLDAO
  extends AbstractMarloDAO<ProjectExpectedStudyImpactAreaIndicator, Long>
  implements ProjectExpectedStudyImpactAreaIndicatorDAO {


  @Inject
  public ProjectExpectedStudyImpactAreaIndicatorMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyImpactAreaIndicator(long projectExpectedStudyImpactAreaIndicatorId) {
    ProjectExpectedStudyImpactAreaIndicator projectExpectedStudyImpactAreaIndicator =
      this.find(projectExpectedStudyImpactAreaIndicatorId);
    this.delete(projectExpectedStudyImpactAreaIndicator);
  }

  @Override
  public boolean existProjectExpectedStudyImpactAreaIndicator(long projectExpectedStudyImpactAreaIndicatorID) {
    ProjectExpectedStudyImpactAreaIndicator projectExpectedStudyImpactAreaIndicator =
      this.find(projectExpectedStudyImpactAreaIndicatorID);
    if (projectExpectedStudyImpactAreaIndicator == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyImpactAreaIndicator find(long id) {
    return super.find(ProjectExpectedStudyImpactAreaIndicator.class, id);

  }

  @Override
  public List<ProjectExpectedStudyImpactAreaIndicator> findAll() {
    String query = "from " + ProjectExpectedStudyImpactAreaIndicator.class.getName();
    List<ProjectExpectedStudyImpactAreaIndicator> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectExpectedStudyImpactAreaIndicator> getAllStudyImpactAreaIndicatorsByStudy(long studyId) {
    String query = "select pesimpactAreaIndicator from ProjectExpectedStudyImpactAreaIndicator pesimpactAreaIndicator "
      + "where pesimpactAreaIndicator.projectExpectedStudy.id = :studyId order by pesimpactAreaIndicator.phase.id";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("studyId", studyId);

    List<ProjectExpectedStudyImpactAreaIndicator> result = super.findAll(createQuery);

    return result;
  }

  @Override
  public ProjectExpectedStudyImpactAreaIndicator getProjectExpectedStudyImpactAreaIndicatorByPhase(Long expectedID,
    Long impactAreaIndicatorID, Long phaseID) {
    String query = "select pesimpactAreaIndicator from ProjectExpectedStudyImpactAreaIndicator pesimpactAreaIndicator "
      + "where pesimpactAreaIndicator.projectExpectedStudy.id = :studyId "
      + "and pesimpactAreaIndicator.impactAreaIndicator.id = :impactAreaIndicatorId and pesimpactAreaIndicator.phase.id = :phaseId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("studyId", expectedID);
    createQuery.setParameter("impactAreaIndicatorId", impactAreaIndicatorID);
    createQuery.setParameter("phaseId", phaseID);

    List<ProjectExpectedStudyImpactAreaIndicator> list = super.findAll(query);

    return (list.size() > 0) ? list.get(0) : null;
  }

  @Override
  public ProjectExpectedStudyImpactAreaIndicator
    save(ProjectExpectedStudyImpactAreaIndicator projectExpectedStudyImpactAreaIndicator) {
    if (projectExpectedStudyImpactAreaIndicator.getId() == null) {
      super.saveEntity(projectExpectedStudyImpactAreaIndicator);
    } else {
      projectExpectedStudyImpactAreaIndicator = super.update(projectExpectedStudyImpactAreaIndicator);
    }


    return projectExpectedStudyImpactAreaIndicator;
  }
}