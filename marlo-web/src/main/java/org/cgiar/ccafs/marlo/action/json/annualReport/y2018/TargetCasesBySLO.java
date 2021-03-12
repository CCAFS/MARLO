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

package org.cgiar.ccafs.marlo.action.json.annualReport.y2018;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetCasesManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetContributionManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicCountry;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicRegion;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetCases;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetContribution;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.utils.APConfig;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;


public class TargetCasesBySLO extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 6040501349920792107L;

  List<Map<String, Object>> sources;

  private String sloTargetID;


  // GlobalUnit Manager
  private GlobalUnitManager crpManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisSrfProgressTargetCasesManager reportSynthesisSrfProgressTargetCasesManager;
  private SrfSloIndicatorTargetManager srfSloIndicatorTargetManager;
  private ReportSynthesisSrfProgressTargetContributionManager reportSynthesisSrfProgressTargetContributionManager;
  private ProgressTargetCaseGeographicScopeManager progressTargetCaseGeographicScopeManager;
  private ProgressTargetCaseGeographicRegionManager progressTargetCaseGeographicRegionManager;
  private ProgressTargetCaseGeographicCountryManager progressTargetCaseGeographicCountryManager;
  private CrpProgramManager crpProgramManager;


  @Inject
  public TargetCasesBySLO(APConfig config, GlobalUnitManager crpManager, PhaseManager phaseManager,
    ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisSrfProgressTargetCasesManager reportSynthesisSrfProgressTargetCasesManager,
    SrfSloIndicatorTargetManager srfSloIndicatorTargetManager,
    ReportSynthesisSrfProgressTargetContributionManager reportSynthesisSrfProgressTargetContributionManager,
    ProgressTargetCaseGeographicScopeManager progressTargetCaseGeographicScopeManager,
    ProgressTargetCaseGeographicRegionManager progressTargetCaseGeographicRegionManager,
    ProgressTargetCaseGeographicCountryManager progressTargetCaseGeographicCountryManager,
    CrpProgramManager crpProgramManager) {
    super(config);
    this.crpManager = crpManager;
    this.reportSynthesisSrfProgressTargetCasesManager = reportSynthesisSrfProgressTargetCasesManager;
    this.srfSloIndicatorTargetManager = srfSloIndicatorTargetManager;
    this.reportSynthesisSrfProgressTargetContributionManager = reportSynthesisSrfProgressTargetContributionManager;
    this.progressTargetCaseGeographicScopeManager = progressTargetCaseGeographicScopeManager;
    this.progressTargetCaseGeographicRegionManager = progressTargetCaseGeographicRegionManager;
    this.progressTargetCaseGeographicCountryManager = progressTargetCaseGeographicCountryManager;
    this.crpProgramManager = crpProgramManager;
  }


  @Override
  public String execute() throws Exception {
    sources = new ArrayList<>();

    GlobalUnit loggedCrp = crpManager.getGlobalUnitById(this.getCrpID());

    List<ReportSynthesisSrfProgressTargetCases> targetCasesTemp = new ArrayList<>();
    SrfSloIndicatorTarget sloTarget = new SrfSloIndicatorTarget();
    GlobalUnit globalUnit = loggedCrp;
    int sloID = 0;
    if (sloTargetID != null && !sloTargetID.isEmpty()) {
      sloID = Integer.parseInt(sloTargetID);
    }
    if (sloID != 0) {

      // Get the list of liaison institutions Flagships and PMU.
      List<LiaisonInstitution> liaisonInstitutionsFg = globalUnit.getLiaisonInstitutions().stream()
        .filter(c -> c.getCrpProgram() != null && c.isActive()
          && c.getCrpProgram().getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue())
        .collect(Collectors.toList());

      if (liaisonInstitutionsFg != null && !liaisonInstitutionsFg.isEmpty()) {
        liaisonInstitutionsFg.sort(Comparator.comparing(LiaisonInstitution::getAcronym));

        for (LiaisonInstitution li : liaisonInstitutionsFg) {
          ReportSynthesis reportSynthesisFP =
            reportSynthesisManager.findSynthesis(this.getActualPhase().getId(), li.getId());

          // Fill sloTargets List
          sloTarget = srfSloIndicatorTargetManager.getSrfSloIndicatorTargetById(Long.parseLong(sloID + ""));

          if (sloTarget != null) {

            // Get value for 'no new evidence' check button
            ReportSynthesisSrfProgressTargetContribution sloContributionTemp =
              new ReportSynthesisSrfProgressTargetContribution();
            if (reportSynthesisSrfProgressTargetContributionManager.findBySloTargetSynthesis(sloTarget.getId(),
              reportSynthesisFP.getId()) != null) {
              sloContributionTemp =
                reportSynthesisSrfProgressTargetContributionManager.findBySloTargetID(sloTarget.getId()).get(0);
            }

            if (sloContributionTemp != null) {
              sloTarget.setHasEvidence(sloContributionTemp.isHasEvidence());
            }

            List<ReportSynthesisSrfProgressTargetCases> targetCases = new ArrayList<>();
            if (reportSynthesisFP != null && reportSynthesisFP.getId() != null && sloTarget != null
              && sloTarget.getId() != null && reportSynthesisSrfProgressTargetCasesManager
                .getReportSynthesisSrfProgressId(reportSynthesisFP.getId(), sloTarget.getId()) != null) {
              targetCases = reportSynthesisSrfProgressTargetCasesManager
                .getReportSynthesisSrfProgressId(reportSynthesisFP.getId(), sloTarget.getId());
            }

            if (targetCases != null && !targetCases.isEmpty()) {

              // Fill target cases
              for (ReportSynthesisSrfProgressTargetCases targetCase : targetCases) {
                List<ProgressTargetCaseGeographicScope> targetCaseGeographicScopes;

                // Geographic scope
                targetCaseGeographicScopes =
                  progressTargetCaseGeographicScopeManager.findGeographicScopeByTargetCase(targetCase.getId());

                if (targetCaseGeographicScopes != null) {
                  targetCase.setGeographicScopes(targetCaseGeographicScopes);
                }

                // Geographic regions
                List<ProgressTargetCaseGeographicRegion> targetCaseGeographicRegions;
                targetCaseGeographicRegions =
                  progressTargetCaseGeographicRegionManager.findGeographicRegionByTargetCase(targetCase.getId());

                if (targetCaseGeographicRegions != null) {
                  targetCase.setGeographicRegions(targetCaseGeographicRegions);
                }

                targetCase.setLiaisonInstitution(li);
                if (li.getCrpProgram() != null && li.getCrpProgram().getId() != null
                  && targetCase.getLiaisonInstitution() != null) {
                  CrpProgram crpProgram = crpProgramManager.getCrpProgramById(li.getCrpProgram().getId());
                  if (crpProgram != null) {
                    targetCase.getLiaisonInstitution().setCrpProgram(crpProgram);
                  }
                }

                // Geographic countries
                List<ProgressTargetCaseGeographicCountry> targetCaseGeographicCountries;
                targetCaseGeographicCountries =
                  progressTargetCaseGeographicCountryManager.findGeographicCountryByTargetCase(targetCase.getId());

                if (targetCaseGeographicCountries != null) {
                  targetCase.setGeographicCountries(targetCaseGeographicCountries);
                }
              }
              targetCasesTemp.addAll(targetCases);
            }

          }
        }
        if (sloTarget != null) {
          sloTarget.setTargetCases(targetCasesTemp);
        }
      }
    }

    return SUCCESS;
  }


  public List<Map<String, Object>> getSources() {
    return sources;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();
    sloTargetID = StringUtils.trim(parameters.get(APConstants.ID).getMultipleValues()[0]);
  }


  public void setSources(List<Map<String, Object>> sources) {
    this.sources = sources;
  }

}
