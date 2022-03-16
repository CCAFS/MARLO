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

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitTypeManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityTypeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.GlobalUnitTypeMapper;

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
public class GlobalUnitTypeItem<T> {

  private GlobalUnitTypeManager globalUnitTypeManager;
  private GlobalUnitTypeMapper globalUnitTypeMapper;

  @Inject
  public GlobalUnitTypeItem(GlobalUnitTypeMapper globalUnitTypeMapper, GlobalUnitTypeManager globalUnitTypeManager) {
    super();
    this.globalUnitTypeManager = globalUnitTypeManager;
    this.globalUnitTypeMapper = globalUnitTypeMapper;
  }

  /**
   * Get a Geographic Scope by id *
   * 
   * @return GeographicScopeDTO founded.
   */
  public ResponseEntity<CGIAREntityTypeDTO> findGlobalUnitTypeById(Long id) {
    GlobalUnitType globalUnitType = this.globalUnitTypeManager.getGlobalUnitTypeById(id);
    return Optional.ofNullable(globalUnitType).filter(c -> c.isVisible())
      .map(this.globalUnitTypeMapper::globalUnitTypeToCGIAREntityTypeDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Global Unit types*
   * 
   * @return a List of CGIAREntityTypeDTO with All the Global Unit types
   *         Items.
   */
  public List<CGIAREntityTypeDTO> getAllGlobalUnitTypes() {
    if (this.globalUnitTypeManager.findAll() != null) {
      List<GlobalUnitType> globalUnitTypes = new ArrayList<>(
        this.globalUnitTypeManager.findAll().stream().filter(c -> c.isVisible()).collect(Collectors.toList()));
      // FIXME: Should change the way to compare which CRP/PTF/Center will
      // show on API
      List<CGIAREntityTypeDTO> cgiarEntityTypeList = globalUnitTypes.stream()
        .map(globalUnitTypeEntity -> this.globalUnitTypeMapper.globalUnitTypeToCGIAREntityTypeDTO(globalUnitTypeEntity))
        .collect(Collectors.toList());
      return cgiarEntityTypeList;
    } else {
      return null;
    }
  }

}
