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

import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;

import java.util.List;


public interface RepIndOrganizationTypeDAO {

  /**
   * This method removes a specific repIndOrganizationType value from the database.
   * 
   * @param repIndOrganizationTypeId is the repIndOrganizationType identifier.
   * @return true if the repIndOrganizationType was successfully deleted, false otherwise.
   */
  public void deleteRepIndOrganizationType(long repIndOrganizationTypeId);

  /**
   * This method validate if the repIndOrganizationType identify with the given id exists in the system.
   * 
   * @param repIndOrganizationTypeID is a repIndOrganizationType identifier.
   * @return true if the repIndOrganizationType exists, false otherwise.
   */
  public boolean existRepIndOrganizationType(long repIndOrganizationTypeID);

  /**
   * This method gets a repIndOrganizationType object by a given repIndOrganizationType identifier.
   * 
   * @param repIndOrganizationTypeID is the repIndOrganizationType identifier.
   * @return a RepIndOrganizationType object.
   */
  public RepIndOrganizationType find(long id);

  /**
   * This method gets a list of repIndOrganizationType that are active
   * 
   * @return a list from RepIndOrganizationType null if no exist records
   */
  public List<RepIndOrganizationType> findAll();


  /**
   * This method saves the information of the given repIndOrganizationType
   * 
   * @param repIndOrganizationType - is the repIndOrganizationType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndOrganizationType was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndOrganizationType save(RepIndOrganizationType repIndOrganizationType);
}
