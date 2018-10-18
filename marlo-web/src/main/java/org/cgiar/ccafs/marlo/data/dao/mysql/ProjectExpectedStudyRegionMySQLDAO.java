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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyRegionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyRegionMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyRegion, Long>
  implements ProjectExpectedStudyRegionDAO {


  @Inject
  public ProjectExpectedStudyRegionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyRegion(long projectExpectedStudyRegionId) {
    ProjectExpectedStudyRegion projectExpectedStudyRegion = this.find(projectExpectedStudyRegionId);
    this.delete(projectExpectedStudyRegion);
  }

  @Override
  public boolean existProjectExpectedStudyRegion(long projectExpectedStudyRegionID) {
    ProjectExpectedStudyRegion projectExpectedStudyRegion = this.find(projectExpectedStudyRegionID);
    if (projectExpectedStudyRegion == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyRegion find(long id) {
    return super.find(ProjectExpectedStudyRegion.class, id);

  }

  @Override
  public List<ProjectExpectedStudyRegion> findAll() {
    String query = "from " + ProjectExpectedStudyRegion.class.getName();
    List<ProjectExpectedStudyRegion> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyRegion save(ProjectExpectedStudyRegion projectExpectedStudyRegion) {
    if (projectExpectedStudyRegion.getId() == null) {
      super.saveEntity(projectExpectedStudyRegion);
    } else {
      projectExpectedStudyRegion = super.update(projectExpectedStudyRegion);
    }


    return projectExpectedStudyRegion;
  }


}