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
import org.cgiar.ccafs.marlo.data.dao.ProjectHighlightInfoDAO;
import org.cgiar.ccafs.marlo.data.manager.ProjectHighlightInfoManager;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectHighlightInfo;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Christian Garcia
 */
@Named
public class ProjectHighlightInfoManagerImpl implements ProjectHighlightInfoManager {


  private ProjectHighlightInfoDAO projectHighlightInfoDAO;
  // Managers
  private PhaseDAO phaseDAO;


  @Inject
  public ProjectHighlightInfoManagerImpl(ProjectHighlightInfoDAO projectHighlightInfoDAO, PhaseDAO phaseDAO) {
    this.projectHighlightInfoDAO = projectHighlightInfoDAO;
    this.phaseDAO = phaseDAO;


  }

  @Override
  public void deleteProjectHighlightInfo(long projectHighlightInfoId) {

    projectHighlightInfoDAO.deleteProjectHighlightInfo(projectHighlightInfoId);
  }

  @Override
  public boolean existProjectHighlightInfo(long projectHighlightInfoID) {

    return projectHighlightInfoDAO.existProjectHighlightInfo(projectHighlightInfoID);
  }

  @Override
  public List<ProjectHighlightInfo> findAll() {

    return projectHighlightInfoDAO.findAll();

  }

  @Override
  public ProjectHighlightInfo getProjectHighlightInfoById(long projectHighlightInfoID) {

    return projectHighlightInfoDAO.find(projectHighlightInfoID);
  }

  public void saveInfoPhase(Phase next, long projectHighlightid, ProjectHighlightInfo projectHighlightInfo) {
    Phase phase = phaseDAO.find(next.getId());

    List<ProjectHighlightInfo> projectHighlightInfos = phase.getProjectHighlightInfos().stream()
      .filter(c -> c.getProjectHighlight().getId().longValue() == projectHighlightid).collect(Collectors.toList());
    if (!projectHighlightInfos.isEmpty()) {
      for (ProjectHighlightInfo projectHighlightInfoPhase : projectHighlightInfos) {
        projectHighlightInfoPhase.updateProjectHighlightInfo(projectHighlightInfo, phase);
        projectHighlightInfoDAO.save(projectHighlightInfoPhase);


      }
    } else {


      // if (cal.get(Calendar.YEAR) >= phase.getYear()) {
      ProjectHighlightInfo projectHighlightInfoAdd = new ProjectHighlightInfo();
      projectHighlightInfoAdd.setProjectHighlight(projectHighlightInfo.getProjectHighlight());
      projectHighlightInfoAdd.updateProjectHighlightInfo(projectHighlightInfo, phase);
      projectHighlightInfoAdd.setPhase(phase);
      projectHighlightInfoDAO.save(projectHighlightInfoAdd);

      // }


    }


    if (phase.getNext() != null) {
      this.saveInfoPhase(phase.getNext(), projectHighlightid, projectHighlightInfo);
    }
  }

  @Override
  public ProjectHighlightInfo saveProjectHighlightInfo(ProjectHighlightInfo projectHighlightInfo) {

    ProjectHighlightInfo sourceInfo = projectHighlightInfoDAO.save(projectHighlightInfo);
    Phase phase = phaseDAO.find(sourceInfo.getPhase().getId());
    if (phase.getDescription().equals(APConstants.REPORTING)) {
      if (projectHighlightInfo.getPhase().getNext() != null) {
        this.saveInfoPhase(projectHighlightInfo.getPhase().getNext(),
          projectHighlightInfo.getProjectHighlight().getId(), projectHighlightInfo);
      }
    }
    return sourceInfo;
  }


}
