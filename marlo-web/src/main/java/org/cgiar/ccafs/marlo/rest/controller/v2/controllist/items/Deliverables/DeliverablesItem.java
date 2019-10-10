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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import com.ibm.icu.util.Calendar;

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
        deliverableDissemination.setPhase(phase);
        deliverableDisseminationManager.saveDeliverableDissemination(deliverableDissemination);

        // creatte deliverable publication metadata
        DeliverablePublicationMetadata deliverablePublicationMetadata = new DeliverablePublicationMetadata();
        deliverablePublicationMetadata.setDeliverable(deliverable);
        deliverablePublicationMetadata.setIssue(deliverableDTO.getIssue());
        deliverablePublicationMetadata.setJournal(deliverableDTO.getJournal());
        deliverablePublicationMetadata.setPages(deliverableDTO.getNpages());
        deliverablePublicationMetadata.setVolume(deliverableDTO.getVolume());
        deliverablePublicationMetadata.setIsiPublication(deliverableDTO.isISIJournal());

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
          deliverableMetadataElementHandle.setElementValue(deliverableDTO.getHandle());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementHandle);

          // deliverable metadataelement DOI
          DeliverableMetadataElement deliverableMetadataElementDoi = new DeliverableMetadataElement();
          deliverableMetadataElementDoi.setDeliverable(deliverable);
          deliverableMetadataElementDoi.setPhase(phase);
          MetadataElement metadataElementDoi =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTDOI))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementDoi.setMetadataElement(metadataElementDoi);
          deliverableMetadataElementDoi.setElementValue(deliverableDTO.getDoi());
          deliverableMetadataElementManager.saveDeliverableMetadataElement(deliverableMetadataElementDoi);

          // deliverable metadataelement Title
          DeliverableMetadataElement deliverableMetadataElementTitle = new DeliverableMetadataElement();
          deliverableMetadataElementTitle.setDeliverable(deliverable);
          deliverableMetadataElementTitle.setPhase(phase);
          MetadataElement metadataElementTitle =
            metadataElements.stream().filter(me -> me.getEcondedName().equals(APConstants.METADATAELEMENTTITLE))
              .collect(Collectors.toList()).get(0);
          deliverableMetadataElementTitle.setMetadataElement(metadataElementTitle);
          deliverableMetadataElementTitle.setElementValue(deliverableDTO.getTitle());
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


          deliverableMetadataElementCitation.setElementValue(authors + "," + deliverableDTO.getYear() + ","
            + deliverableDTO.getTitle() + "," + deliverableDTO.getJournal() + "," + deliverableDTO.getVolume() + ","
            + deliverableDTO.getIssue() + "," + deliverableDTO.getNpages());
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

  public List<PublicationDTO> getAllDeliverables(String entityAcronym, int repoyear, String repoPhase, User user) {
    List<PublicationDTO> deliverablesListDTO = new ArrayList<PublicationDTO>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createDeliverable", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == repoyear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createDeliverable", "phase", repoyear + " is an invalid year"));
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
          fieldErrors.add(new FieldErrorDTO("createDeliverable", "Deliverable", "No data found"));
          throw new MARLOFieldValidationException("Field Validation errors", "",
            fieldErrors.stream()
              .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
              .collect(Collectors.toList()));
        }


      } catch (Exception e) {
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

}
