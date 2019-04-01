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

import org.cgiar.ccafs.marlo.data.manager.RepIndStageProcessManager;
import org.cgiar.ccafs.marlo.data.model.RepIndStageProcess;
import org.cgiar.ccafs.marlo.rest.dto.PolicyMaturityLevelDTO;
import org.cgiar.ccafs.marlo.rest.mappers.PolicyMaturityLevelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class PolicyMaturityLevelItem<T> {

  private RepIndStageProcessManager repIndStageProcessManager;
  private PolicyMaturityLevelMapper policyMaturityLevelMapper;

  @Inject
  public PolicyMaturityLevelItem(RepIndStageProcessManager repIndStageProcessManager,
    PolicyMaturityLevelMapper policyMaturityLevelMapper) {
    this.repIndStageProcessManager = repIndStageProcessManager;
    this.policyMaturityLevelMapper = policyMaturityLevelMapper;
  }

  /**
   * Get All the Policy Level Of Maturity Items *
   * 
   * @return a List of PolicyLevelOfMaturityDTO with all repIndStageProcess
   *         Items.
   */
  public List<PolicyMaturityLevelDTO> getAllPolicyMaturityLevel() {
    if (this.repIndStageProcessManager.findAll() != null) {
      List<RepIndStageProcess> repIndStageProcess = new ArrayList<>(this.repIndStageProcessManager.findAll().stream()
        .filter(c -> c.getYear() == 2018).collect(Collectors.toList()));

      List<PolicyMaturityLevelDTO> policyLevelOfMaturityDTO =
        repIndStageProcess.stream().map(repIndStageProcessEntity -> this.policyMaturityLevelMapper
          .repIndStageProcessToPolicyMaturityLevelDTO(repIndStageProcessEntity)).collect(Collectors.toList());
      return policyLevelOfMaturityDTO;
    } else {
      return null;
    }
  }

  /**
   * Find a Policy Level Of Maturity requesting a MARLO id
   * 
   * @param id
   * @return a PolicyLevelOfMaturityDTO with the Policy Level Of Maturity
   *         data.
   */
  public ResponseEntity<PolicyMaturityLevelDTO> PolicyMaturityLevelById(Long id) {
    RepIndStageProcess repIndStageProcess = this.repIndStageProcessManager.getRepIndStageProcessById(id);
    ResponseEntity<PolicyMaturityLevelDTO> response;
    if (repIndStageProcess != null && repIndStageProcess.getYear() == 2018) {
      response = new ResponseEntity<>(
        this.policyMaturityLevelMapper.repIndStageProcessToPolicyMaturityLevelDTO(repIndStageProcess), HttpStatus.OK);
    } else {
      response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return response;
  }

}
