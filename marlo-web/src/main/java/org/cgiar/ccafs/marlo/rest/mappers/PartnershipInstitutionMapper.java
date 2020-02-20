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

import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalInstitution;
import org.cgiar.ccafs.marlo.rest.dto.CountryOfficeDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Mapper(componentModel = "jsr330", uses = {LocationMapper.class, InstitutionTypeMapper.class})
public interface PartnershipInstitutionMapper {

  @Mappings({@Mapping(source = "headquater", target = "isHeadquarter"),
    @Mapping(source = "locElement.isoNumeric", target = "code"),
    @Mapping(source = "locElement.isoAlpha2.", target = "isoAlpha2"),
    @Mapping(source = "locElement.name.", target = "name"),
    @Mapping(source = "locElement.locElement.", target = "regionDTO")})
  public abstract CountryOfficeDTO
    institutionLocationToInstitutionOfficeCountryDTO(InstitutionLocation institutionLocation);

  @Mappings({@Mapping(source = "institution.id", target = "code"),
    @Mapping(source = "institution.name", target = "name"),
    @Mapping(source = "institution.acronym", target = "acronym"),
    @Mapping(source = "institution.websiteLink", target = "websiteLink"),
    @Mapping(source = "institution.added", target = "added"),
    @Mapping(source = "institution.institutionType", target = "institutionType"),
    @Mapping(source = "institution.institutionsLocations", target = "countryOfficeDTO")})
  public abstract InstitutionDTO reportSynthesisKeyPartnershipExternalInstitutionToInstitutionDTO(
    ReportSynthesisKeyPartnershipExternalInstitution keyPartnershipExternalInstitution);
}
