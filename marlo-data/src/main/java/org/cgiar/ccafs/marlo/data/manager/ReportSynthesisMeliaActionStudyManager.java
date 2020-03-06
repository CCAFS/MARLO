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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaActionStudy;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisMeliaActionStudyManager {


  /**
   * This method removes a specific reportSynthesisMeliaActionStudy value from the database.
   * 
   * @param reportSynthesisMeliaActionStudyId is the reportSynthesisMeliaActionStudy identifier.
   * @return true if the reportSynthesisMeliaActionStudy was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisMeliaActionStudy(long reportSynthesisMeliaActionStudyId);


  /**
   * This method validate if the reportSynthesisMeliaActionStudy identify with the given id exists in the system.
   * 
   * @param reportSynthesisMeliaActionStudyID is a reportSynthesisMeliaActionStudy identifier.
   * @return true if the reportSynthesisMeliaActionStudy exists, false otherwise.
   */
  public boolean existReportSynthesisMeliaActionStudy(long reportSynthesisMeliaActionStudyID);


  /**
   * This method gets a list of reportSynthesisMeliaActionStudy that are active
   * 
   * @return a list from ReportSynthesisMeliaActionStudy null if no exist records
   */
  public List<ReportSynthesisMeliaActionStudy> findAll();


  /**
   * This method gets a reportSynthesisMeliaActionStudy object by a given reportSynthesisMeliaActionStudy identifier.
   * 
   * @param reportSynthesisMeliaActionStudyID is the reportSynthesisMeliaActionStudy identifier.
   * @return a ReportSynthesisMeliaActionStudy object.
   */
  public ReportSynthesisMeliaActionStudy getReportSynthesisMeliaActionStudyById(long reportSynthesisMeliaActionStudyID);

  /**
   * This method saves the information of the given reportSynthesisMeliaActionStudy
   * 
   * @param reportSynthesisMeliaActionStudy - is the reportSynthesisMeliaActionStudy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisMeliaActionStudy was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisMeliaActionStudy saveReportSynthesisMeliaActionStudy(ReportSynthesisMeliaActionStudy reportSynthesisMeliaActionStudy);


}
