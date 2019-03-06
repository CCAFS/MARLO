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

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.ImpactPathway.MilestoneItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.ImpactPathway.OutcomeItem;
import org.cgiar.ccafs.marlo.rest.dto.MilestoneDTO;
import org.cgiar.ccafs.marlo.rest.dto.OutcomeDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;

import com.opensymphony.xwork2.inject.Inject;
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

@RestController
@Api(tags = "Institutions Lists")
public class ImpactPathway {

	private static final Logger LOG = LoggerFactory.getLogger(ImpactPathway.class);

	private OutcomeItem<OutcomeDTO> outcomeItem;
	private MilestoneItem<MilestoneDTO> milestoneItem;

//	private InstitutionItem<InstitutionDTO> institutionItem;
//	private final UserManager userManager;
//
	@Inject
	public ImpactPathway(OutcomeItem<OutcomeDTO> outcomeItem, MilestoneItem<MilestoneDTO> milestoneItem) {
		this.outcomeItem = outcomeItem;
		this.milestoneItem = milestoneItem;

	}

	@ApiOperation(tags = {
			"Table 5 - Status of Planned Outcomes and Milestones" }, value = "Search a Milestone by ID", response = MilestoneDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/{CGIAREntity}/milestones/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MilestoneDTO> findMilestoneById(@PathVariable String CGIAREntity, @PathVariable Long id) {
		LOG.debug("REST request to get Institution : {}", id);
		return this.milestoneItem.findMilestoneById(id, CGIAREntity);
	}

	@ApiOperation(tags = {
			"Table 5 - Status of Planned Outcomes and Milestones" }, value = "Search an Outcomes by ID", response = OutcomeDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/{CGIAREntity}/outcomes/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OutcomeDTO> findOutcomeById(@PathVariable String CGIAREntity, @PathVariable Long id) {
		LOG.debug("REST request to get Institution : {}", id);
		return this.outcomeItem.findOutcomeById(id, CGIAREntity);
	}

	@ApiOperation(tags = {
			"Table 5 - Status of Planned Outcomes and Milestones" }, value = "View a list of milestones", response = MilestoneDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/{CGIAREntity}/milestones/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<MilestoneDTO> getAllMilestones(
			@ApiParam(value = "Acronym of CGIAR Entity") @PathVariable("CGIAREntity") String CGIAREntity,
			@ApiParam(value = "Code of Flagship/Module") @RequestParam("flagshipId") String flagshipId,
			@ApiParam(value = "Target year of outcome") @RequestParam(value = "targetYear", required = false) Integer targetYear,
			@ApiParam(value = "Year of reporting") @RequestParam("reportYear") Integer repoYear)
			throws NotFoundException {
		LOG.debug("REST request to get Institutions");
		List<MilestoneDTO> response = this.milestoneItem.getAllMilestones(flagshipId, CGIAREntity, repoYear);
		if (response == null || response.isEmpty()) {
			throw new NotFoundException("404", "Outcome not found");
		}
		return response;
	}

	@ApiOperation(tags = {
			"Table 5 - Status of Planned Outcomes and Milestones" }, value = "View a list of outcomes", response = OutcomeDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/{CGIAREntity}/outcomes/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<OutcomeDTO> getAllOutcomes(
			@ApiParam(value = "Acronym of CGIAR Entity") @PathVariable("CGIAREntity") String CGIAREntity,
			@ApiParam(value = "Code of Flagship/Module") @RequestParam("flagshipId") String flagshipId,
			@ApiParam(value = "Target year of outcome", example = "2022") @RequestParam(value = "targetYear", required = false) Integer targetYear,
			@ApiParam(value = "Year of reporting", example = "2018") @RequestParam("reportYear") Integer repoYear)
			throws NotFoundException {
		LOG.debug("REST request to get Institutions");
		List<OutcomeDTO> response = this.outcomeItem.getAllOutcomes(flagshipId, CGIAREntity, targetYear, repoYear);
		if (response == null || response.isEmpty()) {
			throw new NotFoundException("404", "Outcome not found");
		}
		return response;
	}

}
