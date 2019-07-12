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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressPolicyDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressPolicyManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFlagshipProgressPolicyManagerImpl implements ReportSynthesisFlagshipProgressPolicyManager {


  private ReportSynthesisFlagshipProgressPolicyDAO reportSynthesisFlagshipProgressPolicyDAO;
  // Managers


  @Inject
  public ReportSynthesisFlagshipProgressPolicyManagerImpl(ReportSynthesisFlagshipProgressPolicyDAO reportSynthesisFlagshipProgressPolicyDAO) {
    this.reportSynthesisFlagshipProgressPolicyDAO = reportSynthesisFlagshipProgressPolicyDAO;


  }

  @Override
  public void deleteReportSynthesisFlagshipProgressPolicy(long reportSynthesisFlagshipProgressPolicyId) {

    reportSynthesisFlagshipProgressPolicyDAO.deleteReportSynthesisFlagshipProgressPolicy(reportSynthesisFlagshipProgressPolicyId);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressPolicy(long reportSynthesisFlagshipProgressPolicyID) {

    return reportSynthesisFlagshipProgressPolicyDAO.existReportSynthesisFlagshipProgressPolicy(reportSynthesisFlagshipProgressPolicyID);
  }

  @Override
  public List<ReportSynthesisFlagshipProgressPolicy> findAll() {

    return reportSynthesisFlagshipProgressPolicyDAO.findAll();

  }

  @Override
  public ReportSynthesisFlagshipProgressPolicy getReportSynthesisFlagshipProgressPolicyById(long reportSynthesisFlagshipProgressPolicyID) {

    return reportSynthesisFlagshipProgressPolicyDAO.find(reportSynthesisFlagshipProgressPolicyID);
  }

  @Override
  public ReportSynthesisFlagshipProgressPolicy saveReportSynthesisFlagshipProgressPolicy(ReportSynthesisFlagshipProgressPolicy reportSynthesisFlagshipProgressPolicy) {

    return reportSynthesisFlagshipProgressPolicyDAO.save(reportSynthesisFlagshipProgressPolicy);
  }


}
