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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnership;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationPartnershipManager {


  /**
   * This method removes a specific projectInnovationPartnership value from the database.
   * 
   * @param projectInnovationPartnershipId is the projectInnovationPartnership identifier.
   * @return true if the projectInnovationPartnership was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationPartnership(long projectInnovationPartnershipId);


  /**
   * This method validate if the projectInnovationPartnership identify with the given id exists in the system.
   * 
   * @param projectInnovationPartnershipID is a projectInnovationPartnership identifier.
   * @return true if the projectInnovationPartnership exists, false otherwise.
   */
  public boolean existProjectInnovationPartnership(long projectInnovationPartnershipID);


  /**
   * This method gets a list of projectInnovationPartnership that are active
   * 
   * @return a list from ProjectInnovationPartnership null if no exist records
   */
  public List<ProjectInnovationPartnership> findAll();


  /**
   * This method gets a projectInnovationPartnership object by a given projectInnovationPartnership identifier.
   * 
   * @param projectInnovationPartnershipID is the projectInnovationPartnership identifier.
   * @return a ProjectInnovationPartnership object.
   */
  public ProjectInnovationPartnership getProjectInnovationPartnershipById(long projectInnovationPartnershipID);

  /**
   * This method saves the information of the given projectInnovationPartnership
   * 
   * @param projectInnovationPartnership - is the projectInnovationPartnership object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationPartnership was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationPartnership saveProjectInnovationPartnership(ProjectInnovationPartnership projectInnovationPartnership);


}
