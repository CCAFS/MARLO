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

package org.cgiar.ccafs.marlo.validation.center.monitoring.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.data.manager.ICenterManager;
import org.cgiar.ccafs.marlo.data.model.Center;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableDocument;
import org.cgiar.ccafs.marlo.data.model.CenterProgram;
import org.cgiar.ccafs.marlo.data.model.CenterProject;
import org.cgiar.ccafs.marlo.data.model.ProjectSectionsEnum;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import com.google.inject.Inject;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
public class CenterDeliverableValidator extends BaseValidator {

  private ICenterManager centerService;

  @Inject
  public CenterDeliverableValidator(ICenterManager centerService) {
    this.centerService = centerService;
  }

  private Path getAutoSaveFilePath(CenterDeliverable deliverable, long centerID) {
    Center center = centerService.getCrpById(centerID);
    String composedClassName = deliverable.getClass().getSimpleName();
    String actionFile = ProjectSectionsEnum.DELIVERABLES.getStatus().replace("/", "_");
    String autoSaveFile =
      deliverable.getId() + "_" + composedClassName + "_" + center.getAcronym() + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
  }

  public void validate(BaseAction baseAction, CenterDeliverable deliverable, CenterProject project,
    CenterProgram selectedProgram, boolean saving) {

    baseAction.setInvalidFields(new HashMap<>());

    if (!saving) {
      Path path = this.getAutoSaveFilePath(deliverable, baseAction.getCenterID());

      if (path.toFile().exists()) {
        this.addMissingField("programImpact.action.draft");
      }
    }

    if (!baseAction.getFieldErrors().isEmpty()) {
      baseAction.addActionError(baseAction.getText("saving.fields.required"));
    }

    this.validateDeliverable(baseAction, deliverable);

    this.saveMissingFields(deliverable, project, "deliverableList");

  }


  public void validateDeliverable(BaseAction baseAction, CenterDeliverable deliverable) {

    if (deliverable.getName() != null) {
      if (!this.isValidString(deliverable.getName()) && this.wordCount(deliverable.getName()) <= 50) {
        this.addMessage(baseAction.getText("deliverable.action.deliverablesName"));
        baseAction.getInvalidFields().put("input-deliverable.name", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(baseAction.getText("deliverable.action.deliverablesName"));
      baseAction.getInvalidFields().put("input-deliverable.name", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (deliverable.getStartDate() == null) {
      this.addMessage(baseAction.getText("deliverable.action.deliverablesStartDate"));
      baseAction.getInvalidFields().put("input-deliverable.startDate", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (deliverable.getEndDate() == null) {
      this.addMessage(baseAction.getText("deliverable.action.deliverablesEndDate"));
      baseAction.getInvalidFields().put("input-deliverable.endDate", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (deliverable.getDeliverableType() != null) {
      if (deliverable.getDeliverableType().getId() == null || deliverable.getDeliverableType().getId() == -1) {
        this.addMessage(baseAction.getText("deliverable.action.deliverablesType"));
        baseAction.getInvalidFields().put("input-deliverable.deliverableType", InvalidFieldsMessages.EMPTYFIELD);
      }
    } else {
      this.addMessage(baseAction.getText("deliverable.action.deliverablesType"));
      baseAction.getInvalidFields().put("input-deliverable.deliverableType", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (deliverable.getOutputs() == null || deliverable.getOutputs().isEmpty()) {
      this.addMessage(baseAction.getText("projectDescription.actio.outputs"));
      baseAction.getInvalidFields().put("list-deliverable.outputs",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Outputs"}));
    }

    if (deliverable.getDocuments() == null || deliverable.getDocuments().isEmpty()) {
      this.addMessage(baseAction.getText("deliverable.action.documents"));
      baseAction.getInvalidFields().put("list-deliverable.documents",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Suporting Documents"}));
    } else {
      for (int i = 0; i < deliverable.getDocuments().size(); i++) {
        this.validateDocument(baseAction, deliverable.getDocuments().get(i), i);
      }
    }


  }

  public void validateDocument(BaseAction baseAction, CenterDeliverableDocument document, int i) {
    if (!this.isValidString(document.getLink()) && this.wordCount(document.getLink()) <= 200) {
      this.addMessage(baseAction.getText("deliverable.action.documents.link", String.valueOf(i + 1)));
      baseAction.getInvalidFields().put("input-deliverable.documents[" + i + "].link",
        InvalidFieldsMessages.EMPTYFIELD);
    }

  }


}
