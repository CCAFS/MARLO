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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyLinkDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLink;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

@Named
public class ProjectExpectedStudyLinkMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyLink, Long>
  implements ProjectExpectedStudyLinkDAO {


  @Inject
  public ProjectExpectedStudyLinkMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyLink(long projectExpectedStudyLinkId) {
    ProjectExpectedStudyLink projectExpectedStudyLink = this.find(projectExpectedStudyLinkId);

    this.delete(projectExpectedStudyLink);
  }

  @Override
  public boolean existProjectExpectedStudyLink(long projectExpectedStudyLinkID) {
    ProjectExpectedStudyLink projectExpectedStudyLink = this.find(projectExpectedStudyLinkID);
    if (projectExpectedStudyLink == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyLink find(long id) {
    return super.find(ProjectExpectedStudyLink.class, id);

  }

  @Override
  public List<ProjectExpectedStudyLink> findAll() {
    String query = "from " + ProjectExpectedStudyLink.class.getName();
    List<ProjectExpectedStudyLink> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyLink findProjectExpectedStudyLinkByPhase(Phase phase,
    ProjectExpectedStudyLink projectExpectedStudyLink) {
    String query = "select distinct du from ProjectExpectedStudyLink du "
      + "where phase.id = :phaseId and projectExpectedStudy.id= :expectedId " + "and du.link = :duLink";
    Query<ProjectExpectedStudyLink> createQuery = this.getSessionFactory().getCurrentSession().createQuery(query);
    createQuery.setParameter("phaseId", phase.getId());
    createQuery.setParameter("expectedId", projectExpectedStudyLink.getProjectExpectedStudy().getId());
    createQuery.setParameter("duLink", projectExpectedStudyLink.getLink());


    Object findSingleResult = super.findSingleResult(ProjectExpectedStudyLink.class, createQuery);
    ProjectExpectedStudyLink projectExpectedStudyLinkResult = (ProjectExpectedStudyLink) findSingleResult;
    return projectExpectedStudyLinkResult;
  }

  @Override
  public ProjectExpectedStudyLink getProjectExpectedStudyLinkByPhase(Long expectedID, String link, Long phaseID) {
    String query = "from " + ProjectExpectedStudyLink.class.getName() + " where expected_id=" + expectedID
      + " and link='" + link + "' and id_phase=" + phaseID;
    List<ProjectExpectedStudyLink> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public ProjectExpectedStudyLink save(ProjectExpectedStudyLink projectExpectedStudyLink) {
    if (projectExpectedStudyLink.getId() == null) {
      super.saveEntity(projectExpectedStudyLink);
    } else {
      projectExpectedStudyLink = super.update(projectExpectedStudyLink);
    }


    return projectExpectedStudyLink;
  }


}