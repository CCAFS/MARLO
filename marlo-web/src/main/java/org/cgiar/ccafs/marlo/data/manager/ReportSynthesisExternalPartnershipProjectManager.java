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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnershipProject;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisExternalPartnershipProjectManager {


  /**
   * This method removes a specific reportSynthesisExternalPartnershipProject value from the database.
   * 
   * @param reportSynthesisExternalPartnershipProjectId is the reportSynthesisExternalPartnershipProject identifier.
   * @return true if the reportSynthesisExternalPartnershipProject was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisExternalPartnershipProject(long reportSynthesisExternalPartnershipProjectId);


  /**
   * This method validate if the reportSynthesisExternalPartnershipProject identify with the given id exists in the system.
   * 
   * @param reportSynthesisExternalPartnershipProjectID is a reportSynthesisExternalPartnershipProject identifier.
   * @return true if the reportSynthesisExternalPartnershipProject exists, false otherwise.
   */
  public boolean existReportSynthesisExternalPartnershipProject(long reportSynthesisExternalPartnershipProjectID);


  /**
   * This method gets a list of reportSynthesisExternalPartnershipProject that are active
   * 
   * @return a list from ReportSynthesisExternalPartnershipProject null if no exist records
   */
  public List<ReportSynthesisExternalPartnershipProject> findAll();


  /**
   * This method gets a reportSynthesisExternalPartnershipProject object by a given reportSynthesisExternalPartnershipProject identifier.
   * 
   * @param reportSynthesisExternalPartnershipProjectID is the reportSynthesisExternalPartnershipProject identifier.
   * @return a ReportSynthesisExternalPartnershipProject object.
   */
  public ReportSynthesisExternalPartnershipProject getReportSynthesisExternalPartnershipProjectById(long reportSynthesisExternalPartnershipProjectID);

  /**
   * This method saves the information of the given reportSynthesisExternalPartnershipProject
   * 
   * @param reportSynthesisExternalPartnershipProject - is the reportSynthesisExternalPartnershipProject object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisExternalPartnershipProject was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisExternalPartnershipProject saveReportSynthesisExternalPartnershipProject(ReportSynthesisExternalPartnershipProject reportSynthesisExternalPartnershipProject);


}
