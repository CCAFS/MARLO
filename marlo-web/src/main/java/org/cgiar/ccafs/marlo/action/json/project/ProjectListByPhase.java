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

import org.cgiar.ccafs.marlo.action.summaries.BaseSummariesAction;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

public class ProjectListByPhase extends BaseSummariesAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private List<Map<String, String>> projects;


  @Inject
  public ProjectListByPhase(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    ProjectManager projectManager) {
    super(config, crpManager, phaseManager, projectManager);

  }


  @Override
  public String execute() throws Exception {
    projects = new ArrayList<Map<String, String>>();

    if (this.getSelectedPhase() != null && this.getSelectedPhase().getProjectPhases().size() > 0) {
      // Status of projects
      String[] statuses = null;

      // Get projects with the status defined
      List<Project> allProjects = this.getActiveProjectsByPhase(this.getSelectedPhase(), 0, statuses);

      for (Project project : allProjects) {
        Map<String, String> projectInfo = new HashMap<String, String>();
        projectInfo.put("id", project.getId().toString());
        if (project.getProjecInfoPhase(this.getSelectedPhase()) != null
          && project.getProjecInfoPhase(this.getSelectedPhase()).getTitle() != null) {
          projectInfo.put("description", project.getProjecInfoPhase(this.getSelectedPhase()).getTitle());
        } else {
          projectInfo.put("description", "");
        }
        projects.add(projectInfo);
      }
    }
    return SUCCESS;

  }


  public List<Map<String, String>> getProjects() {
    return projects;
  }


  @Override
  public void prepare() throws Exception {
    this.setGeneralParameters();
  }


  public void setProjects(List<Map<String, String>> projects) {
    this.projects = projects;
  }


}
