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

import org.cgiar.ccafs.marlo.data.manager.CgiarCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;
import org.cgiar.ccafs.marlo.rest.dto.CrossCuttingMarkerDTO;
import org.cgiar.ccafs.marlo.rest.mappers.CrossCuttingMarkerMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class CrossCuttingMarkerItem<T> {

	private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;
	private CrossCuttingMarkerMapper crossCuttingMarkerMapper;

	@Inject
	public CrossCuttingMarkerItem(CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager,
			CrossCuttingMarkerMapper crossCuttingMarkerMapper) {
		this.cgiarCrossCuttingMarkerManager = cgiarCrossCuttingMarkerManager;
		this.crossCuttingMarkerMapper = crossCuttingMarkerMapper;
	}

	/**
	 * Find a Cross Cutting Marker requesting a MARLO id
	 * 
	 * @param id
	 * @return a CrossCuttingMarkersDTO with the Cross Cutting Marker data.
	 */
	public ResponseEntity<CrossCuttingMarkerDTO> findCrossCuttingMarkerById(Long id) {
		CgiarCrossCuttingMarker cgiarCrossCuttingMarker = this.cgiarCrossCuttingMarkerManager
				.getCgiarCrossCuttingMarkerById(id);

		return Optional.ofNullable(cgiarCrossCuttingMarker)
				.map(this.crossCuttingMarkerMapper::cgiarCrossCuttingMarkerToCrossCuttingMarkerDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Get All the Cross Cutting Markers Items *
	 * 
	 * @return a List of CrossCuttingMarkersDTO with all
	 * RepIndGenderYouthFocusLevel Items.
	 */
	public List<CrossCuttingMarkerDTO> getAllCrossCuttingMarker() {
		if (this.cgiarCrossCuttingMarkerManager.findAll() != null) {
			List<CgiarCrossCuttingMarker> cgiarCrossCuttingMarker = new ArrayList<>(
					this.cgiarCrossCuttingMarkerManager.findAll());

			List<CrossCuttingMarkerDTO> crossCuttingMarkersDTO = cgiarCrossCuttingMarker.stream()
					.map(cgiarCrossCuttingMarkerEntity -> this.crossCuttingMarkerMapper
							.cgiarCrossCuttingMarkerToCrossCuttingMarkerDTO(cgiarCrossCuttingMarkerEntity))
					.collect(Collectors.toList());

			return crossCuttingMarkersDTO;
		} else {
			return null;
		}
	}

}
