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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimensionInnovation;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisCrossCuttingDimensionInnovationManager {


  /**
   * This method removes a specific reportSynthesisCrossCuttingDimensionInnovation value from the database.
   * 
   * @param reportSynthesisCrossCuttingDimensionInnovationId is the reportSynthesisCrossCuttingDimensionInnovation identifier.
   * @return true if the reportSynthesisCrossCuttingDimensionInnovation was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisCrossCuttingDimensionInnovation(long reportSynthesisCrossCuttingDimensionInnovationId);


  /**
   * This method validate if the reportSynthesisCrossCuttingDimensionInnovation identify with the given id exists in the system.
   * 
   * @param reportSynthesisCrossCuttingDimensionInnovationID is a reportSynthesisCrossCuttingDimensionInnovation identifier.
   * @return true if the reportSynthesisCrossCuttingDimensionInnovation exists, false otherwise.
   */
  public boolean existReportSynthesisCrossCuttingDimensionInnovation(long reportSynthesisCrossCuttingDimensionInnovationID);


  /**
   * This method gets a list of reportSynthesisCrossCuttingDimensionInnovation that are active
   * 
   * @return a list from ReportSynthesisCrossCuttingDimensionInnovation null if no exist records
   */
  public List<ReportSynthesisCrossCuttingDimensionInnovation> findAll();


  /**
   * This method gets a reportSynthesisCrossCuttingDimensionInnovation object by a given reportSynthesisCrossCuttingDimensionInnovation identifier.
   * 
   * @param reportSynthesisCrossCuttingDimensionInnovationID is the reportSynthesisCrossCuttingDimensionInnovation identifier.
   * @return a ReportSynthesisCrossCuttingDimensionInnovation object.
   */
  public ReportSynthesisCrossCuttingDimensionInnovation getReportSynthesisCrossCuttingDimensionInnovationById(long reportSynthesisCrossCuttingDimensionInnovationID);

  /**
   * This method saves the information of the given reportSynthesisCrossCuttingDimensionInnovation
   * 
   * @param reportSynthesisCrossCuttingDimensionInnovation - is the reportSynthesisCrossCuttingDimensionInnovation object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisCrossCuttingDimensionInnovation was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisCrossCuttingDimensionInnovation saveReportSynthesisCrossCuttingDimensionInnovation(ReportSynthesisCrossCuttingDimensionInnovation reportSynthesisCrossCuttingDimensionInnovation);


}
