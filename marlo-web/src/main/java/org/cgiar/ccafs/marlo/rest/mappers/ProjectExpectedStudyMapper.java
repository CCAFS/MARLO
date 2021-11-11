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
import org.cgiar.ccafs.marlo.rest.dto.ProjectExpectedStudiesARDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectExpectedStudyDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPageStudiesDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330",
  uses = {ProjectExpectedStudyLinkMapper.class, ProjectExpectedStudyInfoMapper.class, InstitutionMapper.class,
    ProjectExpectedStudySubIdoMapper.class, ProjectExpectedStudySrfSloTargetMapper.class,
    ProjectExpectedStudiesCrpMapper.class, ProjectExpectedStudyInstitutionMapper.class,
    ProjectExpectedStudyFlagshipMapper.class, GeographicScopeMapper.class, LocationMapper.class,
    ProjectExpectedStudyPolicyMapper.class, ProjectExpectedStudyInnovationMapper.class,
    ProjectExpectedStudyLinkMapper.class, ProjectExpectedStudyQuantificationMapper.class,
    ProjectExpectedStudyMilestoneMapper.class, DefaultFieldMapper.class, DefaultFieldPrimaryMapper.class,
    ReferenceCitedMapper.class})
public interface ProjectExpectedStudyMapper {

  @Mappings({@Mapping(source = "projectExpectedStudy.subIdos", target = "srfSubIdoList"),
    @Mapping(source = "projectExpectedStudy.srfTargets", target = "srfSloTargetList"),
    @Mapping(source = "projectExpectedStudy.crps", target = "projectExpectedStudiesCrp"),
    @Mapping(source = "projectExpectedStudy.institutions", target = "institutionsList"),
    @Mapping(source = "projectExpectedStudy.flagships", target = "flagshipsList"),
    @Mapping(source = "projectExpectedStudy.geographicScopes", target = "geographicScopes"),
    @Mapping(source = "projectExpectedStudy.studyRegions", target = "regions"),
    @Mapping(source = "projectExpectedStudy.project.id", target = "project"),
    @Mapping(source = "projectExpectedStudy.countries", target = "countries"),
    @Mapping(source = "projectExpectedStudy.policies", target = "policiesCodeList"),
    @Mapping(source = "projectExpectedStudy.innovations", target = "innovationCodeList"),
    @Mapping(source = "projectExpectedStudy.links", target = "links"),
    @Mapping(source = "projectExpectedStudy.projectExpectedStudyInfo", target = "projectExpectedEstudyInfo"),
    @Mapping(source = "projectExpectedStudy.quantifications", target = "quantificationList"),
    @Mapping(source = "projectExpectedStudy.milestones", target = "milestonesList"),
    @Mapping(source = "projectExpectedStudy.references", target = "referenceList"),
    @Mapping(source = "projectExpectedStudy.projectExpectedStudyInfo.scopeComments", target = "scopeComment")})
  public abstract ProjectExpectedStudiesARDTO
    projectExpectedStudyToProjectExpectedStudyARDTO(ProjectExpectedStudy projectExpectedStudy);

  @Mappings({@Mapping(source = "projectExpectedStudy.phase", target = "phaseID"),
    @Mapping(source = "projectExpectedStudy.projectExpectedStudyInfo.phase", target = "phase"),
    @Mapping(source = "projectExpectedStudy.subIdos", target = "srfSubIdoList"),
    @Mapping(source = "projectExpectedStudy.srfTargets", target = "srfSloTargetList"),
    @Mapping(source = "projectExpectedStudy.crps", target = "projectExpectedStudiesCrp"),
    @Mapping(source = "projectExpectedStudy.institutions", target = "institutionsList"),
    @Mapping(source = "projectExpectedStudy.flagships", target = "flagshipsList"),
    @Mapping(source = "projectExpectedStudy.geographicScopes", target = "geographicScopes"),
    @Mapping(source = "projectExpectedStudy.studyRegions", target = "regions"),
    @Mapping(source = "projectExpectedStudy.project.id", target = "project"),
    @Mapping(source = "projectExpectedStudy.countries", target = "countries"),
    @Mapping(source = "projectExpectedStudy.policies", target = "policiesCodeList"),
    @Mapping(source = "projectExpectedStudy.innovations", target = "innovationCodeList"),
    @Mapping(source = "projectExpectedStudy.links", target = "links"),
    @Mapping(source = "projectExpectedStudy.projectExpectedStudyInfo", target = "projectExpectedEstudyInfo"),
    @Mapping(source = "projectExpectedStudy.quantifications", target = "quantificationList"),
    @Mapping(source = "projectExpectedStudy.milestones", target = "milestonesList"),
    @Mapping(source = "projectExpectedStudy.references", target = "referenceList"),
    @Mapping(source = "projectExpectedStudy.projectExpectedStudyInfo.scopeComments", target = "scopeComments")})

  public abstract ProjectExpectedStudyDTO
    projectExpectedStudyToProjectExpectedStudyDTO(ProjectExpectedStudy projectExpectedStudy);

  @Mappings({@Mapping(source = "projectExpectedStudyInfo.title", target = "title"),
    @Mapping(source = "projectExpectedStudyInfo.year", target = "year"),
    @Mapping(source = "pdfLink", target = "externalLink")})
  public abstract ProjectPageStudiesDTO
    projectExpectedStudyToProjectPageStudiesDTO(ProjectExpectedStudy projectExpectedStudy);


}
