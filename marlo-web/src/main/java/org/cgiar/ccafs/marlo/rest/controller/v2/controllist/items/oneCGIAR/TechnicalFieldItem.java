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

import org.cgiar.ccafs.marlo.data.manager.OneCGIARTechnicalFieldManager;
import org.cgiar.ccafs.marlo.data.model.OneCGIARTechnicalField;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.TechnicalFieldDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.TechnicalFieldMapper;

import java.util.ArrayList;
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
public class TechnicalFieldItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(TechnicalFieldItem.class);

  // Mappers
  private TechnicalFieldMapper technicalFieldMapper;

  // managers
  private OneCGIARTechnicalFieldManager oneCGIARTechnicalFieldManager;

  @Inject
  public TechnicalFieldItem(OneCGIARTechnicalFieldManager oneCGIARTechnicalFieldManager,
    TechnicalFieldMapper technicalFieldMapper) {
    super();
    this.oneCGIARTechnicalFieldManager = oneCGIARTechnicalFieldManager;
    this.technicalFieldMapper = technicalFieldMapper;
  }

  public ResponseEntity<List<TechnicalFieldDTO>> getAll() {
    List<OneCGIARTechnicalField> oneCGIARTechnicalFields = this.oneCGIARTechnicalFieldManager.getAll();

    List<TechnicalFieldDTO> technicalFieldDTOs = CollectionUtils.emptyIfNull(oneCGIARTechnicalFields).stream()
      .map(this.technicalFieldMapper::oneCGIARTechnicalFieldToTechnicalFieldDTO).collect(Collectors.toList());

    return Optional.ofNullable(technicalFieldDTOs).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<TechnicalFieldDTO> getTechnicalFieldById(Long id, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    OneCGIARTechnicalField oneCGIARTechnicalField = null;
    if (id == null || id < 1L) {
      fieldErrors.add(new FieldErrorDTO("OneCGIARTechnicalField", "id", "Invalid ID for an Technical Field"));
    }
    // User validation???

    if (fieldErrors.isEmpty()) {
      oneCGIARTechnicalField = this.oneCGIARTechnicalFieldManager.getOneCGIARTechnicalFieldById(id);
      if (oneCGIARTechnicalField == null) {
        fieldErrors.add(
          new FieldErrorDTO("OneCGIARTechnicalField", "id", "The Technical field with id " + id + " does not exist"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }

    return Optional.ofNullable(oneCGIARTechnicalField)
      .map(this.technicalFieldMapper::oneCGIARTechnicalFieldToTechnicalFieldDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
