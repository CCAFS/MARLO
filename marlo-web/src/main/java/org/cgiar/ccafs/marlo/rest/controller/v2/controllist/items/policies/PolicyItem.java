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

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CgiarCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicySubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyInvestimentTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageProcessManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndOrganizationType;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyInvestimentType;
import org.cgiar.ccafs.marlo.data.model.RepIndStageProcess;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityDTO;
import org.cgiar.ccafs.marlo.rest.dto.CountryDTO;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPolicyCrosscuttingMarkersDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.dto.RegionDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfSubIdoDTO;
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
  private RepIndPolicyInvestimentTypeManager repIndPolicyInvestimentTypeManager;
  private RepIndStageProcessManager repIndStageProcessManager;
  private RepIndOrganizationTypeManager repIndOrganizationTypeManager;
  private ProjectManager projectManager;
  private SrfSubIdoManager SrfSubIdoManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private LocElementManager locElementManager;
  private ProjectPolicyMapper projectPolicyMapper;
  private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;
  private RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager;

  @Inject
  public PolicyItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    ProjectPolicyManager projectPolicyManager, ProjectPolicyGeographicScopeManager projectPolicyGeographicScopeManager,
    ProjectPolicyCrpManager projectPolicyCrpManager, ProjectPolicyInfoManager projectPolicyInfoManager,
    ProjectPolicySubIdoManager projectPolicySubIdoManager, ProjectPolicyMapper projectPolicyMapper,
    RepIndPolicyInvestimentTypeManager repIndPolicyInvestimentTypeManager,
    RepIndStageProcessManager repIndStageProcessManager, RepIndOrganizationTypeManager repIndOrganizationTypeManager,
    ProjectManager projectManager, SrfSubIdoManager srfSubIdoManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager, LocElementManager locElementManager,
    CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager,
    RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.projectPolicyManager = projectPolicyManager;
    this.projectPolicyCrpManager = projectPolicyCrpManager;
    this.projectPolicyMapper = projectPolicyMapper;
    this.projectPolicyInfoManager = projectPolicyInfoManager;
    this.projectPolicyGeographicScopeManager = projectPolicyGeographicScopeManager;
    this.projectPolicySubIdoManager = projectPolicySubIdoManager;
    this.repIndPolicyInvestimentTypeManager = repIndPolicyInvestimentTypeManager;
    this.repIndStageProcessManager = repIndStageProcessManager;
    this.repIndOrganizationTypeManager = repIndOrganizationTypeManager;
    this.projectManager = projectManager;
    this.SrfSubIdoManager = srfSubIdoManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.locElementManager = locElementManager;
    this.cgiarCrossCuttingMarkerManager = cgiarCrossCuttingMarkerManager;
    this.repIndGenderYouthFocusLevelManager = repIndGenderYouthFocusLevelManager;
  }

  public Long createPolicy(NewProjectPolicyDTO newPolicyDTO, String entityAcronym, User user) {
    Long policyID = null;
    ProjectPolicy projectPolicy = new ProjectPolicy();
    ProjectPolicyInfo projectPolicyInfo = new ProjectPolicyInfo();
    List<ProjectPolicyCrp> projectPolicyList = new ArrayList<ProjectPolicyCrp>();
    List<ProjectPolicySubIdo> projectPolicySubIdoList = new ArrayList<ProjectPolicySubIdo>();
    List<ProjectPolicyGeographicScope> projectPolicyGeographicScopeList = new ArrayList<ProjectPolicyGeographicScope>();
    List<ProjectPolicyCountry> projectPolicyCountryList = new ArrayList<ProjectPolicyCountry>();
    List<ProjectPolicyRegion> projectPolicyRegionList = new ArrayList<ProjectPolicyRegion>();
    List<ProjectPolicyCrossCuttingMarker> ProjectPolicyCrossCuttingMarkerList =
      new ArrayList<ProjectPolicyCrossCuttingMarker>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream()
        .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
          && c.getYear() == newPolicyDTO.getPhase().getYear()
          && c.getName().equalsIgnoreCase(newPolicyDTO.getPhase().getName()))
        .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createPolicy", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }
    if (newPolicyDTO.getProjectPoliciesInfo() != null) {
      // policy investiment type
      if (newPolicyDTO.getProjectPoliciesInfo().getRepIndPolicyInvestimentType() != null) {
        RepIndPolicyInvestimentType repIndPolicyInvestimentType =
          this.repIndPolicyInvestimentTypeManager.getRepIndPolicyInvestimentTypeById(
            newPolicyDTO.getProjectPoliciesInfo().getRepIndPolicyInvestimentType().getCode().longValue());
        if (repIndPolicyInvestimentType == null) {
          fieldErrors.add(new FieldErrorDTO("createPolicy", "repIndPolicyInvestimentType",
            new NewProjectPolicyDTO().getProjectPoliciesInfo().getRepIndPolicyInvestimentType().getCode()
              + " is an invalid investiment type code"));
        } else {
          projectPolicyInfo.setRepIndPolicyInvestimentType(repIndPolicyInvestimentType);
        }
      } else {
        fieldErrors
          .add(new FieldErrorDTO("createPolicy", "repIndPolicyInvestimentType", "policy investiment type is need it"));
      }
      // policy info maturity level
      if (newPolicyDTO.getProjectPoliciesInfo().getRepIndStageProcess() != null) {
        RepIndStageProcess repIndStageProcess = repIndStageProcessManager.getRepIndStageProcessById(
          newPolicyDTO.getProjectPoliciesInfo().getRepIndStageProcess().getCode().longValue());
        if (repIndStageProcess == null) {
          fieldErrors.add(new FieldErrorDTO("createPolicy", "repIndStageProcess",
            new NewProjectPolicyDTO().getProjectPoliciesInfo().getRepIndStageProcess().getCode()
              + " is an invalid maturity level code"));
        } else {
          projectPolicyInfo.setRepIndStageProcess(repIndStageProcess);
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("createPolicy", "repIndStageProcess", "policy maturity level is need it"));
      }
      // policy info organization type
      if (newPolicyDTO.getProjectPoliciesInfo().getRepIndOrganizationType() != null) {
        RepIndOrganizationType repIndOrganizationType = repIndOrganizationTypeManager.getRepIndOrganizationTypeById(
          newPolicyDTO.getProjectPoliciesInfo().getRepIndOrganizationType().getCode().longValue());
        if (repIndOrganizationType == null) {
          fieldErrors.add(new FieldErrorDTO("createPolicy", "repIndOrganizationType",
            new NewProjectPolicyDTO().getProjectPoliciesInfo().getRepIndOrganizationType().getCode()
              + " is an invalid organization type code"));
        } else {

        }
      }
      // validate generic project id
      Project project = this.projectManager.getProjectById(newPolicyDTO.getProject().getId());
      if (project == null) {
        fieldErrors.add(new FieldErrorDTO("createPolicy", "Project id",
          newPolicyDTO.getProject().getId() + " is an invalid project id"));
      }
      // validate policy info
      if (fieldErrors.isEmpty()) {
        // validate crp contributing
        if (newPolicyDTO.getProjectPolicyCrpDTO() != null && newPolicyDTO.getProjectPolicyCrpDTO().size() > 0) {
          for (CGIAREntityDTO contributingCRP : newPolicyDTO.getProjectPolicyCrpDTO()) {
            GlobalUnit crp = this.globalUnitManager.findGlobalUnitBySMOCode(contributingCRP.getCode());
            if (crp == null) {
              fieldErrors.add(new FieldErrorDTO("createPolicy", "ContributingCGIAREntities",
                contributingCRP.getCode() + " is an invalid CGIAR entity code"));
            } else {
              ProjectPolicyCrp projectPolicyCrp = new ProjectPolicyCrp();
              projectPolicyCrp.setGlobalUnit(crp);
              projectPolicyCrp.setPhase(phase);
              projectPolicyCrp.setProjectPolicy(projectPolicy);
              projectPolicyList.add(projectPolicyCrp);
            }
          }
        }
        // validate sub-IDO
        if (newPolicyDTO.getSrfSubIdoList() != null && newPolicyDTO.getSrfSubIdoList().size() > 0) {
          for (SrfSubIdoDTO subIdos : newPolicyDTO.getSrfSubIdoList()) {
            SrfSubIdo srfSubIdo = SrfSubIdoManager.getSrfSubIdoByCode(subIdos.getCode());
            if (srfSubIdo == null) {
              fieldErrors
                .add(new FieldErrorDTO("createPolicy", "SrfSubIdo", subIdos.getCode() + " is an invalid Sub-IDO code"));
            } else {
              ProjectPolicySubIdo projectPolicySubIdo = new ProjectPolicySubIdo();
              projectPolicySubIdo.setProjectPolicy(projectPolicy);
              projectPolicySubIdo.setPhase(phase);
              projectPolicySubIdo.setSrfSubIdo(srfSubIdo);
              projectPolicySubIdoList.add(projectPolicySubIdo);
            }
          }
        }
        // validate geographic scope
        if (newPolicyDTO.getGeographicScopes() != null && newPolicyDTO.getGeographicScopes().size() > 0) {
          for (GeographicScopeDTO geographicScope : newPolicyDTO.getGeographicScopes()) {
            RepIndGeographicScope geoScope =
              this.repIndGeographicScopeManager.getRepIndGeographicScopeById(geographicScope.getCode());
            if (geoScope == null) {
              fieldErrors.add(new FieldErrorDTO("createPolicy", "geographicScope",
                geographicScope.getCode() + " is an invalid geographic Scope code"));
            } else {
              ProjectPolicyGeographicScope projectPolicyGeographicScope = new ProjectPolicyGeographicScope();
              projectPolicyGeographicScope.setPhase(phase);
              projectPolicyGeographicScope.setProjectPolicy(projectPolicy);
              projectPolicyGeographicScope.setRepIndGeographicScope(geoScope);
              projectPolicyGeographicScopeList.add(projectPolicyGeographicScope);
            }
          }
        }
        // validate countries
        if (newPolicyDTO.getCountries() != null && newPolicyDTO.getCountries().size() > 0) {
          for (CountryDTO iso : newPolicyDTO.getCountries()) {
            LocElement country = this.locElementManager.getLocElementByNumericISOCode(iso.getCode());
            if (country == null) {
              fieldErrors.add(new FieldErrorDTO("createPolicy", "Countries", iso + " is an invalid country ISO Code"));

            } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_COUNTRY) {
              fieldErrors.add(new FieldErrorDTO("createPolicy", "Countries", iso + " is not a Country ISO code"));
            } else {
              ProjectPolicyCountry projectPolicyCountry = new ProjectPolicyCountry();
              projectPolicyCountry.setLocElement(country);
              projectPolicyCountry.setPhase(phase);
              projectPolicyCountry.setProjectPolicy(projectPolicy);
              projectPolicyCountryList.add(projectPolicyCountry);
            }
          }
        }
        // validate regions
        if (newPolicyDTO.getRegions() != null && newPolicyDTO.getRegions().size() > 0) {
          for (RegionDTO iso : newPolicyDTO.getRegions()) {
            LocElement region = this.locElementManager.getLocElementByNumericISOCode(iso.getUM49Code());
            if (region == null) {
              fieldErrors.add(new FieldErrorDTO("createPolicy", "Regions", iso + " is an invalid region ISO Code"));

            } else if (region.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_REGION) {
              fieldErrors.add(new FieldErrorDTO("createPolicy", "Regions", iso + " is not a region ISO code"));
            } else {
              ProjectPolicyRegion projectPolicyRegion = new ProjectPolicyRegion();
              projectPolicyRegion.setLocElement(region);
              projectPolicyRegion.setPhase(phase);
              projectPolicyRegion.setProjectPolicy(projectPolicy);
              projectPolicyRegionList.add(projectPolicyRegion);
            }
          }
        }
        // validate crosscutting markers
        if (newPolicyDTO.getCrossCuttingMarkers() != null && newPolicyDTO.getCrossCuttingMarkers().size() > 0) {
          for (ProjectPolicyCrosscuttingMarkersDTO crosscuttingmarker : newPolicyDTO.getCrossCuttingMarkers()) {
            CgiarCrossCuttingMarker cgiarCrossCuttingMarker = cgiarCrossCuttingMarkerManager
              .getCgiarCrossCuttingMarkerById(crosscuttingmarker.getCrossCuttingmarker().getCode());
            if (cgiarCrossCuttingMarker == null) {
              fieldErrors.add(new FieldErrorDTO("createPolicy", "Crosscuttingmarker",
                cgiarCrossCuttingMarker + " is an invalid Crosscuttingmarker Code"));

            } else {
              RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel = repIndGenderYouthFocusLevelManager
                .getRepIndGenderYouthFocusLevelById(crosscuttingmarker.getCrossCuttingmarkerScore().getCode());
              if (repIndGenderYouthFocusLevel == null) {
                fieldErrors.add(new FieldErrorDTO("createPolicy", "CrosscuttingmarkerScore",
                  cgiarCrossCuttingMarker + " is an invalid GenderYouthFocusLevel ISO Code"));
              } else {
                ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker = new ProjectPolicyCrossCuttingMarker();
                projectPolicyCrossCuttingMarker.setCgiarCrossCuttingMarker(cgiarCrossCuttingMarker);
                projectPolicyCrossCuttingMarker.setRepIndGenderYouthFocusLevel(repIndGenderYouthFocusLevel);
                projectPolicyCrossCuttingMarker.setPhase(phase);
                projectPolicyCrossCuttingMarker.setProjectPolicy(projectPolicy);
                ProjectPolicyCrossCuttingMarkerList.add(projectPolicyCrossCuttingMarker);
              }
            }
          }

        }
        // can save
        if (fieldErrors.isEmpty()) {
          projectPolicy.setProject(project);
          projectPolicy = projectPolicyManager.saveProjectPolicy(projectPolicy);

          if (projectPolicy != null) {
            policyID = projectPolicy.getId();
          }
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("createPolicy", "projectPolicyInfo", "policy info is need it"));
    }

    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return policyID;
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
