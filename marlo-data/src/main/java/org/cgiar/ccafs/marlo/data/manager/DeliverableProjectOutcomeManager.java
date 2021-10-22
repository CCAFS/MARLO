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

import org.cgiar.ccafs.marlo.data.model.DeliverableProjectOutcome;

import java.util.List;


/**
 * @author CCAFS
 */

public interface DeliverableProjectOutcomeManager {


  /**
   * This method removes a specific deliverableProjectOutcome value from the database.
   * 
   * @param deliverableProjectOutcomeId is the deliverableProjectOutcome identifier.
   * @return true if the deliverableProjectOutcome was successfully deleted, false otherwise.
   */
  public void deleteDeliverableProjectOutcome(long deliverableProjectOutcomeId);


  /**
   * This method validate if the deliverableProjectOutcome identify with the given id exists in the system.
   * 
   * @param deliverableProjectOutcomeID is a deliverableProjectOutcome identifier.
   * @return true if the deliverableProjectOutcome exists, false otherwise.
   */
  public boolean existDeliverableProjectOutcome(long deliverableProjectOutcomeID);


  /**
   * This method gets a list of deliverableProjectOutcome that are active
   * 
   * @return a list from DeliverableProjectOutcome null if no exist records
   */
  public List<DeliverableProjectOutcome> findAll();


  /**
   * This method gets a deliverableProjectOutcome object by a given deliverableProjectOutcome identifier.
   * 
   * @param deliverableProjectOutcomeID is the deliverableProjectOutcome identifier.
   * @return a DeliverableProjectOutcome object.
   */
  public DeliverableProjectOutcome getDeliverableProjectOutcomeById(long deliverableProjectOutcomeID);

  /**
   * This method saves the information of the given deliverableProjectOutcome
   * 
   * @param deliverableProjectOutcome - is the deliverableProjectOutcome object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableProjectOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableProjectOutcome saveDeliverableProjectOutcome(DeliverableProjectOutcome deliverableProjectOutcome);


}
