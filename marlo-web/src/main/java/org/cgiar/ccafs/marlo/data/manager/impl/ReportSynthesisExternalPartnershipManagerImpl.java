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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisExternalPartnershipDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisExternalPartnershipManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnership;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisExternalPartnershipManagerImpl implements ReportSynthesisExternalPartnershipManager {


  private ReportSynthesisExternalPartnershipDAO reportSynthesisExternalPartnershipDAO;
  // Managers


  @Inject
  public ReportSynthesisExternalPartnershipManagerImpl(ReportSynthesisExternalPartnershipDAO reportSynthesisExternalPartnershipDAO) {
    this.reportSynthesisExternalPartnershipDAO = reportSynthesisExternalPartnershipDAO;


  }

  @Override
  public void deleteReportSynthesisExternalPartnership(long reportSynthesisExternalPartnershipId) {

    reportSynthesisExternalPartnershipDAO.deleteReportSynthesisExternalPartnership(reportSynthesisExternalPartnershipId);
  }

  @Override
  public boolean existReportSynthesisExternalPartnership(long reportSynthesisExternalPartnershipID) {

    return reportSynthesisExternalPartnershipDAO.existReportSynthesisExternalPartnership(reportSynthesisExternalPartnershipID);
  }

  @Override
  public List<ReportSynthesisExternalPartnership> findAll() {

    return reportSynthesisExternalPartnershipDAO.findAll();

  }

  @Override
  public ReportSynthesisExternalPartnership getReportSynthesisExternalPartnershipById(long reportSynthesisExternalPartnershipID) {

    return reportSynthesisExternalPartnershipDAO.find(reportSynthesisExternalPartnershipID);
  }

  @Override
  public ReportSynthesisExternalPartnership saveReportSynthesisExternalPartnership(ReportSynthesisExternalPartnership reportSynthesisExternalPartnership) {

    return reportSynthesisExternalPartnershipDAO.save(reportSynthesisExternalPartnership);
  }


}
