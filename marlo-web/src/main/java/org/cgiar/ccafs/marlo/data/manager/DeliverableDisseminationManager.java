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

import org.cgiar.ccafs.marlo.data.manager.impl.DeliverableDisseminationManagerImpl;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(DeliverableDisseminationManagerImpl.class)
public interface DeliverableDisseminationManager {


  /**
   * This method removes a specific deliverableDissemination value from the database.
   * 
   * @param deliverableDisseminationId is the deliverableDissemination identifier.
   * @return true if the deliverableDissemination was successfully deleted, false otherwise.
   */
  public boolean deleteDeliverableDissemination(long deliverableDisseminationId);


  /**
   * This method validate if the deliverableDissemination identify with the given id exists in the system.
   * 
   * @param deliverableDisseminationID is a deliverableDissemination identifier.
   * @return true if the deliverableDissemination exists, false otherwise.
   */
  public boolean existDeliverableDissemination(long deliverableDisseminationID);


  /**
   * This method gets a list of deliverableDissemination that are active
   * 
   * @return a list from DeliverableDissemination null if no exist records
   */
  public List<DeliverableDissemination> findAll();


  /**
   * This method gets a deliverableDissemination object by a given deliverableDissemination identifier.
   * 
   * @param deliverableDisseminationID is the deliverableDissemination identifier.
   * @return a DeliverableDissemination object.
   */
  public DeliverableDissemination getDeliverableDisseminationById(long deliverableDisseminationID);

  /**
   * This method saves the information of the given deliverableDissemination
   * 
   * @param deliverableDissemination - is the deliverableDissemination object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableDissemination was
   *         updated
   *         or -1 is some error occurred.
   */
  public long saveDeliverableDissemination(DeliverableDissemination deliverableDissemination);


}
