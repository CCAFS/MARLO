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

import org.cgiar.ccafs.marlo.data.model.ProjectedBenefitsWeightDescription;
import org.cgiar.ccafs.marlo.data.model.ProjectedBenefitsWeighting;
import org.cgiar.ccafs.marlo.rest.dto.DepthDescriptionsDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectedBenefitsWeightingDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330")
public interface ProjectedBenefitsWeightingMapper {

  @Mappings({@Mapping(source = "id", target = "descriptionID"),
    @Mapping(source = "description", target = "description")})
  public abstract DepthDescriptionsDTO projectedBenefitsWeightDescriptionToDepthDescriptionsDTO(
    ProjectedBenefitsWeightDescription projectedBenefitsWeightDescription);

  @Mappings({@Mapping(source = "weightDescription.id", target = "descriptionID"),
    @Mapping(source = "weightDescription.description", target = "description"),
    @Mapping(source = "value", target = "weightValue")})
  public abstract ProjectedBenefitsWeightingDTO
    projectedBenefitsWeightingToProjectedBenefitsWeightingDTO(ProjectedBenefitsWeighting projectedBenefitsWeighting);

}
