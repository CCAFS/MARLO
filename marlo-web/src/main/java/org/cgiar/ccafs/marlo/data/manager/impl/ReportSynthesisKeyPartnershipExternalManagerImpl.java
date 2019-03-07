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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipExternalDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisKeyPartnershipExternalManagerImpl implements ReportSynthesisKeyPartnershipExternalManager {


  private ReportSynthesisKeyPartnershipExternalDAO reportSynthesisKeyPartnershipExternalDAO;
  // Managers


  @Inject
  public ReportSynthesisKeyPartnershipExternalManagerImpl(ReportSynthesisKeyPartnershipExternalDAO reportSynthesisKeyPartnershipExternalDAO) {
    this.reportSynthesisKeyPartnershipExternalDAO = reportSynthesisKeyPartnershipExternalDAO;


  }

  @Override
  public void deleteReportSynthesisKeyPartnershipExternal(long reportSynthesisKeyPartnershipExternalId) {

    reportSynthesisKeyPartnershipExternalDAO.deleteReportSynthesisKeyPartnershipExternal(reportSynthesisKeyPartnershipExternalId);
  }

  @Override
  public boolean existReportSynthesisKeyPartnershipExternal(long reportSynthesisKeyPartnershipExternalID) {

    return reportSynthesisKeyPartnershipExternalDAO.existReportSynthesisKeyPartnershipExternal(reportSynthesisKeyPartnershipExternalID);
  }

  @Override
  public List<ReportSynthesisKeyPartnershipExternal> findAll() {

    return reportSynthesisKeyPartnershipExternalDAO.findAll();

  }

  @Override
  public ReportSynthesisKeyPartnershipExternal getReportSynthesisKeyPartnershipExternalById(long reportSynthesisKeyPartnershipExternalID) {

    return reportSynthesisKeyPartnershipExternalDAO.find(reportSynthesisKeyPartnershipExternalID);
  }

  @Override
  public ReportSynthesisKeyPartnershipExternal saveReportSynthesisKeyPartnershipExternal(ReportSynthesisKeyPartnershipExternal reportSynthesisKeyPartnershipExternal) {

    return reportSynthesisKeyPartnershipExternalDAO.save(reportSynthesisKeyPartnershipExternal);
  }


}
