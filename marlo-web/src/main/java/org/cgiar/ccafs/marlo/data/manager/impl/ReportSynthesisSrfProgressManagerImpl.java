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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisSrfProgressDAO;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgress;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisSrfProgressManagerImpl implements ReportSynthesisSrfProgressManager {


  private ReportSynthesisSrfProgressDAO reportSynthesisSrfProgressDAO;
  // Managers
  ReportSynthesisManager reportSynthesisManager;
  PhaseManager phaseManager;


  @Inject
  public ReportSynthesisSrfProgressManagerImpl(ReportSynthesisSrfProgressDAO reportSynthesisSrfProgressDAO,
    ReportSynthesisManager reportSynthesisManager, PhaseManager phaseManager) {
    this.reportSynthesisSrfProgressDAO = reportSynthesisSrfProgressDAO;
    this.reportSynthesisManager = reportSynthesisManager;
    this.phaseManager = phaseManager;


  }

  @Override
  public void deleteReportSynthesisSrfProgress(long reportSynthesisSrfProgressId) {

    reportSynthesisSrfProgressDAO.deleteReportSynthesisSrfProgress(reportSynthesisSrfProgressId);
  }

  @Override
  public boolean existReportSynthesisSrfProgress(long reportSynthesisSrfProgressID) {

    return reportSynthesisSrfProgressDAO.existReportSynthesisSrfProgress(reportSynthesisSrfProgressID);
  }

  @Override
  public List<ReportSynthesisSrfProgress> findAll() {

    return reportSynthesisSrfProgressDAO.findAll();

  }

  @Override
  public List<ReportSynthesisSrfProgress> getFlagshipSrfProgress(List<LiaisonInstitution> lInstitutions, long phaseID) {

    List<ReportSynthesisSrfProgress> synthesisSrfProgres = new ArrayList<>();

    for (LiaisonInstitution liaisonInstitution : lInstitutions) {

      ReportSynthesisSrfProgress crpProgress = new ReportSynthesisSrfProgress();
      ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());

      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisSrfProgress() != null) {
          crpProgress = reportSynthesisFP.getReportSynthesisSrfProgress();
        }
      } else {
        ReportSynthesis synthesis = new ReportSynthesis();
        synthesis.setPhase(phaseManager.getPhaseById(phaseID));
        synthesis.setLiaisonInstitution(liaisonInstitution);
        crpProgress.setReportSynthesis(synthesis);
      }
      synthesisSrfProgres.add(crpProgress);
    }

    return synthesisSrfProgres;


  }

  @Override
  public ReportSynthesisSrfProgress getReportSynthesisSrfProgressById(long reportSynthesisSrfProgressID) {

    return reportSynthesisSrfProgressDAO.find(reportSynthesisSrfProgressID);
  }

  @Override
  public ReportSynthesisSrfProgress
    saveReportSynthesisSrfProgress(ReportSynthesisSrfProgress reportSynthesisSrfProgress) {

    return reportSynthesisSrfProgressDAO.save(reportSynthesisSrfProgress);
  }


}
