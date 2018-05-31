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

import org.cgiar.ccafs.marlo.data.model.DeliverableRule;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface DeliverableRuleManager {


  /**
   * This method removes a specific deliverableRule value from the database.
   * 
   * @param deliverableRuleId is the deliverableRule identifier.
   * @return true if the deliverableRule was successfully deleted, false otherwise.
   */
  public void deleteDeliverableRule(long deliverableRuleId);


  /**
   * This method validate if the deliverableRule identify with the given id exists in the system.
   * 
   * @param deliverableRuleID is a deliverableRule identifier.
   * @return true if the deliverableRule exists, false otherwise.
   */
  public boolean existDeliverableRule(long deliverableRuleID);


  /**
   * This method gets a list of deliverableRule that are active
   * 
   * @return a list from DeliverableRule null if no exist records
   */
  public List<DeliverableRule> findAll();


  /**
   * This method gets a deliverableRule object by a given deliverableRule identifier.
   * 
   * @param deliverableRuleID is the deliverableRule identifier.
   * @return a DeliverableRule object.
   */
  public DeliverableRule getDeliverableRuleById(long deliverableRuleID);

  /**
   * This method saves the information of the given deliverableRule
   * 
   * @param deliverableRule - is the deliverableRule object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the deliverableRule was
   *         updated
   *         or -1 is some error occurred.
   */
  public DeliverableRule saveDeliverableRule(DeliverableRule deliverableRule);


}
