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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisRisk;

import java.util.List;


public interface ReportSynthesisRiskDAO {

  /**
   * This method removes a specific reportSynthesisRisk value from the database.
   * 
   * @param reportSynthesisRiskId is the reportSynthesisRisk identifier.
   * @return true if the reportSynthesisRisk was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisRisk(long reportSynthesisRiskId);

  /**
   * This method validate if the reportSynthesisRisk identify with the given id exists in the system.
   * 
   * @param reportSynthesisRiskID is a reportSynthesisRisk identifier.
   * @return true if the reportSynthesisRisk exists, false otherwise.
   */
  public boolean existReportSynthesisRisk(long reportSynthesisRiskID);

  /**
   * This method gets a reportSynthesisRisk object by a given reportSynthesisRisk identifier.
   * 
   * @param reportSynthesisRiskID is the reportSynthesisRisk identifier.
   * @return a ReportSynthesisRisk object.
   */
  public ReportSynthesisRisk find(long id);

  /**
   * This method gets a list of reportSynthesisRisk that are active
   * 
   * @return a list from ReportSynthesisRisk null if no exist records
   */
  public List<ReportSynthesisRisk> findAll();


  /**
   * This method saves the information of the given reportSynthesisRisk
   * 
   * @param reportSynthesisRisk - is the reportSynthesisRisk object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisRisk was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisRisk save(ReportSynthesisRisk reportSynthesisRisk);
}
