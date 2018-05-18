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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyFlagshipDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyFlagshipMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyFlagship, Long>
  implements ProjectExpectedStudyFlagshipDAO {


  @Inject
  public ProjectExpectedStudyFlagshipMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyFlagship(long projectExpectedStudyFlagshipId) {
    ProjectExpectedStudyFlagship projectExpectedStudyFlagship = this.find(projectExpectedStudyFlagshipId);
    this.delete(projectExpectedStudyFlagship);
  }

  @Override
  public boolean existProjectExpectedStudyFlagship(long projectExpectedStudyFlagshipID) {
    ProjectExpectedStudyFlagship projectExpectedStudyFlagship = this.find(projectExpectedStudyFlagshipID);
    if (projectExpectedStudyFlagship == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyFlagship find(long id) {
    return super.find(ProjectExpectedStudyFlagship.class, id);

  }

  @Override
  public List<ProjectExpectedStudyFlagship> findAll() {
    String query = "from " + ProjectExpectedStudyFlagship.class.getName();
    List<ProjectExpectedStudyFlagship> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyFlagship save(ProjectExpectedStudyFlagship projectExpectedStudyFlagship) {
    if (projectExpectedStudyFlagship.getId() == null) {
      super.saveEntity(projectExpectedStudyFlagship);
    } else {
      projectExpectedStudyFlagship = super.update(projectExpectedStudyFlagship);
    }


    return projectExpectedStudyFlagship;
  }


}