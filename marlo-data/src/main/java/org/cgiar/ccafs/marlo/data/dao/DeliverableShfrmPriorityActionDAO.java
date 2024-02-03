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

import org.cgiar.ccafs.marlo.data.model.DeliverableShfrmPriorityAction;

import java.util.List;


public interface DeliverableShfrmPriorityActionDAO {

  /**
   * This method removes a specific deliverableShfrmPriorityAction value from the database.
   * 
   * @param deliverableShfrmPriorityActionId is the deliverableShfrmPriorityAction identifier.
   * @return true if the deliverableShfrmPriorityAction was successfully deleted, false otherwise.
   */
  public void deleteDeliverableShfrmPriorityAction(long deliverableShfrmPriorityActionId);

  /**
   * This method validate if the deliverableShfrmPriorityAction identify with the given id exists in the system.
   * 
   * @param deliverableShfrmPriorityActionID is a deliverableShfrmPriorityAction identifier.
   * @return true if the deliverableShfrmPriorityAction exists, false otherwise.
   */
  public boolean existDeliverableShfrmPriorityAction(long deliverableShfrmPriorityActionID);

  /**
   * This method gets a deliverableShfrmPriorityAction object by a given deliverableShfrmPriorityAction identifier.
   * 
   * @param deliverableShfrmPriorityActionID is the deliverableShfrmPriorityAction identifier.
   * @return a DeliverableShfrmPriorityAction object.
   */
  public DeliverableShfrmPriorityAction find(long id);

  /**
   * This method gets a list of deliverableShfrmPriorityAction that are active
   * 
   * @return a list from DeliverableShfrmPriorityAction null if no exist records
   */
  public List<DeliverableShfrmPriorityAction> findAll();


  public List<DeliverableShfrmPriorityAction> findByDeliverableAndPhase(long deliverableId, long phaseId);


  /**
   * This method saves the information of the given deliverableShfrmPriorityAction
   * 
   * @param deliverableShfrmPriorityAction - is the deliverableShfrmPriorityAction object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         deliverableShfrmPriorityAction was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableShfrmPriorityAction save(DeliverableShfrmPriorityAction deliverableShfrmPriorityAction);
}
