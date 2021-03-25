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

import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPageDeliverablesDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "jsr330", uses = {DeliverableInfoMapper.class})
public abstract interface DeliverablesMapper {

  @Mappings({@Mapping(source = "deliverableInfo.title", target = "title"),
    @Mapping(source = "deliverableInfo.year", target = "year"), @Mapping(source = "isFindable", target = "findable"),
    @Mapping(source = "isAccesible", target = "accesible"),
    @Mapping(source = "isInteroperable", target = "interoperable"),
    @Mapping(source = "isReusable", target = "reusable"),
    @Mapping(source = "deliverableInfo.deliverableType.name", target = "subcategory"),
    @Mapping(source = "dissemination.disseminationUrl", target = "externalLink")})
  public abstract ProjectPageDeliverablesDTO deliverableToProjectPageDeliverablesDTO(Deliverable deliverable);

}
