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
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyInfoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudyRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySrfTargetManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectExpectedStudySubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.ProjectManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSubIdoManager;
import org.cgiar.ccafs.marlo.data.manager.StudyTypeManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.Project;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudy;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyCountry;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyInfo;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudyRegion;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySrfTarget;
import org.cgiar.ccafs.marlo.data.model.ProjectExpectedStudySubIdo;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.data.model.SrfSubIdo;
import org.cgiar.ccafs.marlo.data.model.StudyType;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewProjectExpectedStudiesOtherDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewSrfSubIdoDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

@Named
public class ExpectedStudiesOtherItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private ProjectManager projectManager;
  private StudyTypeManager studyTypeManager;
  private GeneralStatusManager generalStatusManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private SrfSloIndicatorTargetManager srfSloIndicatorTargetManager;
  private SrfSloIndicatorManager srfSloIndicatorManager;
  private LocElementManager locElementManager;
  private SrfSubIdoManager srfSubIdoManager;
  private ProjectExpectedStudyManager projectExpectedStudyManager;
  private ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager;
  private ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager;
  private ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager;
  private ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager;
  private ProjectExpectedStudySrfTargetManager projectExpectedStudySrfTargetManager;
  private ProjectExpectedStudySubIdoManager projectExpectedStudySubIdoManager;

  @Inject
  public ExpectedStudiesOtherItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    ProjectManager projectManager, StudyTypeManager studyTypeManager, GeneralStatusManager generalStatusManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager,
    SrfSloIndicatorTargetManager srfSloIndicatorTargetManager, SrfSloIndicatorManager srfSloIndicatorManager,
    LocElementManager locElementManager, SrfSubIdoManager srfSubIdoManager,
    ProjectExpectedStudyManager projectExpectedStudyManager,
    ProjectExpectedStudyInfoManager projectExpectedStudyInfoManager,
    ProjectExpectedStudyGeographicScopeManager projectExpectedStudyGeographicScopeManager,
    ProjectExpectedStudyCountryManager projectExpectedStudyCountryManager,
    ProjectExpectedStudyRegionManager projectExpectedStudyRegionManager,
    ProjectExpectedStudySrfTargetManager projectExpectedStudySrfTargetManager,
    ProjectExpectedStudySubIdoManager projectExpectedStudySubIdoManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.projectManager = projectManager;
    this.studyTypeManager = studyTypeManager;
    this.generalStatusManager = generalStatusManager;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;
    this.srfSloIndicatorTargetManager = srfSloIndicatorTargetManager;
    this.srfSloIndicatorManager = srfSloIndicatorManager;
    this.locElementManager = locElementManager;
    this.srfSubIdoManager = srfSubIdoManager;
    this.projectExpectedStudyManager = projectExpectedStudyManager;
    this.projectExpectedStudyInfoManager = projectExpectedStudyInfoManager;
    this.projectExpectedStudyGeographicScopeManager = projectExpectedStudyGeographicScopeManager;
    this.projectExpectedStudyCountryManager = projectExpectedStudyCountryManager;
    this.projectExpectedStudyRegionManager = projectExpectedStudyRegionManager;
    this.projectExpectedStudySrfTargetManager = projectExpectedStudySrfTargetManager;
    this.projectExpectedStudySubIdoManager = projectExpectedStudySubIdoManager;

  }

  public Long createExpectedStudy(NewProjectExpectedStudiesOtherDTO newProjectExpectedStudiesOther,
    String entityAcronym, User user) {
    Long expectedStudyID = null;
    Phase phase = null;
    Long id = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "GlobalUnitEntity",
        entityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "GlobalUnitEntity",
          "The Global Unit with acronym " + entityAcronym + " is not active."));
      }

    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream().anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), entityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newProjectExpectedStudiesOther.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "PhaseEntity", "Phase must not be null"));
    } else {
      if (newProjectExpectedStudiesOther.getPhase().getName() == null
        || newProjectExpectedStudiesOther.getPhase().getName().trim().isEmpty()
        || newProjectExpectedStudiesOther.getPhase().getYear() == null
        || newProjectExpectedStudiesOther.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> p.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
            && p.getYear() == newProjectExpectedStudiesOther.getPhase().getYear()
            && p.getName().equalsIgnoreCase(newProjectExpectedStudiesOther.getPhase().getName()))
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors.add(
            new FieldErrorDTO("createExpectedStudyOther", "phase", newProjectExpectedStudiesOther.getPhase().getName()
              + ' ' + newProjectExpectedStudiesOther.getPhase().getYear() + " is an invalid phase"));
        }
      }
    }
    Project project = null;
    if (newProjectExpectedStudiesOther.getProject() != null) {
      project = projectManager.getProjectById(newProjectExpectedStudiesOther.getProject());
      if (project == null) {
        fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "Project",
          newProjectExpectedStudiesOther.getProject() + " is an invalid project ID"));
      }

    } else {
      fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "Project", "Please insert a Project ID"));
    }
    if (fieldErrors.isEmpty()) {
      ProjectExpectedStudy projectExpectedStudy = new ProjectExpectedStudy();
      List<RepIndGeographicScope> geographicScopeList = new ArrayList<RepIndGeographicScope>();
      List<SrfSloIndicator> srfSloIndicatorList = new ArrayList<SrfSloIndicator>();
      List<LocElement> countriesList = new ArrayList<>();
      List<LocElement> regionsList = new ArrayList<>();
      List<ProjectExpectedStudySubIdo> srfSubIdoList = new ArrayList<>();
      int hasPrimary = 0;
      int wordCount = -1;
      if (newProjectExpectedStudiesOther.getNewProjectExpectedStudiesOtherInfo() != null) {
        ProjectExpectedStudyInfo projectExpectedStudyInfo = new ProjectExpectedStudyInfo();
        if (newProjectExpectedStudiesOther.getNewProjectExpectedStudiesOtherInfo().getTitle() != null
          && !newProjectExpectedStudiesOther.getNewProjectExpectedStudiesOtherInfo().getTitle().trim().isEmpty()) {
          projectExpectedStudyInfo
            .setTitle(newProjectExpectedStudiesOther.getNewProjectExpectedStudiesOtherInfo().getTitle());
        } else {
          fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "Title", "Please insert a valid title"));
        }
        if (newProjectExpectedStudiesOther.getNewProjectExpectedStudiesOtherInfo().getYear() > 1900) {
          projectExpectedStudyInfo
            .setYear(newProjectExpectedStudiesOther.getNewProjectExpectedStudiesOtherInfo().getYear());
        } else {
          fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "Year", "Please insert a valid year"));
        }
        projectExpectedStudyInfo.setPhase(phase);
        projectExpectedStudyInfo.setCommissioningStudy(
          newProjectExpectedStudiesOther.getNewProjectExpectedStudiesOtherInfo().getCommissioningStudy());
        projectExpectedStudyInfo.setScopeComments(newProjectExpectedStudiesOther.getScopeComments());
        StudyType studyType = null;
        if (newProjectExpectedStudiesOther.getNewProjectExpectedStudiesOtherInfo().getStudyType() != null) {
          studyType = studyTypeManager
            .getStudyTypeById(newProjectExpectedStudiesOther.getNewProjectExpectedStudiesOtherInfo().getStudyType());
          if (studyType != null) {
            // DANGER! Magic number ahead
            if (studyType.getId().longValue() == 1) {
              fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "Study Type",
                "Study type with id "
                  + newProjectExpectedStudiesOther.getNewProjectExpectedStudiesOtherInfo().getStudyType()
                  + " does not correspond with an MELIA study type"));
            } else {
              projectExpectedStudyInfo.setStudyType(studyType);
            }
          } else {
            fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "Study Type",
              newProjectExpectedStudiesOther.getNewProjectExpectedStudiesOtherInfo().getStudyType()
                + " is an invalid study code"));
          }
        } else {
          fieldErrors
            .add(new FieldErrorDTO("createExpectedStudyOther", "Study", "study identifier can not be null nor empty"));
        }

        GeneralStatus generalStatus = generalStatusManager
          .getGeneralStatusById(newProjectExpectedStudiesOther.getNewProjectExpectedStudiesOtherInfo().getStatus());
        if (generalStatus != null) {
          projectExpectedStudyInfo.setStatus(generalStatus);
        } else {
          fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "Status",
            newProjectExpectedStudiesOther.getNewProjectExpectedStudiesOtherInfo().getStatus()
              + " is an invalid status code"));
        }

        if (fieldErrors.size() == 0) {
          projectExpectedStudy.setPhase(phase.getId());
          projectExpectedStudy.setProjectExpectedStudyInfo(projectExpectedStudyInfo);
          // geographic scope
          if (newProjectExpectedStudiesOther.getGeographicScopes() != null
            && newProjectExpectedStudiesOther.getGeographicScopes().size() > 0) {
            for (String geographicscope : newProjectExpectedStudiesOther.getGeographicScopes()) {
              if (geographicscope != null && !geographicscope.trim().isEmpty()) {
                id =
                  this.tryParseLong(geographicscope.trim(), fieldErrors, "createExpectedStudyOther", "GeographicScope");
                if (id != null) {
                  RepIndGeographicScope repIndGeographicScope =
                    repIndGeographicScopeManager.getRepIndGeographicScopeById(id);
                  if (repIndGeographicScope != null) {
                    geographicScopeList.add(repIndGeographicScope);
                  } else {
                    fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "GeographicScope",
                      id + " is an invalid geographicScope identifier"));
                  }
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "GeographicScope",
                  "geographicScope identifier can not be null nor empty"));
              }
            }
          }
          // Slo Target
          if (newProjectExpectedStudiesOther.getSrfSloTargetList() != null
            && newProjectExpectedStudiesOther.getSrfSloTargetList().size() > 0) {
            for (String sloTarget : newProjectExpectedStudiesOther.getSrfSloTargetList()) {
              if (sloTarget != null && !sloTarget.trim().isEmpty()) {
                SrfSloIndicatorTarget srfSloIndicatorTarget =
                  srfSloIndicatorTargetManager.findbyTargetIndicatorCode(sloTarget);
                if (srfSloIndicatorTarget != null) {
                  SrfSloIndicator srfSloIndicator =
                    srfSloIndicatorManager.getSrfSloIndicatorById(srfSloIndicatorTarget.getSrfSloIndicator().getId());
                  srfSloIndicatorList.add(srfSloIndicator);
                } else {
                  fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "SrfSloIndicatorTarget ",
                    sloTarget + " is an invalid SLOIndicatorTarget identifier"));
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "SrfSloIndicatorTarget",
                  "sloTarget identifier can not be null nor empty"));
              }
            }
          }

          // countries
          if (newProjectExpectedStudiesOther.getCountries() != null
            && newProjectExpectedStudiesOther.getCountries().size() > 0) {
            for (String countries : newProjectExpectedStudiesOther.getCountries()) {
              if (countries != null && !countries.trim().isEmpty()) {
                id = this.tryParseLong(countries.trim(), fieldErrors, "createExpectedStudyOther", "Country");
                if (id != null) {
                  LocElement country = this.locElementManager.getLocElementByNumericISOCode(id);
                  if (country == null) {
                    fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "Countries",
                      countries + " is an invalid country ISO Code"));

                  } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_COUNTRY) {
                    fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "Countries",
                      countries + " is not a Country ISO code"));
                  } else {
                    countriesList.add(country);
                  }
                }
              } else {
                fieldErrors
                  .add(new FieldErrorDTO("putExpectedStudy", "Countries", "A Country code can not be null nor empty."));
              }
            }
          }
          // regions
          if (newProjectExpectedStudiesOther.getRegions() != null
            && newProjectExpectedStudiesOther.getRegions().size() > 0) {
            for (String region : newProjectExpectedStudiesOther.getRegions()) {
              if (region != null && !region.trim().isEmpty()) {
                id = this.tryParseLong(region.trim(), fieldErrors, "createExpectedStudyOther", "Region");
                if (id != null) {
                  LocElement country = this.locElementManager.getLocElementByNumericISOCode(id);
                  if (country == null) {
                    fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "Regions",
                      region + " is an invalid region UM49 Code"));

                  } else if (country.getLocElementType().getId() != APConstants.LOC_ELEMENT_TYPE_REGION) {
                    fieldErrors.add(
                      new FieldErrorDTO("createExpectedStudyOther", "Regions", region + " is not a Region UM49 code"));
                  } else {
                    regionsList.add(country);
                  }
                }
              } else {
                fieldErrors.add(
                  new FieldErrorDTO("createExpectedStudyOther", "Regions", "A Region code can not be null nor empty."));
              }
            }
          }

          // subidos
          if (newProjectExpectedStudiesOther.getSrfSubIdoList() != null
            && newProjectExpectedStudiesOther.getSrfSubIdoList().size() > 0) {
            if (newProjectExpectedStudiesOther.getSrfSubIdoList().size() <= 3) {
              hasPrimary = 0;
              for (NewSrfSubIdoDTO subido : newProjectExpectedStudiesOther.getSrfSubIdoList()) {
                if (subido != null && subido.getSubIdo() != null && !subido.getSubIdo().trim().isEmpty()) {
                  SrfSubIdo srfSubIdo = srfSubIdoManager.getSrfSubIdoByCode(subido.getSubIdo().trim());
                  if (srfSubIdo == null || !srfSubIdo.isActive()) {
                    fieldErrors.add(new FieldErrorDTO("putExpectedStudy", "SubIDO",
                      subido.getSubIdo() + " is an invalid subIDO Code"));
                  } else {
                    ProjectExpectedStudySubIdo obj = new ProjectExpectedStudySubIdo();
                    obj.setSrfSubIdo(srfSubIdo);
                    obj.setPrimary(subido.getPrimary() != null && subido.getPrimary());
                    hasPrimary = hasPrimary + (obj.getPrimary() == true ? 1 : 0);
                    srfSubIdoList.add(obj);
                  }
                } else {
                  fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "SubIDO",
                    "A Sub IDO code can not be null nor empty."));
                }
              }
              if (hasPrimary == 0) {
                fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "SubIDO",
                  "There should be at least one Sub-IDO marked as primary"));
              }
              if (hasPrimary > 1) {
                fieldErrors.add(new FieldErrorDTO("createExpectedStudyOther", "SubIDO",
                  "There can not be more than one Sub-IDO marked as primary"));
              }
            } else {
              fieldErrors.add(
                new FieldErrorDTO("createExpectedStudyOther", "SubIDO", "There can not be more than three SubIDO(s)."));
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
            projectExpectedStudyInfo.setProjectExpectedStudy(projectExpectedStudyDB);
            if (srfSloIndicatorList.size() > 0) {
              projectExpectedStudyInfo.setIsSrfTarget("targetsOptionYes");
            }
            if (projectExpectedStudyInfoManager.saveProjectExpectedStudyInfo(projectExpectedStudyInfo) != null) {
              expectedStudyID = projectExpectedStudyDB.getId();
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
              for (SrfSloIndicator srfSloIndicator : srfSloIndicatorList.stream().distinct()
                .collect(Collectors.toList())) {
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

            }
          }

        }
      }

    }
    return expectedStudyID;
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
