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

import org.cgiar.ccafs.marlo.data.manager.ActionAreasManager;
import org.cgiar.ccafs.marlo.data.model.ActionArea;
import org.cgiar.ccafs.marlo.rest.dto.ActionAreasDTO;
import org.cgiar.ccafs.marlo.rest.mappers.ActionAreasMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ActionAreasItem<T> {

  private ActionAreasManager actionAreasManager;

  private ActionAreasMapper actionAreasMapper;

  @Inject
  public ActionAreasItem(ActionAreasManager actionAreasManager, ActionAreasMapper actionAreasMapper) {
    super();
    this.actionAreasManager = actionAreasManager;
    this.actionAreasMapper = actionAreasMapper;
  }

  public ResponseEntity<ActionAreasDTO> findActionAreaById(Long id) {
    ActionArea actionArea = this.actionAreasManager.getActionAreaById(id);

    return Optional.ofNullable(actionArea).map(this.actionAreasMapper::actionAreasToActionAreasDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Budget type Items *
   * 
   * @return a List of BudgetTypeDTO with all the Budget type Items.
   */
  public List<ActionAreasDTO> getAllActionAreas() {
    if (this.actionAreasManager.getAll() != null) {
      List<ActionArea> actionAreas = new ArrayList<>(this.actionAreasManager.getAll());
      List<ActionAreasDTO> actionAreasDTOs = actionAreas.stream()
        .map(budgetTypesEntity -> this.actionAreasMapper.actionAreasToActionAreasDTO(budgetTypesEntity))
        .collect(Collectors.toList());
      return actionAreasDTOs;
    } else {
      return null;
    }
  }


}
