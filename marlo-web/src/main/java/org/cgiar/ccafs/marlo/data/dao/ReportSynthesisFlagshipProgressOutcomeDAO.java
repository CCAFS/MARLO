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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;

import java.util.List;


public interface ReportSynthesisFlagshipProgressOutcomeDAO {

  /**
   * This method removes a specific reportSynthesisFlagshipProgressOutcome value from the database.
   * 
   * @param reportSynthesisFlagshipProgressOutcomeId is the reportSynthesisFlagshipProgressOutcome identifier.
   * @return true if the reportSynthesisFlagshipProgressOutcome was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisFlagshipProgressOutcome(long reportSynthesisFlagshipProgressOutcomeId);

  /**
   * This method validate if the reportSynthesisFlagshipProgressOutcome identify with the given id exists in the system.
   * 
   * @param reportSynthesisFlagshipProgressOutcomeID is a reportSynthesisFlagshipProgressOutcome identifier.
   * @return true if the reportSynthesisFlagshipProgressOutcome exists, false otherwise.
   */
  public boolean existReportSynthesisFlagshipProgressOutcome(long reportSynthesisFlagshipProgressOutcomeID);

  /**
   * This method gets a reportSynthesisFlagshipProgressOutcome object by a given reportSynthesisFlagshipProgressOutcome
   * identifier.
   * 
   * @param reportSynthesisFlagshipProgressOutcomeID is the reportSynthesisFlagshipProgressOutcome identifier.
   * @return a ReportSynthesisFlagshipProgressOutcome object.
   */
  public ReportSynthesisFlagshipProgressOutcome find(long id);

  /**
   * This method gets a list of reportSynthesisFlagshipProgressOutcome that are active
   * 
   * @return a list from ReportSynthesisFlagshipProgressOutcome null if no exist records
   */
  public List<ReportSynthesisFlagshipProgressOutcome> findAll();


  public ReportSynthesisFlagshipProgressOutcome getOutcomeId(long progressID, long outcomeID);

  /**
   * This method saves the information of the given reportSynthesisFlagshipProgressOutcome
   * 
   * @param reportSynthesisFlagshipProgressOutcome - is the reportSynthesisFlagshipProgressOutcome object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisFlagshipProgressOutcome was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFlagshipProgressOutcome
    save(ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome);
}
