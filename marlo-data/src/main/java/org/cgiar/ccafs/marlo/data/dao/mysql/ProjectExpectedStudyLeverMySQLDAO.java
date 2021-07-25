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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyLeverDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLever;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyLeverMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyLever, Long>
  implements ProjectExpectedStudyLeverDAO {


  @Inject
  public ProjectExpectedStudyLeverMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyLever(long projectExpectedStudyLeverId) {
    ProjectExpectedStudyLever projectExpectedStudyLever = this.find(projectExpectedStudyLeverId);
    this.delete(projectExpectedStudyLever);
  }

  @Override
  public boolean existProjectExpectedStudyLever(long projectExpectedStudyLeverID) {
    ProjectExpectedStudyLever projectExpectedStudyLever = this.find(projectExpectedStudyLeverID);
    if (projectExpectedStudyLever == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyLever find(long id) {
    return super.find(ProjectExpectedStudyLever.class, id);

  }

  @Override
  public List<ProjectExpectedStudyLever> findAll() {
    String query = "from " + ProjectExpectedStudyLever.class.getName();
    List<ProjectExpectedStudyLever> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyLever save(ProjectExpectedStudyLever projectExpectedStudyLever) {
    if (projectExpectedStudyLever.getId() == null) {
      super.saveEntity(projectExpectedStudyLever);
    } else {
      projectExpectedStudyLever = super.update(projectExpectedStudyLever);
    }


    return projectExpectedStudyLever;
  }


}