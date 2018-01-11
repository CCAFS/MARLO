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

import org.cgiar.ccafs.marlo.data.model.DeliverableFundingSource;
import org.cgiar.ccafs.marlo.data.model.Phase;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface DeliverableFundingSourceManager {


  public DeliverableFundingSource copyDeliverableFundingSource(DeliverableFundingSource deliverableFundingSource,
    Phase phase);


  /**
   * This method removes a specific deliverableFundingSource value from the database.
   * 
   * @param deliverableFundingSourceId is the deliverableFundingSource identifier.
   * @return true if the deliverableFundingSource was successfully deleted, false otherwise.
   */
  public void deleteDeliverableFundingSource(long deliverableFundingSourceId);


  /**
   * This method validate if the deliverableFundingSource identify with the given id exists in the system.
   * 
   * @param deliverableFundingSourceID is a deliverableFundingSource identifier.
   * @return true if the deliverableFundingSource exists, false otherwise.
   */
  public boolean existDeliverableFundingSource(long deliverableFundingSourceID);


  /**
   * This method gets a list of deliverableFundingSource that are active
   * 
   * @return a list from DeliverableFundingSource null if no exist records
   */
  public List<DeliverableFundingSource> findAll();

  /**
   * This method gets a deliverableFundingSource object by a given deliverableFundingSource identifier.
   * 
   * @param deliverableFundingSourceID is the deliverableFundingSource identifier.
   * @return a DeliverableFundingSource object.
   */
  public DeliverableFundingSource getDeliverableFundingSourceById(long deliverableFundingSourceID);

  /**
   * This method saves the information of the given deliverableFundingSource
   * 
   * @param deliverableFundingSource - is the deliverableFundingSource object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableFundingSource
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableFundingSource saveDeliverableFundingSource(DeliverableFundingSource deliverableFundingSource);


}
