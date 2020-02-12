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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.progressTowards.ProgressTowardsItem;
import org.cgiar.ccafs.marlo.rest.dto.NewProgressTowardsSRFTargetDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProgressTowardsSRFTargetsDTO;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "ProgressTowards")
public class ProgressTowards {

  private static final Logger LOG = LoggerFactory.getLogger(Policies.class);
  @Autowired
  private Environment env;
  private final UserManager userManager;
  private ProgressTowardsItem<ProgressTowardsSRFTargetsDTO> progressTowardsItem;


  @Inject
  public ProgressTowards(ProgressTowardsItem<ProgressTowardsSRFTargetsDTO> progressTowardsItem,
    UserManager userManager) {
    this.userManager = userManager;
    this.progressTowardsItem = progressTowardsItem;
  }

  @ApiOperation(tags = {"Table 1 - Progress towards SRF targets"},
    value = "${ProgressTowards.progresstowardsSRF.POST.value}", response = ProgressTowardsSRFTargetsDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/progresstowards", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createPolicy(
    @ApiParam(value = "${ProgressTowards.progresstowardsSRF.POST.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ProgressTowards.progresstowardsSRF.POST.param.progresstowardsSRF}",
      required = true) @Valid @RequestBody NewProgressTowardsSRFTargetDTO newProgressTowardsSRFTargetDTO) {
    Long policyId = new Long(0);
    try {
      policyId = this.progressTowardsItem.createProgressTowards(newProgressTowardsSRFTargetDTO, CGIAREntity,
        this.getCurrentUser());
    } catch (Exception e) {
      e.printStackTrace();
    }

    ResponseEntity<Long> response = new ResponseEntity<Long>(policyId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ProgressTowards.progresstowardsSRF.GET.id.404"));
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
