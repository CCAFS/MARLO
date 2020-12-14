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
import org.cgiar.ccafs.marlo.rest.dto.FinancialSumaryDTO;
import org.cgiar.ccafs.marlo.rest.dto.FinancialSummaryBudgetDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewFinancialSummaryBudgetDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewFinancialSummaryDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.FinancialSummaryBudgetMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Named;

import com.opensymphony.xwork2.inject.Inject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
  private FinancialSummaryBudgetMapper financialSummaryBudgetMapper;

  @Inject
  public FinancialSummaryItem(PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFinancialSummaryManager reportSynthesisFinancialSummaryManager,
    ReportSynthesisFinancialSummaryBudgetManager reportSynthesisFinancialSummaryBudgetManager,
    CrpProgramManager crpProgramManager, PowbExpenditureAreasManager powbExpenditureAreasManager,
    FinancialSummaryBudgetMapper financialSummaryBudgetMapper) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisFinancialSummaryManager = reportSynthesisFinancialSummaryManager;
    this.crpProgramManager = crpProgramManager;
    this.reportSynthesisFinancialSummaryBudgetManager = reportSynthesisFinancialSummaryBudgetManager;
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
    this.financialSummaryBudgetMapper = financialSummaryBudgetMapper;
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

  public ResponseEntity<FinancialSumaryDTO> findFinancialSumaryList(String entityAcronym, int year, String phasestr,
    User user) {
    FinancialSumaryDTO financialSumary = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findFinancialSummary", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == year && c.getName().equalsIgnoreCase(phasestr)).findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("findFinancialSummary", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }
    // validate errors
    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("findFinancialSummary", "LiaisonInstitution", "invalid liaison institution"));
      } else {
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
        if (reportSynthesis == null) {
          fieldErrors
            .add(new FieldErrorDTO("findFinancialSummary", "ReportSynthesis", "There is not Sysnthesis report"));
        } else {

          ReportSynthesisFinancialSummary reportSynthesisFinancialSummary =
            reportSynthesisFinancialSummaryManager.getReportSynthesisFinancialSummaryById(reportSynthesis.getId());
          if (reportSynthesisFinancialSummary == null) {
            fieldErrors
              .add(new FieldErrorDTO("findFinancialSummary", "ReportSynthesis", "There is not Financial Summary"));
          } else {
            financialSumary = new FinancialSumaryDTO();
            financialSumary.setNarrative(reportSynthesisFinancialSummary.getNarrative());
            financialSumary.setId(reportSynthesisFinancialSummary.getId());
            financialSumary.setYear(phase.getYear());
            List<FinancialSummaryBudgetDTO> summaryBudgets = new ArrayList<FinancialSummaryBudgetDTO>();
            for (ReportSynthesisFinancialSummaryBudget budgets : reportSynthesisFinancialSummary
              .getReportSynthesisFinancialSummaryBudgets().stream().filter(c -> c.isActive())
              .collect(Collectors.toList())) {
              if (budgets.getLiaisonInstitution() != null) {
                summaryBudgets.add(Optional.ofNullable(budgets)
                  .map(
                    this.financialSummaryBudgetMapper::reportSynthesisFinancialSummaryBudgetToFinancialSummaryBudgetDTO)
                  .orElse(null));
              }

              if (budgets.getExpenditureArea() != null && budgets.getExpenditureArea().getId() == 1) {
                financialSumary.setStrategicCompetitiveResearchGrant(Optional.ofNullable(budgets).map(
                  this.financialSummaryBudgetMapper::reportSynthesisFinancialSummaryBudgetToFinancialSummaryBudgetAreaDTO)
                  .orElse(null));

              }
              if (budgets.getExpenditureArea() != null && budgets.getExpenditureArea().getId() == 2) {
                financialSumary.setCrpManagementSupportCost(Optional.ofNullable(budgets).map(
                  this.financialSummaryBudgetMapper::reportSynthesisFinancialSummaryBudgetToFinancialSummaryBudgetAreaDTO)
                  .orElse(null));
              }
            }

            financialSumary.setFlagshipSummaryBudgets(summaryBudgets);
          }
        }
      }

    }
    return Optional.ofNullable(financialSumary).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
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
        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
        if (reportSynthesis == null) {
          fieldErrors.add(new FieldErrorDTO("updateExpenditure", "ReportSynthesis", "There is not Sysnthesis report"));
        } else {
          ReportSynthesisFinancialSummary reportSynthesisFinancialSummary =
            reportSynthesisFinancialSummaryManager.getReportSynthesisFinancialSummaryById(reportSynthesis.getId());
          if (reportSynthesisFinancialSummary == null) {
            fieldErrors
              .add(new FieldErrorDTO("updateExpenditure", "ReportSynthesis", "There is not Financial Summary"));
          } else {
            // modify financial summary
            reportSynthesisFinancialSummary.setModifiedBy(user);
            reportSynthesisFinancialSummary.setNarrative(financialSummary.getNarrative());
            reportSynthesisFinancialSummary = reportSynthesisFinancialSummaryManager
              .saveReportSynthesisFinancialSummary(reportSynthesisFinancialSummary);
            id = reportSynthesisFinancialSummary.getId();
            List<ReportSynthesisFinancialSummaryBudget> summaryBudgetList =
              new ArrayList<ReportSynthesisFinancialSummaryBudget>();
            for (NewFinancialSummaryBudgetDTO budgets : financialSummary.getFlagshipSummaryBudgets()) {
              CrpProgram flagship = crpProgramManager.getCrpProgramBySmoCode("" + budgets.getFlagshipID());
              if (flagship != null && flagship.getProgramType() == 1) {
                LiaisonInstitution liaisonInstitutionFlagship =
                  liaisonInstitutionManager.findByAcronymAndCrp(flagship.getAcronym(), globalUnitEntity.getId());
                boolean found = false;
                for (ReportSynthesisFinancialSummaryBudget financialSummaryBudget : reportSynthesisFinancialSummary
                  .getReportSynthesisFinancialSummaryBudgets().stream().filter(c -> c.isActive())
                  .collect(Collectors.toList())) {
                  if (financialSummaryBudget.getLiaisonInstitution().getId()
                    .equals(liaisonInstitutionFlagship.getId())) {
                    found = true;
                    ReportSynthesisFinancialSummaryBudget financialSummaryBudgetUpdate = financialSummaryBudget;
                    financialSummaryBudgetUpdate.setModifiedBy(user);
                    financialSummaryBudgetUpdate.setW3Actual(budgets.getPlannedBudgetW3Bilateral());
                    financialSummaryBudgetUpdate.setW3Planned(budgets.getPlannedBudgetW3Bilateral());
                    financialSummaryBudgetUpdate.setW1Actual(budgets.getActualExpenditureW1W2());
                    financialSummaryBudgetUpdate.setW1Planned(budgets.getPlannedBudgetW1W2());
                    financialSummaryBudgetUpdate.setComments(budgets.getComments());
                    summaryBudgetList.add(financialSummaryBudgetUpdate);
                  }
                }
                if (!found) {
                  ReportSynthesisFinancialSummaryBudget flagshipSummaryBudget =
                    new ReportSynthesisFinancialSummaryBudget();
                  flagshipSummaryBudget.setLiaisonInstitution(liaisonInstitutionFlagship);
                  flagshipSummaryBudget.setReportSynthesisFinancialSummary(reportSynthesisFinancialSummary);
                  flagshipSummaryBudget.setCreatedBy(user);
                  flagshipSummaryBudget.setW3Actual(budgets.getPlannedBudgetW3Bilateral());
                  flagshipSummaryBudget.setW3Planned(budgets.getPlannedBudgetW3Bilateral());
                  flagshipSummaryBudget.setW1Actual(budgets.getActualExpenditureW1W2());
                  flagshipSummaryBudget.setW1Planned(budgets.getPlannedBudgetW1W2());
                  flagshipSummaryBudget.setComments(budgets.getComments());
                  summaryBudgetList.add(flagshipSummaryBudget);
                }
              }
              // update or create new flagship budgets
              for (ReportSynthesisFinancialSummaryBudget financialSumaryBudgets : summaryBudgetList) {
                reportSynthesisFinancialSummaryBudgetManager
                  .saveReportSynthesisFinancialSummaryBudget(financialSumaryBudgets);
              }

            }
            // delete flagship budgets
            List<ReportSynthesisFinancialSummaryBudget> financialSumaryBudgetsToRemove =
              new ArrayList<ReportSynthesisFinancialSummaryBudget>();
            for (ReportSynthesisFinancialSummaryBudget financialSumaryBudgets : reportSynthesisFinancialSummary
              .getReportSynthesisFinancialSummaryBudgets().stream()
              .filter(c -> c.getLiaisonInstitution() != null && c.isActive()).collect(Collectors.toList())) {
              boolean found = false;
              for (NewFinancialSummaryBudgetDTO budgets : financialSummary.getFlagshipSummaryBudgets()) {
                CrpProgram flagship = crpProgramManager.getCrpProgramBySmoCode("" + budgets.getFlagshipID());
                if (financialSumaryBudgets.getLiaisonInstitution() != null
                  && financialSumaryBudgets.getLiaisonInstitution().getCrpProgram().getId().equals(flagship.getId())) {
                  found = true;
                }
              }
              if (!found) {
                financialSumaryBudgetsToRemove.add(financialSumaryBudgets);
              }
            }

            // update or delete other financial budget
            if (financialSummary.getStrategicCompetitiveResearchGrant() != null) {
              for (ReportSynthesisFinancialSummaryBudget financialSummaryBudget : reportSynthesisFinancialSummary
                .getReportSynthesisFinancialSummaryBudgets().stream().filter(c -> c.isActive())
                .collect(Collectors.toList())) {
                if (financialSummaryBudget.getExpenditureArea() != null
                  && financialSummaryBudget.getExpenditureArea().getId() == 1) {
                  financialSummaryBudget.setModifiedBy(user);
                  financialSummaryBudget
                    .setW3Actual(financialSummary.getStrategicCompetitiveResearchGrant().getPlannedBudgetW3Bilateral());
                  financialSummaryBudget.setW3Planned(
                    financialSummary.getStrategicCompetitiveResearchGrant().getPlannedBudgetW3Bilateral());
                  financialSummaryBudget
                    .setW1Actual(financialSummary.getStrategicCompetitiveResearchGrant().getActualExpenditureW1W2());
                  financialSummaryBudget
                    .setW1Planned(financialSummary.getStrategicCompetitiveResearchGrant().getPlannedBudgetW1W2());
                  financialSummaryBudget
                    .setComments(financialSummary.getStrategicCompetitiveResearchGrant().getComments());
                  financialSummaryBudget = reportSynthesisFinancialSummaryBudgetManager
                    .saveReportSynthesisFinancialSummaryBudget(financialSummaryBudget);
                }
              }
            } else {
              // check if exists a financial budget
              for (ReportSynthesisFinancialSummaryBudget financialSummaryBudget : reportSynthesisFinancialSummary
                .getReportSynthesisFinancialSummaryBudgets().stream().filter(c -> c.isActive())
                .collect(Collectors.toList())) {
                if (financialSummaryBudget.getExpenditureArea() != null
                  && financialSummaryBudget.getExpenditureArea().getId() == 1) {
                  financialSumaryBudgetsToRemove.add(financialSummaryBudget);
                }
              }
            }

            if (financialSummary.getCrpManagementSupportCost() != null) {
              for (ReportSynthesisFinancialSummaryBudget financialSummaryBudget : reportSynthesisFinancialSummary
                .getReportSynthesisFinancialSummaryBudgets().stream().filter(c -> c.isActive())
                .collect(Collectors.toList())) {
                if (financialSummaryBudget.getExpenditureArea() != null
                  && financialSummaryBudget.getExpenditureArea().getId() == 2) {
                  financialSummaryBudget.setModifiedBy(user);
                  financialSummaryBudget
                    .setW3Actual(financialSummary.getCrpManagementSupportCost().getPlannedBudgetW3Bilateral());
                  financialSummaryBudget
                    .setW3Planned(financialSummary.getCrpManagementSupportCost().getPlannedBudgetW3Bilateral());
                  financialSummaryBudget
                    .setW1Actual(financialSummary.getCrpManagementSupportCost().getActualExpenditureW1W2());
                  financialSummaryBudget
                    .setW1Planned(financialSummary.getCrpManagementSupportCost().getPlannedBudgetW1W2());
                  financialSummaryBudget.setComments(financialSummary.getCrpManagementSupportCost().getComments());
                  financialSummaryBudget = reportSynthesisFinancialSummaryBudgetManager
                    .saveReportSynthesisFinancialSummaryBudget(financialSummaryBudget);
                }
              }
            } else {
              // check if exists a financial budget
              for (ReportSynthesisFinancialSummaryBudget financialSummaryBudget : reportSynthesisFinancialSummary
                .getReportSynthesisFinancialSummaryBudgets().stream().filter(c -> c.isActive())
                .collect(Collectors.toList())) {
                if (financialSummaryBudget.getExpenditureArea() != null
                  && financialSummaryBudget.getExpenditureArea().getId() == 2) {
                  financialSumaryBudgetsToRemove.add(financialSummaryBudget);
                }
              }
            }
            for (ReportSynthesisFinancialSummaryBudget removeFLBudget : financialSumaryBudgetsToRemove) {
              reportSynthesisFinancialSummaryBudgetManager
                .deleteReportSynthesisFinancialSummaryBudget(removeFLBudget.getId());
            }
          }
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


}
