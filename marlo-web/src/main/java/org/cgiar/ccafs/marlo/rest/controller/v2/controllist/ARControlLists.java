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

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.powbar.ContributionOfCrpItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.powbar.CrossCuttingMarkerScoreItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.powbar.DegreeOfInnovationItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.powbar.InnovationTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.powbar.MaturityOfChangeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.powbar.PolicyInvestimentTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.powbar.ResearchPartnershipItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.powbar.StageOfInnovationItem;
import org.cgiar.ccafs.marlo.rest.dto.ContributionOfCrpDTO;
import org.cgiar.ccafs.marlo.rest.dto.CrossCuttingMarkerScoreDTO;
import org.cgiar.ccafs.marlo.rest.dto.DegreeOfInnovationDTO;
import org.cgiar.ccafs.marlo.rest.dto.InnovationTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.MaturityOfChangeDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyInvestimentTypeDTO;
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
  private InnovationTypeItem<ARControlLists> innovationTypesItem;
  private ResearchPartnershipItem<ARControlLists> researchPartnershipsItem;
  private StageOfInnovationItem<ARControlLists> stageOfInnovationItem;
  private ContributionOfCrpItem<ARControlLists> contributionOfCrpItem;
  private DegreeOfInnovationItem<ARControlLists> degreeOfInnovationItem;
  private MaturityOfChangeItem<ARControlLists> maturityOfChangeItem;
  private PolicyInvestimentTypeItem<ARControlLists> policyInvestimentTypeItem;

  @Inject
  public ARControlLists(CrossCuttingMarkerScoreItem<ARControlLists> crossCuttingMarkerScoreItem,
    InnovationTypeItem<ARControlLists> innovationTypesItem,
    ResearchPartnershipItem<ARControlLists> researchPartnershipsItem,
    StageOfInnovationItem<ARControlLists> stageOfInnovationItem,
    ContributionOfCrpItem<ARControlLists> contributionOfCrpItem,
    DegreeOfInnovationItem<ARControlLists> degreeOfInnovationItem,
    MaturityOfChangeItem<ARControlLists> maturityOfChangeItem,
    PolicyInvestimentTypeItem<ARControlLists> policyInvestimentTypeItem) {
    this.crossCuttingMarkerScoreItem = crossCuttingMarkerScoreItem;
    this.innovationTypesItem = innovationTypesItem;
    this.researchPartnershipsItem = researchPartnershipsItem;
    this.stageOfInnovationItem = stageOfInnovationItem;
    this.contributionOfCrpItem = contributionOfCrpItem;
    this.degreeOfInnovationItem = degreeOfInnovationItem;
    this.maturityOfChangeItem = maturityOfChangeItem;
    this.policyInvestimentTypeItem = policyInvestimentTypeItem;

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
  @RequestMapping(value = "/contributionOfCrp/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
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
  @RequestMapping(value = "/degreeOfInnovation/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
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
  @RequestMapping(value = "/maturityOfChange/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
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
  @RequestMapping(value = "/stageOfInnovation/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<StageOfInnovationDTO> findStageOfInnovationById(@PathVariable Long id) {
    LOG.debug("REST request to get Stage of Innovation : {}", id);
    return this.stageOfInnovationItem.findStageOfInnovationById(id);
  }

  /**
   * Get All the Contribution of CRP Items *
   * 
   * @return a List of ContributionOfCrpDTO with all RepIndContributionOfCrp
   *         Items.
   */
  @ApiIgnore
  @ApiOperation(value = "View all The Contribution of CRP to Innovation Types", response = ContributionOfCrpDTO.class,
    responseContainer = "List", position = 1)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/contributionOfCrps", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ContributionOfCrpDTO> getAllContributionOfCrps() {
    LOG.debug("REST request to get Contribution of CRP");
    return this.contributionOfCrpItem.getAllContributionOfCrps();
  }

  /**
   * Get All the Cross Cutting Markers items
   * 
   * @return a List of CrossCuttingMarkersDTO with all Cross Cutting Markers
   *         Items.
   */

  @ApiOperation(tags = "Table2 - CRP Policies", value = "View all The Cross Cutting Markers",
    response = CrossCuttingMarkerScoreDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/cross-cutting-marker-scores", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<CrossCuttingMarkerScoreDTO> getAllCrossCuttingMarkerScores() {
    LOG.debug("REST request to get Cross Cutting Markers");
    return this.crossCuttingMarkerScoreItem.getAllCrossCuttingMarkers();
  }

  /**
   * Get All the Degree of Innovation Items *
   * 
   * @return a List of DegreeOfInnovationDTO with all RepIndDegreeInnovation
   *         Items.
   */
  @ApiIgnore
  @ApiOperation(value = "View all The Degree of Innovations", response = DegreeOfInnovationDTO.class,
    responseContainer = "List", position = 1)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/degreeOfInnovations", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
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
  @ApiOperation(value = "View all The Innovation Types", response = InnovationTypeDTO.class, responseContainer = "List",
    position = 1)
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
  @ApiOperation(value = "View all The Maturity of Changes", response = MaturityOfChangeDTO.class,
    responseContainer = "List", position = 1)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/maturity-of-changes", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<MaturityOfChangeDTO> getAllMaturityOfChanges() {
    LOG.debug("REST request to get  Maturity of Change");
    return this.maturityOfChangeItem.getAllMaturityOfChanges();
  }

  /**
   * get all policy investiment types
   * 
   * @return a List of policyInvestmentTypeDTO with all
   *         RepIndPolicyInvestimentType Items.
   */
  @ApiOperation(tags = "Table2 - CRP Policies", value = "View all policy investiment types",
    response = PolicyInvestimentTypeDTO.class, responseContainer = "List", position = 1)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/policy-investiment-types", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<PolicyInvestimentTypeDTO> getAllPolicyInvestimentType() {
    LOG.debug("REST request to get  Degree of Innovations");
    return this.policyInvestimentTypeItem.getAllPolicyInvestimentType();
  }

  /**
   * Get All the Research Partnerships items
   * 
   * @return a List of ResearchPartnershipsDTO with all ResearchPartnerships
   *         Items.
   */
  @ApiIgnore
  @ApiOperation(value = "View all The Research Partnerships", response = ResearchPartnershipDTO.class,
    responseContainer = "List", position = 1)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/research-partnerships", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ResearchPartnershipDTO> getAllResearchPartnerships() {
    LOG.debug("REST request to get ResearchPartnerships");
    return this.researchPartnershipsItem.getAllResearchPartnerships();
  }

  /**
   * Get All the Stage of Innovations items
   * 
   * @return a List of StageOfInnovationDTO with all RepIndStageInnovation
   *         Items.
   */
  @ApiIgnore
  @ApiOperation(value = "View all The Stage of Innovations", response = StageOfInnovationDTO.class,
    responseContainer = "List", position = 1)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/stageOfInnovations", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<StageOfInnovationDTO> getAllStageOfInnovations() {
    LOG.debug("REST request to get  Stage of Innovations");
    return this.stageOfInnovationItem.getAllStageOfInnovations();
  }

  /**
   * Find a Cross Cutting Marker score by id
   * 
   * @param id
   * @return a CrossCuttingMarkersDTO with the Cross Cutting Marker data.
   */
  @ApiOperation(tags = "Table2 - CRP Policies", value = "Search a Cross Cutting Marker with an ID",
    response = CrossCuttingMarkerScoreDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/cross-cutting-marker-scores/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
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
  @RequestMapping(value = "/innovationType/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
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
  @ApiOperation(tags = "Table2 - CRP Policies", value = "Search a Cross Cutting Marker with an ID",
    response = PolicyInvestimentTypeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/policy-investiment-types/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<PolicyInvestimentTypeDTO> getPolicyInvestimentTypesById(@PathVariable Long id) {
    LOG.debug("REST request to get Cross Cutting Marker : {}", id);
    return this.policyInvestimentTypeItem.PolicyInvestimentTypeById(id);
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
  @RequestMapping(value = "/researchPartnership/{id}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ResearchPartnershipDTO> getResearchPartnershipById(@PathVariable Long id) {
    LOG.debug("REST request to get ResearchPartnership : {}", id);
    return this.researchPartnershipsItem.findResearchPartnershipById(id);
  }

}
