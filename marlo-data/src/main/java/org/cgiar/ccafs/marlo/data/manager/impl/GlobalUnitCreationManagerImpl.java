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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Named
public class GlobalUnitCreationManagerImpl implements GlobalUnitCreationManager {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalUnitCreationManagerImpl.class);
  private static final String CRP_ADMIN_ROLE_ACRONYM = "CRP-Admin";
  private static final String SUPER_ADMIN_ROLE_ACRONYM = "superadmin";

  /** Parameter format used for boolean-like specificities (true/false). */
  private static final int PARAMETER_FORMAT_BOOLEAN = 1;
  /** Parameter category used for specificities. */
  private static final int PARAMETER_CATEGORY_SPECIFICITY = 2;

  /**
   * Keys that must never be copied literally from the template: they are computed for the new GU,
   * forced to a safe startup value, or remapped through cloned roles.
   */
  private static final Set<String> NEVER_INHERIT_PARAMETER_KEYS =
    Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
      APConstants.CURRENT_PHASE_PARAM,
      APConstants.CRP_CUSTOM_FILE,
      APConstants.CRP_CLOSED,
      APConstants.CRP_REFRESH,
      APConstants.CRP_ADMIN_ROLE,
      APConstants.CRP_CL_ROLE,
      APConstants.CRP_FPL_ROLE,
      APConstants.CRP_FPM_ROLE,
      APConstants.CRP_PC_ROLE,
      APConstants.CRP_CP_ROLE,
      APConstants.CRP_PL_ROLE,
      APConstants.CRP_PMU_ROLE,
      APConstants.CRP_RPL_ROLE,
      APConstants.CRP_RPM_ROLE,
      APConstants.CRP_SL_ROLE,
      APConstants.CRP_CD_ROLE,
      APConstants.CRP_CU,
      APConstants.CRP_AICCRA_AF_START_PHASE,
      APConstants.CRP_TIMELINE_WEEK_PARAMETER_VISUALIZATION)));

  private static final Set<String> ROLE_PARAMETER_KEYS =
    Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
      APConstants.CRP_ADMIN_ROLE,
      APConstants.CRP_CL_ROLE,
      APConstants.CRP_FPL_ROLE,
      APConstants.CRP_FPM_ROLE,
      APConstants.CRP_PC_ROLE,
      APConstants.CRP_CP_ROLE,
      APConstants.CRP_PL_ROLE,
      APConstants.CRP_PMU_ROLE,
      APConstants.CRP_RPL_ROLE,
      APConstants.CRP_RPM_ROLE,
      APConstants.CRP_SL_ROLE,
      APConstants.CRP_CD_ROLE)));

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
  private final SessionFactory sessionFactory;

  @Inject
  public GlobalUnitCreationManagerImpl(GlobalUnitManager globalUnitManager, GlobalUnitTypeManager globalUnitTypeManager,
    InstitutionManager institutionManager, PhaseManager phaseManager, CrpUserManager crpUserManager,
    UserManager userManager, RoleManager roleManager, UserRoleManager userRoleManager,
    CrpLocElementTypeManager crpLocElementTypeManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ParameterManager parameterManager,
    CustomParameterManager customParameterManager, SessionFactory sessionFactory) {
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
    this.sessionFactory = sessionFactory;
  }

  @Override
  @Transactional
  public GlobalUnit createGlobalUnit(CreateRequest request) {
    this.validateRequest(request);

    GlobalUnitType globalUnitType = this.getGlobalUnitTypeOrFail(request.getGlobalUnitTypeId());
    GlobalUnit templateGlobalUnit = this.resolveTemplateGlobalUnit(request.getTemplateGlobalUnitId());
    this.validateEnvironmentOrFail(request, globalUnitType, templateGlobalUnit);

    Institution institution = this.resolveInstitution(request.getInstitutionId());
    GlobalUnit globalUnit = this.createBaseGlobalUnit(request, globalUnitType, institution);
    List<Phase> createdPhases = this.createPhases(globalUnit, request.getPhasesInput());

    User superAdminUser = this.resolveConfiguredSuperAdminUser(request.getSuperAdminUserId());
    if (superAdminUser == null || superAdminUser.getId() == null) {
      LOG.warn("No super admin user could be resolved while creating Global Unit '{}'. "
        + "The new Global Unit will be created without super admin access assignment.", globalUnit.getAcronym());
    }
    long templateGlobalUnitId = templateGlobalUnit.getId().longValue();
    this.cloneRoles(globalUnit, templateGlobalUnitId);
    // Flush pending session inserts (global unit + cloned roles) so the following native SQL, which joins on the
    // freshly created target roles, sees them and does not violate FKs or clone zero permissions.
    this.sessionFactory.getCurrentSession().flush();
    roleManager.cloneRolePermissionsByAcronym(templateGlobalUnitId, globalUnit.getId());
    this.cloneLocTypes(globalUnit, templateGlobalUnitId);
    this.ensureCrpUserAccess(globalUnit, superAdminUser);
    this.assignSuperAdminRole(globalUnit, superAdminUser);
    this.assignCrpAdminUsers(globalUnit, request.getCrpAdminUserIds());
    LiaisonInstitution pmuLiaisonInstitution =
      this.createLiaisonInstitution(globalUnit, request.getLiaisonName(), request.getLiaisonAcronym());
    this.initializeCustomParameters(globalUnit, templateGlobalUnit, createdPhases, request.getCurrentPhaseIndex(),
      request.getCustomFileName(), pmuLiaisonInstitution);

    return globalUnit;
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserRole> getCrpAdminTeam(long globalUnitId) {
    GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(globalUnitId);
    if (globalUnit == null || globalUnit.getId() == null) {
      return Collections.emptyList();
    }

    Role crpAdminRole = this.resolveCrpAdminRole(globalUnit);
    if (crpAdminRole == null || crpAdminRole.getId() == null) {
      LOG.warn("The CRP-Admin role could not be resolved while loading Global Unit {}", globalUnitId);
      return Collections.emptyList();
    }
    List<UserRole> assignments = userRoleManager.getUserRolesByRoleId(crpAdminRole.getId());
    return assignments == null ? Collections.emptyList() : assignments;
  }

  @Override
  @Transactional
  public void syncCrpAdminTeam(long globalUnitId, List<Long> submittedUserIds) {
    GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(globalUnitId);
    if (globalUnit == null || globalUnit.getId() == null) {
      throw new IllegalArgumentException("Invalid Global Unit id: " + globalUnitId);
    }

    Role crpAdminRole = this.resolveCrpAdminRole(globalUnit);
    if (crpAdminRole == null || crpAdminRole.getId() == null) {
      throw new IllegalStateException("The CRP-Admin role could not be resolved for Global Unit "
        + globalUnit.getAcronym());
    }

    Set<Long> targetUserIds = submittedUserIds == null ? new HashSet<>()
      : submittedUserIds.stream().filter(Objects::nonNull).filter(userId -> userId.longValue() > 0L)
        .collect(Collectors.toCollection(HashSet::new));
    if (targetUserIds.isEmpty()) {
      throw new IllegalArgumentException("At least one CRP Admin is required for Global Unit "
        + globalUnit.getAcronym());
    }

    List<UserRole> currentAssignments = userRoleManager.getUserRolesByRoleId(crpAdminRole.getId());
    if (currentAssignments == null) {
      currentAssignments = new ArrayList<>();
    }

    Set<Long> currentUserIds = new HashSet<>();
    for (UserRole assignment : currentAssignments) {
      if (assignment != null && assignment.getUser() != null && assignment.getUser().getId() != null) {
        currentUserIds.add(assignment.getUser().getId());
      }
    }

    // Remove assignments that are no longer submitted.
    for (UserRole assignment : currentAssignments) {
      if (assignment == null || assignment.getId() == null || assignment.getUser() == null
        || assignment.getUser().getId() == null) {
        continue;
      }
      if (!targetUserIds.contains(assignment.getUser().getId())) {
        User removedUser = assignment.getUser();
        userRoleManager.deleteUserRole(assignment.getId());
        this.removeCrpUserAccessIfNoRolesRemain(globalUnit, removedUser);
      }
    }

    // Add newly submitted users.
    for (Long userId : targetUserIds) {
      User user = userManager.getUser(userId.longValue());
      if (user == null || user.getId() == null) {
        throw new IllegalArgumentException("Invalid CRP Admin user id: " + userId);
      }
      this.ensureCrpUserAccess(globalUnit, user);
      if (currentUserIds.contains(userId)) {
        continue;
      }
      UserRole userRole = new UserRole();
      userRole.setUser(user);
      userRole.setRole(crpAdminRole);
      userRoleManager.saveUserRole(userRole);
    }
  }

  @Override
  @Transactional
  public void softDeleteGlobalUnit(long globalUnitId) {
    GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(globalUnitId);
    if (globalUnit == null || globalUnit.getId() == null) {
      throw new IllegalArgumentException("Invalid Global Unit id: " + globalUnitId);
    }

    this.removeAllCrpAdminAssignments(globalUnit);
    globalUnitManager.deleteGlobalUnit(globalUnitId);
  }

  /**
   * Removes every CRP-Admin assignment of the given Global Unit. When the CRP-Admin role cannot be resolved the
   * soft-delete still proceeds (legacy / incomplete seed data).
   */
  private void removeAllCrpAdminAssignments(GlobalUnit globalUnit) {
    Role crpAdminRole = this.resolveCrpAdminRole(globalUnit);
    if (crpAdminRole == null || crpAdminRole.getId() == null) {
      LOG.warn("Skipping CRP-Admin cleanup for Global Unit {}: role could not be resolved", globalUnit.getId());
      return;
    }

    List<UserRole> assignments = userRoleManager.getUserRolesByRoleId(crpAdminRole.getId());
    if (assignments == null || assignments.isEmpty()) {
      return;
    }

    for (UserRole assignment : assignments) {
      if (assignment == null || assignment.getId() == null) {
        continue;
      }
      User removedUser = assignment.getUser();
      userRoleManager.deleteUserRole(assignment.getId());
      this.removeCrpUserAccessIfNoRolesRemain(globalUnit, removedUser);
    }
  }

  /**
   * Deactivates the {@code crp_users} access of a user for a Global Unit only when:
   * <ul>
   * <li>the user keeps no other role in that Global Unit, and</li>
   * <li>the user is not SuperAdmin in any Global Unit.</li>
   * </ul>
   * Uses a targeted lookup instead of scanning the whole {@code crp_users} table.
   */
  private void removeCrpUserAccessIfNoRolesRemain(GlobalUnit globalUnit, User user) {
    if (globalUnit == null || globalUnit.getId() == null || user == null || user.getId() == null) {
      return;
    }

    List<UserRole> remainingRoles = userRoleManager.getUserRolesByUserId(user.getId());
    if (remainingRoles != null) {
      boolean keepsRoleInGlobalUnit = remainingRoles.stream().anyMatch(userRole ->
        userRole != null && userRole.getRole() != null && userRole.getRole().getCrp() != null
          && userRole.getRole().getCrp().getId() != null
          && globalUnit.getId().equals(userRole.getRole().getCrp().getId()));
      if (keepsRoleInGlobalUnit) {
        return;
      }

      // SuperAdmin of any Global Unit must keep crp_users access when CRP-Admin is removed.
      boolean isSuperAdminAnywhere = remainingRoles.stream().anyMatch(userRole ->
        userRole != null && userRole.getRole() != null
          && SUPER_ADMIN_ROLE_ACRONYM.equalsIgnoreCase(userRole.getRole().getAcronym()));
      if (isSuperAdminAnywhere) {
        return;
      }
    }

    CrpUser crpUser = crpUserManager.getCrpUserByUserIdAndCrpId(user.getId(), globalUnit.getId());
    if (crpUser != null && crpUser.getId() != null && crpUser.isActive()) {
      crpUserManager.deleteCrpUser(crpUser.getId());
    }
  }

  private GlobalUnit createBaseGlobalUnit(CreateRequest request, GlobalUnitType globalUnitType, Institution institution) {
    GlobalUnit globalUnit = new GlobalUnit();
    globalUnit.setName(StringUtils.trim(request.getName()));
    globalUnit.setAcronym(StringUtils.upperCase(StringUtils.trim(request.getAcronym())));
    globalUnit.setGlobalUnitType(globalUnitType);
    globalUnit.setInstitution(institution);
    // Required for the GU to appear and be selectable on the login page (marlo + login + active).
    globalUnit.setActive(true);
    globalUnit.setMarlo(request.isMarlo());
    globalUnit.setLogin(request.isLogin());
    if (!globalUnit.isMarlo() || !globalUnit.isLogin()) {
      LOG.warn("Global Unit '{}' created with marlo={} login={}. It will not appear as selectable on login "
        + "until both flags are true.", globalUnit.getAcronym(), globalUnit.isMarlo(), globalUnit.isLogin());
    }
    return globalUnitManager.saveGlobalUnit(globalUnit);
  }

  /**
   * Creates the PMU liaison institution required by Admin Management ({@code crp_cu}).
   * Always creates one: Management and other admin screens parse {@code crp_cu} as a Long without null checks.
   */
  private LiaisonInstitution createLiaisonInstitution(GlobalUnit globalUnit, String liaisonName,
    String liaisonAcronym) {
    LiaisonInstitution liaisonInstitution = new LiaisonInstitution();
    liaisonInstitution.setCrp(globalUnit);
    liaisonInstitution.setName(StringUtils.defaultIfBlank(StringUtils.trim(liaisonName), "PMU"));
    liaisonInstitution.setAcronym(StringUtils.defaultIfBlank(StringUtils.trim(liaisonAcronym), "PMU"));
    return liaisonInstitutionManager.saveLiaisonInstitution(liaisonInstitution);
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
        && SUPER_ADMIN_ROLE_ACRONYM.equalsIgnoreCase(role.getAcronym()))
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

  private void assignCrpAdminUsers(GlobalUnit globalUnit, List<Long> crpAdminUserIds) {
    if (crpAdminUserIds == null || crpAdminUserIds.isEmpty()) {
      return;
    }

    Role crpAdminRole = this.resolveCrpAdminRole(globalUnit);
    if (crpAdminRole == null || crpAdminRole.getId() == null) {
      throw new IllegalStateException("The CRP-Admin role could not be resolved for the new Global Unit");
    }

    Set<Long> distinctUserIds = crpAdminUserIds.stream().filter(Objects::nonNull)
      .filter(userId -> userId.longValue() > 0L).collect(Collectors.toSet());
    if (distinctUserIds.isEmpty()) {
      throw new IllegalArgumentException("At least one valid CRP Admin user is required");
    }

    for (Long userId : distinctUserIds) {
      User user = userManager.getUser(userId.longValue());
      if (user == null || user.getId() == null) {
        throw new IllegalArgumentException("Invalid CRP Admin user id: " + userId);
      }

      UserRole userRole = new UserRole();
      userRole.setUser(user);
      userRole.setRole(crpAdminRole);
      userRoleManager.saveUserRole(userRole);
      this.ensureCrpUserAccess(globalUnit, user);
    }
  }

  /**
   * Canonical CRP-Admin role resolution for creation, loading and synchronization.
   * <ol>
   * <li>Use the active {@code crp_admin_rol} custom parameter when it points to the CRP-Admin role of this GU.</li>
   * <li>Fallback to the role acronym for new GUs (whose custom parameters are initialized later) and legacy data.</li>
   * </ol>
   */
  private Role resolveCrpAdminRole(GlobalUnit globalUnit) {
    if (globalUnit == null || globalUnit.getId() == null) {
      throw new IllegalArgumentException("A persisted Global Unit is required to resolve the CRP-Admin role");
    }

    CustomParameter roleParameter = customParameterManager
      .getCustomParameterByParameterKeyAndGlobalUnitId(APConstants.CRP_ADMIN_ROLE, globalUnit.getId());
    if (roleParameter != null && roleParameter.isActive() && StringUtils.isNotBlank(roleParameter.getValue())) {
      try {
        Role configuredRole = roleManager.getRoleById(Long.parseLong(StringUtils.trim(roleParameter.getValue())));
        if (this.isCrpAdminRoleForGlobalUnit(configuredRole, globalUnit)) {
          return configuredRole;
        }
      } catch (NumberFormatException e) {
        LOG.warn("Invalid crp_admin_rol value '{}' for Global Unit {}", roleParameter.getValue(), globalUnit.getId());
      }
    }

    List<Role> roles = roleManager.findAll();
    if (roles != null) {
      Role roleByAcronym = roles.stream().filter(role -> this.isCrpAdminRoleForGlobalUnit(role, globalUnit))
        .findFirst().orElse(null);
      if (roleByAcronym != null) {
        return roleByAcronym;
      }
    }

    return null;
  }

  private boolean isCrpAdminRoleForGlobalUnit(Role role, GlobalUnit globalUnit) {
    return role != null && role.getId() != null && role.getCrp() != null && role.getCrp().getId() != null
      && role.getCrp().getId().equals(globalUnit.getId())
      && CRP_ADMIN_ROLE_ACRONYM.equalsIgnoreCase(role.getAcronym());
  }

  private void ensureCrpUserAccess(GlobalUnit globalUnit, User user) {
    if (globalUnit == null || globalUnit.getId() == null || user == null || user.getId() == null) {
      return;
    }

    CrpUser crpUser = crpUserManager.getCrpUserByUserIdAndCrpId(user.getId(), globalUnit.getId());
    if (crpUser == null) {
      crpUser = new CrpUser();
    }
    crpUser.setCrp(globalUnit);
    crpUser.setUser(user);
    crpUser.setActive(true);
    crpUserManager.saveCrpUser(crpUser);
  }

  /**
   * Resolves the user that receives initial {@code crp_users} access and the SuperAdmin role.
   * Priority: creator passed in the request (logged-in user) → last-resort username heuristic.
   */
  private User resolveConfiguredSuperAdminUser(long superAdminUserId) {
    if (superAdminUserId > 0L) {
      User creatorUser = userManager.getUser(superAdminUserId);
      if (creatorUser != null && creatorUser.getId() != null) {
        return creatorUser;
      }
      LOG.warn("Creator user id {} was provided but could not be loaded.", superAdminUserId);
    }

    User fallbackUser = userManager.getActiveSuperAdminUserByUsernameOccurrence();
    if (fallbackUser != null && fallbackUser.getId() != null) {
      LOG.warn("No creator user id was provided; falling back to username heuristic user id {}.",
        fallbackUser.getId());
      return fallbackUser;
    }

    return null;
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
    if (templateGlobalUnitId <= 0L) {
      return null;
    }
    return globalUnitManager.getGlobalUnitById(templateGlobalUnitId);
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

    if (templateGlobalUnit == null || templateGlobalUnit.getId() == null) {
      errors.add("No valid template Global Unit was selected and the current session has no compatible Global Unit");
    } else if (!templateGlobalUnit.isActive()) {
      errors.add("The template Global Unit must be active");
    } else if (templateGlobalUnit.getGlobalUnitType() == null
      || templateGlobalUnit.getGlobalUnitType().getId() == null
      || templateGlobalUnit.getGlobalUnitType().getId().longValue() != globalUnitType.getId().longValue()) {
      errors.add("The template Global Unit must have the same type as the new Global Unit");
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

  private void initializeCustomParameters(GlobalUnit globalUnit, GlobalUnit templateGlobalUnit,
    List<Phase> createdPhases, int currentPhaseIndex, String customFileName,
    LiaisonInstitution pmuLiaisonInstitution) {
    if (globalUnit == null || globalUnit.getId() == null || globalUnit.getGlobalUnitType() == null
      || globalUnit.getGlobalUnitType().getId() == null) {
      return;
    }

    List<Parameter> typeParameters = this.getParametersForGlobalUnitType(globalUnit.getGlobalUnitType().getId());
    if (typeParameters.isEmpty()) {
      LOG.warn("No parameters found for Global Unit type {} while initializing '{}'.",
        globalUnit.getGlobalUnitType().getId(), globalUnit.getAcronym());
      return;
    }

    Map<String, String> templateValues = this.buildTemplateCustomParameterMap(templateGlobalUnit);
    Map<String, Long> newRolesByAcronym = this.buildNewRolesByAcronym(globalUnit.getId());

    String computedCurrentPhase = null;
    if (currentPhaseIndex >= 0 && createdPhases != null && currentPhaseIndex < createdPhases.size()
      && createdPhases.get(currentPhaseIndex) != null && createdPhases.get(currentPhaseIndex).getId() != null) {
      computedCurrentPhase = String.valueOf(createdPhases.get(currentPhaseIndex).getId());
    }
    String computedCustomFile = StringUtils.trimToNull(customFileName);
    String computedCrpCu = null;
    if (pmuLiaisonInstitution != null && pmuLiaisonInstitution.getId() != null) {
      computedCrpCu = String.valueOf(pmuLiaisonInstitution.getId());
    }

    for (Parameter parameter : typeParameters) {
      if (parameter == null || parameter.getId() == null || StringUtils.isBlank(parameter.getKey())) {
        continue;
      }

      String value = this.resolveCustomParameterValue(parameter, templateValues, newRolesByAcronym,
        computedCurrentPhase, computedCustomFile, computedCrpCu);
      if (value == null) {
        continue;
      }

      this.saveCustomParameterValue(globalUnit, parameter, value);
    }
  }

  private String resolveCustomParameterValue(Parameter parameter, Map<String, String> templateValues,
    Map<String, Long> newRolesByAcronym, String computedCurrentPhase, String computedCustomFile,
    String computedCrpCu) {
    String key = parameter.getKey();

    if (APConstants.CURRENT_PHASE_PARAM.equals(key)) {
      return computedCurrentPhase;
    }
    if (APConstants.CRP_CUSTOM_FILE.equals(key)) {
      return computedCustomFile;
    }
    if (APConstants.CRP_CU.equals(key)) {
      return computedCrpCu;
    }
    // Timeline zoom accepts only 1..8; migration default_value '423' is invalid. New GUs start at 4.
    if (APConstants.CRP_TIMELINE_WEEK_PARAMETER_VISUALIZATION.equals(key)) {
      return "4";
    }
    // New GU must not start closed or force a session refresh cycle from the template.
    if (APConstants.CRP_CLOSED.equals(key) || APConstants.CRP_REFRESH.equals(key)) {
      return "false";
    }
    if (ROLE_PARAMETER_KEYS.contains(key)) {
      String remappedRoleId = this.remapRoleParameterValue(key, templateValues.get(key), newRolesByAcronym);
      if (remappedRoleId != null) {
        return remappedRoleId;
      }
      return StringUtils.trimToNull(parameter.getDefaultValue());
    }

    if (this.isSafeToInherit(parameter) && templateValues.containsKey(key)) {
      return templateValues.get(key);
    }

    return StringUtils.trimToNull(parameter.getDefaultValue());
  }

  private boolean isSafeToInherit(Parameter parameter) {
    if (parameter == null || StringUtils.isBlank(parameter.getKey())) {
      return false;
    }
    if (NEVER_INHERIT_PARAMETER_KEYS.contains(parameter.getKey())) {
      return false;
    }
    if (parameter.getFormat() != null && parameter.getFormat().intValue() == PARAMETER_FORMAT_BOOLEAN) {
      return true;
    }
    if (parameter.getCategory() != null && parameter.getCategory().intValue() == PARAMETER_CATEGORY_SPECIFICITY) {
      return true;
    }
    String key = parameter.getKey().toLowerCase(Locale.ROOT);
    return key.endsWith("_active") || key.endsWith("_module");
  }

  private String remapRoleParameterValue(String parameterKey, String templateRoleIdValue,
    Map<String, Long> newRolesByAcronym) {
    if (StringUtils.isBlank(templateRoleIdValue) || newRolesByAcronym == null || newRolesByAcronym.isEmpty()) {
      return null;
    }

    long templateRoleId;
    try {
      templateRoleId = Long.parseLong(StringUtils.trim(templateRoleIdValue));
    } catch (NumberFormatException e) {
      LOG.warn("Role parameter '{}' has non-numeric template value '{}'; skipping remap.", parameterKey,
        templateRoleIdValue);
      return null;
    }

    Role templateRole = roleManager.getRoleById(templateRoleId);
    if (templateRole == null || StringUtils.isBlank(templateRole.getAcronym())) {
      LOG.warn("Could not resolve template role id {} for parameter '{}'.", templateRoleId, parameterKey);
      return null;
    }

    Long newRoleId = newRolesByAcronym.get(templateRole.getAcronym().toLowerCase(Locale.ROOT));
    if (newRoleId == null) {
      LOG.warn("No cloned role with acronym '{}' found for parameter '{}' on the new Global Unit.",
        templateRole.getAcronym(), parameterKey);
      return null;
    }
    return String.valueOf(newRoleId);
  }

  private Map<String, String> buildTemplateCustomParameterMap(GlobalUnit templateGlobalUnit) {
    Map<String, String> values = new HashMap<>();
    if (templateGlobalUnit == null || templateGlobalUnit.getId() == null) {
      return values;
    }

    List<CustomParameter> templateParameters =
      customParameterManager.getAllCustomParametersByGlobalUnitId(templateGlobalUnit.getId());
    if (templateParameters == null) {
      return values;
    }

    for (CustomParameter customParameter : templateParameters) {
      if (customParameter == null || !customParameter.isActive() || customParameter.getParameter() == null
        || StringUtils.isBlank(customParameter.getParameter().getKey()) || customParameter.getValue() == null) {
        continue;
      }
      values.put(customParameter.getParameter().getKey(), customParameter.getValue());
    }
    return values;
  }

  private Map<String, Long> buildNewRolesByAcronym(Long globalUnitId) {
    Map<String, Long> rolesByAcronym = new HashMap<>();
    if (globalUnitId == null) {
      return rolesByAcronym;
    }

    List<Role> roles = roleManager.findAll();
    if (roles == null) {
      return rolesByAcronym;
    }

    for (Role role : roles) {
      if (role == null || role.getId() == null || role.getCrp() == null || role.getCrp().getId() == null
        || !globalUnitId.equals(role.getCrp().getId()) || StringUtils.isBlank(role.getAcronym())) {
        continue;
      }
      rolesByAcronym.put(role.getAcronym().toLowerCase(Locale.ROOT), role.getId());
    }
    return rolesByAcronym;
  }

  private List<Parameter> getParametersForGlobalUnitType(Long globalUnitTypeId) {
    List<Parameter> parameters = parameterManager.findAll();
    if (parameters == null || parameters.isEmpty() || globalUnitTypeId == null) {
      return Collections.emptyList();
    }

    return parameters.stream()
      .filter(parameter -> parameter != null && parameter.getGlobalUnitType() != null
        && parameter.getGlobalUnitType().getId() != null
        && globalUnitTypeId.equals(parameter.getGlobalUnitType().getId()))
      .collect(Collectors.toList());
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

  private void saveCustomParameterValue(GlobalUnit globalUnit, Parameter parameter, String value) {
    if (globalUnit == null || globalUnit.getId() == null || parameter == null || parameter.getId() == null
      || value == null) {
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
