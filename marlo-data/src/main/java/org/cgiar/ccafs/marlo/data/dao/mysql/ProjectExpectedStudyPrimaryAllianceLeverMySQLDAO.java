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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyPrimaryAllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPrimaryAllianceLever;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyPrimaryAllianceLeverMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyPrimaryAllianceLever, Long> implements ProjectExpectedStudyPrimaryAllianceLeverDAO {


  @Inject
  public ProjectExpectedStudyPrimaryAllianceLeverMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyPrimaryAllianceLever(long projectExpectedStudyPrimaryAllianceLeverId) {
    ProjectExpectedStudyPrimaryAllianceLever projectExpectedStudyPrimaryAllianceLever = this.find(projectExpectedStudyPrimaryAllianceLeverId);
    projectExpectedStudyPrimaryAllianceLever.setActive(false);
    this.update(projectExpectedStudyPrimaryAllianceLever);
  }

  @Override
  public boolean existProjectExpectedStudyPrimaryAllianceLever(long projectExpectedStudyPrimaryAllianceLeverID) {
    ProjectExpectedStudyPrimaryAllianceLever projectExpectedStudyPrimaryAllianceLever = this.find(projectExpectedStudyPrimaryAllianceLeverID);
    if (projectExpectedStudyPrimaryAllianceLever == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyPrimaryAllianceLever find(long id) {
    return super.find(ProjectExpectedStudyPrimaryAllianceLever.class, id);

  }

  @Override
  public List<ProjectExpectedStudyPrimaryAllianceLever> findAll() {
    String query = "from " + ProjectExpectedStudyPrimaryAllianceLever.class.getName() + " where is_active=1";
    List<ProjectExpectedStudyPrimaryAllianceLever> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyPrimaryAllianceLever save(ProjectExpectedStudyPrimaryAllianceLever projectExpectedStudyPrimaryAllianceLever) {
    if (projectExpectedStudyPrimaryAllianceLever.getId() == null) {
      super.saveEntity(projectExpectedStudyPrimaryAllianceLever);
    } else {
      projectExpectedStudyPrimaryAllianceLever = super.update(projectExpectedStudyPrimaryAllianceLever);
    }


    return projectExpectedStudyPrimaryAllianceLever;
  }


}