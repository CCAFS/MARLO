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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.powbar;

import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationTypeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationType;
import org.cgiar.ccafs.marlo.rest.dto.InnovationTypeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.InnovationTypeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class InnovationTypeItem<T> {


  private RepIndInnovationTypeManager repIndInnovationTypeManager;
  private InnovationTypeMapper innovationTypesMapper;

  @Inject
  public InnovationTypeItem(RepIndInnovationTypeManager repIndInnovationTypeManager,
    InnovationTypeMapper innovationTypesMapper) {
    this.repIndInnovationTypeManager = repIndInnovationTypeManager;
    this.innovationTypesMapper = innovationTypesMapper;
  }

  /**
   * Find a Innovation Type requesting a MARLO id
   * 
   * @param id
   * @return a InnovationTypeDTO with the Innovation Type data.
   */
  public ResponseEntity<InnovationTypeDTO> findInnovationTypeById(Long id) {
    RepIndInnovationType repIndInnovationType = repIndInnovationTypeManager.getRepIndInnovationTypeById(id);
    return Optional.ofNullable(repIndInnovationType)
      .map(innovationTypesMapper::repIndInnovationTypeToInnovationTypesDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }


  /**
   * Get All the Innovation Types Items *
   * 
   * @return a List of InnovationTypesDTO with all repIndInnovationType Items.
   */
  public List<InnovationTypeDTO> getAllInnovationTypes() {
    if (repIndInnovationTypeManager.findAll() != null) {
      List<RepIndInnovationType> repIndInnovationTypes = new ArrayList<>(repIndInnovationTypeManager.findAll());
      List<InnovationTypeDTO> innovationTypesDTOs =
        repIndInnovationTypes.stream().map(repIndInnovationTypesEntity -> innovationTypesMapper
          .repIndInnovationTypeToInnovationTypesDTO(repIndInnovationTypesEntity)).collect(Collectors.toList());
      return innovationTypesDTOs;
    } else {
      return null;
    }
  }


}
