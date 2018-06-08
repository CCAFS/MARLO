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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFundingUseExpendituryAreaDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseExpendituryArea;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisFundingUseExpendituryAreaMySQLDAO extends AbstractMarloDAO<ReportSynthesisFundingUseExpendituryArea, Long> implements ReportSynthesisFundingUseExpendituryAreaDAO {


  @Inject
  public ReportSynthesisFundingUseExpendituryAreaMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisFundingUseExpendituryArea(long reportSynthesisFundingUseExpendituryAreaId) {
    ReportSynthesisFundingUseExpendituryArea reportSynthesisFundingUseExpendituryArea = this.find(reportSynthesisFundingUseExpendituryAreaId);
    reportSynthesisFundingUseExpendituryArea.setActive(false);
    this.update(reportSynthesisFundingUseExpendituryArea);
  }

  @Override
  public boolean existReportSynthesisFundingUseExpendituryArea(long reportSynthesisFundingUseExpendituryAreaID) {
    ReportSynthesisFundingUseExpendituryArea reportSynthesisFundingUseExpendituryArea = this.find(reportSynthesisFundingUseExpendituryAreaID);
    if (reportSynthesisFundingUseExpendituryArea == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisFundingUseExpendituryArea find(long id) {
    return super.find(ReportSynthesisFundingUseExpendituryArea.class, id);

  }

  @Override
  public List<ReportSynthesisFundingUseExpendituryArea> findAll() {
    String query = "from " + ReportSynthesisFundingUseExpendituryArea.class.getName() + " where is_active=1";
    List<ReportSynthesisFundingUseExpendituryArea> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisFundingUseExpendituryArea save(ReportSynthesisFundingUseExpendituryArea reportSynthesisFundingUseExpendituryArea) {
    if (reportSynthesisFundingUseExpendituryArea.getId() == null) {
      super.saveEntity(reportSynthesisFundingUseExpendituryArea);
    } else {
      reportSynthesisFundingUseExpendituryArea = super.update(reportSynthesisFundingUseExpendituryArea);
    }


    return reportSynthesisFundingUseExpendituryArea;
  }


}