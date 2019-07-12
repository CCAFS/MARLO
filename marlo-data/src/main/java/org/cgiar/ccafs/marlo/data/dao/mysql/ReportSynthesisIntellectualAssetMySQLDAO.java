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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisIntellectualAssetDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIntellectualAsset;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisIntellectualAssetMySQLDAO extends AbstractMarloDAO<ReportSynthesisIntellectualAsset, Long> implements ReportSynthesisIntellectualAssetDAO {


  @Inject
  public ReportSynthesisIntellectualAssetMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisIntellectualAsset(long reportSynthesisIntellectualAssetId) {
    ReportSynthesisIntellectualAsset reportSynthesisIntellectualAsset = this.find(reportSynthesisIntellectualAssetId);
    reportSynthesisIntellectualAsset.setActive(false);
    this.update(reportSynthesisIntellectualAsset);
  }

  @Override
  public boolean existReportSynthesisIntellectualAsset(long reportSynthesisIntellectualAssetID) {
    ReportSynthesisIntellectualAsset reportSynthesisIntellectualAsset = this.find(reportSynthesisIntellectualAssetID);
    if (reportSynthesisIntellectualAsset == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisIntellectualAsset find(long id) {
    return super.find(ReportSynthesisIntellectualAsset.class, id);

  }

  @Override
  public List<ReportSynthesisIntellectualAsset> findAll() {
    String query = "from " + ReportSynthesisIntellectualAsset.class.getName() + " where is_active=1";
    List<ReportSynthesisIntellectualAsset> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisIntellectualAsset save(ReportSynthesisIntellectualAsset reportSynthesisIntellectualAsset) {
    if (reportSynthesisIntellectualAsset.getId() == null) {
      super.saveEntity(reportSynthesisIntellectualAsset);
    } else {
      reportSynthesisIntellectualAsset = super.update(reportSynthesisIntellectualAsset);
    }


    return reportSynthesisIntellectualAsset;
  }


}