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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummaryBudget;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisFinancialSummaryBudgetManager {


  /**
   * This method removes a specific reportSynthesisFinancialSummaryBudget value from the database.
   * 
   * @param reportSynthesisFinancialSummaryBudgetId is the reportSynthesisFinancialSummaryBudget identifier.
   * @return true if the reportSynthesisFinancialSummaryBudget was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisFinancialSummaryBudget(long reportSynthesisFinancialSummaryBudgetId);


  /**
   * This method validate if the reportSynthesisFinancialSummaryBudget identify with the given id exists in the system.
   * 
   * @param reportSynthesisFinancialSummaryBudgetID is a reportSynthesisFinancialSummaryBudget identifier.
   * @return true if the reportSynthesisFinancialSummaryBudget exists, false otherwise.
   */
  public boolean existReportSynthesisFinancialSummaryBudget(long reportSynthesisFinancialSummaryBudgetID);


  /**
   * This method gets a list of reportSynthesisFinancialSummaryBudget that are active
   * 
   * @return a list from ReportSynthesisFinancialSummaryBudget null if no exist records
   */
  public List<ReportSynthesisFinancialSummaryBudget> findAll();


  /**
   * This method gets a reportSynthesisFinancialSummaryBudget object by a given reportSynthesisFinancialSummaryBudget
   * identifier.
   * 
   * @param reportSynthesisFinancialSummaryBudgetID is the reportSynthesisFinancialSummaryBudget identifier.
   * @return a ReportSynthesisFinancialSummaryBudget object.
   */
  public ReportSynthesisFinancialSummaryBudget
    getReportSynthesisFinancialSummaryBudgetById(long reportSynthesisFinancialSummaryBudgetID);

  public double getTotalW1W2ActualExpenditure(long reportSynthesisId);

  /**
   * This method saves the information of the given reportSynthesisFinancialSummaryBudget
   * 
   * @param reportSynthesisFinancialSummaryBudget - is the reportSynthesisFinancialSummaryBudget object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisFinancialSummaryBudget was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFinancialSummaryBudget saveReportSynthesisFinancialSummaryBudget(
    ReportSynthesisFinancialSummaryBudget reportSynthesisFinancialSummaryBudget);

}
