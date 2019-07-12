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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalMainArea;

import java.util.List;


public interface ReportSynthesisKeyPartnershipExternalMainAreaDAO {

  /**
   * This method removes a specific reportSynthesisKeyPartnershipExternalMainArea value from the database.
   * 
   * @param reportSynthesisKeyPartnershipExternalMainAreaId is the reportSynthesisKeyPartnershipExternalMainArea identifier.
   * @return true if the reportSynthesisKeyPartnershipExternalMainArea was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisKeyPartnershipExternalMainArea(long reportSynthesisKeyPartnershipExternalMainAreaId);

  /**
   * This method validate if the reportSynthesisKeyPartnershipExternalMainArea identify with the given id exists in the system.
   * 
   * @param reportSynthesisKeyPartnershipExternalMainAreaID is a reportSynthesisKeyPartnershipExternalMainArea identifier.
   * @return true if the reportSynthesisKeyPartnershipExternalMainArea exists, false otherwise.
   */
  public boolean existReportSynthesisKeyPartnershipExternalMainArea(long reportSynthesisKeyPartnershipExternalMainAreaID);

  /**
   * This method gets a reportSynthesisKeyPartnershipExternalMainArea object by a given reportSynthesisKeyPartnershipExternalMainArea identifier.
   * 
   * @param reportSynthesisKeyPartnershipExternalMainAreaID is the reportSynthesisKeyPartnershipExternalMainArea identifier.
   * @return a ReportSynthesisKeyPartnershipExternalMainArea object.
   */
  public ReportSynthesisKeyPartnershipExternalMainArea find(long id);

  /**
   * This method gets a list of reportSynthesisKeyPartnershipExternalMainArea that are active
   * 
   * @return a list from ReportSynthesisKeyPartnershipExternalMainArea null if no exist records
   */
  public List<ReportSynthesisKeyPartnershipExternalMainArea> findAll();


  /**
   * This method saves the information of the given reportSynthesisKeyPartnershipExternalMainArea
   * 
   * @param reportSynthesisKeyPartnershipExternalMainArea - is the reportSynthesisKeyPartnershipExternalMainArea object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisKeyPartnershipExternalMainArea was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisKeyPartnershipExternalMainArea save(ReportSynthesisKeyPartnershipExternalMainArea reportSynthesisKeyPartnershipExternalMainArea);
}
