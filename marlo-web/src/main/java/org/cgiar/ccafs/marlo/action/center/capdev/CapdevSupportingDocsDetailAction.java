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

package org.cgiar.ccafs.marlo.action.center.capdev;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CapdevSuppDocsDocumentsManager;
import org.cgiar.ccafs.marlo.data.manager.CapdevSupportingDocsManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.model.CapdevSuppDocsDocuments;
import org.cgiar.ccafs.marlo.data.model.CapdevSupportingDocs;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableType;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.validation.center.capdev.CapdevSupportingDocsValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class CapdevSupportingDocsDetailAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private long supportingDocID;
  private long capdevID;
  private final CapdevSupportingDocsValidator validator;
  private CapdevSupportingDocs capdevSupportingDocs;
  private List<CenterDeliverableType> deliverablesList;
  private List<CenterDeliverableType> deliverablesSubtypesList;
  private List<String> links;
  private List<Map<String, Object>> json;
  private List<CapdevSuppDocsDocuments> documents;
  private final CapdevSupportingDocsManager capdevsupportingDocsService;
  private final CapdevSuppDocsDocumentsManager capdevSuppDocsDocumentsService;
  private final ICenterDeliverableTypeManager centerDeliverableService;

  @Inject
  public CapdevSupportingDocsDetailAction(APConfig config, CapdevSupportingDocsManager capdevsupportingDocsService,
    ICenterDeliverableTypeManager centerDeliverableService,
    CapdevSuppDocsDocumentsManager capdevSuppDocsDocumentsService, CapdevSupportingDocsValidator validator) {
    super(config);
    this.capdevsupportingDocsService = capdevsupportingDocsService;
    this.centerDeliverableService = centerDeliverableService;
    this.capdevSuppDocsDocumentsService = capdevSuppDocsDocumentsService;
    this.validator = validator;
  }


  public String deleteDocumentLink() {
    final Map<String, Object> parameters = this.getParameters();
    final long documentID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    final CapdevSuppDocsDocuments document = capdevSuppDocsDocumentsService.getCapdevSuppDocsDocumentsById(documentID);
    document.setActive(false);
    document.setUsersByModifiedBy(this.getCurrentUser());
    capdevSuppDocsDocumentsService.saveCapdevSuppDocsDocuments(document);
    return SUCCESS;
  }


  public long getCapdevID() {
    return capdevID;
  }


  public CapdevSupportingDocs getCapdevSupportingDocs() {
    return capdevSupportingDocs;
  }


  public List<CenterDeliverableType> getDeliverablesList() {
    return deliverablesList;
  }


  public List<CenterDeliverableType> getDeliverablesSubtypesList() {
    return deliverablesSubtypesList;
  }


  public List<CapdevSuppDocsDocuments> getDocuments() {
    return documents;
  }

  public List<Map<String, Object>> getJson() {
    return json;
  }

  public List<String> getLinks() {
    return links;
  }


  public long getSupportingDocID() {
    return supportingDocID;
  }


  @Override
  public void prepare() throws Exception {
    links = new ArrayList<>();

    deliverablesList = new ArrayList<>(centerDeliverableService.findAll().stream()
      .filter(dl -> dl.isActive() && (dl.getDeliverableType() == null)).collect(Collectors.toList()));
    Collections.sort(deliverablesList, (ra1, ra2) -> ra1.getName().compareTo(ra2.getName()));

    try {
      supportingDocID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter("supportingDocID")));
    } catch (final Exception e) {
      supportingDocID = -1;
    }

    capdevSupportingDocs = capdevsupportingDocsService.getCapdevSupportingDocsById(supportingDocID);

    if (capdevSupportingDocs != null) {
      if (capdevSupportingDocs.getCenterDeliverableTypes() != null) {
        final Long deliverableTypeParentId = capdevSupportingDocs.getCenterDeliverableTypes().getId();

        deliverablesSubtypesList = new ArrayList<>(centerDeliverableService.findAll().stream()
          .filter(
            dt -> (dt.getDeliverableType() != null) && (dt.getDeliverableType().getId() == deliverableTypeParentId))
          .collect(Collectors.toList()));
        Collections.sort(deliverablesSubtypesList, (ra1, ra2) -> ra1.getName().compareTo(ra2.getName()));
      }

      if (capdevSupportingDocs.getCapdevSuppDocsDocumentses() != null) {
        documents = capdevSupportingDocs.getCapdevSuppDocsDocumentses().stream().filter(d -> d.getActive())
          .collect(Collectors.toList());

        Collections.sort(documents, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));
      }


    }


  }


  @Override
  public String save() {
    final CapdevSupportingDocs capdevSupportingDocsDB =
      capdevsupportingDocsService.getCapdevSupportingDocsById(supportingDocID);
    capdevSupportingDocsDB.setTitle(capdevSupportingDocs.getTitle());
    if (capdevSupportingDocs.getCenterDeliverableTypes().getId() > -1) {
      capdevSupportingDocsDB.setCenterDeliverableTypes(capdevSupportingDocs.getCenterDeliverableTypes());
      capdevSupportingDocsDB.setDeliverableSubtype(capdevSupportingDocs.getDeliverableSubtype());
    } else {
      capdevSupportingDocsDB.setCenterDeliverableTypes(null);
      capdevSupportingDocsDB.setDeliverableSubtype(null);
    }
    capdevSupportingDocsDB.setPublicationDate(capdevSupportingDocs.getPublicationDate());
    capdevsupportingDocsService.saveCapdevSupportingDocs(capdevSupportingDocsDB);


    this.saveLinks(links, capdevSupportingDocsDB);

    if (!this.getInvalidFields().isEmpty()) {
      this.setActionMessages(null);
      final List<String> keys = new ArrayList<String>(this.getInvalidFields().keySet());
      for (final String key : keys) {
        this.addActionMessage(key + ": " + this.getInvalidFields().get(key));
      }
    } else {
      this.addActionMessage("message:" + this.getText("saving.saved"));
    }

    return SUCCESS;
  }


  public void saveLinks(List<String> links, CapdevSupportingDocs capdevSupportingDocsDB) {


    if ((capdevSupportingDocsDB.getCapdevSuppDocsDocumentses() != null)
      && (capdevSupportingDocsDB.getCapdevSuppDocsDocumentses().size() > 0)) {
      final List<CapdevSuppDocsDocuments> documentsDB = new ArrayList<>(capdevSupportingDocsDB
        .getCapdevSuppDocsDocumentses().stream().filter(d -> d.getActive()).collect(Collectors.toList()));

      for (final CapdevSuppDocsDocuments document : documentsDB) {
        if (!documents.contains(document)) {
          capdevSuppDocsDocumentsService.deleteCapdevSuppDocsDocuments(document.getId());
        }
      }
    }

    if (!documents.isEmpty()) {
      for (final CapdevSuppDocsDocuments document : documents) {
        CapdevSuppDocsDocuments documentSave = null;
        if (document.getId() == null) {

          documentSave = new CapdevSuppDocsDocuments();
          documentSave.setActive(true);
          documentSave.setActiveSince(new Date());
          documentSave.setLink(document.getLink());
          documentSave.setCapdevSupportingDocs(capdevSupportingDocsDB);
          documentSave.setUsersByCreatedBy(this.getCurrentUser());
          capdevSuppDocsDocumentsService.saveCapdevSuppDocsDocuments(documentSave);
        } else {
          boolean hasChanges = false;
          final CapdevSuppDocsDocuments documentprevio =
            capdevSuppDocsDocumentsService.getCapdevSuppDocsDocumentsById(document.getId());
          if (!documentprevio.getLink().equals(documentprevio.getLink())) {
            hasChanges = true;
            documentprevio.setLink(document.getLink());
          }

          if (hasChanges) {
            documentprevio.setUsersByModifiedBy(this.getCurrentUser());
            documentprevio.setActiveSince(new Date());
            capdevSuppDocsDocumentsService.saveCapdevSuppDocsDocuments(documentprevio);
          }

        }
      }
    }


  }

  public void setCapdevID(long capdevID) {
    this.capdevID = capdevID;
  }


  public void setCapdevSupportingDocs(CapdevSupportingDocs capdevSupportingDocs) {
    this.capdevSupportingDocs = capdevSupportingDocs;
  }


  public void setDeliverablesList(List<CenterDeliverableType> deliverablesList) {
    this.deliverablesList = deliverablesList;
  }


  public void setDeliverablesSubtypesList(List<CenterDeliverableType> deliverablesSubtypesList) {
    this.deliverablesSubtypesList = deliverablesSubtypesList;
  }


  public void setDocuments(List<CapdevSuppDocsDocuments> documents) {
    this.documents = documents;
  }


  public void setJson(List<Map<String, Object>> json) {
    this.json = json;
  }


  public void setLinks(List<String> links) {
    this.links = links;
  }


  public void setSupportingDocID(long supportingDocID) {
    this.supportingDocID = supportingDocID;
  }


  @Override
  public void validate() {
    if (save) {
      validator.validate(this, capdevSupportingDocs, documents);

      // if (capdevSupportingDocs.getTitle().equalsIgnoreCase("")) {
      // this.addFieldError("capdevSupportingDocs.title", "Title is required.");
      // }
      // if (capdevSupportingDocs.getCenterDeliverableTypes().getId() == -1) {
      // this.addFieldError("capdevSupportingDocs.centerDeliverableTypes.id", "Type is required.");
      // }
      // if (capdevSupportingDocs.getDeliverableSubtype().getId() == -1) {
      // this.addFieldError("capdevSupportingDocs.deliverableSubtype.id", "Subtype is required.");
      // }
      // if (capdevSupportingDocs.getPublicationDate() == null) {
      // this.addFieldError("capdevSupportingDocs.publicationDate", "Publication date is required.");
      // }


    }
  }


}
