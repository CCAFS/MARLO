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
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.PowbExpectedCrpProgress;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis2019SectionStatusEnum;
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
public class ProgressOutcomesValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final LiaisonInstitutionManager liaisonInstitutionManager;

  public ProgressOutcomesValidator(GlobalUnitManager crpManager, LiaisonInstitutionManager liaisonInstitutionManager) {
    super();
    this.crpManager = crpManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
  }

  private Path getAutoSaveFilePath(PowbSynthesis powbSynthesis, long crpID, BaseAction baseAction) {
    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = powbSynthesis.getClass().getSimpleName();
    String actionFile = PowbSynthesis2019SectionStatusEnum.PROGRESS_OUTCOMES.getStatus().replace("/", "_");
    String autoSaveFile = powbSynthesis.getId() + "_" + composedClassName + "_" + baseAction.getActualPhase().getName()
      + "_" + baseAction.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

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

      this.validateProgress(action, powbSynthesis);

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }

      this.saveMissingFields(powbSynthesis, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
        action.getActualPhase().getUpkeep(), PowbSynthesis2019SectionStatusEnum.PROGRESS_OUTCOMES.getStatus(), action);
    }

  }

  public void validateProgress(BaseAction action, PowbSynthesis powbSynthesis) {
    if (!(this.isValidString(powbSynthesis.getExpectedProgressNarrative()))) {
      action.addMessage(action.getText("Expected Progress Narrative"));
      action.getInvalidFields().put("input-powbSynthesis.expectedProgressNarrative", InvalidFieldsMessages.EMPTYFIELD);
    }
  }


}
