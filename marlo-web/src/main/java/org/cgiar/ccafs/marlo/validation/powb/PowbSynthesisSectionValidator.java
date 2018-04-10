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

package org.cgiar.ccafs.marlo.validation.powb;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.FileDBManager;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.PowbCollaboration;
import org.cgiar.ccafs.marlo.data.model.PowbCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.PowbCrpStaffing;
import org.cgiar.ccafs.marlo.data.model.PowbEvidence;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlan;
import org.cgiar.ccafs.marlo.data.model.PowbFlagshipPlans;
import org.cgiar.ccafs.marlo.data.model.PowbManagementGovernance;
import org.cgiar.ccafs.marlo.data.model.PowbManagementRisk;
import org.cgiar.ccafs.marlo.data.model.PowbMonitoringEvaluationLearning;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbToc;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
import java.util.Date;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class PowbSynthesisSectionValidator<T extends BaseAction> extends BaseValidator {

  private PowbSynthesisManager powbSynthesisManager;
  private FileDBManager fileDBManager;

  // Validators
  private ToCAdjustmentsValidator toCAdjustmentsValidator;
  private ExpectedCRPProgressValidator expectedCRPProgressValidator;
  private EvidencesValidator evidencesValidator;
  private FlagshipPlansValidator flagshipPlansValidator;
  private CrossCuttingValidator crossCuttingValidator;
  private CrpStaffingValidator crpStaffingValidator;
  private FinancialPlanValidator financialPlanValidator;
  private MonitoringEvaluationLearningValidator monitoringEvaluationLearningValidator;
  private ManagementRiskValidator managementRiskValidator;
  private ManagementGovernanceValidator managementGovernanceValidator;
  private PowbCollaborationValidator powbCollaborationValidator;

  @Inject
  public PowbSynthesisSectionValidator(PowbSynthesisManager powbSynthesisManager,
    ToCAdjustmentsValidator toCAdjustmentsValidator, FileDBManager fileDBManager,
    ExpectedCRPProgressValidator expectedCRPProgressValidator, EvidencesValidator evidencesValidator,
    FlagshipPlansValidator flagshipPlansValidator, CrossCuttingValidator crossCuttingValidator,
    CrpStaffingValidator crpStaffingValidator, FinancialPlanValidator financialPlanValidator,
    ManagementRiskValidator managementRiskValidator, ManagementGovernanceValidator managementGovernanceValidator,
    MonitoringEvaluationLearningValidator monitoringEvaluationLearningValidator,
    PowbCollaborationValidator powbCollaborationValidator) {
    this.powbSynthesisManager = powbSynthesisManager;
    this.toCAdjustmentsValidator = toCAdjustmentsValidator;
    this.fileDBManager = fileDBManager;
    this.expectedCRPProgressValidator = expectedCRPProgressValidator;
    this.evidencesValidator = evidencesValidator;
    this.flagshipPlansValidator = flagshipPlansValidator;
    this.crossCuttingValidator = crossCuttingValidator;
    this.crpStaffingValidator = crpStaffingValidator;
    this.financialPlanValidator = financialPlanValidator;
    this.managementRiskValidator = managementRiskValidator;
    this.managementGovernanceValidator = managementGovernanceValidator;
    this.monitoringEvaluationLearningValidator = monitoringEvaluationLearningValidator;
    this.powbCollaborationValidator = powbCollaborationValidator;
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


  public void validateColaborationIntegration(BaseAction action, PowbSynthesis powbSynthesis) {
    // Check if ToC relation is null -create it
    if (powbSynthesis.getCollaboration() == null) {
      PowbCollaboration powbCollaboration = new PowbCollaboration();
      powbCollaboration.setActive(true);
      powbCollaboration.setActiveSince(new Date());
      powbCollaboration.setCreatedBy(action.getCurrentUser());
      powbCollaboration.setModifiedBy(action.getCurrentUser());
      powbCollaboration.setModificationJustification("");
      // create one to one relation
      powbSynthesis.setCollaboration(powbCollaboration);
      powbCollaboration.setPowbSynthesis(powbSynthesis);

      powbSynthesis.setPowbCollaborationGlobalUnitsList(powbSynthesis.getPowbCollaborationGlobalUnits().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList()));
      powbSynthesis.setRegions(
        powbSynthesis.getPowbCollaborationRegions().stream().filter(c -> c.isActive()).collect(Collectors.toList()));

      powbCollaborationValidator.validate(action, powbSynthesis, false);

      // save the changes
      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    } else {
      powbSynthesis.setPowbCollaborationGlobalUnitsList(powbSynthesis.getPowbCollaborationGlobalUnits().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList()));

      powbCollaborationValidator.validate(action, powbSynthesis, false);
    }
  }

  public void validateCrossCuttingDimensions(BaseAction action, PowbSynthesis powbSynthesis) {

    // Check if CrossCutting relation is null -create it
    if (powbSynthesis.getPowbCrossCuttingDimension() == null && this.isPMU(powbSynthesis.getLiaisonInstitution())) {

      PowbCrossCuttingDimension crossCutting = new PowbCrossCuttingDimension();
      crossCutting.setActive(true);
      crossCutting.setActiveSince(new Date());
      crossCutting.setCreatedBy(action.getCurrentUser());
      crossCutting.setModifiedBy(action.getCurrentUser());
      crossCutting.setModificationJustification("");

      // create one to one relation
      powbSynthesis.setPowbCrossCuttingDimension(crossCutting);
      crossCutting.setPowbSynthesis(powbSynthesis);

      crossCuttingValidator.validate(action, powbSynthesis, false);

      // save the changes
      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);

    } else {
      if (this.isPMU(powbSynthesis.getLiaisonInstitution())) {
        crossCuttingValidator.validate(action, powbSynthesis, false);
      }
    }


  }

  public void validateCrpProgress(BaseAction action, PowbSynthesis powbSynthesis) {


    powbSynthesis.setExpectedCrpProgresses(
      powbSynthesis.getPowbExpectedCrpProgresses().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
    if (!this.isPMU(powbSynthesis.getLiaisonInstitution())) {
      powbSynthesis.getExpectedCrpProgresses()
        .sort((p1, p2) -> p1.getCrpMilestone().getId().compareTo(p2.getCrpMilestone().getId()));

    }

    expectedCRPProgressValidator.validate(action, powbSynthesis, false);

  }

  public void validateCrpStaffing(BaseAction action, PowbSynthesis powbSynthesis) {

    if (powbSynthesis.getCrpStaffing() == null && this.isPMU(powbSynthesis.getLiaisonInstitution())) {
      PowbCrpStaffing newPowbCrpStaffing = new PowbCrpStaffing();
      newPowbCrpStaffing.setActive(true);
      newPowbCrpStaffing.setCreatedBy(action.getCurrentUser());
      newPowbCrpStaffing.setModifiedBy(action.getCurrentUser());
      newPowbCrpStaffing.setActiveSince(new Date());
      newPowbCrpStaffing.setStaffingIssues("");
      powbSynthesis.setCrpStaffing(newPowbCrpStaffing);
      newPowbCrpStaffing.setPowbSynthesis(powbSynthesis);

      powbSynthesis.setPowbSynthesisCrpStaffingCategoryList(powbSynthesis.getPowbSynthesisCrpStaffingCategory().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList()));

      crpStaffingValidator.validate(action, powbSynthesis, false);

      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    } else {
      if (this.isPMU(powbSynthesis.getLiaisonInstitution())) {
        powbSynthesis.setPowbSynthesisCrpStaffingCategoryList(powbSynthesis.getPowbSynthesisCrpStaffingCategory()
          .stream().filter(c -> c.isActive()).collect(Collectors.toList()));

        crpStaffingValidator.validate(action, powbSynthesis, false);
      }
    }


  }

  public void validateEvidence(BaseAction action, PowbSynthesis powbSynthesis) {

    if (powbSynthesis.getPowbEvidence() == null) {
      PowbEvidence evidence = new PowbEvidence();
      evidence.setActive(true);
      evidence.setActiveSince(new Date());
      evidence.setCreatedBy(action.getCurrentUser());
      evidence.setModifiedBy(action.getCurrentUser());
      evidence.setModificationJustification("");
      // create one to one relation
      powbSynthesis.setPowbEvidence(evidence);
      evidence.setPowbSynthesis(powbSynthesis);

      if (!this.isPMU(powbSynthesis.getLiaisonInstitution())) {
        powbSynthesis.getPowbEvidence().setExpectedStudies(new ArrayList<>());
        if (powbSynthesis.getPowbEvidence().getPowbEvidencePlannedStudies() != null) {
          for (PowbEvidencePlannedStudy plannedStudy : powbSynthesis.getPowbEvidence().getPowbEvidencePlannedStudies()
            .stream().filter(ro -> ro.isActive()).collect(Collectors.toList())) {
            powbSynthesis.getPowbEvidence().getExpectedStudies().add(plannedStudy.getProjectExpectedStudy());
          }
        }
      }

      evidencesValidator.validate(action, powbSynthesis, false);


      // save the changes
      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    } else {
      if (!this.isPMU(powbSynthesis.getLiaisonInstitution())) {
        powbSynthesis.getPowbEvidence().setExpectedStudies(new ArrayList<>());
        if (powbSynthesis.getPowbEvidence().getPowbEvidencePlannedStudies() != null) {
          for (PowbEvidencePlannedStudy plannedStudy : powbSynthesis.getPowbEvidence().getPowbEvidencePlannedStudies()
            .stream().filter(ro -> ro.isActive()).collect(Collectors.toList())) {
            powbSynthesis.getPowbEvidence().getExpectedStudies().add(plannedStudy.getProjectExpectedStudy());
          }
        }
      }

      evidencesValidator.validate(action, powbSynthesis, false);
    }


  }

  public void validateFinancialPlan(BaseAction action, PowbSynthesis powbSynthesis) {

    if (powbSynthesis.getFinancialPlan() == null && this.isPMU(powbSynthesis.getLiaisonInstitution())) {
      PowbFinancialPlan newPowbFinancialPlan = new PowbFinancialPlan();
      newPowbFinancialPlan.setActive(true);
      newPowbFinancialPlan.setCreatedBy(action.getCurrentUser());
      newPowbFinancialPlan.setModifiedBy(action.getCurrentUser());
      newPowbFinancialPlan.setActiveSince(new Date());
      newPowbFinancialPlan.setFinancialPlanIssues("");
      newPowbFinancialPlan.setPowbSynthesis(powbSynthesis);
      powbSynthesis.setFinancialPlan(newPowbFinancialPlan);

      powbSynthesis.setPowbFinancialPlannedBudgetList(powbSynthesis.getPowbFinancialPlannedBudget().stream()
        .filter(fp -> fp.isActive()).collect(Collectors.toList()));
      powbSynthesis.setPowbFinancialExpendituresList(
        powbSynthesis.getPowbFinancialExpenditures().stream().filter(fe -> fe.isActive()).collect(Collectors.toList()));

      financialPlanValidator.validate(action, powbSynthesis, false);


      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    } else {
      if (this.isPMU(powbSynthesis.getLiaisonInstitution())) {
        powbSynthesis.setPowbFinancialPlannedBudgetList(powbSynthesis.getPowbFinancialPlannedBudget().stream()
          .filter(fp -> fp.isActive()).collect(Collectors.toList()));
        powbSynthesis.setPowbFinancialExpendituresList(powbSynthesis.getPowbFinancialExpenditures().stream()
          .filter(fe -> fe.isActive()).collect(Collectors.toList()));

        financialPlanValidator.validate(action, powbSynthesis, false);
      }
    }


  }


  public void validateFlagshipPlans(BaseAction action, PowbSynthesis powbSynthesis) {


    if (powbSynthesis.getPowbFlagshipPlans() == null && !this.isPMU(powbSynthesis.getLiaisonInstitution())) {
      PowbFlagshipPlans newPowbFlagshipPlans = new PowbFlagshipPlans();
      newPowbFlagshipPlans.setActive(true);
      newPowbFlagshipPlans.setCreatedBy(action.getCurrentUser());
      newPowbFlagshipPlans.setModifiedBy(action.getCurrentUser());
      newPowbFlagshipPlans.setActiveSince(new Date());
      newPowbFlagshipPlans.setPlanSummary("");
      newPowbFlagshipPlans.setPowbSynthesis(powbSynthesis);
      powbSynthesis.setPowbFlagshipPlans(newPowbFlagshipPlans);

      if (powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile() != null
        && powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile().getId() != null) {
        powbSynthesis.getPowbFlagshipPlans().setFlagshipProgramFile(
          fileDBManager.getFileDBById(powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile().getId()));
      }

      flagshipPlansValidator.validate(action, powbSynthesis, false);


      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    } else {
      if (!this.isPMU(powbSynthesis.getLiaisonInstitution())) {

        if (powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile() != null
          && powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile().getId() != null) {
          powbSynthesis.getPowbFlagshipPlans().setFlagshipProgramFile(
            fileDBManager.getFileDBById(powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile().getId()));
        }


        flagshipPlansValidator.validate(action, powbSynthesis, false);
      }
    }


  }

  public void validateManagementGovernance(BaseAction action, PowbSynthesis powbSynthesis) {

    if (powbSynthesis.getPowbManagementGovernance() == null && this.isPMU(powbSynthesis.getLiaisonInstitution())) {
      PowbManagementGovernance managementGovernance = new PowbManagementGovernance();
      managementGovernance.setActive(true);
      managementGovernance.setActiveSince(new Date());
      managementGovernance.setCreatedBy(action.getCurrentUser());
      managementGovernance.setModifiedBy(action.getCurrentUser());
      managementGovernance.setModificationJustification("");
      // create one to one relation
      powbSynthesis.setPowbManagementGovernance(managementGovernance);
      managementGovernance.setPowbSynthesis(powbSynthesis);

      managementGovernanceValidator.validate(action, powbSynthesis, false);


      // save the changes
      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    } else {
      // managementGovernanceValidator.validate(action, powbSynthesis, false);
    }


  }

  public void validateManagementRisk(BaseAction action, PowbSynthesis powbSynthesis) {

    if (powbSynthesis.getPowbManagementRisk() == null && this.isPMU(powbSynthesis.getLiaisonInstitution())) {
      PowbManagementRisk managementRisk = new PowbManagementRisk();
      managementRisk.setActive(true);
      managementRisk.setActiveSince(new Date());
      managementRisk.setCreatedBy(action.getCurrentUser());
      managementRisk.setModifiedBy(action.getCurrentUser());
      managementRisk.setModificationJustification("");
      // create one to one relation
      powbSynthesis.setPowbManagementRisk(managementRisk);
      managementRisk.setPowbSynthesis(powbSynthesis);

      managementRiskValidator.validate(action, powbSynthesis, false);

      // save the changes
      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    } else {
      // managementRiskValidator.validate(action, powbSynthesis, false);
    }


  }

  public void validateMEL(BaseAction action, PowbSynthesis powbSynthesis) {

    // Check if ToC relation is null -create it
    if (powbSynthesis.getPowbMonitoringEvaluationLearning() == null) {
      PowbMonitoringEvaluationLearning monitoringEvaluationLearning = new PowbMonitoringEvaluationLearning();
      monitoringEvaluationLearning.setActive(true);
      monitoringEvaluationLearning.setActiveSince(new Date());
      monitoringEvaluationLearning.setCreatedBy(action.getCurrentUser());
      monitoringEvaluationLearning.setModifiedBy(action.getCurrentUser());
      monitoringEvaluationLearning.setModificationJustification("");

      // create one to one relation
      powbSynthesis.setPowbMonitoringEvaluationLearning(monitoringEvaluationLearning);
      monitoringEvaluationLearning.setPowbSynthesis(powbSynthesis);

      if (!this.isPMU(powbSynthesis.getLiaisonInstitution())) {
        if (powbSynthesis.getPowbMonitoringEvaluationLearning()
          .getPowbMonitoringEvaluationLearningExercises() != null) {
          powbSynthesis.getPowbMonitoringEvaluationLearning()
            .setExercises(new ArrayList<>(
              powbSynthesis.getPowbMonitoringEvaluationLearning().getPowbMonitoringEvaluationLearningExercises()
                .stream().filter(ps -> ps.isActive()).collect(Collectors.toList())));
        }
      }

      monitoringEvaluationLearningValidator.validate(action, powbSynthesis, false);


      // save the changes
      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    } else {
      if (!this.isPMU(powbSynthesis.getLiaisonInstitution())) {
        if (powbSynthesis.getPowbMonitoringEvaluationLearning()
          .getPowbMonitoringEvaluationLearningExercises() != null) {
          powbSynthesis.getPowbMonitoringEvaluationLearning()
            .setExercises(new ArrayList<>(
              powbSynthesis.getPowbMonitoringEvaluationLearning().getPowbMonitoringEvaluationLearningExercises()
                .stream().filter(ps -> ps.isActive()).collect(Collectors.toList())));
        }
      }

      monitoringEvaluationLearningValidator.validate(action, powbSynthesis, false);

    }


  }

  public void validateTocAdjustments(BaseAction action, PowbSynthesis powbSynthesis) {


    // Check if ToC relation is null -create it
    if (powbSynthesis.getPowbToc() == null) {
      PowbToc toc = new PowbToc();
      toc.setActive(true);
      toc.setActiveSince(new Date());
      toc.setCreatedBy(action.getCurrentUser());
      toc.setModifiedBy(action.getCurrentUser());
      toc.setModificationJustification("");
      // create one to one relation
      powbSynthesis.setPowbToc(toc);
      toc.setPowbSynthesis(powbSynthesis);

      // Check if the pow toc has file
      if (this.isPMU(powbSynthesis.getLiaisonInstitution())) {
        if (powbSynthesis.getPowbToc().getFile() != null) {
          if (powbSynthesis.getPowbToc().getFile().getId() != null) {
            powbSynthesis.getPowbToc()
              .setFile(fileDBManager.getFileDBById(powbSynthesis.getPowbToc().getFile().getId()));
          } else {
            powbSynthesis.getPowbToc().setFile(null);
          }
        }
      } else {
        powbSynthesis.getPowbToc().setFile(null);
      }

      toCAdjustmentsValidator.validate(action, powbSynthesis, false);


      // save the changes
      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    } else {

      // Check if the pow toc has file
      if (this.isPMU(powbSynthesis.getLiaisonInstitution())) {
        if (powbSynthesis.getPowbToc().getFile() != null) {
          if (powbSynthesis.getPowbToc().getFile().getId() != null) {
            powbSynthesis.getPowbToc()
              .setFile(fileDBManager.getFileDBById(powbSynthesis.getPowbToc().getFile().getId()));
          } else {
            powbSynthesis.getPowbToc().setFile(null);
          }
        }
      } else {
        powbSynthesis.getPowbToc().setFile(null);
      }

      toCAdjustmentsValidator.validate(action, powbSynthesis, false);
    }


  }

}
