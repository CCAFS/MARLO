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

import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;
import org.cgiar.ccafs.marlo.rest.dto.PartnerRequestDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mapper(componentModel = "jsr330", uses = LocationMapper.class)
public abstract class InstitutionMapper {
	private static final Logger LOG = LoggerFactory.getLogger(InstitutionMapper.class);

	@Mappings({ @Mapping(source = "locElement", target = "locElement"),
			@Mapping(source = "institutionDTO.institutionType", target = "institutionType"),
			@Mapping(source = "institutionDTO.name", target = "partnerName"),
			@Mapping(source = "institutionDTO.acronym", target = "acronym"),
			@Mapping(source = "institutionDTO.websiteLink", target = "webPage"),
			@Mapping(source = "globalUnit", target = "crp"), @Mapping(source = "user", target = "createdBy"),
			@Mapping(source = "user", target = "modifiedBy"), @Mapping(target = "office", constant = "0"),
			@Mapping(target = "modificationJustification", constant = "0"), @Mapping(target = "id", constant = "0"),
			@Mapping(target = "active", constant = "1"), @Mapping(target = "requestSource", constant = "REST API"),
			@Mapping(target = "activeSince", source = "institutionDTO.added"),
			@Mapping(target = "institution", expression = "java(null)") })
	public abstract PartnerRequest institutionDTOToPartnerRequest(InstitutionDTO institutionDTO, GlobalUnit globalUnit,
			LocElement locElement, User user);

	public abstract InstitutionDTO institutionToInstitutionDTO(Institution institution);

	@Mappings({ @Mapping(source = "locElement", target = "locElementDTO"),
			@Mapping(source = "institution", target = "institutionDTO"),
			@Mapping(source = "institutionType", target = "institutionTypeDTO") })
	public abstract PartnerRequestDTO partnerRequestToPartnerRequestDTO(PartnerRequest PartnerRequest);

}
