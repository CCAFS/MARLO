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
import org.cgiar.ccafs.marlo.rest.dto.KeyExternalPartnershipDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewSrfProgressTowardsTargetDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfProgressTowardsTargetsDTO;
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
@Api(tags = "ProgressTowards")
public class ProgressTowards {

  private static final Logger LOG = LoggerFactory.getLogger(ProgressTowards.class);
  @Autowired
  private Environment env;
  private final UserManager userManager;
  private ProgressTowardsItem<SrfProgressTowardsTargetsDTO> progressTowardsItem;


  @Inject
  public ProgressTowards(ProgressTowardsItem<SrfProgressTowardsTargetsDTO> progressTowardsItem,
    UserManager userManager) {
    this.userManager = userManager;
    this.progressTowardsItem = progressTowardsItem;
  }

  @ApiOperation(tags = {"Table 1 - Progress towards SRF targets"},
    value = "${ProgressTowards.progresstowardsSRF.POST.value}", response = SrfProgressTowardsTargetsDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/progresstowards", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createProgressTowards(
    @ApiParam(value = "${ProgressTowards.progresstowardsSRF.POST.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ProgressTowards.progresstowardsSRF.POST.param.progresstowardsSRF}",
      required = true) @Valid @RequestBody NewSrfProgressTowardsTargetDTO newSrfProgressTowardsDTO) {
    Long policyId = new Long(0);
    try {
      policyId =
        this.progressTowardsItem.createProgressTowards(newSrfProgressTowardsDTO, CGIAREntity, this.getCurrentUser());
    } catch (Exception e) {
      e.printStackTrace();
    }

    ResponseEntity<Long> response = new ResponseEntity<Long>(policyId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ProgressTowards.progresstowardsSRF.GET.id.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Table 1 - Progress towards SRF targets"},
    value = "${ProgressTowards.progresstowardsSRF.DELETE.id.value}", response = SrfProgressTowardsTargetsDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/progresstowards/{id}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SrfProgressTowardsTargetsDTO> deleteKeyExternalPartnershipById(
    @ApiParam(value = "${ProgressTowards.progresstowardsSRF.DELETE.id.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ProgressTowards.progresstowardsSRF.DELETE.id.param.id}",
      required = true) @PathVariable Long id,
    @ApiParam(value = "${ProgressTowards.progresstowardsSRF.DELETE.id.param.year}",
      required = true) @RequestParam Integer year,
    @ApiParam(value = "${ProgressTowards.progresstowardsSRF.DELETE.id.param.phase}",
      required = true) @RequestParam String phase) {
    ResponseEntity<SrfProgressTowardsTargetsDTO> response =
      this.progressTowardsItem.deleteProgressTowardsById(id, CGIAREntity, year, phase, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ProgressTowards.progresstowardsSRF.DELETE.id.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"Table 1 - Progress towards SRF targets"},
    value = "${ProgressTowards.progresstowardsSRF.GET.id.value}", response = KeyExternalPartnershipDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/progresstowards/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<SrfProgressTowardsTargetsDTO> findKeyExternalPartnershipById(
    @ApiParam(value = "${ProgressTowards.progresstowardsSRF.GET.id.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${ProgressTowards.progresstowardsSRF.GET.id.param.id}", required = true) @PathVariable Long id,
    @ApiParam(value = "${ProgressTowards.progresstowardsSRF.GET.id.param.year}",
      required = true) @RequestParam Integer year,
    @ApiParam(value = "${ProgressTowards.progresstowardsSRF.GET.id.param.phase}",
      required = true) @RequestParam String phase) {

    ResponseEntity<SrfProgressTowardsTargetsDTO> response = null;

    try {
      response = this.progressTowardsItem.findProgressTowardsById(id, CGIAREntity, year, phase, this.getCurrentUser());
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("ProgressTowards.progresstowardsSRF.GET.id.404"));
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


}
