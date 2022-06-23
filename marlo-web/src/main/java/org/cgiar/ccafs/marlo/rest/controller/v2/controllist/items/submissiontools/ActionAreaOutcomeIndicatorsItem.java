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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools;

import org.cgiar.ccafs.marlo.data.manager.ActionAreaOutcomeIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.ActionAreaOutcomeIndicator;
import org.cgiar.ccafs.marlo.rest.dto.ActionAreaOutcomeIndicatorDTO;
import org.cgiar.ccafs.marlo.rest.mappers.ActionAreaOutcomesMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ActionAreaOutcomeIndicatorsItem<T> {

  private ActionAreaOutcomeIndicatorManager actionAreaOutcomeIndicatorManager;

  private ActionAreaOutcomesMapper actionAreaOutcomesMapper;

  @Inject
  public ActionAreaOutcomeIndicatorsItem(ActionAreaOutcomeIndicatorManager actionAreaOutcomeIndicatorManager,
    ActionAreaOutcomesMapper actionAreaOutcomesMapper) {
    super();
    this.actionAreaOutcomeIndicatorManager = actionAreaOutcomeIndicatorManager;
    this.actionAreaOutcomesMapper = actionAreaOutcomesMapper;
  }

  public ResponseEntity<ActionAreaOutcomeIndicatorDTO> findActionAreaOutcomeIndicatorById(Long id) {
    ActionAreaOutcomeIndicator actionAreaOutcomeIndicator =
      this.actionAreaOutcomeIndicatorManager.getActionAreaOutcomeIndicatorById(id);

    if (!actionAreaOutcomeIndicator.isActive()) {
      actionAreaOutcomeIndicator = null;
    }

    return Optional.ofNullable(actionAreaOutcomeIndicator)
      .map(this.actionAreaOutcomesMapper::actionAreaOutcomeIndicatorToActionAreaOutcomeIndicatorDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }


  public List<ActionAreaOutcomeIndicatorDTO> getAllActionAreaOutcomeIndicators() {
    if (this.actionAreaOutcomeIndicatorManager.getAll() != null) {
      List<ActionAreaOutcomeIndicator> actionAreaOutcomeIndicators =
        new ArrayList<>(this.actionAreaOutcomeIndicatorManager.getAll());
      List<ActionAreaOutcomeIndicatorDTO> actionAreaOutcomeIndicatorsDTOs =
        actionAreaOutcomeIndicators.stream().filter(aaoi -> aaoi != null && aaoi.getId() != null && aaoi.isActive())
          .map(actionAreaOutcomeIndicatorEntity -> this.actionAreaOutcomesMapper
            .actionAreaOutcomeIndicatorToActionAreaOutcomeIndicatorDTO(actionAreaOutcomeIndicatorEntity))
          .collect(Collectors.toList());
      return actionAreaOutcomeIndicatorsDTOs;
    } else {
      return null;
    }
  }
}
