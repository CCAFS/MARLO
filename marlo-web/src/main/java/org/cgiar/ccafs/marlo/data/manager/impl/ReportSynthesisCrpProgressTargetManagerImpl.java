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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrpProgressTargetDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrpProgressTargetManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisCrpProgressTargetManagerImpl implements ReportSynthesisCrpProgressTargetManager {


  private ReportSynthesisCrpProgressTargetDAO reportSynthesisCrpProgressTargetDAO;
  // Managers


  @Inject
  public ReportSynthesisCrpProgressTargetManagerImpl(ReportSynthesisCrpProgressTargetDAO reportSynthesisCrpProgressTargetDAO) {
    this.reportSynthesisCrpProgressTargetDAO = reportSynthesisCrpProgressTargetDAO;


  }

  @Override
  public void deleteReportSynthesisCrpProgressTarget(long reportSynthesisCrpProgressTargetId) {

    reportSynthesisCrpProgressTargetDAO.deleteReportSynthesisCrpProgressTarget(reportSynthesisCrpProgressTargetId);
  }

  @Override
  public boolean existReportSynthesisCrpProgressTarget(long reportSynthesisCrpProgressTargetID) {

    return reportSynthesisCrpProgressTargetDAO.existReportSynthesisCrpProgressTarget(reportSynthesisCrpProgressTargetID);
  }

  @Override
  public List<ReportSynthesisCrpProgressTarget> findAll() {

    return reportSynthesisCrpProgressTargetDAO.findAll();

  }

  @Override
  public ReportSynthesisCrpProgressTarget getReportSynthesisCrpProgressTargetById(long reportSynthesisCrpProgressTargetID) {

    return reportSynthesisCrpProgressTargetDAO.find(reportSynthesisCrpProgressTargetID);
  }

  @Override
  public ReportSynthesisCrpProgressTarget saveReportSynthesisCrpProgressTarget(ReportSynthesisCrpProgressTarget reportSynthesisCrpProgressTarget) {

    return reportSynthesisCrpProgressTargetDAO.save(reportSynthesisCrpProgressTarget);
  }


}
