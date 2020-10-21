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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.login.AuthenticationItem;
import org.cgiar.ccafs.marlo.rest.dto.UserAutenticationDTO;
import org.cgiar.ccafs.marlo.security.Permission;

import javax.inject.Inject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Api(tags = "User Authentication")
public class UserAuthentication {

  private static final Logger LOG = LoggerFactory.getLogger(Institutions.class);
  @Autowired
  private Environment env;

  private final UserManager userManager;
  private AuthenticationItem<UserAutenticationDTO> authenticationItem;

  @Inject
  public UserAuthentication(UserManager userManager, AuthenticationItem<UserAutenticationDTO> authenticationItem) {
    this.userManager = userManager;
    this.authenticationItem = authenticationItem;
  }

  @ApiOperation(tags = {"User Authentication"}, value = "${UserAuthentication.GET.value}",
    response = UserAutenticationDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/UserAuthentication", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UserAutenticationDTO> userAuthentication(
    @ApiParam(value = "${UserAuthentication.GET.param.user.value}", required = true) @RequestParam String email,
    @ApiParam(value = "${UserAuthentication.GET.param.password.value}",
      required = true) @RequestParam String password) {
    ResponseEntity<UserAutenticationDTO> response = authenticationItem.userAuthentication(email, password);
    return response;
  }


}
