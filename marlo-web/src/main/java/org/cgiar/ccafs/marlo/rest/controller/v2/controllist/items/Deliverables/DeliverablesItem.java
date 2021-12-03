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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.Deliverables;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.DeliverableAffiliationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableAffiliationsNotMappedManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableAltmetricInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableDisseminationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataExternalSourcesManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePublicationMetadataManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserManager;
import org.cgiar.ccafs.marlo.data.manager.ExternalSourceAuthorManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.MetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressDeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliation;
import org.cgiar.ccafs.marlo.data.model.DeliverableAffiliationsNotMapped;
import org.cgiar.ccafs.marlo.data.model.DeliverableAltmetricInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataExternalSources;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.ExternalSourceAuthor;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.MetadataElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Publication;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressDeliverable;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.DeliverableUserDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewPublicationDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewPublicationOtherDTO;
import org.cgiar.ccafs.marlo.rest.dto.PublicationDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.DeliverablesMapper;
import org.cgiar.ccafs.marlo.rest.mappers.PublicationsMapper;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.PublicationAuthorWOS;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.PublicationInstitutionWOS;
import org.cgiar.ccafs.marlo.rest.services.deliverables.model.PublicationWOS;
import org.cgiar.ccafs.marlo.utils.doi.DOIService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.ibm.icu.util.Calendar;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class DeliverablesItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private MetadataElementManager metadataElementManager;
  private DeliverableManager deliverableManager;
  private DeliverableTypeManager deliverableTypeManager;
  private DeliverableInfoManager deliverableInfoManager;
  private DeliverablePublicationMetadataManager deliverablePubMetadataManager;
  private DeliverableDisseminationManager deliverableDisseminationManager;
  private DeliverableMetadataElementManager deliverableMetadataElementManager;
  private DeliverableUserManager deliverableUserManager;
  private DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager;
  private DeliverableAffiliationManager deliverableAffiliationManager;
  private DeliverableAffiliationsNotMappedManager deliverableAffiliationsNotMappedManager;
  private InstitutionManager institutionManager;
  private ExternalSourceAuthorManager externalSourceAuthorManager;
  private DeliverableAltmetricInfoManager deliverableAltmetricInfoManager;


  private DeliverablesMapper deliverablesMapper;
  private PublicationsMapper publicationsMapper;

  // changes to be included to Synthesis
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressDeliverableManager reportSynthesisFlagshipProgressDelvierableManager;


  @Inject
  public DeliverablesItem(PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    MetadataElementManager metadataElementManager, DeliverableManager deliverableManager,
    DeliverableTypeManager deliverableTypeManager, DeliverableInfoManager deliverableInfoManager,
    DeliverablePublicationMetadataManager deliverablePubMetadataManager,
    DeliverableMetadataElementManager deliverableMetadataElementManager, DeliverableUserManager deliverableUserManager,
    DeliverableDisseminationManager deliverableDisseminationManager, DeliverablesMapper deliverablesMapper,
    PublicationsMapper publicationsMapper,
    DeliverableMetadataExternalSourcesManager deliverableMetadataExternalSourcesManager,
    DeliverableAffiliationManager deliverableAffiliationManager,
    DeliverableAffiliationsNotMappedManager deliverableAffiliationsNotMappedManager,
    InstitutionManager institutionManager, ExternalSourceAuthorManager externalSourceAuthorManager,
    DeliverableAltmetricInfoManager deliverableAltmetricInfoManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFlagshipProgressDeliverableManager reportSynthesisFlagshipProgressDelvierableManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.metadataElementManager = metadataElementManager;
    this.deliverableManager = deliverableManager;
    this.deliverableTypeManager = deliverableTypeManager;
    this.deliverableInfoManager = deliverableInfoManager;
    this.deliverablePubMetadataManager = deliverablePubMetadataManager;
    this.deliverableMetadataElementManager = deliverableMetadataElementManager;
    this.deliverableUserManager = deliverableUserManager;
    this.deliverableDisseminationManager = deliverableDisseminationManager;
    this.deliverablesMapper = deliverablesMapper;
    this.publicationsMapper = publicationsMapper;
    this.deliverableMetadataExternalSourcesManager = deliverableMetadataExternalSourcesManager;
    this.deliverableAffiliationManager = deliverableAffiliationManager;
    this.deliverableAffiliationsNotMappedManager = deliverableAffiliationsNotMappedManager;
    this.institutionManager = institutionManager;
    this.externalSourceAuthorManager = externalSourceAuthorManager;
    this.deliverableAltmetricInfoManager = deliverableAltmetricInfoManager;
    this.reportSynthesisFlagshipProgressDelvierableManager = reportSynthesisFlagshipProgressDelvierableManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
  }

  public Long createDeliverable(NewPublicationDTO deliverableDTO, String entityAcronym, User user) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Long deliverablesID;
    Deliverable deliverable = new Deliverable();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createDeliverable", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == deliverableDTO.getPhase().getYear()
        && c.getName().equalsIgnoreCase(deliverableDTO.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors
        .add(new FieldErrorDTO("createDeliverable", "phase", deliverable.getPhase().getYear() + " is an invalid year"));
    }
    if (phase != null && !phase.getEditable()) {
      fieldErrors.add(new FieldErrorDTO("createDeliverable", "phase", "This phase is closed"));
    }

    if (fieldErrors.size() == 0 || fieldErrors.isEmpty()) {
      // create deliverable
      deliverable.setCrp(globalUnitEntity);
      deliverable.setPhase(phase);
      deliverable.setIsPublication(true);
      // save deliverables
      deliverable = deliverableManager.saveDeliverable(deliverable);
      deliverablesID = deliverable.getId();
      if (deliverablesID != null) {
        DeliverableType deliverableType =
          deliverableTypeManager.getDeliverableTypeById(APConstants.IMPORT_DELIVERABLE_VALUE);
        // create deliverable info
        deliverable = deliverableManager.getDeliverableById(deliverablesID);
        DeliverableInfo deliverableInfo = new DeliverableInfo();
        deliverableInfo.setDeliverable(deliverable);
        deliverableInfo.setTitle(deliverableDTO.getTitle()); //
        // this.deliverableinfo.setDescription(deliverable.getTitle());
        deliverableInfo.setYear(deliverable.getPhase().getYear());
        deliverableInfo.setDeliverableType(deliverableType);
        deliverableInfo.setPhase(phase);

        deliverableInfo.setStatus(APConstants.CLARISA_PUBLICATIONS_STATUS);
        deliverableInfo.setModificationJustification(APConstants.MESSAGE_MODIFICATION_JUSTIFICATION
          + sdf.format(new Date(Calendar.getInstance().getTimeInMillis())));

        // save deliverableinfo

        deliverableInfoManager.saveDeliverableInfo(deliverableInfo);
        // create deliverable dissemination data

        DeliverableDissemination deliverableDissemination = new DeliverableDissemination();
        deliverableDissemination.setIsOpenAccess(deliverableDTO.getIsOpenAccess());
        deliverableDissemination.setDeliverable(deliverable);
        deliverableDissemination.setArticleUrl(deliverableDTO.getArticleURL());
        deliverableDissemination.setDisseminationChannel("other");
        deliverableDissemination.setDisseminationUrl("Not Defined");
        if ((deliverableDTO.getDoi() == null || deliverableDTO.getDoi().equals(""))
          && deliverableDTO.getArticleURL() != null && !deliverableDTO.getArticleURL().isEmpty()) {
          deliverableDissemination.setHasDOI(true);
        }
        deliverableDissemination.setPhase(phase);
        deliverableDisseminationManager.saveDeliverableDissemination(deliverableDissemination);

        // creatte deliverable publication metadata
        DeliverablePublicationMetadata deliverablePublicationMetadata = new DeliverablePublicationMetadata();
        deliverablePublicationMetadata.setDeliverable(deliverable);
        deliverablePublicationMetadata.setIssue(deliverableDTO.getIssue());
        deliverablePublicationMetadata.setJournal(deliverableDTO.getJournal());
        deliverablePublicationMetadata.setPages(deliverableDTO.getNpages());
        deliverablePublicationMetadata.setVolume(deliverableDTO.getVolume());
        deliverablePublicationMetadata.setIsiPublication(deliverableDTO.getIsISIJournal());


        deliverablePublicationMetadata.setPhase(phase);
        // save deliverablePublicationMetadata

        deliverablePubMetadataManager.saveDeliverablePublicationMetadata(deliverablePublicationMetadata);
        // get element ID from

        // econded_name to get Handle and DOI and create
        List<MetadataElement> metadataElements = new ArrayList<MetadataElement>(metadataElementManager.findAll());
        if (metadataElements != null) {


          // deliverable metadataelement handle
          DeliverableMetadataElement deliverableMetadataElementHandle = new DeliverableMetadataElement();
          deliverableMetadataElementHandle.setDeliverable(deliverable);
          deliverableMetadataElementHandle.setPhase(phase);
          MetadataElement metadataElementHandle =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTHANDLE))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementHandle.setMetadataElement(metadataElementHandle);
          deliverableMetadataElementHandle
            .setElementValue(deliverableDTO.getHandle() == null ? "" : deliverableDTO.getHandle());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementHandle);

          // deliverable metadataelement DOI
          DeliverableMetadataElement deliverableMetadataElementDoi = new DeliverableMetadataElement();
          deliverableMetadataElementDoi.setDeliverable(deliverable);
          deliverableMetadataElementDoi.setPhase(phase);
          MetadataElement metadataElementDoi =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTDOI))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementDoi.setMetadataElement(metadataElementDoi);
          deliverableMetadataElementDoi
            .setElementValue(deliverableDTO.getDoi() == null ? "" : DOIService.tryGetDoiName(deliverableDTO.getDoi()));
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementDoi);

          // deliverable metadataelement Title
          DeliverableMetadataElement deliverableMetadataElementTitle = new DeliverableMetadataElement();
          deliverableMetadataElementTitle.setDeliverable(deliverable);
          deliverableMetadataElementTitle.setPhase(phase);
          MetadataElement metadataElementTitle =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTTITLE))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementTitle.setMetadataElement(metadataElementTitle);
          deliverableMetadataElementTitle
            .setElementValue(deliverableDTO.getTitle() == null ? "" : deliverableDTO.getTitle());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementTitle);

          // deliverable metadataelement Citation
          DeliverableMetadataElement deliverableMetadataElementCitation = new DeliverableMetadataElement();
          deliverableMetadataElementCitation.setDeliverable(deliverable);
          deliverableMetadataElementCitation.setPhase(phase);
          MetadataElement metadataElementCitation =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTCITATION))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementCitation.setMetadataElement(metadataElementCitation);
          String authors = "";
          for (DeliverableUserDTO author : deliverableDTO.getAuthorList()) {
            if (authors.isEmpty()) {
              authors = authors + "" + author.getLastName() + " " + author.getFirstName();
            } else {
              authors = authors + "," + author.getLastName() + " " + author.getFirstName();
            }
          }
          // if authors not been saved by list, citation can be udapte by authors single row field
          if (authors.isEmpty() && deliverableDTO.getAuthors() != null
            && !deliverableDTO.getAuthors().trim().isEmpty()) {
            authors = deliverableDTO.getAuthors().trim();
          }

          deliverableMetadataElementCitation.setElementValue(authors + (authors.isEmpty() ? "" : ",")
            + deliverableDTO.getYear() + "," + deliverableDTO.getTitle() + "," + deliverableDTO.getJournal() + ","
            + deliverableDTO.getVolume() + "," + deliverableDTO.getIssue() + "," + deliverableDTO.getNpages());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementCitation);

          // deliverable metadataelement Publication
          DeliverableMetadataElement deliverableMetadataElementPublication = new DeliverableMetadataElement();
          deliverableMetadataElementPublication.setDeliverable(deliverable);
          deliverableMetadataElementPublication.setPhase(phase);
          MetadataElement metadataElementPublication =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTPUBLICATION))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementPublication.setMetadataElement(metadataElementPublication);
          deliverableMetadataElementPublication.setElementValue(String.valueOf(deliverableDTO.getYear()));
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementPublication);


          // changes for authors 2019-09-03 apply to a field to include all authors in a single row
          // setdeliverables author
          DeliverableMetadataElement deliverableMetadataElementAuthors = new DeliverableMetadataElement();
          deliverableMetadataElementAuthors.setDeliverable(deliverable);
          deliverableMetadataElementAuthors.setPhase(phase);
          MetadataElement metadataElementAuthors =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTAUTHORS))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementAuthors.setMetadataElement(metadataElementAuthors);
          deliverableMetadataElementAuthors.setElementValue(String.valueOf(deliverableDTO.getAuthors()));
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementAuthors);
        }

        for (DeliverableUserDTO deliverableUserDTO : deliverableDTO.getAuthorList()) {
          DeliverableUser deliverableUser = new DeliverableUser();
          deliverableUser.setFirstName(deliverableUserDTO.getFirstName());
          deliverableUser.setLastName(deliverableUserDTO.getLastName());
          deliverableUser.setDeliverable(deliverable);
          deliverableUser.setPhase(phase);
          deliverableUserManager.saveDeliverableUser(deliverableUser);
        }

        // verify if was included in synthesis PMU
        LiaisonInstitution liaisonInstitution =
          this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
        if (liaisonInstitution != null) {
          boolean existing = true;
          ReportSynthesis reportSynthesis =
            reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
          if (reportSynthesis != null) {
            ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress =
              reportSynthesis.getReportSynthesisFlagshipProgress();
            if (reportSynthesisFlagshipProgress == null) {
              reportSynthesisFlagshipProgress = new ReportSynthesisFlagshipProgress();
              reportSynthesisFlagshipProgress.setReportSynthesis(reportSynthesis);
              reportSynthesisFlagshipProgress.setCreatedBy(user);
              existing = false;
              reportSynthesisFlagshipProgress = reportSynthesisFlagshipProgressManager
                .saveReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
            }
            final Long publicationID = deliverablesID;
            ReportSynthesisFlagshipProgressDeliverable reportSynthesisFlagshipProgressDeliverable =
              reportSynthesisFlagshipProgress.getReportSynthesisFlagshipProgressDeliverables().stream()
                .filter(c -> c.isActive() && c.getDeliverable().getId().longValue() == publicationID).findFirst()
                .orElse(null);
            if (reportSynthesisFlagshipProgressDeliverable != null && existing) {
              reportSynthesisFlagshipProgressDeliverable = reportSynthesisFlagshipProgressDelvierableManager
                .getReportSynthesisFlagshipProgressDeliverableById(reportSynthesisFlagshipProgressDeliverable.getId());
              reportSynthesisFlagshipProgressDeliverable.setActive(false);
              reportSynthesisFlagshipProgressDeliverable = reportSynthesisFlagshipProgressDelvierableManager
                .saveReportSynthesisFlagshipProgressDeliverable(reportSynthesisFlagshipProgressDeliverable);
            } else {
              reportSynthesisFlagshipProgressDeliverable = new ReportSynthesisFlagshipProgressDeliverable();
              reportSynthesisFlagshipProgressDeliverable.setCreatedBy(user);
              reportSynthesisFlagshipProgressDeliverable.setDeliverable(deliverable);
              reportSynthesisFlagshipProgressDeliverable
                .setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
              reportSynthesisFlagshipProgressDeliverable = reportSynthesisFlagshipProgressDelvierableManager
                .saveReportSynthesisFlagshipProgressDeliverable(reportSynthesisFlagshipProgressDeliverable);
              reportSynthesisFlagshipProgressDeliverable.setActive(false);
              reportSynthesisFlagshipProgressDelvierableManager
                .saveReportSynthesisFlagshipProgressDeliverable(reportSynthesisFlagshipProgressDeliverable);
            }
          }
        }

        // web of science integration
        if (deliverableDTO.getDoi() != null) {
          try {
            JsonElement json = this.getServiceWOS(
              "http://clarisa.wos.api.mel.cgiar.org/?link=" + DOIService.tryGetDoiName(deliverableDTO.getDoi()));
            System.out.println(json.toString());

            PublicationWOS publication = new Gson().fromJson(json, PublicationWOS.class);
            if (publication != null) {
              // save deliverable metadata external sources WOS and GARDIAN
              final Long deliverableID = deliverable.getId();
              DeliverableMetadataExternalSources deliverableMetadataExternalSources =
                deliverable.getDeliverableMetadataExternalSources().stream()
                  .filter(c -> c.getDeliverable().getId().equals(deliverableID) && c.getPhase().getId().equals(phase))
                  .findFirst().orElse(null);
              if (deliverableMetadataExternalSources == null) {
                deliverableMetadataExternalSources = new DeliverableMetadataExternalSources();
                deliverableMetadataExternalSources.setDeliverable(deliverable);
                deliverableMetadataExternalSources.setDoi(publication.getDoi());

                deliverableMetadataExternalSources.setIsiStatus(this.getBooleanString(publication.getIs_isi()));
                deliverableMetadataExternalSources.setJournalName(publication.getJournal_name());
                deliverableMetadataExternalSources.setTitle(publication.getTitle());
                deliverableMetadataExternalSources.setOpenAccessStatus(this.getBooleanString(publication.getIs_oa()));
                deliverableMetadataExternalSources.setOpenAccessLink(publication.getOa_link());
                deliverableMetadataExternalSources.setPublicationType(publication.getPublication_type());
                deliverableMetadataExternalSources.setPublicationYear(publication.getPublication_year() != null
                  ? Integer.valueOf(publication.getPublication_year()) : null);
                deliverableMetadataExternalSources.setSource(publication.getSource());
                deliverableMetadataExternalSources.setUrl(publication.getDoi());
                deliverableMetadataExternalSources.setPages(publication.getStart_end_pages());
                deliverableMetadataExternalSources.setPhase(phase);
                deliverableMetadataExternalSources.setCreatedBy(user);
                deliverableMetadataExternalSources.setVolume(publication.getVolume());
                if (publication.getGardian() != null) {
                  deliverableMetadataExternalSources
                    .setGardianAccessibility(publication.getGardian().getAccessibility());
                  deliverableMetadataExternalSources.setGardianFindability(publication.getGardian().getFindability());
                  deliverableMetadataExternalSources
                    .setGardianInteroperability(publication.getGardian().getInteroperability());
                  deliverableMetadataExternalSources.setGardianReusability(publication.getGardian().getReusability());
                }
                deliverableMetadataExternalSources = deliverableMetadataExternalSourcesManager
                  .saveDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                // deliverableMetadataExternalSourcesManager.replicate(deliverableMetadataExternalSources, phase);

                // save institutions with a percentage above APCONSTANT percentage acceptance in deliverable affiliation
                // save institutions with a percentage below APCONSTANT percentage acceptance in deliverable affiliation
                // not mapped
                for (PublicationInstitutionWOS institution : publication.getOrganizations()) {
                  if (institution.getConfidant() != null
                    && institution.getConfidant().longValue() >= APConstants.ACCEPTATION_PERCENTAGE) {
                    DeliverableAffiliation deliverableAffiliation = new DeliverableAffiliation();
                    deliverableAffiliation.setCreatedBy(user);
                    Institution institutionAffiliation =
                      institutionManager.getInstitutionById(institution.getClarisa_id());
                    deliverableAffiliation.setInstitution(institutionAffiliation);
                    deliverableAffiliation.setInstitutionMatchConfidence(institution.getConfidant().intValue());
                    deliverableAffiliation.setDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                    deliverableAffiliation.setInstitutionNameWebOfScience(institution.getName());
                    deliverableAffiliation.setPhase(phase);
                    deliverableAffiliation.setDeliverable(deliverable);
                    deliverableAffiliation =
                      deliverableAffiliationManager.saveDeliverableAffiliation(deliverableAffiliation);
                    // deliverableAffiliationManager.replicate(deliverableAffiliation, phase);
                  }
                  if (institution.getConfidant() != null
                    && (institution.getConfidant().longValue() < APConstants.ACCEPTATION_PERCENTAGE
                      || institution.getConfidant() == null)) {
                    DeliverableAffiliationsNotMapped deliverableAffiliationsNotMapped =
                      new DeliverableAffiliationsNotMapped();
                    deliverableAffiliationsNotMapped.setCountry(institution.getCountry());
                    deliverableAffiliationsNotMapped
                      .setDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                    deliverableAffiliationsNotMapped
                      .setInstitutionMatchConfidence(institution.getConfidant().intValue());
                    deliverableAffiliationsNotMapped.setName(institution.getName());
                    deliverableAffiliationsNotMapped.setFullAddress(institution.getFull_address());
                    deliverableAffiliationsNotMapped.setPossibleInstitution(institution.getClarisa_id() != null
                      ? institutionManager.getInstitutionById(institution.getClarisa_id()) : null);
                    deliverableAffiliationsNotMapped = deliverableAffiliationsNotMappedManager
                      .saveDeliverableAffiliationsNotMapped(deliverableAffiliationsNotMapped);
                    // deliverableAffiliationsNotMappedManager.replicate(deliverableAffiliationsNotMapped, phase);
                  }
                }
                // save authors of WOS external sources authors


                if (publication.getAuthors() != null) {
                  for (PublicationAuthorWOS author : publication.getAuthors()) {
                    ExternalSourceAuthor externalSourceAuthor = new ExternalSourceAuthor();
                    externalSourceAuthor.setDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                    externalSourceAuthor.setCreatedBy(user);
                    externalSourceAuthor.setFullName(author.getFull_name());
                    externalSourceAuthor = externalSourceAuthorManager.saveExternalSourceAuthor(externalSourceAuthor);
                    // externalSourceAuthorManager.replicate(externalSourceAuthor, phase);
                  }
                }
                // save altmetrics information in deliverable altmetrics
                if (publication.getAltmetric() != null) {
                  DeliverableAltmetricInfo altmetrics = deliverable.getDeliverableAltmetricInfo(phase);
                  if (altmetrics == null) {
                    altmetrics = new DeliverableAltmetricInfo();
                  }
                  altmetrics.setDeliverable(deliverable);
                  altmetrics.setAltmetricId(publication.getAltmetric().getAltmetric_id());
                  altmetrics.setAltmetricJid(publication.getAltmetric().getAltmetric_jid());
                  String authors = "";
                  boolean init = true;
                  for (String data : publication.getAltmetric().getAuthors()) {
                    if (init) {
                      authors += data;
                      init = false;
                    } else {
                      authors += ";" + data;
                    }
                  }
                  altmetrics.setAuthors(authors);
                  altmetrics.setCitedByBlogs(publication.getAltmetric().getCited_by_posts_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_posts_count()) : null);
                  altmetrics.setCitedByDelicious(publication.getAltmetric().getCited_by_delicious_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_delicious_count()) : null);
                  altmetrics.setCitedByFacebookPages(publication.getAltmetric().getCited_by_fbwalls_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_fbwalls_count()) : null);
                  altmetrics.setCitedByGooglePlusUsers(publication.getAltmetric().getCited_by_gplus_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_gplus_count()) : null);
                  altmetrics.setCitedByForumUsers(publication.getAltmetric().getCited_by_forum_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_forum_count()) : null);
                  altmetrics.setCitedByLinkedinUsers(publication.getAltmetric().getCited_by_linkedin_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_linkedin_count()) : null);
                  altmetrics.setCitedByNewsOutlets(publication.getAltmetric().getCited_by_msm_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_msm_count()) : null);
                  altmetrics
                    .setCitedByPeerReviewSites(publication.getAltmetric().getCited_by_peer_review_sites_count() != null
                      ? Integer.valueOf(publication.getAltmetric().getCited_by_peer_review_sites_count()) : null);
                  altmetrics.setCitedByPinterestUsers(publication.getAltmetric().getCited_by_pinners_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_pinners_count()) : null);
                  altmetrics.setCitedByPolicies(publication.getAltmetric().getCited_by_policies_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_policies_count()) : null);
                  altmetrics.setCitedByRedditUsers(publication.getAltmetric().getCited_by_rdts_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_rdts_count()) : null);
                  altmetrics
                    .setCitedByResearchHighlightPlatforms(publication.getAltmetric().getCited_by_rh_count() != null
                      ? Integer.valueOf(publication.getAltmetric().getCited_by_rh_count()) : null);
                  altmetrics.setCitedByStackExchangeResources(publication.getAltmetric().getCited_by_qs_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_qs_count()) : null);
                  altmetrics.setCitedByTwitterUsers(publication.getAltmetric().getCited_by_tweeters_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_tweeters_count()) : null);
                  altmetrics.setCitedByWeiboUsers(publication.getAltmetric().getCited_by_weibo_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_weibo_count()) : null);
                  altmetrics.setCitedByWikipediaPages(publication.getAltmetric().getCited_by_wikipedia_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_wikipedia_count()) : null);
                  altmetrics.setCitedByYoutubeChannels(publication.getAltmetric().getCited_by_videos_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_videos_count()) : null);
                  altmetrics.setType(publication.getAltmetric().getType());
                  altmetrics.setTitle(publication.getAltmetric().getTitle());
                  altmetrics.setPhase(phase);
                  altmetrics.setModifiedBy(user);
                  altmetrics.setDoi(publication.getAltmetric().getDoi());
                  altmetrics.setUrl(publication.getAltmetric().getUrl());
                  altmetrics.setUri(publication.getAltmetric().getUri());
                  altmetrics.setScore(publication.getAltmetric().getScore());
                  altmetrics.setJournal(publication.getJournal_name());
                  altmetrics.setHandle(publication.getAltmetric().getHandle());
                  altmetrics.setDetailsUrl(deliverableDTO.getDoi());
                  altmetrics.setAddedOn(publication.getAltmetric().getAdded_on() != null
                    ? new Date(Long.parseLong(publication.getAltmetric().getAdded_on())) : null);
                  altmetrics.setPublishedOn(publication.getAltmetric().getPublished_on() != null
                    ? new Date(Long.parseLong(publication.getAltmetric().getPublished_on())) : null);
                  altmetrics.setIsOpenAccess(String.valueOf(publication.getAltmetric().isIs_oa()));
                  altmetrics.setLastSync(new Date(Calendar.getInstance().getTimeInMillis()));
                  altmetrics.setLastUpdated(publication.getAltmetric().getLast_updated() != null
                    ? new Date(Long.parseLong(publication.getAltmetric().getLast_updated())) : null);
                  altmetrics.setImageSmall(publication.getAltmetric().getImages() != null
                    ? publication.getAltmetric().getImages().getSmall() : null);
                  altmetrics.setImageMedium(publication.getAltmetric().getImages() != null
                    ? publication.getAltmetric().getImages().getMedium() : null);
                  altmetrics.setImageLarge(publication.getAltmetric().getImages() != null
                    ? publication.getAltmetric().getImages().getLarge() : null);
                  altmetrics = deliverableAltmetricInfoManager.saveDeliverableAltmetricInfo(altmetrics);
                  // deliverableAltmetricInfoManager.replicate(altmetrics, phase);
                }
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

      } else {
        fieldErrors.add(new FieldErrorDTO("createDeliverable", "phase", "Error while creating a publication "));
        throw new MARLOFieldValidationException("Field Validation errors", "",
          fieldErrors.stream()
            .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList()));
      }


    } else {
      // validators
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return deliverablesID;
  }

  public Long createDeliverableOther(NewPublicationOtherDTO deliverableDTO, String entityAcronym, User user) {
    Long deliverablesID = null;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Deliverable deliverable = new Deliverable();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createDeliverableOther", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == deliverableDTO.getPhase().getYear()
        && c.getName().equalsIgnoreCase(deliverableDTO.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(
        new FieldErrorDTO("createDeliverableOther", "phase", deliverable.getPhase().getYear() + " is an invalid year"));
    }
    DeliverableType deliverableType = deliverableTypeManager.getDeliverableTypeById(deliverableDTO.getType());
    if (deliverableType == null) {
      fieldErrors.add(new FieldErrorDTO("createDeliverableOther", "DeliverableType",
        deliverableDTO.getType() + " is an invalid publication Type"));
    }

    if (phase != null && !phase.getEditable()) {
      fieldErrors.add(new FieldErrorDTO("createDeliverableOther", "phase", "This is a closed phase"));
    }

    if (fieldErrors.size() == 0 || fieldErrors.isEmpty()) {
      // create deliverable
      deliverable.setCrp(globalUnitEntity);
      deliverable.setPhase(phase);
      deliverable.setIsPublication(true);
      deliverable = deliverableManager.saveDeliverable(deliverable);
      deliverablesID = deliverable.getId();
      if (deliverablesID != null) {

        // create deliverable info
        deliverable = deliverableManager.getDeliverableById(deliverablesID);
        DeliverableInfo deliverableInfo = new DeliverableInfo();
        deliverableInfo.setDeliverable(deliverable);
        deliverableInfo.setTitle(deliverableDTO.getTitle());

        deliverableInfo.setYear(deliverable.getPhase().getYear());
        deliverableInfo.setDeliverableType(deliverableType);
        deliverableInfo.setPhase(phase);
        deliverableInfo.setStatus(APConstants.CLARISA_PUBLICATIONS_STATUS);
        deliverableInfo.setModificationJustification(APConstants.MESSAGE_MODIFICATION_JUSTIFICATION
          + sdf.format(new Date(Calendar.getInstance().getTimeInMillis())));

        // save deliverableinfo

        deliverableInfoManager.saveDeliverableInfo(deliverableInfo);
        // create deliverable dissemination data

        DeliverableDissemination deliverableDissemination = new DeliverableDissemination();
        deliverableDissemination.setIsOpenAccess(deliverableDTO.getIsOpenAccess());
        deliverableDissemination.setDeliverable(deliverable);
        deliverableDissemination.setArticleUrl(deliverableDTO.getArticleURL());
        deliverableDissemination.setDisseminationChannel("other");
        deliverableDissemination.setDisseminationUrl("Not Defined");
        if ((deliverableDTO.getDoi() == null || deliverableDTO.getDoi().equals(""))
          && deliverableDTO.getArticleURL() != null && !deliverableDTO.getArticleURL().isEmpty()) {
          deliverableDissemination.setHasDOI(true);
        }
        deliverableDissemination.setPhase(phase);
        deliverableDisseminationManager.saveDeliverableDissemination(deliverableDissemination);

        // creatte deliverable publication metadata
        DeliverablePublicationMetadata deliverablePublicationMetadata = new DeliverablePublicationMetadata();
        deliverablePublicationMetadata.setDeliverable(deliverable);


        deliverablePublicationMetadata.setPhase(phase);
        // save deliverablePublicationMetadata

        deliverablePubMetadataManager.saveDeliverablePublicationMetadata(deliverablePublicationMetadata);
        // get element ID from

        // econded_name to get Handle and DOI and create
        List<MetadataElement> metadataElements = new ArrayList<MetadataElement>(metadataElementManager.findAll());
        if (metadataElements != null) {


          // deliverable metadataelement handle
          DeliverableMetadataElement deliverableMetadataElementHandle = new DeliverableMetadataElement();
          deliverableMetadataElementHandle.setDeliverable(deliverable);
          deliverableMetadataElementHandle.setPhase(phase);
          MetadataElement metadataElementHandle =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTHANDLE))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementHandle.setMetadataElement(metadataElementHandle);
          deliverableMetadataElementHandle
            .setElementValue(deliverableDTO.getHandle() == null ? "" : deliverableDTO.getHandle());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementHandle);

          // deliverable metadataelement DOI
          DeliverableMetadataElement deliverableMetadataElementDoi = new DeliverableMetadataElement();
          deliverableMetadataElementDoi.setDeliverable(deliverable);
          deliverableMetadataElementDoi.setPhase(phase);
          MetadataElement metadataElementDoi =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTDOI))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementDoi.setMetadataElement(metadataElementDoi);
          deliverableMetadataElementDoi.setElementValue(deliverableDTO.getDoi() == null ? "" : deliverableDTO.getDoi());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementDoi);

          // deliverable metadataelement Title
          DeliverableMetadataElement deliverableMetadataElementTitle = new DeliverableMetadataElement();
          deliverableMetadataElementTitle.setDeliverable(deliverable);
          deliverableMetadataElementTitle.setPhase(phase);
          MetadataElement metadataElementTitle =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTTITLE))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementTitle.setMetadataElement(metadataElementTitle);
          deliverableMetadataElementTitle
            .setElementValue(deliverableDTO.getTitle() == null ? "" : deliverableDTO.getTitle());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementTitle);

          // deliverable metadataelement Citation
          DeliverableMetadataElement deliverableMetadataElementCitation = new DeliverableMetadataElement();
          deliverableMetadataElementCitation.setDeliverable(deliverable);
          deliverableMetadataElementCitation.setPhase(phase);
          MetadataElement metadataElementCitation =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTCITATION))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementCitation.setMetadataElement(metadataElementCitation);
          String authors = "";


          // deliverable metadataelement Publication
          DeliverableMetadataElement deliverableMetadataElementPublication = new DeliverableMetadataElement();
          deliverableMetadataElementPublication.setDeliverable(deliverable);
          deliverableMetadataElementPublication.setPhase(phase);
          MetadataElement metadataElementPublication =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTPUBLICATION))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementPublication.setMetadataElement(metadataElementPublication);
          deliverableMetadataElementPublication.setElementValue(String.valueOf(deliverableDTO.getYear()));
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementPublication);


          // changes for authors 2019-09-03 apply to a field to include all authors in a single row
          // setdeliverables author
          DeliverableMetadataElement deliverableMetadataElementAuthors = new DeliverableMetadataElement();
          deliverableMetadataElementAuthors.setDeliverable(deliverable);
          deliverableMetadataElementAuthors.setPhase(phase);
          MetadataElement metadataElementAuthors =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTAUTHORS))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementAuthors.setMetadataElement(metadataElementAuthors);
          deliverableMetadataElementAuthors.setElementValue(String.valueOf(deliverableDTO.getAuthors()));
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementAuthors);
        }

        for (DeliverableUserDTO deliverableUserDTO : deliverableDTO.getAuthorlist()) {
          DeliverableUser deliverableUser = new DeliverableUser();
          deliverableUser.setFirstName(deliverableUserDTO.getFirstName());
          deliverableUser.setLastName(deliverableUserDTO.getLastName());
          deliverableUser.setDeliverable(deliverable);
          deliverableUser.setPhase(phase);
          deliverableUserManager.saveDeliverableUser(deliverableUser);
        }

        // web of science integration
        if (deliverableDTO.getDoi() != null) {
          try {
            JsonElement json = this.getServiceWOS(
              "http://clarisa.wos.api.mel.cgiar.org/?link=" + DOIService.tryGetDoiName(deliverableDTO.getDoi()));
            System.out.println(json.toString());

            PublicationWOS publication = new Gson().fromJson(json, PublicationWOS.class);
            if (publication != null) {
              // save deliverable metadata external sources WOS and GARDIAN
              final Long deliverableID = deliverable.getId();
              DeliverableMetadataExternalSources deliverableMetadataExternalSources =
                deliverable.getDeliverableMetadataExternalSources().stream()
                  .filter(c -> c.getDeliverable().getId().equals(deliverableID)).findFirst().orElse(null);
              if (deliverableMetadataExternalSources == null) {
                deliverableMetadataExternalSources = new DeliverableMetadataExternalSources();
                deliverableMetadataExternalSources.setDeliverable(deliverable);
                deliverableMetadataExternalSources.setDoi(publication.getDoi());
                deliverableMetadataExternalSources.setIsiStatus(this.getBooleanString(publication.getIs_isi()));
                deliverableMetadataExternalSources.setJournalName(publication.getJournal_name());
                deliverableMetadataExternalSources.setTitle(publication.getTitle());
                deliverableMetadataExternalSources.setOpenAccessStatus(this.getBooleanString(publication.getIs_oa()));
                deliverableMetadataExternalSources.setOpenAccessLink(publication.getOa_link());
                deliverableMetadataExternalSources.setPublicationType(publication.getPublication_type());
                deliverableMetadataExternalSources.setPublicationYear(publication.getPublication_year() != null
                  ? Integer.valueOf(publication.getPublication_year()) : null);
                deliverableMetadataExternalSources.setSource(publication.getSource());
                deliverableMetadataExternalSources.setUrl(publication.getDoi());
                deliverableMetadataExternalSources.setPages(publication.getStart_end_pages());
                deliverableMetadataExternalSources.setPhase(phase);
                deliverableMetadataExternalSources.setCreatedBy(user);
                deliverableMetadataExternalSources.setVolume(publication.getVolume());
                if (publication.getGardian() != null) {
                  deliverableMetadataExternalSources
                    .setGardianAccessibility(publication.getGardian().getAccessibility());
                  deliverableMetadataExternalSources.setGardianFindability(publication.getGardian().getFindability());
                  deliverableMetadataExternalSources
                    .setGardianInteroperability(publication.getGardian().getInteroperability());
                  deliverableMetadataExternalSources.setGardianReusability(publication.getGardian().getReusability());
                }
                deliverableMetadataExternalSources = deliverableMetadataExternalSourcesManager
                  .saveDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                // deliverableMetadataExternalSourcesManager.replicate(deliverableMetadataExternalSources, phase);

                // save institutions with a percentage above APCONSTANT percentage acceptance in deliverable affiliation
                // save institutions with a percentage below APCONSTANT percentage acceptance in deliverable affiliation
                // not mapped
                for (PublicationInstitutionWOS institution : publication.getOrganizations()) {
                  if (institution.getConfidant() != null
                    && institution.getConfidant().longValue() >= APConstants.ACCEPTATION_PERCENTAGE) {
                    DeliverableAffiliation deliverableAffiliation = new DeliverableAffiliation();
                    deliverableAffiliation.setCreatedBy(user);
                    Institution institutionAffiliation =
                      institutionManager.getInstitutionById(institution.getClarisa_id());
                    deliverableAffiliation.setInstitution(institutionAffiliation);
                    deliverableAffiliation.setInstitutionMatchConfidence(institution.getConfidant().intValue());
                    deliverableAffiliation.setDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                    deliverableAffiliation.setInstitutionNameWebOfScience(institution.getName());
                    deliverableAffiliation.setPhase(phase);
                    deliverableAffiliation.setDeliverable(deliverable);
                    deliverableAffiliation =
                      deliverableAffiliationManager.saveDeliverableAffiliation(deliverableAffiliation);
                    // deliverableAffiliationManager.replicate(deliverableAffiliation, phase);
                  }
                  if (institution.getConfidant() != null
                    && (institution.getConfidant().longValue() < APConstants.ACCEPTATION_PERCENTAGE
                      || institution.getConfidant() == null)) {
                    DeliverableAffiliationsNotMapped deliverableAffiliationsNotMapped =
                      new DeliverableAffiliationsNotMapped();
                    deliverableAffiliationsNotMapped.setCountry(institution.getCountry());
                    deliverableAffiliationsNotMapped
                      .setDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                    deliverableAffiliationsNotMapped
                      .setInstitutionMatchConfidence(institution.getConfidant().intValue());
                    deliverableAffiliationsNotMapped.setName(institution.getName());
                    deliverableAffiliationsNotMapped.setFullAddress(institution.getFull_address());
                    deliverableAffiliationsNotMapped.setPossibleInstitution(institution.getClarisa_id() != null
                      ? institutionManager.getInstitutionById(institution.getClarisa_id()) : null);
                    deliverableAffiliationsNotMapped = deliverableAffiliationsNotMappedManager
                      .saveDeliverableAffiliationsNotMapped(deliverableAffiliationsNotMapped);
                    // deliverableAffiliationsNotMappedManager.replicate(deliverableAffiliationsNotMapped, phase);
                  }
                }
                // save authors of WOS external sources authors


                if (publication.getAuthors() != null) {
                  for (PublicationAuthorWOS author : publication.getAuthors()) {
                    ExternalSourceAuthor externalSourceAuthor = new ExternalSourceAuthor();
                    externalSourceAuthor.setDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                    externalSourceAuthor.setCreatedBy(user);
                    externalSourceAuthor.setFullName(author.getFull_name());
                    externalSourceAuthor = externalSourceAuthorManager.saveExternalSourceAuthor(externalSourceAuthor);
                    // externalSourceAuthorManager.replicate(externalSourceAuthor, phase);
                  }
                }
                // save altmetrics information in deliverable altmetrics
                if (publication.getAltmetric() != null) {
                  DeliverableAltmetricInfo altmetrics = deliverable.getDeliverableAltmetricInfo(phase);
                  if (altmetrics == null) {
                    altmetrics = new DeliverableAltmetricInfo();
                  }
                  altmetrics.setDeliverable(deliverable);
                  altmetrics.setAltmetricId(publication.getAltmetric().getAltmetric_id());
                  altmetrics.setAltmetricJid(publication.getAltmetric().getAltmetric_jid());
                  String authors = "";
                  boolean init = true;
                  if (publication.getAltmetric().getAuthors() != null) {
                    for (String data : publication.getAltmetric().getAuthors()) {
                      if (init) {
                        authors += data;
                        init = false;
                      } else {
                        authors += ";" + data;
                      }
                    }
                  }

                  altmetrics.setAuthors(authors);
                  altmetrics.setCitedByBlogs(publication.getAltmetric().getCited_by_posts_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_posts_count()) : null);
                  altmetrics.setCitedByDelicious(publication.getAltmetric().getCited_by_delicious_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_delicious_count()) : null);
                  altmetrics.setCitedByFacebookPages(publication.getAltmetric().getCited_by_fbwalls_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_fbwalls_count()) : null);
                  altmetrics.setCitedByGooglePlusUsers(publication.getAltmetric().getCited_by_gplus_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_gplus_count()) : null);
                  altmetrics.setCitedByForumUsers(publication.getAltmetric().getCited_by_forum_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_forum_count()) : null);
                  altmetrics.setCitedByLinkedinUsers(publication.getAltmetric().getCited_by_linkedin_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_linkedin_count()) : null);
                  altmetrics.setCitedByNewsOutlets(publication.getAltmetric().getCited_by_msm_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_msm_count()) : null);
                  altmetrics
                    .setCitedByPeerReviewSites(publication.getAltmetric().getCited_by_peer_review_sites_count() != null
                      ? Integer.valueOf(publication.getAltmetric().getCited_by_peer_review_sites_count()) : null);
                  altmetrics.setCitedByPinterestUsers(publication.getAltmetric().getCited_by_pinners_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_pinners_count()) : null);
                  altmetrics.setCitedByPolicies(publication.getAltmetric().getCited_by_policies_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_policies_count()) : null);
                  altmetrics.setCitedByRedditUsers(publication.getAltmetric().getCited_by_rdts_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_rdts_count()) : null);
                  altmetrics
                    .setCitedByResearchHighlightPlatforms(publication.getAltmetric().getCited_by_rh_count() != null
                      ? Integer.valueOf(publication.getAltmetric().getCited_by_rh_count()) : null);
                  altmetrics.setCitedByStackExchangeResources(publication.getAltmetric().getCited_by_qs_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_qs_count()) : null);
                  altmetrics.setCitedByTwitterUsers(publication.getAltmetric().getCited_by_tweeters_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_tweeters_count()) : null);
                  altmetrics.setCitedByWeiboUsers(publication.getAltmetric().getCited_by_weibo_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_weibo_count()) : null);
                  altmetrics.setCitedByWikipediaPages(publication.getAltmetric().getCited_by_wikipedia_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_wikipedia_count()) : null);
                  altmetrics.setCitedByYoutubeChannels(publication.getAltmetric().getCited_by_videos_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_videos_count()) : null);
                  altmetrics.setType(publication.getAltmetric().getType());
                  altmetrics.setTitle(publication.getAltmetric().getTitle());
                  altmetrics.setPhase(phase);
                  altmetrics.setModifiedBy(user);
                  altmetrics.setDoi(publication.getAltmetric().getDoi());
                  altmetrics.setUrl(publication.getAltmetric().getUrl());
                  altmetrics.setUri(publication.getAltmetric().getUri());
                  altmetrics.setScore(publication.getAltmetric().getScore());
                  altmetrics.setJournal(publication.getJournal_name());
                  altmetrics.setHandle(publication.getAltmetric().getHandle());
                  altmetrics.setDetailsUrl(deliverableDTO.getDoi());
                  altmetrics.setAddedOn(publication.getAltmetric().getAdded_on() != null
                    ? new Date(Long.parseLong(publication.getAltmetric().getAdded_on())) : null);
                  altmetrics.setPublishedOn(publication.getAltmetric().getPublished_on() != null
                    ? new Date(Long.parseLong(publication.getAltmetric().getPublished_on())) : null);
                  altmetrics.setIsOpenAccess(String.valueOf(publication.getAltmetric().isIs_oa()));
                  altmetrics.setLastSync(new Date(Calendar.getInstance().getTimeInMillis()));
                  altmetrics.setLastUpdated(publication.getAltmetric().getLast_updated() != null
                    ? new Date(Long.parseLong(publication.getAltmetric().getLast_updated())) : null);
                  altmetrics.setImageSmall(publication.getAltmetric().getImages() != null
                    ? publication.getAltmetric().getImages().getSmall() : null);
                  altmetrics.setImageMedium(publication.getAltmetric().getImages() != null
                    ? publication.getAltmetric().getImages().getMedium() : null);
                  altmetrics.setImageLarge(publication.getAltmetric().getImages() != null
                    ? publication.getAltmetric().getImages().getLarge() : null);
                  altmetrics = deliverableAltmetricInfoManager.saveDeliverableAltmetricInfo(altmetrics);
                  // deliverableAltmetricInfoManager.replicate(altmetrics, phase);
                }
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    } else {
      // validators
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return deliverablesID;
  }

  public ResponseEntity<PublicationDTO> deleteDeliverableById(Long id, String CGIARentityAcronym, Integer repoYear,
    String repoPhase, User user) {
    Publication publication = null;
    Deliverable deliverable = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteDeliverableById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteDeliverableById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteDeliverableById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    String strippedRepoPhase = StringUtils.stripToNull(repoPhase);
    Phase phase = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
        && p.getYear() == repoYear && StringUtils.equalsIgnoreCase(p.getName(), strippedRepoPhase) && p.isActive())
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors
        .add(new FieldErrorDTO("deleteDeliverableById", "phase", repoPhase + ' ' + repoYear + " is an invalid phase"));
    } else {
      if (!StringUtils.equalsIgnoreCase(phase.getCrp().getAcronym(), strippedEntityAcronym)) {
        fieldErrors.add(new FieldErrorDTO("deleteDeliverableById", "FlagshipEntity",
          "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym " + CGIARentityAcronym));
      }
    }

    if (fieldErrors.isEmpty()) {
      deliverable = deliverableManager.getDeliverableById(id);
      if (deliverable != null && deliverable.isActive() == true) {
        if (deliverable.getPhase() != null) {
          if (deliverable.getPhase().getId() == phase.getId()) {
            // only soft delete if deliverable is a publication

            if (deliverable.getProject() == null) {
              DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(phase);
              deliverableManager.deleteDeliverable(deliverable.getId());
              // List<DeliverableMetadataElement> phaseMetadata = deliverable.getMetadataElements(phase);
              // Map<String, DeliverableMetadataElement> metadataElements = this.getMetadataElements(phaseMetadata);
              publication = this.getPublication(deliverable, deliverableInfo, phase);
              // publication = this.publicationsMapper.deliverableToPublication(deliverable, phase, metadataElements);
            }
          } else {
            fieldErrors.add(new FieldErrorDTO("deleteDeliverableById", "DeliverableEntity",
              "The Deliverable with id " + id + " do not correspond to the phase entered"));
          }
        } else {
          fieldErrors.add(
            new FieldErrorDTO("deleteDeliverableById", "PhaseEntity", "There is no Phase assosiated to this entity!"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteDeliverableById", "DeliverableEntity",
          id + " is an invalid Report Synthesis Srf Progress Target Code"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(publication).map(this.publicationsMapper::publicationToPublicationDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Find a Deliverable by Id and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a PublicationDTO with the Deliverable Item
   */
  public ResponseEntity<PublicationDTO> findDeliverableById(Long idDeliverable, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    // TODO: Include all security validations
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    Publication publication = null;
    Deliverable deliverable = null;
    DeliverableInfo deliverableInfo = null;

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findDeliverableById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("findDeliverableById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    String strippedRepoPhase = StringUtils.stripToNull(repoPhase);
    Phase phase = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
        && p.getYear() >= APConstants.CLARISA_AVALIABLE_INFO_YEAR && p.getYear() == repoYear
        && StringUtils.equalsIgnoreCase(p.getName(), strippedRepoPhase))
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors
        .add(new FieldErrorDTO("findDeliverableById", "phase", repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    deliverable = deliverableManager.getDeliverableById(idDeliverable);
    if (deliverable == null || !deliverable.isActive()) {
      fieldErrors.add(new FieldErrorDTO("findDeliverableById", "DeliverableEntity",
        idDeliverable + " is an invalid id of a Deliverable"));
    } else {
      if (phase != null) {
        deliverableInfo = deliverable.getDeliverableInfo(phase);
        if (deliverableInfo == null) {
          fieldErrors.add(new FieldErrorDTO("findDeliverableById", "DeliverableEntity",
            "The Deliverable with id " + idDeliverable + " do not correspond to the phase entered"));
        } else {
          if (globalUnitEntity != null && deliverableInfo.getPhase().getCrp().getId() != globalUnitEntity.getId()) {
            fieldErrors.add(new FieldErrorDTO("findDeliverableById", "DeliverableEntity",
              "The Deliverable with id " + idDeliverable + " do not correspond to the CRP entered"));
          }
        }
      }
    }

    if (fieldErrors.isEmpty()) {
      // if it does not have a project associated, it must be a publication
      if (deliverable.getProject() == null) {
        // change to avoid using lazy hibernate
        publication = this.getPublication(deliverable, deliverableInfo, phase);
        // publication = publicationsMapper.deliverableToPublication(deliverable, phase, metadataElements);
      }
    }

    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      // fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(publication).map(this.publicationsMapper::publicationToPublicationDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public List<PublicationDTO> getAllDeliverables(String entityAcronym, int repoyear, String repoPhase, User user) {
    List<PublicationDTO> deliverablesListDTO = new ArrayList<PublicationDTO>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createDeliverable", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }

    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() >= APConstants.CLARISA_AVALIABLE_INFO_YEAR && c.getYear() == repoyear
        && c.getName().equalsIgnoreCase(repoPhase))
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createDeliverable", "phase", repoyear + " is an invalid year"));
    }

    if (fieldErrors.size() == 0 || fieldErrors.isEmpty()) {
      try {
        List<Publication> fullPublicationsList = new ArrayList<Publication>();

        List<Deliverable> deliverableList = deliverableManager.getDeliverablesByParameters(phase, true, false, false);
        for (Deliverable deliverable : deliverableList) {
          // if there is no project associated to the deliverable it means it is a prp
          // if (deliverable.getProject() == null) {
          Publication publication = new Publication();
          publication.setId(deliverable.getId());
          DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(phase);
          // getting deliverable Info
          if (deliverableInfo != null) {
            if (deliverableInfo.getYear() != -1
              && (deliverableInfo.getYear() == repoyear || (deliverableInfo.getNewExpectedYear() != null
                ? deliverableInfo.getNewExpectedYear().intValue() : -1) == repoyear)) {
              publication.setVolume(deliverableInfo.getTitle());
              publication.setYear(deliverableInfo.getYear());
              publication.setPhase(phase);
              deliverable.setCrp(phase.getCrp());
              deliverable.setDeliverableInfo(deliverableInfo);
              // getting deliverableDissemination
              DeliverableDissemination deliverableDissemination =
                deliverableDisseminationManager.findDisseminationByPhaseAndDeliverable(phase, deliverable);
              deliverable.setDissemination(deliverableDissemination);
              /*
               * List<DeliverableDissemination> deliverableDisseminationList =
               * deliverable.getDeliverableDisseminations().stream()
               * .filter(deliverabledisemination -> deliverabledisemination.getPhase().getId().equals(phase.getId()))
               * .collect(Collectors.toList());
               * for (DeliverableDissemination deliverableDissemination : deliverableDisseminationList) {
               * if (deliverableDissemination.getPhase().getId().equals(phase.getId())) {
               * deliverable.setDissemination(deliverableDissemination);
               * break;
               * }
               * }
               */
              publication.setIsOpenAccess(
                deliverable.getDissemination() != null ? (deliverable.getDissemination().getIsOpenAccess() != null
                  ? deliverable.getDissemination().getIsOpenAccess() : false) : false);
              // getting deliverablepublicationMetadata
              Deliverable d = deliverableManager.getDeliverableById(deliverable.getId());

              // DeliverablePublicationMetadata deliverablePublicationMetadata = d.getPublication(phase);
              DeliverablePublicationMetadata deliverablePublicationMetadata =
                deliverablePubMetadataManager.findPublicationMetadataByPhaseAndDeliverable(phase, d);
              if (deliverablePublicationMetadata.getId() != null) {
                publication.setISIJournal(deliverablePublicationMetadata.getIsiPublication() != null
                  ? deliverablePublicationMetadata.getIsiPublication() : false);
                publication.setJournal(deliverablePublicationMetadata.getJournal());
                publication.setIssue(deliverablePublicationMetadata.getIssue());
                publication.setNpages(deliverablePublicationMetadata.getPages());
                publication.setVolume(deliverablePublicationMetadata.getVolume());
              }
              // Getting deliverablemetadataelement
              /*
               * List<DeliverableMetadataElement> deliverableMetadataElementList =
               * deliverable.getDeliverableMetadataElements().stream()
               * .filter(
               * deliverableMetadataElement -> deliverableMetadataElement.getPhase().getId().equals(phase.getId()))
               * .collect(Collectors.toList());
               */
              List<DeliverableMetadataElement> deliverableMetadataElementList =
                deliverableMetadataElementManager.findAllByPhaseAndDeliverable(phase, deliverable);
              for (DeliverableMetadataElement deliverableMetadataElement : deliverableMetadataElementList) {

                if (deliverableMetadataElement.getElementValue() != null
                  && !deliverableMetadataElement.getElementValue().trim().isEmpty()) {
                  if (publication.getTitle() == null) {
                    if (deliverableMetadataElement.getMetadataElement().getEcondedName()
                      .equals(APConstants.METADATAELEMENTTITLE)) {
                      publication.setTitle(deliverableMetadataElement.getElementValue());
                    }
                  }
                  if (deliverableMetadataElement.getMetadataElement().getEcondedName()
                    .equals(APConstants.METADATAELEMENTDOI)) {
                    publication.setDoi(deliverableMetadataElement.getElementValue());

                  }
                  if (deliverableMetadataElement.getMetadataElement().getEcondedName()
                    .equals(APConstants.METADATAELEMENTHANDLE)) {
                    publication.setHandle(deliverableMetadataElement.getElementValue());
                  }

                  if (deliverableMetadataElement.getMetadataElement().getEcondedName()
                    .equals(APConstants.METADATAELEMENTAUTHORS)) {
                    if (!deliverableMetadataElement.getElementValue().contains("[object Object]")) {
                      publication.setAuthors(deliverableMetadataElement.getElementValue());
                    }

                  }
                }
              }
              /*
               * List<DeliverableUser> deliverableUserList = deliverable.getDeliverableUsers().stream()
               * .filter(c -> c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
               */
              List<DeliverableUser> deliverableUserList =
                deliverableUserManager.findAllByPhaseAndDeliverable(phase, deliverable);
              List<DeliverableUser> newdeliverableUserList = new ArrayList<DeliverableUser>();
              for (DeliverableUser deliverableUser : deliverableUserList) {
                newdeliverableUserList.add(deliverableUser);
              }
              publication.setAuthorlist(newdeliverableUserList);
              if (deliverableManager.isDeliverableExcluded(deliverable.getId(), phase.getId())) {
                fullPublicationsList.add(publication);
              }
            }
          }

        }
        if (fullPublicationsList.size() > 0) {

          deliverablesListDTO = fullPublicationsList.stream()
            .map(publication -> this.publicationsMapper.publicationToPublicationDTO(publication))
            .collect(Collectors.toList());

        } else {
          fieldErrors.add(new FieldErrorDTO("createDeliverable", "Deliverable", "No data found"));
          throw new MARLOFieldValidationException("Field Validation errors", "",
            fieldErrors.stream()
              .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
              .collect(Collectors.toList()));
        }


      } catch (Exception e) {
        e.printStackTrace();
        fieldErrors.add(new FieldErrorDTO("createDeliverable", "Deliverable", "ERROR Creating info"));
        throw new MARLOFieldValidationException("Field Validation errors", "",
          fieldErrors.stream()
            .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList()));
      }

    } else {
      if (!fieldErrors.isEmpty()) {
        throw new MARLOFieldValidationException("Field Validation errors", "",
          fieldErrors.stream()
            .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList()));
      }
    }
    return deliverablesListDTO;
  }

  public String getBooleanString(String data) {
    String result = null;
    if (data != null) {
      if (StringUtils.equalsIgnoreCase(data, "yes")) {
        result = "true";
      } else if (StringUtils.equalsIgnoreCase(data, "no")) {
        result = "false";
      } else if (StringUtils.equalsIgnoreCase(data, "n/a")) {
        result = "N/A";
      }
    }
    return result;
  }

  private Map<String, DeliverableMetadataElement>
    getMetadataElements(List<DeliverableMetadataElement> deliverableMetadataElements) {
    Map<String, DeliverableMetadataElement> map = new TreeMap<>(Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));

    if (deliverableMetadataElements != null && !deliverableMetadataElements.isEmpty()) {
      for (DeliverableMetadataElement deliverableMetadataElement : deliverableMetadataElements) {
        map.put(deliverableMetadataElement.getMetadataElement().getEcondedName(), deliverableMetadataElement);
      }
    }

    return map;
  }

  private Publication getPublication(Deliverable deliverable, DeliverableInfo deliverableInfo, Phase phase) {
    Publication publication = new Publication();
    publication.setId(deliverable.getId());
    // deliverableinfo
    publication.setYear(deliverableInfo != null ? deliverableInfo.getYear() : -1);
    // deliverable dissemination
    DeliverableDissemination deliverableDissemination =
      deliverableDisseminationManager.findDisseminationByPhaseAndDeliverable(phase, deliverable);
    publication.setIsOpenAccess(
      deliverableDissemination != null ? BooleanUtils.toBoolean(deliverableDissemination.getIsOpenAccess()) : false);
    publication.setArticleURL(deliverableDissemination != null ? deliverableDissemination.getArticleUrl() : null);
    // deliverablePublicationMetadada
    DeliverablePublicationMetadata deliverablePublicationMetadata =
      deliverablePubMetadataManager.findPublicationMetadataByPhaseAndDeliverable(phase, deliverable);
    publication.setISIJournal(deliverablePublicationMetadata != null
      ? BooleanUtils.toBoolean(deliverablePublicationMetadata.getIsiPublication()) : false);
    publication.setIssue(deliverablePublicationMetadata != null ? deliverablePublicationMetadata.getIssue() : null);
    publication.setJournal(deliverablePublicationMetadata != null ? deliverablePublicationMetadata.getJournal() : null);
    publication.setNpages(deliverablePublicationMetadata != null ? deliverablePublicationMetadata.getPages() : null);
    publication.setPhase(phase);
    publication.setVolume(deliverablePublicationMetadata != null ? deliverablePublicationMetadata.getVolume() : null);

    // Deliverable metadata Elements
    List<DeliverableMetadataElement> phaseMetadata =
      deliverableMetadataElementManager.findAllByPhaseAndDeliverable(phase, deliverable);
    Map<String, DeliverableMetadataElement> metadataElements = this.getMetadataElements(phaseMetadata);
    DeliverableMetadataElement deliverableMetadataElement = null;
    deliverableMetadataElement = metadataElements.get(APConstants.METADATAELEMENTDOI);
    publication.setDoi(deliverableMetadataElement != null ? deliverableMetadataElement.getElementValue() : null);
    deliverableMetadataElement = metadataElements.get(APConstants.METADATAELEMENTHANDLE);
    publication.setHandle(deliverableMetadataElement != null ? deliverableMetadataElement.getElementValue() : null);
    deliverableMetadataElement = metadataElements.get(APConstants.METADATAELEMENTTITLE);
    publication.setTitle(deliverableMetadataElement != null ? deliverableMetadataElement.getElementValue() : null);
    deliverableMetadataElement = metadataElements.get(APConstants.METADATAELEMENTAUTHORS);
    String authors = deliverableMetadataElement != null ? deliverableMetadataElement.getElementValue() : null;
    publication // why is this validation necessary?
      .setAuthors(authors != null && !authors.contains("[object Object]") ? authors : null);
    // deliverable authors
    List<DeliverableUser> authorList = deliverableUserManager.findAllByPhaseAndDeliverable(phase, deliverable);
    publication.setAuthorlist(authorList);
    return publication;
  }

  public JsonElement getServiceWOS(String url) throws MalformedURLException, IOException {
    URL clarisaUrl = new URL(url);

    URLConnection conn = clarisaUrl.openConnection();
    conn.setRequestProperty("Authorization", "3174h8-c40e68-5ge392-218caa-a664b3");
    JsonElement element = null;
    try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
      element = new JsonParser().parse(reader);
    } catch (FileNotFoundException fnfe) {
      element = JsonNull.INSTANCE;
    } catch (IOException ioe) {
      element = JsonNull.INSTANCE;
    }
    return element;
  }

  public Long putDeliverableById(Long idDeliverable, NewPublicationDTO newPublicationDTO, String CGIARentityAcronym,
    User user) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DeliverableInfo deliverableInfo = null;
    Long idDeliverableDB = null;
    Phase phase = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putDeliverable", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putDeliverable", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putDeliverable", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newPublicationDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("putDeliverable", "PhaseEntity", "Phase must not be null"));
    } else {
      String strippedPhaseName = StringUtils.stripToNull(newPublicationDTO.getPhase().getName());
      if (strippedPhaseName == null || newPublicationDTO.getPhase().getYear() == null
      // DANGER! Magic number ahead
        || newPublicationDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("putDeliverable", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
            && p.getYear() == newPublicationDTO.getPhase().getYear()
            && StringUtils.equalsIgnoreCase(p.getName(), strippedPhaseName))
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors.add(new FieldErrorDTO("putDeliverable", "phase", newPublicationDTO.getPhase().getName() + ' '
            + newPublicationDTO.getPhase().getYear() + " is an invalid phase"));
        }
      }
    }

    Deliverable deliverable = deliverableManager.getDeliverableById(idDeliverable);
    if (deliverable == null || deliverable.isActive() == false) {
      fieldErrors.add(new FieldErrorDTO("findDeliverableById", "DeliverableEntity",
        idDeliverable + " is an invalid id of a Deliverable"));
    } else {
      if (phase != null) {
        deliverableInfo = deliverable.getDeliverableInfo(phase);
        if (deliverableInfo == null) {
          fieldErrors.add(new FieldErrorDTO("findDeliverableById", "DeliverableEntity",
            "The Deliverable with id " + idDeliverable + " do not correspond to the phase entered"));
        } else {
          if (globalUnitEntity != null && deliverableInfo.getPhase().getCrp().getId() != globalUnitEntity.getId()) {
            fieldErrors.add(new FieldErrorDTO("findDeliverableById", "DeliverableEntity",
              "The Deliverable with id " + idDeliverable + " do not correspond to the CRP entered"));
          }
        }
      }
    }
    if (phase != null && !phase.getEditable()) {
      fieldErrors.add(new FieldErrorDTO("putDeliverable", "phase", "this  is an closed phase"));
    }

    if (fieldErrors.isEmpty()) {
      // create deliverable
      deliverable.setCrp(globalUnitEntity);
      deliverable.setPhase(phase);
      deliverable.setIsPublication(true);
      // save deliverables
      deliverable = deliverableManager.saveDeliverable(deliverable);
      idDeliverableDB = deliverable.getId();

      if (idDeliverableDB != null) {
        DeliverableType deliverableType =
          deliverableTypeManager.getDeliverableTypeById(APConstants.IMPORT_DELIVERABLE_VALUE);

        // create deliverable info
        deliverable = deliverableManager.getDeliverableById(idDeliverableDB);
        deliverableInfo = deliverable.getDeliverableInfo();
        deliverableInfo.setDeliverable(deliverable);
        deliverableInfo.setTitle(newPublicationDTO.getTitle()); //
        // this.deliverableinfo.setDescription(deliverable.getTitle());
        deliverableInfo.setYear(deliverable.getPhase().getYear());
        deliverableInfo.setDeliverableType(deliverableType);
        deliverableInfo.setPhase(phase);
        deliverableInfo.setStatus(APConstants.CLARISA_PUBLICATIONS_STATUS);
        deliverableInfo.setModificationJustification(APConstants.MESSAGE_MODIFICATION_JUSTIFICATION
          + sdf.format(new Date(Calendar.getInstance().getTimeInMillis())));
        // save deliverableinfo
        deliverableInfoManager.saveDeliverableInfo(deliverableInfo);

        // create deliverable dissemination data ???
        final long phaseID = phase.getId();
        /*
         * DeliverableDissemination deliverableDissemination = deliverable.getDeliverableDisseminations().stream()
         * .filter(c -> c.getPhase().getId().longValue() == phaseID).findFirst().orElse(null);
         */
        DeliverableDissemination deliverableDissemination =
          deliverableDisseminationManager.findDisseminationByPhaseAndDeliverable(phase, deliverable);
        if (deliverableDissemination == null) {
          deliverableDissemination = new DeliverableDissemination();
        }
        deliverableDissemination.setIsOpenAccess(newPublicationDTO.getIsOpenAccess());
        deliverableDissemination.setDeliverable(deliverable);
        deliverableDissemination.setDisseminationUrl("Not Defined");
        deliverableDissemination.setDisseminationChannel("other");
        deliverableDissemination.setArticleUrl(newPublicationDTO.getArticleURL());
        if ((newPublicationDTO.getDoi() == null || newPublicationDTO.getDoi().equals(""))
          && newPublicationDTO.getArticleURL() != null && !newPublicationDTO.getArticleURL().isEmpty()) {
          deliverableDissemination.setHasDOI(true);
        }
        deliverableDissemination.setPhase(phase);
        deliverableDisseminationManager.saveDeliverableDissemination(deliverableDissemination);

        // create deliverable publication metadata
        // DeliverablePublicationMetadata deliverablePublicationMetadata =
        // deliverable.getDeliverablePublicationMetadatas()
        // .stream().filter(m -> m.getPhase().getId() == phaseID).findFirst().orElse(null);
        DeliverablePublicationMetadata deliverablePublicationMetadata =
          deliverablePubMetadataManager.findPublicationMetadataByPhaseAndDeliverable(phase, deliverable);
        if (deliverablePublicationMetadata == null) {
          deliverablePublicationMetadata = new DeliverablePublicationMetadata();
        }
        deliverablePublicationMetadata.setDeliverable(deliverable);
        deliverablePublicationMetadata.setIssue(newPublicationDTO.getIssue());
        deliverablePublicationMetadata.setJournal(newPublicationDTO.getJournal());
        deliverablePublicationMetadata.setPages(newPublicationDTO.getNpages());
        deliverablePublicationMetadata.setVolume(newPublicationDTO.getVolume());
        deliverablePublicationMetadata.setIsiPublication(newPublicationDTO.getIsISIJournal());
        deliverablePublicationMetadata.setPhase(phase);
        deliverablePubMetadataManager.saveDeliverablePublicationMetadata(deliverablePublicationMetadata);

        // get element ID from econded_name to get Handle and DOI and create
        Long phaseId = phase.getId();
        /*
         * List<DeliverableMetadataElement> phaseMetadata = deliverable.getDeliverableMetadataElements().stream()
         * .filter(m -> m.getPhase().getId() == phaseId).collect(Collectors.toList());
         */
        List<DeliverableMetadataElement> phaseMetadata =
          deliverableMetadataElementManager.findAllByPhaseAndDeliverable(phase, deliverable);
        Map<String, DeliverableMetadataElement> metadataElements = this.getMetadataElements(phaseMetadata);
        if (metadataElements != null && !metadataElements.isEmpty()) {
          // deliverable metadataelement handle
          DeliverableMetadataElement deliverableMetadataElementHandle =
            metadataElements.get(APConstants.METADATAELEMENTHANDLE);
          deliverableMetadataElementHandle.setDeliverable(deliverable);
          deliverableMetadataElementHandle.setPhase(phase);
          deliverableMetadataElementHandle
            .setElementValue(newPublicationDTO.getHandle() == null ? "" : newPublicationDTO.getHandle());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementHandle);

          // deliverable metadataelement DOI
          DeliverableMetadataElement deliverableMetadataElementDoi =
            metadataElements.get(APConstants.METADATAELEMENTDOI);
          deliverableMetadataElementDoi.setDeliverable(deliverable);
          deliverableMetadataElementDoi.setPhase(phase);
          deliverableMetadataElementDoi.setElementValue(
            newPublicationDTO.getDoi() == null ? "" : DOIService.tryGetDoiName(newPublicationDTO.getDoi()));
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementDoi);

          // deliverable metadataelement Title
          DeliverableMetadataElement deliverableMetadataElementTitle =
            metadataElements.get(APConstants.METADATAELEMENTTITLE);
          deliverableMetadataElementTitle.setDeliverable(deliverable);
          deliverableMetadataElementTitle.setPhase(phase);
          deliverableMetadataElementTitle
            .setElementValue(newPublicationDTO.getTitle() == null ? "" : newPublicationDTO.getTitle());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementTitle);

          // deliverable metadataelement Citation
          DeliverableMetadataElement deliverableMetadataElementCitation =
            metadataElements.get(APConstants.METADATAELEMENTCITATION);
          deliverableMetadataElementCitation.setDeliverable(deliverable);
          deliverableMetadataElementCitation.setPhase(phase);
          StringBuilder elementCitation = new StringBuilder();
          for (DeliverableUserDTO author : newPublicationDTO.getAuthorList()) {
            elementCitation.append(elementCitation.length() > 0 ? ',' : "").append(author.getLastName()).append(' ')
              .append(author.getFirstName());
          }
          // if authors not been saved by list, citation can be udapte by authors single row field
          if (newPublicationDTO.getAuthorList() != null) {
            if (newPublicationDTO.getAuthorList().isEmpty() && newPublicationDTO.getAuthors() != null
              && !newPublicationDTO.getAuthors().trim().isEmpty()) {
              elementCitation.append(elementCitation.length() > 0 ? ',' : "")
                .append(newPublicationDTO.getAuthors().trim());
            }
          } else {
            if (newPublicationDTO.getAuthors() != null && !newPublicationDTO.getAuthors().trim().isEmpty()) {
              elementCitation.append(elementCitation.length() > 0 ? ',' : "")
                .append(newPublicationDTO.getAuthors().trim());
            }
          }

          deliverableMetadataElementCitation.setElementValue(elementCitation.append(',')
            .append(newPublicationDTO.getYear()).append(',').append(newPublicationDTO.getTitle()).append(',')
            .append(newPublicationDTO.getJournal()).append(',').append(newPublicationDTO.getVolume()).append(',')
            .append(newPublicationDTO.getIssue()).append(',').append(newPublicationDTO.getNpages()).toString());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementCitation);

          // deliverable metadataelement Publication
          DeliverableMetadataElement deliverableMetadataElementPublication =
            metadataElements.get(APConstants.METADATAELEMENTPUBLICATION);
          deliverableMetadataElementPublication.setDeliverable(deliverable);
          deliverableMetadataElementPublication.setPhase(phase);
          deliverableMetadataElementPublication.setElementValue(String.valueOf(newPublicationDTO.getYear()));
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementPublication);

          // changes for authors 2019-09-03 apply to a field to include all authors in a single row
          // setdeliverables author
          DeliverableMetadataElement deliverableMetadataElementAuthors =
            metadataElements.get(APConstants.METADATAELEMENTAUTHORS);
          deliverableMetadataElementAuthors.setDeliverable(deliverable);
          deliverableMetadataElementAuthors.setPhase(phase);
          deliverableMetadataElementAuthors.setElementValue(String.valueOf(newPublicationDTO.getAuthors()));
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementAuthors);
        }

        List<DeliverableUser> authorListDB = deliverableUserManager.findAllByPhaseAndDeliverable(phase, deliverable);
        for (DeliverableUserDTO deliverableUserDTO : newPublicationDTO.getAuthorList()) {
          boolean found = false;
          /*
           * for (DeliverableUser authorsList : deliverable.getDeliverableUsers().stream().filter(c -> c.isActive())
           * .collect(Collectors.toList())) {
           */
          for (DeliverableUser authorsList : authorListDB) {
            if (deliverableUserDTO.getFirstName().toUpperCase().equals(authorsList.getFirstName().toUpperCase())
              && deliverableUserDTO.getLastName().toUpperCase().equals(authorsList.getLastName().toUpperCase())) {
              found = true;
            }
          }
          if (!found) {
            DeliverableUser deliverableUser = new DeliverableUser();
            deliverableUser.setFirstName(deliverableUserDTO.getFirstName());
            deliverableUser.setLastName(deliverableUserDTO.getLastName());
            deliverableUser.setDeliverable(deliverable);
            deliverableUser.setPhase(phase);
            deliverableUserManager.saveDeliverableUser(deliverableUser);
          }
        }
        /*
         * for (DeliverableUser authorsList : deliverable.getDeliverableUsers().stream().filter(c -> c.isActive())
         * .collect(Collectors.toList())) {
         */
        for (DeliverableUser authorsList : authorListDB) {
          boolean found = true;
          for (DeliverableUserDTO deliverableUserDTO : newPublicationDTO.getAuthorList()) {
            if (deliverableUserDTO.getFirstName().toUpperCase().equals(authorsList.getFirstName().toUpperCase())
              && deliverableUserDTO.getLastName().toUpperCase().equals(authorsList.getLastName().toUpperCase())) {
              found = false;
            }
          }
          if (found) {
            deliverableUserManager.deleteDeliverableUser(authorsList.getId());
          }
        }

        // web of science integration
        if (newPublicationDTO.getDoi() != null) {
          try {
            JsonElement json = this.getServiceWOS(
              "http://clarisa.wos.api.mel.cgiar.org/?link=" + DOIService.tryGetDoiName(newPublicationDTO.getDoi()));
            System.out.println(json.toString());

            PublicationWOS publication = new Gson().fromJson(json, PublicationWOS.class);
            if (publication != null) {
              // save deliverable metadata external sources WOS and GARDIAN
              final Long deliverableID = deliverable.getId();
              DeliverableMetadataExternalSources deliverableMetadataExternalSources =
                deliverable.getDeliverableMetadataExternalSources().stream()
                  .filter(c -> c.getDeliverable().getId().equals(deliverableID)).findFirst().orElse(null);
              if (deliverableMetadataExternalSources == null) {
                deliverableMetadataExternalSources = new DeliverableMetadataExternalSources();
              }
              deliverableMetadataExternalSources.setDeliverable(deliverable);
              deliverableMetadataExternalSources.setDoi(publication.getDoi());
              deliverableMetadataExternalSources.setIsiStatus(this.getBooleanString(publication.getIs_isi()));
              deliverableMetadataExternalSources.setJournalName(publication.getJournal_name());
              deliverableMetadataExternalSources.setTitle(publication.getTitle());
              deliverableMetadataExternalSources.setOpenAccessStatus(this.getBooleanString(publication.getIs_oa()));
              deliverableMetadataExternalSources.setOpenAccessLink(publication.getOa_link());
              deliverableMetadataExternalSources.setPublicationType(publication.getPublication_type());
              deliverableMetadataExternalSources.setPublicationYear(
                publication.getPublication_year() != null ? Integer.valueOf(publication.getPublication_year()) : null);
              deliverableMetadataExternalSources.setSource(publication.getSource());
              deliverableMetadataExternalSources.setUrl(publication.getDoi());
              deliverableMetadataExternalSources.setPages(publication.getStart_end_pages());
              deliverableMetadataExternalSources.setPhase(phase);
              deliverableMetadataExternalSources.setCreatedBy(user);
              deliverableMetadataExternalSources.setVolume(publication.getVolume());
              if (publication.getGardian() != null) {
                deliverableMetadataExternalSources.setGardianAccessibility(publication.getGardian().getAccessibility());
                deliverableMetadataExternalSources.setGardianFindability(publication.getGardian().getFindability());
                deliverableMetadataExternalSources
                  .setGardianInteroperability(publication.getGardian().getInteroperability());
                deliverableMetadataExternalSources.setGardianReusability(publication.getGardian().getReusability());
              }
              deliverableMetadataExternalSources = deliverableMetadataExternalSourcesManager
                .saveDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
              // deliverableMetadataExternalSourcesManager.replicate(deliverableMetadataExternalSources, phase);

              // save institutions with a percentage above APCONSTANT percentage acceptance in deliverable affiliation
              for (PublicationInstitutionWOS institution : publication.getOrganizations()) {
                if (institution.getConfidant() != null
                  && institution.getConfidant().longValue() >= APConstants.ACCEPTATION_PERCENTAGE) {
                  // Save or update the Affiliations
                  final Long institutionID = institution.getClarisa_id();

                  List<DeliverableAffiliation> deliverableAffiliations =
                    deliverableAffiliationManager.findByPhaseAndDeliverable(phase, deliverable);
                  DeliverableAffiliation deliverableAffiliation =
                    deliverableAffiliations != null ? deliverableAffiliations.stream()
                      .filter(c -> c.isActive() && c.getInstitution().getId().longValue() == institutionID).findFirst()
                      .orElse(null) : null;
                  /*
                   * deliverableMetadataExternalSources.getDeliverableAffiliations().stream()
                   * .filter(c -> c.isActive() && c.getInstitution().getId().longValue() == institutionID).findFirst()
                   * .orElse(null);
                   */

                  if (deliverableAffiliation == null) {
                    deliverableAffiliation = new DeliverableAffiliation();
                    deliverableAffiliation.setCreatedBy(user);
                  } else {
                    deliverableAffiliation.setModifiedBy(user);
                  }
                  Institution institutionAffiliation =
                    institutionManager.getInstitutionById(institution.getClarisa_id());
                  deliverableAffiliation.setInstitution(institutionAffiliation);
                  deliverableAffiliation.setInstitutionMatchConfidence(institution.getConfidant().intValue());
                  deliverableAffiliation.setDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                  deliverableAffiliation.setInstitutionNameWebOfScience(institution.getName());
                  deliverableAffiliation.setPhase(phase);
                  deliverableAffiliation.setDeliverable(deliverable);
                  deliverableAffiliation =
                    deliverableAffiliationManager.saveDeliverableAffiliation(deliverableAffiliation);
                  // deliverableAffiliationManager.replicate(deliverableAffiliation, phase);
                }
                // save institutions with a percentage below APCONSTANT percentage acceptance in deliverable
                // affiliation not mapped
                if (institution.getConfidant() != null
                  && (institution.getConfidant().longValue() < APConstants.ACCEPTATION_PERCENTAGE
                    || institution.getConfidant() == null)) {
                  DeliverableAffiliationsNotMapped deliverableAffiliationsNotMapped =
                    new DeliverableAffiliationsNotMapped();
                  deliverableAffiliationsNotMapped.setCountry(institution.getCountry());
                  deliverableAffiliationsNotMapped
                    .setDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                  deliverableAffiliationsNotMapped.setInstitutionMatchConfidence(institution.getConfidant().intValue());
                  deliverableAffiliationsNotMapped.setName(institution.getName());
                  deliverableAffiliationsNotMapped.setFullAddress(institution.getFull_address());
                  deliverableAffiliationsNotMapped.setPossibleInstitution(institution.getClarisa_id() != null
                    ? institutionManager.getInstitutionById(institution.getClarisa_id()) : null);
                  deliverableAffiliationsNotMapped = deliverableAffiliationsNotMappedManager
                    .saveDeliverableAffiliationsNotMapped(deliverableAffiliationsNotMapped);
                  // deliverableAffiliationsNotMappedManager.replicate(deliverableAffiliationsNotMapped, phase);
                }
              }

              // Delete Affiliations that are not listed from WOS service
              List<DeliverableAffiliation> deliverableAffiliations =
                deliverableAffiliationManager.findByPhaseAndDeliverable(phase, deliverable);
              if (deliverableAffiliations == null) {
                deliverableAffiliations = new ArrayList<DeliverableAffiliation>();
              }
              List<DeliverableAffiliation> affiliationsDelete = new ArrayList<DeliverableAffiliation>();
              for (DeliverableAffiliation affiliation : deliverableAffiliations.stream().filter(c -> c.isActive())
                .collect(Collectors.toList())) {
                boolean delete = true;
                for (PublicationInstitutionWOS institutionsWOS : publication.getOrganizations()) {
                  if (institutionsWOS.getClarisa_id() != null
                    && affiliation.getInstitution().getId().longValue() == institutionsWOS.getClarisa_id().longValue()
                    && institutionsWOS.getConfidant() >= APConstants.ACCEPTATION_PERCENTAGE) {
                    delete = false;
                  }
                }
                if (delete) {
                  affiliationsDelete.add(affiliation);
                }
              }
              final Long deliverableMetadataExternalSourcesID = deliverableMetadataExternalSources.getId();
              // Delete Affiliations Not mapped that probably will be mapped
              ;
              List<DeliverableAffiliationsNotMapped> affiliationsNotMappedDelete =
                new ArrayList<DeliverableAffiliationsNotMapped>();
              for (DeliverableAffiliationsNotMapped affiliationsNotMapped : deliverableAffiliationsNotMappedManager
                .findAll().stream().filter(c -> c != null && c.isActive() && c.getDeliverableMetadataExternalSources()
                  .getId().longValue() == deliverableMetadataExternalSourcesID.longValue())
                .collect(Collectors.toList())) {
                boolean delete = true;
                for (PublicationInstitutionWOS institutionsWOS : publication.getOrganizations()) {
                  if (affiliationsNotMapped.getPossibleInstitution() != null
                    && affiliationsNotMapped.getName().equals(institutionsWOS.getName())
                    && institutionsWOS.getConfidant() < APConstants.ACCEPTATION_PERCENTAGE) {
                    delete = false;
                  }
                }
                if (delete) {
                  affiliationsNotMappedDelete.add(affiliationsNotMapped);
                }
              }
              for (DeliverableAffiliation dataDelete : affiliationsDelete) {
                deliverableAffiliationManager.deleteDeliverableAffiliation(dataDelete.getId());
                // deliverableAffiliationManager.replicate(dataDelete, phase);
              }

              for (DeliverableAffiliationsNotMapped dataDelete : affiliationsNotMappedDelete) {
                deliverableAffiliationsNotMappedManager.deleteDeliverableAffiliationsNotMapped(dataDelete.getId());
                // deliverableAffiliationsNotMappedManager.replicate(dataDelete, phase);
              }

              // delete all authors and insert again
              externalSourceAuthorManager.deleteAllAuthorsFromPhase(deliverable, phase);
              // save authors of WOS external sources authors
              if (publication.getAuthors() != null) {
                for (PublicationAuthorWOS author : publication.getAuthors()) {
                  ExternalSourceAuthor externalSourceAuthor = new ExternalSourceAuthor();
                  externalSourceAuthor.setDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                  externalSourceAuthor.setCreatedBy(user);
                  externalSourceAuthor.setFullName(author.getFull_name());
                  externalSourceAuthor = externalSourceAuthorManager.saveExternalSourceAuthor(externalSourceAuthor);
                  // externalSourceAuthorManager.replicate(externalSourceAuthor, phase);
                }
              }
              // save altmetrics information in deliverable altmetrics
              if (publication.getAltmetric() != null) {
                DeliverableAltmetricInfo altmetrics = deliverable.getDeliverableAltmetricInfo(phase);
                if (altmetrics == null) {
                  altmetrics = new DeliverableAltmetricInfo();
                }
                altmetrics.setDeliverable(deliverable);
                altmetrics.setAltmetricId(publication.getAltmetric().getAltmetric_id());
                altmetrics.setAltmetricJid(publication.getAltmetric().getAltmetric_jid());
                String authors = "";
                boolean init = true;
                for (String data : publication.getAltmetric().getAuthors()) {
                  if (init) {
                    authors += data;
                    init = false;
                  } else {
                    authors += ";" + data;
                  }
                }
                altmetrics.setAuthors(authors);
                altmetrics.setCitedByBlogs(publication.getAltmetric().getCited_by_posts_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_posts_count()) : null);
                altmetrics.setCitedByDelicious(publication.getAltmetric().getCited_by_delicious_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_delicious_count()) : null);
                altmetrics.setCitedByFacebookPages(publication.getAltmetric().getCited_by_fbwalls_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_fbwalls_count()) : null);
                altmetrics.setCitedByGooglePlusUsers(publication.getAltmetric().getCited_by_gplus_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_gplus_count()) : null);
                altmetrics.setCitedByForumUsers(publication.getAltmetric().getCited_by_forum_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_forum_count()) : null);
                altmetrics.setCitedByLinkedinUsers(publication.getAltmetric().getCited_by_linkedin_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_linkedin_count()) : null);
                altmetrics.setCitedByNewsOutlets(publication.getAltmetric().getCited_by_msm_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_msm_count()) : null);
                altmetrics
                  .setCitedByPeerReviewSites(publication.getAltmetric().getCited_by_peer_review_sites_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_peer_review_sites_count()) : null);
                altmetrics.setCitedByPinterestUsers(publication.getAltmetric().getCited_by_pinners_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_pinners_count()) : null);
                altmetrics.setCitedByPolicies(publication.getAltmetric().getCited_by_policies_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_policies_count()) : null);
                altmetrics.setCitedByRedditUsers(publication.getAltmetric().getCited_by_rdts_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_rdts_count()) : null);
                altmetrics
                  .setCitedByResearchHighlightPlatforms(publication.getAltmetric().getCited_by_rh_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_rh_count()) : null);
                altmetrics.setCitedByStackExchangeResources(publication.getAltmetric().getCited_by_qs_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_qs_count()) : null);
                altmetrics.setCitedByTwitterUsers(publication.getAltmetric().getCited_by_tweeters_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_tweeters_count()) : null);
                altmetrics.setCitedByWeiboUsers(publication.getAltmetric().getCited_by_weibo_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_weibo_count()) : null);
                altmetrics.setCitedByWikipediaPages(publication.getAltmetric().getCited_by_wikipedia_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_wikipedia_count()) : null);
                altmetrics.setCitedByYoutubeChannels(publication.getAltmetric().getCited_by_videos_count() != null
                  ? Integer.valueOf(publication.getAltmetric().getCited_by_videos_count()) : null);
                altmetrics.setType(publication.getAltmetric().getType());
                altmetrics.setTitle(publication.getAltmetric().getTitle());
                altmetrics.setPhase(phase);
                altmetrics.setModifiedBy(user);
                altmetrics.setDoi(publication.getAltmetric().getDoi());
                altmetrics.setUrl(publication.getAltmetric().getUrl());
                altmetrics.setUri(publication.getAltmetric().getUri());
                altmetrics.setScore(publication.getAltmetric().getScore());
                altmetrics.setJournal(publication.getJournal_name());
                altmetrics.setHandle(publication.getAltmetric().getHandle());
                altmetrics.setDetailsUrl(newPublicationDTO.getDoi());
                altmetrics.setAddedOn(publication.getAltmetric().getAdded_on() != null
                  ? new Date(Long.parseLong(publication.getAltmetric().getAdded_on())) : null);
                altmetrics.setPublishedOn(publication.getAltmetric().getPublished_on() != null
                  ? new Date(Long.parseLong(publication.getAltmetric().getPublished_on())) : null);
                altmetrics.setIsOpenAccess(String.valueOf(publication.getAltmetric().isIs_oa()));
                altmetrics.setLastSync(new Date(Calendar.getInstance().getTimeInMillis()));
                altmetrics.setLastUpdated(publication.getAltmetric().getLast_updated() != null
                  ? new Date(Long.parseLong(publication.getAltmetric().getLast_updated())) : null);
                altmetrics.setImageSmall(publication.getAltmetric().getImages() != null
                  ? publication.getAltmetric().getImages().getSmall() : null);
                altmetrics.setImageMedium(publication.getAltmetric().getImages() != null
                  ? publication.getAltmetric().getImages().getMedium() : null);
                altmetrics.setImageLarge(publication.getAltmetric().getImages() != null
                  ? publication.getAltmetric().getImages().getLarge() : null);
                altmetrics = deliverableAltmetricInfoManager.saveDeliverableAltmetricInfo(altmetrics);
                // deliverableAltmetricInfoManager.replicate(altmetrics, phase);
              }

            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

      } else {
        fieldErrors.add(new FieldErrorDTO("putDeliverable", "phase", "Error while creating a publication "));
        throw new MARLOFieldValidationException("Field Validation errors", "",
          fieldErrors.stream()
            .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList()));
      }


    }

    if (!fieldErrors.isEmpty()) {
      // validators
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return idDeliverableDB;
  }

  public Long putDeliverableOtherById(Long idDeliverable, NewPublicationOtherDTO newPublicationDTO,
    String CGIARentityAcronym, User user) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    DeliverableInfo deliverableInfo = null;
    Long idDeliverableDB = null;
    Phase phase = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putDeliverable", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putDeliverable", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putDeliverable", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newPublicationDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("putDeliverable", "PhaseEntity", "Phase must not be null"));
    } else {
      String strippedPhaseName = StringUtils.stripToNull(newPublicationDTO.getPhase().getName());
      if (strippedPhaseName == null || newPublicationDTO.getPhase().getYear() == null
      // DANGER! Magic number ahead
        || newPublicationDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("putDeliverable", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
            && p.getYear() == newPublicationDTO.getPhase().getYear()
            && StringUtils.equalsIgnoreCase(p.getName(), strippedPhaseName))
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors.add(new FieldErrorDTO("putDeliverable", "phase", newPublicationDTO.getPhase().getName() + ' '
            + newPublicationDTO.getPhase().getYear() + " is an invalid phase"));
        }
      }
    }

    Deliverable deliverable = deliverableManager.getDeliverableById(idDeliverable);
    if (deliverable == null || deliverable.isActive() == false) {
      fieldErrors.add(
        new FieldErrorDTO("putDeliverable", "DeliverableEntity", idDeliverable + " is an invalid id of a Deliverable"));
    } else {
      if (phase != null) {
        deliverableInfo = deliverable.getDeliverableInfo(phase);
        if (deliverableInfo == null) {
          fieldErrors.add(new FieldErrorDTO("putDeliverable", "DeliverableEntity",
            "The Deliverable with id " + idDeliverable + " do not correspond to the phase entered"));
        } else {
          if (globalUnitEntity != null && deliverableInfo.getPhase().getCrp().getId() != globalUnitEntity.getId()) {
            fieldErrors.add(new FieldErrorDTO("putDeliverable", "DeliverableEntity",
              "The Deliverable with id " + idDeliverable + " do not correspond to the CRP entered"));
          }
        }
      }
    }
    DeliverableType deliverableType = deliverableTypeManager.getDeliverableTypeById(newPublicationDTO.getType());
    if (deliverableType == null) {
      fieldErrors.add(new FieldErrorDTO("putDeliverable", "DeliverableType",
        newPublicationDTO.getType() + " is an invalid publication Type"));
    }

    if (phase != null && !phase.getEditable()) {
      fieldErrors.add(new FieldErrorDTO("putDeliverable", "phase", "this  is an closed phase"));
    }

    if (fieldErrors.isEmpty()) {
      // create deliverable
      deliverable.setCrp(globalUnitEntity);
      deliverable.setPhase(phase);
      deliverable.setIsPublication(true);
      // save deliverables
      deliverable = deliverableManager.saveDeliverable(deliverable);
      idDeliverableDB = deliverable.getId();

      if (idDeliverableDB != null) {
        // create deliverable info
        deliverable = deliverableManager.getDeliverableById(idDeliverableDB);
        deliverableInfo = deliverable.getDeliverableInfo();
        deliverableInfo.setDeliverable(deliverable);
        deliverableInfo.setTitle(newPublicationDTO.getTitle());
        deliverableInfo.setYear(deliverable.getPhase().getYear());
        deliverableInfo.setDeliverableType(deliverableType);
        deliverableInfo.setPhase(phase);
        deliverableInfo.setStatus(APConstants.CLARISA_PUBLICATIONS_STATUS);
        deliverableInfo.setModificationJustification(APConstants.MESSAGE_MODIFICATION_JUSTIFICATION
          + sdf.format(new Date(Calendar.getInstance().getTimeInMillis())));
        // save deliverableinfo
        deliverableInfoManager.saveDeliverableInfo(deliverableInfo);

        // create deliverable dissemination data ???
        final long phaseID = phase.getId();
        DeliverableDissemination deliverableDissemination = deliverable.getDeliverableDisseminations().stream()
          .filter(c -> c.getPhase().getId().longValue() == phaseID).findFirst().orElse(null);

        if (deliverableDissemination == null) {
          deliverableDissemination = new DeliverableDissemination();
        }
        deliverableDissemination.setIsOpenAccess(newPublicationDTO.getIsOpenAccess());
        deliverableDissemination.setDeliverable(deliverable);
        deliverableDissemination.setArticleUrl(newPublicationDTO.getArticleURL());
        deliverableDissemination.setDisseminationChannel("other");
        deliverableDissemination.setDisseminationUrl("Not Defined");
        if ((newPublicationDTO.getDoi() == null || newPublicationDTO.getDoi().equals(""))
          && newPublicationDTO.getArticleURL() != null && !newPublicationDTO.getArticleURL().isEmpty()) {
          deliverableDissemination.setHasDOI(true);
        }
        deliverableDissemination.setPhase(phase);
        deliverableDisseminationManager.saveDeliverableDissemination(deliverableDissemination);

        // creatte deliverable publication metadata
        DeliverablePublicationMetadata deliverablePublicationMetadata = deliverable.getDeliverablePublicationMetadatas()
          .stream().filter(m -> m.getPhase().getId() == phaseID).findFirst().orElse(null);
        if (deliverablePublicationMetadata == null) {
          deliverablePublicationMetadata = new DeliverablePublicationMetadata();
        }
        deliverablePublicationMetadata.setDeliverable(deliverable);
        deliverablePublicationMetadata.setPhase(phase);
        deliverablePubMetadataManager.saveDeliverablePublicationMetadata(deliverablePublicationMetadata);

        // get element ID from econded_name to get Handle and DOI and create
        Long phaseId = phase.getId();
        List<DeliverableMetadataElement> phaseMetadata = deliverable.getDeliverableMetadataElements().stream()
          .filter(m -> m.getPhase().getId() == phaseId).collect(Collectors.toList());
        Map<String, DeliverableMetadataElement> metadataElements = this.getMetadataElements(phaseMetadata);
        if (metadataElements != null && !metadataElements.isEmpty()) {
          // deliverable metadataelement handle
          DeliverableMetadataElement deliverableMetadataElementHandle =
            metadataElements.get(APConstants.METADATAELEMENTHANDLE);
          deliverableMetadataElementHandle.setDeliverable(deliverable);
          deliverableMetadataElementHandle.setPhase(phase);
          deliverableMetadataElementHandle
            .setElementValue(newPublicationDTO.getHandle() == null ? "" : newPublicationDTO.getHandle());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementHandle);

          // deliverable metadataelement DOI
          DeliverableMetadataElement deliverableMetadataElementDoi =
            metadataElements.get(APConstants.METADATAELEMENTDOI);
          deliverableMetadataElementDoi.setDeliverable(deliverable);
          deliverableMetadataElementDoi.setPhase(phase);
          deliverableMetadataElementDoi.setElementValue(
            newPublicationDTO.getDoi() == null ? "" : DOIService.tryGetDoiName(newPublicationDTO.getDoi()));
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementDoi);

          // deliverable metadataelement Title
          DeliverableMetadataElement deliverableMetadataElementTitle =
            metadataElements.get(APConstants.METADATAELEMENTTITLE);
          deliverableMetadataElementTitle.setDeliverable(deliverable);
          deliverableMetadataElementTitle.setPhase(phase);
          deliverableMetadataElementTitle
            .setElementValue(newPublicationDTO.getTitle() == null ? "" : newPublicationDTO.getTitle());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementTitle);


          // deliverable metadataelement Publication
          DeliverableMetadataElement deliverableMetadataElementPublication =
            metadataElements.get(APConstants.METADATAELEMENTPUBLICATION);
          deliverableMetadataElementPublication.setDeliverable(deliverable);
          deliverableMetadataElementPublication.setPhase(phase);
          deliverableMetadataElementPublication.setElementValue(String.valueOf(newPublicationDTO.getYear()));
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementPublication);

          // changes for authors 2019-09-03 apply to a field to include all authors in a single row
          // setdeliverables author
          DeliverableMetadataElement deliverableMetadataElementAuthors =
            metadataElements.get(APConstants.METADATAELEMENTAUTHORS);
          deliverableMetadataElementAuthors.setDeliverable(deliverable);
          deliverableMetadataElementAuthors.setPhase(phase);
          deliverableMetadataElementAuthors.setElementValue(String.valueOf(newPublicationDTO.getAuthors()));
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementAuthors);
        }


        for (DeliverableUserDTO deliverableUserDTO : newPublicationDTO.getAuthorlist()) {
          boolean found = false;
          for (DeliverableUser authorsList : deliverable.getDeliverableUsers().stream().filter(c -> c.isActive())
            .collect(Collectors.toList())) {
            if (deliverableUserDTO.getFirstName().toUpperCase().equals(authorsList.getFirstName().toUpperCase())
              && deliverableUserDTO.getLastName().toUpperCase().equals(authorsList.getLastName().toUpperCase())) {
              found = true;
            }
          }
          if (!found) {
            DeliverableUser deliverableUser = new DeliverableUser();
            deliverableUser.setFirstName(deliverableUserDTO.getFirstName());
            deliverableUser.setLastName(deliverableUserDTO.getLastName());
            deliverableUser.setDeliverable(deliverable);
            deliverableUser.setPhase(phase);
            deliverableUserManager.saveDeliverableUser(deliverableUser);
          }
        }
        for (DeliverableUser authorsList : deliverable.getDeliverableUsers().stream().filter(c -> c.isActive())
          .collect(Collectors.toList())) {
          boolean found = true;
          for (DeliverableUserDTO deliverableUserDTO : newPublicationDTO.getAuthorlist()) {
            if (deliverableUserDTO.getFirstName().toUpperCase().equals(authorsList.getFirstName().toUpperCase())
              && deliverableUserDTO.getLastName().toUpperCase().equals(authorsList.getLastName().toUpperCase())) {
              found = false;
            }
          }
          if (found) {
            deliverableUserManager.deleteDeliverableUser(authorsList.getId());
          }
        }

        // web of science integration
        if (newPublicationDTO.getDoi() != null) {
          try {
            JsonElement json = this.getServiceWOS(
              "http://clarisa.wos.api.mel.cgiar.org/?link=" + DOIService.tryGetDoiName(newPublicationDTO.getDoi()));
            System.out.println(json.toString());

            PublicationWOS publication = new Gson().fromJson(json, PublicationWOS.class);
            if (publication != null) {
              // save deliverable metadata external sources WOS and GARDIAN
              final Long deliverableID = deliverable.getId();
              DeliverableMetadataExternalSources deliverableMetadataExternalSources =
                deliverable.getDeliverableMetadataExternalSources().stream()
                  .filter(c -> c.getDeliverable().getId().equals(deliverableID)).findFirst().orElse(null);
              if (deliverableMetadataExternalSources != null) {
                deliverableMetadataExternalSources.setDeliverable(deliverable);
                deliverableMetadataExternalSources.setDoi(publication.getDoi());
                deliverableMetadataExternalSources.setIsiStatus(this.getBooleanString(publication.getIs_isi()));
                deliverableMetadataExternalSources.setJournalName(publication.getJournal_name());
                deliverableMetadataExternalSources.setTitle(publication.getTitle());
                deliverableMetadataExternalSources.setOpenAccessStatus(this.getBooleanString(publication.getIs_oa()));
                deliverableMetadataExternalSources.setOpenAccessLink(publication.getOa_link());
                deliverableMetadataExternalSources.setPublicationType(publication.getPublication_type());
                deliverableMetadataExternalSources.setPublicationYear(publication.getPublication_year() != null
                  ? Integer.valueOf(publication.getPublication_year()) : null);
                deliverableMetadataExternalSources.setSource(publication.getSource());
                deliverableMetadataExternalSources.setUrl(publication.getDoi());
                deliverableMetadataExternalSources.setPages(publication.getStart_end_pages());
                deliverableMetadataExternalSources.setPhase(phase);
                deliverableMetadataExternalSources.setCreatedBy(user);
                deliverableMetadataExternalSources.setVolume(publication.getVolume());
                if (publication.getGardian() != null) {
                  deliverableMetadataExternalSources
                    .setGardianAccessibility(publication.getGardian().getAccessibility());
                  deliverableMetadataExternalSources.setGardianFindability(publication.getGardian().getFindability());
                  deliverableMetadataExternalSources
                    .setGardianInteroperability(publication.getGardian().getInteroperability());
                  deliverableMetadataExternalSources.setGardianReusability(publication.getGardian().getReusability());
                }
                deliverableMetadataExternalSources = deliverableMetadataExternalSourcesManager
                  .saveDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                // deliverableMetadataExternalSourcesManager.replicate(deliverableMetadataExternalSources, phase);

                // save institutions with a percentage above APCONSTANT percentage acceptance in deliverable affiliation
                for (PublicationInstitutionWOS institution : publication.getOrganizations()) {
                  if (institution.getConfidant() != null
                    && institution.getConfidant().longValue() >= APConstants.ACCEPTATION_PERCENTAGE) {
                    // Save or update the Affiliations
                    final Long institutionID = institution.getClarisa_id();
                    List<DeliverableAffiliation> deliverableAffiliations =
                      deliverableAffiliationManager.findByPhaseAndDeliverable(phase, deliverable);
                    DeliverableAffiliation deliverableAffiliation =
                      deliverableAffiliations != null ? deliverableAffiliations.stream()
                        .filter(c -> c.isActive() && c.getInstitution().getId().longValue() == institutionID)
                        .findFirst().orElse(null) : null;
                    if (deliverableAffiliation == null) {
                      deliverableAffiliation = new DeliverableAffiliation();
                      deliverableAffiliation.setCreatedBy(user);
                    } else {
                      deliverableAffiliation.setModifiedBy(user);
                    }
                    Institution institutionAffiliation =
                      institutionManager.getInstitutionById(institution.getClarisa_id());
                    deliverableAffiliation.setInstitution(institutionAffiliation);
                    deliverableAffiliation.setInstitutionMatchConfidence(institution.getConfidant().intValue());
                    deliverableAffiliation.setDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                    deliverableAffiliation.setInstitutionNameWebOfScience(institution.getName());
                    deliverableAffiliation.setPhase(phase);
                    deliverableAffiliation.setDeliverable(deliverable);
                    deliverableAffiliation =
                      deliverableAffiliationManager.saveDeliverableAffiliation(deliverableAffiliation);
                    // deliverableAffiliationManager.replicate(deliverableAffiliation, phase);
                  }
                  // save institutions with a percentage below APCONSTANT percentage acceptance in deliverable
                  // affiliation not mapped
                  if (institution.getConfidant() != null
                    && (institution.getConfidant().longValue() < APConstants.ACCEPTATION_PERCENTAGE
                      || institution.getConfidant() == null)) {
                    DeliverableAffiliationsNotMapped deliverableAffiliationsNotMapped =
                      new DeliverableAffiliationsNotMapped();
                    deliverableAffiliationsNotMapped.setCountry(institution.getCountry());
                    deliverableAffiliationsNotMapped
                      .setDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                    deliverableAffiliationsNotMapped
                      .setInstitutionMatchConfidence(institution.getConfidant().intValue());
                    deliverableAffiliationsNotMapped.setName(institution.getName());
                    deliverableAffiliationsNotMapped.setFullAddress(institution.getFull_address());
                    deliverableAffiliationsNotMapped.setPossibleInstitution(institution.getClarisa_id() != null
                      ? institutionManager.getInstitutionById(institution.getClarisa_id()) : null);
                    deliverableAffiliationsNotMapped = deliverableAffiliationsNotMappedManager
                      .saveDeliverableAffiliationsNotMapped(deliverableAffiliationsNotMapped);
                    // deliverableAffiliationsNotMappedManager.replicate(deliverableAffiliationsNotMapped, phase);
                  }
                }

                // Delete Affiliations that are not listed from WOS service
                List<DeliverableAffiliation> deliverableAffiliations =
                  deliverableAffiliationManager.findByPhaseAndDeliverable(phase, deliverable);
                if (deliverableAffiliations == null) {
                  deliverableAffiliations = new ArrayList<DeliverableAffiliation>();
                }
                List<DeliverableAffiliation> affiliationsDelete = new ArrayList<DeliverableAffiliation>();
                for (DeliverableAffiliation affiliation : deliverableAffiliations.stream().filter(c -> c.isActive())
                  .collect(Collectors.toList())) {
                  boolean delete = true;
                  for (PublicationInstitutionWOS institutionsWOS : publication.getOrganizations()) {
                    if (institutionsWOS.getClarisa_id() != null
                      && affiliation.getInstitution().getId().longValue() == institutionsWOS.getClarisa_id().longValue()
                      && institutionsWOS.getConfidant() >= APConstants.ACCEPTATION_PERCENTAGE) {
                      delete = false;
                    }
                  }
                  if (delete) {
                    affiliationsDelete.add(affiliation);
                  }
                }

                // Delete Affiliations Not mapped that probably will be mapped
                final Long deliverableMetadataExternalSourcesID = deliverableMetadataExternalSources.getId();
                List<DeliverableAffiliationsNotMapped> affiliationsNotMappedDelete =
                  new ArrayList<DeliverableAffiliationsNotMapped>();

                for (DeliverableAffiliationsNotMapped affiliationsNotMapped : deliverableAffiliationsNotMappedManager
                  .findAll().stream()
                  .filter(
                    c -> c != null && c.isActive()
                      && c.getDeliverableMetadataExternalSources().getId()
                        .longValue() == deliverableMetadataExternalSourcesID.longValue())
                  .collect(Collectors.toList())) {
                  boolean delete = true;
                  for (PublicationInstitutionWOS institutionsWOS : publication.getOrganizations()) {
                    if (affiliationsNotMapped.getPossibleInstitution() != null
                      && affiliationsNotMapped.getName().equals(institutionsWOS.getName())
                      && institutionsWOS.getConfidant() < APConstants.ACCEPTATION_PERCENTAGE) {
                      delete = false;
                    }
                  }
                  if (delete) {
                    affiliationsNotMappedDelete.add(affiliationsNotMapped);
                  }
                }
                for (DeliverableAffiliation dataDelete : affiliationsDelete) {
                  deliverableAffiliationManager.deleteDeliverableAffiliation(dataDelete.getId());
                  // deliverableAffiliationManager.replicate(dataDelete, phase);
                }

                for (DeliverableAffiliationsNotMapped dataDelete : affiliationsNotMappedDelete) {
                  deliverableAffiliationsNotMappedManager.deleteDeliverableAffiliationsNotMapped(dataDelete.getId());
                  // deliverableAffiliationsNotMappedManager.replicate(dataDelete, phase);
                }

                // delete all authors and insert again
                externalSourceAuthorManager.deleteAllAuthorsFromPhase(deliverable, phase);
                // save authors of WOS external sources authors
                if (publication.getAuthors() != null) {
                  for (PublicationAuthorWOS author : publication.getAuthors()) {
                    ExternalSourceAuthor externalSourceAuthor = new ExternalSourceAuthor();
                    externalSourceAuthor.setDeliverableMetadataExternalSources(deliverableMetadataExternalSources);
                    externalSourceAuthor.setCreatedBy(user);
                    externalSourceAuthor.setFullName(author.getFull_name());
                    externalSourceAuthor = externalSourceAuthorManager.saveExternalSourceAuthor(externalSourceAuthor);
                    // externalSourceAuthorManager.replicate(externalSourceAuthor, phase);
                  }
                }
                // save altmetrics information in deliverable altmetrics
                if (publication.getAltmetric() != null) {
                  DeliverableAltmetricInfo altmetrics = deliverable.getDeliverableAltmetricInfo(phase);
                  if (altmetrics == null) {
                    altmetrics = new DeliverableAltmetricInfo();
                  }
                  altmetrics.setDeliverable(deliverable);
                  altmetrics.setAltmetricId(publication.getAltmetric().getAltmetric_id());
                  altmetrics.setAltmetricJid(publication.getAltmetric().getAltmetric_jid());
                  String authors = "";
                  boolean init = true;
                  for (String data : publication.getAltmetric().getAuthors()) {
                    if (init) {
                      authors += data;
                      init = false;
                    } else {
                      authors += ";" + data;
                    }
                  }
                  altmetrics.setAuthors(authors);
                  altmetrics.setCitedByBlogs(publication.getAltmetric().getCited_by_posts_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_posts_count()) : null);
                  altmetrics.setCitedByDelicious(publication.getAltmetric().getCited_by_delicious_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_delicious_count()) : null);
                  altmetrics.setCitedByFacebookPages(publication.getAltmetric().getCited_by_fbwalls_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_fbwalls_count()) : null);
                  altmetrics.setCitedByGooglePlusUsers(publication.getAltmetric().getCited_by_gplus_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_gplus_count()) : null);
                  altmetrics.setCitedByForumUsers(publication.getAltmetric().getCited_by_forum_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_forum_count()) : null);
                  altmetrics.setCitedByLinkedinUsers(publication.getAltmetric().getCited_by_linkedin_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_linkedin_count()) : null);
                  altmetrics.setCitedByNewsOutlets(publication.getAltmetric().getCited_by_msm_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_msm_count()) : null);
                  altmetrics
                    .setCitedByPeerReviewSites(publication.getAltmetric().getCited_by_peer_review_sites_count() != null
                      ? Integer.valueOf(publication.getAltmetric().getCited_by_peer_review_sites_count()) : null);
                  altmetrics.setCitedByPinterestUsers(publication.getAltmetric().getCited_by_pinners_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_pinners_count()) : null);
                  altmetrics.setCitedByPolicies(publication.getAltmetric().getCited_by_policies_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_policies_count()) : null);
                  altmetrics.setCitedByRedditUsers(publication.getAltmetric().getCited_by_rdts_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_rdts_count()) : null);
                  altmetrics
                    .setCitedByResearchHighlightPlatforms(publication.getAltmetric().getCited_by_rh_count() != null
                      ? Integer.valueOf(publication.getAltmetric().getCited_by_rh_count()) : null);
                  altmetrics.setCitedByStackExchangeResources(publication.getAltmetric().getCited_by_qs_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_qs_count()) : null);
                  altmetrics.setCitedByTwitterUsers(publication.getAltmetric().getCited_by_tweeters_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_tweeters_count()) : null);
                  altmetrics.setCitedByWeiboUsers(publication.getAltmetric().getCited_by_weibo_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_weibo_count()) : null);
                  altmetrics.setCitedByWikipediaPages(publication.getAltmetric().getCited_by_wikipedia_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_wikipedia_count()) : null);
                  altmetrics.setCitedByYoutubeChannels(publication.getAltmetric().getCited_by_videos_count() != null
                    ? Integer.valueOf(publication.getAltmetric().getCited_by_videos_count()) : null);
                  altmetrics.setType(publication.getAltmetric().getType());
                  altmetrics.setTitle(publication.getAltmetric().getTitle());
                  altmetrics.setPhase(phase);
                  altmetrics.setModifiedBy(user);
                  altmetrics.setDoi(publication.getAltmetric().getDoi());
                  altmetrics.setUrl(publication.getAltmetric().getUrl());
                  altmetrics.setUri(publication.getAltmetric().getUri());
                  altmetrics.setScore(publication.getAltmetric().getScore());
                  altmetrics.setJournal(publication.getJournal_name());
                  altmetrics.setHandle(publication.getAltmetric().getHandle());
                  altmetrics.setDetailsUrl(newPublicationDTO.getDoi());
                  altmetrics.setAddedOn(publication.getAltmetric().getAdded_on() != null
                    ? new Date(Long.parseLong(publication.getAltmetric().getAdded_on())) : null);
                  altmetrics.setPublishedOn(publication.getAltmetric().getPublished_on() != null
                    ? new Date(Long.parseLong(publication.getAltmetric().getPublished_on())) : null);
                  altmetrics.setIsOpenAccess(String.valueOf(publication.getAltmetric().isIs_oa()));
                  altmetrics.setLastSync(new Date(Calendar.getInstance().getTimeInMillis()));
                  altmetrics.setLastUpdated(publication.getAltmetric().getLast_updated() != null
                    ? new Date(Long.parseLong(publication.getAltmetric().getLast_updated())) : null);
                  altmetrics.setImageSmall(publication.getAltmetric().getImages() != null
                    ? publication.getAltmetric().getImages().getSmall() : null);
                  altmetrics.setImageMedium(publication.getAltmetric().getImages() != null
                    ? publication.getAltmetric().getImages().getMedium() : null);
                  altmetrics.setImageLarge(publication.getAltmetric().getImages() != null
                    ? publication.getAltmetric().getImages().getLarge() : null);
                  altmetrics = deliverableAltmetricInfoManager.saveDeliverableAltmetricInfo(altmetrics);
                  // deliverableAltmetricInfoManager.replicate(altmetrics, phase);
                }
              }
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }

      } else {
        fieldErrors.add(new FieldErrorDTO("putDeliverable", "phase", "Error while creating a publication "));
        throw new MARLOFieldValidationException("Field Validation errors", "",
          fieldErrors.stream()
            .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList()));
      }


    }
    if (!fieldErrors.isEmpty()) {
      // validators
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return idDeliverableDB;

  }

}
