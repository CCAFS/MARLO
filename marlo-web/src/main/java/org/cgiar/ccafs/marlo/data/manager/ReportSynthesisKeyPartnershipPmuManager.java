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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipPmu;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisKeyPartnershipPmuManager {


  /**
   * This method removes a specific reportSynthesisKeyPartnershipPmu value from the database.
   * 
   * @param reportSynthesisKeyPartnershipPmuId is the reportSynthesisKeyPartnershipPmu identifier.
   * @return true if the reportSynthesisKeyPartnershipPmu was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisKeyPartnershipPmu(long reportSynthesisKeyPartnershipPmuId);


  /**
   * This method validate if the reportSynthesisKeyPartnershipPmu identify with the given id exists in the system.
   * 
   * @param reportSynthesisKeyPartnershipPmuID is a reportSynthesisKeyPartnershipPmu identifier.
   * @return true if the reportSynthesisKeyPartnershipPmu exists, false otherwise.
   */
  public boolean existReportSynthesisKeyPartnershipPmu(long reportSynthesisKeyPartnershipPmuID);


  /**
   * This method gets a list of reportSynthesisKeyPartnershipPmu that are active
   * 
   * @return a list from ReportSynthesisKeyPartnershipPmu null if no exist records
   */
  public List<ReportSynthesisKeyPartnershipPmu> findAll();


  /**
   * This method gets a reportSynthesisKeyPartnershipPmu object by a given reportSynthesisKeyPartnershipPmu identifier.
   * 
   * @param reportSynthesisKeyPartnershipPmuID is the reportSynthesisKeyPartnershipPmu identifier.
   * @return a ReportSynthesisKeyPartnershipPmu object.
   */
  public ReportSynthesisKeyPartnershipPmu getReportSynthesisKeyPartnershipPmuById(long reportSynthesisKeyPartnershipPmuID);

  /**
   * This method saves the information of the given reportSynthesisKeyPartnershipPmu
   * 
   * @param reportSynthesisKeyPartnershipPmu - is the reportSynthesisKeyPartnershipPmu object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisKeyPartnershipPmu was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisKeyPartnershipPmu saveReportSynthesisKeyPartnershipPmu(ReportSynthesisKeyPartnershipPmu reportSynthesisKeyPartnershipPmu);


}
