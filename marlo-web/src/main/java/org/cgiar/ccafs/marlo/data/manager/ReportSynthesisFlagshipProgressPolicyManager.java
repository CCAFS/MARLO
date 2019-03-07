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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisFlagshipProgressPolicyManager {


  /**
   * This method removes a specific reportSynthesisFlagshipProgressPolicy value from the database.
   * 
   * @param reportSynthesisFlagshipProgressPolicyId is the reportSynthesisFlagshipProgressPolicy identifier.
   * @return true if the reportSynthesisFlagshipProgressPolicy was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisFlagshipProgressPolicy(long reportSynthesisFlagshipProgressPolicyId);


  /**
   * This method validate if the reportSynthesisFlagshipProgressPolicy identify with the given id exists in the system.
   * 
   * @param reportSynthesisFlagshipProgressPolicyID is a reportSynthesisFlagshipProgressPolicy identifier.
   * @return true if the reportSynthesisFlagshipProgressPolicy exists, false otherwise.
   */
  public boolean existReportSynthesisFlagshipProgressPolicy(long reportSynthesisFlagshipProgressPolicyID);


  /**
   * This method gets a list of reportSynthesisFlagshipProgressPolicy that are active
   * 
   * @return a list from ReportSynthesisFlagshipProgressPolicy null if no exist records
   */
  public List<ReportSynthesisFlagshipProgressPolicy> findAll();


  /**
   * This method gets a reportSynthesisFlagshipProgressPolicy object by a given reportSynthesisFlagshipProgressPolicy identifier.
   * 
   * @param reportSynthesisFlagshipProgressPolicyID is the reportSynthesisFlagshipProgressPolicy identifier.
   * @return a ReportSynthesisFlagshipProgressPolicy object.
   */
  public ReportSynthesisFlagshipProgressPolicy getReportSynthesisFlagshipProgressPolicyById(long reportSynthesisFlagshipProgressPolicyID);

  /**
   * This method saves the information of the given reportSynthesisFlagshipProgressPolicy
   * 
   * @param reportSynthesisFlagshipProgressPolicy - is the reportSynthesisFlagshipProgressPolicy object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisFlagshipProgressPolicy was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFlagshipProgressPolicy saveReportSynthesisFlagshipProgressPolicy(ReportSynthesisFlagshipProgressPolicy reportSynthesisFlagshipProgressPolicy);


}
