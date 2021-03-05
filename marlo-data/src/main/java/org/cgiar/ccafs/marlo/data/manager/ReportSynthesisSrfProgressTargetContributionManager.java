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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetContribution;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisSrfProgressTargetContributionManager {


  /**
   * This method removes a specific reportSynthesisSrfProgressTargetContribution value from the database.
   * 
   * @param reportSynthesisSrfProgressTargetContributionId is the reportSynthesisSrfProgressTargetContribution
   *        identifier.
   * @return true if the reportSynthesisSrfProgressTargetContribution was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisSrfProgressTargetContribution(long reportSynthesisSrfProgressTargetContributionId);


  /**
   * This method validate if the reportSynthesisSrfProgressTargetContribution identify with the given id exists in the
   * system.
   * 
   * @param reportSynthesisSrfProgressTargetContributionID is a reportSynthesisSrfProgressTargetContribution identifier.
   * @return true if the reportSynthesisSrfProgressTargetContribution exists, false otherwise.
   */
  public boolean existReportSynthesisSrfProgressTargetContribution(long reportSynthesisSrfProgressTargetContributionID);


  /**
   * This method gets a list of reportSynthesisSrfProgressTargetContribution that are active
   * 
   * @return a list from ReportSynthesisSrfProgressTargetContribution null if no exist records
   */
  public List<ReportSynthesisSrfProgressTargetContribution> findAll();


  /**
   * This method gets a reportSynthesisSrfProgressTargetContribution list by a given
   * reportSynthesisSrfProgressTargetContribution identifier.
   * 
   * @param reportSynthesisSrfProgressTargetContributionID is the reportSynthesisSrfProgressTargetContribution
   *        identifier.
   * @return a ReportSynthesisSrfProgressTargetContribution object.
   */
  public List<ReportSynthesisSrfProgressTargetContribution>
    findBySloTargetID(long reportSynthesisSrfProgressTargetContributionID);

  /**
   * This method gets a reportSynthesisSrfProgressTargetContribution object by a given sloTargetID identifier.
   * 
   * @param sloTargetId is the sloTarget identifier.
   * @return a ReportSynthesisSrfProgressTargetContribution object.
   */
  public ReportSynthesisSrfProgressTargetContribution
    getReportSynthesisSrfProgressTargetContributionById(long sloTargetID);

  /**
   * This method saves the information of the given reportSynthesisSrfProgressTargetContribution
   * 
   * @param reportSynthesisSrfProgressTargetContribution - is the reportSynthesisSrfProgressTargetContribution object
   *        with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisSrfProgressTargetContribution was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisSrfProgressTargetContribution saveReportSynthesisSrfProgressTargetContribution(
    ReportSynthesisSrfProgressTargetContribution reportSynthesisSrfProgressTargetContribution);


}
