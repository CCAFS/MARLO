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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIndicatorGeneral;

import java.util.List;


public interface ReportSynthesisIndicatorGeneralDAO {

  /**
   * This method removes a specific reportSynthesisIndicatorGeneral value from the database.
   * 
   * @param reportSynthesisIndicatorGeneralId is the reportSynthesisIndicatorGeneral identifier.
   * @return true if the reportSynthesisIndicatorGeneral was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisIndicatorGeneral(long reportSynthesisIndicatorGeneralId);

  /**
   * This method validate if the reportSynthesisIndicatorGeneral identify with the given id exists in the system.
   * 
   * @param reportSynthesisIndicatorGeneralID is a reportSynthesisIndicatorGeneral identifier.
   * @return true if the reportSynthesisIndicatorGeneral exists, false otherwise.
   */
  public boolean existReportSynthesisIndicatorGeneral(long reportSynthesisIndicatorGeneralID);

  /**
   * This method gets a reportSynthesisIndicatorGeneral object by a given reportSynthesisIndicatorGeneral identifier.
   * 
   * @param reportSynthesisIndicatorGeneralID is the reportSynthesisIndicatorGeneral identifier.
   * @return a ReportSynthesisIndicatorGeneral object.
   */
  public ReportSynthesisIndicatorGeneral find(long id);

  /**
   * This method gets a list of reportSynthesisIndicatorGeneral that are active
   * 
   * @return a list from ReportSynthesisIndicatorGeneral null if no exist records
   */
  public List<ReportSynthesisIndicatorGeneral> findAll();


  /**
   * This method saves the information of the given reportSynthesisIndicatorGeneral
   * 
   * @param reportSynthesisIndicatorGeneral - is the reportSynthesisIndicatorGeneral object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisIndicatorGeneral was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisIndicatorGeneral save(ReportSynthesisIndicatorGeneral reportSynthesisIndicatorGeneral);
}
