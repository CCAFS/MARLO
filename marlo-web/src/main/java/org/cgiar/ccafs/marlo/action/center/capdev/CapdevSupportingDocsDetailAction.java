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
import org.cgiar.ccafs.marlo.data.manager.AuditLogManager;
import org.cgiar.ccafs.marlo.data.manager.CapdevSuppDocsDocumentsManager;
import org.cgiar.ccafs.marlo.data.manager.CapdevSupportingDocsManager;
import org.cgiar.ccafs.marlo.data.manager.ICapacityDevelopmentService;
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableDocumentManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ICenterDeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.model.CapacityDevelopment;
import org.cgiar.ccafs.marlo.data.model.CapdevSupportingDocs;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverable;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableDocument;
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
  private CapacityDevelopment capdev;
  private final CapdevSupportingDocsValidator validator;
  private CapdevSupportingDocs capdevSupportingDocs;
  private CenterDeliverable supportingDoc;
  private List<CenterDeliverableType> deliverablesList;
  private List<CenterDeliverableType> deliverablesSubtypesList;
  private List<String> links;
  private List<Map<String, Object>> json;
  private List<CenterDeliverableDocument> documents;
  private CapdevSupportingDocsManager capdevsupportingDocsService;
  private CapdevSuppDocsDocumentsManager capdevSuppDocsDocumentsService;
  private ICenterDeliverableTypeManager centerDeliverableTypeService;
  private ICapacityDevelopmentService capdevService;
  private ICenterDeliverableManager centerDeliverableService;
  private ICenterDeliverableDocumentManager centerDeliverableDocService;

  private String transaction;
  private final AuditLogManager auditLogService;

  @Inject
  public CapdevSupportingDocsDetailAction(APConfig config, CapdevSupportingDocsManager capdevsupportingDocsService,
    ICenterDeliverableTypeManager centerDeliverableTypeService,
    CapdevSuppDocsDocumentsManager capdevSuppDocsDocumentsService, CapdevSupportingDocsValidator validator,
    AuditLogManager auditLogService, ICapacityDevelopmentService capdevService,
    ICenterDeliverableManager centerDeliverableService, ICenterDeliverableDocumentManager centerDeliverableDocService) {
    super(config);
    this.capdevsupportingDocsService = capdevsupportingDocsService;
    this.centerDeliverableTypeService = centerDeliverableTypeService;
    this.capdevSuppDocsDocumentsService = capdevSuppDocsDocumentsService;
    this.validator = validator;
    this.auditLogService = auditLogService;
    this.capdevService = capdevService;
    this.centerDeliverableService = centerDeliverableService;
    this.centerDeliverableDocService = centerDeliverableDocService;

  }


  public String deleteDocumentLink() {
    Map<String, Object> parameters = this.getParameters();
    long documentID = Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    CenterDeliverableDocument document = centerDeliverableDocService.getDeliverableDocumentById(documentID);
    document.setActive(false);
    document.setModifiedBy(this.getCurrentUser());
    centerDeliverableDocService.saveDeliverableDocument(document);
    return SUCCESS;
  }


  public CapacityDevelopment getCapdev() {
    return capdev;
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


  public List<CenterDeliverableDocument> getDocuments() {
    return documents;
  }


  public List<Map<String, Object>> getJson() {
    return json;
  }


  public List<String> getLinks() {
    return links;
  }


  public CenterDeliverable getSupportingDoc() {
    return supportingDoc;
  }


  public long getSupportingDocID() {
    return supportingDocID;
  }


  public String getTransaction() {
    return transaction;
  }


  @Override
  public void prepare() throws Exception {
    links = new ArrayList<>();

    deliverablesList = new ArrayList<>(centerDeliverableTypeService.findAll().stream()
      .filter(dl -> dl.isActive() && (dl.getDeliverableType() == null)).collect(Collectors.toList()));
    Collections.sort(deliverablesList, (ra1, ra2) -> ra1.getName().compareTo(ra2.getName()));

    try {
      supportingDocID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter("supportingDocID")));
      capdevID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CAPDEV_ID)));
    } catch (final Exception e) {
      supportingDocID = -1;
      capdevID = -1;
    }

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      final CenterDeliverable history = (CenterDeliverable) auditLogService.getHistory(transaction);

      if (history != null) {
        supportingDoc = history;
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      supportingDoc = centerDeliverableService.getDeliverableById(supportingDocID);
    }

    if (supportingDoc != null) {
      capdev = capdevService.getCapacityDevelopmentById(capdevID);

      if (supportingDoc.getDeliverableType() != null) {
        Long deliverableTypeParentId = supportingDoc.getDeliverableType().getDeliverableType().getId();

        deliverablesSubtypesList = new ArrayList<>(centerDeliverableTypeService.findAll().stream()
          .filter(
            dt -> (dt.getDeliverableType() != null) && (dt.getDeliverableType().getId() == deliverableTypeParentId))
          .collect(Collectors.toList()));
        Collections.sort(deliverablesSubtypesList, (ra1, ra2) -> ra1.getName().compareTo(ra2.getName()));

        System.out.println(deliverablesSubtypesList.size());
      }

      if (supportingDoc.getDeliverableDocuments() != null) {

        documents =
          supportingDoc.getDeliverableDocuments().stream().filter(d -> d.isActive()).collect(Collectors.toList());
        Collections.sort(documents, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));
      }


    }


  }

  @Override
  public String save() {
    final CenterDeliverable supportingDocDB = centerDeliverableService.getDeliverableById(supportingDocID);
    supportingDocDB.setName(supportingDoc.getName());


    System.out.println(supportingDoc.getDeliverableType().getId());

    if (supportingDoc.getDeliverableType().getId() != null) {
      if (supportingDoc.getDeliverableType().getId() != -1) {
        CenterDeliverableType deliverableType =
          centerDeliverableTypeService.getDeliverableTypeById(supportingDoc.getDeliverableType().getId());
        supportingDocDB.setDeliverableType(deliverableType);
      }

    }

    supportingDocDB.setStartDate(supportingDoc.getStartDate());


    this.saveLinks(supportingDocDB);

    // Save Supporting Docs with History
    final List<String> relationsName = new ArrayList<>();
    relationsName.add(APConstants.DELIVERABLE_DOCUMENT_RELATION);


    supportingDocDB.setActiveSince(new Date());
    supportingDocDB.setModifiedBy(this.getCurrentUser());

    centerDeliverableService.saveDeliverable(supportingDocDB, this.getActionName(), relationsName);


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


  public void saveLinks(CenterDeliverable supportingDocDB) {


    if ((supportingDocDB.getDeliverableDocuments() != null) && (supportingDocDB.getDeliverableDocuments().size() > 0)) {
      List<CenterDeliverableDocument> documentsDB = new ArrayList<>(
        supportingDocDB.getDeliverableDocuments().stream().filter(d -> d.isActive()).collect(Collectors.toList()));

      for (CenterDeliverableDocument document : documentsDB) {
        if (!documents.contains(document)) {
          // capdevSuppDocsDocumentsService.deleteCapdevSuppDocsDocuments(document.getId());
        }
      }
    }

    if (documents != null) {
      if (!documents.isEmpty()) {
        for (CenterDeliverableDocument document : documents) {
          CenterDeliverableDocument documentSave = null;
          if (document.getId() == null) {

            documentSave = new CenterDeliverableDocument();
            documentSave.setActive(true);
            documentSave.setActiveSince(new Date());
            documentSave.setLink(document.getLink());
            documentSave.setDeliverable(supportingDocDB);
            documentSave.setCreatedBy(this.getCurrentUser());
            documentSave.setModifiedBy(this.getCurrentUser());
            documentSave.setModificationJustification(null);
            centerDeliverableDocService.saveDeliverableDocument(documentSave);
          } else {
            boolean hasChanges = false;
            final CenterDeliverableDocument documentprevio =
              centerDeliverableDocService.getDeliverableDocumentById(document.getId());
            if (!documentprevio.getLink().equals(document.getLink())) {
              hasChanges = true;
              documentprevio.setLink(document.getLink());
            }

            if (hasChanges) {
              documentprevio.setActiveSince(new Date());
              documentprevio.setModifiedBy(this.getCurrentUser());
              documentprevio.setModificationJustification(null);
              centerDeliverableDocService.saveDeliverableDocument(documentprevio);
            }

          }
        }
      }
    }


  }


  public void setCapdev(CapacityDevelopment capdev) {
    this.capdev = capdev;
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


  public void setDocuments(List<CenterDeliverableDocument> documents) {
    this.documents = documents;
  }


  public void setJson(List<Map<String, Object>> json) {
    this.json = json;
  }


  public void setLinks(List<String> links) {
    this.links = links;
  }


  public void setSupportingDoc(CenterDeliverable supportingDoc) {
    this.supportingDoc = supportingDoc;
  }


  public void setSupportingDocID(long supportingDocID) {
    this.supportingDocID = supportingDocID;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


  @Override
  public void validate() {
    if (save) {
      validator.validate(this, supportingDoc, documents);
    }
  }


}
