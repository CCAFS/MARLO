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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.FinancialSumary;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFinancialSummaryBudgetManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFinancialSummaryManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummaryBudget;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewFinancialSummaryBudgetDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewFinancialSummaryDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;

import com.opensymphony.xwork2.inject.Inject;

@Named
public class FinancialSummaryItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisFinancialSummaryManager reportSynthesisFinancialSummaryManager;
  private ReportSynthesisFinancialSummaryBudgetManager reportSynthesisFinancialSummaryBudgetManager;
  private CrpProgramManager crpProgramManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;

  @Inject
  public FinancialSummaryItem(PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFinancialSummaryManager reportSynthesisFinancialSummaryManager,
    ReportSynthesisFinancialSummaryBudgetManager reportSynthesisFinancialSummaryBudgetManager,
    CrpProgramManager crpProgramManager, PowbExpenditureAreasManager powbExpenditureAreasManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisFinancialSummaryManager = reportSynthesisFinancialSummaryManager;
    this.crpProgramManager = crpProgramManager;
    this.reportSynthesisFinancialSummaryBudgetManager = reportSynthesisFinancialSummaryBudgetManager;
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
  }

  public Long createFinancialSummary(NewFinancialSummaryDTO financialSummary, String entityAcronym, User user) {
    Long id = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createFinancialSummary", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == financialSummary.getPhase().getYear()
        && c.getName().equalsIgnoreCase(financialSummary.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createFinancialSummary", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }
    // validate errors
    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("createExpenditure", "LiaisonInstitution", "invalid liaison institution"));
      } else {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
        if (reportSynthesis == null) {
          reportSynthesis = new ReportSynthesis();
          reportSynthesis.setPhase(phase);
          reportSynthesis.setLiaisonInstitution(liaisonInstitution);
          reportSynthesis = this.reportSynthesisManager.saveReportSynthesis(reportSynthesis);
          ReportSynthesisFinancialSummary reportSynthesisFinancialSummary =
            reportSynthesisFinancialSummaryManager.getReportSynthesisFinancialSummaryById(reportSynthesis.getId());
          if (reportSynthesisFinancialSummary == null) {
            reportSynthesisFinancialSummary = new ReportSynthesisFinancialSummary();
            reportSynthesisFinancialSummary.setReportSynthesis(reportSynthesis);
            reportSynthesisFinancialSummary.setCreatedBy(user);
            reportSynthesisFinancialSummary.setNarrative(financialSummary.getNarrative());
            reportSynthesisFinancialSummary = reportSynthesisFinancialSummaryManager
              .saveReportSynthesisFinancialSummary(reportSynthesisFinancialSummary);
            id = reportSynthesisFinancialSummary.getId();
          }
          List<ReportSynthesisFinancialSummaryBudget> summaryBudgetList =
            new ArrayList<ReportSynthesisFinancialSummaryBudget>();
          for (NewFinancialSummaryBudgetDTO budgets : financialSummary.getFlagshipSummaryBudgets()) {
            CrpProgram flagship = crpProgramManager.getCrpProgramBySmoCode("" + budgets.getFlagshipID());
            if (flagship != null && flagship.getProgramType() == 1) {
              LiaisonInstitution liaisonInstitutionFlagship =
                liaisonInstitutionManager.findByAcronymAndCrp(flagship.getAcronym(), globalUnitEntity.getId());
              ReportSynthesisFinancialSummaryBudget flagshipSummaryBudget = new ReportSynthesisFinancialSummaryBudget();
              flagshipSummaryBudget.setLiaisonInstitution(liaisonInstitutionFlagship);
              flagshipSummaryBudget.setReportSynthesisFinancialSummary(reportSynthesisFinancialSummary);
              flagshipSummaryBudget.setCreatedBy(user);
              flagshipSummaryBudget.setW3Actual(budgets.getPlannedBudgetW3Bilateral());
              flagshipSummaryBudget.setW3Planned(budgets.getPlannedBudgetW3Bilateral());
              flagshipSummaryBudget.setW1Actual(budgets.getActualExpenditureW1W2());
              flagshipSummaryBudget.setW1Planned(budgets.getPlannedBudgetW1W2());
              flagshipSummaryBudget.setComments(budgets.getComments());
              flagshipSummaryBudget = reportSynthesisFinancialSummaryBudgetManager
                .saveReportSynthesisFinancialSummaryBudget(flagshipSummaryBudget);
              summaryBudgetList.add(flagshipSummaryBudget);
            }
          }
          if (financialSummary.getStrategicCompetitiveResearchGrant() != null) {
            PowbExpenditureAreas expenditureArea = powbExpenditureAreasManager.getPowbExpenditureAreasById(1);
            ReportSynthesisFinancialSummaryBudget strategicBudget = new ReportSynthesisFinancialSummaryBudget();
            strategicBudget.setExpenditureArea(expenditureArea);
            strategicBudget.setCreatedBy(user);
            strategicBudget.setReportSynthesisFinancialSummary(reportSynthesisFinancialSummary);
            strategicBudget
              .setW3Actual(financialSummary.getStrategicCompetitiveResearchGrant().getPlannedBudgetW3Bilateral());
            strategicBudget
              .setW3Planned(financialSummary.getStrategicCompetitiveResearchGrant().getPlannedBudgetW3Bilateral());
            strategicBudget
              .setW1Actual(financialSummary.getStrategicCompetitiveResearchGrant().getActualExpenditureW1W2());
            strategicBudget
              .setW1Planned(financialSummary.getStrategicCompetitiveResearchGrant().getPlannedBudgetW1W2());
            strategicBudget.setComments(financialSummary.getStrategicCompetitiveResearchGrant().getComments());
            strategicBudget =
              reportSynthesisFinancialSummaryBudgetManager.saveReportSynthesisFinancialSummaryBudget(strategicBudget);
            summaryBudgetList.add(strategicBudget);
          }
          if (financialSummary.getCrpManagementSupportCost() != null) {
            PowbExpenditureAreas expenditureArea = powbExpenditureAreasManager.getPowbExpenditureAreasById(2);
            ReportSynthesisFinancialSummaryBudget crpManagementBudget = new ReportSynthesisFinancialSummaryBudget();
            crpManagementBudget.setExpenditureArea(expenditureArea);
            crpManagementBudget.setCreatedBy(user);
            crpManagementBudget.setReportSynthesisFinancialSummary(reportSynthesisFinancialSummary);
            crpManagementBudget
              .setW3Actual(financialSummary.getCrpManagementSupportCost().getPlannedBudgetW3Bilateral());
            crpManagementBudget
              .setW3Planned(financialSummary.getCrpManagementSupportCost().getPlannedBudgetW3Bilateral());
            crpManagementBudget.setW1Actual(financialSummary.getCrpManagementSupportCost().getActualExpenditureW1W2());
            crpManagementBudget.setW1Planned(financialSummary.getCrpManagementSupportCost().getPlannedBudgetW1W2());
            crpManagementBudget.setComments(financialSummary.getCrpManagementSupportCost().getComments());
            crpManagementBudget = reportSynthesisFinancialSummaryBudgetManager
              .saveReportSynthesisFinancialSummaryBudget(crpManagementBudget);
            summaryBudgetList.add(crpManagementBudget);
          }
          reportSynthesisFinancialSummary.setBudgets(summaryBudgetList);
        } else {
          fieldErrors
            .add(new FieldErrorDTO("createExpenditure", "FinancialSummary", "There is already an financial summary"));
        }

      }
    }
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return id;
  }

  public Long updateFinancialSummary(long idFinancialSummary, NewFinancialSummaryDTO financialSummary,
    String entityAcronym, User user) {
    Long id = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createFinancialSummary", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == financialSummary.getPhase().getYear()
        && c.getName().equalsIgnoreCase(financialSummary.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createFinancialSummary", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }
    // validate errors
    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("updateExpenditure", "LiaisonInstitution", "invalid liaison institution"));
      } else {

      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return id;
  }

}
