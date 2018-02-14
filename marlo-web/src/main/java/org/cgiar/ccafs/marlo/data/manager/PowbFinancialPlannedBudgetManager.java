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

import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlannedBudget;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PowbFinancialPlannedBudgetManager {


  /**
   * This method removes a specific powbFinancialPlannedBudget value from the database.
   * 
   * @param powbFinancialPlannedBudgetId is the powbFinancialPlannedBudget identifier.
   * @return true if the powbFinancialPlannedBudget was successfully deleted, false otherwise.
   */
  public void deletePowbFinancialPlannedBudget(long powbFinancialPlannedBudgetId);


  /**
   * This method validate if the powbFinancialPlannedBudget identify with the given id exists in the system.
   * 
   * @param powbFinancialPlannedBudgetID is a powbFinancialPlannedBudget identifier.
   * @return true if the powbFinancialPlannedBudget exists, false otherwise.
   */
  public boolean existPowbFinancialPlannedBudget(long powbFinancialPlannedBudgetID);


  /**
   * This method gets a list of powbFinancialPlannedBudget that are active
   * 
   * @return a list from PowbFinancialPlannedBudget null if no exist records
   */
  public List<PowbFinancialPlannedBudget> findAll();


  /**
   * This method gets a powbFinancialPlannedBudget object by a given powbFinancialPlannedBudget identifier.
   * 
   * @param powbFinancialPlannedBudgetID is the powbFinancialPlannedBudget identifier.
   * @return a PowbFinancialPlannedBudget object.
   */
  public PowbFinancialPlannedBudget getPowbFinancialPlannedBudgetById(long powbFinancialPlannedBudgetID);

  /**
   * This method saves the information of the given powbFinancialPlannedBudget
   * 
   * @param powbFinancialPlannedBudget - is the powbFinancialPlannedBudget object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbFinancialPlannedBudget was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbFinancialPlannedBudget savePowbFinancialPlannedBudget(PowbFinancialPlannedBudget powbFinancialPlannedBudget);


}
