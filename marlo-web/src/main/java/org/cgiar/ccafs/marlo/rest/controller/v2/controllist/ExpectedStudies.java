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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.expectedStudies.ExpectedStudiesItem;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectExpectedStudyDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectExpectedStudyDTO;
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
@Api(tags = "Table 3 - Outcome/ Impact Case Reports")
public class ExpectedStudies {

  private static final Logger LOG = LoggerFactory.getLogger(ExpectedStudies.class);
  @Autowired
  private Environment env;
  private final UserManager userManager;
  private ExpectedStudiesItem<ProjectExpectedStudyDTO> expectedStudiesItem;

  @Inject
  public ExpectedStudies(ExpectedStudiesItem<ProjectExpectedStudyDTO> expectedStudiesItem, UserManager userManager) {
    this.expectedStudiesItem = expectedStudiesItem;
    this.userManager = userManager;
  }

  @ApiOperation(tags = {"Table 3 - Outcome/ Impact Case Reports"}, value = "${ExpectedStudies.OICR.POST.value}",
    response = ProjectExpectedStudyDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/OICR", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createExpectedStudy(
    @ApiParam(value = "${ExpectedStudies.OICR.POST.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ExpectedStudies.OICR.POST.param.OICR}",
      required = true) @Valid @RequestBody NewProjectExpectedStudyDTO newProjectExpectedStudyDTO) {
    Long policyId = new Long(0);
    try {
      policyId =
        this.expectedStudiesItem.createExpectedStudy(newProjectExpectedStudyDTO, CGIAREntity, this.getCurrentUser());
    } catch (Exception e) {
      e.printStackTrace();
    }

    ResponseEntity<Long> response = new ResponseEntity<Long>(policyId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ExpectedStudies.OICR.GET.id.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Table 3 - Outcome/ Impact Case Reports"}, value = "${ExpectedStudies.OICR.DELETE.id.value}",
    response = ProjectExpectedStudyDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/OICR/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProjectExpectedStudyDTO> deleteExpectedStudyById(
    @ApiParam(value = "${ExpectedStudies.innovation.DELETE.id.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ExpectedStudies.OICR.DELETE.id.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${ExpectedStudies.OICR.DELETE.id.param.year}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${ExpectedStudies.OICR.DELETE.id.param.phase}", required = true) @RequestParam String phase) {

    ResponseEntity<ProjectExpectedStudyDTO> response =
      this.expectedStudiesItem.deleteExpectedStudyById(id, CGIAREntity, year, phase, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ExpectedStudies.OICR.DELETE.id.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Table 3 - Outcome/ Impact Case Reports"}, value = "${ExpectedStudies.OICR.GET.id.value}",
    response = ProjectExpectedStudyDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/OICR/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ProjectExpectedStudyDTO> findExpectedStudyById(
    @ApiParam(value = "${ExpectedStudies.OICR.GET.id.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ExpectedStudies.OICR.GET.id.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${ExpectedStudies.OICR.GET.id.param.year}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${ExpectedStudies.OICR.GET.id.param.phase}", required = true) @RequestParam String phase) {


    ResponseEntity<ProjectExpectedStudyDTO> response = null;
    try {
      response = this.expectedStudiesItem.findExpectedStudyById(id, CGIAREntity, year, phase, this.getCurrentUser());
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("ExpectedStudies.OICR.GET.id.404"));
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return response;
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = this.userManager.getUser(principal);
    return user;
  }

  @ApiOperation(tags = {"Table 3 - Outcome/ Impact Case Reports"}, value = "${ExpectedStudies.OICR.PUT.value}",
    response = ProjectExpectedStudyDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/OICR/{id}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> putInnovation(
    @ApiParam(value = "${ExpectedStudies.OICR.PUT.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ExpectedStudies.OICR.PUT.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${ExpectedStudies.OICR.PUT.param.innovation}",
      required = true) @Valid @RequestBody NewProjectExpectedStudyDTO newProjectExpectedStudyDTO) {
    Long expectedStudyId =
      this.expectedStudiesItem.putExpectedStudyById(id, newProjectExpectedStudyDTO, CGIAREntity, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(expectedStudyId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ExpectedStudies.OICR.GET.id.404"));
    }
    return response;
  }
}
