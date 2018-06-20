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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaStudy;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisMeliaStudyManager {


  /**
   * This method removes a specific reportSynthesisMeliaStudy value from the database.
   * 
   * @param reportSynthesisMeliaStudyId is the reportSynthesisMeliaStudy identifier.
   * @return true if the reportSynthesisMeliaStudy was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisMeliaStudy(long reportSynthesisMeliaStudyId);


  /**
   * This method validate if the reportSynthesisMeliaStudy identify with the given id exists in the system.
   * 
   * @param reportSynthesisMeliaStudyID is a reportSynthesisMeliaStudy identifier.
   * @return true if the reportSynthesisMeliaStudy exists, false otherwise.
   */
  public boolean existReportSynthesisMeliaStudy(long reportSynthesisMeliaStudyID);


  /**
   * This method gets a list of reportSynthesisMeliaStudy that are active
   * 
   * @return a list from ReportSynthesisMeliaStudy null if no exist records
   */
  public List<ReportSynthesisMeliaStudy> findAll();


  /**
   * This method gets a reportSynthesisMeliaStudy object by a given reportSynthesisMeliaStudy identifier.
   * 
   * @param reportSynthesisMeliaStudyID is the reportSynthesisMeliaStudy identifier.
   * @return a ReportSynthesisMeliaStudy object.
   */
  public ReportSynthesisMeliaStudy getReportSynthesisMeliaStudyById(long reportSynthesisMeliaStudyID);

  /**
   * This method saves the information of the given reportSynthesisMeliaStudy
   * 
   * @param reportSynthesisMeliaStudy - is the reportSynthesisMeliaStudy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisMeliaStudy was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisMeliaStudy saveReportSynthesisMeliaStudy(ReportSynthesisMeliaStudy reportSynthesisMeliaStudy);


}
