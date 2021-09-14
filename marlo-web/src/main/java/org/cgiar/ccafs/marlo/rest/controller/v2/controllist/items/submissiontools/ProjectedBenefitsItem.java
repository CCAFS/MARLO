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

import org.cgiar.ccafs.marlo.data.manager.ProjectedBenefitsManager;
import org.cgiar.ccafs.marlo.data.model.ProjectedBenefits;
import org.cgiar.ccafs.marlo.rest.dto.ProjectedBenefitsDTO;
import org.cgiar.ccafs.marlo.rest.mappers.ProjectedBenefitsMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ProjectedBenefitsItem<T> {

  private ProjectedBenefitsManager projectedBenefitsManager;
  private ProjectedBenefitsMapper projectedBenefitsMapper;


  @Inject
  public ProjectedBenefitsItem(ProjectedBenefitsManager projectedBenefitsManager,
    ProjectedBenefitsMapper projectedBenefitsMapper) {
    super();
    this.projectedBenefitsManager = projectedBenefitsManager;
    this.projectedBenefitsMapper = projectedBenefitsMapper;

  }

  /**
   * Get All the projectedBenefits *
   * 
   * @return a List of BudgetTypeDTO with all the Budget type Items.
   */
  public List<ProjectedBenefitsDTO> getProjectedBenefits() {
    List<ProjectedBenefits> projectedBenefitsList = projectedBenefitsManager.findAll();
    List<ProjectedBenefitsDTO> projectedBenefitsDTOs = new ArrayList<ProjectedBenefitsDTO>();
    if (projectedBenefitsList != null) {
      projectedBenefitsDTOs = projectedBenefitsList.stream()
        .map(c -> this.projectedBenefitsMapper.projectBenefitsToProjectedBenefitsDTO(c)).collect(Collectors.toList());
    }
    return projectedBenefitsDTOs;
  }

}
