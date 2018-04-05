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

package org.cgiar.ccafs.marlo.validation.center.impactpathway;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.model.CenterImpact;
import org.cgiar.ccafs.marlo.data.model.CenterImpactObjective;
import org.cgiar.ccafs.marlo.data.model.CenterOutcome;
import org.cgiar.ccafs.marlo.data.model.CenterOutput;
import org.cgiar.ccafs.marlo.data.model.CenterTopic;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;

import com.opensymphony.xwork2.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class CenterImpactPathwaySectionValidation<T extends BaseAction> extends BaseValidator {

  // Managers
  private CrpProgramManager programServcie;

  // Validator
  private OutcomesValidator outcomeValidator;
  private OutputsValidator outputValidator;
  private ProgramImpactsValidator impactValidator;
  private ResearchTopicsValidator topicValidator;

  @Inject
  public CenterImpactPathwaySectionValidation(CrpProgramManager programServcie, OutcomesValidator outcomeValidator,
    OutputsValidator outputValidator, ProgramImpactsValidator impactValidator, ResearchTopicsValidator topicValidator) {
    this.programServcie = programServcie;
    this.outcomeValidator = outcomeValidator;
    this.outputValidator = outputValidator;
    this.impactValidator = impactValidator;
    this.topicValidator = topicValidator;


  }

  public void validateImpact(BaseAction action, long crpProgramID) {
    CrpProgram program = programServcie.getCrpProgramById(crpProgramID);

    if (program != null) {
      List<CenterImpact> impacts =
        new ArrayList<>(program.getResearchImpacts().stream().filter(ri -> ri.isActive()).collect(Collectors.toList()));

      for (CenterImpact researchImpact : impacts) {
        researchImpact.setObjectives(new ArrayList<>());
        if (researchImpact.getResearchImpactObjectives() != null) {
          for (CenterImpactObjective impactObjective : researchImpact.getResearchImpactObjectives().stream()
            .filter(ro -> ro.isActive()).collect(Collectors.toList())) {
            researchImpact.getObjectives().add(impactObjective.getResearchObjective());
            if (researchImpact.getObjectiveValue() == null) {
              researchImpact.setObjectiveValue(impactObjective.getResearchObjective().getId().toString());
            } else {
              researchImpact.setObjectiveValue(
                researchImpact.getObjectiveValue() + "," + impactObjective.getResearchObjective().getId().toString());
            }
          }
        }

        researchImpact.setBeneficiaries(new ArrayList<>(researchImpact.getResearchImpactBeneficiaries().stream()
          .filter(rib -> rib.isActive()).collect(Collectors.toList())));
      }

      impactValidator.validate(action, impacts, program, false);
    }
  }

  public void validateOutcome(BaseAction action, long crpProgramID) {
    CrpProgram program = programServcie.getCrpProgramById(crpProgramID);

    if (program != null) {
      List<CenterTopic> topics =
        new ArrayList<>(program.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList()));
      if (topics != null) {
        for (CenterTopic researchTopic : topics) {
          List<CenterOutcome> outcomes = new ArrayList<>(
            researchTopic.getResearchOutcomes().stream().filter(ro -> ro.isActive()).collect(Collectors.toList()));

          for (CenterOutcome researchOutcome : outcomes) {
            researchOutcome.setMilestones(new ArrayList<>(researchOutcome.getResearchMilestones().stream()
              .filter(rm -> rm.isActive()).collect(Collectors.toList())));

            outcomeValidator.validate(action, researchOutcome, program, false);
          }
        }
      }
    }
  }

  public void validateOutput(BaseAction action, long crpProgramID) {

    CrpProgram program = programServcie.getCrpProgramById(crpProgramID);

    if (program != null) {
      List<CenterOutput> outputs =
        new ArrayList<>(program.getCenterOutputs().stream().filter(op -> op.isActive()).collect(Collectors.toList()));

      for (CenterOutput researchOutput : outputs) {

        researchOutput.setNextUsers(new ArrayList<>(researchOutput.getResearchOutputsNextUsers().stream()
          .filter(nu -> nu.isActive()).collect(Collectors.toList())));

        researchOutput.setOutcomes(new ArrayList<>(
          researchOutput.getCenterOutputsOutcomes().stream().filter(op -> op.isActive()).collect(Collectors.toList())));

        outputValidator.validate(action, researchOutput, program, false);
      }
    }

  }


  public void validateTopic(BaseAction action, long crpProgramID) {
    CrpProgram program = programServcie.getCrpProgramById(crpProgramID);

    if (program != null) {
      List<CenterTopic> topics =
        new ArrayList<>(program.getResearchTopics().stream().filter(rt -> rt.isActive()).collect(Collectors.toList()));

      topicValidator.validate(action, topics, program, false);
    }
  }

}
