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
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndStageStudyManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndStageStudy;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectExpectedStudyDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectPolicyDTO;
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


  @Inject
  public ExpectedStudiesItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    RepIndStageStudyManager repIndStageStudyManager, RepIndGeographicScopeManager repIndGeographicScopeManager,
    SrfSloIndicatorTargetManager srfSloIndicatorTargetManager, SrfSubIdoManager srfSubIdoManager,
    CrpProgramManager crpProgramManager, LocElementManager locElementManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.repIndStageStudyManager = repIndStageStudyManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.srfSloIndicatorTargetManager = srfSloIndicatorTargetManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.crpProgramManager = crpProgramManager;
    this.locElementManager = locElementManager;
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
      List<SrfSubIdo> SrfSubIdoList = new ArrayList<SrfSubIdo>();
      List<GlobalUnit> crpContributing = new ArrayList<GlobalUnit>();
      List<CrpProgram> flagshipList = new ArrayList<>();
      List<LocElement> countriesList = new ArrayList<>();
      if (newProjectExpectedStudy.getProjectExpectedEstudyInfo() != null) {
        ProjectExpectedStudyInfo projectExpectedStudyInfo = new ProjectExpectedStudyInfo();
        projectExpectedStudyInfo.setTitle(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getTitle());
        projectExpectedStudyInfo.setYear(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getYear());

        RepIndStageStudy repIndStageStudy = repIndStageStudyManager
          .getRepIndStageStudyById(newProjectExpectedStudy.getProjectExpectedEstudyInfo().getMaturityOfChange());
        if (repIndStageStudy != null) {
          projectExpectedStudyInfo.setRepIndStageStudy(repIndStageStudy);
        } else {
          fieldErrors.add(new FieldErrorDTO("create Expected Studies", "MaturityOfChange",
            newProjectExpectedStudy.getProjectExpectedEstudyInfo().getMaturityOfChange()
              + " is an invalid Level of maturity of change reported code"));
        }
        // geographic
        if (fieldErrors.size() == 0) {
          if (newProjectExpectedStudy.getGeographicScopes() != null
            && newProjectExpectedStudy.getGeographicScopes().size() > 0) {
            for (String geographicscope : newProjectExpectedStudy.getGeographicScopes()) {
              if (geographicscope != null && this.isNumeric(geographicscope)) {
                RepIndGeographicScope repIndGeographicScope =
                  repIndGeographicScopeManager.getRepIndGeographicScopeById(Long.valueOf(geographicscope));
                if (repIndGeographicScope != null) {
                  geographicScopeList.add(repIndGeographicScope);
                } else {
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudies", "GeographicScope",
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
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudies", "SrfSloIndicator target ",
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
                    .add(new FieldErrorDTO("CreateExpectedStudies", "CRP ", crps + " is an invalid CRP identifier"));
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
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudies", "Flagship/Module",
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
                  fieldErrors.add(new FieldErrorDTO("CreateExpectedStudies", "Countries",
                    countries + " is an invalid country ISO Code"));

                } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_COUNTRY) {
                  fieldErrors.add(
                    new FieldErrorDTO("CreateExpectedStudies", "Countries", countries + " is not a Country ISO code"));
                } else {
                  countriesList.add(country);
                }
              }
            }
          }

          // regions
          if (newProjectExpectedStudy.getRegions() != null && newProjectExpectedStudy.getRegions().size() > 0) {
            for (String region : newProjectExpectedStudy.getRegions()) {

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
