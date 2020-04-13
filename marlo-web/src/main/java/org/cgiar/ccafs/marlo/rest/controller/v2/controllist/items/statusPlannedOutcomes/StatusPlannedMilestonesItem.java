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
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewCrosscuttingMarkersSynthesisDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewStatusPlannedMilestoneDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.StatusPlannedOutcomesMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

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
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private GeneralStatusManager generalStatusManager;
  private CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager;
  private RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager;
  private RepIndMilestoneReasonManager repIndMilestoneReasonManager;

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
    RepIndMilestoneReasonManager repIndMilestoneReasonManager,
    StatusPlannedOutcomesMapper statusPlannedOutcomesMapper) {
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
  }

  public Long createStatusPlannedMilestone(NewStatusPlannedMilestoneDTO newStatusPlannedMilestoneDTO,
    String entityAcronym, User user) {
    Long plannedMilestoneStatusID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == newStatusPlannedMilestoneDTO.getPhase().getYear()
        && c.getName().equalsIgnoreCase(newStatusPlannedMilestoneDTO.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "phase",
        newStatusPlannedMilestoneDTO.getPhase().getYear() + " is an invalid year"));
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream().anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), entityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("createStatusPlannedMilestone", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }
    CrpProgram crpProgram = null;
    if (newStatusPlannedMilestoneDTO.getCrpProgramCode() != null
      && newStatusPlannedMilestoneDTO.getCrpProgramCode().length() > 0) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(newStatusPlannedMilestoneDTO.getCrpProgramCode());
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "CrpProgram", "is an invalid CRP Program"));
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "CrpProgram", "is an invalid CRP Program"));
    }

    CrpProgramOutcome crpProgramOutcome = null;
    if (newStatusPlannedMilestoneDTO.getCrpOutcomeCode() != null
      && newStatusPlannedMilestoneDTO.getCrpOutcomeCode().length() > 0) {
      crpProgramOutcome =
        crpProgramOutcomeManager.getCrpProgramOutcome(newStatusPlannedMilestoneDTO.getCrpOutcomeCode(), phase);
      if (crpProgramOutcome == null) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Outcome", "is an invalid CRP Outcome"));
      }
    }
    CrpMilestone crpMilestone =
      crpMilestoneManager.getCrpMilestoneByPhase(newStatusPlannedMilestoneDTO.getMilestoneCode(), phase.getId());
    if (crpMilestone == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Milestone", "is an invalid CRP Milestone"));
    }
    RepIndMilestoneReason repIndMilestoneReason = null;
    GeneralStatus status = generalStatusManager.getGeneralStatusById(newStatusPlannedMilestoneDTO.getStatus());
    if (status == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Status", "is an invalid Status identifier"));
    } else {
      if (status.getId().longValue() == 4) {
        repIndMilestoneReason =
          repIndMilestoneReasonManager.getRepIndMilestoneReasonById(newStatusPlannedMilestoneDTO.getMainReason());
        if (repIndMilestoneReason == null) {
          fieldErrors.add(
            new FieldErrorDTO("createStatusPlannedMilestone", "Reason", "is an invalid Milestone reason identifier"));
        }
      }
    }


    ReportSynthesis reportSynthesis = null;
    ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress = null;
    ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome;
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
      if (reportSynthesisFlagshipProgressOutcome == null) {
        reportSynthesisFlagshipProgressOutcome = new ReportSynthesisFlagshipProgressOutcome();
        reportSynthesisFlagshipProgressOutcome.setCrpProgramOutcome(crpProgramOutcome);
        reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
          .saveReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
      }
      List<ReportSynthesisFlagshipProgressOutcomeMilestone> reportSynthesisFlagshipProgressOutcomeMilestoneList =
        reportSynthesisFlagshipProgressOutcome.getReportSynthesisFlagshipProgressOutcomeMilestones().stream()
          .filter(c -> c.getCrpMilestone().equals(crpMilestone)).collect(Collectors.toList());
      if (reportSynthesisFlagshipProgressOutcomeMilestoneList != null
        && reportSynthesisFlagshipProgressOutcomeMilestoneList.size() == 0) {
        ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone =
          new ReportSynthesisFlagshipProgressOutcomeMilestone();
        reportSynthesisFlagshipProgressOutcomeMilestone
          .setReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
        reportSynthesisFlagshipProgressOutcomeMilestone.setCrpMilestone(crpMilestone);
        reportSynthesisFlagshipProgressOutcomeMilestone.setMilestonesStatus(status);
        reportSynthesisFlagshipProgressOutcomeMilestone.setEvidence(newStatusPlannedMilestoneDTO.getEvidence());
        reportSynthesisFlagshipProgressOutcomeMilestone.setEvidenceLink(newStatusPlannedMilestoneDTO.getLinkEvidence());
        if (status.getId().longValue() == 4 || status.getId().longValue() == 5 || status.getId().longValue() == 6) {
          reportSynthesisFlagshipProgressOutcomeMilestone.setReason(repIndMilestoneReason);
        }
        if (status.getId().longValue() == 4) {
          reportSynthesisFlagshipProgressOutcomeMilestone
            .setExtendedYear(newStatusPlannedMilestoneDTO.getExtendedYear());
        }
        List<ReportSynthesisFlagshipProgressCrossCuttingMarker> reportSynthesisFlagshipProgressCrossCuttingMarkerList =
          new ArrayList<ReportSynthesisFlagshipProgressCrossCuttingMarker>();
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
              reportSynthesisFlagshipProgressCrossCuttingMarker
                .setReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisFlagshipProgressOutcomeMilestone);
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
        if (fieldErrors.isEmpty()) {
          reportSynthesisFlagshipProgressOutcomeMilestone = reportSynthesisFlagshipProgressOutcomeMilestoneManager
            .saveReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisFlagshipProgressOutcomeMilestone);
          plannedMilestoneStatusID = reportSynthesisFlagshipProgressOutcomeMilestone.getId();
          for (ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker : reportSynthesisFlagshipProgressCrossCuttingMarkerList) {
            reportSynthesisFlagshipProgressCrossCuttingMarkerManager
              .saveReportSynthesisFlagshipProgressCrossCuttingMarker(reportSynthesisFlagshipProgressCrossCuttingMarker);
          }
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Milestone", "is an invalid CRP Milestone"));
      }
      if (!fieldErrors.isEmpty()) {
        fieldErrors.stream().forEach(f -> System.out.println(f.getMessage()));
        throw new MARLOFieldValidationException("Field Validation errors", "",
          fieldErrors.stream()
            .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
            .collect(Collectors.toList()));
      }
    }
    return plannedMilestoneStatusID;
  }

  public Long updateStatusPlannedMilestone(NewStatusPlannedMilestoneDTO newStatusPlannedMilestoneDTO,
    String entityAcronym, User user) {
    Long plannedMilestoneStatusID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == newStatusPlannedMilestoneDTO.getPhase().getYear()
        && c.getName().equalsIgnoreCase(newStatusPlannedMilestoneDTO.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "phase",
        newStatusPlannedMilestoneDTO.getPhase().getYear() + " is an invalid year"));
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream().anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), entityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("createStatusPlannedMilestone", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }
    CrpProgram crpProgram = null;
    if (newStatusPlannedMilestoneDTO.getCrpProgramCode() != null
      && newStatusPlannedMilestoneDTO.getCrpProgramCode().length() > 0) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(newStatusPlannedMilestoneDTO.getCrpProgramCode());
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "CrpProgram", "is an invalid CRP Program"));
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "CrpProgram", "is an invalid CRP Program"));
    }

    CrpProgramOutcome crpProgramOutcome = null;
    if (newStatusPlannedMilestoneDTO.getCrpOutcomeCode() != null
      && newStatusPlannedMilestoneDTO.getCrpOutcomeCode().length() > 0) {
      crpProgramOutcome =
        crpProgramOutcomeManager.getCrpProgramOutcome(newStatusPlannedMilestoneDTO.getCrpOutcomeCode(), phase);
      if (crpProgramOutcome == null) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Outcome", "is an invalid CRP Outcome"));
      }
    }
    CrpMilestone crpMilestone =
      crpMilestoneManager.getCrpMilestoneByPhase(newStatusPlannedMilestoneDTO.getMilestoneCode(), phase.getId());
    if (crpMilestone == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Milestone", "is an invalid CRP Milestone"));
    }
    RepIndMilestoneReason repIndMilestoneReason = null;
    GeneralStatus status = generalStatusManager.getGeneralStatusById(newStatusPlannedMilestoneDTO.getStatus());
    if (status == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "Status", "is an invalid Status identifier"));
    } else {
      if (status.getId().longValue() == 4) {
        repIndMilestoneReason =
          repIndMilestoneReasonManager.getRepIndMilestoneReasonById(newStatusPlannedMilestoneDTO.getMainReason());
        if (repIndMilestoneReason == null) {
          fieldErrors.add(
            new FieldErrorDTO("createStatusPlannedMilestone", "Reason", "is an invalid Milestone reason identifier"));
        }
      }
    }

    ReportSynthesis reportSynthesis = null;
    ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress = null;
    ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome;
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
            List<ReportSynthesisFlagshipProgressOutcomeMilestone> reportSynthesisFlagshipProgressOutcomeMilestoneList =
              reportSynthesisFlagshipProgressOutcome.getReportSynthesisFlagshipProgressOutcomeMilestones().stream()
                .filter(c -> c.getCrpMilestone().equals(crpMilestone)).collect(Collectors.toList());
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
              }
              if (status.getId().longValue() == 4) {
                reportSynthesisFlagshipProgressOutcomeMilestone
                  .setExtendedYear(newStatusPlannedMilestoneDTO.getExtendedYear());
              }
              List<ReportSynthesisFlagshipProgressCrossCuttingMarker> reportSynthesisFlagshipProgressCrossCuttingMarkerList =
                new ArrayList<ReportSynthesisFlagshipProgressCrossCuttingMarker>();
              for (NewCrosscuttingMarkersSynthesisDTO crosscuttingmarker : newStatusPlannedMilestoneDTO
                .getCrosscuttinmarkerList()) {
                CgiarCrossCuttingMarker cgiarCrossCuttingMarker = cgiarCrossCuttingMarkerManager
                  .getCgiarCrossCuttingMarkerById(Long.parseLong(crosscuttingmarker.getCrossCuttingmarker()));
                if (cgiarCrossCuttingMarker != null) {
                  RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
                    repIndGenderYouthFocusLevelManager.getRepIndGenderYouthFocusLevelById(
                      Long.parseLong(crosscuttingmarker.getCrossCuttingmarkerScore()));
                  if (repIndGenderYouthFocusLevel != null) {
                    for (ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker : reportSynthesisFlagshipProgressOutcomeMilestone
                      .getReportSynthesisFlagshipProgressCrossCuttingMarkers().stream().collect(Collectors.toList())) {
                      if (crosscuttingmarker.getCrossCuttingmarker()
                        .equals("" + reportSynthesisFlagshipProgressCrossCuttingMarker.getMarker().getId())) {
                        reportSynthesisFlagshipProgressCrossCuttingMarker
                          .setJust(crosscuttingmarker.getJustification());
                        reportSynthesisFlagshipProgressCrossCuttingMarker.setMarker(cgiarCrossCuttingMarker);
                        reportSynthesisFlagshipProgressCrossCuttingMarker.setFocus(repIndGenderYouthFocusLevel);
                        reportSynthesisFlagshipProgressCrossCuttingMarkerList
                          .add(reportSynthesisFlagshipProgressCrossCuttingMarker);
                      }
                    }
                  } else {
                    fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "CrossCuttingMarkerScore",
                      "is an invalid Gender Youth Focus Level"));
                  }
                } else {
                  fieldErrors.add(new FieldErrorDTO("createStatusPlannedMilestone", "CrossCuttingMarker",
                    "is an invalid Cross Cutting Marker"));
                }
              }
              if (fieldErrors.isEmpty()) {
                reportSynthesisFlagshipProgressOutcomeMilestone = reportSynthesisFlagshipProgressOutcomeMilestoneManager
                  .saveReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisFlagshipProgressOutcomeMilestone);
                plannedMilestoneStatusID = reportSynthesisFlagshipProgressOutcomeMilestone.getId();
                for (ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker : reportSynthesisFlagshipProgressCrossCuttingMarkerList) {
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
    return plannedMilestoneStatusID;
  }


}
