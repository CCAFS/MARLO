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

import org.cgiar.ccafs.marlo.data.manager.ImpactAreasManager;
import org.cgiar.ccafs.marlo.data.model.ImpactArea;
import org.cgiar.ccafs.marlo.rest.dto.ImpactAreasDTO;
import org.cgiar.ccafs.marlo.rest.mappers.ImpactAreasMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ImpactAreasItem<T> {

  private ImpactAreasManager impactAreasManager;

  private ImpactAreasMapper impactAreasMapper;

  @Inject
  public ImpactAreasItem(ImpactAreasManager impactAreasManager, ImpactAreasMapper impactAreasMapper) {
    super();
    this.impactAreasManager = impactAreasManager;
    this.impactAreasMapper = impactAreasMapper;
  }

  public ResponseEntity<ImpactAreasDTO> findActionAreaById(Long id) {
    ImpactArea impactArea = this.impactAreasManager.getImpactAreaById(id);

    return Optional.ofNullable(impactArea).map(this.impactAreasMapper::impactAreaToImpactAreaDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Budget type Items *
   * 
   * @return a List of BudgetTypeDTO with all the Budget type Items.
   */
  public List<ImpactAreasDTO> getAllActionAreas() {
    if (this.impactAreasManager.getAll() != null) {
      List<ImpactArea> impactAereas = new ArrayList<>(this.impactAreasManager.getAll());
      List<ImpactAreasDTO> impactAreasDTOs = impactAereas.stream()
        .map(budgetTypesEntity -> this.impactAreasMapper.impactAreaToImpactAreaDTO(budgetTypesEntity))
        .collect(Collectors.toList());
      return impactAreasDTOs;
    } else {
      return null;
    }
  }


}
