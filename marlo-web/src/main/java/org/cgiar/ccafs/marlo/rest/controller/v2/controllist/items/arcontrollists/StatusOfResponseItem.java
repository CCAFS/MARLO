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

import org.cgiar.ccafs.marlo.data.model.ProjectStatusEnum;
import org.cgiar.ccafs.marlo.rest.dto.StatusOfResponseDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class StatusOfResponseItem<T> {

  private Map<Integer, String> statuses;

  @Inject
  public StatusOfResponseItem() {
  }

  /**
   * Find a Broad area Item MARLO id
   * 
   * @param id
   * @return a BroadAreaDTO with the broad area Item
   */
  public ResponseEntity<StatusOfResponseDTO> findStatusOfResponseById(Long id) {
    this.statuses = new HashMap<>();
    List<ProjectStatusEnum> listStatus = Arrays.asList(ProjectStatusEnum.values());
    StatusOfResponseDTO statusOfResponseDTO = null;
    for (ProjectStatusEnum globalStatusEnum : listStatus) {
      if ((id == 2 || id == 3) && globalStatusEnum.getStatusId().equals(id.toString())) {
        statusOfResponseDTO = new StatusOfResponseDTO();
        statusOfResponseDTO.setCode(Long.parseLong(globalStatusEnum.getStatusId()));
        statusOfResponseDTO.setName(globalStatusEnum.getStatus());
      }
    }
    return Optional.ofNullable(statusOfResponseDTO).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Broad Areas Items *
   * 
   * @return a List of BroadAreaDTO with all the Broad Areas Items.
   */
  public List<StatusOfResponseDTO> getAllStatusOfResponse() {
    List<StatusOfResponseDTO> statusList = new ArrayList<StatusOfResponseDTO>();
    this.statuses = new HashMap<>();
    List<ProjectStatusEnum> listStatus = Arrays.asList(ProjectStatusEnum.values());
    for (ProjectStatusEnum globalStatusEnum : listStatus) {
      if (globalStatusEnum.getStatusId().equals("2") || globalStatusEnum.getStatusId().equals("3")) {
        StatusOfResponseDTO statusOfResponseDTO = new StatusOfResponseDTO();
        statusOfResponseDTO.setCode(Long.parseLong(globalStatusEnum.getStatusId()));
        statusOfResponseDTO.setName(globalStatusEnum.getStatus());
        statusList.add(statusOfResponseDTO);
      }
    }
    return statusList;
  }

}
