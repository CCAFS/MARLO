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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseExpendituryArea;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisFundingUseExpendituryAreaManager {


  /**
   * This method removes a specific reportSynthesisFundingUseExpendituryArea value from the database.
   * 
   * @param reportSynthesisFundingUseExpendituryAreaId is the reportSynthesisFundingUseExpendituryArea identifier.
   * @return true if the reportSynthesisFundingUseExpendituryArea was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisFundingUseExpendituryArea(long reportSynthesisFundingUseExpendituryAreaId);


  /**
   * This method validate if the reportSynthesisFundingUseExpendituryArea identify with the given id exists in the
   * system.
   * 
   * @param reportSynthesisFundingUseExpendituryAreaID is a reportSynthesisFundingUseExpendituryArea identifier.
   * @return true if the reportSynthesisFundingUseExpendituryArea exists, false otherwise.
   */
  public boolean existReportSynthesisFundingUseExpendituryArea(long reportSynthesisFundingUseExpendituryAreaID);


  /**
   * This method gets a list of reportSynthesisFundingUseExpendituryArea that are active
   * 
   * @return a list from ReportSynthesisFundingUseExpendituryArea null if no exist records
   */
  public List<ReportSynthesisFundingUseExpendituryArea> findAll();


  /**
   * This method gets a reportSynthesisFundingUseExpendituryArea object by a given
   * reportSynthesisFundingUseExpendituryArea identifier.
   * 
   * @param reportSynthesisFundingUseExpendituryAreaID is the reportSynthesisFundingUseExpendituryArea identifier.
   * @return a ReportSynthesisFundingUseExpendituryArea object.
   */
  public ReportSynthesisFundingUseExpendituryArea
    getReportSynthesisFundingUseExpendituryAreaById(long reportSynthesisFundingUseExpendituryAreaID);

  /**
   * This method get a total W1W2 of the w1w2 percentage by a given reportSynthesis identifier.
   * 
   * @param reportSynthesisId is the reportSynthesis identifier
   * @return a double object representing the total W1W2 of the w1w2 percentage or 0 if error occurred.
   */
  public double getTotalEstimatedOfW1W2ActualExpenditure(long reportSynthesisId);

  /**
   * This method get a sum of W1W2 percentage by a given reportSynthesis identifier.
   * 
   * @param reportSynthesisId is the reportSynthesis identifier
   * @return a double object representing the sum of w1w2 percentage or 0 if error occurred.
   */
  public double getTotalW1W2Percentage(long reportSynthesisId);

  /**
   * This method saves the information of the given reportSynthesisFundingUseExpendituryArea
   * 
   * @param reportSynthesisFundingUseExpendituryArea - is the reportSynthesisFundingUseExpendituryArea object with the
   *        new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisFundingUseExpendituryArea was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFundingUseExpendituryArea saveReportSynthesisFundingUseExpendituryArea(
    ReportSynthesisFundingUseExpendituryArea reportSynthesisFundingUseExpendituryArea);
}
