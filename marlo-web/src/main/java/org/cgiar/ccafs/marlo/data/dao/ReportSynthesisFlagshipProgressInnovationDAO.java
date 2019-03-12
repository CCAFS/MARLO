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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressInnovation;

import java.util.List;


public interface ReportSynthesisFlagshipProgressInnovationDAO {

  /**
   * This method removes a specific reportSynthesisFlagshipProgressInnovation value from the database.
   * 
   * @param reportSynthesisFlagshipProgressInnovationId is the reportSynthesisFlagshipProgressInnovation identifier.
   * @return true if the reportSynthesisFlagshipProgressInnovation was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisFlagshipProgressInnovation(long reportSynthesisFlagshipProgressInnovationId);

  /**
   * This method validate if the reportSynthesisFlagshipProgressInnovation identify with the given id exists in the system.
   * 
   * @param reportSynthesisFlagshipProgressInnovationID is a reportSynthesisFlagshipProgressInnovation identifier.
   * @return true if the reportSynthesisFlagshipProgressInnovation exists, false otherwise.
   */
  public boolean existReportSynthesisFlagshipProgressInnovation(long reportSynthesisFlagshipProgressInnovationID);

  /**
   * This method gets a reportSynthesisFlagshipProgressInnovation object by a given reportSynthesisFlagshipProgressInnovation identifier.
   * 
   * @param reportSynthesisFlagshipProgressInnovationID is the reportSynthesisFlagshipProgressInnovation identifier.
   * @return a ReportSynthesisFlagshipProgressInnovation object.
   */
  public ReportSynthesisFlagshipProgressInnovation find(long id);

  /**
   * This method gets a list of reportSynthesisFlagshipProgressInnovation that are active
   * 
   * @return a list from ReportSynthesisFlagshipProgressInnovation null if no exist records
   */
  public List<ReportSynthesisFlagshipProgressInnovation> findAll();


  /**
   * This method saves the information of the given reportSynthesisFlagshipProgressInnovation
   * 
   * @param reportSynthesisFlagshipProgressInnovation - is the reportSynthesisFlagshipProgressInnovation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisFlagshipProgressInnovation was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFlagshipProgressInnovation save(ReportSynthesisFlagshipProgressInnovation reportSynthesisFlagshipProgressInnovation);
}
