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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.StringUtils;

@Named
public class GlobalUnitCreationManagerImpl implements GlobalUnitCreationManager {

  private final GlobalUnitManager globalUnitManager;
  private final GlobalUnitTypeManager globalUnitTypeManager;
  private final InstitutionManager institutionManager;
  private final PhaseManager phaseManager;
  private final CrpUserManager crpUserManager;
  private final UserManager userManager;
  private final RoleManager roleManager;
  private final CrpLocElementTypeManager crpLocElementTypeManager;
  private final LiaisonInstitutionManager liaisonInstitutionManager;
  private final ParameterManager parameterManager;
  private final CustomParameterManager customParameterManager;

  @Inject
  public GlobalUnitCreationManagerImpl(GlobalUnitManager globalUnitManager, GlobalUnitTypeManager globalUnitTypeManager,
    InstitutionManager institutionManager, PhaseManager phaseManager, CrpUserManager crpUserManager,
    UserManager userManager, RoleManager roleManager, CrpLocElementTypeManager crpLocElementTypeManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ParameterManager parameterManager,
    CustomParameterManager customParameterManager) {
    this.globalUnitManager = globalUnitManager;
    this.globalUnitTypeManager = globalUnitTypeManager;
    this.institutionManager = institutionManager;
    this.phaseManager = phaseManager;
    this.crpUserManager = crpUserManager;
    this.userManager = userManager;
    this.roleManager = roleManager;
    this.crpLocElementTypeManager = crpLocElementTypeManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.parameterManager = parameterManager;
    this.customParameterManager = customParameterManager;
  }

  @Override
  public GlobalUnit createGlobalUnit(CreateRequest request) {
    this.validateRequest(request);

    GlobalUnitType globalUnitType = this.getGlobalUnitTypeOrFail(request.getGlobalUnitTypeId());
    this.getTemplateGlobalUnitOrFail(request.getTemplateGlobalUnitId());

    Institution institution = this.resolveInstitution(request.getInstitutionId());
    GlobalUnit globalUnit = this.createBaseGlobalUnit(request, globalUnitType, institution);
    List<Phase> createdPhases = this.createPhases(globalUnit, request.getPhasesInput());

    this.createSuperAdminAccess(globalUnit, request.getSuperAdminUserId());
    this.cloneRoles(globalUnit, request.getTemplateGlobalUnitId());
    roleManager.cloneRolePermissionsByAcronym(request.getTemplateGlobalUnitId(), globalUnit.getId());
    this.cloneLocTypes(globalUnit, request.getTemplateGlobalUnitId());
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

  private void createSuperAdminAccess(GlobalUnit globalUnit, long superAdminUserId) {
    if (superAdminUserId <= 0L) {
      return;
    }

    User superAdminUser = userManager.getUser(superAdminUserId);
    if (superAdminUser == null || superAdminUser.getId() == null
      || crpUserManager.existCrpUser(superAdminUser.getId(), globalUnit.getId())) {
      return;
    }

    CrpUser crpUser = new CrpUser();
    crpUser.setCrp(globalUnit);
    crpUser.setUser(superAdminUser);
    crpUserManager.saveCrpUser(crpUser);
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

  private GlobalUnit getTemplateGlobalUnitOrFail(long templateGlobalUnitId) {
    GlobalUnit templateGlobalUnit = globalUnitManager.getGlobalUnitById(templateGlobalUnitId);
    if (templateGlobalUnit == null || templateGlobalUnit.getId() == null) {
      throw new IllegalArgumentException("Invalid template global unit");
    }
    return templateGlobalUnit;
  }

  private Institution resolveInstitution(Long institutionId) {
    if (institutionId == null || institutionId.longValue() <= 0L) {
      return null;
    }
    return institutionManager.getInstitutionById(institutionId.longValue());
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
