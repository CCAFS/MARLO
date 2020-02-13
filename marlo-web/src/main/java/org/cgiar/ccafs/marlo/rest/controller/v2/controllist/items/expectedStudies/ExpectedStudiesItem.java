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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.EvidenceTagManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectInnovationManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectPolicyManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageStudyManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.EvidenceTag;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyQuantification;
import org.cgiar.ccafs.marlo.data.model.ProjectInnovation;
import org.cgiar.ccafs.marlo.data.model.ProjectPolicy;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndStageStudy;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewCrosscuttingMarkersDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectExpectedStudyDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
import org.cgiar.ccafs.marlo.rest.dto.QuantificationDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class ExpectedStudiesItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;

  private RepIndStageStudyManager repIndStageStudyManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private SrfSloIndicatorTargetManager srfSloIndicatorTargetManager;
  private SrfSubIdoManager srfSubIdoManager;
  private CrpProgramManager crpProgramManager;
  private LocElementManager locElementManager;
  private ProjectInnovationManager projectInnovationManager;
  private ProjectPolicyManager projectPolicyManager;
  private InstitutionManager institutionManager;
  private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;
  private RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager;
  private EvidenceTagManager evidenceTagManager;


  @Inject
  public ExpectedStudiesItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    RepIndStageStudyManager repIndStageStudyManager, RepIndGeographicScopeManager repIndGeographicScopeManager,
    SrfSloIndicatorTargetManager srfSloIndicatorTargetManager, SrfSubIdoManager srfSubIdoManager,
    CrpProgramManager crpProgramManager, LocElementManager locElementManager,
    ProjectInnovationManager projectInnovationManager, ProjectPolicyManager projectPolicyManager,
    InstitutionManager institutionManager, CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager,
    RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager, EvidenceTagManager evidenceTagManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.repIndStageStudyManager = repIndStageStudyManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.srfSloIndicatorTargetManager = srfSloIndicatorTargetManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.crpProgramManager = crpProgramManager;
    this.locElementManager = locElementManager;
    this.projectInnovationManager = projectInnovationManager;
    this.projectPolicyManager = projectPolicyManager;
    this.institutionManager = institutionManager;
    this.cgiarCrossCuttingMarkerManager = cgiarCrossCuttingMarkerManager;
    this.repIndGenderYouthFocusLevelManager = repIndGenderYouthFocusLevelManager;
    this.evidenceTagManager = evidenceTagManager;
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

    if (fieldErrors.size() == 0) {
      ProjectExpectedStudy projectExpectedStudy = new ProjectExpectedStudy();
      List<RepIndGeographicScope> geographicScopeList = new ArrayList<RepIndGeographicScope>();
      List<SrfSloIndicatorTarget> srfSloIndicatorTargetList = new ArrayList<SrfSloIndicatorTarget>();
      List<GlobalUnit> crpContributing = new ArrayList<GlobalUnit>();
      List<CrpProgram> flagshipList = new ArrayList<>();
      List<LocElement> countriesList = new ArrayList<>();
      List<LocElement> regionsList = new ArrayList<>();
      List<SrfSubIdo> srfSubIdoList = new ArrayList<>();
      List<ProjectInnovation> projectInnovationList = new ArrayList<ProjectInnovation>();
      List<ProjectPolicy> projectPolicyList = new ArrayList<ProjectPolicy>();
      List<Institution> institutionList = new ArrayList<Institution>();
      List<String> linkList = new ArrayList<String>();
      List<ProjectExpectedStudyQuantification> ExpectedStudyQuantificationList =
        new ArrayList<ProjectExpectedStudyQuantification>();

      if (newProjectExpectedStudy.getProjectExpectedEstudyInfo() != null) {
        ProjectExpectedStudyInfo projectExpectedStudyInfo = new ProjectExpectedStudyInfo();
        projectExpectedStudyInfo.setTitle(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getTitle());
        projectExpectedStudyInfo.setYear(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getYear());

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
                SrfSloIndicatorTarget srfSloIndicatorTarget =
                  srfSloIndicatorTargetManager.getSrfSloIndicatorTargetById(Long.valueOf(sloTarget));
                if (srfSloIndicatorTarget != null) {
                  srfSloIndicatorTargetList.add(srfSloIndicatorTarget);
                } else {
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudy", "SrfSloIndicator target ",
                    srfSloIndicatorTarget + " is an invalid SLOIndicatorTarget identifier"));
                }
              }
            }
          }
          // crps
          if (newProjectExpectedStudy.getProjectExpectedStudiesCrpDTO() != null
            && newProjectExpectedStudy.getProjectExpectedStudiesCrpDTO().size() > 0) {
            for (String crps : newProjectExpectedStudy.getProjectExpectedStudiesCrpDTO()) {
              if (crps != null && this.isNumeric(crps)) {
                GlobalUnit globalUnit = globalUnitManager.getGlobalUnitById(Long.valueOf(crps));
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
            for (String subido : newProjectExpectedStudy.getSrfSubIdoList()) {
              if (subido != null) {
                SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoByCode(subido);
                if (srfSubIdo == null) {
                  fieldErrors
                    .add(new FieldErrorDTO("CreateExpectedStudy", "SubIDO", subido + " is an invalid subIDO Code"));
                } else {
                  srfSubIdoList.add(srfSubIdo);
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

          // crosscutting markers
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
                ExpectedStudyQuantificationList.add(projectExpectedStudyQuantification);
              }

            }
          }
        }
      }
    }
    return projectExpectedStudyID;
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
