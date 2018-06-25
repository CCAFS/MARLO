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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyInfoDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyInfoMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudyInfo, Long>
  implements ProjectExpectedStudyInfoDAO {


  @Inject
  public ProjectExpectedStudyInfoMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudyInfo(long projectExpectedStudyInfoId) {
    ProjectExpectedStudyInfo projectExpectedStudyInfo = this.find(projectExpectedStudyInfoId);
    this.delete(projectExpectedStudyInfo);
  }

  @Override
  public boolean existProjectExpectedStudyInfo(long projectExpectedStudyInfoID) {
    ProjectExpectedStudyInfo projectExpectedStudyInfo = this.find(projectExpectedStudyInfoID);
    if (projectExpectedStudyInfo == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudyInfo find(long id) {
    return super.find(ProjectExpectedStudyInfo.class, id);

  }

  @Override
  public List<ProjectExpectedStudyInfo> findAll() {
    String query = "from " + ProjectExpectedStudyInfo.class.getName();
    List<ProjectExpectedStudyInfo> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectExpectedStudyInfo> getProjectExpectedStudyInfoByPhase(Phase phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("pesi.id as id ");
    query.append("FROM ");
    query.append("project_expected_study_info AS pesi ");
    query.append("INNER JOIN project_expected_studies AS pes ON pes.id = pesi.project_expected_study_id ");
    query.append("INNER JOIN projects AS p ON p.id = pes.project_id AND p.is_active ");
    query.append("WHERE pes.is_active = 1 AND ");
    query.append("pesi.`id_phase` =" + phase.getId());
    query.append(" ORDER BY p.id,pes.id");

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<ProjectExpectedStudyInfo> projectExpectedStudyInfos = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        ProjectExpectedStudyInfo projectExpectedStudyInfo = this.find(Long.parseLong(map.get("id").toString()));
        projectExpectedStudyInfos.add(projectExpectedStudyInfo);
      }
    }

    return projectExpectedStudyInfos;
  }

  @Override
  public ProjectExpectedStudyInfo save(ProjectExpectedStudyInfo projectExpectedStudyInfo) {
    if (projectExpectedStudyInfo.getId() == null) {
      super.saveEntity(projectExpectedStudyInfo);
    } else {
      projectExpectedStudyInfo = super.update(projectExpectedStudyInfo);
    }


    return projectExpectedStudyInfo;
  }


}