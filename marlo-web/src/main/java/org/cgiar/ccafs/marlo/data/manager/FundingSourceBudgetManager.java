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

import org.cgiar.ccafs.marlo.data.manager.impl.FundingSourceBudgetManagerImpl;
import org.cgiar.ccafs.marlo.data.model.FundingSourceBudget;

import java.util.List;

import com.google.inject.ImplementedBy;

/**
 * @author Christian Garcia
 */
@ImplementedBy(FundingSourceBudgetManagerImpl.class)
public interface FundingSourceBudgetManager {


  /**
   * This method removes a specific fundingSourceBudget value from the database.
   * 
   * @param fundingSourceBudgetId is the fundingSourceBudget identifier.
   * @return true if the fundingSourceBudget was successfully deleted, false otherwise.
   */
  public void deleteFundingSourceBudget(long fundingSourceBudgetId);


  /**
   * This method validate if the fundingSourceBudget identify with the given id exists in the system.
   * 
   * @param fundingSourceBudgetID is a fundingSourceBudget identifier.
   * @return true if the fundingSourceBudget exists, false otherwise.
   */
  public boolean existFundingSourceBudget(long fundingSourceBudgetID);


  /**
   * This method gets a list of fundingSourceBudget that are active
   * 
   * @return a list from FundingSourceBudget null if no exist records
   */
  public List<FundingSourceBudget> findAll();


  /**
   * Get a FundingSourceBudget for a specific funding source and year
   * 
   * @param fundingSourceID
   * @param year
   * @return a FundingSourceBudget Object
   */
  public FundingSourceBudget getByFundingSourceAndYear(long fundingSourceID, int year);

  /**
   * This method gets a fundingSourceBudget object by a given fundingSourceBudget identifier.
   * 
   * @param fundingSourceBudgetID is the fundingSourceBudget identifier.
   * @return a FundingSourceBudget object.
   */
  public FundingSourceBudget getFundingSourceBudgetById(long fundingSourceBudgetID);

  /**
   * This method saves the information of the given fundingSourceBudget
   * 
   * @param fundingSourceBudget - is the fundingSourceBudget object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the fundingSourceBudget was
   *         updated
   *         or -1 is some error occurred.
   */
  public FundingSourceBudget saveFundingSourceBudget(FundingSourceBudget fundingSourceBudget);


}
