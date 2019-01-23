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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.locations;

import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.rest.dto.LocElementDTO;
import org.cgiar.ccafs.marlo.rest.mappers.LocElementMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class CountryItem<T> {

	private LocElementManager locElementManager;
	private LocElementMapper locElementMapper;

	public CountryItem(LocElementManager locElementManager, LocElementMapper locElementMapper) {
		this.locElementManager = locElementManager;
		this.locElementMapper = locElementMapper;
	}

	/**
	 * Get All the Country items *
	 * 
	 * @return a List of LocElementDTO with all LocElements Items.
	 */
	public List<LocElementDTO> getAllCountries() {
		if (this.locElementManager.findAll() != null) {
			List<LocElement> countries = new ArrayList<>(this.locElementManager.findAll().stream()
					.filter(c -> c.isActive() && c.getLocElementType().getId() == 2).collect(Collectors.toList()));
			List<LocElementDTO> countryDTOs = countries.stream()
					.map(countryEntity -> this.locElementMapper.locElementToLocElementDTO(countryEntity))
					.collect(Collectors.toList());
			return countryDTOs;
		} else {
			return null;
		}
	}

	/**
	 * Get the country by numeric ISO Code
	 * 
	 * @param Numeric iso code
	 * @return a List of LocElementDTO with all LocElements Items.
	 */
	public ResponseEntity<LocElementDTO> getContryByNumericISOCode(Long ISOCode) {
		LocElement locElement = this.locElementManager.getLocElementByNumericISOCode(ISOCode);
		return Optional.ofNullable(locElement).map(this.locElementMapper::locElementToLocElementDTO)
				.map(result -> new ResponseEntity<>(result, HttpStatus.OK))
				.orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

	}

}
