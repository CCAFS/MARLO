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
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectLocation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyRegion;
import org.cgiar.ccafs.marlo.rest.dto.CountryDTO;
import org.cgiar.ccafs.marlo.rest.dto.LocElementDTO;
import org.cgiar.ccafs.marlo.rest.dto.ParentRegionDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPageCountriesDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPageRegionsDTO;
import org.cgiar.ccafs.marlo.rest.dto.RegionDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Mapper(componentModel = "jsr330")

public interface LocationMapper {

  @Mappings({@Mapping(source = "code", target = "isoNumeric")})
  public abstract LocElement countryDTOToLocElement(CountryDTO countryDTO);

  @Mappings({@Mapping(source = "locElement.isoNumeric", target = "id"),
    @Mapping(source = "locElement.name", target = "name"),
    @Mapping(source = "locElement.isoAlpha2", target = "isoAlpha2")})
  public abstract LocElementDTO institutionLocationToLocElementDTO(InstitutionLocation instututionLocation);

  @Mappings({@Mapping(source = "isoNumeric", target = "code"), @Mapping(source = "locElement", target = "regionDTO")})
  public abstract CountryDTO locElementToCountryDTO(LocElement locElement);

  @Mappings({@Mapping(source = "isoNumeric", target = "UM49Code")})
  public abstract ParentRegionDTO locElementToParentRegionDTO(LocElement regElement);

  @Mappings({@Mapping(source = "isoNumeric", target = "UM49Code"),
    @Mapping(source = "locElement", target = "parentRegion")})
  public abstract RegionDTO locElementToRegionDTO(LocElement regElement);

  @Mappings({@Mapping(source = "projectExpectedStudyCountry.locElement.isoNumeric", target = "code"),
    @Mapping(source = "projectExpectedStudyCountry.locElement.isoAlpha2", target = "isoAlpha2"),
    @Mapping(source = "projectExpectedStudyCountry.locElement.name", target = "name"),
    @Mapping(source = "projectExpectedStudyCountry.locElement.locElement", target = "regionDTO")})
  public abstract CountryDTO
    ProjectExpectedStudyCountryToCountryDTO(ProjectExpectedStudyCountry projectExpectedStudyCountry);

  @Mappings({@Mapping(source = "projectExpectedStudyRegion.locElement.isoNumeric", target = "UM49Code"),
    @Mapping(source = "projectExpectedStudyRegion.locElement.name", target = "name"),
    @Mapping(source = "projectExpectedStudyRegion.locElement.locElement", target = "parentRegion")})
  public abstract RegionDTO
    projectExpectedStudyRegionToRegionDTO(ProjectExpectedStudyRegion projectExpectedStudyRegion);

  @Mappings({@Mapping(source = "projectInnovationCountry.locElement.isoNumeric", target = "code"),
    @Mapping(source = "projectInnovationCountry.locElement.isoAlpha2", target = "isoAlpha2"),
    @Mapping(source = "projectInnovationCountry.locElement.name", target = "name"),
    @Mapping(source = "projectInnovationCountry.locElement.locElement", target = "regionDTO")})
  public abstract CountryDTO projectInnovationCountryToCountryDTO(ProjectInnovationCountry projectInnovationCountry);

  @Mappings({@Mapping(source = "locElement.isoNumeric", target = "UM49Code"),
    @Mapping(source = "locElement.name", target = "name"),
    @Mapping(source = "locElement.locElement", target = "parentRegion")})
  public abstract RegionDTO projectInnovationRegionToRegionDTO(ProjectInnovationRegion projectInnovationRegion);

  @Mappings({@Mapping(source = "locElement.isoNumeric", target = "id"),
    @Mapping(source = "locElement.name", target = "name"),
    @Mapping(source = "locElement.isoAlpha2", target = "isoAlpha2")})
  public abstract LocElementDTO projectLocationToDefaultFieldDTO(ProjectLocation projectLocation);

  @Mappings({@Mapping(source = "locElement.isoAlpha2", target = "isoAlpha2"),
    @Mapping(source = "locElement.name", target = "name")})
  public abstract ProjectPageCountriesDTO projectLocationToProjectPageCountriesDTO(ProjectLocation projectLocation);

  @Mappings({@Mapping(source = "locElement.isoNumeric", target = "UM49code"),
    @Mapping(source = "locElement.name", target = "name")})
  public abstract ProjectPageRegionsDTO projectLocationToProjectPageRegionsDTO(ProjectLocation projectLocation);

  @Mappings({@Mapping(source = "projectPolicyCountry.locElement.isoNumeric", target = "code"),
    @Mapping(source = "projectPolicyCountry.locElement.isoAlpha2", target = "isoAlpha2"),
    @Mapping(source = "projectPolicyCountry.locElement.name", target = "name"),
    @Mapping(source = "projectPolicyCountry.locElement.locElement", target = "regionDTO")})
  public abstract CountryDTO projectPolicyCountryToCountryDTO(ProjectPolicyCountry projectPolicyCountry);


  @Mappings({@Mapping(source = "projectPolicyRegion.locElement.isoNumeric", target = "UM49Code"),
    @Mapping(source = "locElement.name", target = "name"),
    @Mapping(source = "locElement.locElement", target = "parentRegion")})
  public abstract RegionDTO projectPolicyRegionToRegionDTO(ProjectPolicyRegion projectPolicyRegion);


}
