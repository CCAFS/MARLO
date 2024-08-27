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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyRelatedAllianceLeverDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRelatedAllianceLever;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyRelatedAllianceLeverMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyRelatedAllianceLever, Long> implements ProjectExpectedStudyRelatedAllianceLeverDAO {


  @Inject
  public ProjectExpectedStudyRelatedAllianceLeverMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyRelatedAllianceLever(long projectExpectedStudyRelatedAllianceLeverId) {
    ProjectExpectedStudyRelatedAllianceLever projectExpectedStudyRelatedAllianceLever = this.find(projectExpectedStudyRelatedAllianceLeverId);
    projectExpectedStudyRelatedAllianceLever.setActive(false);
    this.update(projectExpectedStudyRelatedAllianceLever);
  }

  @Override
  public boolean existProjectExpectedStudyRelatedAllianceLever(long projectExpectedStudyRelatedAllianceLeverID) {
    ProjectExpectedStudyRelatedAllianceLever projectExpectedStudyRelatedAllianceLever = this.find(projectExpectedStudyRelatedAllianceLeverID);
    if (projectExpectedStudyRelatedAllianceLever == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyRelatedAllianceLever find(long id) {
    return super.find(ProjectExpectedStudyRelatedAllianceLever.class, id);

  }

  @Override
  public List<ProjectExpectedStudyRelatedAllianceLever> findAll() {
    String query = "from " + ProjectExpectedStudyRelatedAllianceLever.class.getName() + " where is_active=1";
    List<ProjectExpectedStudyRelatedAllianceLever> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyRelatedAllianceLever save(ProjectExpectedStudyRelatedAllianceLever projectExpectedStudyRelatedAllianceLever) {
    if (projectExpectedStudyRelatedAllianceLever.getId() == null) {
      super.saveEntity(projectExpectedStudyRelatedAllianceLever);
    } else {
      projectExpectedStudyRelatedAllianceLever = super.update(projectExpectedStudyRelatedAllianceLever);
    }


    return projectExpectedStudyRelatedAllianceLever;
  }


}