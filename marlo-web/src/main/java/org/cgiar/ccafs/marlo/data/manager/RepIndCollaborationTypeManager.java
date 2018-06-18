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

import org.cgiar.ccafs.marlo.data.model.RepIndCollaborationType;

import java.util.List;


/**
 * @author CCAFS
 */

public interface RepIndCollaborationTypeManager {


  /**
   * This method removes a specific repIndCollaborationType value from the database.
   * 
   * @param repIndCollaborationTypeId is the repIndCollaborationType identifier.
   * @return true if the repIndCollaborationType was successfully deleted, false otherwise.
   */
  public void deleteRepIndCollaborationType(long repIndCollaborationTypeId);


  /**
   * This method validate if the repIndCollaborationType identify with the given id exists in the system.
   * 
   * @param repIndCollaborationTypeID is a repIndCollaborationType identifier.
   * @return true if the repIndCollaborationType exists, false otherwise.
   */
  public boolean existRepIndCollaborationType(long repIndCollaborationTypeID);


  /**
   * This method gets a list of repIndCollaborationType that are active
   * 
   * @return a list from RepIndCollaborationType null if no exist records
   */
  public List<RepIndCollaborationType> findAll();


  /**
   * This method gets a repIndCollaborationType object by a given repIndCollaborationType identifier.
   * 
   * @param repIndCollaborationTypeID is the repIndCollaborationType identifier.
   * @return a RepIndCollaborationType object.
   */
  public RepIndCollaborationType getRepIndCollaborationTypeById(long repIndCollaborationTypeID);

  /**
   * This method saves the information of the given repIndCollaborationType
   * 
   * @param repIndCollaborationType - is the repIndCollaborationType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the repIndCollaborationType was
   *         updated
   *         or -1 is some error occurred.
   */
  public RepIndCollaborationType saveRepIndCollaborationType(RepIndCollaborationType repIndCollaborationType);


}
