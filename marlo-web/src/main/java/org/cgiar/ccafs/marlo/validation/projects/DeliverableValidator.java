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
import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionStatusEnum;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class DeliverableValidator extends BaseValidator {

  BaseAction action;

  @Inject
  private CrpManager crpManager;

  @Inject
  public DeliverableValidator() {
  }

  private Path getAutoSaveFilePath(Deliverable deliverable, long crpID) {
    Crp crp = crpManager.getCrpById(crpID);
    String composedClassName = deliverable.getClass().getSimpleName();
    String actionFile = ProjectSectionStatusEnum.DELIVERABLE.getStatus().replace("/", "_");
    String autoSaveFile =
      deliverable.getId() + "_" + composedClassName + "_" + crp.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }


  public void validate(BaseAction action, Deliverable deliverable, boolean saving) {
    this.action = action;
    if (!saving) {
      Path path = this.getAutoSaveFilePath(deliverable, action.getCrpID());

      if (path.toFile().exists()) {
        this.addMissingField("draft");
      }
    }

    if (!(this.isValidString(deliverable.getTitle()) && this.wordCount(deliverable.getTitle()) <= 15)) {
      this.addMessage(action.getText("project.deliverable.generalInformation.title"));
    }

    if (!(this.isValidString(deliverable.getStatusDescription())
      && this.wordCount(deliverable.getStatusDescription()) <= 15)) {
      this.addMessage(action.getText("project.deliverable.generalInformation.description"));
    }

    if (deliverable.getDeliverableType() != null) {
      if (deliverable.getDeliverableType().getId() == -1) {
        this.addMessage(action.getText("project.deliverable.generalInformation.subType"));
      } else {
        if (deliverable.getDeliverableType().getDeliverableType() != null) {
          if (deliverable.getDeliverableType().getDeliverableType().getId() == -1) {
            this.addMessage(action.getText("project.deliverable.generalInformation.type"));
          }
        } else {
          this.addMessage(action.getText("project.deliverable.generalInformation.type"));
        }
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

    if (deliverable.getCrpClusterKeyOutput() != null) {
      if (deliverable.getCrpClusterKeyOutput().getId() == -1) {
        this.addMessage(action.getText("project.deliverable.generalInformation.keyOutput"));
      }
    } else {
      this.addMessage(action.getText("project.deliverable.generalInformation.keyOutput"));
    }

    if (deliverable.getResponsiblePartner() != null) {
      if (deliverable.getResponsiblePartner().getProjectPartnerPerson().getId() == null
        || deliverable.getResponsiblePartner().getProjectPartnerPerson().getId() == -1) {
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
      this.saveMissingFields(deliverable, APConstants.REPORTING, action.getPlanningYear(),
        ProjectSectionStatusEnum.DELIVERABLES.getStatus());
    } else {
      this.saveMissingFields(deliverable, APConstants.PLANNING, action.getPlanningYear(),
        ProjectSectionStatusEnum.DELIVERABLES.getStatus());
    }
  }

}
