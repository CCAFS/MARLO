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

package org.cgiar.ccafs.marlo.validation.annualreport.y2018;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisEfficiency;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFinancialSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFundingUseSummary;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisGovernance;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisIntellectualAsset;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisNarrative;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisRisk;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class ReportSynthesis2018SectionValidator<T extends BaseAction> extends BaseValidator {

  private ReportSynthesisManager reportSynthesisManager;

  // Validations
  private final IntellectualAssetsValidator intellectualAssetsValidator;
  private final CCDimension2018Validator ccDimensionValidator;
  private final FundingUse2018Validator fundingUse2018Validator;
  private final FlagshipProgress2018Validator flagshipProgress2018Validator;
  private final Policies2018Validator policies2018Validator;
  private final StudiesOICR2018Validator studiesOICR2018Validator;
  private final Innovations2018Validator innovations2018Validator;
  private final Publications2018Validator publications2018Validator;
  private final FinancialSummary2018Validator financialSummary2018Validator;
  private final NarrativeValidator narrativeValidator;
  private final Efficiency2018Validator efficiency2018Validator;
  private final Risk2018Validator risk2018Validator;
  private final Governance2018Validator governance2018Validator;


  @Inject
  public ReportSynthesis2018SectionValidator(ReportSynthesisManager reportSynthesisManager,
    IntellectualAssetsValidator intellectualAssetsValidator, CCDimension2018Validator ccDimensionValidator,
    FundingUse2018Validator fundingUse2018Validator, FlagshipProgress2018Validator flagshipProgress2018Validator,
    Policies2018Validator policies2018Validator, StudiesOICR2018Validator studiesOICR2018Validator,
    Innovations2018Validator innovations2018Validator, Publications2018Validator publications2018Validator,
    FinancialSummary2018Validator financialSummary2018Validator, NarrativeValidator narrativeValidator,
    Efficiency2018Validator efficiency2018Validator, Risk2018Validator risk2018Validator,
    Governance2018Validator governance2018Validator) {
    super();
    this.reportSynthesisManager = reportSynthesisManager;
    this.intellectualAssetsValidator = intellectualAssetsValidator;
    this.ccDimensionValidator = ccDimensionValidator;
    this.fundingUse2018Validator = fundingUse2018Validator;
    this.flagshipProgress2018Validator = flagshipProgress2018Validator;
    this.policies2018Validator = policies2018Validator;
    this.studiesOICR2018Validator = studiesOICR2018Validator;
    this.innovations2018Validator = innovations2018Validator;
    this.publications2018Validator = publications2018Validator;
    this.financialSummary2018Validator = financialSummary2018Validator;
    this.narrativeValidator = narrativeValidator;
    this.efficiency2018Validator = efficiency2018Validator;
    this.risk2018Validator = risk2018Validator;
    this.governance2018Validator = governance2018Validator;
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


  public void validateCrossCuttingDimensionValidator(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisCrossCuttingDimension() == null) {
      ReportSynthesisCrossCuttingDimension crossCuttingDimension = new ReportSynthesisCrossCuttingDimension();

      // create one to one relation
      reportSynthesis.setReportSynthesisCrossCuttingDimension(crossCuttingDimension);
      crossCuttingDimension.setReportSynthesis(reportSynthesis);

      ccDimensionValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      ccDimensionValidator.validate(action, reportSynthesis, false);
    }

  }

  public void validateEfficiency(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisEfficiency() == null) {
      ReportSynthesisEfficiency efficiency = new ReportSynthesisEfficiency();

      // create one to one relation
      reportSynthesis.setReportSynthesisEfficiency(efficiency);
      efficiency.setReportSynthesis(reportSynthesis);

      efficiency2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      efficiency2018Validator.validate(action, reportSynthesis, false);
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

        financialSummary2018Validator.validate(action, reportSynthesis, false);

        // save the changes
        reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
      }
    } else {
      financialSummary2018Validator.validate(action, reportSynthesis, false);
    }
  }


  public void validateFlagshipProgressValidator(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();

      // create one to one relation
      reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
      flagshipProgress.setReportSynthesis(reportSynthesis);

      flagshipProgress2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      flagshipProgress2018Validator.validate(action, reportSynthesis, false);
    }

  }

  public void validateFundingUse(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFundingUseSummary() == null) {
      ReportSynthesisFundingUseSummary fundingUseSummary = new ReportSynthesisFundingUseSummary();

      // create one to one relation
      reportSynthesis.setReportSynthesisFundingUseSummary(fundingUseSummary);
      fundingUseSummary.setReportSynthesis(reportSynthesis);

      fundingUse2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      fundingUse2018Validator.validate(action, reportSynthesis, false);
    }

  }

  public void validateGovernance(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisGovernance() == null) {
      ReportSynthesisGovernance intellectualAsset = new ReportSynthesisGovernance();

      // create one to one relation
      reportSynthesis.setReportSynthesisGovernance(intellectualAsset);
      intellectualAsset.setReportSynthesis(reportSynthesis);

      governance2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      governance2018Validator.validate(action, reportSynthesis, false);
    }

  }

  public void validateInnovations(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();

      // create one to one relation
      reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
      flagshipProgress.setReportSynthesis(reportSynthesis);

      innovations2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      innovations2018Validator.validate(action, reportSynthesis, false);
    }

  }

  public void validateIntellectualAssets(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisIntellectualAsset() == null) {
      ReportSynthesisIntellectualAsset intellectualAsset = new ReportSynthesisIntellectualAsset();

      // create one to one relation
      reportSynthesis.setReportSynthesisIntellectualAsset(intellectualAsset);
      intellectualAsset.setReportSynthesis(reportSynthesis);

      intellectualAssetsValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      intellectualAssetsValidator.validate(action, reportSynthesis, false);
    }

  }

  public void validateNarrative(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisNarrative() == null) {
      ReportSynthesisNarrative narrative = new ReportSynthesisNarrative();

      // create one to one relation
      reportSynthesis.setReportSynthesisNarrative(narrative);
      narrative.setReportSynthesis(reportSynthesis);

      narrativeValidator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      narrativeValidator.validate(action, reportSynthesis, false);
    }

  }

  public void validatePolicies(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();

      // create one to one relation
      reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
      flagshipProgress.setReportSynthesis(reportSynthesis);

      policies2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      policies2018Validator.validate(action, reportSynthesis, false);
    }

  }

  public void validatePublications(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();

      // create one to one relation
      reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
      flagshipProgress.setReportSynthesis(reportSynthesis);

      publications2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      publications2018Validator.validate(action, reportSynthesis, false);
    }

  }

  public void validateRisk(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisRisk() == null) {
      ReportSynthesisRisk intellectualAsset = new ReportSynthesisRisk();

      // create one to one relation
      reportSynthesis.setReportSynthesisRisk(intellectualAsset);
      intellectualAsset.setReportSynthesis(reportSynthesis);

      risk2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      risk2018Validator.validate(action, reportSynthesis, false);
    }

  }

  public void validateStudiesOICR(BaseAction action, ReportSynthesis reportSynthesis) {

    if (reportSynthesis.getReportSynthesisFlagshipProgress() == null) {
      ReportSynthesisFlagshipProgress flagshipProgress = new ReportSynthesisFlagshipProgress();

      // create one to one relation
      reportSynthesis.setReportSynthesisFlagshipProgress(flagshipProgress);
      flagshipProgress.setReportSynthesis(reportSynthesis);

      studiesOICR2018Validator.validate(action, reportSynthesis, false);

      // save the changes
      reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
    } else {
      studiesOICR2018Validator.validate(action, reportSynthesis, false);
    }

  }

}