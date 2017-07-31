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
import org.cgiar.ccafs.marlo.data.manager.impl.CenterDeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.model.CapdevSuppDocsDocuments;
import org.cgiar.ccafs.marlo.data.model.CapdevSupportingDocs;
import org.cgiar.ccafs.marlo.data.model.CenterDeliverableType;
import org.cgiar.ccafs.marlo.utils.APConfig;

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
  private CapdevSupportingDocs capdevSupportingDocs;
  private List<CenterDeliverableType> deliverablesList;
  private List<String> links;
  private List<Map<String, Object>> json;
  private final CapdevSupportingDocsManager capdevsupportingDocsService;
  private final CapdevSuppDocsDocumentsManager capdevSuppDocsDocumentsManager;
  private final CenterDeliverableTypeManager centerDeliverableManager;

  @Inject
  public CapdevSupportingDocsDetailAction(APConfig config, CapdevSupportingDocsManager capdevsupportingDocsService,
    CenterDeliverableTypeManager centerDeliverableManager,
    CapdevSuppDocsDocumentsManager capdevSuppDocsDocumentsManager) {
    super(config);
    this.capdevsupportingDocsService = capdevsupportingDocsService;
    this.centerDeliverableManager = centerDeliverableManager;
    this.capdevSuppDocsDocumentsManager = capdevSuppDocsDocumentsManager;
  }


  public String deleteDocumentLink() {
    System.out.println("delete country");
    final Map<String, Object> parameters = this.getParameters();
    final long documentID =
      Long.parseLong(StringUtils.trim(((String[]) parameters.get(APConstants.QUERY_PARAMETER))[0]));
    System.out.println(documentID);
    final CapdevSuppDocsDocuments document = capdevSuppDocsDocumentsManager.getCapdevSuppDocsDocumentsById(documentID);
    document.setActive(false);
    document.setUsersByModifiedBy(this.getCurrentUser());
    capdevSuppDocsDocumentsManager.saveCapdevSuppDocsDocuments(document);
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

    deliverablesList = new ArrayList<>(
      centerDeliverableManager.findAll().stream().filter(dl -> dl.isActive()).collect(Collectors.toList()));
    Collections.sort(deliverablesList, (ra1, ra2) -> ra1.getId().compareTo(ra2.getId()));

    try {
      supportingDocID = Long.parseLong(StringUtils.trim(this.getRequest().getParameter("supportingDocID")));
    } catch (final Exception e) {
      supportingDocID = -1;
    }

    capdevSupportingDocs = capdevsupportingDocsService.getCapdevSupportingDocsById(supportingDocID);
  }

  @Override
  public String save() {
    final CapdevSupportingDocs capdevSupportingDocsDB =
      capdevsupportingDocsService.getCapdevSupportingDocsById(supportingDocID);
    capdevSupportingDocsDB.setTitle(capdevSupportingDocs.getTitle());
    capdevSupportingDocsDB.setCenterDeliverableTypes(capdevSupportingDocs.getCenterDeliverableTypes());
    capdevSupportingDocsDB.setPublicationDate(capdevSupportingDocs.getPublicationDate());
    capdevsupportingDocsService.saveCapdevSupportingDocs(capdevSupportingDocsDB);

    this.saveLinks(links, capdevSupportingDocsDB);

    this.addActionMessage("message: Information was correctly saved.</br>");

    return SUCCESS;
  }


  public void saveLinks(List<String> links, CapdevSupportingDocs capdevSupportingDocs) {
    if (!links.isEmpty()) {
      for (final String link : links) {
        final CapdevSuppDocsDocuments document = new CapdevSuppDocsDocuments();
        document.setLink(link);
        document.setCapdevSupportingDocs(capdevSupportingDocs);
        document.setActive(true);
        document.setActiveSince(new Date());
        document.setUsersByCreatedBy(this.getCurrentUser());
        capdevSuppDocsDocumentsManager.saveCapdevSuppDocsDocuments(document);
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
      if (capdevSupportingDocs.getTitle().equalsIgnoreCase("")) {
        this.addFieldError("capdevSupportingDocs.title", "Title is required.");
      }
      if (capdevSupportingDocs.getCenterDeliverableTypes().getId() == -1) {
        this.addFieldError("capdevSupportingDocs.centerDeliverableTypes.id", "Type is required.");
      }
      if (capdevSupportingDocs.getPublicationDate() == null) {
        this.addFieldError("capdevSupportingDocs.publicationDate", "Publication date is required.");
      }


    }
  }


}
