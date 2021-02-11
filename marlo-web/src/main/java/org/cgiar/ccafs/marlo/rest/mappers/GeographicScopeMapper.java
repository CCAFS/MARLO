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

package org.cgiar.ccafs.marlo.rest.mappers;

import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

@Mapper(componentModel = "jsr330")
public interface GeographicScopeMapper {

  @Mappings({@Mapping(source = "code", target = "id")})
  public abstract RepIndGeographicScope
    geographicScopeDTOToRepIndGeographicScope(GeographicScopeDTO geographicScopeDTO);

  @Mappings({@Mapping(source = "repIndGeographicScope.id", target = "code"),
    @Mapping(source = "repIndGeographicScope.name", target = "name"),
    @Mapping(source = "repIndGeographicScope.definition", target = "definition")})
  public abstract GeographicScopeDTO progressTargetCaseGeographicScopeToGeographicScopeDTO(
    ProgressTargetCaseGeographicScope progressTargetCaseGeographicScope);

  @Mappings({@Mapping(source = "projectExpectedStudyGeographicScope.repIndGeographicScope.id", target = "code"),
    @Mapping(source = "projectExpectedStudyGeographicScope.repIndGeographicScope.name", target = "name"),
    @Mapping(source = "projectExpectedStudyGeographicScope.repIndGeographicScope.definition", target = "definition")})
  public abstract GeographicScopeDTO projectExpectedStudyGeographicScopeToGeographicScopeDTO(
    ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope);

  @Mappings({@Mapping(source = "repIndGeographicScope.id", target = "code"),
    @Mapping(source = "repIndGeographicScope.name", target = "name"),
    @Mapping(source = "repIndGeographicScope.definition", target = "definition")})
  public abstract GeographicScopeDTO projectInnovationGeographicScopeToGeographicScopeDTO(
    ProjectInnovationGeographicScope projectInnovationGeographicScope);


  @Mappings({@Mapping(source = "id", target = "code")})
  public abstract GeographicScopeDTO
    repIndGeographicScopToGeographicScopeDTO(RepIndGeographicScope repIndGeographicScope);
}
