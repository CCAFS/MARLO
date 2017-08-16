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

import org.cgiar.ccafs.marlo.data.dao.ProjectMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestone;

import java.util.List;

import com.google.inject.Inject;
import org.hibernate.SessionFactory;

public class ProjectMilestoneMySQLDAO extends AbstractMarloDAO<ProjectMilestone, Long> implements ProjectMilestoneDAO {


  @Inject
  public ProjectMilestoneMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public boolean deleteProjectMilestone(long projectMilestoneId) {
    ProjectMilestone projectMilestone = this.find(projectMilestoneId);
    projectMilestone.setActive(false);
    return this.save(projectMilestone) > 0;
  }

  @Override
  public boolean existProjectMilestone(long projectMilestoneID) {
    ProjectMilestone projectMilestone = this.find(projectMilestoneID);
    if (projectMilestone == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectMilestone find(long id) {
    return super.find(ProjectMilestone.class, id);

  }

  @Override
  public List<ProjectMilestone> findAll() {
    String query = "from " + ProjectMilestone.class.getName() + " where is_active=1";
    List<ProjectMilestone> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public long save(ProjectMilestone projectMilestone) {
    if (projectMilestone.getId() == null) {
      super.saveEntity(projectMilestone);
    } else {
      super.update(projectMilestone);
    }


    return projectMilestone.getId();
  }


}