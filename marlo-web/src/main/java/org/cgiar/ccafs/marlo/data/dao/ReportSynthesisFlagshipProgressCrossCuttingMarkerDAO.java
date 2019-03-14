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

import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressCrossCuttingMarker;

import java.util.List;


public interface ReportSynthesisFlagshipProgressCrossCuttingMarkerDAO {

  /**
   * This method removes a specific reportSynthesisFlagshipProgressCrossCuttingMarker value from the database.
   * 
   * @param reportSynthesisFlagshipProgressCrossCuttingMarkerId is the reportSynthesisFlagshipProgressCrossCuttingMarker
   *        identifier.
   * @return true if the reportSynthesisFlagshipProgressCrossCuttingMarker was successfully deleted, false otherwise.
   */
  public void
    deleteReportSynthesisFlagshipProgressCrossCuttingMarker(long reportSynthesisFlagshipProgressCrossCuttingMarkerId);

  /**
   * This method validate if the reportSynthesisFlagshipProgressCrossCuttingMarker identify with the given id exists in
   * the system.
   * 
   * @param reportSynthesisFlagshipProgressCrossCuttingMarkerID is a reportSynthesisFlagshipProgressCrossCuttingMarker
   *        identifier.
   * @return true if the reportSynthesisFlagshipProgressCrossCuttingMarker exists, false otherwise.
   */
  public boolean
    existReportSynthesisFlagshipProgressCrossCuttingMarker(long reportSynthesisFlagshipProgressCrossCuttingMarkerID);

  /**
   * This method gets a reportSynthesisFlagshipProgressCrossCuttingMarker object by a given
   * reportSynthesisFlagshipProgressCrossCuttingMarker identifier.
   * 
   * @param reportSynthesisFlagshipProgressCrossCuttingMarkerID is the reportSynthesisFlagshipProgressCrossCuttingMarker
   *        identifier.
   * @return a ReportSynthesisFlagshipProgressCrossCuttingMarker object.
   */
  public ReportSynthesisFlagshipProgressCrossCuttingMarker find(long id);

  /**
   * This method gets a list of reportSynthesisFlagshipProgressCrossCuttingMarker that are active
   * 
   * @return a list from ReportSynthesisFlagshipProgressCrossCuttingMarker null if no exist records
   */
  public List<ReportSynthesisFlagshipProgressCrossCuttingMarker> findAll();


  public ReportSynthesisFlagshipProgressCrossCuttingMarker getCountryMarkerId(long milestoneID,
    long cgiarCrossCuttingMarkerID, long phaseID);


  /**
   * This method saves the information of the given reportSynthesisFlagshipProgressCrossCuttingMarker
   * 
   * @param reportSynthesisFlagshipProgressCrossCuttingMarker - is the reportSynthesisFlagshipProgressCrossCuttingMarker
   *        object with the new information to be added/updated.
   * @return a number greater than 0 representing the new ID assigned by the database, 0 if the
   *         reportSynthesisFlagshipProgressCrossCuttingMarker was
   *         updated
   *         or -1 is some error occurred.
   */
  public ReportSynthesisFlagshipProgressCrossCuttingMarker
    save(ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker);
}
