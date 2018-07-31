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
import org.cgiar.ccafs.marlo.data.dao.ProjectExpectedStudyInfoDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ProjectExpectedStudyInfoManagerImpl implements ProjectExpectedStudyInfoManager {


  private ProjectExpectedStudyInfoDAO projectExpectedStudyInfoDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectExpectedStudyInfoManagerImpl(ProjectExpectedStudyInfoDAO projectExpectedStudyInfoDAO,
    PhaseDAO phaseDAO) {
    this.projectExpectedStudyInfoDAO = projectExpectedStudyInfoDAO;
    this.phaseDAO = phaseDAO;
  }

  @Override
  public void deleteProjectExpectedStudyInfo(long projectExpectedStudyInfoId) {

    projectExpectedStudyInfoDAO.deleteProjectExpectedStudyInfo(projectExpectedStudyInfoId);
  }

  @Override
  public boolean existProjectExpectedStudyInfo(long projectExpectedStudyInfoID) {

    return projectExpectedStudyInfoDAO.existProjectExpectedStudyInfo(projectExpectedStudyInfoID);
  }

  @Override
  public List<ProjectExpectedStudyInfo> findAll() {

    return projectExpectedStudyInfoDAO.findAll();

  }


  @Override
  public ProjectExpectedStudyInfo getProjectExpectedStudyInfoById(long projectExpectedStudyInfoID) {

    return projectExpectedStudyInfoDAO.find(projectExpectedStudyInfoID);
  }

  @Override
  public List<ProjectExpectedStudyInfo> getProjectExpectedStudyInfoByPhase(Phase phase) {
    return projectExpectedStudyInfoDAO.getProjectExpectedStudyInfoByPhase(phase);
  }

  /**
   * Reply the information to the next Phases
   * 
   * @param next - The next Phase
   * @param projectExpectedStudyid - The project expected study number id into the database.
   * @param projectExpectedStudyInfo - The update project expected study info to the current phase.
   */
  public void saveInfoPhase(Phase next, long projectExpectedStudyid,
    ProjectExpectedStudyInfo projectExpectedStudyInfo) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectExpectedStudyInfo> projectExpectedStudyInfos = phase.getProjectExpectedStudyInfos().stream()
      .filter(c -> c.getProjectExpectedStudy().getId().longValue() == projectExpectedStudyid)
      .collect(Collectors.toList());
    if (!projectExpectedStudyInfos.isEmpty()) {
      for (ProjectExpectedStudyInfo projectExpectedStudyPhase : projectExpectedStudyInfos) {
        projectExpectedStudyPhase.updateProjectExpectedStudyInfoInfo(projectExpectedStudyInfo, phase);
        projectExpectedStudyInfoDAO.save(projectExpectedStudyPhase);
      }
    } else {
      ProjectExpectedStudyInfo projectExpectedStudyInfoAdd = new ProjectExpectedStudyInfo();
      projectExpectedStudyInfoAdd.setProjectExpectedStudy(projectExpectedStudyInfo.getProjectExpectedStudy());
      projectExpectedStudyInfoAdd.updateProjectExpectedStudyInfoInfo(projectExpectedStudyInfo, phase);
      projectExpectedStudyInfoAdd.setPhase(phase);
      projectExpectedStudyInfoDAO.save(projectExpectedStudyInfoAdd);
    }


    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectExpectedStudyid, projectExpectedStudyInfo);
    }
  }

  @Override
  public ProjectExpectedStudyInfo saveProjectExpectedStudyInfo(ProjectExpectedStudyInfo projectExpectedStudyInfo) {


    ProjectExpectedStudyInfo sourceInfo = projectExpectedStudyInfoDAO.save(projectExpectedStudyInfo);
    Phase phase = phaseDAO.find(sourceInfo.getPhase().getId());

    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (phase.getNext() != null && phase.getNext().getNext() != null) {
        Phase upkeepPhase = phase.getNext().getNext();
        if (upkeepPhase != null) {
          this.saveInfoPhase(upkeepPhase, projectExpectedStudyInfo.getProjectExpectedStudy().getId(),
            projectExpectedStudyInfo);
        }
      }
    } else {
      if (projectExpectedStudyInfo.getPhase().getNext() != null) {
        this.saveInfoPhase(projectExpectedStudyInfo.getPhase().getNext(),
          projectExpectedStudyInfo.getProjectExpectedStudy().getId(), projectExpectedStudyInfo);
      }
    }


    return sourceInfo;


  }


}
