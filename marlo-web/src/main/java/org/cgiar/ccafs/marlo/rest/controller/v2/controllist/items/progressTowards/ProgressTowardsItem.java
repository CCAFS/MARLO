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

import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisSrfProgressTargetManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorManager;
import org.cgiar.ccafs.marlo.data.manager.SrfSloIndicatorTargetManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTarget;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewSrfProgressTowardsTargetDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.math.NumberUtils;

@Named
public class ProgressTowardsItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private ReportSynthesisSrfProgressTargetManager reportSynthesisSrfProgressTargetManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisSrfProgressManager reportSynthesisSrfProgressManager;
  private CrpProgramManager crpProgramManager;
  private SrfSloIndicatorTargetManager srfSloIndicatorTargetManager;
  private SrfSloIndicatorManager srfSloIndicatorManager;

  @Inject
  public ProgressTowardsItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    ReportSynthesisSrfProgressTargetManager reportSynthesisSrfProgressTargetManager,
    ReportSynthesisManager reportSynthesisManager, LiaisonInstitutionManager liaisonInstitutionManager,
    CrpProgramManager crpProgramManager, ReportSynthesisSrfProgressManager reportSynthesisSrfProgressManager,
    SrfSloIndicatorTargetManager srfSloIndicatorTargetManager, SrfSloIndicatorManager srfSloIndicatorManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.reportSynthesisSrfProgressTargetManager = reportSynthesisSrfProgressTargetManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.crpProgramManager = crpProgramManager;
    this.reportSynthesisSrfProgressManager = reportSynthesisSrfProgressManager;
    this.srfSloIndicatorTargetManager = srfSloIndicatorTargetManager;
    this.srfSloIndicatorManager = srfSloIndicatorManager;
  }

  // TODO remove if it works
  public Long createProgressTowards(NewSrfProgressTowardsTargetDTO newSrfProgressTowardsTargetDTO, String entityAcronym,
    User user) {
    Long srfProgressTargetId = null;

    ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTarget = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createProgressTowards", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }
    Phase phase = phaseManager.findAll().stream()
      .filter(p -> p.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && p.getYear() == newSrfProgressTowardsTargetDTO.getPhase().getYear()
        && p.getName().equalsIgnoreCase(newSrfProgressTowardsTargetDTO.getPhase().getName()))
      .findFirst().get();
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("createProgressTowards", "Phase",
        newSrfProgressTowardsTargetDTO.getPhase().getYear() + " is an invalid year."));
    }

    if (fieldErrors.isEmpty()) {
      CrpProgram crpProgram = new CrpProgram();
      ReportSynthesis reportSynthesis = null;
      LiaisonInstitution liaisonInstitution = null;
      ReportSynthesisSrfProgress reportSynthesisSrfProgress = null;
      SrfSloIndicatorTarget srfSloIndicatorTarget = null;

      // we check if a ReportSynthesisSrfProgressTarget for the Phase and ReportSynthesisSrfProgressTarget already exist
      // start ReportSynthesisSrfProgressTarget
      if (newSrfProgressTowardsTargetDTO.getSrfSloIndicatorTargetId() != null
        && NumberUtils.isParsable(newSrfProgressTowardsTargetDTO.getSrfSloIndicatorTargetId())) {
        reportSynthesisSrfProgressTarget = reportSynthesisSrfProgressTargetManager.findAll().stream()
          .filter(pt -> pt.getReportSynthesisSrfProgress().getReportSynthesis().getPhase().getId() == phase.getId()
            && pt.getSrfSloIndicatorTarget().getId() == Long
              .valueOf(newSrfProgressTowardsTargetDTO.getSrfSloIndicatorTargetId()))
          .findFirst().orElse(null);
        if (reportSynthesisSrfProgressTarget != null) {
          fieldErrors.add(new FieldErrorDTO("createProgressTowards", "ReportSynthesisSrfProgressTargetEntity",
            "A Report Synthesis Srf Progress Target was found for the phase. If you want to update it, please use the update method."));
        }
      }
      // end ReportSynthesisSrfProgressTarget

      // start SrfSloIndicatorTarget
      if (newSrfProgressTowardsTargetDTO.getSrfSloIndicatorTargetId() != null
        && NumberUtils.isParsable(newSrfProgressTowardsTargetDTO.getSrfSloIndicatorTargetId().trim())) {
        srfSloIndicatorTarget = srfSloIndicatorTargetManager.getSrfSloIndicatorTargetById(
          Long.valueOf(newSrfProgressTowardsTargetDTO.getSrfSloIndicatorTargetId().trim()));
        if (srfSloIndicatorTarget == null) {
          fieldErrors.add(new FieldErrorDTO("createProgressTowards", "SrfSloIndicatorTargetEntity",
            srfProgressTargetId + " is an invalid Srf Slo Indicator Target code."));
        }

        // FIXME this will break in 2023 so, good luck!
        // TODO write a better error message
        if (srfSloIndicatorTarget.getYear() > 2022) {
          fieldErrors.add(new FieldErrorDTO("createProgressTowards", "SrfSloIndicatorTargetEntity",
            srfProgressTargetId + " is from a year that have not been activated."));
        }
      }
      // end SrfSloIndicatorTarget

      // start CrpProgram (flagshipProgram)
      if (newSrfProgressTowardsTargetDTO.getFlagshipProgramId() != null
        && NumberUtils.isParsable(newSrfProgressTowardsTargetDTO.getFlagshipProgramId().trim())) {
        crpProgram =
          crpProgramManager.getCrpProgramById(Long.valueOf(newSrfProgressTowardsTargetDTO.getFlagshipProgramId()));
        if (crpProgram == null) {
          fieldErrors.add(new FieldErrorDTO("createProgressTowards", "FlagshipEntity",
            newSrfProgressTowardsTargetDTO.getFlagshipProgramId() + " is an invalid CRP Program code."));
        }
      }
      // end CrpProgram (flagshipProgram)

      // start ReportSynthesis
      if (crpProgram != null) {
        liaisonInstitution = liaisonInstitutionManager.findByAcronym(crpProgram.getAcronym());
        reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      }
      // end ReportSynthesis

      // start ReportSynthesisSrfProgress
      if (reportSynthesis != null) {
        reportSynthesisSrfProgress = reportSynthesis.getReportSynthesisSrfProgress();
      }
      // end ReportSynthesisSrfProgress

      // all validated! now it is supposed to be ok to save the entities
      if (fieldErrors.isEmpty()) {
        reportSynthesisSrfProgressTarget = new ReportSynthesisSrfProgressTarget();
        // creating new ReportSynthesis if it does not exist
        if (reportSynthesis == null) {
          reportSynthesis = new ReportSynthesis();
          liaisonInstitution = liaisonInstitutionManager.findByAcronym(crpProgram.getAcronym());
          reportSynthesis.setLiaisonInstitution(liaisonInstitution);
          reportSynthesis.setPhase(phase);
          reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }
        // creating ReportSynthesisSrfProgress if it does not exist
        if (reportSynthesisSrfProgress == null) {
          reportSynthesisSrfProgress = new ReportSynthesisSrfProgress();
          reportSynthesisSrfProgress.setReportSynthesis(reportSynthesis);
          // TODO summary pending. where should i get it if it does not exist?
          reportSynthesisSrfProgress.setSummary(null);
          reportSynthesisSrfProgressManager.saveReportSynthesisSrfProgress(reportSynthesisSrfProgress);
        }
        reportSynthesisSrfProgressTarget.setReportSynthesisSrfProgress(reportSynthesisSrfProgress);
        reportSynthesisSrfProgressTarget.setSrfSloIndicatorTarget(srfSloIndicatorTarget);
        reportSynthesisSrfProgressTarget.setBirefSummary(newSrfProgressTowardsTargetDTO.getBriefSummary());
        reportSynthesisSrfProgressTarget
          .setAdditionalContribution(newSrfProgressTowardsTargetDTO.getAdditionalContribution());
        ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTargetDB = reportSynthesisSrfProgressTargetManager
          .saveReportSynthesisSrfProgressTarget(reportSynthesisSrfProgressTarget);
        if (reportSynthesisSrfProgressTargetDB != null) {
          srfProgressTargetId = reportSynthesisSrfProgressTargetDB.getId();
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

  // TODO remove if correct
  // public Long createProgressTowards(NewSrfProgressTowardsDTO newSrfProgressTowardsDTO, String entityAcronym,
  // User user) {
  // Long srfProgressId = null;
  // Set<ReportSynthesisSrfProgressTarget> reportSynthesisSrfProgressTargets = null;
  // ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTarget = new ReportSynthesisSrfProgressTarget();
  //
  // List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
  //
  // GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
  // if (globalUnitEntity == null) {
  // fieldErrors.add(new FieldErrorDTO("createProgressTowards", "GlobalUnitEntity",
  // entityAcronym + " is an invalid CGIAR entity acronym"));
  // }
  // Phase phase = phaseManager.findAll().stream()
  // .filter(p -> p.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
  // && p.getYear() == newSrfProgressTowardsDTO.getPhase().getYear()
  // && p.getName().equalsIgnoreCase(newSrfProgressTowardsDTO.getPhase().getName()))
  // .findFirst().get();
  // if (phase == null) {
  // fieldErrors.add(new FieldErrorDTO("createProgressTowards", "Phase",
  // newSrfProgressTowardsDTO.getPhase().getYear() + " is an invalid year."));
  // }
  //
  // if (fieldErrors.isEmpty()) {
  // CrpProgram crpProgram = new CrpProgram();
  // ReportSynthesis reportSynthesis = null;
  // LiaisonInstitution liaisonInstitution = null;
  // ReportSynthesisSrfProgress reportSynthesisSrfProgress = null;
  // SrfSloIndicatorTarget srfSloIndicatorTarget = null;
  //
  // // we check if a ReportSynthesisSrfProgressTarget for the Phase and ReportSynthesisSrfProgressTarget already exist
  // // start ReportSynthesisSrfProgressTarget
  // if (newSrfProgressTowardsDTO.getSrfSloTargets() != null
  // && !newSrfProgressTowardsDTO.getSrfSloTargets().isEmpty()) {
  // reportSynthesisSrfProgressTargets = reportSynthesisSrfProgressTargetManager.findAll().stream()
  // .filter(pt -> newSrfProgressTowardsDTO.getSrfSloTargets().stream()
  // .anyMatch(id -> pt.getReportSynthesisSrfProgress().getReportSynthesis().getPhase().getId() == phase.getId()
  // && id.getSrfSloIndicatorTargetId().equals(pt.getSrfSloIndicatorTarget().getId().toString())))
  // .collect(Collectors.toSet());
  // if (reportSynthesisSrfProgressTargets != null && !reportSynthesisSrfProgressTargets.isEmpty()) {
  // fieldErrors.add(new FieldErrorDTO("createProgressTowards", "ReportSynthesisSrfProgressTargetEntity",
  // "A Report Synthesis Srf Progress Target was found for the phase. If you want to update it, please use the update
  // method."));
  // }
  // }
  // // end ReportSynthesisSrfProgressTarget
  //
  // // // start ReportSynthesisSrfProgressTarget
  // // if (newSrfProgressTowardsDTO.getSrfSloTargets() != null
  // // && NumberUtils.isParsable(newSrfProgressTowardsDTO.getSrfSloIndicatorTargetId())) {
  // // reportSynthesisSrfProgressTarget = reportSynthesisSrfProgressTargetManager.findAll().stream()
  // // .filter(pt -> pt.getReportSynthesisSrfProgress().getReportSynthesis().getPhase().getId() == phase.getId()
  // // && pt.getSrfSloIndicatorTarget().getId() == Long
  // // .valueOf(newSrfProgressTowardsDTO.getSrfSloIndicatorTargetId()))
  // // .findFirst().orElse(null);
  // // if (reportSynthesisSrfProgressTarget != null) {
  // // fieldErrors.add(new FieldErrorDTO("createProgressTowards", "ReportSynthesisSrfProgressTargetEntity",
  // // "A Report Synthesis Srf Progress Target was found for the phase. If you want to update it, please use the
  // // update method."));
  // // }
  // // }
  // // // end ReportSynthesisSrfProgressTarget
  //
  // // start CrpProgram (flagshipProgram)
  // if (newSrfProgressTowardsDTO.getFlagshipProgramId() != null
  // && NumberUtils.isParsable(newSrfProgressTowardsDTO.getFlagshipProgramId().trim())) {
  // crpProgram = crpProgramManager.getCrpProgramById(Long.valueOf(newSrfProgressTowardsDTO.getFlagshipProgramId()));
  // if (crpProgram == null) {
  // fieldErrors.add(new FieldErrorDTO("createProgressTowards", "FlagshipEntity",
  // newSrfProgressTowardsDTO.getFlagshipProgramId() + " is an invalid CRP Program code."));
  // }
  // }
  // // end CrpProgram (flagshipProgram)
  //
  // // start ReportSynthesis
  // if (crpProgram != null) {
  // liaisonInstitution = liaisonInstitutionManager.findByAcronym(crpProgram.getAcronym());
  // reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
  // }
  // // end ReportSynthesis
  //
  // // start ReportSynthesisSrfProgress
  // if (reportSynthesis != null) {
  // reportSynthesisSrfProgress = reportSynthesis.getReportSynthesisSrfProgress();
  // }
  // // end ReportSynthesisSrfProgress
  //
  // // all validated! now it is supposed to be ok to save the entities
  // if (fieldErrors.isEmpty()) {
  // reportSynthesisSrfProgress = new ReportSynthesisSrfProgress();
  // // creating new ReportSynthesis if it does not exist
  // if (reportSynthesis == null) {
  // reportSynthesis = new ReportSynthesis();
  // liaisonInstitution = liaisonInstitutionManager.findByAcronym(crpProgram.getAcronym());
  // reportSynthesis.setLiaisonInstitution(liaisonInstitution);
  // reportSynthesis.setPhase(phase);
  // reportSynthesisManager.saveReportSynthesis(reportSynthesis);
  // }
  //
  // // creating new ReportSynthesis if it does not exist
  // reportSynthesisSrfProgress.setReportSynthesis(reportSynthesis);
  // reportSynthesisSrfProgress.setSummary(newSrfProgressTowardsDTO.getSummary());
  // reportSynthesisSrfProgressManager.saveReportSynthesisSrfProgress(reportSynthesisSrfProgress);
  //
  // for (NewSrfProgressTowardsTargetDTO srfProgressTowardsTargetsDTO : newSrfProgressTowardsDTO
  // .getSrfSloTargets()) {
  // reportSynthesisSrfProgressTarget = new ReportSynthesisSrfProgressTarget();
  // reportSynthesisSrfProgressTarget
  // .setAdditionalContribution(srfProgressTowardsTargetsDTO.getAdditionalContribution());
  // reportSynthesisSrfProgressTarget.setBirefSummary(srfProgressTowardsTargetsDTO.getBriefSummary());
  // reportSynthesisSrfProgressTarget.setReportSynthesisSrfProgress(reportSynthesisSrfProgress);
  // // TODO this can be better
  // SrfSloIndicatorTarget sloIndicatorTarget = srfSloIndicatorTargetManager
  // .getSrfSloIndicatorTargetById(Long.valueOf(srfProgressTowardsTargetsDTO.getSrfSloIndicatorTargetId()));
  // if (sloIndicatorTarget != null) {
  // reportSynthesisSrfProgressTarget.setSrfSloIndicatorTarget(srfSloIndicatorTarget);
  // }
  // }
  //
  // ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTargetDB = reportSynthesisSrfProgressTargetManager
  // .saveReportSynthesisSrfProgressTarget(reportSynthesisSrfProgressTarget);
  // if (reportSynthesisSrfProgressTargetDB != null) {
  // srfProgressId = reportSynthesisSrfProgressTargetDB.getId();
  // }
  // }
  // }
  //
  // if (!fieldErrors.isEmpty()) {
  // fieldErrors.forEach(e -> System.out.println(e.getMessage()));
  // throw new MARLOFieldValidationException("Field Validation errors", "",
  // fieldErrors.stream()
  // .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
  // .collect(Collectors.toList()));
  // }
  //
  // return srfProgressId;
  // }

}
