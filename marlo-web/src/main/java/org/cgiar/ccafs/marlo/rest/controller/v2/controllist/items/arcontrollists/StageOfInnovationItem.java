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

import org.cgiar.ccafs.marlo.data.manager.RepIndStageInnovationManager;
import org.cgiar.ccafs.marlo.data.model.RepIndStageInnovation;
import org.cgiar.ccafs.marlo.rest.dto.StageOfInnovationDTO;
import org.cgiar.ccafs.marlo.rest.mappers.StageOfInnovationMapper;

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
public class StageOfInnovationItem<T> {

  private RepIndStageInnovationManager repIndStageInnovationManager;
  private StageOfInnovationMapper stageOfInnovationMapper;

  @Inject
  public StageOfInnovationItem(RepIndStageInnovationManager repIndStageInnovationManager,
    StageOfInnovationMapper stageOfInnovationMapper) {
    super();
    this.repIndStageInnovationManager = repIndStageInnovationManager;
    this.stageOfInnovationMapper = stageOfInnovationMapper;
  }


  /**
   * Find a Stage of Innovation requesting by id
   * 
   * @param id
   * @return a StageOfInnovationDTO with the Stage of Innovation data.
   */
  public ResponseEntity<StageOfInnovationDTO> findStageOfInnovationById(Long id) {
    RepIndStageInnovation repIndStageInnovation = repIndStageInnovationManager.getRepIndStageInnovationById(id);
    return Optional.ofNullable(repIndStageInnovation)
      .map(stageOfInnovationMapper::repIndStageInnovationToStageOfInnovationDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Stage of Innovation Items *
   * 
   * @return a List of StageOfInnovationDTO with all RepIndStageInnovation Items.
   */
  public List<StageOfInnovationDTO> getAllStageOfInnovations() {
    if (repIndStageInnovationManager.findAll() != null) {
      List<RepIndStageInnovation> repIndStageInnovations = new ArrayList<>(repIndStageInnovationManager.findAll());
      List<StageOfInnovationDTO> stageOfInnovationDTOs =
        repIndStageInnovations.stream().map(repIndStageInnovationEntity -> stageOfInnovationMapper
          .repIndStageInnovationToStageOfInnovationDTO(repIndStageInnovationEntity)).collect(Collectors.toList());
      return stageOfInnovationDTOs;
    } else {
      return null;
    }
  }


}
