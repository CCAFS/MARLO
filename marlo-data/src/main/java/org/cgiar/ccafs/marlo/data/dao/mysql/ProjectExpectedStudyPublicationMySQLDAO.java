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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPublicationDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPublication;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyPublicationMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyPublication, Long> implements ProjectExpectedStudyPublicationDAO {


  @Inject
  public ProjectExpectedStudyPublicationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyPublication(long projectExpectedStudyPublicationId) {
    ProjectExpectedStudyPublication projectExpectedStudyPublication = this.find(projectExpectedStudyPublicationId);
    projectExpectedStudyPublication.setActive(false);
    this.update(projectExpectedStudyPublication);
  }

  @Override
  public boolean existProjectExpectedStudyPublication(long projectExpectedStudyPublicationID) {
    ProjectExpectedStudyPublication projectExpectedStudyPublication = this.find(projectExpectedStudyPublicationID);
    if (projectExpectedStudyPublication == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyPublication find(long id) {
    return super.find(ProjectExpectedStudyPublication.class, id);

  }

  @Override
  public List<ProjectExpectedStudyPublication> findAll() {
    String query = "from " + ProjectExpectedStudyPublication.class.getName() + " where is_active=1";
    List<ProjectExpectedStudyPublication> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyPublication save(ProjectExpectedStudyPublication projectExpectedStudyPublication) {
    if (projectExpectedStudyPublication.getId() == null) {
      super.saveEntity(projectExpectedStudyPublication);
    } else {
      projectExpectedStudyPublication = super.update(projectExpectedStudyPublication);
    }


    return projectExpectedStudyPublication;
  }


}