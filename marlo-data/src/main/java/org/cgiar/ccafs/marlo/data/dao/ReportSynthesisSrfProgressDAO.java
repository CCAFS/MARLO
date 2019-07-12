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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgress;

import java.util.List;


public interface ReportSynthesisSrfProgressDAO {

  /**
   * This method removes a specific reportSynthesisSrfProgress value from the database.
   * 
   * @param reportSynthesisSrfProgressId is the reportSynthesisSrfProgress identifier.
   * @return true if the reportSynthesisSrfProgress was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisSrfProgress(long reportSynthesisSrfProgressId);

  /**
   * This method validate if the reportSynthesisSrfProgress identify with the given id exists in the system.
   * 
   * @param reportSynthesisSrfProgressID is a reportSynthesisSrfProgress identifier.
   * @return true if the reportSynthesisSrfProgress exists, false otherwise.
   */
  public boolean existReportSynthesisSrfProgress(long reportSynthesisSrfProgressID);

  /**
   * This method gets a reportSynthesisSrfProgress object by a given reportSynthesisSrfProgress identifier.
   * 
   * @param reportSynthesisSrfProgressID is the reportSynthesisSrfProgress identifier.
   * @return a ReportSynthesisSrfProgress object.
   */
  public ReportSynthesisSrfProgress find(long id);

  /**
   * This method gets a list of reportSynthesisSrfProgress that are active
   * 
   * @return a list from ReportSynthesisSrfProgress null if no exist records
   */
  public List<ReportSynthesisSrfProgress> findAll();


  /**
   * This method saves the information of the given reportSynthesisSrfProgress
   * 
   * @param reportSynthesisSrfProgress - is the reportSynthesisSrfProgress object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisSrfProgress was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisSrfProgress save(ReportSynthesisSrfProgress reportSynthesisSrfProgress);
}
