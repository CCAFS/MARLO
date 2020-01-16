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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.KeyExternalPartnership.KeyExternalPartnershipItem;
import org.cgiar.ccafs.marlo.rest.dto.KeyExternalPartnershipDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewKeyExternalPartnershipDTO;
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
@Api(tags = "KeyExternalPartnership")
public class KeyExternalPartnership {

  private static final Logger LOG = LoggerFactory.getLogger(KeyExternalPartnership.class);
  @Autowired
  private Environment env;
  private final UserManager userManager;
  private KeyExternalPartnershipItem<KeyExternalPartnershipDTO> keyExternalPartnershipItem;

  @Inject
  public KeyExternalPartnership(KeyExternalPartnershipItem<KeyExternalPartnershipDTO> keyExternalPartnershipItem,
    UserManager userManager) {
    this.userManager = userManager;
    this.keyExternalPartnershipItem = keyExternalPartnershipItem;
  }

  @ApiOperation(tags = {"Table 8 - Key external partnerships"},
    value = "${KeyExternalPartnership.externalpartnerships.POST.value}", response = KeyExternalPartnershipDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/keyexternalpartnership", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createKeyExternalPartnership(
    @ApiParam(value = "${KeyExternalPartnership.externalpartnerships.POST.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${KeyExternalPartnership.externalpartnerships.POST.param.policy}",
      required = true) @Valid @RequestBody NewKeyExternalPartnershipDTO newKeyExternalPartnershipDTO) {
    Long keyExternalPartnershipID = new Long(0);
    try {
      keyExternalPartnershipID = this.keyExternalPartnershipItem
        .createKeyExternalPartnership(newKeyExternalPartnershipDTO, CGIAREntity, this.getCurrentUser());
    } catch (Exception e) {
      e.printStackTrace();
    }

    ResponseEntity<Long> response = new ResponseEntity<Long>(keyExternalPartnershipID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404",
        this.env.getProperty("KeyExternalPartnership.externalpartnerships.GET.id.404"));
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
