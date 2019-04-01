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

import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyInvestimentTypeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyInvestimentType;
import org.cgiar.ccafs.marlo.rest.dto.PolicyInvestmentTypeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.PolicyInvestmentTypeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class PolicyInvestmentTypeItem<T> {

  private RepIndPolicyInvestimentTypeManager repIndPolicyInvestimentTypeManager;
  private PolicyInvestmentTypeMapper policyInvestimentTypeMapper;

  @Inject
  public PolicyInvestmentTypeItem(RepIndPolicyInvestimentTypeManager repIndPolicyInvestimentTypeManager,
    PolicyInvestmentTypeMapper policyInvestimentTypeMapper) {
    this.repIndPolicyInvestimentTypeManager = repIndPolicyInvestimentTypeManager;
    this.policyInvestimentTypeMapper = policyInvestimentTypeMapper;
  }

  /**
   * Get All the policy Investment Types Items *
   * 
   * @return a List of PolicyInvestmentTypeDTO with all repIndInnovationType
   *         Items.
   */
  public List<PolicyInvestmentTypeDTO> getAllPolicyInvestmentType() {
    if (this.repIndPolicyInvestimentTypeManager.findAll() != null) {
      List<RepIndPolicyInvestimentType> repIndPolicyInvestimentType =
        new ArrayList<>(this.repIndPolicyInvestimentTypeManager.findAll());

      List<PolicyInvestmentTypeDTO> policyInvestimentTypeDTOs = repIndPolicyInvestimentType.stream()
        .map(repIndPolicyInvestimentTypeEntity -> this.policyInvestimentTypeMapper
          .RepIndPolicyInvestimentTypeToPolicyInvestimentTypeDTO(repIndPolicyInvestimentTypeEntity))
        .collect(Collectors.toList());
      return policyInvestimentTypeDTOs;
    } else {
      return null;
    }
  }

  /**
   * Find a Policy Invesiment Type requesting a MARLO id
   * 
   * @param id
   * @return a PolicyInvestmentTypeDTO with the Policy Type data.
   */
  public ResponseEntity<PolicyInvestmentTypeDTO> PolicyInvestmentTypeById(Long id) {
    RepIndPolicyInvestimentType repIndPolicyInvestimentType =
      this.repIndPolicyInvestimentTypeManager.getRepIndPolicyInvestimentTypeById(id);
    return Optional.ofNullable(repIndPolicyInvestimentType)
      .map(this.policyInvestimentTypeMapper::RepIndPolicyInvestimentTypeToPolicyInvestimentTypeDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

}
