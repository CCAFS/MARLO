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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnership;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisKeyPartnershipManager {


  /**
   * This method removes a specific reportSynthesisKeyPartnership value from the database.
   * 
   * @param reportSynthesisKeyPartnershipId is the reportSynthesisKeyPartnership identifier.
   * @return true if the reportSynthesisKeyPartnership was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisKeyPartnership(long reportSynthesisKeyPartnershipId);


  /**
   * This method validate if the reportSynthesisKeyPartnership identify with the given id exists in the system.
   * 
   * @param reportSynthesisKeyPartnershipID is a reportSynthesisKeyPartnership identifier.
   * @return true if the reportSynthesisKeyPartnership exists, false otherwise.
   */
  public boolean existReportSynthesisKeyPartnership(long reportSynthesisKeyPartnershipID);


  /**
   * This method gets a list of reportSynthesisKeyPartnership that are active
   * 
   * @return a list from ReportSynthesisKeyPartnership null if no exist records
   */
  public List<ReportSynthesisKeyPartnership> findAll();


  /**
   * This method gets a reportSynthesisKeyPartnership object by a given reportSynthesisKeyPartnership identifier.
   * 
   * @param reportSynthesisKeyPartnershipID is the reportSynthesisKeyPartnership identifier.
   * @return a ReportSynthesisKeyPartnership object.
   */
  public ReportSynthesisKeyPartnership getReportSynthesisKeyPartnershipById(long reportSynthesisKeyPartnershipID);

  /**
   * This method saves the information of the given reportSynthesisKeyPartnership
   * 
   * @param reportSynthesisKeyPartnership - is the reportSynthesisKeyPartnership object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisKeyPartnership was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisKeyPartnership saveReportSynthesisKeyPartnership(ReportSynthesisKeyPartnership reportSynthesisKeyPartnership);


}
