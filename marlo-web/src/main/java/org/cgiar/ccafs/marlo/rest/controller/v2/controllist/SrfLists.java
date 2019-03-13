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

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.srflist.SrfCrossCuttingIssueItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.srflist.SrfIdoItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.srflist.SrfSloItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.srflist.SrfSloTargetItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.srflist.SrfSubIdoItem;
import org.cgiar.ccafs.marlo.rest.dto.SrfCrossCuttingIssueDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfIdoDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfSloDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfSloTargetDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfSubIdoDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @author Hermes Jiménez - CIAT/CCAFS
 */
@RestController
@Api(tags = "SRF Lists")
@Named
public class SrfLists {

	private static final Logger LOG = LoggerFactory.getLogger(SrfLists.class);

	private SrfSloItem<SrfLists> srfSloItem;
	private SrfIdoItem<SrfLists> srfIdoItem;
	private SrfCrossCuttingIssueItem<SrfLists> srfCrossCuttingIssueItem;
	private SrfSubIdoItem<SrfLists> srfSubIdoItem;
	private SrfSloTargetItem<SrfLists> srfSloIndicatorTargetItem;

	@Inject
	public SrfLists(SrfSloItem<SrfLists> srfSloItem, SrfIdoItem<SrfLists> srfIdoItem,
			SrfCrossCuttingIssueItem<SrfLists> srfCrossCuttingIssueItem, SrfSubIdoItem<SrfLists> srfSubIdoItem,
			SrfSloTargetItem<SrfLists> srfSloIndicatorTargetItem) {
		this.srfSloItem = srfSloItem;
		this.srfIdoItem = srfIdoItem;
		this.srfCrossCuttingIssueItem = srfCrossCuttingIssueItem;
		this.srfSubIdoItem = srfSubIdoItem;
		this.srfSloIndicatorTargetItem = srfSloIndicatorTargetItem;

	}

	/**
	 * Find a SRF IDO requesting by Id
	 * 
	 * @param id
	 * @return a SrfIdoDTO with the SRL IDO data.
	 */
	@ApiOperation(value = "${SrfList.srf-ido.code.value}", response = SrfIdoDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srf-idos/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SrfIdoDTO> findSrfIdoById(
			@ApiParam(value = "${SrfList.srf-ido.code.param.code}", required = true) @PathVariable Long code) {
		ResponseEntity<SrfIdoDTO> response = this.srfIdoItem.findSrfIdobyId(code);
		if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
			throw new NotFoundException("404", "SRF IDO not found");
		}
		return response;
	}

	/**
	 * Find a SRF-SLO requesting by Id
	 * 
	 * @param id
	 * @return a SrfSloDTO with the SRL-SLO data.
	 */
	@ApiIgnore
	@ApiOperation(value = "${SrfList.srf-slo.code.value}", response = SrfSloDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srf-slos/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SrfSloDTO> findSrfSloById(
			@ApiParam(value = "${SrfList.srf-slo.code.param.code}", required = true) @PathVariable Long code) {
		ResponseEntity<SrfSloDTO> response = this.srfSloItem.findSrfSlobyId(code);
		if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
			throw new NotFoundException("404", "SRF SLO not found");
		}
		return response;
	}

	/**
	 * Find a SLO indicator Target requesting by code
	 * 
	 * @param id
	 * @return a SrfSloIndicatorTargetDTO with the SLO indicator Target data.
	 */

	@ApiOperation(tags = { "Table 1 - Evidence on Progress towards SRF targets",
			"Table 3 - Outcome/ Impact Case Reports",
			"Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)" }, value = "${SrfList.slo-targets.code.value}", response = SrfSloTargetDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/slo-targets/{code:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SrfSloTargetDTO> findSrfSloTargetByCode(
			@ApiParam(value = "${SrfList.slo-targets.code.param.code}", required = true) @PathVariable String code) {
		ResponseEntity<SrfSloTargetDTO> response = this.srfSloIndicatorTargetItem.findSrfSloIndicatorTargetbyId(code);
		if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
			throw new NotFoundException("404", "SLO indicator Target not found");
		}
		return response;
	}

	/**
	 * Find a SRF Cross Cutting Issue requesting by Id
	 * 
	 * @param id
	 * @return a SrfCrossCuttingIssueDTO with the SRF Cross Cutting Issue data.
	 */
	@ApiIgnore
	@ApiOperation(value = "${SrfList.srf-cross-cutting-issue.code.value}", response = SrfCrossCuttingIssueDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srf-cross-cutting-issues/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SrfCrossCuttingIssueDTO> findSrfSrfCrossCuttingIssueById(
			@ApiParam(value = "${SrfList.srf-cross-cutting-issue.code.param.code}", required = true) @PathVariable Long code) {
		ResponseEntity<SrfCrossCuttingIssueDTO> response = this.srfCrossCuttingIssueItem
				.findSrfCrossCuttingIssuebyId(code);
		if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
			throw new NotFoundException("404", "SRF Cross Cutting Issue not found");
		}
		return response;
	}

	/**
	 * Find a SRF-SubIdo requesting by Id
	 * 
	 * @param id
	 * @return a SrfSubIdoDTO with the SRF-SubIdo data.
	 */

	@ApiOperation(tags = { "Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports",
			"Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)" }, value = "${SrfList.srf-sub-idos.code.value}", response = SrfSubIdoDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srf-sub-idos/{code:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SrfSubIdoDTO> findSrfSubIdoByCode(
			@ApiParam(value = "${SrfList.srf-sub-idos.code.param.code}", required = true) @PathVariable String code) {
		ResponseEntity<SrfSubIdoDTO> response = this.srfSubIdoItem.findSrfSubIdoBycode(code);
		if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
			throw new NotFoundException("404", "SRF SubIdo not found");
		}
		return response;
	}

	/**
	 * Get All the SRF IDO items
	 * 
	 * @return a List of SrfIdoDTO with all SRF IDO Items.
	 */

	@ApiOperation(value = "${SrfList.srf-idos.all.value}", response = SrfIdoDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srf-idos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SrfIdoDTO> getAllSrfIdos() {
		return this.srfIdoItem.getAllSrfIdos();
	}

	/**
	 * Get All the SRF-SLO items
	 * 
	 * @return a List of SrfSloDTO with all SRF-SLO Items.
	 */
	@ApiIgnore
	@ApiOperation(value = "${SrfList.srf-slos.all.value}", response = SrfSloDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srf-slos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SrfSloDTO> getAllSrfSlos() {
		return this.srfSloItem.getAllSrfSlos();
	}

	/**
	 * Get All SLO indicator Target items
	 * 
	 * @return a List of SrfIdoDTO with all SRF IDO Items.
	 */

	@ApiOperation(tags = { "Table 1 - Evidence on Progress towards SRF targets",
			"Table 3 - Outcome/ Impact Case Reports",
			"Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)" }, value = "${SrfList.slo-targets.all.value}", response = SrfSloTargetDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/slo-targets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SrfSloTargetDTO>> getAllSrfSloTarget(
			@RequestParam(required = false, value = "target year (2022 or 2030)") Long year) {
		return this.srfSloIndicatorTargetItem.getAllSrfSloIndicatorTargets(year);
	}

	/**
	 * Get All the SRF Cross Cutting Issue items
	 * 
	 * @return a List of SrfCrossCuttingIssueDTO with all SRF Cross Cutting
	 * Issue Items.
	 */
	@ApiIgnore
	@ApiOperation(value = "${SrfList.srf-cross-cutting-issues.all.value}", response = SrfCrossCuttingIssueDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srf-cross-cutting-issues", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SrfCrossCuttingIssueDTO> getAllSrfSrfCrossCuttingIssues() {
		return this.srfCrossCuttingIssueItem.getAllSrfCrossCuttingIssues();
	}

	/**
	 * Get All the SRF-SubIdo items
	 * 
	 * @return a List of SrfSubIdoDTO with all SRF-SubIdo Items.
	 */

	@ApiOperation(tags = { "Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports",
			"Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)" }, value = "${SrfList.srf-sub-idos.all.value}", response = SrfSubIdoDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srf-sub-idos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SrfSubIdoDTO> getAllSrfSubIdos() {
		return this.srfSubIdoItem.getAllSrfSubIdos();
	}

}
