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

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.locations.CountryItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.locations.GeographicScopeItem;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.dto.LocElementDTO;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@RestController
@Api(tags = "General Control Lists")
@Named
public class GeneralLists {

  private static final Logger LOG = LoggerFactory.getLogger(GeneralLists.class);

  private CountryItem<GeneralLists> countryItem;
  private GeographicScopeItem<GeneralLists> geographicScopeItem;

  @Inject
  public GeneralLists(CountryItem<GeneralLists> countryItem, GeographicScopeItem<GeneralLists> geographicScopeItem) {
    this.countryItem = countryItem;
    this.geographicScopeItem = geographicScopeItem;
  }

  /**
   * Get All the Country items *
   * 
   * @return a List of LocElementDTO with all LocElements Items.
   */
  @ApiOperation(tags = "Table2 - CRP Policies", value = "View all Conutries", response = LocElementDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/countries", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<LocElementDTO> getAllContries() {
    LOG.debug("REST request to get Contries");
    return this.countryItem.getAllCountries();
  }

  /**
   * Get All the Geographic Scope items *
   * 
   * @return a List of GeographicScopeDTO with all RepIndGeographicScope
   *         Items.
   */

  @ApiOperation(tags = "Table2 - CRP Policies", value = "View all Geographic Scopes",
    response = GeographicScopeDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/geographic-scopes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<GeographicScopeDTO> getAllGeographicScopes() {
    LOG.debug("REST request to get Geographic Scopes");
    return this.geographicScopeItem.getAllGeographicScopes();
  }

}
