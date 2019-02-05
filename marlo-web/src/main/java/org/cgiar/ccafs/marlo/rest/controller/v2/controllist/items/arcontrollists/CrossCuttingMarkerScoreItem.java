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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists;

import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.rest.dto.CrossCuttingMarkerScoreDTO;
import org.cgiar.ccafs.marlo.rest.mappers.CrossCuttingMarkerScoreMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class CrossCuttingMarkerScoreItem<T> {

	private RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager;
	private CrossCuttingMarkerScoreMapper crossCuttingMarkerScoreMapper;

	@Inject
	public CrossCuttingMarkerScoreItem(RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager,
			CrossCuttingMarkerScoreMapper crossCuttingMarkerScoreMapper) {
		this.repIndGenderYouthFocusLevelManager = repIndGenderYouthFocusLevelManager;
		this.crossCuttingMarkerScoreMapper = crossCuttingMarkerScoreMapper;
	}

	/**
	 * Find a Cross Cutting Marker requesting a MARLO id
	 * 
	 * @param id
	 * @return a CrossCuttingMarkersDTO with the Cross Cutting Marker data.
	 */
	public ResponseEntity<CrossCuttingMarkerScoreDTO> findCrossCuttingMarkerScoreById(Long id) {
		RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel = this.repIndGenderYouthFocusLevelManager
				.getRepIndGenderYouthFocusLevelById(id);
		return Optional.ofNullable(repIndGenderYouthFocusLevel)
				.map(this.crossCuttingMarkerScoreMapper::repIndGenderYouthFocusLevelToCrossCuttingMarkerScoreDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Get All the Cross Cutting Markers Items *
	 * 
	 * @return a List of CrossCuttingMarkersDTO with all
	 * RepIndGenderYouthFocusLevel Items.
	 */
	public List<CrossCuttingMarkerScoreDTO> getAllCrossCuttingMarkersScores() {
		if (this.repIndGenderYouthFocusLevelManager.findAll() != null) {
			List<RepIndGenderYouthFocusLevel> repIndGenderYouthFocusLevels = new ArrayList<>(
					this.repIndGenderYouthFocusLevelManager.findAll());
			List<CrossCuttingMarkerScoreDTO> crossCuttingMarkerScoresDTO = repIndGenderYouthFocusLevels.stream()
					.map(repIndGenderYouthFocusLevelsEntity -> this.crossCuttingMarkerScoreMapper
							.repIndGenderYouthFocusLevelToCrossCuttingMarkerScoreDTO(
									repIndGenderYouthFocusLevelsEntity))
					.collect(Collectors.toList());
			return crossCuttingMarkerScoresDTO;
		} else {
			return null;
		}
	}

}
