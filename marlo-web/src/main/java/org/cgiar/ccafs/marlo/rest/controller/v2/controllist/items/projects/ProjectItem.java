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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.projects;

import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.rest.dto.ProjectDTO;
import org.cgiar.ccafs.marlo.rest.mappers.ProjectMapper;

import java.util.Optional;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */


public class ProjectItem<T> {

  private ProjectManager projectManager;
  private ProjectMapper projectMapper;

  @Inject
  public ProjectItem(ProjectManager projectManager, ProjectMapper projectMapper) {
    this.projectManager = projectManager;
    this.projectMapper = projectMapper;
  }

  /**
   * find a project requesting by Id
   * 
   * @param id
   * @return
   */
  public ResponseEntity<ProjectDTO> findProjectById(Long id, GlobalUnit globalUnit) {

    Project project = projectManager.getProjectById(id);

    if (project != null) {
      // TODO
    }

    return Optional.ofNullable(project).map(this.projectMapper::projectToProjectDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * for this method get the project information
   * 
   * @param project
   * @return
   */
  public Project getProjectInformation(Project project) {


    return project;
  }

}
