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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnershipPerson;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationPartnershipPersonManager {


  /**
   * This method removes a specific projectInnovationPartnershipPerson value from the database.
   * 
   * @param projectInnovationPartnershipPersonId is the projectInnovationPartnershipPerson identifier.
   * @return true if the projectInnovationPartnershipPerson was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationPartnershipPerson(long projectInnovationPartnershipPersonId);


  /**
   * This method validate if the projectInnovationPartnershipPerson identify with the given id exists in the system.
   * 
   * @param projectInnovationPartnershipPersonID is a projectInnovationPartnershipPerson identifier.
   * @return true if the projectInnovationPartnershipPerson exists, false otherwise.
   */
  public boolean existProjectInnovationPartnershipPerson(long projectInnovationPartnershipPersonID);


  /**
   * This method gets a list of projectInnovationPartnershipPerson that are active
   * 
   * @return a list from ProjectInnovationPartnershipPerson null if no exist records
   */
  public List<ProjectInnovationPartnershipPerson> findAll();


  /**
   * This method gets a projectInnovationPartnershipPerson object by a given projectInnovationPartnershipPerson identifier.
   * 
   * @param projectInnovationPartnershipPersonID is the projectInnovationPartnershipPerson identifier.
   * @return a ProjectInnovationPartnershipPerson object.
   */
  public ProjectInnovationPartnershipPerson getProjectInnovationPartnershipPersonById(long projectInnovationPartnershipPersonID);

  /**
   * This method saves the information of the given projectInnovationPartnershipPerson
   * 
   * @param projectInnovationPartnershipPerson - is the projectInnovationPartnershipPerson object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationPartnershipPerson was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationPartnershipPerson saveProjectInnovationPartnershipPerson(ProjectInnovationPartnershipPerson projectInnovationPartnershipPerson);


}
