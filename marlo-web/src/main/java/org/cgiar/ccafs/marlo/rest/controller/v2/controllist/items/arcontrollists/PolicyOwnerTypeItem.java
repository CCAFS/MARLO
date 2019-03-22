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

import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyTypeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyType;
import org.cgiar.ccafs.marlo.rest.dto.PolicyOwnerTypeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.PolicyOwnerTypeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class PolicyOwnerTypeItem<T> {

  private RepIndPolicyTypeManager repIndPolicyTypeManager;
  private PolicyOwnerTypeMapper policyOwnerTypeMapper;

  @Inject
  public PolicyOwnerTypeItem(RepIndPolicyTypeManager repIndPolicyTypeManager,
    PolicyOwnerTypeMapper policyOwnerTypeMapper) {
    this.repIndPolicyTypeManager = repIndPolicyTypeManager;
    this.policyOwnerTypeMapper = policyOwnerTypeMapper;
  }

  /**
   * Find a Policy owner type by id
   * 
   * @param id
   * @return a PolicyOwnerTypeDTO with the Policy owner type by id
   */
  public ResponseEntity<PolicyOwnerTypeDTO> findPolicyOwnerTypeById(Long id) {
    RepIndPolicyType repIndPolicyType = this.repIndPolicyTypeManager.getRepIndPolicyTypeById(id);
    return Optional.ofNullable(repIndPolicyType).map(this.policyOwnerTypeMapper::repIndPolicyTypeToPolicyOwnerTypeDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Innovation Types Items *
   * 
   * @return a List of InnovationTypesDTO with all repIndInnovationType Items.
   */
  public List<PolicyOwnerTypeDTO> getAllPolicyOwnerTypes() {
    if (this.repIndPolicyTypeManager.findAll() != null) {
      List<RepIndPolicyType> repIndPolicyType = new ArrayList<>(this.repIndPolicyTypeManager.findAll());
      List<PolicyOwnerTypeDTO> policyOwnerTypeDTO = repIndPolicyType.stream().map(
        policyOwnerTypeEntity -> this.policyOwnerTypeMapper.repIndPolicyTypeToPolicyOwnerTypeDTO(policyOwnerTypeEntity))
        .collect(Collectors.toList());
      return policyOwnerTypeDTO;
    } else {
      return null;
    }
  }

}
