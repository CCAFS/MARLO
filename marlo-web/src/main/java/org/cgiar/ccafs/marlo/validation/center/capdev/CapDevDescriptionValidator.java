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
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class CapDevDescriptionValidator extends BaseValidator {

  @Inject
  public CapDevDescriptionValidator() {

  }

  public void validate(BaseAction baseAction, CapacityDevelopment capdev, List<Long> disciplines,
    List<Long> targetGroups, List<Long> partners, List<Long> outputs) {

    baseAction.setInvalidFields(new HashMap<>());

    if (!baseAction.getFieldErrors().isEmpty()) {
      baseAction.addActionError(baseAction.getText("saving.fields.required"));
    }

    this.validateCapdevDescription(baseAction, capdev, disciplines, targetGroups, partners, outputs);
  }

  public void validateCapdevDescription(BaseAction baseAction, CapacityDevelopment capdev, List<Long> disciplines,
    List<Long> targetGroups, List<Long> partners, List<Long> outputs) {

    // remueve el elemento por defecto de la lista de disciplinas cuando no es seleccionada ninguna
    if (disciplines.get(0) == -1) {
      disciplines.remove(0);
    }
    if (disciplines.isEmpty()
      && capdev.getCapdevDiscipline().stream().filter(cd -> cd.isActive()).collect(Collectors.toList()).isEmpty()) {
      this.addMessage(baseAction.getText("capdev.action.disciplines"));
      baseAction.getInvalidFields().put("list-capdev.disciplines",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Disciplines"}));
    }
    // remueve el elemento por defecto de la lista de target groups cuando no es seleccionada ninguno
    if (targetGroups.get(0) == -1) {
      targetGroups.remove(0);
    }
    if (targetGroups.isEmpty()
      && capdev.getCapdevTargetgroups().stream().filter(cd -> cd.isActive()).collect(Collectors.toList()).isEmpty()) {
      this.addMessage(baseAction.getText("capdev.action.targetgroup"));
      baseAction.getInvalidFields().put("list-capdev.targetgroup",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Target Groups"}));
    }

    // remueve el elemento por defecto de la lista de partners cuando no es seleccionada ninguno
    if (partners.get(0) == -1) {
      partners.remove(0);
    }
    if (partners.isEmpty()
      && capdev.getCapdevPartners().stream().filter(cd -> cd.isActive()).collect(Collectors.toList()).isEmpty()) {
      this.addMessage(baseAction.getText("capdev.action.partners"));
      baseAction.getInvalidFields().put("list-capdev.partners",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Partners"}));

    }

    if (capdev.getResearchArea().getId() == -1) {
      this.addMessage(baseAction.getText("capdev.action.researchArea"));
      baseAction.getInvalidFields().put("input-capdev.researchArea.id", InvalidFieldsMessages.EMPTYFIELD);
      // baseAction.getInvalidFields().put("list-capdev.researcharea",
      // baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Research Area"}));
    }

    if (outputs.isEmpty()
      && capdev.getCapdevOutputs().stream().filter(cd -> cd.isActive()).collect(Collectors.toList()).isEmpty()) {
      this.addMessage(baseAction.getText("capdev.action.outputs"));
      baseAction.getInvalidFields().put("list-capdev.outputs",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Outputs"}));
    }


  }

}
