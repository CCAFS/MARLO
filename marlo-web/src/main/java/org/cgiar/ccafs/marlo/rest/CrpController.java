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

import org.cgiar.ccafs.marlo.data.manager.CrpManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.Crp;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.CrpDTO;
import org.cgiar.ccafs.marlo.rest.dto.mapper.CrpMapper;
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
public class CrpController {

  private static final Logger LOG = LoggerFactory.getLogger(CrpController.class);

  private final CrpManager crpManager;

  private final UserManager userManager;

  private final CrpMapper crpMapper;

  @Inject
  public CrpController(CrpManager crpManager, UserManager userManager, CrpMapper crpMapper) {
    this.crpManager = crpManager;
    this.userManager = userManager;
    this.crpMapper = crpMapper;
  }

  @RequiresPermissions(Permission.FULL_REST_API_PERMISSION)
  @RequestMapping(value = "/{crp}/crps", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CrpDTO> createCrp(@PathVariable String crp, @Valid @RequestBody CrpDTO crpDTO) {
    LOG.debug("Create a new crp with : {}", crpDTO);
    Crp newCrp = crpMapper.crpDTOToCrp(crpDTO);

    // These audit fields should be automatically created via a Hibernate post-insert/update listener!
    newCrp.setCreatedBy(this.getCurrentUser());
    // This should not be a non-nullable field in the database, as when created it should be null.
    newCrp.setModifiedBy(this.getCurrentUser());
    newCrp.setActiveSince(new Date());

    newCrp = crpManager.saveCrp(newCrp);

    return new ResponseEntity<CrpDTO>(crpMapper.crpToCrpDTO(newCrp), HttpStatus.CREATED);
  }

  @RequiresPermissions(Permission.FULL_REST_API_PERMISSION)
  @RequestMapping(value = "/{crp}/crps/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Void> deleteCrp(@PathVariable String crp, @PathVariable Long id) {
    LOG.debug("Delete crp with id: {}", id);
    crpManager.deleteCrp(id);
    return ResponseEntity.ok().build();
  }

  @RequiresPermissions(Permission.FULL_REST_API_PERMISSION)
  @RequestMapping(value = "/{crp}/crps", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<CrpDTO> getAllCrps(@PathVariable String crp) {
    LOG.debug("REST request to get Crps");
    List<Crp> crps = crpManager.findAll();
    List<CrpDTO> crpDTOs =
      crps.stream().map(crpEntity -> crpMapper.crpToCrpDTO(crpEntity)).collect(Collectors.toList());
    return crpDTOs;
  }

  @RequiresPermissions(Permission.FULL_REST_API_PERMISSION)
  @RequestMapping(value = "/{crp}/crps/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CrpDTO> getCrp(@PathVariable String crp, @PathVariable Long id) {
    LOG.debug("REST request to get Crp : {}", id);
    Crp crpEntity = crpManager.getCrpById(id);
    return Optional.ofNullable(crpEntity).map(crpMapper::crpToCrpDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = userManager.getUser(principal);
    return user;

  }


  @RequestMapping(value = "/{crp}/crps/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CrpDTO> updateCrp(@PathVariable String crp, @PathVariable Long id,
    @Valid @RequestBody CrpDTO crpDTO) {
    LOG.debug("REST request to update Crp : {}", crpDTO);

    Crp existingCrp = crpManager.getCrpById(crpDTO.getId());

    existingCrp = crpMapper.updateCrpFromCrpDto(crpDTO, existingCrp);

    // Auditing information should be done in a hibernate post-update/insert listener.
    existingCrp.setModifiedBy(this.getCurrentUser());

    // Now update the existingCrp with the updated values.
    existingCrp = crpManager.saveCrp(existingCrp);

    return new ResponseEntity<CrpDTO>(crpMapper.crpToCrpDTO(existingCrp), HttpStatus.OK);

  }


}
