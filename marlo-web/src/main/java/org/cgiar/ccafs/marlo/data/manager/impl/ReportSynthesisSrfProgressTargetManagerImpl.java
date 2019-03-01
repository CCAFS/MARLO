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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisSrfProgressTargetDAO;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetManager;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTarget;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisSrfProgressTargetManagerImpl implements ReportSynthesisSrfProgressTargetManager {


  private ReportSynthesisSrfProgressTargetDAO reportSynthesisSrfProgressTargetDAO;
  // Managers
  private PhaseManager phaseManager;
  private ReportSynthesisManager reportSynthesisManager;


  @Inject
  public ReportSynthesisSrfProgressTargetManagerImpl(
    ReportSynthesisSrfProgressTargetDAO reportSynthesisSrfProgressTargetDAO, PhaseManager phaseManager,
    ReportSynthesisManager reportSynthesisManager) {
    this.reportSynthesisSrfProgressTargetDAO = reportSynthesisSrfProgressTargetDAO;
    this.phaseManager = phaseManager;
    this.reportSynthesisManager = reportSynthesisManager;


  }

  @Override
  public void deleteReportSynthesisSrfProgressTarget(long reportSynthesisSrfProgressTargetId) {

    reportSynthesisSrfProgressTargetDAO.deleteReportSynthesisSrfProgressTarget(reportSynthesisSrfProgressTargetId);
  }

  @Override
  public boolean existReportSynthesisSrfProgressTarget(long reportSynthesisSrfProgressTargetID) {

    return reportSynthesisSrfProgressTargetDAO
      .existReportSynthesisSrfProgressTarget(reportSynthesisSrfProgressTargetID);
  }

  @Override
  public List<ReportSynthesisSrfProgressTarget> findAll() {

    return reportSynthesisSrfProgressTargetDAO.findAll();

  }

  @Override
  public ReportSynthesisSrfProgressTarget getReportSynthesisSrfProgressId(long synthesisID, long srfTargetID) {
    return reportSynthesisSrfProgressTargetDAO.getReportSynthesisSrfProgressId(synthesisID, srfTargetID);
  }

  @Override
  public ReportSynthesisSrfProgressTarget
    getReportSynthesisSrfProgressTargetById(long reportSynthesisSrfProgressTargetID) {

    return reportSynthesisSrfProgressTargetDAO.find(reportSynthesisSrfProgressTargetID);
  }


  // @Override
  // public ReportSynthesisSrfProgressTarget getSrfProgressTargetInfo(List<LiaisonInstitution> lInstitutions,
  // long phaseID) {
  //
  // List<ReportSynthesisSrfProgressTarget> reportSynthesisSrfProgressTarget = new ArrayList<>();
  //
  // for (LiaisonInstitution liaisonInstitution : lInstitutions) {
  //
  // ReportSynthesisSrfProgress crpProgress = new ReportSynthesisSrfProgress();
  // ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());
  //
  // if (reportSynthesisFP != null) {
  // if (reportSynthesisFP.getReportSynthesisSrfProgress() != null) {
  // crpProgress = reportSynthesisFP.getReportSynthesisSrfProgress();
  // }
  // } else {
  // ReportSynthesis synthesis = new ReportSynthesis();
  // synthesis.setPhase(phaseManager.getPhaseById(phaseID));
  // synthesis.setLiaisonInstitution(liaisonInstitution);
  // crpProgress.setReportSynthesis(synthesis);
  // }
  // synthesisSrfProgres.add(crpProgress);
  // }
  //
  // return synthesisSrfProgres;
  //
  //
  // }

  @Override
  public ReportSynthesisSrfProgressTarget
    saveReportSynthesisSrfProgressTarget(ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTarget) {

    return reportSynthesisSrfProgressTargetDAO.save(reportSynthesisSrfProgressTarget);
  }


}