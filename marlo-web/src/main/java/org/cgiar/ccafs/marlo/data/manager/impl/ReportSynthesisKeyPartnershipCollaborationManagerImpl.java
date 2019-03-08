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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipCollaborationDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipCollaborationManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaboration;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisKeyPartnershipCollaborationManagerImpl implements ReportSynthesisKeyPartnershipCollaborationManager {


  private ReportSynthesisKeyPartnershipCollaborationDAO reportSynthesisKeyPartnershipCollaborationDAO;
  // Managers


  @Inject
  public ReportSynthesisKeyPartnershipCollaborationManagerImpl(ReportSynthesisKeyPartnershipCollaborationDAO reportSynthesisKeyPartnershipCollaborationDAO) {
    this.reportSynthesisKeyPartnershipCollaborationDAO = reportSynthesisKeyPartnershipCollaborationDAO;


  }

  @Override
  public void deleteReportSynthesisKeyPartnershipCollaboration(long reportSynthesisKeyPartnershipCollaborationId) {

    reportSynthesisKeyPartnershipCollaborationDAO.deleteReportSynthesisKeyPartnershipCollaboration(reportSynthesisKeyPartnershipCollaborationId);
  }

  @Override
  public boolean existReportSynthesisKeyPartnershipCollaboration(long reportSynthesisKeyPartnershipCollaborationID) {

    return reportSynthesisKeyPartnershipCollaborationDAO.existReportSynthesisKeyPartnershipCollaboration(reportSynthesisKeyPartnershipCollaborationID);
  }

  @Override
  public List<ReportSynthesisKeyPartnershipCollaboration> findAll() {

    return reportSynthesisKeyPartnershipCollaborationDAO.findAll();

  }

  @Override
  public ReportSynthesisKeyPartnershipCollaboration getReportSynthesisKeyPartnershipCollaborationById(long reportSynthesisKeyPartnershipCollaborationID) {

    return reportSynthesisKeyPartnershipCollaborationDAO.find(reportSynthesisKeyPartnershipCollaborationID);
  }

  @Override
  public ReportSynthesisKeyPartnershipCollaboration saveReportSynthesisKeyPartnershipCollaboration(ReportSynthesisKeyPartnershipCollaboration reportSynthesisKeyPartnershipCollaboration) {

    return reportSynthesisKeyPartnershipCollaborationDAO.save(reportSynthesisKeyPartnershipCollaboration);
  }


}
