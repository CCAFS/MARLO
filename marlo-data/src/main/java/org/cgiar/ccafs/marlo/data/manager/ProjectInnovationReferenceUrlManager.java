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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationReferenceUrl;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationReferenceUrlManager {


  /**
   * This method removes a specific projectInnovationReferenceUrl value from the database.
   * 
   * @param projectInnovationReferenceUrlId is the projectInnovationReferenceUrl identifier.
   * @return true if the projectInnovationReferenceUrl was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationReferenceUrl(long projectInnovationReferenceUrlId);


  /**
   * This method validate if the projectInnovationReferenceUrl identify with the given id exists in the system.
   * 
   * @param projectInnovationReferenceUrlID is a projectInnovationReferenceUrl identifier.
   * @return true if the projectInnovationReferenceUrl exists, false otherwise.
   */
  public boolean existProjectInnovationReferenceUrl(long projectInnovationReferenceUrlID);


  /**
   * This method gets a list of projectInnovationReferenceUrl that are active
   * 
   * @return a list from ProjectInnovationReferenceUrl null if no exist records
   */
  public List<ProjectInnovationReferenceUrl> findAll();


  /**
   * This method gets a projectInnovationReferenceUrl object by a given projectInnovationReferenceUrl identifier.
   * 
   * @param projectInnovationReferenceUrlID is the projectInnovationReferenceUrl identifier.
   * @return a ProjectInnovationReferenceUrl object.
   */
  public ProjectInnovationReferenceUrl getProjectInnovationReferenceUrlById(long projectInnovationReferenceUrlID);

  /**
   * This method saves the information of the given projectInnovationReferenceUrl
   * 
   * @param projectInnovationReferenceUrl - is the projectInnovationReferenceUrl object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationReferenceUrl was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationReferenceUrl saveProjectInnovationReferenceUrl(ProjectInnovationReferenceUrl projectInnovationReferenceUrl);


}
