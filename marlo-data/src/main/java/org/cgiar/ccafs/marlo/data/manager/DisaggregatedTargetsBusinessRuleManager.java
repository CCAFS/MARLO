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

import org.cgiar.ccafs.marlo.data.model.DisaggregatedTargetsBusinessRule;

import java.util.List;


/**
 * @author CCAFS
 */

public interface DisaggregatedTargetsBusinessRuleManager {


  /**
   * This method removes a specific disaggregatedTargetsBusinessRule value from the database.
   * 
   * @param disaggregatedTargetsBusinessRuleId is the disaggregatedTargetsBusinessRule identifier.
   * @return true if the disaggregatedTargetsBusinessRule was successfully deleted, false otherwise.
   */
  public void deleteDisaggregatedTargetsBusinessRule(long disaggregatedTargetsBusinessRuleId);


  /**
   * This method validate if the disaggregatedTargetsBusinessRule identify with the given id exists in the system.
   * 
   * @param disaggregatedTargetsBusinessRuleID is a disaggregatedTargetsBusinessRule identifier.
   * @return true if the disaggregatedTargetsBusinessRule exists, false otherwise.
   */
  public boolean existDisaggregatedTargetsBusinessRule(long disaggregatedTargetsBusinessRuleID);


  /**
   * This method gets a list of disaggregatedTargetsBusinessRule that are active
   * 
   * @return a list from DisaggregatedTargetsBusinessRule null if no exist records
   */
  public List<DisaggregatedTargetsBusinessRule> findAll();


  /**
   * This method gets a disaggregatedTargetsBusinessRule object by a given disaggregatedTargetsBusinessRule identifier.
   * 
   * @param disaggregatedTargetsBusinessRuleID is the disaggregatedTargetsBusinessRule identifier.
   * @return a DisaggregatedTargetsBusinessRule object.
   */
  public DisaggregatedTargetsBusinessRule getDisaggregatedTargetsBusinessRuleById(long disaggregatedTargetsBusinessRuleID);

  /**
   * This method saves the information of the given disaggregatedTargetsBusinessRule
   * 
   * @param disaggregatedTargetsBusinessRule - is the disaggregatedTargetsBusinessRule object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the disaggregatedTargetsBusinessRule was
   *         updated
   *         or -1 is some error occurred.
   */
  public DisaggregatedTargetsBusinessRule saveDisaggregatedTargetsBusinessRule(DisaggregatedTargetsBusinessRule disaggregatedTargetsBusinessRule);


}
