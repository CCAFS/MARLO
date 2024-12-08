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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationAllianceOrganization;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationAllianceOrganizationManager {


  /**
   * This method removes a specific projectInnovationAllianceOrganization value from the database.
   * 
   * @param projectInnovationAllianceOrganizationId is the projectInnovationAllianceOrganization identifier.
   * @return true if the projectInnovationAllianceOrganization was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationAllianceOrganization(long projectInnovationAllianceOrganizationId);


  /**
   * This method validate if the projectInnovationAllianceOrganization identify with the given id exists in the system.
   * 
   * @param projectInnovationAllianceOrganizationID is a projectInnovationAllianceOrganization identifier.
   * @return true if the projectInnovationAllianceOrganization exists, false otherwise.
   */
  public boolean existProjectInnovationAllianceOrganization(long projectInnovationAllianceOrganizationID);


  /**
   * This method gets a list of projectInnovationAllianceOrganization that are active
   * 
   * @return a list from ProjectInnovationAllianceOrganization null if no exist records
   */
  public List<ProjectInnovationAllianceOrganization> findAll();

  /**
   * This method gets a projectInnovationAllianceOrganization object by a given projectInnovationAllianceOrganization
   * identifier.
   * 
   * @param projectInnovationAllianceOrganizationID is the projectInnovationAllianceOrganization identifier.
   * @return a ProjectInnovationAllianceOrganization object.
   */
  public ProjectInnovationAllianceOrganization
    getProjectInnovationAllianceOrganizationById(long projectInnovationAllianceOrganizationID);


  /**
   * This method gets a list of projectInnovationAllianceOrganization by a given innovationID and phaseID that are
   * active
   * 
   * @return a list from ProjectInnovationAllianceOrganization null if no exist records
   */
  public List<ProjectInnovationAllianceOrganization>
    getProjectInnovationAllianceOrganizationsByInnovationAndPhase(long innovationID, long phaseID);

  /**
   * This method saves the information of the given projectInnovationAllianceOrganization
   * 
   * @param projectInnovationAllianceOrganization - is the projectInnovationAllianceOrganization object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectInnovationAllianceOrganization was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationAllianceOrganization saveProjectInnovationAllianceOrganization(
    ProjectInnovationAllianceOrganization projectInnovationAllianceOrganization);


}
