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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisFlagshipProgressManager {


  /**
   * This method removes a specific reportSynthesisFlagshipProgress value from the database.
   * 
   * @param reportSynthesisFlagshipProgressId is the reportSynthesisFlagshipProgress identifier.
   * @return true if the reportSynthesisFlagshipProgress was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisFlagshipProgress(long reportSynthesisFlagshipProgressId);


  /**
   * This method validate if the reportSynthesisFlagshipProgress identify with the given id exists in the system.
   * 
   * @param reportSynthesisFlagshipProgressID is a reportSynthesisFlagshipProgress identifier.
   * @return true if the reportSynthesisFlagshipProgress exists, false otherwise.
   */
  public boolean existReportSynthesisFlagshipProgress(long reportSynthesisFlagshipProgressID);


  /**
   * This method gets a list of reportSynthesisFlagshipProgress that are active
   * 
   * @return a list from ReportSynthesisFlagshipProgress null if no exist records
   */
  public List<ReportSynthesisFlagshipProgress> findAll();


  /**
   * This method gets a reportSynthesisFlagshipProgress object by a given reportSynthesisFlagshipProgress identifier.
   * 
   * @param reportSynthesisFlagshipProgressID is the reportSynthesisFlagshipProgress identifier.
   * @return a ReportSynthesisFlagshipProgress object.
   */
  public ReportSynthesisFlagshipProgress getReportSynthesisFlagshipProgressById(long reportSynthesisFlagshipProgressID);

  /**
   * This method saves the information of the given reportSynthesisFlagshipProgress
   * 
   * @param reportSynthesisFlagshipProgress - is the reportSynthesisFlagshipProgress object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisFlagshipProgress was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFlagshipProgress saveReportSynthesisFlagshipProgress(ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress);


}
