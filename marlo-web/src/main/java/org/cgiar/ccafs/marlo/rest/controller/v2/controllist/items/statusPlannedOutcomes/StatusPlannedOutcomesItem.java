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
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressCrossCuttingMarker;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcomeMilestone;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewCrosscuttingMarkersSynthesisDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewStatusPlannedMilestoneDTO;
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

  private StatusPlannedOutcomesMapper statusPlannedOutcomesMapper;


  @Inject
  public StatusPlannedOutcomesItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, CrpMilestoneManager crpMilestoneManager,
    CrpProgramOutcomeManager crpProgramOutcomeManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager,
    ReportSynthesisFlagshipProgressOutcomeMilestoneManager reportSynthesisFlagshipProgressOutcomeMilestoneManager,
    ReportSynthesisFlagshipProgressCrossCuttingMarkerManager reportSynthesisFlagshipProgressCrossCuttingMarkerManager,
    LiaisonInstitutionManager liaisonInstitutionManager, GeneralStatusManager generalStatusManager,
    CgiarCrossCuttingMarkerManager cgiarCrossCuttingMarkerManager,
    RepIndGenderYouthFocusLevelManager repIndGenderYouthFocusLevelManager,
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
    this.statusPlannedOutcomesMapper = statusPlannedOutcomesMapper;
  }


  public Long createStatusPlannedOutcome(NewStatusPlannedOutcomeDTO newStatusPlannedOutcomeDTO, String entityAcronym,
    User user) {
    Long plannedOutcomeStatusID = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == newStatusPlannedOutcomeDTO.getPhase().getYear()
        && c.getName().equalsIgnoreCase(newStatusPlannedOutcomeDTO.getPhase().getName()))
      .findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "phase",
        newStatusPlannedOutcomeDTO.getPhase().getYear() + " is an invalid year"));
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream().anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), entityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("createStatusPlannedOutcome", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }
    CrpProgram crpProgram = null;
    if (newStatusPlannedOutcomeDTO.getCrpProgramCode() != null
      && newStatusPlannedOutcomeDTO.getCrpProgramCode().length() > 0) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(newStatusPlannedOutcomeDTO.getCrpProgramCode());
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "CrpProgram", "is an invalid CRP Program"));
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "CrpProgram", "is an invalid CRP Program"));
    }
    CrpProgramOutcome crpProgramOutcome = null;
    if (newStatusPlannedOutcomeDTO.getCrpOutcomeCode() != null
      && newStatusPlannedOutcomeDTO.getCrpOutcomeCode().length() > 0) {
      crpProgramOutcome =
        crpProgramOutcomeManager.getCrpProgramOutcome(newStatusPlannedOutcomeDTO.getCrpOutcomeCode(), phase);
      if (crpProgramOutcome == null) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Outcome", "is an invalid CRP Outcome"));
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
        // set report synthesis flagship progress
        reportSynthesisFlagshipProgress = new ReportSynthesisFlagshipProgress();
        reportSynthesisFlagshipProgress.setCreatedBy(user);
        // set report synthesis flagship progress outcome
        reportSynthesisFlagshipProgressOutcome = new ReportSynthesisFlagshipProgressOutcome();
        reportSynthesisFlagshipProgressOutcome.setCrpProgramOutcome(crpProgramOutcome);
        reportSynthesisFlagshipProgressOutcome.setSummary(newStatusPlannedOutcomeDTO.getSumary());
        List<ReportSynthesisFlagshipProgressOutcomeMilestone> reportSynthesisFlagshipProgressOutcomeMilestoneList =
          new ArrayList<ReportSynthesisFlagshipProgressOutcomeMilestone>();
        // looking for milestones and their status
        for (NewStatusPlannedMilestoneDTO milestones : newStatusPlannedOutcomeDTO.getStatusMilestoneList()) {
          ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone =
            new ReportSynthesisFlagshipProgressOutcomeMilestone();
          CrpMilestone crpMilestone =
            crpMilestoneManager.getCrpMilestoneByPhase(milestones.getMilestoneCode(), phase.getId());
          // validate milestones by smocode
          if (crpMilestone != null) {
            GeneralStatus status = generalStatusManager.getGeneralStatusById(milestones.getStatus());
            if (status != null) {
              reportSynthesisFlagshipProgressOutcomeMilestone.setCrpMilestone(crpMilestone);
              reportSynthesisFlagshipProgressOutcomeMilestone.setEvidence(milestones.getEvidence());
              reportSynthesisFlagshipProgressOutcomeMilestone.setMilestonesStatus(status);
              reportSynthesisFlagshipProgressOutcomeMilestone
                .setReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
              reportSynthesisFlagshipProgressOutcomeMilestone.setCreatedBy(user);
              List<ReportSynthesisFlagshipProgressCrossCuttingMarker> reportSynthesisFlagshipProgressCrossCuttingMarkerList =
                new ArrayList<ReportSynthesisFlagshipProgressCrossCuttingMarker>();
              // getting justification for cross cutting markers and their levels
              for (NewCrosscuttingMarkersSynthesisDTO crosscuttingmarkers : milestones.getCrosscuttinmarkerList()) {
                CgiarCrossCuttingMarker cgiarCrossCuttingMarker = cgiarCrossCuttingMarkerManager
                  .getCgiarCrossCuttingMarkerById(Long.parseLong(crosscuttingmarkers.getCrossCuttingmarker()));
                if (cgiarCrossCuttingMarker != null) {
                  RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
                    repIndGenderYouthFocusLevelManager.getRepIndGenderYouthFocusLevelById(
                      Long.parseLong(crosscuttingmarkers.getCrossCuttingmarkerScore()));
                  if (repIndGenderYouthFocusLevel != null) {
                    ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker =
                      new ReportSynthesisFlagshipProgressCrossCuttingMarker();
                    reportSynthesisFlagshipProgressCrossCuttingMarker.setMarker(cgiarCrossCuttingMarker);
                    reportSynthesisFlagshipProgressCrossCuttingMarker.setFocus(repIndGenderYouthFocusLevel);
                    reportSynthesisFlagshipProgressCrossCuttingMarker.setJust(crosscuttingmarkers.getJustification());
                    reportSynthesisFlagshipProgressCrossCuttingMarkerList
                      .add(reportSynthesisFlagshipProgressCrossCuttingMarker);
                  }
                } else {
                  fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Crosscutting markers",
                    crosscuttingmarkers.getCrossCuttingmarker() + "is an invalid CGIAR Crosscutting marker"));
                }
                reportSynthesisFlagshipProgressOutcomeMilestone
                  .setMarkers(reportSynthesisFlagshipProgressCrossCuttingMarkerList);
              }
              reportSynthesisFlagshipProgressOutcomeMilestoneList.add(reportSynthesisFlagshipProgressOutcomeMilestone);
            } else {
              fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Status",
                milestones.getStatus() + "is an invalid Milestone status code"));
            }

          } else {
            fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Milestone",
              milestones.getMilestoneCode() + "is an invalid Milestone identifier"));
          }
        }
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
              if (reportSynthesisFlagshipProgressOutcome != null) {
                for (ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone : reportSynthesisFlagshipProgressOutcomeMilestoneList) {
                  reportSynthesisFlagshipProgressOutcomeMilestone
                    .setReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
                  reportSynthesisFlagshipProgressOutcomeMilestone =
                    reportSynthesisFlagshipProgressOutcomeMilestoneManager
                      .saveReportSynthesisFlagshipProgressOutcomeMilestone(
                        reportSynthesisFlagshipProgressOutcomeMilestone);
                  CrpMilestone milestoneObject = crpMilestoneManager
                    .getCrpMilestoneById(reportSynthesisFlagshipProgressOutcomeMilestone.getCrpMilestone().getId());
                  // update milestone
                  milestoneObject
                    .setMilestonesStatus(reportSynthesisFlagshipProgressOutcomeMilestone.getMilestonesStatus());
                  for (ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker : reportSynthesisFlagshipProgressOutcomeMilestone
                    .getMarkers()) {
                    reportSynthesisFlagshipProgressCrossCuttingMarker
                      .setReportSynthesisFlagshipProgressOutcomeMilestone(
                        reportSynthesisFlagshipProgressOutcomeMilestone);
                    reportSynthesisFlagshipProgressCrossCuttingMarkerManager
                      .saveReportSynthesisFlagshipProgressCrossCuttingMarker(
                        reportSynthesisFlagshipProgressCrossCuttingMarker);
                  }
                }
              }
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
          List<ReportSynthesisFlagshipProgressOutcomeMilestone> reportSynthesisFlagshipProgressOutcomeMilestoneList =
            new ArrayList<ReportSynthesisFlagshipProgressOutcomeMilestone>();
          // looking for milestones and their status
          for (NewStatusPlannedMilestoneDTO milestones : newStatusPlannedOutcomeDTO.getStatusMilestoneList()) {
            ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone =
              new ReportSynthesisFlagshipProgressOutcomeMilestone();
            CrpMilestone crpMilestone =
              crpMilestoneManager.getCrpMilestoneByPhase(milestones.getMilestoneCode(), phase.getId());
            // validate milestones by smocode
            if (crpMilestone != null) {
              GeneralStatus status = generalStatusManager.getGeneralStatusById(milestones.getStatus());

              if (status != null) {
                reportSynthesisFlagshipProgressOutcomeMilestone.setCrpMilestone(crpMilestone);
                reportSynthesisFlagshipProgressOutcomeMilestone.setEvidence(milestones.getEvidence());
                reportSynthesisFlagshipProgressOutcomeMilestone.setMilestonesStatus(status);
                crpMilestone.setMilestonesStatus(status);
                reportSynthesisFlagshipProgressOutcomeMilestone
                  .setReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
                reportSynthesisFlagshipProgressOutcomeMilestone.setCreatedBy(user);
                List<ReportSynthesisFlagshipProgressCrossCuttingMarker> reportSynthesisFlagshipProgressCrossCuttingMarkerList =
                  new ArrayList<ReportSynthesisFlagshipProgressCrossCuttingMarker>();
                // getting justification for cross cutting markers and their levels
                for (NewCrosscuttingMarkersSynthesisDTO crosscuttingmarkers : milestones.getCrosscuttinmarkerList()) {
                  CgiarCrossCuttingMarker cgiarCrossCuttingMarker = cgiarCrossCuttingMarkerManager
                    .getCgiarCrossCuttingMarkerById(Long.parseLong(crosscuttingmarkers.getCrossCuttingmarker()));
                  if (cgiarCrossCuttingMarker != null) {
                    RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
                      repIndGenderYouthFocusLevelManager.getRepIndGenderYouthFocusLevelById(
                        Long.parseLong(crosscuttingmarkers.getCrossCuttingmarkerScore()));
                    if (repIndGenderYouthFocusLevel != null) {
                      ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker =
                        new ReportSynthesisFlagshipProgressCrossCuttingMarker();
                      reportSynthesisFlagshipProgressCrossCuttingMarker.setMarker(cgiarCrossCuttingMarker);
                      reportSynthesisFlagshipProgressCrossCuttingMarker.setFocus(repIndGenderYouthFocusLevel);
                      reportSynthesisFlagshipProgressCrossCuttingMarker.setJust(crosscuttingmarkers.getJustification());
                      reportSynthesisFlagshipProgressCrossCuttingMarkerList
                        .add(reportSynthesisFlagshipProgressCrossCuttingMarker);
                    }
                  } else {
                    fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Crosscutting markers",
                      crosscuttingmarkers.getCrossCuttingmarker() + "is an invalid CGIAR Crosscutting marker"));
                  }
                  reportSynthesisFlagshipProgressOutcomeMilestone
                    .setMarkers(reportSynthesisFlagshipProgressCrossCuttingMarkerList);
                }
                reportSynthesisFlagshipProgressOutcomeMilestoneList
                  .add(reportSynthesisFlagshipProgressOutcomeMilestone);
              } else {
                fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Status",
                  milestones.getStatus() + "is an invalid Milestone status code"));
              }

            } else {
              fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Milestone",
                milestones.getMilestoneCode() + "is an invalid Milestone identifier"));
            }
          }
          if (fieldErrors.isEmpty()) {
            reportSynthesisFlagshipProgress.setReportSynthesis(reportSynthesis);
            reportSynthesisFlagshipProgress = reportSynthesisFlagshipProgressManager
              .saveReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
            reportSynthesisFlagshipProgressOutcome.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
            reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
              .saveReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
            plannedOutcomeStatusID = reportSynthesisFlagshipProgressOutcome.getId();
            for (ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone : reportSynthesisFlagshipProgressOutcomeMilestoneList) {
              reportSynthesisFlagshipProgressOutcomeMilestone
                .setReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
              reportSynthesisFlagshipProgressOutcomeMilestone = reportSynthesisFlagshipProgressOutcomeMilestoneManager
                .saveReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisFlagshipProgressOutcomeMilestone);
              CrpMilestone milestoneObject = crpMilestoneManager
                .getCrpMilestoneById(reportSynthesisFlagshipProgressOutcomeMilestone.getCrpMilestone().getId());
              // update milestone
              milestoneObject
                .setMilestonesStatus(reportSynthesisFlagshipProgressOutcomeMilestone.getMilestonesStatus());
              crpMilestoneManager.saveCrpMilestone(milestoneObject);
              for (ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker : reportSynthesisFlagshipProgressOutcomeMilestone
                .getMarkers()) {
                reportSynthesisFlagshipProgressCrossCuttingMarker
                  .setReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisFlagshipProgressOutcomeMilestone);
                reportSynthesisFlagshipProgressCrossCuttingMarkerManager
                  .saveReportSynthesisFlagshipProgressCrossCuttingMarker(
                    reportSynthesisFlagshipProgressCrossCuttingMarker);
              }
            }
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
            List<ReportSynthesisFlagshipProgressOutcomeMilestone> reportSynthesisFlagshipProgressOutcomeMilestoneList =
              new ArrayList<ReportSynthesisFlagshipProgressOutcomeMilestone>();
            for (NewStatusPlannedMilestoneDTO milestones : newStatusPlannedOutcomeDTO.getStatusMilestoneList()) {
              ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone =
                new ReportSynthesisFlagshipProgressOutcomeMilestone();
              CrpMilestone crpMilestone =
                crpMilestoneManager.getCrpMilestoneByPhase(milestones.getMilestoneCode(), phase.getId());
              if (crpMilestone != null) {
                GeneralStatus status = generalStatusManager.getGeneralStatusById(milestones.getStatus());
                if (status != null) {
                  reportSynthesisFlagshipProgressOutcomeMilestone.setCrpMilestone(crpMilestone);
                  reportSynthesisFlagshipProgressOutcomeMilestone.setEvidence(milestones.getEvidence());
                  reportSynthesisFlagshipProgressOutcomeMilestone.setMilestonesStatus(status);
                  reportSynthesisFlagshipProgressOutcomeMilestone
                    .setReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
                  reportSynthesisFlagshipProgressOutcomeMilestone.setCreatedBy(user);
                  List<ReportSynthesisFlagshipProgressCrossCuttingMarker> reportSynthesisFlagshipProgressCrossCuttingMarkerList =
                    new ArrayList<ReportSynthesisFlagshipProgressCrossCuttingMarker>();
                  for (NewCrosscuttingMarkersSynthesisDTO crosscuttingmarkers : milestones.getCrosscuttinmarkerList()) {
                    CgiarCrossCuttingMarker cgiarCrossCuttingMarker = cgiarCrossCuttingMarkerManager
                      .getCgiarCrossCuttingMarkerById(Long.parseLong(crosscuttingmarkers.getCrossCuttingmarker()));
                    if (cgiarCrossCuttingMarker != null) {
                      RepIndGenderYouthFocusLevel repIndGenderYouthFocusLevel =
                        repIndGenderYouthFocusLevelManager.getRepIndGenderYouthFocusLevelById(
                          Long.parseLong(crosscuttingmarkers.getCrossCuttingmarkerScore()));
                      if (repIndGenderYouthFocusLevel != null) {
                        ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker =
                          new ReportSynthesisFlagshipProgressCrossCuttingMarker();
                        reportSynthesisFlagshipProgressCrossCuttingMarker.setMarker(cgiarCrossCuttingMarker);
                        reportSynthesisFlagshipProgressCrossCuttingMarker.setFocus(repIndGenderYouthFocusLevel);
                        reportSynthesisFlagshipProgressCrossCuttingMarker
                          .setJust(crosscuttingmarkers.getJustification());
                        reportSynthesisFlagshipProgressCrossCuttingMarkerList
                          .add(reportSynthesisFlagshipProgressCrossCuttingMarker);
                      }
                    } else {
                      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Crosscutting markers",
                        crosscuttingmarkers.getCrossCuttingmarker() + "is an invalid CGIAR Crosscutting marker"));
                    }
                    reportSynthesisFlagshipProgressOutcomeMilestone
                      .setMarkers(reportSynthesisFlagshipProgressCrossCuttingMarkerList);
                  }
                  reportSynthesisFlagshipProgressOutcomeMilestoneList
                    .add(reportSynthesisFlagshipProgressOutcomeMilestone);
                } else {
                  fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Status",
                    milestones.getStatus() + "is an invalid Milestone status code"));
                }

              } else {
                fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Milestone",
                  milestones.getMilestoneCode() + "is an invalid Milestone identifier"));
              }
            }
            if (fieldErrors.isEmpty()) {
              reportSynthesisFlagshipProgressOutcome
                .setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
              reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
                .saveReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
              plannedOutcomeStatusID = reportSynthesisFlagshipProgressOutcome.getId();
              for (ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone : reportSynthesisFlagshipProgressOutcomeMilestoneList) {
                reportSynthesisFlagshipProgressOutcomeMilestone
                  .setReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
                reportSynthesisFlagshipProgressOutcomeMilestone = reportSynthesisFlagshipProgressOutcomeMilestoneManager
                  .saveReportSynthesisFlagshipProgressOutcomeMilestone(reportSynthesisFlagshipProgressOutcomeMilestone);
                CrpMilestone milestoneObject = crpMilestoneManager
                  .getCrpMilestoneById(reportSynthesisFlagshipProgressOutcomeMilestone.getCrpMilestone().getId());
                // update milestone
                milestoneObject
                  .setMilestonesStatus(reportSynthesisFlagshipProgressOutcomeMilestone.getMilestonesStatus());
                for (ReportSynthesisFlagshipProgressCrossCuttingMarker reportSynthesisFlagshipProgressCrossCuttingMarker : reportSynthesisFlagshipProgressOutcomeMilestone
                  .getMarkers()) {
                  reportSynthesisFlagshipProgressCrossCuttingMarker.setReportSynthesisFlagshipProgressOutcomeMilestone(
                    reportSynthesisFlagshipProgressOutcomeMilestone);
                  reportSynthesisFlagshipProgressCrossCuttingMarkerManager
                    .saveReportSynthesisFlagshipProgressCrossCuttingMarker(
                      reportSynthesisFlagshipProgressCrossCuttingMarker);
                }
              }
            }
          } else {
            fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "ReportSynthesisFlagshipOutocome",
              "Report Synthesis flagship outcome alredy exists"));
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
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "GlobalUnitEntity",
        CGIARentityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().get();

    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "phase", " is an invalid phase-year"));
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), CGIARentityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("createStatusPlannedOutcome", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }
    ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome = null;

    CrpProgramOutcome crpProgramOutcome = null;
    if (outcomeID != null && outcomeID.length() > 0) {
      crpProgramOutcome = crpProgramOutcomeManager.getCrpProgramOutcome(outcomeID, phase);
      if (crpProgramOutcome == null) {
        fieldErrors.add(new FieldErrorDTO("createStatusPlannedOutcome", "Outcome", "is an invalid CRP Outcome"));
      }
    }

    if (fieldErrors.isEmpty()) {
      LiaisonInstitution liaisonInstitution = liaisonInstitutionManager
        .findByAcronymAndCrp(crpProgramOutcome.getCrpProgram().getAcronym(), globalUnitEntity.getId());
      ReportSynthesis reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      if (reportSynthesis != null) {
        ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress =
          reportSynthesisFlagshipProgressManager.getReportSynthesisFlagshipProgressById(reportSynthesis.getId());
        if (reportSynthesisFlagshipProgress != null) {
          reportSynthesisFlagshipProgressOutcome = reportSynthesisFlagshipProgressOutcomeManager
            .getOutcomeId(reportSynthesisFlagshipProgress.getId(), crpProgramOutcome.getId());
          // validate Synthesis FlagshipProgress Outcome
          if (reportSynthesisFlagshipProgressOutcome != null) {
            // setting milestones empty list
            List<ReportSynthesisFlagshipProgressOutcomeMilestone> reportSynthesisFlagshipProgressOutcomeMilestoneList =
              new ArrayList<ReportSynthesisFlagshipProgressOutcomeMilestone>();

            for (ReportSynthesisFlagshipProgressOutcomeMilestone reportSynthesisFlagshipProgressOutcomeMilestone : reportSynthesisFlagshipProgressOutcome
              .getReportSynthesisFlagshipProgressOutcomeMilestones().stream().filter(c -> c.isActive())
              .collect(Collectors.toList())) {
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
              reportSynthesisFlagshipProgressOutcomeMilestoneList.add(reportSynthesisFlagshipProgressOutcomeMilestone);
            }
            reportSynthesisFlagshipProgressOutcome.setMilestones(reportSynthesisFlagshipProgressOutcomeMilestoneList);
          }
        }
      }
    }
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }
    return Optional.ofNullable(reportSynthesisFlagshipProgressOutcome)
      .map(this.statusPlannedOutcomesMapper::reportSynthesisFlagshipProgressOutcomeToStatusPlannedOutcomesDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }
}
