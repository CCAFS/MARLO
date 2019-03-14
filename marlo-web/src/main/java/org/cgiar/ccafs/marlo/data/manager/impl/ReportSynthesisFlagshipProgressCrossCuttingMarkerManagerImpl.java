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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressCrossCuttingMarkerDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressCrossCuttingMarker;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFlagshipProgressCrossCuttingMarkerManagerImpl
  implements ReportSynthesisFlagshipProgressCrossCuttingMarkerManager {


  private ReportSynthesisFlagshipProgressCrossCuttingMarkerDAO reportSynthesisFlagshipProgressCrossCuttingMarkerDAO;
  // Managers


  @Inject
  public ReportSynthesisFlagshipProgressCrossCuttingMarkerManagerImpl(
    ReportSynthesisFlagshipProgressCrossCuttingMarkerDAO reportSynthesisFlagshipProgressCrossCuttingMarkerDAO) {
    this.reportSynthesisFlagshipProgressCrossCuttingMarkerDAO = reportSynthesisFlagshipProgressCrossCuttingMarkerDAO;


  }

  @Override
  public void
    deleteReportSynthesisFlagshipProgressCrossCuttingMarker(long reportSynthesisFlagshipProgressCrossCuttingMarkerId) {

    reportSynthesisFlagshipProgressCrossCuttingMarkerDAO
      .deleteReportSynthesisFlagshipProgressCrossCuttingMarker(reportSynthesisFlagshipProgressCrossCuttingMarkerId);
  }

  @Override
  public boolean
    existReportSynthesisFlagshipProgressCrossCuttingMarker(long reportSynthesisFlagshipProgressCrossCuttingMarkerID) {

    return reportSynthesisFlagshipProgressCrossCuttingMarkerDAO
      .existReportSynthesisFlagshipProgressCrossCuttingMarker(reportSynthesisFlagshipProgressCrossCuttingMarkerID);
  }

  @Override
  public List<ReportSynthesisFlagshipProgressCrossCuttingMarker> findAll() {

    return reportSynthesisFlagshipProgressCrossCuttingMarkerDAO.findAll();

  }

  @Override
  public ReportSynthesisFlagshipProgressCrossCuttingMarker getCountryMarkerId(long milestoneID,
    long cgiarCrossCuttingMarkerID, long phaseID) {
    return reportSynthesisFlagshipProgressCrossCuttingMarkerDAO.getCountryMarkerId(milestoneID,
      cgiarCrossCuttingMarkerID, phaseID);
  }

  @Override
  public ReportSynthesisFlagshipProgressCrossCuttingMarker
    getReportSynthesisFlagshipProgressCrossCuttingMarkerById(long reportSynthesisFlagshipProgressCrossCuttingMarkerID) {

    return reportSynthesisFlagshipProgressCrossCuttingMarkerDAO
      .find(reportSynthesisFlagshipProgressCrossCuttingMarkerID);
  }


  @Override
  public ReportSynthesisFlagshipProgressCrossCuttingMarker saveReportSynthesisFlagshipProgressCrossCuttingMarker(
    ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker) {

    return reportSynthesisFlagshipProgressCrossCuttingMarkerDAO.save(reportSynthesisFlagshipProgressCrossCuttingMarker);
  }


}
