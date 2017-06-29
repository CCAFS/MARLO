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
package org.cgiar.ccafs.marlo.data.manager;

import org.cgiar.ccafs.marlo.data.manager.impl.ProjectManagerImpl;
import org.cgiar.ccafs.marlo.data.model.Project;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(ProjectManagerImpl.class)
public interface ProjectManager {


  /**
   * This method removes a specific project value from the database.
   * 
   * @param projectId is the project identifier.
   * @return true if the project was successfully deleted, false otherwise.
   */
  public boolean deleteProject(Project project);


  /**
   * This method validate if the project identify with the given id exists in the system.
   * 
   * @param projectID is a project identifier.
   * @return true if the project exists, false otherwise.
   */
  public boolean existProject(long projectID);


  /**
   * This method gets a list of project that are active
   * 
   * @return a list from Project null if no exist records
   */
  public List<Project> findAll();


  /**
   * This method gets a project object by a given project identifier.
   * 
   * @param projectID is the project identifier.
   * @return a Project object.
   */
  public Project getProjectById(long projectID);

  /**
   * Get the list of projects that the user can access and edit.
   * 
   * @param userId - The user Id.
   * @param crp - The crp acronym that the user has logged.
   * @return a Project List.
   */
  public List<Project> getUserProjects(long userId, String crp);

  public List<Project> getUserProjectsReporting(long userId, String crp);

  /**
   * This method saves the information of the given project
   * 
   * @param project - is the project object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the project was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveProject(Project project);

  public long saveProject(Project project, String section, List<String> relationsName);


}
