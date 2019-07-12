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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnership;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisKeyPartnershipManagerImpl implements ReportSynthesisKeyPartnershipManager {


  private ReportSynthesisKeyPartnershipDAO reportSynthesisKeyPartnershipDAO;
  // Managers


  @Inject
  public ReportSynthesisKeyPartnershipManagerImpl(ReportSynthesisKeyPartnershipDAO reportSynthesisKeyPartnershipDAO) {
    this.reportSynthesisKeyPartnershipDAO = reportSynthesisKeyPartnershipDAO;


  }

  @Override
  public void deleteReportSynthesisKeyPartnership(long reportSynthesisKeyPartnershipId) {

    reportSynthesisKeyPartnershipDAO.deleteReportSynthesisKeyPartnership(reportSynthesisKeyPartnershipId);
  }

  @Override
  public boolean existReportSynthesisKeyPartnership(long reportSynthesisKeyPartnershipID) {

    return reportSynthesisKeyPartnershipDAO.existReportSynthesisKeyPartnership(reportSynthesisKeyPartnershipID);
  }

  @Override
  public List<ReportSynthesisKeyPartnership> findAll() {

    return reportSynthesisKeyPartnershipDAO.findAll();

  }

  @Override
  public ReportSynthesisKeyPartnership getReportSynthesisKeyPartnershipById(long reportSynthesisKeyPartnershipID) {

    return reportSynthesisKeyPartnershipDAO.find(reportSynthesisKeyPartnershipID);
  }

  @Override
  public ReportSynthesisKeyPartnership saveReportSynthesisKeyPartnership(ReportSynthesisKeyPartnership reportSynthesisKeyPartnership) {

    return reportSynthesisKeyPartnershipDAO.save(reportSynthesisKeyPartnership);
  }


}
