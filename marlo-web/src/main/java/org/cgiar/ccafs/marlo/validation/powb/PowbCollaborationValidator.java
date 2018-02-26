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
import org.cgiar.ccafs.marlo.data.manager.PowbSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.PowbCollaborationGlobalUnit;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesis;
import org.cgiar.ccafs.marlo.data.model.PowbSynthesisSectionStatusEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.inject.Named;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class PowbCollaborationValidator extends BaseValidator {

  private final GlobalUnitManager crpManager;
  private final PowbSynthesisManager powbSynthesisManager;


  public PowbCollaborationValidator(GlobalUnitManager crpManager, PowbSynthesisManager powbSynthesisManager) {
    super();
    this.crpManager = crpManager;
    this.powbSynthesisManager = powbSynthesisManager;
  }

  private Path getAutoSaveFilePath(PowbSynthesis powbSynthesis, long crpID, BaseAction baseAction) {

    GlobalUnit crp = crpManager.getGlobalUnitById(crpID);
    String composedClassName = powbSynthesis.getClass().getSimpleName();
    String actionFile = PowbSynthesisSectionStatusEnum.COLLABORATION.getStatus().replace("/", "_");
    String autoSaveFile =
      powbSynthesis.getId() + "_" + composedClassName + "_" + baseAction.getActualPhase().getDescription() + "_"
        + baseAction.getActualPhase().getYear() + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public LiaisonInstitution getLiaisonInstitution(BaseAction action, long powbSynthesisID) {
    PowbSynthesis powbSynthesis = powbSynthesisManager.getPowbSynthesisById(powbSynthesisID);
    LiaisonInstitution liaisonInstitution = powbSynthesis.getLiaisonInstitution();
    return liaisonInstitution;
  }

  public boolean isPMU(LiaisonInstitution liaisonInstitution) {
    boolean isFP = false;
    if (liaisonInstitution != null) {
      if (liaisonInstitution.getCrpProgram() == null) {
        isFP = true;
      }
    }
    return isFP;
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
      if (!(this.isValidString(powbSynthesis.getCollaboration().getKeyExternalPartners())
        && this.wordCount(powbSynthesis.getCollaboration().getKeyExternalPartners()) <= 100)) {
        action.addMissingField(action.getText("powbSynthesis.collaboration.keyExternalPartners"));
        action.getInvalidFields().put("input-powbSynthesis.collaboration.keyExternalPartners",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!(this.isValidString(powbSynthesis.getCollaboration().getCotributionsPlatafforms())
        && this.wordCount(powbSynthesis.getCollaboration().getCotributionsPlatafforms()) <= 100)) {
        action.addMissingField(action.getText("powbSynthesis.collaboration.cotributionsPlatafforms"));
        action.getInvalidFields().put("input-powbSynthesis.collaboration.cotributionsPlatafforms",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!(this.isValidString(powbSynthesis.getCollaboration().getEffostornCountry())
        && this.wordCount(powbSynthesis.getCollaboration().getEffostornCountry()) <= 100)) {
        action.addMissingField(action.getText("powbSynthesis.collaboration.effostornCountry"));
        action.getInvalidFields().put("input-powbSynthesis.collaboration.effostornCountry",
          InvalidFieldsMessages.EMPTYFIELD);
      }
      if (!(this.isValidString(powbSynthesis.getCollaboration().getCrossCrp())
        && this.wordCount(powbSynthesis.getCollaboration().getCrossCrp()) <= 100)) {
        action.addMissingField(action.getText("powbSynthesis.collaboration.crossCrp"));
        action.getInvalidFields().put("input-powbSynthesis.collaboration.crossCrp", InvalidFieldsMessages.EMPTYFIELD);
      }

      if (!action.getFieldErrors().isEmpty()) {
        action.addActionError(action.getText("saving.fields.required"));
      } else if (action.getValidationMessage().length() > 0) {
        action.addActionMessage(
          " " + action.getText("saving.missingFields", new String[] {action.getValidationMessage().toString()}));
      }
      this.validetGlobalUnit(action, powbSynthesis);

      this.saveMissingFields(powbSynthesis, action.getActualPhase().getDescription(), action.getActualPhase().getYear(),
        PowbSynthesisSectionStatusEnum.COLLABORATION.getStatus(), action);
    }
  }

  public void validetGlobalUnit(BaseAction action, PowbSynthesis powbSynthesis) {
    if (powbSynthesis.getPowbCollaborationGlobalUnitsList() != null) {
      int i = 0;
      for (PowbCollaborationGlobalUnit powbCollaborationGlobalUnit : powbSynthesis
        .getPowbCollaborationGlobalUnitsList()) {
        if (powbCollaborationGlobalUnit.getGlobalUnit() != null
          && powbCollaborationGlobalUnit.getGlobalUnit().getId() > 0) {
          powbCollaborationGlobalUnit
            .setGlobalUnit(crpManager.getGlobalUnitById(powbCollaborationGlobalUnit.getGlobalUnit().getId()));
        } else {
          powbCollaborationGlobalUnit.setGlobalUnit(null);

        }
        if (!(this.isValidString(powbCollaborationGlobalUnit.getFlagship())
          && this.wordCount(powbCollaborationGlobalUnit.getFlagship()) <= 10)) {
          action.addMissingField(action.getText("powbSynthesis.powbCollaborationGlobalUnitsList[" + i + "].flagship"));
          action.getInvalidFields().put("input-powbSynthesis.powbCollaborationGlobalUnitsList[" + i + "].flagship",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        if (!(this.isValidString(powbCollaborationGlobalUnit.getCollaborationType()))) {
          action.addMissingField(
            action.getText("powbSynthesis.powbCollaborationGlobalUnitsList[" + i + "].collaborationType"));
          action.getInvalidFields().put(
            "input-powbSynthesis.powbCollaborationGlobalUnitsList[" + i + "].collaborationType",
            InvalidFieldsMessages.EMPTYFIELD);
        }
        if (!(this.isValidString(powbCollaborationGlobalUnit.getBrief())
          && this.wordCount(powbCollaborationGlobalUnit.getBrief()) <= 100)) {
          action.addMissingField(action.getText("powbSynthesis.powbCollaborationGlobalUnitsList[" + i + "].brief"));
          action.getInvalidFields().put("input-powbSynthesis.powbCollaborationGlobalUnitsList[" + i + "].brief",
            InvalidFieldsMessages.EMPTYFIELD);
        }
        if (powbCollaborationGlobalUnit.getGlobalUnit() == null) {
          action
            .addMissingField(action.getText("powbSynthesis.powbCollaborationGlobalUnitsList[" + i + "].globalUnit.id"));
          action.getInvalidFields().put("input-powbSynthesis.powbCollaborationGlobalUnitsList[" + i + "].globalUnit.id",
            InvalidFieldsMessages.EMPTYFIELD);
        }

        i++;

      }

    } else {

    }
  }

}
