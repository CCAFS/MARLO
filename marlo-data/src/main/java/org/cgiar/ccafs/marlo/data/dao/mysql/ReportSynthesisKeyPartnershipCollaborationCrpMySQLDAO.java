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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipCollaborationCrpDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaborationCrp;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisKeyPartnershipCollaborationCrpMySQLDAO
  extends AbstractMarloDAO<ReportSynthesisKeyPartnershipCollaborationCrp, Long>
  implements ReportSynthesisKeyPartnershipCollaborationCrpDAO {


  @Inject
  public ReportSynthesisKeyPartnershipCollaborationCrpMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void
    deleteReportSynthesisKeyPartnershipCollaborationCrp(long reportSynthesisKeyPartnershipCollaborationCrpId) {
    ReportSynthesisKeyPartnershipCollaborationCrp reportSynthesisKeyPartnershipCollaborationCrp =
      this.find(reportSynthesisKeyPartnershipCollaborationCrpId);

    this.delete(reportSynthesisKeyPartnershipCollaborationCrp);
  }

  @Override
  public boolean
    existReportSynthesisKeyPartnershipCollaborationCrp(long reportSynthesisKeyPartnershipCollaborationCrpID) {
    ReportSynthesisKeyPartnershipCollaborationCrp reportSynthesisKeyPartnershipCollaborationCrp =
      this.find(reportSynthesisKeyPartnershipCollaborationCrpID);
    if (reportSynthesisKeyPartnershipCollaborationCrp == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisKeyPartnershipCollaborationCrp find(long id) {
    return super.find(ReportSynthesisKeyPartnershipCollaborationCrp.class, id);

  }

  @Override
  public List<ReportSynthesisKeyPartnershipCollaborationCrp> findAll() {
    String query = "from " + ReportSynthesisKeyPartnershipCollaborationCrp.class.getName();
    List<ReportSynthesisKeyPartnershipCollaborationCrp> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisKeyPartnershipCollaborationCrp findByCollaborationIdAndGlobalUnitId(long collaborationId,
    long globalUnitId) {
    String query = "from " + ReportSynthesisKeyPartnershipCollaborationCrp.class.getName()
      + " where report_synthesis_key_partnership_collaboration_id = " + collaborationId + " and global_unit_id = "
      + globalUnitId;
    List<ReportSynthesisKeyPartnershipCollaborationCrp> list = super.findAll(query);
    if (list.size() > 0) {
      return list.get(0);
    }
    return null;
  }

  @Override
  public ReportSynthesisKeyPartnershipCollaborationCrp
    save(ReportSynthesisKeyPartnershipCollaborationCrp reportSynthesisKeyPartnershipCollaborationCrp) {
    if (reportSynthesisKeyPartnershipCollaborationCrp.getId() == null) {
      super.saveEntity(reportSynthesisKeyPartnershipCollaborationCrp);
    } else {
      reportSynthesisKeyPartnershipCollaborationCrp = super.update(reportSynthesisKeyPartnershipCollaborationCrp);
    }


    return reportSynthesisKeyPartnershipCollaborationCrp;
  }


}