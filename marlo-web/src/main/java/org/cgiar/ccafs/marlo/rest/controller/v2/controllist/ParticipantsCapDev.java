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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.ParticipantsCapDev.ParticipantsCapDevItem;
import org.cgiar.ccafs.marlo.rest.dto.NewParticipantsCapDevDTO;
import org.cgiar.ccafs.marlo.rest.dto.ParticipantsCapDevDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.security.Permission;

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

@RestController
@Api(tags = "Table 7 - Participants in CapDev Activities")
public class ParticipantsCapDev {

  private static final Logger LOG = LoggerFactory.getLogger(ParticipantsCapDev.class);
  @Autowired
  private Environment env;

  private ParticipantsCapDevItem<ParticipantsCapDevDTO> participantsCapDevItem;
  private final UserManager userManager;

  @Inject
  public ParticipantsCapDev(ParticipantsCapDevItem<ParticipantsCapDevDTO> participantsCapDevItem,
    UserManager userManager) {
    this.participantsCapDevItem = participantsCapDevItem;
    this.userManager = userManager;

  }

  /**
   * Create a new ParticipantsCapDev *
   * 
   * @param acronym of global unit
   * @param year
   * @param NewParticipantsCapDevDTO with participants cap dev info
   * @return a ParticipantsCapDevDTO with the participants cap dev created
   */
  @ApiOperation(tags = {"Table 7 - Participants in CapDev Activities"},
    value = "${ParticipantsCapDev.participantscapdev.POST.value}", response = ParticipantsCapDevDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/participantscapdev/", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createParticipantsCapDev(
    @ApiParam(value = "${ParticipantsCapDev.participantscapdev.POST.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ParticipantsCapDev.participantscapdev.POST.param.participantscapdev}",
      required = true) @Valid @RequestBody NewParticipantsCapDevDTO newParticipantsCapDevDTO) {

    Long reportSynCrossCutDimId = this.participantsCapDevItem.createParticipantsCapDev(newParticipantsCapDevDTO,
      CGIAREntity, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(reportSynCrossCutDimId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ParticipantsCapDev.participantscapdev.GET.id.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Table 7 - Participants in CapDev Activities"},
    value = "${ParticipantsCapDev.participantscapdev.DELETE.id.value}", response = ParticipantsCapDevDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/participantscapdev/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ParticipantsCapDevDTO> deleteParticipantsCapDevById(
    @ApiParam(value = "${ParticipantsCapDev.participantscapdev.DELETE.id.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ParticipantsCapDev.participantscapdev.DELETE.id.param.id}",
      required = true) @PathVariable Long id,
    @ApiParam(value = "${ParticipantsCapDev.participantscapdev.DELETE.id.param.year}",
      required = true) @RequestParam Integer year,
    @ApiParam(value = "${ParticipantsCapDev.participantscapdev.DELETE.id.param.phase}",
      required = true) @RequestParam String phase) {

    ResponseEntity<ParticipantsCapDevDTO> response =
      this.participantsCapDevItem.deleteParticipantsCapDevById(id, CGIAREntity, year, phase, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ParticipantsCapDev.participantscapdev.DELETE.id.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Table 7 - Participants in CapDev Activities"},
    value = "${ParticipantsCapDev.participantscapdev.GET.id.value}", response = ParticipantsCapDevDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/participantscapdev/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ParticipantsCapDevDTO> findParticipantsCapDevById(
    @ApiParam(value = "${ParticipantsCapDev.participantscapdev.GET.id.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ParticipantsCapDev.participantscapdev.GET.id.param.id}",
      required = true) @PathVariable Long id,
    @ApiParam(value = "${ParticipantsCapDev.participantscapdev.GET.id.param.year}",
      required = true) @RequestParam Integer year,
    @ApiParam(value = "${ParticipantsCapDev.participantscapdev.GET.id.param.phase}",
      required = true) @RequestParam String phase) {

    ResponseEntity<ParticipantsCapDevDTO> response =
      this.participantsCapDevItem.findParticipantsCapDevById(id, CGIAREntity, year, phase, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ParticipantsCapDev.participantscapdev.GET.id.404"));
    }
    return response;
  }


  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = this.userManager.getUser(principal);
    return user;
  }


  @ApiOperation(tags = {"Table 7 - Participants in CapDev Activities"},
    value = "${ParticipantsCapDev.participantscapdev.PUT.value}", response = ParticipantsCapDevDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/participantscapdev/{id}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> putParticipantsCapDev(
    @ApiParam(value = "${ParticipantsCapDev.participantscapdev.PUT.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ParticipantsCapDev.participantscapdev.PUT.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${ParticipantsCapDev.participantscapdev.PUT.param.participantscapdev}",
      required = true) @Valid @RequestBody NewParticipantsCapDevDTO newParticipantsCapDevDTO) {
    Long reportSynCrossCutDimId = this.participantsCapDevItem.putParticipantsCapDevById(id, newParticipantsCapDevDTO,
      CGIAREntity, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(reportSynCrossCutDimId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ParticipantsCapDev.participantscapdev.DELETE.id.404"));
    }
    return response;
  }


}
