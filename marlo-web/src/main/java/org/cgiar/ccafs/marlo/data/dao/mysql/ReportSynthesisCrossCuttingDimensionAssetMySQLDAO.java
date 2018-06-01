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


package org.cgiar.ccafs.marlo.data.dao.mysql;

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrossCuttingDimensionAssetDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimensionAsset;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisCrossCuttingDimensionAssetMySQLDAO extends AbstractMarloDAO<ReportSynthesisCrossCuttingDimensionAsset, Long> implements ReportSynthesisCrossCuttingDimensionAssetDAO {


  @Inject
  public ReportSynthesisCrossCuttingDimensionAssetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisCrossCuttingDimensionAsset(long reportSynthesisCrossCuttingDimensionAssetId) {
    ReportSynthesisCrossCuttingDimensionAsset reportSynthesisCrossCuttingDimensionAsset = this.find(reportSynthesisCrossCuttingDimensionAssetId);
    reportSynthesisCrossCuttingDimensionAsset.setActive(false);
    this.update(reportSynthesisCrossCuttingDimensionAsset);
  }

  @Override
  public boolean existReportSynthesisCrossCuttingDimensionAsset(long reportSynthesisCrossCuttingDimensionAssetID) {
    ReportSynthesisCrossCuttingDimensionAsset reportSynthesisCrossCuttingDimensionAsset = this.find(reportSynthesisCrossCuttingDimensionAssetID);
    if (reportSynthesisCrossCuttingDimensionAsset == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisCrossCuttingDimensionAsset find(long id) {
    return super.find(ReportSynthesisCrossCuttingDimensionAsset.class, id);

  }

  @Override
  public List<ReportSynthesisCrossCuttingDimensionAsset> findAll() {
    String query = "from " + ReportSynthesisCrossCuttingDimensionAsset.class.getName() + " where is_active=1";
    List<ReportSynthesisCrossCuttingDimensionAsset> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisCrossCuttingDimensionAsset save(ReportSynthesisCrossCuttingDimensionAsset reportSynthesisCrossCuttingDimensionAsset) {
    if (reportSynthesisCrossCuttingDimensionAsset.getId() == null) {
      super.saveEntity(reportSynthesisCrossCuttingDimensionAsset);
    } else {
      reportSynthesisCrossCuttingDimensionAsset = super.update(reportSynthesisCrossCuttingDimensionAsset);
    }


    return reportSynthesisCrossCuttingDimensionAsset;
  }


}