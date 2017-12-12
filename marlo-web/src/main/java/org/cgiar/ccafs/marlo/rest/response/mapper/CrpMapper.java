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

package org.cgiar.ccafs.marlo.rest.response.mapper;

import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.rest.response.CrpDTO;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

/**
 * Maps Crp domain entites to CrpDTO objects. As JSON serialization frameworks will try and serialize all
 * relations (including bi-directional assocations which can lead to infinite loops unless mitigated against) we
 * either have to create DTO objects or give the JSON serialization framework instructions on which fields to
 * serialize and which ones to not serialize.
 * We use the MapStruct framework here, all we need to do is create our DTO object and our interface mapper and
 * MapStruct will create the implementation for us.
 * 
 * @author GrantL
 */
@Mapper(componentModel = "jsr330")
public interface CrpMapper {

  public Crp crpDTOToCrp(CrpDTO crpDTO);

  public CrpDTO crpToCrpDTO(Crp crp);

  public Crp updateCrpFromCrpDto(CrpDTO crpDTO, @MappingTarget Crp crp);

}
