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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrossCuttingDimensionDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisCrossCuttingDimensionMySQLDAO extends AbstractMarloDAO<ReportSynthesisCrossCuttingDimension, Long> implements ReportSynthesisCrossCuttingDimensionDAO {


  @Inject
  public ReportSynthesisCrossCuttingDimensionMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisCrossCuttingDimension(long reportSynthesisCrossCuttingDimensionId) {
    ReportSynthesisCrossCuttingDimension reportSynthesisCrossCuttingDimension = this.find(reportSynthesisCrossCuttingDimensionId);
    reportSynthesisCrossCuttingDimension.setActive(false);
    this.update(reportSynthesisCrossCuttingDimension);
  }

  @Override
  public boolean existReportSynthesisCrossCuttingDimension(long reportSynthesisCrossCuttingDimensionID) {
    ReportSynthesisCrossCuttingDimension reportSynthesisCrossCuttingDimension = this.find(reportSynthesisCrossCuttingDimensionID);
    if (reportSynthesisCrossCuttingDimension == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisCrossCuttingDimension find(long id) {
    return super.find(ReportSynthesisCrossCuttingDimension.class, id);

  }

  @Override
  public List<ReportSynthesisCrossCuttingDimension> findAll() {
    String query = "from " + ReportSynthesisCrossCuttingDimension.class.getName() + " where is_active=1";
    List<ReportSynthesisCrossCuttingDimension> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisCrossCuttingDimension save(ReportSynthesisCrossCuttingDimension reportSynthesisCrossCuttingDimension) {
    if (reportSynthesisCrossCuttingDimension.getId() == null) {
      super.saveEntity(reportSynthesisCrossCuttingDimension);
    } else {
      reportSynthesisCrossCuttingDimension = super.update(reportSynthesisCrossCuttingDimension);
    }


    return reportSynthesisCrossCuttingDimension;
  }


}