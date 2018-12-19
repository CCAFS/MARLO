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

import org.cgiar.ccafs.marlo.data.manager.RepIndDegreeInnovationManager;
import org.cgiar.ccafs.marlo.data.model.RepIndDegreeInnovation;
import org.cgiar.ccafs.marlo.rest.dto.DegreeOfInnovationDTO;
import org.cgiar.ccafs.marlo.rest.mappers.DegreeOfInnovationMapper;

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
public class DegreeOfInnovationItem<T> {

  private RepIndDegreeInnovationManager repIndDegreeInnovationManager;
  private DegreeOfInnovationMapper degreeOfInnovationMapper;

  @Inject
  public DegreeOfInnovationItem(RepIndDegreeInnovationManager repIndDegreeInnovationManager,
    DegreeOfInnovationMapper degreeOfInnovationMapper) {
    super();
    this.repIndDegreeInnovationManager = repIndDegreeInnovationManager;
    this.degreeOfInnovationMapper = degreeOfInnovationMapper;
  }

  /**
   * Find a Degree of Innovation requesting by id
   * 
   * @param id
   * @return a DegreeOfInnovationDTO with the Degree of Innovation data.
   */
  public ResponseEntity<DegreeOfInnovationDTO> findDegreeOfInnovationById(Long id) {
    RepIndDegreeInnovation repIndDegreeInnovation = repIndDegreeInnovationManager.getRepIndDegreeInnovationById(id);
    return Optional.ofNullable(repIndDegreeInnovation)
      .map(degreeOfInnovationMapper::repIndDegreeInnovationToDegreeOfInnovationDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Degree of Innovation Items *
   * 
   * @return a List of DegreeOfInnovationDTO with all RepIndDegreeInnovation Items.
   */
  public List<DegreeOfInnovationDTO> getAllDegreeOfInnovations() {
    if (repIndDegreeInnovationManager.findAll() != null) {
      List<RepIndDegreeInnovation> repIndDegreeInnovations = new ArrayList<>(repIndDegreeInnovationManager.findAll());
      List<DegreeOfInnovationDTO> degreeOfInnovationDTOs =
        repIndDegreeInnovations.stream().map(repIndDegreeInnovationEntity -> degreeOfInnovationMapper
          .repIndDegreeInnovationToDegreeOfInnovationDTO(repIndDegreeInnovationEntity)).collect(Collectors.toList());
      return degreeOfInnovationDTOs;
    } else {
      return null;
    }
  }

}
