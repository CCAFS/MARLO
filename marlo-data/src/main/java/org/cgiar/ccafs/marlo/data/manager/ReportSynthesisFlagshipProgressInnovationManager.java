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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovation;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisFlagshipProgressInnovationManager {


  /**
   * This method removes a specific reportSynthesisFlagshipProgressInnovation value from the database.
   * 
   * @param reportSynthesisFlagshipProgressInnovationId is the reportSynthesisFlagshipProgressInnovation identifier.
   * @return true if the reportSynthesisFlagshipProgressInnovation was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisFlagshipProgressInnovation(long reportSynthesisFlagshipProgressInnovationId);


  /**
   * This method validate if the reportSynthesisFlagshipProgressInnovation identify with the given id exists in the
   * system.
   * 
   * @param reportSynthesisFlagshipProgressInnovationID is a reportSynthesisFlagshipProgressInnovation identifier.
   * @return true if the reportSynthesisFlagshipProgressInnovation exists, false otherwise.
   */
  public boolean existReportSynthesisFlagshipProgressInnovation(long reportSynthesisFlagshipProgressInnovationID);


  /**
   * This method gets a list of reportSynthesisFlagshipProgressInnovation that are active
   * 
   * @return a list from ReportSynthesisFlagshipProgressInnovation null if no exist records
   */
  public List<ReportSynthesisFlagshipProgressInnovation> findAll();


  /**
   * This method gets a reportSynthesisFlagshipProgressInnovation object by a given
   * reportSynthesisFlagshipProgressInnovation identifier.
   * 
   * @param reportSynthesisFlagshipProgressInnovationID is the reportSynthesisFlagshipProgressInnovation identifier.
   * @return a ReportSynthesisFlagshipProgressInnovation object.
   */
  public ReportSynthesisFlagshipProgressInnovation
    getReportSynthesisFlagshipProgressInnovationById(long reportSynthesisFlagshipProgressInnovationID);

  /**
   * This method gets a reportSynthesisFlagshipProgressInnovation object by a given projectInnovation id and a phase id
   *
   * @param innovationId the ProjectInnovation identifier
   * @param flagshipProgressId the ReportSynthesisFlagshipProgress identifier
   * @return a ReportSynthesisFlagshipProgressInnovation object.
   */
  public ReportSynthesisFlagshipProgressInnovation
    getReportSynthesisFlagshipProgressInnovationByInnovationAndFlagshipProgress(Long innovationId,
      Long flagshipProgressId);

  /**
   * This method saves the information of the given reportSynthesisFlagshipProgressInnovation
   * 
   * @param reportSynthesisFlagshipProgressInnovation - is the reportSynthesisFlagshipProgressInnovation object with the
   *        new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisFlagshipProgressInnovation was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFlagshipProgressInnovation saveReportSynthesisFlagshipProgressInnovation(
    ReportSynthesisFlagshipProgressInnovation reportSynthesisFlagshipProgressInnovation);

  /**
   * Adds or removes a projectInnovation from the Annual Report (arPhase). If a
   * ReportSynthesisFlagshipProgressInnovation
   * exists, we check if is active and depending on the {@code remove} flag, we activate it / deactivate it. If it does
   * not exist and {@code remove} is {@code true}, we create a new ReportSynthesisFlagshipProgressInnovation.
   * 
   * @param projectInnovation the ProjectInnovation to be added to the AR Phase
   * @param flagshipProgress the ReportSynthesisFlagshipProgress to be linked to this project innovation
   * @param user the User adding the innovation to the Annual report
   * @param remove if we want to add or remove from the Annual Report
   * @return a ReportSynthesisFlagshipProgressInnovation object
   */
  public ReportSynthesisFlagshipProgressInnovation toAnnualReport(ProjectInnovation projectInnovation,
    ReportSynthesisFlagshipProgress flagshipProgress, User user, boolean remove);

  /**
   * Adds or removes a ReportSynthesisFlagshipProgressInnovation from the Annual Report (arPhase) according to the
   * {@code remove} parameter
   * 
   * @param progressInnovation the ReportSynthesisFlagshipProgressInnovation to be added or removed
   * @param remove if we want to add or remove from the Annual Report
   * @return a ReportSynthesisFlagshipProgressInnovation object
   */
  public ReportSynthesisFlagshipProgressInnovation
    toAnnualReport(ReportSynthesisFlagshipProgressInnovation progressInnovation, boolean remove);
}
