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
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

/**
 * @author Sebastian Amariles - CIAT/CCAFS
 */
public class ProjectListAction extends BaseAction {

  private static final long serialVersionUID = -793652591843623397L;

  // Managers
  private ProjectManager projectManager;

  // Front-end
  private List<Project> myProjects;

  private List<Project> allProjects;


  @Inject
  public ProjectListAction(APConfig config, ProjectManager projectManager) {
    super(config);
    this.projectManager = projectManager;
  }

  public List<Project> getAllProjects() {
    return allProjects;
  }


  public List<Project> getMyProjects() {
    return myProjects;
  }

  @Override
  public void prepare() throws Exception {

    myProjects = new ArrayList<>();
    // TODO: Projects that the user have privileges

    allProjects = new ArrayList<>();
    if (projectManager.findAll() != null) {
      List<Project> projects = projectManager.findAll().stream().filter(c -> c.isActive()).collect(Collectors.toList());
      for (Project project : projects) {
        allProjects.add(project);
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