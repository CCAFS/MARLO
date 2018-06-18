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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrossCgiarDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiar;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisCrossCgiarMySQLDAO extends AbstractMarloDAO<ReportSynthesisCrossCgiar, Long> implements ReportSynthesisCrossCgiarDAO {


  @Inject
  public ReportSynthesisCrossCgiarMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisCrossCgiar(long reportSynthesisCrossCgiarId) {
    ReportSynthesisCrossCgiar reportSynthesisCrossCgiar = this.find(reportSynthesisCrossCgiarId);
    reportSynthesisCrossCgiar.setActive(false);
    this.update(reportSynthesisCrossCgiar);
  }

  @Override
  public boolean existReportSynthesisCrossCgiar(long reportSynthesisCrossCgiarID) {
    ReportSynthesisCrossCgiar reportSynthesisCrossCgiar = this.find(reportSynthesisCrossCgiarID);
    if (reportSynthesisCrossCgiar == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisCrossCgiar find(long id) {
    return super.find(ReportSynthesisCrossCgiar.class, id);

  }

  @Override
  public List<ReportSynthesisCrossCgiar> findAll() {
    String query = "from " + ReportSynthesisCrossCgiar.class.getName() + " where is_active=1";
    List<ReportSynthesisCrossCgiar> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisCrossCgiar save(ReportSynthesisCrossCgiar reportSynthesisCrossCgiar) {
    if (reportSynthesisCrossCgiar.getId() == null) {
      super.saveEntity(reportSynthesisCrossCgiar);
    } else {
      reportSynthesisCrossCgiar = super.update(reportSynthesisCrossCgiar);
    }


    return reportSynthesisCrossCgiar;
  }


}