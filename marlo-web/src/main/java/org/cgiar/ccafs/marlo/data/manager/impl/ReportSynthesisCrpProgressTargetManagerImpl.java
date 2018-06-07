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
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressTarget;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisCrpProgressTargetManagerImpl implements ReportSynthesisCrpProgressTargetManager {


  private ReportSynthesisCrpProgressTargetDAO reportSynthesisCrpProgressTargetDAO;
  // Managers
  private ReportSynthesisManager reportSynthesisManager;

  @Inject
  public ReportSynthesisCrpProgressTargetManagerImpl(
    ReportSynthesisCrpProgressTargetDAO reportSynthesisCrpProgressTargetDAO,
    ReportSynthesisManager reportSynthesisManager) {
    this.reportSynthesisCrpProgressTargetDAO = reportSynthesisCrpProgressTargetDAO;
    this.reportSynthesisManager = reportSynthesisManager;
  }

  @Override
  public void deleteReportSynthesisCrpProgressTarget(long reportSynthesisCrpProgressTargetId) {

    reportSynthesisCrpProgressTargetDAO.deleteReportSynthesisCrpProgressTarget(reportSynthesisCrpProgressTargetId);
  }

  @Override
  public boolean existReportSynthesisCrpProgressTarget(long reportSynthesisCrpProgressTargetID) {

    return reportSynthesisCrpProgressTargetDAO
      .existReportSynthesisCrpProgressTarget(reportSynthesisCrpProgressTargetID);
  }

  @Override
  public List<ReportSynthesisCrpProgressTarget> findAll() {

    return reportSynthesisCrpProgressTargetDAO.findAll();

  }

  @Override
  public List<ReportSynthesisCrpProgressTarget> flagshipSynthesis(List<LiaisonInstitution> lInstitutions,
    long phaseID) {

    List<ReportSynthesisCrpProgressTarget> progressTargets = new ArrayList<>();

    for (LiaisonInstitution liaisonInstitution : lInstitutions) {
      ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisCrpProgress() != null) {
          if (reportSynthesisFP.getReportSynthesisCrpProgress().getReportSynthesisCrpProgressTargets() != null) {
            List<ReportSynthesisCrpProgressTarget> targets =
              new ArrayList<>(reportSynthesisFP.getReportSynthesisCrpProgress().getReportSynthesisCrpProgressTargets()
                .stream().filter(s -> s.isActive()).collect(Collectors.toList()));
            if (targets != null || !targets.isEmpty()) {
              for (ReportSynthesisCrpProgressTarget crpProgressTarget : targets) {
                progressTargets.add(crpProgressTarget);
              }
            }
          }
        }
      }
    }

    return progressTargets;
  }

  @Override
  public ReportSynthesisCrpProgressTarget
    getReportSynthesisCrpProgressTargetById(long reportSynthesisCrpProgressTargetID) {

    return reportSynthesisCrpProgressTargetDAO.find(reportSynthesisCrpProgressTargetID);
  }

  @Override
  public ReportSynthesisCrpProgressTarget
    saveReportSynthesisCrpProgressTarget(ReportSynthesisCrpProgressTarget reportSynthesisCrpProgressTarget) {

    return reportSynthesisCrpProgressTargetDAO.save(reportSynthesisCrpProgressTarget);
  }


}
