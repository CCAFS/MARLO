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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipCollaborationDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipCollaborationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaboration;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaborationPmu;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisKeyPartnershipCollaborationManagerImpl
  implements ReportSynthesisKeyPartnershipCollaborationManager {


  private ReportSynthesisKeyPartnershipCollaborationDAO reportSynthesisKeyPartnershipCollaborationDAO;
  // Managers
  private ReportSynthesisManager reportSynthesisManager;


  @Inject
  public ReportSynthesisKeyPartnershipCollaborationManagerImpl(
    ReportSynthesisKeyPartnershipCollaborationDAO reportSynthesisKeyPartnershipCollaborationDAO,
    ReportSynthesisManager reportSynthesisManager) {
    this.reportSynthesisKeyPartnershipCollaborationDAO = reportSynthesisKeyPartnershipCollaborationDAO;
    this.reportSynthesisManager = reportSynthesisManager;


  }

  @Override
  public void deleteReportSynthesisKeyPartnershipCollaboration(long reportSynthesisKeyPartnershipCollaborationId) {

    reportSynthesisKeyPartnershipCollaborationDAO
      .deleteReportSynthesisKeyPartnershipCollaboration(reportSynthesisKeyPartnershipCollaborationId);
  }

  @Override
  public boolean existReportSynthesisKeyPartnershipCollaboration(long reportSynthesisKeyPartnershipCollaborationID) {

    return reportSynthesisKeyPartnershipCollaborationDAO
      .existReportSynthesisKeyPartnershipCollaboration(reportSynthesisKeyPartnershipCollaborationID);
  }

  @Override
  public List<ReportSynthesisKeyPartnershipCollaboration> findAll() {

    return reportSynthesisKeyPartnershipCollaborationDAO.findAll();

  }


  @Override
  public ReportSynthesisKeyPartnershipCollaboration
    getReportSynthesisKeyPartnershipCollaborationById(long reportSynthesisKeyPartnershipCollaborationID) {

    return reportSynthesisKeyPartnershipCollaborationDAO.find(reportSynthesisKeyPartnershipCollaborationID);
  }

  /**
   * Get the table 9 Information to load in the word document.
   * 
   * @param flagships - The list of the liaison institution of the flagships
   * @param pmu - The liaison institution of the PMU
   * @param phase - The phase for get the information
   * @return - A list of the CGIAR collaborations that include in the current AR synthesis
   */
  @Override
  public List<ReportSynthesisKeyPartnershipCollaboration> getTable9(List<LiaisonInstitution> flagships,
    LiaisonInstitution pmu, Phase phase) {

    List<ReportSynthesisKeyPartnershipCollaboration> table9 = new ArrayList<>();

    // Record all the flagship to get the information
    for (LiaisonInstitution liaisonInstitution : flagships) {

      ReportSynthesis reportSynthesisFP =
        reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());

      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisKeyPartnership() != null) {
          if (reportSynthesisFP.getReportSynthesisKeyPartnership()
            .getReportSynthesisKeyPartnershipCollaborations() != null
            && !reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborations()
              .isEmpty()) {


            List<ReportSynthesisKeyPartnershipCollaboration> collaborations = new ArrayList<>(
              reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborations()
                .stream().filter(c -> c.isActive()).collect(Collectors.toList()));

            for (ReportSynthesisKeyPartnershipCollaboration collaboration : collaborations) {

              if (collaboration.getReportSynthesisKeyPartnershipCollaborationCrps() != null
                && !collaboration.getReportSynthesisKeyPartnershipCollaborationCrps().isEmpty()) {
                collaboration.setCrps(new ArrayList<>(collaboration.getReportSynthesisKeyPartnershipCollaborationCrps()
                  .stream().filter(c -> c.isActive()).collect(Collectors.toList())));
              }

              table9.add(collaboration);
            }
          }
        }
      }

    }


    ReportSynthesis reportSynthesisPMU = reportSynthesisManager.findSynthesis(phase.getId(), pmu.getId());

    if (reportSynthesisPMU != null) {


      List<ReportSynthesisKeyPartnershipCollaboration> excludeColaborations = new ArrayList<>();

      // check if the PMU exclude some CGIAR collaborations
      if (reportSynthesisPMU.getReportSynthesisKeyPartnership()
        .getReportSynthesisKeyPartnershipCollaborationPmus() != null
        && !reportSynthesisPMU.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborationPmus()
          .isEmpty()) {
        for (ReportSynthesisKeyPartnershipCollaborationPmu plannedPmu : reportSynthesisPMU
          .getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborationPmus().stream()
          .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
          excludeColaborations.add(plannedPmu.getReportSynthesisKeyPartnershipCollaboration());
        }
      }

      if (!excludeColaborations.isEmpty()) {
        // Remove for the list the CGIAR collaborations the the PMU exclude it.
        table9.removeAll(excludeColaborations);
      }

    }

    return table9;

  }

  @Override
  public ReportSynthesisKeyPartnershipCollaboration saveReportSynthesisKeyPartnershipCollaboration(
    ReportSynthesisKeyPartnershipCollaboration reportSynthesisKeyPartnershipCollaboration) {

    return reportSynthesisKeyPartnershipCollaborationDAO.save(reportSynthesisKeyPartnershipCollaboration);
  }


}
