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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.actionTaken.ActionTakenItem;
import org.cgiar.ccafs.marlo.rest.dto.NewRelevantEvaluationDTO;
import org.cgiar.ccafs.marlo.rest.dto.RelevantEvaluationDTO;
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
@Api(tags = "Table 11 - Update on Actions Taken in Response to Relevant Evaluations")
public class RelevantEvaluations {

  private static final Logger LOG = LoggerFactory.getLogger(RelevantEvaluations.class);
  @Autowired
  private Environment env;

  private ActionTakenItem<RelevantEvaluationDTO> actionTakenItem;

  private final UserManager userManager;


  @Inject
  public RelevantEvaluations(ActionTakenItem<RelevantEvaluationDTO> actionTakenItem, UserManager userManager) {
    this.actionTakenItem = actionTakenItem;
    this.userManager = userManager;

  }

  /**
   * Create a new Relevant Evaluation *
   * 
   * @param acronym of global unit
   * @param newRelevantEvaluationDTO with Relevant Evaluation info
   * @return a InnovationDTO with the innovation created
   */
  @ApiOperation(tags = {"Table 11 - Update on Actions Taken in Response to Relevant Evaluations"},
    value = "${ActionTaken.relevantEvaluation.POST.value}", response = RelevantEvaluationDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/relevantEvaluations", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createRelevantActions(
    @ApiParam(value = "${ActionTaken.relevantEvaluation.POST.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ActionTaken.relevantEvaluation.POST.param.relevantEvaluation}",
      required = true) @Valid @RequestBody NewRelevantEvaluationDTO newRelevantEvaluationDTO) {
    Long relevantEvaluationID =
      this.actionTakenItem.createActionTaken(newRelevantEvaluationDTO, CGIAREntity, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(relevantEvaluationID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ActionTaken.relevantEvaluation.GET.id.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Table 11 - Update on Actions Taken in Response to Relevant Evaluations"},
    value = "${ActionTaken.relevantEvaluation.DELETE.value}", response = RelevantEvaluationDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/relevantEvaluations/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public Long deleteActionTakenById(
    @ApiParam(value = "${ActionTaken.relevantEvaluation.DELETE.param.CGIAR.value}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ActionTaken.relevantEvaluation.DELETE.id.value}", required = true) @PathVariable Long id,
    @ApiParam(value = "${ActionTaken.relevantEvaluation.DELETE.year.value}",
      required = true) @RequestParam Integer year,
    @ApiParam(value = "${ActionTaken.relevantEvaluation.DELETE.phase.value}",
      required = true) @RequestParam String phase) {

    Long relevantEvaluationID =
      this.actionTakenItem.deleteActionTaken(id, CGIAREntity, year, phase, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(relevantEvaluationID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ActionTaken.relevantEvaluation.GET.id.404"));
    }
    return relevantEvaluationID;
  }

  @ApiOperation(tags = {"Table 11 - Update on Actions Taken in Response to Relevant Evaluations"},
    value = "${ActionTaken.relevantEvaluation.GET.value}", response = RelevantEvaluationDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/relevantEvaluations/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RelevantEvaluationDTO> findActionTakenById(
    @ApiParam(value = "${ActionTaken.relevantEvaluation.GET.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ActionTaken.relevantEvaluation.GET.id.value}", required = true) @PathVariable Long id,
    @ApiParam(value = "${ActionTaken.relevantEvaluation.GET.year.value}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${ActionTaken.relevantEvaluation.GET.phase.value}",
      required = true) @RequestParam String phase) {

    ResponseEntity<RelevantEvaluationDTO> response =
      this.actionTakenItem.getActionTaken(id, CGIAREntity, year, phase, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ActionTaken.relevantEvaluation.GET.id.404"));
    }
    return response;
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = this.userManager.getUser(principal);
    return user;
  }

  @ApiOperation(tags = {"Table 11 - Update on Actions Taken in Response to Relevant Evaluations"},
    value = "${ActionTaken.relevantEvaluation.PUT.value}", response = RelevantEvaluationDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/relevantEvaluations/{id}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> updateActionTaken(
    @ApiParam(value = "${ActionTaken.relevantEvaluation.PUT.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ActionTaken.relevantEvaluation.PUT.id.value}", required = true) @PathVariable Long id,
    @ApiParam(value = "${ActionTaken.relevantEvaluation.PUT.param.relevantEvaluation}",
      required = true) @Valid @RequestBody NewRelevantEvaluationDTO newRelevantEvaluationDTO) {
    Long relevantEvaluationID =
      this.actionTakenItem.updateActionTaken(id, newRelevantEvaluationDTO, CGIAREntity, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(relevantEvaluationID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ActionTaken.relevantEvaluation.GET.id.404"));
    }
    return response;
  }

}
