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

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.ContributionOfCrpItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.CrossCuttingMarkerItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.CrossCuttingMarkerScoreItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.DegreeOfInnovationItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.InnovationTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.MaturityOfChangeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.PolicyInvestmentTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.PolicyMaturityLevelItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.PolicyOwnerTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.ResearchPartnershipItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.StageOfInnovationItem;
import org.cgiar.ccafs.marlo.rest.dto.ContributionOfCrpDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrossCuttingMarkerDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrossCuttingMarkerScoreDTO;
import org.cgiar.ccafs.marlo.rest.dto.DegreeOfInnovationDTO;
import org.cgiar.ccafs.marlo.rest.dto.InnovationTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.MaturityOfChangeDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyInvestmentTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyMaturityLevelDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyOwnerTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.ResearchPartnershipDTO;
import org.cgiar.ccafs.marlo.rest.dto.StageOfInnovationDTO;
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
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@Api(tags = "All AR 2018 Control Lists")

@Named
public class ARControlLists {

	private static final Logger LOG = LoggerFactory.getLogger(ARControlLists.class);
	private CrossCuttingMarkerScoreItem<ARControlLists> crossCuttingMarkerScoreItem;
	private CrossCuttingMarkerItem<ARControlLists> crossCuttingMarkerItem;
	private InnovationTypeItem<ARControlLists> innovationTypesItem;
	private ResearchPartnershipItem<ARControlLists> researchPartnershipsItem;
	private StageOfInnovationItem<ARControlLists> stageOfInnovationItem;
	private ContributionOfCrpItem<ARControlLists> contributionOfCrpItem;
	private DegreeOfInnovationItem<ARControlLists> degreeOfInnovationItem;
	private MaturityOfChangeItem<ARControlLists> maturityOfChangeItem;
	private PolicyInvestmentTypeItem<ARControlLists> policyInvestmentTypeItem;
	private PolicyOwnerTypeItem<ARControlLists> policyOwnerTypeItem;
	private PolicyMaturityLevelItem<ARControlLists> policyMaturityLevelItem;

	@Inject
	public ARControlLists(CrossCuttingMarkerScoreItem<ARControlLists> crossCuttingMarkerScoreItem,
			CrossCuttingMarkerItem<ARControlLists> crossCuttingMarkerItem,
			InnovationTypeItem<ARControlLists> innovationTypesItem,
			ResearchPartnershipItem<ARControlLists> researchPartnershipsItem,
			StageOfInnovationItem<ARControlLists> stageOfInnovationItem,
			ContributionOfCrpItem<ARControlLists> contributionOfCrpItem,
			DegreeOfInnovationItem<ARControlLists> degreeOfInnovationItem,
			MaturityOfChangeItem<ARControlLists> maturityOfChangeItem,
			PolicyInvestmentTypeItem<ARControlLists> policyInvestmentTypeItem,
			PolicyOwnerTypeItem<ARControlLists> policyOwnerTypeItem,
			PolicyMaturityLevelItem<ARControlLists> policyMaturityLevelItem) {
		this.crossCuttingMarkerScoreItem = crossCuttingMarkerScoreItem;
		this.innovationTypesItem = innovationTypesItem;
		this.researchPartnershipsItem = researchPartnershipsItem;
		this.stageOfInnovationItem = stageOfInnovationItem;
		this.contributionOfCrpItem = contributionOfCrpItem;
		this.degreeOfInnovationItem = degreeOfInnovationItem;
		this.maturityOfChangeItem = maturityOfChangeItem;
		this.policyInvestmentTypeItem = policyInvestmentTypeItem;
		this.crossCuttingMarkerItem = crossCuttingMarkerItem;
		this.policyOwnerTypeItem = policyOwnerTypeItem;
		this.policyMaturityLevelItem = policyMaturityLevelItem;
	}

	/**
	 * Find a Contribution of CRP requesting by id
	 * 
	 * @param id
	 * @return a ContributionOfCrpDTO with the Contribution of CRP data.
	 */
	@ApiIgnore
	@ApiOperation(value = "Search a Contribution of CRP with an ID", response = ContributionOfCrpDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/contributionOfCrp/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ContributionOfCrpDTO> findContributionOfCrpById(@PathVariable Long id) {
		LOG.debug("REST request to get Innovation Type : {}", id);
		return this.contributionOfCrpItem.findContributionOfCrpById(id);
	}

	/**
	 * Find a Degree of Innovation requesting by id
	 * 
	 * @param id
	 * @return a DegreeOfInnovationDTO with the Degree of Innovation data.
	 */
	@ApiIgnore
	@ApiOperation(value = "Search a Degree of Innovation with an ID", response = DegreeOfInnovationDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/degreeOfInnovation/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DegreeOfInnovationDTO> findDegreeOfInnovationById(@PathVariable Long id) {
		LOG.debug("REST request to get Degree of Innovation : {}", id);
		return this.degreeOfInnovationItem.findDegreeOfInnovationById(id);
	}

	/**
	 * Find a Maturity of Change requesting by id
	 * 
	 * @param id
	 * @return a MaturityOfChangeDTO with the Maturity of Change data.
	 */
	@ApiIgnore
	@ApiOperation(value = "Search a Maturity of Change with an ID", response = MaturityOfChangeDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/maturityOfChange/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MaturityOfChangeDTO> findMaturityOfChangeById(@PathVariable Long id) {
		LOG.debug("REST request to get Maturity of Change : {}", id);
		return this.maturityOfChangeItem.findMaturityOfChangeById(id);
	}

	/**
	 * Find a Stage of Innovation requesting by id
	 * 
	 * @param id
	 * @return a StageOfInnovationDTO with the Stage of Innovation data.
	 */
	@ApiIgnore
	@ApiOperation(value = "Search a Stage of Innovation with an ID", response = StageOfInnovationDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/stageOfInnovation/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StageOfInnovationDTO> findStageOfInnovationById(@PathVariable Long id) {
		LOG.debug("REST request to get Stage of Innovation : {}", id);
		return this.stageOfInnovationItem.findStageOfInnovationById(id);
	}

	/**
	 * Get All the Contribution of CRP Items *
	 * 
	 * @return a List of ContributionOfCrpDTO with all RepIndContributionOfCrp
	 * Items.
	 */
	@ApiIgnore
	@ApiOperation(value = "View all The Contribution of CRP to Innovation Types", response = ContributionOfCrpDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/contributionOfCrps", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ContributionOfCrpDTO> getAllContributionOfCrps() {
		LOG.debug("REST request to get Contribution of CRP");
		return this.contributionOfCrpItem.getAllContributionOfCrps();
	}

	/**
	 * Get All the cross cutting markers of CRP Items *
	 * 
	 * @return a List of cross cutting markers
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "View all The Cross Cutting Markers", response = ContributionOfCrpDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/cross-cutting-markers", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CrossCuttingMarkerDTO> getAllCrossCuttingMarkers() {
		LOG.debug("REST request to get all The Cross Cutting Markers");
		return this.crossCuttingMarkerItem.getAllCrossCuttingMarker();
	}

	/**
	 * Get All the Cross Cutting Markers items
	 * 
	 * @return a List of CrossCuttingMarkersDTO with all Cross Cutting Markers
	 * Items.
	 */

	@ApiOperation(tags = "Table 2 - CRP Policies", value = "View all The Cross Cutting Marker Scores", response = CrossCuttingMarkerScoreDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/cross-cutting-marker-scores", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<CrossCuttingMarkerScoreDTO> getAllCrossCuttingMarkerScores() {
		LOG.debug("REST request to get Cross Cutting Markers");
		return this.crossCuttingMarkerScoreItem.getAllCrossCuttingMarkersScores();
	}

	/**
	 * Get All the Degree of Innovation Items *
	 * 
	 * @return a List of DegreeOfInnovationDTO with all RepIndDegreeInnovation
	 * Items.
	 */
	@ApiIgnore
	@ApiOperation(value = "View all The Degree of Innovations", response = DegreeOfInnovationDTO.class, responseContainer = "List", position = 1)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/degreeOfInnovations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<DegreeOfInnovationDTO> getAllDegreeOfInnovations() {
		LOG.debug("REST request to get  Degree of Innovations");
		return this.degreeOfInnovationItem.getAllDegreeOfInnovations();
	}

	/**
	 * Get All the Innovation Types items
	 * 
	 * @return a List of InnovationTypesDTO with all Innovation Types Items.
	 */
	@ApiIgnore
	@ApiOperation(value = "View all The Innovation Types", response = InnovationTypeDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/innovation-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<InnovationTypeDTO> getAllInnovationTypes() {
		LOG.debug("REST request to get Innovation Types");
		return this.innovationTypesItem.getAllInnovationTypes();
	}

	/**
	 * Get All the Maturity of Change Items *
	 * 
	 * @return a List of MaturityOfChangeDTO with all RepIndStageStudy Items.
	 */
	@ApiIgnore
	@ApiOperation(value = "View all The Maturity of Changes", response = MaturityOfChangeDTO.class, responseContainer = "List", position = 1)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/maturity-of-changes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<MaturityOfChangeDTO> getAllMaturityOfChanges() {
		LOG.debug("REST request to get  Maturity of Change");
		return this.maturityOfChangeItem.getAllMaturityOfChanges();
	}

	/**
	 * get all policy investiment types
	 * 
	 * @return a List of policyInvestmentTypeDTO with all
	 * RepIndPolicyInvestimentType Items.
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "View all policy investiment types", response = PolicyInvestmentTypeDTO.class, responseContainer = "List", position = 1)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/policy-investment-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PolicyInvestmentTypeDTO> getAllPolicyInvestmentType() {
		LOG.debug("REST request to get  Degree of Innovations");
		return this.policyInvestmentTypeItem.getAllPolicyInvestmentType();
	}

	/**
	 * Get All the Policy level of maturity items
	 * 
	 * @return a List of PolicyLevelOfMaturityDTO with all olicy level of
	 * maturity Items.
	 */

	@ApiOperation(tags = "Table 2 - CRP Policies", value = "View all Policy level of maturity", response = PolicyMaturityLevelDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/policy-maturity-levels", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PolicyMaturityLevelDTO> getAllPolicyMaturityLevels() {
		LOG.debug("REST request to get Cross Cutting Markers");
		return this.policyMaturityLevelItem.getAllPolicyMaturityLevel();
	}

	/**
	 * Get All the policy owner types
	 * 
	 * @return a List of PolicyOwnerTypeDTO with all the policy owner types
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "View all Policy Owner Types", response = PolicyOwnerTypeDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/policy-owner-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<PolicyOwnerTypeDTO> getAllPolicyOwnerTypes() {
		LOG.debug("REST request to get Innovation Types");
		return this.policyOwnerTypeItem.getAllPolicyOwnerTypes();
	}

	/**
	 * Get All the Research Partnerships items
	 * 
	 * @return a List of ResearchPartnershipsDTO with all ResearchPartnerships
	 * Items.
	 */
	@ApiIgnore
	@ApiOperation(value = "View all The Research Partnerships", response = ResearchPartnershipDTO.class, responseContainer = "List", position = 1)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/research-partnerships", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<ResearchPartnershipDTO> getAllResearchPartnerships() {
		LOG.debug("REST request to get ResearchPartnerships");
		return this.researchPartnershipsItem.getAllResearchPartnerships();
	}

	/**
	 * Get All the Stage of Innovations items
	 * 
	 * @return a List of StageOfInnovationDTO with all RepIndStageInnovation
	 * Items.
	 */
	@ApiIgnore
	@ApiOperation(value = "View all The Stage of Innovations", response = StageOfInnovationDTO.class, responseContainer = "List", position = 1)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/stageOfInnovations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<StageOfInnovationDTO> getAllStageOfInnovations() {
		LOG.debug("REST request to get  Stage of Innovations");
		return this.stageOfInnovationItem.getAllStageOfInnovations();
	}

	/**
	 * Find a Cross Cutting Marker by id
	 * 
	 * @param id
	 * @return a CrossCuttingMarkerScoreDTO with the Cross Cutting Marker data.
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "Search a Cross Cutting Marker with an ID", response = CrossCuttingMarkerScoreDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/cross-cutting-markers/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CrossCuttingMarkerDTO> getCrossCuttingMarkerById(@PathVariable Long id) {
		LOG.debug("REST request to get Cross Cutting Marker : {}", id);
		return this.crossCuttingMarkerItem.findCrossCuttingMarkerById(id);
	}

	/**
	 * Find a Cross Cutting Marker score by id
	 * 
	 * @param id
	 * @return a CrossCuttingMarkerDTO with the Cross Cutting Marker data.
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "Search a Cross Cutting Marker Score with an ID", response = CrossCuttingMarkerScoreDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/cross-cutting-marker-scores/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CrossCuttingMarkerScoreDTO> getCrossCuttingMarkerScoreById(@PathVariable Long id) {
		LOG.debug("REST request to get Cross Cutting Marker : {}", id);
		return this.crossCuttingMarkerScoreItem.findCrossCuttingMarkerScoreById(id);
	}

	/**
	 * Find a Innovation Type requesting by id
	 * 
	 * @param id
	 * @return a InnovationTypesDTO with the Innovation Type data.
	 */
	@ApiIgnore
	@ApiOperation(value = "Search a Innovation Type with an ID", response = InnovationTypeDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/innovationType/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<InnovationTypeDTO> getInnovationTypeById(@PathVariable Long id) {
		LOG.debug("REST request to get Innovation Type : {}", id);
		return this.innovationTypesItem.findInnovationTypeById(id);
	}

	/**
	 * Find a Policy Investment type by id
	 * 
	 * @param id
	 * @return a policyInvestmentTypeDTO with the Cross Cutting Marker data.
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "Search a Cross Cutting Marker with an ID", response = PolicyInvestmentTypeDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/policy-investment-types/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PolicyInvestmentTypeDTO> getPolicyInvestimentTypesById(@PathVariable Long id) {
		LOG.debug("REST request to get Cross Cutting Marker : {}", id);
		return this.policyInvestmentTypeItem.PolicyInvestmentTypeById(id);
	}

	/**
	 * Find a Policy Level of Maturity by id
	 * 
	 * @param id
	 * @return a PolicyLevelOfMaturityDTO with Policy Level of Maturity data.
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "Search a policy maturity level with an ID", response = PolicyMaturityLevelDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/policy-maturity-levels/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PolicyMaturityLevelDTO> getPolicyMaturityLevelById(@PathVariable Long id) {
		LOG.debug("REST request to get Cross Cutting Marker : {}", id);
		return this.policyMaturityLevelItem.PolicyMaturityLevelById(id);
	}

	/**
	 * Find a policy owner type by id
	 * 
	 * @param id
	 * @return a PolicyOwnerTypeDTO with the policy owner type
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "Search a Policy owner type with an ID", response = PolicyOwnerTypeDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/policy-owner-types/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PolicyOwnerTypeDTO> getPolicyOwnerTypeById(@PathVariable Long id) {
		LOG.debug("REST request to get Cross Cutting Marker : {}", id);
		return this.policyOwnerTypeItem.findPolicyOwnerTypeById(id);
	}

	/**
	 * Find a Research Partnership requesting by id
	 * 
	 * @param id
	 * @return a ResearchPartnershipsDTO with the Research Partnership data.
	 */
	@ApiIgnore
	@ApiOperation(value = "Search a Research Partnerships with an ID", response = ResearchPartnershipDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/researchPartnership/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResearchPartnershipDTO> getResearchPartnershipById(@PathVariable Long id) {
		LOG.debug("REST request to get ResearchPartnership : {}", id);
		return this.researchPartnershipsItem.findResearchPartnershipById(id);
	}

}
