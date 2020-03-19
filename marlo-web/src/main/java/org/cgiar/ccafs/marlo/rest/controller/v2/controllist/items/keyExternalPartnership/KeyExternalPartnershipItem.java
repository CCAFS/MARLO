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

package org.cgiar.ccafs.marlo.rest.controller.v2.controllist.items.keyExternalPartnership;

import org.cgiar.ccafs.marlo.data.manager.CrpProgramManager;
import org.cgiar.ccafs.marlo.data.manager.GlobalUnitManager;
import org.cgiar.ccafs.marlo.data.manager.InstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.LiaisonInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.PhaseManager;
import org.cgiar.ccafs.marlo.data.manager.RepIndPartnershipMainAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalInstitutionManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalMainAreaManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipExternalManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisKeyPartnershipManager;
import org.cgiar.ccafs.marlo.data.manager.ReportSynthesisManager;
import org.cgiar.ccafs.marlo.data.model.CrpProgram;
import org.cgiar.ccafs.marlo.data.model.CrpUser;
import org.cgiar.ccafs.marlo.data.model.GlobalUnit;
import org.cgiar.ccafs.marlo.data.model.Institution;
import org.cgiar.ccafs.marlo.data.model.LiaisonInstitution;
import org.cgiar.ccafs.marlo.data.model.Phase;
import org.cgiar.ccafs.marlo.data.model.RepIndPartnershipMainArea;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesis;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnership;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternal;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalInstitution;
import org.cgiar.ccafs.marlo.data.model.ReportSynthesisKeyPartnershipExternalMainArea;
import org.cgiar.ccafs.marlo.data.model.User;
import org.cgiar.ccafs.marlo.rest.dto.KeyExternalPartnershipDTO;
import org.cgiar.ccafs.marlo.rest.dto.NewKeyExternalPartnershipDTO;
import org.cgiar.ccafs.marlo.rest.errors.FieldErrorDTO;
import org.cgiar.ccafs.marlo.rest.errors.MARLOFieldValidationException;
import org.cgiar.ccafs.marlo.rest.mappers.KeyExternalPartnershipMapper;
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

@Named
public class KeyExternalPartnershipItem<T> {

  private static final Logger LOG = LoggerFactory.getLogger(KeyExternalPartnershipItem.class);

  private GlobalUnitManager globalUnitManager;
  private PhaseManager phaseManager;
  private CrpProgramManager crpProgramManager;
  private InstitutionManager institutionManager;
  private ReportSynthesisKeyPartnershipExternalManager keyPartnershipExternalManager;
  private ReportSynthesisKeyPartnershipExternalMainAreaManager keyPartnershipExternalMainAreaManager;
  private ReportSynthesisKeyPartnershipExternalInstitutionManager keyPartnershipExternalInstitutionManager;
  private RepIndPartnershipMainAreaManager repIndPartnershipMainAreaManager;
  private LiaisonInstitutionManager liaisonInstitutionManager;
  private ReportSynthesisManager reportSynthesisManager;
  private ReportSynthesisKeyPartnershipManager reportSynthesisKeyPartnershipManager;

  private KeyExternalPartnershipMapper keyExternalPartnershipMapper;

  @Inject
  public KeyExternalPartnershipItem(GlobalUnitManager globalUnitManager, PhaseManager phaseManager,
    CrpProgramManager crpProgramManager, InstitutionManager institutionManager,
    ReportSynthesisKeyPartnershipExternalManager keyPartnershipExternalManager,
    ReportSynthesisKeyPartnershipExternalMainAreaManager keyPartnershipExternalMainAreaManager,
    ReportSynthesisKeyPartnershipExternalInstitutionManager keyPartnershipExternalInstitutionManager,
    RepIndPartnershipMainAreaManager repIndPartnershipMainAreaManager,
    LiaisonInstitutionManager liaisonInstitutionManager, ReportSynthesisManager reportSynthesisManager,
    ReportSynthesisKeyPartnershipManager reportSynthesisKeyPartnershipManager,
    KeyExternalPartnershipMapper keyExternalPartnershipMapper) {
    super();
    this.globalUnitManager = globalUnitManager;
    this.phaseManager = phaseManager;
    this.crpProgramManager = crpProgramManager;
    this.institutionManager = institutionManager;
    this.keyPartnershipExternalManager = keyPartnershipExternalManager;
    this.keyPartnershipExternalMainAreaManager = keyPartnershipExternalMainAreaManager;
    this.keyPartnershipExternalInstitutionManager = keyPartnershipExternalInstitutionManager;
    this.repIndPartnershipMainAreaManager = repIndPartnershipMainAreaManager;
    this.liaisonInstitutionManager = liaisonInstitutionManager;
    this.reportSynthesisManager = reportSynthesisManager;
    this.reportSynthesisKeyPartnershipManager = reportSynthesisKeyPartnershipManager;
    this.keyExternalPartnershipMapper = keyExternalPartnershipMapper;
  }

  public Long createKeyExternalPartnership(NewKeyExternalPartnershipDTO newKeyExternalPartnershipDTO,
    String CGIARentityAcronym, User user) {
    Long keyExternalPartnershipID = null;
    ReportSynthesisKeyPartnershipExternal keyPartnershipExternal = null;
    ReportSynthesisKeyPartnershipExternalInstitution keyPartnershipExternalInstitution = null;
    ReportSynthesisKeyPartnershipExternalMainArea keyPartnershipExternalMainArea = null;
    CrpProgram crpProgram = null;
    Phase phase = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("createKeyExternalPartnership", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newKeyExternalPartnershipDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "PhaseEntity", "Phase must not be null"));
    } else {
      String strippedPhaseName = StringUtils.stripToNull(newKeyExternalPartnershipDTO.getPhase().getName());
      if (strippedPhaseName == null || newKeyExternalPartnershipDTO.getPhase().getYear() == null
      // DANGER! Magic number ahead
        || newKeyExternalPartnershipDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
            && p.getYear() == newKeyExternalPartnershipDTO.getPhase().getYear()
            && StringUtils.equalsIgnoreCase(p.getName(), strippedPhaseName))
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors.add(
            new FieldErrorDTO("createKeyExternalPartnership", "phase", newKeyExternalPartnershipDTO.getPhase().getName()
              + ' ' + newKeyExternalPartnershipDTO.getPhase().getYear() + " is an invalid phase"));
        }
      }
    }

    strippedId = StringUtils.stripToNull(newKeyExternalPartnershipDTO.getFlagshipProgramId());
    if (strippedId != null) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(strippedId);
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "FlagshipEntity",
          newKeyExternalPartnershipDTO.getFlagshipProgramId() + " is an invalid CRP Program SMO code."));
      } else {
        if (!StringUtils.equalsIgnoreCase(crpProgram.getCrp().getAcronym(), strippedEntityAcronym)) {
          fieldErrors.add(new FieldErrorDTO("createCrossCGIARCollaboration", "FlagshipEntity",
            "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym "
              + CGIARentityAcronym));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "FlagshipEntity",
        "CRP Program SMO code can not be null nor empty."));
    }

    if (fieldErrors.isEmpty()) {
      Set<RepIndPartnershipMainArea> mainAreas = new HashSet<>();
      Set<Institution> institutions = new HashSet<>();
      ReportSynthesis reportSynthesis = null;
      ReportSynthesisKeyPartnership reportSynthesisKeyPartnership = null;
      LiaisonInstitution liaisonInstitution = null;
      Long id = null;

      // start ReportSynthesisKeyPartnershipExternalMainArea
      if (newKeyExternalPartnershipDTO.getPartnershipMainAreaIds() != null
        && !newKeyExternalPartnershipDTO.getPartnershipMainAreaIds().isEmpty()) {
        mainAreas = newKeyExternalPartnershipDTO.getPartnershipMainAreaIds().stream().map(StringUtils::stripToNull)
          .map(new Function<String, RepIndPartnershipMainArea>() {

            @Override
            public RepIndPartnershipMainArea apply(String partnershipMainAreaIdString) {
              RepIndPartnershipMainArea partnershipMainArea = null;
              if (partnershipMainAreaIdString != null) {
                Long idPartnershipMainArea = KeyExternalPartnershipItem.this.tryParseLong(partnershipMainAreaIdString,
                  fieldErrors, "createKeyExternalPartnership", "RepIndPartnershipMainAreaEntity");
                if (idPartnershipMainArea != null) {
                  partnershipMainArea =
                    repIndPartnershipMainAreaManager.getRepIndPartnershipMainAreaById(idPartnershipMainArea);
                  if (partnershipMainArea == null) {
                    fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "RepIndPartnershipMainAreaEntity",
                      partnershipMainAreaIdString + " is an invalid Report Indicator Partnership Main Area code."));
                  }
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "RepIndPartnershipMainAreaEntity",
                  "A Report Indicator Partnership Main Area code can not be null nor empty."));
              }

              return partnershipMainArea;
            }
          }).collect(Collectors.toSet());
      } else {
        fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "RepIndPartnershipMainAreaList",
          "Please enter the main area(s) of partnership."));
      }
      // end ReportSynthesisKeyPartnershipExternalMainArea

      // start Institutions
      if (newKeyExternalPartnershipDTO.getInstitutionIds() != null
        && !newKeyExternalPartnershipDTO.getInstitutionIds().isEmpty()) {
        institutions = newKeyExternalPartnershipDTO.getInstitutionIds().stream().map(StringUtils::stripToNull)
          .map(new Function<String, Institution>() {

            @Override
            public Institution apply(String institutionIdString) {
              Institution institution = null;
              if (institutionIdString != null) {
                Long idInstitution = KeyExternalPartnershipItem.this.tryParseLong(institutionIdString, fieldErrors,
                  "createKeyExternalPartnership", "InstitutionEntity");
                if (idInstitution != null) {
                  institution = institutionManager.getInstitutionById(idInstitution);
                  if (institution == null) {
                    fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "InstitutionEntity",
                      institutionIdString + " is an invalid Institution code."));
                  }
                }
              } else {
                fieldErrors.add(new FieldErrorDTO("createKeyExternalPartnership", "InstitutionEntity",
                  "An Institution code can not be null nor empty."));
              }

              return institution;
            }
          }).collect(Collectors.toSet());
      } else {
        fieldErrors
          .add(new FieldErrorDTO("createKeyExternalPartnership", "InstitutionList", "Please enter the key partners."));
      }
      // end Institutions

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
      if (StringUtils.isBlank(newKeyExternalPartnershipDTO.getDescription())) {
        fieldErrors
          .add(new FieldErrorDTO("createKeyExternalPartnership", "Description", "Please enter a description."));
      }
      // end description

      // all validated! now it is supposed to be ok to save the entities
      if (fieldErrors.isEmpty()) {
        keyPartnershipExternal = new ReportSynthesisKeyPartnershipExternal();
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

        keyPartnershipExternal.setDescription(newKeyExternalPartnershipDTO.getDescription().trim());
        keyPartnershipExternal.setReportSynthesisKeyPartnership(reportSynthesisKeyPartnership);
        ReportSynthesisKeyPartnershipExternal keyPartnershipExternalDB =
          keyPartnershipExternalManager.saveReportSynthesisKeyPartnershipExternal(keyPartnershipExternal);
        if (keyPartnershipExternalDB != null) {
          keyExternalPartnershipID = keyPartnershipExternalDB.getId();
          // start keyPartnershipExternalInstitution
          for (Institution institution : institutions) {
            keyPartnershipExternalInstitution = new ReportSynthesisKeyPartnershipExternalInstitution();
            keyPartnershipExternalInstitution.setInstitution(institution);
            keyPartnershipExternalInstitution.setReportSynthesisKeyPartnershipExternal(keyPartnershipExternalDB);
            keyPartnershipExternalInstitutionManager
              .saveReportSynthesisKeyPartnershipExternalInstitution(keyPartnershipExternalInstitution);
          }
          // end keyPartnershipExternalInstitution

          // start keyPartnershipExternalMainArea
          for (RepIndPartnershipMainArea mainArea : mainAreas) {
            keyPartnershipExternalMainArea = new ReportSynthesisKeyPartnershipExternalMainArea();
            keyPartnershipExternalMainArea.setPartnerArea(mainArea);
            keyPartnershipExternalMainArea.setReportSynthesisKeyPartnershipExternal(keyPartnershipExternalDB);
            keyPartnershipExternalMainAreaManager
              .saveReportSynthesisKeyPartnershipExternalMainArea(keyPartnershipExternalMainArea);
          }
          // end keyPartnershipExternalMainArea
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

    return keyExternalPartnershipID;
  }

  /**
   * Delete a KeyExternalPartnership by Id,Phase and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a KeyExternalPartnershipDTO with the Key External Partnership Item
   */
  public ResponseEntity<KeyExternalPartnershipDTO> deleteKeyExternalPartnershipById(Long id, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    ReportSynthesisKeyPartnershipExternal keyPartnershipExternal = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("deleteKeyExternalPartnership", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("deleteKeyExternalPartnership", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors
        .add(new FieldErrorDTO("deleteKeyExternalPartnership", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    String strippedRepoPhase = StringUtils.stripToNull(repoPhase);
    Phase phase = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
        && p.getYear() == repoYear && StringUtils.equalsIgnoreCase(p.getName(), strippedRepoPhase))
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("deleteKeyExternalPartnership", "phase",
        repoPhase + ' ' + repoYear + " is an invalid phase"));
    } else {
      if (!StringUtils.equalsIgnoreCase(phase.getCrp().getAcronym(), strippedEntityAcronym)) {
        fieldErrors.add(new FieldErrorDTO("deleteKeyExternalPartnership", "FlagshipEntity",
          "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym " + CGIARentityAcronym));
      }
    }

    if (fieldErrors.isEmpty()) {
      keyPartnershipExternal = keyPartnershipExternalManager.getReportSynthesisKeyPartnershipExternalById(id);
      if (keyPartnershipExternal != null) {
        if (keyPartnershipExternal.getReportSynthesisKeyPartnership() == null) {
          fieldErrors.add(new FieldErrorDTO("deleteKeyExternalPartnership", "ReportSynthesisKeyPartnershipEntity",
            "There is no Report Synthesis Key Partnership assosiated to this entity!"));
        } else {
          if (keyPartnershipExternal.getReportSynthesisKeyPartnership().getReportSynthesis() == null) {
            fieldErrors.add(new FieldErrorDTO("deleteKeyExternalPartnership", "ReportSynthesisEntity",
              "There is no Report Synthesis assosiated to this entity!"));
          } else {
            if (keyPartnershipExternal.getReportSynthesisKeyPartnership().getReportSynthesis().getPhase() == null) {
              fieldErrors.add(new FieldErrorDTO("deleteKeyExternalPartnership", "PhaseEntity",
                "There is no Phase assosiated to this entity!"));
            } else {
              if (keyPartnershipExternal.getReportSynthesisKeyPartnership().getReportSynthesis().getPhase()
                .getId() != phase.getId()) {
                fieldErrors
                  .add(new FieldErrorDTO("deleteKeyExternalPartnership", "ReportSynthesisKeyPartnershipExternalEntity",
                    "The Report Synthesis Key Partnership External with id " + id
                      + " do not correspond to the phase entered"));
              } else {
                Long idKeyPartnershipExternal = keyPartnershipExternal.getId();
                keyPartnershipExternal.setReportSynthesisKeyPartnershipExternalInstitutions(
                  keyPartnershipExternalInstitutionManager.findAll().stream()
                    .filter(i -> i.isActive() && i.getReportSynthesisKeyPartnershipExternal()
                      .getReportSynthesisKeyPartnership().getReportSynthesis().getPhase().getId().equals(phase.getId()))
                    .collect(Collectors.toSet()));
                keyPartnershipExternal.setReportSynthesisKeyPartnershipExternalMainAreas(
                  keyPartnershipExternalMainAreaManager.findAll().stream()
                    .filter(a -> a.isActive() && a.getReportSynthesisKeyPartnershipExternal()
                      .getReportSynthesisKeyPartnership().getReportSynthesis().getPhase().getId().equals(phase.getId()))
                    .collect(Collectors.toSet()));

                keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalInstitutions().stream()
                  .filter(i -> i.getReportSynthesisKeyPartnershipExternal().getId() == idKeyPartnershipExternal)
                  .forEach(i -> keyPartnershipExternalInstitutionManager
                    .deleteReportSynthesisKeyPartnershipExternalInstitution(i.getId()));
                keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalMainAreas().stream()
                  .filter(i -> i.getReportSynthesisKeyPartnershipExternal().getId() == idKeyPartnershipExternal)
                  .forEach(i -> keyPartnershipExternalMainAreaManager
                    .deleteReportSynthesisKeyPartnershipExternalMainArea(i.getId()));

                keyPartnershipExternalManager
                  .deleteReportSynthesisKeyPartnershipExternal(keyPartnershipExternal.getId());
              }
            }
          }
        }
      } else {
        fieldErrors.add(new FieldErrorDTO("deleteKeyExternalPartnership", "ReportSynthesisKeyPartnershipExternalEntity",
          id + " is an invalid Report Synthesis Key Partnership External Code"));
      }
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return Optional.ofNullable(keyPartnershipExternal)
      .map(this.keyExternalPartnershipMapper::keyPartnershipExternalToKeyExternalPartnershipDTO)
      .map(result -> new ResponseEntity<>(result, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
  }

  public List<KeyExternalPartnershipDTO> findAllKeyExternalPartnershipByGlobalUnit(String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {

    List<KeyExternalPartnershipDTO> keyExternalPartnerships = new ArrayList<>();
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnershipByGlobalUnit", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnershipByGlobalUnit", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    String strippedRepoPhase = StringUtils.stripToNull(repoPhase);
    Phase phase = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
        && p.getYear() == repoYear && StringUtils.equalsIgnoreCase(p.getName(), strippedRepoPhase))
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnershipByGlobalUnit", "phase",
        repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    if (fieldErrors.isEmpty()) {
      // not all ReportSynthesis have a ReportSynthesisSrfProgress, so we need to filter out those to avoid exceptions
      keyExternalPartnerships = reportSynthesisManager.findAll().stream()
        .filter(rs -> rs.getPhase().getId() == phase.getId() && rs.getReportSynthesisKeyPartnership() != null)
        .flatMap(rs -> rs.getReportSynthesisKeyPartnership().getReportSynthesisKeyPartnershipExternals().stream())
        .map(keyExternalPartnershipMapper::keyPartnershipExternalToKeyExternalPartnershipDTO)
        .collect(Collectors.toList());
    }

    if (!fieldErrors.isEmpty()) {
      fieldErrors.forEach(e -> System.out.println(e.getMessage()));
      throw new MARLOFieldValidationException("Field Validation errors", "",
        fieldErrors.stream()
          .sorted(Comparator.comparing(FieldErrorDTO::getField, Comparator.nullsLast(Comparator.naturalOrder())))
          .collect(Collectors.toList()));
    }

    return keyExternalPartnerships;
  }

  /**
   * Find an KeyExternalPartnership by Id and year
   * 
   * @param id
   * @param year
   * @param phase
   * @return a KeyExternalPartnershipDTO with the keyExternalPartnership Item
   */

  public ResponseEntity<KeyExternalPartnershipDTO> findKeyExternalPartnershipById(Long id, String CGIARentityAcronym,
    Integer repoYear, String repoPhase, User user) {
    // TODO: Include all security validations
    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();
    ReportSynthesisKeyPartnershipExternal keyExternalPartnership = null;

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnership", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnership", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    String strippedRepoPhase = StringUtils.stripToNull(repoPhase);
    Phase phase = this.phaseManager.findAll().stream()
      .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
        && p.getYear() == repoYear && StringUtils.equalsIgnoreCase(p.getName(), strippedRepoPhase))
      .findFirst().orElse(null);
    if (phase == null) {
      fieldErrors.add(
        new FieldErrorDTO("findKeyExternalPartnership", "phase", repoPhase + ' ' + repoYear + " is an invalid phase"));
    }

    if (fieldErrors.isEmpty()) {
      keyExternalPartnership = this.keyPartnershipExternalManager.getReportSynthesisKeyPartnershipExternalById(id);
      if (keyExternalPartnership == null) {
        fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnership", "ReportSynthesisKeyPartnershipExternalEntity",
          id + " is an invalid id of a Report Synthesis Key Partnership External"));
      } else {
        if (keyExternalPartnership.getReportSynthesisKeyPartnership() == null) {
          fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnership", "ReportSynthesisKeyPartnershipEntity",
            "There is no Report Synthesis Key Partnership assosiated to this entity!"));
        } else {
          if (keyExternalPartnership.getReportSynthesisKeyPartnership().getReportSynthesis() == null) {
            fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnership", "ReportSynthesisKeyPartnershipEntity",
              "There is no Report Synthesis assosiated to this entity!"));
          } else {
            if (keyExternalPartnership.getReportSynthesisKeyPartnership().getReportSynthesis().getPhase() == null) {
              fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnership", "ReportSynthesisKeyPartnershipEntity",
                "There is no Phase assosiated to this entity!"));
            } else {
              if (keyExternalPartnership.getReportSynthesisKeyPartnership().getReportSynthesis().getPhase()
                .getId() != phase.getId()) {
                fieldErrors.add(new FieldErrorDTO("findKeyExternalPartnership", "ReportSynthesisKeyPartnershipEntity",
                  "The Report Synthesis Key Partnership External with id " + id
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

    return Optional.ofNullable(keyExternalPartnership)
      .map(this.keyExternalPartnershipMapper::keyPartnershipExternalToKeyExternalPartnershipDTO)
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
  public Long putKeyExternalPartnershipById(Long idKeyExternalPartnership,
    NewKeyExternalPartnershipDTO newKeyExternalPartnershipDTO, String CGIARentityAcronym, User user) {
    Long idKeyExternalPartnershipDB = null;
    Phase phase = null;
    CrpProgram crpProgram = null;
    String strippedId = null;

    List<FieldErrorDTO> fieldErrors = new ArrayList<FieldErrorDTO>();

    String strippedEntityAcronym = StringUtils.stripToNull(CGIARentityAcronym);
    GlobalUnit globalUnitEntity = this.globalUnitManager.findGlobalUnitByAcronym(strippedEntityAcronym);
    if (globalUnitEntity == null) {
      fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "GlobalUnitEntity",
        CGIARentityAcronym + " is not a valid CGIAR entity acronym"));
    } else {
      if (!globalUnitEntity.isActive()) {
        fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "GlobalUnitEntity",
          "The Global Unit with acronym " + CGIARentityAcronym + " is not active."));
      }
    }

    Set<CrpUser> lstUser = user.getCrpUsers();
    if (!lstUser.stream()
      .anyMatch(crp -> StringUtils.equalsIgnoreCase(crp.getCrp().getAcronym(), strippedEntityAcronym))) {
      fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "GlobalUnitEntity", "CGIAR entity not autorized"));
    }

    if (newKeyExternalPartnershipDTO.getPhase() == null) {
      fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "PhaseEntity", "Phase must not be null"));
    } else {
      String strippedPhaseName = StringUtils.stripToNull(newKeyExternalPartnershipDTO.getPhase().getName());
      if (strippedPhaseName == null || newKeyExternalPartnershipDTO.getPhase().getYear() == null
      // DANGER! Magic number ahead
        || newKeyExternalPartnershipDTO.getPhase().getYear() < 2015) {
        fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "PhaseEntity", "Phase is invalid"));
      } else {
        phase = phaseManager.findAll().stream()
          .filter(p -> StringUtils.equalsIgnoreCase(p.getCrp().getAcronym(), strippedEntityAcronym)
            && p.getYear() == newKeyExternalPartnershipDTO.getPhase().getYear()
            && StringUtils.equalsIgnoreCase(p.getName(), strippedPhaseName))
          .findFirst().orElse(null);

        if (phase == null) {
          fieldErrors.add(
            new FieldErrorDTO("putKeyExternalPartnership", "phase", newKeyExternalPartnershipDTO.getPhase().getName()
              + ' ' + newKeyExternalPartnershipDTO.getPhase().getYear() + " is an invalid phase"));
        }
      }
    }

    strippedId = StringUtils.stripToNull(newKeyExternalPartnershipDTO.getFlagshipProgramId());
    if (strippedId != null) {
      crpProgram = crpProgramManager.getCrpProgramBySmoCode(strippedId);
      if (crpProgram == null) {
        fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "CrpProgramEntity",
          newKeyExternalPartnershipDTO.getFlagshipProgramId() + " is an invalid CRP Program SMO Code"));
      } else {
        if (!StringUtils.equalsIgnoreCase(crpProgram.getCrp().getAcronym(), strippedEntityAcronym)) {
          fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "FlagshipEntity",
            "The CRP Program SMO Code entered does not correspond to the GlobalUnit with acronym "
              + CGIARentityAcronym));
        }
      }
    } else {
      fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "CrpProgramEntity",
        newKeyExternalPartnershipDTO.getFlagshipProgramId() + " is an invalid CRP Program SMO Code"));
    }

    if (newKeyExternalPartnershipDTO.getPartnershipMainAreaIds() == null
      || newKeyExternalPartnershipDTO.getPartnershipMainAreaIds().isEmpty()) {
      fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "RepIndPartnershipMainAreaList",
        "Please enter the main area(s) of partnership."));
    }

    if (newKeyExternalPartnershipDTO.getInstitutionIds() == null
      || newKeyExternalPartnershipDTO.getInstitutionIds().isEmpty()) {
      fieldErrors
        .add(new FieldErrorDTO("putKeyExternalPartnership", "InstitutionList", "Please enter the key partners."));
    }

    ReportSynthesisKeyPartnershipExternal keyPartnershipExternal =
      keyPartnershipExternalManager.getReportSynthesisKeyPartnershipExternalById(idKeyExternalPartnership);
    if (keyPartnershipExternal == null) {
      fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "ReportSynthesisKeyPartnershipExternalEntity",
        +idKeyExternalPartnership + " is an invalid Report Synthesis Key Partnership External Code"));
    }

    if (fieldErrors.isEmpty()) {
      idKeyExternalPartnershipDB = keyPartnershipExternal.getId();
      ReportSynthesis reportSynthesis = null;

      Set<String> base = keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalMainAreas().stream()
        .filter(m -> m != null && m.getId() != null).map(m -> m.getPartnerArea().getId().toString())
        .collect(Collectors.toSet());

      Set<String> incoming = newKeyExternalPartnershipDTO.getPartnershipMainAreaIds().stream()
        .map(StringUtils::stripToNull).collect(Collectors.toSet());

      // We track the changes. As a result we get a map where the list with the true key means the elements are
      // present
      // in the database and are no longer selected; false means the elements are new and need to be saved.
      // TODO it might be a better way to do this...
      Map<Boolean, List<String>> changes = ChangeTracker.trackChanges(base, incoming);

      // we know that every code is a number, so it is safe to do what is below
      changes.get(Boolean.TRUE).stream().map(StringUtils::stripToNull)
        .map(m -> this.tryParseLong(m, fieldErrors, "putKeyExternalPartnership", "RepIndPartnershipMainAreaEntity"))
        .map(m -> keyPartnershipExternalMainAreaManager
          .getByPartnershipExternalIdAndMainAreaId(keyPartnershipExternal.getId(), m))
        // this should not throw NullPointerException because we know the entities exists in the db
        .map(ReportSynthesisKeyPartnershipExternalMainArea::getId)
        .forEach(keyPartnershipExternalMainAreaManager::deleteReportSynthesisKeyPartnershipExternalMainArea);

      changes.get(Boolean.FALSE).stream().map(StringUtils::stripToNull).forEach(new Consumer<String>() {

        @Override
        public void accept(String partnershipMainAreaIdString) {
          RepIndPartnershipMainArea incoming = null;
          if (partnershipMainAreaIdString != null) {
            Long idPartnershipMainArea = KeyExternalPartnershipItem.this.tryParseLong(partnershipMainAreaIdString,
              fieldErrors, "putKeyExternalPartnership", "RepIndPartnershipMainAreaEntity");
            if (idPartnershipMainArea != null) {
              incoming = repIndPartnershipMainAreaManager.getRepIndPartnershipMainAreaById(idPartnershipMainArea);
              if (incoming == null) {
                fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "RepIndPartnershipMainAreaEntity",
                  partnershipMainAreaIdString + " is an invalid Report Indicator Partnership Main Area Code"));
              } else {
                ReportSynthesisKeyPartnershipExternalMainArea newMainArea =
                  new ReportSynthesisKeyPartnershipExternalMainArea();
                newMainArea.setPartnerArea(incoming);
                newMainArea.setReportSynthesisKeyPartnershipExternal(keyPartnershipExternal);
                keyPartnershipExternalMainAreaManager.saveReportSynthesisKeyPartnershipExternalMainArea(newMainArea);
              }
            }
          } else {
            fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "RepIndPartnershipMainAreaEntity",
              "A Report Indicator Partnership Main Area code can not be null nor empty."));
          }
        }
      });

      base = keyPartnershipExternal.getReportSynthesisKeyPartnershipExternalInstitutions().stream()
        .filter(i -> i != null && i.getId() != null).map(m -> m.getInstitution().getId().toString())
        .collect(Collectors.toSet());

      incoming = newKeyExternalPartnershipDTO.getInstitutionIds().stream().map(StringUtils::stripToNull)
        .collect(Collectors.toSet());

      changes = ChangeTracker.trackChanges(base, incoming);

      // we know that every code is a number, so it is safe to do what is below
      changes.get(Boolean.TRUE).stream().map(StringUtils::stripToNull)
        .map(i -> this.tryParseLong(i, fieldErrors, "putKeyExternalPartnership", "InstitutionEntity"))
        .map(i -> keyPartnershipExternalInstitutionManager
          .getByPartnershipExternalIdAndInstitutionId(keyPartnershipExternal.getId(), i))
        // this should not throw NullPointerException because we know the entities exists in the db
        .map(ReportSynthesisKeyPartnershipExternalInstitution::getId)
        .forEach(keyPartnershipExternalInstitutionManager::deleteReportSynthesisKeyPartnershipExternalInstitution);

      changes.get(Boolean.FALSE).stream().map(StringUtils::stripToNull).forEach(new Consumer<String>() {

        @Override
        public void accept(String institutionIdString) {
          Institution incoming = null;
          if (institutionIdString != null) {
            Long idInstitution = KeyExternalPartnershipItem.this.tryParseLong(institutionIdString, fieldErrors,
              "putKeyExternalPartnership", "InstitutionEntity");
            if (idInstitution != null) {
              incoming = institutionManager.getInstitutionById(idInstitution);
              if (incoming == null) {
                fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "InstitutionEntity",
                  idInstitution + " is an invalid Institution Code"));
              } else {
                ReportSynthesisKeyPartnershipExternalInstitution newInstitution =
                  new ReportSynthesisKeyPartnershipExternalInstitution();
                newInstitution.setInstitution(incoming);
                newInstitution.setReportSynthesisKeyPartnershipExternal(keyPartnershipExternal);
                keyPartnershipExternalInstitutionManager
                  .saveReportSynthesisKeyPartnershipExternalInstitution(newInstitution);
              }
            }
          } else {
            fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "InstitutionEntity",
              "An Institution code can not be null nor empty."));
          }
        }
      });

      // start description
      if (StringUtils.isBlank(newKeyExternalPartnershipDTO.getDescription())) {
        fieldErrors
          .add(new FieldErrorDTO("createKeyExternalPartnership", "Description", "Please enter a description."));
      } else {
        keyPartnershipExternal.setDescription(newKeyExternalPartnershipDTO.getDescription().trim());
      }
      // end description

      LiaisonInstitution liaisonInstitution =
        liaisonInstitutionManager.findByAcronymAndCrp(crpProgram.getAcronym(), globalUnitEntity.getId());
      if (liaisonInstitution == null) {
        fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "LiaisonInstitutionEntity",
          "A Liaison Institution with the acronym " + crpProgram.getAcronym() + " could not be found"));
      } else {
        reportSynthesis = reportSynthesisManager.findSynthesis(phase.getId(), liaisonInstitution.getId());
        if (reportSynthesis == null) {
          fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "ReportSynthesisEntity",
            "A report entity linked to the Phase with id " + phase.getId() + " and Liaison Institution with id "
              + liaisonInstitution.getId() + " could not be found"));
        } else {
          ReportSynthesisKeyPartnership reportSynthesisKeyPartnership =
            reportSynthesis.getReportSynthesisKeyPartnership();
          if (reportSynthesisKeyPartnership == null) {
            fieldErrors.add(new FieldErrorDTO("putKeyExternalPartnership", "ReportSynthesisEntity",
              "There is no Report Synthesis Key Partnership linked to the Report Synthesis"));
          } else {
            keyPartnershipExternal.setReportSynthesisKeyPartnership(reportSynthesisKeyPartnership);
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

    keyPartnershipExternalManager.saveReportSynthesisKeyPartnershipExternal(keyPartnershipExternal);
    return idKeyExternalPartnershipDB;
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