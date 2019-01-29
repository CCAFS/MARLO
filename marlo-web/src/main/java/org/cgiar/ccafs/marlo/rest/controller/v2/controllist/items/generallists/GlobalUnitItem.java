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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.rest.dto.GlobalUnitDTO;
import org.cgiar.ccafs.marlo.rest.mappers.GlobalUnitMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class GlobalUnitItem<T> {

	private GlobalUnitManager globalUnitManager;
	private GlobalUnitMapper globalUnitMapper;

	@Inject
	public GlobalUnitItem(GlobalUnitManager globalUnitManager, GlobalUnitMapper globalUnitMapper) {
		this.globalUnitManager = globalUnitManager;
		this.globalUnitMapper = globalUnitMapper;
	}

	/**
	 * Find a Global Unit by Acronym
	 * 
	 * @param CRP Acronym
	 * @return a GlobalUnitDTO with the smoCode
	 */
	public ResponseEntity<GlobalUnitDTO> findGlobalUnitByAcronym(String acronym) {
		GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(acronym);
		return Optional.ofNullable(globalUnitEntity).map(this.globalUnitMapper::globalUnitToGlobalUnitDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Find a Global Unit by CGIAR code
	 * 
	 * @param CGIAR id
	 * @return a GlobalUnitDTO with the smoCode
	 */
	public ResponseEntity<GlobalUnitDTO> findGlobalUnitByCGIRARId(String smoCode) {
		GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitBySMOCode(smoCode);
		return Optional.ofNullable(globalUnitEntity).map(this.globalUnitMapper::globalUnitToGlobalUnitDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Get All the Global units
	 * 
	 * @return a List of global units
	 */
	public ResponseEntity<List<GlobalUnitDTO>> getAllGlobaUnits(Long typeId) {
		//
		List<GlobalUnit> globalUnits;

		if (this.globalUnitManager.findAll() != null) {
			if (typeId != null) {
				globalUnits = new ArrayList<>(this.globalUnitManager.findAll().stream()
						.filter(c -> c.isActive() && c.getGlobalUnitType().getId() == typeId)
						.collect(Collectors.toList()));
			} else {
				globalUnits = this.globalUnitManager.findAll();
			}
			List<GlobalUnitDTO> globalUnitDTOs = globalUnits.stream()
					.map(globalUnitEntity -> this.globalUnitMapper.globalUnitToGlobalUnitDTO(globalUnitEntity))
					.collect(Collectors.toList());
			if (globalUnitDTOs == null) {
				return new ResponseEntity<List<GlobalUnitDTO>>(HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<List<GlobalUnitDTO>>(globalUnitDTOs, HttpStatus.OK);
			}

//			List<GlobalUnitDTO> globalUnitDTOs = globalUnits.stream()
//					.map(globalUnitEntity -> this.globalUnitMapper.globalUnitToGlobalUnitDTO(globalUnitEntity))
//					.collect(Collectors.toList());
//			return globalUnitDTOs;

		} else {
			return new ResponseEntity<List<GlobalUnitDTO>>(HttpStatus.NOT_FOUND);
		}
	}
}
