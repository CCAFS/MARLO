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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.PowbExpectedCrpProgress;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.data.model.ProgramType;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Named;

/**
 * @author Christian Garcia- CIAT/CCAFS
 */
@Named
public class ExpectedCRPProgressValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final LiaisonInstitutionManager liaisonInstitutionManager;

  public ExpectedCRPProgressValidator(GlobalUnitManager crpManager,
    LiaisonInstitutionManager liaisonInstitutionManager) {
    super();
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
  }

  private Path getAutoSaveFilePath(PowbSynthesis powbSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = powbSynthesis.getClass().getSimpleName();
    String actionFile = PowbSynthesisSectionStatusEnum.CRP_PROGRESS.getStatus().replace("/", "_");
    String autoSaveFile =
      powbSynthesis.getId() + "_" + composedClassName + "_" + baseAction.getActualPhase().getDescription() + "_"
        + baseAction.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public int getIndex(Long crpMilestoneID, PowbSynthesis powbSynthesis) {
    if (powbSynthesis.getExpectedCrpProgresses() != null) {
      int i = 0;
      for (PowbExpectedCrpProgress powbExpectedCrpProgress : powbSynthesis.getExpectedCrpProgresses()) {
        if (powbExpectedCrpProgress != null) {
          if (powbExpectedCrpProgress.getCrpMilestone().getId().longValue() == crpMilestoneID.longValue()) {
            return i;
          }

        }
        i++;
      }


    } else

    {
      powbSynthesis.setExpectedCrpProgresses(new ArrayList<>());
    }


    return -1;

  }

  public void validate(BaseAction action, PowbSynthesis powbSynthesis, boolean saving) {
    action.setInvalidFields(new HashMap<>());
    if (powbSynthesis != null) {
      if (!saving) {
        Path path = this.getAutoSaveFilePath(powbSynthesis, action.getCrpID(), action);

        if (path.toFile().exists()) {
          action.addMissingField("draft");
        }
      }

      LiaisonInstitution liaisonInstitution =
        liaisonInstitutionManager.getLiaisonInstitutionById(powbSynthesis.getLiaisonInstitution().getId());
      if (liaisonInstitution.getCrpProgram() != null) {
        CrpProgram crpProgram = liaisonInstitution.getCrpProgram();
        if (crpProgram.getProgramType() == ProgramType.FLAGSHIP_PROGRAM_TYPE.getValue()) {
          if (powbSynthesis.getExpectedCrpProgresses() != null) {
            if (!powbSynthesis.getExpectedCrpProgresses().isEmpty()) {
              for (PowbExpectedCrpProgress powbExpectedCrpProgress : powbSynthesis.getExpectedCrpProgresses()) {
                if (powbExpectedCrpProgress != null && powbExpectedCrpProgress.getCrpMilestone() != null
                  && powbExpectedCrpProgress.getCrpMilestone().getId() != null) {
                  int i = this.getIndex(powbExpectedCrpProgress.getCrpMilestone().getId().longValue(), powbSynthesis);
                  if (!(this.isValidString(powbExpectedCrpProgress.getMeans()))) {
                    action.addMissingField(action.getText("powbSynthesis.expectedCrpProgresses[" + i + "].means"));
                    action.getInvalidFields().put("input-powbSynthesis.expectedCrpProgresses[" + i + "].means",
                      InvalidFieldsMessages.EMPTYFIELD);
                  }

                  if (!this.isValidString(powbExpectedCrpProgress.getAssessment())) {
                    action.addMissingField(action.getText("powbSynthesis.expectedCrpProgresses[" + i + "].assessment"));
                    action.getInvalidFields().put("input-powbSynthesis.expectedCrpProgresses[" + i + "].assessment",
                      InvalidFieldsMessages.EMPTYFIELD);
                  }
                }
              }
            } else {
              action.addMissingField(action.getText("Not Expected Crp Progress"));
            }
          } else {
            action.addMissingField(action.getText("Not Expected Crp Progress"));
          }
        }
      } else {
        if (powbSynthesis.getExpectedCrpProgresses() != null) {
          if (!powbSynthesis.getExpectedCrpProgresses().isEmpty()) {
            int i = 0;
            for (PowbExpectedCrpProgress powbExpectedCrpProgress : powbSynthesis.getExpectedCrpProgresses()) {
              if (!(this.isValidString(powbExpectedCrpProgress.getExpectedHighlights()))) {
                action.addMissingField(action.getText("powbSynthesis.expectedCrpProgresses.expectedHighlights"));
                action.getInvalidFields().put("input-powbSynthesis.expectedCrpProgresses[" + i + "].expectedHighlights",
                  InvalidFieldsMessages.EMPTYFIELD);
              }

              i++;
            }
          } else {
            action.addMissingField(action.getText("Not Expected Crp Progress"));
          }
        } else {
          action.addMissingField(action.getText("Not Expected Crp Progress"));
        }
      }

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(powbSynthesis, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
        PowbSynthesisSectionStatusEnum.CRP_PROGRESS.getStatus(), action);
    }

  }


}
