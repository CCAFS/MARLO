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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressStudy;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisCrpProgressStudyManager {


  /**
   * This method removes a specific reportSynthesisCrpProgressStudy value from the database.
   * 
   * @param reportSynthesisCrpProgressStudyId is the reportSynthesisCrpProgressStudy identifier.
   * @return true if the reportSynthesisCrpProgressStudy was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisCrpProgressStudy(long reportSynthesisCrpProgressStudyId);


  /**
   * This method validate if the reportSynthesisCrpProgressStudy identify with the given id exists in the system.
   * 
   * @param reportSynthesisCrpProgressStudyID is a reportSynthesisCrpProgressStudy identifier.
   * @return true if the reportSynthesisCrpProgressStudy exists, false otherwise.
   */
  public boolean existReportSynthesisCrpProgressStudy(long reportSynthesisCrpProgressStudyID);


  /**
   * This method gets a list of reportSynthesisCrpProgressStudy that are active
   * 
   * @return a list from ReportSynthesisCrpProgressStudy null if no exist records
   */
  public List<ReportSynthesisCrpProgressStudy> findAll();


  /**
   * This method gets a reportSynthesisCrpProgressStudy object by a given reportSynthesisCrpProgressStudy identifier.
   * 
   * @param reportSynthesisCrpProgressStudyID is the reportSynthesisCrpProgressStudy identifier.
   * @return a ReportSynthesisCrpProgressStudy object.
   */
  public ReportSynthesisCrpProgressStudy getReportSynthesisCrpProgressStudyById(long reportSynthesisCrpProgressStudyID);

  /**
   * This method saves the information of the given reportSynthesisCrpProgressStudy
   * 
   * @param reportSynthesisCrpProgressStudy - is the reportSynthesisCrpProgressStudy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisCrpProgressStudy was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisCrpProgressStudy saveReportSynthesisCrpProgressStudy(ReportSynthesisCrpProgressStudy reportSynthesisCrpProgressStudy);


}
