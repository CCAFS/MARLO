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

package org.cgiar.ccafs.marlo.validation.annualreport;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.PowbExpenditureAreasManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndSynthesisIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.PowbExpenditureAreas;
import org.cgiar.ccafs.marlo.data.model.RepIndSynthesisIndicator;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCgiar;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrpProgressStudy;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisEfficiency;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisExternalPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseExpendituryArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisGovernance;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIndicator;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIndicatorGeneral;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisMelia;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisProgramVariance;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisRisk;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ReportSynthesisSectionValidator<T extends BaseAction> extends BaseValidator {

  private ReportSynthesisManager reportSynthesisManager;
  private FileDBManager fileDBManager;
  private PowbExpenditureAreasManager powbExpenditureAreasManager;
  private ReportSynthesisIndicatorManager reportSynthesisIndicatorManager;
  private RepIndSynthesisIndicatorManager repIndSynthesisIndicatorManager;

  // Validators
  private final CrpProgressValidator crpProgressValidator;
  private final FlagshipProgressValidator flagshipProgressValidator;
  private final CCDimensionValidator ccdValidator;
  private final ProgramVarianceValidator programVarianceValidator;
  private final FundingUseValidator fundingUseValidator;
  private final ExternalPartnershipsValidator externalPartnershipValidator;
  private final CrossCgiarPartnershipValidator cgiarValidator;
  private final MeliaValidator meliaValidator;
  private final EfficiencyValidator efficiencyValidator;
  private final GovernanceValidator governanceValidator;
  private final RiskValidator riskValidator;
  private final FinancialSummaryValidator financialSummaryValidator;
  private final IndicatorsValidator indicatorsValidator;


  @Inject
  public ReportSynthesisSectionValidator(ReportSynthesisManager reportSynthesisManager, FileDBManager fileDBManager,
    CrpProgressValidator crpProgressValidator, FlagshipProgressValidator flagshipProgressValidator,
    CCDimensionValidator ccdValidator, ProgramVarianceValidator programVarianceValidator,
    FundingUseValidator fundingUseValidator, ExternalPartnershipsValidator externalPartnershipValidator,
    CrossCgiarPartnershipValidator cgiarValidator, MeliaValidator meliaValidator,
    EfficiencyValidator efficiencyValidator, GovernanceValidator governanceValidator, RiskValidator riskValidator,
    IndicatorsValidator indicatorsValidator, PowbExpenditureAreasManager powbExpenditureAreasManager,
    ReportSynthesisIndicatorManager reportSynthesisIndicatorManager,
    RepIndSynthesisIndicatorManager repIndSynthesisIndicatorManager,
    FinancialSummaryValidator financialSummaryValidator) {
    super();
    this.reportSynthesisManager = reportSynthesisManager;
    this.fileDBManager = fileDBManager;
    this.crpProgressValidator = crpProgressValidator;
    this.flagshipProgressValidator = flagshipProgressValidator;
    this.ccdValidator = ccdValidator;
    this.programVarianceValidator = programVarianceValidator;
    this.fundingUseValidator = fundingUseValidator;
    this.externalPartnershipValidator = externalPartnershipValidator;
    this.cgiarValidator = cgiarValidator;
    this.meliaValidator = meliaValidator;
    this.efficiencyValidator = efficiencyValidator;
    this.governanceValidator = governanceValidator;
    this.riskValidator = riskValidator;
    this.indicatorsValidator = indicatorsValidator;
    this.powbExpenditureAreasManager = powbExpenditureAreasManager;
    this.reportSynthesisIndicatorManager = reportSynthesisIndicatorManager;
    this.repIndSynthesisIndicatorManager = repIndSynthesisIndicatorManager;
    this.financialSummaryValidator = financialSummaryValidator;
  }


  /**
   * Check if the Liaison Institution is PMU
   * 
   * @return true if liaisonInstitution exist and is PMU
   */
  public boolean isPMU(LiaisonInstitution liaisonInstitution) {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;
  }


  public void validateCgiarPartnership(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisCrossCgiar() == null) {
      ReportSynthesisCrossCgiar cgiar = new ReportSynthesisCrossCgiar();

      // create one to one relation
      reportSynthesis.setReportSynthesisCrossCgiar(cgiar);
      cgiar.setReportSynthesis(reportSynthesis);

      if (!this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        // CGIAR collaborations Information
        if (reportSynthesis.getReportSynthesisCrossCgiar().getReportSynthesisCrossCgiarCollaborations() != null) {
          reportSynthesis.getReportSynthesisCrossCgiar()
            .setCollaborations(new ArrayList<>(
              reportSynthesis.getReportSynthesisCrossCgiar().getReportSynthesisCrossCgiarCollaborations().stream()
                .filter(st -> st.isActive()).collect(Collectors.toList())));
        }
      }

      cgiarValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      if (!this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        // CGIAR collaborations Information
        if (reportSynthesis.getReportSynthesisCrossCgiar().getReportSynthesisCrossCgiarCollaborations() != null) {
          reportSynthesis.getReportSynthesisCrossCgiar()
            .setCollaborations(new ArrayList<>(
              reportSynthesis.getReportSynthesisCrossCgiar().getReportSynthesisCrossCgiarCollaborations().stream()
                .filter(st -> st.isActive()).collect(Collectors.toList())));
        }
      }
      cgiarValidator.validate(action, reportSynthesis, false);
    }

  }

  public void validateControlIndicators(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisIndicatorGeneral() == null) {
      ReportSynthesisIndicatorGeneral indicators = new ReportSynthesisIndicatorGeneral();

      // create one to one relation
      reportSynthesis.setReportSynthesisIndicatorGeneral(indicators);
      indicators.setReportSynthesis(reportSynthesis);


      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        List<ReportSynthesisIndicator> reportSynthesisIndicators = new ArrayList<>();

        reportSynthesisIndicators = reportSynthesisIndicatorManager.getIndicatorsByType(reportSynthesis,
          APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_CONTROL);


        if (reportSynthesisIndicators != null && !reportSynthesisIndicators.isEmpty()) {
          reportSynthesis.getReportSynthesisIndicatorGeneral()
            .setSynthesisIndicators(new ArrayList<>(reportSynthesisIndicators));
        } else {
          reportSynthesis.getReportSynthesisIndicatorGeneral().setSynthesisIndicators(new ArrayList<>());
          List<RepIndSynthesisIndicator> repIndSynthesisIndicator = new ArrayList<>();

          repIndSynthesisIndicator = repIndSynthesisIndicatorManager.findAll().stream()
            .filter(i -> i.isMarlo() && i.getType().equals(APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_CONTROL))
            .sorted((i1, i2) -> i1.getIndicator().compareTo(i2.getIndicator())).collect(Collectors.toList());


          for (RepIndSynthesisIndicator synthesisIndicator : repIndSynthesisIndicator) {
            ReportSynthesisIndicator reportSynthesisIndicator = new ReportSynthesisIndicator();
            reportSynthesisIndicator.setRepIndSynthesisIndicator(synthesisIndicator);
            reportSynthesis.getReportSynthesisIndicatorGeneral().getSynthesisIndicators().add(reportSynthesisIndicator);
          }

        }
      }

      indicatorsValidator.validate(action, reportSynthesis, false, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {

      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        List<ReportSynthesisIndicator> reportSynthesisIndicators = new ArrayList<>();

        reportSynthesisIndicators = reportSynthesisIndicatorManager.getIndicatorsByType(reportSynthesis,
          APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_CONTROL);


        if (reportSynthesisIndicators != null && !reportSynthesisIndicators.isEmpty()) {
          reportSynthesis.getReportSynthesisIndicatorGeneral()
            .setSynthesisIndicators(new ArrayList<>(reportSynthesisIndicators));
        } else {
          reportSynthesis.getReportSynthesisIndicatorGeneral().setSynthesisIndicators(new ArrayList<>());
          List<RepIndSynthesisIndicator> repIndSynthesisIndicator = new ArrayList<>();

          repIndSynthesisIndicator = repIndSynthesisIndicatorManager.findAll().stream()
            .filter(i -> i.isMarlo() && i.getType().equals(APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_CONTROL))
            .sorted((i1, i2) -> i1.getIndicator().compareTo(i2.getIndicator())).collect(Collectors.toList());


          for (RepIndSynthesisIndicator synthesisIndicator : repIndSynthesisIndicator) {
            ReportSynthesisIndicator reportSynthesisIndicator = new ReportSynthesisIndicator();
            reportSynthesisIndicator.setRepIndSynthesisIndicator(synthesisIndicator);
            reportSynthesis.getReportSynthesisIndicatorGeneral().getSynthesisIndicators().add(reportSynthesisIndicator);
          }

        }
      }
      indicatorsValidator.validate(action, reportSynthesis, false, false);
    }

  }

  public void validateCrossCuttingDimensions(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisCrossCuttingDimension() == null) {
      ReportSynthesisCrossCuttingDimension crossCutting = new ReportSynthesisCrossCuttingDimension();

      // create one to one relation
      reportSynthesis.setReportSynthesisCrossCuttingDimension(crossCutting);
      crossCutting.setReportSynthesis(reportSynthesis);

      ccdValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {

      ccdValidator.validate(action, reportSynthesis, false);
    }

  }

  public void validateCrpProgress(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisCrpProgress() == null) {
      ReportSynthesisCrpProgress crpProgress = new ReportSynthesisCrpProgress();

      // create one to one relation
      reportSynthesis.setReportSynthesisCrpProgress(crpProgress);
      crpProgress.setReportSynthesis(reportSynthesis);

      if (!this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        // Srf Targets List
        if (reportSynthesis.getReportSynthesisCrpProgress().getReportSynthesisCrpProgressTargets() != null) {
          reportSynthesis.getReportSynthesisCrpProgress()
            .setSloTargets(new ArrayList<>(reportSynthesis.getReportSynthesisCrpProgress()
              .getReportSynthesisCrpProgressTargets().stream().filter(t -> t.isActive()).collect(Collectors.toList())));
        }
        // Crp Progress Studies
        reportSynthesis.getReportSynthesisCrpProgress().setExpectedStudies(new ArrayList<>());
        if (reportSynthesis.getReportSynthesisCrpProgress().getReportSynthesisCrpProgressStudies() != null
          && !reportSynthesis.getReportSynthesisCrpProgress().getReportSynthesisCrpProgressStudies().isEmpty()) {
          for (ReportSynthesisCrpProgressStudy plannedStudy : reportSynthesis.getReportSynthesisCrpProgress()
            .getReportSynthesisCrpProgressStudies().stream().filter(ro -> ro.isActive()).collect(Collectors.toList())) {
            reportSynthesis.getReportSynthesisCrpProgress().getExpectedStudies()
              .add(plannedStudy.getProjectExpectedStudy());
          }
        }
      }

      crpProgressValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {

      if (!this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        // Srf Targets List
        if (reportSynthesis.getReportSynthesisCrpProgress().getReportSynthesisCrpProgressTargets() != null) {
          reportSynthesis.getReportSynthesisCrpProgress()
            .setSloTargets(new ArrayList<>(reportSynthesis.getReportSynthesisCrpProgress()
              .getReportSynthesisCrpProgressTargets().stream().filter(t -> t.isActive()).collect(Collectors.toList())));
        }
      }
      crpProgressValidator.validate(action, reportSynthesis, false);
    }
  }


  public void validateEfficency(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisEfficiency() == null) {
      ReportSynthesisEfficiency efficency = new ReportSynthesisEfficiency();

      // create one to one relation
      reportSynthesis.setReportSynthesisEfficiency(efficency);
      efficency.setReportSynthesis(reportSynthesis);

      efficiencyValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      efficiencyValidator.validate(action, reportSynthesis, false);
    }

  }

  public void validateFinancial(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFinancialSummary() == null) {
      ReportSynthesisFinancialSummary financial = new ReportSynthesisFinancialSummary();

      // create one to one relation
      reportSynthesis.setReportSynthesisFinancialSummary(financial);
      financial.setReportSynthesis(reportSynthesis);
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        // Flagships Financial Budgets
        if (reportSynthesis.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets() != null
          && !reportSynthesis.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets()
            .isEmpty()) {
          reportSynthesis.getReportSynthesisFinancialSummary()
            .setBudgets(new ArrayList<>(
              reportSynthesis.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets().stream()
                .filter(t -> t.isActive()).collect(Collectors.toList())));
        }

        financialSummaryValidator.validate(action, reportSynthesis, false);

        // save the changes
        reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
      } else {
        if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
          // Flagships Financial Budgets
          if (reportSynthesis.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets() != null
            && !reportSynthesis.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets()
              .isEmpty()) {
            reportSynthesis.getReportSynthesisFinancialSummary()
              .setBudgets(new ArrayList<>(
                reportSynthesis.getReportSynthesisFinancialSummary().getReportSynthesisFinancialSummaryBudgets()
                  .stream().filter(t -> t.isActive()).collect(Collectors.toList())));
          }
          financialSummaryValidator.validate(action, reportSynthesis, false);
        }
      }
    }
  }


  public void validateFlagshipProgress(BaseAction action, ReportSynthesis reportSynthesis) {


    if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();

      // create one to one relation
      reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
      flagshipProgress.setReportSynthesis(reportSynthesis);


      if (!this.isPMU(reportSynthesis.getLiaisonInstitution())) {


        // Setu up Milestones Flagship Table
        if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {

          reportSynthesis.getReportSynthesisFlagshipProgress().setMilestones(
            reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressMilestones().stream()
              .filter(c -> c.isActive() && c.getCrpMilestone() != null).collect(Collectors.toList()));

          reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones()
            .sort((p1, p2) -> p1.getCrpMilestone().getId().compareTo(p2.getCrpMilestone().getId()));
        }

      }

      flagshipProgressValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {

      if (!this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        // Setu up Milestones Flagship Table
        if (reportSynthesis.getReportSynthesisFlagshipProgress() != null) {

          reportSynthesis.getReportSynthesisFlagshipProgress().setMilestones(
            reportSynthesis.getReportSynthesisFlagshipProgress().getReportSynthesisFlagshipProgressMilestones().stream()
              .filter(c -> c.isActive() && c.getCrpMilestone() != null).collect(Collectors.toList()));

          reportSynthesis.getReportSynthesisFlagshipProgress().getMilestones()
            .sort((p1, p2) -> p1.getCrpMilestone().getId().compareTo(p2.getCrpMilestone().getId()));
        }

      }

      flagshipProgressValidator.validate(action, reportSynthesis, false);
    }
  }


  public void validateFunding(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFundingUseSummary() == null) {
      ReportSynthesisFundingUseSummary funding = new ReportSynthesisFundingUseSummary();

      // create one to one relation
      reportSynthesis.setReportSynthesisFundingUseSummary(funding);
      funding.setReportSynthesis(reportSynthesis);

      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        // Flagships Funding Expenditure Areas
        if (reportSynthesis.getReportSynthesisFundingUseSummary().getReportSynthesisFundingUseExpendituryAreas() != null
          && !reportSynthesis.getReportSynthesisFundingUseSummary().getReportSynthesisFundingUseExpendituryAreas()
            .isEmpty()) {
          reportSynthesis.getReportSynthesisFundingUseSummary()
            .setExpenditureAreas(new ArrayList<>(reportSynthesis.getReportSynthesisFundingUseSummary()
              .getReportSynthesisFundingUseExpendituryAreas().stream().filter(t -> t.isActive())
              .sorted((f1, f2) -> f1.getId().compareTo(f2.getId())).collect(Collectors.toList())));
        } else {
          reportSynthesis.getReportSynthesisFundingUseSummary().setExpenditureAreas(new ArrayList<>());
          List<PowbExpenditureAreas> expAreas = new ArrayList<>(
            powbExpenditureAreasManager.findAll().stream().filter(x -> x.isActive() && x.getIsExpenditure())
              .sorted((f1, f2) -> f1.getId().compareTo(f2.getId())).collect(Collectors.toList()));
          for (PowbExpenditureAreas powbExpenditureAreas : expAreas) {
            ReportSynthesisFundingUseExpendituryArea fundingUseExpenditureArea =
              new ReportSynthesisFundingUseExpendituryArea();
            fundingUseExpenditureArea.setExpenditureArea(powbExpenditureAreas);
            reportSynthesis.getReportSynthesisFundingUseSummary().getExpenditureAreas().add(fundingUseExpenditureArea);
          }
        }
      }

      fundingUseValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {

      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        // Flagships Funding Expenditure Areas
        if (reportSynthesis.getReportSynthesisFundingUseSummary().getReportSynthesisFundingUseExpendituryAreas() != null
          && !reportSynthesis.getReportSynthesisFundingUseSummary().getReportSynthesisFundingUseExpendituryAreas()
            .isEmpty()) {
          reportSynthesis.getReportSynthesisFundingUseSummary()
            .setExpenditureAreas(new ArrayList<>(reportSynthesis.getReportSynthesisFundingUseSummary()
              .getReportSynthesisFundingUseExpendituryAreas().stream().filter(t -> t.isActive())
              .sorted((f1, f2) -> f1.getId().compareTo(f2.getId())).collect(Collectors.toList())));
        } else {
          reportSynthesis.getReportSynthesisFundingUseSummary().setExpenditureAreas(new ArrayList<>());
          List<PowbExpenditureAreas> expAreas = new ArrayList<>(
            powbExpenditureAreasManager.findAll().stream().filter(x -> x.isActive() && x.getIsExpenditure())
              .sorted((f1, f2) -> f1.getId().compareTo(f2.getId())).collect(Collectors.toList()));
          for (PowbExpenditureAreas powbExpenditureAreas : expAreas) {
            ReportSynthesisFundingUseExpendituryArea fundingUseExpenditureArea =
              new ReportSynthesisFundingUseExpendituryArea();
            fundingUseExpenditureArea.setExpenditureArea(powbExpenditureAreas);
            reportSynthesis.getReportSynthesisFundingUseSummary().getExpenditureAreas().add(fundingUseExpenditureArea);
          }
        }
      }

      fundingUseValidator.validate(action, reportSynthesis, false);
    }

  }

  public void validateGobernance(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisGovernance() == null) {
      ReportSynthesisGovernance gobernance = new ReportSynthesisGovernance();

      // create one to one relation
      reportSynthesis.setReportSynthesisGovernance(gobernance);
      gobernance.setReportSynthesis(reportSynthesis);

      governanceValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      governanceValidator.validate(action, reportSynthesis, false);
    }

  }


  public void validateIndicatorsInfluence(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisIndicatorGeneral() == null) {
      ReportSynthesisIndicatorGeneral indicators = new ReportSynthesisIndicatorGeneral();

      // create one to one relation
      reportSynthesis.setReportSynthesisIndicatorGeneral(indicators);
      indicators.setReportSynthesis(reportSynthesis);


      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        List<ReportSynthesisIndicator> reportSynthesisIndicators = new ArrayList<>();

        reportSynthesisIndicators = reportSynthesisIndicatorManager.getIndicatorsByType(reportSynthesis,
          APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_INFLUENCE);


        if (reportSynthesisIndicators != null && !reportSynthesisIndicators.isEmpty()) {
          reportSynthesis.getReportSynthesisIndicatorGeneral()
            .setSynthesisIndicators(new ArrayList<>(reportSynthesisIndicators));
        } else {
          reportSynthesis.getReportSynthesisIndicatorGeneral().setSynthesisIndicators(new ArrayList<>());
          List<RepIndSynthesisIndicator> repIndSynthesisIndicator = new ArrayList<>();

          repIndSynthesisIndicator = repIndSynthesisIndicatorManager.findAll().stream()
            .filter(i -> i.isMarlo() && i.getType().equals(APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_INFLUENCE))
            .sorted((i1, i2) -> i1.getIndicator().compareTo(i2.getIndicator())).collect(Collectors.toList());


          for (RepIndSynthesisIndicator synthesisIndicator : repIndSynthesisIndicator) {
            ReportSynthesisIndicator reportSynthesisIndicator = new ReportSynthesisIndicator();
            reportSynthesisIndicator.setRepIndSynthesisIndicator(synthesisIndicator);
            reportSynthesis.getReportSynthesisIndicatorGeneral().getSynthesisIndicators().add(reportSynthesisIndicator);
          }

        }
      }

      indicatorsValidator.validate(action, reportSynthesis, false, true);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      boolean isInfluence = true;
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        List<ReportSynthesisIndicator> reportSynthesisIndicators = new ArrayList<>();
        if (isInfluence) {
          reportSynthesisIndicators = reportSynthesisIndicatorManager.getIndicatorsByType(reportSynthesis,
            APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_INFLUENCE);
        } else {
          reportSynthesisIndicators = reportSynthesisIndicatorManager.getIndicatorsByType(reportSynthesis,
            APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_CONTROL);
        }

        if (reportSynthesisIndicators != null && !reportSynthesisIndicators.isEmpty()) {
          reportSynthesis.getReportSynthesisIndicatorGeneral()
            .setSynthesisIndicators(new ArrayList<>(reportSynthesisIndicators));
        } else {
          reportSynthesis.getReportSynthesisIndicatorGeneral().setSynthesisIndicators(new ArrayList<>());
          List<RepIndSynthesisIndicator> repIndSynthesisIndicator = new ArrayList<>();
          if (isInfluence) {
            repIndSynthesisIndicator = repIndSynthesisIndicatorManager.findAll().stream()
              .filter(i -> i.isMarlo() && i.getType().equals(APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_INFLUENCE))
              .sorted((i1, i2) -> i1.getIndicator().compareTo(i2.getIndicator())).collect(Collectors.toList());
          } else {
            repIndSynthesisIndicator = repIndSynthesisIndicatorManager.findAll().stream()
              .filter(i -> i.isMarlo() && i.getType().equals(APConstants.REP_IND_SYNTHESIS_INDICATOR_TYPE_CONTROL))
              .sorted((i1, i2) -> i1.getIndicator().compareTo(i2.getIndicator())).collect(Collectors.toList());
          }

          for (RepIndSynthesisIndicator synthesisIndicator : repIndSynthesisIndicator) {
            ReportSynthesisIndicator reportSynthesisIndicator = new ReportSynthesisIndicator();
            reportSynthesisIndicator.setRepIndSynthesisIndicator(synthesisIndicator);
            reportSynthesis.getReportSynthesisIndicatorGeneral().getSynthesisIndicators().add(reportSynthesisIndicator);
          }

        }
      }
      indicatorsValidator.validate(action, reportSynthesis, false, true);
    }

  }


  public void validateKeyExternal(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisExternalPartnership() == null) {
      ReportSynthesisExternalPartnership external = new ReportSynthesisExternalPartnership();

      // create one to one relation
      reportSynthesis.setReportSynthesisExternalPartnership(external);
      external.setReportSynthesis(reportSynthesis);

      externalPartnershipValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      externalPartnershipValidator.validate(action, reportSynthesis, false);
    }

  }

  public void validateMelia(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisMelia() == null) {
      ReportSynthesisMelia melia = new ReportSynthesisMelia();

      // create one to one relation
      reportSynthesis.setReportSynthesisMelia(melia);
      melia.setReportSynthesis(reportSynthesis);
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        if (reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations() != null
          && !reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations().isEmpty()) {
          reportSynthesis.getReportSynthesisMelia()
            .setEvaluations(new ArrayList<>(reportSynthesis.getReportSynthesisMelia()
              .getReportSynthesisMeliaEvaluations().stream().filter(e -> e.isActive()).collect(Collectors.toList())));
        }
      }

      meliaValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      if (this.isPMU(reportSynthesis.getLiaisonInstitution())) {
        if (reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations() != null
          && !reportSynthesis.getReportSynthesisMelia().getReportSynthesisMeliaEvaluations().isEmpty()) {
          reportSynthesis.getReportSynthesisMelia()
            .setEvaluations(new ArrayList<>(reportSynthesis.getReportSynthesisMelia()
              .getReportSynthesisMeliaEvaluations().stream().filter(e -> e.isActive()).collect(Collectors.toList())));
        }
      }
      meliaValidator.validate(action, reportSynthesis, false);
    }

  }


  public void validateRisk(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisRisk() == null) {
      ReportSynthesisRisk risk = new ReportSynthesisRisk();

      // create one to one relation
      reportSynthesis.setReportSynthesisRisk(risk);
      risk.setReportSynthesis(reportSynthesis);

      riskValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      riskValidator.validate(action, reportSynthesis, false);
    }

  }

  public void validateVariance(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisProgramVariance() == null) {
      ReportSynthesisProgramVariance variance = new ReportSynthesisProgramVariance();

      // create one to one relation
      reportSynthesis.setReportSynthesisProgramVariance(variance);
      variance.setReportSynthesis(reportSynthesis);

      programVarianceValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      programVarianceValidator.validate(action, reportSynthesis, false);
    }

  }


}
