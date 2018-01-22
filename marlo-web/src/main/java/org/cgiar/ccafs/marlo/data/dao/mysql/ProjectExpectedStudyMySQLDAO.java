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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;

import java.util.List;

import javax.inject.Named;
import javax.inject.Inject;
import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudy, Long> implements ProjectExpectedStudyDAO {


  @Inject
  public ProjectExpectedStudyMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudy(long projectExpectedStudyId) {
    ProjectExpectedStudy projectExpectedStudy = this.find(projectExpectedStudyId);
    projectExpectedStudy.setActive(false);
    this.save(projectExpectedStudy);
  }

  @Override
  public boolean existProjectExpectedStudy(long projectExpectedStudyID) {
    ProjectExpectedStudy projectExpectedStudy = this.find(projectExpectedStudyID);
    if (projectExpectedStudy == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudy find(long id) {
    return super.find(ProjectExpectedStudy.class, id);

  }

  @Override
  public List<ProjectExpectedStudy> findAll() {
    String query = "from " + ProjectExpectedStudy.class.getName() + " where is_active=1";
    List<ProjectExpectedStudy> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudy save(ProjectExpectedStudy projectExpectedStudy) {
    if (projectExpectedStudy.getId() == null) {
      super.saveEntity(projectExpectedStudy);
    } else {
      projectExpectedStudy = super.update(projectExpectedStudy);
    }


    return projectExpectedStudy;
  }


}