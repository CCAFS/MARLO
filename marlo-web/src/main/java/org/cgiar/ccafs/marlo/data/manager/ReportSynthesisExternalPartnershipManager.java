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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnershipDTO;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisExternalPartnershipManager {


  /**
   * This method removes a specific reportSynthesisExternalPartnership value from the database.
   * 
   * @param reportSynthesisExternalPartnershipId is the reportSynthesisExternalPartnership identifier.
   * @return true if the reportSynthesisExternalPartnership was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisExternalPartnership(long reportSynthesisExternalPartnershipId);


  /**
   * This method validate if the reportSynthesisExternalPartnership identify with the given id exists in the system.
   * 
   * @param reportSynthesisExternalPartnershipID is a reportSynthesisExternalPartnership identifier.
   * @return true if the reportSynthesisExternalPartnership exists, false otherwise.
   */
  public boolean existReportSynthesisExternalPartnership(long reportSynthesisExternalPartnershipID);

  /**
   * This method gets a list of reportSynthesisExternalPartnership that are active
   * 
   * @return a list from ReportSynthesisExternalPartnership null if no exist records
   */
  public List<ReportSynthesisExternalPartnership> findAll();


  /**
   * get the flagships external Partnership information
   * 
   * @param lInstitutions
   * @param phaseID
   * @return
   */
  public List<ReportSynthesisExternalPartnership>
    getFlagshipCExternalPartnership(List<LiaisonInstitution> lInstitutions, long phaseID);

  /**
   * Get the Table G Synthesis Information
   * 
   * @param lInstitutions
   * @param phaseID
   * @param loggedCrp
   * @param liaisonInstitutionPMU
   * @return
   */
  public List<ReportSynthesisExternalPartnershipDTO> getPlannedPartnershipList(List<LiaisonInstitution> lInstitutions,
    long phaseID, GlobalUnit loggedCrp, LiaisonInstitution liaisonInstitutionPMU);

  /**
   * This method gets a reportSynthesisExternalPartnership object by a given reportSynthesisExternalPartnership
   * identifier.
   * 
   * @param reportSynthesisExternalPartnershipID is the reportSynthesisExternalPartnership identifier.
   * @return a ReportSynthesisExternalPartnership object.
   */
  public ReportSynthesisExternalPartnership
    getReportSynthesisExternalPartnershipById(long reportSynthesisExternalPartnershipID);

  /**
   * This method saves the information of the given reportSynthesisExternalPartnership
   * 
   * @param reportSynthesisExternalPartnership - is the reportSynthesisExternalPartnership object with the new
   *        information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisExternalPartnership was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisExternalPartnership
    saveReportSynthesisExternalPartnership(ReportSynthesisExternalPartnership reportSynthesisExternalPartnership);


}
