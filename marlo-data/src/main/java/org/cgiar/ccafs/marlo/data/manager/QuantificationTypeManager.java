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

import org.cgiar.ccafs.marlo.data.model.QuantificationType;

import java.util.List;


/**
 * @author CCAFS
 */

public interface QuantificationTypeManager {


  /**
   * This method removes a specific quantificationType value from the database.
   * 
   * @param quantificationTypeId is the quantificationType identifier.
   * @return true if the quantificationType was successfully deleted, false otherwise.
   */
  public void deleteQuantificationType(long quantificationTypeId);


  /**
   * This method validate if the quantificationType identify with the given id exists in the system.
   * 
   * @param quantificationTypeID is a quantificationType identifier.
   * @return true if the quantificationType exists, false otherwise.
   */
  public boolean existQuantificationType(long quantificationTypeID);


  /**
   * This method gets a list of quantificationType that are active
   * 
   * @return a list from QuantificationType null if no exist records
   */
  public List<QuantificationType> findAll();


  /**
   * This method gets a quantificationType object by a given quantificationType identifier.
   * 
   * @param quantificationTypeID is the quantificationType identifier.
   * @return a QuantificationType object.
   */
  public QuantificationType getQuantificationTypeById(long quantificationTypeID);

  /**
   * This method saves the information of the given quantificationType
   * 
   * @param quantificationType - is the quantificationType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the quantificationType was
   *         updated
   *         or -1 is some error occurred.
   */
  public QuantificationType saveQuantificationType(QuantificationType quantificationType);


}
