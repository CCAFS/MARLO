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


package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;

public class ProjectListAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private List<Map<String, String>> projects;
  private String cycle;
  private int year;

  private GlobalUnit loggedCrp;
  private List<Project> allProjects;


  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private PhaseManager phaseManager;

  @Inject
  public ProjectListAction(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager) {
    super(config);
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;

  }


  @Override
  public String execute() throws Exception {
    projects = new ArrayList<Map<String, String>>();
    loggedCrp = (GlobalUnit) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getGlobalUnitById(loggedCrp.getId());


    allProjects = new ArrayList<>();
    Phase phase = phaseManager.findCycle(cycle, year, loggedCrp.getId().longValue());
    for (ProjectPhase projectPhase : phase.getProjectPhases()) {
      allProjects.add((projectPhase.getProject()));
    }
    for (Project project : allProjects) {
      Map<String, String> projectInfo = new HashMap<String, String>();
      projectInfo.put("id", project.getId().toString());
      projectInfo.put("description", project.getTitle());

      projects.add(projectInfo);
    }
    return SUCCESS;

  }


  public List<Map<String, String>> getProjects() {
    return projects;
  }


  @Override
  public void prepare() throws Exception {
    // Map<String, Object> parameters = this.getParameters();
    // cycle = (StringUtils.trim(((String[]) parameters.get(APConstants.CYCLE))[0]));
    // year = Integer.parseInt((StringUtils.trim(((String[]) parameters.get(APConstants.YEAR_REQUEST))[0])));

    Map<String, Parameter> parameters = this.getParameters();
    cycle = (StringUtils.trim(parameters.get(APConstants.CYCLE).getMultipleValues()[0]));
    year = Integer.parseInt((StringUtils.trim(parameters.get(APConstants.YEAR_REQUEST).getMultipleValues()[0])));
  }


  public void setProjects(List<Map<String, String>> projects) {
    this.projects = projects;
  }


}
