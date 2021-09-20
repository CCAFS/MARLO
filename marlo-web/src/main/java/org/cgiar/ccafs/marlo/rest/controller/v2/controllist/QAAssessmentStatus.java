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

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.qa.IndicatorStatusItem;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.rest.services.qa.IndicatorStatus;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;

import javax.inject.Named;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Configuration
@PropertySource("classpath:clarisa.properties")
@RestController
@Api(tags = "QA Control Lists")
@ApiIgnore
@Named
public class QAAssessmentStatus {

  private IndicatorStatusItem<QAAssessmentStatus> indicatorStatusItem;
  @Autowired
  private Environment env;


  public QAAssessmentStatus(IndicatorStatusItem<QAAssessmentStatus> indicatorStatusItem) {
    super();
    this.indicatorStatusItem = indicatorStatusItem;
  }

  @ApiOperation(tags = {"QA Control Lists"}, value = "${QAControlLists.indicator-status.all.value}",
    response = IndicatorStatus.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/indicator-status/indicator-code/{indicatorCode}/crp/{crp}/year/{year}",
    method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<IndicatorStatus>> findAllIndicatorStatus(
    @ApiParam(value = "${QAControlLists.indicator-status.param.indicator}",
      required = true) @PathVariable Long indicatorCode,
    @ApiParam(value = "${QAControlLists.indicator-status.param.crp}", required = true) @PathVariable String crp,
    @ApiParam(value = "${QAControlLists.indicator-status.param.year}", required = true) @PathVariable Long year) {

    ResponseEntity<List<IndicatorStatus>> response =
      this.indicatorStatusItem.findAllIndicatorStatusByIndicatorIdCrpAndPhase(indicatorCode, crp, year);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("QAControlLists.indicator-status.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"QA Control Lists"}, value = "${QAControlLists.indicator-status.value}",
    response = IndicatorStatus.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/indicator-status/indicator-code/{indicatorCode}/indicator/{id}/crp/{crp}/year/{year}",
    method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<IndicatorStatus> findIndicatorStatus(
    @ApiParam(value = "${QAControlLists.indicator-status.param.indicator}",
      required = true) @PathVariable Long indicatorCode,
    @ApiParam(value = "${QAControlLists.indicator-status.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${QAControlLists.indicator-status.param.crp}", required = true) @PathVariable String crp,
    @ApiParam(value = "${QAControlLists.indicator-status.param.year}", required = true) @PathVariable Long year) {

    ResponseEntity<IndicatorStatus> response =
      this.indicatorStatusItem.findIndicatorStatusByIndicatorIdCrpIdAndPhase(indicatorCode, crp, id, year);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("QAControlLists.indicator-status.404"));
    }

    return response;
  }
}
