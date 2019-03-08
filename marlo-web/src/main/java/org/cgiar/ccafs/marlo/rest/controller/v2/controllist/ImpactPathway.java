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
@Api(tags = "Impact Pathway Lists")
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
			"Table 5 - Status of Planned Outcomes and Milestones" }, value = "${ImpactPathway.milestones.id.value}", response = MilestoneDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/{CGIAREntity}/milestones/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MilestoneDTO> findMilestoneById(
			@ApiParam(value = "${ImpactPathway.milestones.id.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
			@ApiParam(value = "${ImpactPathway.milestones.id.param.id}", required = true) @PathVariable Long id) {
		LOG.debug("REST request to get Institution : {}", id);
		return this.milestoneItem.findMilestoneById(id, CGIAREntity);
	}

	@ApiOperation(tags = {
			"Table 5 - Status of Planned Outcomes and Milestones" }, value = "${ImpactPathway.outcomes.id.value}", response = OutcomeDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/{CGIAREntity}/outcomes/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OutcomeDTO> findOutcomeById(
			@ApiParam(value = "${ImpactPathway.outcomes.id.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
			@ApiParam(value = "${ImpactPathway.outcomes.id.param.id}", required = true) @PathVariable Long id) {
		LOG.debug("REST request to get Institution : {}", id);
		return this.outcomeItem.findOutcomeById(id, CGIAREntity);
	}

	@ApiOperation(tags = {
			"Table 5 - Status of Planned Outcomes and Milestones" }, value = "${ImpactPathway.milestones.all.value}", response = MilestoneDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/{CGIAREntity}/milestones/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<MilestoneDTO> getAllMilestones(
			@ApiParam(value = "${ImpactPathway.milestones.all.param.CGIAR}", required = true) @PathVariable("CGIAREntity") String CGIAREntity,
			@ApiParam(value = "${ImpactPathway.milestones.all.param.flagshipId}", required = true) @RequestParam("flagshipId") String flagshipId,
			@ApiParam(value = "${ImpactPathway.milestones.all.param.targetYear}") @RequestParam(value = "targetYear", required = false) Integer targetYear,
			@ApiParam(value = "${ImpactPathway.milestones.all.param.reportYear}", required = true) @RequestParam("reportYear") Integer repoYear)
			throws NotFoundException {
		LOG.debug("REST request to get Institutions");
		List<MilestoneDTO> response = this.milestoneItem.getAllMilestones(flagshipId, CGIAREntity, repoYear);
		if (response == null || response.isEmpty()) {
			throw new NotFoundException("404", "Outcome not found");
		}
		return response;
	}

	@ApiOperation(tags = {
			"Table 5 - Status of Planned Outcomes and Milestones" }, value = "${ImpactPathway.outcomes.all.value}", response = OutcomeDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/{CGIAREntity}/outcomes/", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<OutcomeDTO> getAllOutcomes(
			@ApiParam(value = "${ImpactPathway.outcomes.all.param.CGIAR}", required = true) @PathVariable("CGIAREntity") String CGIAREntity,
			@ApiParam(value = "${ImpactPathway.outcomes.all.param.flagshipId}", required = true) @RequestParam("flagshipId") String flagshipId,
			@ApiParam(value = "${ImpactPathway.outcomes.all.param.targetYear}") @RequestParam(value = "targetYear", required = false) Integer targetYear,
			@ApiParam(value = "${ImpactPathway.outcomes.all.param.reportYear}", required = true) @RequestParam("reportYear") Integer repoYear)
			throws NotFoundException {
		LOG.debug("REST request to get Institutions");
		List<OutcomeDTO> response = this.outcomeItem.getAllOutcomes(flagshipId, CGIAREntity, targetYear, repoYear);
		if (response == null || response.isEmpty()) {
			throw new NotFoundException("404", "Outcome not found");
		}
		return response;
	}
}
