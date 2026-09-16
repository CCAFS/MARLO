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


package org.cgiar.ccafs.marlo.validation.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.CrpAssumption;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.SectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Objects;

import javax.inject.Named;

/**
 * @author Christian David Garcia -CIAT/CCAFS
 */

@Named
public class OutcomeValidator extends BaseValidator

{

  public OutcomeValidator() {

  }


  private Path getAutoSaveFilePath(CrpProgram program, BaseAction action) {
    String composedClassName = program.getClass().getSimpleName();
    String actionFile = SectionStatusEnum.OUTCOMES.getStatus().replace("/", "_");
    String autoSaveFile = program.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction action, List<CrpProgramOutcome> outcomes, CrpProgram program, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (!saving) {
      Path path = this.getAutoSaveFilePath(program, action);

      if (path.toFile().exists()) {
        action.addMissingField("program.outcomes.draft");
      }
    }
    // ==========================================================
    // FILTER NULL OUTCOMES BEFORE VALIDATION
    // ==========================================================
    outcomes = outcomes.stream()
        .filter(Objects::nonNull)
        .collect(Collectors.toList());

    if (outcomes.size() == 0) {
      action.addMissingField("program.outcomes");
      action.getInvalidFields().put("list-outcomes",
        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Outcomes"}));

    }

    for (int i = 0; i < outcomes.size(); i++) {
      CrpProgramOutcome outcome = outcomes.get(i);
      this.validateOuctome(action, outcome, i);
    }
    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    }
    this.saveMissingFieldsImpactPathway(program, "outcomes", action.getActualPhase().getYear(),
      action.getActualPhase().getDescription(), action.getActualPhase().getUpkeep(), action);
  }


  public void validateAssumption(BaseAction action, CrpAssumption assuption, int i, int j, int k) {
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));
    params.add(String.valueOf(j + 1));
    params.add(String.valueOf(k + 1));


    if (!(this.isValidString(assuption.getDescription()) && this.wordCount(assuption.getDescription()) <= 100)) {
      action.addMessage(action.getText("outcome.action.subido.assumption.required", params));
      action.getInvalidFields().put("input-outcomesForm[" + i + "].subIdos[" + j + "].assumptions[" + k + "].description",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }

  public void validateMilestone(BaseAction action, CrpMilestone milestone, int i, int j) {
    // Requiredness is scoped to the phase that can still answer the target. A period target whose
    // year is behind the phase is rendered read-only -- the form asks BaseAction for exactly this
    // rule -- so reporting it as missing opened a gap the user had no way of closing, and the
    // section kept reading as incomplete however much was filled in.
    if (!action.canEditMilestoneYear(milestone.getYear())) {
      return;
    }

    int year = action.getCurrentCycleYear();
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));
    params.add(String.valueOf(j + 1));
    String customName = "outcomesForm[" + i + "].milestones[" + j + "]";

    if (!(this.isValidString(milestone.getTitle()) && this.wordCount(milestone.getTitle()) <= 100)) {
      action.getInvalidFields().put("input-" + customName + ".title", InvalidFieldsMessages.EMPTYFIELD);
      action.addMessage(action.getText("outcome.action.title.required", params));
    }

    /*
     * if (milestone.getValue() == null || !this.isValidNumber(milestone.getValue().toString())) {
     * action.addMessage(action.getText("outcome.action.milestone.value.required", params));
     * }
     */

    if (!this.isValidNumber(String.valueOf(milestone.getYear())) || milestone.getYear() <= 0) {
      action.addMessage(action.getText("outcome.action.milestone.year.required", params));
      action.getInvalidFields().put("input-" + customName + ".year", InvalidFieldsMessages.EMPTYFIELD);
    }


    if (milestone.getCrpProgramOutcome() != null && milestone.getCrpProgramOutcome().getYear() != null
      && milestone.getYear() == null) {
      action.getInvalidFields().put("input-" + customName + ".year", InvalidFieldsMessages.EMPTYFIELD);
      action.addMessage(action.getText("outcome.action.milestone.year.required", params));
    }
    // A period target dated past the indicator's closing year used to be reported right here, as a
    // missing "Target Year" on the target itself. That made sense while each target carried its own
    // year select; in the matrix the year belongs to the column, is a hidden input, and the same
    // year is shared by every row -- so the report landed on a cell that was often filled in and
    // that the user cannot edit. It is raised once per indicator now, against the closing year,
    // which is the field that resolves it. See validateOuctome.

    if (milestone.getSrfTargetUnit() != null && milestone.getSrfTargetUnit().getId() != -1) {

      if (milestone.getValue() == null || !this.isValidNumber(milestone.getValue().toString())) {
        action.addMessage(action.getText("outcome.action.milestone.value.required", params));
        action.getInvalidFields().put("input-" + customName + ".value", InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    if (milestone.getMilestonesStatus() != null) {
      if (milestone.getMilestonesStatus().getId() == 4) {
        if (!this.isValidNumber(String.valueOf(milestone.getExtendedYear())) || milestone.getExtendedYear() <= 0) {
          action.addMessage(action.getText("outcome.action.milestone.extendedYear.required", params));
          action.getInvalidFields().put("input-" + customName + ".extendedYear", InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }

    /* POWB 2019 validators */
    if (action.hasSpecificities(APConstants.IMPACT_PATHWAY_CROSS_CUTTING_MARKETS_ACTIVE) && milestone.getYear() != null
      && milestone.getYear() == year && milestone.getYear() >= 2019) {

      if (!(this.isValidString(milestone.getPowbMilestoneVerification()))) {
        action.getInvalidFields().put("input-" + customName + ".powbMilestoneVerification",
          InvalidFieldsMessages.EMPTYFIELD);
        action.addMessage(action.getText("outcome.action.milestone.mean.verification.required", params));
      }

      if (milestone.getPowbIndFollowingMilestone() != null) {
        if (milestone.getPowbIndFollowingMilestone().getId() == null
          || milestone.getPowbIndFollowingMilestone().getId() == -1) {
          action.addMessage(action.getText("outcome.action.milestone.following.required", params));
          action.getInvalidFields().put("input-" + customName + ".powbIndFollowingMilestone.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      } else {
        action.addMessage(action.getText("outcome.action.milestone.following.required", params));
        action.getInvalidFields().put("input-" + customName + ".powbIndFollowingMilestone.id",
          InvalidFieldsMessages.EMPTYFIELD);
      }

      if (action.getGlobalUnitType() != 3) {
        if (milestone.getPowbIndAssesmentRisk() != null) {
          if (milestone.getPowbIndAssesmentRisk().getId() == null
            || milestone.getPowbIndAssesmentRisk().getId() == -1) {
            action.addMessage(action.getText("outcome.action.milestone.following.required", params));
            action.getInvalidFields().put("list-" + customName + ".powbIndAssesmentRisk.id",
              InvalidFieldsMessages.EMPTYFIELD);
          } else {
            if (milestone.getPowbIndAssesmentRisk().getId() == 2 || milestone.getPowbIndAssesmentRisk().getId() == 3) {

              if ((milestone.getPowbIndMilestoneRisk() == null) || (milestone.getPowbIndMilestoneRisk().getId() == null)
                || (milestone.getPowbIndMilestoneRisk().getId() == -1)) {
                // Required for medium/high the main risk
                action.addMessage(action.getText("outcome.action.milestone.main.risk.required", params));
                action.getInvalidFields().put("input-" + customName + ".powbIndMilestoneRisk.id",
                  InvalidFieldsMessages.EMPTYFIELD);
              } else {
                // Please specify other risk
                if (milestone.getPowbIndMilestoneRisk().getId() == 7) {
                  if (!(this.isValidString(milestone.getPowbMilestoneOtherRisk()))) {
                    action.getInvalidFields().put("input-" + customName + ".powbMilestoneOtherRisk",
                      InvalidFieldsMessages.EMPTYFIELD);
                    action.addMessage(action.getText("outcome.action.milestone.other.risk.required", params));
                  }
                }
              }
            }
          }
        } else {
          action.addMessage(action.getText("outcome.action.milestone.following.required", params));
          action.getInvalidFields().put("input-" + customName + ".powbIndAssesmentRisk.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }

      if (milestone.getGenderFocusLevel() != null) {
        if (milestone.getGenderFocusLevel().getId() == null || milestone.getGenderFocusLevel().getId() == -1) {
          action.addMessage(action.getText("outcome.action.milestone.gender.required", params));
          action.getInvalidFields().put("input-" + customName + ".genderFocusLevel.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }

      if (milestone.getYouthFocusLevel() != null) {
        if (milestone.getYouthFocusLevel().getId() == null || milestone.getYouthFocusLevel().getId() == -1) {
          action.addMessage(action.getText("outcome.action.milestone.youth.required", params));
          action.getInvalidFields().put("input-" + customName + ".youthFocusLevel.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }

      if (milestone.getCapdevFocusLevel() != null) {
        if (milestone.getCapdevFocusLevel().getId() == null || milestone.getCapdevFocusLevel().getId() == -1) {
          action.addMessage(action.getText("outcome.action.milestone.capdev.required", params));
          action.getInvalidFields().put("input-" + customName + ".capdevFocusLevel.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }

      if (milestone.getClimateFocusLevel() != null) {
        if (milestone.getClimateFocusLevel().getId() == null || milestone.getClimateFocusLevel().getId() == -1) {
          action.addMessage(action.getText("outcome.action.milestone.climate.required", params));
          action.getInvalidFields().put("input-" + customName + ".climateFocusLevel.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }
      }
    }
    /* End POWB Validations */

  }

  public void validateOuctome(BaseAction action, CrpProgramOutcome outcome, int i) {
    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));

    if (outcome == null) {
        return;
    }

    // The "input-" prefix is what the front end keys its field highlighting on.
    String prefix = "input-outcomesForm[" + i + "]"; 
    String listPrefix = "list-outcomesForm[" + i + "]";

    // 1. Statement
    if (!(this.isValidString(outcome.getDescription()) && this.wordCount(outcome.getDescription()) <= 100)) {
        action.addMessage(action.getText("outcome.action.statement.required", params));
        action.getInvalidFields().put(prefix + ".description", InvalidFieldsMessages.EMPTYFIELD);
    }

    // 2. Acronym
    if (!this.isValidString(outcome.getAcronym())) {
        action.addMessage(action.getText("outcome.action.acronym.required", params));
        action.getInvalidFields().put(prefix + ".acronym", InvalidFieldsMessages.EMPTYFIELD);
    }

    // 3. Target value: only collected once the indicator states a unit it can be counted in.
    if (outcome.getSrfTargetUnit() != null && outcome.getSrfTargetUnit().getId() != null
            && outcome.getSrfTargetUnit().getId().longValue() != -1) {
        if (outcome.getValue() == null || !this.isValidNumber(outcome.getValue().toString())) {
            action.addMessage(action.getText("outcome.action.value.required", params));
            action.getInvalidFields().put(prefix + ".value", InvalidFieldsMessages.EMPTYFIELD);
        }
    }

    // 4. Target unit is deliberately not reported as missing. Id -1 is this select's header option
    // and it is labelled "Not Applicable" on purpose (outcome.selectTargetUnit.placeholder); the
    // section's own instructions (outcomes.help) tell the user to pick it when the indicator has no
    // quantifiable target, so it is an answer. A null unit is read the same way rather than as a
    // gap: the form renders null and -1 as the very same selected option, so reporting only null
    // asked the user to fix a field that already showed a valid answer, with nothing on screen left
    // to change. Saving the row normalises it to -1. That leaves this field unable to be reported
    // missing at all -- see REVIEW.md: whether "Not Applicable" should stay on offer is a product
    // question, not a defect to patch here.

    // 5. Portfolio
    if (action.hasSpecificities(APConstants.PORTFOLIO_FEATURE_ACTIVE)) {
        if (outcome.getPortfolio() == null || outcome.getPortfolio().getId() == null
                || outcome.getPortfolio() != null && outcome.getPortfolio().getId() == -1) {
            action.addMessage(action.getText("outcome.action.portfolio.required", params));
            action.getInvalidFields().put(prefix + ".portfolio.id", InvalidFieldsMessages.EMPTYFIELD);
        }
    }

    // 6. Closing year. The baseline (start) year is optional and deliberately not checked:
    // the block that used to stand here read getYear() instead of getStartYear(), so it
    // never validated the baseline at all and only doubled this message.
    if (!this.isValidNumber(String.valueOf(outcome.getYear())) || (outcome.getYear() <= 0)) {
        action.addMessage(action.getText("outcome.action.year.required", params));
        action.getInvalidFields().put(prefix + ".year", InvalidFieldsMessages.EMPTYFIELD);
    }

    int year = action.getCurrentCycleYear();
    
    // 7. Period targets
    if (outcome.getMilestones() != null && !outcome.getMilestones().isEmpty()) {
        for (int j = 0; j < outcome.getMilestones().size(); j++) {
            outcome.getMilestones().get(j).setCrpProgramOutcome(outcome);
            
            this.validateMilestone(action, outcome.getMilestones().get(j), i, j);
        }
    } else {
        // The key was being added as the message itself, so the raw key reached the user.
        action.addMessage(action.getText("outcome.action.milestones.required", params));
        action.getInvalidFields().put(listPrefix + ".milestones",
                action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] { "Milestones" }));
    }

    // 8. Sub-IDOs
    if (!action.isAiccra()) {
        if (outcome.getSubIdos() != null) {
            if (outcome.getSubIdos().isEmpty()) {
                action.addMessage(action.getText("outcome.action.subido.requeried", params));
                // CAMBIO: Usamos outcomesForm
                action.getInvalidFields().put(listPrefix + ".subIdos",
                        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] { "Sub Idos" }));
            }
            double contributions = 0;
            boolean hasPrimarySubIDO = false;
            for (int j = 0; j < outcome.getSubIdos().size(); j++) {
                outcome.getSubIdos().get(j).setCrpProgramOutcome(outcome);
                
                // ¡OJO AQUÍ! Debes entrar a este método también
                this.validateSubIDO(action, outcome.getSubIdos().get(j), i, j);
                
                if (outcome.getSubIdos().get(j).getContribution() != null) {
                    contributions = contributions + outcome.getSubIdos().get(j).getContribution().doubleValue();
                }
                if (outcome.getSubIdos().get(j).getPrimary() != null && outcome.getSubIdos().get(j).getPrimary() == true) {
                    hasPrimarySubIDO = true;
                }
            }
            if (hasPrimarySubIDO == false) {
                action.addMessage(action.getText("outcome.action.primary.required"));
                // CAMBIO: Usamos outcomesForm
                action.getInvalidFields().put(listPrefix + ".subIdos",
                        action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] { "Primary sub IDO" }));
            }

            if (contributions != 100) {
                action.addMessage(action.getText("outcome.action.subido.contribution.required", params));

                for (int j = 0; j < outcome.getSubIdos().size(); j++) {
                    // CAMBIO: Usamos outcomesForm
                    action.getInvalidFields().put(prefix + ".subIdos[" + j + "].contribution",
                            InvalidFieldsMessages.EMPTYFIELD);
                }
            }
        } else {
            action.addMessage(action.getText("outcome.action.subido.requeried", params));
            // CAMBIO: Usamos outcomesForm
            action.getInvalidFields().put(listPrefix + ".subIdos",
                    action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] { "Sub Idos" }));
        }
    }
  }

  // metodo original
  // public void validateOuctome(BaseAction action, CrpProgramOutcome outcome, int i) {
  //   List<String> params = new ArrayList<String>();
  //   params.add(String.valueOf(i + 1));

  //   if (outcome == null) {
  //       return;
  //   }

  //   if (!(this.isValidString(outcome.getDescription()) && this.wordCount(outcome.getDescription()) <= 100)) {
  //     action.addMessage(action.getText("outcome.action.statement.required", params));
  //     action.getInvalidFields().put("input-outcomes[" + i + "].description", InvalidFieldsMessages.EMPTYFIELD);


  //   }


  //   if (outcome.getSrfTargetUnit() != null && outcome.getSrfTargetUnit().getId() != null
  //     && outcome.getSrfTargetUnit().getId().longValue() != -1) {
  //     if (outcome.getValue() == null || !this.isValidNumber(outcome.getValue().toString())) {
  //       action.addMessage(action.getText("outcome.action.value.required", params));
  //       action.getInvalidFields().put("input-outcomes[" + i + "].value", InvalidFieldsMessages.EMPTYFIELD);
  //     }
  //   }

  //   if (outcome.getPortfolio() == null || outcome.getPortfolio().getId() == null
  //     || outcome.getPortfolio().getId() == -1) {
  //     action.addMessage(action.getText("outcome.action.portfolio.required", params));
  //     action.getInvalidFields().put("input-outcomes[" + i + "].portfolio", InvalidFieldsMessages.EMPTYFIELD);
  //   }

  //   if (!this.isValidNumber(String.valueOf(outcome.getYear())) || (outcome.getYear() <= 0)) {
  //     action.addMessage(action.getText("outcome.action.startYear.required", params));
  //     action.getInvalidFields().put("input-outcomes[" + i + "].startYear", InvalidFieldsMessages.EMPTYFIELD);
  //   }

  //   if (!this.isValidNumber(String.valueOf(outcome.getYear())) || (outcome.getYear() <= 0)) {
  //     action.addMessage(action.getText("outcome.action.year.required", params));
  //     action.getInvalidFields().put("input-outcomes[" + i + "].year", InvalidFieldsMessages.EMPTYFIELD);
  //   }

  //   /*
  //    * if (outcome.getSrfTargetUnit() == null || outcome.getSrfTargetUnit().getId() == -1) {
  //    * outcome.setSrfTargetUnit(null);
  //    * action.addMessage(action.getText("outcome.action.srfTargetUnit.required", params));
  //    * action.getInvalidFields().put("input-outcomes[" + i + "].srfTargetUnit.id", InvalidFieldsMessages.EMPTYFIELD);
  //    * }
  //    */
  //   int year = action.getCurrentCycleYear();
  //   if (outcome.getMilestones() != null && !outcome.getMilestones().isEmpty()) {
  //     for (int j = 0; j < outcome.getMilestones().size(); j++) {
  //       outcome.getMilestones().get(j).setCrpProgramOutcome(outcome);

  //       this.validateMilestone(action, outcome.getMilestones().get(j), i, j);

  //     }
  //   } else {
  //     action.addMessage("outcome.action.milestones.requeried");

  //     action.getInvalidFields().put("list-outcomes[" + i + "].milestones",
  //       action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Milestones"}));
  //   }

  //   if (!action.isAiccra()) {
  //     if (outcome.getSubIdos() != null) {
  //       if (outcome.getSubIdos().isEmpty()) {
  //         action.addMessage(action.getText("outcome.action.subido.requeried", params));
  //         action.getInvalidFields().put("list-outcomes[" + i + "].subIdos",
  //           action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Sub Idos"}));
  //       }
  //       double contributions = 0;
  //       boolean hasPrimarySubIDO = false;
  //       for (int j = 0; j < outcome.getSubIdos().size(); j++) {
  //         outcome.getSubIdos().get(j).setCrpProgramOutcome(outcome);
  //         this.validateSubIDO(action, outcome.getSubIdos().get(j), i, j);
  //         if (outcome.getSubIdos().get(j).getContribution() != null) {
  //           contributions = contributions + outcome.getSubIdos().get(j).getContribution().doubleValue();
  //         }
  //         if (outcome.getSubIdos().get(j).getPrimary() != null && outcome.getSubIdos().get(j).getPrimary() == true) {
  //           hasPrimarySubIDO = true;
  //         }
  //       }
  //       if (hasPrimarySubIDO == false) {
  //         action.addMessage(action.getText("outcome.action.primary.required"));
  //         action.getInvalidFields().put("list-outcomes[" + i + "].subIdos",
  //           action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Primary sub IDO"}));
  //       }

  //       if (contributions != 100) {
  //         action.addMessage(action.getText("outcome.action.subido.contribution.required", params));


  //         for (int j = 0; j < outcome.getSubIdos().size(); j++) {
  //           action.getInvalidFields().put("input-outcomes[" + i + "].subIdos[" + j + "].contribution",
  //             InvalidFieldsMessages.EMPTYFIELD);
  //         }


  //       }
  //     } else {
  //       action.addMessage(action.getText("outcome.action.subido.requeried", params));

  //       action.getInvalidFields().put("list-outcomes[" + i + "].subIdos",
  //         action.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Sub Idos"}));
  //     }
  //   }


  // }

  public void validateSubIDO(BaseAction action, CrpOutcomeSubIdo subIdo, int i, int j) {

    List<String> params = new ArrayList<String>();
    params.add(String.valueOf(i + 1));
    params.add(String.valueOf(j + 1));
    if (subIdo.getSrfSubIdo() == null || subIdo.getSrfSubIdo().getId() == null || subIdo.getSrfSubIdo().getId() == -1) {
      subIdo.setSrfSubIdo(null);
      action.addMessage(action.getText("outcome.action.subido.subido.required", params));
      action.getInvalidFields().put("input-outcomesForm[" + i + "].subIdos[" + j + "].srfSubIdo.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    if (subIdo.getContribution() == null || !this.isValidNumber(subIdo.getContribution().toString())
      || subIdo.getContribution().doubleValue() > 100) {
      action.addMessage(action.getText("outcome.action.subido.contribution.required", params));
      action.getInvalidFields().put("input-outcomesForm[" + i + "].subIdos[" + j + "].contribution",
        InvalidFieldsMessages.EMPTYFIELD);
    }
    /*
     * int k = 0;
     * if (subIdo.getAssumptions() != null) {
     * for (CrpAssumption crpAssumption : subIdo.getAssumptions()) {
     * this.validateAssumption(action, crpAssumption, i, j, k);
     * k++;
     * }
     * }
     */
  }
}
