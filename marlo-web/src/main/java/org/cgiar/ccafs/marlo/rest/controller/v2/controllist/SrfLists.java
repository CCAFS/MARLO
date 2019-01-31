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
	 * Find a SLO indicator Target requesting by code
	 * 
	 * @param id
	 * @return a SrfSloIndicatorTargetDTO with the SLO indicator Target data.
	 */

	@ApiOperation(tags = "Table 1 - Evidence on Progress towards SRF targets", value = "Search a SLO Target by code", response = SrfSloTargetDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/slo-targets/{code:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SrfSloTargetDTO> findSrfSloTargetByCode(@PathVariable String code) {
		LOG.debug("REST request to get SrfSloIndicatorTarget : {}", code);
		return this.srfSloIndicatorTargetItem.findSrfSloIndicatorTargetbyId(code);
	}

	/**
	 * Find a SRF-SubIdo requesting by Id
	 * 
	 * @param id
	 * @return a SrfSubIdoDTO with the SRF-SubIdo data.
	 */

	@ApiOperation(tags = "Table 2 - CRP Policies", value = "Search an SRF sub intermediate level objective(sub-IDO) by Code", response = SrfSubIdoDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srf-sub-idos/{code:.+}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SrfSubIdoDTO> findSrfSubIdoByCode(@PathVariable String code) {
		LOG.debug("REST request to get SRF-SubIdo : {}", code);
		return this.srfSubIdoItem.findSrfSubIdoBycode(code);
	}

	/**
	 * Get All the SRF IDO items
	 * 
	 * @return a List of SrfIdoDTO with all SRF IDO Items.
	 */

	@ApiOperation(value = "View all SRF intermediate level objective(IDO)", response = SrfIdoDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srfIdos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SrfIdoDTO> getAllSrfIdos() {
		LOG.debug("REST request to get SRF IDOs");
		return this.srfIdoItem.getAllSrfIdos();
	}

	/**
	 * Get All SLO indicator Target items
	 * 
	 * @return a List of SrfIdoDTO with all SRF IDO Items.
	 */

	@ApiOperation(tags = "Table 1 - Evidence on Progress towards SRF targets", value = "View all SLO Targets", response = SrfSloTargetDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/slo-targets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<SrfSloTargetDTO>> getAllSrfSloIndicatorTarget(
			@RequestParam(required = false, value = "target year (2022 or 2030)") Long year) {
		LOG.debug("REST request to get SLO indicator Target");
		return this.srfSloIndicatorTargetItem.getAllSrfSloIndicatorTargets(year);
	}

	/**
	 * Get All the SRF-SLO items
	 * 
	 * @return a List of SrfSloDTO with all SRF-SLO Items.
	 */
	@ApiIgnore
	@ApiOperation(value = "View all intermediate level objectives (IDOs)", response = SrfSloDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srfSlos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SrfSloDTO> getAllSrfSlos() {
		LOG.debug("REST request to get SRF-SLOs");
		return this.srfSloItem.getAllSrfSlos();
	}

	/**
	 * Get All the SRF Cross Cutting Issue items
	 * 
	 * @return a List of SrfCrossCuttingIssueDTO with all SRF Cross Cutting
	 * Issue Items.
	 */
	@ApiIgnore
	@ApiOperation(value = "View all SRF cross cutting issues", response = SrfCrossCuttingIssueDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srfCrossCuttingIssues", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SrfCrossCuttingIssueDTO> getAllSrfSrfCrossCuttingIssues() {
		LOG.debug("REST request to get SRF Cross Cutting Issues");
		return this.srfCrossCuttingIssueItem.getAllSrfCrossCuttingIssues();
	}

	/**
	 * Get All the SRF-SubIdo items
	 * 
	 * @return a List of SrfSubIdoDTO with all SRF-SubIdo Items.
	 */

	@ApiOperation(tags = "Table 2 - CRP Policies", value = "Get all sub intermediate level objectives (sub-IDOs)", response = SrfSubIdoDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srf-sub-idos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SrfSubIdoDTO> getAllSrfSubIdos() {
		LOG.debug("REST request to get SRF-SubIdos");
		return this.srfSubIdoItem.getAllSrfSubIdos();
	}

	/**
	 * Find a SRF IDO requesting by Id
	 * 
	 * @param id
	 * @return a SrfIdoDTO with the SRL IDO data.
	 */
	@ApiIgnore
	@ApiOperation(value = "Search an SRF intermediate level objective(sub-IDO) by ID", response = SrfIdoDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srfIdo/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SrfIdoDTO> getSrfIdoById(@PathVariable Long id) {
		LOG.debug("REST request to get SRF-SLO : {}", id);
		return this.srfIdoItem.findSrfIdobyId(id);
	}

	/**
	 * Find a SRF-SLO requesting by Id
	 * 
	 * @param id
	 * @return a SrfSloDTO with the SRL-SLO data.
	 */
	@ApiIgnore
	@ApiOperation(value = "Search an SRF-SLO with an ID", response = SrfSloDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srfSlo/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SrfSloDTO> getSrfSloById(@PathVariable Long id) {
		LOG.debug("REST request to get SRF-SLO : {}", id);
		return this.srfSloItem.findSrfSlobyId(id);
	}

	/**
	 * Find a SRF Cross Cutting Issue requesting by Id
	 * 
	 * @param id
	 * @return a SrfCrossCuttingIssueDTO with the SRF Cross Cutting Issue data.
	 */
	@ApiIgnore
	@ApiOperation(value = "Search an SRF Cross Cutting Issue with an ID", response = SrfCrossCuttingIssueDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/srfCrossCuttingIssue/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SrfCrossCuttingIssueDTO> getSrfSrfCrossCuttingIssueById(@PathVariable Long id) {
		LOG.debug("REST request to get SRF Cross Cutting Issue : {}", id);
		return this.srfCrossCuttingIssueItem.findSrfCrossCuttingIssuebyId(id);
	}

}
