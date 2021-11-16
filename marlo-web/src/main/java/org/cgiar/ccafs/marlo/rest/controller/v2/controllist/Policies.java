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

/**
 * @author Diego Perez - CIAT/CCAFS
 **/
package org.cgiar.ccafs.marlo.rest.controller.v2.controllist;

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.policies.PolicyItem;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPolicyARDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.ArrayList;
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
@Api(tags = "Table 2 - CRP Policies")
public class Policies {

  private static final Logger LOG = LoggerFactory.getLogger(Policies.class);
  @Autowired
  private Environment env;
  private final UserManager userManager;
  private PolicyItem<ProjectPolicyDTO> policyItem;


  @Inject
  public Policies(PolicyItem<ProjectPolicyDTO> policyItem, UserManager userManager) {
    this.userManager = userManager;
    this.policyItem = policyItem;
  }

  @ApiOperation(tags = {"Table 2 - CRP Policies"}, value = "${Policy.policies.POST.value}",
    response = ProjectPolicyDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/policies", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createPolicy(
    @ApiParam(value = "${Policy.policies.POST.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Policy.policies.POST.param.policy}",
      required = true) @Valid @RequestBody NewProjectPolicyDTO newProjectPolicyDTO) {

    Long policyId = this.policyItem.createPolicy(newProjectPolicyDTO, CGIAREntity, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(policyId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Policy.policies.GET.id.404"));
    }
    return response;

  }

  @ApiOperation(tags = {"Table 2 - CRP Policies"}, value = "${Policy.policies.DELETE.id.value}",
    response = ProjectPolicyDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/policies/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProjectPolicyDTO> deletePolicyById(
    @ApiParam(value = "${Policy.policies.DELETE.id.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Policy.policies.DELETE.id.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${Policy.policies.DELETE.id.param.year}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${Policy.policies.DELETE.id.param.phase}", required = true) @RequestParam String phase) {

    ResponseEntity<ProjectPolicyDTO> response = null;

    response = this.policyItem.deletePolicyById(id, CGIAREntity, year, phase, this.getCurrentUser());

    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Policy.policies.DELETE.id.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Table 2 - CRP Policies"}, value = "${Policy.policies.GET.all.value}",
    response = ProjectPolicyDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/policies", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ProjectPolicyARDTO> findAllPoliciesByGlobalUnit(
    @ApiParam(value = "${Policy.policies.GET.all.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Policy.policies.GET.all.param.year}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${Policy.policies.GET.all.param.phase}", required = true) @RequestParam String phase) {

    List<ProjectPolicyARDTO> policyList = new ArrayList<ProjectPolicyARDTO>();


    policyList = this.policyItem.findAllPoliciesByGlobalUnit(CGIAREntity, year, phase, this.getCurrentUser());

    return policyList;
  }

  @ApiOperation(tags = {"Table 2 - CRP Policies"}, value = "${Policy.policies.GET.id.value}",
    response = ProjectPolicyDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/policies/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProjectPolicyDTO> findPolicyById(
    @ApiParam(value = "${Policy.policies.GET.id.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Policy.policies.GET.id.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${Policy.policies.GET.id.param.year}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${Policy.policies.GET.id.param.phase}", required = true) @RequestParam String phase) {


    ResponseEntity<ProjectPolicyDTO> response = null;

    response = this.policyItem.findPolicyById(id, CGIAREntity, year, phase, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Policy.policies.GET.id.404"));
    }


    return response;
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = this.userManager.getUser(principal);
    return user;
  }

  @ApiOperation(tags = {"Table 2 - CRP Policies"}, value = "${Policy.policies.PUT.value}",
    response = ProjectPolicyDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/policies/{id}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> putPolicy(
    @ApiParam(value = "${Policy.policies.PUT.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Policy.policies.PUT.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${Policy.policies.PUT.param.policy}",
      required = true) @Valid @RequestBody NewProjectPolicyDTO newPolicyDTO) {
    Long policyID = this.policyItem.putPolicyById(id, newPolicyDTO, CGIAREntity, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(policyID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Policy.policies.GET.id.404"));
    }
    return response;
  }

}
