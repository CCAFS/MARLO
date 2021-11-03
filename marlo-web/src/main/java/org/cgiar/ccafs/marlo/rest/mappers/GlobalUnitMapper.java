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
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationCrp;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;

/**
 * Maps Crp domain entites to GlobalUnitDTO objects. As JSON serialization
 * frameworks will try and serialize all relations (including bi-directional
 * assocations which can lead to infinite loops unless mitigated against) we
 * either have to create DTO objects or give the JSON serialization framework
 * instructions on which fields to serialize and which ones to not serialize. We
 * use the MapStruct framework here, all we need to do is create our DTO object
 * and our interface mapper and MapStruct will create the implementation for us.
 * 
 * @author GrantL
 */
@Mapper(componentModel = "jsr330", uses = {GlobalUnitTypeMapper.class})
public interface GlobalUnitMapper {

  public GlobalUnit globalUnitDTOToGlobalUnit(CGIAREntityDTO globalUnitDTO);

  @Mappings({@Mapping(source = "globalUnit.smoCode", target = "code"),
    @Mapping(source = "globalUnitType", target = "cgiarEntityTypeDTO")})
  public CGIAREntityDTO globalUnitToGlobalUnitDTO(GlobalUnit globalUnit);


  @Mappings({@Mapping(source = "globalUnit.smoCode", target = "code"),
    @Mapping(source = "globalUnit.name", target = "name"), @Mapping(source = "globalUnit.acronym", target = "acronym"),
    @Mapping(source = "globalUnit.globalUnitType", target = "cgiarEntityTypeDTO"),})
  public CGIAREntityDTO projectInnovationCrpToCGIAREntityDTO(ProjectInnovationCrp projectInnovationCrp);


  public GlobalUnit updateGlobalUnitFromGlobalUnitDto(CGIAREntityDTO globalUnitDTO,
    @MappingTarget GlobalUnit globalUnit);


}
