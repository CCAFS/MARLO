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

import org.cgiar.ccafs.marlo.data.manager.RepIndPartnershipMainAreaManager;
import org.cgiar.ccafs.marlo.data.model.RepIndPartnershipMainArea;
import org.cgiar.ccafs.marlo.rest.dto.PartnershipMainAreaDTO;
import org.cgiar.ccafs.marlo.rest.mappers.PartnershipMainAreaMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class PartnershipMainAreaItem<T> {

	private RepIndPartnershipMainAreaManager repIndPartnershipMainAreaManager;
	private PartnershipMainAreaMapper partnershipMainAreaMapper;

	@Inject
	public PartnershipMainAreaItem(RepIndPartnershipMainAreaManager repIndPartnershipMainAreaManager,
			PartnershipMainAreaMapper partnershipMainAreaMapper) {
		this.repIndPartnershipMainAreaManager = repIndPartnershipMainAreaManager;
		this.partnershipMainAreaMapper = partnershipMainAreaMapper;
	}

	/**
	 * Find a Partnership main area Item MARLO id
	 * 
	 * @param id
	 * @return a PartnershipMainAreaDTO with the Partnership main area Item
	 */
	public ResponseEntity<PartnershipMainAreaDTO> findPartnershipMainAreaById(Long id) {
		RepIndPartnershipMainArea repIndPartnershipMainArea = this.repIndPartnershipMainAreaManager
				.getRepIndPartnershipMainAreaById(id);

		return Optional.ofNullable(repIndPartnershipMainArea)
				.map(this.partnershipMainAreaMapper::repIndPartnershipMainAreaToPartnershipMainArea)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Get All the Study Types Items *
	 * 
	 * @return a List of Study type with all Study Types items Items.
	 */
	public List<PartnershipMainAreaDTO> getAllPartnershipMainAreas() {
		if (this.repIndPartnershipMainAreaManager.findAll() != null) {
			List<RepIndPartnershipMainArea> repIndPartnershipMainArea = new ArrayList<>(
					this.repIndPartnershipMainAreaManager.findAll());

			List<PartnershipMainAreaDTO> PartnershipMainAreaDTOs = repIndPartnershipMainArea.stream()
					.map(repIndPartnershipMainAreaEntity -> this.partnershipMainAreaMapper
							.repIndPartnershipMainAreaToPartnershipMainArea(repIndPartnershipMainAreaEntity))
					.collect(Collectors.toList());
			return PartnershipMainAreaDTOs;
		} else {
			return null;
		}
	}

}
