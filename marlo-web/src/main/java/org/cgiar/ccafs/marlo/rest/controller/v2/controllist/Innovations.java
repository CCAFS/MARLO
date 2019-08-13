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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.Innovations.InnovationItem;
import org.cgiar.ccafs.marlo.rest.dto.InnovationDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewInnovationDTO;
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
@Api(tags = "Innovations")
public class Innovations {

  private static final Logger LOG = LoggerFactory.getLogger(Innovations.class);
  @Autowired
  private Environment env;

  private InnovationItem<InnovationDTO> innovationItem;
  private final UserManager userManager;


  @Inject
  public Innovations(InnovationItem<InnovationDTO> innovationItem, UserManager userManager) {
    this.innovationItem = innovationItem;
    this.userManager = userManager;

  }

  /**
   * Create a new Innovation *
   * 
   * @param acronym of global unit
   * @param year
   * @param NewInnovationDTO with innvovation info
   * @return a InnovationDTO with the innovation created
   */
  @ApiOperation(tags = {"Table 4 - CRP Innovations"}, value = "${Innovation.innovation.POST.value}",
    response = InnovationDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/innovations", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createInnovation(
    @ApiParam(value = "${Innovation.innovation.POST.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Innovation.innovation.POST.param.innovation}",
      required = true) @Valid @RequestBody NewInnovationDTO newInnovationDTO) {
    Long innovationId = this.innovationItem.createInnovation(newInnovationDTO, CGIAREntity, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(innovationId, HttpStatus.OK);
    return response;

  }


  @ApiOperation(tags = {"Table 4 - CRP Innovations"}, value = "${Innovation.innovation.DELETE.id.value}",
    response = InnovationDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/innovations/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InnovationDTO> deleteInnovationById(
    @ApiParam(value = "${Innovation.innovation.DELETE.id.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Innovation.innovation.DELETE.id.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${Innovation.innovation.DELETE.id.param.year}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${Innovation.innovation.DELETE.id.param.phase}", required = true) @RequestParam String phase) {

    ResponseEntity<InnovationDTO> response =
      this.innovationItem.deleteInnovationById(id, CGIAREntity, year, phase, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Innovation.innovation.DELETE.id.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Table 4 - CRP Innovations"}, value = "${Innovation.innovation.GET.id.value}",
    response = InnovationDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/innovations/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InnovationDTO> findInnovationById(
    @ApiParam(value = "${Innovation.innovation.GET.id.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Innovation.innovation.GET.id.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${Innovation.innovation.GET.id.param.year}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${Innovation.innovation.GET.id.param.phase}", required = true) @RequestParam String phase) {

    ResponseEntity<InnovationDTO> response =
      this.innovationItem.findInnovationById(id, CGIAREntity, year, phase, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Innovation.innovation.GET.id.404"));
    }
    return response;
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = this.userManager.getUser(principal);
    return user;
  }

  @ApiOperation(tags = {"Table 4 - CRP Innovations"}, value = "${Innovation.innovation.PUT.value}",
    response = InnovationDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/innovations", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> putInnovation(
    @ApiParam(value = "${Innovation.innovation.PUT.param.id}", required = true) @RequestParam Long id,
    @ApiParam(value = "${Innovation.innovation.PUT.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Innovation.innovation.PUT.param.innovation}",
      required = true) @Valid @RequestBody NewInnovationDTO newInnovationDTO) {
    Long innovationId = this.innovationItem.putInnovationById(id, newInnovationDTO, CGIAREntity, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(innovationId, HttpStatus.OK);
    return response;

  }
}

