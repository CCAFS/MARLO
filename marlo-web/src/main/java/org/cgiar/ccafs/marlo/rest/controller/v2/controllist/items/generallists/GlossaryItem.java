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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists;

import org.cgiar.ccafs.marlo.data.manager.GlossaryManager;
import org.cgiar.ccafs.marlo.data.model.Glossary;
import org.cgiar.ccafs.marlo.rest.dto.GlossaryDTO;
import org.cgiar.ccafs.marlo.rest.mappers.GlossaryMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class GlossaryItem<T> {

  private GlossaryManager glossaryManager;
  private GlossaryMapper glossaryMapper;

  @Inject
  public GlossaryItem(GlossaryManager glossaryManager, GlossaryMapper glossaryMapper) {
    this.glossaryManager = glossaryManager;
    this.glossaryMapper = glossaryMapper;
  }

  public ResponseEntity<GlossaryDTO> findGlossaryById(Long id) {
    Glossary glossary = this.glossaryManager.getGlossaryById(id);

    if (glossary != null && (glossary.getId() == null || (!glossary.isActive()))) {
      glossary = null;
    }

    return Optional.ofNullable(glossary).map(this.glossaryMapper::glossaryToGlossaryDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Get All glossary Items *
   * 
   * @return a List of GlossaryDTO with all glossary Items.
   */
  public List<GlossaryDTO> getAllGlossary() {
    if (this.glossaryManager.getAll() != null) {
      List<Glossary> glossaryList = new ArrayList<>(this.glossaryManager.getAll());
      List<GlossaryDTO> glossaryListDTO = glossaryList.stream()
        .filter(g -> g != null && g.getId() != null && g.isActive())
        .sorted(Comparator.comparing(Glossary::getTitle, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
        .map(glossaryEntity -> this.glossaryMapper.glossaryToGlossaryDTO(glossaryEntity)).collect(Collectors.toList());
      return glossaryListDTO;
    } else {
      return null;
    }
  }


}
