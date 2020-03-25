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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.crossCgiarCollabs;

import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipCollaborationCrpManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipCollaborationManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.MarloBaseEntity;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaboration;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipCollaborationCrp;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.CrossCGIARCollaborationDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewCrossCGIARCollaborationDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.KeyPartnershipCollaborationMapper;
import org.cgiar.ccafs.marlo.utils.ChangeTracker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**************
 * @author German C. Martinez - CIAT/CCAFS
 **************/

@Named
public class CrossCGIARCollaborationsItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(CrossCGIARCollaborationsItem.class);

  private GlobalUnitManager globalUnitManager;
  private PhaseManager phaseManager;
  private CrpProgramManager crpProgramManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisKeyPartnershipManager reportSynthesisKeyPartnershipManager;
  private ReportSynthesisKeyPartnershipCollaborationManager reportSynthesisKeyPartnershipCollaborationManager;
  private ReportSynthesisKeyPartnershipCollaborationCrpManager reportSynthesisKeyPartnershipCollaborationCrpManager;

  private KeyPartnershipCollaborationMapper keyPartnershipCollaborationMapper;

  @Inject
  public CrossCGIARCollaborationsItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, LiaisonInstitutionManager liaisonInstitutionManager,
    ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisKeyPartnershipManager reportSynthesisKeyPartnershipManager,
    ReportSynthesisKeyPartnershipCollaborationManager reportSynthesisKeyPartnershipCollaborationManager,
    ReportSynthesisKeyPartnershipCollaborationCrpManager reportSynthesisKeyPartnershipCollaborationCrpManager,
    KeyPartnershipCollaborationMapper keyPartnershipCollaborationMapper) {
    super();
    this.globalUnitManager = globalUnitManager;
    this.phaseManager = phaseManager;
    this.crpProgramManager = crpProgramManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisKeyPartnershipManager = reportSynthesisKeyPartnershipManager;
    this.reportSynthesisKeyPartnershipCollaborationManager = reportSynthesisKeyPartnershipCollaborationManager;
    this.reportSynthesisKeyPartnershipCollaborationCrpManager = reportSynthesisKeyPartnershipCollaborationCrpManager;
    this.keyPartnershipCollaborationMapper = keyPartnershipCollaborationMapper;
  }

  public Long createCrossCGIARCollaboration(NewCrossCGIARCollaborationDTO newCrossCGIARCollaborationDTO,
    String CGIARentityAcronym, User user) {
    Long crossCGIARCollaborationID = null;
    CrpProgram crpProgram = null;
    Phase phase = null;
    ReportSynthesisKeyPartnershipCollaboration keyPartnershipCollaboration =
      new ReportSynthesisKeyPartnershipCollaboration();
    ReportSynthesisKeyPartnershipCollaborationCrp keyPartnershipCollaborationCrp = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("createCrossCGIARCollaboration", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newCrossCGIARCollaborationDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "PhaseEntity", "Phase must not be null"));
    } else {
      String strippedPhaseName = StringUtils.stripToNull(newCrossCGIARCollaborationDTO.getPhase().getName());
      if (strippedPhaseName == null || newCrossCGIARCollaborationDTO.getPhase().getYear() == null
      // DANGER! Magic number ahead
        || newCrossCGIARCollaborationDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
            && p.getYear() == newCrossCGIARCollaborationDTO.getPhase().getYear()
            && StringUtils.equalsIgnoreCase(p.getName(), strippedPhaseName) && p.isActive())
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "phase",
            newCrossCGIARCollaborationDTO.getPhase().getName() + ' '
              + newCrossCGIARCollaborationDTO.getPhase().getYear() + " is an invalid phase"));
        }
      }
    }

    strippedId = StringUtils.stripToNull(newCrossCGIARCollaborationDTO.getFlagshipProgramId());
    if (strippedId != null) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(strippedId);
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "FlagshipEntity",
          newCrossCGIARCollaborationDTO.getFlagshipProgramId() + " is an invalid CRP Program SMO code."));
      } else {
        if (!StringUtils.equalsIgnoreCase(crpProgram.getCrp().getAcronym(), strippedEntityAcronym)) {
          fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "FlagshipEntity",
            "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym "
              + CGIARentityAcronym));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "FlagshipEntity",
        "CRP Program SMO code can not be null nor empty."));
    }

    if (fieldErrors.isEmpty()) {
      Set<GlobalUnit> collaborationCrps = new HashSet<>();
      ReportSynthesis reportSynthesis = null;
      ReportSynthesisKeyPartnership reportSynthesisKeyPartnership = null;
      LiaisonInstitution liaisonInstitution = null;

      // start GlobalUnit
      if (newCrossCGIARCollaborationDTO.getCollaborationCrpIds() != null
        && !newCrossCGIARCollaborationDTO.getCollaborationCrpIds().isEmpty()) {
        collaborationCrps = newCrossCGIARCollaborationDTO.getCollaborationCrpIds().stream()
          .map(StringUtils::stripToNull).map(new Function<String, GlobalUnit>() {

            @Override
            public GlobalUnit apply(String globalUnitId) {
              GlobalUnit globalUnit = null;
              if (globalUnitId != null) {
                globalUnit = globalUnitManager.findGlobalUnitBySMOCode(globalUnitId);
                if (globalUnit == null) {
                  fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "GlobalUnitEntity",
                    globalUnitId + " is an invalid Global Unit SMO code."));
                } else {
                  if (!globalUnit.isActive()) {
                    fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "GlobalUnitEntity",
                      "The Global Unit with SMO code " + globalUnitId + " is not active."));
                  }
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "GlobalUnitEntity",
                  "A Global Unit SMO code can not be null nor empty."));
              }

              return globalUnit;
            }
          }).collect(Collectors.toSet());
      } else {
        fieldErrors.add(
          new FieldErrorDTO("createCrossCGIARCollaboration", "GlobalUnitList", "Please enter the collaborating crps."));
      }
      // end GlobalUnit

      // start ReportSynthesis
      if (crpProgram != null) {
        liaisonInstitution =
          liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), globalUnitEntity.getId());
        reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
      }
      // end ReportSynthesis

      // start ReportSynthesisKeyPartnership
      if (reportSynthesis != null) {
        reportSynthesisKeyPartnership = reportSynthesis.getReportSynthesisKeyPartnership();
      }
      // end ReportSynthesisKeyPartnership

      // start description
      String strippedDescription = StringUtils.stripToNull(newCrossCGIARCollaborationDTO.getDescription());
      if (strippedDescription == null) {
        fieldErrors
          .add(new FieldErrorDTO("createCrossCGIARCollaboration", "Description", "Please enter a description."));
      }
      // end description

      // all validated! now it is supposed to be ok to save the entities
      if (fieldErrors.isEmpty()) {
        // creating new ReportSynthesis if it does not exist
        if (reportSynthesis == null) {
          reportSynthesis = new ReportSynthesis();
          liaisonInstitution =
            liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), globalUnitEntity.getId());
          reportSynthesis.setLiaisonInstitution(liaisonInstitution);
          reportSynthesis.setPhase(phase);
          reportSynthesisManager.saveReportSynthesis(reportSynthesis);
        }

        // creating new ReportSynthesisKeyPartnership if it does not exist
        if (reportSynthesisKeyPartnership == null) {
          reportSynthesisKeyPartnership = new ReportSynthesisKeyPartnership();
          reportSynthesisKeyPartnership.setReportSynthesis(reportSynthesis);
          reportSynthesisKeyPartnershipManager.saveReportSynthesisKeyPartnership(reportSynthesisKeyPartnership);
        }

        keyPartnershipCollaboration.setDescription(newCrossCGIARCollaborationDTO.getDescription());
        keyPartnershipCollaboration.setReportSynthesisKeyPartnership(reportSynthesisKeyPartnership);
        keyPartnershipCollaboration.setValueAdded(newCrossCGIARCollaborationDTO.getValueAdded());
        ReportSynthesisKeyPartnershipCollaboration keyPartnershipCollaborationDB =
          reportSynthesisKeyPartnershipCollaborationManager
            .saveReportSynthesisKeyPartnershipCollaboration(keyPartnershipCollaboration);
        if (keyPartnershipCollaborationDB != null) {
          crossCGIARCollaborationID = keyPartnershipCollaborationDB.getId();
          // start GlobalUnit
          for (GlobalUnit collaborationCrp : collaborationCrps) {
            keyPartnershipCollaborationCrp = new ReportSynthesisKeyPartnershipCollaborationCrp();
            keyPartnershipCollaborationCrp.setGlobalUnit(collaborationCrp);
            keyPartnershipCollaborationCrp.setReportSynthesisKeyPartnershipCollaboration(keyPartnershipCollaborationDB);
            reportSynthesisKeyPartnershipCollaborationCrpManager
              .saveReportSynthesisKeyPartnershipCollaborationCrp(keyPartnershipCollaborationCrp).getId();
          }
          // end GlobalUnit
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

    return crossCGIARCollaborationID;
  }

  public ResponseEntity<CrossCGIARCollaborationDTO> deleteCrossCGIARCollaborationById(Long id,
    String CGIARentityAcronym, Integer repoYear, String repoPhase, User user) {
    ReportSynthesisKeyPartnershipCollaboration keyPartnershipCollaboration = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteCrossCGIARCollaboration", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteCrossCGIARCollaboration", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("deleteCrossCGIARCollaboration", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    String strippedRepoPhase = StringUtils.stripToNull(repoPhase);
    Phase phase = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
        && p.getYear() == repoYear && StringUtils.equalsIgnoreCase(p.getName(), strippedRepoPhase) && p.isActive())
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("deleteCrossCGIARCollaboration", "phase",
        repoPhase + ' ' + repoYear + " is an invalid phase"));
    } else {
      if (!StringUtils.equalsIgnoreCase(phase.getCrp().getAcronym(), strippedEntityAcronym)) {
        fieldErrors.add(new FieldErrorDTO("deleteCrossCGIARCollaboration", "FlagshipEntity",
          "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym " + CGIARentityAcronym));
      }
    }

    if (fieldErrors.isEmpty()) {
      keyPartnershipCollaboration =
        reportSynthesisKeyPartnershipCollaborationManager.getReportSynthesisKeyPartnershipCollaborationById(id);

      if (keyPartnershipCollaboration != null) {
        if (keyPartnershipCollaboration.getReportSynthesisKeyPartnership() == null) {
          fieldErrors.add(new FieldErrorDTO("findCrossCGIARCollaboration", "ReportSynthesisKeyPartnershipEntity",
            "There is no Report Synthesis Key Partnership assosiated to this entity!"));
        } else {
          if (keyPartnershipCollaboration.getReportSynthesisKeyPartnership().getReportSynthesis() == null) {
            fieldErrors.add(new FieldErrorDTO("findCrossCGIARCollaboration", "ReportSynthesisEntity",
              "There is no Report Synthesis assosiated to this entity!"));
          } else {
            if (keyPartnershipCollaboration.getReportSynthesisKeyPartnership().getReportSynthesis()
              .getPhase() == null) {
              fieldErrors.add(new FieldErrorDTO("findCrossCGIARCollaboration", "PhaseEntity",
                "There is no Phase assosiated to this entity!"));
            } else {
              if (keyPartnershipCollaboration.getReportSynthesisKeyPartnership().getReportSynthesis().getPhase()
                .getId() != phase.getId()) {
                fieldErrors.add(
                  new FieldErrorDTO("findCrossCGIARCollaboration", "ReportSynthesisKeyPartnershipCollaborationEntity",
                    "The Report Synthesis Key Partnership Collaboration with id " + id
                      + " do not correspond to the phase entered"));
              } else {
                Long keyParnershipCollaborationId = keyPartnershipCollaboration.getId();
                keyPartnershipCollaboration.setReportSynthesisKeyPartnershipCollaborationCrps(
                  reportSynthesisKeyPartnershipCollaborationCrpManager.findAll().stream()
                    .filter(c -> c.isActive() && c.getReportSynthesisKeyPartnershipCollaboration()
                      .getReportSynthesisKeyPartnership().getReportSynthesis().getPhase().getId().equals(phase.getId()))
                    .collect(Collectors.toSet()));

                keyPartnershipCollaboration.getReportSynthesisKeyPartnershipCollaborationCrps().stream()
                  .filter(
                    c -> c.getReportSynthesisKeyPartnershipCollaboration().getId() == keyParnershipCollaborationId)
                  .map(MarloBaseEntity::getId).forEach(
                    reportSynthesisKeyPartnershipCollaborationCrpManager::deleteReportSynthesisKeyPartnershipCollaborationCrp);

                reportSynthesisKeyPartnershipCollaborationManager
                  .deleteReportSynthesisKeyPartnershipCollaboration(keyPartnershipCollaboration.getId());
              }
            }
          }
        }
      } else {
        fieldErrors
          .add(new FieldErrorDTO("deleteCrossCGIARCollaboration", "ReportSynthesisKeyPartnershipCollaborationEntity",
            id + " is an invalid Report Synthesis Key Partnership Collaboration Code"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(keyPartnershipCollaboration)
      .map(this.keyPartnershipCollaborationMapper::keyPartnershipCollaborationToCrossCGIARCollaboration)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public List<CrossCGIARCollaborationDTO> findAllCrossCGIARCollaborationsByGlobalUnit(String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {

    List<CrossCGIARCollaborationDTO> crossCGIARCollaborations = new ArrayList<>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findCrossCGIARCollaboration", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("findCrossCGIARCollaboration", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    String strippedRepoPhase = StringUtils.stripToNull(repoPhase);
    Phase phase = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
        && p.getYear() == repoYear && StringUtils.equalsIgnoreCase(p.getName(), strippedRepoPhase) && p.isActive())
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(
        new FieldErrorDTO("findCrossCGIARCollaboration", "phase", repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    if (fieldErrors.isEmpty()) {
      // not all ReportSynthesis have a ReportSynthesisKeyPartnership, so we need to filter out those to avoid
      // exceptions
      crossCGIARCollaborations = reportSynthesisManager.findAll().stream()
        .filter(rs -> rs.getPhase().getId() == phase.getId() && rs.getReportSynthesisKeyPartnership() != null)
        .flatMap(rs -> rs.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipCollaborations().stream())
        .map(keyPartnershipCollaborationMapper::keyPartnershipCollaborationToCrossCGIARCollaboration)
        .collect(Collectors.toList());
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return crossCGIARCollaborations;
  }

  public ResponseEntity<CrossCGIARCollaborationDTO> findCrossCGIARCollaborationById(Long id, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    // TODO: Include all security validations
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    ReportSynthesisKeyPartnershipCollaboration keyPartnershipCollaboration = null;

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findCrossCGIARCollaboration", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("findCrossCGIARCollaboration", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    String strippedRepoPhase = StringUtils.stripToNull(repoPhase);
    Phase phase = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
        && p.getYear() == repoYear && StringUtils.equalsIgnoreCase(p.getName(), strippedRepoPhase) && p.isActive())
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(
        new FieldErrorDTO("findCrossCGIARCollaboration", "phase", repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    if (fieldErrors.isEmpty()) {
      keyPartnershipCollaboration =
        reportSynthesisKeyPartnershipCollaborationManager.getReportSynthesisKeyPartnershipCollaborationById(id);

      if (keyPartnershipCollaboration == null) {
        fieldErrors
          .add(new FieldErrorDTO("findCrossCGIARCollaboration", "ReportSynthesisKeyPartnershipCollaborationEntity",
            id + " is an invalid id of a Report Synthesis Key Partnership Collaboration"));
      } else {
        if (keyPartnershipCollaboration.getReportSynthesisKeyPartnership() == null) {
          fieldErrors.add(new FieldErrorDTO("findCrossCGIARCollaboration", "ReportSynthesisKeyPartnershipEntity",
            "There is no Report Synthesis Key Partnership assosiated to this entity!"));
        } else {
          if (keyPartnershipCollaboration.getReportSynthesisKeyPartnership().getReportSynthesis() == null) {
            fieldErrors.add(new FieldErrorDTO("findCrossCGIARCollaboration", "ReportSynthesisEntity",
              "There is no Report Synthesis assosiated to this entity!"));
          } else {
            if (keyPartnershipCollaboration.getReportSynthesisKeyPartnership().getReportSynthesis()
              .getPhase() == null) {
              fieldErrors.add(new FieldErrorDTO("findCrossCGIARCollaboration", "PhaseEntity",
                "There is no Phase assosiated to this entity!"));
            } else {
              if (keyPartnershipCollaboration.getReportSynthesisKeyPartnership().getReportSynthesis().getPhase()
                .getId() != phase.getId()) {
                fieldErrors.add(
                  new FieldErrorDTO("findCrossCGIARCollaboration", "ReportSynthesisKeyPartnershipCollaborationEntity",
                    "The Report Synthesis Key Partnership Collaboration with id " + id
                      + " do not correspond to the phase entered"));
              }
            }
          }
        }
      }
    }

    // TODO more validations!

    // Validate all fields
    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(keyPartnershipCollaboration)
      .map(this.keyPartnershipCollaborationMapper::keyPartnershipCollaborationToCrossCGIARCollaboration)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public Long putCrossCGIARCollaborationById(Long idCrossCGIARCollaboration,
    NewCrossCGIARCollaborationDTO newCrossCGIARCollaborationDTO, String CGIARentityAcronym, User user) {
    Long idCrossCGIARCollaborationDB = null;
    Phase phase = null;
    CrpProgram crpProgram = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }

    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("putCrossCGIARCollaboration", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newCrossCGIARCollaborationDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "PhaseEntity", "Phase must not be null"));
    } else {
      String strippedPhaseName = StringUtils.stripToNull(newCrossCGIARCollaborationDTO.getPhase().getName());
      if (strippedPhaseName == null || newCrossCGIARCollaborationDTO.getPhase().getYear() == null
      // DANGER! Magic number ahead
        || newCrossCGIARCollaborationDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
            && p.getYear() == newCrossCGIARCollaborationDTO.getPhase().getYear()
            && StringUtils.equalsIgnoreCase(p.getName(), strippedPhaseName) && p.isActive())
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors.add(
            new FieldErrorDTO("putCrossCGIARCollaboration", "phase", newCrossCGIARCollaborationDTO.getPhase().getName()
              + ' ' + newCrossCGIARCollaborationDTO.getPhase().getYear() + " is an invalid phase"));
        }
      }
    }

    strippedId = StringUtils.stripToNull(newCrossCGIARCollaborationDTO.getFlagshipProgramId());
    if (strippedId != null) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(strippedId);
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "CrpProgramEntity",
          newCrossCGIARCollaborationDTO.getFlagshipProgramId() + " is an invalid CRP Program SMO Code"));
      } else {
        if (!StringUtils.equalsIgnoreCase(crpProgram.getCrp().getAcronym(), strippedEntityAcronym)) {
          fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "FlagshipEntity",
            "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym "
              + CGIARentityAcronym));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "CrpProgramEntity",
        newCrossCGIARCollaborationDTO.getFlagshipProgramId() + " is an invalid CRP Program SMO Code"));
    }

    if (newCrossCGIARCollaborationDTO.getCollaborationCrpIds() == null
      || newCrossCGIARCollaborationDTO.getCollaborationCrpIds().isEmpty()) {
      fieldErrors
        .add(new FieldErrorDTO("putCrossCGIARCollaboration", "GlobalUnitList", "Please enter the collaborating crps."));
    }

    ReportSynthesisKeyPartnershipCollaboration keyPartnershipCollaboration =
      reportSynthesisKeyPartnershipCollaborationManager
        .getReportSynthesisKeyPartnershipCollaborationById(idCrossCGIARCollaboration);
    if (keyPartnershipCollaboration == null) {
      fieldErrors
        .add(new FieldErrorDTO("putCrossCGIARCollaboration", "ReportSynthesisKeyPartnershipCollaborationEntity",
          idCrossCGIARCollaboration + " is an invalid id of a Report Synthesis Key Partnership Collaboration"));
    }

    if (fieldErrors.isEmpty()) {
      ReportSynthesis reportSynthesis = null;
      idCrossCGIARCollaborationDB = keyPartnershipCollaboration.getId();

      if (keyPartnershipCollaboration.getReportSynthesisKeyPartnership() == null) {
        fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "ReportSynthesisKeyPartnershipEntity",
          "There is no Report Synthesis Key Partnership assosiated to this entity!"));
      } else {
        if (keyPartnershipCollaboration.getReportSynthesisKeyPartnership().getReportSynthesis() == null) {
          fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "ReportSynthesisEntity",
            "There is no Report Synthesis assosiated to this entity!"));
        } else {
          if (keyPartnershipCollaboration.getReportSynthesisKeyPartnership().getReportSynthesis().getPhase() == null) {
            fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "PhaseEntity",
              "There is no Phase assosiated to this entity!"));
          } else {
            if (keyPartnershipCollaboration.getReportSynthesisKeyPartnership().getReportSynthesis().getPhase()
              .getId() != phase.getId()) {
              fieldErrors
                .add(new FieldErrorDTO("putCrossCGIARCollaboration", "ReportSynthesisKeyPartnershipCollaborationEntity",
                  "The Report Synthesis Key Partnership Collaboration with id " + idCrossCGIARCollaboration
                    + " do not correspond to the phase entered"));
            }
          }
        }
      }

      Set<String> base = keyPartnershipCollaboration.getReportSynthesisKeyPartnershipCollaborationCrps().stream()
        .filter(c -> c != null && c.getId() != null).map(c -> c.getGlobalUnit().getSmoCode())
        .collect(Collectors.toSet());

      Set<String> incoming = newCrossCGIARCollaborationDTO.getCollaborationCrpIds().stream()
        .map(StringUtils::stripToNull).collect(Collectors.toSet());

      // We track the changes. As a result we get a map where the list with the true key means the elements are present
      // in the database and are no longer selected; false means the elements are new and need to be saved.
      // TODO it might be a better way to do this...
      Map<Boolean, List<String>> changes = ChangeTracker.trackChanges(base, incoming);

      changes.get(Boolean.TRUE).stream().map(StringUtils::stripToNull)
        .map(ch -> reportSynthesisKeyPartnershipCollaborationCrpManager.getByCollaborationIdAndGlobalUnitId(
          // we know the globalUnit exists by now, so its "safe" to do what is below
          keyPartnershipCollaboration.getId(), globalUnitManager.findGlobalUnitBySMOCode(ch).getId()))
        .map(ReportSynthesisKeyPartnershipCollaborationCrp::getId).forEach(
          reportSynthesisKeyPartnershipCollaborationCrpManager::deleteReportSynthesisKeyPartnershipCollaborationCrp);

      changes.get(Boolean.FALSE).stream().map(StringUtils::stripToNull).forEach(new Consumer<String>() {

        @Override
        public void accept(String smoCode) {
          GlobalUnit incoming = globalUnitManager.findGlobalUnitBySMOCode(smoCode);
          if (incoming == null) {
            fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "GlobalUnitEntity",
              "A Global Unit SMO code can not be null nor empty."));
          } else {
            if (!incoming.isActive()) {
              fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "GlobalUnitEntity",
                "The Global Unit with SMO Code " + smoCode + " is not active."));
            } else {
              ReportSynthesisKeyPartnershipCollaborationCrp newCollab =
                new ReportSynthesisKeyPartnershipCollaborationCrp();
              newCollab.setGlobalUnit(incoming);
              newCollab.setReportSynthesisKeyPartnershipCollaboration(keyPartnershipCollaboration);
              reportSynthesisKeyPartnershipCollaborationCrpManager
                .saveReportSynthesisKeyPartnershipCollaborationCrp(newCollab);
            }
          }
        }
      });

      // start description
      String strippedDescription = StringUtils.stripToNull(newCrossCGIARCollaborationDTO.getDescription());
      if (strippedDescription == null) {
        fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "Description", "Please enter a description."));
      } else {
        keyPartnershipCollaboration.setDescription(strippedDescription);
      }
      // end description

      keyPartnershipCollaboration.setValueAdded(newCrossCGIARCollaborationDTO.getValueAdded());

      LiaisonInstitution liaisonInstitution =
        liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "LiaisonInstitutionEntity",
          "A Liaison Institution with the acronym " + crpProgram.getAcronym() + " could not be found"));
      } else {
        reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
        if (reportSynthesis == null) {
          fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "ReportSynthesisEntity",
            "A report entity linked to the Phase with id " + phase.getId() + " and Liaison Institution with id "
              + liaisonInstitution.getId() + " could not be found"));
        } else {
          ReportSynthesisKeyPartnership reportSynthesisKeyPartnership =
            reportSynthesis.getReportSynthesisKeyPartnership();
          if (reportSynthesisKeyPartnership == null) {
            fieldErrors.add(new FieldErrorDTO("putCrossCGIARCollaboration", "ReportSynthesisEntity",
              "There is no Report Synthesis Key Partnership linked to the Report Synthesis"));
          } else {
            keyPartnershipCollaboration.setReportSynthesisKeyPartnership(reportSynthesisKeyPartnership);
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

    reportSynthesisKeyPartnershipCollaborationManager
      .saveReportSynthesisKeyPartnershipCollaboration(keyPartnershipCollaboration);
    return idCrossCGIARCollaborationDB;
  }

}
