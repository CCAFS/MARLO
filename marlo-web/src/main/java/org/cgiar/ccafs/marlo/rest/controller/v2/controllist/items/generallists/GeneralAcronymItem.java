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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists;

import org.cgiar.ccafs.marlo.data.manager.GeneralAcronymManager;
import org.cgiar.ccafs.marlo.data.model.GeneralAcronym;
import org.cgiar.ccafs.marlo.rest.dto.GeneralAcronymDTO;
import org.cgiar.ccafs.marlo.rest.mappers.GeneralAcronymMapper;

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
public class GeneralAcronymItem<T> {

  private GeneralAcronymManager generalAcronymManager;
  private GeneralAcronymMapper generalAcronymMapper;

  @Inject
  public GeneralAcronymItem(GeneralAcronymMapper generalAcronymMapper, GeneralAcronymManager generalAcronymManager) {
    super();
    this.generalAcronymManager = generalAcronymManager;
    this.generalAcronymMapper = generalAcronymMapper;
  }

  /**
   * Find Acronyms by acronym
   * 
   * @return a List of GeneralAcronymDTO with the General Acronyms Items founded
   */
  public ResponseEntity<List<GeneralAcronymDTO>> findGeneralAcronymByAcronym(String acronym) {
    if (this.generalAcronymManager.getGeneralAcronymByAcronym(acronym) != null) {
      List<GeneralAcronym> generalAcronyms = new ArrayList<>(
        this.generalAcronymManager.getGeneralAcronymByAcronym(acronym).stream().collect(Collectors.toList()));
      List<GeneralAcronymDTO> generalAcronymDTOList = generalAcronyms.stream()
        .map(generalAcronymEntity -> this.generalAcronymMapper.generalAcronymToGeneralAcronymDTO(generalAcronymEntity))
        .collect(Collectors.toList());
      return new ResponseEntity<List<GeneralAcronymDTO>>(generalAcronymDTOList, HttpStatus.OK);
    } else {
      return new ResponseEntity<List<GeneralAcronymDTO>>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Get an Acronym by id *
   * 
   * @return GeneralAcronymDTO founded.
   */
  public ResponseEntity<GeneralAcronymDTO> findGeneralAcronymById(Long id) {
    GeneralAcronym generalAcronym = this.generalAcronymManager.getGeneralAcronymById(id);
    return Optional.ofNullable(generalAcronym).map(this.generalAcronymMapper::generalAcronymToGeneralAcronymDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All Acronyms*
   * 
   * @return a List of GeneralAcronymDTO with All the General Acronyms Items.
   */
  public List<GeneralAcronymDTO> getAllGeneralAcronyms() {
    if (this.generalAcronymManager.findAll() != null) {
      List<GeneralAcronym> generalAcronyms =
        new ArrayList<>(this.generalAcronymManager.findAll().stream().collect(Collectors.toList()));
      List<GeneralAcronymDTO> generalAcronymDTOList = generalAcronyms.stream()
        .map(generalAcronymEntity -> this.generalAcronymMapper.generalAcronymToGeneralAcronymDTO(generalAcronymEntity))
        .collect(Collectors.toList());
      return generalAcronymDTOList;
    } else {
      return null;
    }
  }

}
