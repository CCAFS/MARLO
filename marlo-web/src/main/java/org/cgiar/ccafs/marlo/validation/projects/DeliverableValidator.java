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

package org.cgiar.ccafs.marlo.validation.projects;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableValidator extends BaseValidator {

  BaseAction action;

  @Inject
  public DeliverableValidator() {
  }

  public void validate(BaseAction action, Deliverable deliverable) {
    this.action = action;

    if (!(this.isValidString(deliverable.getTitle()) && this.wordCount(deliverable.getTitle()) <= 15)) {
      this.addMessage(action.getText("project.deliverable.generalInformation.title"));
    }

    if (!(this.isValidString(deliverable.getStatusDescription())
      && this.wordCount(deliverable.getStatusDescription()) <= 15)) {
      this.addMessage(action.getText("project.deliverable.generalInformation.description"));
    }

    if (deliverable.getDeliverableType().getDeliverableType() != null) {
      if (deliverable.getDeliverableType().getDeliverableType().getId() == -1) {
        this.addMessage(action.getText("project.deliverable.generalInformation.type"));
      }
    } else {
      this.addMessage(action.getText("project.deliverable.generalInformation.type"));
    }

    if (deliverable.getDeliverableType() != null) {
      if (deliverable.getDeliverableType().getId() == -1) {
        this.addMessage(action.getText("project.deliverable.generalInformation.subType"));
      }
    } else {
      this.addMessage(action.getText("project.deliverable.generalInformation.subType"));
    }

    if (deliverable.getStatus() != null) {
      if (deliverable.getStatus() == -1) {
        this.addMessage(action.getText("project.deliverable.generalInformation.status"));
      }
    } else {
      this.addMessage(action.getText("project.deliverable.generalInformation.status"));
    }


    if (deliverable.getYear() == -1) {
      this.addMessage(action.getText("project.deliverable.generalInformation.year"));
    }

    if (deliverable.getCrpProgramOutcome() != null) {
      if (deliverable.getCrpProgramOutcome().getId() == -1) {
        this.addMessage(action.getText("project.deliverable.generalInformation.outcome"));
      }
    } else {
      this.addMessage(action.getText("project.deliverable.generalInformation.outcome"));
    }

    if (deliverable.getCrpClusterKeyOutput() != null) {
      if (deliverable.getCrpClusterKeyOutput().getId() == -1) {
        this.addMessage(action.getText("project.deliverable.generalInformation.keyOutput"));
      }
    } else {
      this.addMessage(action.getText("project.deliverable.generalInformation.keyOutput"));
    }

    if (deliverable.getResponsiblePartner() != null) {
      if (deliverable.getResponsiblePartner().getId() == -1) {
        this.addMessage(action.getText("project.deliverable.generalInformation.partnerResponsible"));
      }
    } else {
      this.addMessage(action.getText("project.deliverable.generalInformation.partnerResponsible"));
    }

    if (deliverable.getOtherPartners() != null) {
      if (deliverable.getOtherPartners().size() == 0) {
        this.addMessage(action.getText("project.deliverable.generalInformation.partnerOthers"));
      }
    } else {
      this.addMessage(action.getText("project.deliverable.generalInformation.partnerOthers"));
    }


    if (!action.getFieldErrors().isEmpty()) {
      action.addActionError(action.getText("saving.fields.required"));
    } else if (validationMessage.length() > 0) {
      action
        .addActionMessage(" " + action.getText("saving.missingFields", new String[] {validationMessage.toString()}));
    }
    if (action.isReportingActive()) {
      this.saveMissingFields(deliverable, APConstants.REPORTING, action.getPlanningYear(), "deliverables");
    } else {
      this.saveMissingFields(deliverable, APConstants.PLANNING, action.getPlanningYear(), "deliverables");
    }
  }

}
