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

import org.cgiar.ccafs.marlo.data.model.DeliverableParticipantLocation;

import java.util.List;


/**
 * @author CCAFS
 */

public interface DeliverableParticipantLocationManager {


  /**
   * This method removes a specific deliverableParticipantLocation value from the database.
   * 
   * @param deliverableParticipantLocationId is the deliverableParticipantLocation identifier.
   * @return true if the deliverableParticipantLocation was successfully deleted, false otherwise.
   */
  public void deleteDeliverableParticipantLocation(long deliverableParticipantLocationId);


  /**
   * This method validate if the deliverableParticipantLocation identify with the given id exists in the system.
   * 
   * @param deliverableParticipantLocationID is a deliverableParticipantLocation identifier.
   * @return true if the deliverableParticipantLocation exists, false otherwise.
   */
  public boolean existDeliverableParticipantLocation(long deliverableParticipantLocationID);


  /**
   * This method gets a list of deliverableParticipantLocation that are active
   * 
   * @return a list from DeliverableParticipantLocation null if no exist records
   */
  public List<DeliverableParticipantLocation> findAll();

  /**
   * This method gets a list of DeliverableParticipantLocation that are active with the given id exists in the system
   * 
   * @return a list from DeliverableParticipantLocation null if no exist records
   */
  List<DeliverableParticipantLocation> findParticipantLocationsByParticipant(long deliverableParticipantId);

  /**
   * This method gets a deliverableParticipantLocation object by a given deliverableParticipantLocation identifier.
   * 
   * @param deliverableParticipantLocationID is the deliverableParticipantLocation identifier.
   * @return a DeliverableParticipantLocation object.
   */
  public DeliverableParticipantLocation getDeliverableParticipantLocationById(long deliverableParticipantLocationID);


  /**
   * This method saves the information of the given deliverableParticipantLocation
   * 
   * @param deliverableParticipantLocation - is the deliverableParticipantLocation object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         deliverableParticipantLocation was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableParticipantLocation
    saveDeliverableParticipantLocation(DeliverableParticipantLocation deliverableParticipantLocation);


}
