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

import org.cgiar.ccafs.marlo.data.manager.RepIndInnovationTypeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndInnovationType;
import org.cgiar.ccafs.marlo.rest.dto.InnovationTypeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.InnovationTypeMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class TypeOfInnovationItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(AccountsItem.class);

  // manager
  RepIndInnovationTypeManager repIndInnovationTypeManager;

  // mappers
  InnovationTypeMapper innovationTypeMapper;

  @Inject
  public TypeOfInnovationItem(RepIndInnovationTypeManager repIndInnovationTypeManager,
    InnovationTypeMapper innovationTypeMapper) {
    super();
    this.repIndInnovationTypeManager = repIndInnovationTypeManager;
    this.innovationTypeMapper = innovationTypeMapper;
  }

  public ResponseEntity<List<InnovationTypeDTO>> getAll() {
    List<RepIndInnovationType> repIndInnovationTypes = this.repIndInnovationTypeManager.oneCGIARFindAll();

    List<InnovationTypeDTO> innovationTypeDTOs = CollectionUtils.emptyIfNull(repIndInnovationTypes).stream()
      .map(this.innovationTypeMapper::repIndInnovationTypeToInnovationTypesDTO).collect(Collectors.toList());

    return Optional.ofNullable(innovationTypeDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }


}
