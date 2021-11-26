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
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.generallists.GlobalUnitItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.institutions.CountryOfficeRequestItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.institutions.InstitutionItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.institutions.InstitutionRelatedItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.institutions.InstitutionTypeItem;
import org.cgiar.ccafs.marlo.rest.dto.CountryOfficeRequestDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionRequestDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionSimpleDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.InstitutionsRelatedDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewCountryOfficeRequestDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewInstitutionDTO;
import org.cgiar.ccafs.marlo.rest.errors.NotFoundException;
import org.cgiar.ccafs.marlo.security.Permission;

import java.util.List;

import javax.inject.Inject;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@Api(tags = "Institutions Lists")
public class Institutions {

  private static final Logger LOG = LoggerFactory.getLogger(Institutions.class);
  @Autowired
  private Environment env;

  private InstitutionTypeItem<InstitutionTypeDTO> institutionTypeItem;
  private InstitutionItem<InstitutionDTO> institutionItem;
  private InstitutionRelatedItem<InstitutionsRelatedDTO> institutionRelatedItem;
  private CountryOfficeRequestItem<CountryOfficeRequestDTO> countryOfficeRequestItem;
  private final UserManager userManager;

  @Inject
  public Institutions(InstitutionTypeItem<InstitutionTypeDTO> institutionTypeItem,
    InstitutionItem<InstitutionDTO> institutionItem,
    CountryOfficeRequestItem<CountryOfficeRequestDTO> countryOfficeRequestItem,
    GlobalUnitItem<InstitutionDTO> globalUnitItem,
    InstitutionRelatedItem<InstitutionsRelatedDTO> institutionRelatedItem, UserManager userManager) {
    this.institutionTypeItem = institutionTypeItem;
    this.institutionItem = institutionItem;
    this.userManager = userManager;
    this.countryOfficeRequestItem = countryOfficeRequestItem;
    this.institutionRelatedItem = institutionRelatedItem;
  }


  @ApiOperation(value = "${Institutions.institution-requests.accept.value}", response = InstitutionRequestDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/institutions/accept-institution-request/{code}", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InstitutionRequestDTO> acceptPartnerRequest(
    @ApiParam(value = "${Institutions.institution-requests.create.param.CGIAR}",
      required = true) @PathVariable("CGIAREntity") String CGIAREntity,
    @ApiParam(value = "${Institutions.institution-requests.code.param.requestId}",
      required = true) @PathVariable(name = "code") Long code,
    @ApiParam(value = "${Institutions.institution.code.param.accept}", required = true) @RequestParam boolean accept,
    @ApiParam(value = "${Institutions.institution.code.param.justification}",
      required = false) @RequestParam String justification)
    throws Exception {
    ResponseEntity<InstitutionRequestDTO> response =
      this.institutionItem.acceptPartnerRequest(code, accept, justification, CGIAREntity, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Institutions.institution-requests.code.404"));
    }
    return response;
  }

  /**
   * Create a new Country Office Request *
   * 
   * @param acronym of global unit
   * @param NewCountryOfficeRequestDTO of country office to be created
   * @return a Country Office request created
   */
  @ApiOperation(value = "${Institutions.country-office-requests.create.value}",
    response = CountryOfficeRequestDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/institutions/country-office-requests", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CountryOfficeRequestDTO> createCountryOfficeRequest(
    @ApiParam(value = "${Institutions.country-office-requests.create.param.CGIAR}",
      required = true) @PathVariable("CGIAREntity") String CGIAREntity,
    @ApiParam(value = "${Institutions.country-office-requests.create.param.country}",
      required = true) @Valid @RequestBody NewCountryOfficeRequestDTO newCountryOfficeRequestDTO) {
    return this.countryOfficeRequestItem.createCountryOfficeRequest(newCountryOfficeRequestDTO, CGIAREntity,
      this.getCurrentUser());
  }

  /**
   * Create a new institution request *
   * 
   * @param acronym of global unit
   * @param NewInstitutionDTO of institution to be created
   * @return a institution request created
   * @throws Exception
   */
  @ApiOperation(value = "${Institutions.institution-requests.create.value}", response = InstitutionRequestDTO.class)
  @RequiresPermissions({Permission.FULL_READ_REST_API_PERMISSION})
  @RequestMapping(value = "/{CGIAREntity}/institutions/institution-requests", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InstitutionRequestDTO> createPartnerRequest(
    @ApiParam(value = "${Institutions.institution-requests.create.param.CGIAR}",
      required = true) @PathVariable("CGIAREntity") String CGIAREntity,
    @ApiParam(value = "${Institutions.institution-requests.create.param.institution}",
      required = true) @Valid @RequestBody NewInstitutionDTO newInstitutionDTO)
    throws Exception {
    return this.institutionItem.createPartnerRequest(newInstitutionDTO, CGIAREntity, this.getCurrentUser());
  }

  /**
   * Find a country office request by ID *
   * 
   * @param acronym of global unit
   * @param id of country office request
   * @return a CountryOfficeRequestDTO with country office request data item
   */
  @ApiOperation(value = "${Institutions.country-office-requests.code.value}", response = CountryOfficeRequestDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/institutions/country-office-requests/{requestId}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<CountryOfficeRequestDTO> findCountryOfficeRequestById(
    @ApiParam(value = "${Institutions.country-office-requests.code.param.CGIAR}",
      required = true) @PathVariable(name = "CGIAREntity") String CGIAREntity,
    @ApiParam(value = "${Institutions.country-office-requests.code.param.requestId}",
      required = true) @PathVariable(name = "requestId") Long requestId) {
    ResponseEntity<CountryOfficeRequestDTO> response =
      this.countryOfficeRequestItem.getCountryOfficeRequest(requestId, CGIAREntity);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Institutions.country-office-requests.code.404"));
    }
    return response;

  }

  /**
   * Find a institution by ID *
   * 
   * @param intitution id
   * @return a InstitutionDTO with institution data item
   */
  // @Throttling(type = ThrottlingType.HeaderValue, limit = 1, timeUnit = TimeUnit.SECONDS)
  @ApiOperation(tags = {"Table 4 - CRP Innovations", "Table 3 - Outcome/ Impact Case Reports"},
    value = "${Institutions.institutions.code.value}", response = InstitutionDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/institutions/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InstitutionDTO> findInstitutionById(
    @ApiParam(value = "${Institutions.institution.code.param.code}", required = true) @PathVariable Long code) {
    ResponseEntity<InstitutionDTO> response = this.institutionItem.findInstitutionById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Institutions.institutions.code.404"));
    }
    return response;
  }

  /**
   * Find a institution type by ID *
   * 
   * @param institution type id
   * @return a InstitutionTypeDTO with institution type data item
   */
  @ApiOperation(value = "${Institutions.institution-types.code.value}", response = InstitutionTypeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/institution-types/{code}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InstitutionTypeDTO> findInstitutionTypeById(
    @ApiParam(value = "${Institutions.institution-types.code.param.code}", required = true) @PathVariable Long code) {
    ResponseEntity<InstitutionTypeDTO> response = this.institutionTypeItem.findInstitutionTypeById(code);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Institutions.institution-types.code.404"));
    }
    return response;
  }

  /**
   * Find a institution request by CRP *
   * 
   * @param acronym of global unit
   * @return a InstitutionRequestDTO list with institution request data item
   */
  @ApiOperation(value = "${Institutions.institution-all-requests.code.value}", response = InstitutionRequestDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/institutions/institution-all-requests", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<InstitutionRequestDTO>
    findPartnerRequestByGlobalUnit(@ApiParam(value = "${Institutions.institution-requests.code.param.CGIAR}",
      required = true) @PathVariable(name = "CGIAREntity") String CGIAREntity) {
    List<InstitutionRequestDTO> partnersList =
      this.institutionItem.getParterRequestByGlobalUnit(CGIAREntity, this.getCurrentUser());
    return partnersList;
  }

  /**
   * Find a institution request by ID *
   * 
   * @param acronym of global unit
   * @param id of institution request
   * @return a InstitutionRequestDTO with institution request data item
   */
  @ApiOperation(value = "${Institutions.institution-requests.code.value}", response = InstitutionRequestDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/institutions/institution-requests/{requestId}", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<InstitutionRequestDTO> findPartnerRequestById(
    @ApiParam(value = "${Institutions.institution-requests.code.param.CGIAR}",
      required = true) @PathVariable(name = "CGIAREntity") String CGIAREntity,
    @ApiParam(value = "${Institutions.institution-requests.code.param.requestId}",
      required = true) @PathVariable(name = "requestId") Long requestId) {
    ResponseEntity<InstitutionRequestDTO> response =
      this.institutionItem.getPartnerRequest(requestId, CGIAREntity, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("Institutions.institution-requests.code.404"));
    }
    return response;

  }

  /**
   * Get all institutions *
   * 
   * @return a InstitutionDTO with institution item
   */
  @ApiOperation(tags = {"Table 4 - CRP Innovations", "Table 3 - Outcome/ Impact Case Reports"},
    value = "${Institutions.institutions.all.value}", response = InstitutionDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/institutions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<InstitutionDTO>> getAllInstitutions() {
    ResponseEntity<List<InstitutionDTO>> resp = this.institutionItem.getAllInstitutions();
    return resp;
  }

  @ApiOperation(value = "${Institutions.institution-types.all.value}", response = InstitutionsRelatedDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/institutionRelated", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public List<InstitutionsRelatedDTO> getAllInstitutionsRelated() {
    return institutionRelatedItem.getAllInstitutionRelated();
  }

  @ApiOperation(tags = {"Table 4 - CRP Innovations", "Table 3 - Outcome/ Impact Case Reports"},
    value = "${Institutions.institutions.all.value}", response = InstitutionSimpleDTO.class, responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/institutionsSimple", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<InstitutionSimpleDTO>> getAllInstitutionsSimple() {
    ResponseEntity<List<InstitutionSimpleDTO>> resp = this.institutionItem.getAllInstitutionsSimple();
    return resp;
  }

  /**
   * Get all institution types *
   * 
   * @return a List of InstitutionTypeDTO with institution type items
   */
  @ApiOperation(value = "${Institutions.institution-types.all.value}", response = InstitutionTypeDTO.class,
    responseContainer = "List")
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/institution-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<InstitutionTypeDTO> getAllInstitutionsTypes() {
    return this.institutionTypeItem.getAllInstitutionTypes();
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = this.userManager.getUser(principal);
    return user;
  }

}
