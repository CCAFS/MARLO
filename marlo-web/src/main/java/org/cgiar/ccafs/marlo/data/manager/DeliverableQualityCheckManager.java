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

import org.cgiar.ccafs.marlo.data.model.DeliverableQualityCheck;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface DeliverableQualityCheckManager {


  /**
   * This method removes a specific deliverableQualityCheck value from the database.
   * 
   * @param deliverableQualityCheckId is the deliverableQualityCheck identifier.
   * @return true if the deliverableQualityCheck was successfully deleted, false otherwise.
   */
  public void deleteDeliverableQualityCheck(long deliverableQualityCheckId);


  /**
   * This method validate if the deliverableQualityCheck identify with the given id exists in the system.
   * 
   * @param deliverableQualityCheckID is a deliverableQualityCheck identifier.
   * @return true if the deliverableQualityCheck exists, false otherwise.
   */
  public boolean existDeliverableQualityCheck(long deliverableQualityCheckID);


  /**
   * This method gets a list of deliverableQualityCheck that are active
   * 
   * @return a list from DeliverableQualityCheck null if no exist records
   */
  public List<DeliverableQualityCheck> findAll();


  /**
   * This method gets a deliverableQualityCheck object by a given Deliverable identifier.
   * 
   * @param Deliverable is the Deliverable identifier.
   * @return a DeliverableQualityCheck object.
   */
  public DeliverableQualityCheck getDeliverableQualityCheckByDeliverable(long deliverableID);

  /**
   * This method gets a deliverableQualityCheck object by a given deliverableQualityCheck identifier.
   * 
   * @param deliverableQualityCheckID is the deliverableQualityCheck identifier.
   * @return a DeliverableQualityCheck object.
   */
  public DeliverableQualityCheck getDeliverableQualityCheckById(long deliverableQualityCheckID);

  /**
   * This method saves the information of the given deliverableQualityCheck
   * 
   * @param deliverableQualityCheck - is the deliverableQualityCheck object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableQualityCheck
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableQualityCheck saveDeliverableQualityCheck(DeliverableQualityCheck deliverableQualityCheck);


}
