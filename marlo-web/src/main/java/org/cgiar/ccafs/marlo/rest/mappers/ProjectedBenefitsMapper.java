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

package org.cgiar.ccafs.marlo.rest.mappers;

import org.cgiar.ccafs.marlo.data.model.ProjectedBenefits;
import org.cgiar.ccafs.marlo.rest.dto.ProjectedBenefitsDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330", uses = {DepthScaleMapper.class, ProjectedBenefitsWeightingMapper.class})
public interface ProjectedBenefitsMapper {

  @Mappings({@Mapping(source = "impactAreaIndicator.id", target = "impactAreaIndicator"),
    @Mapping(source = "impactAreaIndicator.indicatorStatement", target = "impactAreaIndicatorName"),
    @Mapping(source = "impactAreaIndicator.impactArea.id", target = "impactAreaId"),
    @Mapping(source = "impactAreaIndicator.impactArea.name", target = "impactAreaName"),
    @Mapping(source = "impactAreaIndicator.isProjectedBenefits", target = "isApplicableProjectedBenefits"),
    @Mapping(source = "impactAreaIndicator.targetYear", target = "targetYear"),
    @Mapping(source = "impactAreaIndicator.targetUnit", target = "targetUnit"),
    @Mapping(source = "impactAreaIndicator.targetValue", target = "value", defaultValue = ""),
    @Mapping(source = "weightingList", target = "weightingValues")})
  public abstract ProjectedBenefitsDTO projectBenefitsToProjectedBenefitsDTO(ProjectedBenefits projectedBenefits);

}
