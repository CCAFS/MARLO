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
import org.cgiar.ccafs.marlo.data.model.CapdevSuppDocsDocuments;
import org.cgiar.ccafs.marlo.data.model.CapdevSupportingDocs;
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

  public void validate(BaseAction baseAction, CapdevSupportingDocs capdevSupportingDocs,
    List<CapdevSuppDocsDocuments> links) {

    baseAction.setInvalidFields(new HashMap<>());

    if (!baseAction.getFieldErrors().isEmpty()) {
      baseAction.addActionError(baseAction.getText("saving.fields.required"));
    }

    this.validateSupportingDocs(baseAction, capdevSupportingDocs, links);
  }


  public void validateSupportingDocs(BaseAction baseAction, CapdevSupportingDocs capdevSupportingDocs,
    List<CapdevSuppDocsDocuments> links) {
    if (capdevSupportingDocs.getTitle().equalsIgnoreCase("")) {
      this.addMessage(baseAction.getText("capdev.action.supportingDocs.title"));
      baseAction.getInvalidFields().put("input-capdevSupportingDocs.title", InvalidFieldsMessages.EMPTYFIELD);
    }


    if (capdevSupportingDocs.getPublicationDate() == null) {
      this.addMessage(baseAction.getText("capdev.action.supportingDocs.publicationdate"));
      baseAction.getInvalidFields().put("input-capdevSupportingDocs.publicationDate", InvalidFieldsMessages.EMPTYFIELD);
    }
    if (links.isEmpty() && capdevSupportingDocs.getCapdevSuppDocsDocumentses().stream().filter(docs -> docs.getActive())
      .collect(Collectors.toList()).isEmpty()) {
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
