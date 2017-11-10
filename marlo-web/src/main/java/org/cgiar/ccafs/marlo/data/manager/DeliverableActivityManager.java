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

import org.cgiar.ccafs.marlo.data.model.DeliverableActivity;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface DeliverableActivityManager {


  /**
   * This method removes a specific deliverableActivity value from the database.
   * 
   * @param deliverableActivityId is the deliverableActivity identifier.
   * @return true if the deliverableActivity was successfully deleted, false otherwise.
   */
  public void deleteDeliverableActivity(long deliverableActivityId);


  /**
   * This method validate if the deliverableActivity identify with the given id exists in the system.
   * 
   * @param deliverableActivityID is a deliverableActivity identifier.
   * @return true if the deliverableActivity exists, false otherwise.
   */
  public boolean existDeliverableActivity(long deliverableActivityID);


  /**
   * This method gets a list of deliverableActivity that are active
   * 
   * @return a list from DeliverableActivity null if no exist records
   */
  public List<DeliverableActivity> findAll();


  /**
   * This method gets a deliverableActivity object by a given deliverable and activity identifier.
   * 
   * @param deliverableId - deliverable identifier
   * @param activityId - activity identifier
   * @return a DeliverableActivity object.
   */
  public DeliverableActivity findByDeliverableAndActivitie(long deliverableId, long activityId);

  /**
   * This method gets a deliverableActivity object by a given deliverableActivity identifier.
   * 
   * @param deliverableActivityID is the deliverableActivity identifier.
   * @return a DeliverableActivity object.
   */
  public DeliverableActivity getDeliverableActivityById(long deliverableActivityID);

  /**
   * This method saves the information of the given deliverableActivity
   * 
   * @param deliverableActivity - is the deliverableActivity object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableActivity was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableActivity saveDeliverableActivity(DeliverableActivity deliverableActivity);
}
