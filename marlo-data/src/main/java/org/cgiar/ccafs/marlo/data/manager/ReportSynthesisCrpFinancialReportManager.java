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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpFinancialReport;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisCrpFinancialReportManager {

  /**
   * This method removes a specific reportSynthesisCrpFinancialReport value from the database.
   * 
   * @param reportSynthesisCrpFinancialReportId is the reportSynthesisCrpFinancialReport identifier.
   * @return true if the reportSynthesisCrpFinancialReport was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisCrpFinancialReport(long reportSynthesisCrpFinancialReportId);


  /**
   * This method validate if the reportSynthesisCrpFinancialReport identify with the given id exists in the system.
   * 
   * @param reportSynthesisCrpFinancialReportID is a reportSynthesisCrpFinancialReport identifier.
   * @return true if the reportSynthesisCrpFinancialReport exists, false otherwise.
   */
  public boolean existReportSynthesisCrpFinancialReport(long reportSynthesisCrpFinancialReportID);


  /**
   * This method gets a list of reportSynthesisCrpFinancialReport that are active
   * 
   * @return a list from ReportSynthesisCrpFinancialReport null if no exist records
   */
  public List<ReportSynthesisCrpFinancialReport> findAll();


  /**
   * This method gets a reportSynthesisCrpFinancialReport object by a given reportSynthesis identifier.
   * 
   * @param reportSynthesisId is the reportSynthesis identifier.
   * @return a ReportSynthesisCrpFinancialReport object.
   */
  public ReportSynthesisCrpFinancialReport findByReportSynthesis(long reportSynthesisId);


  /**
   * This method gets a reportSynthesisCrpFinancialReport object by a given reportSynthesisCrpFinancialReport
   * identifier.
   * 
   * @param reportSynthesisCrpFinancialReportID is the reportSynthesisCrpFinancialReport identifier.
   * @return a ReportSynthesisCrpFinancialReport object.
   */
  public ReportSynthesisCrpFinancialReport
    getReportSynthesisCrpFinancialReportById(long reportSynthesisCrpFinancialReportID);


  /**
   * This method saves the information of the given reportSynthesisCrpFinancialReport
   * 
   * @param reportSynthesisCrpFinancialReport - is the reportSynthesisCrpFinancialReport object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisCrpFinancialReport was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisCrpFinancialReport
    saveReportSynthesisCrpFinancialReport(ReportSynthesisCrpFinancialReport reportSynthesisCrpFinancialReport);

}
