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

package org.cgiar.ccafs.marlo.action.center.monitoring.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class ProjectCapdevAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private long capdevID;
  private long programID;
  private long projectID;
  private CenterProgram selectedProgram;

  private List<CapacityDevelopment> capdevs;

  private ICenterProjectManager projectService;
  private final ICapacityDevelopmentService capdevService;

  @Inject
  public ProjectCapdevAction(APConfig config, ICenterProjectManager projectService,
    ICapacityDevelopmentService capdevService) {
    super(config);
    this.projectService = projectService;
    this.capdevService = capdevService;
  }

  public long getCapdevID() {
    return capdevID;
  }


  public List<CapacityDevelopment> getCapdevs() {
    return capdevs;
  }


  public long getProgramID() {
    return programID;
  }


  public long getProjectID() {
    return projectID;
  }


  public CenterProgram getSelectedProgram() {
    return selectedProgram;
  }


  @Override
  public void prepare() throws Exception {
    try {
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_ID)));
    } catch (Exception e) {
      projectID = -1;
    }

    CenterProject project = projectService.getCenterProjectById(projectID);

    if (project != null) {
      CenterProject ProjectDB = projectService.getCenterProjectById(projectID);
      selectedProgram = ProjectDB.getResearchProgram();
      programID = selectedProgram.getId();

      capdevs = capdevService.findAll().stream()
        .filter(c -> (c.getProject() != null) && (c.getProject().getId() == project.getId()) && c.isActive())
        .collect(Collectors.toList());
      Collections.sort(capdevs, (ra1, ra2) -> (int) (ra2.getId() - ra1.getId()));

    }
  }


  public void setCapdevID(long capdevID) {
    this.capdevID = capdevID;
  }


  public void setCapdevs(List<CapacityDevelopment> capdevs) {
    this.capdevs = capdevs;
  }


  public void setProgramID(long programID) {
    this.programID = programID;
  }


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setSelectedProgram(CenterProgram selectedProgram) {
    this.selectedProgram = selectedProgram;
  }


}
