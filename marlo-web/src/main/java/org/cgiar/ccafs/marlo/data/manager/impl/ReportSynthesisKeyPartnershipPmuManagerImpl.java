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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipPmuDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipPmuManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipPmu;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisKeyPartnershipPmuManagerImpl implements ReportSynthesisKeyPartnershipPmuManager {


  private ReportSynthesisKeyPartnershipPmuDAO reportSynthesisKeyPartnershipPmuDAO;
  // Managers


  @Inject
  public ReportSynthesisKeyPartnershipPmuManagerImpl(ReportSynthesisKeyPartnershipPmuDAO reportSynthesisKeyPartnershipPmuDAO) {
    this.reportSynthesisKeyPartnershipPmuDAO = reportSynthesisKeyPartnershipPmuDAO;


  }

  @Override
  public void deleteReportSynthesisKeyPartnershipPmu(long reportSynthesisKeyPartnershipPmuId) {

    reportSynthesisKeyPartnershipPmuDAO.deleteReportSynthesisKeyPartnershipPmu(reportSynthesisKeyPartnershipPmuId);
  }

  @Override
  public boolean existReportSynthesisKeyPartnershipPmu(long reportSynthesisKeyPartnershipPmuID) {

    return reportSynthesisKeyPartnershipPmuDAO.existReportSynthesisKeyPartnershipPmu(reportSynthesisKeyPartnershipPmuID);
  }

  @Override
  public List<ReportSynthesisKeyPartnershipPmu> findAll() {

    return reportSynthesisKeyPartnershipPmuDAO.findAll();

  }

  @Override
  public ReportSynthesisKeyPartnershipPmu getReportSynthesisKeyPartnershipPmuById(long reportSynthesisKeyPartnershipPmuID) {

    return reportSynthesisKeyPartnershipPmuDAO.find(reportSynthesisKeyPartnershipPmuID);
  }

  @Override
  public ReportSynthesisKeyPartnershipPmu saveReportSynthesisKeyPartnershipPmu(ReportSynthesisKeyPartnershipPmu reportSynthesisKeyPartnershipPmu) {

    return reportSynthesisKeyPartnershipPmuDAO.save(reportSynthesisKeyPartnershipPmu);
  }


}
