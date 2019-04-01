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

import org.cgiar.ccafs.marlo.data.model.RepIndDegreeInnovation;
import org.cgiar.ccafs.marlo.rest.dto.DegreeOfInnovationDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

@Mapper(componentModel = "jsr330")
public interface DegreeOfInnovationMapper {

  @Mappings({@Mapping(source = "code", target = "id")})
  public abstract RepIndDegreeInnovation
    degreeOfInnovationDTOToRepIndDegreeInnovation(DegreeOfInnovationDTO degreeOfInnovationDTO);

  @Mappings({@Mapping(source = "id", target = "code")})
  public abstract DegreeOfInnovationDTO
    repIndDegreeInnovationToDegreeOfInnovationDTO(RepIndDegreeInnovation repIndDegreeInnovation);

}
