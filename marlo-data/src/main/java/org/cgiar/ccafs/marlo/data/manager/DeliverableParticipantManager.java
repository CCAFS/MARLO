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

import org.cgiar.ccafs.marlo.data.model.DeliverableParticipant;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;


/**
 * @author CCAFS
 */

public interface DeliverableParticipantManager {


  /**
   * This method removes a specific deliverableParticipant value from the database.
   * 
   * @param deliverableParticipantId is the deliverableParticipant identifier.
   * @return true if the deliverableParticipant was successfully deleted, false otherwise.
   */
  public void deleteDeliverableParticipant(long deliverableParticipantId);


  /**
   * This method validate if the deliverableParticipant identify with the given id exists in the system.
   * 
   * @param deliverableParticipantID is a deliverableParticipant identifier.
   * @return true if the deliverableParticipant exists, false otherwise.
   */
  public boolean existDeliverableParticipant(long deliverableParticipantID);


  /**
   * This method gets a list of deliverableParticipant that are active
   * 
   * @return a list from DeliverableParticipant null if no exist records
   */
  public List<DeliverableParticipant> findAll();

  /**
   * This method gets a list of DeliverableParticipant that are active with the given deliverable and phase id exists in
   * the system
   * 
   * @return a list from DeliverableParticipant null if no exist records
   */
  public List<DeliverableParticipant> findDeliverableParticipantByDeliverableAndPhase(Long deliverableID, Long phaseID);

  /**
   * This method gets a deliverableParticipant object by a given deliverableParticipant identifier.
   * 
   * @param deliverableParticipantID is the deliverableParticipant identifier.
   * @return a DeliverableParticipant object.
   */
  public DeliverableParticipant getDeliverableParticipantById(long deliverableParticipantID);


  /**
   * This method gets a list of DeliverableParticipant that are active by a given phase
   * 
   * @return a list from DeliverableParticipant null if no exist records
   */
  public List<DeliverableParticipant> getDeliverableParticipantByPhase(Phase phase);

  public List<DeliverableParticipant> getDeliverableParticipantByPhaseAndProject(Phase phase, long projectID);


  /**
   * This method saves the information of the given deliverableParticipant
   * 
   * @param deliverableParticipant - is the deliverableParticipant object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableParticipant
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableParticipant saveDeliverableParticipant(DeliverableParticipant deliverableParticipant);


}
