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

package org.cgiar.ccafs.marlo.rest.mappers;

import org.cgiar.ccafs.marlo.data.model.Publication;
import org.cgiar.ccafs.marlo.rest.dto.PublicationDTO;

import org.mapstruct.Mapper;

@Mapper(componentModel = "jsr330", uses = {PhaseMapper.class, DeliverableUserMapper.class})
public abstract interface PublicationsMapper {

  /*
   * public default Publication deliverableToPublication(Deliverable deliverable, Phase phase,
   * Map<String, DeliverableMetadataElement> metadataElements) {
   * Publication publication = null;
   * if (deliverable != null) {
   * //DeliverableMetadataElement deliverableMetadataElement = null;
   * publication = new Publication();
   * // DeliverableInfo deliverableInfo = deliverable.getDeliverableInfo(phase);
   * //DeliverablePublicationMetadata deliverablePublicationMetadata = deliverable.getPublication(phase);
   * // DeliverableDissemination deliverableDissemination = deliverable.getDissemination(phase);
   * publication.setAuthorlist(deliverable.getUsers(phase));
   * //deliverableMetadataElement = metadataElements.get(APConstants.METADATAELEMENTAUTHORS);
   * //String authors = deliverableMetadataElement != null ? deliverableMetadataElement.getElementValue() : null;
   * //publication // why is this validation necessary?
   * // .setAuthors(authors != null && !authors.contains("[object Object]") ? authors : null);
   * //deliverableMetadataElement = metadataElements.get(APConstants.METADATAELEMENTDOI);
   * //publication.setDoi(deliverableMetadataElement != null ? deliverableMetadataElement.getElementValue() : null);
   * //deliverableMetadataElement = metadataElements.get(APConstants.METADATAELEMENTHANDLE);
   * //publication.setHandle(deliverableMetadataElement != null ? deliverableMetadataElement.getElementValue() : null);
   * // publication.setId(deliverable.getId());
   * // publication.setISIJournal(deliverablePublicationMetadata != null
   * // ? BooleanUtils.toBoolean(deliverablePublicationMetadata.getIsiPublication()) : false);
   * // publication.setIsOpenAccess(
   * // deliverableDissemination != null ? BooleanUtils.toBoolean(deliverableDissemination.getIsOpenAccess()) : false);
   * // publication.setIssue(deliverablePublicationMetadata != null ? deliverablePublicationMetadata.getIssue() :
   * // null);
   * // publication
   * // .setJournal(deliverablePublicationMetadata != null ? deliverablePublicationMetadata.getJournal() : null);
   * // publication.setNpages(deliverablePublicationMetadata != null ? deliverablePublicationMetadata.getPages() :
   * // null);
   * //publication.setPhase(phase);
   * //deliverableMetadataElement = metadataElements.get(APConstants.METADATAELEMENTTITLE);
   * //publication.setTitle(deliverableMetadataElement != null ? deliverableMetadataElement.getElementValue() : null);
   * // publication.setVolume(deliverablePublicationMetadata != null ? deliverablePublicationMetadata.getVolume() :
   * // null);
   * // publication.setYear(deliverableInfo != null ? deliverableInfo.getYear() : -1);
   * // publication.setArticleURL(deliverableDissemination != null ? deliverableDissemination.getArticleUrl() : null);
   * }
   * return publication;
   * }
   */

  public abstract PublicationDTO publicationToPublicationDTO(Publication publication);


}
