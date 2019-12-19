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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.policies;

import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicySubIdoManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.ProjectPolicyMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


@Named
public class PolicyItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private ProjectPolicyManager projectPolicyManager;
  private ProjectPolicyCrpManager projectPolicyCrpManager;
  private ProjectPolicyInfoManager projectPolicyInfoManager;
  private ProjectPolicyGeographicScopeManager projectPolicyGeographicScopeManager;
  private ProjectPolicySubIdoManager projectPolicySubIdoManager;

  private ProjectPolicyMapper projectPolicyMapper;

  @Inject
  public PolicyItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    ProjectPolicyManager projectPolicyManager, ProjectPolicyGeographicScopeManager projectPolicyGeographicScopeManager,
    ProjectPolicyCrpManager projectPolicyCrpManager, ProjectPolicyInfoManager projectPolicyInfoManager,
    ProjectPolicySubIdoManager projectPolicySubIdoManager, ProjectPolicyMapper projectPolicyMapper) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.projectPolicyManager = projectPolicyManager;
    this.projectPolicyCrpManager = projectPolicyCrpManager;
    this.projectPolicyMapper = projectPolicyMapper;
    this.projectPolicyInfoManager = projectPolicyInfoManager;
    this.projectPolicyGeographicScopeManager = projectPolicyGeographicScopeManager;
    this.projectPolicySubIdoManager = projectPolicySubIdoManager;
  }

  public List<ProjectPolicyDTO> findAllPoliciesByGlobalUnit(String CGIARentityAcronym, Integer repoYear,
    String repoPhase, User user) {
    List<ProjectPolicyDTO> policyList = new ArrayList<ProjectPolicyDTO>();
    List<ProjectPolicy> projectPolicyList = new ArrayList<ProjectPolicy>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "GlobalUnitEntity",
        CGIARentityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().get();
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createPolicy", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    } else {
      List<ProjectPolicyInfo> projectPolicyInfoList = phase.getProjectPolicyInfos().stream()
        .filter(c -> c.getPhase().getId().equals(phase.getId())).collect(Collectors.toList());
      for (ProjectPolicyInfo projectPolicyInfo : projectPolicyInfoList) {
        ProjectPolicy projectPolicy =
          projectPolicyManager.getProjectPolicyById(projectPolicyInfo.getProjectPolicy().getId());
        projectPolicy.setProjectPolicyInfo(projectPolicyInfo);
        projectPolicy.setGeographicScopes(projectPolicy.getProjectPolicyGeographicScopes().stream()
          .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicy.getId().longValue()
            && c.getPhase().getId().longValue() == phase.getId().longValue())
          .collect(Collectors.toList()));
        // Setting CRP contributing
        projectPolicy.setCrps(projectPolicyCrpManager.findAll().stream()
          .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicy.getId().longValue()
            && c.getPhase().getId() == phase.getId())
          .collect(Collectors.toList()));
        // Setting CrossCuttingMarker
        projectPolicy.setCrossCuttingMarkers(projectPolicy.getProjectPolicyCrossCuttingMarkers().stream()
          .filter(c -> c.getPhase().getId().longValue() == phase.getId().longValue()).collect(Collectors.toList()));
        // Setting SubIdos
        projectPolicy.setSubIdos(projectPolicySubIdoManager.findAll().stream()
          .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicy.getId().longValue()
            && c.getPhase().getId().longValue() == phase.getId().longValue())
          .collect(Collectors.toList()));
        // setting countries
        projectPolicy.setCountries(projectPolicy.getProjectPolicyCountries().stream()
          .filter(c -> c.getPhase().getId().longValue() == phase.getId().longValue()).collect(Collectors.toList()));
        // setting regions
        projectPolicy.setRegions(projectPolicy.getProjectPolicyRegions().stream()
          .filter(c -> c.getPhase().getId().longValue() == phase.getId().longValue()).collect(Collectors.toList()));
        projectPolicyList.add(projectPolicy);
      }
    }
    policyList = projectPolicyList.stream()
      .map(policy -> this.projectPolicyMapper.projectPolicyToProjectPolicyDTO(policy)).collect(Collectors.toList());
    return policyList;
  }

  public ResponseEntity<ProjectPolicyDTO> findPolicyById(Long id, String CGIARentityAcronym, Integer repoYear,
    String repoPhase, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    ProjectPolicy projectPolicy = projectPolicyManager.getProjectPolicyById(id.longValue());
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().get();

    Set<CrpUser> lstUser = user.getCrpUsers();

    if (!lstUser.stream().anyMatch(crp -> crp.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("findPolicy", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findPolicy", "GlobalUnitEntity",
        CGIARentityAcronym + " is not an invalid CGIAR entity acronym"));
    }
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("findPolicy", "phase", repoYear + " is an invalid year"));
    }

    if (projectPolicy == null || projectPolicy.getProjectPolicyInfo(phase) == null) {
      fieldErrors.add(new FieldErrorDTO("findPolicy", "InnovationId", id + " is an invalid id of an policy"));
    }

    if (projectPolicy.getProjectPolicyInfo() != null) {
      projectPolicy.getProjectPolicyInfo(phase);
      System.out.println("" + projectPolicy.getProjectPolicyInfo().getRepIndStageProcess().getId());
      // Setting Geographic Scope
      projectPolicy.setGeographicScopes(projectPolicy.getProjectPolicyGeographicScopes().stream()
        .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicy.getId().longValue()
          && c.getPhase().getId().longValue() == phase.getId().longValue())
        .collect(Collectors.toList()));
      // Setting CRP contributing
      projectPolicy.setCrps(projectPolicyCrpManager.findAll().stream()
        .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicy.getId().longValue()
          && c.getPhase().getId() == phase.getId())
        .collect(Collectors.toList()));
      // Setting CrossCuttingMarker
      projectPolicy.setCrossCuttingMarkers(projectPolicy.getProjectPolicyCrossCuttingMarkers().stream()
        .filter(c -> c.getPhase().getId().longValue() == phase.getId().longValue()).collect(Collectors.toList()));
      // Setting SubIdos
      projectPolicy.setSubIdos(projectPolicySubIdoManager.findAll().stream()
        .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicy.getId().longValue()
          && c.getPhase().getId().longValue() == phase.getId().longValue())
        .collect(Collectors.toList()));
      // setting countries
      projectPolicy.setCountries(projectPolicy.getProjectPolicyCountries().stream()
        .filter(c -> c.getPhase().getId().longValue() == phase.getId().longValue()).collect(Collectors.toList()));
      // setting regions
      projectPolicy.setRegions(projectPolicy.getProjectPolicyRegions().stream()
        .filter(c -> c.getPhase().getId().longValue() == phase.getId().longValue()).collect(Collectors.toList()));
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(projectPolicy).map(this.projectPolicyMapper::projectPolicyToProjectPolicyDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
