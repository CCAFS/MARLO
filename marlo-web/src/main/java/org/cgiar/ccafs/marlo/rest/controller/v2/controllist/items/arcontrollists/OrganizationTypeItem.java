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

import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.rest.dto.OrganizationTypeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.OrganizationTypeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class OrganizationTypeItem<T> {

  private RepIndOrganizationTypeManager repIndOrganizationTypeManager;
  private OrganizationTypeMapper organizationTypeMapper;

  @Inject
  public OrganizationTypeItem(RepIndOrganizationTypeManager repIndOrganizationTypeManager,
    OrganizationTypeMapper organizationTypeMapper) {
    this.repIndOrganizationTypeManager = repIndOrganizationTypeManager;
    this.organizationTypeMapper = organizationTypeMapper;
  }

  /**
   * Find a Organization Type Item MARLO id
   * 
   * @param id
   * @return a OrganizationTypeDTO with the Organization Type Item
   */
  public ResponseEntity<OrganizationTypeDTO> findOrganizationTypeById(Long id) {
    RepIndOrganizationType repIndOrganizationType =
      this.repIndOrganizationTypeManager.getRepIndOrganizationTypeById(id);
    return Optional.ofNullable(repIndOrganizationType)
      .map(this.organizationTypeMapper::repIndOrganizationTypeToOrganizationTypeDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Organization Types Items *
   * 
   * @return a List of OrganizationTypeDTO with all Organization Types items
   *         Items.
   */
  public List<OrganizationTypeDTO> getAllOrganizationTypes() {
    if (this.repIndOrganizationTypeManager.findAll() != null) {
      List<RepIndOrganizationType> repIndOrganizationTypes =
        new ArrayList<>(this.repIndOrganizationTypeManager.findAll());

      List<OrganizationTypeDTO> organizationTypeDTOs =
        repIndOrganizationTypes.stream().map(repIndOrganizationTypeEntity -> this.organizationTypeMapper
          .repIndOrganizationTypeToOrganizationTypeDTO(repIndOrganizationTypeEntity)).collect(Collectors.toList());
      return organizationTypeDTOs;
    } else {
      return null;
    }
  }

}
