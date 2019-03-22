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

import org.cgiar.ccafs.marlo.data.manager.StudyTypeManager;
import org.cgiar.ccafs.marlo.data.model.StudyType;
import org.cgiar.ccafs.marlo.rest.dto.StudyTypeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.StudyTypeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class StudyTypeItem<T> {

  private StudyTypeManager studyTypeManager;
  private StudyTypeMapper studyTypeMapper;

  @Inject
  public StudyTypeItem(StudyTypeManager studyTypeManager, StudyTypeMapper studyTypeMapper) {
    this.studyTypeManager = studyTypeManager;
    this.studyTypeMapper = studyTypeMapper;
  }

  /**
   * Find a Study Type Item MARLO id
   * 
   * @param id
   * @return a StudyTypeDTO with the Study Type Item
   */
  public ResponseEntity<StudyTypeDTO> findStudyTypeById(Long id) {
    StudyType studyType = this.studyTypeManager.getStudyTypeById(id);

    return Optional.ofNullable(studyType).map(this.studyTypeMapper::studyTipeToStudyTypeDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Study Types Items *
   * 
   * @return a List of Study type with all Study Types items Items.
   */
  public List<StudyTypeDTO> getAllStudyTypes() {
    if (this.studyTypeManager.findAll() != null) {
      List<StudyType> studyTypes = new ArrayList<>(this.studyTypeManager.findAll());

      List<StudyTypeDTO> organizationTypeDTOs =
        studyTypes.stream().map(studyTypeEntity -> this.studyTypeMapper.studyTipeToStudyTypeDTO(studyTypeEntity))
          .collect(Collectors.toList());
      return organizationTypeDTOs;
    } else {
      return null;
    }
  }

}
