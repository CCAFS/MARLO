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
import org.cgiar.ccafs.marlo.data.dao.ProjectPolicyInfoDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInfoManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectPolicyInfoManagerImpl implements ProjectPolicyInfoManager {


  private ProjectPolicyInfoDAO projectPolicyInfoDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectPolicyInfoManagerImpl(ProjectPolicyInfoDAO projectPolicyInfoDAO, PhaseDAO phaseDAO) {
    this.projectPolicyInfoDAO = projectPolicyInfoDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectPolicyInfo(long projectPolicyInfoId) {

    projectPolicyInfoDAO.deleteProjectPolicyInfo(projectPolicyInfoId);
  }


  @Override
  public boolean existProjectPolicyInfo(long projectPolicyInfoID) {

    return projectPolicyInfoDAO.existProjectPolicyInfo(projectPolicyInfoID);
  }

  @Override
  public List<ProjectPolicyInfo> findAll() {

    return projectPolicyInfoDAO.findAll();

  }

  @Override
  public ProjectPolicyInfo getProjectPolicyInfoById(long projectPolicyInfoID) {

    return projectPolicyInfoDAO.find(projectPolicyInfoID);
  }

  /**
   * Reply the information to the next Phases
   * 
   * @param next - The next Phase
   * @param projectPolicyid - The project policy number id into the database.
   * @param projectPolicyInfo - The update project policy info to the current phase.
   */
  public void saveInfoPhase(Phase next, long projectPolicyid, ProjectPolicyInfo projectPolicyInfo) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectPolicyInfo> projectPolicyInfos = phase.getProjectPolicyInfos().stream()
      .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyid).collect(Collectors.toList());
    if (!projectPolicyInfos.isEmpty()) {
      for (ProjectPolicyInfo projectPolicyInfoPhase : projectPolicyInfos) {
        projectPolicyInfoPhase.updateProjectPolicyInfo(projectPolicyInfo, phase);
        projectPolicyInfoDAO.save(projectPolicyInfoPhase);
      }
    } else {
      ProjectPolicyInfo projectPolicyInfoAdd = new ProjectPolicyInfo();
      projectPolicyInfoAdd.setProjectPolicy(projectPolicyInfo.getProjectPolicy());
      projectPolicyInfoAdd.updateProjectPolicyInfo(projectPolicyInfo, phase);
      projectPolicyInfoAdd.setPhase(phase);
      projectPolicyInfoDAO.save(projectPolicyInfoAdd);
    }


    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectPolicyid, projectPolicyInfo);
    }
  }

  @Override
  public ProjectPolicyInfo saveProjectPolicyInfo(ProjectPolicyInfo projectPolicyInfo) {


    ProjectPolicyInfo sourceInfo = projectPolicyInfoDAO.save(projectPolicyInfo);
    Phase phase = phaseDAO.find(sourceInfo.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (projectPolicyInfo.getPhase().getNext() != null) {
        this.saveInfoPhase(projectPolicyInfo.getPhase().getNext(), projectPolicyInfo.getProjectPolicy().getId(),
          projectPolicyInfo);
      }
    }
    return sourceInfo;
  }


}
