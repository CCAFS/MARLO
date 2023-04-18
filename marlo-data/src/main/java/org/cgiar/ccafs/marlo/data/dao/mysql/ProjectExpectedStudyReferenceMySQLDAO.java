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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyReferenceDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyReference;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyReferenceMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyReference, Long>
  implements ProjectExpectedStudyReferenceDAO {

  @Inject
  public ProjectExpectedStudyReferenceMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyReference(long projectExpectedStudyReferenceId) {
    ProjectExpectedStudyReference projectExpectedStudyReference = this.find(projectExpectedStudyReferenceId);

    this.delete(projectExpectedStudyReference);
  }

  @Override
  public boolean existProjectExpectedStudyReference(long projectExpectedStudyReferenceID) {
    ProjectExpectedStudyReference projectExpectedStudyReference = this.find(projectExpectedStudyReferenceID);

    if (projectExpectedStudyReference == null) {
      return false;
    }

    return true;
  }

  @Override
  public ProjectExpectedStudyReference find(long id) {
    return super.find(ProjectExpectedStudyReference.class, id);
  }

  @Override
  public List<ProjectExpectedStudyReference> findAll() {
    String query = "from " + ProjectExpectedStudyReference.class.getName();
    List<ProjectExpectedStudyReference> list = super.findAll(query);

    if (list.size() > 0) {
      return list;
    }

    return null;
  }

  @Override
  public List<ProjectExpectedStudyReference> getAllStudyReferencesByStudy(long studyId) {
    String query =
      "select peslink from ProjectExpectedStudyReference peslink where peslink.projectExpectedStudy.id = :studyId "
        + "order by peslink.phase.id";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);

    createQuery.setParameter("studyId", studyId);

    List<ProjectExpectedStudyReference> result = super.findAll(createQuery);

    return result;
  }

  @Override
  public ProjectExpectedStudyReference getProjectExpectedStudyReferenceByPhase(long expectedID, String link,
    long phaseID) {
    String query = "select distinct du from ProjectExpectedStudyReference du "
      + "where phase.id = :phaseId and projectExpectedStudy.id= :expectedId " + "and du.link = :duLink";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);

    createQuery.setParameter("phaseId", phaseID);
    createQuery.setParameter("expectedId", expectedID);
    createQuery.setParameter("duLink", link);

    Object findSingleResult = super.findSingleResult(ProjectExpectedStudyReference.class, createQuery);
    ProjectExpectedStudyReference projectExpectedStudyReferenceResult =
      (ProjectExpectedStudyReference) findSingleResult;

    return projectExpectedStudyReferenceResult;
  }

  @Override
  public ProjectExpectedStudyReference save(ProjectExpectedStudyReference projectExpectedStudyReference) {
    if (projectExpectedStudyReference.getId() == null) {
      super.saveEntity(projectExpectedStudyReference);
    } else {
      projectExpectedStudyReference = super.update(projectExpectedStudyReference);
    }

    return projectExpectedStudyReference;
  }
}