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


import org.cgiar.ccafs.marlo.data.dao.ReportSynthesisKeyPartnershipExternalDAO;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipPmu;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author CCAFS
 */
@Named
public class ReportSynthesisKeyPartnershipExternalManagerImpl implements ReportSynthesisKeyPartnershipExternalManager {


  private ReportSynthesisKeyPartnershipExternalDAO reportSynthesisKeyPartnershipExternalDAO;
  // Managers
  private ReportSynthesisManager reportSynthesisManager;

  @Inject
  public ReportSynthesisKeyPartnershipExternalManagerImpl(
    ReportSynthesisKeyPartnershipExternalDAO reportSynthesisKeyPartnershipExternalDAO,
    ReportSynthesisManager reportSynthesisManager) {
    this.reportSynthesisKeyPartnershipExternalDAO = reportSynthesisKeyPartnershipExternalDAO;
    this.reportSynthesisManager = reportSynthesisManager;


  }

  @Override
  public void deleteReportSynthesisKeyPartnershipExternal(long reportSynthesisKeyPartnershipExternalId) {

    reportSynthesisKeyPartnershipExternalDAO
      .deleteReportSynthesisKeyPartnershipExternal(reportSynthesisKeyPartnershipExternalId);
  }

  @Override
  public boolean existReportSynthesisKeyPartnershipExternal(long reportSynthesisKeyPartnershipExternalID) {

    return reportSynthesisKeyPartnershipExternalDAO
      .existReportSynthesisKeyPartnershipExternal(reportSynthesisKeyPartnershipExternalID);
  }

  @Override
  public List<ReportSynthesisKeyPartnershipExternal> findAll() {

    return reportSynthesisKeyPartnershipExternalDAO.findAll();

  }

  @Override
  public ReportSynthesisKeyPartnershipExternal
    getReportSynthesisKeyPartnershipExternalById(long reportSynthesisKeyPartnershipExternalID) {

    return reportSynthesisKeyPartnershipExternalDAO.find(reportSynthesisKeyPartnershipExternalID);
  }

  /**
   * Get the table 8 Information to load in the word document.
   * 
   * @param flagships - The list of the liaison institution of the flagships
   * @param pmu - The liaison institution of the PMU
   * @param phase - The phase for get the information
   * @return - A list of the key external partnerships that include in the current AR synthesis
   */
  @Override
  public List<ReportSynthesisKeyPartnershipExternal> getTable8(List<LiaisonInstitution> flagships,
    LiaisonInstitution pmu, Phase phase) {

    List<ReportSynthesisKeyPartnershipExternal> table8 = new ArrayList<>();

    // Record all the flagship to get the information
    for (LiaisonInstitution liaisonInstitution : flagships) {


      ReportSynthesis reportSynthesisFP =
        reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());

      if (reportSynthesisFP != null) {
        if (reportSynthesisFP.getReportSynthesisKeyPartnership() != null) {
          if (reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals() != null
            && !reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals()
              .isEmpty()) {


            List<ReportSynthesisKeyPartnershipExternal> externals = new ArrayList<>(
              reportSynthesisFP.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals().stream()
                .filter(c -> c.isActive()).collect(Collectors.toList()));

            for (ReportSynthesisKeyPartnershipExternal external : externals) {

              if (external.getReportSynthesisKeyPartnershipExternalInstitutions() != null
                && !external.getReportSynthesisKeyPartnershipExternalInstitutions().isEmpty()) {
                external.setInstitutions(new ArrayList<>(external.getReportSynthesisKeyPartnershipExternalInstitutions()
                  .stream().filter(c -> c.isActive()).collect(Collectors.toList())));
              }

              if (external.getReportSynthesisKeyPartnershipExternalMainAreas() != null
                && !external.getReportSynthesisKeyPartnershipExternalMainAreas().isEmpty()) {
                external.setMainAreas(new ArrayList<>(external.getReportSynthesisKeyPartnershipExternalMainAreas()
                  .stream().filter(c -> c.isActive()).collect(Collectors.toList())));
              }
              // add all the key external partnerships that the flagship has been created.
              table8.add(external);

            }
          }
        }
      }

    }

    ReportSynthesis reportSynthesisPMU = reportSynthesisManager.findSynthesis(phase.getId(), pmu.getId());

    if (reportSynthesisPMU != null) {

      List<ReportSynthesisKeyPartnershipExternal> excludeKeyPartnerships = new ArrayList<>();

      // check if the PMU exclude some key external partnership
      if (reportSynthesisPMU.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipPmus() != null
        && !reportSynthesisPMU.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipPmus().isEmpty()) {
        for (ReportSynthesisKeyPartnershipPmu plannedPmu : reportSynthesisPMU.getReportSynthesisKeyPartnership()
          .getReportSynthesisKeyPartnershipPmus().stream().filter(ro -> ro.isActive()).collect(Collectors.toList())) {
          excludeKeyPartnerships.add(plannedPmu.getReportSynthesisKeyPartnershipExternal());
        }
      }

      if (!excludeKeyPartnerships.isEmpty()) {
        // Remove for the list the key external partnership the the PMU exclude it.
        table8.removeAll(excludeKeyPartnerships);
      }

    }

    return table8;

  }

  @Override
  public ReportSynthesisKeyPartnershipExternal saveReportSynthesisKeyPartnershipExternal(
    ReportSynthesisKeyPartnershipExternal reportSynthesisKeyPartnershipExternal) {

    return reportSynthesisKeyPartnershipExternalDAO.save(reportSynthesisKeyPartnershipExternal);
  }


}
