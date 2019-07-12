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

import org.cgiar.ccafs.marlo.data.dao.ProjectLp6ContributionDeliverableDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectLp6ContributionDeliverable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectLp6ContributionDeliverableMySQLDAO extends AbstractMarloDAO<ProjectLp6ContributionDeliverable, Long>
  implements ProjectLp6ContributionDeliverableDAO {


  @Inject
  public ProjectLp6ContributionDeliverableMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectLp6ContributionDeliverable(long projectLp6ContributionDeliverableId) {
    ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable =
      this.find(projectLp6ContributionDeliverableId);
    this.delete(projectLp6ContributionDeliverable);
  }

  @Override
  public boolean existProjectLp6ContributionDeliverable(long projectLp6ContributionDeliverableId) {
    ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable =
      this.find(projectLp6ContributionDeliverableId);
    if (projectLp6ContributionDeliverable == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectLp6ContributionDeliverable find(long id) {
    return super.find(ProjectLp6ContributionDeliverable.class, id);

  }

  @Override
  public List<ProjectLp6ContributionDeliverable> findAll() {
    String query = "from " + ProjectLp6ContributionDeliverable.class.getName();
    List<ProjectLp6ContributionDeliverable> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public List<ProjectLp6ContributionDeliverable>
    getProjectLp6ContributionDeliverablebyPhase(long projectLp6ContributionID, long phaseID) {
    StringBuilder query = new StringBuilder();
    query.append(
      "SELECT project_lp6_contribution_deliverables.id as projectLp6DeliverableID FROM project_lp6_contribution ");
    query.append(
      "INNER JOIN project_lp6_contribution_deliverables ON project_lp6_contribution_deliverables.lp6_contribution_id = project_lp6_contribution.id ");
    query.append("INNER JOIN phases ON project_lp6_contribution_deliverables.id_phase = phases.id ");
    query.append("WHERE project_lp6_contribution.id = ");
    query.append(projectLp6ContributionID);
    query.append(" AND phases.id = ");
    query.append(phaseID);
    List<Map<String, Object>> list = super.findCustomQuery(query.toString());

    List<ProjectLp6ContributionDeliverable> projectLp6ContributionDeliverables =
      new ArrayList<ProjectLp6ContributionDeliverable>();
    for (Map<String, Object> map : list) {
      String projectLp6DeliverableID = map.get("projectLp6DeliverableID").toString();
      long longProjectLp6DeliverableID = Long.parseLong(projectLp6DeliverableID);
      ProjectLp6ContributionDeliverable projectLp6Deliverable = this.find(longProjectLp6DeliverableID);
      projectLp6ContributionDeliverables.add(projectLp6Deliverable);
    }
    return projectLp6ContributionDeliverables;
  }

  @Override
  public ProjectLp6ContributionDeliverable save(ProjectLp6ContributionDeliverable projectLp6ContributionDeliverable) {
    if (projectLp6ContributionDeliverable.getId() == null) {
      super.saveEntity(projectLp6ContributionDeliverable);
    } else {
      projectLp6ContributionDeliverable = super.update(projectLp6ContributionDeliverable);
    }

    return projectLp6ContributionDeliverable;
  }


}