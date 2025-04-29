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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganizationRole;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationContributingOrganizationRoleManager {


  /**
   * This method removes a specific projectInnovationContributingOrganizationRole value from the database.
   * 
   * @param projectInnovationContributingOrganizationRoleId is the projectInnovationContributingOrganizationRole identifier.
   * @return true if the projectInnovationContributingOrganizationRole was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationContributingOrganizationRole(long projectInnovationContributingOrganizationRoleId);


  /**
   * This method validate if the projectInnovationContributingOrganizationRole identify with the given id exists in the system.
   * 
   * @param projectInnovationContributingOrganizationRoleID is a projectInnovationContributingOrganizationRole identifier.
   * @return true if the projectInnovationContributingOrganizationRole exists, false otherwise.
   */
  public boolean existProjectInnovationContributingOrganizationRole(long projectInnovationContributingOrganizationRoleID);


  /**
   * This method gets a list of projectInnovationContributingOrganizationRole that are active
   * 
   * @return a list from ProjectInnovationContributingOrganizationRole null if no exist records
   */
  public List<ProjectInnovationContributingOrganizationRole> findAll();


  /**
   * This method gets a projectInnovationContributingOrganizationRole object by a given projectInnovationContributingOrganizationRole identifier.
   * 
   * @param projectInnovationContributingOrganizationRoleID is the projectInnovationContributingOrganizationRole identifier.
   * @return a ProjectInnovationContributingOrganizationRole object.
   */
  public ProjectInnovationContributingOrganizationRole getProjectInnovationContributingOrganizationRoleById(long projectInnovationContributingOrganizationRoleID);

  /**
   * This method saves the information of the given projectInnovationContributingOrganizationRole
   * 
   * @param projectInnovationContributingOrganizationRole - is the projectInnovationContributingOrganizationRole object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationContributingOrganizationRole was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationContributingOrganizationRole saveProjectInnovationContributingOrganizationRole(ProjectInnovationContributingOrganizationRole projectInnovationContributingOrganizationRole);


}
