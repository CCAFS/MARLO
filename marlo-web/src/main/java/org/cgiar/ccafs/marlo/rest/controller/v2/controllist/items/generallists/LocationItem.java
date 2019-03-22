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

import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.rest.dto.CountryDTO;
import org.cgiar.ccafs.marlo.rest.dto.RegionDTO;
import org.cgiar.ccafs.marlo.rest.mappers.LocationMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@Named
public class LocationItem<T> {

  private LocElementManager locElementManager;
  private LocationMapper locationMapper;

  public LocationItem(LocElementManager locElementManager, LocationMapper locationMapper) {
    this.locElementManager = locElementManager;
    this.locationMapper = locationMapper;
  }

  /**
   * Get All the Country items *
   * 
   * @return a List of LocElementDTO with all LocElements Items.
   */
  public List<CountryDTO> getAllCountries() {
    if (this.locElementManager.findAll() != null) {
      List<LocElement> countries = new ArrayList<>(this.locElementManager.findAll().stream()
        .filter(c -> c.isActive() && c.getLocElementType().getId() == 2).collect(Collectors.toList()));

      List<CountryDTO> countryDTOs = countries.stream()
        .map(countryEntity -> this.locationMapper.locElementToCountryDTO(countryEntity)).collect(Collectors.toList());
      return countryDTOs;
    } else {
      return null;
    }
  }

  /**
   * Get All the Regions items *
   * 
   * @return a List of RegionDTO with all region LocElements Items.
   */
  public List<RegionDTO> getAllRegions() {
    if (this.locElementManager.findAll() != null) {
      List<LocElement> regions = new ArrayList<>(this.locElementManager.findAll().stream()
        .filter(c -> c.isActive() && c.getLocElementType().getId() == 1).collect(Collectors.toList()));
      List<RegionDTO> regionDTOs = regions.stream()
        .map(regionEntity -> this.locationMapper.locElementToRegionDTO(regionEntity)).collect(Collectors.toList());
      return regionDTOs;
    } else {
      return null;
    }
  }

  /**
   * Get the country by Alpha2 ISO Code
   * 
   * @param alpha2 ISO code
   * @return a List of LocElementDTO with all LocElements Items.
   */
  public ResponseEntity<CountryDTO> getContryByAlpha2ISOCode(String ISOCode) {
    LocElement locElement = this.locElementManager.getLocElementByISOCode(ISOCode.toUpperCase());
    ResponseEntity<CountryDTO> response;
    if (locElement != null && locElement.getLocElementType().getId() == 2) {
      response = new ResponseEntity<>(this.locationMapper.locElementToCountryDTO(locElement), HttpStatus.OK);
    } else {
      response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    return response;
  }

  /**
   * Get the country by numeric ISO Code
   * 
   * @param Numeric iso code
   * @return a List of LocElementDTO with all LocElements Items.
   */
  public ResponseEntity<CountryDTO> getContryByNumericISOCode(Long ISOCode) {
    LocElement locElement = this.locElementManager.getLocElementByNumericISOCode(ISOCode);
    ResponseEntity<CountryDTO> response;
    if (locElement != null && locElement.getLocElementType().getId() == 2) {
      response = new ResponseEntity<>(this.locationMapper.locElementToCountryDTO(locElement), HttpStatus.OK);
    } else {
      response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return response;
  }

  /**
   * Get the country by UN code
   * 
   * @param UN M.49 code
   * @return RegionDTO founded
   */
  public ResponseEntity<RegionDTO> getRegionByCode(Long code) {
    LocElement locElement = this.locElementManager.getLocElementByNumericISOCode(code);
    ResponseEntity<RegionDTO> response;
    if (locElement != null && locElement.getLocElementType().getId() == 1) {
      response = new ResponseEntity<>(this.locationMapper.locElementToRegionDTO(locElement), HttpStatus.OK);
    } else {
      response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    return response;
  }
}
