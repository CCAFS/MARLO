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

/**************
 * @author Diego Perez - CIAT/CCAFS
 **************/

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist;

import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools.ActionAreaOutcomeIndicatorsItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools.ActionAreaOutcomesItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools.ActionAreasItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools.GlobalTargetsItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools.ImpactAreasIndicatorsItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools.ImpactAreasItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools.InitiativesItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools.ProjectedBenefitsItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools.SdgItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.submissiontools.WorkpackagesItem;
import org.cgiar.ccafs.marlo.rest.dto.ActionAreaOutcomeDTO;
import org.cgiar.ccafs.marlo.rest.dto.ActionAreaOutcomeIndicatorDTO;
import org.cgiar.ccafs.marlo.rest.dto.ActionAreasDTO;
import org.cgiar.ccafs.marlo.rest.dto.DepthDescriptionsDTO;
import org.cgiar.ccafs.marlo.rest.dto.GlobalTargetDTO;
import org.cgiar.ccafs.marlo.rest.dto.ImpactAreasDTO;
import org.cgiar.ccafs.marlo.rest.dto.ImpactAreasIndicatorsDTO;
import org.cgiar.ccafs.marlo.rest.dto.InitiativesDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectedBenefitsDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectedBenefitsDepthScaleDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectedBenefitsProbabilitiesDTO;
import org.cgiar.ccafs.marlo.rest.dto.SDGIndicatorDTO;
import org.cgiar.ccafs.marlo.rest.dto.SDGTargetDTO;
import org.cgiar.ccafs.marlo.rest.dto.SDGsDTO;
import org.cgiar.ccafs.marlo.rest.dto.WorkPackagesDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;

import javax.inject.Named;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.authz.annotation.RequiresPermissions;
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
import springfox.documentation.annotations.ApiIgnore;


@Configuration
@PropertySource("classpath:clarisa.properties")
@RestController
@Api(tags = "Submission Tool Control Lists")
@ApiIgnore
@Named
public class SubmissionToolsControlLists {


  private ActionAreasItem<SubmissionToolsControlLists> actionAreasItem;
  private ImpactAreasItem<SubmissionToolsControlLists> impactAreasItem;
  private ImpactAreasIndicatorsItem<SubmissionToolsControlLists> impactAreasIndicatorsItem;
  private SdgItem<SubmissionToolsControlLists> sdgItem;
  private InitiativesItem<SubmissionToolsControlLists> initiativesItem;
  private ProjectedBenefitsItem<SubmissionToolsControlLists> projectedBenefitsItem;
  private ActionAreaOutcomesItem<SubmissionToolsControlLists> actionAreaOutcomesItem;
  private WorkpackagesItem<SubmissionToolsControlLists> workpackagesItem;
  private ActionAreaOutcomeIndicatorsItem<SubmissionToolsControlLists> actionAreaOutcomeIndicatorsItem;
  private GlobalTargetsItem<SubmissionToolsControlLists> globalTargetsItem;


  @Autowired
  private Environment env;


  public SubmissionToolsControlLists(ActionAreasItem<SubmissionToolsControlLists> actionAreasItem,
    ImpactAreasItem<SubmissionToolsControlLists> impactAreasItem,
    ImpactAreasIndicatorsItem<SubmissionToolsControlLists> impactAreasIndicatorsItem,
    SdgItem<SubmissionToolsControlLists> sdgItem, InitiativesItem<SubmissionToolsControlLists> initiativesItem,
    ProjectedBenefitsItem<SubmissionToolsControlLists> projectedBenefitsItem,
    ActionAreaOutcomesItem<SubmissionToolsControlLists> actionAreaOutcomesItem,
    WorkpackagesItem<SubmissionToolsControlLists> workpackagesItem,
    ActionAreaOutcomeIndicatorsItem<SubmissionToolsControlLists> actionAreaOutcomeIndicatorsItem,
    GlobalTargetsItem<SubmissionToolsControlLists> globalTargetsItem) {
    super();
    this.actionAreasItem = actionAreasItem;
    this.impactAreasItem = impactAreasItem;
    this.impactAreasIndicatorsItem = impactAreasIndicatorsItem;
    this.sdgItem = sdgItem;
    this.initiativesItem = initiativesItem;
    this.projectedBenefitsItem = projectedBenefitsItem;
    this.actionAreaOutcomesItem = actionAreaOutcomesItem;
    this.workpackagesItem = workpackagesItem;
    this.actionAreaOutcomeIndicatorsItem = actionAreaOutcomeIndicatorsItem;
    this.globalTargetsItem = globalTargetsItem;
  }

  @ApiOperation(tags = {"Submission Tool Control Lists"},
    value = "${SubmissionToolsControlLists.action-areas.code.value}", response = ActionAreasDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/action-areas/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ActionAreasDTO>
    findActionAreaById(@ApiParam(value = "${SubmissionToolsControlLists.action-areas.code.param.code}",
      required = true) @PathVariable Long code) {

    ResponseEntity<ActionAreasDTO> response = this.actionAreasItem.findActionAreaById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("SubmissionToolsControlLists.action-areas.code.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Submission Tool Control Lists"},
    value = "${SubmissionToolsControlLists.impact-areas-indicator.code.value}", response = GlobalTargetDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/globalTargets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<GlobalTargetDTO> findAllGlobalTargets() {
    return globalTargetsItem.getAllGlobalTarget();
  }

  @ApiOperation(tags = {"Submission Tool Control Lists"},
    value = "${SubmissionToolsControlLists.impact-areas.code.value}", response = ImpactAreasDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/impact-areas/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ImpactAreasDTO>
    findImpactAreaById(@ApiParam(value = "${SubmissionToolsControlLists.impact-areas.code.param.code}",
      required = true) @PathVariable Long code) {

    ResponseEntity<ImpactAreasDTO> response = this.impactAreasItem.findActionAreaById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("SubmissionToolsControlLists.impact-areas.code.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Submission Tool Control Lists"},
    value = "${SubmissionToolsControlLists.impact-areas-indicator.code.value}",
    response = ImpactAreasIndicatorsDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/impact-areas-indicator/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ImpactAreasIndicatorsDTO> findImpactAreaIndicatorById(
    @ApiParam(value = "${SubmissionToolsControlLists.impact-areas-indicator.code.param.code}",
      required = true) @PathVariable Long code) {

    ResponseEntity<ImpactAreasIndicatorsDTO> response =
      this.impactAreasIndicatorsItem.findImpactAreaIndicatorById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404",
        this.env.getProperty("SubmissionToolsControlLists.impact-areas-indicator.code.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"Submission Tools Control Lists"}, value = "${SubmissionToolsControlLists.sdgTarget.all.value}",
    response = ActionAreaOutcomeDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/actionAreaOutcomeIndicators", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ActionAreaOutcomeIndicatorDTO> getActionAreaOutcomeIndicators() {
    return this.actionAreaOutcomeIndicatorsItem.getAllActionAreaOutcomeIndicators();
  }

  @ApiOperation(tags = {"Submission Tools Control Lists"}, value = "${SubmissionToolsControlLists.sdgTarget.all.value}",
    response = ActionAreaOutcomeDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/actionAreaOutcomes", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ActionAreaOutcomeDTO> getActionAreaOutcomes() {
    return this.actionAreaOutcomesItem.getAllActionAreaOutcomes();
  }

  @ApiOperation(tags = {"Submission Tool Control Lists"},
    value = "${SubmissionToolsControlLists.action-areas.all.value}", response = ActionAreasDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/action-areas", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ActionAreasDTO> getAllActionAreas() {
    return this.actionAreasItem.getAllActionAreas();
  }

  @ApiOperation(tags = {"Submission Tool Control Lists"},
    value = "${SubmissionToolsControlLists.impact-areas.all.value}", response = ImpactAreasDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/impact-areas", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ImpactAreasDTO> getAllImpactAreas() {
    return this.impactAreasItem.getAllActionAreas();
  }

  @ApiOperation(tags = {"Submission Tool Control Lists"},
    value = "${SubmissionToolsControlLists.impact-areas-indicators.all.value}",
    response = ImpactAreasIndicatorsDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/impact-areas-indicators", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ImpactAreasIndicatorsDTO> getAllImpactAreasIndicators() {
    return this.impactAreasIndicatorsItem.getAllImpactAreasIndicators();
  }

  @ApiOperation(tags = {"Submission Tool Control Lists"},
    value = "${SubmissionToolsControlLists.Initiatives.all.value}", response = InitiativesDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/allInitiatives", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<InitiativesDTO>> getAllInitiatives() {
    return this.initiativesItem.getInitiatives();
  }

  @ApiOperation(tags = {"Submission Tool Control Lists"}, value = "${SubmissionToolsControlLists.sdg.all.value}",
    response = SDGsDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/allSDG", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<SDGsDTO> getAllSDG() {
    return this.sdgItem.getAllSDGs();
  }

  @ApiOperation(tags = {"Submission Tool Control Lists"},
    value = "${SubmissionToolsControlLists.sdgIndicator.all.value}", response = SDGIndicatorDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/allSDGIndicators", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<SDGIndicatorDTO> getAllSDGIndicators() {
    return this.sdgItem.getAllSDGIndicators();
  }

  @ApiOperation(tags = {"Submission Tools Control Lists"}, value = "${SubmissionToolsControlLists.sdgTarget.all.value}",
    response = SDGTargetDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/allSDGTargets", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<SDGTargetDTO> getAllSDGTargets() {
    return this.sdgItem.getAllSDGTargets();
  }

  @ApiOperation(tags = {"Submission Tools Control Lists"}, value = "${SubmissionToolsControlLists.sdgTarget.all.value}",
    response = DepthDescriptionsDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/depthDescriptions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<DepthDescriptionsDTO> getDepthDescriptions() {
    return this.projectedBenefitsItem.getDepthDescriptions();
  }

  @ApiOperation(tags = {"Submission Tools Control Lists"}, value = "${SubmissionToolsControlLists.sdgTarget.all.value}",
    response = ProjectedBenefitsDepthScaleDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/depthScales", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ProjectedBenefitsDepthScaleDTO> getDepthScales() {
    return this.projectedBenefitsItem.getDepthScales();
  }

  @ApiOperation(tags = {"Submission Tool Control Lists"},
    value = "${SubmissionToolsControlLists.Initiatives.all.value}", response = InitiativesDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/onecgiar-workpackages", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<WorkPackagesDTO>> getOneCGIARWorkpackages() {
    return this.workpackagesItem.getOneCGIARWorkpackages();
  }

  @ApiOperation(tags = {"Submission Tools Control Lists"}, value = "${SubmissionToolsControlLists.sdgTarget.all.value}",
    response = ProjectedBenefitsDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/projectedBenefits", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ProjectedBenefitsDTO> getProjectedBenefits() {
    return this.projectedBenefitsItem.getProjectedBenefits();
  }

  @ApiOperation(tags = {"Submission Tools Control Lists"}, value = "${SubmissionToolsControlLists.sdgTarget.all.value}",
    response = ProjectedBenefitsProbabilitiesDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/projectedBenefitsProbabilities", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<ProjectedBenefitsProbabilitiesDTO> getprojectedBenefitsProbabilities() {
    return this.projectedBenefitsItem.getProjectedBenefitsProbabilities();
  }

  @ApiOperation(tags = {"Submission Tool Control Lists"},
    value = "${SubmissionToolsControlLists.Initiatives.all.value}", response = InitiativesDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/workpackages", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<WorkPackagesDTO>> getWorkpackages() {
    return this.workpackagesItem.getWorkPackages();
  }


}
