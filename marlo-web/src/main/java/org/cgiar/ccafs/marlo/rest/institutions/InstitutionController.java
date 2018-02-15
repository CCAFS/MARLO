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

package org.cgiar.ccafs.marlo.rest.institutions;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionLocationManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PartnerRequestManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.InstitutionLocation;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.PartnerRequest;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.institutions.dto.InstitutionDTO;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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
public class InstitutionController {

  private static final Logger LOG = LoggerFactory.getLogger(InstitutionController.class);

  private final InstitutionManager institutionManager;

  private final InstitutionMapper institutionMapper;

  private final InstitutionLocationManager institutionLocationManager;

  private final PartnerRequestManager partnerRequestManager;

  private final GlobalUnitManager globalUnitManager;

  private final LocElementManager locElementManager;

  private final UserManager userManager;

  @Inject
  public InstitutionController(InstitutionManager institutionManager, UserManager userManager,
    InstitutionMapper institutionMapper, InstitutionLocationManager institutionLocationManager,
    PartnerRequestManager partnerRequestManager, GlobalUnitManager globalUnitManager,
    LocElementManager locElementManager) {
    this.institutionManager = institutionManager;
    this.institutionMapper = institutionMapper;
    this.institutionLocationManager = institutionLocationManager;
    this.partnerRequestManager = partnerRequestManager;
    this.globalUnitManager = globalUnitManager;
    this.locElementManager = locElementManager;
    this.userManager = userManager;
  }

  @RequiresPermissions(Permission.INSTITUTIONS_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{globalUnit}/institutions", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InstitutionDTO> createInstitution(@PathVariable String globalUnit,
    @Valid @RequestBody InstitutionDTO institutionDTO) {
    LOG.debug("Create a new institution with : {}", institutionDTO);

    /**
     * For an institution to be accepted it needs to be reviewed. We have a separate entity for this (not sure this
     * is a good idea), so we will use the same institutionDTO and hide the complexity from them and map back and forth
     * between the institutionDTO and the PartnerRequest. Question - how to handle the ids - do we leave blank?
     */

    GlobalUnit globalUnitEntity = globalUnitManager.findGlobalUnitByAcronym(globalUnit);

    LocElement locElement = locElementManager
      .getLocElementByISOCode(institutionDTO.getInstitutionsLocations().get(0).getCountryIsoAlpha2Code());

    PartnerRequest partnerRequestParent = institutionMapper.institutionDTOToPartnerRequest(institutionDTO,
      globalUnitEntity, locElement, this.getCurrentUser());

    partnerRequestParent = partnerRequestManager.savePartnerRequest(partnerRequestParent);

    /**
     * Need to create a parent child relationship for the partnerRequest to display. That design might need to be
     * re-visited.
     */
    PartnerRequest partnerRequestChild = institutionMapper.institutionDTOToPartnerRequest(institutionDTO,
      globalUnitEntity, locElement, this.getCurrentUser());

    partnerRequestChild.setPartnerRequest(partnerRequestParent);

    partnerRequestChild = partnerRequestManager.savePartnerRequest(partnerRequestChild);

    // Return an institutionDTO with a blank id - so that the user doesn't try and look up the institution straight
    // away.
    return new ResponseEntity<InstitutionDTO>(institutionMapper.partnerRequestToInstitutionDTO(partnerRequestParent),
      HttpStatus.CREATED);
  }

  @RequiresPermissions(Permission.INSTITUTIONS_DELETE_REST_API_PERMISSION)
  @RequestMapping(value = "/{globalUnit}/institutions/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> deleteInstitution(@PathVariable String globalUnit, @PathVariable Long id) {
    LOG.debug("Delete institution with id: {}", id);

    /**
     * We need to enable cascade delete on the institution -> institutionLocations relationship
     * if we want to avoid doing this.
     */
    Institution institutionToDelete = institutionManager.getInstitutionById(id);
    Set<InstitutionLocation> institutionsLocations = institutionToDelete.getInstitutionsLocations();
    for (InstitutionLocation institutionLocation : institutionsLocations) {
      institutionLocationManager.deleteInstitutionLocation(institutionLocation.getId());
    }

    // Now delete the institution.
    institutionManager.deleteInstitution(id);
    return ResponseEntity.ok().build();
  }

  @RequiresPermissions(Permission.INSTITUTIONS_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{globalUnit}/institutions", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<InstitutionDTO> getAllInstitutions(@PathVariable String globalUnit) {
    LOG.debug("REST request to get Institutions");
    List<Institution> institutions = institutionManager.findAll();
    List<InstitutionDTO> institutionDTOs = institutions.stream()
      .map(institution -> institutionMapper.institutionToInstitutionDTO(institution)).collect(Collectors.toList());
    return institutionDTOs;
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = userManager.getUser(principal);
    return user;
  }

  @RequiresPermissions(Permission.INSTITUTIONS_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{globalUnit}/institutions/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InstitutionDTO> getInstitution(@PathVariable String globalUnit, @PathVariable Long id) {
    LOG.debug("REST request to get Institution : {}", id);
    Institution institution = institutionManager.getInstitutionById(id);
    return Optional.ofNullable(institution).map(institutionMapper::institutionToInstitutionDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  @RequiresPermissions(Permission.INSTITUTIONS_UPDATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{globalUnit}/institutions/{id}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InstitutionDTO> updateInstitution(@PathVariable String globalUnit, @PathVariable Long id,
    @Valid @RequestBody InstitutionDTO institutionDTO) {
    LOG.debug("REST request to update Institution : {}", institutionDTO);

    Institution existingInstitution = institutionManager.getInstitutionById(institutionDTO.getId());

    /**
     * Note that the institutionLocation information will be ignored as this is better done in a separate web service
     * where each location can be updated individually rather than in bulk.
     */
    existingInstitution = institutionMapper.updateInstitutionFromInstitutionDto(institutionDTO, existingInstitution);

    // Now update the existingInstitution with the updated values.
    existingInstitution = institutionManager.saveInstitution(existingInstitution);

    return new ResponseEntity<InstitutionDTO>(institutionMapper.institutionToInstitutionDTO(existingInstitution),
      HttpStatus.OK);

  }

}
