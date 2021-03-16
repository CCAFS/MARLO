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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetCasesLinks;

import java.util.List;


public interface ReportSynthesisSrfProgressTargetCasesLinksDAO {

  /**
   * This method removes a specific reportSynthesisSrfProgressTargetCasesLinks value from the database.
   * 
   * @param reportSynthesisSrfProgressTargetCasesLinksId is the reportSynthesisSrfProgressTargetCasesLinks identifier.
   * @return true if the reportSynthesisSrfProgressTargetCasesLinks was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisSrfProgressTargetCasesLinks(long reportSynthesisSrfProgressTargetCasesLinksId);

  /**
   * This method validate if the reportSynthesisSrfProgressTargetCasesLinks identify with the given id exists in the system.
   * 
   * @param reportSynthesisSrfProgressTargetCasesLinksID is a reportSynthesisSrfProgressTargetCasesLinks identifier.
   * @return true if the reportSynthesisSrfProgressTargetCasesLinks exists, false otherwise.
   */
  public boolean existReportSynthesisSrfProgressTargetCasesLinks(long reportSynthesisSrfProgressTargetCasesLinksID);

  /**
   * This method gets a reportSynthesisSrfProgressTargetCasesLinks object by a given reportSynthesisSrfProgressTargetCasesLinks identifier.
   * 
   * @param reportSynthesisSrfProgressTargetCasesLinksID is the reportSynthesisSrfProgressTargetCasesLinks identifier.
   * @return a ReportSynthesisSrfProgressTargetCasesLinks object.
   */
  public ReportSynthesisSrfProgressTargetCasesLinks find(long id);

  /**
   * This method gets a list of reportSynthesisSrfProgressTargetCasesLinks that are active
   * 
   * @return a list from ReportSynthesisSrfProgressTargetCasesLinks null if no exist records
   */
  public List<ReportSynthesisSrfProgressTargetCasesLinks> findAll();


  /**
   * This method saves the information of the given reportSynthesisSrfProgressTargetCasesLinks
   * 
   * @param reportSynthesisSrfProgressTargetCasesLinks - is the reportSynthesisSrfProgressTargetCasesLinks object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisSrfProgressTargetCasesLinks was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisSrfProgressTargetCasesLinks save(ReportSynthesisSrfProgressTargetCasesLinks reportSynthesisSrfProgressTargetCasesLinks);
}
