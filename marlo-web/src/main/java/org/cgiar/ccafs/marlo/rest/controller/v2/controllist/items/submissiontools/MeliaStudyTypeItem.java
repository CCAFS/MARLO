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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools;

import org.cgiar.ccafs.marlo.data.manager.OneCGIARStudyTypeManager;
import org.cgiar.ccafs.marlo.data.model.OneCGIARStudyType;
import org.cgiar.ccafs.marlo.rest.dto.OneCGIARStudyTypeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.OneCGIARStudyTypeMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class MeliaStudyTypeItem<T> {

  private OneCGIARStudyTypeManager oneCGIARStudyTypeManager;

  private OneCGIARStudyTypeMapper oneCGIARStudyTypeMapper;

  @Inject
  public MeliaStudyTypeItem(OneCGIARStudyTypeManager oneCGIARStudyTypeManager,
    OneCGIARStudyTypeMapper oneCGIARStudyTypeMapper) {
    super();
    this.oneCGIARStudyTypeManager = oneCGIARStudyTypeManager;
    this.oneCGIARStudyTypeMapper = oneCGIARStudyTypeMapper;
  }

  public List<OneCGIARStudyTypeDTO> getAllStudyTypes() {
    if (this.oneCGIARStudyTypeManager.getAll() != null) {
      List<OneCGIARStudyType> studyTypes = new ArrayList<>(this.oneCGIARStudyTypeManager.getAll());
      List<OneCGIARStudyTypeDTO> oneCGIARStudyTypeDTOs = studyTypes.stream()
        .map(studyTypesEntity -> this.oneCGIARStudyTypeMapper.oneCGIARStudyTypeToOneCGIARStudyTypeDTO(studyTypesEntity))
        .collect(Collectors.toList());
      return oneCGIARStudyTypeDTOs;
    } else {
      return null;
    }
  }


}
