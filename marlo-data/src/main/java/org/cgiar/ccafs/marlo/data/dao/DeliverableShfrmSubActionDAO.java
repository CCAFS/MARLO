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

import org.cgiar.ccafs.marlo.data.model.DeliverableShfrmSubAction;

import java.util.List;


public interface DeliverableShfrmSubActionDAO {

  /**
   * This method removes a specific deliverableShfrmSubAction value from the database.
   * 
   * @param deliverableShfrmSubActionId is the deliverableShfrmSubAction identifier.
   * @return true if the deliverableShfrmSubAction was successfully deleted, false otherwise.
   */
  public void deleteDeliverableShfrmSubAction(long deliverableShfrmSubActionId);

  /**
   * This method validate if the deliverableShfrmSubAction identify with the given id exists in the system.
   * 
   * @param deliverableShfrmSubActionID is a deliverableShfrmSubAction identifier.
   * @return true if the deliverableShfrmSubAction exists, false otherwise.
   */
  public boolean existDeliverableShfrmSubAction(long deliverableShfrmSubActionID);

  /**
   * This method gets a deliverableShfrmSubAction object by a given deliverableShfrmSubAction identifier.
   * 
   * @param deliverableShfrmSubActionID is the deliverableShfrmSubAction identifier.
   * @return a DeliverableShfrmSubAction object.
   */
  public DeliverableShfrmSubAction find(long id);

  /**
   * This method gets a list of deliverableShfrmSubAction that are active
   * 
   * @return a list from DeliverableShfrmSubAction null if no exist records
   */
  public List<DeliverableShfrmSubAction> findAll();


  /**
   * This method saves the information of the given deliverableShfrmSubAction
   * 
   * @param deliverableShfrmSubAction - is the deliverableShfrmSubAction object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableShfrmSubAction was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableShfrmSubAction save(DeliverableShfrmSubAction deliverableShfrmSubAction);
}
