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

import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330", uses = {InstitutionMapper.class, InstitutionTypeMapper.class})
public interface ProjectExpectedStudyInstitutionMapper {

  @Mappings({@Mapping(source = "projectExpectedStudyInstitution.institution.id", target = "code"),
    @Mapping(source = "projectExpectedStudyInstitution.institution.name", target = "name"),
    @Mapping(source = "projectExpectedStudyInstitution.institution.acronym", target = "acronym"),
    @Mapping(source = "projectExpectedStudyInstitution.institution.websiteLink", target = "websiteLink"),
    @Mapping(source = "projectExpectedStudyInstitution.institution.locations", target = "countryOfficeDTO"),
    @Mapping(source = "projectExpectedStudyInstitution.institution.added", target = "added"),
    @Mapping(source = "projectExpectedStudyInstitution.institution.institutionType", target = "institutionType")})

  public abstract InstitutionDTO
    projectExpectedStudyInstitutionToInstitutionDTO(ProjectExpectedStudyInstitution projectExpectedStudyInstitution);

}
