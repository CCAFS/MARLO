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

import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.rest.dto.CountryOfficeDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionSimple2DTO;

import java.util.Date;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330", uses = {LocationMapper.class, InstitutionTypeMapper.class}, imports = Date.class)
public interface InstitutionSimpleMapper {


  @Mappings({@Mapping(source = "headquater", target = "isHeadquarter"),
    @Mapping(source = "locElement.isoNumeric", target = "code"),
    @Mapping(source = "locElement.isoAlpha2.", target = "isoAlpha2"),
    @Mapping(source = "locElement.name.", target = "name"),
    @Mapping(source = "locElement.locElement.", target = "regionDTO")})
  public abstract CountryOfficeDTO
    institutionLocationToInstitutionOfficeCountryDTO(InstitutionLocation institutionLocation);

  @Mappings({@Mapping(source = "id", target = "code"), @Mapping(source = "locations", target = "countryOfficeDTO")})
  public abstract InstitutionSimple2DTO institutionToInstitutionSimple2DTO(Institution institution);
}
