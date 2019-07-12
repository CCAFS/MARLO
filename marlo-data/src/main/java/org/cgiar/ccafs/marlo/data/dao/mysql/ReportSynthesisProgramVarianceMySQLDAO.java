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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisProgramVarianceDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisProgramVariance;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisProgramVarianceMySQLDAO extends AbstractMarloDAO<ReportSynthesisProgramVariance, Long> implements ReportSynthesisProgramVarianceDAO {


  @Inject
  public ReportSynthesisProgramVarianceMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisProgramVariance(long reportSynthesisProgramVarianceId) {
    ReportSynthesisProgramVariance reportSynthesisProgramVariance = this.find(reportSynthesisProgramVarianceId);
    reportSynthesisProgramVariance.setActive(false);
    this.update(reportSynthesisProgramVariance);
  }

  @Override
  public boolean existReportSynthesisProgramVariance(long reportSynthesisProgramVarianceID) {
    ReportSynthesisProgramVariance reportSynthesisProgramVariance = this.find(reportSynthesisProgramVarianceID);
    if (reportSynthesisProgramVariance == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisProgramVariance find(long id) {
    return super.find(ReportSynthesisProgramVariance.class, id);

  }

  @Override
  public List<ReportSynthesisProgramVariance> findAll() {
    String query = "from " + ReportSynthesisProgramVariance.class.getName() + " where is_active=1";
    List<ReportSynthesisProgramVariance> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisProgramVariance save(ReportSynthesisProgramVariance reportSynthesisProgramVariance) {
    if (reportSynthesisProgramVariance.getId() == null) {
      super.saveEntity(reportSynthesisProgramVariance);
    } else {
      reportSynthesisProgramVariance = super.update(reportSynthesisProgramVariance);
    }


    return reportSynthesisProgramVariance;
  }


}