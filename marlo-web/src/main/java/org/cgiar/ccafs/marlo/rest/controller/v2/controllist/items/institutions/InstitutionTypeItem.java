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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.institutions;

import org.cgiar.ccafs.marlo.data.manager.InstitutionTypeManager;
import org.cgiar.ccafs.marlo.data.model.InstitutionType;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionTypeDTO;
import org.cgiar.ccafs.marlo.rest.mappers.InstitutionTypeMapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class InstitutionTypeItem<T> {

	private InstitutionTypeManager institutionTypeManager;
	private InstitutionTypeMapper institutionTypeMapper;

	@Inject
	public InstitutionTypeItem(InstitutionTypeManager institutionTypeManager,
			InstitutionTypeMapper institutionTypeMapper) {
		this.institutionTypeManager = institutionTypeManager;
		this.institutionTypeMapper = institutionTypeMapper;
	}

	/**
	 * Find a institution type requesting a MARLO id
	 * 
	 * @param id
	 * @return a InstitutionType with the Institution Type data.
	 */
	public ResponseEntity<InstitutionTypeDTO> findInstitutionTypeById(Long id) {
		InstitutionType institutionType = this.institutionTypeManager.getInstitutionTypeById(id);
		return Optional.ofNullable(institutionType).map(this.institutionTypeMapper::institutionTypeToInstitutionTypeDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Get All the Innovation Types Items *
	 * 
	 * @return a List of InnovationTypesDTO with all repIndInnovationType Items.
	 */
	public List<InstitutionTypeDTO> getAllInstitutionTypes() {
		List<InstitutionType> institutionTypes = this.institutionTypeManager.findAll();
		List<InstitutionTypeDTO> institutionDTOs = institutionTypes.stream()
				.map(institution -> this.institutionTypeMapper.institutionTypeToInstitutionTypeDTO(institution))
				.collect(Collectors.toList());
		return institutionDTOs;
	}

}
