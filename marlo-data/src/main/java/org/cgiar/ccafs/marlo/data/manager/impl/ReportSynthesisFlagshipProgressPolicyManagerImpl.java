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
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;
import org.cgiar.ccafs.marlo.data.model.User;

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
  public ReportSynthesisFlagshipProgressPolicyManagerImpl(
    ReportSynthesisFlagshipProgressPolicyDAO reportSynthesisFlagshipProgressPolicyDAO) {
    this.reportSynthesisFlagshipProgressPolicyDAO = reportSynthesisFlagshipProgressPolicyDAO;


  }

  @Override
  public void deleteReportSynthesisFlagshipProgressPolicy(long reportSynthesisFlagshipProgressPolicyId) {

    reportSynthesisFlagshipProgressPolicyDAO
      .deleteReportSynthesisFlagshipProgressPolicy(reportSynthesisFlagshipProgressPolicyId);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgressPolicy(long reportSynthesisFlagshipProgressPolicyID) {

    return reportSynthesisFlagshipProgressPolicyDAO
      .existReportSynthesisFlagshipProgressPolicy(reportSynthesisFlagshipProgressPolicyID);
  }

  @Override
  public List<ReportSynthesisFlagshipProgressPolicy> findAll() {

    return reportSynthesisFlagshipProgressPolicyDAO.findAll();

  }

  @Override
  public ReportSynthesisFlagshipProgressPolicy
    getReportSynthesisFlagshipProgressPolicyById(long reportSynthesisFlagshipProgressPolicyID) {

    return reportSynthesisFlagshipProgressPolicyDAO.find(reportSynthesisFlagshipProgressPolicyID);
  }

  @Override
  public ReportSynthesisFlagshipProgressPolicy
    getReportSynthesisFlagshipProgressPolicyByPolicyAndFlagshipProgress(Long policyId, Long flagshipProgressId) {
    return this.reportSynthesisFlagshipProgressPolicyDAO
      .getReportSynthesisFlagshipProgressPolicyByPolicyAndFlagshipProgress(policyId.longValue(),
        flagshipProgressId.longValue());
  }

  @Override
  public ReportSynthesisFlagshipProgressPolicy saveReportSynthesisFlagshipProgressPolicy(
    ReportSynthesisFlagshipProgressPolicy reportSynthesisFlagshipProgressPolicy) {

    return reportSynthesisFlagshipProgressPolicyDAO.save(reportSynthesisFlagshipProgressPolicy);
  }

  @Override
  public ReportSynthesisFlagshipProgressPolicy toAnnualReport(ProjectPolicy projectPolicy,
    ReportSynthesisFlagshipProgress flagshipProgress, User user, boolean remove) {
    ReportSynthesisFlagshipProgressPolicy progressPolicy = null;

    if (projectPolicy != null && projectPolicy.getId() != null && flagshipProgress != null
      && flagshipProgress.getId() != null && user != null && user.getId() != null) {

      progressPolicy = this.getReportSynthesisFlagshipProgressPolicyByPolicyAndFlagshipProgress(projectPolicy.getId(),
        flagshipProgress.getId());

      if (progressPolicy == null) {
        // is not added to ar
        if (remove) {
          // we need to add it to ar. if we do not need to add it to ar, there is no need to create one.
          progressPolicy = new ReportSynthesisFlagshipProgressPolicy();
          progressPolicy.setCreatedBy(user);
          progressPolicy.setReportSynthesisFlagshipProgress(flagshipProgress);
          progressPolicy.setProjectPolicy(projectPolicy);
        }
      }

      if (progressPolicy != null) {
        progressPolicy.setModifiedBy(user);
        progressPolicy.setActive(remove);
      }

      progressPolicy = this.saveReportSynthesisFlagshipProgressPolicy(progressPolicy);
    }

    return progressPolicy;
  }

  @Override
  public ReportSynthesisFlagshipProgressPolicy toAnnualReport(ReportSynthesisFlagshipProgressPolicy progressPolicy,
    boolean remove) {
    if (progressPolicy != null && progressPolicy.getId() != null) {
      progressPolicy.setActive(remove);
      progressPolicy = this.saveReportSynthesisFlagshipProgressPolicy(progressPolicy);
    }

    return progressPolicy;
  }
}
