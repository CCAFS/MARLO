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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.institutions;

import org.cgiar.ccafs.marlo.data.manager.InstitutionDictionaryManager;
import org.cgiar.ccafs.marlo.data.model.InstitutionDictionary;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionsRelatedDTO;
import org.cgiar.ccafs.marlo.rest.mappers.InstitutionRelatedMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class InstitutionRelatedItem<T> {

  private InstitutionDictionaryManager institutionDictionaryManager;
  private InstitutionRelatedMapper institutionRelatedMapper;

  @Inject
  public InstitutionRelatedItem(InstitutionDictionaryManager institutionDictionaryManager,
    InstitutionRelatedMapper institutionRelatedMapper) {
    super();
    this.institutionDictionaryManager = institutionDictionaryManager;
    this.institutionRelatedMapper = institutionRelatedMapper;
  }


  public List<InstitutionsRelatedDTO> getAllInstitutionRelated() {
    if (this.institutionDictionaryManager.getAll() != null) {
      List<InstitutionDictionary> institutionsRelated = new ArrayList<>(this.institutionDictionaryManager.getAll());
      List<InstitutionsRelatedDTO> institutionsRelatedDTOs = institutionsRelated.stream()
        .map(i -> this.institutionRelatedMapper.institutionDictionaryToInstitutionsRelatedDTO(i))
        .collect(Collectors.toList());
      return institutionsRelatedDTOs;
    } else {
      return null;
    }
  }
}
