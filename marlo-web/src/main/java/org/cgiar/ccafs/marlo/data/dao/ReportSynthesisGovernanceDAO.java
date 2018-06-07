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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisGovernance;

import java.util.List;


public interface ReportSynthesisGovernanceDAO {

  /**
   * This method removes a specific reportSynthesisGovernance value from the database.
   * 
   * @param reportSynthesisGovernanceId is the reportSynthesisGovernance identifier.
   * @return true if the reportSynthesisGovernance was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisGovernance(long reportSynthesisGovernanceId);

  /**
   * This method validate if the reportSynthesisGovernance identify with the given id exists in the system.
   * 
   * @param reportSynthesisGovernanceID is a reportSynthesisGovernance identifier.
   * @return true if the reportSynthesisGovernance exists, false otherwise.
   */
  public boolean existReportSynthesisGovernance(long reportSynthesisGovernanceID);

  /**
   * This method gets a reportSynthesisGovernance object by a given reportSynthesisGovernance identifier.
   * 
   * @param reportSynthesisGovernanceID is the reportSynthesisGovernance identifier.
   * @return a ReportSynthesisGovernance object.
   */
  public ReportSynthesisGovernance find(long id);

  /**
   * This method gets a list of reportSynthesisGovernance that are active
   * 
   * @return a list from ReportSynthesisGovernance null if no exist records
   */
  public List<ReportSynthesisGovernance> findAll();


  /**
   * This method saves the information of the given reportSynthesisGovernance
   * 
   * @param reportSynthesisGovernance - is the reportSynthesisGovernance object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisGovernance was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisGovernance save(ReportSynthesisGovernance reportSynthesisGovernance);
}
