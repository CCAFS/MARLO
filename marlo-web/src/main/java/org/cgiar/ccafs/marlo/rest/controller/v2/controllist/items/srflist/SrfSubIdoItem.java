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

import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.rest.dto.SrfSubIdoDTO;
import org.cgiar.ccafs.marlo.rest.mappers.SrfSubIdoMapper;

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
public class SrfSubIdoItem<T> {

	private SrfSubIdoManager srfSubIdoManager;
	private SrfSubIdoMapper srfSubIdoMapper;

	@Inject
	public SrfSubIdoItem(SrfSubIdoManager srfSubIdoManager, SrfSubIdoMapper srfSubIdoMapper) {
		super();
		this.srfSubIdoManager = srfSubIdoManager;
		this.srfSubIdoMapper = srfSubIdoMapper;
	}

	/**
	 * Find a SRF-SubIdo requesting an smo code
	 * 
	 * @param id
	 * @return a SrfSubIdoDTO with the SRF-SubIdo data.
	 */
	public ResponseEntity<SrfSubIdoDTO> findSrfSubIdoBycode(String smoCode) {
		SrfSubIdo srfSubIdo = this.srfSubIdoManager.getSrfSubIdoByCode(smoCode);
		return Optional.ofNullable(srfSubIdo).map(this.srfSubIdoMapper::srfSubIdoToSrfSubIdoDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * Get All the SRF-SubIdo items
	 * 
	 * @return a List of SrfSubIdoDTO with all SRF-SubIdo Items.
	 */
	public List<SrfSubIdoDTO> getAllSrfSubIdos() {
		if (this.srfSubIdoManager.findAll() != null) {
			List<SrfSubIdo> SrfSubIdos = new ArrayList<>(this.srfSubIdoManager.findAll());
			List<SrfSubIdoDTO> SrfSubIdoDTOs = SrfSubIdos.stream()
					.map(srfSubIdoEntity -> this.srfSubIdoMapper.srfSubIdoToSrfSubIdoDTO(srfSubIdoEntity))
					.collect(Collectors.toList());
			return SrfSubIdoDTOs;
		} else {
			return null;
		}
	}

}
