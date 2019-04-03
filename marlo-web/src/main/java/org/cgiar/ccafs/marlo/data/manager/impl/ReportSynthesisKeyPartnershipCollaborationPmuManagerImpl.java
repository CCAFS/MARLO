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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipCollaborationPmuDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipCollaborationPmuManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaborationPmu;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisKeyPartnershipCollaborationPmuManagerImpl implements ReportSynthesisKeyPartnershipCollaborationPmuManager {


  private ReportSynthesisKeyPartnershipCollaborationPmuDAO reportSynthesisKeyPartnershipCollaborationPmuDAO;
  // Managers


  @Inject
  public ReportSynthesisKeyPartnershipCollaborationPmuManagerImpl(ReportSynthesisKeyPartnershipCollaborationPmuDAO reportSynthesisKeyPartnershipCollaborationPmuDAO) {
    this.reportSynthesisKeyPartnershipCollaborationPmuDAO = reportSynthesisKeyPartnershipCollaborationPmuDAO;


  }

  @Override
  public void deleteReportSynthesisKeyPartnershipCollaborationPmu(long reportSynthesisKeyPartnershipCollaborationPmuId) {

    reportSynthesisKeyPartnershipCollaborationPmuDAO.deleteReportSynthesisKeyPartnershipCollaborationPmu(reportSynthesisKeyPartnershipCollaborationPmuId);
  }

  @Override
  public boolean existReportSynthesisKeyPartnershipCollaborationPmu(long reportSynthesisKeyPartnershipCollaborationPmuID) {

    return reportSynthesisKeyPartnershipCollaborationPmuDAO.existReportSynthesisKeyPartnershipCollaborationPmu(reportSynthesisKeyPartnershipCollaborationPmuID);
  }

  @Override
  public List<ReportSynthesisKeyPartnershipCollaborationPmu> findAll() {

    return reportSynthesisKeyPartnershipCollaborationPmuDAO.findAll();

  }

  @Override
  public ReportSynthesisKeyPartnershipCollaborationPmu getReportSynthesisKeyPartnershipCollaborationPmuById(long reportSynthesisKeyPartnershipCollaborationPmuID) {

    return reportSynthesisKeyPartnershipCollaborationPmuDAO.find(reportSynthesisKeyPartnershipCollaborationPmuID);
  }

  @Override
  public ReportSynthesisKeyPartnershipCollaborationPmu saveReportSynthesisKeyPartnershipCollaborationPmu(ReportSynthesisKeyPartnershipCollaborationPmu reportSynthesisKeyPartnershipCollaborationPmu) {

    return reportSynthesisKeyPartnershipCollaborationPmuDAO.save(reportSynthesisKeyPartnershipCollaborationPmu);
  }


}
