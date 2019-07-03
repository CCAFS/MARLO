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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusManager;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.rest.dto.MilestoneStatusDTO;
import org.cgiar.ccafs.marlo.rest.mappers.MilestoneStatusMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class MilestoneStatusItem<T> {

  private GeneralStatusManager generalStatusManager;
  private MilestoneStatusMapper milestoneStatusMapper;

  @Inject
  public MilestoneStatusItem(GeneralStatusManager generalStatusManager, MilestoneStatusMapper milestoneStatusMapper) {
    this.generalStatusManager = generalStatusManager;
    this.milestoneStatusMapper = milestoneStatusMapper;
  }

  /**
   * Find a milestone status Item by id
   * 
   * @param id
   * @return a MilestoneStatusDTO with the milestone status Item
   */
  public ResponseEntity<MilestoneStatusDTO> findMilestoneStatusById(Long id) {
    List<GeneralStatus> statuses =
      this.generalStatusManager.findByTable(APConstants.REPORT_SYNTHESYS_FLAGSHIPS_PROGRESS_MILESTONES_TABLE);
    // GeneralStatus status = statuses.stream().filter(i -> i.getId() == id).findFirst();
    GeneralStatus generalStatus = statuses.stream().filter(i -> i.getId() == id).findFirst().get();

    return Optional.ofNullable(generalStatus).map(this.milestoneStatusMapper::generalStatusToMilestoneStatusDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the milestone status Items *
   * 
   * @return a List of MilestoneStatusDTO with all the milestone status Items.
   */
  public List<MilestoneStatusDTO> getAllMilestoneStatus() {
    if (this.generalStatusManager
      .findByTable(APConstants.REPORT_SYNTHESYS_FLAGSHIPS_PROGRESS_MILESTONES_TABLE) != null) {
      List<GeneralStatus> statuses =
        this.generalStatusManager.findByTable(APConstants.REPORT_SYNTHESYS_FLAGSHIPS_PROGRESS_MILESTONES_TABLE);

      List<MilestoneStatusDTO> milestoneStatusDTOs = statuses.stream()
        .map(statusEntity -> this.milestoneStatusMapper.generalStatusToMilestoneStatusDTO(statusEntity))
        .collect(Collectors.toList());
      return milestoneStatusDTOs;
    } else {
      return null;
    }
  }

}
