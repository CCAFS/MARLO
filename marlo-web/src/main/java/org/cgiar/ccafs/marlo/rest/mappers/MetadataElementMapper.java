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

import org.cgiar.ccafs.marlo.data.model.MetadataElement;
import org.cgiar.ccafs.marlo.rest.dto.MetadataElementDTO;

import java.util.List;

import org.mapstruct.Mapper;

@Mapper(componentModel = "jsr330")
public abstract interface MetadataElementMapper {

  public abstract List<MetadataElementDTO>
    metadataElementListToMetadataElementDTOList(List<MetadataElement> metadataElementlist);

  public abstract MetadataElementDTO metadataElementToMetadataElementDTO(MetadataElement metadataElement);
}
