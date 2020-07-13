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

import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPageDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Luis Fernando - CIAT/CCAFS
 * @author Diego Perez - CIAT/CCAFS
 */

@Mapper(componentModel = "jsr330",
  uses = {ProjecInfoMapper.class, LocationMapper.class, DeliverablesMapper.class, ProjectPolicyMapper.class,
    InnovationMapper.class, ProjectOutcomesMapper.class, ProjectPartnerMapper.class, ProjectExpectedStudyMapper.class})
public interface ProjectPageMapper {


  public abstract Project projectPageDTOToProject(ProjectPageDTO projectPageDTO);

  @Mappings({@Mapping(source = "project.locations", target = "projectCountries"),
    @Mapping(source = "project.projectRegions", target = "projectRegions"),
    @Mapping(source = "project.projectActivities", target = "activities"),
    @Mapping(source = "project.projectDeliverables", target = "deliverablesList"),
    @Mapping(source = "project.policies", target = "policiesList"),
    @Mapping(source = "project.innovations", target = "innovationsList"),
    @Mapping(source = "project.outcomes", target = "outcomesContributing"),
    @Mapping(source = "project.partners", target = "partnersList"),
    @Mapping(source = "project.expectedStudies", target = "outcomeImpactCaseReports"),
    @Mapping(expression = "java(project.getProjectDeliverables().size())", target = "numberDeliverables"),
    @Mapping(expression = "java(project.getPolicies().size())", target = "numberPolicies"),
    @Mapping(expression = "java(project.getPartners().size())", target = "numberPartners"),
    @Mapping(expression = "java(project.getExpectedStudies().size())", target = "numberStudies"),
    @Mapping(expression = "java(project.getInnovations().size())", target = "numberInnovations")})
  public abstract ProjectPageDTO projectToProjectPageDTO(Project project);

}
