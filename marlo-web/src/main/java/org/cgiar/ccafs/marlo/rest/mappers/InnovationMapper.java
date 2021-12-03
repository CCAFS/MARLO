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

import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationEvidenceLink;
import org.cgiar.ccafs.marlo.rest.dto.EvidenceLinkDTO;
import org.cgiar.ccafs.marlo.rest.dto.InnovationDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectInnovationARDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPageInnovationsDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Manuel Almanzar - CIAT/CCAFS
 */

@Mapper(componentModel = "jsr330",
  uses = {StageOfInnovationMapper.class, InstitutionMapper.class, InnovationTypeMapper.class, LocationMapper.class,
    InstitutionTypeMapper.class, ContributionOfCrpMapper.class, GlobalUnitMapper.class, GeographicScopeMapper.class,
    PhaseMapper.class, ProjectInnovationMilestoneMapper.class, ProjectInnovationSubIdoMapper.class,
    ProjectExpectedStudyInnovationMapper.class, DefaultFieldMapper.class, DefaultFieldPrimaryMapper.class})
public interface InnovationMapper {


  public abstract EvidenceLinkDTO
    projectInnovationEvidenceLinkToEvidenceLinkDTO(ProjectInnovationEvidenceLink projectInnovationEvidenceLink);


  @Mappings({@Mapping(source = "project.id", target = "project"),
    @Mapping(source = "projectInnovationInfo.title", target = "title"),
    @Mapping(source = "projectInnovationInfo.narrative", target = "narrative"),
    @Mapping(source = "projectInnovationInfo.year", target = "year"),
    @Mapping(source = "projectInnovation.projectInnovationInfo.clearLead", target = "equitativeEffort"),
    @Mapping(source = "projectInnovation.projectInnovationInfo.repIndStageInnovation", target = "stageOfInnovation"),
    @Mapping(source = "organizations", target = "nextUserOrganizationTypes"),
    @Mapping(source = "projectInnovationInfo.descriptionStage", target = "descriptionStage"),
    @Mapping(source = "projectInnovationInfo.repIndInnovationType", target = "innovationType"),
    @Mapping(source = "projectInnovationInfo.innovationNumber", target = "innovationNumber"),
    @Mapping(source = "projectInnovationInfo.otherInnovationType", target = "otherInnovationType"),
    @Mapping(source = "geographicScopes", target = "geographicScopes"),
    @Mapping(source = "projectInnovationInfo.evidenceLink", target = "evidenceLink"),
    @Mapping(source = "crps", target = "contributingCGIAREntities"), @Mapping(source = "regions", target = "regions"),
    @Mapping(source = "countries", target = "countries"),
    @Mapping(source = "projectInnovationInfo.leadOrganization", target = "leadOrganization"),
    @Mapping(source = "contributingOrganizations", target = "contributingInstitutions"),
    @Mapping(source = "projectInnovation.milestones", target = "milestonesList"),
    @Mapping(source = "projectInnovation.subIdos", target = "srfSubIdoList"),
    @Mapping(source = "projectInnovation.studies", target = "projectExpetedStudyList"),
    @Mapping(source = "projectInnovation.innovationLinks", target = "evidenceLinkList")})
  public abstract ProjectInnovationARDTO projectInnovationToInnovationARDTO(ProjectInnovation projectInnovation);

  @Mappings({@Mapping(source = "projectInnovation.projectInnovationInfo.title", target = "title"),
    @Mapping(source = "projectInnovation.projectInnovationInfo.narrative", target = "narrative"),
    @Mapping(source = "projectInnovation.projectInnovationInfo.clearLead", target = "equitativeEffort"),
    @Mapping(source = "projectInnovation.projectInnovationInfo.repIndStageInnovation", target = "stageOfInnovation"),
    @Mapping(source = "organizations", target = "nextUserOrganizationTypes"),
    @Mapping(source = "projectInnovation.projectInnovationInfo.descriptionStage", target = "descriptionStage"),
    @Mapping(source = "projectInnovation.projectInnovationInfo.leadOrganization", target = "leadOrganization"),
    @Mapping(source = "contributingOrganizations", target = "contributingInstitutions"),
    @Mapping(source = "projectInnovation.projectInnovationInfo.repIndInnovationType", target = "innovationType"),
    @Mapping(source = "projectInnovation.projectInnovationInfo.innovationNumber", target = "innovationNumber"),
    @Mapping(source = "regions", target = "regions"), @Mapping(source = "countries", target = "countries"),
    @Mapping(source = "geographicScopes", target = "geographicScopes"),
    @Mapping(source = "projectInnovation.projectInnovationInfo.evidenceLink", target = "evidenceLink"),
    @Mapping(source = "projectInnovation.crps", target = "contributingCGIAREntities"),
    @Mapping(source = "projectInnovation.projectInnovationInfo.phase", target = "phase"),
    @Mapping(source = "projectInnovation.projectInnovationInfo.otherInnovationType", target = "otherInnovationType"),
    @Mapping(source = "projectInnovation.milestones", target = "milestonesList"),
    @Mapping(source = "projectInnovation.subIdos", target = "srfSubIdoList"),
    @Mapping(source = "projectInnovation.studies", target = "projectExpetedStudyList"),
    @Mapping(source = "projectInnovation.innovationLinks", target = "evidenceLinkList")})
  public abstract InnovationDTO projectInnovationToInnovationDTO(ProjectInnovation projectInnovation);

  @Mappings({@Mapping(source = "projectInnovationInfo.title", target = "title"),
    @Mapping(source = "projectInnovationInfo.year", target = "year"),
    @Mapping(source = "pdfLink", target = "externalLink")})
  public abstract ProjectPageInnovationsDTO
    projectInnovationToProjectPageInnovationsDTO(ProjectInnovation projectInnovation);


}
