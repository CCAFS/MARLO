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
import org.cgiar.ccafs.marlo.data.dao.ProjectInnovationInfoDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationInfoManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectInnovationInfoManagerImpl implements ProjectInnovationInfoManager {


  private ProjectInnovationInfoDAO projectInnovationInfoDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectInnovationInfoManagerImpl(ProjectInnovationInfoDAO projectInnovationInfoDAO, PhaseDAO phaseDAO) {
    this.projectInnovationInfoDAO = projectInnovationInfoDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectInnovationInfo(long projectInnovationInfoId) {

    projectInnovationInfoDAO.deleteProjectInnovationInfo(projectInnovationInfoId);
  }

  @Override
  public boolean existProjectInnovationInfo(long projectInnovationInfoID) {

    return projectInnovationInfoDAO.existProjectInnovationInfo(projectInnovationInfoID);
  }

  @Override
  public List<ProjectInnovationInfo> findAll() {

    return projectInnovationInfoDAO.findAll();

  }

  @Override
  public ProjectInnovationInfo getProjectInnovationInfoById(long projectInnovationInfoID) {

    return projectInnovationInfoDAO.find(projectInnovationInfoID);
  }

  @Override
  public List<ProjectInnovationInfo> getProjectInnovationInfoByPhase(Phase phase) {
    return projectInnovationInfoDAO.getProjectInnovationInfoByPhase(phase);
  }

  /**
   * Reply the information to the next Phases
   * 
   * @param next - The next Phase
   * @param projectInnovationid - The project innovation number id into the database.
   * @param projectInnovationInfo - The update project innovation info to the current phase.
   */
  public void saveInfoPhase(Phase next, long projectInnovationid, ProjectInnovationInfo projectInnovationInfo) {

    Phase phase = phaseDAO.find(next.getId());

    List<ProjectInnovationInfo> projectInnovationInfos = phase.getProjectInnovationInfos().stream()
      .filter(c -> c.getProjectInnovation().getId().longValue() == projectInnovationid).collect(Collectors.toList());
    if (!projectInnovationInfos.isEmpty()) {
      for (ProjectInnovationInfo projectInnovationInfoPhase : projectInnovationInfos) {
        projectInnovationInfoPhase.updateProjectInnovationInfo(projectInnovationInfo, phase);
        projectInnovationInfoDAO.save(projectInnovationInfoPhase);
      }
    } else {
      ProjectInnovationInfo projectInnovationInfoAdd = new ProjectInnovationInfo();
      projectInnovationInfoAdd.setProjectInnovation(projectInnovationInfo.getProjectInnovation());
      projectInnovationInfoAdd.updateProjectInnovationInfo(projectInnovationInfo, phase);
      projectInnovationInfoAdd.setLeadOrganization(projectInnovationInfo.getLeadOrganization());
      projectInnovationInfoAdd.setPhase(phase);
      projectInnovationInfoDAO.save(projectInnovationInfoAdd);
    }


    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectInnovationid, projectInnovationInfo);
    }
  }

  @Override
  public ProjectInnovationInfo saveProjectInnovationInfo(ProjectInnovationInfo projectInnovationInfo) {

    ProjectInnovationInfo sourceInfo = projectInnovationInfoDAO.save(projectInnovationInfo);
    Phase phase = phaseDAO.find(sourceInfo.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (projectInnovationInfo.getPhase().getNext() != null) {
        this.saveInfoPhase(projectInnovationInfo.getPhase().getNext(),
          projectInnovationInfo.getProjectInnovation().getId(), projectInnovationInfo);
      }
    }
    return sourceInfo;
  }


}
