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

import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.AccountTypesItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.AccountsItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.AdministrativeScaleItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.BusinessCategoryItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.EnvironmentalBenefitsItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.GovernanceTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.InnovationReadinessLevelItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.InnovationUseLevelItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.InvestmentTypeItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.RegionTypesItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.RegionsItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.ScienceGroupItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.TechnicalFieldItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.TechnologyDevelopmentStageItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.TypeOfInnovationItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.UnitsItem;
import org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.oneCGIAR.UsersItem;
import org.cgiar.ccafs.marlo.rest.dto.AccountTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.AccountsDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewAccountDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewOneCGIARRegionsDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewScienceGroupDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewUnitDTO;
import org.cgiar.ccafs.marlo.rest.dto.BeneficiariesDTO;
import org.cgiar.ccafs.marlo.rest.dto.BusinessCategoryDTO;
import org.cgiar.ccafs.marlo.rest.dto.EnvironmentalBenefitsDTO;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.dto.GovernanceTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.InnovationReadinessLevelDTO;
import org.cgiar.ccafs.marlo.rest.dto.InnovationTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.InnovationUseLevelDTO;
import org.cgiar.ccafs.marlo.rest.dto.InvestmentTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.OneCGIARRegionTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.OneCGIARRegionsDTO;
import org.cgiar.ccafs.marlo.rest.dto.OneCGIARUserDTO;
import org.cgiar.ccafs.marlo.rest.dto.ScienceGroupDTO;
import org.cgiar.ccafs.marlo.rest.dto.TechnicalFieldDTO;
import org.cgiar.ccafs.marlo.rest.dto.TechnologyDevelopmentStageDTO;
import org.cgiar.ccafs.marlo.rest.dto.UnitDTO;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@PropertySource("classpath:clarisa.properties")
@RestController
@Api(tags = "All CGIAR Control Lists")
@Named
public class OneCGIARControlList {

  private final UserManager userManager;

  private RegionsItem<OneCGIARControlList> regionsItem;
  private RegionTypesItem<OneCGIARControlList> regionTypesItem;
  private ScienceGroupItem<OneCGIARControlList> scienceGroupItem;
  private AccountsItem<OneCGIARControlList> accountsItem;
  private AccountTypesItem<OneCGIARControlList> accountTypesItem;
  private UnitsItem<OneCGIARControlList> unitsItem;
  private BusinessCategoryItem<OneCGIARControlList> businessCategoryItem;
  private TechnicalFieldItem<OneCGIARControlList> technicalFieldItem;
  private TypeOfInnovationItem<OneCGIARControlList> innovationTypeItem;
  private GovernanceTypeItem<OneCGIARControlList> governanceTypeItem;
  private EnvironmentalBenefitsItem<OneCGIARControlList> environmentalBenefitsItem;
  private TechnologyDevelopmentStageItem<OneCGIARControlList> technologyDeploymentStageItem;
  private InnovationReadinessLevelItem<OneCGIARControlList> innovationReadinessLevelItem;
  private AdministrativeScaleItem<OneCGIARControlList> administrativeScaleItem;
  private UsersItem<OneCGIARControlList> usersItem;
  private InvestmentTypeItem<OneCGIARControlList> investmentTypeItem;
  private InnovationUseLevelItem<OneCGIARControlList> innovationUseLevelItem;

  @Autowired
  private Environment env;

  @Inject
  public OneCGIARControlList(RegionsItem<OneCGIARControlList> regionsItem, UnitsItem<OneCGIARControlList> unitsItem,
    RegionTypesItem<OneCGIARControlList> regionTypesItem, ScienceGroupItem<OneCGIARControlList> scienceGroupItem,
    AccountTypesItem<OneCGIARControlList> accountTypesItem, AccountsItem<OneCGIARControlList> accountsItem,
    UserManager userManager, BusinessCategoryItem<OneCGIARControlList> businessCategoryItem,
    TechnicalFieldItem<OneCGIARControlList> technicalFieldItem,
    TypeOfInnovationItem<OneCGIARControlList> innovationTypeItem,
    GovernanceTypeItem<OneCGIARControlList> governanceTypeItem,
    EnvironmentalBenefitsItem<OneCGIARControlList> environmentalBenefitsItem,
    TechnologyDevelopmentStageItem<OneCGIARControlList> technologyDeploymentStageItem,
    InnovationReadinessLevelItem<OneCGIARControlList> innovationReadinessLevelItem,
    AdministrativeScaleItem<OneCGIARControlList> administrativeScaleItem, UsersItem<OneCGIARControlList> usersItem,
    InvestmentTypeItem<OneCGIARControlList> investmentTypeItem,
    InnovationUseLevelItem<OneCGIARControlList> innovationUseLevelItem) {
    super();
    this.regionsItem = regionsItem;
    this.regionTypesItem = regionTypesItem;
    this.scienceGroupItem = scienceGroupItem;
    this.accountTypesItem = accountTypesItem;
    this.accountsItem = accountsItem;
    this.unitsItem = unitsItem;
    this.userManager = userManager;
    this.businessCategoryItem = businessCategoryItem;
    this.technicalFieldItem = technicalFieldItem;
    this.innovationTypeItem = innovationTypeItem;
    this.governanceTypeItem = governanceTypeItem;
    this.environmentalBenefitsItem = environmentalBenefitsItem;
    this.technologyDeploymentStageItem = technologyDeploymentStageItem;
    this.innovationReadinessLevelItem = innovationReadinessLevelItem;
    this.administrativeScaleItem = administrativeScaleItem;
    this.usersItem = usersItem;
    this.investmentTypeItem = investmentTypeItem;
    this.innovationUseLevelItem = innovationUseLevelItem;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Accounts.POST.value}",
    response = AccountsDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/accounts/create", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createAccount(
    @ApiParam(value = "${CGIARControlList.Accounts.POST.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CGIARControlList.Accounts.POST.param.newAccount}",
      required = true) @Valid @RequestBody NewAccountDTO newAccountDTO) {

    Long accountID = this.accountsItem.createAccount(newAccountDTO, CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(accountID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Accounts.GET.id.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Regions.POST.value}",
    response = OneCGIARRegionsDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/regions/create", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createRegion(
    @ApiParam(value = "${CGIARControlList.Regions.POST.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CGIARControlList.Regions.POST.param.newRegion}",
      required = true) @Valid @RequestBody NewOneCGIARRegionsDTO newRegionDTO) {

    Long accountID = this.regionsItem.createRegion(newRegionDTO, CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(accountID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Regions.GET.id.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.ScienceGroups.POST.value}",
    response = AccountsDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/science-groups/create", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createScienceGroup(
    @ApiParam(value = "${CGIARControlList.ScienceGroups.POST.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CGIARControlList.ScienceGroups.POST.param.newScienceGroup}",
      required = true) @Valid @RequestBody NewScienceGroupDTO newScienceGroupDTO) {

    Long scienceGroupID =
      this.scienceGroupItem.createScienceGroup(newScienceGroupDTO, CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(scienceGroupID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.ScienceGroups.GET.id.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.POST.value}",
    response = AccountsDTO.class)
  @RequiresPermissions(Permission.FULL_CREATE_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/units/create", method = RequestMethod.POST,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> createUnit(
    @ApiParam(value = "${CGIARControlList.Units.POST.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CGIARControlList.Units.POST.param.newUnit}",
      required = true) @Valid @RequestBody NewUnitDTO newUnitDTO) {

    Long unitID = this.unitsItem.createUnit(newUnitDTO, CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(unitID, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.GET.id.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Accounts.DELETE.value}",
    response = AccountsDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/accounts/{financialCode}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AccountsDTO> deleteAccountsByFinancialCode(
    @ApiParam(value = "${CGIARControlList.Accounts.DELETE.param.CGIAR.value}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CGIARControlList.Accounts.DELETE.param.id}",
      required = true) @PathVariable String financialCode) {

    ResponseEntity<AccountsDTO> response =
      this.accountsItem.deleteAccountByFinanceCode(financialCode, CGIAREntity, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Accounts.code.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Regions.DELETE.value}",
    response = OneCGIARRegionsDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/regions/{acronym}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<OneCGIARRegionsDTO> deleteRegionsByAcronym(
    @ApiParam(value = "${CGIARControlList.Regions.DELETE.param.CGIAR.value}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CGIARControlList.Regions.DELETE.param.id}", required = true) @PathVariable String acronym) {

    ResponseEntity<OneCGIARRegionsDTO> response =
      this.regionsItem.deleteRegionByAcronym(acronym, CGIAREntity, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Regions.code.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.ScienceGroups.DELETE.value}",
    response = ScienceGroupDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/science-groups/{financialCode}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<ScienceGroupDTO> deleteScienceGroupsByFinancialCode(
    @ApiParam(value = "${CGIARControlList.ScienceGroups.DELETE.param.CGIAR.value}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CGIARControlList.ScienceGroups.DELETE.id.param.id}",
      required = true) @PathVariable String financialCode) {

    ResponseEntity<ScienceGroupDTO> response =
      this.scienceGroupItem.deleteScienceGroupByFinanceCode(financialCode, CGIAREntity, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.ScienceGroups.code.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.DELETE.value}",
    response = UnitDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/units/{financialCode}", method = RequestMethod.DELETE,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<UnitDTO> deleteUnitsByFinancialCode(
    @ApiParam(value = "${CGIARControlList.Units.DELETE.param.CGIAR.value}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CGIARControlList.Units.DELETE.id.param.id}",
      required = true) @PathVariable String financialCode) {

    ResponseEntity<UnitDTO> response =
      this.unitsItem.deleteUnitByFinanceCode(financialCode, CGIAREntity, this.getCurrentUser());
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.code.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Accounts.all.value}",
    response = AccountsDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/accounts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AccountsDTO>> findAllAccounts() {
    try {
      ResponseEntity<List<AccountsDTO>> response = this.accountsItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Accounts.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.AccountTypes.all.value}",
    response = AccountTypeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/accountTypes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AccountTypeDTO>> findAllAccountTypes() {
    try {
      ResponseEntity<List<AccountTypeDTO>> response = this.accountTypesItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.AccountsType.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.all.value}",
    response = GeographicScopeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/administrative-scales", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<GeographicScopeDTO>> findAllAdministrativeScale() {
    try {
      ResponseEntity<List<GeographicScopeDTO>> response = this.administrativeScaleItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.all.value}",
    response = UnitDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/beneficiaries", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<BeneficiariesDTO>> findAllBeneficiaries() {
    try {
      ResponseEntity<List<BeneficiariesDTO>> response = this.usersItem.getAllBeneficiaries();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.AccountTypes.all.value}",
    response = BusinessCategoryDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/business-categories", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<BusinessCategoryDTO>> findAllBussinesCategories() {
    try {
      ResponseEntity<List<BusinessCategoryDTO>> response = this.businessCategoryItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.BussinessCategory.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Regions.all.value}",
    response = OneCGIARRegionsDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/OneCGIARRegions", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<OneCGIARRegionsDTO>> findAllCGIARRegions() {
    try {
      ResponseEntity<List<OneCGIARRegionsDTO>> response = this.regionsItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Regions.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.RegionTypes.all.value}",
    response = OneCGIARRegionTypeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/allCGIARRegionsTypes", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<OneCGIARRegionTypeDTO>> findAllCGIARRegionTypes() {

    ResponseEntity<List<OneCGIARRegionTypeDTO>> response = this.regionTypesItem.findAll();
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.RegionTypes.code.404"));
    }
    return response;
  }
  
  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.all.value}",
    response = UnitDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<OneCGIARUserDTO>> findAllCGIARUsers() {
    try {
      ResponseEntity<List<OneCGIARUserDTO>> response = this.usersItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.all.value}",
    response = EnvironmentalBenefitsDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/environmental-benefits", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<EnvironmentalBenefitsDTO>> findAllEnvironmentalBenefits() {
    try {
      ResponseEntity<List<EnvironmentalBenefitsDTO>> response = this.environmentalBenefitsItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.all.value}",
    response = UnitDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/governance-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<GovernanceTypeDTO>> findAllGovernanceTypes() {
    try {
      ResponseEntity<List<GovernanceTypeDTO>> response = this.governanceTypeItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.all.value}",
    response = InnovationReadinessLevelDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/innovation-readiness-levels", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<InnovationReadinessLevelDTO>> findAllInnovationReadinessLevels() {
    try {
      ResponseEntity<List<InnovationReadinessLevelDTO>> response = this.innovationReadinessLevelItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.all.value}",
    response = InnovationTypeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/type-of-innovations", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<InnovationTypeDTO>> findAllInnovationTypes() {
    try {
      ResponseEntity<List<InnovationTypeDTO>> response = this.innovationTypeItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.all.value}",
    response = InnovationUseLevelDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/innovation-use-levels", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<InnovationUseLevelDTO>> findAllInnovationUseLevels() {
    try {
      ResponseEntity<List<InnovationUseLevelDTO>> response = this.innovationUseLevelItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.all.value}",
    response = InvestmentTypeDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/investment-types", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<InvestmentTypeDTO>> findAllInvestmentTypes() {
    try {
      ResponseEntity<List<InvestmentTypeDTO>> response = this.investmentTypeItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.ScienceGroups.all.value}",
    response = ScienceGroupDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/scienceGroups", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<ScienceGroupDTO>> findAllScienceGroup() {

    ResponseEntity<List<ScienceGroupDTO>> response = this.scienceGroupItem.getAll();
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.ScienceGroups.code.404"));
    }
    return response;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.AccountTypes.all.value}",
    response = TechnicalFieldDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/technical-fields", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<TechnicalFieldDTO>> findAllTechnicalFields() {
    try {
      ResponseEntity<List<TechnicalFieldDTO>> response = this.technicalFieldItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.TechnicalField.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.all.value}",
    response = TechnologyDevelopmentStageDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/technology-development-stages", method = RequestMethod.GET,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<TechnologyDevelopmentStageDTO>> findAllTechnologyDevelopmentStage() {
    try {
      ResponseEntity<List<TechnologyDevelopmentStageDTO>> response = this.technologyDeploymentStageItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.all.value}",
    response = UnitDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/units", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<UnitDTO>> findAllUnits() {
    try {
      ResponseEntity<List<UnitDTO>> response = this.unitsItem.getAll();
      if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
        throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.code.404"));
      }
      return response;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private User getCurrentUser() {
    Subject subject = SecurityUtils.getSubject();
    Long principal = (Long) subject.getPrincipal();
    User user = this.userManager.getUser(principal);
    return user;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Accounts.PUT.value}",
    response = AccountsDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/accounts/edit/{financialCode}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> putAccountByFinanceCode(
    @ApiParam(value = "${CGIARControlList.Accounts.PUT.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CGIARControlList.Accounts.PUT.financialCode.value}",
      required = true) @PathVariable String financialCode,
    @ApiParam(value = "${CGIARControlList.Accounts.PUT.param.newAccount}",
      required = true) @Valid @RequestBody NewAccountDTO newAccountDTO) {

    Long accountId =
      this.accountsItem.putAccountByFinanceCode(financialCode, newAccountDTO, CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(accountId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Accounts.GET.id.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Regions.PUT.value}",
    response = OneCGIARRegionsDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/regions/edit/{financialCode}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> putRegionByAcronym(
    @ApiParam(value = "${CGIARControlList.Regions.PUT.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CGIARControlList.Regions.PUT.acronym.value}",
      required = true) @PathVariable String financialCode,
    @ApiParam(value = "${CGIARControlList.Regions.PUT.param.newRegion}",
      required = true) @Valid @RequestBody NewOneCGIARRegionsDTO newRegionDTO) {

    Long regionId =
      this.regionsItem.putRegionByAcronym(financialCode, newRegionDTO, CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(regionId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Regions.GET.id.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.ScienceGroups.PUT.value}",
    response = ScienceGroupDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/science-groups/edit/{financialCode}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> putScienceGroupByFinanceCode(
    @ApiParam(value = "${CGIARControlList.ScienceGroups.PUT.param.CGIAR}",
      required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CGIARControlList.ScienceGroups.PUT.financialCode.value}",
      required = true) @PathVariable String financialCode,
    @ApiParam(value = "${CGIARControlList.ScienceGroups.PUT.param.newScienceGroup}",
      required = true) @Valid @RequestBody NewScienceGroupDTO newScienceGroupDTO) {

    Long scienceGroupId = this.scienceGroupItem.putScienceGroupByFinanceCode(financialCode, newScienceGroupDTO,
      CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(scienceGroupId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.ScienceGroups.GET.id.404"));
    }

    return response;
  }

  @ApiOperation(tags = {"All CGIAR Control Lists"}, value = "${CGIARControlList.Units.PUT.value}",
    response = UnitDTO.class)
  @RequiresPermissions(Permission.FULL_READ_REST_API_PERMISSION)
  @RequestMapping(value = "/{CGIAREntity}/units/edit/{financialCode}", method = RequestMethod.PUT,
    produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Long> putUnitByFinanceCode(
    @ApiParam(value = "${CGIARControlList.Units.PUT.param.CGIAR}", required = true) @PathVariable String CGIAREntity,
    @ApiParam(value = "${CGIARControlList.Units.PUT.financialCode.value}",
      required = true) @PathVariable String financialCode,
    @ApiParam(value = "${CGIARControlList.Units.PUT.param.newUnit}",
      required = true) @Valid @RequestBody NewUnitDTO newUnitDTO) {

    Long unitId = this.unitsItem.putUnitByFinanceCode(financialCode, newUnitDTO, CGIAREntity, this.getCurrentUser());

    ResponseEntity<Long> response = new ResponseEntity<Long>(unitId, HttpStatus.OK);
    if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
      throw new NotFoundException("404", this.env.getProperty("CGIARControlList.Units.GET.id.404"));
    }

    return response;
  }
}
