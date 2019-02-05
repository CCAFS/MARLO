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

import org.cgiar.ccafs.marlo.data.dao.ProjectLp6ContributionDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6Contribution;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectLp6ContributionMySQLDAO extends AbstractMarloDAO<ProjectLp6Contribution, Long>
  implements ProjectLp6ContributionDAO {


  @Inject
  public ProjectLp6ContributionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectLp6Contribution(long projectMilestoneId) {
    ProjectLp6Contribution projectLp6Contribution = this.find(projectMilestoneId);
    projectLp6Contribution.setActive(false);
    this.save(projectLp6Contribution);
  }

  @Override
  public boolean existProjectLp6Contribution(long projectMilestoneID) {
    ProjectLp6Contribution projectLp6Contribution = this.find(projectMilestoneID);
    if (projectLp6Contribution == null) {
      return false;
    }
    return true;
  }

  @Override
  public ProjectLp6Contribution find(long id) {
    return super.find(ProjectLp6Contribution.class, id);
  }

  @Override
  public List<ProjectLp6Contribution> findAll() {
    String query = "from " + ProjectLp6Contribution.class.getName() + " where is_active=1";
    List<ProjectLp6Contribution> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public ProjectLp6Contribution save(ProjectLp6Contribution projectLp6Contribution) {
    if (projectLp6Contribution.getId() == null) {
      super.saveEntity(projectLp6Contribution);
    } else {
      projectLp6Contribution = super.update(projectLp6Contribution);
    }
    return projectLp6Contribution;
  }


}