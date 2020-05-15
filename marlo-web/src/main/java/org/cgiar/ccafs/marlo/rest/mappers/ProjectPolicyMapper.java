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

/**
 * @author Diego Perez - CIAT/CCAFS
 **/

package org.cgiar.ccafs.marlo.rest.mappers;

import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPolicyDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330",
  uses = {ProjectPoliciesInfoMapper.class, ProjectPolicyCrosscuttingMarkersMapper.class,
    ProjectPolicyGeographicScopeMapper.class, ProjectPolicyCrpMapper.class, ProjectPolicySubIdoMapper.class,
    LocationMapper.class, ProjectPolicyOwnerMapper.class, PolicyMilestoneMapper.class,
    ProjectExpectedStudyPolicyMapper.class, ProjectPolicyInnovationMapper.class})
public interface ProjectPolicyMapper {

  @Mappings({@Mapping(source = "projectPolicyDTO.project", target = "project.id"),})
  public abstract ProjectPolicy projectPolicyDTOToProjectPolicy(ProjectPolicyDTO projectPolicyDTO);

  @Mappings({@Mapping(source = "projectPolicy.crps", target = "projectPolicyCrpDTO"),
    @Mapping(source = "projectPolicy.subIdos", target = "srfSubIdoList"),
    @Mapping(source = "projectPolicy.geographicScopes", target = "geographicScopes"),
    @Mapping(source = "projectPolicy.crossCuttingMarkers", target = "crossCuttingMarkers"),
    @Mapping(source = "projectPolicy.projectPolicyInfo", target = "projectPoliciesInfo"),
    @Mapping(source = "projectPolicy.regions", target = "regions"),
    @Mapping(source = "projectPolicy.countries", target = "countries"),
    @Mapping(source = "projectPolicy.project.id", target = "project"),
    @Mapping(source = "projectPolicy.owners", target = "owners"),
    @Mapping(source = "projectPolicy.evidences", target = "projectExpetedStudyList"),
    @Mapping(source = "projectPolicy.innovations", target = "projectInnovationList")})
  public abstract ProjectPolicyDTO projectPolicyToProjectPolicyDTO(ProjectPolicy projectPolicy);

}
