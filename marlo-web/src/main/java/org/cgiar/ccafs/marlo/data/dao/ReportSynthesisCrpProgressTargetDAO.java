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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressTarget;

import java.util.List;


public interface ReportSynthesisCrpProgressTargetDAO {

  /**
   * This method removes a specific reportSynthesisCrpProgressTarget value from the database.
   * 
   * @param reportSynthesisCrpProgressTargetId is the reportSynthesisCrpProgressTarget identifier.
   * @return true if the reportSynthesisCrpProgressTarget was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisCrpProgressTarget(long reportSynthesisCrpProgressTargetId);

  /**
   * This method validate if the reportSynthesisCrpProgressTarget identify with the given id exists in the system.
   * 
   * @param reportSynthesisCrpProgressTargetID is a reportSynthesisCrpProgressTarget identifier.
   * @return true if the reportSynthesisCrpProgressTarget exists, false otherwise.
   */
  public boolean existReportSynthesisCrpProgressTarget(long reportSynthesisCrpProgressTargetID);

  /**
   * This method gets a reportSynthesisCrpProgressTarget object by a given reportSynthesisCrpProgressTarget identifier.
   * 
   * @param reportSynthesisCrpProgressTargetID is the reportSynthesisCrpProgressTarget identifier.
   * @return a ReportSynthesisCrpProgressTarget object.
   */
  public ReportSynthesisCrpProgressTarget find(long id);

  /**
   * This method gets a list of reportSynthesisCrpProgressTarget that are active
   * 
   * @return a list from ReportSynthesisCrpProgressTarget null if no exist records
   */
  public List<ReportSynthesisCrpProgressTarget> findAll();


  /**
   * This method saves the information of the given reportSynthesisCrpProgressTarget
   * 
   * @param reportSynthesisCrpProgressTarget - is the reportSynthesisCrpProgressTarget object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisCrpProgressTarget was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisCrpProgressTarget save(ReportSynthesisCrpProgressTarget reportSynthesisCrpProgressTarget);
}
