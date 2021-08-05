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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressStudy;
import org.cgiar.ccafs.marlo.data.model.User;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisFlagshipProgressStudyManager {


  /**
   * This method removes a specific reportSynthesisFlagshipProgressStudy value from the database.
   * 
   * @param reportSynthesisFlagshipProgressStudyId is the reportSynthesisFlagshipProgressStudy identifier.
   * @return true if the reportSynthesisFlagshipProgressStudy was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisFlagshipProgressStudy(long reportSynthesisFlagshipProgressStudyId);


  /**
   * This method validate if the reportSynthesisFlagshipProgressStudy identify with the given id exists in the system.
   * 
   * @param reportSynthesisFlagshipProgressStudyID is a reportSynthesisFlagshipProgressStudy identifier.
   * @return true if the reportSynthesisFlagshipProgressStudy exists, false otherwise.
   */
  public boolean existReportSynthesisFlagshipProgressStudy(long reportSynthesisFlagshipProgressStudyID);


  /**
   * This method gets a list of reportSynthesisFlagshipProgressStudy that are active
   * 
   * @return a list from ReportSynthesisFlagshipProgressStudy null if no exist records
   */
  public List<ReportSynthesisFlagshipProgressStudy> findAll();

  /**
   * This method gets a reportSynthesisFlagshipProgressStudy object by a given reportSynthesisFlagshipProgressStudy
   * identifier.
   * 
   * @param reportSynthesisFlagshipProgressStudyID is the reportSynthesisFlagshipProgressStudy identifier.
   * @return a ReportSynthesisFlagshipProgressStudy object.
   */
  public ReportSynthesisFlagshipProgressStudy
    getReportSynthesisFlagshipProgressStudyById(long reportSynthesisFlagshipProgressStudyID);

  /**
   * This method gets a reportSynthesisFlagshipProgressStudy object by a given projectExpectedStudy id and a phase id
   *
   * @param studyId the ProjectExpectedStudy identifier
   * @param flagshipProgressId the ReportSynthesisFlagshipProgress identifier
   * @return a ReportSynthesisFlagshipProgressStudy object.
   */
  public ReportSynthesisFlagshipProgressStudy
    getReportSynthesisFlagshipProgressStudyByStudyAndFlagshipProgress(Long studyId, Long flagshipProgressId);

  /**
   * This method saves the information of the given reportSynthesisFlagshipProgressStudy
   * 
   * @param reportSynthesisFlagshipProgressStudy - is the reportSynthesisFlagshipProgressStudy object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisFlagshipProgressStudy was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFlagshipProgressStudy
    saveReportSynthesisFlagshipProgressStudy(ReportSynthesisFlagshipProgressStudy reportSynthesisFlagshipProgressStudy);

  /**
   * Adds or removes a projectExpectedStudy from the Annual Report (arPhase). If a ReportSynthesisFlagshipProgressStudy
   * exists, we check if is active and depending on the {@code remove} flag, we activate it / deactivate it. If it does
   * not exist and {@code remove} is {@code true}, we create a new ReportSynthesisFlagshipProgressStudy.
   * 
   * @param projectExpectedStudy the ProjectExpectedStudy to be added to the AR Phase
   * @param flagshipProgress the ReportSynthesisFlagshipProgress to be linked to this project study
   * @param user the User adding the study to the Annual report
   * @param remove if we want to add or remove from the Annual Report
   * @return a ReportSynthesisFlagshipProgressStudy object
   */
  public ReportSynthesisFlagshipProgressStudy toAnnualReport(ProjectExpectedStudy projectExpectedStudy,
    ReportSynthesisFlagshipProgress flagshipProgress, User user, boolean remove);

  /**
   * Adds or removes a ReportSynthesisFlagshipProgressStudy from the Annual Report (arPhase) according to the
   * {@code remove} parameter
   * 
   * @param progressStudy the ReportSynthesisFlagshipProgressStudy to be added or removed
   * @param remove if we want to add or remove from the Annual Report
   * @return a ReportSynthesisFlagshipProgressStudy object
   */
  public ReportSynthesisFlagshipProgressStudy toAnnualReport(ReportSynthesisFlagshipProgressStudy progressStudy,
    boolean remove);
}
