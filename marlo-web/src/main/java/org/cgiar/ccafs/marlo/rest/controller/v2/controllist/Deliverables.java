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
import org.cgiar.ccafs.marlo.data.model.Deliverable;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.Deliverables.DeliverablesItem;
import org.cgiar.ccafs.marlo.rest.dto.NewPublicationDTO;
import org.cgiar.ccafs.marlo.rest.dto.PublicationDTO;
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

@RestController
@Api(tags = "Table 6 - Peer-reviewed publications")
public class Deliverables {

  private static final Logger LOG = LoggerFactory.getLogger(Deliverable.class);
  @Autowired
  private Environment env;

  private DeliverablesItem<PublicationDTO> publicationItem;
  private final UserManager userManager;

  /**
   * Create a new Deliverables *
   * 
   * @param acronym of global unit
   * @param DeliverablenDTO with basic deliverable info
   * @return a DeliverablenDTO with the deliverable created
   */
  @Inject
  public Deliverables(DeliverablesItem<PublicationDTO> publicationItem, UserManager userManager) {
    super();
    this.publicationItem = publicationItem;
    this.userManager = userManager;
  }

  @ApiOperation(tags = {"Table 6 - Peer-reviewed publications"}, value = "${Deliverables.deliverable.POST.value}",
    response = PublicationDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/publications", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createDeliverable(
    @ApiParam(value = "${Deliverables.deliverable.POST.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Deliverables.deliverable.POST.param.deliverable}",
      required = true) @Valid @RequestBody NewPublicationDTO newPublicationDTO) {
    Long innovationId = this.publicationItem.createDeliverable(newPublicationDTO, CGIAREntity, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(innovationId, HttpStatus.OK);
    return response;
  }

  @ApiOperation(tags = {"Table 6 - Peer-reviewed publications"}, value = "${Deliverables.deliverable.DELETE.id.value}",
    response = PublicationDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/publications/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PublicationDTO> deleteDeliverableById(
    @ApiParam(value = "${Deliverables.deliverable.DELETE.id.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Deliverables.deliverable.DELETE.id.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${Deliverables.deliverable.DELETE.id.param.year}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${Deliverables.deliverable.DELETE.id.param.phase}",
      required = true) @RequestParam String phase) {

    ResponseEntity<PublicationDTO> response =
      this.publicationItem.deleteDeliverableById(id, CGIAREntity, year, phase, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Deliverables.deliverable.DELETE.id.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"Table 6 - Peer-reviewed publications"}, value = "${Deliverables.deliverable.GET.id.value}",
    response = PublicationDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/publications/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PublicationDTO> findDeliverableById(
    @ApiParam(value = "${Deliverables.deliverable.GET.id.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Deliverables.deliverable.GET.id.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${Deliverables.deliverable.GET.id.param.year}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${Deliverables.deliverable.GET.id.param.phase}", required = true) @RequestParam String phase) {

    ResponseEntity<PublicationDTO> response =
      this.publicationItem.findDeliverableById(id, CGIAREntity, year, phase, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Deliverables.deliverable.GET.id.404"));
    }

    return response;
  }

  /**
   * Get all deliverables by phase and CRP *
   * 
   * @return a DeliverablesDTO with deliverables item
   */
  @ApiOperation(tags = {"Table 6 - Peer-reviewed publications"}, value = "${Deliverables.deliverable.GET.all.value}",
    response = PublicationDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/publications", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PublicationDTO> getAllDeliverables(
    @ApiParam(value = "${Deliverables.deliverable.GET.all.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Deliverables.deliverable.GET.all.param.year}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${Deliverables.deliverable.GET.all.param.phase}", required = true) @RequestParam String phase) {
    return this.publicationItem.getAllDeliverables(CGIAREntity, year, phase, this.getCurrentUser());
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = this.userManager.getUser(principal);
    return user;
  }

  @ApiOperation(tags = {"Table 6 - Peer-reviewed publications"}, value = "${Deliverables.deliverable.PUT.value}",
    response = PublicationDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/publications/{id}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> putDeliverableById(
    @ApiParam(value = "${Deliverables.deliverable.PUT.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Deliverables.deliverable.PUT.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${Deliverables.deliverable.PUT.param.deliverable}",
      required = true) @Valid @RequestBody NewPublicationDTO newPublicationDTO) {

    Long deliverableID =
      this.publicationItem.putDeliverableById(id, newPublicationDTO, CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(deliverableID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Deliverables.deliverable.PUT.id.404"));
    }

    return response;
  }

}
