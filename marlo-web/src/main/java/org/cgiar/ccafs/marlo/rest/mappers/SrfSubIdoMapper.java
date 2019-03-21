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

import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.rest.dto.SrfSubIdoDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

@Mapper(componentModel = "jsr330", uses = { SrfIdoMapper.class })
public interface SrfSubIdoMapper {

//
//	@Mappings({ @Mapping(source = "srfIdo.smoCode", target = "code") })
//	public abstract SrfIdoDTO srfIdoToSrfIdoDTO(SrfIdo srfIdo);

//	@Mappings({ @Mapping(source = "srfSubIdo.smoCode", target = "code"),
//			@Mapping(source = "srfSubIdo.description", target = "description"),
//			@Mapping(source = "srfSubIdo.srfIdo", target = "srfIdoDTO") })
//	public abstract SrfSubIdoDTO crpOutcomeSubIdoToSrfSubIdoDTO(CrpOutcomeSubIdo crpOutcomeSubIdo);
//
//	public abstract CrpOutcomeSubIdo srfSubIdoDTOToCrpOutcomeSubIdo(SrfSubIdoDTO srfSubIdoDTO);

	public abstract SrfSubIdo srfSubIdoDTOToSrfSubIdo(SrfSubIdoDTO srfSubIdoDTO);

	@Mappings({ @Mapping(source = "srfSubIdo.smoCode", target = "code"),
			@Mapping(source = "srfSubIdo.srfIdo.smoCode", target = "srfIdoDTO.code") })
	public abstract SrfSubIdoDTO srfSubIdoToSrfSubIdoDTO(SrfSubIdo srfSubIdo);
}
