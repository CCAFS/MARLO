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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class ProjectListAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private List<Map<String, String>> projects;
  private String cycle;
  private Crp loggedCrp;
  private List<Project> allProjects;


  private CrpManager crpManager;

  @Inject
  public ProjectListAction(APConfig config, CrpManager crpManager) {
    super(config);
    this.crpManager = crpManager;

  }


  @Override
  public String execute() throws Exception {
    projects = new ArrayList<Map<String, String>>();
    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    if (!cycle.equals(APConstants.REPORTING)) {
      allProjects = loggedCrp.getProjects().stream()
        .filter(p -> p.isActive() && p.getStatus() != null
          && p.getStatus().intValue() == Integer.parseInt(ProjectStatusEnum.Ongoing.getStatusId()))
        .collect(Collectors.toList());
    } else {
      allProjects = loggedCrp.getProjects().stream()
        .filter(
          p -> p.isActive() && p.getStatus() != null && p.getReporting() != null && p.getReporting().booleanValue())
        .collect(Collectors.toList());
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
    Map<String, Object> parameters = this.getParameters();
    cycle = (StringUtils.trim(((String[]) parameters.get(APConstants.CYCLE))[0]));
  }


  public void setProjects(List<Map<String, String>> projects) {
    this.projects = projects;
  }


}
