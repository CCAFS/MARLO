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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisIntellectualAssetDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisIntellectualAssetManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIntellectualAsset;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisIntellectualAssetManagerImpl implements ReportSynthesisIntellectualAssetManager {


  private ReportSynthesisIntellectualAssetDAO reportSynthesisIntellectualAssetDAO;
  // Managers


  @Inject
  public ReportSynthesisIntellectualAssetManagerImpl(ReportSynthesisIntellectualAssetDAO reportSynthesisIntellectualAssetDAO) {
    this.reportSynthesisIntellectualAssetDAO = reportSynthesisIntellectualAssetDAO;


  }

  @Override
  public void deleteReportSynthesisIntellectualAsset(long reportSynthesisIntellectualAssetId) {

    reportSynthesisIntellectualAssetDAO.deleteReportSynthesisIntellectualAsset(reportSynthesisIntellectualAssetId);
  }

  @Override
  public boolean existReportSynthesisIntellectualAsset(long reportSynthesisIntellectualAssetID) {

    return reportSynthesisIntellectualAssetDAO.existReportSynthesisIntellectualAsset(reportSynthesisIntellectualAssetID);
  }

  @Override
  public List<ReportSynthesisIntellectualAsset> findAll() {

    return reportSynthesisIntellectualAssetDAO.findAll();

  }

  @Override
  public ReportSynthesisIntellectualAsset getReportSynthesisIntellectualAssetById(long reportSynthesisIntellectualAssetID) {

    return reportSynthesisIntellectualAssetDAO.find(reportSynthesisIntellectualAssetID);
  }

  @Override
  public ReportSynthesisIntellectualAsset saveReportSynthesisIntellectualAsset(ReportSynthesisIntellectualAsset reportSynthesisIntellectualAsset) {

    return reportSynthesisIntellectualAssetDAO.save(reportSynthesisIntellectualAsset);
  }


}
