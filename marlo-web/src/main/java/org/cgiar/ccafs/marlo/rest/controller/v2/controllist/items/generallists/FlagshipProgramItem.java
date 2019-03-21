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

import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.rest.dto.FlagshipProgramDTO;
import org.cgiar.ccafs.marlo.rest.mappers.FlagshipProgramMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */

@Named
public class FlagshipProgramItem<T> {

	private CrpProgramManager crpProgramManager;
	private FlagshipProgramMapper flagshipProgramMapper;

	@Inject
	public FlagshipProgramItem(CrpProgramManager crpProgramManager, FlagshipProgramMapper flagshipProgramMapper) {
		super();
		this.crpProgramManager = crpProgramManager;
		this.flagshipProgramMapper = flagshipProgramMapper;
	}

	/**
	 * Find a Flagship or program requesting by id
	 * 
	 * @param id
	 * @return a FlagshipProgramDTO with the flagship or program data.
	 */
	public ResponseEntity<FlagshipProgramDTO> findFlagshipProgramById(Long id) {
		CrpProgram crpProgram = this.crpProgramManager.getCrpProgramById(id);
		return Optional.ofNullable(crpProgram).map(this.flagshipProgramMapper::crpProgramToCrpProgramDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Find a Flagship or program requesting by smo Code
	 * 
	 * @param smo code
	 * @return a FlagshipProgramDTO with the flagship or program data.
	 */
	public ResponseEntity<FlagshipProgramDTO> findFlagshipProgramBySmoCode(String smoCode) {
		CrpProgram crpProgram = this.crpProgramManager.getCrpProgramBySmoCode(smoCode);
		return Optional.ofNullable(crpProgram)
				.filter(c -> c.getProgramType() == 1 && c.getCrp().getGlobalUnitType().getId() <= 3)
				.map(this.flagshipProgramMapper::crpProgramToCrpProgramDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Get All the Flagship or program Items *
	 * 
	 * @return a List of FlagshipProgramDTO with all the Flagship or program
	 * Items.
	 */
	public List<FlagshipProgramDTO> getAllCrpPrograms() {
		if (this.crpProgramManager.findAll() != null) {
			List<CrpProgram> crpPrograms = new ArrayList<>(this.crpProgramManager.findAll());
			List<FlagshipProgramDTO> flagshipProgramDTOs = crpPrograms.stream()
					.filter(c -> c.getProgramType() == 1 && c.getCrp().getGlobalUnitType().getId() <= 3)
					.sorted(Comparator.comparing(CrpProgram::getSmoCode,
							Comparator.nullsLast(Comparator.naturalOrder())))
					.map(crpProgramsEntity -> this.flagshipProgramMapper.crpProgramToCrpProgramDTO(crpProgramsEntity))
					.collect(Collectors.toList());
			return flagshipProgramDTOs;
		} else {
			return null;
		}
	}

}
