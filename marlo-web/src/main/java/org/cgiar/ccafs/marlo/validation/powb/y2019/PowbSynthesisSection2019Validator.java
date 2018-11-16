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

package org.cgiar.ccafs.marlo.validation.powb.y2019;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.PowbCollaboration;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationGlobalUnitPmu;
import org.cgiar.ccafs.marlo.data.model.PowbEvidence;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;
import org.cgiar.ccafs.marlo.data.model.PowbFinancialPlan;
import org.cgiar.ccafs.marlo.data.model.PowbProgramChange;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbToc;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class PowbSynthesisSection2019Validator<T extends BaseAction> extends BaseValidator {

  private PowbSynthesisManager powbSynthesisManager;

  // Validators
  private ToC2019Validator toC2019Validator;
  private ProgressOutcomesValidator progressOutcomesValidator;
  private ProgramChangeValidator programChangeValidator;
  private PlannedStudiesValidator plannedStudiesValidator;
  private PlannedCollaborationValidator plannedCollaborationValidator;
  private PlannedBudgetValidator plannedBudgetValidator;

  @Inject
  public PowbSynthesisSection2019Validator(PowbSynthesisManager powbSynthesisManager, ToC2019Validator toC2019Validator,
    ProgressOutcomesValidator progressOutcomesValidator, ProgramChangeValidator programChangeValidator,
    PlannedStudiesValidator plannedStudiesValidator, PlannedCollaborationValidator plannedCollaborationValidator,
    PlannedBudgetValidator plannedBudgetValidator) {

    this.powbSynthesisManager = powbSynthesisManager;
    this.toC2019Validator = toC2019Validator;
    this.progressOutcomesValidator = progressOutcomesValidator;
    this.programChangeValidator = programChangeValidator;
    this.plannedStudiesValidator = plannedStudiesValidator;
    this.plannedCollaborationValidator = plannedCollaborationValidator;
    this.plannedBudgetValidator = plannedBudgetValidator;

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


  public void validateCrpProgress(BaseAction action, PowbSynthesis powbSynthesis) {

    progressOutcomesValidator.validate(action, powbSynthesis, false);

  }


  public void validatePlannedBudget(BaseAction action, PowbSynthesis powbSynthesis) {

    if (powbSynthesis.getFinancialPlan() == null && this.isPMU(powbSynthesis.getLiaisonInstitution())) {
      PowbFinancialPlan newPowbFinancialPlan = new PowbFinancialPlan();

      newPowbFinancialPlan.setFinancialPlanIssues("");
      newPowbFinancialPlan.setPowbSynthesis(powbSynthesis);
      powbSynthesis.setFinancialPlan(newPowbFinancialPlan);

      powbSynthesis.setPowbFinancialPlannedBudgetList(powbSynthesis.getPowbFinancialPlannedBudget().stream()
        .filter(fp -> fp.isActive()).collect(Collectors.toList()));

      plannedBudgetValidator.validate(action, powbSynthesis, false);

      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    } else {
      if (this.isPMU(powbSynthesis.getLiaisonInstitution())) {
        powbSynthesis.setPowbFinancialPlannedBudgetList(powbSynthesis.getPowbFinancialPlannedBudget().stream()
          .filter(fp -> fp.isActive()).collect(Collectors.toList()));

        plannedBudgetValidator.validate(action, powbSynthesis, false);
      }
    }
  }


  public void validatePlannedColaboration(BaseAction action, PowbSynthesis powbSynthesis) {
    // Check if ToC relation is null -create it
    if (powbSynthesis.getCollaboration() == null) {
      PowbCollaboration powbCollaboration = new PowbCollaboration();
      // create one to one relation
      powbSynthesis.setCollaboration(powbCollaboration);
      powbCollaboration.setPowbSynthesis(powbSynthesis);

      powbSynthesis.setPowbCollaborationGlobalUnitsList(powbSynthesis.getPowbCollaborationGlobalUnits().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList()));

      // POWB 2019 Select Collaborations
      powbSynthesis.getCollaboration().setCollaborations(new ArrayList<>());
      if (powbSynthesis.getCollaboration().getPowbCollaborationGlobalUnitPmu() != null
        && !powbSynthesis.getCollaboration().getPowbCollaborationGlobalUnitPmu().isEmpty()) {
        for (PowbCollaborationGlobalUnitPmu plannedStudy : powbSynthesis.getCollaboration()
          .getPowbCollaborationGlobalUnitPmu().stream().filter(ro -> ro.isActive()).collect(Collectors.toList())) {
          powbSynthesis.getCollaboration().getCollaborations().add(plannedStudy.getPowbCollaborationGlobalUnit());
        }
      }

      plannedCollaborationValidator.validate(action, powbSynthesis, false);

      // save the changes
      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    } else {
      powbSynthesis.setPowbCollaborationGlobalUnitsList(powbSynthesis.getPowbCollaborationGlobalUnits().stream()
        .filter(c -> c.isActive()).collect(Collectors.toList()));

      // POWB 2019 Select Collaborations
      powbSynthesis.getCollaboration().setCollaborations(new ArrayList<>());
      if (powbSynthesis.getCollaboration().getPowbCollaborationGlobalUnitPmu() != null
        && !powbSynthesis.getCollaboration().getPowbCollaborationGlobalUnitPmu().isEmpty()) {
        for (PowbCollaborationGlobalUnitPmu plannedStudy : powbSynthesis.getCollaboration()
          .getPowbCollaborationGlobalUnitPmu().stream().filter(ro -> ro.isActive()).collect(Collectors.toList())) {
          powbSynthesis.getCollaboration().getCollaborations().add(plannedStudy.getPowbCollaborationGlobalUnit());
        }
      }

      plannedCollaborationValidator.validate(action, powbSynthesis, false);
    }
  }

  public void validatePlannedStudies(BaseAction action, PowbSynthesis powbSynthesis) {

    if (powbSynthesis.getPowbEvidence() == null) {
      PowbEvidence evidence = new PowbEvidence();

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

      plannedStudiesValidator.validate(action, powbSynthesis, false);
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
      plannedStudiesValidator.validate(action, powbSynthesis, false);
    }


  }


  public void validateProgramChanges(BaseAction action, PowbSynthesis powbSynthesis) {


    // Check if ToC relation is null -create it
    if (powbSynthesis.getPowbProgramChange() == null) {
      PowbProgramChange programChange = new PowbProgramChange();

      // create one to one relation
      powbSynthesis.setPowbProgramChange(programChange);
      programChange.setPowbSynthesis(powbSynthesis);

      programChangeValidator.validate(action, powbSynthesis, false);

      // save the changes
      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    } else {
      programChangeValidator.validate(action, powbSynthesis, false);
    }


  }

  public void validateTocAdjustments(BaseAction action, PowbSynthesis powbSynthesis) {

    // Check if ToC relation is null -create it
    if (powbSynthesis.getPowbToc() == null) {
      PowbToc toc = new PowbToc();

      // create one to one relation
      powbSynthesis.setPowbToc(toc);
      toc.setPowbSynthesis(powbSynthesis);

      toC2019Validator.validate(action, powbSynthesis, false);

      // save the changes
      powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
    } else {
      toC2019Validator.validate(action, powbSynthesis, false);
    }


  }

}
