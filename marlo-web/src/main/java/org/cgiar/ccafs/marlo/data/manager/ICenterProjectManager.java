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

import org.cgiar.ccafs.marlo.data.manager.impl.CenterProjectManager;
import org.cgiar.ccafs.marlo.data.model.CenterProject;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(CenterProjectManager.class)
public interface ICenterProjectManager {


  /**
   * This method removes a specific project value from the database.
   * 
   * @param projectId is the project identifier.
   * @return true if the project was successfully deleted, false otherwise.
   */
  public void deleteCenterProject(long projectId);


  /**
   * This method validate if the project identify with the given id exists in the system.
   * 
   * @param projectID is a project identifier.
   * @return true if the project exists, false otherwise.
   */
  public boolean existCenterProject(long projectID);


  /**
   * This method gets a list of project that are active
   * 
   * @return a list from CenterProject null if no exist records
   */
  public List<CenterProject> findAll();


  /**
   * This method gets a project object by a given project identifier.
   * 
   * @param projectID is the project identifier.
   * @return a CenterProject object.
   */
  public CenterProject getCenterProjectById(long projectID);

  /**
   * This method gets a list of projects belongs of the user
   * 
   * @param userId - the user id
   * @return List of Projects or null if the user is invalid or not have roles.
   */
  public List<CenterProject> getCenterProjectsByUserId(Long userId);

  /**
   * This method saves the information of the given project
   * 
   * @param project - is the project object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the project was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterProject saveCenterProject(CenterProject project);

  /**
   * This method saves the information of the given project
   * 
   * @param project - is the project object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the project was
   *         updated
   *         or -1 is some error occurred.
   */
  public CenterProject saveCenterProject(CenterProject project, String actionName, List<String> relationsName);


}
