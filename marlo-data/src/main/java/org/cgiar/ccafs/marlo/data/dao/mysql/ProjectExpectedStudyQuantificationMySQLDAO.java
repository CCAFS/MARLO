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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyQuantificationDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyQuantification;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyQuantificationMySQLDAO
  extends AbstractMarloDAO<ProjectExpectedStudyQuantification, Long> implements ProjectExpectedStudyQuantificationDAO {


  @Inject
  public ProjectExpectedStudyQuantificationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyQuantification(long projectExpectedStudyQuantificationId) {
    ProjectExpectedStudyQuantification projectExpectedStudyQuantification =
      this.find(projectExpectedStudyQuantificationId);
    this.delete(projectExpectedStudyQuantification);
  }

  @Override
  public boolean existProjectExpectedStudyQuantification(long projectExpectedStudyQuantificationID) {
    ProjectExpectedStudyQuantification projectExpectedStudyQuantification =
      this.find(projectExpectedStudyQuantificationID);
    if (projectExpectedStudyQuantification == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyQuantification find(long id) {
    return super.find(ProjectExpectedStudyQuantification.class, id);

  }

  @Override
  public List<ProjectExpectedStudyQuantification> findAll() {
    String query = "from " + ProjectExpectedStudyQuantification.class.getName();
    List<ProjectExpectedStudyQuantification> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyQuantification
    save(ProjectExpectedStudyQuantification projectExpectedStudyQuantification) {
    if (projectExpectedStudyQuantification.getId() == null) {
      super.saveEntity(projectExpectedStudyQuantification);
    } else {
      projectExpectedStudyQuantification = super.update(projectExpectedStudyQuantification);
    }


    return projectExpectedStudyQuantification;
  }


}