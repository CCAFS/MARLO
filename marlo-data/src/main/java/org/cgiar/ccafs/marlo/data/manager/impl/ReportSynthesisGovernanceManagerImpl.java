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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisGovernanceDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisGovernanceManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisGovernance;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisGovernanceManagerImpl implements ReportSynthesisGovernanceManager {


  private ReportSynthesisGovernanceDAO reportSynthesisGovernanceDAO;
  // Managers


  @Inject
  public ReportSynthesisGovernanceManagerImpl(ReportSynthesisGovernanceDAO reportSynthesisGovernanceDAO) {
    this.reportSynthesisGovernanceDAO = reportSynthesisGovernanceDAO;


  }

  @Override
  public void deleteReportSynthesisGovernance(long reportSynthesisGovernanceId) {

    reportSynthesisGovernanceDAO.deleteReportSynthesisGovernance(reportSynthesisGovernanceId);
  }

  @Override
  public boolean existReportSynthesisGovernance(long reportSynthesisGovernanceID) {

    return reportSynthesisGovernanceDAO.existReportSynthesisGovernance(reportSynthesisGovernanceID);
  }

  @Override
  public List<ReportSynthesisGovernance> findAll() {

    return reportSynthesisGovernanceDAO.findAll();

  }

  @Override
  public ReportSynthesisGovernance getReportSynthesisGovernanceById(long reportSynthesisGovernanceID) {

    return reportSynthesisGovernanceDAO.find(reportSynthesisGovernanceID);
  }

  @Override
  public ReportSynthesisGovernance saveReportSynthesisGovernance(ReportSynthesisGovernance reportSynthesisGovernance) {

    return reportSynthesisGovernanceDAO.save(reportSynthesisGovernance);
  }


}
