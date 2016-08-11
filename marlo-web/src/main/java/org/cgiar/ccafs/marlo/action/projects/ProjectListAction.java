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

package org.cgiar.ccafs.marlo.action.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectFocus;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class ProjectListAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  private Crp loggedCrp;

  // Managers
  private ProjectManager projectManager;
  private CrpManager crpManager;

  // Front-end
  private List<Project> myProjects;
  private List<Project> allProjects;


  @Inject
  public ProjectListAction(APConfig config, ProjectManager projectManager, CrpManager crpManager) {
    super(config);
    this.projectManager = projectManager;
    this.crpManager = crpManager;
  }

  public List<Project> getAllProjects() {
    return allProjects;
  }


  public List<Project> getMyProjects() {
    return myProjects;
  }

  @Override
  public void prepare() throws Exception {

    loggedCrp = (Crp) this.getSession().get(APConstants.SESSION_CRP);
    loggedCrp = crpManager.getCrpById(loggedCrp.getId());

    if (projectManager.findAll() != null) {

      if (this.canAccessSuperAdmin() || this.canAcessCrpAdmin()) {
        myProjects = loggedCrp.getProjects().stream().filter(p -> p.isActive()).collect(Collectors.toList());
        for (Project project : myProjects) {
          List<CrpProgram> programs = new ArrayList<>();
          for (ProjectFocus projectFocuses : project.getProjectFocuses().stream()
            .filter(
              c -> c.isActive() && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
            .collect(Collectors.toList())) {
            programs.add(projectFocuses.getCrpProgram());
          }
          project.setFlagships(programs);
        }
      } else {
        allProjects = loggedCrp.getProjects().stream().filter(p -> p.isActive()).collect(Collectors.toList());
        myProjects = projectManager.getUserProjects(this.getCurrentUser().getId(), loggedCrp.getAcronym());
        Collections.sort(myProjects, (p1, p2) -> p1.getId().compareTo(p2.getId()));

        allProjects.removeAll(myProjects);
      }

    }

  }


  @Override
  public String save() {
    return SUCCESS;
  }

  public void setAllProjects(List<Project> allProjects) {
    this.allProjects = allProjects;
  }


  public void setMyProjects(List<Project> myProjects) {
    this.myProjects = myProjects;
  }

  @Override
  public void validate() {
    if (save) {

    }
  }

}