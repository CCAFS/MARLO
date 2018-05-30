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

import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudyDTO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgress;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisCrpProgressManager {


  /**
   * This method removes a specific reportSynthesisCrpProgress value from the database.
   * 
   * @param reportSynthesisCrpProgressId is the reportSynthesisCrpProgress identifier.
   * @return true if the reportSynthesisCrpProgress was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisCrpProgress(long reportSynthesisCrpProgressId);


  /**
   * This method validate if the reportSynthesisCrpProgress identify with the given id exists in the system.
   * 
   * @param reportSynthesisCrpProgressID is a reportSynthesisCrpProgress identifier.
   * @return true if the reportSynthesisCrpProgress exists, false otherwise.
   */
  public boolean existReportSynthesisCrpProgress(long reportSynthesisCrpProgressID);


  /**
   * This method gets a list of reportSynthesisCrpProgress that are active
   * 
   * @return a list from ReportSynthesisCrpProgress null if no exist records
   */
  public List<ReportSynthesisCrpProgress> findAll();


  /**
   * Shows to the pmu the Flagship Crp Progress towards SLOs and Outcomes
   * 
   * @param lInstitutions - List of Crp Flagships
   * @param phaseID - The Current phase
   * @return list of ReportSynthesisCrpProgress
   */
  public List<ReportSynthesisCrpProgress> getFlagshipCrpProgress(List<LiaisonInstitution> lInstitutions, long phaseID);

  /**
   * Shows to the pmu the Flagship Crp Progress Case Studies that included in this annual report synthesis
   * 
   * @param lInstitutions - List of Crp Flagships
   * @param phaseID - The Current pahse
   * @return PowbEvidencePlannedStudyDTO studies information
   */
  List<PowbEvidencePlannedStudyDTO> getPlannedList(List<LiaisonInstitution> lInstitutions, long phaseID,
    GlobalUnit loggedCrp, LiaisonInstitution liaisonInstitutionPMU);

  /**
   * This method gets a reportSynthesisCrpProgress object by a given reportSynthesisCrpProgress identifier.
   * 
   * @param reportSynthesisCrpProgressID is the reportSynthesisCrpProgress identifier.
   * @return a ReportSynthesisCrpProgress object.
   */
  public ReportSynthesisCrpProgress getReportSynthesisCrpProgressById(long reportSynthesisCrpProgressID);

  /**
   * This method saves the information of the given reportSynthesisCrpProgress
   * 
   * @param reportSynthesisCrpProgress - is the reportSynthesisCrpProgress object with the new information to be
   *        added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisCrpProgress was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisCrpProgress
    saveReportSynthesisCrpProgress(ReportSynthesisCrpProgress reportSynthesisCrpProgress);


}
