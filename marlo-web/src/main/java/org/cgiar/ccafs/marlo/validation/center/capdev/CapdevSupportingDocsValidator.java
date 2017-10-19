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
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableDocument;
import org.cgiar.ccafs.marlo.utils.InvalidFieldsMessages;
import org.cgiar.ccafs.marlo.validation.BaseValidator;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;

public class CapdevSupportingDocsValidator extends BaseValidator {

  @Inject
  public CapdevSupportingDocsValidator() {

  }

  public void validate(BaseAction baseAction, CenterDeliverable supportingDoc, List<CenterDeliverableDocument> links) {

    baseAction.setInvalidFields(new HashMap<>());

    if (!baseAction.getFieldErrors().isEmpty()) {
      baseAction.addActionError(baseAction.getText("saving.fields.required"));
    }

    this.validateSupportingDocs(baseAction, supportingDoc, links);
  }


  public void validateSupportingDocs(BaseAction baseAction, CenterDeliverable supportingDoc,
    List<CenterDeliverableDocument> links) {
    if (supportingDoc.getName().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.supportingDocs.title"));
      baseAction.getInvalidFields().put("input-supportingDoc.name", InvalidFieldsMessages.EMPTYFIELD);
    }


    if (supportingDoc.getStartDate() == null) {
      this.addMessage(baseAction.getText("capdev.action.supportingDocs.publicationdate"));
      baseAction.getInvalidFields().put("input-supportingDoc.startDate", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (links.isEmpty() && (supportingDoc.getDeliverableDocuments().stream().filter(docs -> docs.isActive())
      .collect(Collectors.toList()) == null)) {
      this.addMessage(baseAction.getText("capdev.action.supportingDocs.supportingDocs"));
      baseAction.getInvalidFields().put("list-capdev.supportingDocs",
        baseAction.getText(InvalidFieldsMessages.EMPTYLIST, new String[] {"Supporting Documents"}));
    } else {
      for (int j = 0; j < links.size(); j++) {
        this.valideDocument(baseAction, links.get(j).getLink(), j);
      }
    }
  }

  public void valideDocument(BaseAction baseAction, String link, int i) {
    if ((link == null) || link.equals("")) {
      this.addMessage(baseAction.getText("capdev.action.supportingDocs.link"));
      baseAction.getInvalidFields().put("input-documents[" + i + "].link", InvalidFieldsMessages.EMPTYFIELD);
    }
  }

}
