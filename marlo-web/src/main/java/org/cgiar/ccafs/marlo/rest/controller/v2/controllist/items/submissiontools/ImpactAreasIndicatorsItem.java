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

import org.cgiar.ccafs.marlo.data.manager.ImpactAreaIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.ImpactAreaIndicator;
import org.cgiar.ccafs.marlo.rest.dto.ImpactAreasIndicatorsDTO;
import org.cgiar.ccafs.marlo.rest.mappers.ImpactAreasIndicatorMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ImpactAreasIndicatorsItem<T> {

  private ImpactAreaIndicatorManager impactAreaIndicatorManager;
  private ImpactAreasIndicatorMapper impactAreasIndicatorMapper;

  @Inject
  public ImpactAreasIndicatorsItem(ImpactAreaIndicatorManager impactAreaIndicatorManager,
    ImpactAreasIndicatorMapper impactAreasIndicatorMapper) {
    super();
    this.impactAreaIndicatorManager = impactAreaIndicatorManager;
    this.impactAreasIndicatorMapper = impactAreasIndicatorMapper;
  }

  public ResponseEntity<ImpactAreasIndicatorsDTO> findImpactAreaIndicatorById(Long id) {
    ImpactAreaIndicator impactAreaIndicator = this.impactAreaIndicatorManager.getImpactAreaIndicatorById(id);

    return Optional.ofNullable(impactAreaIndicator)
      .map(this.impactAreasIndicatorMapper::impactAreasIndicatorsToImpactAreasIndicatorsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Budget type Items *
   * 
   * @return a List of BudgetTypeDTO with all the Budget type Items.
   */
  public List<ImpactAreasIndicatorsDTO> getAllImpactAreasIndicators() {
    if (this.impactAreaIndicatorManager.findAll() != null) {
      List<ImpactAreaIndicator> impactAreasIndicators = new ArrayList<>(this.impactAreaIndicatorManager.findAll());
      List<ImpactAreasIndicatorsDTO> impactAreasIndicatorsDTOs = impactAreasIndicators.stream()
        .map(i -> this.impactAreasIndicatorMapper.impactAreasIndicatorsToImpactAreasIndicatorsDTO(i))
        .collect(Collectors.toList());
      return impactAreasIndicatorsDTOs;
    } else {
      return null;
    }
  }

}
