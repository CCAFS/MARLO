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

import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPageDTO;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

/**
 * @author Luis Fernando - CIAT/CCAFS
 */

@Mapper(componentModel = "jsr330", uses = {ProjecInfoMapper.class, DefaultFieldMapper.class})
public interface ProjectPageMapper {


  public abstract Project projectPageDTOToProject(ProjectPageDTO projectPageDTO);

  @Mappings({@Mapping(source = "project.locations", target = "countries"),
    @Mapping(source = "project.projectRegions", target = "projectRegions")})
  public abstract ProjectPageDTO projectToProjectPageDTO(Project project);

}
