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
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganization;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.CountryOfficeDTO;
import org.cgiar.ccafs.marlo.rest.dto.CountryOfficeRequestDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionRequestDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionSimpleDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewCountryOfficeRequestDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewInstitutionDTO;

import java.util.Date;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mapper(componentModel = "jsr330", uses = {LocationMapper.class, InstitutionTypeMapper.class}, imports = Date.class)
public abstract class InstitutionMapper {

  private static final Logger LOG = LoggerFactory.getLogger(InstitutionMapper.class);

  @AfterMapping
  void afterMapping(@MappingTarget InstitutionRequestDTO institutionRequestDTO, PartnerRequest partnerRequest) {
    if (partnerRequest.getAcepted() == null) {
      institutionRequestDTO.setRequestStatus("PENDING");
      institutionRequestDTO.setRequestJustification("");
    } else {
      switch (partnerRequest.getAcepted().toString()) {
        case "true":
          institutionRequestDTO.setRequestStatus("APPROVED");
          institutionRequestDTO.setRequestJustification(partnerRequest.getModificationJustification());
          break;
        case "false":
          institutionRequestDTO.setRequestStatus("REJECTED");
          institutionRequestDTO.setRequestJustification(partnerRequest.getRejectJustification());
          break;
        default:
          institutionRequestDTO.setRequestStatus("ERROR");

      }
    }
  }

  @Mappings({@Mapping(source = "headquater", target = "isHeadquarter"),
    @Mapping(source = "locElement.isoNumeric", target = "code"),
    @Mapping(source = "locElement.isoAlpha2.", target = "isoAlpha2"),
    @Mapping(source = "locElement.name.", target = "name"),
    @Mapping(source = "locElement.locElement.", target = "regionDTO")})
  public abstract CountryOfficeDTO
    institutionLocationToInstitutionOfficeCountryDTO(InstitutionLocation institutionLocation);

  @Mappings({@Mapping(source = "id", target = "code"),
    @Mapping(source = "institutionsLocations", target = "countryOfficeDTO")})
  public abstract InstitutionDTO institutionToInstitutionDTO(Institution institution);

  @Mappings({@Mapping(source = "id", target = "code"), @Mapping(source = "type", target = "institutionType"),
    @Mapping(source = "typeId", target = "institutionTypeId")})
  public abstract InstitutionSimpleDTO institutionToInstitutionSimpleDTO(Institution institution);

  @Mappings({@Mapping(source = "locElement", target = "locElement"), @Mapping(source = "globalUnit", target = "crp"),
    @Mapping(source = "user", target = "createdBy"), @Mapping(source = "user", target = "modifiedBy"),
    @Mapping(target = "office", constant = "true"), @Mapping(target = "modificationJustification", constant = "0"),
    @Mapping(target = "id", constant = "0"), @Mapping(target = "active", constant = "1"),
    @Mapping(target = "requestSource", constant = "REST API"),
    @Mapping(target = "activeSince", expression = "java(new Date())"),
    @Mapping(target = "institution", source = "institution"), @Mapping(target = "acronym", expression = "java(null)"),
    @Mapping(target = "institutionType", expression = "java(null)")})
  public abstract PartnerRequest NewCountryOfficeRequestDTOToPartnerRequest(
    NewCountryOfficeRequestDTO newCountryOfficeRequestDTO, GlobalUnit globalUnit, LocElement locElement,
    Institution institution, User user);


  @Mappings({@Mapping(source = "locElement", target = "locElement"),
    @Mapping(source = "institutionType", target = "institutionType"),
    @Mapping(source = "newInstitutionDTO.name", target = "partnerName"),
    @Mapping(source = "newInstitutionDTO.acronym", target = "acronym"),
    @Mapping(source = "newInstitutionDTO.websiteLink", target = "webPage"),
    @Mapping(source = "globalUnit", target = "crp"), @Mapping(source = "user", target = "createdBy"),
    @Mapping(source = "user", target = "modifiedBy"), @Mapping(target = "office", constant = "false"),
    @Mapping(target = "modificationJustification", constant = "0"), @Mapping(target = "id", constant = "0"),
    @Mapping(target = "active", constant = "1"), @Mapping(target = "requestSource", constant = "REST API"),
    @Mapping(target = "activeSince", expression = "java(new Date())"),
    @Mapping(target = "institution", expression = "java(null)")})
  public abstract PartnerRequest newInstitutionDTOToPartnerRequest(NewInstitutionDTO newInstitutionDTO,
    GlobalUnit globalUnit, LocElement locElement, InstitutionType institutionType, User user);


  @Mappings({@Mapping(source = "locElement", target = "countryDTO"),
    @Mapping(source = "institution", target = "institutionDTO"), @Mapping(source = "acepted", target = "isAcepted")})
  public abstract CountryOfficeRequestDTO PartnerRequestToCountryOfficeRequestDTO(PartnerRequest PartnerRequest);

  @Mappings({@Mapping(source = "locElement", target = "countryDTO"),
    @Mapping(source = "institution", target = "institutionDTO"),
    @Mapping(source = "institutionType", target = "institutionTypeDTO")})
  public abstract InstitutionRequestDTO partnerRequestToInstitutionRequestDTO(PartnerRequest PartnerRequest);


  @Mappings({@Mapping(source = "institution.id", target = "code"),
    @Mapping(source = "institution.name", target = "name"),
    @Mapping(source = "institution.acronym", target = "acronym"),
    @Mapping(source = "institution.websiteLink", target = "websiteLink"),
    @Mapping(source = "institution.added", target = "added"),
    @Mapping(source = "institution.institutionType", target = "institutionType"),
    @Mapping(source = "institution.institutionsLocations", target = "countryOfficeDTO")})
  public abstract InstitutionDTO projectInnovationContributingOrganizationToInstitutionDTO(
    ProjectInnovationContributingOrganization projectInnovationContributingOrganization);


}
