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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists;

import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.GeographicScopeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

@Named
public class GeographicScopeItem<T> {

  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private GeographicScopeMapper geographicScopeMapper;

  @Inject
  public GeographicScopeItem(RepIndGeographicScopeManager repIndGeographicScopeManager,
    GeographicScopeMapper geographicScopeMapper) {
    super();
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.geographicScopeMapper = geographicScopeMapper;
  }

  /**
   * Get a Geographic Scope by id *
   * 
   * @return GeographicScopeDTO founded.
   */
  public ResponseEntity<GeographicScopeDTO> findGeographicScopesById(Long id) {
    RepIndGeographicScope repIndGeographicScope = this.repIndGeographicScopeManager.getRepIndGeographicScopeById(id);
    return Optional.ofNullable(repIndGeographicScope)
      .map(this.geographicScopeMapper::repIndGeographicScopToGeographicScopeDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Geographic Scope items *
   * 
   * @return a List of GeographicScopeDTO with all RepIndGeographicScope
   *         Items.
   */
  public List<GeographicScopeDTO> getAllGeographicScopes() {
    if (this.repIndGeographicScopeManager.findAll() != null) {
      List<RepIndGeographicScope> repIndGeographicScopes = new ArrayList<>(this.repIndGeographicScopeManager.findAll());
      List<GeographicScopeDTO> geographicScopeDTOs =
        repIndGeographicScopes.stream().map(geographicScopeEntity -> this.geographicScopeMapper
          .repIndGeographicScopToGeographicScopeDTO(geographicScopeEntity)).collect(Collectors.toList());
      return geographicScopeDTOs;
    } else {
      return null;
    }
  }

}
