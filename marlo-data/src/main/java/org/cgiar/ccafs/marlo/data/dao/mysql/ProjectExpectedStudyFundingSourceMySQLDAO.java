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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyFundingSourceDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFundingSource;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyFundingSourceMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyFundingSource, Long>
  implements ProjectExpectedStudyFundingSourceDAO {


  @Inject
  public ProjectExpectedStudyFundingSourceMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyFundingSource(long projectExpectedStudyFundingSourceId) {
    ProjectExpectedStudyFundingSource projectExpectedStudyFundingSource =
      this.find(projectExpectedStudyFundingSourceId);
    this.delete(projectExpectedStudyFundingSource);
  }

  @Override
  public boolean existProjectExpectedStudyFundingSource(long projectExpectedStudyFundingSourceID) {
    ProjectExpectedStudyFundingSource projectExpectedStudyFundingSource =
      this.find(projectExpectedStudyFundingSourceID);
    if (projectExpectedStudyFundingSource == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyFundingSource find(long id) {
    return super.find(ProjectExpectedStudyFundingSource.class, id);

  }

  @Override
  public List<ProjectExpectedStudyFundingSource> findAll() {
    String query = "from " + ProjectExpectedStudyFundingSource.class.getName();
    List<ProjectExpectedStudyFundingSource> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectExpectedStudyFundingSource> getAllStudyFundingSourcesByStudy(long studyId) {
    String query = "select pesfundingSource from ProjectExpectedStudyFundingSource pesfundingSource "
      + "where pesfundingSource.projectExpectedStudy.id = :studyId order by pesfundingSource.phase.id";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("studyId", studyId);

    List<ProjectExpectedStudyFundingSource> result = super.findAll(createQuery);

    return result;
  }

  @Override
  public ProjectExpectedStudyFundingSource getProjectExpectedStudyFundingSourceByPhase(Long expectedID,
    Long fundingSourceID, Long phaseID) {
    String query = "select pesfundingSource from ProjectExpectedStudyFundingSource "
      + "pesfundingSource where pesfundingSource.projectExpectedStudy.id = :studyId "
      + "and pesfundingSource.fundingSource.id = :fundingSourceId and pesfundingSource.phase.id = :phaseId";

    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("studyId", expectedID);
    createQuery.setParameter("fundingSourceId", fundingSourceID);
    createQuery.setParameter("phaseId", phaseID);

    List<ProjectExpectedStudyFundingSource> list = super.findAll(query);

    return (list.size() > 0) ? list.get(0) : null;
  }

  @Override
  public ProjectExpectedStudyFundingSource save(ProjectExpectedStudyFundingSource projectExpectedStudyFundingSource) {
    if (projectExpectedStudyFundingSource.getId() == null) {
      super.saveEntity(projectExpectedStudyFundingSource);
    } else {
      projectExpectedStudyFundingSource = super.update(projectExpectedStudyFundingSource);
    }


    return projectExpectedStudyFundingSource;
  }
}