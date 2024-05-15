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

import org.cgiar.ccafs.marlo.data.dao.ProjectMilestoneDisaggregatedTargetDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectMilestoneDisaggregatedTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectMilestoneDisaggregatedTargetMySQLDAO extends AbstractMarloDAO<ProjectMilestoneDisaggregatedTarget, Long> implements ProjectMilestoneDisaggregatedTargetDAO {


  @Inject
  public ProjectMilestoneDisaggregatedTargetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectMilestoneDisaggregatedTarget(long projectMilestoneDisaggregatedTargetId) {
    ProjectMilestoneDisaggregatedTarget projectMilestoneDisaggregatedTarget = this.find(projectMilestoneDisaggregatedTargetId);
    projectMilestoneDisaggregatedTarget.setActive(false);
    this.update(projectMilestoneDisaggregatedTarget);
  }

  @Override
  public boolean existProjectMilestoneDisaggregatedTarget(long projectMilestoneDisaggregatedTargetID) {
    ProjectMilestoneDisaggregatedTarget projectMilestoneDisaggregatedTarget = this.find(projectMilestoneDisaggregatedTargetID);
    if (projectMilestoneDisaggregatedTarget == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectMilestoneDisaggregatedTarget find(long id) {
    return super.find(ProjectMilestoneDisaggregatedTarget.class, id);

  }

  @Override
  public List<ProjectMilestoneDisaggregatedTarget> findAll() {
    String query = "from " + ProjectMilestoneDisaggregatedTarget.class.getName() + " where is_active=1";
    List<ProjectMilestoneDisaggregatedTarget> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectMilestoneDisaggregatedTarget save(ProjectMilestoneDisaggregatedTarget projectMilestoneDisaggregatedTarget) {
    if (projectMilestoneDisaggregatedTarget.getId() == null) {
      super.saveEntity(projectMilestoneDisaggregatedTarget);
    } else {
      projectMilestoneDisaggregatedTarget = super.update(projectMilestoneDisaggregatedTarget);
    }


    return projectMilestoneDisaggregatedTarget;
  }


}