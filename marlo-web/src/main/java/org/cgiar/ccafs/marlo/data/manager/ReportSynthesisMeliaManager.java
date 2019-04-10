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
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMelia;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMeliaEvaluation;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisMeliaManager {


  /**
   * This method removes a specific reportSynthesisMelia value from the database.
   * 
   * @param reportSynthesisMeliaId is the reportSynthesisMelia identifier.
   * @return true if the reportSynthesisMelia was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisMelia(long reportSynthesisMeliaId);


  /**
   * This method validate if the reportSynthesisMelia identify with the given id exists in the system.
   * 
   * @param reportSynthesisMeliaID is a reportSynthesisMelia identifier.
   * @return true if the reportSynthesisMelia exists, false otherwise.
   */
  public boolean existReportSynthesisMelia(long reportSynthesisMeliaID);


  /**
   * This method gets a list of reportSynthesisMelia that are active
   * 
   * @return a list from ReportSynthesisMelia null if no exist records
   */
  public List<ReportSynthesisMelia> findAll();


  /**
   * Table I-2
   * 
   * @param lInstitutions
   * @param phaseID
   * @return
   */
  public List<ReportSynthesisMeliaEvaluation> flagshipSynthesisEvaluation(List<LiaisonInstitution> lInstitutions,
    long phaseID);

  /**
   * Shows to the pmu the Flagship melia.
   * 
   * @param lInstitutions - List of Crp Flagships
   * @param phaseID - The Current phase
   * @return list of ReportSynthesisCrpProgress
   */
  public List<ReportSynthesisMelia> getFlagshipMelia(List<LiaisonInstitution> lInstitutions, long phaseID);

  /**
   * Shows to the pmu the Flagship Melia Case Studies that included in this annual report synthesis
   * 
   * @param lInstitutions - List of Crp Flagships
   * @param phaseID - The Current pahse
   * @return PowbEvidencePlannedStudyDTO studies information
   */
  List<PowbEvidencePlannedStudyDTO> getMeliaPlannedList(List<LiaisonInstitution> lInstitutions, long phaseID,
    GlobalUnit loggedCrp, LiaisonInstitution liaisonInstitutionPMU);

  /**
   * This method gets a reportSynthesisMelia object by a given reportSynthesisMelia identifier.
   * 
   * @param reportSynthesisMeliaID is the reportSynthesisMelia identifier.
   * @return a ReportSynthesisMelia object.
   */
  public ReportSynthesisMelia getReportSynthesisMeliaById(long reportSynthesisMeliaID);

  public List<ProjectExpectedStudy> getTable10(List<LiaisonInstitution> lInstitutions, long phaseID,
    GlobalUnit loggedCrp, LiaisonInstitution liaisonInstitutionPMU);


  /**
   * This method saves the information of the given reportSynthesisMelia
   * 
   * @param reportSynthesisMelia - is the reportSynthesisMelia object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisMelia was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisMelia saveReportSynthesisMelia(ReportSynthesisMelia reportSynthesisMelia);

}
