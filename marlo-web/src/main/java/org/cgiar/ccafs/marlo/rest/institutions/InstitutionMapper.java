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

package org.cgiar.ccafs.marlo.rest.institutions;

import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.institutions.dto.InstitutionDTO;
import org.cgiar.ccafs.marlo.rest.institutions.dto.InstitutionLocationDTO;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mapper(componentModel = "jsr330", uses = {InstitutionTypeMapper.class, InstitutionLocationMapper.class})
public abstract class InstitutionMapper {

  private static final Logger LOG = LoggerFactory.getLogger(InstitutionMapper.class);

  // public abstract Institution institutionDTOToInstitution(InstitutionDTO institutionDTO);

  @Mappings({@Mapping(target = "partnerName", source = "institutionDTO.name"),
    @Mapping(target = "acronym", source = "institutionDTO.acronym"),
    @Mapping(target = "webPage", source = "institutionDTO.websiteLink"), @Mapping(target = "id", ignore = true),
    @Mapping(target = "crp", source = "globalUnit"), @Mapping(target = "locElement", source = "locElement"),
    @Mapping(target = "createdBy", source = "requestor"), @Mapping(target = "modifiedBy", source = "requestor"),
    @Mapping(target = "modificationJustification", constant = ""),
    @Mapping(target = "activeSince", source = "institutionDTO.added"), @Mapping(target = "active", constant = "true"),
    @Mapping(target = "office", constant = "false"), @Mapping(target = "requestSource", constant = "rest api")})
  public abstract PartnerRequest institutionDTOToPartnerRequest(InstitutionDTO institutionDTO, GlobalUnit globalUnit,
    LocElement locElement, User requestor);

  public abstract InstitutionDTO institutionToInstitutionDTO(Institution institution);

  public List<InstitutionLocationDTO> locElementToInstitutionLocations(LocElement locElement) {
    List<InstitutionLocationDTO> institutionLocations = new ArrayList<>();

    InstitutionLocationDTO instituionLocationDTO = new InstitutionLocationDTO();
    instituionLocationDTO.setCountryIsoAlpha2Code(locElement.getIsoAlpha2());
    instituionLocationDTO.setCountryName(locElement.getName());
    institutionLocations.add(instituionLocationDTO);
    return institutionLocations;

  }


  @Mappings({@Mapping(target = "name", source = "partnerName"), @Mapping(target = "websiteLink", source = "webPage"),
    @Mapping(target = "id", ignore = true), @Mapping(target = "institutionsLocations", source = "locElement")})
  public abstract InstitutionDTO partnerRequestToInstitutionDTO(PartnerRequest partnerRequest);

  // Ignore InstitutionLocations for updates
  @Mapping(target = "institutionsLocations", ignore = true)
  public abstract Institution updateInstitutionFromInstitutionDto(InstitutionDTO institutionDTO,
    @MappingTarget Institution institution);

  @AfterMapping
  protected void updateInstitutionLocationWithInstitution(InstitutionDTO institutionDTO,
    @MappingTarget Institution institution) {

    LOG.debug("Adding institution to institutionLocations : ", institution);
    institution.getInstitutionsLocations().forEach(location -> location.setInstitution(institution));
  }


}
