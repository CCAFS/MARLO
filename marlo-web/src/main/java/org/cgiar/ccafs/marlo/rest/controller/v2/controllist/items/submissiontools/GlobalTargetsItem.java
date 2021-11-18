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

import org.cgiar.ccafs.marlo.data.manager.GlobalTargetManager;
import org.cgiar.ccafs.marlo.data.model.GlobalTargets;
import org.cgiar.ccafs.marlo.rest.dto.GlobalTargetDTO;
import org.cgiar.ccafs.marlo.rest.mappers.GlobalTargetMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class GlobalTargetsItem<T> {

  private GlobalTargetManager globalTargetManager;

  private GlobalTargetMapper globalTargetMapper;

  public GlobalTargetsItem(GlobalTargetManager globalTargetManager, GlobalTargetMapper globalTargetMapper) {
    super();
    this.globalTargetManager = globalTargetManager;
    this.globalTargetMapper = globalTargetMapper;
  }

  public ResponseEntity<GlobalTargetDTO> findGlobalTargetById(Long id) {
    GlobalTargets globaltarget = this.globalTargetManager.getGlobalTargetsById(id);

    return Optional.ofNullable(globaltarget).map(this.globalTargetMapper::globalTargetToGlobalTargetDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Global Target Items *
   * 
   * @return a List of globalTarget
   */
  public List<GlobalTargetDTO> getAllGlobalTarget() {
    if (this.globalTargetManager.getAll() != null) {
      List<GlobalTargets> globalTargets = new ArrayList<>(this.globalTargetManager.getAll());
      List<GlobalTargetDTO> globalTargetDTOs = globalTargets.stream()
        .map(globalTargetEntity -> this.globalTargetMapper.globalTargetToGlobalTargetDTO(globalTargetEntity))
        .collect(Collectors.toList());
      return globalTargetDTOs;
    } else {
      return null;
    }
  }

}
