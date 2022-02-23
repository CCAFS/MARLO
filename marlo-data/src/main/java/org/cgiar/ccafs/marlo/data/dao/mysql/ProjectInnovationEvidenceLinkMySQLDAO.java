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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationEvidenceLinkDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationEvidenceLink;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.Query;
import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationEvidenceLinkMySQLDAO extends AbstractMarloDAO<ProjectInnovationEvidenceLink, Long>
  implements ProjectInnovationEvidenceLinkDAO {

  @Inject
  public ProjectInnovationEvidenceLinkMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationEvidenceLink(long projectInnovationEvidenceLinkId) {
    ProjectInnovationEvidenceLink projectInnovationEvidenceLink = this.find(projectInnovationEvidenceLinkId);

    this.delete(projectInnovationEvidenceLink);
  }

  @Override
  public boolean existProjectInnovationEvidenceLink(long projectInnovationEvidenceLinkID) {
    ProjectInnovationEvidenceLink projectInnovationEvidenceLink = this.find(projectInnovationEvidenceLinkID);

    if (projectInnovationEvidenceLink == null) {
      return false;
    }

    return true;
  }

  @Override
  public ProjectInnovationEvidenceLink find(long id) {
    return super.find(ProjectInnovationEvidenceLink.class, id);
  }

  @Override
  public List<ProjectInnovationEvidenceLink> findAll() {
    String query = "from " + ProjectInnovationEvidenceLink.class.getName();
    List<ProjectInnovationEvidenceLink> list = super.findAll(query);

    if (list.size() > 0) {
      return list;
    }

    return null;
  }

  @Override
  public List<ProjectInnovationEvidenceLink> getAllInnovationLinksByStudy(long innovationId) {
    String query =
      "select peslink from ProjectInnovationEvidenceLink peslink where peslink.projectInnovation.id = :innovationId "
        + "order by peslink.phase.id";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);

    createQuery.setParameter("innovationId", innovationId);

    List<ProjectInnovationEvidenceLink> result = super.findAll(createQuery);

    return result;
  }

  @Override
  public List<ProjectInnovationEvidenceLink> getProjectInnovationEvidenceLinkByPhase(long innovationID, long phaseID) {
    String query = "select distinct du from ProjectInnovationEvidenceLink du "
      + "where phase.id = :phaseId and projectInnovation.id= :innovationId ";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);

    createQuery.setParameter("phaseId", phaseID);
    createQuery.setParameter("innovationId", innovationID);

    List<ProjectInnovationEvidenceLink> result = super.findAll(createQuery);

    return result;
  }

  @Override
  public ProjectInnovationEvidenceLink getProjectInnovationEvidenceLinkByPhase(long innovationID, String link,
    long phaseID) {
    String query = "select distinct du from ProjectInnovationEvidenceLink du "
      + "where phase.id = :phaseId and projectInnovation.id= :innovationId " + "and du.link = :duLink";
    Query createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);

    createQuery.setParameter("phaseId", phaseID);
    createQuery.setParameter("innovationId", innovationID);
    createQuery.setParameter("duLink", link);

    List<ProjectInnovationEvidenceLink> findAll = super.findAll(createQuery);

    return (findAll != null && findAll.size() > 0) ? findAll.get(0) : null;
  }

  @Override
  public ProjectInnovationEvidenceLink save(ProjectInnovationEvidenceLink projectInnovationEvidenceLink) {
    if (projectInnovationEvidenceLink.getId() == null) {
      super.saveEntity(projectInnovationEvidenceLink);
    } else {
      projectInnovationEvidenceLink = super.update(projectInnovationEvidenceLink);
    }

    return projectInnovationEvidenceLink;
  }
}