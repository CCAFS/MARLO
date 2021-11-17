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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist;

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.statusPlannedOutcomes.StatusPlannedMilestonesItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.statusPlannedOutcomes.StatusPlannedOutcomesItem;
import org.cgiar.ccafs.marlo.rest.dto.NewStatusPlannedMilestoneDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewStatusPlannedOutcomeDTO;
import org.cgiar.ccafs.marlo.rest.dto.StatusPlannedMilestonesDTO;
import org.cgiar.ccafs.marlo.rest.dto.StatusPlannedOutcomesDTO;
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
@Api(tags = "Table 5 - Status of Planned Outcomes and Milestones")
public class StatusPlannedOutcomes {

  private static final Logger LOG = LoggerFactory.getLogger(StatusPlannedOutcomes.class);
  @Autowired
  private Environment env;
  private final UserManager userManager;
  private StatusPlannedOutcomesItem<StatusPlannedOutcomesDTO> statusPlannedOutcomesItem;
  private StatusPlannedMilestonesItem<StatusPlannedMilestonesDTO> statusPlannedMilestonesItem;

  @Inject
  public StatusPlannedOutcomes(StatusPlannedOutcomesItem<StatusPlannedOutcomesDTO> statusPlannedOutcomesItem,
    StatusPlannedMilestonesItem<StatusPlannedMilestonesDTO> statusPlannedMilestonesItem, UserManager userManager) {
    super();
    this.userManager = userManager;
    this.statusPlannedOutcomesItem = statusPlannedOutcomesItem;
    this.statusPlannedMilestonesItem = statusPlannedMilestonesItem;
  }

  @ApiOperation(tags = {"Table 5 - Status of Planned Outcomes and Milestones"},
    value = "${StatusPlannedOutcomes.milestones.POST.value}", response = StatusPlannedOutcomesDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/newstatusMilestones", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createStatusPlannedMilestone(
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.POST.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.POST.param.statusPlannedMilestone}",
      required = true) @Valid @RequestBody NewStatusPlannedMilestoneDTO newStatusPlanneMilestoneDTO) {
    try {
      Long reportSythesisProgressOutcomesID = this.statusPlannedMilestonesItem
        .createStatusPlannedMilestone(newStatusPlanneMilestoneDTO, CGIAREntity, this.getCurrentUser());


      ResponseEntity<Long> response = new ResponseEntity<Long>(reportSythesisProgressOutcomesID, HttpStatus.OK);
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("StatusPlannedOutcomes.milestones.POST.id.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }

  @ApiOperation(tags = {"Table 5 - Status of Planned Outcomes and Milestones"},
    value = "${StatusPlannedOutcomes.outcomes.POST.value}", response = StatusPlannedMilestonesDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/statusOutcomes", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createStatusPlannedOutcome(
    @ApiParam(value = "${StatusPlannedOutcomes.outcomes.POST.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${StatusPlannedOutcomes.outcomes.POST.param.statusPlannedOutcome}",
      required = true) @Valid @RequestBody NewStatusPlannedOutcomeDTO newStatusPlannedOutcomeDTO) {
    Long reportSythesisProgressOutcomeMilestoneID = this.statusPlannedOutcomesItem
      .createStatusPlannedOutcome(newStatusPlannedOutcomeDTO, CGIAREntity, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(reportSythesisProgressOutcomeMilestoneID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("StatusPlannedOutcomes.outcomes.GET.id.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Table 5 - Status of Planned Outcomes and Milestones"},
    value = "${StatusPlannedOutcomes.milestones.DELETE.value}", response = StatusPlannedOutcomesDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/statusMilestones", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> deleteStatusPlannedMilestone(
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.DELETE.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.DELETE.param.phase}",
      required = true) @RequestParam String strphase,
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.DELETE.param.year}",
      required = true) @RequestParam Integer year,
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.DELETE.param.crpprogram.id}",
      required = true) @RequestParam String flagship,
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.DELETE.param.outcome.id}",
      required = true) @RequestParam String outcome,
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.DELETE.param.milestone.id}",
      required = true) @RequestParam String milestone) {
    Long reportSythesisProgressOutcomesID = this.statusPlannedMilestonesItem.deleteStatusPlannedMilestone(strphase,
      year, flagship, outcome, milestone, CGIAREntity, this.getCurrentUser());


    ResponseEntity<Long> response = new ResponseEntity<Long>(reportSythesisProgressOutcomesID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("StatusPlannedOutcomes.milestones.DELETE.id.404"));
    }
    return response;
  }


  @ApiOperation(tags = {"Table 5 - Status of Planned Outcomes and Milestones"},
    value = "${StatusPlannedOutcomes.milestones.GET.id.value}", response = StatusPlannedMilestonesDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/statusMilestones", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StatusPlannedMilestonesDTO> findStatusPlannedMilestone(
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.GET.id.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.GET.id.param.outcome}",
      required = true) @RequestParam String outcomeID,
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.GET.id.param.milestone}",
      required = true) @RequestParam String milestoneID,
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.GET.id.param.year}",
      required = true) @RequestParam Integer year,
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.GET.id.param.phase}",
      required = true) @RequestParam String phase) {
    ResponseEntity<StatusPlannedMilestonesDTO> response = null;
    try {
      response = this.statusPlannedMilestonesItem.findStatusPlannedMilestone(outcomeID, milestoneID, CGIAREntity, year,
        phase, this.getCurrentUser());
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (response != null && response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("StatusPlannedOutcomes.milestones.GET.id.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Table 5 - Status of Planned Outcomes and Milestones"},
    value = "${StatusPlannedOutcomes.outcomes.GET.id.value}", response = StatusPlannedOutcomesDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/statusOutcomes/{outcomeID}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StatusPlannedOutcomesDTO> findStatusPlannedOutcome(
    @ApiParam(value = "${StatusPlannedOutcomes.outcomes.GET.id.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${StatusPlannedOutcomes.outcomes.GET.id.param.outcome}",
      required = true) @PathVariable String outcomeID,
    @ApiParam(value = "${StatusPlannedOutcomes.outcomes.GET.id.param.year}",
      required = true) @RequestParam Integer year,
    @ApiParam(value = "${StatusPlannedOutcomes.outcomes.GET.id.param.phase}",
      required = true) @RequestParam String phase) {
    ResponseEntity<StatusPlannedOutcomesDTO> response = null;
    try {
      response = this.statusPlannedOutcomesItem.findStatusPlannedOutcome(outcomeID, CGIAREntity, year, phase,
        this.getCurrentUser());
    } catch (Exception e) {
      e.printStackTrace();
    }

    if (response != null && response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("StatusPlannedOutcomes.outcomes.GET.id.404"));
    }
    return response;
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = this.userManager.getUser(principal);
    return user;
  }

  @ApiOperation(tags = {"Table 5 - Status of Planned Outcomes and Milestones"},
    value = "${StatusPlannedOutcomes.milestones.PUT.value}", response = Long.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/statusMilestones", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> updateStatusPlannedMilestone(
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.PUT.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${StatusPlannedOutcomes.milestones.PUT.param.statusPlannedMilestone}",
      required = true) @Valid @RequestBody NewStatusPlannedMilestoneDTO newStatusPlanneMilestoneDTO) {
    try {
      Long reportSythesisProgressOutcomesID = this.statusPlannedMilestonesItem
        .updateStatusPlannedMilestone(newStatusPlanneMilestoneDTO, CGIAREntity, this.getCurrentUser());

      ResponseEntity<Long> response = new ResponseEntity<Long>(reportSythesisProgressOutcomesID, HttpStatus.OK);
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("StatusPlannedOutcomes.milestones.PUT.id.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }

  }

  @ApiOperation(tags = {"Table 5 - Status of Planned Outcomes and Milestones"},
    value = "${StatusPlannedOutcomes.outcomes.PUT.value}", response = Long.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/statusOutcomes", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> updateStatusPlannedOutcome(
    @ApiParam(value = "${StatusPlannedOutcomes.outcomes.PUT.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${StatusPlannedOutcomes.outcomes.PUT.param.statusPlannedOutcome}",
      required = true) @Valid @RequestBody NewStatusPlannedOutcomeDTO newStatusPlannedOutcomeDTO) {
    Long reportSythesisProgressOutcomesID = this.statusPlannedOutcomesItem
      .updateStatusPlannedOutcome(newStatusPlannedOutcomeDTO, CGIAREntity, this.getCurrentUser());


    ResponseEntity<Long> response = new ResponseEntity<Long>(reportSythesisProgressOutcomesID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("StatusPlannedOutcomes.outcomes.PUT.id.404"));
    }
    return response;
  }
}
