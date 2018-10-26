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

import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationInfoDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.RepIndStageInnovation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectInnovationInfoMySQLDAO extends AbstractMarloDAO<ProjectInnovationInfo, Long>
  implements ProjectInnovationInfoDAO {


  @Inject
  public ProjectInnovationInfoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectInnovationInfo(long projectInnovationInfoId) {
    ProjectInnovationInfo projectInnovationInfo = this.find(projectInnovationInfoId);
    this.delete(projectInnovationInfo);
  }

  @Override
  public boolean existProjectInnovationInfo(long projectInnovationInfoID) {
    ProjectInnovationInfo projectInnovationInfo = this.find(projectInnovationInfoID);
    if (projectInnovationInfo == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectInnovationInfo find(long id) {
    return super.find(ProjectInnovationInfo.class, id);

  }

  @Override
  public List<ProjectInnovationInfo> findAll() {
    String query = "from " + ProjectInnovationInfo.class.getName();
    List<ProjectInnovationInfo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectInnovationInfo> getInnovationsByStage(RepIndStageInnovation repIndStageInnovation, Phase phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("pii.id as id ");
    query.append("FROM ");
    query.append("project_innovation_info AS pii ");
    query.append("INNER JOIN project_innovations AS pi ON pi.id = pii.project_innovation_id ");
    query.append("INNER JOIN projects AS p ON p.id = pi.project_id ");
    query.append("INNER JOIN rep_ind_stage_innovations si ON si.id = pii.stage_innovation_id ");
    query.append("WHERE pi.is_active = 1 AND ");
    query.append("p.is_active = 1 AND ");
    query.append("pii.`id_phase` =" + phase.getId() + " AND ");
    query.append("pii.`year` =" + phase.getYear() + " AND ");
    query.append("si.`id` =" + repIndStageInnovation.getId());

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<ProjectInnovationInfo> projectInnovationInfos = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        ProjectInnovationInfo projectInnovationInfo = this.find(Long.parseLong(map.get("id").toString()));
        projectInnovationInfos.add(projectInnovationInfo);
      }
    }

    return projectInnovationInfos;
  }

  @Override
  public List<ProjectInnovationInfo> getProjectInnovationInfoByPhase(Phase phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("pii.id as id ");
    query.append("FROM ");
    query.append("project_innovation_info AS pii ");
    query.append("INNER JOIN project_innovations AS pi ON pi.id = pii.project_innovation_id ");
    query.append("INNER JOIN projects AS p ON p.id = pi.project_id ");
    query.append("WHERE pi.is_active = 1 AND ");
    query.append("p.is_active = 1 AND ");
    query.append("pii.`id_phase` =" + phase.getId() + " AND ");
    query.append("pii.`year` =" + phase.getYear());

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<ProjectInnovationInfo> projectInnovationInfos = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        ProjectInnovationInfo projectInnovationInfo = this.find(Long.parseLong(map.get("id").toString()));
        projectInnovationInfos.add(projectInnovationInfo);
      }
    }

    return projectInnovationInfos;
  }

  @Override
  public ProjectInnovationInfo save(ProjectInnovationInfo projectInnovationInfo) {
    if (projectInnovationInfo.getId() == null) {
      super.saveEntity(projectInnovationInfo);
    } else {
      projectInnovationInfo = super.update(projectInnovationInfo);
    }


    return projectInnovationInfo;
  }

}