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
package org.cgiar.ccafs.marlo.data.manager.impl;


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipCollaborationCrpDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipCollaborationCrpManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaborationCrp;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisKeyPartnershipCollaborationCrpManagerImpl implements ReportSynthesisKeyPartnershipCollaborationCrpManager {


  private ReportSynthesisKeyPartnershipCollaborationCrpDAO reportSynthesisKeyPartnershipCollaborationCrpDAO;
  // Managers


  @Inject
  public ReportSynthesisKeyPartnershipCollaborationCrpManagerImpl(ReportSynthesisKeyPartnershipCollaborationCrpDAO reportSynthesisKeyPartnershipCollaborationCrpDAO) {
    this.reportSynthesisKeyPartnershipCollaborationCrpDAO = reportSynthesisKeyPartnershipCollaborationCrpDAO;


  }

  @Override
  public void deleteReportSynthesisKeyPartnershipCollaborationCrp(long reportSynthesisKeyPartnershipCollaborationCrpId) {

    reportSynthesisKeyPartnershipCollaborationCrpDAO.deleteReportSynthesisKeyPartnershipCollaborationCrp(reportSynthesisKeyPartnershipCollaborationCrpId);
  }

  @Override
  public boolean existReportSynthesisKeyPartnershipCollaborationCrp(long reportSynthesisKeyPartnershipCollaborationCrpID) {

    return reportSynthesisKeyPartnershipCollaborationCrpDAO.existReportSynthesisKeyPartnershipCollaborationCrp(reportSynthesisKeyPartnershipCollaborationCrpID);
  }

  @Override
  public List<ReportSynthesisKeyPartnershipCollaborationCrp> findAll() {

    return reportSynthesisKeyPartnershipCollaborationCrpDAO.findAll();

  }

  @Override
  public ReportSynthesisKeyPartnershipCollaborationCrp getReportSynthesisKeyPartnershipCollaborationCrpById(long reportSynthesisKeyPartnershipCollaborationCrpID) {

    return reportSynthesisKeyPartnershipCollaborationCrpDAO.find(reportSynthesisKeyPartnershipCollaborationCrpID);
  }

  @Override
  public ReportSynthesisKeyPartnershipCollaborationCrp saveReportSynthesisKeyPartnershipCollaborationCrp(ReportSynthesisKeyPartnershipCollaborationCrp reportSynthesisKeyPartnershipCollaborationCrp) {

    return reportSynthesisKeyPartnershipCollaborationCrpDAO.save(reportSynthesisKeyPartnershipCollaborationCrp);
  }


}
