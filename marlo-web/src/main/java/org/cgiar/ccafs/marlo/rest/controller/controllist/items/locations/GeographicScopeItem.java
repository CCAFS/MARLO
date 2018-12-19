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

package org.cgiar.ccafs.marlo.rest.controller.controllist.items.locations;

import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.GeographicScopeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

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
   * Get All the Geographic Scope items *
   * 
   * @return a List of GeographicScopeDTO with all RepIndGeographicScope Items.
   */
  public List<GeographicScopeDTO> getAllGeographicScopes() {
    if (repIndGeographicScopeManager.findAll() != null) {
      List<RepIndGeographicScope> repIndGeographicScopes = new ArrayList<>(repIndGeographicScopeManager.findAll());
      List<GeographicScopeDTO> geographicScopeDTOs = repIndGeographicScopes.stream().map(
        geographicScopeEntity -> geographicScopeMapper.repIndGeographicScopToGeographicScopeDTO(geographicScopeEntity))
        .collect(Collectors.toList());
      return geographicScopeDTOs;
    } else {
      return null;
    }
  }

}
