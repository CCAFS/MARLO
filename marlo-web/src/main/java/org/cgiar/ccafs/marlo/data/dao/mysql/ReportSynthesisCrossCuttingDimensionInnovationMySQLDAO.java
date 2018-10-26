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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrossCuttingDimensionInnovationDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimensionInnovation;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisCrossCuttingDimensionInnovationMySQLDAO extends AbstractMarloDAO<ReportSynthesisCrossCuttingDimensionInnovation, Long> implements ReportSynthesisCrossCuttingDimensionInnovationDAO {


  @Inject
  public ReportSynthesisCrossCuttingDimensionInnovationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisCrossCuttingDimensionInnovation(long reportSynthesisCrossCuttingDimensionInnovationId) {
    ReportSynthesisCrossCuttingDimensionInnovation reportSynthesisCrossCuttingDimensionInnovation = this.find(reportSynthesisCrossCuttingDimensionInnovationId);
    reportSynthesisCrossCuttingDimensionInnovation.setActive(false);
    this.update(reportSynthesisCrossCuttingDimensionInnovation);
  }

  @Override
  public boolean existReportSynthesisCrossCuttingDimensionInnovation(long reportSynthesisCrossCuttingDimensionInnovationID) {
    ReportSynthesisCrossCuttingDimensionInnovation reportSynthesisCrossCuttingDimensionInnovation = this.find(reportSynthesisCrossCuttingDimensionInnovationID);
    if (reportSynthesisCrossCuttingDimensionInnovation == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisCrossCuttingDimensionInnovation find(long id) {
    return super.find(ReportSynthesisCrossCuttingDimensionInnovation.class, id);

  }

  @Override
  public List<ReportSynthesisCrossCuttingDimensionInnovation> findAll() {
    String query = "from " + ReportSynthesisCrossCuttingDimensionInnovation.class.getName() + " where is_active=1";
    List<ReportSynthesisCrossCuttingDimensionInnovation> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisCrossCuttingDimensionInnovation save(ReportSynthesisCrossCuttingDimensionInnovation reportSynthesisCrossCuttingDimensionInnovation) {
    if (reportSynthesisCrossCuttingDimensionInnovation.getId() == null) {
      super.saveEntity(reportSynthesisCrossCuttingDimensionInnovation);
    } else {
      reportSynthesisCrossCuttingDimensionInnovation = super.update(reportSynthesisCrossCuttingDimensionInnovation);
    }


    return reportSynthesisCrossCuttingDimensionInnovation;
  }


}