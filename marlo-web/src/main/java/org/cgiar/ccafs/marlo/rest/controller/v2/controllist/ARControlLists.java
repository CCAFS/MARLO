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

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.BroadAreaItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.BudgetTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.ContributionOfCrpItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.CrossCuttingMarkerItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.CrossCuttingMarkerScoreItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.DegreeOfInnovationItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.InnovationTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.MaturityOfChangeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.MilestoneStatusItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.OrganizationTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.PartnershipMainAreaItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.PolicyInvestmentTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.PolicyMaturityLevelItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.PolicyOwnerTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.ResearchPartnershipItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.StageOfInnovationItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.StatusOfResponseItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.StudyTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.arcontrollists.TagItem;
import org.cgiar.ccafs.marlo.rest.dto.BroadAreaDTO;
import org.cgiar.ccafs.marlo.rest.dto.BudgetTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrossCuttingMarkerDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrossCuttingMarkerScoreDTO;
import org.cgiar.ccafs.marlo.rest.dto.InnovationTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.MaturityOfChangeDTO;
import org.cgiar.ccafs.marlo.rest.dto.MilestoneStatusDTO;
import org.cgiar.ccafs.marlo.rest.dto.OrganizationTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.PartnershipMainAreaDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyInvestmentTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyMaturityLevelDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyOwnerTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.StageOfInnovationDTO;
import org.cgiar.ccafs.marlo.rest.dto.StatusOfResponseDTO;
import org.cgiar.ccafs.marlo.rest.dto.StudyTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.TagDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@PropertySource("classpath:clarisa.properties")
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
  private StudyTypeItem<ARControlLists> studyTypeItem;
  private TagItem<ARControlLists> tagItem;
  private PartnershipMainAreaItem<ARControlLists> partnershipMainAreaItem;
  private BudgetTypeItem<ARControlLists> bugdetTypeItem;
  private BroadAreaItem<ARControlLists> broadAreaItem;
  private StatusOfResponseItem<ARControlLists> statusOfResponseItem;
  private MilestoneStatusItem<ARControlLists> milestoneStatusItem;

  @Autowired
  private Environment env;

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
    OrganizationTypeItem<ARControlLists> organizationTypeItem, StudyTypeItem<ARControlLists> studyTypeItem,
    TagItem<ARControlLists> tagItem, PartnershipMainAreaItem<ARControlLists> partnershipMainAreaItem,
    BudgetTypeItem<ARControlLists> bugdetTypeItem, BroadAreaItem<ARControlLists> broadAreaItem,
    StatusOfResponseItem<ARControlLists> statusOfResponseItem,
    MilestoneStatusItem<ARControlLists> milestoneStatusItem) {
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
    this.studyTypeItem = studyTypeItem;
    this.partnershipMainAreaItem = partnershipMainAreaItem;
    this.bugdetTypeItem = bugdetTypeItem;
    this.broadAreaItem = broadAreaItem;
    this.statusOfResponseItem = statusOfResponseItem;
    this.milestoneStatusItem = milestoneStatusItem;
  }

  /**
   * Find a Broad Area by id
   * 
   * @param id
   * @return a BroadAreaDTO with the Broad Area data.
   */
  @ApiOperation(tags = {"Table 12 - Examples of W1/2 Use"}, value = "${ARControlLists.broad-areas.code.value}",
    response = BroadAreaDTO.class)

  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/broad-areas/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BroadAreaDTO> findBroadAreaById(
    @ApiParam(value = "${ARControlLists.broad-areas.code.param.code}", required = true) @PathVariable Long code) {

    ResponseEntity<BroadAreaDTO> response = this.broadAreaItem.findBroadAreaById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.broad-areas.code.404"));
    }
    return response;
  }

  /**
   * Find a Budget Type by id
   * 
   * @param id
   * @return a BudgetTypeDTO with the Budget Type data.
   */
  @ApiOperation(tags = {"Table 13 - CRP Financial Report"}, value = "${ARControlLists.budget-types.code.value}",
    response = BudgetTypeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/budget-types/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BudgetTypeDTO> findBudgetTypeById(
    @ApiParam(value = "${ARControlLists.budget-types.code.param.code}", required = true) @PathVariable Long code) {
    ResponseEntity<BudgetTypeDTO> response = this.bugdetTypeItem.findBudgetTypeById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.budget-types.code.404"));
    }
    return response;
  }


  /**
   * Find a Cross Cutting Marker by id
   * 
   * @param id
   * @return a CrossCuttingMarkerScoreDTO with the Cross Cutting Marker data.
   */
  @ApiOperation(tags = {"Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports"},
    value = "${ARControlLists.cross-cutting-markers.code.value}", response = CrossCuttingMarkerScoreDTO.class)

  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/cross-cutting-markers/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CrossCuttingMarkerDTO>
    findCrossCuttingMarkerById(@ApiParam(value = "${ARControlLists.cross-cutting-markers.code.param.code}",
      required = true) @PathVariable Long code) {

    ResponseEntity<CrossCuttingMarkerDTO> response = this.crossCuttingMarkerItem.findCrossCuttingMarkerById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.cross-cutting-markers.code.404"));
    }
    return response;
  }

  /**
   * Find a Cross Cutting Marker score by id
   * 
   * @param id
   * @return a CrossCuttingMarkerDTO with the Cross Cutting Marker data.
   */
  @ApiOperation(tags = {"Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports"},
    value = "${ARControlLists.cross-cutting-marker-scores.code.value}", response = CrossCuttingMarkerScoreDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/cross-cutting-marker-scores/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CrossCuttingMarkerScoreDTO>
    findCrossCuttingMarkerScoreById(@ApiParam(value = "${ARControlLists.cross-cutting-marker-scores.code.param.code}",
      required = true) @PathVariable Long code) {
    ResponseEntity<CrossCuttingMarkerScoreDTO> response =
      this.crossCuttingMarkerScoreItem.findCrossCuttingMarkerScoreById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.cross-cutting-marker-scores.code.404"));
    }
    return response;
  }

  /**
   * Find a Innovation Type requesting by id
   * 
   * @param id
   * @return a InnovationTypesDTO with the Innovation Type data.
   */

  @ApiOperation(tags = "Table 4 - CRP Innovations", value = "${ARControlLists.innovation-types.code.value}",
    response = InnovationTypeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/innovation-types/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InnovationTypeDTO> findInnovationTypeById(
    @ApiParam(value = "${ARControlLists.innovation-types.code.param.code}", required = true) @PathVariable Long code) {
    ResponseEntity<InnovationTypeDTO> response = this.innovationTypesItem.findInnovationTypeById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.innovation-types.code.404"));
    }
    return response;
  }

  /**
   * Find a Maturity of Change requesting by id
   * 
   * @param id
   * @return a MaturityOfChangeDTO with the Maturity of Change data.
   */
  @ApiOperation(tags = "Table 3 - Outcome/ Impact Case Reports",
    value = "${ARControlLists.maturities-of-change.code.value}", response = MaturityOfChangeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/maturities-of-change/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MaturityOfChangeDTO>
    findMaturityOfChangeById(@ApiParam(value = "${ARControlLists.maturities-of-change.code.param.code}",
      required = true) @PathVariable Long code) {
    ResponseEntity<MaturityOfChangeDTO> response = this.maturityOfChangeItem.findMaturityOfChangeById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.maturities-of-change.code.404"));
    }
    return response;

  }

  /**
   * Find a Milestone status by id
   * 
   * @param id
   * @return a MilestoneStatusDTO with the Milestone Status data.
   */
  @ApiOperation(tags = {"Table 5 - Status of Planned Outcomes and Milestones"},
    value = "${ARControlLists.milestone-statuses.code.value}", response = MilestoneStatusDTO.class)

  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/milestone-statuses/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MilestoneStatusDTO>
    findMilestoneStatusById(@ApiParam(value = "${ARControlLists.milestone-statuses.code.param.code}",
      required = true) @PathVariable Long code) {

    ResponseEntity<MilestoneStatusDTO> response = this.milestoneStatusItem.findMilestoneStatusById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.milestone-statuses.code.404"));
    }
    return response;
  }

  /**
   * Find a Organization Type requesting by id
   * 
   * @param id
   * @return a OrganizationTypeDTO with the Organization Type data.
   */
  @ApiOperation(tags = "Table 4 - CRP Innovations", value = "${ARControlLists.organization-types.code.value}",
    response = OrganizationTypeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/organization-types/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<OrganizationTypeDTO>
    findOrganizationTypeById(@ApiParam(value = "${ARControlLists.organization-types.code.param.code}",
      required = true) @PathVariable Long code) {
    ResponseEntity<OrganizationTypeDTO> response = this.organizationTypeItem.findOrganizationTypeById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.organization-types.code.404"));
    }
    return response;

  }

  /**
   * Find a partnership Main Area by id
   * 
   * @param id
   * @return a PartnershipMainAreaDTO with the partnership Main Area
   */
  @ApiOperation(tags = {"Table 8 - Key external partnerships"},
    value = "${ARControlLists.partnership-main-areas.code.value}", response = PartnershipMainAreaDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/partnership-main-areas/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PartnershipMainAreaDTO>
    findPartnershipMainAreaById(@ApiParam(value = "${ARControlLists.partnership-main-areas.code.param.code}",
      required = true) @PathVariable Long code) {
    ResponseEntity<PartnershipMainAreaDTO> response = this.partnershipMainAreaItem.findPartnershipMainAreaById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.partnership-main-areas.code.404"));
    }
    return response;

  }

  /**
   * Find a Policy Investment type by id
   * 
   * @param id
   * @return a policyInvestmentTypeDTO with the Cross Cutting Marker data.
   */
  @ApiOperation(tags = "Table 2 - CRP Policies", value = "${ARControlLists.policy-investment-types.code.value}",
    response = PolicyInvestmentTypeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/policy-investment-types/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PolicyInvestmentTypeDTO>
    findPolicyInvestimentTypesById(@ApiParam(value = "${ARControlLists.policy-investment-types.code.param.code}",
      required = true) @PathVariable Long code) {
    ResponseEntity<PolicyInvestmentTypeDTO> response = this.policyInvestmentTypeItem.PolicyInvestmentTypeById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.policy-investment-types.code.404"));
    }
    return response;

  }

  /**
   * Find a Policy Level of Maturity by id
   * 
   * @param id
   * @return a PolicyLevelOfMaturityDTO with Policy Level of Maturity data.
   */
  @ApiOperation(tags = "Table 2 - CRP Policies", value = "${ARControlLists.policy-maturity-levels.code.value}",
    response = PolicyMaturityLevelDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/policy-maturity-levels/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PolicyMaturityLevelDTO>
    findPolicyMaturityLevelById(@ApiParam(value = "${ARControlLists.policy-maturity-levels.code.param.code}",
      required = true) @PathVariable Long code) {
    ResponseEntity<PolicyMaturityLevelDTO> response = this.policyMaturityLevelItem.PolicyMaturityLevelById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.policy-maturity-levels.code.404"));
    }
    return response;

  }

  /**
   * Find a policy owner type by id
   * 
   * @param id
   * @return a PolicyOwnerTypeDTO with the policy owner type
   */
  @ApiOperation(tags = "Table 2 - CRP Policies", value = "${ARControlLists.policy-owner-types.code.value}",
    response = PolicyOwnerTypeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/policy-owner-types/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PolicyOwnerTypeDTO>
    findPolicyOwnerTypeById(@ApiParam(value = "${ARControlLists.policy-owner-types.code.param.code}",
      required = true) @PathVariable Long code) {
    ResponseEntity<PolicyOwnerTypeDTO> response = this.policyOwnerTypeItem.findPolicyOwnerTypeById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.policy-owner-types.code.404"));
    }
    return response;

  }

  /**
   * Find a Stage of Innovation requesting by id
   * 
   * @param id
   * @return a StageOfInnovationDTO with the Stage of Innovation data.
   */
  @ApiOperation(tags = "Table 4 - CRP Innovations", value = "${ARControlLists.stage-of-innovations.code.value}",
    response = StageOfInnovationDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/stage-of-innovations/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StageOfInnovationDTO>
    findStageOfInnovationById(@ApiParam(value = "${ARControlLists.stage-of-innovations.code.param.code}",
      required = true) @PathVariable Long code) {
    ResponseEntity<StageOfInnovationDTO> response = this.stageOfInnovationItem.findStageOfInnovationById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.stage-of-innovations.code.404"));
    }
    return response;

  }

  /**
   * Find a Status of response by id
   * 
   * @param id
   * @return a StatusOfResponseDTO with the Status of response data.
   */
  @ApiOperation(tags = {"Table 11 - Update on Actions Taken in Response to Relevant Evaluations"},
    value = "${ARControlLists.status-of-response.code.value}", response = StatusOfResponseDTO.class)

  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/status-of-response/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StatusOfResponseDTO>
    findStatusOfResponseById(@ApiParam(value = "${ARControlLists.status-of-response.code.param.code}",
      required = true) @PathVariable Long code) {

    ResponseEntity<StatusOfResponseDTO> response = this.statusOfResponseItem.findStatusOfResponseById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.status-of-response.code.404"));
    }
    return response;
  }

  /**
   * Find a study Type by id
   * 
   * @param id
   * @return a CStudyTypeDTO with the study Type data.
   */
  @ApiOperation(tags = {"Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)"},
    value = "${ARControlLists.study-types.code.value}", response = StudyTypeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/study-types/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StudyTypeDTO> findStudyTypeById(
    @ApiParam(value = "${ARControlLists.study-types.code.param.code}", required = true) @PathVariable Long code) {
    ResponseEntity<StudyTypeDTO> response = this.studyTypeItem.findStudyTypeById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.study-types.code.404"));
    }
    return response;

  }

  /**
   * Find a Tag requesting by id
   * 
   * @param id
   * @return a TagDTO with the Tag data.
   */
  @ApiOperation(tags = "Table 3 - Outcome/ Impact Case Reports", value = "${ARControlLists.tags.code.value}",
    response = TagDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/tags/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<TagDTO>
    findTagsById(@ApiParam(value = "${ARControlLists.tags.code.param.code}", required = true) @PathVariable Long code) {
    ResponseEntity<TagDTO> response = this.tagItem.findTagById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("ARControlLists.tags.code.404"));
    }
    return response;

  }

  /**
   * Get All the Broad Area items
   * 
   * @return a List of BroadArea with all Cross Cutting Markers Items.
   */

  @ApiOperation(tags = {"Table 12 - Examples of W1/2 Use"}, value = "${ARControlLists.broad-areas.all.value}",
    response = BroadAreaDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/broad-areas", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<BroadAreaDTO> getAllBroadAreas() {
    return this.broadAreaItem.getAllBroadAreas();
  }

  /**
   * Get All the Budget types items
   * 
   * @return a List of CrossCuttingMarkersDTO with all Cross Cutting Markers
   *         Items.
   */

  @ApiOperation(tags = {"Table 13 - CRP Financial Report"}, value = "${ARControlLists.budget-types.all.value}",
    response = BudgetTypeDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/budget-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<BudgetTypeDTO> getAllBudgerTypes() {
    return this.bugdetTypeItem.getAllBudgetTypes();
  }

  /**
   * Get All the cross cutting markers of CRP Items *
   * 
   * @return a List of cross cutting markers
   */
  @ApiOperation(tags = {"Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports"},
    value = "${ARControlLists.cross-cutting-markers.all.value}", response = CrossCuttingMarkerDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/cross-cutting-markers", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<CrossCuttingMarkerDTO> getAllCrossCuttingMarkers() {
    return this.crossCuttingMarkerItem.getAllCrossCuttingMarker();
  }

  /**
   * Get All the Cross Cutting Markers items
   * 
   * @return a List of CrossCuttingMarkersDTO with all Cross Cutting Markers
   *         Items.
   */

  @ApiOperation(tags = {"Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports"},
    value = "${ARControlLists.cross-cutting-marker-scores.all.value}", response = CrossCuttingMarkerScoreDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/cross-cutting-marker-scores", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<CrossCuttingMarkerScoreDTO> getAllCrossCuttingMarkerScores() {
    return this.crossCuttingMarkerScoreItem.getAllCrossCuttingMarkersScores();
  }

  /**
   * Get All the Innovation Types items
   * 
   * @return a List of InnovationTypesDTO with all Innovation Types Items.
   */
  @ApiOperation(tags = "Table 4 - CRP Innovations", value = "${ARControlLists.innovation-types.all.value}",
    response = InnovationTypeDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/innovation-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<InnovationTypeDTO> getAllInnovationTypes() {
    return this.innovationTypesItem.getAllInnovationTypes();
  }

  /**
   * Get All the Maturity of Change Items *
   * 
   * @return a List of MaturityOfChangeDTO with all Maturity of Change Items.
   */

  @ApiOperation(tags = "Table 3 - Outcome/ Impact Case Reports",
    value = "${ARControlLists.maturities-of-change.all.value}", response = MaturityOfChangeDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/maturities-of-change", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<MaturityOfChangeDTO> getAllMaturityOfChanges() {
    return this.maturityOfChangeItem.getAllMaturityOfChanges();
  }

  /**
   * Get All the milestone Statuses items
   * 
   * @return a List of MilestoneStatusDTO with all Milestone Statuses Items.
   */

  @ApiOperation(tags = {"Table 5 - Status of Planned Outcomes and Milestones"},
    value = "${ARControlLists.milestone-statuses.all.value}", response = MilestoneStatusDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/milestone-statuses", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<MilestoneStatusDTO> getAllMilestoneStatuses() {
    return this.milestoneStatusItem.getAllMilestoneStatus();
  }

  /**
   * Get All the Stage of Innovations items
   * 
   * @return a List of StageOfInnovationDTO with all RepIndStageInnovation
   *         Items.
   */
  @ApiOperation(tags = "Table 4 - CRP Innovations", value = "${ARControlLists.organization-types.all.value}",
    response = OrganizationTypeDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/organization-types", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<OrganizationTypeDTO> getAllOrganizationTypes() {
    return this.organizationTypeItem.getAllOrganizationTypes();
  }

  /**
   * Get All the cross cutting markers of CRP Items *
   * 
   * @return a List of cross cutting markers
   */
  @ApiOperation(tags = {"Table 8 - Key external partnerships"},
    value = "${ARControlLists.partnership-main-areas.all.value}", response = PartnershipMainAreaDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/partnership-main-areas", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PartnershipMainAreaDTO> getAllPartnershipMainArea() {
    return this.partnershipMainAreaItem.getAllPartnershipMainAreas();
  }

  /**
   * get all policy investiment types
   * 
   * @return a List of policyInvestmentTypeDTO with all
   *         RepIndPolicyInvestimentType Items.
   */
  @ApiOperation(tags = "Table 2 - CRP Policies", value = "${ARControlLists.policy-investment-types.all.value}",
    response = PolicyInvestmentTypeDTO.class, responseContainer = "List", position = 1)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/policy-investment-types", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PolicyInvestmentTypeDTO> getAllPolicyInvestmentType() {
    return this.policyInvestmentTypeItem.getAllPolicyInvestmentType();
  }

  /**
   * Get All the Policy level of maturity items
   * 
   * @return a List of PolicyLevelOfMaturityDTO with all olicy level of
   *         maturity Items.
   */

  @ApiOperation(tags = "Table 2 - CRP Policies", value = "${ARControlLists.policy-maturity-levels.all.value}",
    response = PolicyMaturityLevelDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/policy-maturity-levels", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PolicyMaturityLevelDTO> getAllPolicyMaturityLevels() {
    return this.policyMaturityLevelItem.getAllPolicyMaturityLevel();
  }

  /**
   * Get All the policy owner types
   * 
   * @return a List of PolicyOwnerTypeDTO with all the policy owner types
   */
  @ApiOperation(tags = "Table 2 - CRP Policies", value = "${ARControlLists.policy-owner-types.all.value}",
    response = PolicyOwnerTypeDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/policy-owner-types", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PolicyOwnerTypeDTO> getAllPolicyOwnerTypes() {
    return this.policyOwnerTypeItem.getAllPolicyOwnerTypes();
  }

  /**
   * Get All the Stage of Innovations items
   * 
   * @return a List of StageOfInnovationDTO with all RepIndStageInnovation
   *         Items.
   */
  @ApiOperation(tags = "Table 4 - CRP Innovations", value = "${ARControlLists.stage-of-innovations.all.value}",
    response = StageOfInnovationDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/stage-of-innovations", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<StageOfInnovationDTO> getAllStageOfInnovations() {
    return this.stageOfInnovationItem.getAllStageOfInnovations();
  }

  /**
   * Get All the Status of response Items *
   * 
   * @return a List of StatusOfResponseDTO with all Status of response Items.
   */

  @ApiOperation(tags = "Table 11 - Update on Actions Taken in Response to Relevant Evaluations",
    value = "${ARControlLists.status-of-response.all.value}", response = StatusOfResponseDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/status-of-response", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<StatusOfResponseDTO> getAllStatusOfResponse() {
    return this.statusOfResponseItem.getAllStatusOfResponse();
  }

  /**
   * Get All the Study types items
   * 
   * @return a List of StudyTypesDTO with all Study types items
   */
  @ApiOperation(tags = "Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)",
    value = "${ARControlLists.study-types.all.value}", response = StudyTypeDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/study-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<StudyTypeDTO> getAllStudyTypes() {
    return this.studyTypeItem.getAllStudyTypes();
  }

  /**
   * Get All tags Items *
   * 
   * @return a List of TagDTO with all tags Items.
   */
  @ApiOperation(tags = "Table 3 - Outcome/ Impact Case Reports", value = "${ARControlLists.tags.all.value}",
    response = TagDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/tags", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<TagDTO> getAllTags() {
    return this.tagItem.getAllTags();
  }

}
