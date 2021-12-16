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

import org.cgiar.ccafs.marlo.data.manager.BudgetTypeManager;
import org.cgiar.ccafs.marlo.data.model.BudgetType;
import org.cgiar.ccafs.marlo.rest.dto.BudgetTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.BudgetTypeOneCGIARDTO;
import org.cgiar.ccafs.marlo.rest.mappers.BudgetTypeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class BudgetTypeItem<T> {

  private BudgetTypeManager budgetTypeManager;
  private BudgetTypeMapper budgetTypeMapper;

  @Inject
  public BudgetTypeItem(BudgetTypeManager budgetTypeManager, BudgetTypeMapper budgetTypeMapper) {
    this.budgetTypeManager = budgetTypeManager;
    this.budgetTypeMapper = budgetTypeMapper;
  }

  /**
   * Find a Budget type Item MARLO id
   * 
   * @param id
   * @return a BudgetTypeDTO with the Budget type Item
   */
  public ResponseEntity<BudgetTypeDTO> findBudgetTypeById(Long id) {
    BudgetType budgetType = this.budgetTypeManager.getBudgetTypeById(id);

    return Optional.ofNullable(budgetType).map(this.budgetTypeMapper::budgetTypeToBudgetTypeDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All the Budget type Items *
   * 
   * @return a List of BudgetTypeDTO with all the Budget type Items.
   */
  public List<BudgetTypeDTO> getAllBudgetTypes() {
    if (this.budgetTypeManager.findAll() != null) {
      List<BudgetType> budgetTypes = new ArrayList<>(this.budgetTypeManager.findAll());

      List<BudgetTypeDTO> budgetTypeDTOs = budgetTypes.stream()
        .map(budgetTypesEntity -> this.budgetTypeMapper.budgetTypeToBudgetTypeDTO(budgetTypesEntity))
        .collect(Collectors.toList());
      return budgetTypeDTOs;
    } else {
      return null;
    }
  }

  public List<BudgetTypeOneCGIARDTO> getAllBudgetTypesCGIAR() {
    if (this.budgetTypeManager.findAllFundingSourcesCGIAR() != null) {
      List<BudgetType> budgetTypes = new ArrayList<>(this.budgetTypeManager.findAllFundingSourcesCGIAR());

      List<BudgetTypeOneCGIARDTO> budgetTypeDTOs = budgetTypes.stream()
        .map(budgetTypesEntity -> this.budgetTypeMapper.budgetTypeToBudgetTypeOneCGIARDTO(budgetTypesEntity))
        .collect(Collectors.toList());
      return budgetTypeDTOs;
    } else {
      return null;
    }
  }

}
