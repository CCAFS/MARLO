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

package org.cgiar.ccafs.marlo.rest.controller.controllist.items.powbar;

import org.cgiar.ccafs.marlo.data.manager.RepIndStageStudyManager;
import org.cgiar.ccafs.marlo.data.model.RepIndStageStudy;
import org.cgiar.ccafs.marlo.rest.dto.MaturityOfChangeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.MaturityOfChangeMapper;

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
public class MaturityOfChangeItem<T> {

  private RepIndStageStudyManager repIndStageStudyManager;
  private MaturityOfChangeMapper maturityOfChangeMapper;

  @Inject
  public MaturityOfChangeItem(RepIndStageStudyManager repIndStageStudyManager,
    MaturityOfChangeMapper maturityOfChangeMapper) {
    super();
    this.repIndStageStudyManager = repIndStageStudyManager;
    this.maturityOfChangeMapper = maturityOfChangeMapper;
  }


  /**
   * Find a Maturity of Change requesting by id
   * 
   * @param id
   * @return a MaturityOfChangeDTO with the Maturity of Change data.
   */
  public ResponseEntity<MaturityOfChangeDTO> findMaturityOfChangeById(Long id) {
    RepIndStageStudy repIndStageStudy = repIndStageStudyManager.getRepIndStageStudyById(id);
    return Optional.ofNullable(repIndStageStudy).map(maturityOfChangeMapper::repIndStageStudyToMaturityOfChangeDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Maturity of Change Items *
   * 
   * @return a List of MaturityOfChangeDTO with all RepIndStageStudy Items.
   */
  public List<MaturityOfChangeDTO> getAllMaturityOfChanges() {
    if (repIndStageStudyManager.findAll() != null) {
      List<RepIndStageStudy> repIndStageStudys = new ArrayList<>(repIndStageStudyManager.findAll());
      List<MaturityOfChangeDTO> maturityOfChangeDTOs = repIndStageStudys.stream().map(
        repIndStageStudyEntity -> maturityOfChangeMapper.repIndStageStudyToMaturityOfChangeDTO(repIndStageStudyEntity))
        .collect(Collectors.toList());
      return maturityOfChangeDTOs;
    } else {
      return null;
    }
  }


}
