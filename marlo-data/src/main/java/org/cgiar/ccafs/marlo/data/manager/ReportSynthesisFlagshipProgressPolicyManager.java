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

import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;
import org.cgiar.ccafs.marlo.data.model.User;

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
   * This method gets a reportSynthesisFlagshipProgressPolicy object by a given reportSynthesisFlagshipProgressPolicy
   * identifier.
   * 
   * @param reportSynthesisFlagshipProgressPolicyID is the reportSynthesisFlagshipProgressPolicy identifier.
   * @return a ReportSynthesisFlagshipProgressPolicy object.
   */
  public ReportSynthesisFlagshipProgressPolicy
    getReportSynthesisFlagshipProgressPolicyById(long reportSynthesisFlagshipProgressPolicyID);

  /**
   * This method gets a reportSynthesisFlagshipProgressPolicy object by a given projectPolicy id and a phase id
   *
   * @param policyId the ProjectPolicy identifier
   * @param flagshipProgressId the ReportSynthesisFlagshipProgress identifier
   * @return a ReportSynthesisFlagshipProgressPolicy object.
   */
  public ReportSynthesisFlagshipProgressPolicy
    getReportSynthesisFlagshipProgressPolicyByPolicyAndFlagshipProgress(Long policyId, Long flagshipProgressId);

  /**
   * This method saves the information of the given reportSynthesisFlagshipProgressPolicy
   * 
   * @param reportSynthesisFlagshipProgressPolicy - is the reportSynthesisFlagshipProgressPolicy object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisFlagshipProgressPolicy was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFlagshipProgressPolicy saveReportSynthesisFlagshipProgressPolicy(
    ReportSynthesisFlagshipProgressPolicy reportSynthesisFlagshipProgressPolicy);

  /**
   * Adds or removes a projectPolicy from the Annual Report (arPhase). If a ReportSynthesisFlagshipProgressPolicy
   * exists, we check if is active and depending on the {@code remove} flag, we activate it / deactivate it. If it does
   * not exist and {@code remove} is {@code true}, we create a new ReportSynthesisFlagshipProgressPolicy.
   * 
   * @param projectPolicy the ProjectPolicy to be added to the AR Phase
   * @param flagshipProgress the ReportSynthesisFlagshipProgress to be linked to this project policy
   * @param user the User adding the policy to the Annual report
   * @param remove if we want to add or remove from the Annual Report
   * @return a ReportSynthesisFlagshipProgressPolicy object
   */
  public ReportSynthesisFlagshipProgressPolicy toAnnualReport(ProjectPolicy projectPolicy,
    ReportSynthesisFlagshipProgress flagshipProgress, User user, boolean remove);

  /**
   * Adds or removes a ReportSynthesisFlagshipProgressPolicy from the Annual Report (arPhase) according to the
   * {@code remove} parameter
   * 
   * @param progressPolicy the ReportSynthesisFlagshipProgressPolicy to be added or removed
   * @param remove if we want to add or remove from the Annual Report
   * @return a ReportSynthesisFlagshipProgressPolicy object
   */
  public ReportSynthesisFlagshipProgressPolicy toAnnualReport(ReportSynthesisFlagshipProgressPolicy progressPolicy,
    boolean remove);
}
