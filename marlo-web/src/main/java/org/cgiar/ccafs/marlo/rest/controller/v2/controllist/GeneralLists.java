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

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.FlagshipProgramItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.GeographicScopeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.GlobalUnitItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.GlobalUnitTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.LocationItem;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityDTO;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.ContributionOfCrpDTO;
import org.cgiar.ccafs.marlo.rest.dto.CountryDTO;
import org.cgiar.ccafs.marlo.rest.dto.FlagshipProgramDTO;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.dto.RegionDTO;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
	private FlagshipProgramItem<GeneralLists> flagshipProgramItem;

	@Inject
	public GeneralLists(LocationItem<GeneralLists> countryItem, GeographicScopeItem<GeneralLists> geographicScopeItem,
			GlobalUnitItem<GeneralLists> globalUnitItem, GlobalUnitTypeItem<GeneralLists> globalUnitTypeItem,
			FlagshipProgramItem<GeneralLists> flagshipProgramItem) {
		this.locationItem = countryItem;
		this.geographicScopeItem = geographicScopeItem;
		this.globalUnitItem = globalUnitItem;
		this.globalUnitTypeItem = globalUnitTypeItem;
		this.flagshipProgramItem = flagshipProgramItem;
	}

	/**
	 * Find a country requesting numeric ISO Codeby id
	 * 
	 * @param numeric ISO Code
	 * @return a LocElementDTO with the country founded.
	 */

	@ApiOperation(tags = { "Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports",
			"Table 3 - Outcome/ Impact Case Reports", "Table 4 - CRP Innovations",
			"Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)" }, value = "${GeneralLists.countries.code.value}", response = ContributionOfCrpDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/countries/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CountryDTO> findCountryByNumericISOCode(
			@ApiParam(value = "${GeneralLists.countries.code.param.code}", required = true) @PathVariable("Iso Apha2 Code") String code) {
		return this.locationItem.getContryByAlpha2ISOCode(code);
	}

	/**
	 * Find a Flagship or Program by smo code
	 * 
	 * @param smo flagship/program code
	 * @return a FlagshipProgramDTO with Flagship or Program data.
	 */
	@ApiOperation(tags = { "Table 3 - Outcome/ Impact Case Reports",
			"Table 5 - Status of Planned Outcomes and Milestones",
			"Table 8 - Key external partnerships" }, value = "${GeneralLists.flagships-modules.code.value}", response = FlagshipProgramDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/flagships-modules/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<FlagshipProgramDTO> findFlagshipProgramBySmoCode(
			@ApiParam(value = "${GeneralLists.flagships-modules.code.param.code}", required = true) @PathVariable String code) {
		return this.flagshipProgramItem.findFlagshipProgramBySmoCode(code);
	}

	/**
	 * Find a Geographic Scope by id
	 * 
	 * @param id
	 * @return a GeographicScopeDTO with the geo scope founded.
	 */

	@ApiOperation(tags = { "Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports",
			"Table 4 - CRP Innovations",
			"Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)" }, value = "${GeneralLists.geographic-scopes.code.value}", response = ContributionOfCrpDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/geographic-scopes/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<GeographicScopeDTO> findGeographicScopeById(
			@ApiParam(value = "${GeneralLists.geographic-scopes.code.param.code}", required = true) @PathVariable Long code) {
		return this.geographicScopeItem.findGeographicScopesById(code);
	}

	/**
	 * Find a global unit requesting by smo id
	 * 
	 * @param smo ID
	 * @return a GlobalUnit with the global unit founded.
	 */
	@RequiresPermissions(Permission.CRPS_READ_REST_API_PERMISSION)
	@ApiOperation(tags = { "Table 1 - Evidence on Progress towards SRF targets", "Table 2 - CRP Policies",
			"Table 3 - Outcome/ Impact Case Reports", "Table 4 - CRP Innovations",
			"Table 5 - Status of Planned Outcomes and Milestones", "Table 6 - Peer-reviewed publications",
			"Table 7 - Participants in CapDev Activities", "Table 8 - Key external partnerships",
			"Table 9 - Internal Cross-CGIAR Collaborations",
			"Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)" }, value = "${GeneralLists.cgiar-entities.code.value}", response = CGIAREntityDTO.class)
	@RequestMapping(value = "/cgiar-entities/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CGIAREntityDTO> findGlobalUnitByCGIARId(
			@ApiParam(value = "${GeneralLists.cgiar-entities.code.param.code}", required = true) @PathVariable String code) {
		return this.globalUnitItem.findGlobalUnitByCGIRARId(code);
	}

	/**
	 * Get a CGIAR Entity Type by code
	 * 
	 * @return a CGIAREntityTypeDTO founded by the code.
	 */
	@ApiOperation(value = "${GeneralLists.cgiar-entity-types.code.value}", response = CGIAREntityTypeDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/cgiar-entity-types/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CGIAREntityTypeDTO> findGlobalUnitTypeByCode(
			@ApiParam(value = "${GeneralLists.cgiar-entity-types.code.param.code}", required = true) @PathVariable Long code) {
		return this.globalUnitTypeItem.findGlobalUnitTypeById(code);
	}

	/**
	 * Get a Region by code
	 * 
	 * @return a RegionDTO founded by the code.
	 */
	@ApiOperation(tags = { "Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports",
			"Table 4 - CRP Innovations",
			"Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)" }, value = "${GeneralLists.un-regions.code.value}", response = RegionDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/un-regions/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<RegionDTO> findtRegionByCode(
			@ApiParam(value = "${GeneralLists.un-regions.code.param.code}", required = true) @PathVariable Long code) {
		return this.locationItem.getRegionByCode(code);
	}

	/**
	 * Get All the Country items *
	 * 
	 * @return a List of LocElementDTO with all LocElements Items.
	 */
	@ApiOperation(tags = { "Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports",
			"Table 4 - CRP Innovations",
			"Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)" }, value = "${GeneralLists.countries.all.value}", response = CountryDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/countries", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CountryDTO> getAllContries() {
		return this.locationItem.getAllCountries();
	}

	/**
	 * Get All the Flagship or Program items *
	 * 
	 * @return a List of FlagshipProgramDTO with all Flagship or Program Items.
	 */
	@ApiOperation(tags = { "Table 3 - Outcome/ Impact Case Reports",
			"Table 5 - Status of Planned Outcomes and Milestones",
			"Table 8 - Key external partnerships" }, value = "${GeneralLists.flagships-modules.all.value}", response = FlagshipProgramDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/flagships-modules", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<FlagshipProgramDTO> getAllFlagshipsPrograms() {
		return this.flagshipProgramItem.getAllCrpPrograms();
	}

	/**
	 * Get All the Geographic Scope items *
	 * 
	 * @return a List of GeographicScopeDTO with all RepIndGeographicScope
	 * Items.
	 */

	@ApiOperation(tags = { "Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports",
			"Table 4 - CRP Innovations",
			"Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)" }, value = "${GeneralLists.geographic-scopes.all.value}", response = GeographicScopeDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/geographic-scopes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<GeographicScopeDTO> getAllGeographicScopes() {
		return this.geographicScopeItem.getAllGeographicScopes();
	}
// (Optional) Entity type can be Center, CRP or Platform. Please refer to the entity-type control list. (edited) 

	/**
	 * get all global Units (CGIAR Entities)
	 * 
	 * @return a LocElementDTO with the country founded.
	 */
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@ApiOperation(tags = { "Table 1 - Evidence on Progress towards SRF targets", "Table 2 - CRP Policies",
			"Table 3 - Outcome/ Impact Case Reports", "Table 4 - CRP Innovations",
			"Table 5 - Status of Planned Outcomes and Milestones", "Table 6 - Peer-reviewed publications",
			"Table 7 - Participants in CapDev Activities", "Table 8 - Key external partnerships",
			"Table 9 - Internal Cross-CGIAR Collaborations",
			"Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)" }, value = "${GeneralLists.cgiar-entities.all.value}", response = CGIAREntityDTO.class, responseContainer = "List")
	@RequestMapping(value = "/cgiar-entities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<CGIAREntityDTO>> getAllGlobalUnits(
			@ApiParam(value = "${GeneralLists.cgiar-entities.all.param.typeId}") @RequestParam(value = "typeId", required = false) Long typeId) {
		return this.globalUnitItem.getAllGlobaUnits(typeId);
	}

	/**
	 * Get All CGIAR entities Types *
	 * 
	 * @return a List of CGIAREntityTypeDTO with all CGIAR entities Types.
	 */
	@ApiOperation(value = "${GeneralLists.cgiar-entity-types.all.value}", response = CGIAREntityTypeDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/cgiar-entity-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CGIAREntityTypeDTO> getAllGlobalUnitTypes() {
		return this.globalUnitTypeItem.getAllGlobalUnitTypes();
	}

	/**
	 * Get All the Region items *
	 * 
	 * @return a List of RegionDTO with all LocElements regions Items.
	 */
	@ApiOperation(tags = { "Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports",
			"Table 4 - CRP Innovations",
			"Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)" }, value = "${GeneralLists.un-regions.all.value}", response = RegionDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/un-regions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<RegionDTO> getAllRegions() {
		return this.locationItem.getAllRegions();
	}

}
