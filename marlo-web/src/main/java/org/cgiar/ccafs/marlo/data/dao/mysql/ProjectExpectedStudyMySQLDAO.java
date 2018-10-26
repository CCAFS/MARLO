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

import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectExpectedStudyMySQLDAO extends AbstractMarloDAO<ProjectExpectedStudy, Long>
  implements ProjectExpectedStudyDAO {


  @Inject
  public ProjectExpectedStudyMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectExpectedStudy(long projectExpectedStudyId) {
    ProjectExpectedStudy projectExpectedStudy = this.find(projectExpectedStudyId);
    projectExpectedStudy.setActive(false);
    this.save(projectExpectedStudy);
  }

  @Override
  public boolean existProjectExpectedStudy(long projectExpectedStudyID) {
    ProjectExpectedStudy projectExpectedStudy = this.find(projectExpectedStudyID);
    if (projectExpectedStudy == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectExpectedStudy find(long id) {
    return super.find(ProjectExpectedStudy.class, id);

  }

  @Override
  public List<ProjectExpectedStudy> findAll() {
    String query = "from " + ProjectExpectedStudy.class.getName() + " where is_active=1";
    List<ProjectExpectedStudy> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }


  @Override
  public List<ProjectExpectedStudy> getStudiesByOrganizationType(RepIndOrganizationType repIndOrganizationType,
    Phase phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("s.id as id ");
    query.append("FROM ");
    query.append("project_expected_studies AS s ");
    query.append("INNER JOIN project_expected_study_info AS si ON si.project_expected_study_id = s.id ");
    query.append("INNER JOIN rep_ind_organization_types ot ON ot.id = si.rep_ind_organization_type_id ");
    query.append("WHERE s.is_active = 1 AND ");
    query.append("s.`year` =" + phase.getYear() + " AND ");
    query.append("si.`id_phase` =" + phase.getId() + " AND ");
    query.append("si.`is_contribution` = 1 AND ");
    query.append("ot.`id` =" + repIndOrganizationType.getId());

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<ProjectExpectedStudy> projectExpectedStudies = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        ProjectExpectedStudy projectExpectedStudy = this.find(Long.parseLong(map.get("id").toString()));
        projectExpectedStudies.add(projectExpectedStudy);
      }
    }

    return projectExpectedStudies;

  }

  @Override
  public List<ProjectExpectedStudy> getStudiesByPhase(Phase phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT  ");
    query.append("s.id as id ");
    query.append("FROM ");
    query.append("project_expected_studies AS s ");
    query.append("INNER JOIN project_expected_study_info AS si ON si.project_expected_study_id = s.id ");
    query.append("WHERE s.is_active = 1 AND ");
    query.append("si.`is_contribution` = 1 AND ");
    query.append("s.`year` =" + phase.getYear() + " AND ");
    query.append("si.`id_phase` =" + phase.getId());

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<ProjectExpectedStudy> projectExpectedStudies = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        ProjectExpectedStudy projectExpectedStudy = this.find(Long.parseLong(map.get("id").toString()));
        projectExpectedStudies.add(projectExpectedStudy);
      }
    }

    return projectExpectedStudies;
  }

  @Override
  public List<Map<String, Object>> getUserStudies(long userId, String crp) {
    List<Map<String, Object>> list = new ArrayList<>();
    StringBuilder builder = new StringBuilder();
    builder.append("select DISTINCT project_id from user_permission where permission_id in "
      + "(select id from permissions where permission = 'crp:{0}:studies:{1}:canEdit')");
    if (super.getTemTableUserId() == userId) {
      list = super.findCustomQuery(builder.toString());
    } else {
      list = super.excuteStoreProcedure(" call getPermissions(" + userId + ")", builder.toString());
    }
    return list;
  }

  @Override
  public ProjectExpectedStudy save(ProjectExpectedStudy projectExpectedStudy) {
    if (projectExpectedStudy.getId() == null) {
      super.saveEntity(projectExpectedStudy);
    } else {
      projectExpectedStudy = super.update(projectExpectedStudy);
    }


    return projectExpectedStudy;
  }

  @Override
  public ProjectExpectedStudy save(ProjectExpectedStudy projectExpectedStudy, String section,
    List<String> relationsName, Phase phase) {
    if (projectExpectedStudy.getId() == null) {
      super.saveEntity(projectExpectedStudy, section, relationsName, phase);
    } else {
      projectExpectedStudy = super.update(projectExpectedStudy, section, relationsName, phase);
    }
    return projectExpectedStudy;
  }

}