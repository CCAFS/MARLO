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


package org.cgiar.ccafs.marlo.data.dao;

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudy;

import java.util.List;


public interface ReportSynthesisFlagshipProgressStudyDAO {

  /**
   * This method removes a specific reportSynthesisFlagshipProgressStudy value from the database.
   * 
   * @param reportSynthesisFlagshipProgressStudyId is the reportSynthesisFlagshipProgressStudy identifier.
   * @return true if the reportSynthesisFlagshipProgressStudy was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisFlagshipProgressStudy(long reportSynthesisFlagshipProgressStudyId);

  /**
   * This method validate if the reportSynthesisFlagshipProgressStudy identify with the given id exists in the system.
   * 
   * @param reportSynthesisFlagshipProgressStudyID is a reportSynthesisFlagshipProgressStudy identifier.
   * @return true if the reportSynthesisFlagshipProgressStudy exists, false otherwise.
   */
  public boolean existReportSynthesisFlagshipProgressStudy(long reportSynthesisFlagshipProgressStudyID);

  /**
   * This method gets a reportSynthesisFlagshipProgressStudy object by a given reportSynthesisFlagshipProgressStudy identifier.
   * 
   * @param reportSynthesisFlagshipProgressStudyID is the reportSynthesisFlagshipProgressStudy identifier.
   * @return a ReportSynthesisFlagshipProgressStudy object.
   */
  public ReportSynthesisFlagshipProgressStudy find(long id);

  /**
   * This method gets a list of reportSynthesisFlagshipProgressStudy that are active
   * 
   * @return a list from ReportSynthesisFlagshipProgressStudy null if no exist records
   */
  public List<ReportSynthesisFlagshipProgressStudy> findAll();


  /**
   * This method saves the information of the given reportSynthesisFlagshipProgressStudy
   * 
   * @param reportSynthesisFlagshipProgressStudy - is the reportSynthesisFlagshipProgressStudy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisFlagshipProgressStudy was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFlagshipProgressStudy save(ReportSynthesisFlagshipProgressStudy reportSynthesisFlagshipProgressStudy);
}
