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

package org.cgiar.ccafs.marlo.rest.dto.mapper;

import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionLocationDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "jsr330", uses = LocElementMapper.class)
public interface InstitutionLocationMapper {

  default Institution institutionIdToInstitution(Long institutionId) {
    Institution institution = new Institution();
    institution.setId(institutionId);
    return institution;
  }

  @Mapping(source = "institutionId", target = "institution")
  public InstitutionLocation institutionLocationDTOToInstitutionLocation(InstitutionLocationDTO institutionLocationDTO);

  @Mapping(source = "institution", target = "institutionId")
  public InstitutionLocationDTO institutionLocationToInstitutionLocationDTO(InstitutionLocation institutionLocation);

  default Long institutionToInstitutionId(Institution institution) {
    return institution.getId();
  }

  @Mapping(source = "institutionId", target = "institution")
  public InstitutionLocation updateInstitutionLocationFromInstitutionLocationDto(
    InstitutionLocationDTO institutionLocationDTO, @MappingTarget InstitutionLocation institutionLocation);

}
