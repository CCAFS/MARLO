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


package org.cgiar.ccafs.marlo.action.center.summaries;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.ICenterAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProgramManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterProjectManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterArea;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class SummaryListAction extends BaseAction {


  /**
   * 
   */
  private static final long serialVersionUID = -4099604925246954828L;


  private List<CenterProject> allProjects;

  private List<CenterProgram> programs;

  private List<CenterArea> researchAreas;

  private Center loggedCenter;
  private final ICenterManager centerService;

  private final ICenterProgramManager programService;
  private final ICenterProjectManager projectService;
  private final ICenterAreaManager researchAreaService;

  @Inject
  public SummaryListAction(APConfig config, ICenterManager centerService, ICenterProgramManager programService,
    ICenterProjectManager projectService, ICenterAreaManager researchAreaService) {
    super(config);
    this.centerService = centerService;
    this.programService = programService;
    this.projectService = projectService;
    this.researchAreaService = researchAreaService;
  }

  public List<CenterProject> getAllProjects() {
    return allProjects;
  }


  public List<CenterProgram> getPrograms() {
    return programs;
  }

  public List<CenterArea> getResearchAreas() {
    return researchAreas;
  }


  @Override
  public void prepare() throws Exception {
    loggedCenter = (Center) this.getSession().get(APConstants.SESSION_CENTER);
    loggedCenter = centerService.getCrpById(loggedCenter.getId());

    allProjects = new ArrayList<CenterProject>(
      projectService.findAll().stream().filter(p -> p.isActive()).collect(Collectors.toList()));

    programs =
      new ArrayList<>(programService.findAll().stream().filter(p -> p.isActive()).collect(Collectors.toList()));

    researchAreas = new ArrayList<CenterArea>(
      researchAreaService.findAll().stream().filter(ra -> ra.isActive()).collect(Collectors.toList()));


  }

  public void setAllProjects(List<CenterProject> allProjects) {
    this.allProjects = allProjects;
  }


  public void setPrograms(List<CenterProgram> programs) {
    this.programs = programs;
  }


  public void setResearchAreas(List<CenterArea> researchAreas) {
    this.researchAreas = researchAreas;
  }

}
