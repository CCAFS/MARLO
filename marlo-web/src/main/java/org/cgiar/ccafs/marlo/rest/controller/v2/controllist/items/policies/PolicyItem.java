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
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitProjectManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.PolicyMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyOwnerManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicySubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndOrganizationTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyInvestimentTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPolicyTypeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageProcessManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.GlobalUnitProject;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.PolicyMilestone;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovationInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyOwner;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicySubIdo;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyInvestimentType;
import org.cgiar.ccafs.marlo.data.model.RepIndPolicyType;
import org.cgiar.ccafs.marlo.data.model.RepIndStageProcess;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressPolicy;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.CGIAREntityDTO;
import org.cgiar.ccafs.marlo.rest.dto.CountryDTO;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewCrosscuttingMarkersDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewMilestonesDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewSrfSubIdoDTO;
import org.cgiar.ccafs.marlo.rest.dto.PolicyOwnerTypeDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPolicyARDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.dto.RegionDTO;
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

import org.apache.commons.lang3.StringUtils;
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
  private ProjectPolicyCountryManager projectPolicyCountryManager;
  private ProjectPolicyRegionManager projectPolicyRegionManager;
  private ProjectPolicyCrossCuttingMarkerManager projectPolicyCrossCuttingMarkerManager;
  private ProjectPolicyOwnerManager projectPolicyOwnerManager;
  private ProjectPolicyInnovationManager projectPolicyInnovationManager;
  private RepIndPolicyInvestimentTypeManager repIndPolicyInvestimentTypeManager;
  private ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager;
  private RepIndStageProcessManager repIndStageProcessManager;
  private RepIndPolicyTypeManager repIndPolicyTypeManager;
  private ProjectManager projectManager;
  private SrfSubIdoManager srfSubIdoManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private LocElementManager locElementManager;
  private ProjectPolicyMapper projectPolicyMapper;
  private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;
  private RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager;
  private PolicyMilestoneManager policyMilestoneManager;
  private CrpMilestoneManager crpMilestoneManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private GlobalUnitProjectManager globalUnitProjectManager;

  private ProjectInnovationManager projectInnovationManager;
  // changes to be included to Synthesis
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressPolicyManager reportSynthesisFlagshipProgressPolicyManager;

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
    RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager,
    ProjectPolicyCountryManager projectPolicyCountryManager, ProjectPolicyRegionManager projectPolicyRegionManager,
    ProjectPolicyCrossCuttingMarkerManager projectPolicyCrossCuttingMarkerManager,
    ProjectPolicyOwnerManager projectPolicyOwnerManager, RepIndPolicyTypeManager repIndPolicyTypeManager,
    PolicyMilestoneManager policyMilestoneManager, CrpMilestoneManager crpMilestoneManager,
    ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager,
    ProjectExpectedStudyManager projectExpectedStudyManager,
    ProjectPolicyInnovationManager projectPolicyInnovationManager, ProjectInnovationManager projectInnovationManager,
    GlobalUnitProjectManager globalUnitProjectManager, LiaisonInstitutionManager liaisonInstitutionManager,
    ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFlagshipProgressPolicyManager reportSynthesisFlagshipProgressPolicyManager) {
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
    this.projectManager = projectManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.locElementManager = locElementManager;
    this.cgiarCrossCuttingMarkerManager = cgiarCrossCuttingMarkerManager;
    this.repIndGenderYouthFocusLevelManager = repIndGenderYouthFocusLevelManager;
    this.projectPolicyCountryManager = projectPolicyCountryManager;
    this.projectPolicyRegionManager = projectPolicyRegionManager;
    this.projectPolicyCrossCuttingMarkerManager = projectPolicyCrossCuttingMarkerManager;
    this.projectPolicyOwnerManager = projectPolicyOwnerManager;
    this.repIndPolicyTypeManager = repIndPolicyTypeManager;
    this.policyMilestoneManager = policyMilestoneManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.projectExpectedStudyPolicyManager = projectExpectedStudyPolicyManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectPolicyInnovationManager = projectPolicyInnovationManager;
    this.projectInnovationManager = projectInnovationManager;
    this.globalUnitProjectManager = globalUnitProjectManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFlagshipProgressPolicyManager = reportSynthesisFlagshipProgressPolicyManager;
  }

  public Long createPolicy(NewProjectPolicyDTO newPolicyDTO, String entityAcronym, User user) {
    Long policyID = null;
    ProjectPolicy projectPolicy = new ProjectPolicy();
    ProjectPolicyInfo projectPolicyInfo = new ProjectPolicyInfo();
    List<ProjectPolicyCrp> projectPolicyCrpList = new ArrayList<ProjectPolicyCrp>();
    List<ProjectPolicySubIdo> projectPolicySubIdoList = new ArrayList<ProjectPolicySubIdo>();
    List<ProjectPolicyGeographicScope> projectPolicyGeographicScopeList = new ArrayList<ProjectPolicyGeographicScope>();
    List<ProjectPolicyCountry> projectPolicyCountryList = new ArrayList<ProjectPolicyCountry>();
    List<ProjectPolicyRegion> projectPolicyRegionList = new ArrayList<ProjectPolicyRegion>();
    List<ProjectPolicyCrossCuttingMarker> ProjectPolicyCrossCuttingMarkerList =
      new ArrayList<ProjectPolicyCrossCuttingMarker>();
    List<PolicyMilestone> policyMilestones = new ArrayList<>();
    List<ProjectPolicyInnovation> projectPolicyInnovationsList = new ArrayList<ProjectPolicyInnovation>();
    List<ProjectPolicyOwner> projectPolicyOwnerList = new ArrayList<ProjectPolicyOwner>();

    String strippedId = null;
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
            newPolicyDTO.getProjectPoliciesInfo().getRepIndPolicyInvestimentType().getCode()
              + " is an invalid investiment type code"));
        } else {
          if (newPolicyDTO.getProjectPoliciesInfo().getRepIndPolicyInvestimentType().getCode().longValue() == 3) {
            if (newPolicyDTO.getProjectPoliciesInfo().getBudgetAmount() == null) {
              fieldErrors.add(new FieldErrorDTO("createPolicy", "budgetAmount", " budget amount is required"));
            } else if (newPolicyDTO.getProjectPoliciesInfo().getBudgetAmount() <= 0) {
              fieldErrors
                .add(new FieldErrorDTO("createPolicy", "budgetAmount", " budget amount needs to be more than zero"));
            } else {
              projectPolicyInfo.setAmount(newPolicyDTO.getProjectPoliciesInfo().getBudgetAmount());
            }
          } else {
            projectPolicyInfo.setAmount(null);
          }
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

      // validate generic project id
      Project project = this.projectManager.getProjectById(newPolicyDTO.getProject());
      if (project == null) {
        fieldErrors.add(
          new FieldErrorDTO("createPolicy", "Project id", newPolicyDTO.getProject() + " is an invalid project id"));
      }
      if (phase != null && !phase.getEditable()) {
        fieldErrors.add(new FieldErrorDTO("createPolicy", "phase",
          new NewProjectPolicyDTO().getPhase().getYear() + " is closed phase"));
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
              projectPolicyCrpList.add(projectPolicyCrp);
            }
          }
          // validate if owner CRP was included
          boolean exist = false;
          for (CGIAREntityDTO contributingCRP : newPolicyDTO.getProjectPolicyCrpDTO()) {
            GlobalUnit crp = this.globalUnitManager.findGlobalUnitBySMOCode(globalUnitEntity.getSmoCode());
            if (crp.getAcronym().equals(globalUnitEntity.getAcronym())) {
              exist = true;
            }
          }
          if (!exist) {
            GlobalUnit crp = this.globalUnitManager.findGlobalUnitBySMOCode(globalUnitEntity.getSmoCode());
            ProjectPolicyCrp projectPolicyCrp = new ProjectPolicyCrp();
            projectPolicyCrp.setGlobalUnit(crp);
            projectPolicyCrp.setPhase(phase);
            projectPolicyCrp.setProjectPolicy(projectPolicy);
            projectPolicyCrpList.add(projectPolicyCrp);
          }
        }
        // validate sub-IDO
        if (newPolicyDTO.getSrfSubIdoList() != null && newPolicyDTO.getSrfSubIdoList().size() > 0) {
          for (NewSrfSubIdoDTO subIdos : newPolicyDTO.getSrfSubIdoList()) {
            SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoByCode(subIdos.getSubIdo());
            if (srfSubIdo == null) {
              fieldErrors.add(
                new FieldErrorDTO("createPolicy", "SrfSubIdo", subIdos.getSubIdo() + " is an invalid Sub-IDO code"));
            } else {
              ProjectPolicySubIdo projectPolicySubIdo = new ProjectPolicySubIdo();
              projectPolicySubIdo.setProjectPolicy(projectPolicy);
              projectPolicySubIdo.setPhase(phase);
              projectPolicySubIdo.setSrfSubIdo(srfSubIdo);
              if (subIdos.getPrimary() != null && subIdos.getPrimary()) {
                projectPolicySubIdo.setPrimary(subIdos.getPrimary());
              } else {
                projectPolicySubIdo.setPrimary(false);
              }
              projectPolicySubIdoList.add(projectPolicySubIdo);
            }
          }
        }
        // validate milestones
        if (newPolicyDTO.getMilestonesCodeList() != null && !newPolicyDTO.getMilestonesCodeList().isEmpty()) {
          for (NewMilestonesDTO milestones : newPolicyDTO.getMilestonesCodeList()) {
            if (milestones != null && !milestones.getMilestone().trim().isEmpty()) {
              CrpMilestone crpMilestone =
                crpMilestoneManager.getCrpMilestoneByPhase(milestones.getMilestone().trim(), phase.getId());
              if (crpMilestone != null) {
                PolicyMilestone policyMilestone = new PolicyMilestone();
                policyMilestone.setCrpMilestone(crpMilestone);
                policyMilestone.setPhase(phase);
                if (milestones.getPrimary() != null && milestones.getPrimary()) {
                  policyMilestone.setPrimary(milestones.getPrimary());
                } else {
                  policyMilestone.setPrimary(false);
                }
                policyMilestones.add(policyMilestone);
              } else {
                fieldErrors.add(new FieldErrorDTO("createPolicy", "Milestone",
                  milestones.getMilestone() + " is an invalid CrpMilestone identifier"));
              }
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

        // validate Innovations
        if (newPolicyDTO.getInnovationCodeList() != null && newPolicyDTO.getInnovationCodeList().size() > 0) {
          for (String innovation : newPolicyDTO.getInnovationCodeList()) {
            if (innovation != null && !innovation.trim().isEmpty()) {
              if (this.tryParseLong(innovation.trim(), fieldErrors, "createPolicy", "Innovation") != null) {
                ProjectInnovation projectInnovation =
                  projectInnovationManager.getProjectInnovationById(Long.valueOf(innovation));
                if (projectInnovation != null) {
                  ProjectPolicyInnovation projectPolicyInnovation = new ProjectPolicyInnovation();
                  projectPolicyInnovation.setPhase(phase);
                  projectPolicyInnovation.setProjectPolicy(projectPolicy);
                  projectPolicyInnovation.setProjectInnovation(projectInnovation);
                  projectPolicyInnovationsList.add(projectPolicyInnovation);
                } else {
                  fieldErrors.add(new FieldErrorDTO("createPolicy", "Innovation",
                    innovation + " is an invalid innovation identifier"));
                }
              }
            } else {
              fieldErrors
                .add(new FieldErrorDTO("createPolicy", "Innovation", "An Innovation code can not be null nor empty."));
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
        Long id = null;
        if (newPolicyDTO.getCrossCuttingMarkers() != null && newPolicyDTO.getCrossCuttingMarkers().size() > 0) {
          for (NewCrosscuttingMarkersDTO crosscuttingmarker : newPolicyDTO.getCrossCuttingMarkers()) {
            strippedId = StringUtils.stripToNull(crosscuttingmarker.getCrossCuttingmarker());
            id = this.tryParseLong(strippedId, fieldErrors, "createPolicy", "Crosscuttingmarker");
            if (id != null) {
              CgiarCrossCuttingMarker cgiarCrossCuttingMarker =
                cgiarCrossCuttingMarkerManager.getCgiarCrossCuttingMarkerById(id);
              if (cgiarCrossCuttingMarker == null) {
                fieldErrors.add(new FieldErrorDTO("createPolicy", "Crosscuttingmarker",
                  id + " is an invalid Crosscuttingmarker Code"));
              } else {
                strippedId = StringUtils.stripToNull(crosscuttingmarker.getCrossCuttingmarkerScore());
                id = this.tryParseLong(strippedId, fieldErrors, "createPolicy", "CrosscuttingmarkerScore");
                if (id != null) {
                  RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
                    repIndGenderYouthFocusLevelManager.getRepIndGenderYouthFocusLevelById(id);
                  if (repIndGenderYouthFocusLevel == null) {
                    fieldErrors.add(new FieldErrorDTO("createPolicy", "CrosscuttingmarkerScore",
                      id + " is an invalid GenderYouthFocusLevel ISO Code"));
                  } else {
                    ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker =
                      new ProjectPolicyCrossCuttingMarker();
                    projectPolicyCrossCuttingMarker.setCgiarCrossCuttingMarker(cgiarCrossCuttingMarker);
                    projectPolicyCrossCuttingMarker.setRepIndGenderYouthFocusLevel(repIndGenderYouthFocusLevel);
                    projectPolicyCrossCuttingMarker.setPhase(phase);
                    projectPolicyCrossCuttingMarker.setProjectPolicy(projectPolicy);
                    ProjectPolicyCrossCuttingMarkerList.add(projectPolicyCrossCuttingMarker);
                  }
                }
              }
            }
          }
        }

        if (newPolicyDTO.getOwners() != null && newPolicyDTO.getOwners().size() > 0) {
          for (PolicyOwnerTypeDTO policyOwnerTypeDTO : newPolicyDTO.getOwners()) {
            RepIndPolicyType repIndPolicyType =
              repIndPolicyTypeManager.getRepIndPolicyTypeById(policyOwnerTypeDTO.getCode());
            if (repIndPolicyType == null) {
              fieldErrors.add(new FieldErrorDTO("createPolicy", "PolicyOwnerType",
                repIndPolicyType + " is an invalid PolicyOwnerType Code"));
            } else {
              ProjectPolicyOwner projectPolicyOwner = new ProjectPolicyOwner();
              projectPolicyOwner.setPhase(phase);
              projectPolicyOwner.setProjectPolicy(projectPolicy);
              projectPolicyOwner.setRepIndPolicyType(repIndPolicyType);
              projectPolicyOwnerList.add(projectPolicyOwner);
            }
          }
        }
        // can save
        if (fieldErrors.isEmpty()) {
          projectPolicy.setProject(project);
          projectPolicy = projectPolicyManager.saveProjectPolicy(projectPolicy);
          if (projectPolicy != null) {
            policyID = projectPolicy.getId();
            projectPolicyInfo.setProjectPolicy(projectPolicy);
            projectPolicyInfo.setPhase(phase);
            projectPolicyInfo.setYear(newPolicyDTO.getProjectPoliciesInfo().getYear());
            projectPolicyInfo.setTitle(newPolicyDTO.getProjectPoliciesInfo().getTitle());
            projectPolicyInfo.setDescription(newPolicyDTO.getProjectPoliciesInfo().getDescription());
            projectPolicyInfo.setNarrativeEvidence(newPolicyDTO.getProjectPoliciesInfo().getNarrativeEvidence());
            projectPolicyInfo.setHasMilestones(false);
            if (newPolicyDTO.getMilestonesCodeList() != null && newPolicyDTO.getMilestonesCodeList().size() > 0) {
              projectPolicyInfo.setHasMilestones(true);
            }
            projectPolicyInfoManager.saveProjectPolicyInfo(projectPolicyInfo);

            for (ProjectPolicyCrp projectPolicyCrp : projectPolicyCrpList) {
              projectPolicyCrp.setProjectPolicy(projectPolicy);
              projectPolicyCrpManager.saveProjectPolicyCrp(projectPolicyCrp);
            }

            for (ProjectPolicySubIdo projectPolicySubIdo : projectPolicySubIdoList) {
              projectPolicySubIdo.setProjectPolicy(projectPolicy);
              projectPolicySubIdoManager.saveProjectPolicySubIdo(projectPolicySubIdo);
            }

            for (PolicyMilestone policyMilestone : policyMilestones) {
              policyMilestone.setPolicy(projectPolicy);
              policyMilestoneManager.savePolicyMilestone(policyMilestone);
            }

            for (ProjectPolicyGeographicScope projectPolicyGeographicScope : projectPolicyGeographicScopeList) {
              projectPolicyGeographicScope.setProjectPolicy(projectPolicy);
              projectPolicyGeographicScopeManager.saveProjectPolicyGeographicScope(projectPolicyGeographicScope);
            }

            for (ProjectPolicyCountry projectPolicyCountry : projectPolicyCountryList) {
              projectPolicyCountry.setProjectPolicy(projectPolicy);
              projectPolicyCountryManager.saveProjectPolicyCountry(projectPolicyCountry);
            }

            for (ProjectPolicyRegion projectPolicyRegion : projectPolicyRegionList) {
              projectPolicyRegion.setProjectPolicy(projectPolicy);
              projectPolicyRegionManager.saveProjectPolicyRegion(projectPolicyRegion);
            }

            for (ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker : ProjectPolicyCrossCuttingMarkerList) {
              projectPolicyCrossCuttingMarker.setProjectPolicy(projectPolicy);
              projectPolicyCrossCuttingMarkerManager
                .saveProjectPolicyCrossCuttingMarker(projectPolicyCrossCuttingMarker);
            }

            for (ProjectPolicyOwner projectPolicyOwner : projectPolicyOwnerList) {
              projectPolicyOwner.setProjectPolicy(projectPolicy);
              projectPolicyOwnerManager.saveProjectPolicyOwner(projectPolicyOwner);
            }

            for (ProjectPolicyInnovation projectPolicyInnovation : projectPolicyInnovationsList) {
              projectPolicyInnovation.setProjectPolicy(projectPolicy);
              projectPolicyInnovationManager.saveProjectPolicyInnovation(projectPolicyInnovation);
            }
            // verify if was included in synthesis PMU
            LiaisonInstitution liaisonInstitution = this.liaisonInstitutionManager
              .findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
            if (liaisonInstitution != null) {
              boolean existing = true;
              ReportSynthesis reportSynthesis =
                reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
              if (reportSynthesis != null) {
                ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress =
                  reportSynthesis.getReportSynthesisFlagshipProgress();
                if (reportSynthesisFlagshipProgress == null) {
                  reportSynthesisFlagshipProgress = new ReportSynthesisFlagshipProgress();
                  reportSynthesisFlagshipProgress.setReportSynthesis(reportSynthesis);
                  reportSynthesisFlagshipProgress.setCreatedBy(user);
                  existing = false;
                  reportSynthesisFlagshipProgress = reportSynthesisFlagshipProgressManager
                    .saveReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
                }
                final Long policy = policyID;
                ReportSynthesisFlagshipProgressPolicy reportSynthesisFlagshipProgressPolicy =
                  reportSynthesisFlagshipProgress.getReportSynthesisFlagshipProgressPolicies().stream()
                    .filter(c -> c.isActive() && c.getProjectPolicy().getId().longValue() == policy).findFirst()
                    .orElse(null);
                if (reportSynthesisFlagshipProgressPolicy != null && existing) {
                  reportSynthesisFlagshipProgressPolicy = reportSynthesisFlagshipProgressPolicyManager
                    .getReportSynthesisFlagshipProgressPolicyById(reportSynthesisFlagshipProgressPolicy.getId());
                  reportSynthesisFlagshipProgressPolicy.setActive(false);
                  reportSynthesisFlagshipProgressPolicy = reportSynthesisFlagshipProgressPolicyManager
                    .saveReportSynthesisFlagshipProgressPolicy(reportSynthesisFlagshipProgressPolicy);
                } else {
                  reportSynthesisFlagshipProgressPolicy = new ReportSynthesisFlagshipProgressPolicy();
                  reportSynthesisFlagshipProgressPolicy.setCreatedBy(user);
                  reportSynthesisFlagshipProgressPolicy.setProjectPolicy(projectPolicy);
                  reportSynthesisFlagshipProgressPolicy
                    .setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);

                  reportSynthesisFlagshipProgressPolicy = reportSynthesisFlagshipProgressPolicyManager
                    .saveReportSynthesisFlagshipProgressPolicy(reportSynthesisFlagshipProgressPolicy);
                  reportSynthesisFlagshipProgressPolicy.setActive(false);
                  reportSynthesisFlagshipProgressPolicyManager
                    .saveReportSynthesisFlagshipProgressPolicy(reportSynthesisFlagshipProgressPolicy);
                }
              }
            }

          }
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("createPolicy", "projectPolicyInfo", "policy info is need it"));
    }

    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      fieldErrors.stream().forEach(f -> System.out.println(f.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return policyID;
  }

  /**
   * Delete an Policy by Id,Phase and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a ProjectPolicyDTO with the policy Item
   */
  public ResponseEntity<ProjectPolicyDTO> deletePolicyById(Long id, String CGIARentityAcronym, Integer repoYear,
    String repoPhase, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().get();
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createPolicy", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }

    ProjectPolicy projectPolicy = projectPolicyManager.getProjectPolicyById(id.longValue());
    if (projectPolicy != null) {
      if (projectPolicy.getProjectPolicyInfo() != null) {
        projectPolicy.getProjectPolicyInfo(phase);
        projectPolicy = this.getPolicyInfoPhase(projectPolicy, phase);
      }

      projectPolicyManager.deleteProjectPolicy(id);
    } else {
      fieldErrors.add(new FieldErrorDTO("deletePolicy", "Policies", id + " is an invalid policy Code"));
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

  public List<ProjectPolicyARDTO> findAllPoliciesByGlobalUnit(String CGIARentityAcronym, Integer repoYear,
    String repoPhase, User user) {
    List<ProjectPolicyARDTO> policyList = new ArrayList<ProjectPolicyARDTO>();
    List<ProjectPolicy> projectPolicyList = new ArrayList<ProjectPolicy>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createInnovation", "GlobalUnitEntity",
        CGIARentityAcronym + " is an invalid CGIAR entity acronym"));
    }

    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() >= APConstants.CLARISA_AVALIABLE_INFO_YEAR && c.getYear() == repoYear
        && c.getName().equalsIgnoreCase(repoPhase))
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createPolicy", "phase", repoYear + " is an invalid year"));
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    } else {
      List<ProjectPolicyInfo> projectPolicyInfoList = phase.getProjectPolicyInfos().stream()
        .filter(
          c -> c.isActive() && c.getYear().longValue() == phase.getYear() && c.getPhase().getId().equals(phase.getId()))
        .collect(Collectors.toList());
      for (ProjectPolicyInfo projectPolicyInfo : projectPolicyInfoList) {
        ProjectPolicy projectPolicy =
          projectPolicyManager.getProjectPolicyById(projectPolicyInfo.getProjectPolicy().getId());

        if (projectPolicy.isActive() && projectPolicyManager.isPolicyExcluded(projectPolicy.getId(), phase.getId())) {
          projectPolicy.setProjectPolicyInfo(projectPolicyInfo);
          projectPolicy = this.getPolicyInfoPhase(projectPolicy, phase);
          projectPolicyList.add(projectPolicy);
        }
      }

    }

    policyList = projectPolicyList.stream().map(this.projectPolicyMapper::projectPolicyToProjectPolicyARDTO)
      .collect(Collectors.toList());
    return policyList;
  }

  public ResponseEntity<ProjectPolicyDTO> findPolicyById(Long id, String CGIARentityAcronym, Integer repoYear,
    String repoPhase, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    ProjectPolicy projectPolicy = projectPolicyManager.getProjectPolicyById(id.longValue());

    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() >= APConstants.CLARISA_AVALIABLE_INFO_YEAR && c.getYear() == repoYear
        && c.getName().equalsIgnoreCase(repoPhase))
      .findFirst().orElse(null);

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
    } else {

      if (projectPolicy == null || projectPolicy.getProjectPolicyInfo(phase) == null) {
        fieldErrors.add(new FieldErrorDTO("findPolicy", "InnovationId", id + " is an invalid id of an policy"));
      }
    }

    if (projectPolicy.getProjectPolicyInfo() != null) {
      projectPolicy.getProjectPolicyInfo(phase);
      projectPolicy = this.getPolicyInfoPhase(projectPolicy, phase);
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

  public ProjectPolicy getPolicyInfoPhase(ProjectPolicy projectPolicy, Phase phase) {
    if (projectPolicy != null) {
      final long projectPolicyID = projectPolicy.getId().longValue();
      projectPolicy.getProjectPolicyInfo(phase);

      // Setting Geographic Scope
      projectPolicy.setGeographicScopes(projectPolicyGeographicScopeManager.findAll().stream()
        .filter(
          c -> c.getProjectPolicy().getId().longValue() == projectPolicyID && c.getPhase().getId() == phase.getId())
        .collect(Collectors.toList()));
      // Setting CRP contributing
      projectPolicy.setCrps(projectPolicyCrpManager.findAll().stream()
        .filter(
          c -> c.getProjectPolicy().getId().longValue() == projectPolicyID && c.getPhase().getId() == phase.getId())
        .collect(Collectors.toList()));
      // Setting CrossCuttingMarker
      projectPolicy.setCrossCuttingMarkers(projectPolicyCrossCuttingMarkerManager.findAll().stream()
        .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
          && c.getPhase().getId().longValue() == phase.getId().longValue())
        .collect(Collectors.toList()));
      // Setting SubIdos
      projectPolicy.setSubIdos(projectPolicySubIdoManager.findAll().stream()
        .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
          && c.getPhase().getId().longValue() == phase.getId().longValue())
        .collect(Collectors.toList()));
      // Setting Milestones
      projectPolicy.setMilestones(policyMilestoneManager.findAll().stream()
        .filter(c -> c.getPolicy().getId().longValue() == projectPolicyID && c.getPhase().getId() == phase.getId())
        .collect(Collectors.toList()));
      System.out.println(policyMilestoneManager.findAll().stream()
        .filter(c -> c.getPolicy().getId().longValue() == projectPolicyID && c.getPhase().getId() == phase.getId())
        .collect(Collectors.toList()));
      // setting countries
      projectPolicy.setCountries(projectPolicyCountryManager.findAll().stream()
        .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
          && c.getPhase().getId().longValue() == phase.getId().longValue())
        .collect(Collectors.toList()));
      // setting regions
      projectPolicy.setRegions(projectPolicyRegionManager.findAll().stream()
        .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
          && c.getPhase().getId().longValue() == phase.getId().longValue())
        .collect(Collectors.toList()));
      // setting owners
      projectPolicy.setOwners(projectPolicyOwnerManager.findAll().stream()
        .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
          && c.getPhase().getId().longValue() == phase.getId().longValue())
        .collect(Collectors.toList()));
      // setting innvoations

      List<ProjectPolicyInnovation> projectPolicyInnovationList = new ArrayList<ProjectPolicyInnovation>();
      for (ProjectPolicyInnovation projectPolicyInnovation : projectPolicyInnovationManager.findAll().stream()
        .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
          && c.getPhase().getId().longValue() == phase.getId().longValue())
        .collect(Collectors.toList())) {
        ProjectInnovation projectInnovation =
          projectInnovationManager.getProjectInnovationById(projectPolicyInnovation.getProjectInnovation().getId());
        ProjectInnovationInfo projectInnovationInfo = projectInnovation.getProjectInnovationInfo(phase);
        projectInnovation.setProjectInnovationInfo(projectInnovationInfo);
        projectPolicyInnovation.setProjectInnovation(projectInnovation);
        projectPolicyInnovationList.add(projectPolicyInnovation);
      }
      projectPolicy.setInnovations(projectPolicyInnovationList);
      // setting OCIRs
      List<ProjectExpectedStudyPolicy> projectExpectedStudyPolicyList = new ArrayList<ProjectExpectedStudyPolicy>();
      for (ProjectExpectedStudyPolicy projectExpectedStudyPolicy : projectExpectedStudyPolicyManager.findAll().stream()
        .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
          && c.getPhase().getId().longValue() == phase.getId().longValue())
        .collect(Collectors.toList())) {
        ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyManager
          .getProjectExpectedStudyById(projectExpectedStudyPolicy.getProjectExpectedStudy().getId());
        ProjectExpectedStudyInfo info = projectExpectedStudy.getProjectExpectedStudyInfo(phase);
        projectExpectedStudy.setProjectExpectedStudyInfo(info);
        projectExpectedStudyPolicy.setProjectExpectedStudy(projectExpectedStudy);
        projectExpectedStudyPolicyList.add(projectExpectedStudyPolicy);
      }
      projectPolicy.setEvidences(projectExpectedStudyPolicyList);
    }
    return projectPolicy;
  }

  /**
   * Update an Policy by Id and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a NewProjectPolicyDTO with the policy Item
   */

  public Long putPolicyById(Long id, NewProjectPolicyDTO newPolicyDTO, String entityAcronym, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    List<ProjectPolicyCrp> projectPolicyCrpList = new ArrayList<ProjectPolicyCrp>();
    List<ProjectPolicySubIdo> projectPolicySubIdoList = new ArrayList<ProjectPolicySubIdo>();
    List<ProjectPolicyGeographicScope> projectPolicyGeographicScopeList = new ArrayList<ProjectPolicyGeographicScope>();
    List<ProjectPolicyCountry> projectPolicyCountryList = new ArrayList<ProjectPolicyCountry>();
    List<ProjectPolicyRegion> projectPolicyRegionList = new ArrayList<ProjectPolicyRegion>();
    List<ProjectPolicyCrossCuttingMarker> ProjectPolicyCrossCuttingMarkerList =
      new ArrayList<ProjectPolicyCrossCuttingMarker>();
    List<ProjectPolicyInnovation> projectPolicyInnovationList = new ArrayList<ProjectPolicyInnovation>();
    List<PolicyMilestone> policyMilestones = new ArrayList<>();
    List<ProjectPolicyOwner> projectPolicyOwnerList = new ArrayList<ProjectPolicyOwner>();
    String strippedId = null;
    Long policyID = null;
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
      fieldErrors.add(new FieldErrorDTO("updatePolicy", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }

    Project project = null;
    if (newPolicyDTO.getProject() != null) {
      project = projectManager.getProjectById(newPolicyDTO.getProject());
      if (project == null) {
        fieldErrors.add(new FieldErrorDTO("updatePolicy", "Project", newPolicyDTO.getProject() + " is an project ID"));
      } else {
        GlobalUnitProject crpProject =
          globalUnitProjectManager.findByProjectAndGlobalUnitId(newPolicyDTO.getProject(), globalUnitEntity.getId());
        if (crpProject == null) {
          fieldErrors
            .add(new FieldErrorDTO("updatePolicy", "Project", newPolicyDTO.getProject() + " is an invalid project ID"));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("updatePolicy", "Project", "A projectID can not be null"));
    }

    ProjectPolicy projectPolicy = null;
    if (id != null) {
      projectPolicy = projectPolicyManager.getProjectPolicyById(id.longValue());
      if (projectPolicy == null) {
        fieldErrors.add(new FieldErrorDTO("updatePolicy", "policy", id.longValue() + " does not exists"));
      } else {
        if (projectPolicy.getProject().getId().longValue() != newPolicyDTO.getProject()) {
          fieldErrors
            .add(new FieldErrorDTO("updatePolicy", "Project", newPolicyDTO.getProject() + " is an invalid project ID"));
        }
      }
    }

    if (phase != null && !phase.getEditable()) {
      fieldErrors.add(new FieldErrorDTO("updatePolicy", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is closed phase"));
    }
    if (fieldErrors.size() == 0) {

      ProjectPolicyInfo projectPolicyInfo = null;
      if (projectPolicy.getProjectPolicyInfo(phase) != null) {
        projectPolicyInfo =
          projectPolicyInfoManager.getProjectPolicyInfoById(projectPolicy.getProjectPolicyInfo(phase).getId());
        projectPolicyInfo.setProjectPolicy(projectPolicy);
        projectPolicyInfo.setPhase(phase);
        projectPolicyInfo.setYear(newPolicyDTO.getProjectPoliciesInfo().getYear());
        projectPolicyInfo.setTitle(newPolicyDTO.getProjectPoliciesInfo().getTitle());
        projectPolicyInfo.setDescription(newPolicyDTO.getProjectPoliciesInfo().getDescription());
        projectPolicyInfo.setNarrativeEvidence(newPolicyDTO.getProjectPoliciesInfo().getNarrativeEvidence());
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
            if (newPolicyDTO.getProjectPoliciesInfo().getRepIndPolicyInvestimentType().getCode().longValue() == 3) {
              if (newPolicyDTO.getProjectPoliciesInfo().getBudgetAmount() == null) {
                fieldErrors.add(new FieldErrorDTO("createPolicy", "budgetAmount", " budget amount is required"));
              } else if (newPolicyDTO.getProjectPoliciesInfo().getBudgetAmount() <= 0) {
                fieldErrors
                  .add(new FieldErrorDTO("createPolicy", "budgetAmount", " budget amount needs to be more than zero"));
              } else {
                projectPolicyInfo.setAmount(newPolicyDTO.getProjectPoliciesInfo().getBudgetAmount());
              }
            } else {
              projectPolicyInfo.setAmount(null);
            }
            projectPolicyInfo.setRepIndPolicyInvestimentType(repIndPolicyInvestimentType);
          }
        } else {
          fieldErrors.add(
            new FieldErrorDTO("createPolicy", "repIndPolicyInvestimentType", "policy investiment type is need it"));
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
        projectPolicy.setProjectPolicyInfo(projectPolicyInfo);

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
              projectPolicyCrpList.add(projectPolicyCrp);
            }
          }
        }
        // validate sub-IDO
        if (newPolicyDTO.getSrfSubIdoList() != null && newPolicyDTO.getSrfSubIdoList().size() > 0) {
          for (NewSrfSubIdoDTO subIdos : newPolicyDTO.getSrfSubIdoList()) {
            SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoByCode(subIdos.getSubIdo());
            if (srfSubIdo == null) {
              fieldErrors.add(
                new FieldErrorDTO("createPolicy", "SrfSubIdo", subIdos.getSubIdo() + " is an invalid Sub-IDO code"));
            } else {
              ProjectPolicySubIdo projectPolicySubIdo = new ProjectPolicySubIdo();
              projectPolicySubIdo.setProjectPolicy(projectPolicy);
              projectPolicySubIdo.setPhase(phase);
              projectPolicySubIdo.setSrfSubIdo(srfSubIdo);
              if (subIdos.getPrimary() != null && subIdos.getPrimary()) {
                projectPolicySubIdo.setPrimary(subIdos.getPrimary());
              } else {
                projectPolicySubIdo.setPrimary(false);
              }
              projectPolicySubIdoList.add(projectPolicySubIdo);
            }
          }
        }
        // validate milestones
        if (newPolicyDTO.getMilestonesCodeList() != null && !newPolicyDTO.getMilestonesCodeList().isEmpty()) {
          for (NewMilestonesDTO milestones : newPolicyDTO.getMilestonesCodeList()) {
            if (milestones != null && !milestones.getMilestone().trim().isEmpty()) {
              CrpMilestone crpMilestone =
                crpMilestoneManager.getCrpMilestoneByPhase(milestones.getMilestone().trim(), phase.getId());
              if (crpMilestone != null) {
                PolicyMilestone policyMilestone = new PolicyMilestone();
                policyMilestone.setCrpMilestone(crpMilestone);
                policyMilestone.setPhase(phase);
                if (milestones.getPrimary() != null && milestones.getPrimary()) {
                  policyMilestone.setPrimary(milestones.getPrimary());
                } else {
                  policyMilestone.setPrimary(false);
                }
                policyMilestones.add(policyMilestone);
              } else {
                fieldErrors.add(new FieldErrorDTO("createPolicy", "Milestone",
                  milestones.getMilestone() + " is an invalid CrpMilestone identifier"));
              }
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

        // validate Innovations
        if (newPolicyDTO.getInnovationCodeList() != null && newPolicyDTO.getInnovationCodeList().size() > 0) {
          for (String innovation : newPolicyDTO.getInnovationCodeList()) {
            if (innovation != null && !innovation.trim().isEmpty()) {
              if (this.tryParseLong(innovation.trim(), fieldErrors, "createPolicy", "Innovation") != null) {
                ProjectInnovation projectInnovation =
                  projectInnovationManager.getProjectInnovationById(Long.valueOf(innovation));
                if (projectInnovation != null) {
                  ProjectPolicyInnovation projectPolicyInnovation = new ProjectPolicyInnovation();
                  projectPolicyInnovation.setPhase(phase);
                  projectPolicyInnovation.setProjectPolicy(projectPolicy);
                  projectPolicyInnovation.setProjectInnovation(projectInnovation);
                  projectPolicyInnovationList.add(projectPolicyInnovation);
                } else {
                  fieldErrors.add(new FieldErrorDTO("createPolicy", "Innovation",
                    innovation + " is an invalid innovation identifier"));
                }
              }
            } else {
              fieldErrors
                .add(new FieldErrorDTO("createPolicy", "Innovation", "An Innovation code can not be null nor empty."));
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
        Long idCross = null;
        if (newPolicyDTO.getCrossCuttingMarkers() != null && newPolicyDTO.getCrossCuttingMarkers().size() > 0) {
          for (NewCrosscuttingMarkersDTO crosscuttingmarker : newPolicyDTO.getCrossCuttingMarkers()) {
            strippedId = StringUtils.stripToNull(crosscuttingmarker.getCrossCuttingmarker());
            idCross = this.tryParseLong(strippedId, fieldErrors, "createPolicy", "Crosscuttingmarker");
            if (idCross != null) {
              CgiarCrossCuttingMarker cgiarCrossCuttingMarker =
                cgiarCrossCuttingMarkerManager.getCgiarCrossCuttingMarkerById(idCross);
              if (cgiarCrossCuttingMarker == null) {
                fieldErrors.add(new FieldErrorDTO("createPolicy", "Crosscuttingmarker",
                  idCross + " is an invalid Crosscuttingmarker Code"));
              } else {
                strippedId = StringUtils.stripToNull(crosscuttingmarker.getCrossCuttingmarkerScore());
                idCross = this.tryParseLong(strippedId, fieldErrors, "createPolicy", "CrosscuttingmarkerScore");
                if (idCross != null) {
                  RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
                    repIndGenderYouthFocusLevelManager.getRepIndGenderYouthFocusLevelById(idCross);
                  if (repIndGenderYouthFocusLevel == null) {
                    fieldErrors.add(new FieldErrorDTO("createPolicy", "CrosscuttingmarkerScore",
                      idCross + " is an invalid GenderYouthFocusLevel ISO Code"));
                  } else {
                    ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker =
                      new ProjectPolicyCrossCuttingMarker();
                    projectPolicyCrossCuttingMarker.setCgiarCrossCuttingMarker(cgiarCrossCuttingMarker);
                    projectPolicyCrossCuttingMarker.setRepIndGenderYouthFocusLevel(repIndGenderYouthFocusLevel);
                    projectPolicyCrossCuttingMarker.setPhase(phase);
                    projectPolicyCrossCuttingMarker.setProjectPolicy(projectPolicy);
                    ProjectPolicyCrossCuttingMarkerList.add(projectPolicyCrossCuttingMarker);
                  }
                }
              }
            }
          }
        }

        if (newPolicyDTO.getOwners() != null && newPolicyDTO.getOwners().size() > 0) {
          for (PolicyOwnerTypeDTO policyOwnerTypeDTO : newPolicyDTO.getOwners()) {
            RepIndPolicyType repIndPolicyType =
              repIndPolicyTypeManager.getRepIndPolicyTypeById(policyOwnerTypeDTO.getCode());
            if (repIndPolicyType == null) {
              fieldErrors.add(new FieldErrorDTO("createPolicy", "PolicyOwnerType",
                repIndPolicyType + " is an invalid PolicyOwnerType Code"));
            } else {
              ProjectPolicyOwner projectPolicyOwner = new ProjectPolicyOwner();
              projectPolicyOwner.setPhase(phase);
              projectPolicyOwner.setProjectPolicy(projectPolicy);
              projectPolicyOwner.setRepIndPolicyType(repIndPolicyType);
              projectPolicyOwnerList.add(projectPolicyOwner);
            }
          }
        }
      }
      // can update
      if (fieldErrors.isEmpty()) {
        final long projectPolicyID = id;
        projectPolicyInfo.setHasMilestones(false);
        if (newPolicyDTO.getMilestonesCodeList() != null && newPolicyDTO.getMilestonesCodeList().size() > 0) {
          projectPolicyInfo.setHasMilestones(true);
        }
        projectPolicyInfoManager.saveProjectPolicyInfo(projectPolicyInfo);
        // *************Policy CRP*****************/
        // getting saved projectPolicyCRPList
        List<ProjectPolicyCrp> projectPolicyCrpListDB = projectPolicyCrpManager.findAll().stream()
          .filter(
            c -> c.getProjectPolicy().getId().longValue() == projectPolicyID && c.getPhase().getId() == phase.getId())
          .collect(Collectors.toList());
        // create an array to save existing data
        List<ProjectPolicyCrp> existingProjectPolicyList = new ArrayList<ProjectPolicyCrp>();
        // search if a policycrp was created
        for (ProjectPolicyCrp projectPolicyCrp : projectPolicyCrpList) {
          projectPolicyCrp.setProjectPolicy(projectPolicy);
          ProjectPolicyCrp temp =
            projectPolicyCrpManager.getProjectPolicyCrpByPhase(projectPolicyCrp.getProjectPolicy().getId().longValue(),
              projectPolicyCrp.getGlobalUnit().getId().longValue(), projectPolicyCrp.getPhase().getId().longValue());
          if (temp != null) {
            existingProjectPolicyList.add(temp);
          } else {
            projectPolicyCrpManager.saveProjectPolicyCrp(projectPolicyCrp);
          }
        }
        // verify deleted policycrps
        for (ProjectPolicyCrp obj : projectPolicyCrpListDB) {
          if (!existingProjectPolicyList.contains(obj)) {
            if (!obj.getGlobalUnit().getAcronym().equals(globalUnitEntity.getAcronym())) {
              projectPolicyCrpManager.deleteProjectPolicyCrp(obj.getId());
            }
          }
        }

        // *************Policy SubIdo*****************/
        List<ProjectPolicySubIdo> projectPolicySubIdoListDB = projectPolicySubIdoManager.findAll().stream()
          .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
            && c.getPhase().getId().longValue() == phase.getId().longValue())
          .collect(Collectors.toList());
        List<ProjectPolicySubIdo> existingProjectPolicySubIdoList = new ArrayList<ProjectPolicySubIdo>();
        for (ProjectPolicySubIdo projectPolicySubIdo : projectPolicySubIdoList) {
          projectPolicySubIdo.setProjectPolicy(projectPolicy);
          ProjectPolicySubIdo temp = projectPolicySubIdoManager.getProjectPolicySubIdoByPhase(
            projectPolicySubIdo.getProjectPolicy().getId().longValue(),
            projectPolicySubIdo.getSrfSubIdo().getId().longValue(), projectPolicySubIdo.getPhase().getId().longValue());
          if (temp != null) {
            temp.setPrimary(projectPolicySubIdo.getPrimary());
            existingProjectPolicySubIdoList.add(temp);
            projectPolicySubIdoManager.saveProjectPolicySubIdo(temp);
          } else {
            projectPolicySubIdoManager.saveProjectPolicySubIdo(projectPolicySubIdo);
          }
        }
        // verify deleted policy_subIdos
        for (ProjectPolicySubIdo obj : projectPolicySubIdoListDB) {
          if (!existingProjectPolicySubIdoList.contains(obj)) {
            projectPolicySubIdoManager.deleteProjectPolicySubIdo(obj.getId());
          }
        }

        // *************Policy Milestone*****************/
        List<PolicyMilestone> policyMilestoneDB =
          policyMilestoneManager.findAll().stream().filter(c -> c.getPolicy().getId().longValue() == projectPolicyID
            && c.getPhase().getId().longValue() == phase.getId().longValue()).collect(Collectors.toList());
        List<PolicyMilestone> existingPolicyMilestonesList = new ArrayList<>();
        for (PolicyMilestone policyMilestone : policyMilestones) {
          policyMilestone.setPolicy(projectPolicy);

          PolicyMilestone temp =
            policyMilestoneManager.findByCrpMilestonePolicyAndPhase(policyMilestone.getCrpMilestone().getId(),
              policyMilestone.getPolicy().getId(), policyMilestone.getPhase().getId());
          if (temp != null) {
            temp.setPrimary(policyMilestone.getPrimary());
            existingPolicyMilestonesList.add(temp);
            policyMilestoneManager.savePolicyMilestone(temp);
          } else {
            policyMilestoneManager.savePolicyMilestone(policyMilestone);
          }
        }
        // verify deleted policy_milestones
        for (PolicyMilestone obj : policyMilestoneDB) {
          if (!existingPolicyMilestonesList.contains(obj)) {
            policyMilestoneManager.deletePolicyMilestone(obj.getId());
          }
        }

        // *************Policy GeographicScope*****************/
        List<ProjectPolicyGeographicScope> projectPolicyGeographicScopeListDB = projectPolicyGeographicScopeManager
          .findAll().stream().filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
            && c.getPhase().getId().longValue() == phase.getId().longValue())
          .collect(Collectors.toList());
        List<ProjectPolicyGeographicScope> existingProjectPolicyGeographicScopeList =
          new ArrayList<ProjectPolicyGeographicScope>();
        for (ProjectPolicyGeographicScope projectPolicyGeographicScope : projectPolicyGeographicScopeList) {
          projectPolicyGeographicScope.setProjectPolicy(projectPolicy);
          ProjectPolicyGeographicScope temp = projectPolicyGeographicScopeManager
            .getProjectPolicyGeographicScopeByPhase(projectPolicyGeographicScope.getProjectPolicy().getId().longValue(),
              projectPolicyGeographicScope.getRepIndGeographicScope().getId().longValue(),
              projectPolicyGeographicScope.getPhase().getId().longValue());
          if (temp != null) {
            existingProjectPolicyGeographicScopeList.add(temp);
          } else {
            projectPolicyGeographicScopeManager.saveProjectPolicyGeographicScope(projectPolicyGeographicScope);
          }
        }
        // verify deleted GeographicScope
        for (ProjectPolicyGeographicScope obj : projectPolicyGeographicScopeListDB) {
          if (!existingProjectPolicyGeographicScopeList.contains(obj)) {
            projectPolicyGeographicScopeManager.deleteProjectPolicyGeographicScope(obj.getId());
          }
        }

        // *************Policy Innovations*****************/
        List<ProjectPolicyInnovation> projectPolicyInnovationListDB = projectPolicyInnovationManager.findAll().stream()
          .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
            && c.getPhase().getId().longValue() == phase.getId().longValue())
          .collect(Collectors.toList());
        List<ProjectPolicyInnovation> existingProjectPolicyInnovationList = new ArrayList<ProjectPolicyInnovation>();
        for (ProjectPolicyInnovation projectPolicyInnovation : projectPolicyInnovationList) {
          projectPolicyInnovation.setProjectPolicy(projectPolicy);
          ProjectPolicyInnovation temp = projectPolicyInnovationManager.getProjectPolicyInnovationByPhase(
            projectPolicyInnovation.getProjectPolicy().getId().longValue(),
            projectPolicyInnovation.getProjectInnovation().getId().longValue(),
            projectPolicyInnovation.getPhase().getId().longValue());
          if (temp != null) {
            existingProjectPolicyInnovationList.add(temp);
          } else {
            projectPolicyInnovationManager.saveProjectPolicyInnovation(projectPolicyInnovation);
          }
        }
        // verify deleted innovations
        for (ProjectPolicyInnovation obj : projectPolicyInnovationListDB) {
          if (!existingProjectPolicyInnovationList.contains(obj)) {
            projectPolicyInnovationManager.deleteProjectPolicyInnovation(obj.getId());
          }
        }
        // *************Policy Countries*****************/
        List<ProjectPolicyCountry> projectPolicyCountryListDB = projectPolicyCountryManager.findAll().stream()
          .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
            && c.getPhase().getId().longValue() == phase.getId().longValue())
          .collect(Collectors.toList());
        List<ProjectPolicyCountry> existingProjectPolicyCountryList = new ArrayList<ProjectPolicyCountry>();
        for (ProjectPolicyCountry projectPolicyCountry : projectPolicyCountryList) {
          projectPolicyCountry.setProjectPolicy(projectPolicy);
          ProjectPolicyCountry temp = projectPolicyCountryManager.getProjectPolicyCountryByPhase(
            projectPolicyCountry.getProjectPolicy().getId().longValue(),
            projectPolicyCountry.getLocElement().getId().longValue(),
            projectPolicyCountry.getPhase().getId().longValue());
          if (temp != null) {
            existingProjectPolicyCountryList.add(temp);
          } else {
            projectPolicyCountryManager.saveProjectPolicyCountry(projectPolicyCountry);
          }
        }
        // verify deleted ProjectPolicyCountry
        for (ProjectPolicyCountry obj : projectPolicyCountryListDB) {
          if (!existingProjectPolicyCountryList.contains(obj)) {
            projectPolicyCountryManager.deleteProjectPolicyCountry(obj.getId());
          }
        }

        // *************Policy Regions*****************/
        List<ProjectPolicyRegion> projectPolicyRegionListDB = projectPolicy.getProjectPolicyRegions().stream()
          .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
            && c.getPhase().getId().longValue() == phase.getId().longValue())
          .collect(Collectors.toList());
        List<ProjectPolicyRegion> existingProjectPolicyRegionList = new ArrayList<ProjectPolicyRegion>();
        for (ProjectPolicyRegion projectPolicyRegion : projectPolicyRegionList) {
          projectPolicyRegion.setProjectPolicy(projectPolicy);
          ProjectPolicyRegion temp = projectPolicyRegionManager.getProjectPolicyRegionByPhase(
            projectPolicyRegion.getProjectPolicy().getId().longValue(),
            projectPolicyRegion.getLocElement().getId().longValue(),
            projectPolicyRegion.getPhase().getId().longValue());
          if (temp != null) {
            existingProjectPolicyRegionList.add(temp);
          } else {
            projectPolicyRegionManager.saveProjectPolicyRegion(projectPolicyRegion);
          }
        }
        // verify deleted ProjectPolicyregion
        for (ProjectPolicyRegion obj : projectPolicyRegionListDB) {
          if (!existingProjectPolicyRegionList.contains(obj)) {
            projectPolicyRegionManager.deleteProjectPolicyRegion(obj.getId());
          }
        }

        // *************Policy Crosscutingmarkers*****************/
        List<ProjectPolicyCrossCuttingMarker> projectPolicyCrossCuttingMarkerListDB =
          projectPolicy.getProjectPolicyCrossCuttingMarkers().stream()
            .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
              && c.getPhase().getId().longValue() == phase.getId().longValue())
            .collect(Collectors.toList());
        List<ProjectPolicyCrossCuttingMarker> existingProjectPolicyCrossCuttingMarkerList =
          new ArrayList<ProjectPolicyCrossCuttingMarker>();
        for (ProjectPolicyCrossCuttingMarker projectPolicyCrossCuttingMarker : ProjectPolicyCrossCuttingMarkerList) {
          projectPolicyCrossCuttingMarker.setProjectPolicy(projectPolicy);
          ProjectPolicyCrossCuttingMarker temp = projectPolicyCrossCuttingMarkerManager.getPolicyCrossCountryMarkerId(
            projectPolicyCrossCuttingMarker.getProjectPolicy().getId().longValue(),
            projectPolicyCrossCuttingMarker.getCgiarCrossCuttingMarker().getId().longValue(),
            projectPolicyCrossCuttingMarker.getPhase().getId().longValue());
          if (temp != null) {
            temp.setRepIndGenderYouthFocusLevel(projectPolicyCrossCuttingMarker.getRepIndGenderYouthFocusLevel());
            projectPolicyCrossCuttingMarkerManager.saveProjectPolicyCrossCuttingMarker(temp);
            existingProjectPolicyCrossCuttingMarkerList.add(temp);
          } else {
            projectPolicyCrossCuttingMarkerManager.saveProjectPolicyCrossCuttingMarker(projectPolicyCrossCuttingMarker);
          }
        }
        // verify deleted ProjectPolicyCrossCuttingMarker
        for (ProjectPolicyCrossCuttingMarker obj : projectPolicyCrossCuttingMarkerListDB) {
          if (!existingProjectPolicyCrossCuttingMarkerList.contains(obj)) {
            projectPolicyCrossCuttingMarkerManager.deleteProjectPolicyCrossCuttingMarker(obj.getId());
          }
        }

        // *************Policy owners*****************/
        List<ProjectPolicyOwner> projectProjectPolicyOwnerListDB = projectPolicyOwnerManager.findAll().stream()
          .filter(c -> c.getProjectPolicy().getId().longValue() == projectPolicyID
            && c.getPhase().getId().longValue() == phase.getId().longValue())
          .collect(Collectors.toList());
        List<ProjectPolicyOwner> existingProjectPolicyOwnerList = new ArrayList<ProjectPolicyOwner>();
        for (ProjectPolicyOwner projectPolicyOwner : projectPolicyOwnerList) {
          projectPolicyOwner.setProjectPolicy(projectPolicy);
          ProjectPolicyOwner temp = projectPolicyOwnerManager.getProjectPolicyOwnerById(
            projectPolicyOwner.getProjectPolicy().getId().longValue(),
            projectPolicyOwner.getRepIndPolicyType().getId().longValue(),
            projectPolicyOwner.getPhase().getId().longValue());
          if (temp != null) {
            existingProjectPolicyOwnerList.add(temp);
          } else {
            projectPolicyOwnerManager.saveProjectPolicyOwner(projectPolicyOwner);
          }
        }
        // verify deleted ProjectPolicyCrossCuttingMarker
        for (ProjectPolicyOwner obj : projectProjectPolicyOwnerListDB) {
          if (!existingProjectPolicyOwnerList.contains(obj)) {
            projectPolicyOwnerManager.deleteProjectPolicyOwner(obj.getId());
          }
        }
        projectPolicy = null;
        projectPolicy = projectPolicyManager.getProjectPolicyById(id.longValue());
        projectPolicy = this.getPolicyInfoPhase(projectPolicy, phase);


        // verify if was included in synthesis PMU
        LiaisonInstitution liaisonInstitution =
          this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
        if (liaisonInstitution != null) {
          boolean existing = true;
          ReportSynthesis reportSynthesis =
            reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
          if (reportSynthesis != null) {
            ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress =
              reportSynthesis.getReportSynthesisFlagshipProgress();
            if (reportSynthesisFlagshipProgress == null) {
              reportSynthesisFlagshipProgress = new ReportSynthesisFlagshipProgress();
              reportSynthesisFlagshipProgress.setReportSynthesis(reportSynthesis);
              reportSynthesisFlagshipProgress.setCreatedBy(user);
              existing = false;
              reportSynthesisFlagshipProgress = reportSynthesisFlagshipProgressManager
                .saveReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
            }
            final Long policy = projectPolicy.getId();
            ReportSynthesisFlagshipProgressPolicy reportSynthesisFlagshipProgressPolicy =
              reportSynthesisFlagshipProgress.getReportSynthesisFlagshipProgressPolicies().stream()
                .filter(c -> c.isActive() && c.getProjectPolicy().getId().longValue() == policy).findFirst()
                .orElse(null);
            if (reportSynthesisFlagshipProgressPolicy != null && existing) {
              reportSynthesisFlagshipProgressPolicy = reportSynthesisFlagshipProgressPolicyManager
                .getReportSynthesisFlagshipProgressPolicyById(reportSynthesisFlagshipProgressPolicy.getId());
              reportSynthesisFlagshipProgressPolicy.setActive(false);
              reportSynthesisFlagshipProgressPolicy = reportSynthesisFlagshipProgressPolicyManager
                .saveReportSynthesisFlagshipProgressPolicy(reportSynthesisFlagshipProgressPolicy);
            } else {
              reportSynthesisFlagshipProgressPolicy = new ReportSynthesisFlagshipProgressPolicy();
              reportSynthesisFlagshipProgressPolicy.setCreatedBy(user);
              reportSynthesisFlagshipProgressPolicy.setProjectPolicy(projectPolicy);
              reportSynthesisFlagshipProgressPolicy.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);

              reportSynthesisFlagshipProgressPolicy = reportSynthesisFlagshipProgressPolicyManager
                .saveReportSynthesisFlagshipProgressPolicy(reportSynthesisFlagshipProgressPolicy);
              reportSynthesisFlagshipProgressPolicy.setActive(false);
              reportSynthesisFlagshipProgressPolicyManager
                .saveReportSynthesisFlagshipProgressPolicy(reportSynthesisFlagshipProgressPolicy);
            }
          }
        }
      }
    }


    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    } else {
      policyID = projectPolicy.getId();
    }
    return policyID;
  }

  public Long tryParseLong(String value, List<FieldErrorDTO> fieldErrors, String httpMethod, String field) {
    Long result = null;
    try {
      result = Long.parseLong(value);
    } catch (NumberFormatException nfe) {
      fieldErrors
        .add(new FieldErrorDTO(httpMethod, field, value + " is an invalid " + field + " numeric identification code"));
    }
    return result;
  }

}
