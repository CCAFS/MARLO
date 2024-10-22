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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovationPartnerType;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ProjectInnovationPartnerTypeManager {


  /**
   * This method removes a specific projectInnovationPartnerType value from the database.
   * 
   * @param projectInnovationPartnerTypeId is the projectInnovationPartnerType identifier.
   * @return true if the projectInnovationPartnerType was successfully deleted, false otherwise.
   */
  public void deleteProjectInnovationPartnerType(long projectInnovationPartnerTypeId);


  /**
   * This method validate if the projectInnovationPartnerType identify with the given id exists in the system.
   * 
   * @param projectInnovationPartnerTypeID is a projectInnovationPartnerType identifier.
   * @return true if the projectInnovationPartnerType exists, false otherwise.
   */
  public boolean existProjectInnovationPartnerType(long projectInnovationPartnerTypeID);


  /**
   * This method gets a list of projectInnovationPartnerType that are active
   * 
   * @return a list from ProjectInnovationPartnerType null if no exist records
   */
  public List<ProjectInnovationPartnerType> findAll();


  /**
   * This method gets a projectInnovationPartnerType object by a given projectInnovationPartnerType identifier.
   * 
   * @param projectInnovationPartnerTypeID is the projectInnovationPartnerType identifier.
   * @return a ProjectInnovationPartnerType object.
   */
  public ProjectInnovationPartnerType getProjectInnovationPartnerTypeById(long projectInnovationPartnerTypeID);

  /**
   * This method saves the information of the given projectInnovationPartnerType
   * 
   * @param projectInnovationPartnerType - is the projectInnovationPartnerType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the projectInnovationPartnerType was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectInnovationPartnerType saveProjectInnovationPartnerType(ProjectInnovationPartnerType projectInnovationPartnerType);


}
