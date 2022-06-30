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
import org.cgiar.ccafs.marlo.data.manager.ActionAreaOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.ActionAreaOutcomeIndicator;
import org.cgiar.ccafs.marlo.rest.dto.ActionAreaOutcomeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.ActionAreaOutcomesMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

@Named
public class ActionAreaOutcomesItem<T> {

  private ActionAreaOutcomeManager actionAreaOutcomeManager;
  private ActionAreaOutcomeIndicatorManager actionAreaOutcomeIndicatorManager;

  private ActionAreaOutcomesMapper actionAreaOutcomesMapper;

  Comparator<ActionAreaOutcomeDTO> comparator =
    Comparator.comparing(ActionAreaOutcomeDTO::getActionAreaId).thenComparing(ActionAreaOutcomeDTO::getOutcomeId);

  @Inject
  public ActionAreaOutcomesItem(ActionAreaOutcomeManager actionAreaOutcomeManager,
    ActionAreaOutcomeIndicatorManager actionAreaOutcomeIndicatorManager,
    ActionAreaOutcomesMapper actionAreaOutcomesMapper) {
    super();
    this.actionAreaOutcomeIndicatorManager = actionAreaOutcomeIndicatorManager;
    this.actionAreaOutcomeManager = actionAreaOutcomeManager;
    this.actionAreaOutcomesMapper = actionAreaOutcomesMapper;
  }


  /*
   * public ResponseEntity<ActionAreaOutcomeDTO> findActionAreaOutcomeById(Long id) {
   * ActionAreaOutcome actionAreaOutcome = this.actionAreaOutcomeManager.getActionAreaOutcomeById(id);
   * if (!actionAreaOutcome.isActive()) {
   * actionAreaOutcome = null;
   * }
   * return Optional.ofNullable(actionAreaOutcome)
   * .map(this.actionAreaOutcomesMapper::ActionAreaOutcomesToActionAreaOutcomeDTO)
   * .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
   * }
   */


  public List<ActionAreaOutcomeDTO> actionAreaOutcomesByActionAreaCode(String actionAreaCode) {
    if (this.actionAreaOutcomeIndicatorManager.getAll() != null) {
      List<ActionAreaOutcomeIndicator> actionAreaOutcomeIndicators =
        new ArrayList<>(this.actionAreaOutcomeIndicatorManager.getAll());
      TreeSet<ActionAreaOutcomeDTO> actionAreaOutcomeDTOs = actionAreaOutcomeIndicators.stream()
        .filter(actionAreaOutcomeEntity -> actionAreaOutcomeEntity != null && actionAreaOutcomeEntity.getId() != null
          && actionAreaOutcomeEntity.isActive() && actionAreaOutcomeEntity.getActionArea() != null
          && actionAreaOutcomeEntity.getActionArea().getId() != null
          && StringUtils.containsIgnoreCase(actionAreaOutcomeEntity.getActionArea().getSmoCode(), actionAreaCode))
        .map(actionAreaOutcomeEntity -> this.actionAreaOutcomesMapper
          .actionAreaOutcomeIndicatorToActionAreaOutcomeDTO(actionAreaOutcomeEntity))
        .collect(Collectors.toCollection(() -> new TreeSet<>(comparator)));
      return new ArrayList<>(actionAreaOutcomeDTOs);
    } else {
      return null;
    }
  }

  public List<ActionAreaOutcomeDTO> actionAreaOutcomesByActionAreaId(Long actionAreaId) {
    if (this.actionAreaOutcomeIndicatorManager.getAll() != null) {
      List<ActionAreaOutcomeIndicator> actionAreaOutcomeIndicators =
        new ArrayList<>(this.actionAreaOutcomeIndicatorManager.getAll());
      TreeSet<ActionAreaOutcomeDTO> actionAreaOutcomeDTOs = actionAreaOutcomeIndicators.stream()
        .filter(actionAreaOutcomeEntity -> actionAreaOutcomeEntity != null && actionAreaOutcomeEntity.getId() != null
          && actionAreaOutcomeEntity.isActive() && actionAreaOutcomeEntity.getActionArea() != null
          && actionAreaOutcomeEntity.getActionArea().getId() != null
          && actionAreaOutcomeEntity.getActionArea().getId().equals(actionAreaId))
        .map(actionAreaOutcomeEntity -> this.actionAreaOutcomesMapper
          .actionAreaOutcomeIndicatorToActionAreaOutcomeDTO(actionAreaOutcomeEntity))
        .collect(Collectors.toCollection(() -> new TreeSet<>(comparator)));
      return new ArrayList<>(actionAreaOutcomeDTOs);
    } else {
      return null;
    }
  }

  public List<ActionAreaOutcomeDTO> getAllActionAreaOutcomes() {
    if (this.actionAreaOutcomeIndicatorManager.getAll() != null) {
      List<ActionAreaOutcomeIndicator> actionAreaOutcomeIndicators =
        new ArrayList<>(this.actionAreaOutcomeIndicatorManager.getAll());
      TreeSet<ActionAreaOutcomeDTO> actionAreaOutcomeDTOs =
        actionAreaOutcomeIndicators.stream().filter(actionAreaOutcomeEntity -> actionAreaOutcomeEntity.isActive())
          .map(actionAreaOutcomeEntity -> this.actionAreaOutcomesMapper
            .actionAreaOutcomeIndicatorToActionAreaOutcomeDTO(actionAreaOutcomeEntity))
          .collect(Collectors.toCollection(() -> new TreeSet<>(comparator)));
      return new ArrayList<>(actionAreaOutcomeDTOs);
    } else {
      return null;
    }
  }

}
