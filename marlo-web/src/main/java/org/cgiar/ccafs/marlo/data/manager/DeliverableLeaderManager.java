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

import org.cgiar.ccafs.marlo.data.manager.impl.DeliverableLeaderManagerImpl;
import org.cgiar.ccafs.marlo.data.model.DeliverableLeader;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(DeliverableLeaderManagerImpl.class)
public interface DeliverableLeaderManager {


  /**
   * This method removes a specific deliverableLeader value from the database.
   * 
   * @param deliverableLeaderId is the deliverableLeader identifier.
   * @return true if the deliverableLeader was successfully deleted, false otherwise.
   */
  public void deleteDeliverableLeader(long deliverableLeaderId);


  /**
   * This method validate if the deliverableLeader identify with the given id exists in the system.
   * 
   * @param deliverableLeaderID is a deliverableLeader identifier.
   * @return true if the deliverableLeader exists, false otherwise.
   */
  public boolean existDeliverableLeader(long deliverableLeaderID);


  /**
   * This method gets a list of deliverableLeader that are active
   * 
   * @return a list from DeliverableLeader null if no exist records
   */
  public List<DeliverableLeader> findAll();


  /**
   * This method gets a deliverableLeader object by a given deliverableLeader identifier.
   * 
   * @param deliverableLeaderID is the deliverableLeader identifier.
   * @return a DeliverableLeader object.
   */
  public DeliverableLeader getDeliverableLeaderById(long deliverableLeaderID);

  /**
   * This method saves the information of the given deliverableLeader
   * 
   * @param deliverableLeader - is the deliverableLeader object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableLeader was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableLeader saveDeliverableLeader(DeliverableLeader deliverableLeader);


}
