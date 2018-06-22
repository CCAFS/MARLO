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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrossCuttingDimensionAssetDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCuttingDimensionAssetManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimensionAsset;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisCrossCuttingDimensionAssetManagerImpl implements ReportSynthesisCrossCuttingDimensionAssetManager {


  private ReportSynthesisCrossCuttingDimensionAssetDAO reportSynthesisCrossCuttingDimensionAssetDAO;
  // Managers


  @Inject
  public ReportSynthesisCrossCuttingDimensionAssetManagerImpl(ReportSynthesisCrossCuttingDimensionAssetDAO reportSynthesisCrossCuttingDimensionAssetDAO) {
    this.reportSynthesisCrossCuttingDimensionAssetDAO = reportSynthesisCrossCuttingDimensionAssetDAO;


  }

  @Override
  public void deleteReportSynthesisCrossCuttingDimensionAsset(long reportSynthesisCrossCuttingDimensionAssetId) {

    reportSynthesisCrossCuttingDimensionAssetDAO.deleteReportSynthesisCrossCuttingDimensionAsset(reportSynthesisCrossCuttingDimensionAssetId);
  }

  @Override
  public boolean existReportSynthesisCrossCuttingDimensionAsset(long reportSynthesisCrossCuttingDimensionAssetID) {

    return reportSynthesisCrossCuttingDimensionAssetDAO.existReportSynthesisCrossCuttingDimensionAsset(reportSynthesisCrossCuttingDimensionAssetID);
  }

  @Override
  public List<ReportSynthesisCrossCuttingDimensionAsset> findAll() {

    return reportSynthesisCrossCuttingDimensionAssetDAO.findAll();

  }

  @Override
  public ReportSynthesisCrossCuttingDimensionAsset getReportSynthesisCrossCuttingDimensionAssetById(long reportSynthesisCrossCuttingDimensionAssetID) {

    return reportSynthesisCrossCuttingDimensionAssetDAO.find(reportSynthesisCrossCuttingDimensionAssetID);
  }

  @Override
  public ReportSynthesisCrossCuttingDimensionAsset saveReportSynthesisCrossCuttingDimensionAsset(ReportSynthesisCrossCuttingDimensionAsset reportSynthesisCrossCuttingDimensionAsset) {

    return reportSynthesisCrossCuttingDimensionAssetDAO.save(reportSynthesisCrossCuttingDimensionAsset);
  }


}
