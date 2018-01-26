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

package org.cgiar.ccafs.marlo.rest;

import org.cgiar.ccafs.marlo.data.manager.InstitutionLocationManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;
import org.cgiar.ccafs.marlo.rest.dto.mapper.InstitutionMapper;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.shiro.authz.annotation.RequiresPermissions;
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
public class InstitutionController {

  private static final Logger LOG = LoggerFactory.getLogger(InstitutionController.class);

  private final InstitutionManager institutionManager;

  private final InstitutionMapper institutionMapper;

  private final InstitutionLocationManager institutionLocationManager;

  @Inject
  public InstitutionController(InstitutionManager institutionManager, UserManager userManager,
    InstitutionMapper institutionMapper, InstitutionLocationManager institutionLocationManager) {
    this.institutionManager = institutionManager;
    this.institutionMapper = institutionMapper;
    this.institutionLocationManager = institutionLocationManager;
  }

  @RequiresPermissions(Permission.FULL_REST_API_PERMISSION)
  @RequestMapping(value = "/{crp}/institutions", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InstitutionDTO> createInstitution(@PathVariable String crp,
    @Valid @RequestBody InstitutionDTO institutionDTO) {
    LOG.debug("Create a new institution with : {}", institutionDTO);
    Institution newInstitution = institutionMapper.institutionDTOToInstitution(institutionDTO);

    /**
     * TODO find out why Institution entities implement IAuditLog but doesn't have any audit fields.
     */
    // newInstitution.setCreatedBy(this.getCurrentUser());
    // // This should not be a non-nullable field in the database, as when created it should be null.
    // newInstitution.setModifiedBy(this.getCurrentUser());
    // newInstitution.setActiveSince(new Date());

    newInstitution = institutionManager.saveInstitution(newInstitution);

    /**
     * Unfortunately we are not using hibernate cascade update which means we need to save each
     * of the locations after saving the institution.
     */
    newInstitution.getInstitutionsLocations()
      .forEach(institutionLocation -> institutionLocationManager.saveInstitutionLocation(institutionLocation));

    return new ResponseEntity<InstitutionDTO>(institutionMapper.institutionToInstitutionDTO(newInstitution),
      HttpStatus.CREATED);
  }

  // TODO check if the institutionLocation(s) are deleted.
  @RequiresPermissions(Permission.FULL_REST_API_PERMISSION)
  @RequestMapping(value = "/{crp}/institutions/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> deleteInstitution(@PathVariable String crp, @PathVariable Long id) {
    LOG.debug("Delete institution with id: {}", id);
    institutionManager.deleteInstitution(id);
    return ResponseEntity.ok().build();
  }

  @RequiresPermissions(Permission.FULL_REST_API_PERMISSION)
  @RequestMapping(value = "/{crp}/institutions", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<InstitutionDTO> getAllInstitutions(@PathVariable String crp) {
    LOG.debug("REST request to get Institutions");
    List<Institution> institutions = institutionManager.findAll();
    List<InstitutionDTO> institutionDTOs = institutions.stream()
      .map(institution -> institutionMapper.institutionToInstitutionDTO(institution)).collect(Collectors.toList());
    return institutionDTOs;
  }

  @RequiresPermissions(Permission.FULL_REST_API_PERMISSION)
  @RequestMapping(value = "/{crp}/institutions/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InstitutionDTO> getInstitution(@PathVariable String crp, @PathVariable Long id) {
    LOG.debug("REST request to get Institution : {}", id);
    Institution institution = institutionManager.getInstitutionById(id);
    return Optional.ofNullable(institution).map(institutionMapper::institutionToInstitutionDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @RequiresPermissions(Permission.FULL_REST_API_PERMISSION)
  @RequestMapping(value = "/{crp}/institutions/{id}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InstitutionDTO> updateInstitution(@PathVariable String crp, @PathVariable Long id,
    @Valid @RequestBody InstitutionDTO institutionDTO) {
    LOG.debug("REST request to update Institution : {}", institutionDTO);

    Institution existingInstitution = institutionManager.getInstitutionById(institutionDTO.getId());

    existingInstitution = institutionMapper.updateInstitutionFromInstitutionDto(institutionDTO, existingInstitution);

    // // Auditing information should be done in a hibernate post-update/insert listener.
    // existingInstitution.setModifiedBy(this.getCurrentUser());

    // Now update the existingInstitution with the updated values.
    existingInstitution = institutionManager.saveInstitution(existingInstitution);

    return new ResponseEntity<InstitutionDTO>(institutionMapper.institutionToInstitutionDTO(existingInstitution),
      HttpStatus.OK);

  }

}
