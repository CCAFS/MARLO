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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.srflist;

import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.rest.dto.SrfSloTargetDTO;
import org.cgiar.ccafs.marlo.rest.mappers.SrfSloIndicatorTargetMapper;

import java.util.ArrayList;
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
public class SrfSloTargetItem<T> {

	// Managers
	private SrfSloIndicatorTargetManager srfSloIndicatorTargetManager;

	// Mappers
	private SrfSloIndicatorTargetMapper srfSloIndicatorTargetMapper;

	@Inject
	public SrfSloTargetItem(SrfSloIndicatorTargetManager srfSloIndicatorTargetManager,
			SrfSloIndicatorTargetMapper srfSloIndicatorTargetMapper) {
		this.srfSloIndicatorTargetManager = srfSloIndicatorTargetManager;
		this.srfSloIndicatorTargetMapper = srfSloIndicatorTargetMapper;
	}

	/**
	 * Find a SRF slo target Indicator requesting a code
	 * 
	 * @param smo code
	 * @return a SrfSloIndicatorTargetDTO with SRF slo target Indicator
	 * requesting data.
	 */
	public ResponseEntity<SrfSloTargetDTO> findSrfSloIndicatorTargetbyId(String code) {
		SrfSloIndicatorTarget SrfSloIndicatorTarget = this.srfSloIndicatorTargetManager.findbyTargetIndicatorCode(code);

		return Optional.ofNullable(SrfSloIndicatorTarget)
				.map(this.srfSloIndicatorTargetMapper::srfSloIndicatorTargetToSrfSloIndicatorTargetDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Get All the slo target Indicator items *
	 * 
	 * @return a List of SrfSloIndicatorTargetDTO with all slo target Indicator
	 * Items.
	 */
	public ResponseEntity<List<SrfSloTargetDTO>> getAllSrfSloIndicatorTargets(Long year) {
		List<SrfSloIndicatorTarget> SrfSloIndicatorTargets;

		if (this.srfSloIndicatorTargetManager.findAll() != null) {
			if (year != null) {
				SrfSloIndicatorTargets = new ArrayList<>(this.srfSloIndicatorTargetManager.findAll().stream()
						.filter(c -> c.getYear() == year).collect(Collectors.toList()));
			} else {
				SrfSloIndicatorTargets = new ArrayList<>(this.srfSloIndicatorTargetManager.findAll());
			}
			List<SrfSloTargetDTO> srfSloIndicatorTargetDTOs = SrfSloIndicatorTargets.stream()
					.map(srfSloIndicatorTargetEntity -> this.srfSloIndicatorTargetMapper
							.srfSloIndicatorTargetToSrfSloIndicatorTargetDTO(srfSloIndicatorTargetEntity))
					.collect(Collectors.toList());
			if (srfSloIndicatorTargetDTOs == null || srfSloIndicatorTargetDTOs.size() == 0) {
				return new ResponseEntity<List<SrfSloTargetDTO>>(HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<List<SrfSloTargetDTO>>(srfSloIndicatorTargetDTOs, HttpStatus.OK);
			}
		} else {
			return new ResponseEntity<List<SrfSloTargetDTO>>(HttpStatus.NOT_FOUND);
		}
	}

}
