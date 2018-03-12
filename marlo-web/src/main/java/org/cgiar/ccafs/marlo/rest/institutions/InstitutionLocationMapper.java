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

import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.rest.institutions.dto.InstitutionLocationDTO;

import javax.inject.Inject;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330")
public abstract class InstitutionLocationMapper {


  /**
   * We either have to expect the REST client to know the locElementIds (e.g. by providing them a separate web service)
   * or we need to use the LocElementManager to look up the LocElement using the mandatory supplied isoAlpha2 code.
   */
  @Inject
  private LocElementManager locElementManager;

  public Institution institutionIdToInstitution(Long institutionId) {
    Institution institution = new Institution();
    institution.setId(institutionId);
    return institution;
  }

  @Mappings({@Mapping(source = "institutionId", target = "institution"),
    @Mapping(source = "countryIsoAlpha2Code", target = "locElement"), @Mapping(target = "city", ignore = true)})
  public abstract InstitutionLocation
    institutionLocationDTOToInstitutionLocation(InstitutionLocationDTO institutionLocationDTO);

  @Mappings({@Mapping(source = "institution", target = "institutionId"),
    @Mapping(source = "locElement.isoAlpha2", target = "countryIsoAlpha2Code"),
    @Mapping(source = "locElement.name", target = "countryName")})
  public abstract InstitutionLocationDTO institutionLocationToInstitutionDTO(InstitutionLocation institutionLocation);

  public Long institutionToInstitutionId(Institution institution) {
    return institution.getId();
  }

  public LocElement isoAlphaCountryCodeToLocElement(String isoAlphaCountryCode) {
    LocElement locElementByISOCode = locElementManager.getLocElementByISOCode(isoAlphaCountryCode);

    return locElementByISOCode;
  }


}
