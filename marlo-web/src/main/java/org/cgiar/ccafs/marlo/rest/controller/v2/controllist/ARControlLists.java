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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.OrganizationTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.PolicyInvestmentTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.PolicyMaturityLevelItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.PolicyOwnerTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.ResearchPartnershipItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.StageOfInnovationItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.TagItem;
import org.cgiar.ccafs.marlo.rest.dto.ContributionOfCrpDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrossCuttingMarkerDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrossCuttingMarkerScoreDTO;
import org.cgiar.ccafs.marlo.rest.dto.DegreeOfInnovationDTO;
import org.cgiar.ccafs.marlo.rest.dto.InnovationTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.MaturityOfChangeDTO;
import org.cgiar.ccafs.marlo.rest.dto.OrganizationTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyInvestmentTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyMaturityLevelDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyOwnerTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.ResearchPartnershipDTO;
import org.cgiar.ccafs.marlo.rest.dto.StageOfInnovationDTO;
import org.cgiar.ccafs.marlo.rest.dto.TagDTO;
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
	private OrganizationTypeItem<ARControlLists> organizationTypeItem;
	private TagItem<ARControlLists> tagItem;

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
			PolicyMaturityLevelItem<ARControlLists> policyMaturityLevelItem,
			OrganizationTypeItem<ARControlLists> organizationTypeItem, TagItem<ARControlLists> tagItem) {
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
		this.organizationTypeItem = organizationTypeItem;
		this.tagItem = tagItem;

	}

	/**
	 * Find a Contribution of CRP requesting by id
	 * 
	 * @param id
	 * @return a ContributionOfCrpDTO with the Contribution of CRP data.
	 */
	@ApiIgnore
	@ApiOperation(value = "Search a contribution of CRP with an ID", response = ContributionOfCrpDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/contributionOfCrp/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ContributionOfCrpDTO> findContributionOfCrpById(@PathVariable Long code) {
		LOG.debug("REST request to get Innovation Type : {}", code);
		return this.contributionOfCrpItem.findContributionOfCrpById(code);
	}

	/**
	 * Find a Cross Cutting Marker by id
	 * 
	 * @param id
	 * @return a CrossCuttingMarkerScoreDTO with the Cross Cutting Marker data.
	 */
	@ApiOperation(tags = { "Table 2 - CRP Policies",
			"Table 3 - Outcome/ Impact Case Reports" }, value = "Search a cross cutting marker by ID", response = CrossCuttingMarkerScoreDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/cross-cutting-markers/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CrossCuttingMarkerDTO> findCrossCuttingMarkerById(@PathVariable Long code) {
		LOG.debug("REST request to get Cross Cutting Marker : {}", code);
		return this.crossCuttingMarkerItem.findCrossCuttingMarkerById(code);
	}

	/**
	 * Find a Cross Cutting Marker score by id
	 * 
	 * @param id
	 * @return a CrossCuttingMarkerDTO with the Cross Cutting Marker data.
	 */
	@ApiOperation(tags = { "Table 2 - CRP Policies",
			"Table 3 - Outcome/ Impact Case Reports" }, value = "Search a cross cutting marker score by ID", response = CrossCuttingMarkerScoreDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/cross-cutting-marker-scores/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CrossCuttingMarkerScoreDTO> findCrossCuttingMarkerScoreById(@PathVariable Long code) {
		LOG.debug("REST request to get Cross Cutting Marker : {}", code);
		return this.crossCuttingMarkerScoreItem.findCrossCuttingMarkerScoreById(code);
	}

	/**
	 * Find a Degree of Innovation requesting by id
	 * 
	 * @param id
	 * @return a DegreeOfInnovationDTO with the Degree of Innovation data.
	 */
	@ApiIgnore
	@ApiOperation(value = "Search a degree of innovation with an ID", response = DegreeOfInnovationDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/degreeOfInnovation/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<DegreeOfInnovationDTO> findDegreeOfInnovationById(@PathVariable Long code) {
		LOG.debug("REST request to get Degree of Innovation : {}", code);
		return this.degreeOfInnovationItem.findDegreeOfInnovationById(code);
	}

	/**
	 * Find a Innovation Type requesting by id
	 * 
	 * @param id
	 * @return a InnovationTypesDTO with the Innovation Type data.
	 */

	@ApiOperation(tags = "Table 4 - CRP Innovations", value = "Search a Innovation Type with an ID", response = InnovationTypeDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/innovationType/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<InnovationTypeDTO> findInnovationTypeById(@PathVariable Long code) {
		LOG.debug("REST request to get Innovation Type : {}", code);
		return this.innovationTypesItem.findInnovationTypeById(code);
	}

	/**
	 * Find a Maturity of Change requesting by id
	 * 
	 * @param id
	 * @return a MaturityOfChangeDTO with the Maturity of Change data.
	 */
	@ApiOperation(tags = "Table 3 - Outcome/ Impact Case Reports", value = "Search a maturity of change with an ID", response = MaturityOfChangeDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/maturities-of-change/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MaturityOfChangeDTO> findMaturityOfChangeById(@PathVariable Long code) {
		LOG.debug("REST request to get Maturity of Change : {}", code);
		return this.maturityOfChangeItem.findMaturityOfChangeById(code);
	}

	/**
	 * Find a Organization Type requesting by id
	 * 
	 * @param id
	 * @return a OrganizationTypeDTO with the Organization Type data.
	 */
	@ApiOperation(tags = "Table 4 - CRP Innovations", value = "Search a stage of innovation with an ID", response = OrganizationTypeDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/organization-types/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<OrganizationTypeDTO> findOrganizationTypeById(@PathVariable Long code) {
		LOG.debug("REST request to get  Organization Type : {}", code);
		return this.organizationTypeItem.findOrganizationTypeById(code);
	}

	/**
	 * Find a Policy Investment type by id
	 * 
	 * @param id
	 * @return a policyInvestmentTypeDTO with the Cross Cutting Marker data.
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "Search a cross cutting marker by ID", response = PolicyInvestmentTypeDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/policy-investment-types/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PolicyInvestmentTypeDTO> findPolicyInvestimentTypesById(@PathVariable Long code) {
		LOG.debug("REST request to get Cross Cutting Marker : {}", code);
		return this.policyInvestmentTypeItem.PolicyInvestmentTypeById(code);
	}

	/**
	 * Find a Policy Level of Maturity by id
	 * 
	 * @param id
	 * @return a PolicyLevelOfMaturityDTO with Policy Level of Maturity data.
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "Search a policy maturity level with an ID", response = PolicyMaturityLevelDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/policy-maturity-levels/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PolicyMaturityLevelDTO> findPolicyMaturityLevelById(@PathVariable Long code) {
		LOG.debug("REST request to get Cross Cutting Marker : {}", code);
		return this.policyMaturityLevelItem.PolicyMaturityLevelById(code);
	}

	/**
	 * Find a policy owner type by id
	 * 
	 * @param id
	 * @return a PolicyOwnerTypeDTO with the policy owner type
	 */
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "Search a policy owner type with an ID", response = PolicyOwnerTypeDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/policy-owner-types/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<PolicyOwnerTypeDTO> findPolicyOwnerTypeById(@PathVariable Long code) {
		LOG.debug("REST request to get Cross Cutting Marker : {}", code);
		return this.policyOwnerTypeItem.findPolicyOwnerTypeById(code);
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
	@RequestMapping(value = "/researchPartnership/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ResearchPartnershipDTO> findResearchPartnershipById(@PathVariable Long code) {
		LOG.debug("REST request to get ResearchPartnership : {}", code);
		return this.researchPartnershipsItem.findResearchPartnershipById(code);
	}

	/**
	 * Find a Stage of Innovation requesting by id
	 * 
	 * @param id
	 * @return a StageOfInnovationDTO with the Stage of Innovation data.
	 */
	@ApiOperation(tags = "Table 4 - CRP Innovations", value = "Search a stage of innovation with an ID", response = StageOfInnovationDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/stage-of-innovations/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StageOfInnovationDTO> findStageOfInnovationById(@PathVariable Long code) {
		LOG.debug("REST request to get Stage of Innovation : {}", code);
		return this.stageOfInnovationItem.findStageOfInnovationById(code);
	}

	/**
	 * Find a Tag requesting by id
	 * 
	 * @param id
	 * @return a TagDTO with the Tag data.
	 */
	@ApiOperation(tags = "Table 3 - Outcome/ Impact Case Reports", value = "Search a Tag with an ID", response = TagDTO.class)
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/tags/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<TagDTO> findTagsById(@PathVariable Long code) {
		LOG.debug("REST request to get  Tags : {}", code);
		return this.tagItem.findTagById(code);
	}

	/**
	 * Get All the Contribution of CRP Items *
	 * 
	 * @return a List of ContributionOfCrpDTO with all RepIndContributionOfCrp
	 * Items.
	 */
	@ApiIgnore
	@ApiOperation(value = "View all contribution of CRP to Innovation Types", response = ContributionOfCrpDTO.class, responseContainer = "List")
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
	@ApiOperation(tags = { "Table 2 - CRP Policies",
			"Table 3 - Outcome/ Impact Case Reports" }, value = "View all cross cutting markers", response = ContributionOfCrpDTO.class, responseContainer = "List")
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

	@ApiOperation(tags = { "Table 2 - CRP Policies",
			"Table 3 - Outcome/ Impact Case Reports" }, value = "View all cross cutting marker scores", response = CrossCuttingMarkerScoreDTO.class, responseContainer = "List")
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
	@ApiOperation(value = "View all degrees of innovations", response = DegreeOfInnovationDTO.class, responseContainer = "List", position = 1)
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
	@ApiOperation(tags = "Table 4 - CRP Innovations", value = "View all innovation types", response = InnovationTypeDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/innovation-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<InnovationTypeDTO> getAllInnovationTypes() {
		LOG.debug("REST request to get Innovation Types");
		return this.innovationTypesItem.getAllInnovationTypes();
	}

	/**
	 * Get All the Maturity of Change Items *
	 * 
	 * @return a List of MaturityOfChangeDTO with all Maturity of Change Items.
	 */

	@ApiOperation(tags = "Table 3 - Outcome/ Impact Case Reports", value = "View all maturity of changes", response = MaturityOfChangeDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/maturities-of-change", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<MaturityOfChangeDTO> getAllMaturityOfChanges() {
		LOG.debug("REST request to get  Maturity of Change");
		return this.maturityOfChangeItem.getAllMaturityOfChanges();
	}

	/**
	 * Get All the Stage of Innovations items
	 * 
	 * @return a List of StageOfInnovationDTO with all RepIndStageInnovation
	 * Items.
	 */
	@ApiOperation(tags = "Table 4 - CRP Innovations", value = "View all Organization Types", response = OrganizationTypeDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/organization-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<OrganizationTypeDTO> getAllOrganizationTypes() {
		LOG.debug("REST request to get  organization-types");
		return this.organizationTypeItem.getAllOrganizationTypes();
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

	@ApiOperation(tags = "Table 2 - CRP Policies", value = "View all policy level of maturity", response = PolicyMaturityLevelDTO.class, responseContainer = "List")
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
	@ApiOperation(tags = "Table 2 - CRP Policies", value = "View all policy owner Types", response = PolicyOwnerTypeDTO.class, responseContainer = "List")
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
	@ApiOperation(tags = "Table 4 - CRP Innovations", value = "View all Stages of Innovations", response = StageOfInnovationDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/stage-of-innovations", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<StageOfInnovationDTO> getAllStageOfInnovations() {
		LOG.debug("REST request to get  Stage of Innovations");
		return this.stageOfInnovationItem.getAllStageOfInnovations();
	}

	/**
	 * Get All tags Items *
	 * 
	 * @return a List of TagDTO with all tags Items.
	 */
	@ApiOperation(tags = "Table 3 - Outcome/ Impact Case Reports", value = "View all degrees of innovations", response = TagDTO.class, responseContainer = "List")
	@RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
	@RequestMapping(value = "/tags", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<TagDTO> getAllTags() {
		LOG.debug("REST request to get  all Tags");
		return this.tagItem.getAllTags();
	}

}
