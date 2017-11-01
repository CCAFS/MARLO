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
import org.cgiar.ccafs.marlo.utils.AutoSaveReader;
import org.cgiar.ccafs.marlo.validation.center.capdev.CapdevSupportingDocsValidator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import org.apache.commons.lang3.StringUtils;

public class CapdevSupportingDocsDetailAction extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = 1L;

  private long deliverableID;
  private long capdevID;
  private long projectID;
  private CapacityDevelopment capdev;
  private CapdevSupportingDocsValidator validator;
  private CapdevSupportingDocs capdevSupportingDocs;
  private CenterDeliverable deliverable;
  private List<CenterDeliverableType> deliverablesList;
  private List<CenterDeliverableType> deliverablesSubtypesList;
  private List<String> links;
  private List<Map<String, Object>> json;
  private List<CenterDeliverableDocument> documents;

  private ICenterDeliverableTypeManager centerDeliverableTypeService;
  private ICapacityDevelopmentService capdevService;
  private ICenterDeliverableManager centerDeliverableService;
  private ICenterDeliverableDocumentManager centerDeliverableDocService;

  private String transaction;
  private final AuditLogManager auditLogService;

  @Inject
  public CapdevSupportingDocsDetailAction(APConfig config, ICenterDeliverableTypeManager centerDeliverableTypeService,
    CapdevSupportingDocsValidator validator, AuditLogManager auditLogService, ICapacityDevelopmentService capdevService,
    ICenterDeliverableManager centerDeliverableService, ICenterDeliverableDocumentManager centerDeliverableDocService) {
    super(config);

    this.centerDeliverableTypeService = centerDeliverableTypeService;

    this.validator = validator;
    this.auditLogService = auditLogService;
    this.capdevService = capdevService;
    this.centerDeliverableService = centerDeliverableService;
    this.centerDeliverableDocService = centerDeliverableDocService;

  }


  @Override
  public String cancel() {
    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {

      boolean fileDeleted = path.toFile().delete();
    }

    this.setDraft(false);
    Collection<String> messages = this.getActionMessages();
    if (!messages.isEmpty()) {
      String validationMessage = messages.iterator().next();
      this.setActionMessages(null);
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    } else {
      this.addActionMessage("draft:" + this.getText("cancel.autoSave"));
    }
    messages = this.getActionMessages();

    return SUCCESS;
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


  private Path getAutoSaveFilePath() {
    String composedClassName = deliverable.getClass().getSimpleName();
    String actionFile = this.getActionName().replace("/", "_");
    String autoSaveFile = deliverable.getId() + "_" + composedClassName + "_" + actionFile + ".json";

    return Paths.get(config.getAutoSaveFolder() + autoSaveFile);
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


  public CenterDeliverable getDeliverable() {
    return deliverable;
  }


  public long getDeliverableID() {
    return deliverableID;
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


  public long getProjectID() {
    return projectID;
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
      deliverableID =
        Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CENTER_DELIVERABLE_ID)));
      capdevID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.CAPDEV_ID)));
      projectID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter(APConstants.PROJECT_ID)));
    } catch (final Exception e) {
      deliverableID = -1;
      capdevID = -1;
      projectID = 0;
    }

    if (this.getRequest().getParameter(APConstants.TRANSACTION_ID) != null) {

      transaction = StringUtils.trim(this.getRequest().getParameter(APConstants.TRANSACTION_ID));
      CenterDeliverable history = (CenterDeliverable) auditLogService.getHistory(transaction);

      if (history != null) {
        deliverable = history;
      } else {
        this.transaction = null;
        this.setTransaction("-1");
      }
    } else {
      deliverable = centerDeliverableService.getDeliverableById(deliverableID);
    }

    if (deliverable != null) {
      Path path = this.getAutoSaveFilePath();

      if (path.toFile().exists() && this.getCurrentUser().isAutoSave() && this.isEditable()) {
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(path.toFile()));
        Gson gson = new GsonBuilder().create();
        JsonObject jReader = gson.fromJson(reader, JsonObject.class);
        reader.close();

        AutoSaveReader autoSaveReader = new AutoSaveReader();


        deliverable = (CenterDeliverable) autoSaveReader.readFromJson(jReader);
        capdev = capdevService.getCapacityDevelopmentById(capdevID);

        deliverable.setCapdev(capdev);
        System.out.println(deliverable.getDeliverableType().getId());
        System.out.println(deliverable.getDeliverableType().getDeliverableType().getId());
        if (deliverable.getDeliverableType().getDeliverableType().getId() != -1) {
          Long deliverableTypeParentId = deliverable.getDeliverableType().getDeliverableType().getId();

          deliverablesSubtypesList = new ArrayList<>(centerDeliverableTypeService.findAll().stream()
            .filter(
              dt -> (dt.getDeliverableType() != null) && (dt.getDeliverableType().getId() == deliverableTypeParentId))
            .collect(Collectors.toList()));
          Collections.sort(deliverablesSubtypesList, (ra1, ra2) -> ra1.getName().compareTo(ra2.getName()));

        }


        if (deliverable.getDocuments() != null) {
          List<CenterDeliverableDocument> documents = new ArrayList<>();
          for (CenterDeliverableDocument document : deliverable.getDocuments()) {
            CenterDeliverableDocument doc = centerDeliverableDocService.getDeliverableDocumentById(document.getId());
            documents.add(doc);
          }
          deliverable.setDocuments(documents);
        }


        this.setDraft(true);

      } else {
        this.setDraft(false);
        capdev = capdevService.getCapacityDevelopmentById(capdevID);

        if (deliverable.getDeliverableType().getDeliverableType().getId() != -1) {
          Long deliverableTypeParentId = deliverable.getDeliverableType().getDeliverableType().getId();

          deliverablesSubtypesList = new ArrayList<>(centerDeliverableTypeService.findAll().stream()
            .filter(
              dt -> (dt.getDeliverableType() != null) && (dt.getDeliverableType().getId() == deliverableTypeParentId))
            .collect(Collectors.toList()));
          Collections.sort(deliverablesSubtypesList, (ra1, ra2) -> ra1.getName().compareTo(ra2.getName()));

        }

        if (deliverable.getDeliverableDocuments() != null) {

          List<CenterDeliverableDocument> documents =
            deliverable.getDeliverableDocuments().stream().filter(d -> d.isActive()).collect(Collectors.toList());
          Collections.sort(documents, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));
          deliverable.setDocuments(documents);
        }
      }

    }

  }


  @Override
  public String save() {
    final CenterDeliverable supportingDocDB = centerDeliverableService.getDeliverableById(deliverableID);
    supportingDocDB.setName(deliverable.getName());


    if (deliverable.getDeliverableType().getDeliverableType() != null) {
      if (deliverable.getDeliverableType().getDeliverableType().getId() != -1) {
        CenterDeliverableType deliverableType =
          centerDeliverableTypeService.getDeliverableTypeById(deliverable.getDeliverableType().getId());
        supportingDocDB.setDeliverableType(deliverableType);
      }
    }

    supportingDocDB.setStartDate(deliverable.getStartDate());
    supportingDocDB.setEndDate(deliverable.getEndDate());


    this.saveLinks(supportingDocDB);

    // Save Supporting Docs with History
    final List<String> relationsName = new ArrayList<>();
    relationsName.add(APConstants.DELIVERABLE_DOCUMENT_RELATION);


    supportingDocDB.setActiveSince(new Date());
    supportingDocDB.setModifiedBy(this.getCurrentUser());

    centerDeliverableService.saveDeliverable(supportingDocDB, this.getActionName(), relationsName);

    Path path = this.getAutoSaveFilePath();

    if (path.toFile().exists()) {
      path.toFile().delete();
    }


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
        if (!deliverable.getDocuments().contains(document)) {
          // capdevSuppDocsDocumentsService.deleteCapdevSuppDocsDocuments(document.getId());
        }
      }
    }

    if (deliverable.getDocuments() != null) {
      if (!deliverable.getDocuments().isEmpty()) {
        for (CenterDeliverableDocument document : deliverable.getDocuments()) {
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


  public void setDeliverable(CenterDeliverable deliverable) {
    this.deliverable = deliverable;
  }


  public void setDeliverableID(long deliverableID) {
    this.deliverableID = deliverableID;
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


  public void setProjectID(long projectID) {
    this.projectID = projectID;
  }


  public void setTransaction(String transaction) {
    this.transaction = transaction;
  }


  @Override
  public void validate() {
    if (save) {
      validator.validate(this, deliverable);
    }
  }


}
