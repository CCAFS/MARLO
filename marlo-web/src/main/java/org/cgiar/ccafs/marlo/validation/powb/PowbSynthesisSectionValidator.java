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
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PowbEvidence;
import org.cgiar.ccafs.marlo.data.model.PowbEvidencePlannedStudy;
import org.cgiar.ccafs.marlo.data.model.PowbFlagshipPlans;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbToc;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class PowbSynthesisSectionValidator<T extends BaseAction> extends BaseValidator {

  private PowbSynthesisManager powbSynthesisManager;
  private FileDBManager fileDBManager;

  // Validators
  private ToCAdjustmentsValidator toCAdjustmentsValidator;
  private ExpectedCRPProgressValidator expectedCRPProgressValidator;
  private EvidencesValidator evidencesValidator;
  private FlagshipPlansValidator flagshipPlansValidator;

  @Inject
  public PowbSynthesisSectionValidator(PowbSynthesisManager powbSynthesisManager,
    ToCAdjustmentsValidator toCAdjustmentsValidator, FileDBManager fileDBManager,
    ExpectedCRPProgressValidator expectedCRPProgressValidator, EvidencesValidator evidencesValidator,
    FlagshipPlansValidator flagshipPlansValidator) {
    this.powbSynthesisManager = powbSynthesisManager;
    this.toCAdjustmentsValidator = toCAdjustmentsValidator;
    this.fileDBManager = fileDBManager;
    this.expectedCRPProgressValidator = expectedCRPProgressValidator;
    this.evidencesValidator = evidencesValidator;
    this.flagshipPlansValidator = flagshipPlansValidator;
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

  public void validateCrpProgress(BaseAction action, Phase phase) {
    List<PowbSynthesis> powbSynthesisList =
      new ArrayList<>(phase.getPowbSynthesis().stream().filter(powb -> powb.isActive()).collect(Collectors.toList()));
    for (PowbSynthesis powbSynthesis : powbSynthesisList) {

      powbSynthesis.setExpectedCrpProgresses(
        powbSynthesis.getPowbExpectedCrpProgresses().stream().filter(c -> c.isActive()).collect(Collectors.toList()));
      powbSynthesis.getExpectedCrpProgresses()
        .sort((p1, p2) -> p1.getCrpMilestone().getId().compareTo(p2.getCrpMilestone().getId()));

      expectedCRPProgressValidator.validate(action, powbSynthesis, false);
    }
  }

  public void validateEvidence(BaseAction action, Phase phase) {
    List<PowbSynthesis> powbSynthesisList =
      new ArrayList<>(phase.getPowbSynthesis().stream().filter(powb -> powb.isActive()).collect(Collectors.toList()));
    for (PowbSynthesis powbSynthesis : powbSynthesisList) {
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
        // save the changes
        powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
      }

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

  public void validateFlagshipPlans(BaseAction action, Phase phase) {

    List<PowbSynthesis> powbSynthesisList =
      new ArrayList<>(phase.getPowbSynthesis().stream().filter(powb -> powb.isActive()).collect(Collectors.toList()));
    for (PowbSynthesis powbSynthesis : powbSynthesisList) {

      if (powbSynthesis.getPowbFlagshipPlans() == null && !this.isPMU(powbSynthesis.getLiaisonInstitution())) {
        PowbFlagshipPlans newPowbFlagshipPlans = new PowbFlagshipPlans();
        newPowbFlagshipPlans.setActive(true);
        newPowbFlagshipPlans.setCreatedBy(action.getCurrentUser());
        newPowbFlagshipPlans.setModifiedBy(action.getCurrentUser());
        newPowbFlagshipPlans.setActiveSince(new Date());
        newPowbFlagshipPlans.setPlanSummary("");
        newPowbFlagshipPlans.setPowbSynthesis(powbSynthesis);
        powbSynthesis.setPowbFlagshipPlans(newPowbFlagshipPlans);
        powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
      }

      if (powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile() != null
        && powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile().getId() != null) {
        powbSynthesis.getPowbFlagshipPlans().setFlagshipProgramFile(
          fileDBManager.getFileDBById(powbSynthesis.getPowbFlagshipPlans().getFlagshipProgramFile().getId()));
      }

      flagshipPlansValidator.validate(action, powbSynthesis, false);
    }
  }

  public void validateTocAdjustments(BaseAction action, Phase phase) {
    List<PowbSynthesis> powbSynthesisList =
      new ArrayList<>(phase.getPowbSynthesis().stream().filter(powb -> powb.isActive()).collect(Collectors.toList()));
    for (PowbSynthesis powbSynthesis : powbSynthesisList) {

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
        // save the changes
        powbSynthesis = powbSynthesisManager.savePowbSynthesis(powbSynthesis);
      }

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
