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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPhase;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class ProjectListAction extends BaseSummariesAction {

  /**
   * 
   */
  private static final long serialVersionUID = -4335064142194555431L;
  private List<Map<String, String>> projects;


  private List<Project> allProjects;


  private CrpManager crpManager;


  private PhaseManager phaseManager;


  @Inject
  public ProjectListAction(APConfig config, CrpManager crpManager, PhaseManager phaseManager) {
    super(config, crpManager, phaseManager);
    this.crpManager = crpManager;
    this.phaseManager = phaseManager;

  }


  @Override
  public String execute() throws Exception {
    projects = new ArrayList<Map<String, String>>();


    allProjects = new ArrayList<>();
    if (this.getSelectedPhase() != null && this.getSelectedPhase().getProjectPhases().size() > 0) {
      for (ProjectPhase projectPhase : this.getSelectedPhase().getProjectPhases().stream()
        .filter(pf -> pf.isActive() && pf.getProject().isActive())
        .sorted((pf1, pf2) -> pf1.getProject().getId().compareTo(pf2.getProject().getId()))
        .collect(Collectors.toList())) {
        allProjects.add((projectPhase.getProject()));
      }
      for (Project project : allProjects) {
        Map<String, String> projectInfo = new HashMap<String, String>();
        projectInfo.put("id", project.getId().toString());
        projectInfo.put("description", project.getProjecInfoPhase(this.getSelectedPhase()).getTitle());
        projects.add(projectInfo);
      }
    }
    System.out.println(projects.size());
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
