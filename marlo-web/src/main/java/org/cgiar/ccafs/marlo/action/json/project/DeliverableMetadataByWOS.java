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

package org.cgiar.ccafs.marlo.action.json.project;

import org.cgiar.ccafs.marlo.action.BaseAction;
import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableAffiliationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableAffiliationsNotMappedManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataExternalSourcesManager;
import org.cgiar.ccafs.marlo.data.manager.ExternalSourceAuthorManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliation;
import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliationsNotMapped;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataExternalSources;
import org.cgiar.ccafs.marlo.data.model.ExternalSourceAuthor;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.MetadataGardianModel;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.MetadataWOSModel;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.WOSAuthor;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.WOSInstitution;
import org.cgiar.ccafs.marlo.utils.APConfig;
import org.cgiar.ccafs.marlo.utils.doi.DOIService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.dispatcher.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

public class DeliverableMetadataByWOS extends BaseAction {

  /**
   * 
   */
  private static final long serialVersionUID = -1340291586140709256L;

  // Logger
  private static final Logger LOG = LoggerFactory.getLogger(DeliverableMetadataByWOS.class);

  private String link;
  private String jsonStringResponse;
  private MetadataWOSModel response;
  private Long deliverableId;

  // Managers
  private DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager;
  private DeliverableAffiliationManager deliverableAffiliationManager;
  private DeliverableAffiliationsNotMappedManager deliverableAffiliationsNotMappedManager;
  private ExternalSourceAuthorManager externalSourceAuthorManager;
  private DeliverableManager deliverableManager;
  private InstitutionManager institutionManager;

  @Inject
  public DeliverableMetadataByWOS(APConfig config, DeliverableAffiliationManager deliverableAffiliationManager,
    DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager,
    DeliverableAffiliationsNotMappedManager deliverableAffiliationsNotMappedManager,
    ExternalSourceAuthorManager externalSourceAuthorManager, DeliverableManager deliverableManager,
    InstitutionManager institutionManager) {
    super(config);
    this.deliverableAffiliationManager = deliverableAffiliationManager;
    this.deliverableMetadataExternalSourcesManager = deliverableMetadataExternalSourcesManager;
    this.deliverableAffiliationsNotMappedManager = deliverableAffiliationsNotMappedManager;
    this.externalSourceAuthorManager = externalSourceAuthorManager;
    this.deliverableManager = deliverableManager;
    this.institutionManager = institutionManager;
  }

  @Override
  public String execute() throws Exception {
    /*
     * if (this.jsonStringResponse == null || StringUtils.equalsIgnoreCase(this.jsonStringResponse, "null")) {
     * return NOT_FOUND;
     * }
     */
    if (this.jsonStringResponse != null && !StringUtils.equalsIgnoreCase(this.jsonStringResponse, "null")) {
      this.response = new Gson().fromJson(jsonStringResponse, MetadataWOSModel.class);

      this.saveInfo();
    }

    return SUCCESS;
  }

  public String getJsonStringResponse() {
    return jsonStringResponse;
  }

  public String getLink() {
    return link;
  }

  public MetadataWOSModel getResponse() {
    return response;
  }

  @Override
  public void prepare() throws Exception {
    Map<String, Parameter> parameters = this.getParameters();

    // If there are parameters, take its values
    try {
      String incomingUrl = StringUtils.stripToEmpty(parameters.get(APConstants.WOS_LINK).getMultipleValues()[0]);
      this.link = DOIService.tryGetDoiName(incomingUrl);
      this.deliverableId = Long.valueOf(
        StringUtils.stripToEmpty(parameters.get(APConstants.PROJECT_DELIVERABLE_REQUEST_ID).getMultipleValues()[0]));
    } catch (Exception e) {
      this.link = null;
      this.deliverableId = 0L;
    }

    if (!this.link.isEmpty() && DOIService.REGEXP_PLAINDOI.matcher(this.link).lookingAt()) {
      JsonElement response = this.readWOSDataFromClarisa(this.link);

      this.jsonStringResponse = StringUtils.stripToNull(new GsonBuilder().serializeNulls().create().toJson(response));
    }
  }

  private JsonElement readWOSDataFromClarisa(final String url) throws IOException {
    URL clarisaUrl = new URL(config.getClarisaWOSLink().replace("{1}", url));
    String loginData = config.getClarisaWOSUser() + ":" + config.getClarisaWOSPassword();
    String encoded = Base64.encodeBase64String(loginData.getBytes());

    HttpURLConnection conn = (HttpURLConnection) clarisaUrl.openConnection();
    conn.setRequestProperty("Authorization", "Basic " + encoded);
    JsonElement element = null;

    if (conn.getResponseCode() < 300) {
      try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
        element = new JsonParser().parse(reader);
      } catch (FileNotFoundException fnfe) {
        element = JsonNull.INSTANCE;
      }
    }

    return element;
  }

  private void saveAffiliations(Phase phase, Deliverable deliverable) {
    DeliverableMetadataExternalSources externalSource =
      this.deliverableMetadataExternalSourcesManager.findByPhaseAndDeliverable(phase, deliverable);
    List<WOSInstitution> incomingInstitutions = this.response.getInstitutions();

    if (incomingInstitutions != null) {
      List<DeliverableAffiliation> dbAffiliations =
        this.deliverableAffiliationManager.findAll() != null ? this.deliverableAffiliationManager.findAll().stream()
          .filter(da -> da != null && da.getId() != null && da.getPhase() != null && da.getDeliverable() != null
            && da.getPhase().equals(phase) && da.getDeliverable().getId() != null
            && da.getDeliverable().getId().equals(deliverable.getId())
            && da.getDeliverableMetadataExternalSources() != null
            && da.getDeliverableMetadataExternalSources().getId() != null
            && da.getDeliverableMetadataExternalSources().getId().equals(externalSource.getId()))
          .collect(Collectors.toList()) : Collections.emptyList();

      for (DeliverableAffiliation dbDeliverableAffiliation : dbAffiliations) {
        if (dbDeliverableAffiliation != null && dbDeliverableAffiliation.getInstitution() != null
          && (incomingInstitutions.stream()
            .filter(i -> i != null && i.getClarisaId() != null
              && i.getClarisaId().equals(dbDeliverableAffiliation.getInstitution().getId())
              && i.getClarisaMatchConfidence() < APConstants.ACCEPTATION_PERCENTAGE)
            .count() == 0)) {
          this.deliverableAffiliationManager.deleteDeliverableAffiliation(dbDeliverableAffiliation.getId());
          this.deliverableAffiliationManager.replicate(dbDeliverableAffiliation, phase.getNext());
        }
      }

      // save
      for (WOSInstitution incomingAffiliation : incomingInstitutions) {
        if (incomingAffiliation.getClarisaMatchConfidence() >= APConstants.ACCEPTATION_PERCENTAGE) {
          DeliverableAffiliation newDeliverableAffiliation =
            this.deliverableAffiliationManager.findByPhaseAndDeliverable(phase, deliverable).stream()
              .filter(nda -> nda != null && nda.getDeliverableMetadataExternalSources() != null
                && nda.getDeliverableMetadataExternalSources().getId() != null
                && nda.getDeliverableMetadataExternalSources().getId().equals(externalSource.getId())
                && nda.getInstitution() != null && nda.getInstitution().getId() != null
                && nda.getInstitution().getId().equals(incomingAffiliation.getClarisaId()))
              .findFirst().orElse(null);
          if (newDeliverableAffiliation == null) {
            newDeliverableAffiliation = new DeliverableAffiliation();
            newDeliverableAffiliation.setPhase(phase);
            newDeliverableAffiliation.setDeliverable(deliverable);
            newDeliverableAffiliation.setCreateDate(new Date());
            newDeliverableAffiliation.setCreatedBy(this.getCurrentUser());
          }

          Institution incomingInstitution = (incomingAffiliation.getClarisaId() != null)
            ? this.institutionManager.getInstitutionById(incomingAffiliation.getClarisaId()) : null;

          newDeliverableAffiliation.setInstitution(incomingInstitution);
          newDeliverableAffiliation.setInstitutionMatchConfidence(incomingAffiliation.getClarisaMatchConfidence());
          newDeliverableAffiliation.setDeliverableMetadataExternalSources(externalSource);
          newDeliverableAffiliation.setInstitutionNameWebOfScience(incomingAffiliation.getFullName());
          newDeliverableAffiliation.setInstitutionMatchConfidence(incomingAffiliation.getClarisaMatchConfidence());
          newDeliverableAffiliation.setActive(true);

          newDeliverableAffiliation =
            this.deliverableAffiliationManager.saveDeliverableAffiliation(newDeliverableAffiliation);
          this.deliverableAffiliationManager.replicate(newDeliverableAffiliation, phase.getNext());
        }
      }
    }
  }

  private void saveAffiliationsNotMapped(Phase phase, Deliverable deliverable) {
    DeliverableMetadataExternalSources externalSource =
      this.deliverableMetadataExternalSourcesManager.findByPhaseAndDeliverable(phase, deliverable);
    List<WOSInstitution> incomingInstitutions = this.response.getInstitutions();

    if (incomingInstitutions != null) {
      List<DeliverableAffiliationsNotMapped> dbAffiliationsNotMapped =
        this.deliverableAffiliationsNotMappedManager.findAll() != null
          ? this.deliverableAffiliationsNotMappedManager.findAll().stream()
            .filter(danm -> danm != null && danm.getId() != null && danm.getDeliverableMetadataExternalSources() != null
              && danm.getDeliverableMetadataExternalSources().getId() != null
              && danm.getDeliverableMetadataExternalSources().getId().equals(externalSource.getId()))
            .collect(Collectors.toList())
          : Collections.emptyList();

      for (DeliverableAffiliationsNotMapped dbDeliverableAffiliationNotMapped : dbAffiliationsNotMapped) {
        if (dbDeliverableAffiliationNotMapped != null
          && dbDeliverableAffiliationNotMapped.getPossibleInstitution() != null
          && (incomingInstitutions.stream()
            .filter(i -> i != null && i.getClarisaId() != null
              && i.getClarisaId().equals(dbDeliverableAffiliationNotMapped.getPossibleInstitution().getId())
              && i.getClarisaMatchConfidence() >= APConstants.ACCEPTATION_PERCENTAGE)
            .count() == 0)) {
          this.deliverableAffiliationsNotMappedManager
            .deleteDeliverableAffiliationsNotMapped(dbDeliverableAffiliationNotMapped.getId());
          this.deliverableAffiliationsNotMappedManager.replicate(dbDeliverableAffiliationNotMapped, phase.getNext());
        }
      }

      // save
      for (WOSInstitution incomingAffiliation : incomingInstitutions) {
        if (incomingAffiliation.getClarisaMatchConfidence() < APConstants.ACCEPTATION_PERCENTAGE) {
          DeliverableAffiliationsNotMapped newDeliverableAffiliationNotMapped =
            this.deliverableAffiliationsNotMappedManager.findAll() != null
              ? this.deliverableAffiliationsNotMappedManager.findAll().stream()
                .filter(nda -> nda != null && nda.getDeliverableMetadataExternalSources() != null
                  && nda.getDeliverableMetadataExternalSources().getId() != null
                  && nda.getDeliverableMetadataExternalSources().getId().equals(externalSource.getId())
                  && nda.getPossibleInstitution() != null && nda.getPossibleInstitution().getId() != null
                  && nda.getPossibleInstitution().getId().equals(incomingAffiliation.getClarisaId()))
                .findFirst().orElse(null)
              : null;
          if (newDeliverableAffiliationNotMapped == null) {
            newDeliverableAffiliationNotMapped = new DeliverableAffiliationsNotMapped();
            newDeliverableAffiliationNotMapped.setDeliverableMetadataExternalSources(externalSource);
            newDeliverableAffiliationNotMapped.setCreateDate(new Date());
            newDeliverableAffiliationNotMapped.setCreatedBy(this.getCurrentUser());
          }

          Institution incomingInstitution = (incomingAffiliation.getClarisaId() != null)
            ? this.institutionManager.getInstitutionById(incomingAffiliation.getClarisaId()) : null;

          newDeliverableAffiliationNotMapped.setPossibleInstitution(incomingInstitution);
          newDeliverableAffiliationNotMapped
            .setInstitutionMatchConfidence(incomingAffiliation.getClarisaMatchConfidence());
          newDeliverableAffiliationNotMapped.setName(incomingAffiliation.getFullName());
          newDeliverableAffiliationNotMapped.setCountry(incomingAffiliation.getCountry());
          newDeliverableAffiliationNotMapped.setFullAddress(incomingAffiliation.getFullAddress());
          newDeliverableAffiliationNotMapped
            .setInstitutionMatchConfidence(incomingAffiliation.getClarisaMatchConfidence());
          newDeliverableAffiliationNotMapped.setActive(true);

          newDeliverableAffiliationNotMapped = this.deliverableAffiliationsNotMappedManager
            .saveDeliverableAffiliationsNotMapped(newDeliverableAffiliationNotMapped);
          this.deliverableAffiliationsNotMappedManager.replicate(newDeliverableAffiliationNotMapped, phase.getNext());
        }
      }
    }
  }

  private void saveExternalSourceAuthors(Phase phase, Deliverable deliverable) {
    DeliverableMetadataExternalSources externalSource =
      this.deliverableMetadataExternalSourcesManager.findByPhaseAndDeliverable(phase, deliverable);
    List<WOSAuthor> incomingAuthors = this.response.getAuthors();

    if (incomingAuthors != null) {
      List<ExternalSourceAuthor> dbExternalSourceAuthors =
        this.externalSourceAuthorManager.findAll() != null ? this.externalSourceAuthorManager.findAll().stream()
          .filter(esa -> esa != null && esa.getId() != null && esa.getDeliverableMetadataExternalSources() != null
            && esa.getDeliverableMetadataExternalSources().getId() != null
            && esa.getDeliverableMetadataExternalSources().getId().equals(externalSource.getId()))
          .collect(Collectors.toList()) : Collections.emptyList();

      // we are going to remove all of them and just accept the incoming authors
      for (ExternalSourceAuthor dbExternalSourceAuthor : dbExternalSourceAuthors) {
        this.externalSourceAuthorManager.deleteExternalSourceAuthor(dbExternalSourceAuthor.getId());
        this.externalSourceAuthorManager.replicate(dbExternalSourceAuthor, phase.getNext());
      }

      // save
      for (WOSAuthor incomingAuthor : incomingAuthors) {
        ExternalSourceAuthor externalSourceAuthor = new ExternalSourceAuthor();
        externalSourceAuthor.setCreateDate(new Date());
        externalSourceAuthor.setCreatedBy(this.getCurrentUser());
        externalSourceAuthor.setDeliverableMetadataExternalSources(externalSource);
        externalSourceAuthor.setFullName(incomingAuthor.getFullName());

        externalSourceAuthor = this.externalSourceAuthorManager.saveExternalSourceAuthor(externalSourceAuthor);
        this.externalSourceAuthorManager.replicate(externalSourceAuthor, phase.getNext());
      }
    }
  }

  private void saveExternalSources(Phase phase, Deliverable deliverable) {
    DeliverableMetadataExternalSources externalSource =
      this.deliverableMetadataExternalSourcesManager.findByPhaseAndDeliverable(phase, deliverable);
    MetadataGardianModel gardianInfo = this.response.getGardianInfo();

    if (externalSource == null) {
      externalSource = new DeliverableMetadataExternalSources();
      externalSource.setPhase(phase);
      externalSource.setDeliverable(deliverable);
      externalSource.setCreateDate(new Date());
      externalSource.setCreatedBy(this.getCurrentUser());
      externalSource =
        this.deliverableMetadataExternalSourcesManager.saveDeliverableMetadataExternalSources(externalSource);
    }

    externalSource.setUrl(this.response.getUrl());
    externalSource.setDoi(this.response.getDoi());
    externalSource.setTitle(this.response.getTitle());
    externalSource.setPublicationType(this.response.getPublicationType());
    externalSource.setPublicationYear(this.response.getPublicationYear());
    externalSource.setOpenAccessStatus(String.valueOf(this.response.getIsOpenAccess()));
    externalSource.setOpenAccessLink(this.response.getOpenAcessLink());
    externalSource.setIsiStatus(String.valueOf(this.response.getIsISI()));
    externalSource.setJournalName(this.response.getJournalName());
    externalSource.setVolume(this.response.getVolume());
    externalSource.setPages(this.response.getPages());

    if (gardianInfo != null) {
      externalSource.setGardianFindability(gardianInfo.getFindability());
      externalSource.setGardianAccessibility(gardianInfo.getAccessibility());
      externalSource.setGardianInteroperability(gardianInfo.getInteroperability());
      externalSource.setGardianReusability(gardianInfo.getReusability());
      externalSource.setGardianTitle(gardianInfo.getTitle());
    }

    externalSource =
      this.deliverableMetadataExternalSourcesManager.saveDeliverableMetadataExternalSources(externalSource);

    this.deliverableMetadataExternalSourcesManager.replicate(externalSource, phase.getNext());
  }

  private void saveInfo() {
    Phase phase = this.getActualPhase();
    Deliverable deliverable = this.deliverableManager.getDeliverableById(this.deliverableId);

    this.saveExternalSources(phase, deliverable);
    this.saveAffiliations(phase, deliverable);
    this.saveAffiliationsNotMapped(phase, deliverable);
    this.saveExternalSourceAuthors(phase, deliverable);
  }
}
