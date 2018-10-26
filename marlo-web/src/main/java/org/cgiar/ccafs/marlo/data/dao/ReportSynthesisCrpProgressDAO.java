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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgress;

import java.util.List;


public interface ReportSynthesisCrpProgressDAO {

  /**
   * This method removes a specific reportSynthesisCrpProgress value from the database.
   * 
   * @param reportSynthesisCrpProgressId is the reportSynthesisCrpProgress identifier.
   * @return true if the reportSynthesisCrpProgress was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisCrpProgress(long reportSynthesisCrpProgressId);

  /**
   * This method validate if the reportSynthesisCrpProgress identify with the given id exists in the system.
   * 
   * @param reportSynthesisCrpProgressID is a reportSynthesisCrpProgress identifier.
   * @return true if the reportSynthesisCrpProgress exists, false otherwise.
   */
  public boolean existReportSynthesisCrpProgress(long reportSynthesisCrpProgressID);

  /**
   * This method gets a reportSynthesisCrpProgress object by a given reportSynthesisCrpProgress identifier.
   * 
   * @param reportSynthesisCrpProgressID is the reportSynthesisCrpProgress identifier.
   * @return a ReportSynthesisCrpProgress object.
   */
  public ReportSynthesisCrpProgress find(long id);

  /**
   * This method gets a list of reportSynthesisCrpProgress that are active
   * 
   * @return a list from ReportSynthesisCrpProgress null if no exist records
   */
  public List<ReportSynthesisCrpProgress> findAll();


  /**
   * This method saves the information of the given reportSynthesisCrpProgress
   * 
   * @param reportSynthesisCrpProgress - is the reportSynthesisCrpProgress object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisCrpProgress was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisCrpProgress save(ReportSynthesisCrpProgress reportSynthesisCrpProgress);
}
