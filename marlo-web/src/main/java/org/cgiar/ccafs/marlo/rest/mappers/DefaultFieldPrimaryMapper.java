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

import org.cgiar.ccafs.marlo.data.model.PolicyMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationSubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;
import org.cgiar.ccafs.marlo.rest.dto.DefaultFieldPrimaryDTO;
import org.cgiar.ccafs.marlo.rest.dto.MilestoneNameDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330")
public interface DefaultFieldPrimaryMapper {

  @Mappings({@Mapping(source = "crpMilestone.composeID", target = "id"),
    @Mapping(source = "crpMilestone.title", target = "name"), @Mapping(source = "primary", target = "primary")})
  public abstract MilestoneNameDTO policyMilestoneToMilestoneNameDTO(PolicyMilestone policyMilestone);

  // **************** Studies

  @Mappings({@Mapping(source = "crpMilestone.composeID", target = "id"),
    @Mapping(source = "crpMilestone.title", target = "name"), @Mapping(source = "primary", target = "primary")})
  public abstract MilestoneNameDTO
    projectExpectedStudyMilestoneToMilestoneNameDTO(ProjectExpectedStudyMilestone projectExpectedStudyMilestone);

  @Mappings({@Mapping(source = "srfSubIdo.smoCode", target = "id"),
    @Mapping(source = "srfSubIdo.description", target = "name"), @Mapping(source = "primary", target = "primary")})
  public abstract DefaultFieldPrimaryDTO
    projectExpectedStudySubIdoToDefaultFieldPrimaryDTO(ProjectExpectedStudySubIdo projectExpectedStudySubIdo);


  // ****************Innovations

  @Mappings({@Mapping(source = "crpMilestone.composeID", target = "id"),
    @Mapping(source = "crpMilestone.title", target = "name"), @Mapping(source = "primary", target = "primary")})
  public abstract MilestoneNameDTO
    projectInnovationMilestoneToMilestoneNameDTO(ProjectInnovationMilestone projectInnovationMilestone);

  @Mappings({@Mapping(source = "srfSubIdo.smoCode", target = "id"),
    @Mapping(source = "srfSubIdo.description", target = "name"), @Mapping(source = "primary", target = "primary")})
  public abstract DefaultFieldPrimaryDTO
    projectInnovationSubIdoToDefaultFieldPrimaryDTO(ProjectInnovationSubIdo projectInnovationSubIdo);


  // ****************Policies


  @Mappings({@Mapping(source = "srfSubIdo.smoCode", target = "id"),
    @Mapping(source = "srfSubIdo.description", target = "name"), @Mapping(source = "primary", target = "primary")})
  public abstract DefaultFieldPrimaryDTO
    projectPolicySubIdoToDefaultFieldPrimaryDTO(ProjectPolicySubIdo projectPolicySubIdo);


}
