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
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mapper(componentModel = "jsr330", uses = {InstitutionTypeMapper.class, InstitutionLocationMapper.class})
public abstract class InstitutionMapper {

  private static final Logger LOG = LoggerFactory.getLogger(InstitutionMapper.class);

  public abstract Institution institutionDTOToInstitution(InstitutionDTO institutionDTO);

  public abstract InstitutionDTO institutionToInstitutionDTO(Institution institution);

  public abstract Institution updateInstitutionFromInstitutionDto(InstitutionDTO institutionDTO,
    @MappingTarget Institution institution);


  @AfterMapping
  protected void updateInstitutionLocationWithInstitution(InstitutionDTO institutionDTO,
    @MappingTarget Institution institution) {

    LOG.debug("Adding institution to institutionLocations : ", institution);
    institution.getInstitutionsLocations().forEach(location -> location.setInstitution(institution));
  }


}
