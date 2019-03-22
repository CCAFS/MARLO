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

import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipCollaborationDAO;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaboration;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.hibernate.SessionFactory;

@Named
public class ReportSynthesisKeyPartnershipCollaborationMySQLDAO
  extends AbstractMarloDAO<ReportSynthesisKeyPartnershipCollaboration, Long>
  implements ReportSynthesisKeyPartnershipCollaborationDAO {


  @Inject
  public ReportSynthesisKeyPartnershipCollaborationMySQLDAO(SessionFactory sessionFactory) {
    super(sessionFactory);
  }

  @Override
  public void deleteReportSynthesisKeyPartnershipCollaboration(long reportSynthesisKeyPartnershipCollaborationId) {
    ReportSynthesisKeyPartnershipCollaboration reportSynthesisKeyPartnershipCollaboration =
      this.find(reportSynthesisKeyPartnershipCollaborationId);
    this.delete(reportSynthesisKeyPartnershipCollaboration);
  }

  @Override
  public boolean existReportSynthesisKeyPartnershipCollaboration(long reportSynthesisKeyPartnershipCollaborationID) {
    ReportSynthesisKeyPartnershipCollaboration reportSynthesisKeyPartnershipCollaboration =
      this.find(reportSynthesisKeyPartnershipCollaborationID);
    if (reportSynthesisKeyPartnershipCollaboration == null) {
      return false;
    }
    return true;

  }

  @Override
  public ReportSynthesisKeyPartnershipCollaboration find(long id) {
    return super.find(ReportSynthesisKeyPartnershipCollaboration.class, id);

  }

  @Override
  public List<ReportSynthesisKeyPartnershipCollaboration> findAll() {
    String query = "from " + ReportSynthesisKeyPartnershipCollaboration.class.getName();
    List<ReportSynthesisKeyPartnershipCollaboration> list = super.findAll(query);
    if (list.size() > 0) {
      return list;
    }
    return null;

  }

  @Override
  public ReportSynthesisKeyPartnershipCollaboration
    save(ReportSynthesisKeyPartnershipCollaboration reportSynthesisKeyPartnershipCollaboration) {
    if (reportSynthesisKeyPartnershipCollaboration.getId() == null) {
      super.saveEntity(reportSynthesisKeyPartnershipCollaboration);
    } else {
      reportSynthesisKeyPartnershipCollaboration = super.update(reportSynthesisKeyPartnershipCollaboration);
    }


    return reportSynthesisKeyPartnershipCollaboration;
  }


}