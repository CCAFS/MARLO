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
package org.cgiar.ccafs.marlo.data.manager.impl;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpLocElementTypeManager;
import org.cgiar.ccafs.marlo.data.manager.CrpUserManager;
import org.cgiar.ccafs.marlo.data.manager.CustomParameterManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitCreationManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitTypeManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ParameterManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.RoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserRoleManager;
import org.cgiar.ccafs.marlo.data.manager.UserManager;
import org.cgiar.ccafs.marlo.data.model.CrpLocElementType;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.CustomParameter;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitType;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Parameter;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Role;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.data.model.UserRole;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

@Named
public class GlobalUnitCreationManagerImpl implements GlobalUnitCreationManager {

  private static final long PREFERRED_SUPER_ADMIN_USER_ID = 1082L;
  private static final List<String> REQUIRED_SUPER_ADMIN_FALLBACK_PERMISSIONS =
    Arrays.asList("*", "superadmin:canEdit");

  private final GlobalUnitManager globalUnitManager;
  private final GlobalUnitTypeManager globalUnitTypeManager;
  private final InstitutionManager institutionManager;
  private final PhaseManager phaseManager;
  private final CrpUserManager crpUserManager;
  private final UserManager userManager;
  private final RoleManager roleManager;
  private final UserRoleManager userRoleManager;
  private final CrpLocElementTypeManager crpLocElementTypeManager;
  private final LiaisonInstitutionManager liaisonInstitutionManager;
  private final ParameterManager parameterManager;
  private final CustomParameterManager customParameterManager;

  @Inject
  public GlobalUnitCreationManagerImpl(GlobalUnitManager globalUnitManager, GlobalUnitTypeManager globalUnitTypeManager,
    InstitutionManager institutionManager, PhaseManager phaseManager, CrpUserManager crpUserManager,
    UserManager userManager, RoleManager roleManager, UserRoleManager userRoleManager,
    CrpLocElementTypeManager crpLocElementTypeManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ParameterManager parameterManager,
    CustomParameterManager customParameterManager) {
    this.globalUnitManager = globalUnitManager;
    this.globalUnitTypeManager = globalUnitTypeManager;
    this.institutionManager = institutionManager;
    this.phaseManager = phaseManager;
    this.crpUserManager = crpUserManager;
    this.userManager = userManager;
    this.roleManager = roleManager;
    this.userRoleManager = userRoleManager;
    this.crpLocElementTypeManager = crpLocElementTypeManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.parameterManager = parameterManager;
    this.customParameterManager = customParameterManager;
  }

  @Override
  public GlobalUnit createGlobalUnit(CreateRequest request) {
    this.validateRequest(request);

    GlobalUnitType globalUnitType = this.getGlobalUnitTypeOrFail(request.getGlobalUnitTypeId());
    GlobalUnit templateGlobalUnit = this.resolveTemplateGlobalUnit(request.getTemplateGlobalUnitId());
    this.validateEnvironmentOrFail(request, globalUnitType, templateGlobalUnit);

    Institution institution = this.resolveInstitution(request.getInstitutionId());
    GlobalUnit globalUnit = this.createBaseGlobalUnit(request, globalUnitType, institution);
    List<Phase> createdPhases = this.createPhases(globalUnit, request.getPhasesInput());

    User superAdminUser = this.resolveConfiguredSuperAdminUser(request.getSuperAdminUserId());
    if (templateGlobalUnit != null && templateGlobalUnit.getId() != null) {
      long templateGlobalUnitId = templateGlobalUnit.getId().longValue();
      this.cloneRoles(globalUnit, templateGlobalUnitId);
      roleManager.cloneRolePermissionsByAcronym(templateGlobalUnitId, globalUnit.getId());
      this.cloneLocTypes(globalUnit, templateGlobalUnitId);
    } else {
      roleManager.ensureSuperAdminRoleAndPermissions(globalUnit.getId(), 45L);
    }
    this.createSuperAdminAccess(globalUnit, superAdminUser);
    this.assignSuperAdminRole(globalUnit, superAdminUser);
    this.createLiaisonInstitution(globalUnit, request.getLiaisonName(), request.getLiaisonAcronym());
    this.saveCriticalParameters(globalUnit, createdPhases, request.getCurrentPhaseIndex(), request.getCustomFileName());

    return globalUnit;
  }

  private GlobalUnit createBaseGlobalUnit(CreateRequest request, GlobalUnitType globalUnitType, Institution institution) {
    GlobalUnit globalUnit = new GlobalUnit();
    globalUnit.setName(StringUtils.trim(request.getName()));
    globalUnit.setAcronym(StringUtils.upperCase(StringUtils.trim(request.getAcronym())));
    globalUnit.setGlobalUnitType(globalUnitType);
    globalUnit.setInstitution(institution);
    globalUnit.setMarlo(request.isMarlo());
    globalUnit.setLogin(request.isLogin());
    return globalUnitManager.saveGlobalUnit(globalUnit);
  }

  private void createLiaisonInstitution(GlobalUnit globalUnit, String liaisonName, String liaisonAcronym) {
    if (StringUtils.isBlank(liaisonName) && StringUtils.isBlank(liaisonAcronym)) {
      return;
    }

    LiaisonInstitution liaisonInstitution = new LiaisonInstitution();
    liaisonInstitution.setCrp(globalUnit);
    liaisonInstitution.setName(StringUtils.defaultIfBlank(StringUtils.trim(liaisonName), "PMU"));
    liaisonInstitution.setAcronym(StringUtils.defaultIfBlank(StringUtils.trim(liaisonAcronym), "PMU"));
    liaisonInstitutionManager.saveLiaisonInstitution(liaisonInstitution);
  }

  private void assignSuperAdminRole(GlobalUnit globalUnit, User superAdminUser) {
    if (globalUnit == null || globalUnit.getId() == null || superAdminUser == null || superAdminUser.getId() == null) {
      return;
    }

    List<Role> globalUnitRoles = roleManager.findAll();
    if (globalUnitRoles == null || globalUnitRoles.isEmpty()) {
      return;
    }

    Role superAdminRole = globalUnitRoles.stream()
      .filter(role -> role != null && role.getId() != null && role.getCrp() != null && role.getCrp().getId() != null
        && role.getCrp().getId().equals(globalUnit.getId()) && role.getAcronym() != null
        && "superadmin".equalsIgnoreCase(role.getAcronym()))
      .findFirst().orElse(null);

    if (superAdminRole == null || superAdminRole.getId() == null) {
      return;
    }

    List<UserRole> existingUserRoles = userRoleManager.getUserRolesByUserId(superAdminUser.getId());
    if (existingUserRoles != null && existingUserRoles.stream().anyMatch(userRole -> userRole != null
      && userRole.getRole() != null && userRole.getRole().getId() != null
      && userRole.getRole().getId().equals(superAdminRole.getId()))) {
      return;
    }

    UserRole userRole = new UserRole();
    userRole.setUser(superAdminUser);
    userRole.setRole(superAdminRole);
    userRoleManager.saveUserRole(userRole);
  }

  private void createSuperAdminAccess(GlobalUnit globalUnit, User superAdminUser) {
    if (globalUnit == null || globalUnit.getId() == null || superAdminUser == null || superAdminUser.getId() == null
      || crpUserManager.existCrpUser(superAdminUser.getId(), globalUnit.getId())) {
      return;
    }

    CrpUser crpUser = new CrpUser();
    crpUser.setCrp(globalUnit);
    crpUser.setUser(superAdminUser);
    crpUserManager.saveCrpUser(crpUser);
  }

  private User resolveConfiguredSuperAdminUser(long superAdminUserId) {
    User superAdminUser = null;

    superAdminUser = userManager.getUser(PREFERRED_SUPER_ADMIN_USER_ID);

    if (superAdminUser == null || superAdminUser.getId() == null) {
      superAdminUser = null;
    }

    if ((superAdminUser == null || superAdminUser.getId() == null) && superAdminUserId > 0L) {
      superAdminUser = userManager.getUser(superAdminUserId);
    }

    if (superAdminUser != null && superAdminUser.getId() != null) {
      return superAdminUser;
    }

    if (superAdminUser == null || superAdminUser.getId() == null) {
      superAdminUser = userManager.getActiveSuperAdminUserByUsernameOccurrence();
    }

    if (superAdminUser == null || superAdminUser.getId() == null) {
      return null;
    }

    return superAdminUser;
  }

  private List<Phase> createPhases(GlobalUnit globalUnit, List<PhaseInput> phasesInput) {
    List<Phase> createdPhases = new ArrayList<>();

    for (PhaseInput phaseInput : phasesInput) {
      Phase phase = new Phase();
      phase.setCrp(globalUnit);
      phase.setName(StringUtils.trim(phaseInput.getName()));
      phase.setDescription(StringUtils.defaultIfBlank(phaseInput.getDescription(), phaseInput.getName()));
      phase.setYear(phaseInput.getYear());
      phase.setStartDate(phaseInput.getStartDate());
      phase.setEndDate(phaseInput.getEndDate());
      phase.setUpkeep(phaseInput.isUpkeep() != null ? phaseInput.isUpkeep() : Boolean.FALSE);
      phase.setEditable(phaseInput.isEditable() != null ? phaseInput.isEditable() : Boolean.TRUE);
      phase.setVisible(phaseInput.isVisible() != null ? phaseInput.isVisible() : Boolean.TRUE);
      createdPhases.add(phaseManager.savePhase(phase));
    }

    for (int index = 0; index < createdPhases.size(); index++) {
      Phase currentPhase = createdPhases.get(index);
      Phase nextPhase = (index + 1) < createdPhases.size() ? createdPhases.get(index + 1) : null;
      currentPhase.setNext(nextPhase);
      createdPhases.set(index, phaseManager.savePhase(currentPhase));
    }

    return createdPhases;
  }

  private void cloneLocTypes(GlobalUnit globalUnit, long templateGlobalUnitId) {
    List<CrpLocElementType> templateLocTypes = crpLocElementTypeManager.findAll().stream()
      .filter(item -> item != null && item.getCrp() != null && item.getCrp().getId() != null
        && item.getCrp().getId().longValue() == templateGlobalUnitId && item.getLocElementType() != null)
      .collect(Collectors.toList());

    for (CrpLocElementType templateLocType : templateLocTypes) {
      CrpLocElementType crpLocElementType = new CrpLocElementType();
      crpLocElementType.setCrp(globalUnit);
      crpLocElementType.setLocElementType(templateLocType.getLocElementType());
      crpLocElementTypeManager.saveCrpLocElementType(crpLocElementType);
    }
  }

  private void cloneRoles(GlobalUnit globalUnit, long templateGlobalUnitId) {
    List<Role> templateRoles = roleManager.findAll().stream()
      .filter(role -> role != null && role.getCrp() != null && role.getCrp().getId() != null
        && role.getCrp().getId().longValue() == templateGlobalUnitId)
      .collect(Collectors.toList());

    for (Role templateRole : templateRoles) {
      Role role = new Role();
      role.setCrp(globalUnit);
      role.setAcronym(templateRole.getAcronym());
      role.setDescription(templateRole.getDescription());
      role.setOrder(templateRole.getOrder());
      roleManager.saveRole(role);
    }
  }

  private GlobalUnitType getGlobalUnitTypeOrFail(long globalUnitTypeId) {
    GlobalUnitType globalUnitType = globalUnitTypeManager.getGlobalUnitTypeById(globalUnitTypeId);
    if (globalUnitType == null || globalUnitType.getId() == null) {
      throw new IllegalArgumentException("Invalid global unit type");
    }
    return globalUnitType;
  }

  private GlobalUnit resolveTemplateGlobalUnit(long templateGlobalUnitId) {
    if (templateGlobalUnitId > 0L) {
      GlobalUnit templateGlobalUnit = globalUnitManager.getGlobalUnitById(templateGlobalUnitId);
      if (templateGlobalUnit != null && templateGlobalUnit.getId() != null) {
        return templateGlobalUnit;
      }
    }

    List<GlobalUnit> availableGlobalUnits = globalUnitManager.findAll();
    if (availableGlobalUnits == null) {
      return null;
    }

    return availableGlobalUnits.stream().filter(globalUnit -> globalUnit != null && globalUnit.getId() != null)
      .findFirst().orElse(null);
  }

  private Institution resolveInstitution(Long institutionId) {
    if (institutionId == null || institutionId.longValue() <= 0L) {
      return null;
    }
    return institutionManager.getInstitutionById(institutionId.longValue());
  }

  private void validateEnvironmentOrFail(CreateRequest request, GlobalUnitType globalUnitType,
    GlobalUnit templateGlobalUnit) {
    List<String> errors = new ArrayList<>();

    List<GlobalUnitType> availableGlobalUnitTypes = globalUnitTypeManager.findAll();
    if (availableGlobalUnitTypes == null || availableGlobalUnitTypes.isEmpty()) {
      errors.add("No Global Unit Types available to create new records");
    }

    if (!this.existsParameterForType(globalUnitType, APConstants.CURRENT_PHASE_PARAM)) {
      errors.add("Missing parameter key for current phase: " + APConstants.CURRENT_PHASE_PARAM);
    }

    if (StringUtils.isNotBlank(request.getCustomFileName())
      && !this.existsParameterForType(globalUnitType, APConstants.CRP_CUSTOM_FILE)) {
      errors.add("Missing parameter key for custom file: " + APConstants.CRP_CUSTOM_FILE);
    }

    if (templateGlobalUnit == null && !roleManager.existsPermissionsByNames(REQUIRED_SUPER_ADMIN_FALLBACK_PERMISSIONS)) {
      errors.add("Missing required fallback permissions: " + String.join(", ", REQUIRED_SUPER_ADMIN_FALLBACK_PERMISSIONS));
    }

    if (!errors.isEmpty()) {
      throw new IllegalStateException("Creation precheck failed: " + String.join("; ", errors));
    }
  }

  private boolean existsParameterForType(GlobalUnitType globalUnitType, String parameterKey) {
    if (globalUnitType == null || globalUnitType.getId() == null || StringUtils.isBlank(parameterKey)) {
      return false;
    }

    List<Parameter> parameters = parameterManager.findAll();
    if (parameters == null || parameters.isEmpty()) {
      return false;
    }

    return parameters.stream().anyMatch(parameter -> parameter != null && parameter.getKey() != null
      && parameter.getGlobalUnitType() != null && parameter.getGlobalUnitType().getId() != null
      && parameterKey.equals(parameter.getKey()) && globalUnitType.getId().equals(parameter.getGlobalUnitType().getId()));
  }

  private void saveCriticalParameters(GlobalUnit globalUnit, List<Phase> createdPhases, int currentPhaseIndex,
    String customFileName) {
    if (currentPhaseIndex >= 0 && currentPhaseIndex < createdPhases.size()) {
      Phase currentPhase = createdPhases.get(currentPhaseIndex);
      this.saveCustomParameterValue(globalUnit, APConstants.CURRENT_PHASE_PARAM, String.valueOf(currentPhase.getId()));
    }

    if (StringUtils.isNotBlank(customFileName)) {
      this.saveCustomParameterValue(globalUnit, APConstants.CRP_CUSTOM_FILE, StringUtils.trim(customFileName));
    }
  }

  private void validateRequest(CreateRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Create request is required");
    }
    if (StringUtils.isBlank(request.getName()) || StringUtils.isBlank(request.getAcronym())) {
      throw new IllegalArgumentException("Global Unit name and acronym are required");
    }
    if (request.getPhasesInput() == null || request.getPhasesInput().isEmpty()) {
      throw new IllegalArgumentException("At least one phase is required");
    }
    if (request.getCurrentPhaseIndex() < 0 || request.getCurrentPhaseIndex() >= request.getPhasesInput().size()) {
      throw new IllegalArgumentException("Current phase index is out of range");
    }
  }

  private void saveCustomParameterValue(GlobalUnit globalUnit, String parameterKey, String value) {
    if (globalUnit == null || globalUnit.getGlobalUnitType() == null || StringUtils.isBlank(parameterKey)) {
      return;
    }

    Parameter parameter = parameterManager.findAll().stream()
      .filter(p -> p != null && p.getKey() != null && p.getGlobalUnitType() != null && p.getGlobalUnitType().getId() != null
        && parameterKey.equals(p.getKey())
        && p.getGlobalUnitType().getId().equals(globalUnit.getGlobalUnitType().getId()))
      .findFirst().orElse(null);

    if (parameter == null || parameter.getId() == null) {
      return;
    }

    CustomParameter customParameter =
      customParameterManager.getCustomParameterByParameterKeyAndGlobalUnitId(parameter.getKey(), globalUnit.getId());
    if (customParameter == null || customParameter.getId() == null) {
      customParameter = new CustomParameter();
      customParameter.setCrp(globalUnit);
      customParameter.setParameter(parameter);
    }
    customParameter.setValue(value);
    customParameterManager.saveCustomParameter(customParameter);
  }
}
