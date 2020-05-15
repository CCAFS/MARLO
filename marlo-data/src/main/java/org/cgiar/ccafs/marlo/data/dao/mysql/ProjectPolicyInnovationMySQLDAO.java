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

import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyInnovationDAO;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInnovation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectPolicyInnovationMySQLDAO extends AbstractMarloDAO<ProjectPolicyInnovation, Long>
  implements ProjectPolicyInnovationDAO {


  @Inject
  public ProjectPolicyInnovationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectPolicyInnovation(long projectPolicyInnovationId) {
    ProjectPolicyInnovation projectPolicyInnovation = this.find(projectPolicyInnovationId);
    this.delete(projectPolicyInnovation);
  }

  @Override
  public boolean existProjectPolicyInnovation(long projectPolicyInnovationID) {
    ProjectPolicyInnovation projectPolicyInnovation = this.find(projectPolicyInnovationID);
    if (projectPolicyInnovation == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectPolicyInnovation find(long id) {
    return super.find(ProjectPolicyInnovation.class, id);

  }

  @Override
  public List<ProjectPolicyInnovation> findAll() {
    String query = "from " + ProjectPolicyInnovation.class.getName();
    List<ProjectPolicyInnovation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ProjectPolicyInnovation getProjectPolicyInnovationByPhase(Long projectPolicyID, Long projectInnovationID,
    Long phaseID) {
    String query =
      "from " + ProjectPolicyInnovation.class.getName() + " WHERE project_policy_id=" + projectPolicyID.longValue()
        + " AND project_innovation_id=" + projectInnovationID.longValue() + " AND id_phase=" + phaseID.longValue();
    List<ProjectPolicyInnovation> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;

  }

  @Override
  public ProjectPolicyInnovation save(ProjectPolicyInnovation projectPolicyInnovation) {
    if (projectPolicyInnovation.getId() == null) {
      super.saveEntity(projectPolicyInnovation);
    } else {
      projectPolicyInnovation = super.update(projectPolicyInnovation);
    }


    return projectPolicyInnovation;
  }


}