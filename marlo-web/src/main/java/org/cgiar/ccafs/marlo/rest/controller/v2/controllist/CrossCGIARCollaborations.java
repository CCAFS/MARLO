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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist;

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.crossCgiarCollabs.CrossCGIARCollaborationsItem;
import org.cgiar.ccafs.marlo.rest.dto.CrossCGIARCollaborationDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewCrossCGIARCollaborationDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;

import javax.validation.Valid;

import com.opensymphony.xwork2.inject.Inject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@RestController
@Api(tags = "Table 9 - Internal Cross-CGIAR Collaborations")
public class CrossCGIARCollaborations {

  private static final Logger LOG = LoggerFactory.getLogger(CrossCGIARCollaborations.class);
  @Autowired
  private Environment env;
  private final UserManager userManager;
  private CrossCGIARCollaborationsItem<CrossCGIARCollaborationDTO> crossCollabItem;

  @Inject
  public CrossCGIARCollaborations(CrossCGIARCollaborationsItem<CrossCGIARCollaborationDTO> crossCollabItem,
    UserManager userManager) {
    this.userManager = userManager;
    this.crossCollabItem = crossCollabItem;
  }

  @ApiOperation(tags = {"Table 9 - Internal Cross-CGIAR Collaborations"},
    value = "${CrossCollabs.crossCGIARcollaborations.POST.value}", response = CrossCGIARCollaborationDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/crosscgiarcollaboration", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createCrossCGIARCollaboration(
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.POST.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.POST.param.progresstowardsSRF}",
      required = true) @Valid @RequestBody NewCrossCGIARCollaborationDTO newCrossCGIARCollaborationDTO) {

    Long crossCGIARCollaborationId = this.crossCollabItem.createCrossCGIARCollaboration(newCrossCGIARCollaborationDTO,
      CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(crossCGIARCollaborationId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CrossCollabs.crossCGIARcollaborations.GET.id.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"Table 9 - Internal Cross-CGIAR Collaborations"},
    value = "${CrossCollabs.crossCGIARcollaborations.DELETE.id.value}", response = CrossCGIARCollaborationDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/crosscgiarcollaboration/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CrossCGIARCollaborationDTO> deleteCrossCGIARCollaborationById(
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.DELETE.id.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.DELETE.id.param.id}",
      required = true) @PathVariable Long id,
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.DELETE.id.param.year}",
      required = true) @RequestParam Integer year,
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.DELETE.id.param.phase}",
      required = true) @RequestParam String phase) {

    ResponseEntity<CrossCGIARCollaborationDTO> response =
      this.crossCollabItem.deleteCrossCGIARCollaborationById(id, CGIAREntity, year, phase, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CrossCollabs.crossCGIARcollaborations.DELETE.id.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"Table 9 - Internal Cross-CGIAR Collaborations"},
    value = "${CrossCollabs.crossCGIARcollaborations.GET.all.value}", response = CrossCGIARCollaborationDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/crosscgiarcollaboration", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<CrossCGIARCollaborationDTO> findAllProgressTowardsByGlobalUnit(
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.GET.all.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.GET.all.param.year}",
      required = true) @RequestParam Integer year,
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.GET.all.param.phase}",
      required = true) @RequestParam String phase) {
    return this.crossCollabItem.findAllCrossCGIARCollaborationsByGlobalUnit(CGIAREntity, year, phase,
      this.getCurrentUser());
  }

  @ApiOperation(tags = {"Table 9 - Internal Cross-CGIAR Collaborations"},
    value = "${CrossCollabs.crossCGIARcollaborations.GET.id.value}", response = CrossCGIARCollaborationDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/crosscgiarcollaboration/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CrossCGIARCollaborationDTO> findCrossCGIARCollaborationById(
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.GET.id.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.GET.id.param.id}",
      required = true) @PathVariable Long id,
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.GET.id.param.year}",
      required = true) @RequestParam Integer year,
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.GET.id.param.phase}",
      required = true) @RequestParam String phase) {

    ResponseEntity<CrossCGIARCollaborationDTO> response =
      this.crossCollabItem.findCrossCGIARCollaborationById(id, CGIAREntity, year, phase, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CrossCollabs.crossCGIARcollaborations.GET.id.404"));
    }

    return response;
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = this.userManager.getUser(principal);
    return user;
  }

  @ApiOperation(tags = {"Table 9 - Internal Cross-CGIAR Collaborations"},
    value = "${CrossCollabs.crossCGIARcollaborations.PUT.value}", response = CrossCGIARCollaborationDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/crosscgiarcollaboration/{id}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> putCrossCGIARCollaborationById(
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.PUT.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.PUT.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${CrossCollabs.crossCGIARcollaborations.PUT.param.progresstowardsSRF}",
      required = true) @Valid @RequestBody NewCrossCGIARCollaborationDTO newCrossCGIARCollaborationDTO) {

    Long crossCGIARCollaborationId = this.crossCollabItem.putCrossCGIARCollaborationById(id,
      newCrossCGIARCollaborationDTO, CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(crossCGIARCollaborationId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CrossCollabs.crossCGIARcollaborations.PUT.id.404"));
    }

    return response;
  }

}
