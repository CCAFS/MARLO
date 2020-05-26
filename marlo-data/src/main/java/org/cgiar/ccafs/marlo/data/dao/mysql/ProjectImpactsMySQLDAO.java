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

import org.cgiar.ccafs.marlo.data.dao.ProjectImpactsDAO;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectImpacts;
import org.cgiar.ccafs.marlo.data.model.ReportProjectImpactsCovid19DTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ProjectImpactsMySQLDAO extends AbstractMarloDAO<ProjectImpacts, Long> implements ProjectImpactsDAO {

  private static String ACRONYM_ROL_MANAGEMENT_LIAISON = "ML";

  @Inject
  public ProjectImpactsMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteProjectImpacts(long projectImpactsId) {
    ProjectImpacts projectImpacts = this.find(projectImpactsId);
    projectImpacts.setActive(false);
    this.update(projectImpacts);
  }

  @Override
  public boolean existProjectImpacts(long projectImpactsID) {
    ProjectImpacts projectImpacts = this.find(projectImpactsID);
    if (projectImpacts == null) {
      return false;
    }
    return true;

  }

  @Override
  public ProjectImpacts find(long id) {
    return super.find(ProjectImpacts.class, id);

  }

  @Override
  public List<ProjectImpacts> findAll() {
    String query = "from " + ProjectImpacts.class.getName() + " where is_active=1";
    List<ProjectImpacts> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public List<ReportProjectImpactsCovid19DTO> findByProjectAndYears(Phase selectedPhase) {
    List<ReportProjectImpactsCovid19DTO> reportProjectImpactsCovid19DTO = new ArrayList();
    List<ProjectImpacts> projectImpacts = this.findAll();

    for (ProjectImpacts projectImpact : projectImpacts) {
      if (reportProjectImpactsCovid19DTO.stream()
        .anyMatch(c -> c.getProjectId().equals(projectImpact.getId().toString()))) {

        reportProjectImpactsCovid19DTO.stream().filter(c -> c.getProjectId().equals(projectImpact.getId().toString()))
          .forEach(e -> e.getAnswer().put(projectImpact.getYear(), projectImpact.getAnswer()));
      } else {
        reportProjectImpactsCovid19DTO
          .add(this.projectImpactsToReportProjectImpactsCovid19DTO(projectImpact, selectedPhase));
      }
    }
    return reportProjectImpactsCovid19DTO;
  }

  @Override
  public List<ProjectImpacts> findByProjectId(long projectId) {
    String query = "from " + ProjectImpacts.class.getName() + " where is_active=1 and project_id=" + projectId;
    List<ProjectImpacts> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;
  }

  @Override
  public List<ProjectImpacts> getProjectImpactsByPhase(Phase phase) {
    StringBuilder query = new StringBuilder();
    query.append("SELECT DISTINCT pi.id ");
    query.append("FROM project_impacts AS pi ");
    query.append("INNER JOIN projects_info AS pin ");
    query.append("ON pi.project_id = pin.project_id ");
    query.append("WHERE pi.is_active = 1 AND pin.id_phase = " + phase.getId());

    List<Map<String, Object>> rList = super.findCustomQuery(query.toString());
    List<ProjectImpacts> projectImpacts = new ArrayList<>();

    if (rList != null) {
      for (Map<String, Object> map : rList) {
        ProjectImpacts projectImpact = this.find(Long.parseLong(map.get("id").toString()));
        projectImpacts.add(projectImpact);
      }
    }

    if (projectImpacts.size() > 0) {
      return projectImpacts;
    }
    return null;
  }

  public ReportProjectImpactsCovid19DTO projectImpactsToReportProjectImpactsCovid19DTO(ProjectImpacts projectImpact,
    Phase selectedPhase) {
    ReportProjectImpactsCovid19DTO ReportProjectImpactsCovid19DTO = new ReportProjectImpactsCovid19DTO();

    ReportProjectImpactsCovid19DTO.setProjectId(projectImpact.getProject().getId().toString());
    ReportProjectImpactsCovid19DTO.setTitle(projectImpact.getProject().getProjecInfoPhase(selectedPhase).getTitle());

    if (projectImpact.getProject().getProjectInfo().getSummary() != null
      && !projectImpact.getProject().getProjectInfo().getSummary().isEmpty()) {
      ReportProjectImpactsCovid19DTO.setProjectSummary(projectImpact.getProject().getProjectInfo().getSummary());
    }

    ReportProjectImpactsCovid19DTO
      .setProjectLeader(projectImpact.getProject().getLeaderPersonDB(selectedPhase).getUser().getFirstName() + " "
        + projectImpact.getProject().getLeaderPersonDB(selectedPhase).getUser().getLastName());

    ReportProjectImpactsCovid19DTO.setManagementLiasion(
      projectImpact.getProject().getRolPersonDB(selectedPhase, ACRONYM_ROL_MANAGEMENT_LIAISON).getUser().getFirstName()
        + " " + projectImpact.getProject().getRolPersonDB(selectedPhase, ACRONYM_ROL_MANAGEMENT_LIAISON).getUser()
          .getLastName());

    HashMap<Integer, String> answer = new HashMap<Integer, String>();
    answer.put(projectImpact.getYear(), projectImpact.getAnswer());
    ReportProjectImpactsCovid19DTO.setAnswer(answer);

    return ReportProjectImpactsCovid19DTO;
  }

  @Override
  public ProjectImpacts save(ProjectImpacts projectImpacts) {
    if (projectImpacts.getId() == null) {
      super.saveEntity(projectImpacts);
    } else {
      projectImpacts = super.update(projectImpacts);
    }


    return projectImpacts;
  }

}