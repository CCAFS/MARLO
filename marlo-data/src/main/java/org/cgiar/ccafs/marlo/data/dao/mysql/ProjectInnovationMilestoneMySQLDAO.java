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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationMilestone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationMilestoneMySQLDAO extends AbstractMarloDAO<ProjectInnovationMilestone, Long> implements ProjectInnovationMilestoneDAO {


  @Inject
  public ProjectInnovationMilestoneMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationMilestone(long projectInnovationMilestoneId) {
    ProjectInnovationMilestone projectInnovationMilestone = this.find(projectInnovationMilestoneId);
    this.update(projectInnovationMilestone);
  }

  @Override
  public boolean existProjectInnovationMilestone(long projectInnovationMilestoneID) {
    ProjectInnovationMilestone projectInnovationMilestone = this.find(projectInnovationMilestoneID);
    if (projectInnovationMilestone == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationMilestone find(long id) {
    return super.find(ProjectInnovationMilestone.class, id);

  }

  @Override
  public List<ProjectInnovationMilestone> findAll() {
    String query = "from " + ProjectInnovationMilestone.class.getName();
    List<ProjectInnovationMilestone> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectInnovationMilestone save(ProjectInnovationMilestone projectInnovationMilestone) {
    if (projectInnovationMilestone.getId() == null) {
      super.saveEntity(projectInnovationMilestone);
    } else {
      projectInnovationMilestone = super.update(projectInnovationMilestone);
    }


    return projectInnovationMilestone;
  }


}