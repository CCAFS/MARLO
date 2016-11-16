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

import org.cgiar.ccafs.marlo.data.manager.impl.DeliverableGenderLevelManagerImpl;
import org.cgiar.ccafs.marlo.data.model.DeliverableGenderLevel;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(DeliverableGenderLevelManagerImpl.class)
public interface DeliverableGenderLevelManager {


  /**
   * This method removes a specific deliverableGenderLevel value from the database.
   * 
   * @param deliverableGenderLevelId is the deliverableGenderLevel identifier.
   * @return true if the deliverableGenderLevel was successfully deleted, false otherwise.
   */
  public boolean deleteDeliverableGenderLevel(long deliverableGenderLevelId);


  /**
   * This method validate if the deliverableGenderLevel identify with the given id exists in the system.
   * 
   * @param deliverableGenderLevelID is a deliverableGenderLevel identifier.
   * @return true if the deliverableGenderLevel exists, false otherwise.
   */
  public boolean existDeliverableGenderLevel(long deliverableGenderLevelID);


  /**
   * This method gets a list of deliverableGenderLevel that are active
   * 
   * @return a list from DeliverableGenderLevel null if no exist records
   */
  public List<DeliverableGenderLevel> findAll();


  /**
   * This method gets a deliverableGenderLevel object by a given deliverableGenderLevel identifier.
   * 
   * @param deliverableGenderLevelID is the deliverableGenderLevel identifier.
   * @return a DeliverableGenderLevel object.
   */
  public DeliverableGenderLevel getDeliverableGenderLevelById(long deliverableGenderLevelID);

  /**
   * This method saves the information of the given deliverableGenderLevel
   * 
   * @param deliverableGenderLevel - is the deliverableGenderLevel object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableGenderLevel was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveDeliverableGenderLevel(DeliverableGenderLevel deliverableGenderLevel);


}
