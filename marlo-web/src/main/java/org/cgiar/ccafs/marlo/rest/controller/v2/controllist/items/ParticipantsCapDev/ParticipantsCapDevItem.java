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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.ParticipantsCapDev;

import org.cgiar.ccafs.marlo.config.APConstants;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisCrossCuttingDimensionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisCrossCuttingDimension;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.NewParticipantsCapDevDTO;
import org.cgiar.ccafs.marlo.rest.dto.ParticipantsCapDevDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.ReportSynthesisCrossCuttingDimensionMapper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Named
public class ParticipantsCapDevItem<T> {

  // Managers and mappers
  private GlobalUnitManager globalUnitManager;
  private PhaseManager phaseManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisCrossCuttingDimensionManager reportSynthesisCrossCuttingDimensionManager;
  private ReportSynthesisCrossCuttingDimensionMapper reportSynthesisCrossCuttingDimensionMapper;

  // Variables
  // private List<FieldErrorDTO> fieldErrors;
  // private ParticipantsCapDev participantsCapDev;
  // private long innovationID;

  @Inject
  public ParticipantsCapDevItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisCrossCuttingDimensionManager reportSynthesisCrossCuttingDimensionManager,
    ReportSynthesisCrossCuttingDimensionMapper reportSynthesisCrossCuttingDimensionMapper) {
    this.globalUnitManager = globalUnitManager;
    this.phaseManager = phaseManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisCrossCuttingDimensionManager = reportSynthesisCrossCuttingDimensionManager;
    this.reportSynthesisCrossCuttingDimensionMapper = reportSynthesisCrossCuttingDimensionMapper;
  }

  /**
   * Create a new ParticipantsCapDev
   * 
   * @param newParticipantsCapDevDTO all Participants CapDev data
   * @param CGIAR entity acronym who is requesting
   * @param year of reporting
   * @param Logged user on system
   * @return innovation id created
   */
  public Long createParticipantsCapDev(NewParticipantsCapDevDTO newParticipantsCapDevDTO, String entityAcronym,
    User user) {

    // TODO: Add the save to history
    // TODO: Include all data validations
    // TODO: return an ParticipantsCapDevDTO
    Long reportSynCrossCutDimId = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(entityAcronym);

    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createParticipantsCapDev", "GlobalUnitEntity",
        entityAcronym + " is an invalid CGIAR entity acronym"));
    }

    Phase phase = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(entityAcronym)
        && c.getYear() == newParticipantsCapDevDTO.getPhase().getYear()
        && c.getName().equalsIgnoreCase(newParticipantsCapDevDTO.getPhase().getName()))
      .findFirst().orElse(null);

    if (this.valuesMinorsToZero(newParticipantsCapDevDTO)) {
      fieldErrors.add(new FieldErrorDTO("createParticipantsCapDev", "ParticipantsCapDevDTO",
        "One or more of the values are minors to zero"));
    } else {

      if (phase == null) {
        fieldErrors
          .add(new FieldErrorDTO("createParticipantsCapDev", "phase", newParticipantsCapDevDTO.getPhase().getYear()
            + " / " + newParticipantsCapDevDTO.getPhase().getName() + " is an invalid year or name phase"));
      }
      if (phase != null && !phase.getEditable()) {
        fieldErrors
          .add(new FieldErrorDTO("createParticipantsCapDev", "phase", newParticipantsCapDevDTO.getPhase().getYear()
            + " / " + newParticipantsCapDevDTO.getPhase().getName() + " is a closed phase"));
      }

      if (fieldErrors.isEmpty()) {
        LiaisonInstitution liaisonInstitution =
          this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());

        if (liaisonInstitution == null) {
          fieldErrors
            .add(new FieldErrorDTO("createParticipantsCapDev", "LiaisonInstitution", "invalid liaison institution"));
        } else {

          ReportSynthesis reportSynthesis =
            reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());

          ReportSynthesisCrossCuttingDimension reportSynthesisCrossCuttingDimension = null;

          if (reportSynthesis == null) {
            reportSynthesis = new ReportSynthesis();
            reportSynthesis.setPhase(phase);
            reportSynthesis.setLiaisonInstitution(liaisonInstitution);
            reportSynthesis = this.reportSynthesisManager.saveReportSynthesis(reportSynthesis);
          } else {
            reportSynthesisCrossCuttingDimension = reportSynthesisCrossCuttingDimensionManager
              .getReportSynthesisCrossCuttingDimensionById(reportSynthesis.getId());
          }

          if (reportSynthesisCrossCuttingDimension == null && reportSynthesis != null) {
            reportSynthesisCrossCuttingDimension = new ReportSynthesisCrossCuttingDimension();
            reportSynthesisCrossCuttingDimension
              .setTraineesLongTermFemale(new Double(newParticipantsCapDevDTO.getTraineesLongTermFemale()));
            reportSynthesisCrossCuttingDimension
              .setTraineesLongTermMale(new Double(newParticipantsCapDevDTO.getTraineesLongTermMale()));
            reportSynthesisCrossCuttingDimension
              .setTraineesShortTermFemale(new Double(newParticipantsCapDevDTO.getTraineesShortTermFemale()));
            reportSynthesisCrossCuttingDimension
              .setTraineesShortTermMale(new Double(newParticipantsCapDevDTO.getTraineesShortTermMale()));
            reportSynthesisCrossCuttingDimension
              .setPhdFemale(new Double(newParticipantsCapDevDTO.getTraineesPhdFemale()));
            reportSynthesisCrossCuttingDimension.setPhdMale(new Double(newParticipantsCapDevDTO.getTraineesPhdMale()));
            reportSynthesisCrossCuttingDimension.setEvidenceLink(newParticipantsCapDevDTO.getEvidencelink());
            reportSynthesisCrossCuttingDimension.setReportSynthesis(reportSynthesis);
            reportSynthesisCrossCuttingDimension.setIsQAIncluded(new Boolean(false));

            reportSynthesisCrossCuttingDimension = this.reportSynthesisCrossCuttingDimensionManager
              .saveReportSynthesisCrossCuttingDimension(reportSynthesisCrossCuttingDimension);

            reportSynCrossCutDimId = reportSynthesisCrossCuttingDimension.getId();
          } else {

            if (reportSynthesisCrossCuttingDimension.getTraineesLongTermFemale() == null
              && reportSynthesisCrossCuttingDimension.getTraineesLongTermMale() == null
              && reportSynthesisCrossCuttingDimension.getTraineesShortTermFemale() == null
              && reportSynthesisCrossCuttingDimension.getTraineesShortTermMale() == null
              && reportSynthesisCrossCuttingDimension.getPhdFemale() == null
              && reportSynthesisCrossCuttingDimension.getPhdMale() == null) {

              reportSynthesisCrossCuttingDimension
                .setTraineesLongTermFemale(new Double(newParticipantsCapDevDTO.getTraineesLongTermFemale()));
              reportSynthesisCrossCuttingDimension
                .setTraineesLongTermMale(new Double(newParticipantsCapDevDTO.getTraineesLongTermMale()));
              reportSynthesisCrossCuttingDimension
                .setTraineesShortTermFemale(new Double(newParticipantsCapDevDTO.getTraineesShortTermFemale()));
              reportSynthesisCrossCuttingDimension
                .setTraineesShortTermMale(new Double(newParticipantsCapDevDTO.getTraineesShortTermMale()));
              reportSynthesisCrossCuttingDimension
                .setPhdFemale(new Double(newParticipantsCapDevDTO.getTraineesPhdFemale()));
              reportSynthesisCrossCuttingDimension
                .setPhdMale(new Double(newParticipantsCapDevDTO.getTraineesPhdMale()));
              reportSynthesisCrossCuttingDimension.setEvidenceLink(newParticipantsCapDevDTO.getEvidencelink());
              reportSynthesisCrossCuttingDimension.setReportSynthesis(reportSynthesis);

              reportSynthesisCrossCuttingDimension = this.reportSynthesisCrossCuttingDimensionManager
                .saveReportSynthesisCrossCuttingDimension(reportSynthesisCrossCuttingDimension);

              reportSynCrossCutDimId = reportSynthesisCrossCuttingDimension.getId();

            } else {
              fieldErrors.add(new FieldErrorDTO("createParticipantsCapDev", "ReportSynthesisCrossCuttingDimension",
                "Report Synthesis Cross Cutting Dimension already exits ID "
                  + reportSynthesisCrossCuttingDimension.getId()));
            }
          }
        }
      }
    }
    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return reportSynCrossCutDimId;
  }

  /**
   * Delete an ParticipantsCapDev by Id, phase and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a ParticipantsCapDevDTO with the ParticipantsCapDev Item
   */
  public Long deleteParticipantsCapDevById(Long id, String cGIAREntity, Integer year, String strphase,
    User currentUser) {
    Long reportSynCrossCutDimId = null;
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(cGIAREntity);

    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteParticipantsCapDev", "GlobalUnitEntity",
        cGIAREntity + " is an invalid CGIAR entity acronym"));
    }

    Phase phase = this.phaseManager.findAll().stream().filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(cGIAREntity)
      && c.getYear() == year && c.getName().equalsIgnoreCase(strphase)).findFirst().orElse(null);


    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("deleteParticipantsCapDev", "phase",
        year + " / " + strphase + " is an invalid year or name phase"));
    } else {

      LiaisonInstitution liaisonInstitution =
        this.liaisonInstitutionManager.findByAcronymAndCrp(APConstants.CLARISA_ACRONYM_PMU, globalUnitEntity.getId());

      if (liaisonInstitution == null) {
        fieldErrors
          .add(new FieldErrorDTO("deleteParticipantsCapDev", "LiaisonInstitution", "invalid liaison institution"));
      } else {

        ReportSynthesis reportSynthesis =
          reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());

        ReportSynthesisCrossCuttingDimension reportSynthesisCrossCuttingDimension = null;

        if (reportSynthesis == null) {
          fieldErrors
            .add(new FieldErrorDTO("deleteParticipantsCapDev", "Report Synthesis", "invalid liaison institution"));
        } else {
          reportSynthesisCrossCuttingDimension =
            reportSynthesisCrossCuttingDimensionManager.getReportSynthesisCrossCuttingDimensionById(id);
        }

        if (fieldErrors.isEmpty() && reportSynthesisCrossCuttingDimension != null && reportSynthesis != null) {
          reportSynthesisCrossCuttingDimension.setTraineesLongTermFemale(null);
          reportSynthesisCrossCuttingDimension.setTraineesLongTermMale(null);
          reportSynthesisCrossCuttingDimension.setTraineesShortTermFemale(null);
          reportSynthesisCrossCuttingDimension.setTraineesShortTermMale(null);
          reportSynthesisCrossCuttingDimension.setPhdFemale(null);
          reportSynthesisCrossCuttingDimension.setPhdMale(null);
          reportSynthesisCrossCuttingDimension.setEvidenceLink(null);
          reportSynthesisCrossCuttingDimension.setIsQAIncluded(null);

          reportSynthesisCrossCuttingDimension = this.reportSynthesisCrossCuttingDimensionManager
            .saveReportSynthesisCrossCuttingDimension(reportSynthesisCrossCuttingDimension);

          reportSynCrossCutDimId = reportSynthesisCrossCuttingDimension.getId();
        } else {
          fieldErrors.add(new FieldErrorDTO("deleteParticipantsCapDev", "ReportSynthesisCrossCuttingDimension",
            "Report Synthesis Cross Cutting Dimension does not exits with ID " + id));
        }
      }
    }

    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return reportSynCrossCutDimId;
  }

  /**
   * Find an ParticipantsCapDev by Id, phase and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a ParticipantsCapDevDTO with the ParticipantsCapDev Item
   */
  public ResponseEntity<ParticipantsCapDevDTO> findParticipantsCapDevById(Long id, String cGIAREntity, Integer year,
    String phase, User currentUser) {

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(cGIAREntity);

    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findParticipantsCapDevById", "GlobalUnitEntity",
        cGIAREntity + " is an invalid CGIAR entity acronym"));
    }
    Phase phasedata = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), cGIAREntity)
        && p.getYear() >= APConstants.CLARISA_AVALIABLE_INFO_YEAR && p.getYear() == year
        && StringUtils.equalsIgnoreCase(p.getName(), phase))
      .findFirst().orElse(null);
    if (phasedata == null) {
      fieldErrors.add(new FieldErrorDTO("findDeliverableById", "phase", phase + ' ' + year + " is an invalid phase"));
    }
    ReportSynthesisCrossCuttingDimension reportSynthesisCrossCuttingDimension =
      reportSynthesisCrossCuttingDimensionManager.getReportSynthesisCrossCuttingDimensionById(id);
    if (fieldErrors.isEmpty()) {
      if (reportSynthesisCrossCuttingDimension == null) {
        fieldErrors.add(new FieldErrorDTO("findParticipantsCapDevById", "ParticipantsCapDev",
          id + " is an invalid Report Synthesis Cross Cutting Dimension code"));
      } else {
        if (reportSynthesisCrossCuttingDimension.getReportSynthesis() == null) {
          fieldErrors.add(new FieldErrorDTO("findParticipantsCapDevById", "ParticipantsCapDev",
            id + " not exists code in Report Synthesis"));
        } else {
          if (!(reportSynthesisCrossCuttingDimension.getReportSynthesis().getPhase().getName().equalsIgnoreCase(phase)
            && reportSynthesisCrossCuttingDimension.getReportSynthesis().getPhase().getYear() == year)) {
            fieldErrors.add(new FieldErrorDTO("findParticipantsCapDevById", "ParticipantsCapDev",
              year + " / " + phase + " is an invalid year or name phase"));
          }
        }
      }
    }


    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(reportSynthesisCrossCuttingDimension)
      .map(this.reportSynthesisCrossCuttingDimensionMapper::reportSynthesisCrossCuttingDimensionToParticipantsCapDevDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

  }

  /**
   * Update an ParticipantsCapDev by Id, newParticipantsCapDevDTO
   * 
   * @param id
   * @param newParticipantsCapDevDTO
   * @return a ParticipantsCapDevDTO with the ParticipantsCapDev Item
   */
  public Long putParticipantsCapDevById(Long id, NewParticipantsCapDevDTO newParticipantsCapDevDTO, String cGIAREntity,
    User currentUser) {
    return this.saveParticipantsCapDev(id, cGIAREntity, newParticipantsCapDevDTO).getBody().getId();
  }

  protected ResponseEntity<ParticipantsCapDevDTO> saveParticipantsCapDev(Long id, String cGIAREntity,
    NewParticipantsCapDevDTO newParticipantsCapDevDTO) {

    Integer year = newParticipantsCapDevDTO.getPhase().getYear();
    String phase = newParticipantsCapDevDTO.getPhase().getName();

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(cGIAREntity);

    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("saveParticipantsCapDev", "GlobalUnitEntity",
        cGIAREntity + " is an invalid CGIAR entity acronym"));
    }

    Phase phaseObj = this.phaseManager.findAll().stream()
      .filter(c -> c.getCrp().getAcronym().equalsIgnoreCase(cGIAREntity)
        && c.getYear() == newParticipantsCapDevDTO.getPhase().getYear()
        && c.getName().equalsIgnoreCase(newParticipantsCapDevDTO.getPhase().getName()))
      .findFirst().orElse(null);
    if (phaseObj != null && !phaseObj.getEditable()) {
      fieldErrors.add(new FieldErrorDTO("saveParticipantsCapDev", "phase", newParticipantsCapDevDTO.getPhase().getYear()
        + " / " + newParticipantsCapDevDTO.getPhase().getName() + " is a closed phase"));
    }

    ReportSynthesisCrossCuttingDimension reportSynthesisCrossCuttingDimension = null;

    if (this.valuesMinorsToZero(newParticipantsCapDevDTO)) {
      fieldErrors.add(new FieldErrorDTO("saveParticipantsCapDev", "ParticipantsCapDevDTO",
        "One or more of the values are minors to zero"));
    } else {

      reportSynthesisCrossCuttingDimension =
        reportSynthesisCrossCuttingDimensionManager.getReportSynthesisCrossCuttingDimensionById(id);

      if (reportSynthesisCrossCuttingDimension == null) {
        fieldErrors.add(new FieldErrorDTO("saveParticipantsCapDev", "ParticipantsCapDev",
          id + " is an invalid Report Synthesis Cross Cutting Dimension code"));
      } else {
        if (reportSynthesisCrossCuttingDimension.getReportSynthesis() == null) {
          fieldErrors.add(new FieldErrorDTO("saveParticipantsCapDev", "ParticipantsCapDev",
            id + " not exists code in Report Synthesis"));
        } else {
          if (fieldErrors.isEmpty()
            && reportSynthesisCrossCuttingDimension.getReportSynthesis().getPhase().getName().equalsIgnoreCase(phase)
            && reportSynthesisCrossCuttingDimension.getReportSynthesis().getPhase().getYear() == year) {

            reportSynthesisCrossCuttingDimension
              .setTraineesShortTermFemale(newParticipantsCapDevDTO.getTraineesShortTermFemale() != null
                ? newParticipantsCapDevDTO.getTraineesShortTermFemale().doubleValue() : null);
            reportSynthesisCrossCuttingDimension
              .setTraineesShortTermMale(newParticipantsCapDevDTO.getTraineesShortTermMale() != null
                ? newParticipantsCapDevDTO.getTraineesShortTermMale().doubleValue() : null);
            reportSynthesisCrossCuttingDimension
              .setTraineesLongTermFemale(newParticipantsCapDevDTO.getTraineesLongTermFemale() != null
                ? newParticipantsCapDevDTO.getTraineesLongTermFemale().doubleValue() : null);
            reportSynthesisCrossCuttingDimension
              .setTraineesLongTermMale(newParticipantsCapDevDTO.getTraineesLongTermMale() != null
                ? newParticipantsCapDevDTO.getTraineesLongTermMale().doubleValue() : null);
            reportSynthesisCrossCuttingDimension.setPhdFemale(newParticipantsCapDevDTO.getTraineesPhdFemale() != null
              ? newParticipantsCapDevDTO.getTraineesPhdFemale().doubleValue() : null);
            reportSynthesisCrossCuttingDimension.setPhdMale(newParticipantsCapDevDTO.getTraineesPhdMale() != null
              ? newParticipantsCapDevDTO.getTraineesPhdMale().doubleValue() : null);
            reportSynthesisCrossCuttingDimension.setEvidenceLink(
              newParticipantsCapDevDTO.getEvidencelink() != null ? newParticipantsCapDevDTO.getEvidencelink() : null);
            reportSynthesisCrossCuttingDimension.setIsQAIncluded(new Boolean(false));

            this.reportSynthesisCrossCuttingDimensionManager
              .saveReportSynthesisCrossCuttingDimension(reportSynthesisCrossCuttingDimension);
          } else {
            fieldErrors.add(new FieldErrorDTO("saveParticipantsCapDev", "ParticipantsCapDev",
              year + " / " + phase + " is an invalid year or name phase"));
          }
        }
      }
    }
    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(reportSynthesisCrossCuttingDimension)
      .map(this.reportSynthesisCrossCuttingDimensionMapper::reportSynthesisCrossCuttingDimensionToParticipantsCapDevDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

  }

  private Boolean valuesMinorsToZero(NewParticipantsCapDevDTO newParticipantsCapDevDTO) {

    if (newParticipantsCapDevDTO.getTraineesLongTermFemale() < 0
      || newParticipantsCapDevDTO.getTraineesLongTermMale() < 0
      || newParticipantsCapDevDTO.getTraineesShortTermFemale() < 0
      || newParticipantsCapDevDTO.getTraineesShortTermMale() < 0 || newParticipantsCapDevDTO.getTraineesPhdFemale() < 0
      || newParticipantsCapDevDTO.getTraineesPhdMale() < 0) {
      return true;
    }
    return false;
  }

}
