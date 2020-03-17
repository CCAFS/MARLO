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
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgress;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisSrfProgressTarget;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicator;
import org.cgiar.ccafs.marlo.data.model.SrfSloIndicatorTarget;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewSrfProgressTowardsTargetDTO;
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
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ProgressTowardsItem<T> {

  private PhaseManager phaseManager;
  private GlobalUnitManager globalUnitManager;
  private ReportSynthesisSrfProgressTargetManager reportSynthesisSrfProgressTargetManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisSrfProgressManager reportSynthesisSrfProgressManager;
  private CrpProgramManager crpProgramManager;
  private SrfSloIndicatorManager srfSloIndicatorManager;

  private SrfProgressTowardsTargetMapper srfProgressTowardsTargetMapper;

  @Inject
  public ProgressTowardsItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    ReportSynthesisSrfProgressTargetManager reportSynthesisSrfProgressTargetManager,
    ReportSynthesisManager reportSynthesisManager, LiaisonInstitutionManager liaisonInstitutionManager,
    CrpProgramManager crpProgramManager, ReportSynthesisSrfProgressManager reportSynthesisSrfProgressManager,
    SrfProgressTowardsTargetMapper srfProgressTowardsTargetMapper, SrfSloIndicatorManager srfSloIndicatorManager) {
    this.phaseManager = phaseManager;
    this.globalUnitManager = globalUnitManager;
    this.reportSynthesisSrfProgressTargetManager = reportSynthesisSrfProgressTargetManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.crpProgramManager = crpProgramManager;
    this.reportSynthesisSrfProgressManager = reportSynthesisSrfProgressManager;
    this.srfSloIndicatorManager = srfSloIndicatorManager;

    this.srfProgressTowardsTargetMapper = srfProgressTowardsTargetMapper;
  }

  public Long createProgressTowards(NewSrfProgressTowardsTargetDTO newSrfProgressTowardsTargetDTO, String entityAcronym,
    User user) {
    Long srfProgressTargetId = null;
    ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTarget = null;
    Phase phase = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createProgressTowards", "GlobalUnitEntity",
        entityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createProgressTowards", "GlobalUnitEntity",
          "The Global Unit with acronym " + entityAcronym + " is not active."));
      }

    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream().anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), entityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("createKeyExternalPartnership", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newSrfProgressTowardsTargetDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("createProgressTowards", "PhaseEntity", "Phase must not be null"));
    } else {
      if (newSrfProgressTowardsTargetDTO.getPhase().getName() == null
        || newSrfProgressTowardsTargetDTO.getPhase().getName().trim().isEmpty()
        || newSrfProgressTowardsTargetDTO.getPhase().getYear() == null
        // DANGER! Magic number ahead
        || newSrfProgressTowardsTargetDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("createProgressTowards", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> p.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
            && p.getYear() == newSrfProgressTowardsTargetDTO.getPhase().getYear()
            && p.getName().equalsIgnoreCase(newSrfProgressTowardsTargetDTO.getPhase().getName()))
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors
            .add(new FieldErrorDTO("createProgressTowards", "phase", newSrfProgressTowardsTargetDTO.getPhase().getName()
              + ' ' + newSrfProgressTowardsTargetDTO.getPhase().getYear() + " is an invalid phase"));
        }

      }

    }

    if (fieldErrors.isEmpty()) {
      CrpProgram crpProgram = null;
      ReportSynthesis reportSynthesis = null;
      LiaisonInstitution liaisonInstitution = null;
      ReportSynthesisSrfProgress reportSynthesisSrfProgress = null;
      SrfSloIndicatorTarget srfSloIndicatorTarget = null;
      SrfSloIndicator srfSloIndicator = null;

      // we check if a ReportSynthesisSrfProgressTarget for the Phase and ReportSynthesisSrfProgressTarget already exist
      // start ReportSynthesisSrfProgressTarget
      if (newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId() != null
        && !newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId().trim().isEmpty()) {
        String sloIndicatorId = newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId().trim();
        if (NumberUtils.isParsable(sloIndicatorId)) {
          Long phaseId = phase.getId();
          reportSynthesisSrfProgressTarget = reportSynthesisSrfProgressTargetManager.findAll().stream()
            .filter(pt -> pt.getReportSynthesisSrfProgress().getReportSynthesis().getPhase().getId() == phaseId
              && pt.getSrfSloIndicatorTarget().getSrfSloIndicator().getId() == Long.valueOf(sloIndicatorId))
            .findFirst().orElse(null);

          if (reportSynthesisSrfProgressTarget != null) {
            fieldErrors.add(new FieldErrorDTO("createProgressTowards", "ReportSynthesisSrfProgressTargetEntity",
              "A Report Synthesis Srf Progress Target was found for the phase. If you want to update it, please use the update method."));
          }

        } else {
          fieldErrors.add(new FieldErrorDTO("createProgressTowards", "SrfSloIndicatorEntity",
            "Srf Slo Indicator code is an invalid numeric id."));
        }

      } else {
        fieldErrors.add(new FieldErrorDTO("createProgressTowards", "SrfSloIndicatorEntity",
          "Srf Slo Indicator code can not be null nor empty."));
      }
      // end ReportSynthesisSrfProgressTarget

      // start SrfSloIndicatorTarget
      if (newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId() != null
        && !newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId().trim().isEmpty()) {
        if (NumberUtils.isParsable(newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId().trim())) {
          srfSloIndicator = srfSloIndicatorManager
            .getSrfSloIndicatorById(Long.valueOf(newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId().trim()));

          if (srfSloIndicator == null) {
            fieldErrors.add(new FieldErrorDTO("createProgressTowards", "SrfSloIndicatorEntity",
              newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId() + " is an invalid Srf Slo Indicator code."));
          } else {
            srfSloIndicatorTarget = srfSloIndicator.getSrfSloIndicatorTargets().stream()
              .sorted((t1, t2) -> Integer.compare(t1.getYear(), t2.getYear()))
              .filter(t -> t.getYear() > Calendar.getInstance().get(Calendar.YEAR)).findFirst().orElse(null);

            // TODO write a better error message
            if (srfSloIndicatorTarget == null) {
              fieldErrors.add(new FieldErrorDTO("createProgressTowards", "SrfSloIndicatorTargetEntity",
                newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId()
                  + " is from a year that have not been activated."));
            }

          }
        } else {
          fieldErrors.add(new FieldErrorDTO("createProgressTowards", "SrfSloIndicatorEntity",
            "Srf Slo Indicator code is an invalid numeric id."));
        }

      } else {
        fieldErrors.add(new FieldErrorDTO("createProgressTowards", "SrfSloIndicatorTargetEntity",
          "Srf Slo Indicator code can not be null nor empty."));
      }
      // end SrfSloIndicatorTarget

      // start CrpProgram (flagshipProgram)
      if (newSrfProgressTowardsTargetDTO.getFlagshipProgramId() != null
        && !newSrfProgressTowardsTargetDTO.getFlagshipProgramId().trim().isEmpty()) {
        crpProgram = crpProgramManager.getCrpProgramBySmoCode(newSrfProgressTowardsTargetDTO.getFlagshipProgramId());
        if (crpProgram == null) {
          fieldErrors.add(new FieldErrorDTO("createProgressTowards", "FlagshipEntity",
            newSrfProgressTowardsTargetDTO.getFlagshipProgramId() + " is an invalid CRP Program SMO code."));
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("createProgressTowards", "FlagshipEntity",
          "CRP Program SMO code can not be null nor empty."));
      }
      // end CrpProgram (flagshipProgram)

      // start ReportSynthesis
      if (crpProgram != null) {
        liaisonInstitution =
          liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), globalUnitEntity.getId());
        reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      }
      // end ReportSynthesis

      // start ReportSynthesisSrfProgress
      if (reportSynthesis != null) {
        reportSynthesisSrfProgress = reportSynthesis.getReportSynthesisSrfProgress();
      }
      // end ReportSynthesisSrfProgress

      // start description
      if (newSrfProgressTowardsTargetDTO.getBriefSummary() != null
        && !newSrfProgressTowardsTargetDTO.getBriefSummary().trim().isEmpty()) {
        reportSynthesisSrfProgressTarget.setBirefSummary(newSrfProgressTowardsTargetDTO.getBriefSummary());
      } else {
        fieldErrors.add(new FieldErrorDTO("createProgressTowards", "Summary", "Please enter a brief summary"));
      }
      // end description

      // all validated! now it is supposed to be ok to save the entities
      if (fieldErrors.isEmpty()) {
        reportSynthesisSrfProgressTarget = new ReportSynthesisSrfProgressTarget();
        // creating new ReportSynthesis if it does not exist
        if (reportSynthesis == null) {
          reportSynthesis = new ReportSynthesis();
          liaisonInstitution =
            liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), globalUnitEntity.getId());
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

  public ResponseEntity<SrfProgressTowardsTargetDTO> deleteProgressTowardsById(Long id, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
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
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), CGIARentityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("deleteKeyExternalPartnership", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("deleteKeyExternalPartnership", "phase",
        repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    ReportSynthesisSrfProgressTarget srfProgressTarget =
      reportSynthesisSrfProgressTargetManager.getReportSynthesisSrfProgressTargetById(id);

    if (srfProgressTarget != null) {
      reportSynthesisSrfProgressTargetManager.deleteReportSynthesisSrfProgressTarget(srfProgressTarget.getId());
    } else {
      fieldErrors.add(new FieldErrorDTO("deleteProgressTowardsById", "ReportSynthesisSrfProgressTargetEntity",
        id + " is an invalid Report Synthesis Srf Progress Target Code"));
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(srfProgressTarget)
      .map(this.srfProgressTowardsTargetMapper::reportSynthesisSrfProgressTargetToSrfProgressTowardsTargetsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public List<SrfProgressTowardsTargetDTO> findAllProgressTowardsByGlobalUnit(String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {

    List<SrfProgressTowardsTargetDTO> progressTowardsTargets = new ArrayList<>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findProgressTowardsByGlobalUnit", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("findProgressTowardsByGlobalUnit", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("findProgressTowardsByGlobalUnit", "phase",
        repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    if (fieldErrors.isEmpty()) {
      // not all ReportSynthesis have a ReportSynthesisSrfProgress, so we need to filter out those to avoid exceptions
      progressTowardsTargets = reportSynthesisManager.findAll().stream()
        .filter(rs -> rs.getPhase().getId() == phase.getId() && rs.getReportSynthesisSrfProgress() != null)
        .flatMap(rs -> rs.getReportSynthesisSrfProgress().getReportSynthesisSrfProgressTargets().stream())
        .map(srfProgressTowardsTargetMapper::reportSynthesisSrfProgressTargetToSrfProgressTowardsTargetsDTO)
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
   * Find an KeyExternalPartnership by Id and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a KeyExternalPartnershipDTO with the keyExternalPartnership Item
   */

  public ResponseEntity<SrfProgressTowardsTargetDTO> findProgressTowardsById(Long id, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    // TODO: Include all security validations
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findProgressTowardsById", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("findProgressTowardsById", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    Phase phase =
      this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
        && c.getYear() == repoYear && c.getName().equalsIgnoreCase(repoPhase)).findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(
        new FieldErrorDTO("findProgressTowardsById", "phase", repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTarget =
      reportSynthesisSrfProgressTargetManager.getReportSynthesisSrfProgressTargetById(id);
    if (reportSynthesisSrfProgressTarget == null) {
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
                "The Report Synthesis Srf Progress Target with id " + id + " do not correspond to the phase entered"));
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
      .map(this.srfProgressTowardsTargetMapper::reportSynthesisSrfProgressTargetToSrfProgressTowardsTargetsDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  /**
   * Update a Key External Partnership by Id and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a InnovationDTO with the innovation Item
   */
  public Long putProgressTowardsById(Long idProgressTowards,
    NewSrfProgressTowardsTargetDTO newSrfProgressTowardsTargetDTO, String CGIARentityAcronym, User user) {
    Long idProgressTowardsDB = null;
    Phase phase = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(CGIARentityAcronym);
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
    if (!lstUser.stream().anyMatch(crp -> crp.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putProgressTowards", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newSrfProgressTowardsTargetDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("putProgressTowards", "PhaseEntity", "Phase must not be null"));
    } else {
      if (newSrfProgressTowardsTargetDTO.getPhase().getName() == null
        || newSrfProgressTowardsTargetDTO.getPhase().getName().trim().isEmpty()
        || newSrfProgressTowardsTargetDTO.getPhase().getYear() == null
        // DANGER! Magic number ahead
        || newSrfProgressTowardsTargetDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("putProgressTowards", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> p.getCrp().getAcronym().equalsIgnoreCase(CGIARentityAcronym)
            && p.getYear() == newSrfProgressTowardsTargetDTO.getPhase().getYear()
            && p.getName().equalsIgnoreCase(newSrfProgressTowardsTargetDTO.getPhase().getName()))
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors
            .add(new FieldErrorDTO("putProgressTowards", "phase", newSrfProgressTowardsTargetDTO.getPhase().getName()
              + ' ' + newSrfProgressTowardsTargetDTO.getPhase().getYear() + " is an invalid phase"));
        }

      }

    }

    CrpProgram crpProgram = null;
    if (newSrfProgressTowardsTargetDTO.getFlagshipProgramId() != null) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(newSrfProgressTowardsTargetDTO.getFlagshipProgramId());
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("putProgressTowards", "CrpProgramEntity",
          newSrfProgressTowardsTargetDTO.getFlagshipProgramId() + " is an invalid CRP Program SMO Code"));
      }

    } else {
      fieldErrors.add(new FieldErrorDTO("putProgressTowards", "CrpProgramEntity",
        newSrfProgressTowardsTargetDTO.getFlagshipProgramId() + " is an invalid CRP Program SMO Code"));
    }

    ReportSynthesisSrfProgressTarget reportSynthesisSrfProgressTarget =
      reportSynthesisSrfProgressTargetManager.getReportSynthesisSrfProgressTargetById(idProgressTowards);
    if (reportSynthesisSrfProgressTarget == null) {
      fieldErrors.add(new FieldErrorDTO("putProgressTowards", "ReportSynthesisSrfProgressTargetEntity",
        +idProgressTowards + " is an invalid Report Synthesis Srf Progress Target Code"));
    }

    if (fieldErrors.isEmpty()) {
      idProgressTowardsDB = reportSynthesisSrfProgressTarget.getId();
      SrfSloIndicatorTarget srfSloIndicatorTarget = null;
      SrfSloIndicator srfSloIndicator = null;

      reportSynthesisSrfProgressTarget
        .setAdditionalContribution(newSrfProgressTowardsTargetDTO.getAdditionalContribution());

      if (newSrfProgressTowardsTargetDTO.getBriefSummary() != null
        && !newSrfProgressTowardsTargetDTO.getBriefSummary().trim().isEmpty()) {
        reportSynthesisSrfProgressTarget.setBirefSummary(newSrfProgressTowardsTargetDTO.getBriefSummary());
      } else {
        fieldErrors.add(new FieldErrorDTO("putProgressTowards", "Summary", "Please enter a brief summary"));
      }

      if (newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId() != null
        && !newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId().trim().isEmpty()) {
        String sloIndicatorId = newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId().trim();
        if (NumberUtils.isParsable(sloIndicatorId)) {
          srfSloIndicator = srfSloIndicatorManager.getSrfSloIndicatorById(Long.valueOf(sloIndicatorId));
          if (srfSloIndicator == null) {
            fieldErrors.add(new FieldErrorDTO("putProgressTowards", "SrfSloIndicatorEntity",
              newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId() + " is an invalid Srf Slo Indicator code."));
          } else {
            srfSloIndicatorTarget = srfSloIndicator.getSrfSloIndicatorTargets().stream()
              .sorted((t1, t2) -> Integer.compare(t1.getYear(), t2.getYear()))
              .filter(t -> t.getYear() > Calendar.getInstance().get(Calendar.YEAR)).findFirst().orElse(null);

            // TODO write a better error message
            if (srfSloIndicatorTarget == null) {
              fieldErrors.add(new FieldErrorDTO("putProgressTowards", "SrfSloIndicatorTargetEntity",
                newSrfProgressTowardsTargetDTO.getSrfSloIndicatorId()
                  + " is from a year that have not been activated."));
            }

          }
        } else {
          fieldErrors.add(new FieldErrorDTO("createProgressTowards", "SrfSloIndicatorEntity",
            "Srf Slo Indicator code is an invalid numeric id."));
        }

      } else {
        fieldErrors.add(new FieldErrorDTO("putProgressTowards", "SrfSloIndicatorEntity",
          "Srf Slo Indicator code can not be null nor empty."));
      }

      reportSynthesisSrfProgressTarget.setSrfSloIndicatorTarget(srfSloIndicatorTarget);

      LiaisonInstitution liaisonInstitution =
        liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("putProgressTowards", "LiaisonInstitutionEntity",
          "A Liaison Institution with the acronym " + crpProgram.getAcronym() + " could not be found"));
      }

      // possible NullPointerException if for some reason a LiaisonInstitution have not been created for the CrpProgram
      ReportSynthesis reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      if (reportSynthesis == null) {
        fieldErrors.add(new FieldErrorDTO("putProgressTowards", "ReportSynthesisEntity",
          "A report entity linked to the Phase with id " + phase.getId() + " and Liaison Institution with id "
            + liaisonInstitution.getId() + " could not be found"));
      }

      ReportSynthesisSrfProgress reportSynthesisSrfProgress = reportSynthesis.getReportSynthesisSrfProgress();
      reportSynthesisSrfProgressTarget.setReportSynthesisSrfProgress(reportSynthesisSrfProgress);
    }

    if (!fieldErrors.isEmpty()) {
      // fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    reportSynthesisSrfProgressTargetManager.saveReportSynthesisSrfProgressTarget(reportSynthesisSrfProgressTarget);
    return idProgressTowardsDB;
  }

}
