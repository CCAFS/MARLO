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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists;

import org.cgiar.ccafs.marlo.data.manager.EvidenceTagManager;
import org.cgiar.ccafs.marlo.data.model.EvidenceTag;
import org.cgiar.ccafs.marlo.rest.dto.TagDTO;
import org.cgiar.ccafs.marlo.rest.mappers.TagMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

@Named
public class TagItem<T> {

  private EvidenceTagManager evidenceTagManager;
  private TagMapper tagMapper;

  @Inject
  public TagItem(EvidenceTagManager evidenceTagManager, TagMapper tagMapper) {
    super();
    this.evidenceTagManager = evidenceTagManager;
    this.tagMapper = tagMapper;
  }

  /**
   * Find a Tag requesting by id
   * 
   * @param id
   * @return a TagDTO with the tag data.
   */
  public ResponseEntity<TagDTO> findTagById(Long id) {
    EvidenceTag evidenceTag = this.evidenceTagManager.getEvidenceTagById(id);
    return Optional.ofNullable(evidenceTag).map(this.tagMapper::evidenceTagToTagDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All Tags Items *
   * 
   * @return a List of TagDTO with all TAGS Items.
   */
  public List<TagDTO> getAllTags() {
    if (this.evidenceTagManager.findAll() != null) {
      List<EvidenceTag> evidenceTags = new ArrayList<>(this.evidenceTagManager.findAll());
      List<TagDTO> tagDTOs = evidenceTags.stream()
        .map(evidenceTagsEntity -> this.tagMapper.evidenceTagToTagDTO(evidenceTagsEntity)).collect(Collectors.toList());
      return tagDTOs;
    } else {
      return null;
    }
  }

}
