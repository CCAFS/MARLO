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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.Expenditures.ExpendituresItem;
import org.cgiar.ccafs.marlo.rest.dto.NewW1W2ExpenditureDTO;
import org.cgiar.ccafs.marlo.rest.dto.W1W2ExpenditureDTO;
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
@Api(tags = "Table 12 - Examples of W1/2 Use")
public class Expenditures {

  private static final Logger LOG = LoggerFactory.getLogger(Expenditures.class);
  @Autowired
  private Environment env;
  private final UserManager userManager;
  private ExpendituresItem<W1W2ExpenditureDTO> expendituresItem;

  @Inject
  public Expenditures(UserManager userManager, ExpendituresItem<W1W2ExpenditureDTO> expendituresItem) {

    this.userManager = userManager;
    this.expendituresItem = expendituresItem;
  }

  @ApiOperation(tags = {"Table 12 - Examples of W1/2 Use"}, value = "${Expenditure.Example.POST.value}",
    response = W1W2ExpenditureDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/ExpenditureExample", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createExpenditureExample(
    @ApiParam(value = "${Expenditure.Example.POST.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Expenditure.Example.POST.param.expenditureExample}",
      required = true) @Valid @RequestBody NewW1W2ExpenditureDTO newW1W2ExpenditureDTO) {
    Long ExpenditureAreaId =
      this.expendituresItem.createExpenditure(newW1W2ExpenditureDTO, CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(ExpenditureAreaId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Expenditure.Example.GET.id.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Table 12 - Examples of W1/2 Use"}, value = "${Expenditure.Example.DELETE.value}",
    response = W1W2ExpenditureDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/ExpenditureExample/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> deleteExpenditureExampleById(
    @ApiParam(value = "${Expenditure.Example.DELETE.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Expenditure.Example.DELETE.id.value}", required = true) @PathVariable Long id,
    @ApiParam(value = "${Expenditure.Example.DELETE.year.value}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${Expenditure.Example.DELETE.phase.value}", required = true) @RequestParam String phase) {

    Long ExpenditureAreaId =
      this.expendituresItem.deleteExpenditure(id, CGIAREntity, year, phase, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(ExpenditureAreaId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Expenditure.Example.GET.id.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Table 12 - Examples of W1/2 Use"}, value = "${Expenditure.Example.GET.value}",
    response = W1W2ExpenditureDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/ExpenditureExample/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<W1W2ExpenditureDTO> findExpenditureExampleById(
    @ApiParam(value = "${Expenditure.Example.GET.id.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Expenditure.Example.GET.id.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${Expenditure.Example.GET.id.param.year}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${Expenditure.Example.GET.id.param.phase}", required = true) @RequestParam String phase) {

    ResponseEntity<W1W2ExpenditureDTO> response = null;
    try {
      response = this.expendituresItem.getExpenditure(id, CGIAREntity, year, phase, this.getCurrentUser());
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("Expenditure.Example.GET.id.404"));
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

  @ApiOperation(tags = {"Table 12 - Examples of W1/2 Use"}, value = "${Expenditure.Example.PUT.value}",
    response = W1W2ExpenditureDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/ExpenditureExample/{id}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> updateExpenditureExample(
    @ApiParam(value = "${Expenditure.Example.PUT.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Expenditure.Example.PUT.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${Expenditure.Example.PUT.param.expenditureExample}",
      required = true) @Valid @RequestBody NewW1W2ExpenditureDTO newW1W2ExpenditureDTO) {
    Long expectedStudyId =
      this.expendituresItem.updateExpenditure(id, newW1W2ExpenditureDTO, CGIAREntity, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(expectedStudyId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ExpectedStudies.Example.GET.id.404"));
    }
    return response;
  }

}
