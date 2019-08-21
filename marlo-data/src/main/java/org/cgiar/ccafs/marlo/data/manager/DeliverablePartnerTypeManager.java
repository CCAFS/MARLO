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

import org.cgiar.ccafs.marlo.data.model.DeliverablePartnerType;

import java.util.List;


/**
 * @author CCAFS
 */

public interface DeliverablePartnerTypeManager {


  /**
   * This method removes a specific deliverablePartnerType value from the database.
   * 
   * @param deliverablePartnerTypeId is the deliverablePartnerType identifier.
   * @return true if the deliverablePartnerType was successfully deleted, false otherwise.
   */
  public void deleteDeliverablePartnerType(long deliverablePartnerTypeId);


  /**
   * This method validate if the deliverablePartnerType identify with the given id exists in the system.
   * 
   * @param deliverablePartnerTypeID is a deliverablePartnerType identifier.
   * @return true if the deliverablePartnerType exists, false otherwise.
   */
  public boolean existDeliverablePartnerType(long deliverablePartnerTypeID);


  /**
   * This method gets a list of deliverablePartnerType that are active
   * 
   * @return a list from DeliverablePartnerType null if no exist records
   */
  public List<DeliverablePartnerType> findAll();


  /**
   * This method gets a deliverablePartnerType object by a given deliverablePartnerType identifier.
   * 
   * @param deliverablePartnerTypeID is the deliverablePartnerType identifier.
   * @return a DeliverablePartnerType object.
   */
  public DeliverablePartnerType getDeliverablePartnerTypeById(long deliverablePartnerTypeID);

  /**
   * This method saves the information of the given deliverablePartnerType
   * 
   * @param deliverablePartnerType - is the deliverablePartnerType object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverablePartnerType was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverablePartnerType saveDeliverablePartnerType(DeliverablePartnerType deliverablePartnerType);


}
