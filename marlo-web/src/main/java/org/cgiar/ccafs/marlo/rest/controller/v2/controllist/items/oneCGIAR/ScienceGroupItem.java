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

import org.cgiar.ccafs.marlo.data.manager.OneCGIARScienceGroupManager;
import org.cgiar.ccafs.marlo.data.model.OneCGIARScienceGroup;
import org.cgiar.ccafs.marlo.data.model.Region;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.ScienceGroupDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.OneCGIARScienceGorupMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ScienceGroupItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(Region.class);
  @Autowired
  private Environment env;
  private OneCGIARScienceGroupManager oneCGIARScienceGroupManager;
  private OneCGIARScienceGorupMapper oneCGIARScienceGorupMapper;

  @Inject
  public ScienceGroupItem(OneCGIARScienceGroupManager oneCGIARScienceGroupManager,
    OneCGIARScienceGorupMapper oneCGIARScienceGorupMapper) {
    super();
    this.oneCGIARScienceGroupManager = oneCGIARScienceGroupManager;
    this.oneCGIARScienceGorupMapper = oneCGIARScienceGorupMapper;
  }

  public ResponseEntity<List<ScienceGroupDTO>> getAll() {
    List<OneCGIARScienceGroup> oneCGIARScienceGroupList =
      oneCGIARScienceGroupManager.getAll().stream().collect(Collectors.toList());
    // FIXME: SOLUCIÓN TEMPORAL
    List<ScienceGroupDTO> oneCGIARScienceGroupDTO = oneCGIARScienceGroupList.stream()
      .map(ScienceGroup -> this.oneCGIARScienceGorupMapper.oneCGIARScienceGroupToScienceGroupDTO(ScienceGroup))
      .collect(Collectors.toList());

    return Optional.ofNullable(oneCGIARScienceGroupDTO).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<ScienceGroupDTO> getScienceGroup(Long id, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    OneCGIARScienceGroup oneCGIARScienceGroup = oneCGIARScienceGroupManager.getScienceGroupById(id);
    if (oneCGIARScienceGroup == null) {
      fieldErrors.add(new FieldErrorDTO("Regions", "getRegion", "This region not exist"));
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }
    return Optional.ofNullable(oneCGIARScienceGroup)
      .map(this.oneCGIARScienceGorupMapper::oneCGIARScienceGroupToScienceGroupDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }


}
