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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityDTO;
import org.cgiar.ccafs.marlo.rest.mappers.GlobalUnitMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class GlobalUnitItem<T> {

  private GlobalUnitManager globalUnitManager;
  private GlobalUnitMapper globalUnitMapper;

  @Inject
  public GlobalUnitItem(GlobalUnitManager globalUnitManager, GlobalUnitMapper globalUnitMapper) {
    this.globalUnitManager = globalUnitManager;
    this.globalUnitMapper = globalUnitMapper;
  }

  /**
   * Find a Global Unit by Acronym
   * 
   * @param CRP Acronym
   * @return a GlobalUnitDTO with the smoCode
   */
  public ResponseEntity<CGIAREntityDTO> findGlobalUnitByAcronym(String acronym) {
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(acronym);

    return Optional.ofNullable(globalUnitEntity)
      .filter(c -> c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CRP
        || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CGIAR_CENTER_TYPE
        || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_PLATFORM)
      .map(this.globalUnitMapper::globalUnitToGlobalUnitDTO).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Find a Global Unit by CGIAR code
   * 
   * @param CGIAR id
   * @return a GlobalUnitDTO with the smoCode
   */
  public ResponseEntity<CGIAREntityDTO> findGlobalUnitByCGIRARId(String smoCode) {
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitBySMOCode(smoCode);
    return Optional.ofNullable(globalUnitEntity)
      .filter(c -> c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CRP
        || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CGIAR_CENTER_TYPE
        || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_PLATFORM
        || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_APPLICATION)
      .map(this.globalUnitMapper::globalUnitToGlobalUnitDTO)
      // FIXME: Should change the way to compare which CRP/PTF/Center
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Global units
   * 
   * @return a List of global units
   */
  public ResponseEntity<List<CGIAREntityDTO>> getAllCGIAREntities() {

    List<GlobalUnit> globalUnits;
    if (this.globalUnitManager.findAll() != null) {

      globalUnits = this.globalUnitManager.findAll().stream()
        .filter(c -> c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CRP
          || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CGIAR_CENTER_TYPE
          || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_PLATFORM
          || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_INITIATIVES
          || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_OFFICES)
        .sorted(Comparator.comparing(GlobalUnit::getSmoCode, Comparator.nullsLast(Comparator.naturalOrder())))
        .collect(Collectors.toList());;

      List<CGIAREntityDTO> globalUnitDTOs =
        globalUnits.stream().map(globalUnitEntity -> this.globalUnitMapper.globalUnitToGlobalUnitDTO(globalUnitEntity))
          .collect(Collectors.toList());
      if (globalUnitDTOs == null || globalUnitDTOs.size() == 0) {
        return new ResponseEntity<List<CGIAREntityDTO>>(HttpStatus.NOT_FOUND);
      } else {
        return new ResponseEntity<List<CGIAREntityDTO>>(globalUnitDTOs, HttpStatus.OK);
      }

    } else {
      return new ResponseEntity<List<CGIAREntityDTO>>(HttpStatus.NOT_FOUND);
    }
  }

  /**
   * Get All the Global units
   * 
   * @return a List of global units
   */
  public ResponseEntity<List<CGIAREntityDTO>> getAllGlobaUnits(Long typeId) {

    List<GlobalUnit> globalUnits;
    if (this.globalUnitManager.findAll() != null) {
      if (typeId != null) {
        globalUnits = new ArrayList<>(
          this.globalUnitManager.findAll().stream().filter(c -> c.isActive() && c.getGlobalUnitType().getId() == typeId)
            .filter(c -> c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CRP
              || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CGIAR_CENTER_TYPE
              || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_PLATFORM
              || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_INITIATIVES
              || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_OFFICES)
            .sorted(Comparator.comparing(GlobalUnit::getSmoCode, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList()));
      } else {
        globalUnits = this.globalUnitManager.findAll().stream()
          .filter(c -> c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CRP
            || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_CGIAR_CENTER_TYPE
            || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_PLATFORM
            || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_INITIATIVES
            || c.getGlobalUnitType().getId() == APConstants.GLOBAL_UNIT_OFFICES)
          .sorted(Comparator.comparing(GlobalUnit::getSmoCode, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList());;
      }
      List<CGIAREntityDTO> globalUnitDTOs =
        globalUnits.stream().map(globalUnitEntity -> this.globalUnitMapper.globalUnitToGlobalUnitDTO(globalUnitEntity))
          .collect(Collectors.toList());
      if (globalUnitDTOs == null || globalUnitDTOs.size() == 0) {
        return new ResponseEntity<List<CGIAREntityDTO>>(HttpStatus.NOT_FOUND);
      } else {
        return new ResponseEntity<List<CGIAREntityDTO>>(globalUnitDTOs, HttpStatus.OK);
      }

    } else {
      return new ResponseEntity<List<CGIAREntityDTO>>(HttpStatus.NOT_FOUND);
    }
  }
}
