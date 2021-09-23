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

import org.cgiar.ccafs.marlo.data.manager.ActionAreaOutcomeManager;
import org.cgiar.ccafs.marlo.data.model.ActionAreaOutcome;
import org.cgiar.ccafs.marlo.rest.dto.ActionAreaOutcomeDTO;
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
public class ActionAreaOutcomesItem<T> {

  private ActionAreaOutcomeManager actionAreaOutcomeManager;

  private ActionAreaOutcomesMapper actionAreaOutcomesMapper;

  @Inject
  public ActionAreaOutcomesItem(ActionAreaOutcomeManager actionAreaOutcomeManager,
    ActionAreaOutcomesMapper actionAreaOutcomesMapper) {
    super();
    this.actionAreaOutcomeManager = actionAreaOutcomeManager;
    this.actionAreaOutcomesMapper = actionAreaOutcomesMapper;
  }


  public ResponseEntity<ActionAreaOutcomeDTO> findActionAreaOutcomeById(Long id) {
    ActionAreaOutcome actionAreaOutcome = this.actionAreaOutcomeManager.getActionAreaOutcomeById(id);

    return Optional.ofNullable(actionAreaOutcome)
      .map(this.actionAreaOutcomesMapper::ActionAreaOutcomesToActionAreaOutcomeDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }


  public List<ActionAreaOutcomeDTO> getAllActionAreaOutcomes() {
    if (this.actionAreaOutcomeManager.getAll() != null) {
      List<ActionAreaOutcome> actionAreaOutcomes = new ArrayList<>(this.actionAreaOutcomeManager.getAll());
      List<ActionAreaOutcomeDTO> actionAreaOutcomeDTOs =
        actionAreaOutcomes.stream().map(actionAreaOutcomeEntity -> this.actionAreaOutcomesMapper
          .ActionAreaOutcomesToActionAreaOutcomeDTO(actionAreaOutcomeEntity)).collect(Collectors.toList());
      return actionAreaOutcomeDTOs;
    } else {
      return null;
    }
  }

}
