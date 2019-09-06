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
import org.cgiar.ccafs.marlo.data.manager.DeliverableDisseminationManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableInfoManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableMetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverablePublicationMetadataManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableTypeManager;
import org.cgiar.ccafs.marlo.data.manager.DeliverableUserManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.MetadataElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.DeliverableDissemination;
import org.cgiar.ccafs.marlo.data.model.DeliverableInfo;
import org.cgiar.ccafs.marlo.data.model.DeliverableMetadataElement;
import org.cgiar.ccafs.marlo.data.model.DeliverablePublicationMetadata;
import org.cgiar.ccafs.marlo.data.model.DeliverableType;
import org.cgiar.ccafs.marlo.data.model.DeliverableUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.MetadataElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Publication;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.DeliverableUserDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewPublicationDTO;
import org.cgiar.ccafs.marlo.rest.dto.PublicationDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.DeliverablesMapper;
import org.cgiar.ccafs.marlo.rest.mappers.PublicationsMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

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

  // Variables
  private List<FieldErrorDTO> fieldErrors;
  private long deliverablesID;

  private Deliverable deliverable;
  private DeliverableType deliverableType;
  private DeliverableInfo deliverableInfo;
  private DeliverableUser deliverableUser;
  private DeliverablePublicationMetadata deliverablePublicationMetadata;
  private DeliverableDissemination deliverableDissemination;
  private DeliverableMetadataElement deliverableMetadataElementHandle;
  private DeliverableMetadataElement deliverableMetadataElementDoi;
  private DeliverableMetadataElement deliverableMetadataElementTitle;
  private DeliverableMetadataElement deliverableMetadataElementCitation;
  private DeliverableMetadataElement deliverableMetadataElementPublication;
  private DeliverableMetadataElement deliverableMetadataElementAuthors;
  private MetadataElement metadataElementHandle;
  private MetadataElement metadataElementDoi;
  private MetadataElement metadataElementTitle;
  private MetadataElement metadataElementCitation;
  private MetadataElement metadataElementPublication;
  private MetadataElement metadataElementAuthors;
  private DeliverablesMapper deliverablesMapper;
  private PublicationsMapper publicationsMapper;


  @Inject
  public DeliverablesItem(PhaseManager phaseManager, GlobalUnitManager globalUnitManager,
    MetadataElementManager metadataElementManager, DeliverableManager deliverableManager,
    DeliverableTypeManager deliverableTypeManager, DeliverableInfoManager deliverableInfoManager,
    DeliverablePublicationMetadataManager deliverablePubMetadataManager,
    DeliverableMetadataElementManager deliverableMetadataElementManager, DeliverableUserManager deliverableUserManager,
    DeliverableDisseminationManager deliverableDisseminationManager, DeliverablesMapper deliverablesMapper,
    PublicationsMapper publicationsMapper) {
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
  }

  public long createDeliverable(NewPublicationDTO deliverable, String entityAcronym, User user) {

    this.deliverable = new Deliverable();
    this.fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      this.fieldErrors.add(new FieldErrorDTO("createDeliverable", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream()
        .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
          && c.getYear() == deliverable.getPhase().getYear()
          && c.getName().equalsIgnoreCase(deliverable.getPhase().getName()))
        .findFirst().get();

    if (phase == null) {
      this.fieldErrors
        .add(new FieldErrorDTO("createDeliverable", "phase", deliverable.getPhase().getYear() + " is an invalid year"));
    }
    if (fieldErrors.size() == 0 || fieldErrors.isEmpty()) {
      // create deliverable
      this.deliverable.setCrp(globalUnitEntity);
      this.deliverable.setPhase(phase);
      this.deliverable.setIsPublication(true);
      // save deliverables
      this.deliverable = deliverableManager.saveDeliverable(this.deliverable);
      deliverablesID = this.deliverable.getId();

      this.deliverableType = deliverableTypeManager.getDeliverableTypeById(APConstants.IMPORT_DELIVERABLE_VALUE);
      // create deliverable info
      this.deliverable = deliverableManager.getDeliverableById(this.deliverablesID);
      this.deliverableInfo = new DeliverableInfo();
      this.deliverableInfo.setDeliverable(this.deliverable);
      this.deliverableInfo.setTitle(deliverable.getTitle()); //
      // this.deliverableinfo.setDescription(deliverable.getTitle());
      this.deliverableInfo.setYear(deliverable.getPhase().getYear());
      this.deliverableInfo.setDeliverableType(deliverableType);
      this.deliverableInfo.setPhase(phase);

      // save deliverableinfo

      deliverableInfoManager.saveDeliverableInfo(this.deliverableInfo);
      // create deliverable dissemination data

      deliverableDissemination = new DeliverableDissemination();
      deliverableDissemination.setIsOpenAccess(deliverable.getIsOpenAccess());
      deliverableDissemination.setDeliverable(this.deliverable);
      deliverableDissemination.setPhase(phase);
      deliverableDisseminationManager.saveDeliverableDissemination(deliverableDissemination);

      // creatte deliverable publication metadata
      this.deliverablePublicationMetadata = new DeliverablePublicationMetadata();
      this.deliverablePublicationMetadata.setDeliverable(this.deliverable);
      this.deliverablePublicationMetadata.setIssue(deliverable.getIssue());
      this.deliverablePublicationMetadata.setJournal(deliverable.getJournal());
      this.deliverablePublicationMetadata.setPages(deliverable.getNpages());
      this.deliverablePublicationMetadata.setVolume(deliverable.getVolume());
      this.deliverablePublicationMetadata.setIsiPublication(deliverable.isISIJournal());

      this.deliverablePublicationMetadata.setPhase(phase);
      // save deliverablePublicationMetadata

      deliverablePubMetadataManager.saveDeliverablePublicationMetadata(this.deliverablePublicationMetadata);
      // get element ID from

      // econded_name to get Handle and DOI and create
      List<MetadataElement> metadataElements = new ArrayList<MetadataElement>(metadataElementManager.findAll());
      if (metadataElements != null) {


        // deliverable metadataelement handle
        this.deliverableMetadataElementHandle = new DeliverableMetadataElement();
        this.deliverableMetadataElementHandle.setDeliverable(this.deliverable);
        this.deliverableMetadataElementHandle.setPhase(phase);
        metadataElementHandle =
          metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTHANDLE))
            .collect(Collectors.toList()).get(0);
        this.deliverableMetadataElementHandle.setMetadataElement(metadataElementHandle);
        this.deliverableMetadataElementHandle.setElementValue(deliverable.getHandle());
        deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementHandle);

        // deliverable metadataelement DOI
        this.deliverableMetadataElementDoi = new DeliverableMetadataElement();
        this.deliverableMetadataElementDoi.setDeliverable(this.deliverable);
        this.deliverableMetadataElementDoi.setPhase(phase);
        metadataElementDoi = metadataElements.stream()
          .filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTDOI)).collect(Collectors.toList()).get(0);
        this.deliverableMetadataElementDoi.setMetadataElement(metadataElementDoi);
        this.deliverableMetadataElementDoi.setElementValue(deliverable.getDoi());
        deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementDoi);

        // deliverable metadataelement Title
        this.deliverableMetadataElementTitle = new DeliverableMetadataElement();
        this.deliverableMetadataElementTitle.setDeliverable(this.deliverable);
        this.deliverableMetadataElementTitle.setPhase(phase);
        metadataElementTitle =
          metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTTITLE))
            .collect(Collectors.toList()).get(0);
        this.deliverableMetadataElementTitle.setMetadataElement(metadataElementTitle);
        this.deliverableMetadataElementTitle.setElementValue(deliverable.getTitle());
        deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementTitle);

        // deliverable metadataelement Citation
        this.deliverableMetadataElementCitation = new DeliverableMetadataElement();
        this.deliverableMetadataElementCitation.setDeliverable(this.deliverable);
        this.deliverableMetadataElementCitation.setPhase(phase);
        metadataElementCitation =
          metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTCITATION))
            .collect(Collectors.toList()).get(0);
        this.deliverableMetadataElementCitation.setMetadataElement(metadataElementCitation);
        String authors = "";
        for (DeliverableUserDTO author : deliverable.getAuthorList()) {
          if (authors.isEmpty()) {
            authors = authors + "" + author.getLastName() + " " + author.getFirstName();
          } else {
            authors = authors + "," + author.getLastName() + " " + author.getFirstName();
          }
        }


        this.deliverableMetadataElementCitation.setElementValue(
          authors + "," + deliverable.getYear() + "," + deliverable.getTitle() + "," + deliverable.getJournal() + ","
            + deliverable.getVolume() + "," + deliverable.getIssue() + "," + deliverable.getNpages());
        deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementCitation);

        // deliverable metadataelement Publication
        this.deliverableMetadataElementPublication = new DeliverableMetadataElement();
        this.deliverableMetadataElementPublication.setDeliverable(this.deliverable);
        this.deliverableMetadataElementPublication.setPhase(phase);
        metadataElementPublication =
          metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTPUBLICATION))
            .collect(Collectors.toList()).get(0);
        this.deliverableMetadataElementPublication.setMetadataElement(metadataElementPublication);
        this.deliverableMetadataElementPublication.setElementValue(String.valueOf(deliverable.getYear()));
        deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementPublication);


        // changes for authors 2019-09-03 apply to a field to include all authors in a single row
        // setdeliverables author
        this.deliverableMetadataElementAuthors = new DeliverableMetadataElement();
        this.deliverableMetadataElementAuthors.setDeliverable(this.deliverable);
        this.deliverableMetadataElementAuthors.setPhase(phase);
        metadataElementAuthors =
          metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTAUTHORS))
            .collect(Collectors.toList()).get(0);
        this.deliverableMetadataElementAuthors.setMetadataElement(metadataElementAuthors);
        this.deliverableMetadataElementAuthors.setElementValue(String.valueOf(deliverable.getAuthors()));
        deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementAuthors);
      }

      for (DeliverableUserDTO deliverableUserDTO : deliverable.getAuthorList()) {
        deliverableUser = new DeliverableUser();
        deliverableUser.setFirstName(deliverableUserDTO.getFirstName());
        deliverableUser.setLastName(deliverableUserDTO.getLastName());
        deliverableUser.setDeliverable(this.deliverable);
        deliverableUser.setPhase(phase);
        deliverableUserManager.saveDeliverableUser(deliverableUser);
      }


    } else {
      // validators
      throw new MARLOFieldValidationException("Field Validation errors", "",
        this.fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return deliverablesID;
  }

  public List<PublicationDTO> getAllDeliverables(String entityAcronym, int repoyear, String repoPhase, User user) {
    List<PublicationDTO> deliverablesListDTO = new ArrayList<PublicationDTO>();
    this.fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      this.fieldErrors.add(new FieldErrorDTO("createDeliverable", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == repoyear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().get();

    if (phase == null) {
      this.fieldErrors
        .add(new FieldErrorDTO("createDeliverable", "phase", deliverable.getPhase().getYear() + " is an invalid year"));
    }
    if (fieldErrors.size() == 0 || fieldErrors.isEmpty()) {
      try {

        List<Publication> fullPublicationsList = new ArrayList<Publication>();

        List<Deliverable> deliverableList = deliverableManager.getPublicationsByPhase(phase.getId());
        for (Deliverable deliverable : deliverableList) {
          if (deliverable.getProject() == null) {
            Publication publication = new Publication();
            publication.setId(deliverable.getId());
            DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(phase);
            // getting deliverable Info
            if (deliverableInfo != null) {
              publication.setVolume(deliverableInfo.getTitle());
              publication.setYear(deliverableInfo.getYear());
              publication.setPhase(phase);
              deliverable.setCrp(phase.getCrp());
              deliverable.setDeliverableInfo(deliverableInfo);
              // getting deliverableDissemination
              List<DeliverableDissemination> deliverableDisseminationList =
                deliverable.getDeliverableDisseminations().stream()
                  .filter(deliverabledisemination -> deliverabledisemination.getPhase().getId().equals(phase.getId()))
                  .collect(Collectors.toList());
              for (DeliverableDissemination deliverableDissemination : deliverableDisseminationList) {
                if (deliverableDissemination.getPhase().getId().equals(phase.getId())) {
                  deliverable.setDissemination(deliverableDissemination);
                  break;
                }
              }
              publication.setIsOpenAccess(
                deliverable.getDissemination() != null ? (deliverable.getDissemination().getIsOpenAccess()) : false);
              // getting deliverablepublicationMetadata
              Deliverable d = deliverableManager.getDeliverableById(deliverable.getId());

              DeliverablePublicationMetadata deliverablePublicationMetadata = d.getPublication(phase);
              if (deliverablePublicationMetadata.getId() != null) {
                publication.setISIJournal(deliverablePublicationMetadata.getIsiPublication() != null
                  ? deliverablePublicationMetadata.getIsiPublication() : false);
                publication.setJournal(deliverablePublicationMetadata.getJournal());
                publication.setIssue(deliverablePublicationMetadata.getIssue());
                publication.setNpages(deliverablePublicationMetadata.getPages());
                publication.setVolume(deliverablePublicationMetadata.getVolume());
              }
              // Getting deliverablemetadataelement
              List<DeliverableMetadataElement> deliverableMetadataElementList =
                deliverable.getDeliverableMetadataElements().stream()
                  .filter(
                    deliverableMetadataElement -> deliverableMetadataElement.getPhase().getId().equals(phase.getId()))
                  .collect(Collectors.toList());
              for (DeliverableMetadataElement deliverableMetadataElement : deliverableMetadataElementList) {
                if (!deliverableMetadataElement.getElementValue().trim().isEmpty()) {
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
              List<DeliverableUser> deliverableUserList = deliverable.getDeliverableUsers().stream()
                .filter(c -> c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
              List<DeliverableUser> newdeliverableUserList = new ArrayList<DeliverableUser>();
              for (DeliverableUser deliverableUser : deliverableUserList) {
                newdeliverableUserList.add(deliverableUser);
              }
              publication.setAuthorlist(newdeliverableUserList);
              fullPublicationsList.add(publication);
            }
          }
        }
        if (fullPublicationsList.size() > 0) {

          deliverablesListDTO = fullPublicationsList.stream()
            .map(publication -> this.publicationsMapper.publicationToPublicationDTO(publication))
            .collect(Collectors.toList());

        } else {
          this.fieldErrors.add(new FieldErrorDTO("createDeliverable", "Deliverable", "No data found"));
          throw new MARLOFieldValidationException("Field Validation errors", "",
            this.fieldErrors.stream()
              .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
              .collect(Collectors.toList()));
        }


      } catch (Exception e) {
        this.fieldErrors.add(new FieldErrorDTO("createDeliverable", "Deliverable", "ERROR Creating info"));
        throw new MARLOFieldValidationException("Field Validation errors", "",
          this.fieldErrors.stream()
            .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList()));
      }

    } else {
      if (!this.fieldErrors.isEmpty()) {
        throw new MARLOFieldValidationException("Field Validation errors", "",
          this.fieldErrors.stream()
            .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList()));
      }
    }
    return deliverablesListDTO;
  }

}
