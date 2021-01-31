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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisSrfProgressTargetCasesDAO;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetCasesManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetCases;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisSrfProgressTargetCasesManagerImpl implements ReportSynthesisSrfProgressTargetCasesManager {

  // Managers
  private ReportSynthesisSrfProgressTargetCasesDAO reportSynthesisSrfProgressTargetCasesDAO;
  private PhaseManager phaseManager;
  private ReportSynthesisManager reportSynthesisManager;

  @Inject
  public ReportSynthesisSrfProgressTargetCasesManagerImpl(
    ReportSynthesisSrfProgressTargetCasesDAO reportSynthesisSrfProgressTargetCasesDAO, PhaseManager phaseManager,
    ReportSynthesisManager reportSynthesisManager) {
    this.reportSynthesisSrfProgressTargetCasesDAO = reportSynthesisSrfProgressTargetCasesDAO;
    this.phaseManager = phaseManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }

  @Override
  public void deleteReportSynthesisSrfProgressTargetCases(long reportSynthesisSrfProgressTargetCasesId) {

    reportSynthesisSrfProgressTargetCasesDAO
      .deleteReportSynthesisSrfProgressTargetCases(reportSynthesisSrfProgressTargetCasesId);
  }

  @Override
  public boolean existReportSynthesisSrfProgressTargetCases(long reportSynthesisSrfProgressTargetCasesID) {

    return reportSynthesisSrfProgressTargetCasesDAO
      .existReportSynthesisSrfProgressTargetCases(reportSynthesisSrfProgressTargetCasesID);
  }

  @Override
  public List<ReportSynthesisSrfProgressTargetCases> findAll() {

    return reportSynthesisSrfProgressTargetCasesDAO.findAll();

  }

  @Override
  public List<ReportSynthesisSrfProgressTargetCases> getReportSynthesisSrfProgressId(long synthesisID,
    long srfTargetID) {
    return reportSynthesisSrfProgressTargetCasesDAO.getReportSynthesisSrfProgressId(synthesisID, srfTargetID);
  }

  @Override
  public List<ReportSynthesisSrfProgressTargetCases>
    getReportSynthesisSrfProgressTargetCaseBySrfProgress(long srfProgressId) {
    return reportSynthesisSrfProgressTargetCasesDAO.getReportSynthesisSrfProgressTargetCaseBySrfProgress(srfProgressId);
  }

  @Override
  public ReportSynthesisSrfProgressTargetCases
    getReportSynthesisSrfProgressTargetCasesById(long reportSynthesisSrfProgressTargetCasesID) {

    return reportSynthesisSrfProgressTargetCasesDAO.find(reportSynthesisSrfProgressTargetCasesID);
  }

  @Override
  public ReportSynthesisSrfProgressTargetCases getSrfProgressTargetInfo(LiaisonInstitution institutions, long phaseID,
    long targetID) {

    ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTargetCases =
      new ReportSynthesisSrfProgressTargetCases();

    ReportSynthesisSrfProgress crpProgress = new ReportSynthesisSrfProgress();
    ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, institutions.getId());

    if (reportSynthesisFP != null) {
      if (reportSynthesisFP.getReportSynthesisSrfProgress() != null) {
        crpProgress = reportSynthesisFP.getReportSynthesisSrfProgress();
        // reportSynthesisSrfProgressTargetCases =
        // this.getReportSynthesisSrfProgressId(reportSynthesisFP.getId(), targetID);
      }
    } else {
      ReportSynthesis synthesis = new ReportSynthesis();
      synthesis.setPhase(phaseManager.getPhaseById(phaseID));
      synthesis.setLiaisonInstitution(institutions);
      crpProgress.setReportSynthesis(synthesis);
      reportSynthesisSrfProgressTargetCases.setReportSynthesisSrfProgress(crpProgress);
    }

    return reportSynthesisSrfProgressTargetCases;

  }

  @Override
  public ReportSynthesisSrfProgressTargetCases saveReportSynthesisSrfProgressTargetCases(
    ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTargetCases) {

    return reportSynthesisSrfProgressTargetCasesDAO.save(reportSynthesisSrfProgressTargetCases);
  }

}
