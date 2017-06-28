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


package org.cgiar.ccafs.marlo.action.summaries;


import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConfig;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.service.ICenterService;
import org.cgiar.ccafs.marlo.data.service.ICenterProgramService;
import org.cgiar.ccafs.marlo.utils.APConstants;

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

  private Center loggedCenter;
  private ICenterService centerService;

  private ICenterProgramService programService;

  @Inject
  public SummaryListAction(APConfig config, ICenterService centerService, ICenterProgramService programService) {
    super(config);
    this.centerService = centerService;
    this.programService = programService;
  }

  public List<CenterProject> getAllProjects() {
    return allProjects;
  }


  public List<CenterProgram> getPrograms() {
    return programs;
  }

  @Override
  public void prepare() throws Exception {
    loggedCenter = (Center) this.getSession().get(APConstants.SESSION_CENTER);
    loggedCenter = centerService.getCrpById(loggedCenter.getId());

    allProjects = new ArrayList<CenterProject>();

    programs =
      new ArrayList<>(programService.findAll().stream().filter(p -> p.isActive()).collect(Collectors.toList()));


  }


  public void setAllProjects(List<CenterProject> allProjects) {
    this.allProjects = allProjects;
  }

  public void setPrograms(List<CenterProgram> programs) {
    this.programs = programs;
  }

}
