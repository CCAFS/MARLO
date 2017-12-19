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
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.HashMap;

import com.google.inject.Inject;

public class CapdevSupportingDocsValidator extends BaseValidator {

  @Inject
  public CapdevSupportingDocsValidator() {

  }

  public void validate(BaseAction baseAction, CenterDeliverable deliverable) {

    baseAction.setInvalidFields(new HashMap<>());

    if (!baseAction.getFieldErrors().isEmpty()) {
      baseAction.addActionError(baseAction.getText("saving.fields.required"));
    }
    if (deliverable.getDeliverableType() != null) {
      if ((deliverable.getDeliverableType().getId() == null)
        || (deliverable.getDeliverableType().getId().longValue() == -1)) {
        deliverable.setDeliverableType(null);
      }
    }

    this.validateSupportingDocs(baseAction, deliverable);
    this.saveMissingFields(deliverable, deliverable.getCapdev(), "supportingDocs");
  }


  public void validateSupportingDocs(BaseAction baseAction, CenterDeliverable deliverable) {

    if (deliverable.getName() == null) {
      this.addMessage(baseAction.getText("capdev.action.supportingDocs.title"));
      baseAction.getInvalidFields().put("input-deliverable.name", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (deliverable.getName() != null) {
      if (deliverable.getName().equals("")) {
        this.addMessage(baseAction.getText("capdev.action.supportingDocs.title"));
        baseAction.getInvalidFields().put("input-deliverable.name", InvalidFieldsMessages.EMPTYFIELD);
      }
    }

    if (deliverable.getDeliverableType() == null) {
      this.addMessage(baseAction.getText("capdev.action.supportingDocs.subtype"));
      baseAction.getInvalidFields().put("input-deliverable.deliverableType.deliverableType.id",
        InvalidFieldsMessages.EMPTYFIELD);
    }

    if (deliverable.getDeliverableType() != null) {
      if (deliverable.getDeliverableType().getId() == -1) {
        this.addMessage(baseAction.getText("capdev.action.supportingDocs.type"));
        baseAction.getInvalidFields().put("input-deliverable.deliverableType.id", InvalidFieldsMessages.EMPTYFIELD);
      }

    }


    if (deliverable.getStartDate() == null) {
      this.addMessage(baseAction.getText("capdev.action.supportingDocs.publicationdate"));
      baseAction.getInvalidFields().put("input-deliverable.startDate", InvalidFieldsMessages.EMPTYFIELD);
    }

    if (deliverable.getDocuments() == null) {
      this.addMessage(baseAction.getText("capdev.action.supportingDocs.supportingDocs"));
      baseAction.getInvalidFields().put("list-capdev.supportingDocs", InvalidFieldsMessages.EMPTYLIST);
    } else {
      for (int j = 0; j < deliverable.getDocuments().size(); j++) {
        this.valideDocument(baseAction, deliverable.getDocuments().get(j).getLink(), j);
      }
    }
  }

  public void valideDocument(BaseAction baseAction, String link, int i) {
    if ((link == null) || link.equals("")) {
      this.addMessage(baseAction.getText("capdev.action.supportingDocs.link"));
      baseAction.getInvalidFields().put("input-deliverable.documents[" + i + "].link",
        InvalidFieldsMessages.EMPTYFIELD);
    }
  }

}