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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimensionAsset;

import java.util.List;


/**
 * @author CCAFS
 */

public interface ReportSynthesisCrossCuttingDimensionAssetManager {


  /**
   * This method removes a specific reportSynthesisCrossCuttingDimensionAsset value from the database.
   * 
   * @param reportSynthesisCrossCuttingDimensionAssetId is the reportSynthesisCrossCuttingDimensionAsset identifier.
   * @return true if the reportSynthesisCrossCuttingDimensionAsset was successfully deleted, false otherwise.
   */
  public void deleteReportSynthesisCrossCuttingDimensionAsset(long reportSynthesisCrossCuttingDimensionAssetId);


  /**
   * This method validate if the reportSynthesisCrossCuttingDimensionAsset identify with the given id exists in the system.
   * 
   * @param reportSynthesisCrossCuttingDimensionAssetID is a reportSynthesisCrossCuttingDimensionAsset identifier.
   * @return true if the reportSynthesisCrossCuttingDimensionAsset exists, false otherwise.
   */
  public boolean existReportSynthesisCrossCuttingDimensionAsset(long reportSynthesisCrossCuttingDimensionAssetID);


  /**
   * This method gets a list of reportSynthesisCrossCuttingDimensionAsset that are active
   * 
   * @return a list from ReportSynthesisCrossCuttingDimensionAsset null if no exist records
   */
  public List<ReportSynthesisCrossCuttingDimensionAsset> findAll();


  /**
   * This method gets a reportSynthesisCrossCuttingDimensionAsset object by a given reportSynthesisCrossCuttingDimensionAsset identifier.
   * 
   * @param reportSynthesisCrossCuttingDimensionAssetID is the reportSynthesisCrossCuttingDimensionAsset identifier.
   * @return a ReportSynthesisCrossCuttingDimensionAsset object.
   */
  public ReportSynthesisCrossCuttingDimensionAsset getReportSynthesisCrossCuttingDimensionAssetById(long reportSynthesisCrossCuttingDimensionAssetID);

  /**
   * This method saves the information of the given reportSynthesisCrossCuttingDimensionAsset
   * 
   * @param reportSynthesisCrossCuttingDimensionAsset - is the reportSynthesisCrossCuttingDimensionAsset object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the reportSynthesisCrossCuttingDimensionAsset was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisCrossCuttingDimensionAsset saveReportSynthesisCrossCuttingDimensionAsset(ReportSynthesisCrossCuttingDimensionAsset reportSynthesisCrossCuttingDimensionAsset);


}
