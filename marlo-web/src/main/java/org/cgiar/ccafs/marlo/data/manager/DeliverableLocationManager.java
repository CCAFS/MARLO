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

import org.cgiar.ccafs.marlo.data.model.DeliverableLocation;

import java.util.List;


/**
 * @author CCAFS
 */

public interface DeliverableLocationManager {


  /**
   * This method removes a specific deliverableLocation value from the database.
   * 
   * @param deliverableLocationId is the deliverableLocation identifier.
   * @return true if the deliverableLocation was successfully deleted, false otherwise.
   */
  public void deleteDeliverableLocation(long deliverableLocationId);


  /**
   * This method validate if the deliverableLocation identify with the given id exists in the system.
   * 
   * @param deliverableLocationID is a deliverableLocation identifier.
   * @return true if the deliverableLocation exists, false otherwise.
   */
  public boolean existDeliverableLocation(long deliverableLocationID);


  /**
   * This method gets a list of deliverableLocation that are active
   * 
   * @return a list from DeliverableLocation null if no exist records
   */
  public List<DeliverableLocation> findAll();


  /**
   * This method gets a deliverableLocation object by a given deliverableLocation identifier.
   * 
   * @param deliverableLocationID is the deliverableLocation identifier.
   * @return a DeliverableLocation object.
   */
  public DeliverableLocation getDeliverableLocationById(long deliverableLocationID);

  /**
   * This method gets a deliverableLocation object list by a given deliverable and phase identifier.
   * 
   * @param deliverableID is the deliverable identifier.
   * @param phaseID is the phase identifier.
   * @return a DeliverableLocation object list.
   */
  public List<DeliverableLocation> getDeliverableLocationbyPhase(long deliverableID, long phaseID);

  /**
   * This method saves the information of the given deliverableLocation
   * 
   * @param deliverableLocation - is the deliverableLocation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableLocation was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableLocation saveDeliverableLocation(DeliverableLocation deliverableLocation);
}
