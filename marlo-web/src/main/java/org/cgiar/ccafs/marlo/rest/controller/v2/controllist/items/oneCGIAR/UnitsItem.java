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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR;

import org.cgiar.ccafs.marlo.data.manager.OneCGIARUnitManager;
import org.cgiar.ccafs.marlo.data.model.OneCGIARUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.UnitDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.OneCGIARUnitMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Named
public class UnitsItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(UnitsItem.class);

  // Managers
  private OneCGIARUnitManager unitManager;

  // Mappers
  private OneCGIARUnitMapper unitMapper;

  @Inject
  public UnitsItem(OneCGIARUnitManager unitManager, OneCGIARUnitMapper unitMapper) {
    super();
    this.unitManager = unitManager;
    this.unitMapper = unitMapper;
  }

  public ResponseEntity<List<UnitDTO>> getAll() {
    List<OneCGIARUnit> oneCGIARUnits = this.unitManager.getAll();

    List<UnitDTO> unitDTOs = CollectionUtils.emptyIfNull(oneCGIARUnits).stream()
      .map(this.unitMapper::oneCGIARUnitToUnitDTO).collect(Collectors.toList());

    return Optional.ofNullable(unitDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<UnitDTO> getUnitByFinancialCode(String financialCode, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    OneCGIARUnit oneCGIARUnit = null;
    if (StringUtils.isBlank(financialCode)) {
      fieldErrors.add(new FieldErrorDTO("Units", "financialCode", "Invalid Financial Code for an unit"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      oneCGIARUnit = this.unitManager.getUnitByFinancialCode(financialCode);
      if (oneCGIARUnit == null) {
        fieldErrors.add(new FieldErrorDTO("Units", "financialCode",
          "The unit with financialCode " + financialCode + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(oneCGIARUnit).map(this.unitMapper::oneCGIARUnitToUnitDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<UnitDTO> getUnitById(Long id, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    OneCGIARUnit oneCGIARUnit = null;
    if (id == null || id < 1L) {
      fieldErrors.add(new FieldErrorDTO("Units", "id", "Invalid ID for an unit"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      oneCGIARUnit = this.unitManager.getUnitById(id);
      if (oneCGIARUnit == null) {
        fieldErrors.add(new FieldErrorDTO("Units", "id", "The unit with id " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(oneCGIARUnit).map(this.unitMapper::oneCGIARUnitToUnitDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<UnitDTO>> getUnitByParent(Long parentId, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    List<UnitDTO> unitDTOs = null;
    if (parentId == null || parentId < 1L) {
      fieldErrors.add(new FieldErrorDTO("Units", "parentId", "Invalid Parent Unit ID for an unit"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      List<OneCGIARUnit> oneCGIARUnits = this.unitManager.getUnitsByParent(parentId);
      unitDTOs = CollectionUtils.emptyIfNull(oneCGIARUnits).stream().map(this.unitMapper::oneCGIARUnitToUnitDTO)
        .collect(Collectors.toList());
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(unitDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<List<UnitDTO>> getUnitsByScienceGroup(Long scienceGroupId, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    List<UnitDTO> unitDTOs = null;
    if (scienceGroupId == null || scienceGroupId < 1L) {
      fieldErrors.add(new FieldErrorDTO("Units", "scienceGroupId", "Invalid Unit Type ID for an unit"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      List<OneCGIARUnit> oneCGIARUnits = this.unitManager.getUnitsByScienceGroup(scienceGroupId);
      unitDTOs = CollectionUtils.emptyIfNull(oneCGIARUnits).stream().map(this.unitMapper::oneCGIARUnitToUnitDTO)
        .collect(Collectors.toList());
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(unitDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
