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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummary;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisFinancialSummaryManager {


  /**
   * This method removes a specific reportSynthesisFinancialSummary value from the database.
   * 
   * @param reportSynthesisFinancialSummaryId is the reportSynthesisFinancialSummary identifier.
   * @return true if the reportSynthesisFinancialSummary was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisFinancialSummary(long reportSynthesisFinancialSummaryId);


  /**
   * This method validate if the reportSynthesisFinancialSummary identify with the given id exists in the system.
   * 
   * @param reportSynthesisFinancialSummaryID is a reportSynthesisFinancialSummary identifier.
   * @return true if the reportSynthesisFinancialSummary exists, false otherwise.
   */
  public boolean existReportSynthesisFinancialSummary(long reportSynthesisFinancialSummaryID);


  /**
   * This method gets a list of reportSynthesisFinancialSummary that are active
   * 
   * @return a list from ReportSynthesisFinancialSummary null if no exist records
   */
  public List<ReportSynthesisFinancialSummary> findAll();


  /**
   * This method gets a reportSynthesisFinancialSummary object by a given reportSynthesisFinancialSummary identifier.
   * 
   * @param reportSynthesisFinancialSummaryID is the reportSynthesisFinancialSummary identifier.
   * @return a ReportSynthesisFinancialSummary object.
   */
  public ReportSynthesisFinancialSummary getReportSynthesisFinancialSummaryById(long reportSynthesisFinancialSummaryID);

  /**
   * This method saves the information of the given reportSynthesisFinancialSummary
   * 
   * @param reportSynthesisFinancialSummary - is the reportSynthesisFinancialSummary object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisFinancialSummary was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFinancialSummary saveReportSynthesisFinancialSummary(ReportSynthesisFinancialSummary reportSynthesisFinancialSummary);


}
