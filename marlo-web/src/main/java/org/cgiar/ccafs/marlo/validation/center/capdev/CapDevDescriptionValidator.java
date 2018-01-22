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

package org.cgiar.ccafs.marlo.validation.center.capdev;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.HashMap;

import javax.inject.Inject;


public class CapDevDescriptionValidator extends BaseValidator {

  @Inject
  public CapDevDescriptionValidator() {

  }

  public void validate(BaseAction baseAction, CapacityDevelopment capdev) {

    baseAction.setInvalidFields(new HashMap<>());

    if (!baseAction.getFieldErrors().isEmpty()) {
      baseAction.addActionError(baseAction.getText("saving.fields.required"));
    }

    this.validateCapdevDescription(baseAction, capdev);
    this.saveMissingFields(capdev, "descriptionCapdev");
  }

  public void validateCapdevDescription(BaseAction baseAction, CapacityDevelopment capdev) {

    if (capdev.getCapdevDisciplineList() == null) {
      this.addMessage(baseAction.getText("capdev.action.disciplines"));
      baseAction.getInvalidFields().put("list-capdev.disciplines",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Disciplines"}));
    }

    if (capdev.getCapdevDisciplineList() != null) {
      if (capdev.getCapdevDisciplineList().isEmpty()) {
        this.addMessage(baseAction.getText("capdev.action.disciplines"));
        baseAction.getInvalidFields().put("list-capdev.disciplines",
          baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Disciplines"}));
      }
    }

    if (capdev.getCapdevTargetGroupList() == null) {
      this.addMessage(baseAction.getText("capdev.action.targetgroup"));
      baseAction.getInvalidFields().put("list-capdev.targetgroup",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Target Groups"}));
    }

    if (capdev.getCapdevTargetGroupList() != null) {
      if (capdev.getCapdevTargetGroupList().isEmpty()) {
        this.addMessage(baseAction.getText("capdev.action.targetgroup"));
        baseAction.getInvalidFields().put("list-capdev.targetgroup",
          baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Target Groups"}));
      }
    }

    if (capdev.getCapdevPartnersList() == null) {
      this.addMessage(baseAction.getText("capdev.action.partners"));
      baseAction.getInvalidFields().put("list-capdev.partners",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Partners"}));

    }


    if (capdev.getCapdevPartnersList() != null) {
      if (capdev.getCapdevPartnersList().isEmpty()) {
        this.addMessage(baseAction.getText("capdev.action.partners"));
        baseAction.getInvalidFields().put("list-capdev.partners",
          baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Partners"}));

      }

    }


    if (capdev.getResearchArea() == null) {
      this.addMessage(baseAction.getText("capdev.action.researchArea"));
      baseAction.getInvalidFields().put("input-capdev.researchArea.id", InvalidFieldsMessages.EMPTYFIELD);
      // baseAction.getInvalidFields().put("list-capdev.researcharea",
      // baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Research Area"}));

    }


    if (capdev.getCapdevOutputsList() == null) {
      this.addMessage(baseAction.getText("capdev.action.outputs"));
      baseAction.getInvalidFields().put("list-capdev.outputs",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Outputs"}));
    }

    if (capdev.getCapdevOutputsList() != null) {
      if (capdev.getCapdevOutputsList().isEmpty()) {
        this.addMessage(baseAction.getText("capdev.action.outputs"));
        baseAction.getInvalidFields().put("list-capdev.outputs",
          baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Outputs"}));
      }
    }


  }

}
