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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTarget;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisSrfProgressTargetManager {


  /**
   * This method removes a specific reportSynthesisSrfProgressTarget value from the database.
   * 
   * @param reportSynthesisSrfProgressTargetId is the reportSynthesisSrfProgressTarget identifier.
   * @return true if the reportSynthesisSrfProgressTarget was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisSrfProgressTarget(long reportSynthesisSrfProgressTargetId);


  /**
   * This method validate if the reportSynthesisSrfProgressTarget identify with the given id exists in the system.
   * 
   * @param reportSynthesisSrfProgressTargetID is a reportSynthesisSrfProgressTarget identifier.
   * @return true if the reportSynthesisSrfProgressTarget exists, false otherwise.
   */
  public boolean existReportSynthesisSrfProgressTarget(long reportSynthesisSrfProgressTargetID);


  /**
   * This method gets a list of reportSynthesisSrfProgressTarget that are active
   * 
   * @return a list from ReportSynthesisSrfProgressTarget null if no exist records
   */
  public List<ReportSynthesisSrfProgressTarget> findAll();


  /**
   * This method gets a reportSynthesisSrfProgressTarget object by a given reportSynthesisSrfProgressTarget identifier.
   * 
   * @param reportSynthesisSrfProgressTargetID is the reportSynthesisSrfProgressTarget identifier.
   * @return a ReportSynthesisSrfProgressTarget object.
   */
  public ReportSynthesisSrfProgressTarget getReportSynthesisSrfProgressTargetById(long reportSynthesisSrfProgressTargetID);

  /**
   * This method saves the information of the given reportSynthesisSrfProgressTarget
   * 
   * @param reportSynthesisSrfProgressTarget - is the reportSynthesisSrfProgressTarget object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisSrfProgressTarget was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisSrfProgressTarget saveReportSynthesisSrfProgressTarget(ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTarget);


}
