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

import org.cgiar.ccafs.marlo.data.model.ProjectPartnerPartnershipLocation;

import java.util.List;


public interface ProjectPartnerPartnershipLocationDAO {

  /**
   * This method removes a specific projectPartnerPartnershipLocation value from the database.
   * 
   * @param projectPartnerPartnershipLocationId is the projectPartnerPartnershipLocation identifier.
   * @return true if the projectPartnerPartnershipLocation was successfully deleted, false otherwise.
   */
  public void deleteProjectPartnerPartnershipLocation(long projectPartnerPartnershipLocationId);

  /**
   * This method validate if the projectPartnerPartnershipLocation identify with the given id exists in the system.
   * 
   * @param projectPartnerPartnershipLocationID is a projectPartnerPartnershipLocation identifier.
   * @return true if the projectPartnerPartnershipLocation exists, false otherwise.
   */
  public boolean existProjectPartnerPartnershipLocation(long projectPartnerPartnershipLocationID);

  /**
   * This method gets a projectPartnerPartnershipLocation object by a given projectPartnerPartnershipLocation
   * identifier.
   * 
   * @param projectPartnerPartnershipLocationID is the projectPartnerPartnershipLocation identifier.
   * @return a ProjectPartnerPartnershipLocation object.
   */
  public ProjectPartnerPartnershipLocation find(long id);

  /**
   * This method gets a list of projectPartnerPartnershipLocation that are active
   * 
   * @return a list from ProjectPartnerPartnershipLocation null if no exist records
   */
  public List<ProjectPartnerPartnershipLocation> findAll();


  public List<ProjectPartnerPartnershipLocation> findParnershipLocationByPartnership(long projectPartnerPartnershipnId);

  /**
   * This method saves the information of the given projectPartnerPartnershipLocation
   * 
   * @param projectPartnerPartnershipLocation - is the projectPartnerPartnershipLocation object with the new information
   *        to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         projectPartnerPartnershipLocation was
   *         updated
   *         or -1 is some error occurred.
   */
  public ProjectPartnerPartnershipLocation save(ProjectPartnerPartnershipLocation projectPartnerPartnershipLocation);
}
