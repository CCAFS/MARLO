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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganization;

import java.util.List;


public interface ProjectInnovationContributingOrganizationDAO {

  /**
   * This method removes a specific projectInnovationContributingOrganization value from the database.
   * 
   * @param projectInnovationContributingOrganizationId is the projectInnovationContributingOrganization identifier.
   * @return true if the projectInnovationContributingOrganization was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationContributingOrganization(long projectInnovationContributingOrganizationId);

  /**
   * This method validate if the projectInnovationContributingOrganization identify with the given id exists in the system.
   * 
   * @param projectInnovationContributingOrganizationID is a projectInnovationContributingOrganization identifier.
   * @return true if the projectInnovationContributingOrganization exists, false otherwise.
   */
  public boolean existProjectInnovationContributingOrganization(long projectInnovationContributingOrganizationID);

  /**
   * This method gets a projectInnovationContributingOrganization object by a given projectInnovationContributingOrganization identifier.
   * 
   * @param projectInnovationContributingOrganizationID is the projectInnovationContributingOrganization identifier.
   * @return a ProjectInnovationContributingOrganization object.
   */
  public ProjectInnovationContributingOrganization find(long id);

  /**
   * This method gets a list of projectInnovationContributingOrganization that are active
   * 
   * @return a list from ProjectInnovationContributingOrganization null if no exist records
   */
  public List<ProjectInnovationContributingOrganization> findAll();


  /**
   * This method saves the information of the given projectInnovationContributingOrganization
   * 
   * @param projectInnovationContributingOrganization - is the projectInnovationContributingOrganization object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationContributingOrganization was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationContributingOrganization save(ProjectInnovationContributingOrganization projectInnovationContributingOrganization);
}
