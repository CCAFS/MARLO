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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ProjectImpactsDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectImpactsManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectImpacts;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPerson;
import org.cgiar.ccafs.marlo.data.model.ReportProjectImpactsCovid19DTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectImpactsManagerImpl implements ProjectImpactsManager {


  private static String ACRONYM_ROL_MANAGEMENT_LIAISON = "ML";
  private ProjectImpactsDAO projectImpactsDAO;

  // Managers
  private ProjectInfoManager projectInfoManager;

  @Inject
  public ProjectImpactsManagerImpl(ProjectImpactsDAO projectImpactsDAO, ProjectInfoManager projectInfoManager) {
    this.projectImpactsDAO = projectImpactsDAO;
    this.projectInfoManager = projectInfoManager;
  }

  @Override
  public void deleteProjectImpacts(long projectImpactsId) {

    projectImpactsDAO.deleteProjectImpacts(projectImpactsId);
  }

  @Override
  public boolean existProjectImpacts(long projectImpactsID) {

    return projectImpactsDAO.existProjectImpacts(projectImpactsID);
  }

  @Override
  public List<ProjectImpacts> findAll() {

    return projectImpactsDAO.findAll();

  }

  @Override
  public ProjectImpacts getProjectImpactsById(long projectImpactsID) {

    return projectImpactsDAO.find(projectImpactsID);
  }

  @Override
  public List<ProjectImpacts> getProjectImpactsByPhase(Phase phase) {

    return projectImpactsDAO.getProjectImpactsByPhase(phase);
  }

  @Override
  public List<ReportProjectImpactsCovid19DTO> getProjectImpactsByProjectAndYears(Phase phase) {
    List<ReportProjectImpactsCovid19DTO> reportProjectImpactsCovid19DTO = new ArrayList();
    List<ProjectImpacts> projectImpacts = this.getProjectImpactsByPhase(phase);
    if (projectImpacts != null) {
      for (ProjectImpacts projectImpact : projectImpacts) {
        String projectId = projectImpact.getProject().getId().toString();
        ProjectInfo info = new ProjectInfo();
        info = projectInfoManager.getProjectInfoByProjectPhase(new Long(projectId), phase.getId());
        projectImpact.getProject().setProjectInfo(info);
        if (reportProjectImpactsCovid19DTO.stream().anyMatch(c -> c.getProjectId().equals(projectId))) {
          reportProjectImpactsCovid19DTO.stream().filter(c -> c.getProjectId().equals(projectId))
            .forEach(e -> e.getAnswer().put(projectImpact.getYear(), projectImpact.getAnswer()));
        } else {
          reportProjectImpactsCovid19DTO.add(this.projectImpactsToReportProjectImpactsCovid19DTO(projectImpact, phase));
        }
      }
    }
    return reportProjectImpactsCovid19DTO;
  }

  @Override
  public List<ProjectImpacts> getProjectImpactsByProjectId(long projectId) {
    return projectImpactsDAO.findByProjectId(projectId);
  }

  public ReportProjectImpactsCovid19DTO projectImpactsToReportProjectImpactsCovid19DTO(ProjectImpacts projectImpact,
    Phase phase) {
    ReportProjectImpactsCovid19DTO ReportProjectImpactsCovid19DTO = new ReportProjectImpactsCovid19DTO();

    ReportProjectImpactsCovid19DTO.setProjectId(projectImpact.getProject().getId().toString());

    String title = projectImpact.getProject().getProjecInfoPhase(phase).getTitle();

    if (title != null && !title.isEmpty()) {
      ReportProjectImpactsCovid19DTO.setTitle(title);
    }

    String summary = projectImpact.getProject().getProjectInfo().getSummary();

    if (summary != null && !summary.isEmpty()) {
      ReportProjectImpactsCovid19DTO.setProjectSummary(summary);
    }

    ProjectPartnerPerson projectLeader = projectImpact.getProject().getLeaderPersonDB(phase);

    if (projectLeader != null) {
      ReportProjectImpactsCovid19DTO
        .setProjectLeader((projectLeader.getUser().getFirstName() == null ? "" : projectLeader.getUser().getFirstName())
          + " " + (projectLeader.getUser().getLastName() == null ? "" : projectLeader.getUser().getLastName()));

      ReportProjectImpactsCovid19DTO
        .setProjectLeaderEmail(projectLeader.getUser().getEmail() == null ? "" : projectLeader.getUser().getEmail());
    }

    LiaisonInstitution managementLiasion = projectImpact.getProject().getProjectInfo().getLiaisonInstitution();

    if (managementLiasion != null) {
      ReportProjectImpactsCovid19DTO.setManagementLiasion(managementLiasion.getName());
      ReportProjectImpactsCovid19DTO.setManagementLiasionAcronym(managementLiasion.getAcronym());
    }

    HashMap<Integer, String> answer = new HashMap<Integer, String>();
    answer.put(projectImpact.getYear(), projectImpact.getAnswer());
    ReportProjectImpactsCovid19DTO.setAnswer(answer);

    ReportProjectImpactsCovid19DTO.setProjectUrl("P" + projectImpact.getProject().getId().toString());

    ReportProjectImpactsCovid19DTO.setPhaseId(phase.getId().toString());

    return ReportProjectImpactsCovid19DTO;
  }

  @Override
  public ProjectImpacts saveProjectImpacts(ProjectImpacts projectImpacts) {

    return projectImpactsDAO.save(projectImpacts);
  }

}
