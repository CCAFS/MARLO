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

package org.cgiar.ccafs.marlo.validation.bilaterals;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.ProjectBilateralCofinancing;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CofundedValidator extends BaseValidator {

  private BaseAction action;

  @Inject
  public CofundedValidator() {
  }

  public void validate(BaseAction action, ProjectBilateralCofinancing project) {

    this.action = action;

    if (!(this.isValidString(project.getTitle()) && this.wordCount(project.getTitle()) <= 15)) {
      this.addMessage(action.getText("projectCofunded.title"));
    }

    if (project.getStartDate() == null) {
      this.addMessage(action.getText("projectCofunded.startDate"));
    }
    if (project.getEndDate() == null) {
      this.addMessage(action.getText("projectCofunded.endDate"));
    }

    if (!(this.isValidString(project.getFinanceCode()) && this.wordCount(project.getFinanceCode()) <= 15)) {
      this.addMessage(action.getText("projectCofunded.financeCode"));
    }

    if (!(this.isValidString(project.getContactPersonEmail())
      && this.wordCount(project.getContactPersonEmail()) <= 25)) {
      this.addMessage(action.getText("projectCofunded.contactEmail"));
    }

    if (!(this.isValidNumber(String.valueOf(project.getBudget())))) {
      this.addMessage(action.getText("projectCofunded.budgetAgreementPeriod"));
    }

    if (!(this.isValidString(project.getContactPersonName()) && this.wordCount(project.getContactPersonName()) <= 25)) {
      this.addMessage(action.getText("projectCofunded.contactName"));
    }

    if (project.getLiaisonInstitution() != null) {
      if (project.getLiaisonInstitution().getId() == -1) {
        this.addMessage(action.getText("projectCofunded.leadCenter"));
      }
    } else {
      this.addMessage(action.getText("projectCofunded.leadCenter"));
    }

    if (project.getInstitution() != null) {
      if (project.getInstitution().getId() == -1) {
        this.addMessage(action.getText("projectCofunded.donor"));
      }
    } else {
      this.addMessage(action.getText("projectCofunded.donor"));
    }


    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }
    if (action.isReportingActive()) {
      this.saveMissingFields(project, APConstants.REPORTING, action.getPlanningYear(), "cofunded");
    } else {
      this.saveMissingFields(project, APConstants.PLANNING, action.getPlanningYear(), "cofunded");
    }

  }

}
