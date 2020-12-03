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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetCases;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisSrfProgressTargetCasesManager {


  /**
   * This method removes a specific reportSynthesisSrfProgressTargetCases value from the database.
   * 
   * @param reportSynthesisSrfProgressTargetCasesId is the reportSynthesisSrfProgressTargetCases identifier.
   * @return true if the reportSynthesisSrfProgressTargetCases was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisSrfProgressTargetCases(long reportSynthesisSrfProgressTargetCasesId);


  /**
   * This method validate if the reportSynthesisSrfProgressTargetCases identify with the given id exists in the system.
   * 
   * @param reportSynthesisSrfProgressTargetCasesID is a reportSynthesisSrfProgressTargetCases identifier.
   * @return true if the reportSynthesisSrfProgressTargetCases exists, false otherwise.
   */
  public boolean existReportSynthesisSrfProgressTargetCases(long reportSynthesisSrfProgressTargetCasesID);


  /**
   * This method gets a list of reportSynthesisSrfProgressTargetCases that are active
   * 
   * @return a list from ReportSynthesisSrfProgressTargetCases null if no exist records
   */
  public List<ReportSynthesisSrfProgressTargetCases> findAll();


  /**
   * This method gets a reportSynthesisSrfProgressTargetCases object by a given reportSynthesisSrfProgressTargetCases identifier.
   * 
   * @param reportSynthesisSrfProgressTargetCasesID is the reportSynthesisSrfProgressTargetCases identifier.
   * @return a ReportSynthesisSrfProgressTargetCases object.
   */
  public ReportSynthesisSrfProgressTargetCases getReportSynthesisSrfProgressTargetCasesById(long reportSynthesisSrfProgressTargetCasesID);

  /**
   * This method saves the information of the given reportSynthesisSrfProgressTargetCases
   * 
   * @param reportSynthesisSrfProgressTargetCases - is the reportSynthesisSrfProgressTargetCases object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisSrfProgressTargetCases was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisSrfProgressTargetCases saveReportSynthesisSrfProgressTargetCases(ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTargetCases);


}
