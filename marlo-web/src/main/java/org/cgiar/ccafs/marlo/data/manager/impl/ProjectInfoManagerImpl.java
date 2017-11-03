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


import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.dao.PhaseDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectInfoDAO;
import org.cgiar.ccafs.marlo.data.dao.ProjectPhaseDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInfoManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;

import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Christian Garcia
 */
public class ProjectInfoManagerImpl implements ProjectInfoManager {


  private ProjectInfoDAO projectInfoDAO;
  // Managers
  private PhaseDAO phaseMySQLDAO;
  private ProjectPhaseDAO projectPhaseDAO;

  @Inject
  public ProjectInfoManagerImpl(ProjectInfoDAO projectInfoDAO, PhaseDAO phaseMySQLDAO,
    ProjectPhaseDAO projectPhaseDAO) {
    this.projectInfoDAO = projectInfoDAO;
    this.phaseMySQLDAO = phaseMySQLDAO;
    this.projectPhaseDAO = projectPhaseDAO;

  }

  @Override
  public boolean deleteProjectInfo(long projectInfoId) {

    return projectInfoDAO.deleteProjectInfo(projectInfoId);
  }

  @Override
  public boolean existProjectInfo(long projectInfoID) {

    return projectInfoDAO.existProjectInfo(projectInfoID);
  }

  @Override
  public List<ProjectInfo> findAll() {

    return projectInfoDAO.findAll();

  }

  @Override
  public ProjectInfo getProjectInfoById(long projectInfoID) {

    return projectInfoDAO.find(projectInfoID);
  }

  public void saveInfoPhase(Phase next, long projecID, ProjectInfo projectInfo) {
    Phase phase = phaseMySQLDAO.find(next.getId());
    Calendar cal = Calendar.getInstance();
    if (projectInfo.getEndDate() != null) {
      cal.setTime(projectInfo.getEndDate());
    }

    if (phase.getEditable() != null && phase.getEditable()) {
      List<ProjectInfo> projectInfos = phase.getProjectInfos().stream()
        .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID).collect(Collectors.toList());
      if (!projectInfos.isEmpty()) {
        for (ProjectInfo projectInfoPhase : projectInfos) {
          projectInfoPhase.updateProjectInfo(projectInfo);


          if (cal.get(Calendar.YEAR) < phase.getYear()) {

            projectInfoDAO.deleteProjectInfo(projectInfoPhase.getId());
            List<ProjectPhase> projectPhases = phase.getProjectPhases().stream()
              .filter(c -> c.isActive() && c.getProject().getId().longValue() == projecID).collect(Collectors.toList());
            if (!projectPhases.isEmpty()) {
              projectPhaseDAO.deleteProjectPhase(projectPhases.get(0).getId());
            }
          } else {
            projectInfoDAO.save(projectInfoPhase);
          }

        }
      } else {
        if (projectInfo.getEndDate() != null) {

          if (cal.get(Calendar.YEAR) >= phase.getYear()) {
            ProjectInfo projectInfoPhaseAdd = new ProjectInfo();
            projectInfoPhaseAdd.setProject(projectInfo.getProject());
            projectInfoPhaseAdd.setPhase(phase);
            projectInfoPhaseAdd.setProjectEditLeader(false);
            projectInfoPhaseAdd.updateProjectInfo(projectInfo);
            projectInfoDAO.save(projectInfoPhaseAdd);
            ProjectPhase projectPhase = new ProjectPhase();
            projectPhase.setPhase(phase);
            projectPhase.setProject(projectInfo.getProject());
            projectPhaseDAO.save(projectPhase);
          }
        }
      }


    }
    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projecID, projectInfo);
    }


  }

  @Override
  public long saveProjectInfo(ProjectInfo projectInfo) {

    long resultProjectInfo = projectInfoDAO.save(projectInfo);
    if (projectInfo.getPhase().getDescription().equals(APConstants.PLANNING)) {
      if (projectInfo.getPhase().getNext() != null) {
        this.saveInfoPhase(projectInfo.getPhase().getNext(), projectInfo.getProject().getId(), projectInfo);
      }
    }
    return resultProjectInfo;
  }


}
