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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.expectedStudies;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CgiarCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.EvidenceTagManager;
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyFlagshipManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyLinkManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyQuantificationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySrfTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageStudyManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.StudyTypeManager;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.EvidenceTag;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCrp;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyFlagship;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInstitution;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyLink;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyMilestone;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyPolicy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyQuantification;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndStageStudy;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.StudyType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewCrosscuttingMarkersDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewMilestonesDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectExpectedStudyDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewSrfSubIdoDTO;
import org.cgiar.ccafs.marlo.rest.dto.ProjectExpectedStudyDTO;
import org.cgiar.ccafs.marlo.rest.dto.QuantificationDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.ProjectExpectedStudyMapper;

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
public class ExpectedStudiesItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;

  private RepIndStageStudyManager repIndStageStudyManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private SrfSloIndicatorManager srfSloIndicatorManager;
  private SrfSubIdoManager srfSubIdoManager;
  private CrpProgramManager crpProgramManager;
  private LocElementManager locElementManager;
  private ProjectInnovationManager projectInnovationManager;
  private ProjectPolicyManager projectPolicyManager;
  private InstitutionManager institutionManager;
  private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;
  private RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager;
  private EvidenceTagManager evidenceTagManager;
  private GeneralStatusManager generalStatusManager;
  private ProjectManager projectManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private StudyTypeManager studyTypeManager;
  private CrpMilestoneManager crpMilestoneManager;
  private ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;
  private ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager;
  private ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager;
  private ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager;
  private ProjectExpectedStudySrfTargetManager projectExpectedStudySrfTargetManager;
  private ProjectExpectedStudySubIdoManager projectExpectedStudySubIdoManager;
  private ProjectExpectedStudyFlagshipManager projectExpectedStudyFlagshipManager;
  private ProjectExpectedStudyLinkManager projectExpectedStudyLinkManager;
  private ProjectExpectedStudyInstitutionManager projectExpectedStudyInstitutionManager;
  private ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager;
  private ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager;
  private ProjectExpectedStudyCrpManager projectExpectedStudyCrpManager;
  private ProjectExpectedStudyQuantificationManager projectExpectedStudyQuantificationManager;
  private ProjectExpectedStudyMilestoneManager projectExpectedStudyMilestoneManager;
  private ProjectExpectedStudyMapper projectExpectedStudyMapper;


  @Inject
  public ExpectedStudiesItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    RepIndStageStudyManager repIndStageStudyManager, RepIndGeographicScopeManager repIndGeographicScopeManager,
    SrfSloIndicatorManager srfSloIndicatorManager, SrfSubIdoManager srfSubIdoManager,
    CrpProgramManager crpProgramManager, LocElementManager locElementManager,
    ProjectInnovationManager projectInnovationManager, ProjectPolicyManager projectPolicyManager,
    InstitutionManager institutionManager, CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager,
    RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager, EvidenceTagManager evidenceTagManager,
    GeneralStatusManager generalStatusManager, ProjectExpectedStudyManager projectExpectedStudyManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager,
    ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager,
    ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager,
    ProjectExpectedStudySrfTargetManager projectExpectedStudySrfTargetManager,
    ProjectExpectedStudySubIdoManager projectExpectedStudySubIdoManager,
    ProjectExpectedStudyFlagshipManager projectExpectedStudyFlagshipManager,
    ProjectExpectedStudyLinkManager projectExpectedStudyLinkManager,
    ProjectExpectedStudyInstitutionManager projectExpectedStudyInstitutionManager,
    ProjectExpectedStudyPolicyManager projectExpectedStudyPolicyManager,
    ProjectExpectedStudyInnovationManager projectExpectedStudyInnovationManager,
    ProjectExpectedStudyCrpManager projectExpectedStudyCrpManager,
    ProjectExpectedStudyQuantificationManager projectExpectedStudyQuantificationManager,
    StudyTypeManager studyTypeManager, ProjectManager projectManager, CrpMilestoneManager crpMilestoneManager,
    ProjectExpectedStudyMilestoneManager projectExpectedStudyMilestoneManager,
    ProjectExpectedStudyMapper projectExpectedStudyMapper) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.repIndStageStudyManager = repIndStageStudyManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.srfSloIndicatorManager = srfSloIndicatorManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.crpProgramManager = crpProgramManager;
    this.locElementManager = locElementManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectPolicyManager = projectPolicyManager;
    this.institutionManager = institutionManager;
    this.cgiarCrossCuttingMarkerManager = cgiarCrossCuttingMarkerManager;
    this.repIndGenderYouthFocusLevelManager = repIndGenderYouthFocusLevelManager;
    this.evidenceTagManager = evidenceTagManager;
    this.generalStatusManager = generalStatusManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectExpectedStudyInfoManager = projectExpectedStudyInfoManager;
    this.projectExpectedStudyGeographicScopeManager = projectExpectedStudyGeographicScopeManager;
    this.projectExpectedStudyCountryManager = projectExpectedStudyCountryManager;
    this.projectExpectedStudyRegionManager = projectExpectedStudyRegionManager;
    this.projectExpectedStudySrfTargetManager = projectExpectedStudySrfTargetManager;
    this.projectExpectedStudyFlagshipManager = projectExpectedStudyFlagshipManager;
    this.projectExpectedStudyInstitutionManager = projectExpectedStudyInstitutionManager;
    this.projectExpectedStudyPolicyManager = projectExpectedStudyPolicyManager;
    this.projectExpectedStudyInnovationManager = projectExpectedStudyInnovationManager;
    this.projectExpectedStudyCrpManager = projectExpectedStudyCrpManager;
    this.projectExpectedStudyQuantificationManager = projectExpectedStudyQuantificationManager;
    this.projectExpectedStudyLinkManager = projectExpectedStudyLinkManager;
    this.studyTypeManager = studyTypeManager;
    this.projectManager = projectManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.projectExpectedStudySubIdoManager = projectExpectedStudySubIdoManager;

    this.projectExpectedStudyMapper = projectExpectedStudyMapper;
  }

  public Long createExpectedStudy(NewProjectExpectedStudyDTO newProjectExpectedStudy, String entityAcronym, User user) {
    Long projectExpectedStudyID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createExpetedStudy", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == newProjectExpectedStudy.getPhase().getYear()
        && c.getName().equalsIgnoreCase(newProjectExpectedStudy.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("create Expected Studies", "phase",
        new NewProjectPolicyDTO().getPhase().getYear() + " is an invalid year"));
    }
    Project project = null;
    if (newProjectExpectedStudy.getProject() != null) {
      project = projectManager.getProjectById(newProjectExpectedStudy.getProject());
      if (project == null) {
        fieldErrors.add(new FieldErrorDTO("create Expected Studies", "Project",
          newProjectExpectedStudy.getProject() + " is an project ID"));
      }
    }

    if (fieldErrors.size() == 0) {
      ProjectExpectedStudy projectExpectedStudy = new ProjectExpectedStudy();
      List<RepIndGeographicScope> geographicScopeList = new ArrayList<RepIndGeographicScope>();
      List<SrfSloIndicator> srfSloIndicatorList = new ArrayList<SrfSloIndicator>();
      List<GlobalUnit> crpContributing = new ArrayList<GlobalUnit>();
      List<CrpProgram> flagshipList = new ArrayList<>();
      List<LocElement> countriesList = new ArrayList<>();
      List<LocElement> regionsList = new ArrayList<>();
      List<ProjectExpectedStudySubIdo> srfSubIdoList = new ArrayList<>();
      List<ProjectInnovation> projectInnovationList = new ArrayList<ProjectInnovation>();
      List<ProjectPolicy> projectPolicyList = new ArrayList<ProjectPolicy>();
      List<Institution> institutionList = new ArrayList<Institution>();
      List<String> linkList = new ArrayList<String>();
      List<ProjectExpectedStudyMilestone> milestoneList = new ArrayList<ProjectExpectedStudyMilestone>();
      List<ProjectExpectedStudyQuantification> ExpectedStudyQuantificationList =
        new ArrayList<ProjectExpectedStudyQuantification>();

      if (newProjectExpectedStudy.getProjectExpectedEstudyInfo() != null) {
        ProjectExpectedStudyInfo projectExpectedStudyInfo = new ProjectExpectedStudyInfo();
        projectExpectedStudyInfo.setTitle(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getTitle());
        projectExpectedStudyInfo.setYear(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getYear());
        projectExpectedStudyInfo.setPhase(phase);


        StudyType studyType =
          studyTypeManager.getStudyTypeById(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getStudyType());
        if (studyType != null) {
          projectExpectedStudyInfo.setStudyType(studyType);
        } else {
          fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Study Type",
            newProjectExpectedStudy.getProjectExpectedEstudyInfo().getStudyType() + " is an invalid status code"));
        }
        GeneralStatus generalStatus =
          generalStatusManager.getGeneralStatusById(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getStatus());
        if (generalStatus != null) {
          projectExpectedStudyInfo.setStatus(generalStatus);
        } else {
          fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Status",
            newProjectExpectedStudy.getProjectExpectedEstudyInfo().getStatus() + " is an invalid status code"));
        }
        EvidenceTag evidenceTag =
          evidenceTagManager.getEvidenceTagById(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getTag());
        if (evidenceTag != null) {
          projectExpectedStudyInfo.setEvidenceTag(evidenceTag);
        } else {
          fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Evidence Tag",
            newProjectExpectedStudy.getProjectExpectedEstudyInfo().getMaturityOfChange()
              + " is an invalid Evidence Tag code"));
        }
        RepIndStageStudy repIndStageStudy = repIndStageStudyManager
          .getRepIndStageStudyById(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getMaturityOfChange());
        if (repIndStageStudy != null) {
          projectExpectedStudyInfo.setRepIndStageStudy(repIndStageStudy);
        } else {
          fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "MaturityOfChange",
            newProjectExpectedStudy.getProjectExpectedEstudyInfo().getMaturityOfChange()
              + " is an invalid Level of maturity of change reported code"));
        }
        // geographic
        if (fieldErrors.size() == 0) {
          projectExpectedStudy.setPhase(phase.getId());
          projectExpectedStudy.setProjectExpectedStudyInfo(projectExpectedStudyInfo);
          if (newProjectExpectedStudy.getGeographicScopes() != null
            && newProjectExpectedStudy.getGeographicScopes().size() > 0) {
            for (String geographicscope : newProjectExpectedStudy.getGeographicScopes()) {
              if (geographicscope != null && this.isNumeric(geographicscope)) {
                RepIndGeographicScope repIndGeographicScope =
                  repIndGeographicScopeManager.getRepIndGeographicScopeById(Long.valueOf(geographicscope));
                if (repIndGeographicScope != null) {
                  geographicScopeList.add(repIndGeographicScope);
                } else {
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "GeographicScope",
                    repIndGeographicScope + " is an invalid geographicScope identifier"));
                }
              }
            }
          }
          // Slo Target
          if (newProjectExpectedStudy.getSrfSloTargetList() != null
            && newProjectExpectedStudy.getSrfSloTargetList().size() > 0) {
            for (String sloTarget : newProjectExpectedStudy.getSrfSloTargetList()) {
              if (sloTarget != null && this.isNumeric(sloTarget)) {
                SrfSloIndicator srfSloIndicator =
                  srfSloIndicatorManager.getSrfSloIndicatorById(Long.valueOf(sloTarget));
                if (srfSloIndicator != null) {
                  srfSloIndicatorList.add(srfSloIndicator);
                } else {
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "SrfSloIndicator target ",
                    sloTarget + " is an invalid SLOIndicatorTarget identifier"));
                }
              }
            }
          }
          // crps
          if (newProjectExpectedStudy.getProjectExpectedStudiesCrpDTO() != null
            && newProjectExpectedStudy.getProjectExpectedStudiesCrpDTO().size() > 0) {
            for (String crps : newProjectExpectedStudy.getProjectExpectedStudiesCrpDTO()) {
              if (crps != null) {
                GlobalUnit globalUnit = globalUnitManager.findGlobalUnitBySMOCode(crps);
                if (globalUnit != null) {
                  crpContributing.add(globalUnit);
                } else {
                  fieldErrors
                    .add(new FieldErrorDTO("CreateExpectedStudy", "CRP ", crps + " is an invalid CRP identifier"));
                }
              }
            }
          }
          // flagships
          if (newProjectExpectedStudy.getFlagshipsList() != null
            && newProjectExpectedStudy.getFlagshipsList().size() > 0) {
            for (String flagship : newProjectExpectedStudy.getFlagshipsList()) {
              if (flagship != null && this.isNumeric(flagship)) {
                CrpProgram crpProgram = crpProgramManager.getCrpProgramById(Long.valueOf(flagship));
                if (crpProgram != null) {
                  flagshipList.add(crpProgram);
                } else {
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Flagship/Module",
                    flagship + " is an invalid Flagship/Module code"));
                }
              }
            }
          }
          // countries
          if (newProjectExpectedStudy.getCountries() != null && newProjectExpectedStudy.getCountries().size() > 0) {
            for (String countries : newProjectExpectedStudy.getCountries()) {
              if (countries != null && this.isNumeric(countries)) {
                LocElement country = this.locElementManager.getLocElementByNumericISOCode(Long.valueOf(countries));
                if (country == null) {
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Countries",
                    countries + " is an invalid country ISO Code"));

                } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_COUNTRY) {
                  fieldErrors.add(
                    new FieldErrorDTO("CreateExpectedStudy", "Countries", countries + " is not a Country ISO code"));
                } else {
                  countriesList.add(country);
                }
              }
            }
          }

          // regions
          if (newProjectExpectedStudy.getRegions() != null && newProjectExpectedStudy.getRegions().size() > 0) {
            for (String region : newProjectExpectedStudy.getRegions()) {
              if (region != null && this.isNumeric(region)) {
                LocElement country = this.locElementManager.getLocElementByNumericISOCode(Long.valueOf(region));
                if (country == null) {
                  fieldErrors.add(
                    new FieldErrorDTO("CreateExpectedStudy", "Regions", region + " is an invalid region UM49 Code"));

                } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_REGION) {
                  fieldErrors
                    .add(new FieldErrorDTO("CreateExpectedStudy", "Regions", region + " is not a Region UM49 code"));
                } else {
                  regionsList.add(country);
                }
              }
            }
          }
          // subidos
          if (newProjectExpectedStudy.getSrfSubIdoList() != null
            && newProjectExpectedStudy.getSrfSubIdoList().size() > 0) {
            for (NewSrfSubIdoDTO subido : newProjectExpectedStudy.getSrfSubIdoList()) {
              if (subido != null && subido.getSubIdo() != null) {
                SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoByCode(subido.getSubIdo());
                if (srfSubIdo == null) {
                  fieldErrors
                    .add(new FieldErrorDTO("CreateExpectedStudy", "SubIDO", subido + " is an invalid subIDO Code"));
                } else {

                  ProjectExpectedStudySubIdo obj = new ProjectExpectedStudySubIdo();
                  if (subido.getPrimary() != null && subido.getPrimary()) {
                    obj.setPrimary(subido.getPrimary());
                  } else {
                    obj.setPrimary(false);
                  }
                  obj.setSrfSubIdo(srfSubIdo);
                  srfSubIdoList.add(obj);
                }
              }
            }
          }
          // innovation link
          if (newProjectExpectedStudy.getInnovationCodeList() != null
            && newProjectExpectedStudy.getInnovationCodeList().size() > 0) {
            for (String innovation : newProjectExpectedStudy.getInnovationCodeList()) {
              if (innovation != null && this.isNumeric(innovation)) {
                ProjectInnovation projectInnovation =
                  projectInnovationManager.getProjectInnovationById(Long.valueOf(innovation));
                if (projectInnovation != null) {
                  projectInnovationList.add(projectInnovation);
                } else {
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Innovation",
                    innovation + " is an invalid innovation identifier"));
                }
              }
            }
          }
          // policies link
          if (newProjectExpectedStudy.getPoliciesCodeList() != null
            && newProjectExpectedStudy.getPoliciesCodeList().size() > 0) {
            for (String policy : newProjectExpectedStudy.getPoliciesCodeList()) {
              if (policy != null && this.isNumeric(policy)) {
                ProjectPolicy projectPolicy = projectPolicyManager.getProjectPolicyById(Long.valueOf(policy));
                if (projectPolicy != null) {
                  projectPolicyList.add(projectPolicy);
                } else {
                  fieldErrors.add(
                    new FieldErrorDTO("CreateExpectedStudy", "Policy", policy + " is an invalid Policy identifier"));
                }
              }
            }
          }
          // institutions
          if (newProjectExpectedStudy.getInstitutionsList() != null
            && newProjectExpectedStudy.getInstitutionsList().size() > 0) {
            for (String institution : newProjectExpectedStudy.getInstitutionsList()) {
              if (institution != null && this.isNumeric(institution)) {
                Institution institutionDB = institutionManager.getInstitutionById(Long.valueOf(institution));
                if (institutionDB != null) {
                  institutionList.add(institutionDB);
                } else {
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Institution",
                    institution + " is an invalid Institution identifier"));
                }
              }
            }
          }

          // milestones
          if (newProjectExpectedStudy.getMilestonesCodeList() != null
            && newProjectExpectedStudy.getMilestonesCodeList().size() > 0) {
            for (NewMilestonesDTO milestones : newProjectExpectedStudy.getMilestonesCodeList()) {
              if (milestones != null && this.isNumeric(milestones.getMilestone())) {
                CrpMilestone crpMilestone =
                  crpMilestoneManager.getCrpMilestoneById(Long.valueOf(milestones.getMilestone()));
                if (crpMilestone != null) {
                  ProjectExpectedStudyMilestone obj = new ProjectExpectedStudyMilestone();
                  obj.setCrpMilestone(crpMilestone);
                  if (milestones.getPrimary() != null && milestones.getPrimary()) {
                    obj.setPrimary(milestones.getPrimary());
                  } else {
                    obj.setPrimary(false);
                  }
                  milestoneList.add(obj);
                } else {
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Milestone",
                    milestones.getMilestone() + " is an invalid Institution identifier"));
                }
              }
            }
          }
          // Crosscutting markers
          if (newProjectExpectedStudy.getCrossCuttingMarkers() != null
            && newProjectExpectedStudy.getCrossCuttingMarkers().size() > 0) {
            for (NewCrosscuttingMarkersDTO crosscuttingmark : newProjectExpectedStudy.getCrossCuttingMarkers()) {
              if (crosscuttingmark != null && crosscuttingmark.getCrossCuttingmarker() != null
                && crosscuttingmark.getCrossCuttingmarkerScore() != null) {
                if (this.isNumeric(crosscuttingmark.getCrossCuttingmarker())) {
                  CgiarCrossCuttingMarker cgiarCrossCuttingMarker = cgiarCrossCuttingMarkerManager
                    .getCgiarCrossCuttingMarkerById(Long.valueOf(crosscuttingmark.getCrossCuttingmarker()));
                  if (cgiarCrossCuttingMarker == null) {
                    fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Crosscuttingmarker",
                      cgiarCrossCuttingMarker + " is an invalid Crosscuttingmarker Code"));

                  } else {
                    if (this.isNumeric(crosscuttingmark.getCrossCuttingmarkerScore())) {
                      RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
                        repIndGenderYouthFocusLevelManager.getRepIndGenderYouthFocusLevelById(
                          Long.valueOf(crosscuttingmark.getCrossCuttingmarkerScore()));
                      if (repIndGenderYouthFocusLevel == null) {
                        fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "CrosscuttingmarkerScore",
                          cgiarCrossCuttingMarker + " is an invalid GenderYouthFocusLevel Code"));
                      } else {
                        if (cgiarCrossCuttingMarker.getId()
                          .longValue() == APConstants.CGIAR_CROSS_CUTTING_MARKERS_GENDER) {
                          projectExpectedStudyInfo.setGenderLevel(repIndGenderYouthFocusLevel);
                        }
                        if (cgiarCrossCuttingMarker.getId()
                          .longValue() == APConstants.CGIAR_CROSS_CUTTING_MARKERS_YOUTH) {
                          projectExpectedStudyInfo.setYouthLevel(repIndGenderYouthFocusLevel);
                        }
                        if (cgiarCrossCuttingMarker.getId()
                          .longValue() == APConstants.CGIAR_CROSS_CUTTING_MARKERS_CAPDEV) {
                          projectExpectedStudyInfo.setCapdevLevel(repIndGenderYouthFocusLevel);
                        }
                        if (cgiarCrossCuttingMarker.getId()
                          .longValue() == APConstants.CGIAR_CROSS_CUTTING_MARKERS_CLIMATECHANGE) {
                          projectExpectedStudyInfo.setClimateChangeLevel(repIndGenderYouthFocusLevel);
                        }
                      }
                    }
                  }
                }
              }
            }
          }
          // links
          for (String link : newProjectExpectedStudy.getLinks()) {
            if (link != null && link.length() > 0) {
              linkList.add(link);
            }
          }
          // quantifications
          if (newProjectExpectedStudy.getQuantificationList() != null) {
            for (QuantificationDTO quantification : newProjectExpectedStudy.getQuantificationList()) {
              if (quantification.getTargetUnit().length() > 0 && quantification.getNumber() >= 0
                && (quantification.getQuantificationType().equals("A")
                  || quantification.getQuantificationType().equals("B"))) {
                ProjectExpectedStudyQuantification projectExpectedStudyQuantification =
                  new ProjectExpectedStudyQuantification();
                projectExpectedStudyQuantification.setPhase(phase);
                projectExpectedStudyQuantification.setTargetUnit(quantification.getTargetUnit());
                projectExpectedStudyQuantification.setTypeQuantification(quantification.getQuantificationType());
                projectExpectedStudyQuantification.setNumber(quantification.getNumber());
                projectExpectedStudyQuantification.setComments(quantification.getComments());
                ExpectedStudyQuantificationList.add(projectExpectedStudyQuantification);
              } else {
                fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "Quantification",
                  "You have to fill all fields for quantification"));
              }
            }
          }
          if (fieldErrors.size() == 0 && project != null) {
            projectExpectedStudy.setPhase(phase.getId());
            projectExpectedStudy.setYear(phase.getYear());
            projectExpectedStudy.setProject(project);
            ProjectExpectedStudy projectExpectedStudyDB =
              projectExpectedStudyManager.saveProjectExpectedStudy(projectExpectedStudy);
            if (projectExpectedStudyDB != null) {
              projectExpectedStudyID = projectExpectedStudyDB.getId();
              projectExpectedStudyInfo.setProjectExpectedStudy(projectExpectedStudyDB);
              if (projectPolicyList.size() > 0) {
                projectExpectedStudyInfo.setIsContribution(true);
              }

              if (srfSloIndicatorList.size() > 0) {
                projectExpectedStudyInfo.setIsSrfTarget("targetsOptionYes");
              }
              if (projectExpectedStudyInfoManager.saveProjectExpectedStudyInfo(projectExpectedStudyInfo) != null) {
                // save geographicscopes
                for (RepIndGeographicScope repIndGeographicScope : geographicScopeList) {
                  ProjectExpectedStudyGeographicScope projectExpectedStudyGeographicScope =
                    new ProjectExpectedStudyGeographicScope();
                  projectExpectedStudyGeographicScope.setPhase(phase);
                  projectExpectedStudyGeographicScope.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyGeographicScope.setRepIndGeographicScope(repIndGeographicScope);
                  projectExpectedStudyGeographicScopeManager
                    .saveProjectExpectedStudyGeographicScope(projectExpectedStudyGeographicScope);
                }
                // countries
                for (LocElement country : countriesList) {
                  ProjectExpectedStudyCountry projectExpectedStudyCountry = new ProjectExpectedStudyCountry();
                  projectExpectedStudyCountry.setLocElement(country);
                  projectExpectedStudyCountry.setPhase(phase);
                  projectExpectedStudyCountry.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyCountryManager.saveProjectExpectedStudyCountry(projectExpectedStudyCountry);
                }
                // regions
                for (LocElement country : regionsList) {
                  ProjectExpectedStudyRegion projectExpectedStudyRegion = new ProjectExpectedStudyRegion();
                  projectExpectedStudyRegion.setLocElement(country);
                  projectExpectedStudyRegion.setPhase(phase);
                  projectExpectedStudyRegion.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyRegionManager.saveProjectExpectedStudyRegion(projectExpectedStudyRegion);
                }
                // SLO targets
                for (SrfSloIndicator srfSloIndicator : srfSloIndicatorList) {
                  ProjectExpectedStudySrfTarget projectExpectedStudySrfTarget = new ProjectExpectedStudySrfTarget();
                  projectExpectedStudySrfTarget.setSrfSloIndicator(srfSloIndicator);
                  projectExpectedStudySrfTarget.setPhase(phase);
                  projectExpectedStudySrfTarget.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudySrfTargetManager.saveProjectExpectedStudySrfTarget(projectExpectedStudySrfTarget);
                }
                // SudIDOs
                for (ProjectExpectedStudySubIdo srfSubIdo : srfSubIdoList) {
                  ProjectExpectedStudySubIdo projectExpectedStudySubIdo = new ProjectExpectedStudySubIdo();
                  projectExpectedStudySubIdo.setPhase(phase);
                  projectExpectedStudySubIdo.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudySubIdo.setSrfSubIdo(srfSubIdo.getSrfSubIdo());
                  projectExpectedStudySubIdo.setPrimary(srfSubIdo.getPrimary());
                  // to do Set as a primary if is necessary
                  projectExpectedStudySubIdoManager.saveProjectExpectedStudySubIdo(projectExpectedStudySubIdo);
                }
                // flagships
                for (CrpProgram crpProgram : flagshipList) {
                  ProjectExpectedStudyFlagship projectExpectedStudyFlagship = new ProjectExpectedStudyFlagship();
                  projectExpectedStudyFlagship.setCrpProgram(crpProgram);
                  projectExpectedStudyFlagship.setPhase(phase);
                  projectExpectedStudyFlagship.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyFlagshipManager.saveProjectExpectedStudyFlagship(projectExpectedStudyFlagship);
                }
                // Links
                for (String link : linkList) {
                  ProjectExpectedStudyLink projectExpectedStudyLink = new ProjectExpectedStudyLink();
                  projectExpectedStudyLink.setPhase(phase);
                  projectExpectedStudyLink.setLink(link);
                  projectExpectedStudyLink.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyLinkManager.saveProjectExpectedStudyLink(projectExpectedStudyLink);
                }
                // institutions
                for (Institution institution : institutionList) {
                  ProjectExpectedStudyInstitution projectExpectedStudyInstitution =
                    new ProjectExpectedStudyInstitution();
                  projectExpectedStudyInstitution.setInstitution(institution);
                  projectExpectedStudyInstitution.setPhase(phase);
                  projectExpectedStudyInstitution.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyInstitutionManager
                    .saveProjectExpectedStudyInstitution(projectExpectedStudyInstitution);
                }
                // policies
                for (ProjectPolicy projectPolicy : projectPolicyList) {
                  ProjectExpectedStudyPolicy projectExpectedStudyPolicy = new ProjectExpectedStudyPolicy();
                  projectExpectedStudyPolicy.setProjectPolicy(projectPolicy);
                  projectExpectedStudyPolicy.setPhase(phase);
                  projectExpectedStudyPolicy.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyPolicyManager.saveProjectExpectedStudyPolicy(projectExpectedStudyPolicy);
                }
                // innovations
                for (ProjectInnovation projectInnovation : projectInnovationList) {
                  ProjectExpectedStudyInnovation projectExpectedStudyInnovation = new ProjectExpectedStudyInnovation();
                  projectExpectedStudyInnovation.setPhase(phase);
                  projectExpectedStudyInnovation.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyInnovation.setProjectInnovation(projectInnovation);
                  projectExpectedStudyInnovationManager
                    .saveProjectExpectedStudyInnovation(projectExpectedStudyInnovation);
                }

                // milestones
                for (ProjectExpectedStudyMilestone projectExpectedStudyMilestones : milestoneList) {
                  ProjectExpectedStudyMilestone expectedStudyMilestone = new ProjectExpectedStudyMilestone();
                  expectedStudyMilestone.setCrpMilestone(projectExpectedStudyMilestones.getCrpMilestone());
                  expectedStudyMilestone.setPhase(phase);
                  expectedStudyMilestone.setPrimary(projectExpectedStudyMilestones.getPrimary());
                  expectedStudyMilestone.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyMilestoneManager.saveProjectExpectedStudyMilestone(expectedStudyMilestone);
                }
                // crps
                for (GlobalUnit globalUnit : crpContributing) {
                  ProjectExpectedStudyCrp projectExpectedStudyCrp = new ProjectExpectedStudyCrp();
                  projectExpectedStudyCrp.setGlobalUnit(globalUnit);
                  projectExpectedStudyCrp.setPhase(phase);
                  projectExpectedStudyCrp.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyCrpManager.saveProjectExpectedStudyCrp(projectExpectedStudyCrp);
                }
                // quantification
                for (ProjectExpectedStudyQuantification projectExpectedStudyQuantification : ExpectedStudyQuantificationList) {
                  ProjectExpectedStudyQuantification projectExpectedStudyQuantificationDB =
                    new ProjectExpectedStudyQuantification();
                  projectExpectedStudyQuantificationDB.setNumber(projectExpectedStudyQuantification.getNumber());
                  projectExpectedStudyQuantificationDB.setPhase(phase);
                  projectExpectedStudyQuantificationDB.setProjectExpectedStudy(projectExpectedStudyDB);
                  projectExpectedStudyQuantificationDB
                    .setTargetUnit(projectExpectedStudyQuantification.getTargetUnit());
                  projectExpectedStudyQuantificationDB
                    .setTypeQuantification(projectExpectedStudyQuantification.getTypeQuantification());
                  projectExpectedStudyQuantificationDB.setComments(projectExpectedStudyQuantification.getComments());
                  projectExpectedStudyQuantificationManager
                    .saveProjectExpectedStudyQuantification(projectExpectedStudyQuantificationDB);
                }
              }
            }
          }
        }
      }
    }
    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      for (FieldErrorDTO errors : fieldErrors) {
        System.out.println("FieldErrorDTO " + errors.getMessage());
      }
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return projectExpectedStudyID;
  }

  public ResponseEntity<ProjectExpectedStudyDTO> findExpectedStudyById(Long id, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    ProjectExpectedStudy projectExpectedStudy = projectExpectedStudyManager.getProjectExpectedStudyById(id.longValue());


    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().get();

    Set<CrpUser> lstUser = user.getCrpUsers();

    if (!lstUser.stream().anyMatch(crp -> crp.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("findExpectedStudy", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findExpectedStudy", "GlobalUnitEntity",
        CGIARentityAcronym + " is not an invalid CGIAR entity acronym"));
    }
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("findExpectedStudy", "phase", repoYear + " is an invalid year"));
    }

    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    if (projectExpectedStudy != null) {
      ProjectExpectedStudyInfo projectExpectedStudyInfo = projectExpectedStudy.getProjectExpectedStudyInfo(phase);
      // Flagship
      List<ProjectExpectedStudyFlagship> projectExpectedStudyFlagshipList =
        projectExpectedStudy.getProjectExpectedStudyFlagships().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setFlagships(projectExpectedStudyFlagshipList);
      // SubIDOs
      List<ProjectExpectedStudySubIdo> projectExpectedStudySubIdoList =
        projectExpectedStudy.getProjectExpectedStudySubIdos().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setSubIdos(projectExpectedStudySubIdoList);
      if (projectExpectedStudyInfo.getIsSrfTarget() != null
        && projectExpectedStudyInfo.getIsSrfTarget().equals("Yes")) {
        // SrfSlo
        List<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargetList =
          projectExpectedStudy.getProjectExpectedStudySrfTargets().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
        projectExpectedStudy.setSrfTargets(projectExpectedStudySrfTargetList);
      } else {
        List<ProjectExpectedStudySrfTarget> projectExpectedStudySrfTargetList =
          new ArrayList<ProjectExpectedStudySrfTarget>();
        projectExpectedStudy.setSrfTargets(projectExpectedStudySrfTargetList);
      }

      // GeographicScope
      List<ProjectExpectedStudyGeographicScope> projectExpectedStudyGeographicScopeList =
        projectExpectedStudy.getProjectExpectedStudyGeographicScopes().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setGeographicScopes(projectExpectedStudyGeographicScopeList);
      // Institutions
      List<ProjectExpectedStudyInstitution> projectExpectedStudyInstitutionList =
        projectExpectedStudy.getProjectExpectedStudyInstitutions().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setInstitutions(projectExpectedStudyInstitutionList);
      // CRPs contribution
      List<ProjectExpectedStudyCrp> projectExpectedStudyCrpList = projectExpectedStudy.getProjectExpectedStudyCrps()
        .stream().filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setCrps(projectExpectedStudyCrpList);
      // Regions
      List<ProjectExpectedStudyRegion> projectExpectedStudyRegionList = projectExpectedStudyRegionManager
        .findAll().stream().filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
        .collect(Collectors.toList());
      projectExpectedStudy.setStudyRegions(projectExpectedStudyRegionList);
      // Countries
      List<ProjectExpectedStudyCountry> projectExpectedStudyCountryList = projectExpectedStudyCountryManager
        .findAll().stream().filter(c -> c.isActive()
          && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
        .collect(Collectors.toList());
      projectExpectedStudy.setCountries(projectExpectedStudyCountryList);
      // Quantification
      List<ProjectExpectedStudyQuantification> projectExpectedStudyQuantificationList =
        projectExpectedStudyQuantificationManager
          .findAll().stream().filter(c -> c.isActive()
            && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
          .collect(Collectors.toList());
      projectExpectedStudy.setQuantifications(projectExpectedStudyQuantificationList);
      // Links
      List<ProjectExpectedStudyLink> projectExpectedStudyLinkList = projectExpectedStudy.getProjectExpectedStudyLinks()
        .stream().filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setLinks(projectExpectedStudyLinkList);

      // innovations
      List<ProjectExpectedStudyInnovation> projectExpectedStudyInnovationList =
        projectExpectedStudy.getProjectExpectedStudyInnovations().stream()
          .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
      projectExpectedStudy.setInnovations(projectExpectedStudyInnovationList);
      if (projectExpectedStudyInfo.getIsContribution() != null && projectExpectedStudyInfo.getIsContribution()) {
        // Policies
        List<ProjectExpectedStudyPolicy> projectExpectedStudyPolicyList =
          projectExpectedStudy.getProjectExpectedStudyPolicies().stream()
            .filter(c -> c.isActive() && c.getPhase().equals(phase)).collect(Collectors.toList());
        projectExpectedStudy.setPolicies(projectExpectedStudyPolicyList);
      } else {
        List<ProjectExpectedStudyPolicy> projectExpectedStudyPolicyList = new ArrayList<ProjectExpectedStudyPolicy>();
        projectExpectedStudy.setPolicies(projectExpectedStudyPolicyList);
      }

      List<ProjectExpectedStudyMilestone> projectExpectedStudyMilestoneList = projectExpectedStudy
        .getProjectExpectedStudyMilestones().stream().filter(c -> c != null
          && c.getProjectExpectedStudy().getId().equals(projectExpectedStudy.getId()) && c.getPhase().equals(phase))
        .collect(Collectors.toList());
      projectExpectedStudy.setMilestones(projectExpectedStudyMilestoneList);
    }

    return Optional.ofNullable(projectExpectedStudy)
      .map(this.projectExpectedStudyMapper::projectExpectedStudyToProjectExpectedStudyDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public boolean isNumeric(String value) {
    boolean validation = true;
    try {
      Long.parseLong(value);
    } catch (Exception e) {
      validation = false;
    }
    return validation;
  }
}
