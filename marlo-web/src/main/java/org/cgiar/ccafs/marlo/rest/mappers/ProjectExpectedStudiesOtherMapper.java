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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.rest.dto.ProjectExpectedStudiesOtherDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330",
  uses = {ProjectExpectedStudySubIdoMapper.class, ProjectExpectedStudySrfSloTargetMapper.class,
    GeographicScopeMapper.class, LocationMapper.class, ProjectExpectedStudiesOtherInfoMapper.class})
public interface ProjectExpectedStudiesOtherMapper {

  @Mappings({@Mapping(source = "projectExpectedStudy.projectExpectedStudyInfo.phase", target = "phase"),
    @Mapping(source = "projectExpectedStudy.subIdos", target = "srfSubIdoList"),
    @Mapping(source = "projectExpectedStudy.srfTargets", target = "srfSloTargetList"),
    @Mapping(source = "projectExpectedStudy.geographicScopes", target = "geographicScopes"),
    @Mapping(source = "projectExpectedStudy.studyRegions", target = "regions"),
    @Mapping(source = "projectExpectedStudy.project.id", target = "project"),
    @Mapping(source = "projectExpectedStudy.countries", target = "countries"),
    @Mapping(source = "projectExpectedStudy.projectExpectedStudyInfo", target = "projectExpectedStudiesOtherInfo")})
  public abstract ProjectExpectedStudiesOtherDTO
    projectExpectedStudyToProjectExpectedStudiesOtherDTO(ProjectExpectedStudy projectExpectedStudy);

}
