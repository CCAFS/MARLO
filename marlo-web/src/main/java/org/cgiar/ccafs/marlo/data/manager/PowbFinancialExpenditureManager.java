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

import org.cgiar.ccafs.marlo.data.model.PowbFinancialExpenditure;

import java.util.List;


/**
 * @author Christian Garcia
 */

public interface PowbFinancialExpenditureManager {


  /**
   * This method removes a specific powbFinancialExpenditure value from the database.
   * 
   * @param powbFinancialExpenditureId is the powbFinancialExpenditure identifier.
   * @return true if the powbFinancialExpenditure was successfully deleted, false otherwise.
   */
  public void deletePowbFinancialExpenditure(long powbFinancialExpenditureId);


  /**
   * This method validate if the powbFinancialExpenditure identify with the given id exists in the system.
   * 
   * @param powbFinancialExpenditureID is a powbFinancialExpenditure identifier.
   * @return true if the powbFinancialExpenditure exists, false otherwise.
   */
  public boolean existPowbFinancialExpenditure(long powbFinancialExpenditureID);


  /**
   * This method gets a list of powbFinancialExpenditure that are active
   * 
   * @return a list from PowbFinancialExpenditure null if no exist records
   */
  public List<PowbFinancialExpenditure> findAll();


  /**
   * This method gets a powbFinancialExpenditure object by a given powbFinancialExpenditure identifier.
   * 
   * @param powbFinancialExpenditureID is the powbFinancialExpenditure identifier.
   * @return a PowbFinancialExpenditure object.
   */
  public PowbFinancialExpenditure getPowbFinancialExpenditureById(long powbFinancialExpenditureID);

  /**
   * This method saves the information of the given powbFinancialExpenditure
   * 
   * @param powbFinancialExpenditure - is the powbFinancialExpenditure object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the powbFinancialExpenditure was
   *         updated
   *         or -1 is some error occurred.
   */
  public PowbFinancialExpenditure savePowbFinancialExpenditure(PowbFinancialExpenditure powbFinancialExpenditure);


}
