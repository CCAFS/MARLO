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

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.GeographicScopeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.GlobalUnitItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.GlobalUnitTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.LocationItem;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityDTO;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.ContributionOfCrpDTO;
import org.cgiar.ccafs.marlo.rest.dto.CountryDTO;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.dto.RegionDTO;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@RestController
@Api(tags = "General Control Lists")
@Named
public class GeneralLists {

	private static final Logger LOG = LoggerFactory.getLogger(GeneralLists.class);

	private LocationItem<GeneralLists> locationItem;
	private GeographicScopeItem<GeneralLists> geographicScopeItem;
	private GlobalUnitItem<GeneralLists> globalUnitItem;
	private GlobalUnitTypeItem<GeneralLists> globalUnitTypeItem;

	@Inject
	public GeneralLists(LocationItem<GeneralLists> countryItem, GeographicScopeItem<GeneralLists> geographicScopeItem,
			GlobalUnitItem<GeneralLists> globalUnitItem, GlobalUnitTypeItem<GeneralLists> globalUnitTypeItem) {
		this.locationItem = countryItem;
		this.geographicScopeItem = geographicScopeItem;
		this.globalUnitItem = globalUnitItem;
		this.globalUnitTypeItem = globalUnitTypeItem;
	}

	/**
	 * Find a country requesting numeric ISO Codeby id
	 * 
	 * @param numeric ISO Code
	 * @return a LocElementDTO with the country founded.
	 */

	@ApiOperation(tags = "Table 2 - CRP Policies", value = "Search a country by alpha2 ISO code", response = ContributionOfCrpDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/countries/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CountryDTO> findCountryByNumericISOCode(@PathVariable("Iso Apha2 Code") String code) {
		LOG.debug("REST request country requesting alpha2 ISO Codeby id : {}", code);
		return this.locationItem.getContryByAlpha2ISOCode(code);
	}

	/**
	 * Find a Geographic Scope by id
	 * 
	 * @param id
	 * @return a GeographicScopeDTO with the geo scope founded.
	 */

	@ApiOperation(tags = "Table 2 - CRP Policies", value = "Search a geographic scope by id ", response = ContributionOfCrpDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/geographic-scopes/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GeographicScopeDTO> findGeographicScopeById(@PathVariable Long id) {
		LOG.debug("REST geo scope by id : {}", id);
		return this.geographicScopeItem.findGeographicScopesById(id);
	}

	/**
	 * Get All the Country items *
	 * 
	 * @return a List of LocElementDTO with all LocElements Items.
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "View all countries", response = CountryDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/countries", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CountryDTO> getAllContries() {
		LOG.debug("REST request to get Contries");
		return this.locationItem.getAllCountries();
	}

	/**
	 * Get All the Geographic Scope items *
	 * 
	 * @return a List of GeographicScopeDTO with all RepIndGeographicScope
	 * Items.
	 */

	@ApiOperation(tags = "Table 2 - CRP Policies", value = "View all geographic scopes", response = GeographicScopeDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/geographic-scopes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<GeographicScopeDTO> getAllGeographicScopes() {
		LOG.debug("REST request to get Geographic Scopes");
		return this.geographicScopeItem.getAllGeographicScopes();
	}
// (Optional) Entity type can be Center, CRP or Platform. Please refer to the entity-type control list. (edited) 

	/**
	 * get all global Units (CGIAR Entities)
	 * 
	 * @return a LocElementDTO with the country founded.
	 */
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@ApiOperation(tags = { "Table 2 - CRP Policies",
			"Table 1 - Evidence on Progress towards SRF targets" }, value = "View official list of CGIAR Centers, CGIAR Research Programs (CRPs) and CGIAR Platforms (PTFs)", response = CGIAREntityDTO.class, responseContainer = "List")
	@RequestMapping(value = "/cgiar-entities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CGIAREntityDTO>> getAllGlobalUnits(
			@RequestParam(required = false, value = "id of cgiar entity type") Long typeId) {
		LOG.debug("REST request to get GlobalUnits");
		LOG.debug("entityType requested: " + typeId);
		return this.globalUnitItem.getAllGlobaUnits(typeId);
	}

	/**
	 * Get All CGIAR entities Types *
	 * 
	 * @return a List of CGIAREntityTypeDTO with all CGIAR entities Types.
	 */
	@ApiOperation(value = "View all CGIAR entities Types", response = CGIAREntityTypeDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/cgiar-entity-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CGIAREntityTypeDTO> getAllGlobalUnitTypes() {
		LOG.debug("REST request to get Regions");
		return this.globalUnitTypeItem.getAllGlobalUnitTypes();
	}

	/**
	 * Get All the Region items *
	 * 
	 * @return a List of RegionDTO with all LocElements regions Items.
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "View all United Nations regions (UN M.49)", response = RegionDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/un-regions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<RegionDTO> getAllRegions() {
		LOG.debug("REST request to get Regions");
		return this.locationItem.getAllRegions();
	}

	/**
	 * Find a global unit requesting by smo id
	 * 
	 * @param smo ID
	 * @return a GlobalUnit with the country founded.
	 */
	@RequiresPermissions(Permission.CRPS_READ_REST_API_PERMISSION)
	@ApiOperation(tags = { "Table 2 - CRP Policies",
			"Table 1 - Evidence on Progress towards SRF targets" }, value = "Search a specific Center, CRP or Platform by code", response = CGIAREntityDTO.class)
	@RequestMapping(value = "/cgiar-entities/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CGIAREntityDTO> getGlobalUnitByCGIARId(@PathVariable String code) {
		LOG.debug("REST request to get GlobalUnit : {}", code);
		return this.globalUnitItem.findGlobalUnitByCGIRARId(code);
	}

	/**
	 * Get a CGIAR Entity Type by code
	 * 
	 * @return a CGIAREntityTypeDTO founded by the code.
	 */
	@ApiOperation(value = "Get a CGIAR Entity Type by code", response = CGIAREntityTypeDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/cgiar-entity-types/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CGIAREntityTypeDTO> getGlobalUnitTypeByCode(@PathVariable Long code) {
		LOG.debug("REST request to get Regions");
		return this.globalUnitTypeItem.findGlobalUnitTypeById(code);
	}

	/**
	 * Get a Region by code
	 * 
	 * @return a RegionDTO founded by the code.
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "Get a United Nations regions (UN M.49) by code", response = RegionDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/un-regions/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RegionDTO> getRegionByCode(@PathVariable Long code) {
		LOG.debug("REST request to get Regions");
		return this.locationItem.getRegionByCode(code);
	}

}
