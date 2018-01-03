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

import org.cgiar.ccafs.marlo.data.model.DeliverableDataSharing;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface DeliverableDataSharingManager {


  /**
   * This method removes a specific deliverableDataSharing value from the database.
   * 
   * @param deliverableDataSharingId is the deliverableDataSharing identifier.
   * @return true if the deliverableDataSharing was successfully deleted, false otherwise.
   */
  public void deleteDeliverableDataSharing(long deliverableDataSharingId);


  /**
   * This method validate if the deliverableDataSharing identify with the given id exists in the system.
   * 
   * @param deliverableDataSharingID is a deliverableDataSharing identifier.
   * @return true if the deliverableDataSharing exists, false otherwise.
   */
  public boolean existDeliverableDataSharing(long deliverableDataSharingID);


  /**
   * This method gets a list of deliverableDataSharing that are active
   * 
   * @return a list from DeliverableDataSharing null if no exist records
   */
  public List<DeliverableDataSharing> findAll();


  /**
   * This method gets a deliverableDataSharing object by a given deliverableDataSharing identifier.
   * 
   * @param deliverableDataSharingID is the deliverableDataSharing identifier.
   * @return a DeliverableDataSharing object.
   */
  public DeliverableDataSharing getDeliverableDataSharingById(long deliverableDataSharingID);

  /**
   * This method saves the information of the given deliverableDataSharing
   * 
   * @param deliverableDataSharing - is the deliverableDataSharing object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableDataSharing
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableDataSharing saveDeliverableDataSharing(DeliverableDataSharing deliverableDataSharing);


}
