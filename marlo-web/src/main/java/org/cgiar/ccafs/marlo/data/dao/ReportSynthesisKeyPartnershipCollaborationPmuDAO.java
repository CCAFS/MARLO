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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaborationPmu;

import java.util.List;


public interface ReportSynthesisKeyPartnershipCollaborationPmuDAO {

  /**
   * This method removes a specific reportSynthesisKeyPartnershipCollaborationPmu value from the database.
   * 
   * @param reportSynthesisKeyPartnershipCollaborationPmuId is the reportSynthesisKeyPartnershipCollaborationPmu identifier.
   * @return true if the reportSynthesisKeyPartnershipCollaborationPmu was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisKeyPartnershipCollaborationPmu(long reportSynthesisKeyPartnershipCollaborationPmuId);

  /**
   * This method validate if the reportSynthesisKeyPartnershipCollaborationPmu identify with the given id exists in the system.
   * 
   * @param reportSynthesisKeyPartnershipCollaborationPmuID is a reportSynthesisKeyPartnershipCollaborationPmu identifier.
   * @return true if the reportSynthesisKeyPartnershipCollaborationPmu exists, false otherwise.
   */
  public boolean existReportSynthesisKeyPartnershipCollaborationPmu(long reportSynthesisKeyPartnershipCollaborationPmuID);

  /**
   * This method gets a reportSynthesisKeyPartnershipCollaborationPmu object by a given reportSynthesisKeyPartnershipCollaborationPmu identifier.
   * 
   * @param reportSynthesisKeyPartnershipCollaborationPmuID is the reportSynthesisKeyPartnershipCollaborationPmu identifier.
   * @return a ReportSynthesisKeyPartnershipCollaborationPmu object.
   */
  public ReportSynthesisKeyPartnershipCollaborationPmu find(long id);

  /**
   * This method gets a list of reportSynthesisKeyPartnershipCollaborationPmu that are active
   * 
   * @return a list from ReportSynthesisKeyPartnershipCollaborationPmu null if no exist records
   */
  public List<ReportSynthesisKeyPartnershipCollaborationPmu> findAll();


  /**
   * This method saves the information of the given reportSynthesisKeyPartnershipCollaborationPmu
   * 
   * @param reportSynthesisKeyPartnershipCollaborationPmu - is the reportSynthesisKeyPartnershipCollaborationPmu object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisKeyPartnershipCollaborationPmu was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisKeyPartnershipCollaborationPmu save(ReportSynthesisKeyPartnershipCollaborationPmu reportSynthesisKeyPartnershipCollaborationPmu);
}
