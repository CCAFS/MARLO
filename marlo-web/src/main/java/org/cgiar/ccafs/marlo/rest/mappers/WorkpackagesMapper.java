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

import org.cgiar.ccafs.marlo.data.model.OneCGIARWorkpackageImpactArea;
import org.cgiar.ccafs.marlo.data.model.OneCGIARWorkpackageScienceGroup;
import org.cgiar.ccafs.marlo.data.model.OneCGIARWorkpackageSdg;
import org.cgiar.ccafs.marlo.rest.dto.ImpactAreasDTO;
import org.cgiar.ccafs.marlo.rest.dto.SDGsDTO;
import org.cgiar.ccafs.marlo.rest.dto.ScienceGroupDTO;
import org.cgiar.ccafs.marlo.rest.dto.WorkPackagesDTO;
import org.cgiar.ccafs.marlo.rest.services.submissionTools.workpackages.Workpackage;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "jsr330", uses = {OneCGIARScienceGroupMapper.class})
public interface WorkpackagesMapper {

  @Mappings({@Mapping(source = "impactArea.id", target = "id"),
    @Mapping(source = "impactArea.description", target = "description"),
    @Mapping(source = "impactArea.financialCode", target = "financialCode"),
    @Mapping(source = "impactArea.name", target = "name")})
  public ImpactAreasDTO
    oneCGIARWorkpackageImpactAreaToImpactAreasDTO(OneCGIARWorkpackageImpactArea oneCGIARWorkpackageImpactArea);

  @Mappings({@Mapping(source = "scienceGroup.id", target = "code"),
    @Mapping(source = "scienceGroup.description", target = "description"),
    @Mapping(source = "scienceGroup.financialCode", target = "financialCode"),
    @Mapping(source = "scienceGroup.parentScienceGroup", target = "parent")})
  public ScienceGroupDTO
    oneCGIARWorkpackageScienceGroupToScienceGroupDTO(OneCGIARWorkpackageScienceGroup oneCGIARWorkpackageScienceGroup);


  @Mappings({@Mapping(source = "sdg.smoCode", target = "usndCode"),
    @Mapping(source = "sdg.shortName", target = "shortName"),
    @Mapping(source = "sdg.financialCode", target = "financialCode"),
    @Mapping(source = "sdg.fullName", target = "fullName")})
  public SDGsDTO oneCGIARWorkpackageSdgToSDGsDTO(OneCGIARWorkpackageSdg oneCGIARWorkpackageSdg);

  @Mappings({@Mapping(source = "scienceGroupList", target = "scienceGroupList"),
    @Mapping(source = "sdgList", target = "sdgList"), @Mapping(source = "impactAreaList", target = "impactAreaList")})
  public abstract WorkPackagesDTO oneCGIARWorkpackageToWorkPackagesDTO(
    org.cgiar.ccafs.marlo.rest.services.submissionTools.onecgiarworkpackages.Workpackage workpackage);

  @Mappings({@Mapping(source = "scienceGroupList", target = "scienceGroupList"),
    @Mapping(source = "sdgList", target = "sdgList"), @Mapping(source = "impactAreaList", target = "impactAreaList")})
  public abstract WorkPackagesDTO workpackageToWorkPackagesDTO(Workpackage workpackage);


}
