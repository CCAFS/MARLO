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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisMeliaDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMelia;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisMeliaMySQLDAO extends AbstractMarloDAO<ReportSynthesisMelia, Long> implements ReportSynthesisMeliaDAO {


  @Inject
  public ReportSynthesisMeliaMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisMelia(long reportSynthesisMeliaId) {
    ReportSynthesisMelia reportSynthesisMelia = this.find(reportSynthesisMeliaId);
    reportSynthesisMelia.setActive(false);
    this.update(reportSynthesisMelia);
  }

  @Override
  public boolean existReportSynthesisMelia(long reportSynthesisMeliaID) {
    ReportSynthesisMelia reportSynthesisMelia = this.find(reportSynthesisMeliaID);
    if (reportSynthesisMelia == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisMelia find(long id) {
    return super.find(ReportSynthesisMelia.class, id);

  }

  @Override
  public List<ReportSynthesisMelia> findAll() {
    String query = "from " + ReportSynthesisMelia.class.getName() + " where is_active=1";
    List<ReportSynthesisMelia> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisMelia save(ReportSynthesisMelia reportSynthesisMelia) {
    if (reportSynthesisMelia.getId() == null) {
      super.saveEntity(reportSynthesisMelia);
    } else {
      reportSynthesisMelia = super.update(reportSynthesisMelia);
    }


    return reportSynthesisMelia;
  }


}