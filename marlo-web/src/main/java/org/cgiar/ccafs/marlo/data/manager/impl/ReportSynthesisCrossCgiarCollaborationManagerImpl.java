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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisCrossCgiarCollaborationDAO;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCgiarCollaborationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiar;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiarCollaboration;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisCrossCgiarCollaborationManagerImpl
  implements ReportSynthesisCrossCgiarCollaborationManager {


  private ReportSynthesisCrossCgiarCollaborationDAO reportSynthesisCrossCgiarCollaborationDAO;
  // Managers
  private PhaseManager phaseManager;
  private ReportSynthesisManager reportSynthesisManager;


  @Inject
  public ReportSynthesisCrossCgiarCollaborationManagerImpl(
    ReportSynthesisCrossCgiarCollaborationDAO reportSynthesisCrossCgiarCollaborationDAO, PhaseManager phaseManager,
    ReportSynthesisManager reportSynthesisManager) {
    this.reportSynthesisCrossCgiarCollaborationDAO = reportSynthesisCrossCgiarCollaborationDAO;
    this.phaseManager = phaseManager;
    this.reportSynthesisManager = reportSynthesisManager;


  }

  @Override
  public void deleteReportSynthesisCrossCgiarCollaboration(long reportSynthesisCrossCgiarCollaborationId) {

    reportSynthesisCrossCgiarCollaborationDAO
      .deleteReportSynthesisCrossCgiarCollaboration(reportSynthesisCrossCgiarCollaborationId);
  }

  @Override
  public boolean existReportSynthesisCrossCgiarCollaboration(long reportSynthesisCrossCgiarCollaborationID) {

    return reportSynthesisCrossCgiarCollaborationDAO
      .existReportSynthesisCrossCgiarCollaboration(reportSynthesisCrossCgiarCollaborationID);
  }

  @Override
  public List<ReportSynthesisCrossCgiarCollaboration> findAll() {

    return reportSynthesisCrossCgiarCollaborationDAO.findAll();

  }

  @Override
  public List<ReportSynthesisCrossCgiarCollaboration> getFlagshipCollaborations(List<LiaisonInstitution> lInstitutions,
    long phaseID) {

    List<ReportSynthesisCrossCgiarCollaboration> synthesisCrpProgres = new ArrayList<>();

    for (LiaisonInstitution liaisonInstitution : lInstitutions) {

      ReportSynthesisCrossCgiar crossCgiar = new ReportSynthesisCrossCgiar();
      ReportSynthesis reportSynthesisFP = reportSynthesisManager.findSynthesis(phaseID, liaisonInstitution.getId());

      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisCrossCgiar() != null) {
          crossCgiar = reportSynthesisFP.getReportSynthesisCrossCgiar();
          crossCgiar.setCollaborations(new ArrayList<>());
          if (crossCgiar.getReportSynthesisCrossCgiarCollaborations() != null) {
            crossCgiar.setCollaborations(new ArrayList<>(crossCgiar.getReportSynthesisCrossCgiarCollaborations()
              .stream().filter(st -> st.isActive()).collect(Collectors.toList())));
          }
        } else {
          crossCgiar.setCollaborations(new ArrayList<>());
        }
      } else {
        ReportSynthesis synthesis = new ReportSynthesis();
        synthesis.setPhase(phaseManager.getPhaseById(phaseID));
        synthesis.setLiaisonInstitution(liaisonInstitution);
        crossCgiar.setReportSynthesis(synthesis);
        crossCgiar.setCollaborations(new ArrayList<>());
      }
      synthesisCrpProgres.addAll(crossCgiar.getCollaborations());
    }

    return synthesisCrpProgres;


  }

  @Override
  public ReportSynthesisCrossCgiarCollaboration
    getReportSynthesisCrossCgiarCollaborationById(long reportSynthesisCrossCgiarCollaborationID) {

    return reportSynthesisCrossCgiarCollaborationDAO.find(reportSynthesisCrossCgiarCollaborationID);
  }

  @Override
  public ReportSynthesisCrossCgiarCollaboration saveReportSynthesisCrossCgiarCollaboration(
    ReportSynthesisCrossCgiarCollaboration reportSynthesisCrossCgiarCollaboration) {

    return reportSynthesisCrossCgiarCollaborationDAO.save(reportSynthesisCrossCgiarCollaboration);
  }


}
