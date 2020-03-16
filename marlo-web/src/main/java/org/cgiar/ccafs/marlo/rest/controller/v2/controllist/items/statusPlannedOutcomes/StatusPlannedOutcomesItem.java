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

import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.CrpProgramOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisFlagshipProgressOutcomeManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpProgramOutcome;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisFlagshipProgressOutcome;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewStatusPlannedOutcomeDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;

@Named
public class StatusPlannedOutcomesItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private CrpProgramManager crpProgramManager;
  private CrpProgramOutcomeManager crpProgramOutcomeManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager;
  private ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;


  @Inject
  public StatusPlannedOutcomesItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, CrpProgramOutcomeManager crpProgramOutcomeManager,
    ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisFlagshipProgressManager reportSynthesisFlagshipProgressManager,
    ReportSynthesisFlagshipProgressOutcomeManager reportSynthesisFlagshipProgressOutcomeManager,
    LiaisonInstitutionManager liaisonInstitutionManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.crpProgramManager = crpProgramManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisFlagshipProgressManager = reportSynthesisFlagshipProgressManager;
    this.reportSynthesisFlagshipProgressOutcomeManager = reportSynthesisFlagshipProgressOutcomeManager;
    this.crpProgramOutcomeManager = crpProgramOutcomeManager;
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
    if (fieldErrors.isEmpty()) {
      ReportSynthesis reportSynthesis = null;
      LiaisonInstitution liaisonInstitution =
        liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), globalUnitEntity.getId());
      reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      if (reportSynthesis == null) {
        reportSynthesis = new ReportSynthesis();
        reportSynthesis.setLiaisonInstitution(liaisonInstitution);
        reportSynthesis.setPhase(phase);
        reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        // set report synthesis flagship progress
        ReportSynthesisFlagshipProgress reportSynthesisFlagshipProgress = new ReportSynthesisFlagshipProgress();
        reportSynthesisFlagshipProgress.setCreatedBy(user);
        reportSynthesisFlagshipProgress.setReportSynthesis(reportSynthesis);
        reportSynthesisFlagshipProgressManager.saveReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
        // set report synthesis flagship progress outcome
        ReportSynthesisFlagshipProgressOutcome reportSynthesisFlagshipProgressOutcome =
          new ReportSynthesisFlagshipProgressOutcome();
        reportSynthesisFlagshipProgressOutcome.setReportSynthesisFlagshipProgress(reportSynthesisFlagshipProgress);
        reportSynthesisFlagshipProgressOutcome.setCrpProgramOutcome(crpProgramOutcome);
        reportSynthesisFlagshipProgressOutcome.setSummary(newStatusPlannedOutcomeDTO.getSumary());
        reportSynthesisFlagshipProgressOutcomeManager
          .saveReportSynthesisFlagshipProgressOutcome(reportSynthesisFlagshipProgressOutcome);
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
