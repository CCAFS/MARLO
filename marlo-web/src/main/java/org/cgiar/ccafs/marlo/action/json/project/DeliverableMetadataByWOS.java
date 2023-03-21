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
import org.cgiar.ccafs.marlo.data.manager.DeliverableAltmetricInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataExternalSourcesManager;
import org.cgiar.ccafs.marlo.data.manager.ExternalSourceAuthorManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliation;
import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliationsNotMapped;
import org.cgiar.ccafs.marlo.data.model.DeliverableAltmetricInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataExternalSources;
import org.cgiar.ccafs.marlo.data.model.ExternalSourceAuthor;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.MetadataAltmetricModel;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.MetadataAltmetricModel2;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.MetadataGardianModel;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.MetadataReadersModel;
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
  // private MetadataWOSModel response2;
  private Long deliverableId;
  private Long phaseId;

  // Managers
  private DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager;
  private DeliverableAffiliationManager deliverableAffiliationManager;
  private DeliverableAffiliationsNotMappedManager deliverableAffiliationsNotMappedManager;
  private ExternalSourceAuthorManager externalSourceAuthorManager;
  private DeliverableManager deliverableManager;
  private InstitutionManager institutionManager;
  private PhaseManager phaseManager;
  private DeliverableAltmetricInfoManager deliverableAltmetricInfoManager;

  @Inject
  public DeliverableMetadataByWOS(APConfig config, DeliverableAffiliationManager deliverableAffiliationManager,
    DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager,
    DeliverableAffiliationsNotMappedManager deliverableAffiliationsNotMappedManager,
    ExternalSourceAuthorManager externalSourceAuthorManager, DeliverableManager deliverableManager,
    InstitutionManager institutionManager, PhaseManager phaseManager,
    DeliverableAltmetricInfoManager deliverableAltmetricInfoManager) {
    super(config);
    this.deliverableAffiliationManager = deliverableAffiliationManager;
    this.deliverableMetadataExternalSourcesManager = deliverableMetadataExternalSourcesManager;
    this.deliverableAffiliationsNotMappedManager = deliverableAffiliationsNotMappedManager;
    this.externalSourceAuthorManager = externalSourceAuthorManager;
    this.deliverableManager = deliverableManager;
    this.institutionManager = institutionManager;
    this.phaseManager = phaseManager;
    this.deliverableAltmetricInfoManager = deliverableAltmetricInfoManager;
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
      // this.response2 = new Gson().fromJson(jsonStringResponse, MetadataWOSModel.class);
      this.phaseId = this.getActualPhase().getId();

      this.saveInfo();
      this.manualSetAlmetricInfo();
    }

    return SUCCESS;
  }

  private String getBooleanStringOrNotAvailable(String string) {
    String result = null;
    if (string != null) {
      if (StringUtils.equalsIgnoreCase(string, "yes")) {
        result = "true";
      } else if (StringUtils.equalsIgnoreCase(string, "no")) {
        result = "false";
      } else if (StringUtils.equalsIgnoreCase(string, "n/a")) {
        result = "N/A";
      }
    }

    return result;
  }

  public String getJsonStringResponse() {
    return jsonStringResponse;
  }

  public String getLink() {
    return link;
  }

  public Long getPhaseId() {
    return phaseId;
  }

  public MetadataWOSModel getResponse() {
    return response;
  }


  public void manualSetAlmetricInfo() {
    if (this.response != null && this.response.getAltmetricInfo2() != null) {

      MetadataAltmetricModel info = new MetadataAltmetricModel();
      MetadataAltmetricModel2 info2 = this.response.getAltmetricInfo2();

      info.setAltmetricId(info2.getAltmetricId());
      info.setAltmetricJid(info2.getAltmetricJid());
      info.setCitedByBlogs(info2.getCitedByBlogs());
      info.setCitedByDelicious(info2.getCitedByDelicious());
      info.setCitedByFacebookPages(info2.getCitedByFacebookPages());
      info.setCitedByForumUsers(info2.getCitedByForumUsers());
      info.setCitedByGooglePlusUsers(info2.getCitedByGooglePlusUsers());
      info.setCitedByLinkedinUsers(info2.getCitedByLinkedinUsers());
      info.setCitedByNewsOutlets(info2.getCitedByNewsOutlets());
      info.setCitedByPeerReviewSites(info2.getCitedByPeerReviewSites());
      info.setCitedByPinterestUsers(info2.getCitedByPinterestUsers());
      info.setCitedByPolicies(info2.getCitedByPolicies());
      info.setCitedByPosts(info2.getCitedByPosts());
      info.setCitedByRedditUsers(info2.getCitedByRedditUsers());
      info.setCitedByResearchHighlightPlatforms(info2.getCitedByResearchHighlightPlatforms());
      info.setCitedByStackExchangeResources(info2.getCitedByStackExchangeResources());
      info.setCitedByTwitterUsers(info2.getCitedByTwitterUsers());
      info.setCitedByWeiboUsers(info2.getCitedByWeiboUsers());
      info.setCitedByWikipediaPages(info2.getCitedByWikipediaPages());
      info.setCitedByYoutubeChannels(info2.getCitedByYoutubeChannels());
      info.setDoi(info2.getDoi());
      info.setHandle(info2.getHandle());
      if (info2.getImages() != null && info2.getImages().getLarge() != null) {
        info.setImageLarge(info2.getImages().getLarge());
      } else {
        info.setImageLarge(info2.getImageLarge());
      }
      if (info2.getImages() != null && info2.getImages().getMedium() != null) {
        info.setImageMedium(info2.getImages().getMedium());
      } else {
        info.setImageMedium(info2.getImageMedium());
      }
      if (info2.getImages() != null && info2.getImages().getSmall() != null) {
        info.setImageSmall(info2.getImages().getSmall());
      } else {
        info.setImageSmall(info2.getImageSmall());
      }
      info.setIsOpenAccess(info2.getIsOpenAccess());
      info.setJournal(info2.getJournal());
      info.setLastUpdated(info2.getLastUpdated());
      info.setPublishedOn(info2.getPublishedOn());
      info.setScore(info2.getScore());
      info.setTitle(info2.getTitle());
      info.setType(info2.getTitle());
      info.setUri(info2.getUri());
      info.setUrl(info2.getUrl());

      if (info2.getReaders() != null && info2.getReaders().getMendeley() != null) {
        if (info.getReaders() == null) {
          MetadataReadersModel readers = new MetadataReadersModel();
          info.setReaders(readers);
        }
        info.getReaders().setMendeley(info2.getReaders().getMendeley());
      }

      this.response.setAltmetricInfo(info);
    }
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
      JsonElement response = this.readWOSDataFromClarisa();
      this.jsonStringResponse = StringUtils.stripToNull(new GsonBuilder().serializeNulls().create().toJson(response));
    } else if (!this.link.isEmpty() && this.link.contains("handle")) {
      JsonElement response = this.readWOSDataFromClarisa2();
      this.jsonStringResponse = StringUtils.stripToNull(new GsonBuilder().serializeNulls().create().toJson(response));
    }


  }

  private JsonElement readWOSDataFromClarisa() throws IOException {
    URL clarisaUrl = new URL(config.getClarisaWOSLink().replace("{1}", this.link));
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

  private JsonElement readWOSDataFromClarisa2() throws IOException {
    URL clarisaUrl = new URL(config.getClarisaWOSLink2().replace("{1}", this.link));
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
          && (incomingInstitutions.stream().filter(i -> i != null && i.getFullName() != null
            && StringUtils.equalsIgnoreCase(i.getFullName(), dbDeliverableAffiliation.getInstitutionNameWebOfScience())
            && i.getClarisaMatchConfidence() < APConstants.ACCEPTATION_PERCENTAGE).count() == 0)) {
          this.deliverableAffiliationManager.deleteDeliverableAffiliation(dbDeliverableAffiliation.getId());
          if (deliverable.getIsPublication() == null || deliverable.getIsPublication() == false) {
            this.deliverableAffiliationManager.replicate(dbDeliverableAffiliation,
              phase.getDescription().equals(APConstants.REPORTING) ? phase.getNext().getNext() : phase.getNext());
          }
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
                && nda.getInstitutionNameWebOfScience() != null && StringUtils
                  .equalsIgnoreCase(incomingAffiliation.getFullName(), nda.getInstitutionNameWebOfScience()))
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

          if (deliverable.getIsPublication() == null || deliverable.getIsPublication() == false) {
            this.deliverableAffiliationManager.replicate(newDeliverableAffiliation,
              phase.getDescription().equals(APConstants.REPORTING) ? phase.getNext().getNext() : phase.getNext());
          }
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
            .filter(i -> i != null && i.getFullName() != null
              && i.getFullName().equals(dbDeliverableAffiliationNotMapped.getPossibleInstitution().getName())
              && i.getClarisaMatchConfidence() >= APConstants.ACCEPTATION_PERCENTAGE)
            .count() == 0)) {
          this.deliverableAffiliationsNotMappedManager
            .deleteDeliverableAffiliationsNotMapped(dbDeliverableAffiliationNotMapped.getId());
          if (deliverable.getIsPublication() == null || deliverable.getIsPublication() == false) {
            this.deliverableAffiliationsNotMappedManager.replicate(dbDeliverableAffiliationNotMapped,
              phase.getDescription().equals(APConstants.REPORTING) ? phase.getNext().getNext() : phase.getNext());
          }
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
                  && nda.getName() != null
                  && StringUtils.equalsIgnoreCase(incomingAffiliation.getFullName(), nda.getName()))
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

          if (deliverable.getIsPublication() == null || deliverable.getIsPublication() == false) {
            this.deliverableAffiliationsNotMappedManager.replicate(newDeliverableAffiliationNotMapped,
              phase.getDescription().equals(APConstants.REPORTING) ? phase.getNext().getNext() : phase.getNext());
          }
        }
      }
    }
  }

  private void saveAltmetricInfo(Phase phase, Deliverable deliverable) {
    DeliverableAltmetricInfo altmetricInfo =
      this.deliverableAltmetricInfoManager.findByPhaseAndDeliverable(phase, deliverable);
    MetadataAltmetricModel incomingAltmetricInfo = null;
    incomingAltmetricInfo = this.response.getAltmetricInfo();

    if (incomingAltmetricInfo != null) {
      if (altmetricInfo == null) {
        altmetricInfo = new DeliverableAltmetricInfo();
        altmetricInfo.setDeliverable(deliverable);
        altmetricInfo.setPhase(phase);
        altmetricInfo.setCreatedBy(this.getCurrentUser());
        altmetricInfo.setLastSync(new Date());
      }

      altmetricInfo.setActive(true);
      altmetricInfo.setAddedOn(
        incomingAltmetricInfo.getAddedOn() != null ? new Date(incomingAltmetricInfo.getAddedOn() * 1000L) : null);
      altmetricInfo.setAltmetricId(incomingAltmetricInfo.getAltmetricId());
      altmetricInfo.setAltmetricJid(incomingAltmetricInfo.getAltmetricJid());
      altmetricInfo.setAuthors(incomingAltmetricInfo.getAuthors() != null
        ? incomingAltmetricInfo.getAuthors().stream().filter(StringUtils::isNotBlank).collect(Collectors.joining("; "))
        : null);
      altmetricInfo.setCitedByBlogs(incomingAltmetricInfo.getCitedByBlogs());
      altmetricInfo.setCitedByDelicious(incomingAltmetricInfo.getCitedByDelicious());
      altmetricInfo.setCitedByFacebookPages(incomingAltmetricInfo.getCitedByFacebookPages());
      altmetricInfo.setCitedByForumUsers(incomingAltmetricInfo.getCitedByForumUsers());
      altmetricInfo.setCitedByGooglePlusUsers(incomingAltmetricInfo.getCitedByGooglePlusUsers());
      altmetricInfo.setCitedByLinkedinUsers(incomingAltmetricInfo.getCitedByLinkedinUsers());
      altmetricInfo.setCitedByNewsOutlets(incomingAltmetricInfo.getCitedByNewsOutlets());
      altmetricInfo.setCitedByPeerReviewSites(incomingAltmetricInfo.getCitedByPeerReviewSites());
      altmetricInfo.setCitedByPinterestUsers(incomingAltmetricInfo.getCitedByPinterestUsers());
      altmetricInfo.setCitedByPolicies(incomingAltmetricInfo.getCitedByPolicies());
      altmetricInfo.setCitedByPosts(incomingAltmetricInfo.getCitedByPosts());
      altmetricInfo.setCitedByRedditUsers(incomingAltmetricInfo.getCitedByRedditUsers());
      altmetricInfo.setCitedByResearchHighlightPlatforms(incomingAltmetricInfo.getCitedByResearchHighlightPlatforms());
      altmetricInfo.setCitedByStackExchangeResources(incomingAltmetricInfo.getCitedByStackExchangeResources());
      altmetricInfo.setCitedByTwitterUsers(incomingAltmetricInfo.getCitedByTwitterUsers());
      altmetricInfo.setCitedByWeiboUsers(incomingAltmetricInfo.getCitedByWeiboUsers());
      altmetricInfo.setCitedByWikipediaPages(incomingAltmetricInfo.getCitedByWikipediaPages());
      altmetricInfo.setCitedByYoutubeChannels(incomingAltmetricInfo.getCitedByYoutubeChannels());
      altmetricInfo.setDetailsUrl(this.link);
      altmetricInfo.setDoi(incomingAltmetricInfo.getDoi());
      altmetricInfo.setHandle(incomingAltmetricInfo.getHandle());
      if (incomingAltmetricInfo.getImages() != null && incomingAltmetricInfo.getImages().getLarge() != null) {
        altmetricInfo.setImageLarge(incomingAltmetricInfo.getImages().getLarge());
      } else {
        altmetricInfo.setImageLarge(incomingAltmetricInfo.getImageLarge());
      }
      if (incomingAltmetricInfo.getImages() != null && incomingAltmetricInfo.getImages().getMedium() != null) {
        altmetricInfo.setImageMedium(incomingAltmetricInfo.getImages().getMedium());
      } else {
        altmetricInfo.setImageMedium(incomingAltmetricInfo.getImageMedium());
      }
      if (incomingAltmetricInfo.getImages() != null && incomingAltmetricInfo.getImages().getSmall() != null) {
        altmetricInfo.setImageSmall(incomingAltmetricInfo.getImages().getSmall());
      } else {
        altmetricInfo.setImageSmall(incomingAltmetricInfo.getImageSmall());
      }
      altmetricInfo.setIsOpenAccess(incomingAltmetricInfo.getIsOpenAccess());
      altmetricInfo.setJournal(incomingAltmetricInfo.getJournal());
      altmetricInfo.setLastUpdated(incomingAltmetricInfo.getLastUpdated() != null
        ? new Date(incomingAltmetricInfo.getLastUpdated() * 1000L) : null);
      altmetricInfo.setModifiedBy(this.getCurrentUser());
      altmetricInfo.setPublishedOn(incomingAltmetricInfo.getPublishedOn() != null
        ? new Date(incomingAltmetricInfo.getPublishedOn() * 1000L) : null);
      altmetricInfo.setScore(incomingAltmetricInfo.getScore());
      altmetricInfo.setTitle(incomingAltmetricInfo.getTitle());
      altmetricInfo.setType(incomingAltmetricInfo.getTitle());
      altmetricInfo.setUri(incomingAltmetricInfo.getUri());
      altmetricInfo.setUrl(incomingAltmetricInfo.getUrl());
      if (incomingAltmetricInfo.getReaders() != null) {
        altmetricInfo.setMendeleyReaders(incomingAltmetricInfo.getReaders().getMendeley());
      }
      altmetricInfo = this.deliverableAltmetricInfoManager.saveDeliverableAltmetricInfo(altmetricInfo);

      if (deliverable.getIsPublication() == null || deliverable.getIsPublication() == false) {
        this.deliverableAltmetricInfoManager.replicate(altmetricInfo,
          phase.getDescription().equals(APConstants.REPORTING) ? phase.getNext().getNext() : phase.getNext());
      }
    } else {
      this.saveAltmetricInfo2(phase, deliverable);
    }
  }

  private void saveAltmetricInfo2(Phase phase, Deliverable deliverable) {
    DeliverableAltmetricInfo altmetricInfo =
      this.deliverableAltmetricInfoManager.findByPhaseAndDeliverable(phase, deliverable);
    MetadataAltmetricModel2 incomingAltmetricInfo = null;
    if (this.response != null && this.response.getAltmetricInfo2() != null) {
      incomingAltmetricInfo = this.response.getAltmetricInfo2();
    }
    if (incomingAltmetricInfo != null) {
      if (altmetricInfo == null) {
        altmetricInfo = new DeliverableAltmetricInfo();
        altmetricInfo.setDeliverable(deliverable);
        altmetricInfo.setPhase(phase);
        altmetricInfo.setCreatedBy(this.getCurrentUser());
      }
      altmetricInfo.setLastSync(new Date());
      altmetricInfo.setActive(true);
      altmetricInfo.setAddedOn(
        incomingAltmetricInfo.getAddedOn() != null ? new Date(incomingAltmetricInfo.getAddedOn() * 1000L) : null);
      altmetricInfo.setAltmetricId(incomingAltmetricInfo.getAltmetricId());
      altmetricInfo.setAltmetricJid(incomingAltmetricInfo.getAltmetricJid());
      altmetricInfo.setAuthors(incomingAltmetricInfo.getAuthors() != null
        ? incomingAltmetricInfo.getAuthors().stream().filter(StringUtils::isNotBlank).collect(Collectors.joining("; "))
        : null);
      altmetricInfo.setCitedByBlogs(incomingAltmetricInfo.getCitedByBlogs());
      altmetricInfo.setCitedByDelicious(incomingAltmetricInfo.getCitedByDelicious());
      altmetricInfo.setCitedByFacebookPages(incomingAltmetricInfo.getCitedByFacebookPages());
      altmetricInfo.setCitedByForumUsers(incomingAltmetricInfo.getCitedByForumUsers());
      altmetricInfo.setCitedByGooglePlusUsers(incomingAltmetricInfo.getCitedByGooglePlusUsers());
      altmetricInfo.setCitedByLinkedinUsers(incomingAltmetricInfo.getCitedByLinkedinUsers());
      altmetricInfo.setCitedByNewsOutlets(incomingAltmetricInfo.getCitedByNewsOutlets());
      altmetricInfo.setCitedByPeerReviewSites(incomingAltmetricInfo.getCitedByPeerReviewSites());
      altmetricInfo.setCitedByPinterestUsers(incomingAltmetricInfo.getCitedByPinterestUsers());
      altmetricInfo.setCitedByPolicies(incomingAltmetricInfo.getCitedByPolicies());
      altmetricInfo.setCitedByPosts(incomingAltmetricInfo.getCitedByPosts());
      altmetricInfo.setCitedByRedditUsers(incomingAltmetricInfo.getCitedByRedditUsers());
      altmetricInfo.setCitedByResearchHighlightPlatforms(incomingAltmetricInfo.getCitedByResearchHighlightPlatforms());
      altmetricInfo.setCitedByStackExchangeResources(incomingAltmetricInfo.getCitedByStackExchangeResources());
      altmetricInfo.setCitedByTwitterUsers(incomingAltmetricInfo.getCitedByTwitterUsers());
      altmetricInfo.setCitedByWeiboUsers(incomingAltmetricInfo.getCitedByWeiboUsers());
      altmetricInfo.setCitedByWikipediaPages(incomingAltmetricInfo.getCitedByWikipediaPages());
      altmetricInfo.setCitedByYoutubeChannels(incomingAltmetricInfo.getCitedByYoutubeChannels());
      altmetricInfo.setDetailsUrl(this.link);
      altmetricInfo.setDoi(incomingAltmetricInfo.getDoi());
      altmetricInfo.setHandle(incomingAltmetricInfo.getHandle());
      if (incomingAltmetricInfo.getImages() != null && incomingAltmetricInfo.getImages().getLarge() != null) {
        altmetricInfo.setImageLarge(incomingAltmetricInfo.getImages().getLarge());
      } else {
        altmetricInfo.setImageLarge(incomingAltmetricInfo.getImageLarge());
      }
      if (incomingAltmetricInfo.getImages() != null && incomingAltmetricInfo.getImages().getMedium() != null) {
        altmetricInfo.setImageMedium(incomingAltmetricInfo.getImages().getMedium());
      } else {
        altmetricInfo.setImageMedium(incomingAltmetricInfo.getImageMedium());
      }
      if (incomingAltmetricInfo.getImages() != null && incomingAltmetricInfo.getImages().getSmall() != null) {
        altmetricInfo.setImageSmall(incomingAltmetricInfo.getImages().getSmall());
      } else {
        altmetricInfo.setImageSmall(incomingAltmetricInfo.getImageSmall());
      }
      altmetricInfo.setIsOpenAccess(incomingAltmetricInfo.getIsOpenAccess());
      altmetricInfo.setJournal(incomingAltmetricInfo.getJournal());
      altmetricInfo.setLastUpdated(incomingAltmetricInfo.getLastUpdated() != null
        ? new Date(incomingAltmetricInfo.getLastUpdated() * 1000L) : null);
      altmetricInfo.setModifiedBy(this.getCurrentUser());
      altmetricInfo.setPublishedOn(incomingAltmetricInfo.getPublishedOn() != null
        ? new Date(incomingAltmetricInfo.getPublishedOn() * 1000L) : null);
      altmetricInfo.setScore(incomingAltmetricInfo.getScore());
      altmetricInfo.setTitle(incomingAltmetricInfo.getTitle());
      altmetricInfo.setType(incomingAltmetricInfo.getTitle());
      altmetricInfo.setUri(incomingAltmetricInfo.getUri());
      altmetricInfo.setUrl(incomingAltmetricInfo.getUrl());
      if (incomingAltmetricInfo.getReaders() != null) {
        altmetricInfo.setMendeleyReaders(incomingAltmetricInfo.getReaders().getMendeley());
      }

      this.manualSetAlmetricInfo();
      altmetricInfo = this.deliverableAltmetricInfoManager.saveDeliverableAltmetricInfo(altmetricInfo);
      if (deliverable.getIsPublication() == null || deliverable.getIsPublication() == false) {
        this.deliverableAltmetricInfoManager.replicate(altmetricInfo,
          phase.getDescription().equals(APConstants.REPORTING) ? phase.getNext().getNext() : phase.getNext());
      }
    }
  }

  private void saveExternalSourceAuthors(Phase phase, Deliverable deliverable) {
    DeliverableMetadataExternalSources externalSource =
      this.deliverableMetadataExternalSourcesManager.findByPhaseAndDeliverable(phase, deliverable);
    List<WOSAuthor> incomingAuthors = this.response.getAuthors();

    if (incomingAuthors != null) {
      // we are going to remove all of them and just accept the incoming authors
      this.externalSourceAuthorManager.deleteAllAuthorsFromPhase(deliverable, phase);

      // save
      for (WOSAuthor incomingAuthor : incomingAuthors) {
        ExternalSourceAuthor externalSourceAuthor = new ExternalSourceAuthor();
        externalSourceAuthor.setCreateDate(new Date());
        externalSourceAuthor.setCreatedBy(this.getCurrentUser());
        externalSourceAuthor.setDeliverableMetadataExternalSources(externalSource);
        externalSourceAuthor.setFullName(incomingAuthor.getFullName());

        externalSourceAuthor = this.externalSourceAuthorManager.saveExternalSourceAuthor(externalSourceAuthor);

        if (deliverable.getIsPublication() == null || deliverable.getIsPublication() == false) {
          this.externalSourceAuthorManager.replicate(externalSourceAuthor,
            phase.getDescription().equals(APConstants.REPORTING) ? phase.getNext().getNext() : phase.getNext());
        }
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
    externalSource.setOpenAccessStatus(this.getBooleanStringOrNotAvailable(this.response.getIsOpenAccess()));
    externalSource.setOpenAccessLink(this.response.getOpenAcessLink());
    externalSource.setIsiStatus(this.getBooleanStringOrNotAvailable(this.response.getIsISI()));
    externalSource.setJournalName(this.response.getJournalName());
    externalSource.setVolume(this.response.getVolume());
    externalSource.setIssue(this.response.getIssue());
    externalSource.setPages(this.response.getPages());
    externalSource.setSource(this.response.getSource());

    if (gardianInfo != null) {
      externalSource.setGardianFindability(gardianInfo.getFindability());
      externalSource.setGardianAccessibility(gardianInfo.getAccessibility());
      externalSource.setGardianInteroperability(gardianInfo.getInteroperability());
      externalSource.setGardianReusability(gardianInfo.getReusability());
      externalSource.setGardianTitle(gardianInfo.getTitle());
    }

    externalSource =
      this.deliverableMetadataExternalSourcesManager.saveDeliverableMetadataExternalSources(externalSource);

    if (deliverable.getIsPublication() == null || deliverable.getIsPublication() == false) {
      this.deliverableMetadataExternalSourcesManager.replicate(externalSource,
        phase.getDescription().equals(APConstants.REPORTING) ? phase.getNext().getNext() : phase.getNext());
    }
  }

  private void saveInfo() {
    Deliverable deliverable = this.deliverableManager.getDeliverableById(this.deliverableId);
    Phase phase = this.phaseManager.getPhaseById(this.phaseId);

    this.saveExternalSources(phase, deliverable);
    this.saveAffiliations(phase, deliverable);
    this.saveAffiliationsNotMapped(phase, deliverable);
    this.saveExternalSourceAuthors(phase, deliverable);
    this.saveAltmetricInfo(phase, deliverable);
  }

  /**
   * This method is created ONLY to be used for the deliverables bulk sync
   * 
   * @param phase the phase the sync info is going to be saved
   * @param deliverableId the deliverableId
   * @param link the DOI/URL link to be used for the metadata harvesting
   * @return
   * @throws IOException
   */
  public boolean saveInfo(Long phaseId, Long deliverableId, String link) throws IOException {
    this.phaseId = phaseId;
    this.deliverableId = deliverableId;
    this.link = link;

    this.response = new Gson().fromJson(this.readWOSDataFromClarisa(), MetadataWOSModel.class);


    if (this.response != null) {
      this.saveInfo();
      // return true;
    }
    MetadataAltmetricModel incomingAltmetricInfoTemp = null;
    if (this.response != null && this.response.getAltmetricInfo() != null) {
      incomingAltmetricInfoTemp = this.response.getAltmetricInfo();
    }
    if (incomingAltmetricInfoTemp == null) {
      this.response = new Gson().fromJson(this.readWOSDataFromClarisa2(), MetadataWOSModel.class);
    }

    if (this.response != null) {
      Deliverable deliverable = this.deliverableManager.getDeliverableById(this.deliverableId);
      Phase phase = this.phaseManager.getPhaseById(this.phaseId);
      this.saveAltmetricInfo(phase, deliverable);
      return true;
    }


    return false;
  }
}