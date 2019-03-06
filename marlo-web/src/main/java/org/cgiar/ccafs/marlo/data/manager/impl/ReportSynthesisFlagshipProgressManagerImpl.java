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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisFlagshipProgressDAO;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisFlagshipProgressManagerImpl implements ReportSynthesisFlagshipProgressManager {


  private ReportSynthesisFlagshipProgressDAO reportSynthesisFlagshipProgressDAO;
  private PhaseManager phaseManager;
  private ReportSynthesisManager reportSynthesisManager;


  @Inject
  public ReportSynthesisFlagshipProgressManagerImpl(
    ReportSynthesisFlagshipProgressDAO reportSynthesisFlagshipProgressDAO, PhaseManager phaseManager,
    ReportSynthesisManager reportSynthesisManager) {
    this.reportSynthesisFlagshipProgressDAO = reportSynthesisFlagshipProgressDAO;
    this.phaseManager = phaseManager;
    this.reportSynthesisManager = reportSynthesisManager;
  }

  @Override
  public void deleteReportSynthesisFlagshipProgress(long reportSynthesisFlagshipProgressId) {

    reportSynthesisFlagshipProgressDAO.deleteReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressId);
  }

  @Override
  public boolean existReportSynthesisFlagshipProgress(long reportSynthesisFlagshipProgressID) {

    return reportSynthesisFlagshipProgressDAO.existReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgressID);
  }

  @Override
  public List<ReportSynthesisFlagshipProgress> findAll() {

    return reportSynthesisFlagshipProgressDAO.findAll();

  }

  @Override
  public List<ReportSynthesisFlagshipProgress>
    getFlagshipsReportSynthesisFlagshipProgress(List<LiaisonInstitution> liaisonInstitutions, Long phaseID) {
    List<ReportSynthesisFlagshipProgress> synthesisFlagshipProgress = new ArrayList<>();

    for (LiaisonInstitution liaisonInstitution : liaisonInstitutions) {

      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();
      ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());

      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisFlagshipProgress() != null) {
          flagshipProgress = reportSynthesisFP.getReportSynthesisFlagshipProgress();
        }
      } else {
        ReportSynthesis synthesis = new ReportSynthesis();
        synthesis.setPhase(phaseManager.getPhaseById(phaseID));
        synthesis.setLiaisonInstitution(liaisonInstitution);
        flagshipProgress.setReportSynthesis(synthesis);
      }
      synthesisFlagshipProgress.add(flagshipProgress);
    }

    return synthesisFlagshipProgress;
  }

  @Override
  public ReportSynthesisFlagshipProgress
    getReportSynthesisFlagshipProgressById(long reportSynthesisFlagshipProgressID) {

    return reportSynthesisFlagshipProgressDAO.find(reportSynthesisFlagshipProgressID);
  }

  @Override
  public ReportSynthesisFlagshipProgress
    saveReportSynthesisFlagshipProgress(ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress) {

    return reportSynthesisFlagshipProgressDAO.save(reportSynthesisFlagshipProgress);
  }


}
