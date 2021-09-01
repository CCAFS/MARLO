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

import org.cgiar.ccafs.marlo.data.dao.ProjectOutcomeDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectOutcome;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectOutcomeMySQLDAO extends AbstractMarloDAO<ProjectOutcome, Long> implements ProjectOutcomeDAO {


  @Inject
  public ProjectOutcomeMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }


  @Override
  public void deleteProjectOutcome(long projectOutcomeId) {
    ProjectOutcome projectOutcome = this.find(projectOutcomeId);
    projectOutcome.setActive(false);
    super.update(projectOutcome);
  }


  @Override
  public boolean existProjectOutcome(long projectOutcomeID) {
    ProjectOutcome projectOutcome = this.find(projectOutcomeID);
    if (projectOutcome == null) {
      return false;
    }
    return true;

  }


  @Override
  public ProjectOutcome find(long id) {
    return super.find(ProjectOutcome.class, id);

  }

  @Override
  public List<ProjectOutcome> findAll() {
    String query = "from " + ProjectOutcome.class.getName() + " where is_active=1";
    List<ProjectOutcome> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ProjectOutcome> getProjectOutcomeByPhase(Phase phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT ");
    query.append("project_outcomes.id ");
    query.append("FROM ");
    query.append("project_outcomes ");
    query.append("INNER JOIN projects ON project_outcomes.project_id = projects.id ");
    query.append("WHERE projects.is_active = 1 AND ");
    query.append("project_outcomes.id_phase=" + phase.getId());
    query.append(" AND project_outcomes.is_active = 1");


    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<ProjectOutcome> projectOutcomes = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        ProjectOutcome projectOutcome = this.find(Long.parseLong(map.get("id").toString()));
        projectOutcomes.add(projectOutcome);
      }
    }

    return projectOutcomes.stream().collect(Collectors.toList());
  }

  @Override
  public List<ProjectOutcome> getProjectOutcomeByProgramOutcomeAndProject(long programOutcomeId, long projectId) {
    programOutcomeId = programOutcomeId - 1;
    StringBuilder query = new StringBuilder();
    query.append("SELECT ");
    query.append("project_outcomes.id ");
    query.append("FROM ");
    query.append("project_outcomes ");
    query.append("INNER JOIN projects ON project_outcomes.project_id = projects.id ");
    query.append("WHERE project_outcomes.project_id = " + projectId + " AND projects.is_active = 1 AND ");
    query.append("project_outcomes.outcome_id=" + programOutcomeId);
    query.append(" AND project_outcomes.is_active = 1");


    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<ProjectOutcome> projectOutcomes = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        ProjectOutcome projectOutcome = this.find(Long.parseLong(map.get("id").toString()));
        projectOutcomes.add(projectOutcome);
      }
    }

    return projectOutcomes.stream().collect(Collectors.toList());
  }

  @Override
  public ProjectOutcome save(ProjectOutcome projectOutcome) {
    if (projectOutcome.getId() == null) {
      super.saveEntity(projectOutcome);
    } else {
      projectOutcome = super.update(projectOutcome);
    }

    return projectOutcome;
  }

  @Override
  public ProjectOutcome save(ProjectOutcome projectOutcome, String section, List<String> relationsName, Phase phase) {
    if (projectOutcome.getId() == null) {
      super.saveEntity(projectOutcome, section, relationsName, phase);
    } else {
      projectOutcome = super.update(projectOutcome, section, relationsName, phase);
    }


    return projectOutcome;
  }


}