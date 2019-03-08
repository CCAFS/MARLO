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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaborationCrp;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisKeyPartnershipCollaborationCrpManager {


  /**
   * This method removes a specific reportSynthesisKeyPartnershipCollaborationCrp value from the database.
   * 
   * @param reportSynthesisKeyPartnershipCollaborationCrpId is the reportSynthesisKeyPartnershipCollaborationCrp identifier.
   * @return true if the reportSynthesisKeyPartnershipCollaborationCrp was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisKeyPartnershipCollaborationCrp(long reportSynthesisKeyPartnershipCollaborationCrpId);


  /**
   * This method validate if the reportSynthesisKeyPartnershipCollaborationCrp identify with the given id exists in the system.
   * 
   * @param reportSynthesisKeyPartnershipCollaborationCrpID is a reportSynthesisKeyPartnershipCollaborationCrp identifier.
   * @return true if the reportSynthesisKeyPartnershipCollaborationCrp exists, false otherwise.
   */
  public boolean existReportSynthesisKeyPartnershipCollaborationCrp(long reportSynthesisKeyPartnershipCollaborationCrpID);


  /**
   * This method gets a list of reportSynthesisKeyPartnershipCollaborationCrp that are active
   * 
   * @return a list from ReportSynthesisKeyPartnershipCollaborationCrp null if no exist records
   */
  public List<ReportSynthesisKeyPartnershipCollaborationCrp> findAll();


  /**
   * This method gets a reportSynthesisKeyPartnershipCollaborationCrp object by a given reportSynthesisKeyPartnershipCollaborationCrp identifier.
   * 
   * @param reportSynthesisKeyPartnershipCollaborationCrpID is the reportSynthesisKeyPartnershipCollaborationCrp identifier.
   * @return a ReportSynthesisKeyPartnershipCollaborationCrp object.
   */
  public ReportSynthesisKeyPartnershipCollaborationCrp getReportSynthesisKeyPartnershipCollaborationCrpById(long reportSynthesisKeyPartnershipCollaborationCrpID);

  /**
   * This method saves the information of the given reportSynthesisKeyPartnershipCollaborationCrp
   * 
   * @param reportSynthesisKeyPartnershipCollaborationCrp - is the reportSynthesisKeyPartnershipCollaborationCrp object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisKeyPartnershipCollaborationCrp was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisKeyPartnershipCollaborationCrp saveReportSynthesisKeyPartnershipCollaborationCrp(ReportSynthesisKeyPartnershipCollaborationCrp reportSynthesisKeyPartnershipCollaborationCrp);


}
