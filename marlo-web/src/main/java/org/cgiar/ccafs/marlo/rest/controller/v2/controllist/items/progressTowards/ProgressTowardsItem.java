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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.progressTowards;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LocElementManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicCountryManager;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicRegionManager;
import org.cgiar.ccafs.marlo.data.manager.ProgressTargetCaseGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGeographicScopeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetCasesManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.LocElement;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicCountry;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicRegion;
import org.cgiar.ccafs.marlo.data.model.ProgressTargetCaseGeographicScope;
import org.cgiar.ccafs.marlo.data.model.RepIndGeographicScope;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTargetCases;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.CountryDTO;
import org.cgiar.ccafs.marlo.rest.dto.GeographicScopeDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewSrfProgressTowardsTargetDTO;
import org.cgiar.ccafs.marlo.rest.dto.RegionDTO;
import org.cgiar.ccafs.marlo.rest.dto.SrfProgressTowardsTargetDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.SrfProgressTowardsTargetMapper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ProgressTowardsItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private ReportSynthesisSrfProgressTargetCasesManager reportSynthesisSrfProgressTargetCasesManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisSrfProgressManager reportSynthesisSrfProgressManager;
  private RepIndGeographicScopeManager repIndGeographicScopeManager;
  private CrpProgramManager crpProgramManager;
  private SrfSloIndicatorManager srfSloIndicatorManager;
  private LocElementManager locElementManager;
  private ProgressTargetCaseGeographicScopeManager progressTargetCaseGeographicScopeManager;
  private ProgressTargetCaseGeographicRegionManager progressTargetCaseGeographicRegionManager;
  private ProgressTargetCaseGeographicCountryManager progressTargetCaseGeographicCountryManager;

  private SrfProgressTowardsTargetMapper srfProgressTowardsTargetMapper;

  @Inject
  public ProgressTowardsItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    ReportSynthesisSrfProgressTargetCasesManager reportSynthesisSrfProgressTargetCasesManager,
    ReportSynthesisManager reportSynthesisManager, LiaisonInstitutionManager liaisonInstitutionManager,
    CrpProgramManager crpProgramManager, ReportSynthesisSrfProgressManager reportSynthesisSrfProgressManager,
    SrfProgressTowardsTargetMapper srfProgressTowardsTargetMapper, SrfSloIndicatorManager srfSloIndicatorManager,
    RepIndGeographicScopeManager repIndGeographicScopeManager, LocElementManager locElementManager,
    ProgressTargetCaseGeographicRegionManager progressTargetCaseGeographicRegionManager,
    ProgressTargetCaseGeographicScopeManager progressTargetCaseGeographicScopeManager,
    ProgressTargetCaseGeographicCountryManager progressTargetCaseGeographicCountryManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.reportSynthesisSrfProgressTargetCasesManager = reportSynthesisSrfProgressTargetCasesManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    // this.crpProgramManager = crpProgramManager;
    this.reportSynthesisSrfProgressManager = reportSynthesisSrfProgressManager;
    this.srfSloIndicatorManager = srfSloIndicatorManager;

    this.srfProgressTowardsTargetMapper = srfProgressTowardsTargetMapper;
    this.repIndGeographicScopeManager = repIndGeographicScopeManager;

    this.locElementManager = locElementManager;

    this.progressTargetCaseGeographicScopeManager = progressTargetCaseGeographicScopeManager;
    this.progressTargetCaseGeographicRegionManager = progressTargetCaseGeographicRegionManager;
    this.progressTargetCaseGeographicCountryManager = progressTargetCaseGeographicCountryManager;
  }

  public Long createProgressTowards(NewSrfProgressTowardsTargetDTO newSrfProgressTowardsTargetDTO,
    String CGIARentityAcronym, User user) {
    Long srfProgressTargetId = null;
    // CrpProgram crpProgram = null;
    LiaisonInstitution liaisonInstitution = null;
    ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTarget = null;
    Phase phase = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createProgressTowards", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createProgressTowards", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("createProgressTowards", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newSrfProgressTowardsTargetDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("createProgressTowards", "PhaseEntity", "Phase must not be null"));
    } else {
      String strippedPhaseName = StringUtils.stripToNull(newSrfProgressTowardsTargetDTO.getPhase().getName());
      if (strippedPhaseName == null || newSrfProgressTowardsTargetDTO.getPhase().getYear() == null
      // DANGER! Magic number ahead
        || newSrfProgressTowardsTargetDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("createProgressTowards", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
            && p.getYear() == newSrfProgressTowardsTargetDTO.getPhase().getYear()
            && StringUtils.equalsIgnoreCase(p.getName(), strippedPhaseName) && p.isActive())
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors
            .add(new FieldErrorDTO("createProgressTowards", "phase", newSrfProgressTowardsTargetDTO.getPhase().getName()
              + ' ' + newSrfProgressTowardsTargetDTO.getPhase().getYear() + " is an invalid phase"));
        }
      }
    }

    if (globalUnitEntity != null) {
      liaisonInstitution =
        liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("createProgressTowards", "LiaisonInstitutionEntity",
          "A Liaison Institution with the acronym " + APConstants.CLARISA_ACRONYM_PMU + " could not be found for "
            + CGIARentityAcronym));
      }
    }
    // strippedId = StringUtils.stripToNull(newSrfProgressTowardsTargetDTO.getFlagshipProgramId());
    // if (strippedId != null) {
    // crpProgram = crpProgramManager.getCrpProgramBySmoCode(strippedId);
    // if (crpProgram == null) {
    // fieldErrors.add(new FieldErrorDTO("createProgressTowards", "FlagshipEntity",
    // newSrfProgressTowardsTargetDTO.getFlagshipProgramId() + " is an invalid CRP Program SMO code."));
    // } else {
    // if (!StringUtils.equalsIgnoreCase(crpProgram.getCrp().getAcronym(), strippedEntityAcronym)) {
    // fieldErrors.add(new FieldErrorDTO("createProgressTowards", "FlagshipEntity",
    // "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym "
    // + CGIARentityAcronym));
    // }
    // }
    // } else {
    // fieldErrors.add(new FieldErrorDTO("createProgressTowards", "FlagshipEntity",
    // "CRP Program SMO code can not be null nor empty."));
    // }
    if (phase != null && !phase.getEditable()) {
      fieldErrors
        .add(new FieldErrorDTO("createProgressTowards", "phase", newSrfProgressTowardsTargetDTO.getPhase().getName()
          + ' ' + newSrfProgressTowardsTargetDTO.getPhase().getYear() + " is a closed phase"));
    }
    if (fieldErrors.isEmpty()) {
      ReportSynthesis reportSynthesis = null;
      ReportSynthesisSrfProgress reportSynthesisSrfProgress = null;
      SrfSloIndicatorTarget srfSloIndicatorTarget = null;
      SrfSloIndicator srfSloIndicator = null;
      Long id = null;

      // we check if a ReportSynthesisSrfProgressTarget for the Phase and ReportSynthesisSrfProgressTarget already
      // exist
      // start ReportSynthesisSrfProgressTarget
      strippedId = StringUtils.stripToNull(newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId());
      if (strippedId != null) {
        id = this.tryParseLong(strippedId, fieldErrors, "createProgressTowards", "SrfSloIndicator");
        if (id != null) {
          // aux variables to stop stream complaint
          Long phaseId = phase.getId();
          Long sloIndicatorId = id;

          reportSynthesisSrfProgressTarget = reportSynthesisSrfProgressTargetCasesManager.findAll().stream()
            .filter(pt -> pt.getReportSynthesisSrfProgress().getReportSynthesis().getPhase().getId() == phaseId
              && StringUtils.equalsIgnoreCase(
                pt.getReportSynthesisSrfProgress().getReportSynthesis().getLiaisonInstitution().getAcronym(),
                APConstants.CLARISA_ACRONYM_PMU)
              && pt.getSrfSloIndicatorTarget().getSrfSloIndicator().getId() == sloIndicatorId)
            .findFirst().orElse(null);

          /*
           * if (reportSynthesisSrfProgressTarget != null) {
           * fieldErrors.add(new FieldErrorDTO("createProgressTowards", "ReportSynthesisSrfProgressTargetEntity",
           * "A Report Synthesis Srf Progress Target was found for the phase. If you want to update it, please use the update method."
           * ));
           * }
           */
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("createProgressTowards", "SrfSloIndicatorEntity",
          "Srf Slo Indicator code can not be null nor empty."));
      }
      // end ReportSynthesisSrfProgressTarget

      // start SrfSloIndicatorTarget
      strippedId = StringUtils.stripToNull(newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId());
      if (strippedId != null) {
        id = this.tryParseLong(strippedId, fieldErrors, "createProgressTowards", "SrfSloIndicator");
        if (id != null) {
          srfSloIndicator = srfSloIndicatorManager.getSrfSloIndicatorById(id);
          if (srfSloIndicator == null) {
            fieldErrors.add(new FieldErrorDTO("createProgressTowards", "SrfSloIndicatorEntity",
              newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId() + " is an invalid Srf Slo Indicator code."));
          } else {
            srfSloIndicatorTarget = srfSloIndicator.getSrfSloIndicatorTargets().stream()
              .sorted((t1, t2) -> Integer.compare(t1.getYear(), t2.getYear()))
              .filter(t -> t.getYear() >= Calendar.getInstance().get(Calendar.YEAR)).findFirst().orElse(null);
            // TODO write a better error message
            if (srfSloIndicatorTarget == null) {
              fieldErrors.add(new FieldErrorDTO("createProgressTowards", "SrfSloIndicatorTargetEntity",
                newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId()
                  + " is from a year that have not been activated."));
            }
          }
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("createProgressTowards", "SrfSloIndicatorTargetEntity",
          "Srf Slo Indicator code can not be null nor empty."));
      }
      // end SrfSloIndicatorTarget

      if (liaisonInstitution != null) {
        reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      }
      // end ReportSynthesis

      // start ReportSynthesisSrfProgress
      if (reportSynthesis != null) {
        reportSynthesisSrfProgress = reportSynthesis.getReportSynthesisSrfProgress();
      }
      // end ReportSynthesisSrfProgress

      // start description
      if (StringUtils.isBlank(newSrfProgressTowardsTargetDTO.getBriefSummary())) {
        fieldErrors.add(new FieldErrorDTO("createProgressTowards", "Summary", "Please enter a brief summary"));
      }
      // end description

      // all validated! now it is supposed to be ok to save the entities
      if (fieldErrors.isEmpty()) {
        reportSynthesisSrfProgressTarget = new ReportSynthesisSrfProgressTargetCases();
        // creating new ReportSynthesis if it does not exist
        if (reportSynthesis == null) {
          reportSynthesis = new ReportSynthesis();
          reportSynthesis.setLiaisonInstitution(liaisonInstitution);
          reportSynthesis.setPhase(phase);
          reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }

        // creating ReportSynthesisSrfProgress if it does not exist
        if (reportSynthesisSrfProgress == null) {
          reportSynthesisSrfProgress = new ReportSynthesisSrfProgress();
          reportSynthesisSrfProgress.setReportSynthesis(reportSynthesis);
          reportSynthesisSrfProgress.setSummary(null);
          reportSynthesisSrfProgressManager.saveReportSynthesisSrfProgress(reportSynthesisSrfProgress);
        }

        reportSynthesisSrfProgressTarget.setReportSynthesisSrfProgress(reportSynthesisSrfProgress);
        reportSynthesisSrfProgressTarget.setSrfSloIndicatorTarget(srfSloIndicatorTarget);
        reportSynthesisSrfProgressTarget.setBriefSummary(newSrfProgressTowardsTargetDTO.getBriefSummary().trim());
        reportSynthesisSrfProgressTarget
          .setAdditionalContribution(newSrfProgressTowardsTargetDTO.getAdditionalContribution());
        reportSynthesisSrfProgressTarget.setIsQAIncluded(new Boolean(false));

        // AR2020 changes
        List<ProgressTargetCaseGeographicScope> scopeList = new ArrayList<ProgressTargetCaseGeographicScope>();
        for (GeographicScopeDTO geographicScope : newSrfProgressTowardsTargetDTO.getGeographicScope()) {
          if (geographicScope.getCode() != null) {
            RepIndGeographicScope repIndGeographicScope =
              repIndGeographicScopeManager.getRepIndGeographicScopeById(geographicScope.getCode().longValue());
            if (repIndGeographicScope != null) {
              ProgressTargetCaseGeographicScope scope = new ProgressTargetCaseGeographicScope();
              scope.setPhase(phase);
              scope.setRepIndGeographicScope(repIndGeographicScope);
              scopeList.add(scope);
            } else {
              fieldErrors.add(new FieldErrorDTO("createProgressTowards", "Geographic Scope",
                geographicScope.getCode().longValue() + " is not a valid Geographic Scope"));
            }
          } else {
            fieldErrors
              .add(new FieldErrorDTO("createProgressTowards", "Geographic Scope", "Geographic Scope code is required"));
          }
        }
        // regions
        List<ProgressTargetCaseGeographicRegion> regionList = new ArrayList<ProgressTargetCaseGeographicRegion>();
        for (RegionDTO regions : newSrfProgressTowardsTargetDTO.getRegions()) {
          if (regions.getUM49Code() != null) {
            LocElement location = locElementManager.getLocElementByNumericISOCode(regions.getUM49Code());
            if (location != null && location.getLocElementType().getId().longValue() == 1) {
              ProgressTargetCaseGeographicRegion region = new ProgressTargetCaseGeographicRegion();
              region.setLocElement(location);
              regionList.add(region);
            } else {
              fieldErrors.add(new FieldErrorDTO("createProgressTowards", "Regions",
                regions.getUM49Code().longValue() + " is not a valid Region UN49 code"));
            }
          } else {
            fieldErrors.add(new FieldErrorDTO("createProgressTowards", "Region", "Region UN49 code is required"));
          }
        }
        // countries
        List<ProgressTargetCaseGeographicCountry> countryList = new ArrayList<ProgressTargetCaseGeographicCountry>();
        for (CountryDTO countries : newSrfProgressTowardsTargetDTO.getCountries()) {
          if (countries.getCode() != null) {
            LocElement location = locElementManager.getLocElementByNumericISOCode(countries.getCode());
            if (location != null && location.getLocElementType().getId().longValue() == 2) {
              ProgressTargetCaseGeographicCountry country = new ProgressTargetCaseGeographicCountry();
              country.setLocElement(location);
              countryList.add(country);
            } else {
              fieldErrors.add(new FieldErrorDTO("createProgressTowards", "Countries",
                countries.getCode().longValue() + " is not a valid country"));
            }
          } else {
            fieldErrors.add(new FieldErrorDTO("createProgressTowards", "Country", "Country iso code is required"));
          }
        }
        if (fieldErrors.isEmpty()) {
          ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTargetDB =
            reportSynthesisSrfProgressTargetCasesManager
              .saveReportSynthesisSrfProgressTargetCases(reportSynthesisSrfProgressTarget);
          if (reportSynthesisSrfProgressTargetDB != null) {
            srfProgressTargetId = reportSynthesisSrfProgressTargetDB.getId();
          }
          for (ProgressTargetCaseGeographicScope scope : scopeList) {
            scope.setTargetCase(reportSynthesisSrfProgressTargetDB);
            progressTargetCaseGeographicScopeManager.saveProgressTargetCaseGeographicScope(scope);
          }

          for (ProgressTargetCaseGeographicRegion location : regionList) {
            location.setTargetCase(reportSynthesisSrfProgressTargetDB);
            progressTargetCaseGeographicRegionManager.saveProgressTargetCaseGeographicRegion(location);
          }

          for (ProgressTargetCaseGeographicCountry location : countryList) {
            location.setTargetCase(reportSynthesisSrfProgressTargetDB);
            progressTargetCaseGeographicCountryManager.saveProgressTargetCaseGeographicCountry(location);
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
    }

    return srfProgressTargetId;
  }

  public ResponseEntity<SrfProgressTowardsTargetDTO> deleteProgressTowardsById(Long id, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    ReportSynthesisSrfProgressTargetCases srfProgressTarget = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteProgressTowardsById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteProgressTowardsById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("deleteProgressTowards", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    String strippedRepoPhase = StringUtils.stripToNull(repoPhase);
    Phase phase = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
        && p.getYear() == repoYear && StringUtils.equalsIgnoreCase(p.getName(), strippedRepoPhase) && p.isActive())
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors
        .add(new FieldErrorDTO("deleteProgressTowards", "phase", repoPhase + ' ' + repoYear + " is an invalid phase"));
    } else {
      if (!StringUtils.equalsIgnoreCase(phase.getCrp().getAcronym(), strippedEntityAcronym)) {
        fieldErrors.add(new FieldErrorDTO("deleteProgressTowards", "FlagshipEntity",
          "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym " + CGIARentityAcronym));
      }
    }

    if (fieldErrors.isEmpty()) {
      srfProgressTarget = reportSynthesisSrfProgressTargetCasesManager.getReportSynthesisSrfProgressTargetCasesById(id);
      if (srfProgressTarget != null && srfProgressTarget.isActive() == true) {
        if (srfProgressTarget.getReportSynthesisSrfProgress() == null) {
          fieldErrors.add(new FieldErrorDTO("findProgressTowardsById", "ReportSynthesisSrfProgressEntity",
            "There is no Report Synthesis SRF Progress assosiated to this entity!"));
        } else {
          if (srfProgressTarget.getReportSynthesisSrfProgress().getReportSynthesis() == null) {
            fieldErrors.add(new FieldErrorDTO("findProgressTowardsById", "ReportSynthesisEntity",
              "There is no Report Synthesis assosiated to this entity!"));
          } else {
            if (srfProgressTarget.getReportSynthesisSrfProgress().getReportSynthesis().getPhase() == null) {
              fieldErrors.add(new FieldErrorDTO("findProgressTowardsById", "PhaseEntity",
                "There is no Phase assosiated to this entity!"));
            } else {
              if (srfProgressTarget.getReportSynthesisSrfProgress().getReportSynthesis().getPhase().getId() != phase
                .getId()) {
                fieldErrors.add(new FieldErrorDTO("findProgressTowardsById", "ReportSynthesisSrfProgressTargetEntity",
                  "The Report Synthesis Srf Progress Target with id " + id
                    + " do not correspond to the phase entered"));
              } else {
                List<ProgressTargetCaseGeographicRegion> regions =
                  srfProgressTarget.getProgressTargetCaseGeographicRegions().stream().collect(Collectors.toList());
                for (ProgressTargetCaseGeographicRegion region : regions) {
                  progressTargetCaseGeographicRegionManager.deleteProgressTargetCaseGeographicRegion(region.getId());
                }

                List<ProgressTargetCaseGeographicCountry> countries =
                  srfProgressTarget.getProgressTargetCaseGeographicCountries().stream().collect(Collectors.toList());

                for (ProgressTargetCaseGeographicCountry country : countries) {
                  progressTargetCaseGeographicCountryManager.deleteProgressTargetCaseGeographicCountry(country.getId());
                }

                List<ProgressTargetCaseGeographicScope> scopes =
                  srfProgressTarget.getProgressTargetCaseGeographicScopes().stream().collect(Collectors.toList());
                for (ProgressTargetCaseGeographicScope scope : scopes) {
                  progressTargetCaseGeographicScopeManager.deleteProgressTargetCaseGeographicScope(scope.getId());
                }
                reportSynthesisSrfProgressTargetCasesManager
                  .deleteReportSynthesisSrfProgressTargetCases(srfProgressTarget.getId());
              }
            }
          }
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteProgressTowardsById", "ReportSynthesisSrfProgressTargetEntity",
          id + " is an invalid Report Synthesis Srf Progress Target Code"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(srfProgressTarget)
      .map(this.srfProgressTowardsTargetMapper::reportSynthesisSrfProgressCasesTargetToSrfProgressTowardsTargetsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public List<SrfProgressTowardsTargetDTO> findAllProgressTowardsByGlobalUnit(String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    List<SrfProgressTowardsTargetDTO> progressTowardsTargets = new ArrayList<>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    List<ReportSynthesisSrfProgressTargetCases> reportSynthesisSrfProgressTargetList =
      new ArrayList<ReportSynthesisSrfProgressTargetCases>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findProgressTowardsByGlobalUnit", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("findProgressTowardsByGlobalUnit", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    String strippedRepoPhase = StringUtils.stripToNull(repoPhase);
    Phase phase = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
        && p.getYear() >= APConstants.CLARISA_AVALIABLE_INFO_YEAR && p.getYear() == repoYear
        && StringUtils.equalsIgnoreCase(p.getName(), strippedRepoPhase) && p.isActive())
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("findProgressTowardsByGlobalUnit", "phase",
        repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    if (fieldErrors.isEmpty()) {
      // not all ReportSynthesis have a ReportSynthesisSrfProgress, so we need to filter out those to avoid exceptions


      for (ReportSynthesisSrfProgressTargetCases progressTowarsCase : reportSynthesisManager.findAll().stream()
        .filter(rs -> rs.getPhase().getId() == phase.getId() && rs.getReportSynthesisSrfProgress() != null
          && rs.getReportSynthesisSrfProgress().isActive() == true && rs.isActive() == true)
        .flatMap(rs -> rs.getReportSynthesisSrfProgress().getReportSynthesisSrfProgressTargetsCases().stream())
        .collect(Collectors.toList())) {

        progressTowarsCase.setGeographicScopes(progressTowarsCase.getProgressTargetCaseGeographicScopes().stream()
          .filter(c -> c.isActive()).collect(Collectors.toList()));
        progressTowarsCase.setGeographicCountries(progressTowarsCase.getProgressTargetCaseGeographicCountries().stream()
          .filter(c -> c != null && c.isActive()).collect(Collectors.toList()));
        progressTowarsCase.setGeographicRegions(progressTowarsCase.getProgressTargetCaseGeographicRegions().stream()
          .filter(c -> c != null && c.isActive()).collect(Collectors.toList()));
        reportSynthesisSrfProgressTargetList.add(progressTowarsCase);
      }

      progressTowardsTargets = reportSynthesisSrfProgressTargetList.stream()
        .map(srfProgressTowardsTargetMapper::reportSynthesisSrfProgressCasesTargetToSrfProgressTowardsTargetsDTO)
        .collect(Collectors.toList());
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return progressTowardsTargets;
  }

  /**
   * Find an ReportSynthesisSrfProgressTarget by Id and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a SrfProgressTowardsTargetDTO with the reportSynthesisSrfProgressTarget Item
   */
  public ResponseEntity<SrfProgressTowardsTargetDTO> findProgressTowardsById(Long id, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    // TODO: Include all security validations
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTarget = null;
    ReportSynthesis reportSynthesis = null;
    ReportSynthesisSrfProgress reportSynthesisSrfProgress = null;
    LiaisonInstitution liaisonInstitution = null;
    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findProgressTowardsById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("findProgressTowardsById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    String strippedRepoPhase = StringUtils.stripToNull(repoPhase);
    Phase phase = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
        && p.getYear() >= APConstants.CLARISA_AVALIABLE_INFO_YEAR && p.getYear() == repoYear
        && StringUtils.equalsIgnoreCase(p.getName(), strippedRepoPhase) && p.isActive())
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(
        new FieldErrorDTO("findProgressTowardsById", "phase", repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    if (globalUnitEntity != null) {
      liaisonInstitution =
        liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(
          new FieldErrorDTO("findProgressTowards", "LiaisonInstitutionEntity", "A Liaison Institution with the acronym "
            + APConstants.CLARISA_ACRONYM_PMU + " could not be found for " + CGIARentityAcronym));
      }
    }

    if (fieldErrors.isEmpty()) {
      if (liaisonInstitution != null) {
        reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      }
      // start ReportSynthesisSrfProgress
      if (reportSynthesis != null) {
        reportSynthesisSrfProgress = reportSynthesis.getReportSynthesisSrfProgress();
      }

      reportSynthesisSrfProgressTarget = reportSynthesisSrfProgress.getReportSynthesisSrfProgressTargetsCases().stream()
        .filter(c -> c.getId().longValue() == id.longValue()).findFirst().orElse(null);
      reportSynthesisSrfProgressTarget.setReportSynthesisSrfProgress(reportSynthesisSrfProgress);
      if (reportSynthesisSrfProgressTarget == null || reportSynthesisSrfProgressTarget.isActive() == false) {
        fieldErrors.add(new FieldErrorDTO("findProgressTowardsById", "ReportSynthesisSrfProgressTargetEntity",
          id + " is an invalid id of a Report Synthesis Srf Progress Target"));
      } else {
        if (reportSynthesisSrfProgressTarget.getReportSynthesisSrfProgress() == null) {
          fieldErrors.add(new FieldErrorDTO("findProgressTowardsById", "ReportSynthesisSrfProgressEntity",
            "There is no Report Synthesis SRF Progress assosiated to this entity!"));
        } else {
          if (reportSynthesisSrfProgressTarget.getReportSynthesisSrfProgress().getReportSynthesis() == null) {
            fieldErrors.add(new FieldErrorDTO("findProgressTowardsById", "ReportSynthesisEntity",
              "There is no Report Synthesis assosiated to this entity!"));
          } else {
            if (reportSynthesisSrfProgressTarget.getReportSynthesisSrfProgress().getReportSynthesis()
              .getPhase() == null) {
              fieldErrors.add(new FieldErrorDTO("findProgressTowardsById", "PhaseEntity",
                "There is no Phase assosiated to this entity!"));
            } else {
              if (reportSynthesisSrfProgressTarget.getReportSynthesisSrfProgress().getReportSynthesis().getPhase()
                .getId() != phase.getId()) {
                fieldErrors.add(new FieldErrorDTO("findProgressTowardsById", "ReportSynthesisSrfProgressTargetEntity",
                  "The Report Synthesis Srf Progress Target with id " + id
                    + " do not correspond to the phase entered"));
              } else {
                reportSynthesisSrfProgressTarget
                  .setGeographicScopes(reportSynthesisSrfProgressTarget.getProgressTargetCaseGeographicScopes().stream()
                    .filter(c -> c.isActive()).collect(Collectors.toList()));

                reportSynthesisSrfProgressTarget
                  .setGeographicCountries(reportSynthesisSrfProgressTarget.getProgressTargetCaseGeographicCountries()
                    .stream().filter(c -> c != null && c.isActive()).collect(Collectors.toList()));
                reportSynthesisSrfProgressTarget
                  .setGeographicRegions(reportSynthesisSrfProgressTarget.getProgressTargetCaseGeographicRegions()
                    .stream().filter(c -> c != null && c.isActive()).collect(Collectors.toList()));

              }
            }
          }
        }
      }
    }

    // TODO more validations!

    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      // fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(reportSynthesisSrfProgressTarget)
      .map(this.srfProgressTowardsTargetMapper::reportSynthesisSrfProgressCasesTargetToSrfProgressTowardsTargetsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Update a ReportSynthesisSrfProgressTarget by Id and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a SrfProgressTowardsTargetDTO with the ReportSynthesisSrfProgressTarget Item
   */
  public Long putProgressTowardsById(Long idProgressTowards,
    NewSrfProgressTowardsTargetDTO newSrfProgressTowardsTargetDTO, String CGIARentityAcronym, User user) {
    Long idProgressTowardsDB = null;
    Phase phase = null;
    ReportSynthesis reportSynthesis = null;
    ReportSynthesisSrfProgress reportSynthesisSrfProgress = null;
    ReportSynthesisSrfProgressTargetCases reportSynthesisSrfProgressTarget = null;
    LiaisonInstitution liaisonInstitution = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putProgressTowards", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putProgressTowards", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putProgressTowards", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newSrfProgressTowardsTargetDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("putProgressTowards", "PhaseEntity", "Phase must not be null"));
    } else {
      String strippedPhaseName = StringUtils.stripToNull(newSrfProgressTowardsTargetDTO.getPhase().getName());
      if (strippedPhaseName == null || newSrfProgressTowardsTargetDTO.getPhase().getYear() == null
      // DANGER! Magic number ahead
        || newSrfProgressTowardsTargetDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("putProgressTowards", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
            && p.getYear() == newSrfProgressTowardsTargetDTO.getPhase().getYear()
            && StringUtils.equalsIgnoreCase(p.getName(), strippedPhaseName) && p.isActive())
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors
            .add(new FieldErrorDTO("putProgressTowards", "phase", newSrfProgressTowardsTargetDTO.getPhase().getName()
              + ' ' + newSrfProgressTowardsTargetDTO.getPhase().getYear() + " is an invalid phase"));
        }
      }
    }

    if (globalUnitEntity != null) {
      liaisonInstitution =
        liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(
          new FieldErrorDTO("putProgressTowards", "LiaisonInstitutionEntity", "A Liaison Institution with the acronym "
            + APConstants.CLARISA_ACRONYM_PMU + " could not be found for " + CGIARentityAcronym));
      }
    }

    if (liaisonInstitution != null) {
      reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      if (reportSynthesis == null) {
        fieldErrors.add(new FieldErrorDTO("putProgressTowards", "ReportSynthesisEntity",
          "A report entity linked to the Phase with id " + phase.getId() + " and Liaison Institution with id "
            + liaisonInstitution.getId() + " could not be found"));
      } else {
        reportSynthesisSrfProgress = reportSynthesis.getReportSynthesisSrfProgress();
        if (reportSynthesisSrfProgress == null) {
          fieldErrors.add(new FieldErrorDTO("putProgressTowards", "ReportSynthesisEntity",
            "There is no Report Synthesis SRF Progress linked to the Report Synthesis"));
        }
      }
    }
    reportSynthesisSrfProgressTarget = reportSynthesisSrfProgress.getReportSynthesisSrfProgressTargetsCases().stream()
      .filter(c -> c.getId().longValue() == idProgressTowards.longValue()).findFirst().orElse(null);
    reportSynthesisSrfProgressTarget.setReportSynthesisSrfProgress(reportSynthesisSrfProgress);

    if (reportSynthesisSrfProgressTarget == null || reportSynthesisSrfProgressTarget.isActive() == false) {
      fieldErrors.add(new FieldErrorDTO("putProgressTowards", "ReportSynthesisSrfProgressTargetEntity",
        idProgressTowards + " is an invalid Report Synthesis Srf Progress Target Code"));
    }

    if (phase != null && !phase.getEditable()) {
      fieldErrors
        .add(new FieldErrorDTO("putProgressTowards", "phase", newSrfProgressTowardsTargetDTO.getPhase().getName() + ' '
          + newSrfProgressTowardsTargetDTO.getPhase().getYear() + " is a closed phase"));
    }
    if (fieldErrors.isEmpty()) {

      idProgressTowardsDB = reportSynthesisSrfProgressTarget.getId();
      SrfSloIndicatorTarget srfSloIndicatorTarget = null;
      SrfSloIndicator srfSloIndicator = null;
      Long id = null;

      reportSynthesisSrfProgressTarget
        .setAdditionalContribution(newSrfProgressTowardsTargetDTO.getAdditionalContribution());

      String strippedBriefSummary = StringUtils.stripToNull(newSrfProgressTowardsTargetDTO.getBriefSummary());
      if (strippedBriefSummary != null) {
        reportSynthesisSrfProgressTarget.setBriefSummary(strippedBriefSummary);
        reportSynthesisSrfProgressTarget.setIsQAIncluded(new Boolean(false));
      } else {
        fieldErrors.add(new FieldErrorDTO("putProgressTowards", "Summary", "Please enter a brief summary"));
      }

      strippedId = StringUtils.stripToNull(newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId());
      if (strippedId != null) {
        id = this.tryParseLong(strippedId, fieldErrors, "putProgressTowards", "SrfSloIndicator");
        if (id != null) {
          srfSloIndicator = srfSloIndicatorManager.getSrfSloIndicatorById(id);
          if (srfSloIndicator == null) {
            fieldErrors.add(new FieldErrorDTO("putProgressTowards", "SrfSloIndicatorEntity",
              newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId() + " is an invalid Srf Slo Indicator code."));
          } else {
            srfSloIndicatorTarget = srfSloIndicator.getSrfSloIndicatorTargets().stream()
              .sorted((t1, t2) -> Integer.compare(t1.getYear(), t2.getYear()))
              .filter(t -> t.getYear() >= Calendar.getInstance().get(Calendar.YEAR)).findFirst().orElse(null);
            // TODO write a better error message
            if (srfSloIndicatorTarget == null) {
              fieldErrors.add(new FieldErrorDTO("putProgressTowards", "SrfSloIndicatorTargetEntity",
                newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId()
                  + " is from a year that have not been activated."));
            }
          }
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("putProgressTowards", "SrfSloIndicatorTargetEntity",
          "Srf Slo Indicator code can not be null nor empty."));
      }

      reportSynthesisSrfProgressTarget.setSrfSloIndicatorTarget(srfSloIndicatorTarget);


      // geographicscope
      List<ProgressTargetCaseGeographicScope> scopeList = new ArrayList<ProgressTargetCaseGeographicScope>();
      List<ProgressTargetCaseGeographicScope> deleteScopeList = new ArrayList<ProgressTargetCaseGeographicScope>();
      for (GeographicScopeDTO scopesDTO : newSrfProgressTowardsTargetDTO.getGeographicScope()) {
        boolean found = false;
        for (ProgressTargetCaseGeographicScope scope : reportSynthesisSrfProgressTarget
          .getProgressTargetCaseGeographicScopes().stream().collect(Collectors.toList())) {
          if (scope.getRepIndGeographicScope().getId().longValue() == scopesDTO.getCode().longValue()) {
            found = true;
          }
        }
        if (!found) {
          RepIndGeographicScope geographicScope =
            repIndGeographicScopeManager.getRepIndGeographicScopeById(scopesDTO.getCode());
          if (geographicScope != null) {
            ProgressTargetCaseGeographicScope scope = new ProgressTargetCaseGeographicScope();
            scope.setPhase(phase);
            scope.setTargetCase(reportSynthesisSrfProgressTarget);
            scope.setRepIndGeographicScope(geographicScope);
            scopeList.add(scope);
          } else {
            fieldErrors.add(new FieldErrorDTO("putProgressTowards", "Geographic Scope",
              "There is no Report Synthesis SRF Progress linked to the Report Synthesis"));
          }
        }
      }

      for (ProgressTargetCaseGeographicScope scope : reportSynthesisSrfProgressTarget
        .getProgressTargetCaseGeographicScopes().stream().collect(Collectors.toList())) {
        boolean found = true;
        for (GeographicScopeDTO scopesDTO : newSrfProgressTowardsTargetDTO.getGeographicScope()) {
          if (scope.getRepIndGeographicScope().getId().longValue() == scopesDTO.getCode().longValue()) {
            found = false;
          }
        }
        if (found) {
          deleteScopeList.add(scope);
        }
      }
      List<ProgressTargetCaseGeographicRegion> regionList = new ArrayList<ProgressTargetCaseGeographicRegion>();
      List<ProgressTargetCaseGeographicRegion> deleteRegionList = new ArrayList<ProgressTargetCaseGeographicRegion>();
      // regions
      for (RegionDTO regionsDTO : newSrfProgressTowardsTargetDTO.getRegions()) {
        LocElement loc = locElementManager.getLocElementByNumericISOCode(regionsDTO.getUM49Code());
        if (loc != null) {
          boolean found = false;
          for (ProgressTargetCaseGeographicRegion region : reportSynthesisSrfProgressTarget
            .getProgressTargetCaseGeographicRegions().stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
            if (region.getLocElement().getIsoNumeric().longValue() == regionsDTO.getUM49Code()) {
              found = true;
            }
          }
          if (!found) {
            ProgressTargetCaseGeographicRegion region = new ProgressTargetCaseGeographicRegion();
            region.setTargetCase(reportSynthesisSrfProgressTarget);
            region.setLocElement(loc);
            regionList.add(region);
          }
        } else {
          fieldErrors.add(new FieldErrorDTO("putProgressTowards", "Regions",
            regionsDTO.getUM49Code().longValue() + " is not a valid Region UN49 code"));

        }
      }
      for (ProgressTargetCaseGeographicRegion region : reportSynthesisSrfProgressTarget
        .getProgressTargetCaseGeographicRegions().stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
        boolean found = true;
        for (RegionDTO regionsDTO : newSrfProgressTowardsTargetDTO.getRegions()) {
          if (regionsDTO.getUM49Code().longValue() == region.getLocElement().getIsoNumeric().longValue()) {
            found = false;
          }
        }
        if (found) {
          deleteRegionList.add(region);
        }
      }

      // countries
      List<ProgressTargetCaseGeographicCountry> countryList = new ArrayList<ProgressTargetCaseGeographicCountry>();
      List<ProgressTargetCaseGeographicCountry> deleteCountryList =
        new ArrayList<ProgressTargetCaseGeographicCountry>();
      for (CountryDTO countriesDTO : newSrfProgressTowardsTargetDTO.getCountries()) {
        LocElement loc = locElementManager.getLocElementByNumericISOCode(countriesDTO.getCode());
        if (loc != null) {
          boolean found = false;
          for (ProgressTargetCaseGeographicCountry countries : reportSynthesisSrfProgressTarget
            .getProgressTargetCaseGeographicCountries().stream().filter(c -> c.isActive())
            .collect(Collectors.toList())) {
            if (countries.getLocElement().getIsoNumeric().longValue() == countriesDTO.getCode().longValue()) {
              found = true;
            }
          }
          if (!found) {
            ProgressTargetCaseGeographicCountry country = new ProgressTargetCaseGeographicCountry();
            country.setTargetCase(reportSynthesisSrfProgressTarget);
            country.setLocElement(loc);
            countryList.add(country);
          }
        } else {
          fieldErrors.add(new FieldErrorDTO("putProgressTowards", "Countries",
            countriesDTO.getCode().longValue() + " is not a valid ISO numeric code"));

        }
      }
      for (ProgressTargetCaseGeographicCountry countries : reportSynthesisSrfProgressTarget
        .getProgressTargetCaseGeographicCountries().stream().filter(c -> c.isActive()).collect(Collectors.toList())) {
        boolean found = true;
        for (CountryDTO countryDTO : newSrfProgressTowardsTargetDTO.getCountries()) {
          if (countryDTO.getCode().longValue() == countries.getLocElement().getIsoNumeric().longValue()) {
            found = false;
          }
        }
        if (found) {
          deleteCountryList.add(countries);
        }
      }
      if (fieldErrors.isEmpty()) {
        reportSynthesisSrfProgressTargetCasesManager
          .saveReportSynthesisSrfProgressTargetCases(reportSynthesisSrfProgressTarget);
        for (ProgressTargetCaseGeographicScope scope : scopeList) {
          progressTargetCaseGeographicScopeManager.saveProgressTargetCaseGeographicScope(scope);
        }
        for (ProgressTargetCaseGeographicScope scope : deleteScopeList) {
          progressTargetCaseGeographicScopeManager.deleteProgressTargetCaseGeographicScope(scope.getId());
        }
        for (ProgressTargetCaseGeographicRegion regions : regionList) {
          progressTargetCaseGeographicRegionManager.saveProgressTargetCaseGeographicRegion(regions);
        }
        for (ProgressTargetCaseGeographicRegion regions : deleteRegionList) {
          progressTargetCaseGeographicRegionManager.deleteProgressTargetCaseGeographicRegion(regions.getId());
        }

        for (ProgressTargetCaseGeographicCountry countries : countryList) {
          progressTargetCaseGeographicCountryManager.saveProgressTargetCaseGeographicCountry(countries);
        }
        for (ProgressTargetCaseGeographicCountry countries : deleteCountryList) {
          progressTargetCaseGeographicCountryManager.deleteProgressTargetCaseGeographicCountry(countries.getId());
        }
      }
    }
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return idProgressTowardsDB;
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
