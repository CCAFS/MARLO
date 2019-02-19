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

import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.rest.dto.CrpProgramDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewFlagshipDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Mapper(componentModel = "jsr330")
public interface CrpProgramMapper {

	@Mappings({ @Mapping(source = "code", target = "id") })
	public abstract CrpProgram crpProgramDTOToCrpProgram(CrpProgramDTO crpProgramDTO);

	@Mappings({ @Mapping(source = "id", target = "code") })
	public abstract CrpProgramDTO crpProgramToCrpProgramDTO(CrpProgram crpProgram);

	public abstract CrpProgram newFlagshipDTOToCrpProgram(NewFlagshipDTO newFlagshipDTO);

	public abstract CrpProgram updateCrpProgramFromCrpProgramDto(CrpProgramDTO crpProgramDTO,
			@MappingTarget CrpProgram crpProgram);

}
