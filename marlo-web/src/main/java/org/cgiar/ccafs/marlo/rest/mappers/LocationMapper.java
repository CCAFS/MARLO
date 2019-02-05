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

import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.rest.dto.CountryDTO;
import org.cgiar.ccafs.marlo.rest.dto.ParentRegionDTO;
import org.cgiar.ccafs.marlo.rest.dto.RegionDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Mapper(componentModel = "jsr330")

public interface LocationMapper {
	@Mappings({ @Mapping(source = "code", target = "isoNumeric") })
	public abstract LocElement countryDTOToLocElement(CountryDTO countryDTO);

	@Mappings({ @Mapping(source = "isoNumeric", target = "code"),
			@Mapping(source = "locElement", target = "regionDTO") })
	public abstract CountryDTO locElementToCountryDTO(LocElement locElement);

	@Mappings({ @Mapping(source = "isoNumeric", target = "UM49Code") })
	public abstract ParentRegionDTO locElementToParentRegionDTO(LocElement regElement);

	@Mappings({ @Mapping(source = "isoNumeric", target = "UM49Code"),
			@Mapping(source = "locElement", target = "parentRegion") })
	public abstract RegionDTO locElementToRegionDTO(LocElement regElement);

	/*
	 * public abstract LocElement updateLocElementFromLocElementDto(CountryDTO
	 * locElementDTO,
	 * 
	 * @MappingTarget LocElement locElement);
	 */

}
