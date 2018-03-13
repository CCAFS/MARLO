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

package org.cgiar.ccafs.marlo.rest.globalUnits;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GlobalUnitController {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalUnitController.class);

  private final GlobalUnitManager globalUnitManager;

  private final UserManager userManager;

  private final GlobalUnitMapper globalUnitMapper;

  @Inject
  public GlobalUnitController(GlobalUnitManager globalUnitManager, UserManager userManager,
    GlobalUnitMapper globalUnitMapper) {
    this.globalUnitManager = globalUnitManager;
    this.userManager = userManager;
    this.globalUnitMapper = globalUnitMapper;
  }

  @RequiresPermissions(Permission.CRPS_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{globalUnit}/globalUnits", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GlobalUnitDTO> createGlobalUnit(@PathVariable String globalUnit,
    @Valid @RequestBody GlobalUnitDTO globalUnitDTO) {
    LOG.debug("Create a new globalUnit with : {}", globalUnitDTO);
    GlobalUnit newGlobalUnit = globalUnitMapper.globalUnitDTOToGlobalUnit(globalUnitDTO);

    // These audit fields should be automatically created via a Hibernate post-insert/update listener!
    newGlobalUnit.setCreatedBy(this.getCurrentUser());
    // This should not be a non-nullable field in the database, as when created it should be null.
    newGlobalUnit.setModifiedBy(this.getCurrentUser());
    newGlobalUnit.setActiveSince(new Date());

    newGlobalUnit = globalUnitManager.saveGlobalUnit(newGlobalUnit);

    return new ResponseEntity<GlobalUnitDTO>(globalUnitMapper.globalUnitToGlobalUnitDTO(newGlobalUnit),
      HttpStatus.CREATED);
  }

  @RequiresPermissions(Permission.CRPS_DELETE_REST_API_PERMISSION)
  @RequestMapping(value = "/{globalUnit}/globalUnits/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> deleteGlobalUnit(@PathVariable String globalUnit, @PathVariable Long id) {
    LOG.debug("Delete globalUnit with id: {}", id);
    globalUnitManager.deleteGlobalUnit(id);
    return ResponseEntity.ok().build();
  }

  @RequiresPermissions(Permission.CRPS_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{globalUnit}/globalUnits", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<GlobalUnitDTO> getAllGlobalUnits(@PathVariable String globalUnit) {
    LOG.debug("REST request to get GlobalUnits");
    List<GlobalUnit> globalUnits = globalUnitManager.findAll();
    List<GlobalUnitDTO> globalUnitDTOs =
      globalUnits.stream().map(globalUnitEntity -> globalUnitMapper.globalUnitToGlobalUnitDTO(globalUnitEntity))
        .collect(Collectors.toList());
    return globalUnitDTOs;
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = userManager.getUser(principal);
    return user;
  }

  @RequiresPermissions(Permission.CRPS_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{globalUnit}/globalUnits/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GlobalUnitDTO> getGlobalUnit(@PathVariable String globalUnit, @PathVariable Long id) {
    LOG.debug("REST request to get GlobalUnit : {}", id);
    GlobalUnit globalUnitEntity = globalUnitManager.getGlobalUnitById(id);
    return Optional.ofNullable(globalUnitEntity).map(globalUnitMapper::globalUnitToGlobalUnitDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }


  @RequiresPermissions(Permission.CRPS_UPDATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{globalUnit}/globalUnits/{id}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GlobalUnitDTO> updateGlobalUnit(@PathVariable String globalUnit, @PathVariable Long id,
    @Valid @RequestBody GlobalUnitDTO globalUnitDTO) {
    LOG.debug("REST request to update GlobalUnit : {}", globalUnitDTO);

    GlobalUnit existingGlobalUnit = globalUnitManager.getGlobalUnitById(globalUnitDTO.getId());

    existingGlobalUnit = globalUnitMapper.updateGlobalUnitFromGlobalUnitDto(globalUnitDTO, existingGlobalUnit);

    // Auditing information should be done in a hibernate post-update/insert listener.
    existingGlobalUnit.setModifiedBy(this.getCurrentUser());

    // Now update the existingGlobalUnit with the updated values.
    existingGlobalUnit = globalUnitManager.saveGlobalUnit(existingGlobalUnit);

    return new ResponseEntity<GlobalUnitDTO>(globalUnitMapper.globalUnitToGlobalUnitDTO(existingGlobalUnit),
      HttpStatus.OK);

  }


}
