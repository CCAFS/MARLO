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

import org.cgiar.ccafs.marlo.data.manager.impl.DeliverableUserManagerImpl;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(DeliverableUserManagerImpl.class)
public interface DeliverableUserManager {


  /**
   * This method removes a specific deliverableUser value from the database.
   * 
   * @param deliverableUserId is the deliverableUser identifier.
   * @return true if the deliverableUser was successfully deleted, false otherwise.
   */
  public void deleteDeliverableUser(long deliverableUserId);


  /**
   * This method validate if the deliverableUser identify with the given id exists in the system.
   * 
   * @param deliverableUserID is a deliverableUser identifier.
   * @return true if the deliverableUser exists, false otherwise.
   */
  public boolean existDeliverableUser(long deliverableUserID);


  /**
   * This method gets a list of deliverableUser that are active
   * 
   * @return a list from DeliverableUser null if no exist records
   */
  public List<DeliverableUser> findAll();


  /**
   * This method gets a deliverableUser object by a given deliverableUser identifier.
   * 
   * @param deliverableUserID is the deliverableUser identifier.
   * @return a DeliverableUser object.
   */
  public DeliverableUser getDeliverableUserById(long deliverableUserID);

  /**
   * This method saves the information of the given deliverableUser
   * 
   * @param deliverableUser - is the deliverableUser object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableUser was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableUser saveDeliverableUser(DeliverableUser deliverableUser);


}
