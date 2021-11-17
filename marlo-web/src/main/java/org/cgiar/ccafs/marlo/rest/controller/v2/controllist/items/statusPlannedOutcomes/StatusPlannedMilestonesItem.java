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
import org.cgiar.ccafs.marlo.data.manager.CgiarCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.CrpMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.GeneralStatusManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndGenderYouthFocusLevelManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndMilestoneReasonManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressCrossCuttingMarkerManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeMilestoneManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CgiarCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.CrpMilestone;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GeneralStatus;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.RepIndGenderYouthFocusLevel;
import org.cgiar.ccafs.marlo.data.model.RepIndMilestoneReason;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestoneLink;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.EvidenceLinkDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewCrosscuttingMarkersSynthesisDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewStatusPlannedMilestoneDTO;
import org.cgiar.ccafs.marlo.rest.dto.StatusPlannedMilestonesDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.StatusPlannedMilestonesMapper;
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
public class StatusPlannedMilestonesItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private CrpProgramManager crpProgramManager;
  private CrpMilestoneManager crpMilestoneManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager;
  private ReportSynthesisFlagshipProgressOutcomeMilestoneManager reportSynthesisFlagshipProgressOutcomeMilestoneManager;
  private ReportSynthesisFlagshipProgressCrossCuttingMarkerManager reportSynthesisFlagshipProgressCrossCuttingMarkerManager;
  private ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private GeneralStatusManager generalStatusManager;
  private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;
  private RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager;
  private RepIndMilestoneReasonManager repIndMilestoneReasonManager;
  private StatusPlannedMilestonesMapper statusPlannedMilestonesMapper;

  @Inject
  public StatusPlannedMilestonesItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, CrpMilestoneManager crpMilestoneManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager,
    ReportSynthesisFlagshipProgressOutcomeMilestoneManager reportSynthesisFlagshipProgressOutcomeMilestoneManager,
    ReportSynthesisFlagshipProgressCrossCuttingMarkerManager reportSynthesisFlagshipProgressCrossCuttingMarkerManager,
    LiaisonInstitutionManager liaisonInstitutionManager, GeneralStatusManager generalStatusManager,
    CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager,
    RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager,
    RepIndMilestoneReasonManager repIndMilestoneReasonManager, StatusPlannedOutcomesMapper statusPlannedOutcomesMapper,
    StatusPlannedMilestonesMapper statusPlannedMilestonesMapper,
    ReportSynthesisFlagshipProgressOutcomeMilestoneLinkManager reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.crpProgramManager = crpProgramManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFlagshipProgressOutcomeManager = reportSynthesisFlagshipProgressOutcomeManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
    this.crpMilestoneManager = crpMilestoneManager;
    this.generalStatusManager = generalStatusManager;
    this.cgiarCrossCuttingMarkerManager = cgiarCrossCuttingMarkerManager;
    this.repIndGenderYouthFocusLevelManager = repIndGenderYouthFocusLevelManager;
    this.reportSynthesisFlagshipProgressOutcomeMilestoneManager =
      reportSynthesisFlagshipProgressOutcomeMilestoneManager;
    this.reportSynthesisFlagshipProgressCrossCuttingMarkerManager =
      reportSynthesisFlagshipProgressCrossCuttingMarkerManager;
    this.repIndMilestoneReasonManager = repIndMilestoneReasonManager;
    this.statusPlannedMilestonesMapper = statusPlannedMilestonesMapper;
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

  public Long createStatusPlannedMilestone(NewStatusPlannedMilestoneDTO newStatusPlannedMilestoneDTO,
    String CGIARentityAcronym, User user) {
    Long plannedMilestoneStatusID = null;
    String strippedId = null;
    Phase phase = null;

    ReportSynthesis reportSynthesis = null;
    ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress = null;
    ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome = null;
    List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink> evidenceLinks =
      new ArrayList<ReportSynthesisFlagshipProgressOutcomeMilestoneLink>();

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("createStatusPlannedMilestone", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newStatusPlannedMilestoneDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "PhaseEntity", "Phase must not be null"));
    } else {
      String strippedPhaseName = StringUtils.stripToNull(newStatusPlannedMilestoneDTO.getPhase().getName());
      if (strippedPhaseName == null || newStatusPlannedMilestoneDTO.getPhase().getYear() == null
      // DANGER! Magic number ahead
        || newStatusPlannedMilestoneDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
            && p.getYear() == newStatusPlannedMilestoneDTO.getPhase().getYear()
            && StringUtils.equalsIgnoreCase(p.getName(), strippedPhaseName) && p.isActive())
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors.add(
            new FieldErrorDTO("createStatusPlannedMilestone", "phase", newStatusPlannedMilestoneDTO.getPhase().getName()
              + ' ' + newStatusPlannedMilestoneDTO.getPhase().getYear() + " is an invalid phase"));
        }
        if (phase != null && !phase.getEditable()) {
          fieldErrors.add(
            new FieldErrorDTO("createStatusPlannedMilestone", "phase", newStatusPlannedMilestoneDTO.getPhase().getName()
              + ' ' + newStatusPlannedMilestoneDTO.getPhase().getYear() + " is closed phase"));
        }
      }
    }

    CrpProgram crpProgram = null;
    strippedId = StringUtils.stripToNull(newStatusPlannedMilestoneDTO.getCrpProgramCode());
    if (strippedId != null) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(strippedId);
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "FlagshipEntity",
          newStatusPlannedMilestoneDTO.getCrpProgramCode() + " is an invalid CRP Program SMO code."));
      } else {
        if (!StringUtils.equalsIgnoreCase(crpProgram.getCrp().getAcronym(), strippedEntityAcronym)) {
          fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "FlagshipEntity",
            "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym "
              + CGIARentityAcronym));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "FlagshipEntity",
        "CRP Program SMO code can not be null nor empty."));
    }

    CrpProgramOutcome crpProgramOutcome = null;
    strippedId = StringUtils.stripToNull(newStatusPlannedMilestoneDTO.getCrpOutcomeCode());
    if (strippedId != null && phase != null) {
      crpProgramOutcome = crpProgramOutcomeManager.getCrpProgramOutcome(strippedId, phase);
      if (crpProgramOutcome == null) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Outcome", "is an invalid CRP Outcome"));
      } else {
        if (!crpProgramOutcome.getCrpProgram().getId().equals(crpProgram.getId())) {
          fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Outcome",
            "The entered CRP Outcome do not correspond with the CRP Program given"));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "CrpProgramOutcomeEntity",
        "CRP Program Outcome composed ID code can not be null nor empty."));
    }

    CrpMilestone crpMilestone = null;
    strippedId = StringUtils.stripToNull(newStatusPlannedMilestoneDTO.getMilestoneCode());
    if (strippedId != null && phase != null) {
      crpMilestone = crpMilestoneManager.getCrpMilestoneByPhase(strippedId, phase.getId());
      if (crpMilestone == null) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Milestone", "is an invalid CRP Milestone"));
      } else {
        if (crpMilestone.getYear() != phase.getYear()) {
          if (crpMilestone.getExtendedYear() != phase.getYear()) {
            fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Milestone",
              "Milestone's year do not correspond to the phase"));
          }
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "CrpProgramOutcomeEntity",
        "CRP Milestone composed ID code can not be null nor empty."));
    }

    RepIndMilestoneReason repIndMilestoneReason = null;
    GeneralStatus status = generalStatusManager.getGeneralStatusById(newStatusPlannedMilestoneDTO.getStatus());
    if (status == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Status", "is an invalid Status identifier"));
    } else {
      if (status.getId().longValue() == 4 || status.getId().longValue() == 5 || status.getId().longValue() == 6) {
        repIndMilestoneReason =
          repIndMilestoneReasonManager.getRepIndMilestoneReasonById(newStatusPlannedMilestoneDTO.getMainReason());
        if (repIndMilestoneReason == null) {
          fieldErrors.add(
            new FieldErrorDTO("createStatusPlannedMilestone", "Reason", "is an invalid Milestone reason identifier"));
        }
        // TODO remember to take into account the "otherReason" field in the future validator for this (if
        // repIndMilestoneReason = 7 Other)
      }
    }
    // limit words validation
    if (this.countWords(newStatusPlannedMilestoneDTO.getEvidence()) > 200) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Evidence",
        "Evidence field excedes the maximum number of words (200 words)"));
    }

    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), globalUnitEntity.getId());
      reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      if (reportSynthesis == null) {
        reportSynthesis = new ReportSynthesis();
        reportSynthesis.setLiaisonInstitution(liaisonInstitution);
        reportSynthesis.setPhase(phase);
        reportSynthesis = reportSynthesisManager.saveReportSynthesis(reportSynthesis);
      }

      reportSynthesisFlagshipProgress =
        reportSynthesisFlagshipProgressManager.getReportSynthesisFlagshipProgressById(reportSynthesis.getId());
      if (reportSynthesisFlagshipProgress == null) {
        reportSynthesisFlagshipProgress = new ReportSynthesisFlagshipProgress();
        reportSynthesisFlagshipProgress.setCreatedBy(user);
        reportSynthesisFlagshipProgress.setReportSynthesis(reportSynthesis);
        reportSynthesisFlagshipProgress =
          reportSynthesisFlagshipProgressManager.saveReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
      }

      reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
        .getOutcomeId(reportSynthesisFlagshipProgress.getId(), crpProgramOutcome.getId());
      boolean newRSFPOutcome = false;
      if (reportSynthesisFlagshipProgressOutcome == null) {
        reportSynthesisFlagshipProgressOutcome = new ReportSynthesisFlagshipProgressOutcome();
        reportSynthesisFlagshipProgressOutcome.setCrpProgramOutcome(crpProgramOutcome);
        reportSynthesisFlagshipProgressOutcome.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
        reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
          .saveReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
        newRSFPOutcome = true;
      }

      List<ReportSynthesisFlagshipProgressOutcomeMilestone> reportSynthesisFlagshipProgressOutcomeMilestoneList = null;
      if (newRSFPOutcome) {
        reportSynthesisFlagshipProgressOutcomeMilestoneList = new ArrayList<>();
      } else {
        long milestoneCode = crpMilestone.getId();
        reportSynthesisFlagshipProgressOutcomeMilestoneList = reportSynthesisFlagshipProgressOutcome
          .getReportSynthesisFlagshipProgressOutcomeMilestones().stream()
          .filter(c -> c.isActive() && c.getCrpMilestone().getId().equals(milestoneCode)).collect(Collectors.toList());
      }

      ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone = null;
      boolean proceed = false;
      if (reportSynthesisFlagshipProgressOutcomeMilestoneList != null
        && reportSynthesisFlagshipProgressOutcomeMilestoneList.isEmpty()) {
        reportSynthesisFlagshipProgressOutcomeMilestone = new ReportSynthesisFlagshipProgressOutcomeMilestone();
        proceed = true;
      }

      if (proceed) {
        reportSynthesisFlagshipProgressOutcomeMilestone
          .setReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
        reportSynthesisFlagshipProgressOutcomeMilestone.setCrpMilestone(crpMilestone);
        reportSynthesisFlagshipProgressOutcomeMilestone.setMilestonesStatus(status);
        reportSynthesisFlagshipProgressOutcomeMilestone.setEvidence(newStatusPlannedMilestoneDTO.getEvidence());
        reportSynthesisFlagshipProgressOutcomeMilestone.setEvidenceLink(newStatusPlannedMilestoneDTO.getLinkEvidence());
        if (status.getId().longValue() == 4 || status.getId().longValue() == 5 || status.getId().longValue() == 6) {
          reportSynthesisFlagshipProgressOutcomeMilestone.setReason(repIndMilestoneReason);
          if (repIndMilestoneReason != null && repIndMilestoneReason.getId().longValue() == 7) {
            reportSynthesisFlagshipProgressOutcomeMilestone
              .setOtherReason(newStatusPlannedMilestoneDTO.getOtherReason());
          }
        }

        if (status.getId().longValue() == 4) {
          reportSynthesisFlagshipProgressOutcomeMilestone
            .setExtendedYear(newStatusPlannedMilestoneDTO.getExtendedYear());
        } else {
          reportSynthesisFlagshipProgressOutcomeMilestone
            .setExtendedYear(newStatusPlannedMilestoneDTO.getExtendedYear() != 0
              ? newStatusPlannedMilestoneDTO.getExtendedYear() : null);
        }
        for (EvidenceLinkDTO link : newStatusPlannedMilestoneDTO.getEvidenceLinks()) {
          ReportSynthesisFlagshipProgressOutcomeMilestoneLink milestoneEvidenceLink =
            new ReportSynthesisFlagshipProgressOutcomeMilestoneLink();
          milestoneEvidenceLink.setLink(link.getLink());
          evidenceLinks.add(milestoneEvidenceLink);
        }

        List<ReportSynthesisFlagshipProgressCrossCuttingMarker> reportSynthesisFlagshipProgressCrossCuttingMarkerList =
          new ArrayList<ReportSynthesisFlagshipProgressCrossCuttingMarker>();
        if (newStatusPlannedMilestoneDTO.getCrosscuttinmarkerList() != null) {
          for (NewCrosscuttingMarkersSynthesisDTO crosscuttingmarker : newStatusPlannedMilestoneDTO
            .getCrosscuttinmarkerList()) {
            CgiarCrossCuttingMarker cgiarCrossCuttingMarker = cgiarCrossCuttingMarkerManager
              .getCgiarCrossCuttingMarkerById(Long.parseLong(crosscuttingmarker.getCrossCuttingmarker()));
            if (cgiarCrossCuttingMarker != null) {
              RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel = repIndGenderYouthFocusLevelManager
                .getRepIndGenderYouthFocusLevelById(Long.parseLong(crosscuttingmarker.getCrossCuttingmarkerScore()));
              if (repIndGenderYouthFocusLevel != null) {
                ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker =
                  new ReportSynthesisFlagshipProgressCrossCuttingMarker();

                reportSynthesisFlagshipProgressCrossCuttingMarker.setJust(crosscuttingmarker.getJustification());
                reportSynthesisFlagshipProgressCrossCuttingMarker.setMarker(cgiarCrossCuttingMarker);
                reportSynthesisFlagshipProgressCrossCuttingMarker.setFocus(repIndGenderYouthFocusLevel);
                reportSynthesisFlagshipProgressCrossCuttingMarkerList
                  .add(reportSynthesisFlagshipProgressCrossCuttingMarker);
              } else {
                fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "CrossCuttingMarkerScore",
                  "is an invalid Gender Youth Focus Level"));
              }
            } else {
              fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "CrossCuttingMarker",
                "is an invalid Cross Cutting Marker"));
            }
          }
        } else {
          fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "CrossCuttingMarkerList",
            "CrossCuttingMarkerList needs to be filled or declared empty [ ]"));
        }


        if (fieldErrors.isEmpty()) {
          reportSynthesisFlagshipProgressOutcomeMilestone = reportSynthesisFlagshipProgressOutcomeMilestoneManager
            .saveReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisFlagshipProgressOutcomeMilestone);
          plannedMilestoneStatusID = reportSynthesisFlagshipProgressOutcomeMilestone.getId();
          for (ReportSynthesisFlagshipProgressOutcomeMilestoneLink reportSynthesisFlagshipProgressOutcomeMilestoneLink : evidenceLinks) {
            reportSynthesisFlagshipProgressOutcomeMilestoneLink
              .setReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisFlagshipProgressOutcomeMilestone);
            reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager
              .saveReportSynthesisFlagshipProgressOutcomeMilestoneLink(
                reportSynthesisFlagshipProgressOutcomeMilestoneLink);
          }
          for (ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker : reportSynthesisFlagshipProgressCrossCuttingMarkerList) {
            reportSynthesisFlagshipProgressCrossCuttingMarker
              .setReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisFlagshipProgressOutcomeMilestone);
            reportSynthesisFlagshipProgressCrossCuttingMarkerManager
              .saveReportSynthesisFlagshipProgressCrossCuttingMarker(reportSynthesisFlagshipProgressCrossCuttingMarker);
          }
        }
      } else {
        fieldErrors.add(
          new FieldErrorDTO("createStatusPlannedMilestone", "Milestone", "There is an Status of milestone created"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.stream().forEach(f -> System.out.println(f.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return plannedMilestoneStatusID;
  }

  public Long deleteStatusPlannedMilestone(String repoPhase, int repoYear, String flagship, String outcome,
    String milestone, String CGIARentityAcronym, User user) {
    Long plannedMilestoneStatusID = null;
    String strippedId = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("deleteStatusPlannedOutcome", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    String strippedRepoPhase = StringUtils.stripToNull(repoPhase);
    Phase phase = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
        && p.getYear() == repoYear && StringUtils.equalsIgnoreCase(p.getName(), strippedRepoPhase) && p.isActive())
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(
        new FieldErrorDTO("deleteStatusPlannedOutcome", "phase", repoPhase + ' ' + repoYear + " is an invalid phase"));
    } else {
      if (!StringUtils.equalsIgnoreCase(phase.getCrp().getAcronym(), strippedEntityAcronym)) {
        fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "FlagshipEntity",
          "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym " + CGIARentityAcronym));
      }
    }

    CrpProgram crpProgram = null;
    strippedId = StringUtils.stripToNull(flagship);
    if (strippedId != null) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(strippedId);
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "FlagshipEntity",
          flagship + " is an invalid CRP Program SMO code."));
      } else {
        if (!StringUtils.equalsIgnoreCase(crpProgram.getCrp().getAcronym(), strippedEntityAcronym)) {
          fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "FlagshipEntity",
            "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym "
              + CGIARentityAcronym));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "FlagshipEntity",
        "CRP Program SMO code can not be null nor empty."));
    }

    CrpProgramOutcome crpProgramOutcome = null;
    strippedId = StringUtils.stripToNull(outcome);
    if (strippedId != null && phase != null) {
      crpProgramOutcome = crpProgramOutcomeManager.getCrpProgramOutcome(strippedId, phase);
      if (crpProgramOutcome == null) {
        fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "Outcome", "is an invalid CRP Outcome"));
      } else {
        if (!crpProgramOutcome.getCrpProgram().getId().equals(crpProgram.getId())) {
          fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "Outcome",
            "The entered CRP Outcome do not correspond with the CRP Program given"));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "CrpProgramOutcomeEntity",
        "CRP Program Outcome composed ID code can not be null nor empty."));
    }

    CrpMilestone crpMilestone = null;
    strippedId = StringUtils.stripToNull(milestone);
    if (strippedId != null && phase != null) {
      crpMilestone = crpMilestoneManager.getCrpMilestoneByPhase(strippedId, phase.getId());
      if (crpMilestone == null) {
        fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "Milestone", "is an invalid CRP Milestone"));
      } else {
        if (crpMilestone.getYear() != phase.getYear()) {
          if (crpMilestone.getExtendedYear() != phase.getYear()) {
            fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "Milestone",
              "Milestone's year do not correspond to the phase"));
          }
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "CrpProgramOutcomeEntity",
        "CRP Milestone composed ID code can not be null nor empty."));
    }

    ReportSynthesis reportSynthesis = null;
    ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress = null;
    ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome = null;
    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), globalUnitEntity.getId());
      reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      if (reportSynthesis != null) {
        reportSynthesisFlagshipProgress =
          reportSynthesisFlagshipProgressManager.getReportSynthesisFlagshipProgressById(reportSynthesis.getId());
        if (reportSynthesisFlagshipProgress != null) {
          reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
            .getOutcomeId(reportSynthesisFlagshipProgress.getId(), crpProgramOutcome.getId());
          if (reportSynthesisFlagshipProgressOutcome != null) {
            long milestoneCode = crpMilestone.getId();
            List<ReportSynthesisFlagshipProgressOutcomeMilestone> reportSynthesisFlagshipProgressOutcomeMilestoneList =
              reportSynthesisFlagshipProgressOutcome.getReportSynthesisFlagshipProgressOutcomeMilestones().stream()
                .filter(c -> c.isActive() && c.getCrpMilestone().getId().equals(milestoneCode))
                .collect(Collectors.toList());
            if (reportSynthesisFlagshipProgressOutcomeMilestoneList != null
              && reportSynthesisFlagshipProgressOutcomeMilestoneList.size() > 0) {
              ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone =
                reportSynthesisFlagshipProgressOutcomeMilestoneList.get(0);
              List<ReportSynthesisFlagshipProgressCrossCuttingMarker> crosscutingMarkers =
                new ArrayList<ReportSynthesisFlagshipProgressCrossCuttingMarker>();
              for (ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker : reportSynthesisFlagshipProgressOutcomeMilestone
                .getReportSynthesisFlagshipProgressCrossCuttingMarkers().stream().collect(Collectors.toList())) {
                crosscutingMarkers.add(reportSynthesisFlagshipProgressCrossCuttingMarker);

              }

              for (ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker : crosscutingMarkers) {
                reportSynthesisFlagshipProgressCrossCuttingMarkerManager
                  .deleteReportSynthesisFlagshipProgressCrossCuttingMarker(
                    reportSynthesisFlagshipProgressCrossCuttingMarker.getId());
              }

              plannedMilestoneStatusID = reportSynthesisFlagshipProgressOutcomeMilestone.getId();
              reportSynthesisFlagshipProgressOutcomeMilestoneManager
                .deleteReportSynthesisFlagshipProgressOutcomeMilestone(plannedMilestoneStatusID);
            } else {
              fieldErrors
                .add(new FieldErrorDTO("deleteStatusPlannedOutcome", "Milestone", "There is no milestone status"));
            }
          } else {
            fieldErrors
              .add(new FieldErrorDTO("deleteStatusPlannedOutcome", "Milestone", "There is no milestone status"));
          }
        } else {
          fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "Milestone", "There is no milestone status"));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteStatusPlannedOutcome", "Milestone", "There is no milestone status"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.stream().forEach(f -> System.out.println(f.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return plannedMilestoneStatusID;
  }

  public ResponseEntity<StatusPlannedMilestonesDTO> findStatusPlannedMilestone(String outcomeID, String milestoneID,
    String CGIARentityAcronym, Integer repoYear, String repoPhase, User user) {
    ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    String strippedId = null;
    LiaisonInstitution liaisonInstitution = null;
    CrpProgramOutcome crpProgramOutcome = null;
    CrpMilestone milestone = null;
    ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome = null;
    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findStatusPlannedMilestoneById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("findStatusPlannedMilestoneById", "GlobalUnitEntity",
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
      fieldErrors.add(new FieldErrorDTO("findStatusPlannedMilestoneById", "phase",
        repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), CGIARentityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("findStatusPlannedMilestoneById", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    strippedId = StringUtils.stripToNull(outcomeID);
    if (strippedId != null && phase != null) {
      crpProgramOutcome = crpProgramOutcomeManager.getCrpProgramOutcome(strippedId, phase);
      if (crpProgramOutcome == null) {
        fieldErrors.add(new FieldErrorDTO("findStatusPlannedMilestoneById", "Outcome",
          strippedId + " is an invalid CRP Outcome code"));
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("findStatusPlannedMilestoneById", "CrpProgramOutcomeEntity",
        "A CRP Program Outcome with composed ID " + strippedId + " do not exist for the given phase."));
    }

    if (globalUnitEntity != null && crpProgramOutcome != null) {
      liaisonInstitution = liaisonInstitutionManager.findByAcronymAndCrp(crpProgramOutcome.getCrpProgram().getAcronym(),
        globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("findStatusPlannedMilestoneById", "LiaisonInstitutionEntity",
          "A Liaison Institution with the acronym " + crpProgramOutcome.getCrpProgram().getAcronym()
            + " could not be found for " + CGIARentityAcronym));
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("findStatusPlannedMilestoneById", "LiaisonInstitutionEntity",
        "A Liaison Institution can not be found if either the CRP or the Flagship/Module is invalid"));
    }

    if (crpProgramOutcome != null && phase != null) {
      milestone = crpMilestoneManager.getCrpMilestoneByPhase(milestoneID, phase.getId().longValue());
      if (milestone == null) {
        fieldErrors.add(new FieldErrorDTO("findStatusPlannedMilestoneById", "CrpMilestone",
          "CRP milestone with compose ID " + milestoneID + " do not exist for the given phase"));
      }
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
            reportSynthesisFlagshipProgressOutcomeMilestone =
              reportSynthesisFlagshipProgressOutcome.getReportSynthesisFlagshipProgressOutcomeMilestones().stream()
                .filter(c -> c.isActive() && c.getCrpMilestone().getComposeID().equals(milestoneID)).findFirst()
                .orElse(null);
            if (reportSynthesisFlagshipProgressOutcomeMilestone == null) {
              fieldErrors.add(new FieldErrorDTO("findStatusPlannedMilestoneById", "Milestone status",
                "Milestone status with compose ID " + milestoneID + " do not exist for the given phase"));
            } else {
              List<ReportSynthesisFlagshipProgressCrossCuttingMarker> markersList =
                new ArrayList<ReportSynthesisFlagshipProgressCrossCuttingMarker>();
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
              reportSynthesisFlagshipProgressOutcomeMilestone.setMarkers(markersList);
            }
          }
        }
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.stream().forEach(f -> System.out.println(f.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return Optional.ofNullable(reportSynthesisFlagshipProgressOutcomeMilestone)
      .map(
        this.statusPlannedMilestonesMapper::reportSynthesisFlagshipProgressOutcomeMilestoneToStatusPlannedMilestonesDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public Long updateStatusPlannedMilestone(NewStatusPlannedMilestoneDTO newStatusPlannedMilestoneDTO,
    String CGIARentityAcronym, User user) {
    Long plannedMilestoneStatusID = null;
    Phase phase = null;
    String strippedId = null;
    ReportSynthesis reportSynthesis = null;
    ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress = null;
    ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome = null;
    List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink> reportSynthesisFlagshipProgressOutcomeMilestoneLinkDB =
      new ArrayList<ReportSynthesisFlagshipProgressOutcomeMilestoneLink>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("updateStatusPlannedMilestone", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newStatusPlannedMilestoneDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "PhaseEntity", "Phase must not be null"));
    } else {
      String strippedPhaseName = StringUtils.stripToNull(newStatusPlannedMilestoneDTO.getPhase().getName());
      if (strippedPhaseName == null || newStatusPlannedMilestoneDTO.getPhase().getYear() == null
      // DANGER! Magic number ahead
        || newStatusPlannedMilestoneDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
            && p.getYear() == newStatusPlannedMilestoneDTO.getPhase().getYear()
            && StringUtils.equalsIgnoreCase(p.getName(), strippedPhaseName) && p.isActive())
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors.add(
            new FieldErrorDTO("updateStatusPlannedMilestone", "phase", newStatusPlannedMilestoneDTO.getPhase().getName()
              + ' ' + newStatusPlannedMilestoneDTO.getPhase().getYear() + " is an invalid phase"));
        }
        if (phase != null && !phase.getEditable()) {
          fieldErrors.add(
            new FieldErrorDTO("updateStatusPlannedMilestone", "phase", newStatusPlannedMilestoneDTO.getPhase().getName()
              + ' ' + newStatusPlannedMilestoneDTO.getPhase().getYear() + " is closed phase"));
        }
      }
    }

    CrpProgram crpProgram = null;
    strippedId = StringUtils.stripToNull(newStatusPlannedMilestoneDTO.getCrpProgramCode());
    if (strippedId != null) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(strippedId);
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "FlagshipEntity",
          newStatusPlannedMilestoneDTO.getCrpProgramCode() + " is an invalid CRP Program SMO code."));
      } else {
        if (!StringUtils.equalsIgnoreCase(crpProgram.getCrp().getAcronym(), strippedEntityAcronym)) {
          fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "FlagshipEntity",
            "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym "
              + CGIARentityAcronym));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "FlagshipEntity",
        "CRP Program SMO code can not be null nor empty."));
    }

    CrpProgramOutcome crpProgramOutcome = null;
    strippedId = StringUtils.stripToNull(newStatusPlannedMilestoneDTO.getCrpOutcomeCode());
    if (strippedId != null && phase != null) {
      crpProgramOutcome = crpProgramOutcomeManager.getCrpProgramOutcome(strippedId, phase);
      if (crpProgramOutcome == null) {
        fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "Outcome", "is an invalid CRP Outcome"));
      } else {
        if (!crpProgramOutcome.getCrpProgram().getId().equals(crpProgram.getId())) {
          fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "Outcome",
            "The entered CRP Outcome do not correspond with the CRP Program given"));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "CrpProgramOutcomeEntity",
        "CRP Program Outcome composed ID code can not be null nor empty."));
    }

    CrpMilestone crpMilestone = null;
    strippedId = StringUtils.stripToNull(newStatusPlannedMilestoneDTO.getMilestoneCode());
    if (strippedId != null && phase != null) {
      crpMilestone = crpMilestoneManager.getCrpMilestoneByPhase(strippedId, phase.getId());
      if (crpMilestone == null) {
        fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "Milestone", "is an invalid CRP Milestone"));
      } else {
        if (crpMilestone.getYear() != phase.getYear()) {
          if (crpMilestone.getExtendedYear() != phase.getYear()) {
            fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "Milestone",
              "Milestone's year do not correspond to the phase"));
          }
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "CrpProgramOutcomeEntity",
        "CRP Milestone composed ID code can not be null nor empty."));
    }

    RepIndMilestoneReason repIndMilestoneReason = null;
    GeneralStatus status = generalStatusManager.getGeneralStatusById(newStatusPlannedMilestoneDTO.getStatus());
    if (status == null) {
      fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "Status", "is an invalid Status identifier"));
    } else {
      if (status.getId().longValue() == 4 || status.getId().longValue() == 5 || status.getId().longValue() == 6) {
        repIndMilestoneReason =
          repIndMilestoneReasonManager.getRepIndMilestoneReasonById(newStatusPlannedMilestoneDTO.getMainReason());
        if (repIndMilestoneReason == null) {
          fieldErrors.add(
            new FieldErrorDTO("updateStatusPlannedMilestone", "Reason", "is an invalid Milestone reason identifier"));
        }
      }
    }

    // limit words validation
    if (this.countWords(newStatusPlannedMilestoneDTO.getEvidence()) > 200) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Evidence",
        "Evidence field excedes the maximum number of words (200 words)"));
    }

    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution =
        liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), globalUnitEntity.getId());
      reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      if (reportSynthesis != null) {
        reportSynthesisFlagshipProgress =
          reportSynthesisFlagshipProgressManager.getReportSynthesisFlagshipProgressById(reportSynthesis.getId());
        if (reportSynthesisFlagshipProgress != null) {
          reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
            .getOutcomeId(reportSynthesisFlagshipProgress.getId(), crpProgramOutcome.getId());
          if (reportSynthesisFlagshipProgressOutcome != null) {
            long milestoneCode = crpMilestone.getId();
            List<ReportSynthesisFlagshipProgressOutcomeMilestone> reportSynthesisFlagshipProgressOutcomeMilestoneList =
              reportSynthesisFlagshipProgressOutcome.getReportSynthesisFlagshipProgressOutcomeMilestones().stream()
                .filter(c -> c.isActive() && c.getCrpMilestone().getId().equals(milestoneCode))
                .collect(Collectors.toList());
            if (reportSynthesisFlagshipProgressOutcomeMilestoneList != null
              && reportSynthesisFlagshipProgressOutcomeMilestoneList.size() > 0) {
              ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone =
                reportSynthesisFlagshipProgressOutcomeMilestoneList.get(0);
              reportSynthesisFlagshipProgressOutcomeMilestone.setMilestonesStatus(status);
              reportSynthesisFlagshipProgressOutcomeMilestone.setEvidence(newStatusPlannedMilestoneDTO.getEvidence());
              reportSynthesisFlagshipProgressOutcomeMilestone
                .setEvidenceLink(newStatusPlannedMilestoneDTO.getLinkEvidence());
              if (status.getId().longValue() == 4 || status.getId().longValue() == 5
                || status.getId().longValue() == 6) {
                reportSynthesisFlagshipProgressOutcomeMilestone.setReason(repIndMilestoneReason);
                if (repIndMilestoneReason != null && repIndMilestoneReason.getId() == 7) {
                  reportSynthesisFlagshipProgressOutcomeMilestone
                    .setOtherReason(newStatusPlannedMilestoneDTO.getOtherReason());
                }
              }

              if (status.getId().longValue() == 4) {
                reportSynthesisFlagshipProgressOutcomeMilestone
                  .setExtendedYear(newStatusPlannedMilestoneDTO.getExtendedYear());
              } else {
                reportSynthesisFlagshipProgressOutcomeMilestone
                  .setExtendedYear(newStatusPlannedMilestoneDTO.getExtendedYear() != 0
                    ? newStatusPlannedMilestoneDTO.getExtendedYear() : null);
              }
              List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink> evidenceLinks =
                new ArrayList<ReportSynthesisFlagshipProgressOutcomeMilestoneLink>();
              List<ReportSynthesisFlagshipProgressOutcomeMilestoneLink> existingReportSynthesisFlagshipProgressOutcomeMilestoneLink =
                new ArrayList<ReportSynthesisFlagshipProgressOutcomeMilestoneLink>();
              if (newStatusPlannedMilestoneDTO.getEvidenceLinks() != null) {
                for (EvidenceLinkDTO link : newStatusPlannedMilestoneDTO.getEvidenceLinks()) {
                  boolean linkFound = false;

                  for (ReportSynthesisFlagshipProgressOutcomeMilestoneLink reportSynthesisFlagshipProgressOutcomeMilestoneLink : reportSynthesisFlagshipProgressOutcomeMilestone
                    .getReportSynthesisFlagshipProgressOutcomeMilestoneLinks().stream().filter(c -> c != null)
                    .collect(Collectors.toList())) {
                    if (link.getLink().equals(reportSynthesisFlagshipProgressOutcomeMilestoneLink.getLink())) {
                      linkFound = true;
                      existingReportSynthesisFlagshipProgressOutcomeMilestoneLink
                        .add(reportSynthesisFlagshipProgressOutcomeMilestoneLink);
                    }
                  }
                  if (!linkFound) {
                    ReportSynthesisFlagshipProgressOutcomeMilestoneLink reportSynthesisFlagshipProgressOutcomeMilestoneLink =
                      new ReportSynthesisFlagshipProgressOutcomeMilestoneLink();
                    reportSynthesisFlagshipProgressOutcomeMilestoneLink.setLink(link.getLink());
                    evidenceLinks.add(reportSynthesisFlagshipProgressOutcomeMilestoneLink);
                  }
                }

                for (ReportSynthesisFlagshipProgressOutcomeMilestoneLink reportSynthesisFlagshipProgressOutcomeMilestoneLink : reportSynthesisFlagshipProgressOutcomeMilestone
                  .getReportSynthesisFlagshipProgressOutcomeMilestoneLinks().stream().collect(Collectors.toList())) {
                  boolean linkFound = true;
                  for (EvidenceLinkDTO link : newStatusPlannedMilestoneDTO.getEvidenceLinks()) {
                    if (link.getLink().equals(reportSynthesisFlagshipProgressOutcomeMilestoneLink.getLink())) {
                      linkFound = false;
                    }
                  }
                  if (linkFound) {
                    reportSynthesisFlagshipProgressOutcomeMilestoneLinkDB
                      .add(reportSynthesisFlagshipProgressOutcomeMilestoneLink);
                  }
                }

              } else {
                fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "Evidence Links",
                  "Evidence Links needs to be filled or declared empty [ ]"));
              }
              List<ReportSynthesisFlagshipProgressCrossCuttingMarker> reportSynthesisFlagshipProgressCrossCuttingMarkerList =
                new ArrayList<ReportSynthesisFlagshipProgressCrossCuttingMarker>();
              if (newStatusPlannedMilestoneDTO.getCrosscuttinmarkerList() != null) {
                for (NewCrosscuttingMarkersSynthesisDTO crosscuttingmarker : newStatusPlannedMilestoneDTO
                  .getCrosscuttinmarkerList()) {
                  String incomingMarkerId = StringUtils.stripToEmpty(crosscuttingmarker.getCrossCuttingmarker());
                  CgiarCrossCuttingMarker cgiarCrossCuttingMarker =
                    cgiarCrossCuttingMarkerManager.getCgiarCrossCuttingMarkerById(Long.parseLong(incomingMarkerId));
                  if (cgiarCrossCuttingMarker != null) {
                    RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
                      repIndGenderYouthFocusLevelManager.getRepIndGenderYouthFocusLevelById(
                        Long.parseLong(crosscuttingmarker.getCrossCuttingmarkerScore()));
                    if (repIndGenderYouthFocusLevel != null) {
                      boolean markerFound = false;
                      for (ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker : reportSynthesisFlagshipProgressOutcomeMilestone
                        .getReportSynthesisFlagshipProgressCrossCuttingMarkers().stream()
                        .collect(Collectors.toList())) {
                        String dbMarkerId =
                          reportSynthesisFlagshipProgressCrossCuttingMarker.getMarker().getId().toString();
                        if (incomingMarkerId.equals(dbMarkerId)) {
                          reportSynthesisFlagshipProgressCrossCuttingMarker
                            .setJust(crosscuttingmarker.getJustification());
                          reportSynthesisFlagshipProgressCrossCuttingMarker.setMarker(cgiarCrossCuttingMarker);
                          reportSynthesisFlagshipProgressCrossCuttingMarker.setFocus(repIndGenderYouthFocusLevel);
                          reportSynthesisFlagshipProgressCrossCuttingMarkerList
                            .add(reportSynthesisFlagshipProgressCrossCuttingMarker);
                          markerFound = true;
                        }
                      }
                      // is newest crosscutting marker
                      if (!markerFound) {
                        ReportSynthesisFlagshipProgressCrossCuttingMarker newCrossCuttingMarker =
                          new ReportSynthesisFlagshipProgressCrossCuttingMarker();
                        newCrossCuttingMarker.setFocus(repIndGenderYouthFocusLevel);
                        newCrossCuttingMarker.setJust(crosscuttingmarker.getJustification());
                        newCrossCuttingMarker.setMarker(cgiarCrossCuttingMarker);
                        reportSynthesisFlagshipProgressCrossCuttingMarkerList.add(newCrossCuttingMarker);
                      }
                    } else {
                      fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "CrossCuttingMarkerScore",
                        "is an invalid Gender Youth Focus Level"));
                    }
                  } else {
                    fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "CrossCuttingMarker",
                      "is an invalid Cross Cutting Marker"));
                  }
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "CrossCuttingMarkerList",
                  "CrossCuttingMarkerList needs to be filled or declared empty [ ]"));
              }


              if (fieldErrors.isEmpty()) {
                reportSynthesisFlagshipProgressOutcomeMilestone = reportSynthesisFlagshipProgressOutcomeMilestoneManager
                  .saveReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisFlagshipProgressOutcomeMilestone);
                plannedMilestoneStatusID = reportSynthesisFlagshipProgressOutcomeMilestone.getId();
                for (ReportSynthesisFlagshipProgressOutcomeMilestoneLink reportSynthesisFlagshipProgressOutcomeMilestoneLink : evidenceLinks) {
                  reportSynthesisFlagshipProgressOutcomeMilestoneLink
                    .setReportSynthesisFlagshipProgressOutcomeMilestone(
                      reportSynthesisFlagshipProgressOutcomeMilestone);
                  reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager
                    .saveReportSynthesisFlagshipProgressOutcomeMilestoneLink(
                      reportSynthesisFlagshipProgressOutcomeMilestoneLink);
                }
                for (ReportSynthesisFlagshipProgressOutcomeMilestoneLink reportSynthesisFlagshipProgressOutcomeMilestoneLink : reportSynthesisFlagshipProgressOutcomeMilestoneLinkDB) {
                  reportSynthesisFlagshipProgressOutcomeMilestoneLinkManager
                    .deleteReportSynthesisFlagshipProgressOutcomeMilestoneLink(
                      reportSynthesisFlagshipProgressOutcomeMilestoneLink.getId());
                }
                for (ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker : reportSynthesisFlagshipProgressCrossCuttingMarkerList) {
                  reportSynthesisFlagshipProgressCrossCuttingMarker.setReportSynthesisFlagshipProgressOutcomeMilestone(
                    reportSynthesisFlagshipProgressOutcomeMilestone);
                  reportSynthesisFlagshipProgressCrossCuttingMarkerManager
                    .saveReportSynthesisFlagshipProgressCrossCuttingMarker(
                      reportSynthesisFlagshipProgressCrossCuttingMarker);
                }
              }
            }
          } else {
            fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "Outcome", "There is no Outcome Status"));
          }
        } else {
          fieldErrors.add(new FieldErrorDTO("updateStatusPlannedMilestone", "ReportSynthesisFlagship",
            "There is no flagship synthesis report"));
        }
      } else {
        fieldErrors
          .add(new FieldErrorDTO("updateStatusPlannedMilestone", "ReportSynthesis", "There is no synthesis report"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.stream().forEach(f -> System.out.println(f.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return plannedMilestoneStatusID;
  }

}