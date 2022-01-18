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

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.FlagshipProgramItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.GeneralAcronymItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.GeographicScopeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.GlobalUnitItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.GlobalUnitTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.LocationItem;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityDTO;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.ContributionOfCrpDTO;
import org.cgiar.ccafs.marlo.rest.dto.CountryDTO;
import org.cgiar.ccafs.marlo.rest.dto.FlagshipProgramDTO;
import org.cgiar.ccafs.marlo.rest.dto.GeneralAcronymDTO;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewCGIAREntityDTO;
import org.cgiar.ccafs.marlo.rest.dto.RegionDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
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

  private final UserManager userManager;

  private LocationItem<GeneralLists> locationItem;
  private GeographicScopeItem<GeneralLists> geographicScopeItem;
  private GlobalUnitItem<GeneralLists> globalUnitItem;
  private GlobalUnitTypeItem<GeneralLists> globalUnitTypeItem;
  private FlagshipProgramItem<GeneralLists> flagshipProgramItem;
  private GeneralAcronymItem<GeneralLists> generalAcronymItem;

  @Autowired
  private Environment env;

  @Inject
  public GeneralLists(LocationItem<GeneralLists> countryItem, GeographicScopeItem<GeneralLists> geographicScopeItem,
    GlobalUnitItem<GeneralLists> globalUnitItem, GlobalUnitTypeItem<GeneralLists> globalUnitTypeItem,
    FlagshipProgramItem<GeneralLists> flagshipProgramItem, GeneralAcronymItem<GeneralLists> generalAcronymItem,
    UserManager userManager) {
    this.locationItem = countryItem;
    this.geographicScopeItem = geographicScopeItem;
    this.globalUnitItem = globalUnitItem;
    this.globalUnitTypeItem = globalUnitTypeItem;
    this.flagshipProgramItem = flagshipProgramItem;
    this.generalAcronymItem = generalAcronymItem;
    this.userManager = userManager;
  }

  @ApiOperation(tags = {"${GeneralLists.acronyms.acronym.value}"}, value = "${GeneralLists.cgiar-entities.POST.value}",
    response = CGIAREntityDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/cgiar-entities", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createEntity(
    @ApiParam(value = "${GeneralLists.cgiar-entities.POST.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${GeneralLists.cgiar-entities.POST.param.newEntity}",
      required = true) @Valid @RequestBody NewCGIAREntityDTO newCGIAREntityDTO) {

    Long globalUnitID = this.globalUnitItem.createGlobalUnit(newCGIAREntityDTO, CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(globalUnitID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("GeneralLists.cgiar-entities.GET.id.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"${GeneralLists.acronyms.acronym.value}"},
    value = "${GeneralLists.cgiar-entities.DELETE.value}", response = CGIAREntityDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/cgiar-entities/{acronym}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CGIAREntityDTO> deleteEntityByAcronym(
    @ApiParam(value = "${GeneralLists.cgiar-entities.DELETE.param.CGIAR.value}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${GeneralLists.cgiar-entities.DELETE.param.id}", required = true) @PathVariable String acronym) {

    ResponseEntity<CGIAREntityDTO> response =
      this.globalUnitItem.deleteGlobalUnitByAcronym(acronym, CGIAREntity, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("GeneralLists.cgiar-entities.code.404"));
    }

    return response;
  }

  /**
   * find Acronyms by acronym *
   * 
   * @return a List of GeneralAcronymDTO with General Acronym Items founded.
   */

  @ApiOperation(value = "${GeneralLists.acronyms.acronym.value}", response = GeneralAcronymDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/acronyms/{acronym}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<GeneralAcronymDTO>> findAcronymByAcronym(
    @ApiParam(value = "${GeneralLists.acronyms.acronym.param.acronym}", required = true) @PathVariable String acronym) {
    ResponseEntity<List<GeneralAcronymDTO>> response = this.generalAcronymItem.findGeneralAcronymByAcronym(acronym);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("GeneralLists.acronyms.acronym.404"));
    }
    return response;
  }

  /**
   * Find a country requesting numeric ISO Codeby id
   * 
   * @param numeric ISO Code
   * @return a LocElementDTO with the country founded.
   */

  @ApiOperation(
    tags = {"Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports",
      "Table 3 - Outcome/ Impact Case Reports", "Table 4 - CRP Innovations",
      "Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)"},
    value = "${GeneralLists.countries.code.value}", response = ContributionOfCrpDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/countries/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CountryDTO> findCountryByNumericISOCode(
    @ApiParam(value = "${GeneralLists.countries.code.param.code}", required = true) @PathVariable String code) {
    ResponseEntity<CountryDTO> response = this.locationItem.getContryByAlpha2ISOCode(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("GeneralLists.countries.code.404"));
    }
    return response;

  }


  /**
   * Find a Flagship or Program by smo code
   * 
   * @param smo flagship/program code
   * @return a FlagshipProgramDTO with Flagship or Program data.
   */
  @ApiOperation(
    tags = {"Table 3 - Outcome/ Impact Case Reports", "Table 5 - Status of Planned Outcomes and Milestones",
      "Table 8 - Key external partnerships", "Table 13 - CRP Financial Report"},
    value = "${GeneralLists.flagships-modules.code.value}", response = FlagshipProgramDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/flagships-modules/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<FlagshipProgramDTO> findFlagshipProgramBySmoCode(
    @ApiParam(value = "${GeneralLists.flagships-modules.code.param.code}", required = true) @PathVariable String code) {
    ResponseEntity<FlagshipProgramDTO> response = this.flagshipProgramItem.findFlagshipProgramBySmoCode(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("GeneralLists.flagships-modules.code.404"));
    }
    return response;
  }

  /**
   * Find a Geographic Scope by id
   * 
   * @param id
   * @return a GeographicScopeDTO with the geo scope founded.
   */

  @ApiOperation(
    tags = {"Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports", "Table 4 - CRP Innovations",
      "Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)"},
    value = "${GeneralLists.geographic-scopes.code.value}", response = ContributionOfCrpDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/geographic-scopes/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<GeographicScopeDTO> findGeographicScopeById(
    @ApiParam(value = "${GeneralLists.geographic-scopes.code.param.code}", required = true) @PathVariable Long code) {
    ResponseEntity<GeographicScopeDTO> response = this.geographicScopeItem.findGeographicScopesById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("GeneralLists.geographic-scopes.code.404"));
    }
    return response;
  }

  /**
   * Find a global unit requesting by smo id
   * 
   * @param smo ID
   * @return a GlobalUnit with the global unit founded.
   */
  @RequiresPermissions(Permission.CRPS_READ_REST_API_PERMISSION)
  @ApiOperation(
    tags = {"Table 1 - Progress towards SRF targets", "Table 2 - CRP Policies",
      "Table 3 - Outcome/ Impact Case Reports", "Table 4 - CRP Innovations",
      "Table 5 - Status of Planned Outcomes and Milestones", "Table 6 - Peer-reviewed publications",
      "Table 7 - Participants in CapDev Activities", "Table 8 - Key external partnerships",
      "Table 9 - Internal Cross-CGIAR Collaborations",
      "Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)",
      "Table 11 - Update on Actions Taken in Response to Relevant Evaluations", "Table 12 - Examples of W1/2 Use",
      "Table 13 - CRP Financial Report"},
    value = "${GeneralLists.cgiar-entities.GET.value}", response = CGIAREntityDTO.class)
  @RequestMapping(value = "/cgiar-entities/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CGIAREntityDTO>
    findGlobalUnitByCGIARId(@ApiParam(value = "${GeneralLists.cgiar-entities.GET.financialCode.value}",
      required = true) @PathVariable String code) {
    ResponseEntity<CGIAREntityDTO> response = this.globalUnitItem.findGlobalUnitByCGIRARId(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("GeneralLists.cgiar-entities.code.404"));
    }
    return response;
  }

  /**
   * Get a CGIAR Entity Type by code
   * 
   * @return a CGIAREntityTypeDTO founded by the code.
   */
  @ApiOperation(value = "${GeneralLists.cgiar-entity-types.code.value}", response = CGIAREntityTypeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/cgiar-entity-types/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CGIAREntityTypeDTO> findGlobalUnitTypeByCode(
    @ApiParam(value = "${GeneralLists.cgiar-entity-types.code.param.code}", required = true) @PathVariable Long code) {
    ResponseEntity<CGIAREntityTypeDTO> response = this.globalUnitTypeItem.findGlobalUnitTypeById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("GeneralLists.cgiar-entity-types.code.404"));
    }
    return response;
  }

  /**
   * Get a Region by code
   * 
   * @return a RegionDTO founded by the code.
   */
  @ApiOperation(
    tags = {"Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports", "Table 4 - CRP Innovations",
      "Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)"},
    value = "${GeneralLists.un-regions.code.value}", response = RegionDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/un-regions/{code}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<RegionDTO> findtRegionByCode(
    @ApiParam(value = "${GeneralLists.un-regions.code.param.code}", required = true) @PathVariable Long code) {
    ResponseEntity<RegionDTO> response = this.locationItem.getRegionByCode(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("GeneralLists.un-regions.code.404"));
    }
    return response;
  }

  /**
   * Get All the Acronym items *
   * 
   * @return a List of GeneralAcronymDTO with all General Acronym Items.
   */

  @ApiOperation(value = "${GeneralLists.acronyms.all.value}", response = GeneralAcronymDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/acronyms", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<GeneralAcronymDTO> getAllAcronyms() {
    return this.generalAcronymItem.getAllGeneralAcronyms();
  }


  /**
   * Get All the Country items *
   * 
   * @return a List of LocElementDTO with all LocElements Items.
   */
  @ApiOperation(
    tags = {"Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports", "Table 4 - CRP Innovations",
      "Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)"},
    value = "${GeneralLists.countries.all.value}", response = CountryDTO.class, responseContainer = "List")
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
  @ApiOperation(
    tags = {"Table 3 - Outcome/ Impact Case Reports", "Table 5 - Status of Planned Outcomes and Milestones",
      "Table 8 - Key external partnerships", "Table 13 - CRP Financial Report"},
    value = "${GeneralLists.flagships-modules.all.value}", response = FlagshipProgramDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/flagships-modules", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<FlagshipProgramDTO> getAllFlagshipsPrograms() {
    return this.flagshipProgramItem.getAllCrpPrograms();
  }


  /**
   * Get All the Geographic Scope items *
   * 
   * @return a List of GeographicScopeDTO with all RepIndGeographicScope Items.
   */

  @ApiOperation(
    tags = {"Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports", "Table 4 - CRP Innovations",
      "Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)"},
    value = "${GeneralLists.geographic-scopes.all.value}", response = GeographicScopeDTO.class,
    responseContainer = "List")
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
  @ApiOperation(
    tags = {"Table 1 - Progress towards SRF targets", "Table 2 - CRP Policies",
      "Table 3 - Outcome/ Impact Case Reports", "Table 4 - CRP Innovations",
      "Table 5 - Status of Planned Outcomes and Milestones", "Table 6 - Peer-reviewed publications",
      "Table 7 - Participants in CapDev Activities", "Table 8 - Key external partnerships",
      "Table 9 - Internal Cross-CGIAR Collaborations",
      "Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)",
      "Table 11 - Update on Actions Taken in Response to Relevant Evaluations", "Table 12 - Examples of W1/2 Use",
      "Table 13 - CRP Financial Report"},
    value = "${GeneralLists.cgiar-entities.all.value}", response = CGIAREntityDTO.class, responseContainer = "List")
  @RequestMapping(value = "/cgiar-entities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<CGIAREntityDTO>> getAllGlobalUnits(
    @ApiParam(value = "${GeneralLists.cgiar-entities.GET.typeId.value}") @RequestParam(value = "typeId",
      required = false) Long typeId) {
    ResponseEntity<List<CGIAREntityDTO>> response = this.globalUnitItem.getAllGlobaUnits(typeId);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("GeneralLists.cgiar-entities.all.404"));
    }
    return response;
  }

  /**
   * Get All CGIAR entities Types *
   * 
   * @return a List of CGIAREntityTypeDTO with all CGIAR entities Types.
   */
  @ApiOperation(value = "${GeneralLists.cgiar-entity-types.all.value}", response = CGIAREntityTypeDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/cgiar-entity-types", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<CGIAREntityTypeDTO> getAllGlobalUnitTypes() {
    return this.globalUnitTypeItem.getAllGlobalUnitTypes();
  }

  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @ApiOperation(value = "${GeneralLists.cgiar-entities.all.value}", response = CGIAREntityDTO.class,
    responseContainer = "List")
  @RequestMapping(value = "/onecgiar-entities", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<CGIAREntityDTO>> getAllOneCGIARGlobalUnits() {
    ResponseEntity<List<CGIAREntityDTO>> response = this.globalUnitItem.getAllCGIAREntities();
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("GeneralLists.cgiar-entities.all.404"));
    }
    return response;
  }

  /**
   * Get All the Region items *
   * 
   * @return a List of RegionDTO with all LocElements regions Items.
   */
  @ApiOperation(
    tags = {"Table 2 - CRP Policies", "Table 3 - Outcome/ Impact Case Reports", "Table 4 - CRP Innovations",
      "Table 10 - Monitoring, Evaluation, Learning and Impact Assessment (MELIA)"},
    value = "${GeneralLists.un-regions.all.value}", response = RegionDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/un-regions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<RegionDTO> getAllRegions() {
    return this.locationItem.getAllRegions();
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = this.userManager.getUser(principal);
    return user;
  }

  @ApiOperation(tags = {"${GeneralLists.acronyms.acronym.value}"}, value = "${GeneralLists.cgiar-entities.PUT.value}",
    response = CGIAREntityDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/cgiar-entities/{acronym}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> putEntityByAcronym(
    @ApiParam(value = "${GeneralLists.cgiar-entities.PUT.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${GeneralLists.cgiar-entities.PUT.id.value}", required = true) @PathVariable Long id,
    @ApiParam(value = "${GeneralLists.cgiar-entities.PUT.param.newEntity}",
      required = true) @Valid @RequestBody NewCGIAREntityDTO newAccountDTO) {

    Long globalUnitId = this.globalUnitItem.putGlobalUnitById(id, newAccountDTO, CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(globalUnitId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("GeneralLists.cgiar-entities.GET.id.404"));
    }

    return response;
  }

}
