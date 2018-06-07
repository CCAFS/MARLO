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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;

import java.util.List;


public interface ReportSynthesisCrossCuttingDimensionDAO {

  /**
   * This method removes a specific reportSynthesisCrossCuttingDimension value from the database.
   * 
   * @param reportSynthesisCrossCuttingDimensionId is the reportSynthesisCrossCuttingDimension identifier.
   * @return true if the reportSynthesisCrossCuttingDimension was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisCrossCuttingDimension(long reportSynthesisCrossCuttingDimensionId);

  /**
   * This method validate if the reportSynthesisCrossCuttingDimension identify with the given id exists in the system.
   * 
   * @param reportSynthesisCrossCuttingDimensionID is a reportSynthesisCrossCuttingDimension identifier.
   * @return true if the reportSynthesisCrossCuttingDimension exists, false otherwise.
   */
  public boolean existReportSynthesisCrossCuttingDimension(long reportSynthesisCrossCuttingDimensionID);

  /**
   * This method gets a reportSynthesisCrossCuttingDimension object by a given reportSynthesisCrossCuttingDimension identifier.
   * 
   * @param reportSynthesisCrossCuttingDimensionID is the reportSynthesisCrossCuttingDimension identifier.
   * @return a ReportSynthesisCrossCuttingDimension object.
   */
  public ReportSynthesisCrossCuttingDimension find(long id);

  /**
   * This method gets a list of reportSynthesisCrossCuttingDimension that are active
   * 
   * @return a list from ReportSynthesisCrossCuttingDimension null if no exist records
   */
  public List<ReportSynthesisCrossCuttingDimension> findAll();


  /**
   * This method saves the information of the given reportSynthesisCrossCuttingDimension
   * 
   * @param reportSynthesisCrossCuttingDimension - is the reportSynthesisCrossCuttingDimension object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisCrossCuttingDimension was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisCrossCuttingDimension save(ReportSynthesisCrossCuttingDimension reportSynthesisCrossCuttingDimension);
}
