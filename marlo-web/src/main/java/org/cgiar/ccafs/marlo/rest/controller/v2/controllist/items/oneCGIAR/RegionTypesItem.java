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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR;

import org.cgiar.ccafs.marlo.data.manager.RegionTypesManager;
import org.cgiar.ccafs.marlo.data.model.RegionType;
import org.cgiar.ccafs.marlo.rest.dto.OneCGIARRegionTypeDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.RegionTypeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.catalina.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class RegionTypesItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(RegionType.class);
  @Autowired
  private Environment env;
  private RegionTypesManager regionTypesManager;
  private RegionTypeMapper regionTypeMapper;

  @Inject
  public RegionTypesItem(RegionTypesManager regionTypesManager, RegionTypeMapper regionTypeMapper) {
    super();
    this.regionTypesManager = regionTypesManager;
    this.regionTypeMapper = regionTypeMapper;
  }

  public ResponseEntity<List<OneCGIARRegionTypeDTO>> findAll() {

    List<RegionType> regionTypeList = regionTypesManager.findAll();
    List<OneCGIARRegionTypeDTO> regionTypeDTO =
      regionTypeList.stream().map(regionTypes -> this.regionTypeMapper.regionTypeToOneCGIARRegionTypeDTO(regionTypes))
        .collect(Collectors.toList());
    return Optional.ofNullable(regionTypeDTO).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<OneCGIARRegionTypeDTO> getRegionTypeDTO(Long id, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    RegionType regionType = regionTypesManager.find(id);
    if (regionType == null) {
      fieldErrors.add(new FieldErrorDTO("Region Types", "getRegionType", "This region type not exist"));
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }
    return Optional.ofNullable(regionType).map(this.regionTypeMapper::regionTypeToOneCGIARRegionTypeDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }


}
