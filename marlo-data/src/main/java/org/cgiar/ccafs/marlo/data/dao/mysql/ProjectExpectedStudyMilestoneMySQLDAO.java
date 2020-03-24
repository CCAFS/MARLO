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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyMilestoneDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyMilestone;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyMilestoneMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyMilestone, Long>
  implements ProjectExpectedStudyMilestoneDAO {


  @Inject
  public ProjectExpectedStudyMilestoneMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyMilestone(long projectExpectedStudyMilestoneId) {
    ProjectExpectedStudyMilestone projectExpectedStudyMilestone = this.find(projectExpectedStudyMilestoneId);
    this.delete(projectExpectedStudyMilestone);
  }

  @Override
  public boolean existProjectExpectedStudyMilestone(long projectExpectedStudyMilestoneID) {
    ProjectExpectedStudyMilestone projectExpectedStudyMilestone = this.find(projectExpectedStudyMilestoneID);
    if (projectExpectedStudyMilestone == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyMilestone find(long id) {
    return super.find(ProjectExpectedStudyMilestone.class, id);

  }

  @Override
  public List<ProjectExpectedStudyMilestone> findAll() {
    String query = "from " + ProjectExpectedStudyMilestone.class.getName();
    List<ProjectExpectedStudyMilestone> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyMilestone getProjectExpectedStudyMilestoneByPhase(Long expectedID, Long milestoneID,
    Long phaseID) {
    String query = "from " + ProjectExpectedStudyMilestone.class.getName() + " where expected_id=" + expectedID
      + " and crp_milestone_id=" + milestoneID + " and id_phase=" + phaseID;
    List<ProjectExpectedStudyMilestone> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;

  }

  @Override
  public ProjectExpectedStudyMilestone save(ProjectExpectedStudyMilestone projectExpectedStudyMilestone) {
    if (projectExpectedStudyMilestone.getId() == null) {
      super.saveEntity(projectExpectedStudyMilestone);
    } else {
      projectExpectedStudyMilestone = super.update(projectExpectedStudyMilestone);
    }


    return projectExpectedStudyMilestone;
  }


}