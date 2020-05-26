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
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationContributingOrganization;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyRegion;
import org.cgiar.ccafs.marlo.rest.dto.DefaultFieldDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330")
public interface DefaultFieldMapper {

  // *******Innovations**********//


  public abstract DefaultFieldDTO InstitutionToDefaultFieldDTO(Institution institution);

  @Mappings({@Mapping(source = "institution.id", target = "id"),
    @Mapping(source = "institution.name", target = "name")})
  public abstract DefaultFieldDTO ProjectInnovationContributingOrganizationToDefaultFieldDTO(
    ProjectInnovationContributingOrganization ProjectInnovationContributingOrganization);

  @Mappings({@Mapping(source = "locElement.isoNumeric", target = "id"),
    @Mapping(source = "locElement.name", target = "name")})
  public abstract DefaultFieldDTO
    projectInnovationCountryToDefaultFieldDTO(ProjectInnovationCountry projectInnovationCountry);

  @Mappings({@Mapping(source = "globalUnit.smoCode", target = "id"),
    @Mapping(source = "globalUnit.name", target = "name")})
  public abstract DefaultFieldDTO projectInnovationCrpToDefaultFieldDTO(ProjectInnovationCrp ProjectInnovationCrp);

  @Mappings({@Mapping(source = "repIndGeographicScope.id", target = "id"),
    @Mapping(source = "repIndGeographicScope.name", target = "name")})
  public abstract DefaultFieldDTO projectInnovationGeographicScopeToDefaultFieldDTO(
    ProjectInnovationGeographicScope projectInnovationGeographicScope);

  @Mappings({@Mapping(source = "locElement.isoNumeric", target = "id"),
    @Mapping(source = "locElement.name", target = "name")})
  public abstract DefaultFieldDTO
    projectInnovationRegionToDefaultFieldDTO(ProjectInnovationRegion projectInnovationRegion);


  // *****Policies*******//

  @Mappings({@Mapping(source = "locElement.isoNumeric", target = "id"),
    @Mapping(source = "locElement.name", target = "name")})
  public abstract DefaultFieldDTO projectPolicyCountryToDefaultFieldDTO(ProjectPolicyCountry projectPolicyCountry);

  @Mappings({@Mapping(source = "globalUnit.smoCode", target = "id"),
    @Mapping(source = "globalUnit.name", target = "name")})
  public abstract DefaultFieldDTO projectPolicyCrpToDefaultFieldDTO(ProjectPolicyCrp projectPolicyCrp);

  @Mappings({@Mapping(source = "repIndGeographicScope.id", target = "id"),
    @Mapping(source = "repIndGeographicScope.name", target = "name")})
  public abstract DefaultFieldDTO
    projectPolicyGeographicScopeToDefaultFieldDTO(ProjectPolicyGeographicScope projectPolicyGeographicScope);

  @Mappings({@Mapping(source = "locElement.isoNumeric", target = "id"),
    @Mapping(source = "locElement.name", target = "name")})
  public abstract DefaultFieldDTO projectPolicyRegionToDefaultFieldDTO(ProjectPolicyRegion projectPolicyRegion);

}
