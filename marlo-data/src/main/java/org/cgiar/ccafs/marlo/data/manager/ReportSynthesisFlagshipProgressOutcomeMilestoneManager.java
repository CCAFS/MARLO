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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisFlagshipProgressOutcomeMilestoneManager {


  /**
   * This method removes a specific reportSynthesisFlagshipProgressOutcomeMilestone value from the database.
   * 
   * @param reportSynthesisFlagshipProgressOutcomeMilestoneId is the reportSynthesisFlagshipProgressOutcomeMilestone
   *        identifier.
   * @return true if the reportSynthesisFlagshipProgressOutcomeMilestone was successfully deleted, false otherwise.
   */
  public void
    deleteReportSynthesisFlagshipProgressOutcomeMilestone(long reportSynthesisFlagshipProgressOutcomeMilestoneId);


  /**
   * This method validate if the reportSynthesisFlagshipProgressOutcomeMilestone identify with the given id exists in
   * the system.
   * 
   * @param reportSynthesisFlagshipProgressOutcomeMilestoneID is a reportSynthesisFlagshipProgressOutcomeMilestone
   *        identifier.
   * @return true if the reportSynthesisFlagshipProgressOutcomeMilestone exists, false otherwise.
   */
  public boolean
    existReportSynthesisFlagshipProgressOutcomeMilestone(long reportSynthesisFlagshipProgressOutcomeMilestoneID);


  /**
   * This method gets a list of reportSynthesisFlagshipProgressOutcomeMilestone that are active
   * 
   * @return a list from ReportSynthesisFlagshipProgressOutcomeMilestone null if no exist records
   */
  public List<ReportSynthesisFlagshipProgressOutcomeMilestone> findAll();


  public ReportSynthesisFlagshipProgressOutcomeMilestone getMilestoneId(long progressID, long outcomeID);

  /**
   * This method gets a reportSynthesisFlagshipProgressOutcomeMilestone object by a given
   * reportSynthesisFlagshipProgressOutcomeMilestone identifier.
   * 
   * @param reportSynthesisFlagshipProgressOutcomeMilestoneID is the reportSynthesisFlagshipProgressOutcomeMilestone
   *        identifier.
   * @return a ReportSynthesisFlagshipProgressOutcomeMilestone object.
   */
  public ReportSynthesisFlagshipProgressOutcomeMilestone
    getReportSynthesisFlagshipProgressOutcomeMilestoneById(long reportSynthesisFlagshipProgressOutcomeMilestoneID);

  public ReportSynthesisFlagshipProgressOutcomeMilestone getReportSynthesisMilestoneFromOutcomeIdAndMilestoneId(
    long reportSynthesisFlagshipProgressId, long crpProgramOutcomeId, long crpMilestoneId);

  /**
   * This method saves the information of the given reportSynthesisFlagshipProgressOutcomeMilestone
   * 
   * @param reportSynthesisFlagshipProgressOutcomeMilestone - is the reportSynthesisFlagshipProgressOutcomeMilestone
   *        object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisFlagshipProgressOutcomeMilestone was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFlagshipProgressOutcomeMilestone saveReportSynthesisFlagshipProgressOutcomeMilestone(
    ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone);


}
