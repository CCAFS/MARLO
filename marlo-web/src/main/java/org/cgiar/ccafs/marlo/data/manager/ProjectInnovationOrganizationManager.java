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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationOrganization;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface ProjectInnovationOrganizationManager {


  /**
   * This method removes a specific projectInnovationOrganization value from the database.
   * 
   * @param projectInnovationOrganizationId is the projectInnovationOrganization identifier.
   * @return true if the projectInnovationOrganization was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationOrganization(long projectInnovationOrganizationId);


  /**
   * This method validate if the projectInnovationOrganization identify with the given id exists in the system.
   * 
   * @param projectInnovationOrganizationID is a projectInnovationOrganization identifier.
   * @return true if the projectInnovationOrganization exists, false otherwise.
   */
  public boolean existProjectInnovationOrganization(long projectInnovationOrganizationID);


  /**
   * This method gets a list of projectInnovationOrganization that are active
   * 
   * @return a list from ProjectInnovationOrganization null if no exist records
   */
  public List<ProjectInnovationOrganization> findAll();


  /**
   * This method gets a projectInnovationOrganization object by a given projectInnovationOrganization identifier.
   * 
   * @param projectInnovationOrganizationID is the projectInnovationOrganization identifier.
   * @return a ProjectInnovationOrganization object.
   */
  public ProjectInnovationOrganization getProjectInnovationOrganizationById(long projectInnovationOrganizationID);

  /**
   * This method saves the information of the given projectInnovationOrganization
   * 
   * @param projectInnovationOrganization - is the projectInnovationOrganization object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationOrganization was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationOrganization saveProjectInnovationOrganization(ProjectInnovationOrganization projectInnovationOrganization);


}
