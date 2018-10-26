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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIndicator;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisIndicatorManager {


  /**
   * This method removes a specific reportSynthesisIndicator value from the database.
   * 
   * @param reportSynthesisIndicatorId is the reportSynthesisIndicator identifier.
   * @return true if the reportSynthesisIndicator was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisIndicator(long reportSynthesisIndicatorId);


  /**
   * This method validate if the reportSynthesisIndicator identify with the given id exists in the system.
   * 
   * @param reportSynthesisIndicatorID is a reportSynthesisIndicator identifier.
   * @return true if the reportSynthesisIndicator exists, false otherwise.
   */
  public boolean existReportSynthesisIndicator(long reportSynthesisIndicatorID);


  /**
   * This method gets a list of reportSynthesisIndicator that are active
   * 
   * @return a list from ReportSynthesisIndicator null if no exist records
   */
  public List<ReportSynthesisIndicator> findAll();


  /**
   * This method gets a list of reportSynthesisIndicator by a given type indicator that are active
   * 
   * @return a list from ReportSynthesisIndicator null if no exist records
   */
  public List<ReportSynthesisIndicator> getIndicatorsByType(ReportSynthesis reportSynthesis, String indicatorType);

  /**
   * This method gets a reportSynthesisIndicator object by a given reportSynthesisIndicator identifier.
   * 
   * @param reportSynthesisIndicatorID is the reportSynthesisIndicator identifier.
   * @return a ReportSynthesisIndicator object.
   */
  public ReportSynthesisIndicator getReportSynthesisIndicatorById(long reportSynthesisIndicatorID);


  /**
   * This method saves the information of the given reportSynthesisIndicator
   * 
   * @param reportSynthesisIndicator - is the reportSynthesisIndicator object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisIndicator
   *         was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisIndicator saveReportSynthesisIndicator(ReportSynthesisIndicator reportSynthesisIndicator);
}
