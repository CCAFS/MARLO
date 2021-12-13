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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.statusPlannedOutcomes;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpOutcomeSubIdo;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestoneLink;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewStatusPlannedOutcomeDTO;
import org.cgiar.ccafs.marlo.rest.dto.StatusPlannedOutcomesDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.StatusPlannedOutcomesMapper;

import java.util.ArrayList;
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
public class StatusPlannedOutcomesItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private CrpProgramManager crpProgramManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager;
  private ReportSynthesisFlagshipProgressOutcomeMilestoneManager reportSynthesisFlagshipProgressOutcomeMilestoneManager;
  private ReportSynthesisFlagshipProgressCrossCuttingMarkerManager reportSynthesisFlagshipProgressCrossCuttingMarkerManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager;
  // private GeneralStatusManager generalStatusManager;
  // private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;
  // private RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager;
  // private CrpMilestoneManager crpMilestoneManager;

  private StatusPlannedOutcomesMapper statusPlannedOutcomesMapper;


  @Inject
  public StatusPlannedOutcomesItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, CrpProgramOutcomeManager crpProgramOutcomeManager,
    ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager,
    ReportSynthesisFlagshipProgressOutcomeMilestoneManager reportSynthesisFlagshipProgressOutcomeMilestoneManager,
    ReportSynthesisFlagshipProgressCrossCuttingMarkerManager reportSynthesisFlagshipProgressCrossCuttingMarkerManager,
    LiaisonInstitutionManager liaisonInstitutionManager, StatusPlannedOutcomesMapper statusPlannedOutcomesMapper,
    ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.crpProgramManager = crpProgramManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFlagshipProgressOutcomeManager = reportSynthesisFlagshipProgressOutcomeManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    // this.crpMilestoneManager = crpMilestoneManager;
    // this.generalStatusManager = generalStatusManager;
    // this.cgiarCrossCuttingMarkerManager = cgiarCrossCuttingMarkerManager;
    // this.repIndGenderYouthFocusLevelManager = repIndGenderYouthFocusLevelManager;
    this.reportSynthesisFlagshipProgressOutcomeMilestoneManager =
      reportSynthesisFlagshipProgressOutcomeMilestoneManager;
    this.reportSynthesisFlagshipProgressCrossCuttingMarkerManager =
      reportSynthesisFlagshipProgressCrossCuttingMarkerManager;
    this.statusPlannedOutcomesMapper = statusPlannedOutcomesMapper;
    this.reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager =
      reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager;
  }

  private int countWords(String string) {
    int wordCount = 0;
    string = StringUtils.stripToEmpty(string);
    if (!string.isEmpty()) {
      String[] words = StringUtils.split(string);
      wordCount = words.length;
    }

    return wordCount;
  }

  public Long createStatusPlannedOutcome(NewStatusPlannedOutcomeDTO newStatusPlannedOutcomeDTO,
    String CGIARentityAcronym, User user) {
    Long plannedOutcomeStatusID = null;
    CrpProgram crpProgram = null;
    Phase phase = null;
    LiaisonInstitution liaisonInstitution = null;

    CrpProgramOutcome crpProgramOutcome = null;
    ReportSynthesis reportSynthesis = null;
    ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress = null;
    ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome = null;

    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    if (newStatusPlannedOutcomeDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "PhaseEntity", "Phase must not be null"));
    } else {
      String strippedPhaseName = StringUtils.stripToNull(newStatusPlannedOutcomeDTO.getPhase().getName());
      if (strippedPhaseName == null || newStatusPlannedOutcomeDTO.getPhase().getYear() == null
      // DANGER! Magic number ahead
        || newStatusPlannedOutcomeDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
            && p.getYear() == newStatusPlannedOutcomeDTO.getPhase().getYear()
            && StringUtils.equalsIgnoreCase(p.getName(), strippedPhaseName) && p.isActive())
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors.add(
            new FieldErrorDTO("createStatusPlannedOutcome", "phase", newStatusPlannedOutcomeDTO.getPhase().getName()
              + ' ' + newStatusPlannedOutcomeDTO.getPhase().getYear() + " is an invalid phase"));
        }
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), CGIARentityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("createStatusPlannedOutcome", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.stripToNull(newStatusPlannedOutcomeDTO.getCrpProgramCode());
    if (strippedId != null) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(strippedId);
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "FlagshipEntity",
          newStatusPlannedOutcomeDTO.getCrpProgramCode() + " is an invalid CRP Program SMO code."));
      } else {
        if (!StringUtils.equalsIgnoreCase(crpProgram.getCrp().getAcronym(), strippedEntityAcronym)) {
          fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "FlagshipEntity",
            "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym "
              + CGIARentityAcronym));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "FlagshipEntity",
        "CRP Program SMO code can not be null nor empty."));
    }

    if (globalUnitEntity != null && crpProgram != null) {
      liaisonInstitution =
        liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "LiaisonInstitutionEntity",
          "A Liaison Institution with the acronym " + crpProgram.getAcronym() + " could not be found for "
            + CGIARentityAcronym));
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "LiaisonInstitutionEntity",
        "A Liaison Institution can not be found if either the CRP or the Flagship/Module is invalid"));
    }

    long wordCount = this.countWords(newStatusPlannedOutcomeDTO.getSumary());
    if (wordCount > 100) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Summary Narrative",
        "Summary Narrative excedes the maximum number of words (100 words)"));
    }

    strippedId = StringUtils.stripToNull(newStatusPlannedOutcomeDTO.getCrpOutcomeCode());
    if (strippedId != null) {
      crpProgramOutcome = crpProgramOutcomeManager.getCrpProgramOutcome(strippedId, phase);
      if (crpProgramOutcome == null) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Outcome", "is an invalid CRP Outcome"));
      } else {
        if (!crpProgramOutcome.getCrpProgram().getId().equals(crpProgram.getId())) {
          fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Outcome",
            "The entered CRP Outcome do not correspond with the CRP Program given"));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "CrpProgramOutcomeEntity",
        "CRP Program Outcome composed ID code can not be null nor empty."));
    }

    if (fieldErrors.isEmpty()) {
      reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      if (reportSynthesis == null) {
        reportSynthesis = new ReportSynthesis();
        reportSynthesis.setLiaisonInstitution(liaisonInstitution);
        reportSynthesis.setPhase(phase);
        // set report synthesis flagship progress
        reportSynthesisFlagshipProgress = new ReportSynthesisFlagshipProgress();
        reportSynthesisFlagshipProgress.setCreatedBy(user);
        // set report synthesis flagship progress outcome
        reportSynthesisFlagshipProgressOutcome = new ReportSynthesisFlagshipProgressOutcome();
        reportSynthesisFlagshipProgressOutcome.setCrpProgramOutcome(crpProgramOutcome);
        reportSynthesisFlagshipProgressOutcome.setSummary(newStatusPlannedOutcomeDTO.getSumary());

        if (fieldErrors.isEmpty()) {
          reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
          if (reportSynthesis != null) {
            reportSynthesisFlagshipProgress.setReportSynthesis(reportSynthesis);
            reportSynthesisFlagshipProgress = reportSynthesisFlagshipProgressManager
              .saveReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
            if (reportSynthesisFlagshipProgress != null) {
              reportSynthesisFlagshipProgressOutcome
                .setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
              reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
                .saveReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
              plannedOutcomeStatusID = reportSynthesisFlagshipProgressOutcome.getId();
            }
          }
        }
      } else {
        // existing report synthesis progress
        reportSynthesisFlagshipProgress =
          reportSynthesisFlagshipProgressManager.getReportSynthesisFlagshipProgressById(reportSynthesis.getId());
        if (reportSynthesisFlagshipProgress == null) {
          // if progress does not exist, create a new one
          reportSynthesisFlagshipProgress = new ReportSynthesisFlagshipProgress();
          reportSynthesisFlagshipProgress.setCreatedBy(user);
          reportSynthesisFlagshipProgress.setReportSynthesis(reportSynthesis);
          // set report synthesis flagship progress outcome
          reportSynthesisFlagshipProgressOutcome = new ReportSynthesisFlagshipProgressOutcome();
          reportSynthesisFlagshipProgressOutcome.setCrpProgramOutcome(crpProgramOutcome);
          reportSynthesisFlagshipProgressOutcome.setSummary(newStatusPlannedOutcomeDTO.getSumary());
          if (fieldErrors.isEmpty()) {
            reportSynthesisFlagshipProgress.setReportSynthesis(reportSynthesis);
            reportSynthesisFlagshipProgress = reportSynthesisFlagshipProgressManager
              .saveReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
            reportSynthesisFlagshipProgressOutcome.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
            reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
              .saveReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
            plannedOutcomeStatusID = reportSynthesisFlagshipProgressOutcome.getId();
          }
        } else {
          // if exist validate if exist progress outcome
          reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
            .getOutcomeId(reportSynthesisFlagshipProgress.getId(), crpProgramOutcome.getId());
          if (reportSynthesisFlagshipProgressOutcome == null) {
            // set report synthesis flagship progress outcome
            reportSynthesisFlagshipProgressOutcome = new ReportSynthesisFlagshipProgressOutcome();
            reportSynthesisFlagshipProgressOutcome.setCrpProgramOutcome(crpProgramOutcome);
            reportSynthesisFlagshipProgressOutcome.setSummary(newStatusPlannedOutcomeDTO.getSumary());

            if (fieldErrors.isEmpty()) {
              reportSynthesisFlagshipProgressOutcome
                .setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
              reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
                .saveReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
              plannedOutcomeStatusID = reportSynthesisFlagshipProgressOutcome.getId();
            }
          } else {
            fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "ReportSynthesisFlagshipOutocome",
              "A Report Synthesis Flagship Outcome alredy exists. If you want to update it, please use the PUT method"));
          }

        }
      }
    }

    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      fieldErrors.stream().forEach(f -> System.out.println(f.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return plannedOutcomeStatusID;
  }

  public ResponseEntity<StatusPlannedOutcomesDTO> findStatusPlannedOutcome(String outcomeID, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    String strippedId = null;
    LiaisonInstitution liaisonInstitution = null;
    CrpProgramOutcome crpProgramOutcome = null;
    ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findStatusPlannedOutcomeById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("findStatusPlannedOutcomeById", "GlobalUnitEntity",
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
      fieldErrors.add(new FieldErrorDTO("findStatusPlannedOutcomeById", "phase",
        repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), CGIARentityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("findStatusPlannedOutcomeById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.stripToNull(outcomeID);
    if (strippedId != null && phase != null) {
      crpProgramOutcome = crpProgramOutcomeManager.getCrpProgramOutcome(strippedId, phase);
      if (crpProgramOutcome == null) {
        fieldErrors.add(
          new FieldErrorDTO("findStatusPlannedOutcomeById", "Outcome", strippedId + " is an invalid CRP Outcome code"));
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("findStatusPlannedOutcomeById", "CrpProgramOutcomeEntity",
        "A CRP Program Outcome with composed ID " + strippedId + " do not exist for the given phase."));
    }

    if (globalUnitEntity != null && crpProgramOutcome != null) {
      liaisonInstitution = liaisonInstitutionManager.findByAcronymAndCrp(crpProgramOutcome.getCrpProgram().getAcronym(),
        globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("findStatusPlannedOutcomeById", "LiaisonInstitutionEntity",
          "A Liaison Institution with the acronym " + crpProgramOutcome.getCrpProgram().getAcronym()
            + " could not be found for " + CGIARentityAcronym));
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("findStatusPlannedOutcomeById", "LiaisonInstitutionEntity",
        "A Liaison Institution can not be found if either the CRP or the Flagship/Module is invalid"));
    }

    if (fieldErrors.isEmpty()) {
      ReportSynthesis reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      if (reportSynthesis != null) {
        ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress =
          reportSynthesisFlagshipProgressManager.getReportSynthesisFlagshipProgressById(reportSynthesis.getId());
        if (reportSynthesisFlagshipProgress != null) {
          reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
            .getOutcomeId(reportSynthesisFlagshipProgress.getId(), crpProgramOutcome.getId());
          // validate Synthesis FlagshipProgress Outcome
          if (reportSynthesisFlagshipProgressOutcome != null) {
            // setting subIDOS
            List<CrpOutcomeSubIdo> subIdos = crpProgramOutcome.getCrpOutcomeSubIdos().stream()
              .filter(c -> c.isActive() && c.getCrpProgramOutcome().getPhase().getId().equals(phase.getId()))
              .collect(Collectors.toList());
            reportSynthesisFlagshipProgressOutcome.getCrpProgramOutcome().setSubIdos(subIdos);
            // setting milestones empty list
            List<ReportSynthesisFlagshipProgressOutcomeMilestone> reportSynthesisFlagshipProgressOutcomeMilestoneList =
              new ArrayList<ReportSynthesisFlagshipProgressOutcomeMilestone>();

            for (ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone : reportSynthesisFlagshipProgressOutcome
              .getReportSynthesisFlagshipProgressOutcomeMilestones().stream().filter(c -> c.isActive())
              .collect(Collectors.toList())) {
              List<ReportSynthesisFlagshipProgressCrossCuttingMarker> markersList =
                new ArrayList<ReportSynthesisFlagshipProgressCrossCuttingMarker>();
              List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink> evidenceLinks =
                new ArrayList<ReportSynthesisFlagshipProgressOutcomeMilestoneLink>();
              reportSynthesisFlagshipProgressOutcomeMilestone = reportSynthesisFlagshipProgressOutcomeMilestoneManager
                .getReportSynthesisFlagshipProgressOutcomeMilestoneById(
                  reportSynthesisFlagshipProgressOutcomeMilestone.getId());
              for (ReportSynthesisFlagshipProgressCrossCuttingMarker marker : reportSynthesisFlagshipProgressOutcomeMilestone
                .getReportSynthesisFlagshipProgressCrossCuttingMarkers().stream().filter(c -> c.isActive())
                .collect(Collectors.toList())) {
                marker = reportSynthesisFlagshipProgressCrossCuttingMarkerManager
                  .getReportSynthesisFlagshipProgressCrossCuttingMarkerById(marker.getId());
                markersList.add(marker);
              }
              for (ReportSynthesisFlagshipProgressOutcomeMilestoneLink link : reportSynthesisFlagshipProgressOutcomeMilestone
                .getReportSynthesisFlagshipProgressOutcomeMilestoneLinks().stream().filter(c -> c.isActive())
                .collect(Collectors.toList())) {
                link = reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager
                  .getReportSynthesisFlagshipProgressOutcomeMilestoneLinkById(link.getId());
                evidenceLinks.add(link);
              }
              reportSynthesisFlagshipProgressOutcomeMilestone.setMarkers(markersList);
              reportSynthesisFlagshipProgressOutcomeMilestone.setLinks(evidenceLinks);
              reportSynthesisFlagshipProgressOutcomeMilestoneList.add(reportSynthesisFlagshipProgressOutcomeMilestone);
            }
            reportSynthesisFlagshipProgressOutcome.setMilestones(reportSynthesisFlagshipProgressOutcomeMilestoneList);
          } else {
            fieldErrors.add(new FieldErrorDTO("findStatusPlannedOutcomeById", "ReportSynthesisEntity",
              "There is no Report Synthesis Progress Outcome for the phase and flagship/module!"));
          }
        } else {
          fieldErrors.add(new FieldErrorDTO("findStatusPlannedOutcomeById", "ReportSynthesisEntity",
            "There is no Report Synthesis Progress for the phase and flagship/module!"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("findStatusPlannedOutcomeById", "ReportSynthesisEntity",
          "There is no Report Synthesis for the phase and flagship/module!"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.stream().forEach(f -> System.out.println(f.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(reportSynthesisFlagshipProgressOutcome)
      .map(this.statusPlannedOutcomesMapper::reportSynthesisFlagshipProgressOutcomeToStatusPlannedOutcomesDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public Long updateStatusPlannedOutcome(NewStatusPlannedOutcomeDTO newStatusPlannedOutcomeDTO,
    String CGIARentityAcronym, User user) {
    Long plannedOutcomeStatusID = null;
    String strippedId = null;
    CrpProgram crpProgram = null;
    Phase phase = null;
    LiaisonInstitution liaisonInstitution = null;

    CrpProgramOutcome crpProgramOutcome = null;
    ReportSynthesis reportSynthesis = null;
    ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress = null;
    ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    if (newStatusPlannedOutcomeDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "PhaseEntity", "Phase must not be null"));
    } else {
      String strippedPhaseName = StringUtils.stripToNull(newStatusPlannedOutcomeDTO.getPhase().getName());
      if (strippedPhaseName == null || newStatusPlannedOutcomeDTO.getPhase().getYear() == null
      // DANGER! Magic number ahead
        || newStatusPlannedOutcomeDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
            && p.getYear() == newStatusPlannedOutcomeDTO.getPhase().getYear()
            && StringUtils.equalsIgnoreCase(p.getName(), strippedPhaseName) && p.isActive())
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors.add(
            new FieldErrorDTO("updateStatusPlannedOutcome", "phase", newStatusPlannedOutcomeDTO.getPhase().getName()
              + ' ' + newStatusPlannedOutcomeDTO.getPhase().getYear() + " is an invalid phase"));
        }
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), CGIARentityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("updateStatusPlannedOutcome", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.stripToNull(newStatusPlannedOutcomeDTO.getCrpProgramCode());
    if (strippedId != null) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(strippedId);
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "FlagshipEntity",
          newStatusPlannedOutcomeDTO.getCrpProgramCode() + " is an invalid CRP Program SMO code."));
      } else {
        if (!StringUtils.equalsIgnoreCase(crpProgram.getCrp().getAcronym(), strippedEntityAcronym)) {
          fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "FlagshipEntity",
            "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym "
              + CGIARentityAcronym));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "FlagshipEntity",
        "CRP Program SMO code can not be null nor empty."));
    }

    if (globalUnitEntity != null && crpProgram != null) {
      liaisonInstitution =
        liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "LiaisonInstitutionEntity",
          "A Liaison Institution with the acronym " + crpProgram.getAcronym() + " could not be found for "
            + CGIARentityAcronym));
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "LiaisonInstitutionEntity",
        "A Liaison Institution can not be found if either the CRP or the Flagship/Module is invalid"));
    }

    strippedId = StringUtils.stripToNull(newStatusPlannedOutcomeDTO.getCrpOutcomeCode());
    if (strippedId != null) {
      crpProgramOutcome =
        crpProgramOutcomeManager.getCrpProgramOutcome(newStatusPlannedOutcomeDTO.getCrpOutcomeCode(), phase);
      if (crpProgramOutcome == null) {
        fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "Outcome", "is an invalid CRP Outcome"));
      } else {
        if (!crpProgramOutcome.getCrpProgram().getId().equals(crpProgram.getId())) {
          fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "Outcome",
            "The entered CRP Outcome do not correspond with the CRP Program given"));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "CrpProgramOutcomeEntity",
        "CRP Program Outcome composed ID code can not be null nor empty."));
    }

    if (fieldErrors.isEmpty()) {
      reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      if (reportSynthesis != null) {
        reportSynthesisFlagshipProgress = reportSynthesis.getReportSynthesisFlagshipProgress();
        if (reportSynthesisFlagshipProgress != null) {
          Long cpoId = crpProgramOutcome.getId();
          List<ReportSynthesisFlagshipProgressOutcome> reportSynthesisFlagshipProgressOutcomeList =
            reportSynthesisFlagshipProgress.getReportSynthesisFlagshipProgressOutcomes().stream()
              .filter(c -> c.getCrpProgramOutcome().getId().equals(cpoId)).collect(Collectors.toList());
          if (reportSynthesisFlagshipProgressOutcomeList != null
            && reportSynthesisFlagshipProgressOutcomeList.size() > 0) {
            reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeList.get(0);
            reportSynthesisFlagshipProgressOutcome.setSummary(newStatusPlannedOutcomeDTO.getSumary());
            reportSynthesisFlagshipProgressOutcome.setModifiedBy(user);
            reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
              .saveReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
            if (reportSynthesisFlagshipProgressOutcome != null) {
              plannedOutcomeStatusID = reportSynthesisFlagshipProgressOutcome.getId();
            } else {
              fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "plannedOutcomeStatusID",
                "can not save plannedOutcomeStatusID"));
            }
          } else {
            fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "ReportSynthesisProgress",
              "can not find synthesis progress outcome report"));
          }
        } else {
          fieldErrors.add(new FieldErrorDTO("updateStatusPlannedOutcome", "ReportSynthesisProgress",
            "can not find synthesis progress report"));
        }
      } else {
        fieldErrors
          .add(new FieldErrorDTO("updateStatusPlannedOutcome", "ReportSynthesis", "can not find synthesis report"));
      }
    }

    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      fieldErrors.stream().forEach(f -> System.out.println(f.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return plannedOutcomeStatusID;
  }
}
