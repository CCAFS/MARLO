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
 * @author Diego Fernando Perez - CIAT/CCAFS
 */


package org.cgiar.ccafs.marlo.rest.mappers;

import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPoliciesInfoDTO;

import org.mapstruct.Mapper;

@Mapper(componentModel = "jsr330", uses = {PhaseMapper.class, PolicyMaturityLevelMapper.class,
  OrganizationTypeMapper.class, PolicyInvestmentTypeMapper.class})
public interface ProjectPoliciesInfoMapper {


  public abstract ProjectPoliciesInfoDTO projectPolicyInfoToProjectPoliciesInfoDTO(ProjectPolicyInfo projectPolicyInfo);

}
