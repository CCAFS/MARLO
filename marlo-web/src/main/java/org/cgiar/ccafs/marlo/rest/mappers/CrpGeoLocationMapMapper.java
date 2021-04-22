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

import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.FundingSource;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPartner;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapDeliverablesDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapFundingSourcesDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapInnovationsDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapOICRDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapPartnersDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrpGeoLocationMapProjectsDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330", uses = CrpProgramMapper.class)
public interface CrpGeoLocationMapMapper {

  @Mappings({@Mapping(source = "deliverableInfo.title", target = "title"),
    @Mapping(source = "deliverableInfo.deliverableType.name", target = "deliverableType"),
    @Mapping(source = "project.id", target = "projectId"),
    @Mapping(source = "dissemination.disseminationUrl", target = "link"),
    @Mapping(source = "deliverableInfo.status", target = "status")})
  public abstract CrpGeoLocationMapDeliverablesDTO
    deliverableToCrpGeoLocationMapDeliverablesDTO(Deliverable deliverable);

  @Mappings({@Mapping(source = "fundingSourceInfo.title", target = "title"), @Mapping(source = "id", target = "id")})
  public abstract CrpGeoLocationMapFundingSourcesDTO
    fundingSourceToCrpGeoLocationMapFundingSourcesDTO(FundingSource fundingSource);

  @Mappings({@Mapping(source = "projectExpectedStudyInfo.title", target = "title"),
    @Mapping(source = "projectExpectedStudyInfo.year", target = "year"),
    @Mapping(source = "projectExpectedStudyInfo.repIndStageProcess.name", target = "status"),
    @Mapping(source = "pdfLink", target = "link")})
  public abstract CrpGeoLocationMapOICRDTO
    projectExpectedStudyToCrpGeoLocationMapOICRDTO(ProjectExpectedStudy expectedStudy);

  @Mappings({@Mapping(source = "id", target = "id"), @Mapping(source = "projectInnovationInfo.title", target = "title"),
    @Mapping(source = "project.id", target = "projectId"),
    @Mapping(source = "projectInnovationInfo.repIndInnovationType.name", target = "type"),
    @Mapping(source = "projectInnovationInfo.repIndStageInnovation.name", target = "stage"),
    @Mapping(source = "projectInnovationInfo.year", target = "year"), @Mapping(source = "pdfLink", target = "link")})
  public abstract CrpGeoLocationMapInnovationsDTO
    projectInnovationToCrpGeoLocationMapInnovationsDTO(ProjectInnovation projectInnovation);

  @Mappings({@Mapping(source = "institution.id", target = "id"),
    @Mapping(source = "institution.name", target = "institutionName"),
    @Mapping(source = "institution.acronym", target = "acronym"),
    @Mapping(source = "institution.institutionType.name", target = "institutionType"),
    @Mapping(source = "institution.websiteLink", target = "website")})
  public abstract CrpGeoLocationMapPartnersDTO
    projectPartnersToCrpGeoLocationMapPartnersDTO(ProjectPartner projectPartner);

  @Mappings({@Mapping(source = "id", target = "id"), @Mapping(source = "projectInfo.title", target = "title"),
    @Mapping(source = "project.flagships", target = "flagships"),
    @Mapping(source = "project.regions", target = "regionalPrograms"),
    @Mapping(source = "project.projectInfo.statusName", target = "status"),
    @Mapping(source = "project.projectInfo.crossCuttingCapacity", target = "capdev"),
    @Mapping(source = "project.projectInfo.crossCuttingClimate", target = "climateChange"),
    @Mapping(source = "project.projectInfo.crossCuttingGender", target = "gender"),
    @Mapping(source = "project.projectInfo.crossCuttingYouth", target = "youth")})
  public abstract CrpGeoLocationMapProjectsDTO projectToCrpGeoLocationMapProjectsDTO(Project project);


}
