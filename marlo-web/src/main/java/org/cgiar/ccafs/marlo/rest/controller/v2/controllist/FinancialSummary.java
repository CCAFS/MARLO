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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.FinancialSumary.FinancialSummaryItem;
import org.cgiar.ccafs.marlo.rest.dto.FinancialSumaryDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewFinancialSummaryDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.security.Permission;

import javax.validation.Valid;

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
@Api(tags = "Table 13 - CRP Financial Report")
public class FinancialSummary {

  private static final Logger LOG = LoggerFactory.getLogger(FinancialSummary.class);
  @Autowired
  private Environment env;
  private final UserManager userManager;
  private FinancialSummaryItem<FinancialSumaryDTO> financialSummaryItem;

  public FinancialSummary(UserManager userManager, FinancialSummaryItem<FinancialSumaryDTO> financialSummaryItem) {
    super();
    this.financialSummaryItem = financialSummaryItem;
    this.userManager = userManager;
  }

  @ApiOperation(tags = {"Table 13 - CRP Financial Report"}, value = "${Expenditure.Example.POST.value}",
    response = FinancialSumaryDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/FinancialSummary", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createFinancialSummary(
    @ApiParam(value = "${Expenditure.Example.POST.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Expenditure.Example.POST.param.expenditureExample}",
      required = true) @Valid @RequestBody NewFinancialSummaryDTO newFinancialSummaryDTO) {
    Long financialSummaryId =
      financialSummaryItem.createFinancialSummary(newFinancialSummaryDTO, CGIAREntity, this.getCurrentUser());
    ResponseEntity<Long> response = new ResponseEntity<Long>(financialSummaryId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Expenditure.Example.GET.id.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Table 13 - CRP Financial Report"}, value = "${Expenditure.Example.GET.value}",
    response = FinancialSumaryDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/FinancialSumamary/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FinancialSumaryDTO> findFinancialSummaryById(
    @ApiParam(value = "${Expenditure.Example.GET.id.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${Expenditure.Example.GET.id.param.year}", required = true) @RequestParam Integer year,
    @ApiParam(value = "${Expenditure.Example.GET.id.param.phase}", required = true) @RequestParam String phase) {

    ResponseEntity<FinancialSumaryDTO> response = null;
    try {
      response = this.financialSummaryItem.findFinancialSumaryList(CGIAREntity, year, phase, this.getCurrentUser());
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("Expenditure.Example.GET.id.404"));
      }
    } catch (Exception e) {
      LOG.trace("Error findFinancialSummaryById");
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

}
