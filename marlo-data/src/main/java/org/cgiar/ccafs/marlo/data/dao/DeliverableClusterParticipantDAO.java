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

import org.cgiar.ccafs.marlo.data.model.DeliverableClusterParticipant;

import java.util.List;


public interface DeliverableClusterParticipantDAO {

  /**
   * This method removes a specific deliverableClusterParticipant value from the database.
   * 
   * @param deliverableClusterParticipantId is the deliverableClusterParticipant identifier.
   * @return true if the deliverableClusterParticipant was successfully deleted, false otherwise.
   */
  public void deleteDeliverableClusterParticipant(long deliverableClusterParticipantId);

  /**
   * This method validate if the deliverableClusterParticipant identify with the given id exists in the system.
   * 
   * @param deliverableClusterParticipantID is a deliverableClusterParticipant identifier.
   * @return true if the deliverableClusterParticipant exists, false otherwise.
   */
  public boolean existDeliverableClusterParticipant(long deliverableClusterParticipantID);

  /**
   * This method gets a deliverableClusterParticipant object by a given deliverableClusterParticipant identifier.
   * 
   * @param deliverableClusterParticipantID is the deliverableClusterParticipant identifier.
   * @return a DeliverableClusterParticipant object.
   */
  public DeliverableClusterParticipant find(long id);

  /**
   * This method gets a list of deliverableClusterParticipant that are active
   * 
   * @return a list from DeliverableClusterParticipant null if no exist records
   */
  public List<DeliverableClusterParticipant> findAll();

  /**
   * This method gets a deliverableClusterParticipant object by a given deliverableID identifier and phase identifier.
   * 
   * @param deliverableID is the deliverable identifier.
   * @param phaseID is the phase identifier.
   * @return a List of DeliverableClusterParticipant object.
   */
  public List<DeliverableClusterParticipant> getDeliverableClusterParticipantByDeliverableAndPhase(long deliverableID,
    long phaseID);

  /**
   * This method gets a deliverableClusterParticipant object by a given deliverableID identifier, phase identifier and
   * project identifier.
   * 
   * @param deliverableID is the deliverable identifier.
   * @param phaseID is the phase identifier.
   * @param projectID is the phase identifier.
   * @return a List of DeliverableClusterParticipant object.
   */
  public List<DeliverableClusterParticipant>
    getDeliverableClusterParticipantByDeliverableProjectPhase(long deliverableID, long projectID, long phaseID);

  /**
   * This method gets a deliverableClusterParticipant object by a given deliverableID identifier, phase identifier and
   * project identifier.
   * 
   * @param phaseID is the phase identifier.
   * @param projectID is the phase identifier.
   * @return a List of DeliverableClusterParticipant object.
   */
  public List<DeliverableClusterParticipant> getDeliverableClusterParticipantByProjectAndPhase(long projectID,
    long phaseID);

  /**
   * This method saves the information of the given deliverableClusterParticipant
   * 
   * @param deliverableClusterParticipant - is the deliverableClusterParticipant object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         deliverableClusterParticipant was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableClusterParticipant save(DeliverableClusterParticipant deliverableClusterParticipant);
}
