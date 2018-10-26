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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressMilestone;

import java.util.List;


public interface ReportSynthesisFlagshipProgressMilestoneDAO {

  /**
   * This method removes a specific reportSynthesisFlagshipProgressMilestone value from the database.
   * 
   * @param reportSynthesisFlagshipProgressMilestoneId is the reportSynthesisFlagshipProgressMilestone identifier.
   * @return true if the reportSynthesisFlagshipProgressMilestone was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisFlagshipProgressMilestone(long reportSynthesisFlagshipProgressMilestoneId);

  /**
   * This method validate if the reportSynthesisFlagshipProgressMilestone identify with the given id exists in the
   * system.
   * 
   * @param reportSynthesisFlagshipProgressMilestoneID is a reportSynthesisFlagshipProgressMilestone identifier.
   * @return true if the reportSynthesisFlagshipProgressMilestone exists, false otherwise.
   */
  public boolean existReportSynthesisFlagshipProgressMilestone(long reportSynthesisFlagshipProgressMilestoneID);

  /**
   * This method gets a reportSynthesisFlagshipProgressMilestone object by a given
   * reportSynthesisFlagshipProgressMilestone identifier.
   * 
   * @param reportSynthesisFlagshipProgressMilestoneID is the reportSynthesisFlagshipProgressMilestone identifier.
   * @return a ReportSynthesisFlagshipProgressMilestone object.
   */
  public ReportSynthesisFlagshipProgressMilestone find(long id);

  /**
   * This method gets a list of reportSynthesisFlagshipProgressMilestone that are active
   * 
   * @return a list from ReportSynthesisFlagshipProgressMilestone null if no exist records
   */
  public List<ReportSynthesisFlagshipProgressMilestone> findAll();


  public List<ReportSynthesisFlagshipProgressMilestone> findByProgram(long crpProgramID);

  /**
   * This method saves the information of the given reportSynthesisFlagshipProgressMilestone
   * 
   * @param reportSynthesisFlagshipProgressMilestone - is the reportSynthesisFlagshipProgressMilestone object with the
   *        new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisFlagshipProgressMilestone was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFlagshipProgressMilestone
    save(ReportSynthesisFlagshipProgressMilestone reportSynthesisFlagshipProgressMilestone);
}
