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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipExternalMainAreaDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalMainArea;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisKeyPartnershipExternalMainAreaMySQLDAO
  extends AbstractMarloDAO<ReportSynthesisKeyPartnershipExternalMainArea, Long>
  implements ReportSynthesisKeyPartnershipExternalMainAreaDAO {


  @Inject
  public ReportSynthesisKeyPartnershipExternalMainAreaMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void
    deleteReportSynthesisKeyPartnershipExternalMainArea(long reportSynthesisKeyPartnershipExternalMainAreaId) {
    ReportSynthesisKeyPartnershipExternalMainArea reportSynthesisKeyPartnershipExternalMainArea =
      this.find(reportSynthesisKeyPartnershipExternalMainAreaId);
    this.delete(reportSynthesisKeyPartnershipExternalMainArea);
  }

  @Override
  public boolean
    existReportSynthesisKeyPartnershipExternalMainArea(long reportSynthesisKeyPartnershipExternalMainAreaID) {
    ReportSynthesisKeyPartnershipExternalMainArea reportSynthesisKeyPartnershipExternalMainArea =
      this.find(reportSynthesisKeyPartnershipExternalMainAreaID);
    if (reportSynthesisKeyPartnershipExternalMainArea == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisKeyPartnershipExternalMainArea find(long id) {
    return super.find(ReportSynthesisKeyPartnershipExternalMainArea.class, id);

  }

  @Override
  public List<ReportSynthesisKeyPartnershipExternalMainArea> findAll() {
    String query = "from " + ReportSynthesisKeyPartnershipExternalMainArea.class.getName();
    List<ReportSynthesisKeyPartnershipExternalMainArea> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisKeyPartnershipExternalMainArea
    save(ReportSynthesisKeyPartnershipExternalMainArea reportSynthesisKeyPartnershipExternalMainArea) {
    if (reportSynthesisKeyPartnershipExternalMainArea.getId() == null) {
      super.saveEntity(reportSynthesisKeyPartnershipExternalMainArea);
    } else {
      reportSynthesisKeyPartnershipExternalMainArea = super.update(reportSynthesisKeyPartnershipExternalMainArea);
    }


    return reportSynthesisKeyPartnershipExternalMainArea;
  }


}