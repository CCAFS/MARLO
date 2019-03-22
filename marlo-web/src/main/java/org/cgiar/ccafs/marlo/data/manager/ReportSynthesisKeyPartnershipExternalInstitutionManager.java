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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalInstitution;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisKeyPartnershipExternalInstitutionManager {


  /**
   * This method removes a specific reportSynthesisKeyPartnershipExternalInstitution value from the database.
   * 
   * @param reportSynthesisKeyPartnershipExternalInstitutionId is the reportSynthesisKeyPartnershipExternalInstitution identifier.
   * @return true if the reportSynthesisKeyPartnershipExternalInstitution was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisKeyPartnershipExternalInstitution(long reportSynthesisKeyPartnershipExternalInstitutionId);


  /**
   * This method validate if the reportSynthesisKeyPartnershipExternalInstitution identify with the given id exists in the system.
   * 
   * @param reportSynthesisKeyPartnershipExternalInstitutionID is a reportSynthesisKeyPartnershipExternalInstitution identifier.
   * @return true if the reportSynthesisKeyPartnershipExternalInstitution exists, false otherwise.
   */
  public boolean existReportSynthesisKeyPartnershipExternalInstitution(long reportSynthesisKeyPartnershipExternalInstitutionID);


  /**
   * This method gets a list of reportSynthesisKeyPartnershipExternalInstitution that are active
   * 
   * @return a list from ReportSynthesisKeyPartnershipExternalInstitution null if no exist records
   */
  public List<ReportSynthesisKeyPartnershipExternalInstitution> findAll();


  /**
   * This method gets a reportSynthesisKeyPartnershipExternalInstitution object by a given reportSynthesisKeyPartnershipExternalInstitution identifier.
   * 
   * @param reportSynthesisKeyPartnershipExternalInstitutionID is the reportSynthesisKeyPartnershipExternalInstitution identifier.
   * @return a ReportSynthesisKeyPartnershipExternalInstitution object.
   */
  public ReportSynthesisKeyPartnershipExternalInstitution getReportSynthesisKeyPartnershipExternalInstitutionById(long reportSynthesisKeyPartnershipExternalInstitutionID);

  /**
   * This method saves the information of the given reportSynthesisKeyPartnershipExternalInstitution
   * 
   * @param reportSynthesisKeyPartnershipExternalInstitution - is the reportSynthesisKeyPartnershipExternalInstitution object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisKeyPartnershipExternalInstitution was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisKeyPartnershipExternalInstitution saveReportSynthesisKeyPartnershipExternalInstitution(ReportSynthesisKeyPartnershipExternalInstitution reportSynthesisKeyPartnershipExternalInstitution);


}
