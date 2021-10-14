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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR;

import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.RegionsManager;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.LocElementRegion;
import org.cgiar.ccafs.marlo.data.model.Region;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.OneCGIARRegionsDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.RegionsMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class RegionsItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(Region.class);
  @Autowired
  private Environment env;
  private RegionsManager regionsManager;
  private LocElementManager locElementManager;
  private RegionsMapper regionsMapper;

  @Inject
  public RegionsItem(RegionsManager regionsManager, RegionsMapper regionsMapper, LocElementManager locElementManager) {
    super();
    this.regionsManager = regionsManager;
    this.regionsMapper = regionsMapper;
    this.locElementManager = locElementManager;
  }

  public ResponseEntity<List<OneCGIARRegionsDTO>> getAll() {
    List<Region> regionList = new ArrayList<Region>();
    List<Region> regions = regionsManager.findAll().stream().filter(c -> c.getRegionType().getId().longValue() == 1)
      .collect(Collectors.toList());
    List<LocElement> regionCountries;
    if (regions != null) {
      for (Region region : regions) {
        regionCountries = new ArrayList<LocElement>();
        for (LocElementRegion locElementRegion : region.getRegionCountries().stream().collect(Collectors.toList())) {
          LocElement loc = locElementManager.getLocElementById(locElementRegion.getLocElement().getId());
          if (loc != null) {

            regionCountries.add(loc);
          }
        }
        region.setCountries(regionCountries);
        regionList.add(region);
      }
    }
    //
    List<OneCGIARRegionsDTO> regionsDTO = regionList.stream()
      .map(region -> this.regionsMapper.regionsToOneCGIARRegionsDTO(region)).collect(Collectors.toList());


    return Optional.ofNullable(regionsDTO).map(result -> new ResponseEntity<>(result, HttpStatus.OK))
      .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public ResponseEntity<OneCGIARRegionsDTO> getRegion(Long id, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    Region region = regionsManager.find(id);
    if (region == null) {
      fieldErrors.add(new FieldErrorDTO("Regions", "getRegion", "This region not exist"));
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "", fieldErrors);
    }
    return Optional.ofNullable(region).map(this.regionsMapper::regionsToOneCGIARRegionsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

}
